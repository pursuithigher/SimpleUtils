package com.dzbook.service;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.DzLog;
import com.dzbook.log.DzLogMap;
import com.dzbook.log.LogConstants;
import com.dzbook.model.UserGrow;
import com.dzbook.utils.DBUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 支付公共处理类。
 *
 * @author zhenglk
 */
public class MarketDao {

    /**
     * 删除 明确需要删除的图书，和未读的下架图书。
     *
     * @param context    上下文
     * @param maskBookId 屏蔽bookId当前次调用不能删除。比如正在读的书不能马上删除。
     */
    public static void deleteSomeBook(final Context context, final String maskBookId) {

        if (Looper.getMainLooper() == Looper.myLooper()) {
            DzSchedulers.child(new Runnable() {
                @Override
                public void run() {
                    deleteSomeBookInner(context, maskBookId);
                }
            });

        } else {
            deleteSomeBookInner(context, maskBookId);
        }
    }

    private static void deleteSomeBookInner(Context context, String maskBookId) {
        ArrayList<BookInfo> books = DBUtils.findAllBooks(context);
        if (null != books) {
            for (final BookInfo bookInfo : books) {
                if (null == bookInfo) {
                    continue;
                }
                if (!TextUtils.isEmpty(maskBookId) && maskBookId.equals(bookInfo.bookid)) {
                    continue;
                }
                boolean flag = (1 != bookInfo.hasRead && bookInfo.isShowOffShelf(context)) || bookInfo.isMustDeleteBook(context);
                if (flag) {
                    ALog.dLk("删除下架图书:" + bookInfo.bookid + "《" + bookInfo.bookname + "》");

                    if (bookInfo.isAddBook == 2) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("mode", "2");
                        map.put("bid", bookInfo.bookid);
                        if (bookInfo.isMustDeleteBook(context)) {
                            //强制删除
                            map.put("type", "3");
                        } else if (bookInfo.isShowOffShelf(context)) {
                            //下架
                            map.put("type", "2");
                        } else {
                            map.put("type", "1");
                        }
                        DzLog.getInstance().logEvent(LogConstants.EVENT_SCSJ, map, null);
                    }

                    DBUtils.deleteCatalogByBoodId(context, bookInfo.bookid);
                    DBUtils.deleteBook(context, bookInfo);
                }
            }
        }
    }


    /**
     * 打开图书的时候，付费意向状态重置。
     * (6) -> (4)
     *
     * @param context 上下文
     * @param bookid  图书id
     */
    public static void markResetWilling(Context context, String bookid) {
        BookInfo bookInfo = DBUtils.findByBookId(context, bookid);
        if (null == bookInfo) {
            return;
        }
        if (6 == bookInfo.confirmStatus) {
            BookInfo newInfo = new BookInfo();
            newInfo.bookid = bookid;
            newInfo.confirmStatus = 4;
            DBUtils.updateBook(context, newInfo);
            ALog.iLk("confirmStatus: (" + bookInfo.confirmStatus + ") -> (" + 4 + "), markResetWilling");
        } else {
            ALog.iLk("confirmStatus: (" + bookInfo.confirmStatus + ") no change, markResetWilling");
        }
    }

    /**
     * 已经点下了确认订购，发起支付意向。如果已显示确认订购成功了，状态保持。
     * (2) 保持不变 <br/>
     * (?) - (6)<br/>
     *
     * @param context 上下文
     * @param bookid  图书id
     */
    public static void markConfirmWilling(final Context context, final String bookid) {
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                Context mContext = context.getApplicationContext();

                BookInfo bookInfo = DBUtils.findByBookId(mContext, bookid);
                if (null == bookInfo) {
                    return;
                }
                if (2 != bookInfo.confirmStatus) {
                    BookInfo newInfo = new BookInfo();
                    newInfo.bookid = bookid;
                    newInfo.confirmStatus = 6;
                    DBUtils.updateBook(mContext, newInfo);
                    ALog.iLk("confirmStatus: (" + bookInfo.confirmStatus + ") -> (" + 6 + "), markConfirmWilling");
                } else {
                    ALog.iLk("confirmStatus: (" + bookInfo.confirmStatus + ") no change, markConfirmWilling");
                }
            }
        });
    }

    /**
     * 标记已购买。
     * (5) -> 保持不变
     * (2) -> 保持不变
     * (6) -> (2)
     * 订购过程遇到了订购页：(?) -> (5) 否则：(?) -> (4)
     * <p/>
     * <p/>
     * 对于自有支付体系的图书，未勾选连续订购的场景，
     * (?) -> (5)
     *
     * @param context      上下文
     * @param bookInfo     图书id
     * @param existCmOrder 订购过程是否遇到了订购页
     * @param confirmDst   dex内部手动订购成功了,直接标记为稳定订购成功。
     * @param isReader     是否来源阅读器
     */
    public static void markConfirmOnSuccess(Context context, BookInfo bookInfo, boolean existCmOrder, int confirmDst, boolean isReader) {
        if (null == bookInfo) {
            return;
        }

        int confirmStatus = bookInfo.confirmStatus;
        if (1 == bookInfo.payRemind) {
            confirmStatus = 5;
        } else if (confirmDst > 0) {
            // dex内部手动订购成功了,直接标记为稳定订购成功。
            confirmStatus = confirmDst;
        } else {
            switch (bookInfo.confirmStatus) {
                case 5:// 已锁定。不变。
                case 2:// 已显式订购。不变。
                    break;

                case 6:
                    confirmStatus = 2;
                    break;

                default:
                    if (existCmOrder) {
                        confirmStatus = 5;
                    } else {
                        confirmStatus = 4;
                    }
                    break;
            }
        }

        if (confirmStatus != bookInfo.confirmStatus || 2 != bookInfo.payStatus) {
            BookInfo newInfo = new BookInfo();
            newInfo.bookid = bookInfo.bookid;

            newInfo.payStatus = 2;
            newInfo.isAddBook = 2;
            newInfo.confirmStatus = confirmStatus;

            DBUtils.updateBook(context, newInfo);
            if (bookInfo.isAddBook != 2) {
                // 加入书架，同步成长值
                UserGrow.userGrowOnceToday(context, UserGrow.USER_GROW_ADD_BOOK);

                //只打一次
                if (isReader) {
                    DzLog.getInstance().logClick(LogConstants.MODULE_YDQ, LogConstants.ZONE_YDQ_JRSJ_FF, bookInfo.bookid, DzLogMap.getPreLastMap(), null);
                } else {
                    DzLog.getInstance().logClick(LogConstants.MODULE_SJXQ, LogConstants.ZONE_YDQ_JRSJ_FF, bookInfo.bookid, DzLogMap.getPreLastMap(), null);
                }
            }

            ALog.iLk("confirmStatus: (" + bookInfo.confirmStatus + ") -> (" + confirmStatus + "), existCmOrder=" + existCmOrder + ", markConfirmOnSuccess");
        } else {
            ALog.iLk("confirmStatus: (" + bookInfo.confirmStatus + ") no change, existCmOrder=" + existCmOrder + ", markConfirmOnSuccess");
        }

    }

    /**
     * 服务器免费屏蔽状态图书，标记为已付费状态，已确认订购状态。
     *
     * @param context  上下文
     * @param bookInfo 图书info
     */
    public static void markForFree(Context context, BookInfo bookInfo) {
        if (null == bookInfo) {
            return;
        }
        if (2 != bookInfo.payStatus || 2 != bookInfo.confirmStatus) {
            bookInfo.payStatus = 2;
            bookInfo.confirmStatus = 2;

            BookInfo newInfo = new BookInfo();
            newInfo.bookid = bookInfo.bookid;
            newInfo.payStatus = 2;
            newInfo.confirmStatus = 2;

            // TODO 付费记录上传 看看 单本书的付费记录是怎么上传的 还得看看预加载流程
            DBUtils.updateBook(context, newInfo);
            ALog.iLk("confirmStatus: (" + bookInfo.confirmStatus + ") -> (2), markForFree");
        } else {
            ALog.iLk("confirmStatus: (" + bookInfo.confirmStatus + ") no change, markForFree");
        }
    }
}

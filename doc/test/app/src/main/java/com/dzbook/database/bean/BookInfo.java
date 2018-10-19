package com.dzbook.database.bean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.iss.bean.BaseBean;
import com.iss.db.IssDbFactory;
import com.iss.db.TableColumn;

import org.json.JSONObject;

/**
 * 书对应的表 存放数据库的bean
 *
 * @author dllik 2013-11-23
 */
public class BookInfo extends BaseBean<BookInfo> {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    /**
     * 书籍唯一id（本地书籍为书籍路径）
     */
    @TableColumn(type = TableColumn.Types.TEXT, isIndex = true)
    public String bookid;

    /**
     * 作者名称
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String author;

    /**
     * 时间（排序用：时间戳）
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String time;

    /**
     * 来源<br>
     * 修改：1(网络) 2(本地)
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public int bookfrom;

    /**
     * 书名(本地书籍：文件名)
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String bookname;

    /**
     * 封面地址
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String coverurl;

    /**
     * 书籍格式<br>
     * 修改：3(.comic)  2(.txt) 1(.epub)
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public int format;

    /**
     * 书架默认book<br>
     * 修改 2(true) 1(false)
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public int isdefautbook;

    /**
     * 是不是已添加到书架<br>
     * 修改 2(true) 1(false)
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public int isAddBook;

    /**
     * 1.基地书籍 <br/>
     * 判断是否这本书籍是否支付过<br>
     * 修改 2(已支付过) 1(未支付过) <br/>
     * 2.如果是自有支付系统,则代表 <br/>
     * 2(不需要每个付费章节进行确认提示) 1(需要每个付费章节进行确认提示)
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public int payStatus;

    /**
     * 判断是否这本书籍是否支付过<br>
     * <p/>
     * (1) (未确认订购过)<br>
     * (2) (已确认订购过)显式标记<br>
     * (3) 不入数据库，表示check pay发起的支付<br>
     * (4) (已确认订购过)隐式标记（修改）<br>
     * (5) 锁定状态。显示标记过的图书不允许标记此状态，标记过此状态的不允许标记隐式确认<br>
     * (6) 点下了确认订购，发起支付意向<br>
     * <p/>
     * 状态机:<br/>
     * ~----------------------------------------------------------------------------|
     * |                                                                            |
     * |  /-<-服务端状态，限免变为非限免时---< (2)                                      |
     * |  |                                /\                                       |
     * |  |                                | 订购完成                                |
     * |  |                                |                                        |
     * |  |                               /\                                        |
     * |  |             />-点击确认订购--> (6) ---<---点击确认订购，解除锁定---<----\    |
     * |  |             |                \/                                    |    |
     * |  |             |                |                                     |    |
     * |  \/            |                |                                     |    |
     * | (1) >--新订购-->|      打开图书，消除付费意向                             |    |
     * |                |                |                                     |    |
     * |                |                |                                     |    |
     * |                |                \/                                    |    |
     * |                \>--订购完成---> (4) >---订购成功,探测到基地订购页,锁定--> (5)   |
     * |                                                                            |
     * |                                                                            |
     * ~----------------------------------------------------------------------------|
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public int confirmStatus;

    /**
     * 当前阅读章节(默认第一张，由阅读器更新)
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String currentCatalogId;

    /**
     * 修改 是否单本 2(连载) 1(单本)
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public int bookstatus;

    /**
     * 如果是单本 则此字段存储的是 单本书籍的价格 如果是连载 则此字段存储的是 单章的价格
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String price;

    /**
     * 修改：是否已完结 2(已完结) 1（未完结）
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public int isEnd;

    /**
     * 是否书籍已经更新 2(书籍已更新) 1(书籍未更新) 3(目录更新中)
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public int isUpdate;

    /**
     * 是否打开过 2(未打开过) 1(已经打开过)
     */
    @TableColumn(type = TableColumn.Types.INTEGER)
    public int hasRead;

    /**
     * 书籍状态
     * 1：常态，
     * 2：限免
     * 3：vip用户限免
     * 4：图书已下架，
     * 5：删除
     * <p>
     * 优先级（当前）：5>4>3>2>1
     */
    @TableColumn(type = TableColumn.Types.INTEGER)
    public int control;


    /**
     * 是否免费：0否 1：是（是否限免完全由服务端决定）
     */
    @TableColumn(type = TableColumn.Types.INTEGER)
    public int isFree;

    /**
     * 此为自有充值系统需要的字段：是否每个付费章节提醒 <br>
     * 2(不需要,已勾选) 1(需要,未勾选)
     */
    @TableColumn(type = TableColumn.Types.INTEGER)
    public int payRemind;

    /**
     * 版权来源
     * 例如:内容来自中国移动阅读基地
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String sourceFrom;


    /**
     * cmt 2017/11/22 新增固化字段  用于表达阅读的书籍的来源
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public String readerFrom;

    /**
     * 书籍是否支持横屏
     * 1.支持，2：不支持
     */
    @TableColumn(type = TableColumn.Types.TEXT)
    public int isSupportH;


    /**
     * 是否选中<br>
     */
    public boolean blnIsChecked;

    /**
     * 是否是书架中添加按钮
     */
    private boolean isAddButton;

    /**
     * 构造
     */
    public BookInfo() {
    }

    /**
     * 构造
     *
     * @param bookname    bookname
     * @param isAddButton isAddButton
     */
    public BookInfo(String bookname, boolean isAddButton) {
        this.bookname = bookname;
        this.isAddButton = isAddButton;
    }

    private int getMarketStatus(Context context) {
        return control;
    }

    /**
     * 是否是图书 显示 限免状态。
     *
     * @param context context
     * @return boolean
     */
    public boolean isFreeStatus(Context context) {
        int status = getMarketStatus(context);
        return status == 2 || status == 3;
    }

    /**
     * 是否展示免费角标
     *
     * @param context context
     * @return boolean
     */
    public boolean isShowFreeStatus(Context context) {
        int status = getMarketStatus(context);
        return status == 2;
    }

    /**
     * 是否是图书 显示 下架状态。
     *
     * @param context context
     * @return boolean
     */
    public boolean isShowOffShelf(Context context) {
        int marketStatus = getMarketStatus(context);
        return marketStatus == 4;
    }

    /**
     * 是否是图书 需要及时删除的图书。
     *
     * @param context context
     * @return boolean
     */
    public boolean isMustDeleteBook(Context context) {
        int marketStatus = getMarketStatus(context);
        return marketStatus == 5;
    }


    /**
     * 是否vip用户免费：书架和李东确认VIP限免高于限免
     *
     * @param context context
     * @return boolean
     */
    public boolean isVipFree(Context context) {
        int marketStatus = getMarketStatus(context);
        return marketStatus == 3;
    }

    public boolean isAddButton() {
        return isAddButton;
    }

    /**
     * 对 confirm 的值限制后，发起支付识别的订购码
     *
     * @return int
     */
    public int getLimitConfirmStatus() {
        return getLimitConfirmStatus(confirmStatus);
    }

    /**
     * 对 confirm 的值限制后，发起支付识别的订购码
     *
     * @param value confirm
     * @return int
     */
    private static int getLimitConfirmStatus(int value) {
        if (value < 1) {
            value = 1;
        } else if (value > 6) {
            value = 4;
        }

        switch (value) {
            case 4:
                return 2;
            case 5:
            case 6:
                return 1;
            default:
                break;
        }
        return value;
    }

    /**
     * 设置支付方式
     *
     * @param from sourceFrom
     */
    public void setRechargeParams(String from) {
        if (!TextUtils.isEmpty(from)) {
            this.sourceFrom = from;
        }
    }

    /**
     * 设置支付方式
     *
     * @param remind payRemind
     * @param from   sourceFrom
     */
    public void setRechargeParams(String from, int remind) {
        if (!TextUtils.isEmpty(from)) {
            this.sourceFrom = from;
        }
        this.payRemind = remind;
    }


    /**
     * 是否书籍
     * 1:Epub 2:txt 3:漫画
     * <p>
     * 0x20以后为跳转类型
     * 0x20：.doc
     * 0x21：.docx
     * 0x22：.pdf
     * 0x23：.ppt
     * 0x24:.pptx
     * 0x25:.pps
     * 0x26:.ppsx
     * 0x27:.xls
     * 0x28:.xlsx
     *
     * @return boolean
     */
    public boolean isJump() {
        return format >= 0x20 && format <= 0x29;
    }

    /**
     * 是否本地书籍
     *
     * @return boolean
     */
    public boolean isLocalBook() {
        return bookfrom == 2;
    }

    /**
     * 是否阅读器支持横屏
     *
     * @return boolean
     */
    public boolean isReaderSupportH() {
        return isSupportH == 1;
    }

    /**
     * 是否单本书籍
     *
     * @return boolean
     */
    public boolean isSingleBook() {
        return bookstatus == 1;
    }


    @Override
    public BookInfo parseJSON(JSONObject jsonObj) {
        return null;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public BookInfo cursorToBean(Cursor cursor) {
        try {
            bookid = cursor.getString(cursor.getColumnIndex("bookid"));
            author = cursor.getString(cursor.getColumnIndex("author"));
            bookfrom = cursor.getInt(cursor.getColumnIndex("bookfrom"));
            coverurl = cursor.getString(cursor.getColumnIndex("coverurl"));
            format = cursor.getInt(cursor.getColumnIndex("format"));
            isdefautbook = cursor.getInt(cursor.getColumnIndex("isdefautbook"));
            bookname = cursor.getString(cursor.getColumnIndex("bookname"));
            time = cursor.getString(cursor.getColumnIndex("time"));
            isAddBook = cursor.getInt(cursor.getColumnIndex("isAddBook"));
            payStatus = cursor.getInt(cursor.getColumnIndex("payStatus"));
            confirmStatus = cursor.getInt(cursor.getColumnIndex("confirmStatus"));
            // 未初始化过的确认订购状态，使用购买状态标记。
            if (0 == confirmStatus) {
                confirmStatus = payStatus;
            }
            bookstatus = cursor.getInt(cursor.getColumnIndex("bookstatus"));
            currentCatalogId = cursor.getString(cursor.getColumnIndex("currentCatalogId"));
            readerFrom = cursor.getString(cursor.getColumnIndex("readerFrom"));
            price = cursor.getString(cursor.getColumnIndex("price"));
            isEnd = cursor.getInt(cursor.getColumnIndex("isEnd"));
            isUpdate = cursor.getInt(cursor.getColumnIndex("isUpdate"));
            hasRead = cursor.getInt(cursor.getColumnIndex("hasRead"));
            control = cursor.getInt(cursor.getColumnIndex("control"));
            isFree = cursor.getInt(cursor.getColumnIndex("isFree"));
            payRemind = cursor.getInt(cursor.getColumnIndex("payRemind"));

            sourceFrom = cursor.getString(cursor.getColumnIndex("sourceFrom"));
            isSupportH = cursor.getInt(cursor.getColumnIndex("isSupportH"));
            if (isSupportH == 0) {
                isSupportH = 1;
            }


        } catch (IllegalStateException e) {
            try {
                IssDbFactory.getInstance().updateTable(this.getClass());
            } catch (Exception ignored) {
            }
        }

        return this;
    }

    @Override
    public ContentValues beanToValues() {
        ContentValues values = new ContentValues();
        putContentValue(values, "bookid", bookid);
        putContentValue(values, "author", author);
        putContentValue(values, "bookfrom", bookfrom);
        putContentValue(values, "coverurl", coverurl);
        putContentValue(values, "format", format);
        // putContentValue(values,"uri", uri);
        putContentValue(values, "isdefautbook", isdefautbook);
        putContentValue(values, "bookname", bookname);
        putContentValue(values, "time", time);
        putContentValue(values, "isAddBook", isAddBook);
        putContentValue(values, "payStatus", payStatus);
        putContentValue(values, "confirmStatus", confirmStatus);
        putContentValue(values, "bookstatus", bookstatus);
        putContentValue(values, "currentCatalogId", currentCatalogId);
        putContentValue(values, "price", price);
        putContentValue(values, "isEnd", isEnd);
        putContentValue(values, "isUpdate", isUpdate);
        putContentValue(values, "hasRead", hasRead);
        putContentValue(values, "control", control);
        putContentValue(values, "isFree", isFree);
        putContentValue(values, "payRemind", payRemind);
        putContentValue(values, "sourceFrom", sourceFrom);
        putContentValue(values, "readerFrom", readerFrom);

        putContentValue(values, "isSupportH", isSupportH, 0);
        return values;
    }

}

package com.dzbook.view.comment;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dzbook.activity.comment.BookCommentItemDetailActivity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.NetworkUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import hw.sdk.net.bean.bookDetail.BeanCommentAction;
import hw.sdk.net.bean.bookDetail.BeanCommentInfo;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * CommentBaseView
 *
 * @author caimantang on 2018/4/29.
 */

public class CommentBaseView extends FrameLayout {
    /**
     * 评论详情页面 评论item
     */
    public static final int TYPE_ITEM_BOOKDETAIL = 1;
    /**
     * 更多评论页面 item
     */
    public static final int TYPE_ITEM_MORE_COMMENT = 2;
    /**
     * 个人中心 我的评论item
     */
    public static final int TYPE_ITEM_PERSON_CENTER = 3;
    /**
     * 评论详情页面 评论item 详情
     */

    public static final int TYPE_ITEM_COMMENT_DETAIL = 4;
    /**
     * 个人中心 我的评论item详情
     */
    public static final int TYPE_ITEM_MY_COMMENT_DETAIL = 5;

    /**
     * 取消点赞
     */
    public static final int TYPE_PRAISE = 1;
    /**
     * 点赞
     */
    public static final int TYPE_CANCLE_PRAISE = 2;
    /**
     * 删除
     */
    public static final int TYPE_DELETE = 3;
    protected BeanCommentInfo info;
    long[] mHits = new long[2];
    private CommentPopWindow popWindow;
    private String from;

    /**
     * 构造
     *
     * @param context context
     */
    public CommentBaseView(@NonNull Context context) {
        super(context);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CommentBaseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 去详情页
     *
     * @param bookId   bookId
     * @param bookName bookName
     */
    protected void toBookDetail(String bookId, String bookName) {
        if (TextUtils.isEmpty(bookId)) {
            ToastAlone.showShort(R.string.download_chapter_error);
            return;
        }
        BookDetailActivity.launch(getContext(), bookId, bookName);
    }

    /**
     * 处理结果
     *
     * @param value   value
     * @param what    what
     * @param info    info
     * @param context context
     */
    public static void dealResultSuccess(BeanCommentAction value, int what, BeanCommentInfo info, Context context) {
        if (value.status == 1) {
            String msg = "提交成功";
            switch (what) {
                case TYPE_PRAISE:
                    msg = "已点赞";
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("commentInfo", info);
                    EventBusUtils.sendMessage(EventConstant.CODE_PARISE_BOOK_COMMENT, EventConstant.TYPE_BOOK_COMMENT, bundle);
                    break;
                case TYPE_CANCLE_PRAISE:
                    msg = "已取消点赞";
                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable("commentInfo", info);
                    EventBusUtils.sendMessage(EventConstant.CODE_PARISE_BOOK_COMMENT, EventConstant.TYPE_BOOK_COMMENT, bundle2);
                    break;
                case TYPE_DELETE:
                    msg = "已删除";
                    if (context instanceof BookCommentItemDetailActivity) {
                        ((BookCommentItemDetailActivity) context).finish();
                    }
                    Bundle mBundle = new Bundle();
                    mBundle.putString("comment_id", info.commentId);

                    String type = EventConstant.TYPE_BOOK_COMMENT;
                    EventBusUtils.sendMessage(EventConstant.CODE_DELETE_BOOK_COMMENT, type, mBundle);
                    break;
                default:
                    break;
            }
            //            ToastAlone.showShort(msg);
        } else {
            ToastAlone.showShort(value.tip);
        }
    }

    /**
     * 点赞
     *
     * @param textView     textView
     * @param imageView    imageView
     * @param animationSet animationSet
     */
    protected void clickLike(TextView textView, ImageView imageView, AnimationSet animationSet) {
        if (null == info) {
            return;
        }
        if (!NetworkUtils.getInstance().checkNet()) {

            if (getContext() instanceof BaseActivity) {
                ((BaseActivity) getContext()).showNotNetDialog();
            }
            return;
        }
        //1秒内不能再点击
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[1] < (mHits[0] + 1000)) {
            ToastAlone.showShort(R.string.comment_fail_quikly);
            return;
        }
        info.praiseNum = (!info.praise) ? (info.praiseNum + 1) : (info.praiseNum - 1);
        int oldNum = info.praiseNum;
        info.praise = !info.praise;
        oldNum = (oldNum < 0) ? 0 : oldNum;
        String text = "";
        if (oldNum > 10000) {
            text = oldNum / 10000 + "万+";
        } else if (oldNum == 10000) {
            text = oldNum / 10000 + "万";
        } else {
            text = oldNum + "";
        }
        setText(textView, text);
        if (null != imageView && null != animationSet) {
            imageView.startAnimation(animationSet);
        }
    }

    /**
     * 设置书名
     *
     * @return String
     */
    public String getBookName() {
        if (null != info) {
            return info.bookName;
        }
        return "";
    }

    /**
     * 点击更多
     *
     * @param imageView imageView
     */
    protected void clickMore(ImageView imageView) {
        if (null != imageView) {
            popWindow = new CommentPopWindow(getContext(), info, this, from);
            int[] size = new int[2];
            imageView.getLocationInWindow(size);
            popWindow.showAtLocation(imageView, Gravity.TOP | Gravity.START, size[0] - DimensionPixelUtil.dip2px(getContext(), 10), size[1] + DimensionPixelUtil.dip2px(getContext(), 20));
        }
    }

    /**
     * 获取星数
     *
     * @param info1 info1
     * @return float
     */
    protected float getStarNum(BeanCommentInfo info1) {
        float starNum = info1.score;
        if (starNum > 10) {
            starNum = 10;
        } else if (starNum < 0) {
            starNum = 0;
        }
        return starNum;
    }

    public String getFrom() {
        return from;
    }

    /**
     * 打点使用
     *
     * @param from from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * setText
     * @param tv tv
     * @param text text
     */
    protected void setText(TextView tv, String text) {
        if (tv != null && !TextUtils.isEmpty(text)) {
            tv.setText(text);
        }
    }

    /**
     * setVisibility
     * @param view view
     * @param visibility visibility
     */
    protected void setVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    /**
     * initLikeNum
     * @param textView textView
     */
    protected void initLikeNum(TextView textView) {
        if (null != info) {
            int pariseNum = info.praiseNum;
            if (pariseNum / 10000 >= 1 && pariseNum % 10000 > 0) {
                setText(textView, pariseNum / 10000 + "万+");
            } else if (pariseNum / 10000 >= 1) {
                setText(textView, pariseNum / 10000 + "万");
            } else {
                setText(textView, pariseNum + "");
            }
        }
    }

    /**
     * setcover
     * @param imageView imageView
     * @param url url
     * @param resId resId
     */
    protected void setcover(ImageView imageView, String url, int resId) {
        if (imageView != null) {
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), imageView, url, resId);
        }
    }

    /**
     * setBackgroundResource
     * @param imageviewLike imageviewLike
     * @param resId resId
     */
    protected void setBackgroundResource(ImageView imageviewLike, int resId) {
        if (null != imageviewLike) {
            imageviewLike.setBackgroundResource(resId);
        }
    }

    /**
     * 设置点赞
     *
     * @param imageviewLike imageviewLike
     * @param praise        praise
     */
    protected void setPraised(ImageView imageviewLike, boolean praise) {
        setBackgroundResource(imageviewLike, praise ? R.drawable.hw_ic_comment_praise : R.drawable.hw_ic_comment_praise_cancel);
    }

    /**
     * 设置行标
     *
     * @param ratingbar ratingbar
     * @param meg       meg
     */
    protected void setStar(CommentRatingBarView ratingbar, float meg) {
        if (null != ratingbar) {
            ratingbar.setStar(meg);
        }
    }

    /**
     * 操作评论
     *
     * @param context context
     * @param info    info
     * @param type    type
     */
    public static void operationComment(final Context context, final BeanCommentInfo info, final int type) {
        if (info == null) {
            ToastAlone.showShort(R.string.toast_comment_error);
            return;
        }
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {

                try {
                    final BeanCommentAction beanCommentAction = HwRequestLib.getInstance().commentActionRequest(type, info.bookId, info.commentId);
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            if (beanCommentAction != null) {
                                if (beanCommentAction.isSuccess()) {
                                    dealResultSuccess(beanCommentAction, type, info, context);
                                } else {
                                    ToastAlone.showShort(R.string.comment_commit_error);
                                }
                            } else {
                                ToastAlone.showShort(R.string.comment_commit_error);
                            }
                        }
                    });
                } catch (Exception e) {
                    if (context instanceof BaseActivity) {
                        ((BaseActivity) context).showNotNetDialog();
                    }
                    e.printStackTrace();
                }

            }
        });

    }
}

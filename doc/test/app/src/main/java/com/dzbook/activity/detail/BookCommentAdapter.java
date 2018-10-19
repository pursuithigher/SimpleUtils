package com.dzbook.activity.detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.utils.ALog;
import com.dzbook.utils.ListUtils;
import com.dzbook.view.comment.CommentItemView;

import java.util.ArrayList;

import hw.sdk.net.bean.bookDetail.BeanCommentInfo;

/**
 * 图书评论
 *
 * @author Winzows on 2017/11/27.
 */
public class BookCommentAdapter extends RecyclerView.Adapter<BookCommentAdapter.ViewHolder> {
    /**
     * 加载模式-刷新
     */
    public static final int LOAD_TYPE_REFRESH = 1;
    /**
     * 加载模式-默认
     */
    public static final int LOAD_TYPE_DEFAULT = 2;
    /**
     * 加载模式-加载更多
     */
    public static final int LOAD_TYPE_LOADMORE = 3;
    private Context mContext;
    private ArrayList<BeanCommentInfo> dataList;
    private String titleName, from;
    //页面类型
    private int pageType = -1;

    private OnItemClickListener clickListener;

    /**
     * 构造
     *
     * @param mContext mContext
     * @param pageType pageType
     * @param from     from
     */
    public BookCommentAdapter(Context mContext, int pageType, String from) {
        this.mContext = mContext;
        dataList = new ArrayList<>();
        this.from = from;
        this.pageType = pageType;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new CommentItemView(mContext), from, pageType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (!ListUtils.isEmpty(dataList)) {
            BeanCommentInfo commentInfo = dataList.get(position);
            if (TextUtils.isEmpty(commentInfo.bookName) && !TextUtils.isEmpty(titleName)) {
                commentInfo.bookName = titleName;
            }
            holder.commentView.bindData(pageType, commentInfo, position == dataList.size() - 1);
            holder.commentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onClick(dataList.get(position));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 填充数据
     *
     * @param beanList list
     * @param dataType type
     */
    public void fillData(ArrayList<BeanCommentInfo> beanList, int dataType) {
        switch (dataType) {
            case LOAD_TYPE_LOADMORE:
                ArrayList<BeanCommentInfo> newList = new ArrayList<>();
                if (beanList != null && beanList.size() > 0 && dataList != null && dataList.size() > 0) {
                    newList.addAll(dataList);//先拷贝出一份来

                    long s1 = System.currentTimeMillis();
                    for (int i = 0; i < dataList.size(); i++) {
                        BeanCommentInfo oldInfo = dataList.get(i);
                        for (int j = 0; j < beanList.size(); j++) {
                            BeanCommentInfo newInfo = beanList.get(j);
                            if (newInfo.hashCode() == oldInfo.hashCode()) {
                                ALog.iWz("BookCommentAdapter remove  -----" + oldInfo);
                                newList.remove(oldInfo);
                            }
                        }
                    }
                    ALog.dWz("BookCommentAdapter fillData use time =  ", (System.currentTimeMillis() - s1) + "");
                }

                if (dataList != null && beanList != null && beanList.size() > 0 && newList.size() > 0) {
                    dataList.clear();
                    dataList.addAll(newList);
                    dataList.addAll(beanList);
                }

                break;

            case LOAD_TYPE_REFRESH:
            case LOAD_TYPE_DEFAULT:
            default:
                dataList.clear();
                dataList.addAll(beanList);
                break;
        }
        notifyDataSetChanged();
    }

    /**
     * 删除评论
     *
     * @param commentID id
     */
    public void deleteItemByCommentId(String commentID) {
        if (!TextUtils.isEmpty(commentID) && !ListUtils.isEmpty(dataList)) {
            for (int i = 0; i < dataList.size(); i++) {
                if (TextUtils.equals(dataList.get(i).commentId, commentID)) {
                    dataList.remove(i);
                    notifyDataSetChanged();
                    if (ListUtils.isEmpty(dataList)) {
                        //书籍为空了 提醒外面刷新。
                        String tag = (mContext instanceof BookDetailActivity) ? EventConstant.TYPE_BOOK_DETAIL : EventConstant.TYPE_BOOK_COMMENT;
                        EventBusUtils.sendMessage(EventConstant.CODE_DELETE_BOOK_IS_EMPTY, tag, null);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 刷新评论
     *
     * @param beanCommentInfo beanCommentInfo
     */
    public void refreshComment(BeanCommentInfo beanCommentInfo) {
        ALog.cmtDebug("refreshComment:" + (null != beanCommentInfo ? beanCommentInfo.praise : "null"));
        if (null == beanCommentInfo || ListUtils.isEmpty(dataList)) {
            return;
        }
        for (int i = 0; i < dataList.size(); i++) {
            BeanCommentInfo info = dataList.get(i);
            if (!TextUtils.isEmpty(info.commentId) && info.commentId.equals(beanCommentInfo.commentId)) {
                dataList.set(i, beanCommentInfo);
                notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * holder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        CommentItemView commentView;

        ViewHolder(View itemView, String from, int pageType) {
            super(itemView);
            commentView = (CommentItemView) itemView;
            //打点
            commentView.setFrom(from);
            commentView.bindView(pageType);
        }
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }


    /**
     * item 点击监听
     */
    public interface OnItemClickListener {
        /**
         * 点击回调
         *
         * @param info info
         */
        void onClick(BeanCommentInfo info);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }
}

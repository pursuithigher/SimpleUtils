package com.dzbook.view.bookdetail;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.activity.comment.BookCommentSendActivity;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.presenter.BookCommentSendPresenter;
import com.dzbook.utils.GlideImageLoadUtils;
import com.ishugui.R;

import java.util.HashMap;

/**
 * CommentEmpty
 * 空评论
 *
 * @author caimantang on 2018/5/7.
 */

public class CommentEmpty extends LinearLayout implements View.OnClickListener {
    private String bookName, bookId;
    private TextView tvBookname, tvAuthor;
    private ImageView ivCover;

    private BookCommentSendPresenter sendPresenter;

    /**
     * 构造
     *
     * @param context context
     */
    public CommentEmpty(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CommentEmpty(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public void setSendPresenter(BookCommentSendPresenter sendPresenter) {
        this.sendPresenter = sendPresenter;
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_empty_comment, this, true);
        ivCover = findViewById(R.id.iv_cover);
        tvBookname = findViewById(R.id.tv_bookName);
        tvAuthor = findViewById(R.id.tv_author);
        View tvSendcomment = findViewById(R.id.tv_sendComment);
        tvSendcomment.setOnClickListener(this);
    }

    /**
     * 设置书籍信息
     *
     * @param bookCover  bookCover
     * @param bookName1  bookName1
     * @param bookAuthor bookAuthor
     * @param bookId1    bookId1
     */
    public void setBookInfo(String bookCover, String bookName1, String bookAuthor, String bookId1) {
        this.bookName = bookName1;
        this.bookId = bookId1;
        if (!TextUtils.isEmpty(bookCover)) {
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), ivCover, bookCover, 0);
        }
        if (!TextUtils.isEmpty(bookName1)) {
            tvBookname.setText(bookName1);
        }
        if (!TextUtils.isEmpty(bookAuthor)) {
            tvAuthor.setText(bookAuthor);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sendComment:
                sendPresenter.checkCommentStatus((Activity) getContext(), bookId, bookName, BookCommentSendActivity.TYPE_SEND);
                HashMap<String, String> map = new HashMap<>();
                map.put(LogConstants.KEY_RECHARGE_BID, bookId + "");
                map.put("book_name", bookName + "");
                map.put("type", "1");
                DzLog.getInstance().logClick(LogConstants.MODULE_QBPL, LogConstants.ZONE_FSPL, "", map, "");
                break;
            default:
                break;

        }
    }
}

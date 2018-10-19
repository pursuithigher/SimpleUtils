package com.dzbook.activity.comment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dzbook.BaseTransparencyLoadActivity;
import com.dzbook.DzConstant;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.ElasticScrollView;
import com.dzbook.view.comment.CommentBaseView;
import com.dzbook.view.comment.CommentItemView;
import com.ishugui.R;

import hw.sdk.net.bean.bookDetail.BeanCommentInfo;

/**
 * 评论详情页  编辑页
 *
 * @author Winzows on 2017/12/5.
 */
public class BookCommentItemDetailActivity extends BaseTransparencyLoadActivity {
    /**
     * tag_comment_bean
     */
    public static final String TAG_COMMENT_INFO = "tag_comment_bean";
    /**
     * tag
     */
    public static final String TAG = "BookCommentItemDetailActivity";
    private CommentItemView commentView;

    private DianZhongCommonTitle mTitleView;

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_item_detail);
    }

    @Override
    protected void initView() {
        ElasticScrollView scrollView = findViewById(R.id.scroll_view);
        mTitleView = findViewById(R.id.commontitle);
        commentView = new CommentItemView(this);
        scrollView.addView(commentView);
    }

    @Override
    protected void initData() {
        Intent mGetIntent = getIntent();
        if (mGetIntent != null) {
            int pageType = mGetIntent.getIntExtra(DzConstant.PAGE_TYPE, -1);
            commentView.bindView(pageType);
            BeanCommentInfo commentInfo = (BeanCommentInfo) mGetIntent.getSerializableExtra(TAG_COMMENT_INFO);
            if (commentInfo != null) {
                commentView.bindData(pageType, commentInfo, true);
            }
            if (pageType == CommentBaseView.TYPE_ITEM_COMMENT_DETAIL) {
                mTitleView.setTitle(getResources().getString(R.string.look_book_comment));
            }

        }
    }

    @Override
    protected void setListener() {
        mTitleView.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}

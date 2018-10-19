package com.dzbook.view.vip;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.utils.GlideImageLoadUtils;
import com.ishugui.R;

import hw.sdk.net.bean.store.TempletContant;
import hw.sdk.net.bean.vip.VipBookInfo;

/**
 * 书籍布局
 *
 * @author gavin
 */
public class SigleBooKVipView extends ConstraintLayout {

    private Context mContext;
    private long clickDelayTime = 0;
    private ImageView mImageView;
    private TextView mTextView;
    private TextView mTextViewAuthor;
    private VipBookInfo.BookBean bean;

    /**
     * 构造
     *
     * @param context context
     */
    public SigleBooKVipView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public SigleBooKVipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long current = System.currentTimeMillis();
                if (current - clickDelayTime > TempletContant.CLICK_DISTANSE) {
                    clickDelayTime = current;
                    BookDetailActivity.launch(mContext, bean.id, "");
                }
            }
        });
    }

    private void initData() {

    }

    private void initView() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        LayoutInflater.from(getContext()).inflate(R.layout.view_siglebookviewv, this);
        mImageView = findViewById(R.id.imageview_book);
        mTextView = findViewById(R.id.textview_title);
        mTextViewAuthor = findViewById(R.id.textview_author);
    }

    /**
     * 绑定数据
     *
     * @param bean1 bean
     */
    public void bindData(VipBookInfo.BookBean bean1) {
        this.bean = bean1;
        mTextView.setText(bean1.title);
        mTextViewAuthor.setText(bean1.author);
        String imgUrl = bean1.imgUrl;
        if (!TextUtils.isEmpty(imgUrl)) {
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(mContext, mImageView, imgUrl, 0);
        }
    }

}

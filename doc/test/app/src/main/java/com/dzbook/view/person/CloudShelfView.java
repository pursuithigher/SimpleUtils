package com.dzbook.view.person;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.mvp.presenter.PersonCloudShelfPresenter;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import hw.sdk.net.bean.BeanBookInfo;

/**
 * 云书架
 *
 * @author dongdianzhou on 2017/11/20.
 */

public class CloudShelfView extends RelativeLayout {

    private ImageView mImageView;
    private TextView mTextViewName;
    private TextView mTextViewChapter;
    private TextView textViewOper;

    private BookInfo mBookInfo;
    private BeanBookInfo mBean;
    private long lastClickTime;

    private Context mContext;

    private PersonCloudShelfPresenter mPresenter;
    private View vLine;

    /**
     * 构造
     *
     * @param context context
     */
    public CloudShelfView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CloudShelfView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        setBackgroundResource(R.drawable.selector_hw_list_item);
        initData();
        setListener();
    }

    public void setPersonCloudShelfPresenter(PersonCloudShelfPresenter mPresenter1) {
        this.mPresenter = mPresenter1;
    }

    private void setListener() {
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPresenter.deleteItems(mBean);
                return true;
            }
        });
        textViewOper.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentClickTime = System.currentTimeMillis();
                if (currentClickTime - lastClickTime > 1000) {
                    if (mBookInfo != null && mBookInfo.isAddBook == 2) {
                        mPresenter.continueReadBook(mBookInfo);
                    } else {
                        mPresenter.addBookShelf(mBean.bookId);
                    }
                }
                lastClickTime = currentClickTime;
            }
        });
    }

    private void initData() {

    }

    private void initView() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_cloudshelf, this);
        mImageView = rootView.findViewById(R.id.iv_book_icon);
        mTextViewName = rootView.findViewById(R.id.tv_book_name);
        mTextViewChapter = rootView.findViewById(R.id.tv_book_chapter_profile);
        textViewOper = rootView.findViewById(R.id.bt_operate);
        TypefaceUtils.setHwChineseMediumFonts(textViewOper);
        vLine = rootView.findViewById(R.id.view_line);


    }

    /**
     * 绑定数据
     *
     * @param bean          bean
     * @param isShowEndLine isShowEndLine
     */
    public void bindData(BeanBookInfo bean, boolean isShowEndLine) {
        mBean = bean;
        GlideImageLoadUtils.getInstanse().glideImageLoadFromUrlDefaultBookRes(getContext(), mImageView, bean.coverWap);
        mTextViewName.setText(bean.bookName);
        if (bean.isDeleteOrUndercarriage()) {
            textViewOper.setClickable(false);
            textViewOper.setText(mContext.getString(R.string.str_book_detail_menu_yxz));
        } else {
            textViewOper.setClickable(true);
            mBookInfo = DBUtils.findByBookId(getContext(), bean.bookId);
            if (mBookInfo != null && mBookInfo.isAddBook == 2) {
                textViewOper.setText(mContext.getString(R.string.str_open_to_continue_read));
            } else {
                textViewOper.setText(mContext.getString(R.string.str_book_detail_menu_jrsj));
            }
        }
        if (!TextUtils.isEmpty(bean.introduction)) {
            mTextViewChapter.setText(bean.introduction);
        }

        if (null != vLine) {
            if (!isShowEndLine) {
                vLine.setVisibility(View.VISIBLE);
            } else {
                vLine.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 清除图片
     */
    public void clearImageView() {
        if (mImageView != null) {
            Glide.with(getContext()).clear(mImageView);
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrlDefaultBookRes(getContext(), mImageView, null);
        }
    }
}

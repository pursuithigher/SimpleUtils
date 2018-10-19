package com.dzbook.view.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.utils.ViewUtils;
import com.ishugui.R;

import hw.sdk.net.bean.BeanBookInfo;

/**
 * BookListItemView
 *
 * @author lizhongzhong 2018/3/9.
 */

public class BookListItemView extends RelativeLayout {
    /**
     * 排行榜
     */
    public static final int RANK_TOP = 0x001;
    /**
     * 详情
     */
    public static final int TYPE_DETAIL = 0x002;

    int size = 2;
    private BookImageView ivBookIcon;
    private View viewLine;

    private TextView tvBookName, tvBookAuthor, tvBookContent, tvOrderName;
    private int showType = -1;


    /**
     * 构造
     *
     * @param context  context
     * @param showType showType
     */
    public BookListItemView(Context context, int showType) {
        this(context, null);
        this.showType = showType;
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public BookListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
        initData();
        setListener();
    }

    private void initView(AttributeSet attrs) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 152));
        setLayoutParams(params);
        setBackgroundResource(R.drawable.selector_hw_list_item);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_bookstoretop2, this, true);
        ivBookIcon = view.findViewById(R.id.ivBookIcon);
        tvBookName = view.findViewById(R.id.tvBookName);
        tvBookAuthor = view.findViewById(R.id.tvAuthorName);
        tvBookContent = view.findViewById(R.id.tvBookContent);
        viewLine = view.findViewById(R.id.view_line);
        final float num1 = 1.1f;
        final float num2 = 1.2f;

        TypefaceUtils.setLineSpacingBylocale(getContext(), tvBookContent, num2, num1);
        tvOrderName = view.findViewById(R.id.tvOrderName);

        if (showType == TYPE_DETAIL) {
            tvOrderName.setVisibility(GONE);
        }
    }


    private void initData() {
        ivBookIcon.setImageResource(0);
        tvBookName.setText("");
        tvBookAuthor.setText("");
        tvBookContent.setText("");
        tvOrderName.setText("");
        tvOrderName.setVisibility(View.GONE);
    }

    private void setListener() {
    }


    /**
     * 绑定数据
     *
     * @param bean            bean
     * @param position        position
     * @param isNeedOrderText isNeedOrderText
     * @param itemCount       itemCount
     */
    public void bindData(BeanBookInfo bean, int position, boolean isNeedOrderText, int itemCount) {
        bindData("", bean, position, isNeedOrderText, itemCount);
    }

    /**
     * 绑定数据
     *
     * @param searchKey       searchKey
     * @param bean            bean
     * @param position        position
     * @param isNeedOrderText isNeedOrderText
     * @param itemCount       itemCount
     */
    @SuppressLint("SetTextI18n")
    public void bindData(String searchKey, BeanBookInfo bean, int position, boolean isNeedOrderText, int itemCount) {
        if (bean != null) {
            if (!TextUtils.isEmpty(bean.coverWap)) {
                GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), ivBookIcon, bean.coverWap, 0);
            }
            tvBookAuthor.setText(bean.author);
            tvBookName.setText(bean.bookName);
            tvBookContent.setText(bean.introduction);

            if (!TextUtils.isEmpty(searchKey)) {
                ViewUtils.highlightTextSearch(getContext(), tvBookAuthor, searchKey);
                ViewUtils.highlightTextSearch(getContext(), tvBookName, searchKey);
            }

            tvOrderName.setText((position + 1) + "");
            if (itemCount > 0 && position == (itemCount - 1)) {
                viewLine.setVisibility(GONE);
            } else {
                viewLine.setVisibility(VISIBLE);
            }

            if (isNeedOrderText) {
                if (position <= size) {
                    int color = CompatUtils.getColor(getContext(), R.color.color_ee3333);
                    tvOrderName.setTextColor(color);
                } else {
                    int color = CompatUtils.getColor(getContext(), R.color.color_b5b5b5);
                    tvOrderName.setTextColor(color);
                }
                tvOrderName.setVisibility(View.VISIBLE);
            }
        }

    }

    /**
     * 清除图片
     */
    public void clearImageView() {
        if (ivBookIcon != null) {
            Glide.with(getContext()).clear(ivBookIcon);
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), ivBookIcon, null, 0);
        }
    }
}

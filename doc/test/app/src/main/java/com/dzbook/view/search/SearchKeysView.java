package com.dzbook.view.search;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.utils.ViewUtils;
import com.dzbook.view.common.BookImageView;
import com.ishugui.R;

import hw.sdk.net.bean.seach.SuggestItem;

/**
 * 搜索关键词
 *
 * @author dongdianzhou on 2017/3/29.
 */

public class SearchKeysView extends LinearLayout {
    /**
     * 阅读
     */
    public static final int TO_READ = 1;
    /**
     * 详情
     */
    public static final int TO_BOOKDETAIL = 2;
    /**
     * 搜索
     */
    public static final int TO_SEARCH_NO_NEXT = 3;
    /**
     * 联想词
     */
    public static final int TO_SEARCH_EXIST_NEXT = 4;

    /**
     * 点击类型
     */
    public int clickType = 0;
    private Context mContext;

    private TextView mTextViewTitle;
    private TextView mTextViewRead;
    private ImageView imageViewMark;
    private TextView icTip;
    private BookImageView imageViewNook;
    private View viewLine;

    /**
     * 构造
     *
     * @param context context
     */
    public SearchKeysView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public SearchKeysView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_searchkeys, this);
        mTextViewTitle = view.findViewById(R.id.textview_title);
        mTextViewRead = view.findViewById(R.id.textview_read);
        icTip = view.findViewById(R.id.ic_tip);
        imageViewMark = view.findViewById(R.id.imageview_mark);
        imageViewNook = view.findViewById(R.id.imageview_book);
        viewLine = view.findViewById(R.id.view_line);

        TypefaceUtils.setHwChineseMediumFonts(mTextViewRead);
    }

    private void resetImageview(boolean isBook) {
        if (isBook) {
            imageViewNook.setVisibility(VISIBLE);

            imageViewMark.setVisibility(GONE);
        } else {
            imageViewNook.setVisibility(GONE);
            imageViewMark.setVisibility(VISIBLE);
        }
    }

    private void resetTip(boolean isShow, String type) {
        if (isShow && !TextUtils.isEmpty(type)) {
            icTip.setVisibility(VISIBLE);
            icTip.setText(type);
        } else {
            icTip.setVisibility(GONE);
        }
    }

    /**
     * 重置封面
     *
     * @param type     type
     * @param coverWap coverWap
     */
    public void resetCover(String type, String coverWap) {
        int defaultIcon = -10;
        ViewGroup.LayoutParams layoutParams = imageViewMark.getLayoutParams();
        if ("1".equals(type) || "4".equals(type)) {
            //书籍
            resetTip(false, "");
            resetImageview(true);
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrlDefaultBookRes(getContext(), imageViewNook, coverWap);
        } else if ("2".equals(type)) {
            //作者
            resetImageview(false);
            resetTip(true, getResources().getString(R.string.str_search_hint_author));
            imageViewMark.setBackground(null);
            layoutParams.height = (int) getResources().getDimension(R.dimen.hw_dp_40);
            layoutParams.width = (int) getResources().getDimension(R.dimen.hw_dp_40);
            defaultIcon = R.drawable.hw_avatar;
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), imageViewMark, coverWap, defaultIcon);
        } else if ("3".equals(type)) {
            //标签
            resetTip(true, getResources().getString(R.string.str_search_hint_tag));
            resetImageview(false);
            imageViewMark.setBackground(null);
            defaultIcon = R.drawable.hw_default_tag;
            layoutParams.height = (int) getResources().getDimension(R.dimen.hw_dp_32);
            layoutParams.width = (int) getResources().getDimension(R.dimen.hw_dp_32);
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), imageViewMark, coverWap, defaultIcon);

        } else if ("0".equals(type)) {
            //本地书
            resetTip(false, "");
            resetImageview(true);
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrlDefaultBookRes(getContext(), imageViewNook, coverWap);
        } else if ("".equals(type)) {
            resetTip(false, "");
            resetImageview(false);
            defaultIcon = R.drawable.hw_search_grey;
            imageViewMark.setBackground(null);
            layoutParams.height = (int) getResources().getDimension(R.dimen.hw_dp_19);
            layoutParams.width = (int) getResources().getDimension(R.dimen.hw_dp_19);
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), imageViewMark, coverWap, defaultIcon);
        }
        imageViewMark.setLayoutParams(layoutParams);
    }

    /**
     * 绑定数据
     *
     * @param highKey    highKey
     * @param bean       bean
     * @param isShowLine isShowLine
     */
    public void bindData(String highKey, Object bean, boolean isShowLine) {
        viewLine.setVisibility(isShowLine ? VISIBLE : GONE);
        if (bean instanceof BookInfo) {
            final BookInfo bookInfo = (BookInfo) bean;
            mTextViewTitle.setText(bookInfo.bookname);
            mTextViewRead.setText(getResources().getString(R.string.str_searchkeys_read));
            resetCover("0", bookInfo.coverurl);
            mTextViewRead.setVisibility(VISIBLE);
            clickType = TO_READ;
        } else if (bean instanceof SuggestItem) {
            mTextViewRead.setVisibility(GONE);
            final SuggestItem suggestItem = (SuggestItem) bean;
            String suggestItemType = suggestItem.type;
            String suggestItemTitle = suggestItem.title;
            String suggestItemCover = suggestItem.cover;
            String suggestItemAuthorName = suggestItem.authorName;
            String suggestItemBookId = suggestItem.bookId;
            if (!TextUtils.isEmpty(suggestItemTitle)) {
                mTextViewTitle.setText(suggestItemTitle);
            }
            resetCover(suggestItemType, suggestItemCover);
            if ("1".equals(suggestItemType) || "4".equals(suggestItemType)) {
                //书籍
                if (!TextUtils.isEmpty(suggestItemBookId)) {
                    clickType = TO_BOOKDETAIL;
                }
            } else if ("2".equals(suggestItemType) || "3".equals(suggestItemType)) {
                //作者
                clickType = TO_SEARCH_NO_NEXT;
            } else if ("".equals(suggestItemType)) {
                //联想词
                clickType = TO_SEARCH_EXIST_NEXT;
            }
        }

        ViewUtils.highlightTextSearch(getContext(), mTextViewTitle, highKey);
    }
}

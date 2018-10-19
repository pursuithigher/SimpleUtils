package com.dzbook.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.log.LogConstants;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.bookdetail.AuthorBookView;
import com.dzbook.view.bookdetail.RecommendBookView;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.BeanBookInfo;

/**
 * BookLinearLayout
 *
 * @author caimantang on 2018/4/18.
 */

public class BookLinearLayout extends LinearLayout {

    private TextView mTextViewLeft;
    private View mReMore;
    private boolean isUseMore = false;
    private int maxLine;
    private List<LinearLayout> lines;
    private String currentBookId;
    private int verticalNumb = 3;
    private int sameLength;

    /**
     * 构造
     *
     * @param context context
     */
    public BookLinearLayout(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public BookLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    public BookLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_book_linearlayout, this, true);
        mTextViewLeft = findViewById(R.id.textView_left);
        TypefaceUtils.setHwChineseMediumFonts(mTextViewLeft);
        mReMore = findViewById(R.id.re_more);
        if (null != attrs) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BookLinearLayoutView, 0, 0);
            if (null != typedArray) {
                isUseMore = typedArray.getBoolean(R.styleable.BookLinearLayoutView_useMore, false);
                String textLeft = typedArray.getString(R.styleable.BookLinearLayoutView_textLeft);
                verticalNumb = typedArray.getInt(R.styleable.BookLinearLayoutView_verticalNumb, 3);
                if (!TextUtils.isEmpty(textLeft)) {
                    mTextViewLeft.setText(textLeft);
                }
                typedArray.recycle();
            }
        }
    }

    /**
     * 更多
     *
     * @param isMore isUseMore
     */
    public void userMore(boolean isMore) {
        this.isUseMore = isMore;
    }

    /**
     * 左侧文本
     *
     * @param textLeft textLeft
     */
    public void setTextLeft(String textLeft) {
        mTextViewLeft.setText(textLeft);
    }

    /**
     * 绑定数据
     *
     * @param isMore        isMore
     * @param currentbookid currentbookid
     * @param list          list
     */
    public void bindData(boolean isMore, String currentbookid, List<BeanBookInfo> list) {
        isUseMore = isMore;
        this.currentBookId = currentbookid;
        if (ListUtils.isEmpty(list)) {
            return;
        }
        int size = list.size();
        switch (getOrientation()) {
            case VERTICAL:
                if (size >= verticalNumb) {
                    list = list.subList(0, verticalNumb);
                }
                break;
            case HORIZONTAL:
                //横向显示最多显示两行一行有三个
                maxLine = size / 3;
                if (maxLine == 0) {
                    setVisibility(GONE);
                    return;
                }
                lines = new ArrayList<>();
                list = list.subList(0, maxLine * 3);
                for (int i = 0; i < maxLine; i++) {
                    lines.add(createLinearLayout(HORIZONTAL));
                }

                break;
            default:
                break;
        }
        mReMore.setVisibility(isUseMore ? VISIBLE : GONE);
        for (int i = 0; i < list.size(); i++) {
            sameLength = list.size();
            bindDataItem(list.get(i), i);
        }
        if (getOrientation() == HORIZONTAL && maxLine > 0) {
            for (int i = 0; i < maxLine; i++) {
                addView(lines.get(i));
            }
            setOrientation(VERTICAL);
        }
    }

    private LinearLayout createLinearLayout(int orientation) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(orientation);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((int) getResources().getDimension(R.dimen.dp_16), 0, (int) getResources().getDimension(R.dimen.dp_16), 0);
        linearLayout.setLayoutParams(layoutParams);
        return linearLayout;
    }

    /**
     * 绑定item数据
     *
     * @param autOtherBook autOtherBook
     * @param position     position
     */
    public void bindDataItem(final BeanBookInfo autOtherBook, int position) {

        try {
            if (autOtherBook == null || TextUtils.isEmpty(autOtherBook.bookId)) {
                return;
            }
            int orientation = getOrientation();
            if (orientation == VERTICAL) {
                boolean goneLine = false;
                if (position == sameLength - 1) {
                    goneLine = true;
                }
                addVerticalBookView(autOtherBook, goneLine);
            } else if (orientation == HORIZONTAL) {
                addHorizontalBookView(lines.get((position < 3) ? 0 : 1), position, autOtherBook);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addVerticalBookView(BeanBookInfo autOtherBook, boolean isGoneLine) {
        AuthorBookView authorBookView = new AuthorBookView(getContext());
        authorBookView.bindData(LogConstants.MODULE_SJXQ, LogConstants.ZONE_SJXQ_QTSJ, currentBookId, autOtherBook);
        if (isGoneLine) {
            authorBookView.goneLine();
        }
        addView(authorBookView);
    }

    /**
     * 添加横向布局书本
     *
     * @param linearLayout linearLayout
     * @param position     position
     * @param beanBookInfo beanBookInfo
     */
    public void addHorizontalBookView(LinearLayout linearLayout, int position, final BeanBookInfo beanBookInfo) {
        int column = position % 3;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
        RecommendBookView bookView = new RecommendBookView(getContext());
        if (column == 0) {
            params.gravity = Gravity.START;
            bookView.setGravityBook(Gravity.LEFT);
        } else if (column == 2) {
            params.gravity = Gravity.RIGHT;
            bookView.setGravityBook(Gravity.RIGHT);
        } else {
            bookView.setGravityBook(Gravity.CENTER_HORIZONTAL);
        }
        bookView.bindData(LogConstants.MODULE_SJXQ, LogConstants.ZONE_SJXQ_HZK, currentBookId, beanBookInfo);
        linearLayout.addView(bookView, params);
    }

    /**
     * 点击更多
     *
     * @param onMoreClickListener onMoreClickListener
     */
    public void setOnMoreClickListener(final OnMoreClickListener onMoreClickListener) {
        if (null != onMoreClickListener && null != mReMore) {
            mReMore.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isUseMore) {
                        onMoreClickListener.onClick();
                    }
                }
            });
        }
    }

    /**
     * 点击更多接口
     */
    public interface OnMoreClickListener {
        /**
         * 点击接口
         */
        void onClick();
    }
}

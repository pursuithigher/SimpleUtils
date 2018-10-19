package com.dzbook.view.search;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

/**
 * 搜索标题栏
 *
 * @author caimantang on 2018/3/7.
 */

public class SearchTitleView extends LinearLayout {

    private TextView mTvTitle, mTvRight;

    /**
     * 构造
     *
     * @param context context
     */
    public SearchTitleView(Context context) {
        super(context);
        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_search_title, this);
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvRight = view.findViewById(R.id.tv_right);
        TypefaceUtils.setHwChineseMediumFonts(mTvTitle);
    }

    /**
     * 设置标题
     *
     * @param title title
     */
    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    /**
     * 设置右侧textview
     *
     * @param string string
     */
    public void setTvRight(String string) {
        mTvRight.setText(string);
    }

    /**
     * 设置标签
     *
     * @param title title
     */
    public void setTagTitle(String title) {
        //        String book_title = getResources().getString(R.string.string_search_tips);
        //        mTvTitle.setText(String.format(book_title, "\"" + getTitle(title) + "\""));
    }

    /**
     * setAuthorTitle
     *
     * @param title title
     */
    public void setAuthorTitle(String title) {
        //        String author_title = getResources().getString(R.string.string_search_author);
        //        mTvTitle.setText(String.format(author_title, "\"" + getTitle(title) + "\""));
    }

    /**
     * 设置推荐标题
     */
    public void setRecommendTitle() {
        mTvTitle.setText(getResources().getString(R.string.string_search_recommend));
    }

    /**
     * 移除标题
     */
    public void removeTitle() {
        mTvTitle.setText("");
    }

    /**
     * 设置书籍标题
     *
     * @param bookTitle bookTitle
     */
    public void setBookTitle(String bookTitle) {
        //        mTvTitle.setText(getResources().getString(R.string.string_book));
    }

    private String getTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return "";
        }

        if (title.length() > 5) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(title.substring(0, 5)).append("...");
        }
        return title;
    }
}

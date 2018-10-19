package com.dzbook.view.bookdetail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.activity.detail.BookDetailChapterActivity;
import com.dzbook.activity.search.SearchActivity;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.mvp.UI.BookDetailUI;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.tips.TipFlowLayout;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.util.List;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.BeanChapterInfo;

/**
 * DetailBookIntroView
 *
 * @author wxliao on 17/7/24.
 */
public class DetailBookIntroView extends LinearLayout implements View.OnClickListener {
    private TextView textviewChapternum;
    private ExpandTextView textviewBrief;
    private TipFlowLayout flowlayoutTips;
    /**
     * 最新章节bean
     */
    private BeanChapterInfo mLastChapter;
    private BeanBookInfo mBeanBookInfo;

    /**
     * 构造
     *
     * @param context context
     */
    public DetailBookIntroView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public DetailBookIntroView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.view_book_detail_book_intro, this, true);
        RelativeLayout layoutChapternum = findViewById(R.id.layout_chapterNum);
        textviewBrief = findViewById(R.id.textView_brief);
        textviewChapternum = findViewById(R.id.textView_chapterNum);
        flowlayoutTips = findViewById(R.id.flowlayout_tips);
        TextView tvBrief = findViewById(R.id.tv_brief_validity);
        TextView tvCatalog = findViewById(R.id.tv_catalog);
        layoutChapternum.setOnClickListener(this);

        TypefaceUtils.setHwChineseMediumFonts(tvBrief);
        TypefaceUtils.setRegularFonts(tvCatalog);
    }

    /**
     * 绑定数据
     *
     * @param beanBookInfo beanBookInfo
     * @param lastChapter  lastChapter
     * @param ui           ui
     */
    @SuppressLint("SetTextI18n")
    public void bindData(BeanBookInfo beanBookInfo, BeanChapterInfo lastChapter, BookDetailUI ui) {
        mBeanBookInfo = beanBookInfo;
        mLastChapter = lastChapter;
        flowlayoutTips.removeAllViews();
        final List<String> tagList = mBeanBookInfo.tagList;
        if (!ListUtils.isEmpty(tagList)) {
            flowlayoutTips.setVisibility(VISIBLE);
            for (int i = 0; i < tagList.size(); i++) {
                if (i >= 6) {
                    break;
                }
                final TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.view_book_detail_tip_textview, null);
                final String tag = tagList.get(i);
                textView.setText(tagList.get(i));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SearchActivity.toSearch(getContext(), tag, "3", true);
                    }
                });
                flowlayoutTips.addView(textView);
            }
        } else {
            flowlayoutTips.setVisibility(GONE);
        }

        String totalChapterStr = mBeanBookInfo.totalChapterNum;


        // 修改（@huzs）：如果有回车换行，预览简介显示则会有误。
        final String introduction = StringUtil.delSpaceAndLn(mBeanBookInfo.introduction);
        textviewBrief.setText(introduction);


        if (mLastChapter != null && mBeanBookInfo.status == 0) {
            textviewChapternum.setText(getContext().getString(R.string.dialog_the_lastest_chapter) + mLastChapter.chapterName);
        } else {
            if (!TextUtils.isEmpty(totalChapterStr)) {
                if (totalChapterStr.contains("章")) {
                    textviewChapternum.setText(getResources().getString(R.string.In_total) + " " + totalChapterStr);
                } else {
                    textviewChapternum.setText(getResources().getString(R.string.In_total) + " " + totalChapterStr + " " + getResources().getString(R.string.chapter));
                }
                textviewChapternum.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_introMore:
                ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.BOOK_DETAIL_UMENG_ID, ThirdPartyLog.BOOK_DETAIL_BRIEF_VALUE, 1);
                break;
            case R.id.layout_chapterNum:
                ThirdPartyLog.onEvent(getContext(), ThirdPartyLog.DTL_CATALOG);
                ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.BOOK_DETAIL_UMENG_ID, ThirdPartyLog.BOOK_DETAIL_CATALOG_VALUE, 1);
                Intent intent = new Intent(getContext(), BookDetailChapterActivity.class);
                if (mBeanBookInfo != null) {
                    intent.putExtra(BookDetailChapterActivity.BOOK_DETAIL_DATA, mBeanBookInfo);
                }
                getContext().startActivity(intent);
                BaseActivity.showActivity(getContext());
                break;
            default:
                break;
        }
    }
}

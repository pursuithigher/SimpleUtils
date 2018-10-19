package com.dzbook.view.bookdetail;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.AppConst;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.LogConstants;
import com.dzbook.utils.GlideImageLoadUtils;
import com.ishugui.R;

import hw.sdk.net.bean.BeanBookInfo;

/**
 * RecommendBookView
 *
 * @author wxliao on 17/5/25.
 */

public class RecommendBookView extends LinearLayout {
    ImageView imageviewCover;
    TextView textviewBookname;
    RelativeLayout reMain;
    //图书详情页跳转需要传当前图书详情页的bookid
    private String currentBookId;
    private BeanBookInfo mBook;
    private String module;
    private String zone;
    private long lastDetailTime = 0;

    /**
     * 构造
     *
     * @param context context
     */
    public RecommendBookView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public RecommendBookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_book_detail_recommend, this, true);
        textviewBookname = findViewById(R.id.textView_bookName);
        imageviewCover = findViewById(R.id.imageView_cover);
        reMain = findViewById(R.id.re_main);
        setClickable(true);
        setFocusable(true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });
        imageviewCover.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });
    }

    private void click() {
        ALog.cmtDebug("recommend");
        if (mBook == null || TextUtils.isEmpty(mBook.bookId)) {
            return;
        }

        final long thisTime = System.currentTimeMillis();
        if (thisTime - lastDetailTime > AppConst.MAX_CLICK_INTERVAL_TIME) {
            lastDetailTime = thisTime;
            BookDetailActivity.launch(getContext(), module, zone, currentBookId, mBook, mBook.bookName);

        }
    }

    private void toBookDetailActivity() {

    }

    /**
     * 绑定数据
     *
     * @param module1        module
     * @param zone1          zone
     * @param currentBookId1 currentBookId
     * @param book           book
     */
    public void bindData(@LogConstants.Module final String module1, @LogConstants.Zone String zone1, String currentBookId1, BeanBookInfo book) {
        mBook = book;
        this.currentBookId = currentBookId1;
        this.module = module1;
        this.zone = zone1;
        textviewBookname.setText(book.bookName);

        String imageUrl = book.coverWap;
        if (!TextUtils.isEmpty(imageUrl)) {
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrlDefaultBookResSkipMemoryCache((Activity) getContext(), imageviewCover, imageUrl);
        }
    }


    /**
     * 设置Gravity
     *
     * @param gravityBook gravityBook
     */
    public void setGravityBook(int gravityBook) {
        reMain.setGravity(gravityBook);
    }
}

package com.dzbook.view.bookdetail;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.AppConst;
import com.dzbook.BaseLoadActivity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.listener.AddBookListener;
import com.dzbook.log.LogConstants;
import com.dzbook.model.UserGrow;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.web.ActionEngine;
import com.ishugui.R;

import hw.sdk.net.bean.BeanBookInfo;

/**
 * 作者的其他书
 *
 * @author wxliao on 17/5/25.
 */
public class AuthorBookView extends LinearLayout implements View.OnClickListener {
    //图书详情页跳转需要传当前图书详情页的bookid
    private String mCurrentBookId;
    private TextView mTextViewDesc, mTvAddShelf;
    private ImageView mImageViewCover;
    private TextView mTextViewBookName;
    private BookInfo mBookInfo;
    private View mBookLine;
    private long lastClickTime;
    private BeanBookInfo mBook;
    private String mModule;
    private String mZone;
    private long lastDetailTime = 0;

    /**
     * 构造
     *
     * @param context context
     */
    public AuthorBookView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public AuthorBookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(final Context context) {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        LayoutInflater.from(context).inflate(R.layout.view_book_linearlayout_vertical_item, this, true);
        mBookLine = findViewById(R.id.book_line);
        mTextViewDesc = findViewById(R.id.textViewDesc);
        mImageViewCover = findViewById(R.id.imageViewCover);
        mTvAddShelf = findViewById(R.id.tv_add_shelf);
        TypefaceUtils.setHwChineseMediumFonts(mTvAddShelf);
        mTextViewBookName = findViewById(R.id.textViewBookName);
        mTvAddShelf.setOnClickListener(this);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBook == null || TextUtils.isEmpty(mBook.bookId)) {
                    return;
                }
                final long thisTime = System.currentTimeMillis();
                if (thisTime - lastDetailTime > AppConst.MAX_CLICK_INTERVAL_TIME) {
                    lastDetailTime = thisTime;
                    BookDetailActivity.launch(getContext(), mModule, mZone, mCurrentBookId, mBook, mBook.bookName);

                }
            }
        });
    }

    /**
     * 绑定数据
     *
     * @param module        module
     * @param zone          zone
     * @param currentBookId currentBookId
     * @param book          book
     */
    public void bindData(@LogConstants.Module final String module, @LogConstants.Zone String zone, String currentBookId, BeanBookInfo book) {
        mBook = book;
        this.mCurrentBookId = currentBookId;
        this.mModule = module;
        this.mZone = zone;
        setTextView(mTextViewBookName, book.bookName);
        setTextView(mTextViewDesc, book.introduction);
        String imageUrl = book.coverWap;
        if (!TextUtils.isEmpty(imageUrl)) {
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrlDefaultBookRes(getContext(), mImageViewCover, imageUrl);
        }
        setTvAddShelf();
    }

    private boolean isAddBookShelf() {
        return mBookInfo != null && mBookInfo.isAddBook == 2;
    }

    private void setTextView(TextView textView, String text) {
        if (null != textView && !TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
    }

    private void setTvAddShelf() {
        if (null != mBook && !TextUtils.isEmpty(mBook.bookId)) {
            mBookInfo = DBUtils.findByBookId(getContext(), mBook.bookId);
            if (isAddBookShelf()) {
                mTvAddShelf.setText(getResources().getString(R.string.keep_read));
            } else {
                mTvAddShelf.setText(getResources().getString(R.string.add_book_shelf));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_shelf:
                if (mBook == null || TextUtils.isEmpty(mBook.bookId)) {
                    return;
                }
                long currentClickTime = System.currentTimeMillis();
                if (currentClickTime - lastClickTime > 1000) {

                    if (isAddBookShelf()) {
                        //继续阅读
                        if (getContext() instanceof BaseLoadActivity) {
                            BookInfo bookInfo = DBUtils.findByBookId(getContext(), mBook.bookId);
                            if (null != bookInfo) {
                                ReaderUtils.continueReadBook((BaseLoadActivity) getContext(), bookInfo);
                            }
                        }
                    } else {
                        //加入书架

                        if (getContext() instanceof BaseLoadActivity) {
                            ActionEngine.getInstance().addBookShelf((BaseLoadActivity) getContext(), mBook.bookId, "", new AddBookListener() {
                                @Override
                                public void success() {
                                    // 加入书架，同步成长值
                                    UserGrow.userGrowOnceToday(getContext(), UserGrow.USER_GROW_ADD_BOOK);
                                    setTvAddShelf();
                                }

                                @Override
                                public void fail() {

                                }
                            });
                        }
                    }
                }
                lastClickTime = currentClickTime;

                break;
            default:
                break;
        }
    }

    /**
     * 隐藏line
     */
    public void goneLine() {
        if (mBookLine != null) {
            mBookLine.setVisibility(INVISIBLE);
        }
    }
}

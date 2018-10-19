package com.dzbook.view.shelf;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.mvp.presenter.MainShelfPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.TimeUtils;
import com.dzbook.view.common.ShelfListBookImageView;
import com.ishugui.R;

import java.io.File;

import huawei.widget.HwProgressBar;

/**
 * 书架别表item
 */
public class ShelfListItemView extends RelativeLayout {

    private Context mContext;

    private Fragment fragment;

    private ShelfListBookImageView imageViewIcon;
    private TextView textViewTitle;
    private TextView textViewAuthor;
    private TextView textViewTime;
    private ImageView imageViewSelected;

    private HwProgressBar loadingView;

    private BookInfo mBookInfo;
    private boolean isShelf;

    private MainShelfPresenter mPresenter;
    private long lastClickTime = 0;
    private long lastLongClickTime = 0;

    /**
     * 构造
     *
     * @param context        context
     * @param fragment       fragment
     * @param shelfPresenter shelfPresenter
     */
    public ShelfListItemView(Context context, Fragment fragment, MainShelfPresenter shelfPresenter) {
        super(context);
        mContext = context;
        this.fragment = fragment;
        mPresenter = shelfPresenter;
        initView();
        initData();
        setListener();
    }

    public BookInfo getBookInfo() {
        return mBookInfo;
    }

    public void setMainShelfPresenter(MainShelfPresenter mPresenter1) {
        this.mPresenter = mPresenter1;
    }

    private void setListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentClickTime = System.currentTimeMillis();
                if (currentClickTime - lastClickTime > 200) {
                    if (isShelf) {
                        mPresenter.skipToReaderActivity(mBookInfo, imageViewIcon, ShelfListItemView.this);
                    } else {
                        operManagerModeClickListener();
                    }
                }
                lastClickTime = currentClickTime;
            }
        });
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                long currentLongClickTime = System.currentTimeMillis();
                if (currentLongClickTime - lastLongClickTime > 200) {
                    if (isShelf) {
                        mPresenter.skipToShelfManagerMode(mBookInfo.bookid);
                    } else {
                        operManagerModeClickListener();
                    }
                }
                lastLongClickTime = currentLongClickTime;
                return true;
            }
        });
        imageViewSelected.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                operManagerModeClickListener();
            }
        });
    }

    /**
     * 处理管理模式的点击事件
     */
    private void operManagerModeClickListener() {
        mBookInfo.blnIsChecked = !mBookInfo.blnIsChecked;
        imageViewSelected.setSelected(mBookInfo.blnIsChecked);
        EventBusUtils.sendMessage(EventConstant.REQUESTCODE_REFERENCESHELFMANAGERVIEW, EventConstant.TYPE_MAINSHELFFRAGMENT, null);
    }

    private void initData() {

    }

    private void initView() {
        setBackgroundResource(R.drawable.selector_hw_list_item);
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_shelf_listitem, this);
        imageViewIcon = view.findViewById(R.id.imageview);
        textViewTitle = view.findViewById(R.id.textview_title);
        textViewAuthor = view.findViewById(R.id.textview_author);
        textViewTime = view.findViewById(R.id.textview_time);
        imageViewSelected = view.findViewById(R.id.imageview_checkbox);
        loadingView = view.findViewById(R.id.shelf_loadding);
    }

    /**
     * 绑定数据
     *
     * @param bookInfo bookInfo
     * @param isShelf1 isShelf1
     */
    public void bindData(BookInfo bookInfo, boolean isShelf1) {
        this.isShelf = isShelf1;
        mBookInfo = bookInfo;
        String bookStatus = "";
        if (bookInfo.isShowOffShelf(getContext()) || bookInfo.isMustDeleteBook(getContext())) {
            bookStatus = getResources().getString(R.string.str_book_xj);
        } else if (bookInfo.isVipFree(getContext())) {
            bookStatus = getResources().getString(R.string.str_book_vipxm);
        } else if (bookInfo.isShowFreeStatus(getContext())) {
            bookStatus = getResources().getString(R.string.str_book_xm);
        } else {
            if (bookInfo.isUpdate == 2) {
                bookStatus = getResources().getString(R.string.str_book_gx);
            }
        }
        imageViewIcon.setBookStatus(bookStatus);
        textViewTitle.setText(bookInfo.bookname);
        textViewAuthor.setText(bookInfo.author);
        if (!TextUtils.isEmpty(bookInfo.time)) {
            textViewTime.setText(TimeUtils.getShowTimeByReadTime(bookInfo.time));
        } else {
            textViewTime.setText(getResources().getString(R.string.str_weizhi));
        }
        if (!TextUtils.isEmpty(bookInfo.coverurl) && bookInfo.coverurl.contains(SDCardUtil.getInstance().getSDCardAndroidRootDir())) {
            Glide.with(this).load(new File(bookInfo.coverurl)).into(imageViewIcon);
        } else {
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(fragment, imageViewIcon, bookInfo.coverurl);
        }
        if (!isShelf1) {
            textViewTime.setVisibility(GONE);
            imageViewSelected.setVisibility(VISIBLE);
            imageViewSelected.setSelected(bookInfo.blnIsChecked);
        } else {
            imageViewSelected.setVisibility(GONE);
            textViewTime.setVisibility(VISIBLE);
        }
    }

    public ImageView getImageViewBookCover() {
        return imageViewIcon;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(DimensionPixelUtil.dip2px(mContext, 88), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 清除图片
     */
    public void clearListImageView() {
        if (imageViewIcon != null) {
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(fragment, imageViewIcon, "");
        }
    }

    /**
     * 显示加载动画
     */
    public void showLoaddingView() {
        if (textViewTime.getVisibility() == VISIBLE) {
            textViewTime.setVisibility(GONE);
        }
        if (loadingView.getVisibility() != VISIBLE) {
            loadingView.setVisibility(VISIBLE);
        }
    }

    /**
     * 隐藏加载动画
     */
    public void hideLoaddingView() {
        if (loadingView.getVisibility() == VISIBLE) {
            loadingView.setVisibility(GONE);
        }
        if (textViewTime.getVisibility() != VISIBLE) {
            textViewTime.setVisibility(VISIBLE);
        }
    }
}

package com.dzbook.view.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dzbook.bean.LocalFileBean;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.mvp.presenter.MainShelfPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.GlideImageLoadUtils;
import com.ishugui.R;

import java.io.File;

import huawei.widget.HwProgressBar;

/***
 * 书籍封面 ImageView __ 封面模式使用
 * @author wangjianchen
 */
public class ShelfGridBookImageView extends FrameLayout {

    /**
     * 封面
     */
    public BookImageView bookImageView;
    private Fragment fragment;
    private MainShelfPresenter shelfPresenter;
    private long lastClickTime = 0;
    private long lastLongClickTime = 0;
    private BookInfo bookInfo;
    private boolean isShelf = true;
    private boolean isShowLoading;

    private Bitmap addBitmap;
    private RectF addBitmapRect;
    private Paint addBitmapPaint;
    private TextPaint addBookPaint;
    private HwProgressBar loadingView;

    /**
     * 构造
     *
     * @param context        context
     * @param fragment       fragment
     * @param shelfPresenter shelfPresenter
     */
    public ShelfGridBookImageView(Context context, Fragment fragment, MainShelfPresenter shelfPresenter) {
        super(context);
        this.fragment = fragment;
        this.shelfPresenter = shelfPresenter;
        this.bookImageView = new BookImageView(getContext());
        removeView(bookImageView);
        addView(bookImageView);
        resetBookSize();
        setListener();
        bookImageView.setBookStatusPadding(DimensionPixelUtil.dip2px(context, 8));
        bookImageView.setCheckBoxPadding(DimensionPixelUtil.dip2px(context, 2));
        bookImageView.setHavClick(true);
        bookImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        init();
    }

    private void init() {
    }

    private void setListener() {
        bookImageView.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                long currentClickTime = System.currentTimeMillis();
                if (currentClickTime - lastClickTime > 200) {
                    if (bookInfo.isAddButton()) {
                        shelfPresenter.skipToSpecialOfferBookActivity();
                        return;
                    }
                    //书架
                    if (isShelf) {
                        shelfPresenter.skipToReaderActivity(bookInfo, ShelfGridBookImageView.this.bookImageView, null);
                    } else {
                        //书架管理
                        operManagerModeClickListener();
                    }
                }
                lastClickTime = currentClickTime;
            }
        });

        bookImageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (bookInfo != null && bookInfo.isAddButton()) {
                    return true;
                }
                long currentLongClickTime = System.currentTimeMillis();
                if (currentLongClickTime - lastLongClickTime > 200) {
                    if (isShelf) {
                        shelfPresenter.skipToShelfManagerMode(bookInfo.bookid);
                    } else {
                        //书架管理
                        operManagerModeClickListener();
                    }
                }
                lastLongClickTime = currentLongClickTime;
                return true;
            }
        });

        bookImageView.setCheckBoxClickListener(new BookImageView.CheckBoxClickListener() {
            @Override
            public void onClick(View v) {
                operManagerModeClickListener();
            }
        });
    }

    /**
     * 处理管理模式的点击事件
     */
    private void operManagerModeClickListener() {
        bookInfo.blnIsChecked = !bookInfo.blnIsChecked;
        bookImageView.setChecked(bookInfo.blnIsChecked);
        EventBusUtils.sendMessage(EventConstant.REQUESTCODE_REFERENCESHELFMANAGERVIEW, EventConstant.TYPE_MAINSHELFFRAGMENT, null);
    }


    private void resetBookSize() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (width == 0) {
            width = DimensionPixelUtil.dip2px(getContext(), 90);
        }
        int height = width * 120 / 90;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 绑定数据
     *
     * @param info    bookInfo
     * @param isshelf isShelf
     */
    public void bindData(BookInfo info, boolean isshelf) {
        this.bookInfo = info;
        this.isShelf = isshelf;
        if (info.isAddButton()) {
            bookImageView.setStyleIsBookAdd(true);
            bookImageView.setBookStatus("");
            bookImageView.hideCheck();
            bookImageView.setBookName(getResources().getString(R.string.shelf_free_zone));
            invalidate();
            return;
        }
        bookImageView.setForm(LocalFileBean.getFileTypeNoDirName(info.format));
        bookImageView.setStyleIsBookAdd(false);
        bookImageView.setBookName(info.bookname);
        String bookStatus = "";
        if (info.isShowOffShelf(getContext()) || info.isMustDeleteBook(getContext())) {
            bookStatus = getResources().getString(R.string.str_book_xj);
        } else if (info.isVipFree(getContext())) {
            bookStatus = getResources().getString(R.string.str_book_vipxm);
        } else if (info.isShowFreeStatus(getContext())) {
            bookStatus = getResources().getString(R.string.str_book_xm);
        } else {
            if (info.isUpdate == 2) {
                bookStatus = getResources().getString(R.string.str_book_gx);
            }
        }
        bookImageView.setBookStatus(bookStatus);
        if (!TextUtils.isEmpty(info.coverurl) && info.coverurl.contains(SDCardUtil.getInstance().getSDCardAndroidRootDir())) {
            Glide.with(this).load(new File(info.coverurl)).into(bookImageView);
        } else {
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(fragment, bookImageView, info.coverurl);
        }
        if (!isshelf) {
            bookImageView.setChecked(info.blnIsChecked);
        } else {
            bookImageView.hideCheck();
        }
        // 需要考虑复用
        isShowLoading = false;
        bookImageView.setHavClick2(false);
        invalidate();
    }

    public BookInfo getBookInfo() {
        return bookInfo;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (bookInfo != null && bookInfo.isAddButton()) {
            drawAddBitmap(canvas);
            drawAddBookTxt(canvas);
        }
        drawLoading(canvas);
    }

    private void drawAddBitmap(Canvas canvas) {
        final float num = 2f;
        int addBitmapSize = DimensionPixelUtil.dip2px(getContext(), 17);
        if (addBitmap == null) {
            addBitmap = ((BitmapDrawable) CompatUtils.getDrawable(getContext(), R.drawable.hw_bookshelf_bot_add)).getBitmap();
        }
        if (addBitmapRect == null) {
            addBitmapRect = new RectF();
            addBitmapRect.left = (getMeasuredWidth() - addBitmapSize) / num;
            addBitmapRect.top = DimensionPixelUtil.dip2px(getContext(), 37);
            addBitmapRect.right = addBitmapRect.left + addBitmapSize;
            addBitmapRect.bottom = addBitmapRect.top + addBitmapSize;
        }
        if (addBitmapPaint == null) {
            addBitmapPaint = new Paint();
            addBitmapPaint.setAntiAlias(true);
        }
        canvas.drawBitmap(addBitmap, null, addBitmapRect, addBitmapPaint);
    }

    private void drawAddBookTxt(Canvas canvas) {
        if (addBookPaint == null) {
            addBookPaint = new TextPaint();
            addBookPaint.setAntiAlias(true);
            addBookPaint.setColor(Color.parseColor("#000000"));
            addBookPaint.setTextSize(DimensionPixelUtil.dip2px(getContext(), 12));
        }
        // 以底部为基准进行计算
        String descTxt = getResources().getString(R.string.shelf_free_zone);
        int left = (int) ((getMeasuredWidth() - addBookPaint.measureText(descTxt)) / 2);
        int top = getMeasuredHeight() - DimensionPixelUtil.dip2px(getContext(), 42);
        canvas.drawText(descTxt, 0, descTxt.length(), left, top, addBookPaint);
    }

    /**
     * 清除图片
     */
    public void clearGridImageView() {
        Glide.with(fragment).clear(this);
        GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(fragment, bookImageView, null);
    }

    private void drawLoading(Canvas canvas) {
        if (!isShowLoading) {
            return;
        }
        if (loadingView == null) {
            int width = DimensionPixelUtil.dip2px(getContext(), 27);
            loadingView = (HwProgressBar) View.inflate(getContext(), R.layout.view_loading_smart, null);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, width);
            layoutParams.gravity = Gravity.CENTER;
            loadingView.setLayoutParams(layoutParams);
            loadingView.setVisibility(GONE);
            removeView(loadingView);
            addView(loadingView);
        }
        showLoading();
        loadingView.setVisibility(VISIBLE);
    }

    /**
     * 显示加载动画
     */
    public void showLoading() {
        bookImageView.setHavClick2(true);
        isShowLoading = true;
        if (loadingView != null) {
            loadingView.setVisibility(VISIBLE);
            loadingView.invalidate();
        }
        bookImageView.invalidate();
        invalidate();
    }

    /**
     * 停止加载
     */
    public void stopLoading() {
        if (isShowLoading) {
            bookImageView.setHavClick2(false);
            bookImageView.invalidate();
            isShowLoading = false;
            if (loadingView != null) {
                loadingView.setVisibility(GONE);
            }
        }
    }
}

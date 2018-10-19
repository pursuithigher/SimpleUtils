package com.dzbook.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.dzbook.event.EventBusUtils;
import com.dzbook.lib.utils.ALog;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.DeviceUtils;
import com.dzbook.utils.ImageUtils;
import com.dzbook.view.common.BookImageView;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A view to show the cover of a book. With method
 *
 * @author wenping0820@163.com  2015-07-13
 */
public class BookView {
    private static final String TAG_TODAY = "tag_today";
    private static volatile BookView sOpenedBookView;
    private static final int ANIMATION_DURATION = 1000;
    /**
     * 背景图片缓存
     */
    private Map<String, Bitmap> bgCache = new HashMap<>();

    private AtomicBoolean mIsOpen = new AtomicBoolean(false);
    private AtomicInteger mAnimationCount = new AtomicInteger(0);
    private float mBgScaleX;
    private float mBgScaleY;
    private float mCoverScaleX;
    private float mCoverScaleY;
    private int[] mLocation = new int[2];
    private WindowManager mWindowManager;
    private FrameLayout mWmRootView;
    // cover
    private BookImageView mCover = null;
    private BookImageView mAnimCover;
    private ImageView mAnimBackground;
    private int mTotalOpenBookAnim;
    private float mOpenBookEndBgY = 0;
    private Context mContext;
    private long lastActionTime;

    private int statusBarHeight;
    private List<String> adapterPhoneList;
    private int startOpenBookRequestCode;
    private String startOpenBookTAG;
    private Bundle startBundle;
    private String bookId = "";
    private int closeBookRequestCode;
    private String closeTag;

    /**
     * 构造
     *
     * @param context context
     */
    public BookView(Context context) {
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        adapterPhoneList = new ArrayList<>();
        addNeedAdapterPhoneList();
        statusBarHeight = getStatusBarHeight(context);
        if (statusBarHeight <= 0) {
            statusBarHeight = 50;
        }
    }

    /**
     * 由于动画依赖的getLocationInWindow在不同
     * 手机上有着不同的返回下面列表中都包含状态栏
     */
    private void addNeedAdapterPhoneList() {
        adapterPhoneList.add("ivvi" + ":" + "SK3-02");
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public String getBookId() {
        return this.bookId;
    }

    /**
     * Start opening book animation.
     *
     * @param startOpenRequestCode startOpenBookRequestCode
     * @param tag                  tag
     * @param bundle               bundle
     * @param id                   bookId
     * @param coverImageView       coverImageView
     * @return boolean
     */
    @SuppressLint("NewApi")
    public synchronized boolean startOpenBookAnimation(ImageView coverImageView, int startOpenRequestCode, String tag, Bundle bundle, String id) {
        if (null != mWmRootView) {
            removeWindowView();
        }
        ALog.dLk("BookView startOpenBookAnimation. mIsOpen.get()=" + mIsOpen.get());
        if (DeviceUtils.getMemoryTotalSize() < 512 || coverImageView == null || mIsOpen.get()) {
            int count = mAnimationCount.incrementAndGet();
            ALog.dLk("BookView startOpenBookAnimation.count=" + count);
            return false;
        }
        lastActionTime = System.currentTimeMillis();
        try {
            this.bookId = id;
            this.startOpenBookRequestCode = startOpenRequestCode;
            this.startOpenBookTAG = tag;
            startBundle = bundle;
            final long num = 3000L;
            //            mOpenBookAnimEndListener = l;
            //            mCloseBookAnimEndListener = null;
            mCover = (BookImageView) coverImageView;
            mWmRootView = new FrameLayout(mContext);
            WindowManager.LayoutParams winParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, 0, 0, WindowManager.LayoutParams.TYPE_APPLICATION_PANEL, WindowManager.LayoutParams.FLAG_FULLSCREEN, PixelFormat.RGBA_8888);
            mWindowManager.addView(mWmRootView, winParams);
            mWmRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (System.currentTimeMillis() - lastActionTime > num) {
                        resetGlobal();
                        removeWindowView();
                    }
                }
            });
            // new animation views
            mAnimCover = new BookImageView(mContext);
            mAnimBackground = new ImageView(mContext);
            int width = mCover.getMeasuredWidth() - mCover.getPaddingRight();
            int height = mCover.getMeasuredHeight() - mCover.getPaddingBottom();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
            mAnimBackground.setLayoutParams(lp);
            mAnimCover.setLayoutParams(lp);
            mAnimCover.setScaleType(mCover.getScaleType());
            mAnimBackground.setScaleType(mCover.getScaleType());
            Drawable drawable = mCover.getDrawable();
            if (drawable != null) {
                mAnimCover.setImageDrawable(drawable);
            } else {
                mAnimCover.setBookName(mCover.getBookName());
                mAnimCover.setForm(mCover.getBookForm());
            }

            // 夜晚黑图，白天绿图
            setAnimBkg();

            // Add view to root. Be careful that the height and width of 'params' should be
            // specified values. WRAP_CONTENT or MATCH_PARENT will lead to wrong effect.
            mWmRootView.addView(mAnimBackground);
            mWmRootView.addView(mAnimCover);
            mCover.getLocationInWindow(mLocation);
            initDate();

            // start animation
            mAnimBackground.setPivotX(0);
            mAnimBackground.setPivotY(0);
            mAnimCover.setPivotX(0);
            mAnimCover.setPivotY(0);
            // Reset total opening animations scheduled.
            mTotalOpenBookAnim = 0;

            // background animation
            int translationYStart = getAdapterTranslationY();
            startIndividualAnim(mAnimBackground, "translationX", mLocation[0], 0, true);
            startIndividualAnim(mAnimBackground, "translationY", translationYStart, mOpenBookEndBgY, true);
            startIndividualAnim(mAnimBackground, "scaleX", 1.0f, mBgScaleX, true);
            startIndividualAnim(mAnimBackground, "scaleY", 1.0f, mBgScaleY, true);
            // cover animation
            startIndividualAnim(mAnimCover, "translationX", mLocation[0], 0, true);
            startIndividualAnim(mAnimCover, "translationY", translationYStart, mOpenBookEndBgY, true);
            startIndividualAnim(mAnimCover, "scaleX", 1.0f, mCoverScaleX, true);
            startIndividualAnim(mAnimCover, "scaleY", 1.0f, mCoverScaleY, true);
            startIndividualAnim(mAnimCover, "rotationY", 0, -180, true);
            return true;
        } catch (Exception e) {
            resetGlobal();
            removeWindowView();
            ALog.printStackTrace(e);
        }
        return false;
    }

    /**
     * 做具体手机的适配
     *
     * @return Y
     */
    private int getAdapterTranslationY() {
        String phoneInfo = DeviceInfoUtils.getInstanse().getBrand() + ":" + DeviceInfoUtils.getInstanse().getModel();
        if (adapterPhoneList.contains(phoneInfo)) {
            return mLocation[1] - statusBarHeight;
        } else {
            return mLocation[1];
        }
    }

    /**
     * 关闭图书
     *
     * @param requestCode requestCode
     * @param tag         tag
     */
    public synchronized void startCloseBookDirect(int requestCode, String tag) {
        int count = mAnimationCount.decrementAndGet();
        ALog.dLk("BookView startCloseBookDirect. mIsOpen.get()=" + mIsOpen.get() + " count=" + count);

        resetGlobal();
        removeWindowView();
        EventBusUtils.sendMessage(requestCode, tag, null);
    }

    /**
     * Close book animation.
     *
     * @param coverImageView coverImageView
     * @param requestCode    requestCode
     * @param tag            tag
     */
    public synchronized void startCloseBookAnimation(ImageView coverImageView, int requestCode, String tag) {
        ALog.dLk("BookView startCloseBookAnimation. mIsOpen.get()=" + mIsOpen.get());
        this.closeBookRequestCode = requestCode;
        this.closeTag = tag;

        if (DeviceUtils.getMemoryTotalSize() < 512) {
            startCloseBookDirect(requestCode, tag);
            return;
        }
        lastActionTime = System.currentTimeMillis();
        if (mIsOpen.get()) {
            resetGlobal();
            if (coverImageView != null) {
                mCover = (BookImageView) coverImageView;
                mCover.getLocationOnScreen(mLocation);
                initDate();
            }
            // start animation
            //            ALog.eDongdz("关闭动画：mLocation[0]:" + mLocation[0] + " mLocation[1]: " + mLocation[1] + " mCoverScaleX: " + mCoverScaleX
            //                    + " mCoverScaleY: " + mCoverScaleY + " mBgScaleX: " + mBgScaleX + " mBgScaleY: " + mBgScaleY);
            setAnimBkg();

            // Reset open animation count.
            mTotalOpenBookAnim = 0;
            int translationYEnd = getAdapterTranslationY();
            // background animation
            startIndividualAnim(mAnimBackground, "translationX", 0, mLocation[0], false);
            startIndividualAnim(mAnimBackground, "translationY", mOpenBookEndBgY, translationYEnd, false);
            startIndividualAnim(mAnimBackground, "scaleX", mBgScaleX, 1.0f, false);
            startIndividualAnim(mAnimBackground, "scaleY", mBgScaleY, 1.0f, false);
            // cover animation
            startIndividualAnim(mAnimCover, "translationX", 0, mLocation[0], false);
            startIndividualAnim(mAnimCover, "translationY", mOpenBookEndBgY, translationYEnd, false);
            startIndividualAnim(mAnimCover, "scaleX", mCoverScaleX, 1.0f, false);
            startIndividualAnim(mAnimCover, "scaleY", mCoverScaleY, 1.0f, false);
            startIndividualAnim(mAnimCover, "rotationY", -180, 0, false);
        } else {
            removeWindowView();
        }
    }

    /**
     * 开关书籍的背景图
     */
    private void setAnimBkg() {
        Bitmap dayBitmap;
        if (bgCache.containsKey(TAG_TODAY)) {
            dayBitmap = bgCache.get(TAG_TODAY);
        } else {
            dayBitmap = ImageUtils.readBitmap(R.drawable.aa_shelf_icon_open_book_bg, true);
            bgCache.put(TAG_TODAY, dayBitmap);
        }
        mAnimBackground.setImageBitmap(dayBitmap);
    }

    private void initDate() {
        // view scale
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        float scaleW = screenWidth / (float) (mCover.getWidth() - mCover.getPaddingRight());
        float scaleH = screenHeight / (float) (mCover.getHeight() - mCover.getPaddingBottom());
        mBgScaleX = scaleW;
        mBgScaleY = scaleH;
        mCoverScaleX = scaleW / 5;
        mCoverScaleY = scaleH;
    }

    /**
     * Play one individual animation.
     *
     * @param target     target
     * @param property   property
     * @param startValue startValue
     * @param endValue   endValue
     */
    private void startIndividualAnim(View target, String property, float startValue, float endValue, final boolean isOpen) {
        // Increase total opening animations scheduled.
        mTotalOpenBookAnim++;

        ObjectAnimator animator = ObjectAnimator.ofFloat(target, property, startValue, endValue).setDuration(ANIMATION_DURATION);
        animator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isOpen) {
                    if (mAnimationCount.incrementAndGet() >= mTotalOpenBookAnim) {
                        mIsOpen.set(true);
                        EventBusUtils.sendMessage(startOpenBookRequestCode, startOpenBookTAG, startBundle);
                    }
                } else {
                    resetGlobal();
                    removeWindowView();
                    if (mAnimationCount.decrementAndGet() <= 0) {
                        bookId = "";
                        EventBusUtils.sendMessage(closeBookRequestCode, closeTag, null);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    public AtomicBoolean isOpen() {
        return mIsOpen;
    }

    private synchronized void removeWindowView() {
        ALog.dLk("BookView removeWindowView. mIsOpen.get()=" + mIsOpen.get());
        try {
            if (mWmRootView != null && mContext != null && !mContext.isRestricted()) {
                mWindowManager.removeView(mWmRootView);
                mWmRootView = null;
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    public static void setOpenedBookView(BookView bookView) {
        BookView.sOpenedBookView = bookView;
    }

    public static BookView getOpenedBookView() {
        return sOpenedBookView;
    }

    private void resetGlobal() {
        setOpenedBookView(null);
        mIsOpen.set(false);
    }
}

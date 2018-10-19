package com.dzbook.view.store;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.view.hwbanner.BannerPagerAdapter;
import com.dzbook.view.hwbanner.DotsViewPager;
import com.huawei.uxwidget.hwdotspageindicator.HwTopBannerIndicator;
import com.huawei.uxwidget.topbanner.BackgroundTaskUtil;
import com.huawei.uxwidget.topbanner.TopBanner;
import com.ishugui.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.store.BeanBannerInfo;
import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * Bn0View
 *
 * @author dongdianzhou on 2018/1/17.
 */

public class Bn0View extends RelativeLayout {
    private static final int DEFAULT_DURATION = 700;


    private Fragment mFragment;
    private TempletPresenter mPresenter;

    private Context mContext;

    private DotsViewPager dotsViewPager;
    private HwTopBannerIndicator bannerIndicator;

    private BannerPagerAdapter viewPagerAdapter = null;
    private int isLeftOrRight = 0;
    private int isFirst = 0;
    private int currPage = 0;

    private int templetPosition;

    private Handler shadowHandler;
    private DisplayMetrics metrics;

    private List<BeanSubTempletInfo> list;
    private BeanTempletInfo templetInfo;
    private long clickDelayTime = 0;


    /**
     * 构造
     *
     * @param context context
     */
    public Bn0View(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context          context
     * @param fragment         fragment
     * @param templetPresenter templetPresenter
     * @param templetPosition  templetPosition
     */
    public Bn0View(Context context, Fragment fragment, TempletPresenter templetPresenter, int templetPosition) {
        this(context, null);
        mContext = context;
        mFragment = fragment;
        mPresenter = templetPresenter;
        shadowHandler = new Handler();
        this.templetPosition = templetPosition;
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public Bn0View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }


    private void setListener() {
    }

    private void initData() {
        list = new ArrayList<>();
    }


    private void initView() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        LayoutInflater.from(getContext()).inflate(R.layout.view_banner0, this);
        dotsViewPager = findViewById(R.id.banner_viewpager);
        bannerIndicator = findViewById(R.id.hwdotspageindicator);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (bannerIndicator != null) {
            bannerIndicator.startAutoPlay();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bannerIndicator != null) {
            bannerIndicator.stopAutoPlay();
        }
    }

    /**
     * 判断左右滑动方向
     *
     * @param position 位置
     * @return 滑动方向
     */
    private int getSlideSize(int position) {
        if (isFirst == 0) {
            if (currPage > position) {
                //右滑
                isLeftOrRight = 1;
            } else if (currPage == position) {
                //左滑
                isLeftOrRight = -1;
            }
            if (null != bannerIndicator) {
                if (bannerIndicator.isAutoPlay()) {
                    isLeftOrRight = -1;
                }
            }

            isFirst = 1;
        }
        return isLeftOrRight;
    }

    /**
     * 停止自动轮播
     */
    public void stopAutoPlay() {
        if (bannerIndicator != null) {
            bannerIndicator.stopAutoPlay();
        }
    }

    /**
     * 开始自动轮播
     */
    public void startAutoPlay() {
        if (bannerIndicator != null) {
            bannerIndicator.startAutoPlay();
        }
    }

    /**
     * 图片监听
     */
    private static class MainPictureImgListener implements GlideImageLoadUtils.DownloadImageListener {
        private final TopBanner topBanner;
        private final int position;

        MainPictureImgListener(TopBanner topBanner, int position) {
            this.topBanner = topBanner;
            this.position = position;
        }

        @Override
        public void downloadSuccess(Bitmap resource) {
            try {
                if (resource != null) {
                    topBanner.getMainPictureImg().setImageBitmap(resource);
                    topBanner.setMainPicture(resource);
                    topBanner.setTextColor();
                    //                    if (position == 1) {
                    //                        refreshShadowInMain(topBanner);
                    //                    } else {
                    //                        refreshShadow(topBanner);
                    //                    }
                }
            } catch (Exception ex) {
                //严格模式下aar内部有使用RenderScrip（类流）打开未关闭的操作
            }
        }

        @Override
        public void downloadSuccess(File resource) {

        }

        @Override
        public void downloadFailed() {

        }
    }

    /**
     * 背景图片监听
     */
    private static class BackPictureImageListener implements GlideImageLoadUtils.DownloadImageListener {
        private final TopBanner topBanner;
        private final int position;

        BackPictureImageListener(TopBanner topBanner, int position) {
            this.topBanner = topBanner;
            this.position = position;
        }

        @Override
        public void downloadSuccess(Bitmap resource) {
            try {
                if (resource != null) {
                    topBanner.getBackPicture().setImageBitmap(resource);
                    topBanner.setColorPlate(resource);
                    topBanner.setTextColor();
                    //                    if (position == 1) {
                    //                        refreshShadowInMain(topBanner);
                    //                    } else {
                    //                        refreshShadow(topBanner);
                    //                    }
                }
            } catch (Exception ex) {
                //严格模式下aar内部有使用RenderScrip（类流）打开未关闭的操作
            }
        }

        @Override
        public void downloadSuccess(File resource) {

        }

        @Override
        public void downloadFailed() {

        }
    }

    private void refreshShadowInMain(TopBanner topBanner) {
        if (isNeedShadow()) {
            Bitmap var1 = getDrawableCache(topBanner);
            if (var1 == null) {
                return;
            }

            Bitmap var2 = blurBitmap(getContext(), var1);
            topBanner.setShadowBitmap(var2);
        }

    }

    private Bitmap getDrawableCache(TopBanner topBanner) {
        topBanner.setDrawingCacheEnabled(true);
        Bitmap var1 = topBanner.getDrawingCache();
        if (var1 == null) {
            return null;
        } else {
            Bitmap var2 = Bitmap.createBitmap(var1);
            topBanner.setDrawingCacheEnabled(false);
            return var2;
        }
    }

    private void refreshShadow(final TopBanner topBanner) {
        if (isNeedShadow()) {
            final Bitmap var1 = getDrawableCache(topBanner);
            if (var1 == null) {
                return;
            }
            BackgroundTaskUtil.a(new Runnable() {
                @Override
                public void run() {
                    final Bitmap var1x = blurBitmap(getContext(), var1);
                    shadowHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            topBanner.setShadowBitmap(var1x);
                        }
                    });
                }
            });
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Bitmap blurBitmap(Context var0, Bitmap var1) {
        if (isNeedShadow()) {
            ScriptIntrinsicBlur intrinsicBlur = null;
            Allocation allocation1 = null;
            Allocation allocation2 = null;
            try {
                final float num1 = 0.36F;
                final float num2 = 0.38F;
                final float num3 = 24.0F;
                int var2 = Math.round((float) var1.getWidth() * num1);
                int var3 = Math.round((float) var1.getHeight() * num2);
                Bitmap var4 = Bitmap.createScaledBitmap(var1, var2, var3, false);
                Bitmap var5 = Bitmap.createBitmap(var4);
                RenderScript var6 = RenderScript.create(var0);
                intrinsicBlur = ScriptIntrinsicBlur.create(var6, Element.U8_4(var6));
                allocation1 = Allocation.createFromBitmap(var6, var4);
                allocation2 = Allocation.createFromBitmap(var6, var5);
                intrinsicBlur.setRadius(num3);
                intrinsicBlur.setInput(allocation1);
                intrinsicBlur.forEach(allocation2);
                allocation2.copyTo(var5);
                return var5;
            } catch (Exception ex) {

            } finally {
                if (intrinsicBlur != null) {
                    intrinsicBlur.destroy();
                }
                if (allocation1 != null) {
                    allocation1.destroy();
                }
                if (allocation2 != null) {
                    allocation2.destroy();
                }
            }
        }
        return null;
    }

    /**
     * 是否需要模糊
     *
     * @return
     */
    private boolean isNeedShadow() {
        return DeviceInfoUtils.getInstanse().getOsVersionInt() >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * CommodityImageListener
     */
    private static class CommodityImageListener implements GlideImageLoadUtils.DownloadImageListener {
        private final TopBanner topBanner;

        public CommodityImageListener(TopBanner topBanner) {
            this.topBanner = topBanner;
        }

        @Override
        public void downloadSuccess(Bitmap resource) {
            try {
                if (resource != null) {
                    topBanner.getCommodity().setImageBitmap(resource);
                    topBanner.setCommodityInfo(resource);
                }
            } catch (Exception ex) {

            }
        }

        @Override
        public void downloadSuccess(File resource) {

        }

        @Override
        public void downloadFailed() {

        }
    }

    /**
     * 圆点指示器改变监听
     */
    private class DotsPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (positionOffset != 0) {
                int side = getSlideSize(position);
                switch (side) {
                    case 1:
                        //右滑
                        if (position < viewPagerAdapter.getCount() - 1 && position >= 0) {
                            TopBanner nextBanner = viewPagerAdapter.getItemView(position + 1).findViewById(R.id.hwtopbanner);
                            if (null != nextBanner) {
                                nextBanner.setRightView2(position, positionOffset, positionOffsetPixels);
                            }
                            TopBanner curBannerView = viewPagerAdapter.getItemView(position).findViewById(R.id.hwtopbanner);
                            if (null != curBannerView) {
                                curBannerView.setRightView1(position, positionOffset, positionOffsetPixels);
                            }

                        }
                        break;
                    case -1:
                        //左滑
                        if (position < viewPagerAdapter.getCount() - 1 && position >= 0) {
                            TopBanner nextBanner = viewPagerAdapter.getItemView(position + 1).findViewById(R.id.hwtopbanner);
                            if (null != nextBanner) {
                                nextBanner.setLeftView2(position, positionOffset, positionOffsetPixels);
                            }
                            TopBanner curBannerView = viewPagerAdapter.getItemView(position).findViewById(R.id.hwtopbanner);
                            if (null != curBannerView) {
                                curBannerView.setLeftView1(position, positionOffset, positionOffsetPixels);
                            }
                        }
                        break;

                    default:
                        break;
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            onPageSelectedInit(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //Log.v("onPageScrolledState", "state " + state); 0：什么都没做 1：开始滑动 2：滑动结束
            onScrollStateChanged(state);
        }
    }

    private void onScrollStateChanged(int state) {
        if (state == 0) {
            isFirst = 0;
            isLeftOrRight = 0;
            //用于动画停止时的复位
            backInitialPosition(currPage);
        }
    }

    private void backInitialPosition(int position) {
        if (position >= 0 && position < viewPagerAdapter.getCount()) {
            TopBanner bannerView = viewPagerAdapter.getItemView(position).findViewById(R.id.hwtopbanner);
            if (null != bannerView) {
                bannerView.setInitialPosition();
            }

        }
    }

    private void onPageSelectedInit(int position) {
        int realPos = position - 1;
        if (position == 0) {
            realPos = list.size();
        }

        if (position == list.size() + 1) {
            realPos = 0;
        }

        if (realPos < 0) {
            return;
        }
        currPage = position;
    }

    /**
     * 绑定数据
     *
     * @param templetInfo1 templetInfo1
     * @param isReference  isReference
     */
    public void bindData(BeanTempletInfo templetInfo1, boolean isReference) {
        if (!isReference) {
            return;
        }
        ArrayList<View> viewList = new ArrayList<>();
        if (templetInfo1 != null) {
            this.templetInfo = templetInfo1;
            this.list = templetInfo1.items;
            int size = list.size();
            boolean isShowIndicator = true;
            if (size == 1) {
                isShowIndicator = false;
                BeanSubTempletInfo lastTempletInfo = list.get(0);
                if (lastTempletInfo != null) {
                    addSingleBanner(lastTempletInfo, 0, viewList);
                }
            } else if (size > 0) {
                isShowIndicator = true;
                BeanSubTempletInfo lastTempletInfo = list.get(size - 1);
                if (lastTempletInfo != null) {
                    addSingleBanner(lastTempletInfo, size - 1, viewList);
                }
                for (int i = 0; i < size; i++) {
                    BeanSubTempletInfo subTempletInfo = list.get(i);
                    if (subTempletInfo != null) {
                        addSingleBanner(subTempletInfo, i, viewList);
                    }
                }
                BeanSubTempletInfo firstTempletInfo = list.get(0);
                if (firstTempletInfo != null) {
                    addSingleBanner(firstTempletInfo, 0, viewList);
                }
            }
            if (viewList.size() > 0) {
                viewPagerAdapter = new BannerPagerAdapter(viewList);
                dotsViewPager.setAdapter(viewPagerAdapter);
                bannerIndicator.setIsNeedChangeViewPagerScrollDuration(true);
                bannerIndicator.setViewPager(dotsViewPager);
                dotsViewPager.setCurrentItem(1, false);
                bannerIndicator.setIsRecycle(true);
                bannerIndicator.setScrollDuration(DEFAULT_DURATION);
                if (isShowIndicator) {
                    if (bannerIndicator.getVisibility() != VISIBLE) {
                        bannerIndicator.setVisibility(VISIBLE);
                    }
                } else {
                    if (bannerIndicator.getVisibility() == VISIBLE) {
                        bannerIndicator.setVisibility(GONE);
                    }
                }
                dotsViewPager.addOnPageChangeListener(new DotsPageChangeListener());
            }
        }
    }

    private void addSingleBanner(final BeanSubTempletInfo beanSubTempletInfo, final int position, ArrayList<View> viewList) {
        if (beanSubTempletInfo == null) {
            return;
        }
        View view = null;
        //        if (position == 1) {
        //            view = View.inflate(mContext, R.layout.banfragment_main, null);
        //        } else {
        view = View.inflate(mContext, R.layout.banfragment, null);
        //        }
        if (view == null) {
            return;
        }
        final TopBanner topBanner = view.findViewById(R.id.hwtopbanner);
        if (topBanner == null) {
            return;
        }
        //        if (DeviceInfoUtils.getInstanse().getOsVersionInt() >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        topBanner.setNeedShadow(false);
        //        } else {
        //            topBanner.setNeedShadow(false);
        //        }
        BeanBannerInfo bannerInfo = beanSubTempletInfo.bannerInfo;
        if (bannerInfo == null) {
            return;
        }

        int width = getImageWidth();
        int heightBg = getBgImageHeight();
        int heightMain = getMainImageHeight();

        //背景图
        if (!TextUtils.isEmpty(bannerInfo.backImgUrl)) {
            GlideImageLoadUtils.getInstanse().loadBannerPic(mContext, bannerInfo.backImgUrl, width, heightBg, new BackPictureImageListener(topBanner, position), true);
        }
        //主图
        if (!TextUtils.isEmpty(bannerInfo.mainImgUrl)) {
            GlideImageLoadUtils.getInstanse().downloadImageBitmapFromUrl(mFragment, bannerInfo.mainImgUrl, width, heightMain, new MainPictureImgListener(topBanner, position), true);
        }
        //icon 图片
        if (!TextUtils.isEmpty(bannerInfo.iconImgUrl)) {
            GlideImageLoadUtils.getInstanse().downloadImageBitmapFromUrl(mFragment, bannerInfo.iconImgUrl, width, heightMain, new CommodityImageListener(topBanner), true);
        }
        //0：白色 1：智能选色
        topBanner.setTextColorType(1);
        //textPositionType 文本位置: 0:左上、2:左下
        topBanner.setTextPositionType(0);
        topBanner.getTextOneTv().setText(bannerInfo.text1);
        topBanner.getTextTwoTv().setText(bannerInfo.text2);
        topBanner.getTextThreeTv().setText(bannerInfo.text3);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long current = System.currentTimeMillis();
                if (current - clickDelayTime > TempletContant.CLICK_DISTANSE) {
                    mPresenter.setActionClick(beanSubTempletInfo, templetInfo, templetPosition, TempletPresenter.LOG_CLICK_ACTION_BN0);
                    //mPresenter.actionOper(beanSubTempletInfo.action, beanSubTempletInfo.title);
                    //                    mPresenter.logClick(TempletPresenter.LOG_CLICK_ACTION_BN0, beanSubTempletInfo, templetPosition);
                    //                    boolean isBook = TempletContant.ACTION_TYPE_BOOK.equals(beanSubTempletInfo.action.type);
                    //                    mPresenter.logHw(templetInfo, beanSubTempletInfo, templetPosition, TempletPresenter.LOG_CLICK_ACTION_BN0, TempletPresenter.LOG_CLICK_NONE, isBook);
                    clickDelayTime = current;
                }
            }
        });
        viewList.add(view);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        metrics = mContext.getResources().getDisplayMetrics();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(getBn0Width(), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(getBnHeight(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int getBn0Width() {
        if (metrics == null) {
            metrics = mContext.getResources().getDisplayMetrics();
        }
        return metrics.widthPixels;
    }

    private int getBnHeight() {
        return getBn0Width() * 226 / 360;
    }

    private int getImageWidth() {
        return getBn0Width() - DimensionPixelUtil.dip2px(mContext, 32);
    }

    private int getBgImageHeight() {
        return getImageWidth() * 768 / 1312;
    }

    private int getMainImageHeight() {
        return getImageWidth() * 800 / 1312;
    }
}

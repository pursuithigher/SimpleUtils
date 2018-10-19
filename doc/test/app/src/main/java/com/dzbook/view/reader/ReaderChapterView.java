package com.dzbook.view.reader;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.activity.reader.ReaderCatalogActivity;
import com.dzbook.activity.reader.ReaderChapterAdapter;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.presenter.ReaderCatalogPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 章节
 *
 * @author wxliao on 17/8/17.
 */
public class ReaderChapterView extends LinearLayout implements View.OnClickListener {

    /**
     * 本地
     */
    public static final int LOCAL_DISABLE = 0x01;
    /**
     * 有后续章节
     */
    public static final int SERVER_ENABLE = 0x03;
    /**
     * 无后续章节
     */
    public static final int SERVER_DISABLE = 0x04;
    /**
     * 下载中
     */
    public static final int SERVER_LOADING = 0x05;

    private ListView listviewChapter;
    private ReaderChapterAdapter adapterChapter;
    private TextView textviewChaptermessage, textviewDownloaddes, textviewSelectblock;
    private TextView textviewChapternum;
    private ReaderCatalogActivity activity;
    private boolean isShowPop;
    private View mTriangle;

    private RelativeLayout layoutPurchasedchapters;

    /**
     * 构造
     *
     * @param context context
     */
    public ReaderChapterView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderChapterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        activity = (ReaderCatalogActivity) context;
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.view_reader_catalog, this, true);
        textviewChaptermessage = findViewById(R.id.textView_chapterMessage);
        listviewChapter = findViewById(R.id.listView_chapter);
        textviewChapternum = findViewById(R.id.textView_chapterNum);
        textviewDownloaddes = findViewById(R.id.textView_downloadDes);
        textviewSelectblock = findViewById(R.id.textView_selectBlock);
        mTriangle = findViewById(R.id.view_triangle);
        textviewSelectblock.setOnClickListener(this);
        //设置字重
        TypefaceUtils.setHwChineseMediumFonts(textviewChapternum);
        TypefaceUtils.setHwChineseMediumFonts(textviewSelectblock);
        TypefaceUtils.setHwChineseMediumFonts(textviewDownloaddes);

        layoutPurchasedchapters = findViewById(R.id.layout_purchasedChapters);

        adapterChapter = new ReaderChapterAdapter(context, textviewChaptermessage);
        listviewChapter.setAdapter(adapterChapter);
        listviewChapter.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        ReaderCatalogPresenter presenter = ((ReaderCatalogActivity) getContext()).getPresenter();
                        if (presenter == null) {
                            return;
                        }
                        textviewSelectblock.setText((absListView.getFirstVisiblePosition() + 1) + "-" + (absListView.getLastVisiblePosition() + 1) + getResources().getString(R.string.chapter));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            try {
                //反射—fastscrollbar
                Field mFastScroll = AbsListView.class.getDeclaredField("mFastScroll");
                mFastScroll.setAccessible(true);
                Object fastScroller = mFastScroll.get(listviewChapter);
                Field mThumbImage = fastScroller.getClass().getDeclaredField("mThumbImage");
                mThumbImage.setAccessible(true);
                ImageView imageView = (ImageView) mThumbImage.get(fastScroller);
                imageView.setImageDrawable(CompatUtils.getDrawable(getContext(), R.drawable.hw_shape_fastscroll));
                imageView.setMinimumWidth(DimensionPixelUtil.dip2px(context, 20));
                imageView.setMinimumHeight(DimensionPixelUtil.dip2px(context, 80));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        layoutPurchasedchapters.setOnClickListener(this);
    }

    /**
     * 点击
     *
     * @param position position
     */
    public void setBlockClick(int position) {
        if (listviewChapter != null) {
            int num = position * 50;
            listviewChapter.setSelection(num);
            int endNum = listviewChapter.getAdapter().getCount() > (num + 9) ? (num + 9) : listviewChapter.getAdapter().getCount();
            textviewSelectblock.setText((num + 1) + "-" + endNum + getResources().getString(R.string.chapter));
        }
    }

    /**
     * 添加数据
     *
     * @param list  list
     * @param clear clear
     */
    public void addItem(List<CatalogInfo> list, boolean clear) {
        if (adapterChapter != null) {
            adapterChapter.addItem(list, clear);
        }
        if (list != null) {
            isShowPop = list.size() > 50;
            textviewChapternum.setText(getResources().getString(R.string.In_total) + " " + list.size() + " " + getResources().getString(R.string.chapter));
            if (list.size() > 10) {
                textviewSelectblock.setText("1-10" + " " + getResources().getString(R.string.chapter));
            } else {
                textviewSelectblock.setText("1-" + list.size() + " " + getResources().getString(R.string.chapter));
            }
        } else {
            textviewChapternum.setText("");
        }
        if (!isShowPop) {
            mTriangle.setVisibility(GONE);
        }
    }

    /**
     * 显示顶部位置
     *
     * @param catalogId catalogId
     */
    public void setSelectionFromTop(final String catalogId) {
        listviewChapter.post(new Runnable() {
            @Override
            public void run() {
                int position = adapterChapter.getIndex(catalogId);
                int y = DimensionPixelUtil.dip2px(getContext(), 50);
                listviewChapter.setSelectionFromTop(position, y * 3);
            }
        });

    }

    /**
     * 刷新
     */
    public void refresh() {
        adapterChapter.notifyDataSetChanged();
    }

    /**
     * 设置下载按钮状态
     *
     * @param status     status
     * @param remainSize remainSize
     * @param totalSize  totalSize
     */
    public void setPurchasedButtonStatus(int status, int remainSize, int totalSize) {
        switch (status) {
            case LOCAL_DISABLE:
                layoutPurchasedchapters.setVisibility(View.GONE);
                break;
            case SERVER_DISABLE:
                layoutPurchasedchapters.setVisibility(View.VISIBLE);
                textviewDownloaddes.setText(R.string.str_no_chapter_can_download);
                layoutPurchasedchapters.setEnabled(false);
                break;
            case SERVER_ENABLE:
                layoutPurchasedchapters.setVisibility(View.VISIBLE);
                textviewDownloaddes.setText(R.string.str_download_purchase_chapters);
                layoutPurchasedchapters.setEnabled(true);
                break;
            case SERVER_LOADING:
                layoutPurchasedchapters.setVisibility(View.VISIBLE);
                int progress = Math.max(totalSize - remainSize, 0);
                textviewDownloaddes.setText(getContext().getString(R.string.str_downling) + " " + getDownloadProgress(progress, totalSize));
                layoutPurchasedchapters.setEnabled(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_purchasedChapters:
                ReaderCatalogPresenter presenter = ((ReaderCatalogActivity) getContext()).getPresenter();
                if (presenter != null) {
                    presenter.handlePurchasedClick();
                }
                break;
            case R.id.textView_selectBlock:
                if (!isShowPop) {
                    return;
                }
                if (activity != null && !activity.isFinishing()) {
                    activity.showSelectPop(listviewChapter.getFirstVisiblePosition(), textviewSelectblock);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 下载进度(alreadydownNum/downloadSumNum)
     *
     * @param alreadydownNum alreadydownNum
     * @param downloadSumNum downloadSumNum
     * @return string
     */
    public String getDownloadProgress(int alreadydownNum, int downloadSumNum) {

        String result = "0";
        if (downloadSumNum != 0) {

            double data = (float) alreadydownNum / (float) downloadSumNum * 100;
            if ((int) data > 0) {

                DecimalFormat df = new DecimalFormat("#.00");
                result = df.format(data);
            } else {

                DecimalFormat df = new DecimalFormat("#.00");
                result = df.format(data);
                result = "0" + result;
            }
        }
        return result + "%";
    }
}

package com.dzbook.activity.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.BaseTransparencyLoadActivity;
import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.dialog.common.CatalogSelectDialog;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.UI.BookDetailChapterUI;
import com.dzbook.mvp.presenter.BookDetailChapterPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.common.NetErrorTopView;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import java.util.List;

import hw.sdk.net.bean.BeanBlock;
import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.BeanChapterCatalog;
import hw.sdk.net.bean.BeanChapterInfo;

/**
 * 图书详情目录
 *
 * @author lizhongzhong 2014-9-10 书籍章节目录页面
 */
public class BookDetailChapterActivity extends BaseTransparencyLoadActivity implements OnClickListener, BookDetailChapterUI {
    /**
     * BOOK_DETAIL_DATA
     */
    public static final String BOOK_DETAIL_DATA = "book_detail_Bean";
    /**
     * tag
     */
    public static final String TAG = "BookDetailChapterActivity";

    private TextView textViewChapterNum, textViewSelectBlock;

    private BookDetailChapterAdapter chapterAdapter;

    private RelativeLayout layoutBlock;

    private PullLoadMoreRecycleLayout mPullLoadMoreRecyclerViewLinearLayout;
    private View layoutBlockRight;

    private CatalogSelectDialog catalogSelectDialog;

    private DianZhongCommonTitle mTitleView;

    private BookDetailChapterPresenter mPresenter;

    private NetErrorTopView netErrorTopView;
    private LinearLayout netErrorTopLayout;

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_bookdetail_chapter);
    }

    @Override
    protected void initView() {
        netErrorTopLayout = findViewById(R.id.net_error_layout_view);
        mPullLoadMoreRecyclerViewLinearLayout = findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerViewLinearLayout.setRefreshDisable();//取消下拉刷新
        mPullLoadMoreRecyclerViewLinearLayout.setLinearLayout();
        textViewChapterNum = findViewById(R.id.textView_chapterNum);
        textViewChapterNum.setVisibility(View.INVISIBLE);
        textViewSelectBlock = findViewById(R.id.textView_selectBlock);
        layoutBlockRight = findViewById(R.id.layout_blockRight);
        layoutBlock = findViewById(R.id.layout_block);
        mTitleView = findViewById(R.id.commontitle);
        catalogSelectDialog = new CatalogSelectDialog(this);

        TypefaceUtils.setHwChineseMediumFonts(textViewChapterNum);
        TypefaceUtils.setHwChineseMediumFonts(textViewSelectBlock);
    }

    @Override
    protected void initData() {

        chapterAdapter = new BookDetailChapterAdapter(this);

        mPullLoadMoreRecyclerViewLinearLayout.setAdapter(chapterAdapter);

        Intent intent = getIntent();

        BeanBookInfo beanBookInfo = (BeanBookInfo) intent.getSerializableExtra(BOOK_DETAIL_DATA);

        if (beanBookInfo == null) {
            finish();
            return;
        }

        if (!TextUtils.isEmpty(beanBookInfo.totalChapterNum)) {
            int position = beanBookInfo.totalChapterNum.indexOf("章");
            if (position != -1) {
                textViewChapterNum.setVisibility(View.VISIBLE);
                textViewChapterNum.setText(getString(R.string.In_total) + " " + beanBookInfo.totalChapterNum);
            } else {
                textViewChapterNum.setVisibility(View.VISIBLE);
                textViewChapterNum.setText(getString(R.string.In_total) + " " + beanBookInfo.totalChapterNum + " " + getString(R.string.chapter));
            }
        }

        mPresenter = new BookDetailChapterPresenter(this, beanBookInfo);
        if (NetworkUtils.getInstance().checkNet()) {
            mPresenter.getMoreChapters("", true, DialogConstants.TYPE_INIT_PAGE, null);
        } else {
            layoutBlock.setVisibility(View.GONE);
            BookDetailChapterActivity.this.showNotNetDialog();
        }


    }

    @Override
    public void addItem(BeanChapterCatalog beanChapterCatalog, boolean isInit, final BeanBlock selectBlockBean) {

        List<BeanChapterInfo> chapterInfoList = beanChapterCatalog.chapterInfoList;
        if (isInit) {
            chapterAdapter.addChapterItem(chapterInfoList, true);
        } else {
            if (chapterInfoList != null && chapterInfoList.size() > 0) {
                chapterAdapter.addChapterItem(chapterInfoList, false);
            } else {
                ToastAlone.showShort(R.string.no_more_data);
            }
        }

        List<BeanBlock> blockBeanList = beanChapterCatalog.blockList;
        if (isInit) {
            if (blockBeanList != null && blockBeanList.size() > 0) {
                catalogSelectDialog.addItem(blockBeanList);
                textViewSelectBlock.setText(blockBeanList.get(0).tip);
            }
        }

        if (selectBlockBean != null) {
            setListPosition(chapterAdapter.getIndex(selectBlockBean));
        }

        mPullLoadMoreRecyclerViewLinearLayout.setPullLoadMoreCompleted();
    }

    @Override
    public void setListPosition(final int position) {
        mPullLoadMoreRecyclerViewLinearLayout.post(new Runnable() {
            @Override
            public void run() {
                mPullLoadMoreRecyclerViewLinearLayout.setSelectionFromTop(position);
            }
        });
    }

    @Override
    protected void setListener() {
        mTitleView.setLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layoutBlockRight.setOnClickListener(this);

        chapterAdapter.setOnItemClickListener(new BookDetailChapterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, BeanChapterInfo bean) {
                if (bean == null) {
                    ToastAlone.showShort(R.string.toast_getcapter_error);
                    return;
                }
                mPresenter.loadChapter(chapterAdapter.getChapterList(), bean);
            }
        });

        catalogSelectDialog.setBlockAction(new CatalogSelectDialog.BlockAction() {
            @Override
            public void onBlockClick(int position, BeanBlock beanBlock) {
                textViewSelectBlock.setText(beanBlock.tip);
                int positionItem = chapterAdapter.getIndex(beanBlock);
                if (positionItem != -1) {
                    setListPosition(positionItem);
                } else {
                    String startChapterId = chapterAdapter.getLastChapterId();
                    mPresenter.getMoreChapters(startChapterId, false, DialogConstants.TYPE_GET_DATA, beanBlock);
                }
            }
        });

        mPullLoadMoreRecyclerViewLinearLayout.setOnPullLoadMoreListener(new PullLoadMoreRecycleLayout.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                initNetErrorStatus();
                if (NetworkUtils.getInstance().checkNet()) {
                    String startChapterId = chapterAdapter.getLastChapterId();
                    mPresenter.getMoreChapters(startChapterId, false, DialogConstants.TYPE_NO_DIALOG, null);
                } else {
                    mPullLoadMoreRecyclerViewLinearLayout.setPullLoadMoreCompleted();
                }

            }
        });

    }

    @Override
    public void dissMissDialog() {
        super.dissMissDialog();
        mPullLoadMoreRecyclerViewLinearLayout.setPullLoadMoreCompleted();
    }

    @Override
    public void intoReaderCatalogInfo(CatalogInfo catalogInfo) {
        ReaderUtils.intoReader(this, catalogInfo, catalogInfo.currentPos);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_blockRight:
                catalogSelectDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public Context getContext() {
        return this;
    }


    @Override
    public void initNetErrorStatus() {
        if (!NetworkUtils.getInstance().checkNet() && chapterAdapter != null && chapterAdapter.getItemCount() > 0) {
            initNetView();
        } else {
            destoryNetView();
        }
    }

    @Override
    public BaseActivity getHostActivity() {
        return this;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    private void initNetView() {
        if (netErrorTopView == null) {
            netErrorTopView = new NetErrorTopView(getContext());
            netErrorTopLayout.addView(netErrorTopView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 48)));
        }
    }

    private void destoryNetView() {
        if (netErrorTopView != null) {
            netErrorTopLayout.removeView(netErrorTopView);
            netErrorTopView = null;
        }
    }
}

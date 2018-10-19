package com.dzbook.activity.reader;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.dzbook.BaseTransparencyLoadActivity;
import com.dzbook.adapter.ChaseRecommendAdapter;
import com.dzbook.mvp.UI.ChaseRecommendUI;
import com.dzbook.mvp.presenter.ChaseRecommendPresenter;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.common.StatusView;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import hw.sdk.net.bean.reader.BeanBookRecomment;

/**
 * 追更
 *
 * @author lizhongzhong 2018/3/9.
 */
public class ChaseRecommendActivity extends BaseTransparencyLoadActivity implements ChaseRecommendUI {

    /**
     * tag
     */
    public static final String TAG = "ChaseRecommendActivity";
    private ChaseRecommendPresenter mPresenter;

    private DianZhongCommonTitle commonTitle;

    private RelativeLayout relativeProgressBar;

    private RecyclerView rvChaseRecommed;

    private ChaseRecommendAdapter recommendAdapter;

    private StatusView statusView;

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ac_chase_recommend);
    }

    @Override
    protected void initView() {
        commonTitle = findViewById(R.id.commontitle);
        relativeProgressBar = findViewById(R.id.relative_progressBar);
        rvChaseRecommed = findViewById(R.id.rv_chase_recommed);
        statusView = findViewById(R.id.statusView);

    }

    @Override
    protected void initData() {
        mPresenter = new ChaseRecommendPresenter(this);
        recommendAdapter = new ChaseRecommendAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvChaseRecommed.setLayoutManager(new LinearLayoutManager(this));
        rvChaseRecommed.setAdapter(recommendAdapter);

        mPresenter.getParams();
        mPresenter.getChaseRecommendBooksInfo();
        mPresenter.logZgtsjl();
    }

    @Override
    protected void setListener() {

        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                mPresenter.getChaseRecommendBooksInfo();
            }
        });
        commonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.logPv();
        }
    }

    @Override
    public void setTitle(String bookName) {
        commonTitle.setTitle(bookName);
    }

    @Override
    public void showSuccess() {
        statusView.showSuccess();
    }

    @Override
    public void showLoadProgresss() {
        if (relativeProgressBar.getVisibility() == View.GONE) {
            relativeProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void dismissProgress() {
        if (relativeProgressBar.getVisibility() == View.VISIBLE) {
            relativeProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public BaseActivity getHostActivity() {
        return this;
    }

    @Override
    public void setLoadFail() {
        dismissProgress();
        statusView.showNetError();
    }

    @Override
    public void setChaseRecommendInfo(String bookId, BeanBookRecomment beanInfo) {
        if (beanInfo != null) {
            recommendAdapter.addItemData(bookId, beanInfo.data, beanInfo, true);
        }
    }


    @Override
    public void myFinish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    @Override
    protected boolean isCustomPv() {
        return true;
    }
}

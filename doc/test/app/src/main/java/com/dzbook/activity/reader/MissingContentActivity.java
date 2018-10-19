package com.dzbook.activity.reader;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dzbook.BaseLoadActivity;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.mvp.UI.MissingContentUI;
import com.dzbook.mvp.presenter.MissingContentPresenter;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.ishugui.R;
import com.iss.app.BaseActivity;

/**
 * 阅读器异常章节处理页面
 *
 * @author lizhongzhong 2017/8/2.
 */
public class MissingContentActivity extends BaseLoadActivity implements MissingContentUI, View.OnClickListener {

    /**
     * tag
     */
    public static final String TAG = "MissingContentActivity";
    private MissingContentPresenter mPresenter;

    private TextView mTvNextChapter;

    private TextView mTvReceiveAward;

    private TextView mTvReaderChapterError1, mVWeight;

    private DianZhongCommonTitle commonTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ac_reader_error_chapter);
    }

    @Override
    protected void initView() {
        mTvNextChapter = findViewById(R.id.bt_next_chapter);
        mTvReceiveAward = findViewById(R.id.bt_receive_award);
        mTvReaderChapterError1 = findViewById(R.id.tv_reader_chapter_error1);
        mVWeight = findViewById(R.id.v_weight);
        commonTitle = findViewById(R.id.commontitle);
        TypefaceUtils.setHwChineseMediumFonts(mTvNextChapter);
        TypefaceUtils.setHwChineseMediumFonts(mTvReceiveAward);


    }

    @Override
    protected void initData() {
        mPresenter = new MissingContentPresenter(this);
        mPresenter.getParams();
        mPresenter.setPageInfo();
    }

    @Override
    protected void setListener() {
        mTvReceiveAward.setOnClickListener(this);
        mTvNextChapter.setOnClickListener(this);

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
        mPresenter.dzEvenLog();
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            int id = v.getId();
            if (id == R.id.bt_next_chapter) {

                if (mPresenter != null) {
                    mPresenter.loadNextChapter();
                }

            } else if (id == R.id.bt_receive_award) {
                if (mPresenter != null) {
                    mPresenter.receiveAward();
                }
            }
        }
    }

    @Override
    public BaseActivity getHostActivity() {
        return this;
    }

    @Override
    public void setAlreadyReceveAward() {
        mTvReceiveAward.setText(R.string.already_received);
        mTvReceiveAward.setClickable(false);
        mTvReceiveAward.setEnabled(false);
    }

    @Override
    public void setDeleteChapterReceiveAwardShow() {
        mTvReceiveAward.setVisibility(View.GONE);
        mVWeight.setVisibility(View.GONE);
        mTvReaderChapterError1.setText(getResources().getString(R.string.reader_chapter_error_tips2));
    }

    @Override
    public void setNormalReceiveAwardShow() {

    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    public void intoReaderCatalogInfo(CatalogInfo catalogInfo) {
        ReaderUtils.intoReader(this, catalogInfo, catalogInfo.currentPos);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destory();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void setTitle(String pName) {
        commonTitle.setTitle(pName);
    }
}

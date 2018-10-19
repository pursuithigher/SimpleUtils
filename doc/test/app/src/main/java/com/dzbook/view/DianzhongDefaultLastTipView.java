package com.dzbook.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.AppConst;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.presenter.GuidePresenterImpl;
import com.dzbook.utils.HwLog;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

/**
 * DianzhongDefaultLastTipView
 *
 * @author dongdianzhou on 2017/8/22.
 */
public class DianzhongDefaultLastTipView extends RelativeLayout implements View.OnClickListener {

    private Button mButtonJump;
    private LinearLayout mLlMan;
    private LinearLayout mLlWoman;
    private GuidePresenterImpl mPresenter;

    private long lastDetailTime = 0;

    /**
     * 构造
     *
     * @param context context
     */
    public DianzhongDefaultLastTipView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public DianzhongDefaultLastTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        mLlMan.setOnClickListener(this);
        mLlWoman.setOnClickListener(this);
        mButtonJump.setOnClickListener(this);
    }

    private void initData() {
    }

    public void setPresenter(GuidePresenterImpl presenter) {
        this.mPresenter = presenter;
    }

    private void initView() {
        int chineseLength = 16;
        int chineseLine = 1;
        int englishLine = 2;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.a_guide_v3, this);
        mButtonJump = view.findViewById(R.id.btn_guide_jump);
        mLlMan = view.findViewById(R.id.iv_guide_select_man);
        mLlWoman = view.findViewById(R.id.iv_guide_select_gril);
        TextView mTvGuideLastTips2 = view.findViewById(R.id.tv_guide_last_tips2);
        TextView mTvGuideLastTips1 = view.findViewById(R.id.tv_guide_last_tips1);
        TextView mTvMan = view.findViewById(R.id.tv_man);
        TextView mTvWoman = view.findViewById(R.id.tv_woman);
        TypefaceUtils.setHwChineseMediumFonts(mTvGuideLastTips1);
        TypefaceUtils.setHwChineseMediumFonts(mTvMan);
        TypefaceUtils.setHwChineseMediumFonts(mTvWoman);
        if (chineseLength < mTvGuideLastTips2.getText().length()) {
            mTvGuideLastTips2.setLines(englishLine);
        } else {
            mTvGuideLastTips2.setLines(chineseLine);
        }
    }

    public Activity getActivity() {
        return (Activity) getContext();
    }

    /**
     * 开始动画
     *
     * @param isBoy isBoy
     */
    public void startSelectAnim(final boolean isBoy) {

        if (isBoy) {
            HwLog.setPh("1");
            SpUtil.getinstance(getActivity()).setPersonReadPref(1);
            DzLog.getInstance().logClick(LogConstants.MODULE_YDYM, LogConstants.ZONE_YDYM_YHPH, "1", null, null);
        } else {
            HwLog.setPh("2");
            SpUtil.getinstance(getActivity()).setPersonReadPref(2);
            DzLog.getInstance().logClick(LogConstants.MODULE_YDYM, LogConstants.ZONE_YDYM_YHPH, "2", null, null);
        }
        SpUtil.getinstance(getActivity()).setPersonExistsReadPref(true);

        mPresenter.finishGuide();

    }

    @Override
    public void onClick(View v) {

        if (v != null) {
            int id = v.getId();
            if (id == R.id.iv_guide_select_man) {
                long thisTime = System.currentTimeMillis();
                if (thisTime - lastDetailTime > AppConst.MAX_CLICK_INTERVAL_TIME) {
                    lastDetailTime = thisTime;
                    startSelectAnim(true);
                }
            } else if (id == R.id.iv_guide_select_gril) {

                long thisTime = System.currentTimeMillis();
                if (thisTime - lastDetailTime > AppConst.MAX_CLICK_INTERVAL_TIME) {
                    lastDetailTime = thisTime;
                    startSelectAnim(false);
                }
            } else if (id == R.id.btn_guide_jump) {
                //设置了男女之后再点击跳过，则不再设置阅读偏好为默认
                boolean isExistReadPref = SpUtil.getinstance(getActivity()).getPersonExistsReadPref();
                if (!isExistReadPref) {
                    HwLog.setPh("0");
                    SpUtil.getinstance(getActivity()).setPersonReadPref(0);
                    SpUtil.getinstance(getActivity()).setPersonExistsReadPref(true);
                }

                DzLog.getInstance().logClick(LogConstants.MODULE_YDYM, LogConstants.ZONE_YDYM_YHPH, "3", null, null);
                mPresenter.finishGuide();
            }
        }
    }

}

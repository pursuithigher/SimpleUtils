package com.dzbook.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.RankTopUI;
import com.dzbook.mvp.presenter.RankTopPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import java.util.List;

import hw.sdk.net.bean.BeanRankTopResBeanInfo;

/**
 * 排行榜头布局
 *
 * @author lizhongzhong 2018/3/10.
 */
public class RankTopTopView extends LinearLayout {
    String firstId = "-1";
    String secondId = "-1";

    private LinearLayout linearFirstRankTop, linearSecondRankTop;

    private HorizontalScrollView hScrollviewSecondRankTop;

    private int firstMenuCheckedPosition = -1;

    private int secondMenuCheckedPosition = -1;

    private SparseArray<RankTopFirstItemView> firstMenuArray = new SparseArray<RankTopFirstItemView>();

    private SparseArray<TextView> secondMenuArray = new SparseArray<TextView>();

    private Context mContext;

    private RankTopPresenter mRankTopPresenter;

    private HorizontalScrollView scrollView;

    private RankTopUI mUI;

    /**
     * 构造
     *
     * @param context context
     */
    public RankTopTopView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public RankTopTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView(attrs);
        initData();
        setListener();
    }

    public void setRankTopPresenter(RankTopPresenter presenter) {
        this.mRankTopPresenter = presenter;
    }

    public void setRankTopUI(RankTopUI ui) {
        this.mUI = ui;
    }

    private void initView(AttributeSet attrs) {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        setOrientation(VERTICAL);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_rank_top, this);
        linearFirstRankTop = view.findViewById(R.id.linear_first_rank_top);
        linearSecondRankTop = view.findViewById(R.id.linear_second_rank_top);
        hScrollviewSecondRankTop = view.findViewById(R.id.hscrollview_second_rank_top);
        scrollView = view.findViewById(R.id.scroll_view);
    }


    private void initData() {
        linearFirstRankTop.removeAllViews();
        linearSecondRankTop.removeAllViews();
        setVisibility(View.GONE);
    }

    private void setListener() {

    }

    /**
     * 绑定数据
     *
     * @param beanInfo beanInfo
     */
    public void bindData(BeanRankTopResBeanInfo beanInfo) {
        if (beanInfo != null) {
            setVisibility(View.VISIBLE);
            firstCreateMenuData(beanInfo.rankTopResBean);
        }
    }

    /**
     * firstCreateMenuData
     *
     * @param rankTopResBean rankTopResBean
     */
    public void firstCreateMenuData(final List<BeanRankTopResBeanInfo.RandTopBean> rankTopResBean) {

        firstMenuCheckedPosition = -1;
        secondMenuCheckedPosition = -1;
        firstMenuArray.clear();
        secondMenuArray.clear();

        if (rankTopResBean != null && rankTopResBean.size() > 0) {

            if (rankTopResBean.size() == 1) {
                mRankTopPresenter.removeRecycleViewHeader();
                return;
            }

            linearFirstRankTop.removeAllViews();
            scrollView.setFillViewport(rankTopResBean.size() > 4);

            int size = rankTopResBean.size();
            for (int i = 0; i < size; i++) {
                final BeanRankTopResBeanInfo.RandTopBean bean = rankTopResBean.get(i);
                if (bean != null) {

                    final RankTopFirstItemView firstItemView = new RankTopFirstItemView(mContext);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.rightMargin = DimensionPixelUtil.dip2px(mContext, 24);
                    if (i == size - 1) {
                        linearFirstRankTop.addView(firstItemView);
                    } else {
                        linearFirstRankTop.addView(firstItemView, layoutParams);
                    }
                    firstItemView.bindData(bean);
                    firstMenuArray.put(i, firstItemView);
                    firstItemView.setTag(bean.rankSecondResBeans);

                    if (firstMenuCheckedPosition < 0) {
                        firstMenuCheckedPosition = 0;
                    }

                    if (i == firstMenuCheckedPosition) {
                        firstId = bean.id;
                        secondId = "-1";
                        firstItemView.select();
                    } else {
                        firstItemView.unSelect();
                    }

                    firstItemView.setOnClickListener(new FirstItemOnClickListener(bean, firstItemView));
                }
            }

            BeanRankTopResBeanInfo.RandTopBean bean = rankTopResBean.get(0);
            if (bean != null && bean.rankSecondResBeans != null && bean.rankSecondResBeans.size() > 0) {
                mUI.setLoadProgressMarginTop(true);
                createSecondRandData(bean.rankSecondResBeans, true);
            } else {
                mUI.setLoadProgressMarginTop(false);
                if (bean != null && !TextUtils.isEmpty(bean.id) && mRankTopPresenter != null) {
                    mRankTopPresenter.setLoadParentId(bean.id);
                }
                hScrollviewSecondRankTop.setVisibility(View.GONE);
                linearSecondRankTop.setVisibility(View.GONE);
            }
        }
    }

    private void createSecondRandData(List<BeanRankTopResBeanInfo.RandSecondBean> beanList, boolean isFirstLoad) {
        if (beanList != null && beanList.size() > 0) {
            secondMenuCheckedPosition = -1;
            linearSecondRankTop.setVisibility(View.VISIBLE);
            hScrollviewSecondRankTop.setVisibility(View.VISIBLE);
            linearSecondRankTop.removeAllViews();

            int size = beanList.size();
            for (int i = 0; i < size; i++) {
                final BeanRankTopResBeanInfo.RandSecondBean bean = beanList.get(i);
                if (bean != null) {
                    final TextView textView = createSecondRankView(bean.name);
                    linearSecondRankTop.addView(textView);
                    secondMenuArray.put(i, textView);
                    textView.setTag(bean);

                    if (secondMenuCheckedPosition < 0) {
                        secondMenuCheckedPosition = 0;
                    }

                    if (i == secondMenuCheckedPosition) {
                        secondId = bean.id;
                        textView.setSelected(true);
                        TypefaceUtils.setHwChineseMediumFonts(textView);
                    } else {
                        textView.setSelected(false);
                        TypefaceUtils.setRegularFonts(textView);
                    }

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            secondId = bean.id;
                            DzLog.getInstance().logClick(LogConstants.MODULE_PHBT, LogConstants.ZONE_PHBEJFL, bean.id, null, "");
                            BeanRankTopResBeanInfo.RandSecondBean secondBean = (BeanRankTopResBeanInfo.RandSecondBean) textView.getTag();
                            int position = secondMenuArray.indexOfValue(textView);
                            setSecondMenuSelect(position, secondBean);
                            TypefaceUtils.setHwChineseMediumFonts(textView);
                            for (int i1 = 0; i1 < secondMenuArray.size(); i1++) {
                                if (i1 != position) {
                                    TypefaceUtils.setRegularFonts(secondMenuArray.get(i1));
                                }
                            }
                        }
                    });
                }
            }
            if (!isFirstLoad) {
                if (mRankTopPresenter != null) {
                    mRankTopPresenter.getClickRankTopInfo(beanList.get(0).firstId, beanList.get(0).id);
                }
            } else {
                if (beanList.get(0) != null) {
                    if (!TextUtils.isEmpty(beanList.get(0).firstId)) {
                        mRankTopPresenter.setLoadParentId(beanList.get(0).firstId);
                    }

                    if (!TextUtils.isEmpty(beanList.get(0).id)) {
                        mRankTopPresenter.setLoadSubId(beanList.get(0).id);
                    }
                }
            }
        }
    }

    public String getCurrentInfo() {
        return firstId + "_" + secondId;
    }

    private TextView createSecondRankView(String name) {

        TextView tv = new TextView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.rightMargin = DimensionPixelUtil.dip2px(mContext, 21);
        tv.setTextSize(14);
        ColorStateList colorStateList = CompatUtils.getColorStateList(getContext(), R.color.color_rank_top_text);
        tv.setTextColor(colorStateList);
        tv.setLayoutParams(params);
        tv.setText(name);
        tv.setGravity(Gravity.CENTER_VERTICAL);

        return tv;
    }

    private void setFirstMenuSelect(int position) {
        firstMenuArray.get(position).select();
        if (position != firstMenuCheckedPosition) {
            firstMenuArray.get(firstMenuCheckedPosition).unSelect();
        }
        firstMenuCheckedPosition = position;
    }

    private void setSecondMenuSelect(int position, BeanRankTopResBeanInfo.RandSecondBean bean) {
        secondMenuArray.get(position).setSelected(true);
        if (position != secondMenuCheckedPosition) {
            secondMenuArray.get(secondMenuCheckedPosition).setSelected(false);
        }
        secondMenuCheckedPosition = position;
        if (mRankTopPresenter != null) {
            mRankTopPresenter.getClickRankTopInfo(bean.firstId, bean.id);
        }
    }

    /**
     * 销毁
     */
    public void destory() {
        if (firstMenuArray != null) {
            firstMenuArray.clear();
        }

        if (secondMenuArray != null) {
            secondMenuArray.clear();
        }
    }

    /**
     * FirstItemOnClickListener
     */
    private class FirstItemOnClickListener implements OnClickListener {
        private final BeanRankTopResBeanInfo.RandTopBean bean;
        private final RankTopFirstItemView firstItemView;

        FirstItemOnClickListener(BeanRankTopResBeanInfo.RandTopBean bean, RankTopFirstItemView firstItemView) {
            this.bean = bean;
            this.firstItemView = firstItemView;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onClick(View v) {
            firstId = bean.id;
            secondId = "-1";
            DzLog.getInstance().logClick(LogConstants.MODULE_PHBT, LogConstants.ZONE_PHBYJFL, bean.id, null, "");

            List<BeanRankTopResBeanInfo.RandSecondBean> secondBeans = (List<BeanRankTopResBeanInfo.RandSecondBean>) firstItemView.getTag();
            int position = firstMenuArray.indexOfValue(firstItemView);

            setFirstMenuSelect(position);

            if (secondBeans == null || secondBeans.size() <= 0) {
                //通过一级菜单的id去请求数据
                linearSecondRankTop.setVisibility(View.GONE);
                hScrollviewSecondRankTop.setVisibility(View.GONE);
                mUI.setLoadProgressMarginTop(false);

                if (mRankTopPresenter != null) {
                    mRankTopPresenter.getClickRankTopInfo(bean.id, "");
                }

            } else {
                mUI.setLoadProgressMarginTop(true);
                createSecondRandData(secondBeans, false);
            }
        }
    }
}

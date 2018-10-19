package com.dzbook.view.type;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.presenter.MainTypeDetailPresenterImpl;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.search.ExpandFlowLayout;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hw.sdk.net.bean.type.BeanCategoryMark;
import hw.sdk.net.bean.type.BeanMainType;
import hw.sdk.net.bean.type.BeanMainTypeDetail;
import hw.sdk.net.bean.type.BeanSortMark;
import hw.sdk.net.bean.type.BeanStatusMark;
import hw.sdk.net.bean.type.BeanTypeInterface;

/**
 * 分类页面 二级菜单 recycleView的顶部
 *
 * @author Winzows 2018/3/6
 */

public class MainTypeDetailTopView extends RelativeLayout {

    /**
     * top
     */
    public static final int TYPE_TOP_VIEW = 0x001;

    /**
     * sub
     */
    public static final int TYPE_SUP_VIEW = 0x002;

    /**
     * 第二部分全部按钮
     */
    private static final String DE_ALL = "0";

    /**
     * 第三部分全部按钮
     */
    private static final String DS_ALL = "0";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_TYPE = "type";

    /**
     * 第一部分固化id
     */

    public String firstGhId = "-1";
    /**
     * 第二部分固化id
     */
    public String secondGhId = "-1";
    /**
     * 第三部分固化id
     */
    public String thirdGhId = "-1";
    private ExpandFlowLayout flowMark1, flowMark2, flowMark3;
    private MainTypeDetailPresenterImpl typeDetailPresenter;
    private BeanMainTypeDetail.TypeFilterBean filterBean;
    private TextView tvSecondLeftAll, tvThreeLeftAll;
    private LinearLayout llSecondLine, rlBaseview, llThirdLine;

    /**
     * 记录当前点击的按钮
     */
    private TextView firstLineLastClickView, secondLineLastClickView, threeLineLastClickView;
    private int viewType = TYPE_TOP_VIEW;
    private String defaultSelectCid;

    private int childPaddingV = 0, childPaddingH = 0;

    /**
     * 构造
     *
     * @param context context
     */
    public MainTypeDetailTopView(Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public MainTypeDetailTopView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 构造
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    public MainTypeDetailTopView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        childPaddingH = DimensionPixelUtil.dip2px(context, 8);
        childPaddingV = DimensionPixelUtil.dip2px(context, 12);
        View baseView = LayoutInflater.from(context).inflate(R.layout.view_native_type_detail_top, this, true);
        flowMark1 = baseView.findViewById(R.id.flowMark1);
        flowMark2 = baseView.findViewById(R.id.flowMark2);
        flowMark3 = baseView.findViewById(R.id.flowMark3);
        llSecondLine = baseView.findViewById(R.id.llSecondLine);
        llThirdLine = baseView.findViewById(R.id.llThirdLine);
        rlBaseview = baseView.findViewById(R.id.rl_baseView);
        tvSecondLeftAll = baseView.findViewById(R.id.tv_mark_title);
        tvSecondLeftAll.setSelected(true);
        TypefaceUtils.setHwChineseMediumFonts(tvSecondLeftAll);

        Bundle bundle = new Bundle();
        bundle.putString(TAG_TITLE, getResources().getString(R.string.str_all));
        tvSecondLeftAll.setTag(bundle);

        tvThreeLeftAll = baseView.findViewById(R.id.tvStatus);
        tvThreeLeftAll.setSelected(true);
        TypefaceUtils.setHwChineseMediumFonts(tvThreeLeftAll);

        //setTag 主要是为了 方便悬浮窗从tag里取title
        bundle = new Bundle();
        bundle.putString(TAG_TITLE, getResources().getString(R.string.str_all));
        tvThreeLeftAll.setTag(bundle);

        //初始化打点固化信息
        secondGhId = DE_ALL;
        thirdGhId = DS_ALL;
        initListener();

        secondLineLastClickView = tvSecondLeftAll;
        threeLineLastClickView = tvThreeLeftAll;
    }

    private void initListener() {
        tvSecondLeftAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (secondLineLastClickView != null) {
                    secondLineLastClickView.setSelected(false);
                    TypefaceUtils.setRegularFonts(secondLineLastClickView);
                }
                secondLineLastClickView = tvSecondLeftAll;
                filterBean.setTid("");
                secondGhId = DE_ALL;
                request(secondGhId, "2", "");
                tvSecondLeftAll.setSelected(true);
                TypefaceUtils.setHwChineseMediumFonts(tvSecondLeftAll);
            }
        });

        tvThreeLeftAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (threeLineLastClickView != null) {
                    threeLineLastClickView.setSelected(false);
                    TypefaceUtils.setRegularFonts(threeLineLastClickView);
                }
                threeLineLastClickView = tvThreeLeftAll;
                thirdGhId = DS_ALL;
                filterBean.setStatus("");
                request(secondGhId, "3", "");
                tvThreeLeftAll.setSelected(true);
                TypefaceUtils.setHwChineseMediumFonts(tvThreeLeftAll);
            }
        });

        rlBaseview.setOnClickListener(null);
    }

    /**
     * 打点的map
     *
     * @param lineNumb 打点
     * @return 打点Map
     */
    public HashMap<String, String> getMap(String lineNumb) {
        HashMap<String, String> hashMap = new HashMap<>(16);
        hashMap.put("line_numb", lineNumb);
        return hashMap;
    }

    /**
     * 获取当前的固化信息
     *
     * @return 打点信息
     */
    public String getCurrentGHInfo() {
        return firstGhId + "_" + secondGhId + "_" + thirdGhId;
    }

    /**
     * 分类 第一行
     * 热门书籍  评分最高啊 这种的
     *
     * @param sortMarkList 数据源
     * @param isClick      isClick
     */
    public void bindFirstMarkData(final List<BeanSortMark> sortMarkList, boolean isClick) {
        if (flowMark1 != null) {
            if (sortMarkList != null && sortMarkList.size() > 0) {
                if (!isClick) {
                    BeanSortMark sortMark = sortMarkList.get(0);
                    if (null != sortMark) {
                        sortMark.isChecked = true;
                        firstGhId = !TextUtils.isEmpty(sortMark.markId) ? sortMark.markId : "-1";
                    }
                }
                ArrayList<View> viewList = new ArrayList<>();
                for (int i = 0; i < sortMarkList.size(); i++) {
                    final BeanSortMark sortMark = sortMarkList.get(i);
                    if (!TextUtils.isEmpty(sortMark.title)) {
                        TextView title = getTextView(sortMark);
                        if (i == 0) {
                            title.setSelected(true);
                            TypefaceUtils.setHwChineseMediumFonts(title);
                            firstLineLastClickView = title;
                        }
                        viewList.add(title);
                    }
                }

                flowMark1.addView(viewList);
            } else {
                flowMark1.setVisibility(GONE);
                if (filterBean != null) {
                    filterBean.setSort("1");
                }
            }
        }
    }

    /**
     * 统一封装的 获取通用View
     *
     * @return view
     */
    private TextView getTextView(BeanTypeInterface beanTypeInterface) {

        final TextView textView = new TextView(getContext());
        textView.setPadding(childPaddingH, childPaddingV, childPaddingH, childPaddingV);
        textView.setMaxEms(10);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextSize(12);
        textView.setTextColor(CompatUtils.getColorStateList(getContext(), R.color.color_pd0_text));

        Bundle bundle = new Bundle();
        bundle.putString(TAG_ID, beanTypeInterface.getMarkId());
        bundle.putString(TAG_TITLE, beanTypeInterface.getTitle());
        bundle.putString(TAG_TYPE, beanTypeInterface.getType());

        textView.setTag(bundle);

        textView.setClickable(true);
        textView.setText(beanTypeInterface.getTitle());

        textView.setOnClickListener(new TvOnClickListener(textView));
        return textView;
    }


    /**
     * 分类 第一行
     *
     * @param sortMarkList sortMarkList
     */
    public void bindFirstMarkData(List<BeanSortMark> sortMarkList) {
        bindFirstMarkData(sortMarkList, false);
    }

    /**
     * 三级分类id 第二行的 前三行
     *
     * @param categoryMarkList 元数据
     */
    public void bindCategoryData(final List<BeanCategoryMark> categoryMarkList) {
        if (flowMark2 != null && categoryMarkList != null && categoryMarkList.size() > 0) {
            //除了全部按钮外 有没有别的按钮也被选上了
            ArrayList<View> viewList = new ArrayList<>();
            for (int i = 0; i < categoryMarkList.size(); i++) {
                final BeanCategoryMark categoryMark = categoryMarkList.get(i);
                if (!TextUtils.isEmpty(categoryMark.title)) {
                    TextView textView = getTextView(categoryMark);
                    if (!TextUtils.isEmpty(defaultSelectCid)) {
                        if (TextUtils.equals(defaultSelectCid, categoryMark.getTitle())) {
                            textView.setSelected(true);
                            TypefaceUtils.setHwChineseMediumFonts(textView);

                            tvSecondLeftAll.setSelected(false);
                            TypefaceUtils.setRegularFonts(tvSecondLeftAll);

                            secondLineLastClickView = textView;
                            filterBean.setTid(defaultSelectCid);
                            request(defaultSelectCid, "2", "");
                        }
                    } else {
                        textView.setSelected(categoryMark.isChecked);
                        if (categoryMark.isChecked) {
                            TypefaceUtils.setHwChineseMediumFonts(textView);
                        } else {
                            TypefaceUtils.setRegularFonts(textView);
                        }
                    }
                    viewList.add(textView);
                }
            }
            flowMark2.addView(viewList);
        } else {
            llSecondLine.setVisibility(GONE);
            if (filterBean != null) {
                filterBean.setTid("");
            }
        }
    }

    /**
     * 绑定书籍状态 第三行
     *
     * @param statusMarkList 元数据
     */
    public void bindBookStatusData(final List<BeanStatusMark> statusMarkList) {
        if (flowMark3 != null && statusMarkList != null && statusMarkList.size() > 0) {
            ArrayList<View> arrayList = new ArrayList<>();
            for (final BeanStatusMark statusMark : statusMarkList) {
                if (!TextUtils.isEmpty(statusMark.title)) {
                    arrayList.add(getTextView(statusMark));
                }
            }
            flowMark3.addView(arrayList);
        } else {
            llThirdLine.setVisibility(GONE);
            if (filterBean != null) {
                filterBean.setStatus("");
            }
        }
    }

    /**
     * presenter
     *
     * @param typeDetailPresenter presenter
     */
    public void setTypeDetailPresenter(MainTypeDetailPresenterImpl typeDetailPresenter) {
        this.typeDetailPresenter = typeDetailPresenter;
    }

    /**
     * 筛选条件
     *
     * @param filterBean 上传查数据的bean
     */
    public void setFilterBean(BeanMainTypeDetail.TypeFilterBean filterBean) {
        this.filterBean = filterBean;
    }

    /**
     * 请求数据
     *
     * @param adid     adid
     * @param lineNumb lineNumb
     * @param trackId  trackId
     */
    @SuppressLint("WrongConstant")
    public void request(String adid, String lineNumb, String trackId) {
        if (null != filterBean) {
            DzLog.getInstance().logClick(LogConstants.MODULE_FLEJT, filterBean.getCid(), adid, getMap(lineNumb), trackId);
        }
        if (viewType == TYPE_SUP_VIEW) {
            EventBusUtils.sendMessage(EventConstant.CODE_TYPE_SUBVIEW_CLICK, EventConstant.TYPE_MAIN_TYPE_SUBVIEW_CLICK, null);
        }

        if (typeDetailPresenter != null && filterBean != null) {
            typeDetailPresenter.onRequestStart();
            typeDetailPresenter.requestData(MainTypeDetailPresenterImpl.LOAD_TYPE_LOAD_SWITCH, filterBean);
        }
    }


    /**
     * 获取悬浮窗的文本内容
     *
     * @return 三个title拼接
     */
    public String getSubTitleStr() {
        StringBuilder titleStr = new StringBuilder();
        appendFirstLineTitle(titleStr);
        appendSecondLineTitle(titleStr);
        appendThreeLineTitle(titleStr);
        if (TextUtils.isEmpty(titleStr.toString())) {
            return "全部";
        }
        return titleStr.toString();
    }

    private void appendFirstLineTitle(StringBuilder titleStr) {
        Bundle bundle;
        if (flowMark1 != null && flowMark1.getVisibility() == VISIBLE && firstLineLastClickView != null) {
            bundle = (Bundle) firstLineLastClickView.getTag();
            titleStr.append(bundle.getString(TAG_TITLE));
        }
    }

    private void appendSecondLineTitle(StringBuilder titleStr) {
        Bundle bundle;
        if (flowMark2 != null && flowMark2.getVisibility() == VISIBLE && secondLineLastClickView != null) {
            bundle = (Bundle) secondLineLastClickView.getTag();
            String secondTitle = bundle.getString(TAG_TITLE);
            if (!TextUtils.equals(secondTitle, getContext().getResources().getString(R.string.str_all))) {
                if (!TextUtils.isEmpty(titleStr.toString())) {
                    titleStr.append("/").append(secondTitle);
                } else {
                    titleStr.append(secondTitle);
                }
            }
        }
    }

    private void appendThreeLineTitle(StringBuilder titleStr) {
        Bundle bundle;
        if (flowMark3 != null && flowMark3.getVisibility() == VISIBLE && threeLineLastClickView != null) {
            bundle = (Bundle) threeLineLastClickView.getTag();
            String thirdTitle = bundle.getString(TAG_TITLE);
            if (!TextUtils.equals(thirdTitle, getContext().getResources().getString(R.string.str_all))) {
                if (!TextUtils.isEmpty(titleStr.toString())) {
                    titleStr.append("/").append(thirdTitle);
                } else {
                    titleStr.append(thirdTitle);
                }
            }
        }
    }


    public void setViewType(int viewType) {
        this.viewType = viewType;
        if (viewType == TYPE_SUP_VIEW) {
            flowMark2.toggleExpand();
        }
    }

    public void setDefaultSelectTag(String cid) {
        this.defaultSelectCid = cid;
    }


    /**
     * ClickListener
     */
    private class TvOnClickListener implements OnClickListener {
        private final TextView textView;

        public TvOnClickListener(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void onClick(View v) {
            Bundle getBundle = (Bundle) textView.getTag();
            String type = getBundle.getString(TAG_TYPE);
            String id = getBundle.getString(TAG_ID);
            if (!TextUtils.isEmpty(type)) {
                String adId = !TextUtils.isEmpty(id) ? id : "-1";
                switch (type) {
                    case BeanMainType.TYPE_FIRST:
                        if (firstLineLastClickView != null) {
                            firstLineLastClickView.setSelected(false);
                            TypefaceUtils.setRegularFonts(firstLineLastClickView);
                        }
                        firstLineLastClickView = textView;
                        if (filterBean != null) {
                            filterBean.setSort(id);
                        }
                        request(adId, "1", "");
                        break;
                    case BeanMainType.TYPE_SECOND:
                        if (secondLineLastClickView != null) {
                            secondLineLastClickView.setSelected(false);
                            TypefaceUtils.setRegularFonts(secondLineLastClickView);
                        }
                        secondLineLastClickView = textView;
                        filterBean.setTid(id);
                        request(adId, "2", "");
                        break;
                    case BeanMainType.TYPE_THREE:
                        if (threeLineLastClickView != null) {
                            threeLineLastClickView.setSelected(false);
                            TypefaceUtils.setRegularFonts(threeLineLastClickView);
                        }
                        filterBean.setStatus(id);
                        threeLineLastClickView = textView;
                        thirdGhId = !TextUtils.isEmpty(id) ? id : "-1";
                        request(adId, "3", "");
                        break;
                    default:
                        break;
                }
            }
            textView.setSelected(true);
            TypefaceUtils.setHwChineseMediumFonts(textView);

        }
    }
}

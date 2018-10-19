package com.dzbook.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.DzSpanBuilder;
import com.dzpay.recharge.netbean.RechargeProductBean;
import com.ishugui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * RechargeWayMoneyAdapter
 *
 * @author lizz 2018/4/16.
 */
public class RechargeWayMoneyAdapter extends BaseAdapter {


    /**
     * 充值金额集合
     */
    private List<RechargeProductBean> rechargeListBeans;

    private Activity mActivity;

    /**
     * 选中位置
     */
    private int selectPosition;

    /**
     * 构造
     *
     * @param context context
     */
    public RechargeWayMoneyAdapter(Activity context) {
        this.mActivity = context;
        this.rechargeListBeans = new ArrayList<RechargeProductBean>();
    }

    /**
     * 添加数据
     *
     * @param list  list
     * @param clear clear
     */
    public void addItems(List<RechargeProductBean> list, boolean clear) {

        if (clear) {
            rechargeListBeans.clear();
        }
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                rechargeListBeans.add(list.get(i));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return rechargeListBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return rechargeListBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = View.inflate(mActivity, R.layout.item_recharge_money, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RechargeProductBean info = rechargeListBeans.get(position);

        setData(info, selectPosition, position, viewHolder);

        return convertView;
    }


    /**
     * 设置数据
     *
     * @param bean       bean
     * @param selectPos  selectPos
     * @param position   position
     * @param viewHolder viewHolder
     */
    public void setData(RechargeProductBean bean, int selectPos, int position, ViewHolder viewHolder) {
        initData(viewHolder);

        boolean isSelected = selectPos == position;
        DzSpanBuilder dzSpan = new DzSpanBuilder();
        int blackColor = CompatUtils.getColor(mActivity, R.color.color_50_000000);

        if (bean != null) {
            viewHolder.rbRechargeBg.setChecked(isSelected);

            if (isSelected) {
                int color = CompatUtils.getColor(mActivity, R.color.color_100_FFFFFF);
                viewHolder.tvMoneyName.setTextColor(color);
            } else {
                int color = CompatUtils.getColor(mActivity, R.color.color_100_FB6522);
                viewHolder.tvMoneyName.setTextColor(color);
            }
            viewHolder.tvMoneyName.setText(bean.amount);

            if (!TextUtils.isEmpty(bean.give)) {
                if (isSelected) {
                    int color = CompatUtils.getColor(mActivity, R.color.color_100_FFFFFF);
                    dzSpan.appendColor(bean.product, color).appendColor("+" + bean.give, color);
                    viewHolder.tvMoneyTipsBLBR.setText(dzSpan);
                } else {
                    int color = CompatUtils.getColor(mActivity, R.color.color_100_FB6522);
                    dzSpan.appendColor(bean.product, blackColor).appendColor("+" + bean.give, color);
                    viewHolder.tvMoneyTipsBLBR.setText(dzSpan);
                }

            } else {
                if (isSelected) {
                    int color = CompatUtils.getColor(mActivity, R.color.color_100_FFFFFF);
                    dzSpan.appendColor(bean.product, color);
                    viewHolder.tvMoneyTipsBLBR.setText(dzSpan);

                } else {
                    dzSpan.appendColor(bean.product, blackColor);
                    viewHolder.tvMoneyTipsBLBR.setText(dzSpan);
                }
            }

            if (!TextUtils.isEmpty(bean.corner)) {
                viewHolder.rlCorner.setVisibility(View.VISIBLE);
                viewHolder.tvCornerRate.setText(bean.corner + "");
            } else {
                viewHolder.rlCorner.setVisibility(View.GONE);
            }
        }

    }


    /**
     * 初始化数据
     *
     * @param viewHolder viewHolder
     */
    public void initData(ViewHolder viewHolder) {
        viewHolder.tvMoneyName.setText("");
        viewHolder.tvCornerRate.setText("");
        viewHolder.tvMoneyTipsBLBR.setText("");
        viewHolder.rlCorner.setVisibility(View.GONE);


        viewHolder.rlCorner.setVisibility(View.GONE);
        viewHolder.rbRechargeBg.setChecked(false);

        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(mActivity, 76));
        viewHolder.rootView.setLayoutParams(params);
    }


    /**
     * ViewHolder
     */
    private static class ViewHolder {

        public TextView tvMoneyName;

        public TextView tvCornerRate;

        public TextView tvMoneyTipsBLBR;

        public RadioButton rbRechargeBg;

        public TextView rlCorner;

        public View rootView;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            tvMoneyName = rootView.findViewById(R.id.textview_money_name);
            tvCornerRate = rootView.findViewById(R.id.tv_corner_rate);
            tvMoneyTipsBLBR = rootView.findViewById(R.id.textview_money_tipsBL_BR);
            rbRechargeBg = rootView.findViewById(R.id.radiobutton_recharge_bg);
            rlCorner = rootView.findViewById(R.id.relative_corner);
        }
    }

    /**
     * 选中位置
     *
     * @param position position
     */
    public void setSelectionPosition(int position) {
        selectPosition = position;
        notifyDataSetChanged();
    }
}

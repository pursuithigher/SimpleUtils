package com.dzbook.view.order;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzbook.activity.vip.MyVipActivity;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.DzSpanBuilder;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.common.BookImageView;
import com.dzpay.recharge.netbean.OrdersCommonBean;
import com.ishugui.R;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

/**
 * CommonOrdersView
 *
 * @author lizz 2018/4/16.
 */

public class CommonOrdersView extends RelativeLayout {

    private Context mContext;

    private LinearLayout linearAutoOrder;

    private RelativeLayout rlOpenVip;

    private CheckBox cbAutoOrderSw;

    private TextView tvSerialOrderTitle, tvSerialOrderName;

    private TextView tvPrice, tvVouchersDeduction, tvOpenVip, tvNeedPay, tvNeedPayHint;

    private TextView tvRemain, tvVoucher, tvVouchersDeductionTitle, tvVoucherTitle;

    private TextView tvBookName, tvBookAuthor, tvRechargeTitle, tvRechargeName, tvBookChapter;

    private RelativeLayout tvSingleLayout, tvSerialLayout, tvVoucherLayout;

    private View tvSingleLine;

    private BookImageView bookImageView;

    private OrdersCommonBean mBean;

    private boolean isLotOrder = false;

    private boolean isRechargeOrder = false;

    private boolean isEnglish;

    /**
     * 构造
     *
     * @param context context
     */
    public CommonOrdersView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CommonOrdersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
        setListener();
    }


    private void initView() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dz_recharge_orders_info, this);

        tvRechargeTitle = view.findViewById(R.id.tv_single_order_title);
        tvRechargeName = view.findViewById(R.id.tv_single_order_name);
        bookImageView = view.findViewById(R.id.order_book_image_view);
        tvBookName = view.findViewById(R.id.order_book_name);
        tvBookAuthor = view.findViewById(R.id.order_book_author);
        tvBookChapter = view.findViewById(R.id.order_book_chapter);
        tvSingleLayout = view.findViewById(R.id.orders_head_layout);
        tvSingleLine = view.findViewById(R.id.orders_head_line);
        tvSerialLayout = view.findViewById(R.id.relative_title);
        tvVoucherLayout = view.findViewById(R.id.rl_voucher);

        linearAutoOrder = view.findViewById(R.id.linear_auto_order);
        rlOpenVip = view.findViewById(R.id.rl_open_vip);
        cbAutoOrderSw = view.findViewById(R.id.cb_auto_order_sw);

        tvPrice = view.findViewById(R.id.tv_price);
        tvVouchersDeduction = view.findViewById(R.id.tv_vouchers_deduction);
        tvOpenVip = view.findViewById(R.id.tv_open_vip);
        tvNeedPay = view.findViewById(R.id.tv_need_pay);
        tvNeedPayHint = view.findViewById(R.id.tv_need_pay_title);
        tvVouchersDeductionTitle = view.findViewById(R.id.tv_vouchers_deduction_title);

        tvRemain = view.findViewById(R.id.tv_remain);
        tvVoucher = view.findViewById(R.id.tv_voucher);

        tvSerialOrderTitle = view.findViewById(R.id.tv_serial_order_title);
        tvSerialOrderName = view.findViewById(R.id.tv_serial_order_name);

        tvVoucherTitle = view.findViewById(R.id.tv_voucher_title);

        TypefaceUtils.setHwChineseMediumFonts(tvSerialOrderName);
        TypefaceUtils.setHwChineseMediumFonts(tvNeedPay);
        TypefaceUtils.setHwChineseMediumFonts(tvNeedPayHint);
    }

    private void initData() {
        Locale locale = CompatUtils.getLocale(getResources().getConfiguration());
        String language = locale.getLanguage();
        isEnglish = TextUtils.equals(language, "en");
    }

    private void setListener() {
        cbAutoOrderSw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbAutoOrderSw.setChecked(cbAutoOrderSw.isChecked());
            }
        });

        rlOpenVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellClick();
            }
        });
    }

    private void bindData(OrdersCommonBean ordersBean) {
        if (ordersBean == null) {
            setVisibility(GONE);
            return;
        }
        mBean = ordersBean;
        String needPay = ordersBean.needPay + " " + ordersBean.priceUnit;
        String deduction = "-" + ordersBean.deduction + " " + ordersBean.vUnit;
        String orderName = ordersBean.orderName;
        BookInfo bookInfo = DBUtils.findByBookId(getContext(), ordersBean.getBookId());

        if (!ordersBean.isShowDeductionView()) {
            tvVoucherLayout.setVisibility(GONE);
            tvVouchersDeductionTitle.setVisibility(View.GONE);
            tvVouchersDeduction.setVisibility(View.GONE);
        } else {
            tvVouchersDeduction.setText(deduction);
        }

        if (isRechargeOrder) {
            // 充值 (比较特殊，恢复成老的样式)
            isRechargeOrder = false;
            tvRechargeTitle.setText(orderName);
            tvRechargeName.setText(ordersBean.author);
            tvSingleLine.setVisibility(View.GONE);
            tvSingleLayout.setVisibility(View.GONE);
            tvSerialLayout.setVisibility(View.VISIBLE);
            tvRechargeTitle.setVisibility(View.VISIBLE);
            tvRechargeName.setVisibility(View.VISIBLE);
        } else if (ordersBean.isSingleBook()) {
            // 单章
            initBookCoverView(orderName, ordersBean.author, bookInfo);
        } else {
            if (!isLotOrder) {
                // 连续购买、单本书
                initBookCoverView(orderName, ordersBean.author, bookInfo);
                tvSerialOrderTitle.setText(R.string.str_order_buy_chapter);
                tvSerialOrderName.setText(orderName);
                if (bookInfo != null && !TextUtils.isEmpty(bookInfo.bookname)) {
                    tvBookName.setText(bookInfo.bookname);
                }
                if (!TextUtils.isEmpty(orderName)) {
                    tvBookChapter.setVisibility(VISIBLE);
                    tvBookChapter.setText(getResources().getString(R.string.str_to_buy) + orderName);
                }
            } else {
                // 批量
                tvSerialOrderTitle.setText(R.string.str_order_start_chapter);
                if (!isEnglish) {
                    orderName += getResources().getString(R.string.str_order_buy_chapter_tips);
                }
                tvSerialOrderName.setText(orderName);
                isLotOrder = false;
                tvSingleLine.setVisibility(View.GONE);
                tvSingleLayout.setVisibility(View.GONE);
                tvSerialOrderName.setVisibility(View.VISIBLE);
                tvSerialOrderTitle.setVisibility(View.VISIBLE);
                tvSerialLayout.setVisibility(View.VISIBLE);
            }
        }

        setPrice(ordersBean);
        tvNeedPay.setText(needPay);
        if (!TextUtils.isEmpty(ordersBean.vipTips)) {
            rlOpenVip.setVisibility(View.VISIBLE);
            tvOpenVip.setText(ordersBean.vipTips);
        } else {
            rlOpenVip.setVisibility(View.GONE);
        }
        if (ordersBean.vouchers > 0) {
            String vouchersAndUnit = ordersBean.vouchers + " " + ordersBean.vUnit;
            tvVoucher.setText(vouchersAndUnit);
        } else {
            tvVoucherTitle.setVisibility(GONE);
            tvVoucher.setVisibility(GONE);
        }

        String remainAndUnit = ordersBean.remain + " " + ordersBean.priceUnit;
        tvRemain.setText(remainAndUnit);

        setRemianAndVouchers(ordersBean);
    }

    /**
     * 封面view绑定
     */
    private void initBookCoverView(String orderName, String author, BookInfo bookInfo) {
        TypefaceUtils.setHwChineseMediumFonts(tvBookName);
        tvBookName.setText(orderName);
        tvBookAuthor.setText(author);
        tvSingleLine.setVisibility(View.VISIBLE);
        tvSingleLayout.setVisibility(View.VISIBLE);
        tvSerialOrderName.setVisibility(View.GONE);
        tvSerialOrderTitle.setVisibility(View.GONE);
        tvSerialLayout.setVisibility(View.GONE);
        //绑定封面
        if (bookInfo != null) {
            if (!TextUtils.isEmpty(bookInfo.coverurl) && bookInfo.coverurl.contains(SDCardUtil.getInstance().getSDCardAndroidRootDir())) {
                Glide.with(this).load(new File(bookInfo.coverurl)).into(bookImageView);
            } else {
                GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl((Activity) getContext(), bookImageView, bookInfo.coverurl);
            }
        }
    }

    /**
     * 单本
     *
     * @param orderBean orderBean
     */
    public void bindSingleBookData(OrdersCommonBean orderBean) {
        linearAutoOrder.setVisibility(View.GONE);
        mBean = orderBean;
        bindData(orderBean);
    }

    /**
     * 绑定数据
     *
     * @param orderBean orderBean
     */
    public void bindSerialBookData(OrdersCommonBean orderBean) {
        linearAutoOrder.setVisibility(View.VISIBLE);
        mBean = orderBean;
        bindData(orderBean);
    }

    /**
     * 充值
     *
     * @param orderBean orderBean
     */
    public void bindRechargeOrdersData(OrdersCommonBean orderBean) {
        linearAutoOrder.setVisibility(View.GONE);
        rlOpenVip.setVisibility(View.GONE);
        mBean = orderBean;
        isRechargeOrder = true;
        bindData(orderBean);
    }

    /**
     * 批量订购
     *
     * @param orderBean orderBean
     */
    public void bindLotOrderData(OrdersCommonBean orderBean) {
        linearAutoOrder.setVisibility(View.GONE);
        mBean = orderBean;
        isLotOrder = true;
        bindData(orderBean);
    }

    /**
     * 是否自动购买下一章勾选了
     *
     * @return boolean
     */
    public boolean isAutoOrderChecked() {
        return cbAutoOrderSw.isChecked();
    }

    private void setRemianAndVouchers(final OrdersCommonBean ordersBean) {
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                SpUtil spUtil = SpUtil.getinstance(getContext());
                spUtil.setUserRemain(ordersBean.remain + "", ordersBean.priceUnit);
                spUtil.setUserVouchers(ordersBean.vouchers + "", ordersBean.vUnit);
            }
        });
    }

    private void setPrice(OrdersCommonBean bean) {
        DzSpanBuilder dzSpanBuilder = new DzSpanBuilder();
        dzSpanBuilder.append(bean.disTips).append(" ").appendStrike(bean.oldPrice).appendColor(" " + bean.price + " " + bean.priceUnit, CompatUtils.getColor(getContext(), R.color.color_100_000000));
        tvPrice.setText(dzSpanBuilder);
    }

    /**
     * 跳转MyVipActivity
     */
    public void sellClick() {
        dzLogSellClick(mBean);
        MyVipActivity.launch(getContext());
    }

    private void dzLogSellClick(OrdersCommonBean bean) {
        if (bean != null && bean.bookIdAndChapterIdNoEmpty()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(LogConstants.KEY_ORDER_BID, bean.getBookId());
            map.put(LogConstants.KEY_ORDER_CID, bean.getChapterId());
            DzLog.getInstance().logClick(LogConstants.MODULE_DG_SELL, null, null, map, bean.trackId);
        }

    }
}

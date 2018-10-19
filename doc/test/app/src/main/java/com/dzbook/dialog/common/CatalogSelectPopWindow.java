package com.dzbook.dialog.common;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dzbook.activity.reader.BasePopupWindow;
import com.dzbook.adapter.CatalogSelectorPopAdapter;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;

import java.util.List;

import hw.sdk.net.bean.BeanBlock;

/**
 * 章节页面选择弹窗
 *
 * @author gavin
 */
public class CatalogSelectPopWindow extends BasePopupWindow {
    private CatalogSelectorPopAdapter selectorAdapter;
    private ListView chapterList;
    private Activity mActivity;
    private BlockAction mListener;

    /**
     * 构造
     *
     * @param activity activity
     */
    public CatalogSelectPopWindow(@NonNull Activity activity) {
        super(activity);
        this.mActivity = activity;
        setContentView(LayoutInflater.from(mActivity).inflate(R.layout.dialog_pop_select, null));
    }

    @Override
    protected void initView(View view) {
        chapterList = view.findViewById(R.id.catalog_pop_list);
    }

    @Override
    protected void initData(View view) {
        setWidth(DimensionPixelUtil.dip2px(mActivity, 156));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        int color = CompatUtils.getColor(mActivity, android.R.color.transparent);
        this.setBackgroundDrawable(new ColorDrawable(color));

        selectorAdapter = new CatalogSelectorPopAdapter(mActivity);
        chapterList.setAdapter(selectorAdapter);
    }

    @Override
    protected void setListener(View view) {
        chapterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                if (mListener != null) {
                    mListener.onBlockClick(position, selectorAdapter.getItem(position));
                }
            }
        });
    }


    /**
     * 添加数据
     *
     * @param blockBeanList blockBeanList
     */
    public void addItem(List<BeanBlock> blockBeanList) {
        selectorAdapter.resetData(blockBeanList);
    }

    /**
     * 点击接口
     */
    public interface BlockAction {
        /**
         * 点击事件
         *
         * @param position  position
         * @param beanBlock beanBlock
         */
        void onBlockClick(int position, BeanBlock beanBlock);
    }

    public void setBlockAction(BlockAction blokcAction) {
        mListener = blokcAction;
    }

    /**
     * 初始选中位置
     *
     * @param position position
     */
    public void initPosition(int position) {
        int num = position / 50;
        chapterList.setSelection(num);
    }

}

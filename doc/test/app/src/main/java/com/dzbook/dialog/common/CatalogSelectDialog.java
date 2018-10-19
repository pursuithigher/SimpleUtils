package com.dzbook.dialog.common;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dzbook.adapter.CatalogSelectorAdapter;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

import java.util.List;

import hw.sdk.net.bean.BeanBlock;

import static hw.sdk.utils.UiHelper.getDisplayMetrics;

/**
 * Dialog
 *
 * @author gavin
 */
public class CatalogSelectDialog extends Dialog {
    private CatalogSelectorAdapter selectorAdapter;
    private ListView chapterList;
    private BlockAction mListener;

    /**
     * 构造
     *
     * @param context context
     */
    public CatalogSelectDialog(@NonNull Context context) {
        super(context, R.style.cmt_dialog);
        setContentView(R.layout.dialog_catalog_select);

        chapterList = findViewById(R.id.catalog_list);
        TextView tvSelectChapter = findViewById(R.id.tv_select_chapter);
        Window window = getWindow();
        android.view.WindowManager.LayoutParams p = window.getAttributes();
        p.width = getScreenWidth(context);
        window.setAttributes(p);
        window.setGravity(Gravity.BOTTOM);

        TypefaceUtils.setHwChineseMediumFonts(tvSelectChapter);

        selectorAdapter = new CatalogSelectorAdapter(context);
        chapterList.setAdapter(selectorAdapter);
        chapterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectorAdapter.setChecked(position);
                if (mListener != null) {
                    mListener.onBlockClick(position, selectorAdapter.getItem(position));
                }
                dismiss();
            }
        });
        TextView tvSelectCancel = findViewById(R.id.tv_select_cancel);
        TypefaceUtils.setHwChineseMediumFonts(tvSelectCancel);

        tvSelectCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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
     * 点击事件接口
     */
    public interface BlockAction {
        /**
         * 点击
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
     * 初始位置
     *
     * @param position position
     */
    public void initPosition(int position) {
        int num = position / 50;
        selectorAdapter.setChecked(num);
        chapterList.setSelection(num);
    }

    private static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return displayMetrics.widthPixels;
    }
}

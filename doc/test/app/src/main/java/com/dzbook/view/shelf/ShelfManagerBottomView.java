package com.dzbook.view.shelf;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.UI.MainShelfUI;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

/**
 * 书架管理底部view
 *
 * @author dongdianzhou on 2017/7/21.
 */

public class ShelfManagerBottomView extends RelativeLayout implements View.OnClickListener {

    private RelativeLayout mRelativeLayoutSelect;
    private RelativeLayout mRelativeLayoutDelete;
    private RelativeLayout mRelativeLayoutSort;
    private TextView mTextViewSelect;
    private TextView mTextViewDelete;
    private TextView mTextViewSort;
    private ImageView mImageViewSelect;
    private ImageView mImageViewDelete;

    private MainShelfUI mUI;

    private long lastClickTime = 0;

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ShelfManagerBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }

    public void setMainShelfUI(MainShelfUI mainShelfUI) {
        this.mUI = mainShelfUI;
    }

    private void setListener() {
        mRelativeLayoutSort.setOnClickListener(this);
        mRelativeLayoutSelect.setOnClickListener(this);
        mRelativeLayoutDelete.setOnClickListener(this);
    }

    private void initData() {

    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_shelfmanager_bottom, this);
        mRelativeLayoutSelect = view.findViewById(R.id.rl_manage_select);
        mRelativeLayoutDelete = view.findViewById(R.id.rl_manage_delete);
        mRelativeLayoutSort = view.findViewById(R.id.rl_manage_sort);
        mImageViewDelete = view.findViewById(R.id.iv_manage_delete);
        mImageViewSelect = view.findViewById(R.id.iv_manage_select);
        mTextViewDelete = view.findViewById(R.id.tv_manage_delete);
        mTextViewSelect = view.findViewById(R.id.tv_manage_select);
        mTextViewSort = view.findViewById(R.id.textview_sort);
        TypefaceUtils.setHwChineseMediumFonts(mTextViewSelect);
        TypefaceUtils.setHwChineseMediumFonts(mTextViewDelete);
        TypefaceUtils.setHwChineseMediumFonts(mTextViewSort);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - lastClickTime > 200) {
            if (id == R.id.rl_manage_sort) {
                mUI.popSortDialog();
            } else if (id == R.id.rl_manage_delete) {
                mUI.popDeleteBookDialog();
            }
            lastClickTime = currentClickTime;
        } else {
            ToastAlone.showShort(R.string.toast_please_wait);
        }
        if (id == R.id.rl_manage_select) {
            if (mImageViewSelect.isSelected()) {
                mUI.setAllItemSelectStatus(false);
                setDeleteManageEnable(false);
            } else {
                mUI.setAllItemSelectStatus(true);
                setDeleteManageEnable(true);
            }
            setAllSelectViewStatus(mImageViewSelect.isSelected());
        }
    }


    /**
     * 全选状态
     *
     * @param isAllSelect isAllSelect
     */
    public void setAllSelectViewStatus(boolean isAllSelect) {
        if (isAllSelect) {
            mImageViewSelect.setSelected(false);
            mTextViewSelect.setText(getResources().getString(R.string.all_select));
        } else {
            mImageViewSelect.setSelected(true);
            mTextViewSelect.setText(getResources().getString(R.string.cancel_all_select));
        }
    }

    /**
     * 删除状态管理
     *
     * @param isEnable isEnable
     */
    public void setDeleteManageEnable(boolean isEnable) {
        mTextViewDelete.setEnabled(isEnable);
        mRelativeLayoutDelete.setEnabled(isEnable);
        if (isEnable) {
            mImageViewDelete.setImageAlpha(255);
            int color = CompatUtils.getColor(getContext(), R.color.color_100_000000);
            mTextViewDelete.setTextColor(color);
        } else {
            int color = CompatUtils.getColor(getContext(), R.color.color_30_000000);
            mImageViewDelete.setImageAlpha(77);
            mTextViewDelete.setTextColor(color);

        }
    }


}

package com.dzbook.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.activity.UpLoadActivity;
import com.dzbook.activity.person.CloudBookShelfActivity;
import com.dzbook.activity.reader.BasePopupWindow;
import com.dzbook.adapter.shelf.DzShelfDelegateAdapter;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.UI.MainShelfUI;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.hw.LoginUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;

/**
 * DialogShelfMenuManage
 * author lizhongzhong 2017/4/7.
 */

public class DialogShelfMenuManage extends BasePopupWindow implements View.OnClickListener {

    private TextView tvShelfMenuCloud, tvShelfMenuLocalImport, tvShelfMenuManage, tvShelfMenuBookshelfMode;

    private LinearLayout llShelfManageMenu;

    private MainShelfUI mUi;

    private Activity mActivity;

    /**
     * 构造
     *
     * @param mActivity mActivity
     * @param mUi       mUi
     */
    public DialogShelfMenuManage(Activity mActivity, MainShelfUI mUi) {
        super(mActivity);
        this.mUi = mUi;
        this.mActivity = mActivity;
        setContentView(LayoutInflater.from(mActivity).inflate(R.layout.dialog_shelf_manage_menu, null));
    }

    public void setMainShelfUI(MainShelfUI mainshelfui) {
        this.mUi = mainshelfui;
    }

    @Override
    protected void initView(View view) {
        tvShelfMenuCloud = view.findViewById(R.id.tv_shelf_menu_cloud);
        tvShelfMenuLocalImport = view.findViewById(R.id.tv_shelf_menu_local_import);
        tvShelfMenuManage = view.findViewById(R.id.tv_shelf_menu_manage);
        tvShelfMenuBookshelfMode = view.findViewById(R.id.tv_shelf_menu_bookshelf_mode);
        llShelfManageMenu = view.findViewById(R.id.ll_shelf_manage_menu);
    }

    @Override
    protected void initData(View view) {
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(DimensionPixelUtil.dip2px(mActivity, 192));
        int color = CompatUtils.getColor(mActivity, android.R.color.transparent);
        this.setBackgroundDrawable(new ColorDrawable(color));

    }

    @Override
    protected void setListener(View view) {
        tvShelfMenuCloud.setOnClickListener(this);
        tvShelfMenuLocalImport.setOnClickListener(this);
        tvShelfMenuManage.setOnClickListener(this);
        tvShelfMenuBookshelfMode.setOnClickListener(this);
        llShelfManageMenu.setOnClickListener(this);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);

        if (SpUtil.getinstance(mActivity).getBookShelfMode() == DzShelfDelegateAdapter.MODE_GRID) {
            tvShelfMenuBookshelfMode.setText(mActivity.getResources().getString(R.string.bookshelf_list_mode));
        } else if (SpUtil.getinstance(mActivity).getBookShelfMode() == DzShelfDelegateAdapter.MODE_LIST) {
            tvShelfMenuBookshelfMode.setText(mActivity.getResources().getString(R.string.bookshelf_page_mode));
        }
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            int id = view.getId();
            if (id == R.id.tv_shelf_menu_cloud) {
                if (!NetworkUtils.getInstance().checkNet()) {
                    if (mActivity instanceof BaseActivity) {
                        ((BaseActivity) mActivity).showNotNetDialog();
                    }
                } else {
                    LoginUtils.getInstance().forceLoginCheck(mActivity, new LoginUtils.LoginCheckListener() {
                        @Override
                        public void loginComplete() {
                            ThirdPartyLog.onEventValueOldClick(mActivity, ThirdPartyLog.BOOK_SHELF_MENU_UMENG_ID, ThirdPartyLog.CLOUD_BOOKSHELF_VALUE, 1);
                            Intent intent = new Intent(mActivity, CloudBookShelfActivity.class);
                            mActivity.startActivity(intent);
                            BaseActivity.showActivity(mActivity);
                        }
                    });
                }
            } else if (id == R.id.tv_shelf_menu_local_import) {
                ThirdPartyLog.onEventValueOldClick(mActivity, ThirdPartyLog.BOOK_SHELF_MENU_UMENG_ID, ThirdPartyLog.LOCAL_IMPORT_VALUE, 1);
                Intent intent = new Intent(mActivity, UpLoadActivity.class);
                mActivity.startActivity(intent);
                BaseActivity.showActivity(mActivity);
            } else if (id == R.id.tv_shelf_menu_manage) {
                ThirdPartyLog.onEventValueOldClick(mActivity, ThirdPartyLog.BOOK_SHELF_MENU_UMENG_ID, ThirdPartyLog.BOOKSHELF_MANAGEMENT_VALUE, 1);
                //编辑模式
                mUi.openManager("");

            } else if (id == R.id.tv_shelf_menu_bookshelf_mode) {
                //书架显示模式
                switch (SpUtil.getinstance(mActivity).getBookShelfMode()) {
                    case DzShelfDelegateAdapter.MODE_GRID:
                        //展示列表模式 并且存储sp中
                        ThirdPartyLog.onEventValueOldClick(mActivity, ThirdPartyLog.BOOK_SHELF_MENU_UMENG_ID, ThirdPartyLog.LIST_MODE_LIST_VALUE, 1);
                        mUi.setBookShlefMode(DzShelfDelegateAdapter.MODE_LIST);
                        break;
                    case DzShelfDelegateAdapter.MODE_LIST:
                        //展示图片模式 并且存储sp中
                        ThirdPartyLog.onEventValueOldClick(mActivity, ThirdPartyLog.BOOK_SHELF_MENU_UMENG_ID, ThirdPartyLog.GRID_MODE_LIST_VALUE, 1);
                        mUi.setBookShlefMode(DzShelfDelegateAdapter.MODE_GRID);
                        break;
                    default:
                        break;
                }

            }
            dismiss();
        }
    }

    public boolean isVisible() {
        return llShelfManageMenu != null && llShelfManageMenu.getVisibility() == View.VISIBLE;
    }
}

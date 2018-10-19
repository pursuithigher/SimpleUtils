package com.dzbook.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dzbook.AppConst;
import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.adapter.LocalFileAdapter;
import com.dzbook.adapter.SubTabUpLoadPagerAdapter;
import com.dzbook.bean.LocalFileBean;
import com.dzbook.lib.utils.ALog;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.LocalFileUI;
import com.dzbook.mvp.presenter.LocalBookPresenter;
import com.dzbook.templet.UpLoadBaseFragment;
import com.dzbook.templet.UpLoadIndexFragment;
import com.dzbook.templet.UpLoadLocalFragment;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.util.ArrayList;

import huawei.widget.HwSubTabWidget;

/**
 * 书架-侧边滑动菜单-本地图书
 *
 * @author dllik
 */
public class UpLoadActivity extends BaseSwipeBackActivity implements OnClickListener, LocalFileUI {

    private static final String TAG = "UpLoadActivity";
    private static final float ALPHA = 0.3f;

    /**
     * 添加书籍到书架的按钮事件
     */
    private TextView textviewAdd, textviewDelete;

    private ViewPager viewPager;
    private View layoutAdd;
    private View layoutDelete;
    private View layoutSelect;
    private CheckedTextView textviewSelect;
    private ImageView imageviewSelect;

    private DianZhongCommonTitle mTitle;
    private LocalBookPresenter mPresenter;
    private SubTabUpLoadPagerAdapter subTabUpLoadPagerAdapter;
    private HwSubTabWidget hwSubTabWidget;

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected boolean isNoFragmentCache() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_upload);
    }

    @Override
    public void refreshIndexError() {
        UpLoadBaseFragment currentFragment = getCurrentFragment();
        if (null != currentFragment && currentFragment instanceof UpLoadIndexFragment) {
            currentFragment.showNetError();
        }
    }


    @Override
    public void refreshIndexInfo(ArrayList<LocalFileBean> list, String currentPath) {
        try {
            UpLoadBaseFragment currentFragment = getCurrentFragment();
            if (null == currentFragment || !(currentFragment instanceof UpLoadIndexFragment)) {
                return;
            }
            UpLoadIndexFragment upLoadIndexFragment = (UpLoadIndexFragment) currentFragment;
            if (list == null || list.size() <= 0) {
                upLoadIndexFragment.showNetError();
            } else {
                upLoadIndexFragment.showSuccess();
            }
            upLoadIndexFragment.setAdapterData(list);
            refreshSelectState();
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    private UpLoadBaseFragment getCurrentFragment() {
        return subTabUpLoadPagerAdapter.getCurrentFragment();
    }

    @Override
    public void refreshLocalError() {

        UpLoadBaseFragment currentFragment = getCurrentFragment();
        if (null != currentFragment && currentFragment instanceof UpLoadLocalFragment) {
            currentFragment.showNetError();
        }

    }

    @Override
    public void refreshLocalInfo(ArrayList<LocalFileBean> list, String currentPath) {
        try {

            UpLoadBaseFragment currentFragment = getCurrentFragment();
            if (null == currentFragment || !(currentFragment instanceof UpLoadLocalFragment)) {
                return;
            }
            UpLoadLocalFragment upLoadLocalFragment = (UpLoadLocalFragment) currentFragment;

            if (list != null && list.size() > 0) {
                upLoadLocalFragment.showSuccess();
            } else if (mPresenter.isLocalShouldEmpty()) {
                upLoadLocalFragment.showNetError();
            }
            upLoadLocalFragment.setAdapterData(list);
            upLoadLocalFragment.addPath(currentPath);
            refreshSelectState();
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    @Override
    public void deleteBean(ArrayList<LocalFileBean> list) {

        int count = subTabUpLoadPagerAdapter.getCount();
        for (int i = 0; i < count; i++) {
            Fragment item = subTabUpLoadPagerAdapter.getItem(i);
            if (item instanceof UpLoadBaseFragment) {
                ((UpLoadBaseFragment) item).deleteBean(list);
                refreshSelectState();
            }
        }
    }

    @Override
    public void bookAdded(LocalFileBean addedBean) {

        int count = subTabUpLoadPagerAdapter.getCount();
        for (int i = 0; i < count; i++) {
            Fragment item = subTabUpLoadPagerAdapter.getItem(i);
            if (item instanceof UpLoadBaseFragment && addedBean.isImportSuccess) {
                ((UpLoadBaseFragment) item).setBookAdded(addedBean);
                refreshSelectState();
            }
        }
    }

    @Override
    public void bookAddComplete(ArrayList<LocalFileBean> list) {
        ArrayList<LocalFileBean> importFailList = new ArrayList<>();
        for (LocalFileBean bean : list) {
            if (!bean.isImportSuccess) {
                importFailList.add(bean);
            }
        }
        if (importFailList.size() > 0) {
            int maxLength = 2;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < importFailList.size() && i < maxLength; i++) {
                sb.append(importFailList.get(i).fileName);
                sb.append("、");
            }
            if (importFailList.size() > maxLength) {
                sb.replace(sb.length() - 1, sb.length(), "...");
            } else {
                sb.replace(sb.length() - 1, sb.length(), "");
            }
            ToastAlone.showShort(getResources().getString(R.string.str_upload_error, sb.toString()));
        }
    }

    @Override
    protected void initView() {
        mTitle = findViewById(R.id.commontitle);
        layoutSelect = findViewById(R.id.layout_select);
        textviewSelect = findViewById(R.id.textView_select);
        imageviewSelect = findViewById(R.id.imageView_select);
        layoutDelete = findViewById(R.id.layout_delete);
        textviewAdd = findViewById(R.id.textView_add);
        textviewDelete = findViewById(R.id.tv_manage_delete);
        layoutAdd = findViewById(R.id.layout_add);
        viewPager = findViewById(R.id.viewPager_search);
        hwSubTabWidget = initializeSubTabs(getContext());

        TypefaceUtils.setHwChineseMediumFonts(textviewAdd);
        TypefaceUtils.setHwChineseMediumFonts(textviewDelete);
        TypefaceUtils.setHwChineseMediumFonts(textviewSelect);

    }

    private void bindData() {


        UpLoadIndexFragment upLoadIndexFragment = new UpLoadIndexFragment();
        upLoadIndexFragment.setPresenter(mPresenter);
        HwSubTabWidget.SubTab subTabIndex = hwSubTabWidget.newSubTab(AppConst.getApp().getResources().getString(R.string.file_smart_import));


        UpLoadLocalFragment upLoadLocalFragment = new UpLoadLocalFragment();
        upLoadLocalFragment.setPresenter(mPresenter);
        HwSubTabWidget.SubTab subTabLocal = hwSubTabWidget.newSubTab(AppConst.getApp().getResources().getString(R.string.file_local_import));


        subTabUpLoadPagerAdapter.addSubTab(subTabIndex, upLoadIndexFragment, null, true);
        subTabUpLoadPagerAdapter.addSubTab(subTabLocal, upLoadLocalFragment, null, false);
    }

    private HwSubTabWidget initializeSubTabs(Context context) {
        HwSubTabWidget subTabWidget = findViewById(R.id.layout_tab);
        subTabUpLoadPagerAdapter = new SubTabUpLoadPagerAdapter((FragmentActivity) context, viewPager, subTabWidget);
        return subTabWidget;
    }

    @Override
    protected void initData() {
        setSwipeBackEnable(false);
        mPresenter = new LocalBookPresenter(this);
        bindData();
    }

    @Override
    protected void setListener() {
        layoutDelete.setOnClickListener(this);
        layoutSelect.setOnClickListener(this);
        layoutAdd.setOnClickListener(this);
        mTitle.setLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                subTabUpLoadPagerAdapter.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                subTabUpLoadPagerAdapter.setSelect(position);
                DzLog.getInstance().logPv(position == 0 ? LogConstants.PV_ZNDS : LogConstants.PV_BDML, null, null);
                UpLoadBaseFragment currentFragment = getCurrentFragment();
                if (null != currentFragment) {
                    currentFragment.select();
                    refreshSelectState();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public void refreshSelectState() {
        int temp = 0;
        int totalFileSize = 0;
        if (null == getCurrentAdapter() || ListUtils.isEmpty(getCurrentAdapter().getData())) {
            return;
        }
        boolean havCheck = false;
        for (LocalFileBean bean : getCurrentAdapter().getData()) {
            if (!bean.isTitle && bean.isAcceptFile() && !bean.isAdded) {
                totalFileSize++;
                havCheck = true;
                if (bean.isChecked) {
                    temp++;
                }
            }
        }
        if (temp == 0) {
            textviewSelect.setText(R.string.all_select);
            imageviewSelect.setImageResource(R.drawable.ic_shelf_manage_all_select);
            textviewSelect.setChecked(false);
        } else if (temp == totalFileSize) {
            textviewSelect.setText(R.string.cancel_all_select);
            imageviewSelect.setImageResource(R.drawable.ic_shelf_manage_no_select);
            textviewSelect.setChecked(true);
        } else {
            textviewSelect.setText(R.string.all_select);
            imageviewSelect.setImageResource(R.drawable.ic_shelf_manage_all_select);
            textviewSelect.setChecked(false);
        }
        if (havCheck) {
            textviewSelect.setAlpha(1);
            imageviewSelect.setImageAlpha(255);
        } else {
            textviewSelect.setAlpha(ALPHA);
            imageviewSelect.setImageAlpha((int) (255 * ALPHA));
        }
        textviewAdd.setText(getContext().getString(R.string.string_book_add, String.valueOf(temp)));
        bottomUIControl(temp);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        LocalFileAdapter currentAdapter = getCurrentAdapter();
        if (null == currentAdapter) {
            return;
        }
        if (id == R.id.layout_select) {
            if (textviewSelect.isChecked()) {
                currentAdapter.unSelectAll();
                refreshSelectState();
            } else {
                currentAdapter.selectAll();
                refreshSelectState();
            }
        } else if (id == R.id.layout_add) {
            mPresenter.addToShelf(currentAdapter.getCheckedList());
        } else if (id == R.id.layout_delete) {
            mPresenter.removeBooks(currentAdapter.getCheckedList());
        }
    }

    private LocalFileAdapter getCurrentAdapter() {
        UpLoadBaseFragment currentFragment = getCurrentFragment();
        return currentFragment.getAdapter();
    }

    private void bottomUIControl(int temp) {
        if (temp == 0) {
            layoutAdd.setAlpha(ALPHA);
            layoutDelete.setAlpha(ALPHA);
        } else {
            layoutAdd.setAlpha(1);
            layoutDelete.setAlpha(1);
        }
    }

    @Override
    public int getNavigationBarColor() {
        return R.color.color_100_f2f2f2;
    }
}

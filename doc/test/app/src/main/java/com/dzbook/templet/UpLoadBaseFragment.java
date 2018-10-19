package com.dzbook.templet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.adapter.LocalFileAdapter;
import com.dzbook.bean.LocalFileBean;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.mvp.presenter.LocalBookPresenter;
import com.dzbook.utils.DBUtils;

import java.util.ArrayList;

/**
 * 上传aseFragment
 */
public class UpLoadBaseFragment extends BaseFragment {

    protected LocalBookPresenter mPresenter;
    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LocalFileAdapter adapter = getAdapter();
            if (null == adapter) {
                return;
            }
            LocalFileBean bean = adapter.getItem(position);
            if (bean != null) {
                if (bean.fileType == LocalFileBean.TYPE_DIR) {
                    mPresenter.setCurrentPath(bean.filePath);
                } else if (bean.isAdded) {
                    CatalogInfo catalog = DBUtils.getCatalog(getContext(), bean.filePath, bean.filePath);
                    if (catalog != null) {
                        ReaderUtils.intoReader(getContext(), catalog, catalog.currentPos);
                    }
                } else if (bean.isChecked) {
                    adapter.unSelect(bean);
                    if (null != mPresenter) {
                        mPresenter.refreshSelectState();
                    }
                } else {
                    adapter.select(bean);
                    if (null != mPresenter) {
                        mPresenter.refreshSelectState();
                    }
                }
            }
        }
    };

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    protected void initView(View uiView) {

    }

    @Override
    protected void initData(View uiView) {

    }

    @Override
    protected void setListener(View uiView) {

    }

    @Override
    public String getTagName() {
        return null;
    }

    /**
     * 删除数据
     *
     * @param list list
     */
    public void deleteBean(ArrayList<LocalFileBean> list) {

    }

    /**
     * setBookAdded
     *
     * @param addedBean addedBean
     */
    public void setBookAdded(LocalFileBean addedBean) {

    }

    /**
     * 选中
     */
    public void select() {

    }

    public void setPresenter(LocalBookPresenter presenter) {
        this.mPresenter = presenter;
    }

    /**
     * 显示网络错误
     */
    public void showNetError() {

    }

    /**
     * 显示成功状态
     */
    public void showSuccess() {

    }

    public LocalFileAdapter getAdapter() {
        return null;
    }

    /**
     * 绑定adapter数据
     *
     * @param adapterData adapterData
     */
    public void setAdapterData(ArrayList<LocalFileBean> adapterData) {
    }
}

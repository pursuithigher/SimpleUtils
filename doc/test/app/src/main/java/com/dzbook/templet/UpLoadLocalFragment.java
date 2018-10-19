package com.dzbook.templet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dzbook.adapter.LocalFileAdapter;
import com.dzbook.bean.LocalFileBean;
import com.dzbook.view.CustomFilePathView;
import com.dzbook.view.common.StatusView;
import com.ishugui.R;

import java.util.ArrayList;

/**
 * UpLoadLocalFragment
 */
public class UpLoadLocalFragment extends UpLoadBaseFragment {

    private View viewLocal;
    private StatusView loadStatusViewLocal;
    private ListView listviewLocal;
    /**
     * 显示当前目录
     */
    private CustomFilePathView pathviewPath;
    private LocalFileAdapter localFileAdapter;

    @Override
    public LocalFileAdapter getAdapter() {
        return localFileAdapter;
    }

    @Override
    public void setAdapterData(ArrayList<LocalFileBean> adapterData) {
        super.setAdapterData(adapterData);
        localFileAdapter.setData(adapterData);
    }

    @Override
    public void showSuccess() {
        super.showSuccess();
        loadStatusViewLocal.showSuccess();
    }

    @Override
    public void showNetError() {
        super.showNetError();
        loadStatusViewLocal.showNetError();
    }

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == viewLocal) {
            viewLocal = View.inflate(getActivity(), R.layout.ac_local_local, null);
        }
        return viewLocal;
    }

    @Override
    protected void initView(View uiView) {
        loadStatusViewLocal = uiView.findViewById(R.id.loadStatusView);
        listviewLocal = uiView.findViewById(R.id.listView_local);
        pathviewPath = uiView.findViewById(R.id.pathView_path);

    }

    @Override
    public void setBookAdded(LocalFileBean addedBean) {
        super.setBookAdded(addedBean);
        localFileAdapter.setBookAdded(addedBean);
    }

    @Override
    public void deleteBean(ArrayList<LocalFileBean> list) {
        super.deleteBean(list);
        localFileAdapter.deleteBean(list);
    }

    @Override
    protected void initData(View uiView) {
        localFileAdapter = new LocalFileAdapter(getActivity());
        listviewLocal.setAdapter(localFileAdapter);

    }

    @Override
    public void select() {
        super.select();
        if (null != loadStatusViewLocal && null != localFileAdapter && localFileAdapter.getCount() <= 0 && null != mPresenter) {
            loadStatusViewLocal.showLoading();
            mPresenter.searchLocalFile();
        }
    }

    @Override
    protected void setListener(View uiView) {
        listviewLocal.setOnItemClickListener(mOnItemClickListener);
        pathviewPath.setPathClickListener(new CustomFilePathView.PathClickListener() {
            @Override
            public void clickBack(String path) {
                if (null != mPresenter) {
                    mPresenter.setCurrentPath(path);
                }
            }
        });

    }

    @Override
    public String getTagName() {
        return null;
    }

    /**
     * 添加路径
     *
     * @param currentPath currentPath
     */
    public void addPath(String currentPath) {
        pathviewPath.addPath(currentPath);
    }
}

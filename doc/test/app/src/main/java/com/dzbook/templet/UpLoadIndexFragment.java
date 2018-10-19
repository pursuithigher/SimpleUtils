package com.dzbook.templet;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dzbook.adapter.LocalFileAdapter;
import com.dzbook.bean.LocalFileBean;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.view.common.StatusView;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.util.ArrayList;

/**
 * 上传Fragment
 */
public class UpLoadIndexFragment extends UpLoadBaseFragment {

    private View viewIndex;
    private StatusView loadStatusViewIndex;
    private ListView listviewIndex;
    private LocalFileAdapter localFileAdapter;

    @Override
    public LocalFileAdapter getAdapter() {
        return localFileAdapter;
    }

    @Override
    public void showSuccess() {
        super.showSuccess();
        loadStatusViewIndex.showSuccess();
    }

    @Override
    public void setAdapterData(ArrayList<LocalFileBean> adapterData) {
        super.setAdapterData(adapterData);
        localFileAdapter.setData(adapterData);
    }

    @Override
    public void showNetError() {
        super.showNetError();
        loadStatusViewIndex.showEmpty(getResources().getString(R.string.string_empty_no_local_book));
    }

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == viewIndex) {
            viewIndex = View.inflate(getActivity(), R.layout.ac_local_index, null);
        }
        return viewIndex;
    }

    @Override
    protected void initView(View uiView) {
        loadStatusViewIndex = viewIndex.findViewById(R.id.loadStatusView);
        listviewIndex = viewIndex.findViewById(R.id.listView_index);

    }

    @Override
    protected void initData(View uiView) {
        localFileAdapter = new LocalFileAdapter(getActivity());
        listviewIndex.setAdapter(localFileAdapter);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            loadStatusViewIndex.showLoading();
            if (null != mPresenter) {
                mPresenter.searchIndexFile();
            }
        } else {
            ToastAlone.showShort(R.string.toast_sd_unavailable);
        }
        DzLog.getInstance().logPv(LogConstants.PV_ZNDS, null, null);

    }

    @Override
    public void setBookAdded(LocalFileBean addedBean) {
        super.setBookAdded(addedBean);
        localFileAdapter.setBookAdded(addedBean);
    }

    @Override
    public void select() {
        super.select();
        if (null != localFileAdapter && localFileAdapter.getCount() <= 0 && null != mPresenter) {
            mPresenter.searchIndexFile();

        }
    }

    @Override
    public void deleteBean(ArrayList<LocalFileBean> list) {
        super.deleteBean(list);
        localFileAdapter.deleteBean(list);
    }

    @Override
    protected void setListener(View uiView) {
        listviewIndex.setOnItemClickListener(mOnItemClickListener);

    }

    @Override
    public String getTagName() {
        return null;
    }
}

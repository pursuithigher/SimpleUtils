package com.dzbook.templet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.database.bean.BookMarkNew;
import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.mvp.presenter.ReaderCatalogPresenter;
import com.dzbook.view.reader.ReaderMarkView;

import java.util.List;

/**
 * 阅读书签Fragment
 *
 * @author gavin
 */
public class ReaderMarkFragment extends BaseFragment {

    private ReaderMarkView readerMarkView;
    private ReaderCatalogPresenter mPresenter;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == readerMarkView) {
            readerMarkView = new ReaderMarkView(getContext());
        }
        return readerMarkView;
    }

    @Override
    protected void initView(View uiView) {

    }

    @Override
    protected void initData(View uiView) {
        if (null != mPresenter) {
            mPresenter.getBookMarkTask();
        }

    }

    @Override
    protected void setListener(View uiView) {

    }


    @Override
    public String getTagName() {
        return null;
    }

    /**
     * 添加数据
     *
     * @param list  list
     * @param clear clear
     */
    public void addItem(List<BookMarkNew> list, boolean clear) {
        if (null == readerMarkView) {
            return;
        }
        readerMarkView.addItem(list, clear);
    }

    public void setPresenter(ReaderCatalogPresenter presenter) {
        this.mPresenter = presenter;
    }
}

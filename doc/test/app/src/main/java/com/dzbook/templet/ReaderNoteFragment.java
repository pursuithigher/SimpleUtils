package com.dzbook.templet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.database.bean.BookMarkNew;
import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.mvp.presenter.ReaderCatalogPresenter;
import com.dzbook.view.reader.ReaderNoteView;

import java.util.List;

/**
 * 阅读笔记Fragment
 *
 * @author gavin
 */
public class ReaderNoteFragment extends BaseFragment {

    private ReaderNoteView readerNoteView;
    private ReaderCatalogPresenter mPresenter;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == readerNoteView) {
            readerNoteView = new ReaderNoteView(getContext());
        }
        return readerNoteView;
    }

    @Override
    protected void initView(View uiView) {

    }

    @Override
    protected void initData(View uiView) {
        if (null != mPresenter) {
            mPresenter.getBookNoteTask();
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
        if (null == readerNoteView) {
            return;
        }
        readerNoteView.addItem(list, clear);
    }

    public void setPresenter(ReaderCatalogPresenter presenter) {
        this.mPresenter = presenter;
    }
}

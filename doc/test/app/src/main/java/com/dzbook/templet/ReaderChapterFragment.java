package com.dzbook.templet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.mvp.presenter.ReaderCatalogPresenter;
import com.dzbook.view.reader.ReaderChapterView;

import java.util.List;

/**
 * R阅读章节Fragment
 */
public class ReaderChapterFragment extends BaseFragment {

    private ReaderChapterView readerChapterView;
    private ReaderCatalogPresenter mPresenter;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == readerChapterView) {
            readerChapterView = new ReaderChapterView(getContext());
        }
        return readerChapterView;
    }

    @Override
    protected void initView(View uiView) {

    }

    @Override
    protected void initData(View uiView) {
        if (null != mPresenter) {
            mPresenter.getChapterTask();
        }
    }

    @Override
    protected void setListener(View uiView) {

    }

    /**
     * 设置购买按钮状态
     *
     * @param status     status
     * @param remainSize remainSize
     * @param totalSize  totalSize
     */
    public void setPurchasedButtonStatus(int status, int remainSize, int totalSize) {
        if (null == readerChapterView) {
            return;
        }
        readerChapterView.setPurchasedButtonStatus(status, remainSize, totalSize);
    }

    @Override
    public String getTagName() {
        return null;
    }

    /**
     * 设置选中位置
     *
     * @param selectionFromTop selectionFromTop
     */
    public void setSelectionFromTop(String selectionFromTop) {
        if (null == readerChapterView) {
            return;
        }
        readerChapterView.setSelectionFromTop(selectionFromTop);
    }

    /**
     * 刷新
     */
    public void refresh() {
        if (null == readerChapterView) {
            return;
        }
        readerChapterView.refresh();
    }

    /**
     * 添加数据
     *
     * @param list  list
     * @param clear clear
     */
    public void addItem(List<CatalogInfo> list, boolean clear) {
        if (null == readerChapterView) {
            return;
        }
        readerChapterView.addItem(list, clear);
    }

    /**
     * 设置点击
     *
     * @param blockClick blockClick
     */
    public void setBlockClick(int blockClick) {
        if (null == readerChapterView) {
            return;
        }
        readerChapterView.setBlockClick(blockClick);
    }


    public void setPresenter(ReaderCatalogPresenter presenter) {
        this.mPresenter = presenter;
    }
}

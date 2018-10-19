package com.dzbook.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.utils.DBUtils;
import com.dzbook.view.SwitchButton;
import com.dzbook.view.person.PersonSwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * CancelAutoOrderAdapter
 *
 * @author lizhongzhong 2015/9/21.
 */
public class CancelAutoOrderAdapter extends BaseAdapter {

    /**
     * context
     */
    private Context mContext;
    /**
     * 数据的集合
     */
    private List<BookInfo> dataList;

    /**
     * 构造
     *
     * @param context context
     */
    public CancelAutoOrderAdapter(Context context) {
        this.mContext = context;
        dataList = new ArrayList<BookInfo>();
    }

    /**
     * 添加数据
     *
     * @param list    list
     * @param isClear isClear
     */
    public void addItem(List<BookInfo> list, boolean isClear) {
        if (isClear) {
            dataList.clear();
        }
        if (list != null && list.size() > 0) {
            dataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (dataList == null) {
            return 0;
        } else {
            return dataList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PersonSwitchView switchView = null;
        if (convertView == null) {
            switchView = new PersonSwitchView(mContext);
        } else {
            switchView = (PersonSwitchView) convertView;
        }
        switchView.setIconVisible(false);
        if (position < dataList.size()) {
            final BookInfo bookInfo = dataList.get(position);
            if (bookInfo != null) {
                switchView.setTitle(bookInfo.bookname);
                if (bookInfo.payRemind == 1) {
                    switchView.closedSwitch();
                } else {
                    switchView.openSwitch();
                }
                switchView.mSwitchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                        if (!isChecked) {
                            BookInfo newInfo = new BookInfo();
                            newInfo.bookid = bookInfo.bookid;
                            newInfo.confirmStatus = 1;
                            newInfo.payRemind = 1;
                            DBUtils.updateBook(mContext, newInfo);
                            bookInfo.confirmStatus = 1;
                            bookInfo.payRemind = 1;
                        } else {
                            BookInfo newInfo = new BookInfo();
                            newInfo.bookid = bookInfo.bookid;
                            newInfo.confirmStatus = 2;
                            newInfo.payRemind = 2;
                            DBUtils.updateBook(mContext, newInfo);
                            bookInfo.confirmStatus = 2;
                            bookInfo.payRemind = 2;
                        }
                    }
                });
            }
        }
        return switchView;
    }

}

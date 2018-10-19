package com.dzbook.adapter.shelf;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.mvp.presenter.MainShelfPresenter;
import com.dzbook.utils.DeviceInfoUtils;
import com.dzbook.utils.DeviceUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.SpUtil;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import hw.sdk.net.bean.shelf.BeanBookUpdateInfo;
import hw.sdk.utils.UiHelper;

/**
 * DzShelfDelegateAdapter
 */
public class DzShelfDelegateAdapter extends DelegateAdapter {

    /**
     * 网格模式
     */
    public static final int MODE_GRID = 1;
    /**
     * 列表模式
     */
    public static final int MODE_LIST = 2;

    /**
     * 普通模式
     */
    public static final int MODE_COMMON = 1001;
    /**
     * 管理模式
     */
    public static final int MODE_MANAGER = 1002;
    /**
     * 书架模式
     */
    public int shelfShowMode = MODE_GRID;
    /**
     * 书架管理模式
     */
    public int shelfManaageMode = MODE_COMMON;

    private int shelfNum = 3;

    private Context mContext;
    private Fragment mFragment;

    private MainShelfPresenter shelfPresenter;

    private List<BookInfo> list;


    private BeanBookUpdateInfo updateInfo;

    private int gridItemNum = 3;

    private ShelfSignInAdapter shelfSignInAdapter;

    /**
     * DzShelfDelegateAdapter
     * @param layoutManager layoutManager
     */
    public DzShelfDelegateAdapter(VirtualLayoutManager layoutManager) {
        this(layoutManager, false);
    }

    /**
     * DzShelfDelegateAdapter
     * @param layoutManager layoutManager
     * @param hasConsistItemType hasConsistItemType
     */
    public DzShelfDelegateAdapter(VirtualLayoutManager layoutManager, boolean hasConsistItemType) {
        this(layoutManager, hasConsistItemType, null, null, null);
    }

    /**
     * DzShelfDelegateAdapter
     * @param layoutManager layoutManager
     * @param hasConsistItemType hasConsistItemType
     * @param context context
     * @param fragment fragment
     * @param shelfPresenter shelfPresenter
     */
    public DzShelfDelegateAdapter(VirtualLayoutManager layoutManager, boolean hasConsistItemType, Context context, Fragment fragment, MainShelfPresenter shelfPresenter) {
        super(layoutManager, hasConsistItemType);
        mContext = context;
        mFragment = fragment;
        this.shelfPresenter = shelfPresenter;
        list = new ArrayList<>();
        intiConstantNum();
    }

    public void setUpdateInfo(BeanBookUpdateInfo updateInfo) {
        this.updateInfo = updateInfo;
    }

    public int getCurrentManagerMode() {
        return shelfManaageMode;
    }

    private void intiConstantNum() {
        if (DeviceUtils.isPad(mContext)) {
            gridItemNum = 4;
        } else {
            gridItemNum = 3;
        }
        int screenHeight = DeviceInfoUtils.getInstanse().getHeightReturnInt();
        int width = UiHelper.getScreenWidth(mContext) - DimensionPixelUtil.dip2px(mContext, 48 + 21 * (gridItemNum - 1));
        int height = width / gridItemNum * 120 / 90 + DimensionPixelUtil.dip2px(mContext, 35);
        shelfNum = (screenHeight - DimensionPixelUtil.dip2px(mContext, 80 + 48 * 2) - DeviceInfoUtils.getInstanse().getStatusBarHeight()) / height;
    }

    /**
     * 设置模式多数情况不需要重新刷书架数据，只有删除需要重刷数据
     *
     * @param bookid               bookid
     * @param currentManagerMode   currentManagerMode
     * @param isReferenceShelfData isReferenceShelfData
     */
    public void setCurrentManagerMode(int currentManagerMode, String bookid, boolean isReferenceShelfData) {
        this.shelfManaageMode = currentManagerMode;
        if (isReferenceShelfData) {
            //删除返回
            if (shelfPresenter != null) {
                shelfPresenter.getBookFromLocal(false);
            }
        } else {
            if (shelfShowMode == MODE_GRID) {
                for (int i = 0; i < list.size(); i++) {
                    BookInfo bookInfo = list.get(i);
                    bookInfo.blnIsChecked = !TextUtils.isEmpty(bookid) && TextUtils.equals(bookid, bookInfo.bookid);
                }
                List<GridItem> gridItemList = listToGridItems(currentManagerMode == MODE_MANAGER);
                List<Adapter> adapters = coverGridItemToAdapters(gridItemList);
                if (adapters != null) {
                    setAdapters(adapters);
                    notifyDataSetChanged();
                }
            } else if (shelfShowMode == MODE_LIST) {
                List<ListItem> listItemList = listToListItems(bookid, currentManagerMode == MODE_MANAGER);
                List<Adapter> adapters = coverListItemToAdapters(listItemList);
                if (adapters != null) {
                    setAdapters(adapters);
                    notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * 设置当前页面九宫格模式还是列表模式
     * @param currentShelfMode currentShelfMode
     */
    public void setCurrentShelfMode(int currentShelfMode) {
        if (shelfShowMode == currentShelfMode) {
            return;
        }
        shelfShowMode = currentShelfMode;
        if (shelfShowMode == MODE_GRID) {
            List<GridItem> gridItemList = listToGridItems(false);
            List<Adapter> adapters = coverGridItemToAdapters(gridItemList);
            if (adapters != null) {
                setAdapters(adapters);
                notifyDataSetChanged();
            }
        } else if (shelfShowMode == MODE_LIST) {
            List<ListItem> listItemList = listToListItems("", false);
            List<Adapter> adapters = coverListItemToAdapters(listItemList);
            if (adapters != null) {
                setAdapters(adapters);
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 获取到列表数据的前面50本书用于刷新书籍
     *
     * @return 可能为空，若为空再去请求数据库。
     */
    public List<BookInfo> getShelfDatas() {
        if (list != null && list.size() > 0) {
            try {
                int updateNum = SpUtil.getinstance(mContext).getUpdateBookNum();
                if (updateNum < 50) {
                    updateNum = 50;
                }
                return list.subList(0, Math.min(updateNum, list.size()));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * 校验列表是否全部选中：管理模式下
     * @return boolean
     */
    public boolean isAllSelect() {
        int bookNum = 0;
        int selectedBookNum = 0;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                BookInfo bookInfo = list.get(i);
                if (bookInfo != null && !bookInfo.isAddButton()) {
                    bookNum++;
                    if (bookInfo.blnIsChecked) {
                        selectedBookNum++;
                    }
                }
            }
            return !(bookNum == selectedBookNum);
        }
        return true;
    }

    /**
     * 设置全选中/全不选中
     * @param isAllSelected isAllSelected
     */
    public void setAllItemSelectStatus(boolean isAllSelected) {
        if (list == null || list.size() <= 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            BookInfo bookInfo = list.get(i);
            if (bookInfo != null && !bookInfo.isAddButton()) {
                bookInfo.blnIsChecked = isAllSelected;
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 得到所有选中的书籍列表
     * @return List
     */
    public List<BookInfo> getAllSelectedBooks() {
        if (list == null || list.size() <= 0) {
            return null;
        }
        List<BookInfo> selectedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            BookInfo bookInfo = list.get(i);
            if (bookInfo != null && bookInfo.blnIsChecked) {
                selectedList.add(bookInfo);
            }
        }
        return selectedList;
    }

    /**
     * 应用内排序（不在走数据库排序）
     *
     * @param sortType ：0：时间，1：书名
     *
     */
    public void sortShelfData(String sortType) {
        if (list != null && list.size() > 0) {
            if ("0".equals(sortType)) {
                Collections.sort(list, new Comparator<BookInfo>() {
                    @Override
                    public int compare(BookInfo item1, BookInfo item2) {
                        if (item1.time == null) {
                            item1.time = "";
                        }
                        if (item2.time == null) {
                            item2.time = "";
                        }
                        return item2.time.compareTo(item1.time);
                    }
                });
            } else if ("1".equals(sortType)) {
                Collections.sort(list, new Comparator<BookInfo>() {
                    @Override
                    public int compare(BookInfo item1, BookInfo item2) {
                        if (item1.bookname == null) {
                            item1.bookname = "";
                        }
                        if (item2.bookname == null) {
                            item2.bookname = "";
                        }
                        return Collator.getInstance(Locale.CHINESE).compare(item1.bookname, item2.bookname);
                    }
                });
            }
            notifyDataSetChanged();
        }
    }

    /**
     * addItems
     * @param aList list
     */
    public void addItems(List<BookInfo> aList) {
        if (aList == null) {
            return;
        }
        this.list = aList;
        //添加免费专区数据
        this.list.add(new BookInfo("", true));
        if (shelfShowMode == MODE_GRID) {
            List<GridItem> gridItemList = listToGridItems(false);
            List<Adapter> adapters = coverGridItemToAdapters(gridItemList);
            if (adapters != null) {
                setAdapters(adapters);
                notifyDataSetChanged();
            }
        } else if (shelfShowMode == MODE_LIST) {
            List<ListItem> listItemList = listToListItems("", false);
            List<Adapter> adapters = coverListItemToAdapters(listItemList);
            if (adapters != null) {
                setAdapters(adapters);
                notifyDataSetChanged();
            }
        }
    }


    /**
     * 刷新签到状态：签到完成和服务器同步签到完成
     *
     * @param aUpdateInfo updateInfo
     */
    public void referenceSignInStatus(BeanBookUpdateInfo aUpdateInfo) {
        if (shelfSignInAdapter != null) {
            shelfSignInAdapter.referenceSignInStatus(aUpdateInfo);
        }
    }

    /**
     * 转化list列表adapter
     *
     * @param listItemList ：list列表
     * @return
     */
    private List<Adapter> coverListItemToAdapters(List<ListItem> listItemList) {
        List<DelegateAdapter.Adapter> adapters = new LinkedList<>();
        for (int i = 0; i < listItemList.size(); i++) {
            ListItem listItem = listItemList.get(i);
            if (listItem != null) {
                if (listItem.type == ListItem.TYPE_BOOK_SIGNIN) {
                    shelfSignInAdapter = new ShelfSignInAdapter(mContext, updateInfo);
                    adapters.add(shelfSignInAdapter);
                } else if (listItem.type == ListItem.TYPE_BOOK_EMPTY) {
                    ShelfListEmptyAdapter emptyAdapter = new ShelfListEmptyAdapter(mContext);
                    adapters.add(emptyAdapter);
                } else if (listItem.type == ListItem.TYPE_BOOK_LIST) {
                    boolean isShelf = true;
                    if (shelfManaageMode == MODE_MANAGER) {
                        isShelf = false;
                    }
                    ShelfListAdapter listAdapter = new ShelfListAdapter(mContext, mFragment, shelfPresenter, listItem.bookInfo, isShelf);
                    adapters.add(listAdapter);
                }
            }
        }
        return adapters;
    }

    /**
     * 转化宫格列表
     *
     * @param gridItemList：宫格列表
     * @return
     */
    private List<Adapter> coverGridItemToAdapters(List<GridItem> gridItemList) {
        List<DelegateAdapter.Adapter> adapters = new LinkedList<>();
        int count = 0;
        for (int i = 0; i < gridItemList.size(); i++) {
            GridItem gridItem = gridItemList.get(i);
            if (gridItem != null) {
                if (gridItem.type == GridItem.TYPE_BOOK_SIGNIN) {
                    shelfSignInAdapter = new ShelfSignInAdapter(mContext, updateInfo);
                    adapters.add(shelfSignInAdapter);
                } else if (gridItem.type == GridItem.TYPE_BOOK_GRID) {
                    boolean isShelf = true;
                    if (shelfManaageMode == MODE_MANAGER) {
                        isShelf = false;
                    }
                    ShelfGridAdapter gridAdapter = new ShelfGridAdapter(mContext, gridItem.list, mFragment, shelfPresenter, isShelf, gridItemNum);
                    adapters.add(gridAdapter);
                    count++;
                }
            }
        }

        if (count < shelfNum) {
            for (int i = 0; i < shelfNum - count; i++) {
                ShelfGridBkAdapter gridBkAdapter = new ShelfGridBkAdapter(mContext);
                adapters.add(gridBkAdapter);
            }
        }
        return adapters;
    }

    /**
     * 书籍转为list列表
     *
     * @param bookid
     * @return
     */
    private List<ListItem> listToListItems(String bookid, boolean isManager) {
        List<ListItem> listItemList = new ArrayList<>();
        if (shelfManaageMode == MODE_COMMON) {
            listItemList.add(new ListItem(ListItem.TYPE_BOOK_SIGNIN));
        }
        listItemList.add(new ListItem(ListItem.TYPE_BOOK_EMPTY));
        for (int i = 0; i < list.size(); i++) {
            BookInfo bookInfo = list.get(i);
            if (isManager) {
                if (!bookInfo.isAddButton()) {
                    bookInfo.blnIsChecked = !TextUtils.isEmpty(bookid) && TextUtils.equals(bookid, bookInfo.bookid);
                    listItemList.add(new ListItem(ListItem.TYPE_BOOK_LIST, bookInfo));
                }
            } else {
                bookInfo.blnIsChecked = !TextUtils.isEmpty(bookid) && TextUtils.equals(bookid, bookInfo.bookid);
                listItemList.add(new ListItem(ListItem.TYPE_BOOK_LIST, bookInfo));
            }
        }
        return listItemList;
    }

    /**
     * 书籍转为宫格列表
     *
     * @return
     */
    private List<GridItem> listToGridItems(boolean isManager) {
        List<GridItem> gridItemList = new ArrayList<>();
        if (shelfManaageMode == MODE_COMMON) {
            gridItemList.add(new GridItem(GridItem.TYPE_BOOK_SIGNIN));
        }
        int position = 0;
        if (isManager && list.size() > 0) {
            BookInfo bookInfo = list.get(list.size() - 1);
            if (bookInfo != null && bookInfo.isAddButton()) {
                list.remove(bookInfo);
            }
        } else {
            if (list.size() > 0) {
                BookInfo bookInfo = list.get(list.size() - 1);
                if (bookInfo != null && !bookInfo.isAddButton()) {
                    list.add(new BookInfo("", true));
                }
            }
        }
        while (position * gridItemNum < list.size()) {
            gridItemList.add(new GridItem(GridItem.TYPE_BOOK_GRID, list.subList(position * gridItemNum, Math.min(position * gridItemNum + gridItemNum, list.size()))));
            position++;
        }
        return gridItemList;
    }

    /**
     * GridItem
     */
    public static class GridItem {
        static final int TYPE_BOOK_SIGNIN = 0x01;
        static final int TYPE_BOOK_GRID = 0x02;

        /**
         * 类型
         */
        public int type;
        /**
         * 数据
         */
        public List<BookInfo> list;

        GridItem(int type) {
            this.type = type;
        }

        GridItem(int type, List<BookInfo> list) {
            this.type = type;
            this.list = list;
        }
    }

    /**
     * ListItem
     */
    public static class ListItem {
        static final int TYPE_BOOK_SIGNIN = 0x01;
        static final int TYPE_BOOK_EMPTY = 0x02;
        static final int TYPE_BOOK_LIST = 0x03;

        /**
         * type
         */
        public int type;
        /**
         * bookInfo
         */
        public BookInfo bookInfo;

        ListItem(int type) {
            this.type = type;
        }

        ListItem(int type, BookInfo bookInfo) {
            this.type = type;
            this.bookInfo = bookInfo;
        }
    }
}

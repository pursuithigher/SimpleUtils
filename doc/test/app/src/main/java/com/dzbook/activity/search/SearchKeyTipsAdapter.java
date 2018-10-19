package com.dzbook.activity.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.BaseLoadActivity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.presenter.SearchPresenterImpl;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.WpsModel;
import com.dzbook.view.search.SearchKeysView;
import com.dzbook.view.search.SearchTextView;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.seach.BeanSearchEmpty;
import hw.sdk.net.bean.seach.SuggestItem;

/**
 * 搜索adapter
 *
 * @author dongdianzhou on 2017/3/29.
 */
public class SearchKeyTipsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_MORE_BOOK = 2;
    private static final int TYPE_DEFAULT = 3;
    private SearchKeysBeanInfo mSearchKeys;

    private String highKey = "";
    private SearchPresenterImpl mSearchPresenter;
    private List<Object> list;


    private Activity mActivity;
    private int firstSuggestItem = -1;

    /**
     * 构造
     *
     * @param activity activity
     */
    public SearchKeyTipsAdapter(Activity activity) {
        list = new ArrayList<>();
        this.mActivity = activity;
    }

    public void setSearchPresenter(SearchPresenterImpl searchPresenter) {
        this.mSearchPresenter = searchPresenter;
    }

    @Override
    public int getItemViewType(int position) {
        if (!ListUtils.isEmpty(list)) {
            Object object = list.get(position);
            if (null == object) {
                return 0;
            }
            if (object instanceof BeanSearchEmpty) {
                BeanSearchEmpty beanSearchEmpty = (BeanSearchEmpty) object;
                if (beanSearchEmpty.getType() == 1) {
                    return TYPE_HEADER;
                } else {
                    return TYPE_MORE_BOOK;
                }
            } else {
                return TYPE_DEFAULT;
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        switch (type) {
            case TYPE_HEADER:
            case TYPE_MORE_BOOK:
                return new SearchEmptyViewHolder(new SearchTextView(viewGroup.getContext()));
            case TYPE_DEFAULT:
            default:
                return new SearchKeyViewHolder(new SearchKeysView(viewGroup.getContext()));
        }
    }

    private int getFirstSuggestItem() {
        if (!ListUtils.isEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                Object o = list.get(i);
                if (o instanceof SuggestItem) {
                    return i;
                }
            }

        }
        return -1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Object object = list.get(position);
        boolean isShowLine = false;
        if (null == object) {
            return;
        }
        if (viewHolder instanceof SearchEmptyViewHolder) {
            BeanSearchEmpty beanSearchEmpty = (BeanSearchEmpty) object;
            ((SearchEmptyViewHolder) viewHolder).bindData(mSearchKeys, highKey, beanSearchEmpty);
        } else {
            if (mSearchKeys.isExistBooks() && firstSuggestItem == position) {
                isShowLine = true;
            }
            ((SearchKeyViewHolder) viewHolder).bindData(highKey, object, isShowLine);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 清空数据
     */
    public void clearData() {
        mSearchKeys = null;
        this.highKey = "";
        if (!ListUtils.isEmpty(list)) {
            list.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 设置数据
     *
     * @param searchKeysBeanInfo searchKeysBeanInfo
     * @param key                key
     * @param isShowMore         isShowMore
     */
    @SuppressLint({"StringFormatMatches", "StringFormatInvalid"})
    public void setData(SearchKeysBeanInfo searchKeysBeanInfo, String key, boolean isShowMore) {
        if (!ListUtils.isEmpty(list)) {
            list.clear();
        }
        this.highKey = key;
        mSearchKeys = searchKeysBeanInfo;
        if (null != mSearchKeys) {
            if (mSearchKeys.isExistBooks()) {
                if (isShowMore) {
                    list.addAll(mSearchKeys.getShowLocalBooks());
                } else {
                    list.addAll(mSearchKeys.getShowAllLocalBooks());
                }
                BeanSearchEmpty beanSearchEmptyHeader = new BeanSearchEmpty();
                beanSearchEmptyHeader.setMsg(String.format(mActivity.getResources().getString(R.string.str_has_numb_book), mSearchKeys.getShowAllLocalBooks().size()));
                beanSearchEmptyHeader.setType(1);
                list.add(0, beanSearchEmptyHeader);
            }
            if (isShowMore) {
                if (mSearchKeys.isShowMoreLocalBooks()) {
                    BeanSearchEmpty beanSearchEmpty = new BeanSearchEmpty();
                    beanSearchEmpty.setMsg(String.format(mActivity.getResources().getString(R.string.str_look_all_book), mSearchKeys.getShowAllLocalBooks().size()));
                    beanSearchEmpty.setType(2);
                    list.add(beanSearchEmpty);
                }
            }
            if (mSearchKeys.isExistKeys()) {
                list.addAll(mSearchKeys.getSearchKeys());
            }
        }
        notifyDataSetChanged();
        firstSuggestItem = getFirstSuggestItem();
    }

    /**
     * 空页面的adapter
     */
    class SearchEmptyViewHolder extends RecyclerView.ViewHolder {

        private final SearchTextView searchTextView;

        public SearchEmptyViewHolder(View itemView) {
            super(itemView);
            searchTextView = (SearchTextView) itemView;
        }

        public void bindData(SearchKeysBeanInfo searchKeysBeanInfo, String key, BeanSearchEmpty beanSearchEmpty) {
            searchTextView.bindData(beanSearchEmpty);

            bindClick(searchKeysBeanInfo, key, beanSearchEmpty);
        }

        private void bindClick(final SearchKeysBeanInfo searchKeysBeanInfo, final String key, BeanSearchEmpty beanSearchEmpty) {
            if (beanSearchEmpty == null || beanSearchEmpty.getType() == 1) {
                return;
            }
            searchTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setData(searchKeysBeanInfo, key, false);
                }
            });
        }
    }

    /**
     * holder
     */
    class SearchKeyViewHolder extends RecyclerView.ViewHolder {

        private SearchKeysView searchKeysView;

        public SearchKeyViewHolder(final View itemView) {
            super(itemView);
            searchKeysView = (SearchKeysView) itemView;
        }

        public void bindData(String key, Object bean, boolean isShowLine) {
            searchKeysView.bindData(key, bean, isShowLine);
            bindClick(bean);
        }

        private void bindClick(final Object bean) {
            if (null == bean) {
                return;
            }
            searchKeysView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SuggestItem suggestItem = null;
                    BookInfo bookInfo = null;
                    if (bean instanceof BookInfo) {
                        bookInfo = (BookInfo) bean;
                    } else if (bean instanceof SuggestItem) {
                        suggestItem = (SuggestItem) bean;
                    }

                    switch (searchKeysView.clickType) {
                        case SearchKeysView.TO_BOOKDETAIL:
                            //去图书详情
                            mSearchPresenter.addHistoryList(suggestItem.title);
                            toBookDetail(view, suggestItem);
                            break;
                        case SearchKeysView.TO_READ:
                            //去阅读
                            toRead(view, bookInfo);
                            break;
                        case SearchKeysView.TO_SEARCH_NO_NEXT:
                            //去搜索
                            toSearchNoNext(suggestItem);
                            break;
                        case SearchKeysView.TO_SEARCH_EXIST_NEXT:
                            toSearchExistNext(suggestItem);
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        private void toBookDetail(View view, SuggestItem suggestItem) {
            if (null != suggestItem && !TextUtils.isEmpty(suggestItem.bookId)) {
                DzLog.getInstance().logClick(LogConstants.MODULE_SSYM, LogConstants.ZONE_SSYM_SJSS, suggestItem.title, null, null);
                Intent intent = new Intent(view.getContext(), BookDetailActivity.class);
                intent.putExtra("bookId", suggestItem.bookId);
                view.getContext().startActivity(intent);
                BaseActivity.showActivity(view.getContext());
            }
        }

        private void toRead(View view, BookInfo bookInfo) {
            if (null != bookInfo) {
                DzLog.getInstance().logClick(LogConstants.MODULE_SSYM, LogConstants.ZONE_SSYM_QYD, bookInfo.bookname, null, null);
                if (bookInfo.isJump()) {
                    WpsModel.openFile4wps(mActivity, bookInfo);
                } else {
                    ReaderUtils.continueReadBook((BaseLoadActivity) view.getContext(), bookInfo);
                }
            } else {
                if (mActivity instanceof BaseActivity) {
                    ((BaseActivity) mActivity).showNotNetDialog();
                }
            }
        }

        private void toSearchNoNext(SuggestItem suggestItem) {
            if (null != suggestItem && !TextUtils.isEmpty(suggestItem.title)) {
                if (!TextUtils.isEmpty(suggestItem.type)) {
                    mSearchPresenter.addHistoryList(suggestItem.title);
//                    SearchActivity.toSearch(searchKeysView.getContext(), suggestItem.title, suggestItem.type, false);
                    mSearchPresenter.searchFixedKey(suggestItem.title, LogConstants.ZONE_SSYM_LXSS, suggestItem.type, true);
                } else {
                    mSearchPresenter.searchFixedKey(suggestItem.title, LogConstants.ZONE_SSYM_LXSS, suggestItem.type, true);
                }
            }
        }

        private void toSearchExistNext(SuggestItem suggestItem) {
            if (null != suggestItem && !TextUtils.isEmpty(suggestItem.title)) {
                mSearchPresenter.searchFixedKey(suggestItem.title, LogConstants.ZONE_SSYM_LXSS, suggestItem.type, false);
            }
        }
    }
}

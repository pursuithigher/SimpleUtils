package com.dzbook.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.utils.WhiteListWorker;
import com.dzbook.view.common.BookListItemView;
import com.dzbook.view.common.StatusView;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.util.ArrayList;
import java.util.HashMap;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.type.BeanMainTypeDetail;


/**
 * MainTypeDetailAdapter
 *
 * @author Winzows  2018/3/5
 */

public class MainTypeDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int EMPTY_VIEW = 0x00013;
    /**
     * 书籍数据
     */
    public ArrayList<BeanBookInfo> bookInfoList;
    private Activity context;
    private boolean isLoading = false;
    private BeanMainTypeDetail.TypeFilterBean filterBean;

    /**
     * 构造
     *
     * @param context context
     */
    public MainTypeDetailAdapter(Activity context) {
        this.context = context;
        bookInfoList = new ArrayList<>();
    }

    /**
     * 构造
     *
     * @param activity   activity
     * @param filterBean filterBean
     */
    public MainTypeDetailAdapter(Activity activity, BeanMainTypeDetail.TypeFilterBean filterBean) {
        this.context = activity;
        bookInfoList = new ArrayList<>();
        this.filterBean = filterBean;
    }

    /**
     * 设置数据
     *
     * @param infos    bookInfoList
     * @param isAppend isAppend
     */
    public void putData(ArrayList<BeanBookInfo> infos, boolean isAppend) {
        if (this.bookInfoList == null) {
            this.bookInfoList = new ArrayList<>();
        }
        if (isAppend) {
            if (infos != null && infos.size() > 0) {
                this.bookInfoList.addAll(infos);
            }
        } else {
            this.bookInfoList.clear();
            if (infos != null && infos.size() > 0) {
                this.bookInfoList.addAll(infos);
            }
        }
        notifyDataSetChanged();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == EMPTY_VIEW) {
            view = LayoutInflater.from(context).inflate(R.layout.view_native_type_empty_loading, parent, false);
            return new EmptyViewHolder(view);
        } else {
            view = new BookListItemView(context, BookListItemView.TYPE_DETAIL);
            return new BookListItemViewViewHolder(view);
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder != null && holder instanceof BookListItemViewViewHolder) {
            BookListItemViewViewHolder itemViewViewHolder = (BookListItemViewViewHolder) holder;
            itemViewViewHolder.clearImageView();
        }
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof BookListItemViewViewHolder) {
            final BeanBookInfo bean = bookInfoList.get(position);
            holder.itemView.setTag(bean);
            ((BookListItemViewViewHolder) holder).bindData(bean, position, getItemCount());
        } else if (holder instanceof EmptyViewHolder) {
            StatusView statusView = ((EmptyViewHolder) holder).defaultviewRechargeEmpty;
            if (isLoading) {
                statusView.setVisibility(View.GONE);
                ((EmptyViewHolder) holder).linearlayoutLoading.setVisibility(View.VISIBLE);
            } else {
                statusView.showEmpty(context.getResources().getString(R.string.string_empty_type));
                ((EmptyViewHolder) holder).linearlayoutLoading.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (bookInfoList == null || bookInfoList.size() == 0) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (bookInfoList != null) {
            int size = bookInfoList.size();
            if (size == 0) {
                return 1;
            } else {
                return size;
            }
        }
        return 0;
    }

    /**
     * EmptyViewHolder
     */
    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        /**
         * StatusView
         */
        public StatusView defaultviewRechargeEmpty;
        /**
         * LinearLayout
         */
        public LinearLayout linearlayoutLoading;

        /**
         * 构造
         *
         * @param rootView rootView
         */
        public EmptyViewHolder(View rootView) {
            super(rootView);

            defaultviewRechargeEmpty = rootView.findViewById(R.id.defaultview_recharge_empty);
            linearlayoutLoading = rootView.findViewById(R.id.linearlayout_loading);
        }
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }


    /**
     * ViewHolder
     */
    public class BookListItemViewViewHolder extends RecyclerView.ViewHolder {

        private BookListItemView view;

        /**
         * 构造
         *
         * @param view view
         */
        public BookListItemViewViewHolder(View view) {
            super(view);
            this.view = (BookListItemView) view;
        }

        /**
         * 绑定数据
         *
         * @param bean      bean
         * @param position  position
         * @param itemCount itemCount
         */
        public void bindData(final BeanBookInfo bean, final int position, int itemCount) {
            if (bean != null) {
                this.view.bindData(bean, position, false, itemCount);
                this.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(bean.bookId)) {
                            ToastAlone.showShort(R.string.download_chapter_error);
                            return;
                        }
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("index", position + "");
                        String zone = "";
                        if (null != filterBean && !TextUtils.isEmpty(filterBean.getCid())) {
                            zone = filterBean.getCid();
                        }
                        WhiteListWorker.resetBookSourceFrom(context);
                        DzLog.getInstance().logClick(LogConstants.MODULE_FLEJB, zone, bean.bookId, hashMap, "");

                        BookDetailActivity.launch(context, bean.bookId, bean.bookName);
                    }
                });
            }
        }

        /**
         * 清除图片
         */
        public void clearImageView() {
            if (this.view != null) {
                this.view.clearImageView();
            }
        }
    }

}

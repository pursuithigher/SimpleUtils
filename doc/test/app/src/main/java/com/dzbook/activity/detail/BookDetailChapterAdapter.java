package com.dzbook.activity.detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ishugui.R;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.BeanBlock;
import hw.sdk.net.bean.BeanChapterInfo;

/**
 * 图书详情目录 adapter
 *
 * @author wangwenzhou on 2017/1/11.
 */
public class BookDetailChapterAdapter extends RecyclerView.Adapter<BookDetailChapterAdapter.ViewHolder> {
    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 章节集合
     */
    private ArrayList<BeanChapterInfo> listBookChapter;

    private ArrayList<String> chapterIdList;

    private OnItemClickListener mOnItemClickListener;

    BookDetailChapterAdapter(Context context) {
        this.mContext = context;
        listBookChapter = new ArrayList<>();
        chapterIdList = new ArrayList<>();
    }

    /**
     * 添加数据
     *
     * @param list  list
     * @param clear clear
     */
    public void addChapterItem(List<BeanChapterInfo> list, boolean clear) {

        if (clear) {
            listBookChapter.clear();
            chapterIdList.clear();
        }
        if (list != null && list.size() > 0) {
            for (BeanChapterInfo bean : list) {
                if (!chapterIdList.contains(bean.chapterId)) {
                    listBookChapter.add(bean);
                    chapterIdList.add(bean.chapterId);
                }
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<BeanChapterInfo> getChapterList() {
        return listBookChapter;
    }

    /**
     * 获取index
     *
     * @param blockBean blockBean
     * @return int
     */
    public int getIndex(BeanBlock blockBean) {
        return chapterIdList.indexOf(blockBean.startId);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View convertView = View.inflate(mContext, R.layout.a_item_chapter, null);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, (BeanChapterInfo) v.getTag());
                }
            }
        });
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        BeanChapterInfo bean = listBookChapter.get(i);
        boolean isHideLine = i == listBookChapter.size() - 1;
        setData(bean, viewHolder, isHideLine);
        viewHolder.itemView.setTag(bean);
    }


    @Override
    public int getItemCount() {
        return listBookChapter.size();
    }

    /**
     * 最新章节
     *
     * @return cid
     */
    public String getLastChapterId() {
        if (chapterIdList.size() > 0) {
            return chapterIdList.get(chapterIdList.size() - 1);
        } else {
            return "";
        }
    }

    /**
     * holder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textFree;
        View viewLine;

        ViewHolder(View rootView) {

            super(rootView);
            textViewName = rootView.findViewById(R.id.textView_name);
            textFree = rootView.findViewById(R.id.textView_free_chapter);
            viewLine = rootView.findViewById(R.id.view_end_line);
        }
    }

    /**
     * 设置数据
     *
     * @param bean       bean
     * @param viewHolder holder
     * @param isHideLine isHideLine
     */
    public void setData(BeanChapterInfo bean, BookDetailChapterAdapter.ViewHolder viewHolder, boolean isHideLine) {
        initData(viewHolder);
        if (bean != null) {
            String free = TextUtils.equals(bean.isCharge, "0") ? "免费" : "";
            viewHolder.textViewName.setText(bean.chapterName + "");
            viewHolder.textFree.setText(free);
            if (isHideLine) {
                viewHolder.viewLine.setVisibility(View.GONE);
            } else {
                viewHolder.viewLine.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initData(BookDetailChapterAdapter.ViewHolder viewHolder) {
        viewHolder.textViewName.setText("");
    }

    /**
     * 自定义item点击事件回调
     */
    public interface OnItemClickListener {
        /**
         * 详情目录被点击
         *
         * @param view view
         * @param bean data
         */
        void onItemClick(View view, BeanChapterInfo bean);
    }

    public void setOnItemClickListener(BookDetailChapterAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

}

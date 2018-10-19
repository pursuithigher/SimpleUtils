package com.dzbook.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dzbook.bean.LocalFileBean;
import com.dzbook.utils.FileUtils;
import com.ishugui.R;

import java.util.ArrayList;

/**
 * 列出文件有BaseAdapter
 *
 * @author lizhongzhong 2013-11-23
 */
public class LocalFileAdapter extends BaseAdapter {

    private static final int TYPE_TITLE = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_GONE = 2;
    private static final int TYPE_COUNT = 3;

    /**
     * 文件的集合、数据源
     */
    private ArrayList<LocalFileBean> beanList;
    private Context mContext;

    /**
     * 构造
     *
     * @param context context
     */
    public LocalFileAdapter(Context context) {
        super();
        mContext = context;
        beanList = new ArrayList<>();
    }

    /**
     * 设置数据
     *
     * @param list list
     */
    public void setData(ArrayList<LocalFileBean> list) {
        beanList.clear();
        if (list != null) {
            beanList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public ArrayList<LocalFileBean> getData() {
        return beanList;
    }

    /**
     * 获取list
     *
     * @return list
     */
    public ArrayList<LocalFileBean> getCheckedList() {
        ArrayList<LocalFileBean> list = new ArrayList<>();
        LocalFileBean bean;
        for (int i = 0; i < beanList.size(); i++) {
            bean = beanList.get(i);
            if (!bean.isTitle && bean.isCheckedFile()) {
                list.add(bean);
            }
        }
        return list;
    }

    /**
     * 删除bean
     *
     * @param list list
     */
    public void deleteBean(ArrayList<LocalFileBean> list) {
        for (LocalFileBean bean : list) {
            for (LocalFileBean abean : beanList) {
                if (!abean.isTitle && abean.fileName.equals(bean.fileName)) {
                    beanList.remove(abean);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 选中全部
     *
     * @return int
     */
    public int selectAll() {
        int sum = 0;
        for (LocalFileBean bean : beanList) {
            if (bean.isAcceptFile() && !bean.isAdded) {
                bean.isChecked = true;
                sum++;
            }
        }
        notifyDataSetChanged();
        return sum;
    }

    /**
     * 全不选
     */
    public void unSelectAll() {
        for (LocalFileBean bean : beanList) {
            if (bean.isAcceptFile() && !bean.isAdded) {
                bean.isChecked = false;
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 不选
     *
     * @param index index
     */
    public void unSelect(LocalFileBean index) {
        for (LocalFileBean bean : beanList) {
            if (TextUtils.equals(bean.filePath, index.filePath)) {
                bean.isChecked = false;
                notifyDataSetChanged();
                break;
            }
        }

    }

    /**
     * 选中
     *
     * @param index index
     */
    public void select(LocalFileBean index) {
        for (LocalFileBean bean : beanList) {
            if (TextUtils.equals(bean.filePath, index.filePath)) {
                bean.isChecked = true;
                notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 添加书籍
     *
     * @param index index
     */
    public void setBookAdded(LocalFileBean index) {
        for (LocalFileBean bean : beanList) {
            if (TextUtils.equals(bean.filePath, index.filePath)) {
                bean.isAdded = true;
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public int getCount() {
        if (beanList == null) {
            return 0;
        } else {
            return beanList.size();

        }
    }

    @Override
    public LocalFileBean getItem(int position) {
        if (position < beanList.size()) {
            return beanList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (beanList.get(position).isTitle) {
            if (position == beanList.size() - 1) {
                return TYPE_GONE;
            }
            if (position < beanList.size() - 1) {
                LocalFileBean nextFileBean = beanList.get(position + 1);
                if (nextFileBean.isTitle || !nextFileBean.firstLetter.equals(beanList.get(position).firstLetter)) {
                    return TYPE_GONE;
                }
            }
            return TYPE_TITLE;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == TYPE_TITLE) {
            TitleViewHolder titleViewHolder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_local_title, null);
                titleViewHolder = new TitleViewHolder(convertView);
                convertView.setTag(titleViewHolder);
            } else {
                titleViewHolder = (TitleViewHolder) convertView.getTag();
            }
            LocalFileBean localFileBean = beanList.get(position);
            setTitleData(localFileBean, titleViewHolder);
        } else if (getItemViewType(position) == TYPE_NORMAL) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_upload, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            LocalFileBean localFileBean = beanList.get(position);
            setData(localFileBean, viewHolder, position);
        } else {
            convertView = new View(mContext);
        }
        return convertView;
    }


    /**
     * 设置数据
     *
     * @param bean       bean
     * @param viewHolder viewHolder
     * @param position   position
     */
    public void setData(LocalFileBean bean, ViewHolder viewHolder, int position) {
        if (bean != null) {

            if (!TextUtils.isEmpty(bean.fileName)) {
                viewHolder.txtUploadTitle.setText(bean.fileName);
            } else {
                viewHolder.txtUploadTitle.setText("");
            }
            if (bean.fileType == LocalFileBean.TYPE_DIR) {
                viewHolder.viewFileIcon.setVisibility(View.VISIBLE);
                viewHolder.viewLineShort.setVisibility(View.VISIBLE);
                viewHolder.viewLineLong.setVisibility(View.INVISIBLE);
                viewHolder.iViUploadFile.setText("");
                viewHolder.iViUploadFile.setBackgroundResource(R.drawable.shelf_upload_filedir_icon);
                viewHolder.txtUploadNum.setText(bean.childNum);
                viewHolder.chbUploadFileFile.setVisibility(View.GONE);
                viewHolder.textViewImported.setVisibility(View.GONE);
            } else {
                viewHolder.viewLineShort.setVisibility(View.INVISIBLE);
                viewHolder.viewLineLong.setVisibility(View.VISIBLE);
                viewHolder.viewFileIcon.setVisibility(View.GONE);
                if (bean.fileType == LocalFileBean.TYPE_TXT) {
                    viewHolder.iViUploadFile.setBackgroundResource(R.drawable.shape_hw_book_type_txt);
                    viewHolder.iViUploadFile.setText("TXT");
                } else if (bean.fileType == LocalFileBean.TYPE_EPUB) {
                    viewHolder.iViUploadFile.setBackgroundResource(R.drawable.shape_hw_book_type_epub);
                    viewHolder.iViUploadFile.setText("EPUB");
                } else {
                    viewHolder.iViUploadFile.setBackgroundResource(R.drawable.shape_hw_book_type_epub);
                    viewHolder.iViUploadFile.setText(LocalFileBean.getFileTypeName(bean.fileType));
                }
                String size = FileUtils.formatFileSize(bean.size);
                viewHolder.txtUploadNum.setText(String.valueOf(size));
                if (bean.isAdded) {
                    viewHolder.chbUploadFileFile.setVisibility(View.GONE);
                    viewHolder.textViewImported.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.chbUploadFileFile.setVisibility(View.VISIBLE);
                    viewHolder.textViewImported.setVisibility(View.GONE);
                    viewHolder.chbUploadFileFile.setChecked(bean.isChecked);
                }
            }
        }
        // 去掉租后一个item的横线（上面type 不一样）
        if (position < beanList.size() - 1 && getItemViewType(position + 1) == TYPE_TITLE) {
            viewHolder.viewLineShort.setVisibility(View.INVISIBLE);
            viewHolder.viewLineLong.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.viewLineShort.setVisibility(View.VISIBLE);
            viewHolder.viewLineLong.setVisibility(View.VISIBLE);
        }
        if (position == beanList.size() - 1) {
            viewHolder.viewLineShort.setVisibility(View.INVISIBLE);
            viewHolder.viewLineLong.setVisibility(View.INVISIBLE);
        }
    }

    private void setTitleData(LocalFileBean bean, TitleViewHolder titleViewHolder) {
        String title;
        if (bean.fileType == LocalFileBean.TYPE_DIR) {
            title = "文件夹";
        } else if (bean.sortType == LocalFileBean.TYPE_SORT_TIEM) {
            title = bean.lastModifiedDesc;
        } else {
            title = bean.firstLetter;
        }
        titleViewHolder.txtTitle.setText(title);
    }

    /**
     * ViewHolder内部类
     */
    public static class ViewHolder {

        private TextView txtUploadTitle;
        private TextView txtUploadNum;
        private TextView iViUploadFile;
        private CheckBox chbUploadFileFile;
        private TextView textViewImported;
        private View viewLineLong;
        private View viewLineShort;
        private View viewFileIcon;

        /**
         * 构造
         *
         * @param rootView rootView
         */
        public ViewHolder(View rootView) {

            txtUploadTitle = rootView.findViewById(R.id.textview_upload_title);
            txtUploadNum = rootView.findViewById(R.id.textview_upload_num);
            iViUploadFile = rootView.findViewById(R.id.imageview_upload_file_img);
            chbUploadFileFile = rootView.findViewById(R.id.checkbox_uploadfile_file);
            textViewImported = rootView.findViewById(R.id.textview_imported);
            viewLineLong = rootView.findViewById(R.id.line_long);
            viewLineShort = rootView.findViewById(R.id.line_short);
            viewFileIcon = rootView.findViewById(R.id.file_right_icon);
        }
    }

    /**
     * ViewHolder
     */
    public static class TitleViewHolder {

        private TextView txtTitle;

        TitleViewHolder(View rootView) {
            txtTitle = rootView.findViewById(R.id.local_title);
        }
    }

}

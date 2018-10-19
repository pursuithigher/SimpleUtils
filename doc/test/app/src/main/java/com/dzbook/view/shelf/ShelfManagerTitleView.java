package com.dzbook.view.shelf;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

/**
 * 书架管理标题
 *
 * @author dongdianzhou on 2017/7/21.
 */

public class ShelfManagerTitleView extends RelativeLayout {

    private ImageView imageViewCloesd;
    private TextView textViewSelect;

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ShelfManagerTitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }

    private void setListener() {

    }

    private void initData() {

    }

    /**
     * 关闭监听
     *
     * @param onClickListener onClickListener
     */
    public void setClosedListener(OnClickListener onClickListener) {
        imageViewCloesd.setOnClickListener(onClickListener);
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_shelmanager_title, this, true);
        imageViewCloesd = findViewById(R.id.imageview_closed);
        textViewSelect = findViewById(R.id.textview_select);
        TypefaceUtils.setHwChineseMediumFonts(textViewSelect);
    }

    /**
     * 设置标题
     *
     * @param selectBooksNum selectBooksNum
     */
    public void setTitleText(int selectBooksNum) {
        if (selectBooksNum > 0) {
            textViewSelect.setText(String.format(getResources().getString(R.string.str_shelf_selected_count), selectBooksNum));
        } else {
            textViewSelect.setText(getResources().getString(R.string.str_shelf_noselect));
        }
    }


}

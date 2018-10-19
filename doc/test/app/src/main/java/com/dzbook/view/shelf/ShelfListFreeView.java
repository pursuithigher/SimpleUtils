package com.dzbook.view.shelf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.mvp.presenter.MainShelfPresenter;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;

/**
 * 书架列表免费标签
 */
public class ShelfListFreeView extends RelativeLayout {

    /**
     * 构造
     *
     * @param context        context
     * @param shelfPresenter shelfPresenter
     */
    public ShelfListFreeView(Context context, final MainShelfPresenter shelfPresenter) {
        super(context, null);
        View view = LayoutInflater.from(context).inflate(R.layout.view_shelffree, this);
        view.setBackground(null);
        TextView textView = view.findViewById(R.id.textview);
        TypefaceUtils.setHwChineseMediumFonts(textView);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shelfPresenter.skipToSpecialOfferBookActivity();
            }
        });
    }


}

package com.dzbook.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dzbook.activity.search.SearchActivity;
import com.dzbook.dialog.DialogShelfMenuManage;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.MainShelfUI;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.ishugui.R;

import hw.sdk.net.bean.store.TempletContant;

/**
 * 华为首页通用title：
 *
 * @author dongdianzhou on 2017/4/5.
 */

public class DzComTitleIndex extends RelativeLayout implements View.OnClickListener {

    private Context mContext;

    private LinearLayout linearLayoutSearch;
    private ImageView mImageViewOper1;

    private DialogShelfMenuManage mDialogShelfMenu;

    private MainShelfUI shelfUI;

    private int defaultPadding = 0;

    private boolean isFromShelf;

    private long lastClickTime = 0;

    /**
     * 构造
     *
     * @param context context
     */
    public DzComTitleIndex(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public DzComTitleIndex(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        defaultPadding = DimensionPixelUtil.dip2px(getContext(), 16);
        initView(attrs);
        initData();
        setListener();
    }

    public void setShelfUI(MainShelfUI shelfUI) {
        this.shelfUI = shelfUI;
    }

    private void setListener() {
        linearLayoutSearch.setOnClickListener(this);
        mImageViewOper1.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - lastClickTime > TempletContant.CLICK_DISTANSE) {
            int id = v.getId();
            if (id == R.id.imageviewopr1) {
                ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.BOOK_SHELF_TM_UMENG_ID, null, 1);
                if (mDialogShelfMenu == null) {
                    mDialogShelfMenu = new DialogShelfMenuManage((Activity) mContext, shelfUI);
                }
                mDialogShelfMenu.setMainShelfUI(shelfUI);
                int yOff = DimensionPixelUtil.dip2px(mContext, 6);
                mDialogShelfMenu.showAsDropDown(getOper1View(), 0, -yOff);
            } else if (id == R.id.linearlayout_search) {
                openSearch();
            }
        }
        lastClickTime = currentClickTime;
    }

    private void initView(AttributeSet attrs) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.title_common_index, this);
        mImageViewOper1 = view.findViewById(R.id.imageviewopr1);
        linearLayoutSearch = view.findViewById(R.id.linearlayout_search);
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.DzComTitleIndex, 0, 0);
            if (array != null) {
                Drawable drawable = array.getDrawable(R.styleable.DzComTitleIndex_common_title_opr1);
                if (mImageViewOper1 != null) {
                    if (drawable != null) {
                        mImageViewOper1.setImageDrawable(drawable);
                    }
                    boolean isShowOpr1 = array.getBoolean(R.styleable.DzComTitleIndex_common_title_showopr1, true);
                    if (isShowOpr1) {
                        mImageViewOper1.setVisibility(VISIBLE);
                        setPadding(defaultPadding, 0, 0, 0);
                    } else {
                        mImageViewOper1.setVisibility(GONE);
                        setPadding(defaultPadding, 0, defaultPadding, 0);
                    }
                }
                array.recycle();
            }
        }
    }

    /**
     * 得到第一个操作对view
     *
     * @return view
     */
    public View getOper1View() {
        return mImageViewOper1;
    }


    /**
     * 销毁PopWindow
     */
    public void destroyPopWindow() {
        if (mDialogShelfMenu != null && mDialogShelfMenu.isVisible()) {
            mDialogShelfMenu.dismiss();
            mDialogShelfMenu = null;
        }
    }

    public void setSource(boolean source) {
        this.isFromShelf = source;
    }

    private void openSearch() {
        DzLog.getInstance().logClick(LogConstants.MODULE_NSC, LogConstants.ZONE_NSC_NSCSS, "", null, "");
        ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.BOOK_SHELF_SEACH_UMENG_ID, null, 1);
        SearchActivity.launch((Activity) getContext(), isFromShelf);
    }
}

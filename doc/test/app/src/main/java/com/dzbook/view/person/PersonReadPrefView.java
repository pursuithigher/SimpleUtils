package com.dzbook.view.person;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.utils.HwLog;
import com.dzbook.utils.SpUtil;
import com.ishugui.R;

import java.util.HashMap;

/**
 * 偏好
 *
 * @author dongdianzhou on 2017/4/6.
 */

public class PersonReadPrefView extends RelativeLayout implements View.OnClickListener {

    private ImageView mImageViewSelect;
    private LinearLayout mLinearLayoutSelect;

    private boolean isBoy;
    private SpUtil sp;
    private View viewLine;

    /**
     * 构造
     *
     * @param context context
     */
    public PersonReadPrefView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public PersonReadPrefView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
        initData();
        setListener();
    }

    private void setListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem();
            }
        });
        mLinearLayoutSelect.setOnClickListener(this);
        mImageViewSelect.setOnClickListener(this);
    }

    private void selectItem() {
        int readpref = 2;
        if (isBoy) {
            readpref = 1;
        }
        int lastReadPref = sp.getPersonReadPref();
        if (readpref == lastReadPref) {
            return;
        }
        setSelectViewState(true);
        sp.setPersonReadPref(readpref);
        HwLog.setPh(readpref + "");
        sp.setPersonExistsReadPref(true);
        /*Bundle bundle = new Bundle();
        bundle.putString(EventConstant.EVENT_BOOKSTORE_TYPE, EventConstant.SKIP_TAB_STORE);
        EventBusUtils.sendMessage(EventConstant.UPDATA_FEATURED_URL_REQUESTCODE, EventConstant.TYPE_BOOK_STORE, bundle);*/
        HashMap<String, String> map = new HashMap<>();
        String lastAdId = lastReadPref + "";
        switch (lastReadPref) {
            case 0:
                lastAdId = "3";
                break;
            default:
                break;
        }
        map.put("lastAdId", lastAdId);
        DzLog.getInstance().logClick(LogConstants.MODULE_PHSZ, LogConstants.ZONE_YDYM_YHPH, readpref + "", map, null);
        ((Activity) getContext()).finish();
    }

    private void initData() {
        sp = SpUtil.getinstance(getContext());
    }

    private void initView(AttributeSet attrs) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_person_readpref, this);
        TextView mTvTitle = view.findViewById(R.id.textview_title);
        TextView mTvContent = view.findViewById(R.id.tv_content);
        mImageViewSelect = view.findViewById(R.id.imageview_select);
        mLinearLayoutSelect = view.findViewById(R.id.linearlayout_select);
        viewLine = view.findViewById(R.id.view_line);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PersonReadPrefView, 0, 0);
        if (a != null) {
            String title = a.getString(R.styleable.PersonReadPrefView_view_title);
            mTvTitle.setText(title);
            String content = a.getString(R.styleable.PersonReadPrefView_view_content);
            mTvContent.setText(content);
            isBoy = a.getBoolean(R.styleable.PersonReadPrefView_isBoy, true);
            boolean isLine = a.getBoolean(R.styleable.PersonReadPrefView_isLine, true);
            if (!isLine) {
                viewLine.setVisibility(GONE);
            }
            a.recycle();
        }
    }

    /**
     * 设置选中状态
     *
     * @param isSelect isSelect
     */
    public void setSelectViewState(boolean isSelect) {
        mImageViewSelect.setSelected(isSelect);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_select:
            case R.id.linearlayout_select:
                selectItem();
                break;
            default:
                break;
        }
    }

}

package com.dzbook.activity.person;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dzbook.BaseSwipeBackActivity;
import com.dzbook.utils.SpUtil;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.person.PersonReadPrefView;
import com.ishugui.R;
import com.iss.app.BaseActivity;

/**
 * 用户偏好
 *
 * @author dongdianzhou on 2017/4/6.
 */
public class PersonReadPrefActivity extends BaseSwipeBackActivity {


    private static final String TAG = "PersonReadPrefActivity";

    private DianZhongCommonTitle mCommonTitle;
    private PersonReadPrefView mPrefBoy;
    private PersonReadPrefView mPrefGirl;

    private SpUtil spUtil;

    /**
     * 启动
     *
     * @param activity activity
     */
    public static void launch(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, PersonReadPrefActivity.class);
        activity.startActivity(intent);
        BaseActivity.showActivity(activity);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personreadpref);
    }

    @Override
    protected void initView() {
        super.initView();
        mCommonTitle = findViewById(R.id.commontitle);
        mPrefBoy = findViewById(R.id.readPrefView_boy);
        mPrefGirl = findViewById(R.id.readPrefView_girl);
    }

    @Override
    protected void initData() {
        super.initData();
        spUtil = SpUtil.getinstance(this);
        setReadPrefSelectState();
    }

    private void setReadPrefSelectState() {
        int readPref = spUtil.getPersonReadPref();
        switch (readPref) {
            case 0://跳过
                break;
            case 1://男生
                mPrefGirl.setSelectViewState(false);
                mPrefBoy.setSelectViewState(true);
                break;
            case 2://女声
                mPrefBoy.setSelectViewState(false);
                mPrefGirl.setSelectViewState(true);
                break;
            default:
                break;
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        mPrefBoy.setSelectListener(new PersonReadPrefView.SelectListener() {
//            @Override
//            public void onSelect() {
//                setReadPrefSelectState();
//            }
//        });
//        mPrefGirl.setSelectListener(new PersonReadPrefView.SelectListener() {
//            @Override
//            public void onSelect() {
//                setReadPrefSelectState();
//            }
//        });
    }
}

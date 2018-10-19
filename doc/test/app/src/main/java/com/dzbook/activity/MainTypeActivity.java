package com.dzbook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dzbook.BaseTransparencyLoadActivity;
import com.dzbook.fragment.MainTypeContentFragment;
import com.ishugui.R;
import com.iss.app.BaseActivity;

/**
 * 分类
 *
 * @author Winzows 2018/3/9
 */
public class MainTypeActivity extends BaseTransparencyLoadActivity {

    private static final String TAG = "MainTypeActivity";

    /**
     * 打开分类
     *
     * @param context context
     */
    public static void launch(Context context) {
        Intent intent = new Intent(context, MainTypeActivity.class);
        context.startActivity(intent);
        BaseActivity.showActivity(context);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_native_type);
        //必需继承FragmentActivity,嵌套fragment只需要这行代码
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new MainTypeContentFragment()).commitAllowingStateLoss();
    }

    @Override
    protected void initView() {
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    public int getStatusColor() {
        return R.color.color_100_ffffff;
    }
}

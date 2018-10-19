package com.ishugui.wxapi;

import android.os.Bundle;

import com.dzbook.BaseWXEnTryActivity;

/**
 * 微信分享回调页
 *
 * @author wangwenzhou on 16/9/8.
 */
public class WXEntryActivity extends BaseWXEnTryActivity {

    private static final String TAG = "WXEntryActivity";

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}

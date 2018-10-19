package com.dzbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dzbook.activity.account.ConsumeSecondActivity;
import com.dzbook.fragment.main.BaseFragment;
import com.ishugui.R;

/**
 * 消费书籍
 *
 * @author KongXP on 2018/4/20.
 */
public class ConsumeBookSecondFragment extends BaseFragment {

    private View giftView;
    private View vipView;

    @Override
    public String getTagName() {
        return "ConsumeBookSecondFragment";
    }

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consume_two_view, container, false);
    }

    @Override
    protected void initView(View uiView) {
        giftView = uiView.findViewById(R.id.gift);
        vipView = uiView.findViewById(R.id.vip);
    }

    @Override
    protected void initData(View uiView) {

    }

    @Override
    protected void setListener(View uiView) {
        giftView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConsumeSecondActivity.launch(getActivity(), "", "2");

            }
        });
        vipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConsumeSecondActivity.launch(getActivity(), "", "3");
            }
        });
    }
}

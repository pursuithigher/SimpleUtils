package com.views.ui.menu;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.views.simpleutils.R;

/**
 * Created by qzzhu on 17-5-23.
 */
public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        setMenuBackGround();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        init();
    }

    /**
     * use theme in XML to change popUp text color and popUp background
     */
    private void init(){
        Toolbar bar = (Toolbar) findViewById(R.id.menus_toolbar);
        bar.setLogo(R.mipmap.ic_launcher);
        bar.setTitle("title");
        bar.setSubtitle("sub");

        setSupportActionBar(bar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menus,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu1:

                break;
            case R.id.menu2:

                break;
            case R.id.menu3:

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 代码修改menu
     */
    protected void setMenuBackGround() {
        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), new LayoutInflaterFactory()
        {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs)
            {

                //你可以在这里将系统类替换为自定义View
                //appcompat 创建view代码
                AppCompatDelegate delegate = getDelegate();
                View view = delegate.createView(parent, name, context, attrs);
                //你可以在这里直接new自定义View
                // 指定自定义inflater的对象
                if (name.equalsIgnoreCase("android.support.v7.view.menu.ListMenuItemView")) {
                    try {
                        if (view instanceof TextView) {
                            ((TextView) view).setTextColor(Color.BLUE/*这里修改颜色*/);
                        }
                        return view;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return view;
            }
        });
    }

}

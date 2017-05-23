package com.views.ui.coordinate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.views.simpleutils.R;
import com.views.ui.coordinate.behavior.BottomSheetActivity;

/**
 * Created by qzzhu on 17-5-23.
 */

public class CoordinateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coorAndFloatingButton();
//        coorAndToolBar();
//        coorAndCollpaseBar();
    }

    private void coorAndFloatingButton(){
        setContentView(R.layout.coordiate_design);
        final View content = findViewById(R.id.coordinateLayout);
        FloatingActionButton floating_button = (FloatingActionButton) findViewById(R.id.floating_button);
        floating_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Snackbar.make(content,"clicked",Snackbar.LENGTH_SHORT).show();
            }
        });
        BottomSheetActivity.setBottomSheet(content);
    }

    private void coorAndToolBar(){
        setContentView(R.layout.coordiate_toolbar_list);
        Toolbar bar = (Toolbar) findViewById(R.id.coor_toolbar);
        bar.setLogo(R.mipmap.ic_launcher);
        bar.setTitle("title");
        bar.setSubtitle("sub");

        setSupportActionBar(bar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("title1"));
        tabLayout.addTab(tabLayout.newTab().setText("title2"));

    }

    private void coorAndCollpaseBar(){
        setContentView(R.layout.coordiate_collpase_list);
        Toolbar bar = (Toolbar) findViewById(R.id.coor_toolbar);
        bar.setLogo(R.mipmap.ic_launcher);
        bar.setTitle("title");
        bar.setSubtitle("sub");
        setSupportActionBar(bar);
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
//        tabLayout.addTab(tabLayout.newTab().setText("title1"));
//        tabLayout.addTab(tabLayout.newTab().setText("title2"));
    }
}

package com.test;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.views.simpleutils.R;
import com.views.ui.customviewgroup.SwipeLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swpieLayoutTest();
    }

    private void swpieLayoutTest(){
        setContentView(R.layout.swipelayout_test);
        SwipeLayout layout = (SwipeLayout) findViewById(R.id.linearGroup);
        layout.addViews(getButton("1"),getButton("3"),getButton("2"),getButton("5"));
    }
    private Button getButton(String text){
        Button button = new Button(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(96,LinearLayout.LayoutParams.MATCH_PARENT));
        button.setText(text);
        button.setTextColor(Color.RED);
        button.setOnClickListener(clickListener);
        button.setTag(text);
        return button;
    }
    private final View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String value = (String) v.getTag();
            Toast.makeText(MainActivity.this,value,Toast.LENGTH_SHORT).show();
        }
    };
}

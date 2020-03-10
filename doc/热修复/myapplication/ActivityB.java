package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityB extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.activity_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityB.this,ActivityB.class);
                intent.putExtra("from",String.valueOf(System.currentTimeMillis()));
                startActivity(intent);
                HookUtils.getActivities();
            }
        });
        TextView textView = findViewById(R.id.activity_text);
        textView.setText(getIntent().getStringExtra("from"));
    }
}

package com.test;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.data.provider.AccountBean;
import com.views.simpleutils.R;
import com.views.ui.customview.RecyclerviewDecorAndDrag.TestAdapter;
import com.views.ui.customview.dialog.LoadingProcess;
import com.views.ui.customviewgroup.LoadLayout;
import com.views.ui.customviewgroup.SwipeLayout;

import static com.data.provider.AccountBean.URI_TABEL1;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        swpieLayoutTest();
//        testProvider();
        setTestLoading();
    }

    LoadLayout process;
    private void setTestLoading(){
        setContentView(R.layout.activity_loadcover);
        process = (LoadLayout) findViewById(R.id.item_loader);
        RecyclerView recycler =process.getRecycleView();
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        TestAdapter adapter = new TestAdapter(this);
        recycler.setAdapter(adapter);
        process.setPrecessChangeListener(new LoadLayout.onPrecessChangeListener() {
            @Override
            public void onLoadProcessChange(View footer, int process) {

            }

            @Override
            public void onRefreshProcessChange(View header, int process) {

            }

            @Override
            public void onLoad(View footer) {
                LoadingProcess process = ((LoadingProcess)footer);
                process.pause();
                process.startAccAnim();
            }

            @Override
            public void onRefresh(View header) {
                LoadingProcess process = ((LoadingProcess)header);
                process.pause();
                process.startAccAnim();
            }
        });
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

    private void testProvider(){
        setContentView(R.layout.mycontent_providertest);
        final TextView textView = (TextView) findViewById(R.id.provider_test_text);
        View.OnClickListener clickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch(v.getId())
                {
                    case R.id.provider_test_save:
                        textView.setText(saveDB(String.valueOf(System.currentTimeMillis())));
                        break;
                    case R.id.provider_test_get:
                        textView.setText(getDB());
                        break;
                }
            }
        };
        findViewById(R.id.provider_test_save).setOnClickListener(clickListener);
        findViewById(R.id.provider_test_get).setOnClickListener(clickListener);
    }

    private String saveDB(String text){
        Uri uri = getContentResolver().insert(AccountBean.URI_BASE,
                new AccountBean(text,text,"impu","offline","belong","A","false").getContentValues());
        return uri.toString();
    }

    private String getDB(){
        Cursor cursor = getContentResolver().query(URI_TABEL1,null,null,null,null);
        int count = cursor.getCount();
        return String.valueOf(count);
    }
}

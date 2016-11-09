package com.views.ui.customview.RecyclerviewDecorAndDrag;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.views.simpleutils.R;
import com.views.simpleutils.RecyclerBinding;

public class MainActivity extends AppCompatActivity{

    RecyclerBinding binding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.recycle_test);
        initialView();
    }

    private void initialView(){
        binding.recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        TestAdapter adapter = new TestAdapter(this);
        binding.recycler.setAdapter(adapter);
//        binding.recycler.addItemDecoration(new CustomDecor());
        ItemTouchHelper helper =new ItemTouchHelper(new MyRecyclerTouchHelper(adapter));
        helper.attachToRecyclerView(binding.recycler);
    }
}

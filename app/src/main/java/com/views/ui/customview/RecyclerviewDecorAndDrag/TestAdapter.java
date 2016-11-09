package com.views.ui.customview.RecyclerviewDecorAndDrag;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.views.simpleutils.R;

import java.util.ArrayList;

/**
 * Created by qzzhu on 16-10-25.
 *
 */
public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> implements ItemTouchListener{

    private ArrayList<String> datas = new ArrayList<>();
    private ContextThemeWrapper context;

    public TestAdapter(ContextThemeWrapper context){
        for(int i = 0;i<30;i++)
        {
            datas.add(String.valueOf(i));
        }
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*********************            此处必须这样写                     ****************************/
        View view = LayoutInflater.from(this.context).inflate(R.layout.recycle_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setText(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Log.d("onitem move","form:"+fromPosition+"\t to:"+toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        Log.d("onitem dismiss","position:"+position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView view = null;
        public ViewHolder(View itemView) {
            super(itemView);
            view = (TextView) itemView.findViewById(R.id.recycle_item_text);
        }

        public void setText(String args){
//            ViewGroup.LayoutParams params = view.getLayoutParams();
//            params.width = ViewGroup.LayoutParams.MATCH_PARENT;//720;//
//            view.setLayoutParams(params);
            view.setText(args);
        }
    }
}

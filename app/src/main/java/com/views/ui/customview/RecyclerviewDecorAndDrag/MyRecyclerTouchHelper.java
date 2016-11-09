package com.views.ui.customview.RecyclerviewDecorAndDrag;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by qzzhu on 16-11-7.
 * provide item long press to move effect
 */
public class MyRecyclerTouchHelper extends ItemTouchHelper.Callback {

    //声明ItemTouchHelper，并绑定到待管理的RecyclerView上
//    private void useage(){
//        ItemTouchHelper.Callback helperCallback = new MyItemTouchHelperCallback(adapter);
//        helper = new ItemTouchHelper(helperCallback);
//        helper.attachToRecyclerView(recyclerView);
//    }

    private ItemTouchListener listener = null;

    public MyRecyclerTouchHelper(ItemTouchListener listener) {
        this.listener = listener;
    }

    /**
     * 支持长按进入拖动
     * @return default true
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;//super.isLongPressDragEnabled();
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;//super.isItemViewSwipeEnabled();
    }

    /**
     * 指定拖动和滑动支持的方向
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    //List部分功能
    //        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;//拖动支持向下和向上
    //        int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END;//滑动支持向左和向右
        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        //第二个参数表示是否可以滑动删除
        return makeMovementFlags(dragFlag,swipeFlag);
    }

    /**
     * 在每次View Holder的状态变成拖拽 (ACTION_STATE_DRAG) 或者 滑动 (ACTION_STATE_SWIPE)的时候被调用。
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 在一个view被拖拽然后被放开的时候被调用，
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        listener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onItemDismiss(viewHolder.getAdapterPosition());
    }

}

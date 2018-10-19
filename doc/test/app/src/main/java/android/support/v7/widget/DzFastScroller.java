package android.support.v7.widget;

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.dzbook.AppConst;
import com.dzbook.utils.DimensionPixelUtil;

/**
 * Class responsible to animate and provide a fast scroller.
 *
 * @author miaoed
 */
@VisibleForTesting
public class DzFastScroller extends FastScroller {
    /**
     * 绑定RecyclerView对象
     */
    private RecyclerView mRecyclerView;
    /**
     * RecyclerView控件宽度
     */
    private int mRecyclerViewWidth = 0;
    /**
     * RecyclerView控件高度
     */
    private int mRecyclerViewHeight = 0;
    /**
     * Scrollbar展示的最小范围
     */
    private int mScrollbarMinimumRange = 0;

    /**
     * 构造
     *
     * @param recyclerView            recyclerView
     * @param verticalThumbDrawable   verticalThumbDrawable
     * @param verticalTrackDrawable   verticalTrackDrawable
     * @param horizontalThumbDrawable horizontalThumbDrawable
     * @param horizontalTrackDrawable horizontalTrackDrawable
     * @param defaultWidth            defaultWidth
     * @param scrollbarMinimumRange   scrollbarMinimumRange
     * @param margin                  margin
     */
    public DzFastScroller(RecyclerView recyclerView,
                          StateListDrawable verticalThumbDrawable, Drawable verticalTrackDrawable,
                          StateListDrawable horizontalThumbDrawable, Drawable horizontalTrackDrawable,
                          int defaultWidth, int scrollbarMinimumRange, int margin) {
        super(recyclerView,
                verticalThumbDrawable, verticalTrackDrawable,
                horizontalThumbDrawable, horizontalTrackDrawable,
                defaultWidth, scrollbarMinimumRange, margin);
        mScrollbarMinimumRange = scrollbarMinimumRange;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        super.attachToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        if (mRecyclerViewWidth != mRecyclerView.getWidth() || mRecyclerViewHeight != mRecyclerView.getHeight()) {
            mRecyclerViewWidth = mRecyclerView.getWidth();
            mRecyclerViewHeight = mRecyclerView.getHeight();
        }
    }


    /**
     * Notify the scroller of external change of the scroll, e.g. through dragging or flinging on
     * the view itself.
     *
     * @param offsetX The new scroll X offset.
     * @param offsetY The new scroll Y offset.
     */
    @Override
    void updateScrollPosition(int offsetX, int offsetY) {
        super.updateScrollPosition(offsetX, offsetY);

        int verticalContentLength = mRecyclerView.computeVerticalScrollRange();
        int verticalVisibleLength = mRecyclerViewHeight;
        boolean mNeedVerticalScrollbar = verticalContentLength - verticalVisibleLength > 0 && mRecyclerViewHeight >= mScrollbarMinimumRange;

        int horizontalContentLength = mRecyclerView.computeHorizontalScrollRange();
        int horizontalVisibleLength = mRecyclerViewWidth;
        boolean mNeedHorizontalScrollbar = horizontalContentLength - horizontalVisibleLength > 0 && mRecyclerViewWidth >= mScrollbarMinimumRange;

        if (!mNeedVerticalScrollbar && !mNeedHorizontalScrollbar) {
            return;
        }

        //修改部分，重新计算滚动条中点的的y坐标
        if (mNeedVerticalScrollbar) {
            mVerticalThumbHeight = DimensionPixelUtil.dip2px(AppConst.getApp(), 80);
            mVerticalThumbCenterY = ((verticalVisibleLength - mVerticalThumbHeight) * offsetY) / (verticalContentLength - verticalVisibleLength) + mVerticalThumbHeight / 2;
        }
    }
}

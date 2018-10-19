/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.dzbook.view.photoview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.widget.ImageView;


/**
 * A zoomable {@link ImageView}. See {@link PhotoViewAttacher} for most of the details on how the zooming
 * is accomplished
 */
public class PhotoView extends AppCompatImageView {

    private PhotoViewAttacher attacher;
    private ScaleType pendingScaleType;

    /**
     * 构造
     *
     * @param context context
     */
    public PhotoView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attr    attr
     */
    public PhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    /**
     * 构造
     *
     * @param context  context
     * @param attr     attr
     * @param defStyle defStyle
     */
    public PhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init();
    }

    //    @TargetApi(21)
    //    public PhotoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    //        super(context, attrs, defStyleAttr, defStyleRes);
    //        register();
    //    }

    private void init() {
        attacher = new PhotoViewAttacher(this);
        //We always pose as a Matrix scale type, though we can change to another scale type
        //via the attacher
        super.setScaleType(ScaleType.MATRIX);
        //apply the previously applied scale type
        if (pendingScaleType != null) {
            setScaleType(pendingScaleType);
            pendingScaleType = null;
        }
    }

    /**
     * Get the current {@link PhotoViewAttacher} for this view. Be wary of holding on to references
     * to this attacher, as it has a reference to this view, which, if a reference is held in the
     * wrong place, can cause memory leaks.
     *
     * @return the attacher.
     */
    public PhotoViewAttacher getAttacher() {
        return attacher;
    }

    @Override
    public ScaleType getScaleType() {
        return attacher.getScaleType();
    }

    @Override
    public Matrix getImageMatrix() {
        return attacher.getImageMatrix();
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        attacher.setOnLongClickListener(l);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        attacher.setOnClickListener(l);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (attacher == null) {
            pendingScaleType = scaleType;
        } else {
            attacher.setScaleType(scaleType);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        // setImageBitmap calls through to this method
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        if (changed) {
            attacher.update();
        }
        return changed;
    }

    /**
     * setRotationTo
     * @param rotationDegree rotationDegree
     */
    public void setRotationTo(float rotationDegree) {
        attacher.setRotationTo(rotationDegree);
    }

    /**
     * setRotationBy
     * @param rotationDegree rotationDegree
     */
    public void setRotationBy(float rotationDegree) {
        attacher.setRotationBy(rotationDegree);
    }

    @Deprecated
    public boolean isZoomEnabled() {
        return attacher.isZoomEnabled();
    }

    public boolean isZoomable() {
        return attacher.isZoomable();
    }

    /**
     * setZoomable
     * @param zoomable zoomable
     */
    public void setZoomable(boolean zoomable) {
        attacher.setZoomable(zoomable);
    }

    public RectF getDisplayRect() {
        return attacher.getDisplayRect();
    }

    /**
     * getDisplayMatrix
     * @param matrix matrix
     */
    public void getDisplayMatrix(Matrix matrix) {
        attacher.getDisplayMatrix(matrix);
    }

    /**
     * setDisplayMatrix
     * @param finalRectangle finalRectangle
     * @return boolean
     */
    public boolean setDisplayMatrix(Matrix finalRectangle) {
        return attacher.setDisplayMatrix(finalRectangle);
    }

    /**
     * getSuppMatrix
     * @param matrix matrix
     */
    public void getSuppMatrix(Matrix matrix) {
        attacher.getSuppMatrix(matrix);
    }

    /**
     * setSuppMatrix
     * @param matrix matrix
     * @return boolean
     */
    public boolean setSuppMatrix(Matrix matrix) {
        return attacher.setDisplayMatrix(matrix);
    }

    public float getMinimumScale() {
        return attacher.getMinimumScale();
    }

    //    public float getMediumScale() {
    //        return attacher.getMediumScale();
    //    }

    public float getMaximumScale() {
        return attacher.getMaximumScale();
    }

    public float getScale() {
        return attacher.getScale();
    }

    /**
     * setAllowParentInterceptOnEdge
     * @param allow allow
     */
    public void setAllowParentInterceptOnEdge(boolean allow) {
        attacher.setAllowParentInterceptOnEdge(allow);
    }

    /**
     * setMinimumScale
     * @param minimumScale minimumScale
     */
    public void setMinimumScale(float minimumScale) {
        attacher.setMinimumScale(minimumScale);
    }

    //    public void setMediumScale(float mediumScale) {
    //        attacher.setMediumScale(mediumScale);
    //    }

    /**
     * setMaximumScale
     * @param maximumScale maximumScale
     */
    public void setMaximumScale(float maximumScale) {
        attacher.setMaximumScale(maximumScale);
    }

    /**
     * setScaleLevels
     * @param minimumScale minimumScale
     * @param mediumScale mediumScale
     * @param maximumScale maximumScale
     */
    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        attacher.setScaleLevels(minimumScale, mediumScale, maximumScale);
    }

    /**
     * setOnMatrixChangeListener
     * @param listener listener
     */
    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        attacher.setOnMatrixChangeListener(listener);
    }

    /**
     *setOnPhotoTapListener
     * @param listener listener
     */
    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        attacher.setOnPhotoTapListener(listener);
    }

    /**
     * setOnOutsidePhotoTapListener
     * @param listener listener
     */
    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener listener) {
        attacher.setOnOutsidePhotoTapListener(listener);
    }

    /**
     * setOnViewTapListener
     * @param listener listener
     */
    public void setOnViewTapListener(OnViewTapListener listener) {
        attacher.setOnViewTapListener(listener);
    }

    /**
     * setOnViewDragListener
     * @param listener listener
     */
    public void setOnViewDragListener(OnViewDragListener listener) {
        attacher.setOnViewDragListener(listener);
    }

    /**
     * 设置比例
     *
     * @param scale scale
     */
    public void setScale(float scale) {
        attacher.setScale(scale);
    }

    /**
     * 设置比例
     *
     * @param scale   scale
     * @param animate animate
     */
    public void setScale(float scale, boolean animate) {
        attacher.setScale(scale, animate);
    }

    /**
     * 设置比例
     *
     * @param scale   scale
     * @param focalY  focalY
     * @param animate animate
     * @param focalX  focalX
     */
    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        attacher.setScale(scale, focalX, focalY, animate);
    }

    /**
     * setZoomTransitionDuration
     * @param milliseconds milliseconds
     */
    public void setZoomTransitionDuration(int milliseconds) {
        attacher.setZoomTransitionDuration(milliseconds);
    }

    /**
     * setOnDoubleTapListener
     * @param onDoubleTapListener onDoubleTapListener
     */
    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
        attacher.setOnDoubleTapListener(onDoubleTapListener);
    }

    /**
     * setOnScaleChangeListener
     * @param onScaleChangedListener onScaleChangedListener
     */
    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangedListener) {
        attacher.setOnScaleChangeListener(onScaleChangedListener);
    }

    /**
     * setOnSingleFlingListener
     * @param onSingleFlingListener onSingleFlingListener
     */
    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        attacher.setOnSingleFlingListener(onSingleFlingListener);
    }
}

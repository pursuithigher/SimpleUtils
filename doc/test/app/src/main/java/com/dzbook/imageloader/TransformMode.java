package com.dzbook.imageloader;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.Transformation;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * TransformMode
 *
 * @author wxliao on 17/5/17.
 */

public interface TransformMode {

    /**
     * getTransform
     * @param context context
     * @return Transformation
     */
    Transformation<Bitmap> getTransform(Context context);

    /**
     * CircleMode
     */
    class CircleMode implements TransformMode {

        @Override
        public Transformation<Bitmap> getTransform(Context context) {
            return new CropCircleTransformation(context);
        }
    }

    /**
     * RoundedCornersMode
     */
    class RoundedCornersMode implements TransformMode {
        private int radius;
        private int margin;

        public RoundedCornersMode(int radius) {
            this(radius, 0);
        }

        public RoundedCornersMode(int radius, int margin) {
            this.radius = radius;
            this.margin = margin;
        }


        @Override
        public Transformation<Bitmap> getTransform(Context context) {
            return new RoundedCornersTransformation(context, radius, margin);
        }
    }

}

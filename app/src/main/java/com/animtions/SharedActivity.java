package com.animtions;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.squareup.leakcanary.LeakCanary;
import com.views.simpleutils.R;

/**
 * Created by qzzhu on 17-5-17.
 *
 */
public class SharedActivity extends AppCompatActivity {

    ImageView image2;
    boolean show = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stateanim);
        image2 = (ImageView) findViewById(R.id.image2);
    }

    public void startReveal(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (show) {
                hide(image2,1500);
            } else {
                show(image2,1500);
            }
            show = !show;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void show(View myView,long duration){
    // previously invisible view

    // get the center for the clipping circle
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

    // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

    // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
        anim.setDuration(duration);
        anim.setInterpolator(new LinearInterpolator());

    // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void hide(View view, long duration){
        // previously visible view
        final View myView = view;

        // get the center for the clipping circle
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        anim.setDuration(duration);
        anim.setInterpolator(new LinearInterpolator());
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }
}

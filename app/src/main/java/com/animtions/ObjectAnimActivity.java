package com.animtions;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.views.simpleutils.R;

/**
 * Created by qzzhu on 17-5-17.
 */

public class ObjectAnimActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView image;
    View sharedButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        // inside your activity (if you did not enable transitions in your theme)
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//
//        // set an exit transition
//        getWindow().setExitTransition(new Explode());

        setContentView(R.layout.activity_main);
//        setAnimtionTest();
    }

    private void setAnimtionTest(){
        setContentView(R.layout.activity_objectanim);
        findViewById(R.id.animatorset).setOnClickListener(this);
        findViewById(R.id.animatorset1).setOnClickListener(this);
        findViewById(R.id.animatorset2).setOnClickListener(this);
        findViewById(R.id.animatorset4).setOnClickListener(this);
        sharedButton = findViewById(R.id.animatorset3);
        sharedButton.setOnClickListener(this);
        image = (ImageView) findViewById(R.id.images);
        image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                loadAnimatorSelector();
                Toast.makeText(ObjectAnimActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.animatorset:
                setAnimator();
                break;
            case R.id.animatorset1:
                setweenAnim();
                break;
            case R.id.animatorset2:
                loadRevealActivity();
                break;
            case R.id.animatorset3:
                loadRevealActivityTransition(image);
                break;
            case R.id.animatorset4:
                loadAnimatorStateChange();
                break;
        }
    }

    /**
     *  load the objectAnimator
     */
    private void setAnimator(){
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                R.animator.animatorsets);
        set.setTarget(image);
        set.setInterpolator(new LinearInterpolator());
        set.start();
//        ObjectAnimator.of***
//        ValueAnimator.of***
    }

    /**
     * load the tween Animation
     */
    private void setweenAnim(){
        Animation hyperspaceJump = AnimationUtils.loadAnimation(this, R.anim.tweenanims);
        image.startAnimation(hyperspaceJump);
//        ScaleAnimation
//        TranslateAnimation
//        RotateAnimation
//        AlphaAnimation
    }

    private void loadRevealActivity(){
        Intent i = new Intent(this,SharedActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    /*
    explode - Moves views in or out from the center of the scene.
    slide - Moves views in or out from one of the edges of the scene.
    fade - Adds or removes a view from the scene by changing its opacity.
    */

    /**
     * create shared elements activity
     * To specify transitions in your code, call these methods with a Transition object:
         Window.setEnterTransition()
         Window.setExitTransition()
         Window.setSharedElementEnterTransition()
         Window.setSharedElementExitTransition()

     Start an activity with a shared element
     1: To make a screen transition animation between two activities that have a shared element:

     Enable window content transitions in your theme.
     2: getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS) or set Theme;

     Specify a shared elements transition in your style.
     3: both side activity shared elements have the same android:transitionName="robot" property

     Define your transition as an XML resource.
     Assign a common name to the shared elements in both layouts with the android:transitionName attribute.
     Use the ActivityOptions.makeSceneTransitionAnimation() method.
     */
    private void loadRevealActivityTransition(View sharedElements){
        Intent i = new Intent(this,SharedActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = //ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                    //ActivityOptions.makeSceneTransitionAnimation(this, sharedElements, "robot"); //single shared view
                    ActivityOptions.makeSceneTransitionAnimation(this,Pair.create(sharedElements, "robot"),
                            Pair.create(sharedButton, "button1"));//muti shared view
            startActivity(i,
                    options.toBundle());
        }else{
            startActivity(i);
        }
    }

    /**
     * see drawable-v21 animator_state_change.xml
     *
     * When your theme extends the material theme, buttons have a Z animation by default.
     * To avoid this behavior in your buttons, set the android:stateListAnimator attribute to @null.
     *
     * To assign a state list animator to a view in your code,
     * use the AnimatorInflater.loadStateListAnimator() method,
     * and assign the animator to your view with the View.setStateListAnimator() method
     */
    private void loadAnimatorStateChange(){
        //see drawable-v21 animator_state_change.xml
    }

    /**
     * 状态改变的动画集
     * 见@drawable/animated_selector.xml
     * 从D到F再到P
     */
    private void loadAnimatorSelector(){
//        android:background="@drawable/animated_selector"
//        no android:src property
    }
}

package com.views.ui.coordinate.behavior;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.views.simpleutils.R;

/**
 * Created by qzzhu on 17-5-23.
 * see CoordinateActivity.coorAndFloatingButton
 */
public class BottomSheetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static void setBottomSheet(View coordinatorLayout){
//        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(coordinatorLayout.findViewById(R.id.bottom_sheet));

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    //text1.setText("Collapse Me!");
                } else {
                    //text1.setText("Expand Me!");
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("onslide",String.valueOf(slideOffset));
            }
        });
    }

    /**
     * create a dialog form bottom
     * @param sheetlayout
     * @param context
     * @return
     */
    public static BottomSheetDialog getBottomSheet(@LayoutRes int sheetlayout, ContextThemeWrapper context){
        BottomSheetDialog bsDialog = new BottomSheetDialog(context);
        bsDialog.setContentView(sheetlayout);
        return bsDialog;
//        bsDialog.show();
    }
}

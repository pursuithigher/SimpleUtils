package com.views.ui.palette;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.View;

import com.views.simpleutils.R;

/**
 * Created by Administrator on 2017/5/18 0018.
 * D:\Developer\sdk\extras\android\support\v7\palette\libs
 */
public class PaletteActivity extends AppCompatActivity implements View.OnClickListener {
    private Bitmap imageView;
    View container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);

        findViewById(R.id.darkmute).setOnClickListener(this);
        findViewById(R.id.darkvibrant).setOnClickListener(this);
        findViewById(R.id.lingmute).setOnClickListener(this);
        findViewById(R.id.lightvibrant).setOnClickListener(this);
        findViewById(R.id.mute).setOnClickListener(this);
        findViewById(R.id.vibrant).setOnClickListener(this);
        imageView = BitmapFactory.decodeResource(getResources(),R.drawable.imga);
        container = findViewById(R.id.container);
    }

    private void getBitmapColor(Bitmap bitmap,final int id){
        //sync
//        Palette palette = Palette.from(bitmap).generate();
//        getColor(palette,id);

        //async
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                getColor(palette,id);
            }
        });
    }

    private void getColor(Palette palette,int id){
        switch (id)
        {
            case R.id.darkmute:
                if(palette.getDarkMutedSwatch() != null)
                    container.setBackgroundColor(palette.getDarkMutedSwatch().getRgb());
                break;
            case R.id.darkvibrant:
                if(palette.getDarkVibrantSwatch() != null)
                    container.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                break;
            case R.id.lingmute:
                if(palette.getLightMutedSwatch() != null)
                    container.setBackgroundColor(palette.getLightMutedSwatch().getRgb());
                break;
            case R.id.lightvibrant:
                if(palette.getLightVibrantSwatch() != null)
                    container.setBackgroundColor(palette.getLightVibrantSwatch().getRgb());
                break;
//            case R.id.dominant:
//                if(palette.getDominantSwatch() != null)
//                    container.setBackgroundColor(palette.getDominantSwatch().getRgb());
//                break;
            case R.id.vibrant:
                if(palette.getVibrantSwatch() != null)
                    container.setBackgroundColor(palette.getVibrantSwatch().getRgb());
                break;
            case R.id.mute:
                if(palette.getMutedSwatch() != null)
                    container.setBackgroundColor(palette.getMutedSwatch().getRgb());
                break;
        }
    }

    @Override
    public void onClick(View v) {
        getBitmapColor(imageView,v.getId());
    }
}

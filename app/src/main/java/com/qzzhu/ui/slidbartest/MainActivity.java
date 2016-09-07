package com.qzzhu.ui.slidbartest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.qzzhu.ui.slidbar.CharacterParser;
import com.qzzhu.ui.slidbar.PinyinComparator;
import com.qzzhu.ui.slidbar.SortModel;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by qzzhu on 16-9-7.
 */

public class MainActivity extends AppCompatActivity{

    public static class ListBean implements SortModel {

        private String sortLetter;

        private String sortMain;

        public void setSortMain(String sortMain) {
            this.sortMain = sortMain;
        }

        @Override
        public String getSortLetter() {
            return sortLetter;
        }

        @Override
        public void setSortLetter() {
            sortLetter = CharacterParser.getInstance().getSelling(sortMain);
        }
    }

    ArrayList<ListBean> datas = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Collections.sort(datas,new PinyinComparator());
    }
}

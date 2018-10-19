/*
 * Copyright (C) 2012 The Android Open Source Project
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

package android.support.v4.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TabHost;

import com.dzbook.lib.utils.ALog;

/**
 * Special TabHost that allows the use of {@link Fragment} objects for
 * its tab content.  When placing this in a view hierarchy, after inflating
 * the hierarchy you must call {@link #setup(Context, FragmentManager, int)}
 * to complete the initialization of the tab host.
 *
 * @author zhenglk
 */
public class DzFragmentTabHost extends FragmentTabHost {

    /**
     * 构造
     *
     * @param context context
     */
    public DzFragmentTabHost(Context context) {
        // Note that we call through to the version that takes an AttributeSet,
        // because the simple Context construct can result in a broken object!
        super(context);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public DzFragmentTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void addTab(@NonNull TabHost.TabSpec tabSpec, @NonNull Class<?> clss, @Nullable Bundle args) {
        try {
            super.addTab(tabSpec, clss, args);
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        try {
            super.onAttachedToWindow();
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }


    @Override
    public void onTabChanged(String tabId) {
        try {
            super.onTabChanged(tabId);
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }
}

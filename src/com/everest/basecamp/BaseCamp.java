/*
 * Copyright (C) 2024 ProjectEverest
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

package com.everest.basecamp;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Surface;
import android.widget.LinearLayout;

import com.android.internal.logging.nano.MetricsProto;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.widget.NestedScrollView;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.everest.basecamp.fragments.*;

import android.content.Intent;

import com.google.android.material.card.MaterialCardView;

public class BaseCamp extends SettingsPreferenceFragment implements View.OnClickListener, View.OnLongClickListener {

    private LinearLayout[] settingCards;
    private MaterialCardView mLockScreenSettingsCard;
    private NestedScrollView scrollView;
    private int scrollY = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.basecamp, container, false);
        scrollView = view.findViewById(R.id.nested_scroll_view);
        settingCards = new LinearLayout[]{
                view.findViewById(R.id.themecard_2)
        };
        for (LinearLayout card : settingCards) {
            card.setOnClickListener(this);
        }
        mLockScreenSettingsCard = view.findViewById(R.id.customization_picker_button);
        mLockScreenSettingsCard.setOnClickListener(this);
        mLockScreenSettingsCard.setOnLongClickListener(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("scrollY", scrollView.getScrollY());
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            scrollY = savedInstanceState.getInt("scrollY", 0);
            scrollView.post(() -> scrollView.scrollTo(0, scrollY));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Fragment fragment = null;
        String title = null;
        if (id == R.id.themecard_2) {
            fragment = new QuickSettings();
            title = getString(R.string.quicksettings_title);
        }
        if (fragment != null && title != null) {
            replaceFragment(fragment, title);
        }
    }

     private void replaceFragment(Fragment fragment, String title) {
    FragmentManager fragmentManager = getFragmentManager();
    if (fragmentManager != null) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
            R.anim.slide_in,  // enter
            R.anim.fade_out,  // exit
            R.anim.fade_in,   // popEnter
            R.anim.slide_out  // popExit
        );
        transaction.replace(this.getId(), fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        getActivity().setTitle(title != null ? title : "Everest Basecamp");
    }
}

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.customization_picker_button) {
            launchWallpaperPickerActivity();
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.EVEREST;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Everest Basecamp");
    }

    private void launchWallpaperPickerActivity() {
        Intent intent = new Intent();
        intent.setClassName("com.google.android.apps.wallpaper", "com.google.android.apps.wallpaper.picker.CategoryPickerActivity");
        startActivity(intent);
    }

    public static void lockCurrentOrientation(Activity activity) {
        int currentRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int orientation = activity.getResources().getConfiguration().orientation;
        int frozenRotation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        switch (currentRotation) {
            case Surface.ROTATION_0:
                frozenRotation = orientation == Configuration.ORIENTATION_LANDSCAPE
                        ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_90:
                frozenRotation = orientation == Configuration.ORIENTATION_PORTRAIT
                        ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_180:
                frozenRotation = orientation == Configuration.ORIENTATION_LANDSCAPE
                        ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            case Surface.ROTATION_270:
                frozenRotation = orientation == Configuration.ORIENTATION_PORTRAIT
                        ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
        }
        activity.setRequestedOrientation(frozenRotation);
    }
}

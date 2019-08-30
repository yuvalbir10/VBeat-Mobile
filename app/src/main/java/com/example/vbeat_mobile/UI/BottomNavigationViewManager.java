package com.example.vbeat_mobile.UI;

import android.app.Activity;
import android.view.View;

import com.example.vbeat_mobile.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationViewManager {
    public static void enable(final Activity a, final boolean enable) {
        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BottomNavigationView bnv = a.findViewById(R.id.bottom_nav_bar);
                if(enable){
                    bnv.setVisibility(View.VISIBLE);
                } else {
                    bnv.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}

package com.example.vbeat_mobile.utility;

import android.app.Activity;
import android.widget.Toast;

public class UiUtils {
    public static void showMessage(final Activity a, final String message){
        safeRunOnUiThread(a, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(a.getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void safeRunOnUiThread(Activity a, Runnable r){
        if(a != null) {
            a.runOnUiThread(r);
        }
    }
}

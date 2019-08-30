package com.example.vbeat_mobile.viewmodel;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.UI.BottomNavigationViewManager;

import java.util.Objects;

public class RedirectionUtils {
    private static final String TAG = "RedirectionUtils";

    public static void redirectAndEnableNavBar(Activity a, View currentView, int id){
        if(currentView == null) {
            Log.e(TAG, "currentView == null");
            throw new IllegalStateException(TAG + " currentView == null");
        }

        NavController navController = null;

        // find navigation controller
        navController = Navigation.findNavController(currentView);

        // enable nav bar
        BottomNavigationViewManager.enable(Objects.requireNonNull(a), true);

        // navigate to page
        navController.navigate(id);
    }
}

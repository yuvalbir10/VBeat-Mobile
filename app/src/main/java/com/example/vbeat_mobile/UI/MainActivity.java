package com.example.vbeat_mobile.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.cache.AppLocalDB;
import com.example.vbeat_mobile.backend.cache.FirebaseImageCache;
import com.example.vbeat_mobile.backend.user.FirebaseUserManager;
import com.example.vbeat_mobile.backend.user.UserManager;
import com.example.vbeat_mobile.utility.ImageViewUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initialize();

        FirebaseUserManager userManager = FirebaseUserManager.getInstance();
        if(userManager.isUserLoggedIn()) {
            navController.navigate(R.id.action_initialFragment_to_feedFragment);
        }
    }

    private void initialize() {
        initializeNavigation();
        initializeSingeltons();
    }

    private void initializeNavigation(){
        navController = Navigation.findNavController(this, R.id.nav_fragment);
        initializeBottomNavigationBar();
    }

    private void initializeBottomNavigationBar(){
        // as shown in presentation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    private void initializeSingeltons(){
        Application appContext = getApplication();

        // initialize all singletons that need application context
        FirebaseImageCache.setApplicationContext(appContext);
        AppLocalDB.initialize(appContext);
        ImageViewUtil.initialize(appContext);
    }
}

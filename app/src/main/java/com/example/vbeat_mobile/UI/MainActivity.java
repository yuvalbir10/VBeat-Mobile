package com.example.vbeat_mobile.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

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

public class MainActivity extends AppCompatActivity {
    private UserManager userManager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initialize();

        userManager = FirebaseUserManager.getInstance();
        if(userManager.isUserLoggedIn()) {
            Navigation.findNavController(this, R.id.nav_fragment).navigate(R.id.action_initialFragment_to_feedFragment);
        }
    }

    private void initialize(){

        Application appContext = getApplication();

        // initialize all singletons that need application context
        FirebaseImageCache.setApplicationContext(appContext);
        AppLocalDB.initialize(appContext);
        ImageViewUtil.initialize(appContext);
    }
}

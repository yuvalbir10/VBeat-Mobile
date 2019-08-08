package com.example.vbeat_mobile.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.user.FirebaseUserManager;
import com.example.vbeat_mobile.backend.user.UserManager;

public class MainActivity extends AppCompatActivity {
    private UserManager userManager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userManager = new FirebaseUserManager();
        if(userManager.isUserLoggedIn()) {
            Navigation.findNavController(this, R.id.nav_fragment)
                    .navigate(R.id.action_initialFragment_to_uploadPostFragment);
        }
    }
}

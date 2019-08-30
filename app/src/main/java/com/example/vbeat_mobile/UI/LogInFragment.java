package com.example.vbeat_mobile.UI;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.user.FirebaseUserManager;
import com.example.vbeat_mobile.backend.user.UserLoginFailedException;
import com.example.vbeat_mobile.backend.user.UserManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;
import com.example.vbeat_mobile.utility.UiUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogInFragment extends Fragment {
    private static final String TAG  = "LogInFragment";

    private Button loginButton = null;
    private ProgressBar prBar = null;
    private UserManager userManager;

    public LogInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v =  inflater.inflate(R.layout.fragment_log_in, container, false);
        userManager = FirebaseUserManager.getInstance();

        loginButton = v.findViewById(R.id.log_in_button);
        prBar = v.findViewById(R.id.indeterminateBar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginInBackground();
            }
        });
        return v;
    }



    private void loginInBackground() {
        //get the email and password strings from UI
        final View v = getView();

        if(v == null) {
            throw new IllegalStateException("no view available can't login background");
        }

        EditText usernameTB = v.findViewById(R.id.username_textbox);
        EditText passwordTB = v.findViewById(R.id.password_textbox);
        final String username = usernameTB.getText().toString();
        final String password = passwordTB.getText().toString();

        // show progress bar & disable login button
        loginButton.setEnabled(false);
        prBar.setVisibility(View.VISIBLE);

        // create account in background so
        // ui won't be stuck!
        new Thread(new Runnable() {
            @Override
            public void run() {
                Activity a = LogInFragment.this.getActivity();
                try {
                    userManager.login(username, password);
                    UiUtils.safeRunOnUiThread(a,new Runnable() {
                        @Override
                        public void run() {
                            handleSuccessfulLogin();
                        }
                    });
                } catch(final UserLoginFailedException e) {
                    //error if login failed
                    final TextView errorTextView = v.findViewById(R.id.error_textView);


                    UiUtils.safeRunOnUiThread(a, new Runnable() {
                        @Override
                        public void run() {
                            errorTextView.setText(e.getMessage());
                            errorTextView.setVisibility(View.VISIBLE);
                        }
                    });

                } finally {

                    // hide progress bar & show login button
                    UiUtils.safeRunOnUiThread(a, new Runnable() {
                        @Override
                        public void run() {
                            loginButton.setEnabled(true);
                            prBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        }).start();
    }


    private void handleSuccessfulLogin(){
        View currentView = getView();
        if(currentView == null) {
            Log.e(TAG, "currentView == null");
            throw new IllegalStateException(TAG + " currentView == null");
        }

        NavController navController = null;
        navController = Navigation.findNavController(currentView);

        BottomNavigationViewManager.enable(Objects.requireNonNull(getActivity()), true);

        // cleaning up the stack up to now
        // so back will exit the app
        navController.navigate(R.id.action_logInFragment_to_feedFragment);
    }

}

package com.example.vbeat_mobile.UI;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.example.vbeat_mobile.backend.user.UserManager;
import com.example.vbeat_mobile.backend.user.UserRegistrationFailedException;
import com.example.vbeat_mobile.utility.UiUtils;
import com.example.vbeat_mobile.viewmodel.RedirectionUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    private Button signupButton = null;
    private ProgressBar prBar = null;
    private UserManager userManager;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v =  inflater.inflate(R.layout.fragment_sign_up, container, false);

        userManager = FirebaseUserManager.getInstance();

        signupButton = v.findViewById(R.id.sign_up_button);
        prBar = v.findViewById(R.id.indeterminateBar);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpInBackground();
            }
        });

        return v;
    }

    private void signUpInBackground() {
        //get the email and password strings from UI
        final View v = getView();

        if(v == null) {
            throw new IllegalStateException("no view available can't sign up background");
        }

        EditText usernameTB = v.findViewById(R.id.username_textbox);
        EditText passwordTB = v.findViewById(R.id.password_textbox);
        final String username = usernameTB.getText().toString();
        final String password = passwordTB.getText().toString();

        disableLoginUi(false, View.VISIBLE);

        // create account in background so
        // ui won't be stuck!
        new Thread(new Runnable() {
            @Override
            public void run() {
                Activity a = SignUpFragment.this.getActivity();
                try {
                    userManager.createAccount(username, password);
                    handleSuccessfulSignUp(a);
                } catch(final UserRegistrationFailedException e) {
                    showRegistrationErrorMessage(a, e, v);
                } finally {
                    enableLoginUi(a);
                }
            }
        }).start();
    }

    private void handleSuccessfulSignUp(Activity a) {
        UiUtils.safeRunOnUiThread(a, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SignUpFragment.this.getContext(),
                        "Sign up successful!",
                        Toast.LENGTH_SHORT).show();

                redirectToFeed();
            }
        });
    }

    private void disableLoginUi(boolean b, int visible) {
        // show progress bar & disable sign up button
        signupButton.setEnabled(b);
        prBar.setVisibility(visible);
    }

    private void enableLoginUi(Activity a) {
        // hide progress bar & show sign up button
        UiUtils.safeRunOnUiThread(a, new Runnable() {
            @Override
            public void run() {
                disableLoginUi(true, View.INVISIBLE);
            }
        });
    }

    private void showRegistrationErrorMessage(Activity a, final UserRegistrationFailedException e, View v) {
        //error if sign up failed
        final TextView errorTextView = v.findViewById(R.id.error_textView);

        UiUtils.safeRunOnUiThread(a, new Runnable() {
            @Override
            public void run() {
                errorTextView.setText(e.getMessage());
                errorTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void redirectToFeed(){
        RedirectionUtils.redirectAndEnableNavBar(
                getActivity(),
                getView(),
                R.id.action_signUpFragment_to_feedFragment);
    }

}

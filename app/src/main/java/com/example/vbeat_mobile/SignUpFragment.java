package com.example.vbeat_mobile;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vbeat_mobile.backend.FirebaseUserManager;
import com.example.vbeat_mobile.backend.UserManager;
import com.example.vbeat_mobile.backend.user.UserRegistrationFailedException;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {


    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v =  inflater.inflate(R.layout.fragment_sign_up, container, false);

        final UserManager userManager = new FirebaseUserManager();

        v.findViewById(R.id.sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the email and password strings from UI
                EditText usernameTB = v.findViewById(R.id.username_textbox);
                EditText passwordTB = v.findViewById(R.id.password_textbox);
                final String username = usernameTB.getText().toString();
                final String password = passwordTB.getText().toString();

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            userManager.createAccount(username, password);
                        } catch(UserRegistrationFailedException e) {
                            //error if sign up failed
                            TextView errorTextView = v.findViewById(R.id.error_textView);
                            errorTextView.setText(e.getMessage());
                            errorTextView.setVisibility(View.VISIBLE);
                        } finally {

                        }
                    }
                });
            }
        });

        return v;
    }

}

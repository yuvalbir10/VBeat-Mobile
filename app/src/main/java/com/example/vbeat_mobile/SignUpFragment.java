package com.example.vbeat_mobile;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vbeat_mobile.backend.FirebaseUserManager;


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
        v.findViewById(R.id.sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the email and password strings from UI
                EditText usernameTB = v.findViewById(R.id.username_textbox);
                EditText passwordTB = v.findViewById(R.id.password_textbox);
                String username = usernameTB.getText().toString();
                String password = passwordTB.getText().toString();

                //try to sign up using firebase user manager
                FirebaseUserManager userManager = new FirebaseUserManager();
                if(!userManager.createAccount(username, password)){
                    //error if sign up failed
                    TextView errorTextView = v.findViewById(R.id.error_textView);
                    errorTextView.setText("Sign Up Failed");
                    errorTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        return v;
    }

}

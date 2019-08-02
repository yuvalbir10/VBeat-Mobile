package com.example.vbeat_mobile;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class InitialFragment extends Fragment {


    public InitialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View v = inflater.inflate(R.layout.fragment_initial, container, false);
        Button loginButton = v.findViewById(R.id.log_in_button);
        loginButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_initialFragment_to_logInFragment));

        Button signUpButton = v.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_initialFragment_to_signUpFragment));


        return v;
    }

}

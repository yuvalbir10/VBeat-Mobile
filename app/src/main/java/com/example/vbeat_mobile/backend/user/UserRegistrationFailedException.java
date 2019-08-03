package com.example.vbeat_mobile.backend.user;

public class UserRegistrationFailedException extends Exception {
    public UserRegistrationFailedException(){
        super();
    }

    public UserRegistrationFailedException(String msg){
        super(msg);
    }
}

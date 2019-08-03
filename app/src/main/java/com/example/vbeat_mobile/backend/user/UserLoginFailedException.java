package com.example.vbeat_mobile.backend.user;

public class UserLoginFailedException extends Exception {

    public UserLoginFailedException(){
        super();
    }

    public UserLoginFailedException(String msg){
        super(msg);
    }
}

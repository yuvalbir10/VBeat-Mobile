package com.example.vbeat_mobile.backend.user;

public class UserLogoutFailedException extends Exception {
    public UserLogoutFailedException(){
        super();
    }

    public UserLogoutFailedException(String msg){
        super(msg);
    }
}

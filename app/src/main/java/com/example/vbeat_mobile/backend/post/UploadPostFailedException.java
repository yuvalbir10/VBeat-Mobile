package com.example.vbeat_mobile.backend.post;

public class UploadPostFailedException extends Exception {
    public UploadPostFailedException(){
        super();
    }

    public UploadPostFailedException(String msg){
        super(msg);
    }
}

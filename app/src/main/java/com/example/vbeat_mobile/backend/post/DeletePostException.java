package com.example.vbeat_mobile.backend.post;

public class DeletePostException extends Exception {
    public DeletePostException(){
        super();
    }

    public DeletePostException(String msg){
        super(msg);
    }
}

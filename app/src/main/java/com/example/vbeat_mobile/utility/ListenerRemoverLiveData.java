package com.example.vbeat_mobile.utility;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.ListenerRegistration;

public class ListenerRemoverLiveData<T> extends MutableLiveData<T> {
    private FirebaseListenerCreator<T> listenerCreator;
    private ListenerRegistration listenerRegistration;

    public ListenerRemoverLiveData(FirebaseListenerCreator<T> listenerCreator){
        this.listenerCreator = listenerCreator;
        listenerRegistration = null;
    }

    @Override
    protected void onActive() {
        super.onActive();
        listenerRegistration = listenerCreator.createListener(this);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        listenerRegistration.remove();
    }

    public interface FirebaseListenerCreator<T> {
        ListenerRegistration createListener(MutableLiveData<T> liveData);
    }
}

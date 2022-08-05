package com.kirandroid.bhojanforall.firebase;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class FireBaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

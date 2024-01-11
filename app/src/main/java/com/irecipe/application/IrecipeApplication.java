package com.irecipe.application;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;

import io.paperdb.Paper;

public class IrecipeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Paper.init(this);
        FirebaseApp.initializeApp(this);
    }
}

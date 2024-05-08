package com.khafizov.ferrum.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

public class SharedViewModelApplication extends
        Application implements ViewModelStoreOwner {
private ViewModelStore viewModelStore;
@Override
public void onCreate() {
super.onCreate();
viewModelStore = new ViewModelStore();}
@NonNull
@Override
public ViewModelStore getViewModelStore() {
return viewModelStore;
}
public ViewModelProvider getViewModelProvider() {
        return new ViewModelProvider(this);
    }}

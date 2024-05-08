package com.khafizov.ferrum.models;
import androidx.lifecycle.ViewModel;


public class SharedViewModel extends ViewModel {
    private boolean isNewServiceAdded;
    public boolean isNewServiceAdded() {
        return isNewServiceAdded;}
    public void setNewServiceAdded(boolean newServiceAdded) {
        isNewServiceAdded = newServiceAdded;}}



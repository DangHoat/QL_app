package com.vn.quanly.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData <Integer> numberPage = new MutableLiveData<>();
    public MainViewModel(@NonNull Application application) {
        super(application);
        numberPage.setValue(0);
    }
    public MutableLiveData<Integer> getNumberPage() {
        return numberPage;
    }

    public void setNumberPage(int score) {
        numberPage.setValue(score);
    }
}

package com.vn.quanly.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vn.quanly.model.BillOfSale;
import com.vn.quanly.model.PayBook;

import java.util.ArrayList;
import java.util.List;

public class PaybookViewModel extends AndroidViewModel {
    private MutableLiveData <List<PayBook> > payBooks = new MutableLiveData<>();


    public PaybookViewModel(@NonNull Application application) {
        super(application);
        payBooks.setValue(null);
    }
   public MutableLiveData<List<PayBook>> getDataPayBook(){
        return payBooks;
   }
    public void addData(List<PayBook> payBook){
        payBooks.setValue(payBook);
    }

}

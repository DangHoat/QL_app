package com.vn.quanly.adapter.Interface;

import com.vn.quanly.model.BillOfSale;

import java.util.List;

public interface connectCustomerActivity {
    void ShowDialog();
    void ExportFile(List<BillOfSale> billOfSales);
    void UpdateBill(BillOfSale billOfSale,int index);
}

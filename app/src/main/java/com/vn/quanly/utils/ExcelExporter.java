package com.vn.quanly.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.vn.quanly.model.BillOfSale;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelExporter {

    public static void export(Context context, String filename, List<BillOfSale> billList) {

        File sd = Environment.getExternalStorageDirectory();
        String csvFile = filename+".xls";

        File directory = new File(sd.getAbsolutePath());

        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale(Locale.GERMAN.getLanguage(), Locale.GERMAN.getCountry()));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);

            //Excel sheetA first sheetA
            WritableSheet sheetA = workbook.createSheet(filename, 0);

            WritableFont cellFont = new WritableFont(WritableFont.TIMES, 12);
            cellFont.setColour(Colour.BLUE);

            WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
            cellFormat.setBackground(Colour.ORANGE);
            cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            cellFont.setPointSize(6);
            NumberFormat currencyVN = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));
//            for (int j =0;j<10;j++) {
//                sheetA.addCell(new Label(j, 0, titles[j]));
//            }
            sheetA.addCell(new Label(0, 0, "Số thứ tự",cellFormat));
            sheetA.addCell(new Label(1, 0, "Ngày tháng",cellFormat));
            sheetA.setColumnView(1,20);
            sheetA.addCell(new Label(2, 0, "Địa Chỉ",cellFormat));
            sheetA.setColumnView(2,25);
            sheetA.addCell(new Label(3, 0, "Hạng mục",cellFormat));
            sheetA.addCell(new Label(4, 0, "Chủng loại",cellFormat));
            sheetA.setColumnView(4,25);
            sheetA.addCell(new Label(5, 0, "ĐVT",cellFormat));
            sheetA.addCell(new Label(6, 0,"Số Lượng",cellFormat));
            sheetA.addCell(new Label(7, 0, "Đơn Giá (VNĐ)",cellFormat));
            sheetA.setColumnView(7,20);
            sheetA.addCell(new Label(8, 0,"Thành Tiền (VNĐ)",cellFormat));
            sheetA.setColumnView(8,20);
            sheetA.addCell(new Label(9, 0, "Ghi Chú",cellFormat));

            Double cost = 0.0;
            for(int i = 1;i<=billList.size();i++){
                    BillOfSale bill = billList.get(i-1);
                    cost += Double.parseDouble(bill.getQuantity())*Double.parseDouble(bill.getUnit_price());
                    sheetA.addCell(new Label(0, i, Integer.toString(i )));
                    sheetA.addCell(new Label(1, i, bill.getDate()));
                    sheetA.addCell(new Label(2, i, bill.getAddress()));
                    sheetA.addCell(new Label(3, i, bill.getCategories()));
                    sheetA.addCell(new Label(4, i, bill.getType()));
                    sheetA.addCell(new Label(5, i, bill.getUnit()));
                    sheetA.addCell(new Label(6, i, (bill.getQuantity())));
                    sheetA.addCell(new Label(7, i, currencyVN.format(Double.parseDouble(bill.getUnit_price()))));
                    sheetA.addCell(new Label(8, i, currencyVN.format(Double.parseDouble(bill.getQuantity()) * Double.parseDouble(bill.getUnit_price()))));
                    sheetA.addCell(new Label(9, i, bill.getNote().equals("null")?"":bill.getNote()));

                }
            WritableFont font = new WritableFont(WritableFont.TIMES, 12,WritableFont.BOLD,true);
            WritableCellFormat format_last = new WritableCellFormat(font);
            format_last.setBorder(Border.ALL, BorderLineStyle.THIN);
            font.setPointSize(12);
            sheetA.addCell(new Label(1,billList.size()+1,"TỔNG TIỀN",format_last));
            sheetA.addCell(new Label(7,billList.size()+1,currencyVN.format(cost),format_last));
            // close workbook
            workbook.write();
            workbook.close();

            Uri path = FileProvider.getUriForFile(context, "com.vn.quanly.fileprovider", file);
            Log.e("path",path.toString());
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/xls");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            context.startActivity(Intent.createChooser(fileIntent, "Gửi bản hóa đơn"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void writeXLSXFile() throws IOException {

        String excelFileName = "fileCreat.xlsx";//name of excel file

        org.apache.poi.ss.usermodel.Workbook wb=new HSSFWorkbook();
        Cell cell=null;
        CellStyle cellStyle=wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //Now we are creating sheet
        Sheet sheet=null;
        sheet = wb.createSheet("Name of sheet");
        //Now column and row
        Row row =sheet.createRow(0);

        cell=row.createCell(0);
        cell.setCellValue("Name");
        cell.setCellStyle(cellStyle);

        cell=row.createCell(1);
        cell.setCellValue("Number");
        cell.setCellStyle(cellStyle);

        sheet.setColumnWidth(0,(10*200));
        sheet.setColumnWidth(1,(10*200));

        FileOutputStream fileOut = new FileOutputStream(excelFileName);

        //write this workbook to an Outputstream.
        wb.write(fileOut);
//        fileOut.flush();
        fileOut.close();

//        Uri path = FileProvider.getUriForFile(context, "com.vn.quanly.fileprovider", file);
//        Log.e("path",path.toString());
//        Intent fileIntent = new Intent(Intent.ACTION_SEND);
//        fileIntent.setType("text/xls");
//        fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
//        fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        fileIntent.putExtra(Intent.EXTRA_STREAM, path);
//        context.startActivity(Intent.createChooser(fileIntent, "Gửi bản hóa đơn"));

//        File filelocation = new File(getFilesDir(), excelFileName);
//        Uri path = FileProvider.getUriForFile(getApplicationContext(), "com.vn.quanly.fileprovider", filelocation);
//        Intent fileIntent = new Intent(Intent.ACTION_SEND);
//        fileIntent.setType("text/csv");
//        fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
//        fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        fileIntent.putExtra(Intent.EXTRA_STREAM, path);
//        startActivity(Intent.createChooser(fileIntent, "Gửi bảng giá cho khách hàng"));
    }
}

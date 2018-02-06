package com.thecamhi.base;

import java.text.SimpleDateFormat;  
import java.util.Date;  
  
public class MyDate {  
    public static String getFileName() {  
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
        String date = format.format(new Date(System.currentTimeMillis()));  
        return date;// 2012Äê10ÔÂ03ÈÕ 23:41:31  
    }  
  
    public static String getDateEN() {  
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        String date1 = format1.format(new Date(System.currentTimeMillis()));  
        return date1;// 2012-10-03 23:41:31  
    }  
    
    public static String getDateEN2() {  
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");  
        String date1 = format1.format(new Date(System.currentTimeMillis()));  
        return date1;// 2012-10-03 23:41:31  
    }  
  
}  
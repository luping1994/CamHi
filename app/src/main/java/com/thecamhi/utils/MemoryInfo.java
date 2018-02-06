package com.thecamhi.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.hichip.base.HiLog;

import android.app.ActivityManager;
import android.content.Context;
import android.text.format.Formatter;


public class MemoryInfo {
	
	public static void displayBriefMemory(Context context) {    

        final ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);    

        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();   

        activityManager.getMemoryInfo(info);    

        HiLog.e("系统剩余内存:"+(info.availMem >> 10)+"k");
        HiLog.e("系统是否处于低内存运行："+info.lowMemory);
        HiLog.e("当系统剩余内存低于"+info.threshold+"时就看成低内存运行");

    } 

	public static String getAvailMemory(Context context) {// 閼惧嘲褰嘺ndroid瑜版挸澧犻崣顖滄暏閸愬懎鐡ㄦ径褍鐨�  
		  
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);  
        android.app.ActivityManager.MemoryInfo mi = new android.app.ActivityManager.MemoryInfo();  
        am.getMemoryInfo(mi);  
        //mi.availMem; 瑜版挸澧犵化鑽ょ埠閻ㄥ嫬褰查悽銊ュ敶鐎涳拷  
  
        return Formatter.formatFileSize(context, mi.availMem);// 鐏忓棜骞忛崣鏍畱閸愬懎鐡ㄦ径褍鐨憴鍕壐閸栵拷  
    }  
  
	public static String getTotalMemory(Context context) {  
        String str1 = "/proc/meminfo";// 缁崵绮洪崘鍛摠娣団剝浼呴弬鍥︽  
        String str2;  
        String[] arrayOfString;  
        long initial_memory = 0;  
  
        try {  
            FileReader localFileReader = new FileReader(str1);  
            BufferedReader localBufferedReader = new BufferedReader(  
                    localFileReader, 8192);  
            str2 = localBufferedReader.readLine();// 鐠囪褰噈eminfo缁楊兛绔寸悰宀嬬礉缁崵绮洪幀璇插敶鐎涙ê銇囩亸锟�  
  
            arrayOfString = str2.split("\\s+");  
            for (String num : arrayOfString) {  
//                Log.i(str2, num + "\t");  
            }  
  
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 閼惧嘲绶辩化鑽ょ埠閹鍞寸�涙﹫绱濋崡鏇氱秴閺勭枩B閿涘奔绠绘禒锟�1024鏉烆剚宕叉稉绡墆te  
            localBufferedReader.close();  
  
        } catch (IOException e) {  
        }  
        return Formatter.formatFileSize(context, initial_memory);// Byte鏉烆剚宕叉稉绡旴閹存牞锟藉尐B閿涘苯鍞寸�涙ê銇囩亸蹇氼潐閺嶇厧瀵�  
    }  
	
}
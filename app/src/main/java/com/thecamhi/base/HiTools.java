package com.thecamhi.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.hichip.base.HiLog;
import com.thecamhi.activity.LiveViewActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class HiTools {

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 *  type=0,filename.jpg;  type=1,filename.mp4;  type=2,没有后缀名
	 * @param type
	 * @return
	 */
	public static String getFileNameWithTime(int type) {

		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH) + 1;
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMinute = c.get(Calendar.MINUTE);
		int mSec = c.get(Calendar.SECOND);
		//		int mMilliSec = c.get(Calendar.MILLISECOND);

		StringBuffer sb = new StringBuffer();
		if(type == 0){
			sb.append("IMG_");
		}
		sb.append(mYear);
		if (mMonth < 10)
			sb.append('0');
		sb.append(mMonth);
		if (mDay < 10)
			sb.append('0');
		sb.append(mDay);
		sb.append('_');
		if (mHour < 10)
			sb.append('0');
		sb.append(mHour);
		if (mMinute < 10)
			sb.append('0');
		sb.append(mMinute);
		if (mSec < 10)
			sb.append('0');
		sb.append(mSec);


		if(type == 0){
			sb.append(".jpg");
		}
		else if(type==1){
			sb.append(".mp4");
		}else{

		}

		return sb.toString();
	}

	public static boolean isSDCardValid() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}


	public static boolean saveImage(String fileName, Bitmap frame) {

		if (fileName == null || fileName.length() <= 0)
			return false;

		boolean bErr = false;
		FileOutputStream fos = null;

		try {

			fos = new FileOutputStream(fileName, false);
			frame.compress(Bitmap.CompressFormat.JPEG, 90, fos);
			fos.flush();
			fos.close();

		} catch (Exception e) {

			bErr = true;
			System.out.println("saveImage(.): " + e.getMessage());

		} finally {

			if (bErr) {

				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return false;
			}
		}
		
		

		return true;
	}	


	public static String formetFileSize(long fileSize) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0B";
		if (fileSize == 0) {
			return wrongSize;
		}
		if (fileSize < 1024) {
			fileSizeString = df.format((double) fileSize) + "B";
		} else if (fileSize < 1048576) {
			fileSizeString = df.format((double) fileSize / 1024) + "KB";
		} else if (fileSize < 1073741824) {
			fileSizeString = df.format((double) fileSize / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileSize / 1073741824) + "GB";
		}
		return fileSizeString;
	}

	public static String sdfTimeSec(long timeLong){
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		String timeStr = df.format(timeLong);	
		return timeStr;
	}
	public static String sdfTimeDay(long timeLong){
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd 00:00:00");  
		String timeStr = df.format(timeLong);	
		return timeStr;
	}
	
	
	public static void saveBitmap(Bitmap bitmap,String fileName)
    {
		if(bitmap==null){
			return;
		}
		
        File file = new File(fileName);
        if(file.exists()){
            file.delete();
        }
        
        FileOutputStream out;
        try{
        	
            out = new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.PNG, 90, out))
            {
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        }
	
	
	
	

	public static int getAndroidVersion(){

		char[] c=android.os.Build.VERSION.RELEASE.toCharArray();
		String str=null;
		if(c!=null&&c.length>=1){
			str=String.valueOf(c[0]);
		}

		if(str==null){
			return 0;
		}

		return Integer.valueOf(str);
	}
	

	
	
	public static boolean checkPermission(Context context,String permission){
		
		int checkCallPhonePermission = ContextCompat.
				checkSelfPermission(context,permission);
		if(checkCallPhonePermission == PackageManager.PERMISSION_GRANTED){
			return true;
		}
		return false;
	}
	
	/**
	 * 检查并征求用户当前app要用到所有的权限
	 */
	public static void checkPermissionAll(Activity activity) {
		List<String> list=new ArrayList<String>();
		if(!HiTools.checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
			list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}
		if(!HiTools.checkPermission(activity, Manifest.permission.RECORD_AUDIO)){
			list.add(Manifest.permission.RECORD_AUDIO);
		}
		if(!HiTools.checkPermission(activity, Manifest.permission.CAMERA)){
			list.add(Manifest.permission.CAMERA);
		}
		if(list.size()>0){
		String[] permissions=new String[list.size()];
		for(int i=0;i<list.size();i++){
			permissions[i]=list.get(i);
		}
		ActivityCompat.requestPermissions(activity, permissions, 0);
		}
	}

}

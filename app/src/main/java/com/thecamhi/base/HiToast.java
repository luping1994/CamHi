package com.thecamhi.base;

import android.content.Context;
import android.widget.Toast;

public class HiToast {
	static Toast toast;
	public static void showToast(Context context,String str){
		if(toast==null){
			toast=Toast.makeText(context, str, Toast.LENGTH_SHORT);
		}else{
			toast.setText(str);
		}
		toast.show();
	}
}

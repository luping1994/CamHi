package com.thecamhi.base;

import com.thecamhi.bean.HiDataValue;
import com.thecamhi.main.MainActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.support.v4.app.ActivityCompat;
import android.text.TextDirectionHeuristic;
import android.util.Log;
import android.widget.Toast;

public class CrashApplication extends Application{
	private static  CrashApplication app;
	@Override
	public void onCreate() {
		super.onCreate();
		app=this;
		CrashHandler.getInstance().init(this);
	}
	
	public static synchronized CrashApplication getInstance(){
		return app;
	}
}










package com.thecamhi.base;

import android.content.Context;
import android.util.AttributeSet;

import com.hichip.control.HiGLMonitor;

public class MyPlaybackGLMonitor extends HiGLMonitor{

	public MyPlaybackGLMonitor(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
//		HiLog.e("==========MyGLMonitor===========");
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		HiLog.e("==========MyGLMonitor  onPause===========");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		HiLog.e("==========MyGLMonitor  onResume===========");
	}
	
	
	
	
	

}

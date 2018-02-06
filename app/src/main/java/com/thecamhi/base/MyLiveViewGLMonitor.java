package com.thecamhi.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.hichip.base.HiLog;
import com.hichip.content.HiChipDefines;
import com.hichip.control.HiGLMonitor;
import com.hichip.sdk.HiChipP2P;
import com.thecamhi.bean.MyCamera;

public class MyLiveViewGLMonitor extends HiGLMonitor implements OnTouchListener,GestureDetector.OnGestureListener{

	private long ptzTime = 0;
	private GestureDetector mGestureDetector;
	private static final int FLING_MIN_DISTANCE = 50;
	public static final int PTZ_STEP=25;

	private MyCamera mCamera = null;


	Matrix matrix = new Matrix();

	private OnTouchListener mOnTouchListener;

	private Activity context;

	private int state=0;//normal=0, larger=1,two finger touch=3
	private int touchMoved;  //not move=0,  move=1, two point=2

	private SurfaceHolder sfh;
	public int left;
	public int width;
	public int height;
	public int bottom;
	public float screen_width ;
	public float screen_height ;

	public MyLiveViewGLMonitor(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mGestureDetector = new GestureDetector(context, this);
		super.setOnTouchListener(this);
		setOnTouchListener(this);  
		setFocusable(true);     
		setClickable(true);     
		setLongClickable(true);   
		this.context=(Activity)context;

		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width  = dm.widthPixels;
		screen_height = dm.heightPixels;
		
	}

	public int getTouchMove(){
		return this.touchMoved;
	}
	public void setTouchMove(int touchMoved){
		this.touchMoved=touchMoved;
	}

	public int getState(){
		return state;
	}

	public void setState(int state){
		this.state=state;
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



	//View当前的位置  
	private float rawX = 0;  
	private float rawY = 0;  
	//View之前的位置  
	private float lastX = 0;  
	private float lastY = 0;  

	int xlenOld;
	int ylenOld;

	private int pyl=20;
	double nLenStart = 0;

	@SuppressLint("WrongCall") @Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		//		HiLog.v("onTouch:");

		if(mOnTouchListener != null) {
			mOnTouchListener.onTouch(v, event);
		}

		int nCnt = event.getPointerCount();
		HiLog.e("mMonitor.state="+state); 
		if(state==1){
			if(nCnt==2){
				return false;
			}

			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:  
				HiLog.e("mMonitor.ACTION_DOWN");  
				//获取手指落下的坐标并保存  
				rawX = (event.getRawX());  
				rawY = (event.getRawY());  
				lastX = rawX;  
				lastY = rawY;  
				break;  
			case MotionEvent.ACTION_MOVE:  
				if(touchMoved==2){
					break;
				}

				HiLog.e("mMonitor.ACTION_MOVE");  
				//手指拖动时，获得当前位置  
				rawX = event.getRawX();  
				rawY = event.getRawY();  
				//手指移动的x轴和y轴偏移量分别为当前坐标-上次坐标  
				float offsetX = rawX - lastX;  
				float offsetY = rawY - lastY;  
				//通过View.layout来设置左上右下坐标位置  
				//获得当前的left等坐标并加上相应偏移量  
				if(Math.abs(offsetX)<pyl && Math.abs(offsetY)<pyl){
					
					return false;
				}

				left += offsetX;
				bottom -= offsetY;

				if(left>0){
					left=0;
				}
				if(bottom>0){
					bottom=0;
				}

				if((left+width<(screen_width))){
					left=(int) (screen_width-width);
				}

				if(bottom+height<screen_height){
					bottom=(int) (screen_height-height);
				}


				if(left<=(-width)){
					left=(-width);
				}

				if(bottom<=(-height)){
					bottom=(-height);
				}

				
				setMatrix(left,  
						bottom,  
						width,  
						height);  
				//移动过后，更新lastX与lastY  
				lastX = rawX;  
				lastY = rawY;  
				break;  
			}
			return true;  
		}else if(state==0&&nCnt==1){
			HiLog.e("mMonitor.mGestureDetector");  
			return mGestureDetector.onTouchEvent(event);
		}
		return true;

		/*		
		if(mOnTouchListener != null) {
			mOnTouchListener.onTouch(v, event);
		}




		return mGestureDetector.onTouchEvent(event);*/

		//		HiLog.v("onTouch:"+event);

		//		return false;
	}

	public void saveMatrix(int left,int bottom,int width,int height){
		this.left=left;
		this.bottom=bottom;
		this.width=width;
		this.height=height;
	}


	float resetWidth;
	float resetHeight;

	public void setView(){
		WindowManager.LayoutParams wlp =context.getWindow().getAttributes();
		wlp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;

		context.getWindow().setAttributes(wlp);
		context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		float screen_width  = dm.widthPixels;
		float screen_height = dm.heightPixels;

		if(resetWidth==0){
			resetWidth=screen_width;
			resetHeight=screen_height;
		}
		resetWidth+=100;
		resetHeight+=100;

		/*SurfaceView sfv=MyLiveViewGLMonitor.this;
		sfh=sfv.getHolder();
		HiLog.e(sfh==null?"sfh is null":"sfh is not null");
		canvas = sfh.lockCanvas();
		HiLog.e(canvas==null?"canvas is null":"canvas is not null");
		if(canvas!=null){


			canvas.scale((float)1.5,(float)1.5,screen_width / 2, screen_height / 2);

			canvas.drawColor(android.R.color.transparent);
			canvas.restore();
			sfh.unlockCanvasAndPost(canvas);


		}*/
		/*	FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				(int)resetWidth, (int)resetHeight);
		setLayoutParams(lp);
		 */
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		//		HiLog.v("onDown:");
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		//		HiLog.v("onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		//		HiLog.v("onSingleTapUp");
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		//		HiLog.v("onScroll:");


		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		//		HiLog.v("onLongPress:");
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		//		HiLog.v("velocityX: " + Math.abs(velocityX) + ", velocityY: " + Math.abs(velocityY));
		if(mCamera == null)
			return false;

		long curTime = System.currentTimeMillis();
		if(curTime - ptzTime > 500){
			ptzTime = curTime;
		}else {
			return false;
		}

		this.scrollTo((int)velocityX,(int)velocityY);

		invalidate();
		HiLog.e("onFling");
		if(state==0){
			if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > Math.abs(velocityY)) {
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_CTRL,HiChipDefines.HI_P2P_S_PTZ_CTRL.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, HiChipDefines.HI_P2P_PTZ_CTRL_LEFT, HiChipDefines.HI_P2P_PTZ_MODE_STEP,(short)PTZ_STEP,(short)PTZ_STEP));
			} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > Math.abs(velocityY)) {
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_CTRL,HiChipDefines.HI_P2P_S_PTZ_CTRL.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, HiChipDefines.HI_P2P_PTZ_CTRL_RIGHT, HiChipDefines.HI_P2P_PTZ_MODE_STEP,(short)PTZ_STEP,(short)PTZ_STEP));
			} else if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(velocityY) > Math.abs(velocityX)) {
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_CTRL,HiChipDefines.HI_P2P_S_PTZ_CTRL.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, HiChipDefines.HI_P2P_PTZ_CTRL_UP, HiChipDefines.HI_P2P_PTZ_MODE_STEP,(short)PTZ_STEP,(short)PTZ_STEP));
			} else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE && Math.abs(velocityY) > Math.abs(velocityX)) {
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_CTRL,HiChipDefines.HI_P2P_S_PTZ_CTRL.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, HiChipDefines.HI_P2P_PTZ_CTRL_DOWN, HiChipDefines.HI_P2P_PTZ_MODE_STEP,(short)PTZ_STEP,(short)PTZ_STEP));
			}
		}

		return false;
	}



	public void setCamera(MyCamera mCamera) {
		this.mCamera = mCamera;
	}


	public void setOnTouchListener(OnTouchListener mOnTouchListener) {
		this.mOnTouchListener = mOnTouchListener;
	}




}

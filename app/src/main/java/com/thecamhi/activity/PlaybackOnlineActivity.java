package com.thecamhi.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.hichip.R;
import com.hichip.base.HiLog;
import com.hichip.base.HiThread;
import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.callback.ICameraPlayStateCallback;
import com.hichip.content.HiChipDefines;
import com.hichip.control.HiCamera;
import com.hichip.tools.Packet;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.MyPlaybackGLMonitor;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;

public class PlaybackOnlineActivity extends HiActivity implements ICameraIOSessionCallback,ICameraPlayStateCallback {

	//	private final static int HANDLE_MESSAGE_PLAY_START = 0x90000001;
	//	private final static int HANDLE_MESSAGE_PLAY_STATE = 0x90000001;

	private final static int HANDLE_MESSAGE_PROGRESSBAR_RUN = 0x90000002;
	private final  static int HANDLE_MESSAGE_SEEKBAR_RUN=0x90000003;

	public final static short HI_P2P_PB_PLAY=1;
	public final static short HI_P2P_PB_STOP=2;
	public final static short HI_P2P_PB_PAUSE=3;
	public final static short HI_P2P_PB_SETPOS=4;
	public final static short HI_P2P_PB_GETPOS=5;

	private int video_width;
	private int video_height;

	private ProgressThread pthread = null;


	private ProgressBar prs_loading;
	private ImageView img_shade;

	private byte[] startTime;
	private byte[] oldStartTime;
	private MyPlaybackGLMonitor mMonitor;
	private MyCamera mCamera;
	private SeekBar prs_playing;


	private long playback_time;
	private long startTimeLong;
	private long endTimeLong;

	private int progressTime;

	private short model;//PLAY=1,STOP=2,PAUSE=3,SETPOS=4,GETPOS=5

	private LinearLayout laypout_playback_exit;
	private RelativeLayout playback_view_screen;
	private boolean visible=true;
	private boolean isSelected=true;
	private ImageView play_btn_playback_online;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题

		setContentView(R.layout.activity_playback_online_landscape);


		Bundle bundle = this.getIntent().getExtras();
		String uid = bundle.getString(HiDataValue.EXTRAS_KEY_UID);
		byte[] b_startTime =  bundle.getByteArray("st");
		oldStartTime = new byte[8]; 
		System.arraycopy(b_startTime, 0, oldStartTime, 0, 8);
		playback_time =  bundle.getLong("pb_time");

		startTimeLong=bundle.getLong(VideoOnlineActivity.VIDEO_PLAYBACK_START_TIME);	
		endTimeLong=bundle.getLong(VideoOnlineActivity.VIDEO_PLAYBACK_END_TIME);	


		for(MyCamera camera: HiDataValue.CameraList) {
			if(camera.getUid().equals(uid)) {
				mCamera = camera;
				break;
			}
		}


		initView();
		showLoadingShade();


		mCamera.registerIOSessionListener(this);
		mCamera.registerPlayStateListener(PlaybackOnlineActivity.this);

		if(mCamera.getCommandFunction(HiChipDefines.HI_P2P_PB_QUERY_START_NODST)){
			//HiLog.e("sendCMD: HI_P2P_PB_QUERY_START_NODST");
			startTime=oldStartTime;
		}else{
			if(mCamera.getSummerTimer()){
				
				HiChipDefines.STimeDay newTime = new HiChipDefines.STimeDay(oldStartTime,0);
				newTime.resetData(-1);
				startTime = newTime.parseContent();
			}else{
				startTime=oldStartTime;
			}
		}
		
		

		mCamera.startPlayback(new HiChipDefines.STimeDay(startTime, 0),mMonitor);
		model=HI_P2P_PB_PLAY;
		
	}



	private void initView() {
		/*	if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setContentView(R.layout.activity_playback_online_portrait);


			TitleView nb = (TitleView)findViewById(R.id.title_top);

			nb.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
//			nb.setButton(HiNavigationBar.NAVIGATION_BUTTON_RIGHT);
			nb.setTextTitle(getString(R.string.title_play_vidoe));
			nb.setNavigationBarButtonListener(new TitleView.NavigationBarButtonListener() {

				@Override
				public void OnNavigationButtonClick(int which) {
					// TODO Auto-generated method stub
					switch(which) {
					case TitleView.NAVIGATION_BUTTON_LEFT:
						finish();
						break;
					case TitleView.NAVIGATION_BUTTON_RIGHT:
						break;
					}
				}
			});
		}
		else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {*/
		//	setContentView(R.layout.activity_playback_online_landscape);
		//}

		mMonitor = (MyPlaybackGLMonitor)findViewById(R.id.monitor_playback_view);
		mCamera.setLiveShowMonitor(mMonitor);
		prs_loading = (ProgressBar)findViewById(R.id.prs_loading);
		img_shade = (ImageView)findViewById(R.id.img_shade);


		progressTime=(int) ((endTimeLong-startTimeLong)/1000);
		prs_playing=(SeekBar)findViewById(R.id.prs_playing);
		prs_playing.setMax(progressTime); 
		prs_playing.setProgress(0);

		prs_playing.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int count=seekBar.getProgress();
				long time=count*1000;
				//	mCamera.stopPlayback();
				//	mCamera.startPlayback(new HiChipDefines.STimeDay(Packet.longToByteArray_Little(time), 0),mMonitor);

				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_PB_POS_SET, 
						HiChipDefines.HI_P2P_PB_SETPOS_REQ.parseContent(0, (int)(count*100/progressTime),
								startTime));

				/*		HiLog.e("channel="+0+"  time rate="+(int)(count*100/progressTime)+"  startTime=:"+
						Packet.getHex(startTime, startTime.length));*/

				/*mCamera.sendIOCtrl(HiChipDefines.HI_P2P_PB_PLAY_CONTROL,HiChipDefines.HI_P2P_S_PB_PLAY_REQ
						.parseContent(0, HI_P2P_PB_PAUSE, startTime));*/
				model=HI_P2P_PB_PAUSE;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {


			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {


			}
		});

		laypout_playback_exit=(LinearLayout)findViewById(R.id.laypout_playback_exit);
		laypout_playback_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				finish();

			}
		});

		playback_view_screen=(RelativeLayout)findViewById(R.id.playback_view_screen);
		playback_view_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(model==0){
					return;
				}
				visible=!visible;
				setImageVisible(visible);
			}
		});

		play_btn_playback_online=(ImageView)findViewById(R.id.play_btn_playback_online);
		play_btn_playback_online.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mCamera.getConnectState()==HiCamera.CAMERA_CONNECTION_STATE_LOGIN){
					if(model==HI_P2P_PB_STOP){
						model=HI_P2P_PB_PLAY;
						mCamera.startPlayback(new HiChipDefines.STimeDay(startTime, 0),mMonitor);

					}else{
						mCamera.sendIOCtrl(HiChipDefines.HI_P2P_PB_PLAY_CONTROL,HiChipDefines.HI_P2P_S_PB_PLAY_REQ
								.parseContent(0, HI_P2P_PB_PAUSE, startTime));
						//pause  and  restart  is the same
					}
				}
			}
		});
	}

	private void setImageVisible(boolean b){
		if(b){
			laypout_playback_exit.setVisibility(View.VISIBLE);
			prs_playing.setVisibility(View.VISIBLE);
			play_btn_playback_online.setVisibility(View.VISIBLE);

		}else{

			play_btn_playback_online.setVisibility(View.GONE);
			laypout_playback_exit.setVisibility(View.GONE);
			prs_playing.setVisibility(View.GONE);
		}
	}



	private void showLoadingShade() {

		prs_loading.setMax(100);
		prs_loading.setProgress(10);
		pthread = new ProgressThread();
		pthread.startThread();
		//		mCamera.startLiveShow(1, mMonitor);
	}

	private void displayLoadingShade() {
		if(pthread != null)
			pthread.stopThread();
		pthread = null;
		prs_loading .setVisibility(View.GONE);
		img_shade.setVisibility(View.GONE);

		visible=true;
		setImageVisible(visible);
	}


	@Override
	protected void onResume() {

		super.onResume();
		if(mCamera != null) {
			//	mCamera.startPlayback(new HiChipDefines.STimeDay(startTime, 0),mMonitor);
			mCamera.registerIOSessionListener(this);
			mCamera.registerPlayStateListener(this);
		}
	}


	@Override
	public void onPause() {

		super.onPause();

		if(mCamera != null) {
			
			
			if(model!=0){
				model=0;
				oldTime=0;
			}
			
			
			mCamera.stopPlayback();
			mCamera.unregisterIOSessionListener(this);
			mCamera.unregisterPlayStateListener(this);
			HiLog.e("unregister");

		}else{
			HiLog.e("camera == null");
		}

		if(pthread!=null){


			pthread.stopThread();
			pthread=null;

		}

		finish();
	}
	@Override
	protected void onDestroy() {

		super.onDestroy();
	}


	private class ProgressThread extends HiThread {
		public void run() {
			while(isRunning) {
				sleep(100);
				Message msg = handler.obtainMessage();
				msg.what = HANDLE_MESSAGE_PROGRESSBAR_RUN;
				handler.sendMessage(msg);
			}
		}
	}


	/*	@Override
	public void callbackState(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		HiLog.e("flag:"+arg0+ "   width:" + arg1 + "   heigth:"+arg2);

		//		if(arg0 != ICameraPlayStateCallback.PLAY_STATE_START)
		//			return;
		Message msg = handler.obtainMessage();
		msg.what = arg0;
		msg.arg1 = arg1;
		msg.arg2 = arg2;
		handler.sendMessage(msg);
	}
	 */
	@Override
	public void receiveIOCtrlData(HiCamera camera, int arg1, byte[] arg2, int arg3) {

		if(mCamera!=camera)return;

		Bundle bundle = new Bundle();
		bundle.putByteArray(HiDataValue.EXTRAS_KEY_DATA, arg2);
		Message msg=handler.obtainMessage();
		msg.what=arg1;
		msg.arg2 = arg3;
		msg.setData(bundle);
		handler.sendMessage(msg);

	}

	@Override
	public void receiveSessionState(HiCamera arg0, int arg1) {


	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {

			case ICameraPlayStateCallback.PLAY_STATE_START:

				HiLog.e("state=PLAY_STATE_START");

				video_width = msg.arg1;
				video_height = msg.arg2;


				model=HI_P2P_PB_PLAY;
				//HiLog.e("model:"+model);

				resetMonitorSize();



				break;

			case ICameraPlayStateCallback.PLAY_STATE_EDN:


				isSelected=true;
				play_btn_playback_online.setSelected(isSelected);

				model=HI_P2P_PB_STOP;
				HiToast.showToast(PlaybackOnlineActivity.this, getString(R.string.tips_stop_video));

				break;
			case ICameraPlayStateCallback.PLAY_STATE_POS:
				//HiLog.e("state=PLAY_STATE_POS");

				break;

			case HANDLE_MESSAGE_PROGRESSBAR_RUN:
			{	

				int cur = prs_loading.getProgress();

				//	HiLog.e("HANDLE_MESSAGE_PROGRESSBAR_RUN:"+cur);
				if(cur>=100) {
					prs_loading.setProgress(10);
				}
				else {
					prs_loading.setProgress(cur + 8);
				}

				model=HI_P2P_PB_PLAY;

			}				
			break;


			case HANDLE_MESSAGE_SEEKBAR_RUN:

				//HiLog.e("HANDLE_MESSAGE_SEEKBAR_RUN:"+msg.arg1);


				//int count=prs_playing.getProgress();
				prs_playing.setProgress(msg.arg1); 


				break;
			case HiChipDefines.HI_P2P_PB_POS_SET:

				//	HiLog.e("state=HI_P2P_PB_POS_SET");

				/*mCamera.sendIOCtrl(HiChipDefines.HI_P2P_PB_PLAY_CONTROL,
						HiChipDefines.HI_P2P_S_PB_PLAY_REQ.parseContent(0, HI_P2P_PB_PLAY, startTime));*/


				try {
					Thread.sleep(600); //每一帧的时间间隔是500毫秒
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				model=HI_P2P_PB_PLAY;

				break;
				/*case HiChipDefines.HI_P2P_GET_TIME_ZONE:

				if(msg.arg2==0){
					Bundle bundle = msg.getData();
					byte[] data = bundle.getByteArray(HiDataValue.EXTRAS_KEY_DATA);



					HiChipDefines.HI_P2P_S_TIME_ZONE timezone = new HiChipDefines.HI_P2P_S_TIME_ZONE(data);

					if(timezone.u32DstMode == 1 ) {

						HiChipDefines.STimeDay newTime = new HiChipDefines.STimeDay(oldStartTime,0);
						newTime.resetData(-1);
						startTime = newTime.parseContent();

					}else{
						startTime=oldStartTime;
					}


					mCamera.startPlayback(new HiChipDefines.STimeDay(startTime, 0),mMonitor);

				}

				break;*/


			case HiChipDefines.HI_P2P_PB_PLAY_CONTROL:

				//HiLog.e("state=HI_P2P_PB_PLAY_CONTROL");

				isSelected=!isSelected;
				play_btn_playback_online.setSelected(isSelected);

				break;


			}
		}
	};

	private void resetMonitorSize() {

		if(video_width==0 || video_height==0){
			return;
		}
		displayLoadingShade();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screen_width  = dm.widthPixels;
		int screen_height = dm.heightPixels;

		//	HiLog.e("screen_width" +screen_width + "   screen_height"+screen_height);

		/*if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

			WindowManager.LayoutParams wlp = getWindow().getAttributes();
            wlp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(wlp);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

			int width = screen_width;
			int height = (int)((float)width/((float)video_width/video_height));

			HiLog.e("width" +width + "   height"+height);

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					width, height);
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
			lp.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);

			mMonitor.setLayoutParams(lp);

		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {*/

		WindowManager.LayoutParams wlp = getWindow().getAttributes();
		wlp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setAttributes(wlp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		int width = screen_width;
		int height = screen_height;

		//	HiLog.e("width" +width + "   height"+height);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				width, height);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
		lp.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);

		mMonitor.setLayoutParams(lp);
		//	}

	}





	long oldTime;
	@Override
	public void callbackPlayUTC(HiCamera camera, int timeInteger) {
		if(mCamera!=camera||model==HI_P2P_PB_PAUSE||model==0)return;



		if(oldTime==0){
			oldTime=(long)timeInteger;
		}

		long sub=(long)timeInteger-oldTime;

		//	HiLog.e("timeInteger="+timeInteger+"\n oldTime="+oldTime);

		int step= (int) (sub/1000);
		Message msg = handler.obtainMessage();
		msg.what = HANDLE_MESSAGE_SEEKBAR_RUN;
		msg.arg1=step;
		handler.sendMessage(msg);

	}



	@Override
	public void callbackState(HiCamera camera, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if(mCamera!=camera)return;
		//HiLog.e("flag:"+camera+ "   width:" + arg1 + "   heigth:"+arg2);

		if(arg1 == ICameraPlayStateCallback.PLAY_STATE_START){
			HiLog.e("state=PLAY_STATE_START");
		}

		Message msg = handler.obtainMessage();
		msg.what = arg1;
		msg.arg1 = arg2;
		msg.arg2 = arg3;
		handler.sendMessage(msg);

	}



}

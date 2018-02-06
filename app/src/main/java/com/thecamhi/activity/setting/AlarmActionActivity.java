package com.thecamhi.activity.setting;

import android.Manifest;
import android.R.bool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.hichip.R;
import com.hichip.base.HiLog;
import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.content.HiChipDefines;
import com.hichip.control.HiCamera;
import com.hichip.data.HiDeviceInfo;
import com.tencent.android.tpush.XGPushConfig;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.HiTools;
import com.thecamhi.base.TitleView;
import com.thecamhi.base.TitleView.NavigationBarButtonListener;
import com.thecamhi.bean.CamHiDefines;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.bean.MyCamera.OnBindPushResult;
import com.thecamhi.main.HiActivity;

public class AlarmActionActivity extends HiActivity implements ICameraIOSessionCallback{
	private MyCamera mCamera;

	private HiChipDefines.HI_P2P_S_ALARM_PARAM param;
	private HiChipDefines.HI_P2P_SNAP_ALARM snapParam;
	private ToggleButton alarm_push_push_tgbtn,alarm_push_sd_video_tgbtn,alarm_push_email_alarm_tgbtn,
	alarm_push_save_picture_tgbtn,alarm_push_video_tgbtn;
	private Spinner alarm_push_pictures_num_spn;


	private final static int HANDLE_MESSAGE_BIND_SUCCESS = 0x80000001;
	private final static int HANDLE_MESSAGE_BIND_FAIL = 0x80000002;
	private final static int HANDLE_MESSAGE_UNBIND_SUCCESS = 0x80000003;
	private final static int HANDLE_MESSAGE_UNBIND_FAIL = 0x80000004;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_action_with_alarm);

		String uid=getIntent().getStringExtra(HiDataValue.EXTRAS_KEY_UID);

		for (MyCamera camera : HiDataValue.CameraList) {
			if (uid.equals(camera.getUid())) {
				mCamera = camera;

				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_ALARM_PARAM, null);
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_SNAP_ALARM_PARAM, null);

				break;

			}
		}


		initView();
	}

	private void initView() {
		TitleView title=(TitleView)findViewById(R.id.title_top);
		title.setTitle(getResources().getString(R.string.title_action_with_alarm));
		title.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		title.setNavigationBarButtonListener(new NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				switch (which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					AlarmActionActivity.this.finish();
					break;
				}

			}
		});


		alarm_push_push_tgbtn=(ToggleButton)findViewById(R.id.alarm_push_push_tgbtn);

		if(mCamera.getPushState() > 0) {
			alarm_push_push_tgbtn.setChecked(true);
		}
		else {
			alarm_push_push_tgbtn.setChecked(false);
		}
		alarm_push_push_tgbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				ToggleButton tbtn = (ToggleButton)v;



				if(HiDataValue.XGToken == null){

					if(HiDataValue.ANDROID_VERSION>=6){
						if(!HiTools.checkPermission(AlarmActionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
							ActivityCompat.requestPermissions(AlarmActionActivity.this,new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
						}
					}

					HiDataValue.XGToken = XGPushConfig.getToken(AlarmActionActivity.this);

					if(HiDataValue.XGToken == null) {
						tbtn.setChecked(!tbtn.isChecked());
						//修改提示语：信鸽获取异常请稍后重试
						HiToast.showToast(AlarmActionActivity.this,"null token");
						return;
					}
				}



				showLoadingProgress();
				mCamera.bindPushState(tbtn.isChecked(), bindPushResult);
				//				if(mCamera.getPushState() > 0 && !togbtn_push_notification.isChecked()) {
				//				//关闭推送
				//				}
				//				if(mCamera.getPushState() <= 0 && togbtn_push_notification.isChecked()) {
				//					//打开推送
				//				}
			}
		});


		alarm_push_sd_video_tgbtn=(ToggleButton)findViewById(R.id.alarm_push_sd_video_tgbtn);
		alarm_push_email_alarm_tgbtn=(ToggleButton)findViewById(R.id.alarm_push_email_alarm_tgbtn);
		alarm_push_save_picture_tgbtn=(ToggleButton)findViewById(R.id.alarm_push_save_picture_tgbtn);
		alarm_push_video_tgbtn=(ToggleButton)findViewById(R.id.alarm_push_video_tgbtn);

		alarm_push_pictures_num_spn=(Spinner)findViewById(R.id.alarm_push_pictures_num_spn);
		ArrayAdapter<CharSequence> adapter_frequency = ArrayAdapter.createFromResource(this, R.array.alarm_action_picture_num, android.R.layout.simple_spinner_item);
		adapter_frequency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		alarm_push_pictures_num_spn.setAdapter(adapter_frequency);

		LinearLayout action_alarm_picture_num_ll=(LinearLayout)findViewById(R.id.action_alarm_picture_num_ll);
		if(mCamera.getChipVersion()==HiDeviceInfo.CHIP_VERSION_HISI){
			action_alarm_picture_num_ll.setVisibility(View.VISIBLE);
		}


		Button alarm_push_application_btn=(Button)findViewById(R.id.alarm_push_application_btn);
		alarm_push_application_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendFTPSetting();

			}
		});

	}

	private void sendRegister(){
		if(mCamera.getPushState()==1){
			return;
		}
		if(!mCamera.getCommandFunction(CamHiDefines.HI_P2P_ALARM_TOKEN_REGIST)){
			HiLog.v("REGIST FUCTION: false ");
			return;
		}
		
		
		byte[] info=CamHiDefines.
				HI_P2P_ALARM_TOKEN_INFO.parseContent(0,mCamera.getPushState(),(int)(System.currentTimeMillis()/1000/3600),mCamera.getPushState()>0?1:0);

		HiLog.e("HiDataValue.XGToken:"+HiDataValue.XGToken+" Time:"+(int)(System.currentTimeMillis()/1000/3600)+" subID:"+mCamera.getPushState());
		HiLog.e(info+"");
		mCamera.sendIOCtrl(CamHiDefines.HI_P2P_ALARM_TOKEN_REGIST, info);
	}

	private void sendUnRegister(){
		if(mCamera.getPushState()==0){
			return;
		}
		if(!mCamera.getCommandFunction(CamHiDefines.HI_P2P_ALARM_TOKEN_UNREGIST)){
			HiLog.v("UNREGIST FUCTION: false ");
			return;
		}
		
		byte[] info=CamHiDefines.HI_P2P_ALARM_TOKEN_INFO.parseContent(0,mCamera.getPushState(),(int)(System.currentTimeMillis()/1000/3600),mCamera.getPushState()>0?1:0);
		HiLog.e("HiDataValue.XGToken:"+mCamera.getPushState()+" Time:"+(int)(System.currentTimeMillis()/1000/3600)+" enable:"+mCamera.getPushState());
		HiLog.e(info+"");
		mCamera.sendIOCtrl(CamHiDefines.HI_P2P_ALARM_TOKEN_UNREGIST, info);
	}

	private OnBindPushResult bindPushResult = new OnBindPushResult() {

		@Override
		public void onBindSuccess(MyCamera camera) {
			// TODO Auto-generated method stub
			Message msg = handler.obtainMessage();
			msg.what = HANDLE_MESSAGE_BIND_SUCCESS;
			handler.sendMessage(msg);
		}

		@Override
		public void onBindFail(MyCamera camera) {
			// TODO Auto-generated method stub
			Message msg = handler.obtainMessage();
			msg.what = HANDLE_MESSAGE_BIND_FAIL;
			handler.sendMessage(msg);
		}

		@Override
		public void onUnBindSuccess(MyCamera camera) {
			// TODO Auto-generated method stub
			Message msg = handler.obtainMessage();
			msg.what = HANDLE_MESSAGE_UNBIND_SUCCESS;
			handler.sendMessage(msg);
		}

		@Override
		public void onUnBindFail(MyCamera camera) {
			// TODO Auto-generated method stub
			Message msg = handler.obtainMessage();
			msg.what = HANDLE_MESSAGE_UNBIND_FAIL;
			handler.sendMessage(msg);
		}


	};

	protected void sendFTPSetting() {
		if(param==null)return;
		//param.u32Svr=alarm_push_push_tgbtn.isChecked()?1:0;
		param.u32SDRec=alarm_push_sd_video_tgbtn.isChecked()?1:0;
		param.u32EmailSnap=alarm_push_email_alarm_tgbtn.isChecked()?1:0;
		param.u32FtpSnap=alarm_push_save_picture_tgbtn.isChecked()?1:0;
		param.u32FtpRec=alarm_push_video_tgbtn.isChecked()?1:0;

		HiLog.e("\n param.u32SDRec: "+param.u32SDRec+"\n param.u32EmailSnap: "+param.u32EmailSnap
				+"\n param.u32FtpSnap: "+param.u32FtpSnap+"\n param.u32FtpRec"+param.u32FtpRec);
		mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_ALARM_PARAM, param.parseContent());
		if(snapParam==null)return;
		snapParam.u32Number=(int) alarm_push_pictures_num_spn.getSelectedItemPosition()+1;
		snapParam.u32Interval=snapParam.u32Interval<5?5:snapParam.u32Interval;
		mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_SNAP_ALARM_PARAM, snapParam.parseContent());

	}

	@Override
	public void receiveIOCtrlData(HiCamera arg0, int arg1, byte[] arg2, int arg3) {
		if(arg0 != mCamera)
			return;

		Bundle bundle = new Bundle();
		bundle.putByteArray(HiDataValue.EXTRAS_KEY_DATA, arg2);
		Message msg = handler.obtainMessage();
		msg.what = HiDataValue.HANDLE_MESSAGE_RECEIVE_IOCTRL;
		msg.obj = arg0;
		msg.arg1 = arg1;
		msg.arg2 = arg3;
		msg.setData(bundle);
		handler.sendMessage(msg);

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case  HANDLE_MESSAGE_BIND_SUCCESS:

				alarm_push_push_tgbtn.setChecked(true);
				dismissLoadingProgress();
				mCamera.updateInDatabase(AlarmActionActivity.this);
				sendRegister();
				break;
			case  HANDLE_MESSAGE_BIND_FAIL:
				alarm_push_push_tgbtn.setChecked(false);
				dismissLoadingProgress();
				mCamera.updateInDatabase(AlarmActionActivity.this);
				break;
			case  HANDLE_MESSAGE_UNBIND_SUCCESS:
				alarm_push_push_tgbtn.setChecked(false);
				
				sendUnRegister();
				mCamera.setPushState(HiDataValue.DEFAULT_PUSH_STATE);
				dismissLoadingProgress();
				
				mCamera.updateInDatabase(AlarmActionActivity.this);
			
				
				break;
			case  HANDLE_MESSAGE_UNBIND_FAIL:
				alarm_push_push_tgbtn.setChecked(true);
				dismissLoadingProgress();
				mCamera.updateInDatabase(AlarmActionActivity.this);
				break;


			case HiDataValue.HANDLE_MESSAGE_RECEIVE_IOCTRL:
			{
				if(msg.arg2==0) {
					//					MyCamera camera = (MyCamera)msg.obj;
					Bundle bundle = msg.getData();
					byte[] data = bundle.getByteArray(HiDataValue.EXTRAS_KEY_DATA);
					switch (msg.arg1) {
					case HiChipDefines.HI_P2P_GET_ALARM_PARAM:
						param=new HiChipDefines.HI_P2P_S_ALARM_PARAM(data);
						//						alarm_push_push_tgbtn.setChecked(mCamera.getPushState()==1?true:false);
						//	alarm_push_push_tgbtn.setChecked(param.u32Svr==1?true:false);
						alarm_push_sd_video_tgbtn.setChecked(param.u32SDRec==1?true:false);
						alarm_push_email_alarm_tgbtn.setChecked(param.u32EmailSnap==1?true:false);
						alarm_push_save_picture_tgbtn.setChecked(param.u32FtpSnap==1?true:false);
						alarm_push_video_tgbtn.setChecked(param.u32FtpRec==1?true:false);

						break;
					case HiChipDefines.HI_P2P_GET_SNAP_ALARM_PARAM:
						snapParam=new HiChipDefines.HI_P2P_SNAP_ALARM(data);
						alarm_push_pictures_num_spn.setSelection(snapParam.u32Number-1);

						break;
					case HiChipDefines.HI_P2P_SET_ALARM_PARAM:

						HiToast.showToast(AlarmActionActivity.this, 
								getResources().getString(R.string.alarm_action_save_success));

						//						mCamera.setPushState(alarm_push_push_tgbtn.isChecked()?1:0);
						mCamera.updateInDatabase(AlarmActionActivity.this);
						finish();
						break;
					case HiChipDefines.HI_P2P_SET_SNAP_ALARM_PARAM:

						break;
					case CamHiDefines.HI_P2P_ALARM_TOKEN_UNREGIST:
						HiLog.v(data+"");
						
						break;
					}
				}else{
					switch (msg.arg1) {

					case HiChipDefines.HI_P2P_SET_ALARM_PARAM:
						HiToast.showToast(AlarmActionActivity.this, 
								getResources().getString(R.string.alarm_action_save_failed));

						break;
					}
				}
			}
			}
		}
	};	


	@Override
	public void receiveSessionState(HiCamera arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mCamera != null) {
			mCamera.registerIOSessionListener(this);
		}
	}




	@Override
	public void onPause() {
		super.onPause();
		if(mCamera != null) {
			mCamera.unregisterIOSessionListener(this);
		}
	}
}

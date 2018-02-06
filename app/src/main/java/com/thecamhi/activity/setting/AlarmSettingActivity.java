package com.thecamhi.activity.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.hichip.R;
import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.content.HiChipDefines;
import com.hichip.control.HiCamera;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.TitleView;
import com.thecamhi.base.TitleView.NavigationBarButtonListener;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;

public class AlarmSettingActivity extends HiActivity implements ICameraIOSessionCallback{

	private MyCamera mCamera;

	private Spinner spinner_motion_sensitivity;
	HiChipDefines.HI_P2P_S_MD_PARAM md_param = null;
	HiChipDefines.HI_P2P_S_MD_PARAM md_param2 = null;
	HiChipDefines.HI_P2P_S_MD_PARAM md_param3 = null;
	HiChipDefines.HI_P2P_S_MD_PARAM md_param4 = null;
	private ToggleButton togbtn_motion_detection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_setting);

		String	uid=getIntent().getStringExtra(HiDataValue.EXTRAS_KEY_UID);


		for (MyCamera camera : HiDataValue.CameraList) {
			if (uid.equals(camera.getUid())) {
				mCamera = camera;
				
				HiChipDefines.HI_P2P_S_MD_PARAM mdparam2 = new HiChipDefines.HI_P2P_S_MD_PARAM(
						0,new HiChipDefines.HI_P2P_S_MD_AREA(HiChipDefines.HI_P2P_MOTION_AREA_2,0,0,0,0,0,0)
						);
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_MD_PARAM, mdparam2.parseContent());
				
				HiChipDefines.HI_P2P_S_MD_PARAM mdparam3 = new HiChipDefines.HI_P2P_S_MD_PARAM(
						0,new HiChipDefines.HI_P2P_S_MD_AREA(HiChipDefines.HI_P2P_MOTION_AREA_3,0,0,0,0,0,0)
						);
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_MD_PARAM, mdparam3.parseContent());
				
				HiChipDefines.HI_P2P_S_MD_PARAM mdparam4 = new HiChipDefines.HI_P2P_S_MD_PARAM(
						0,new HiChipDefines.HI_P2P_S_MD_AREA(HiChipDefines.HI_P2P_MOTION_AREA_4,0,0,0,0,0,0)
						);
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_MD_PARAM, mdparam4.parseContent());
				

				HiChipDefines.HI_P2P_S_MD_PARAM mdparam = new HiChipDefines.HI_P2P_S_MD_PARAM(
						0,new HiChipDefines.HI_P2P_S_MD_AREA(HiChipDefines.HI_P2P_MOTION_AREA_1,0,0,0,0,0,0)
						);
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_MD_PARAM, mdparam.parseContent());
				break;

			}
		}


		initView();

	}

	private void initView() {
		TitleView title=(TitleView)findViewById(R.id.title_top);
		title.setTitle(getResources().getString(R.string.title_alarm_motion_detection));
		title.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		title.setNavigationBarButtonListener(new NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				switch (which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					AlarmSettingActivity.this.finish();
					break;
				case TitleView.NAVIGATION_BUTTON_RIGHT:

					break;

				}

			}
		});


		spinner_motion_sensitivity = (Spinner) findViewById(R.id.spinner_motion_sensitivity);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.motion_detection_sensitivity, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_motion_sensitivity.setAdapter(adapter);

		togbtn_motion_detection = (ToggleButton) findViewById(R.id.togbtn_motion_detection);

		Button alarm_setting_btn=(Button)findViewById(R.id.alarm_setting_btn);
		alarm_setting_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendMotionDetection();

			}
		});
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

	protected void sendMotionDetection() {
		if(md_param == null&& md_param2==null&&md_param3==null&&md_param4==null) {
			return;
		}

		int guard_switch = togbtn_motion_detection.isChecked()?1:0;
		int motion_detection = spinner_motion_sensitivity.getSelectedItemPosition();
		md_param.struArea.u32Enable = guard_switch;
		int md = 0;

		if (motion_detection == 2)
			md = 25;
		else if (motion_detection == 1)
			md = 50;
		else if (motion_detection == 0)
			md = 75;

		md_param.struArea.u32Sensi = md;
		mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_MD_PARAM, md_param.parseContent());

		if(!togbtn_motion_detection.isChecked() && md_param2!= null) {
			md_param2.struArea.u32Enable = 0;
			mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_MD_PARAM, md_param2.parseContent());
		}
		if(!togbtn_motion_detection.isChecked() && md_param3!= null) {
			md_param3.struArea.u32Enable = 0;
			mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_MD_PARAM, md_param3.parseContent());
		}
		if(!togbtn_motion_detection.isChecked() && md_param4!= null) {
			md_param4.struArea.u32Enable = 0;
			mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_MD_PARAM, md_param4.parseContent());
		}

	}



	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case HiDataValue.HANDLE_MESSAGE_RECEIVE_IOCTRL:
			{
				if(msg.arg2==0) {
					//					MyCamera camera = (MyCamera)msg.obj;
					Bundle bundle = msg.getData();
					byte[] data = bundle.getByteArray(HiDataValue.EXTRAS_KEY_DATA);
					switch (msg.arg1) {

					case HiChipDefines.HI_P2P_GET_MD_PARAM:
						HiChipDefines.HI_P2P_S_MD_PARAM md_param_temp = new HiChipDefines.HI_P2P_S_MD_PARAM(data);

						if(md_param_temp.struArea.u32Area == HiChipDefines.HI_P2P_MOTION_AREA_1)
						{
							md_param = md_param_temp;
							togbtn_motion_detection.setChecked(md_param.struArea.u32Enable==1?true:false);

							int sensitivity = md_param.struArea.u32Sensi;
							if (sensitivity >= 0 && sensitivity <= 25) {
								spinner_motion_sensitivity.setSelection(2);
								//								m_motion_detection = 0;
							} else if (sensitivity > 25 && sensitivity <= 50) {
								spinner_motion_sensitivity.setSelection(1);
								//								m_motion_detection = 1;
							} else if (sensitivity > 50) {
								spinner_motion_sensitivity.setSelection(0);
								//								m_motion_detection = 2;
							}
						}
						else if(md_param_temp.struArea.u32Area == HiChipDefines.HI_P2P_MOTION_AREA_2) {
							md_param2 = md_param_temp;
						}
						else if(md_param_temp.struArea.u32Area == HiChipDefines.HI_P2P_MOTION_AREA_3) {
							md_param3 = md_param_temp;
						}
						else if(md_param_temp.struArea.u32Area == HiChipDefines.HI_P2P_MOTION_AREA_4) {
							md_param4 = md_param_temp;
						}




						break;
					case HiChipDefines.HI_P2P_SET_MD_PARAM:	
						HiToast.showToast(AlarmSettingActivity.this, getString(R.string.tips_alarm_setting_success));
						finish();
						break;


					}
				}else{
					switch (msg.arg1) {
					case HiChipDefines.HI_P2P_SET_MD_PARAM:
						HiToast.showToast(AlarmSettingActivity.this, getString(R.string.tips_alarm_setting_failed));
						break;

					default:
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

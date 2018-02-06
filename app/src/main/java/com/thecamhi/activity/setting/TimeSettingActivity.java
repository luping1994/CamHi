package com.thecamhi.activity.setting;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hichip.R;
import com.hichip.base.HiLog;
import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.content.HiChipDefines;
import com.hichip.content.HiChipDefines.STimeDay;
import com.hichip.control.HiCamera;
import com.hichip.sdk.HiChipP2P;
import com.hichip.system.HiDefaultData;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.TitleView;
import com.thecamhi.base.TitleView.NavigationBarButtonListener;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;

public class TimeSettingActivity extends HiActivity implements ICameraIOSessionCallback,OnClickListener{
	private MyCamera mCamera;
	//	private TextView equipment_time_tv;
	private STimeDay camera_time;
	private Spinner equipment_time_zone_spn;
	private CheckBox togbtn_daylight_saving_time;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_equipment_time_setting);

		String uid = getIntent().getStringExtra(HiDataValue.EXTRAS_KEY_UID);

		for (MyCamera camera : HiDataValue.CameraList) {
			if (uid.equals(camera.getUid())) {
				mCamera = camera;
				//mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_TIME_PARAM, new byte[0]);
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_TIME_ZONE, new byte[0]);
				break;
			}
		}

		initView();
		showLoadingProgress();
	}

	private void initView() {
		TitleView title=(TitleView)findViewById(R.id.title_top);
		title.setTitle(getResources().getString(R.string.title_equipment_setting));
		title.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		title.setNavigationBarButtonListener(new NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				switch (which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					TimeSettingActivity.this.finish();
					break;
				}

			}
		});

		final LinearLayout	lay_daylight_saving_time = (LinearLayout)findViewById(R.id.lay_daylight_saving_time);
		togbtn_daylight_saving_time=(CheckBox)findViewById(R.id.togbtn_daylight_saving_time);


		//	equipment_time_tv=(TextView)findViewById(R.id.equipment_time_tv);
		equipment_time_zone_spn=(Spinner)findViewById(R.id.equipment_time_zone_spn);
		ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.device_timezone, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		equipment_time_zone_spn.setAdapter(adapter);

		equipment_time_zone_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Log.v("hichip", "onItemSelected"+arg2+"      "+arg3);

				if(HiDefaultData.TimeZoneField[arg2][1] == 1) {
					lay_daylight_saving_time.setVisibility(View.VISIBLE);
				}
				else {
					lay_daylight_saving_time.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});


		TextView phone_time_zone_et = (TextView)findViewById(R.id.phone_time_zone_et);

		TimeZone tz = TimeZone.getDefault();
		Log.v("hichip", "tz.getDisplayName();:  "+tz.getDisplayName()+ "     "+tz.getID()+"   "+  tz.getDSTSavings());

		float tim = (float)tz.getRawOffset()/(3600000.0f);
		String gmt = null;
		gmt = "GMT"+tim;
		if(tim > 0 ) {
			gmt = "GMT+"+tim;
		}
		phone_time_zone_et.setText(gmt+"  "+tz.getDisplayName());

		Button setting_time_zone_btn=(Button)findViewById(R.id.setting_time_zone_btn);
		setting_time_zone_btn.setOnClickListener(this);
		Button synchronization_time_btn=(Button)findViewById(R.id.synchronization_time_btn);
		synchronization_time_btn.setOnClickListener(this);
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
					case HiChipDefines.HI_P2P_GET_TIME_PARAM:
						//	camera_time = new STimeDay(data,0);
						//	equipment_time_tv.setText(camera_time.toString2());

						/*handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_TIME_PARAM, new byte[0]);
							}
						}, 1000);*/
						//						String.format("%04d:%02d", camera_time.year,camera_time.month,camera_time.)

						break;
					case HiChipDefines.HI_P2P_GET_TIME_ZONE:

						HiChipDefines.HI_P2P_S_TIME_ZONE timezone = new HiChipDefines.HI_P2P_S_TIME_ZONE(data);

						togbtn_daylight_saving_time.setChecked(timezone.u32DstMode == 1?true:false);

						int index = 0;
						for(int i=0;i<24;i++) {
							if(HiDefaultData.TimeZoneField[i][0] == timezone.s32TimeZone) {
								index = i;
								break;
							}
						}

						equipment_time_zone_spn.setSelection(index);

						dismissLoadingProgress();

						break;
					case HiChipDefines.HI_P2P_SET_TIME_PARAM:

						HiToast.showToast(TimeSettingActivity.this, getString(R.string.tips_device_time_setting_synchroned_time));

						break;
					case HiChipDefines.HI_P2P_SET_TIME_ZONE:


						if(!mCamera.getCommandFunction(HiChipDefines.HI_P2P_PB_QUERY_START_NODST)){
							mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_REBOOT,new byte[0]);

							HiToast.showToast(TimeSettingActivity.this, getString(R.string.tips_device_time_setting_timezone));
						}else{
							mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_REBOOT,new byte[0]);
							HiToast.showToast(TimeSettingActivity.this, getString(R.string.tips_device_time_setting_timezone));
						}
						finish();

						break;

					}
				}
			}
			break;
			}
		}
	};


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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.synchronization_time_btn:

			Calendar cal = Calendar.getInstance(TimeZone.getDefault());
			cal.setTimeInMillis(System.currentTimeMillis());

			byte[] time = HiChipDefines.HI_P2P_S_TIME_PARAM.parseContent(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));


			mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_TIME_PARAM, time);
			break;
		case R.id.setting_time_zone_btn:

			if(mCamera.getCommandFunction(HiChipDefines.HI_P2P_PB_QUERY_START_NODST)){
				//HiLog.e("sendCMD: HI_P2P_PB_QUERY_START_NODST");
				//sendTimeZone();
				showAlertDialog();
			}else{
				showAlertDialog();
			}


			break;
		default:
			break;
		}

	}

	public void sendTimeZone(){

		int desMode = 0;

		int tz = HiDefaultData.TimeZoneField[equipment_time_zone_spn.getSelectedItemPosition()][0];

		if(HiDefaultData.TimeZoneField[equipment_time_zone_spn.getSelectedItemPosition()][1] == 0) {
			desMode = 0;
		}
		else {

			desMode = togbtn_daylight_saving_time.isChecked()? 1:0;
		}

		mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_TIME_ZONE, HiChipDefines.HI_P2P_S_TIME_ZONE.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, tz, desMode));

	}

	public void showAlertDialog(){


		AlertDialog.Builder builder=new AlertDialog.Builder(TimeSettingActivity.this);

		builder.setTitle(getString(R.string.tips_warning));
		builder.setMessage(getResources().getString(R.string.tips_device_time_setting_reboot_camera));
		builder.setPositiveButton(getString(R.string.btn_ok),new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				sendTimeZone();
			}
		});
		builder.setNegativeButton(getString(R.string.cancel),null);
		builder.show();
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

	@Override
	public void receiveSessionState(HiCamera arg0, int arg1) {
		// TODO Auto-generated method stub

	}
}

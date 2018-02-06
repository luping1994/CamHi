package com.thecamhi.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.hichip.R;
import com.hichip.base.HiLog;


import com.thecamhi.base.TitleView;
import com.thecamhi.base.TitleView.NavigationBarButtonListener;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;

public class AliveSettingActivity extends HiActivity implements OnClickListener{
	private MyCamera mCamera;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alive_setting_activity);
		String uid=getIntent().getStringExtra(HiDataValue.EXTRAS_KEY_UID);

		for (MyCamera camera : HiDataValue.CameraList) {
			if (uid.equals(camera.getUid())) {
				mCamera = camera;
				break;
			}
		}

		initView();
	}

	private void initView() {

		TitleView titleView=(TitleView)findViewById(R.id.title_top);
		titleView.setTitle(getString(R.string.camera_setup));
		titleView.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		titleView.setNavigationBarButtonListener(new NavigationBarButtonListener() {
			@Override
			public void OnNavigationButtonClick(int which) {
				finish();
			}
		});
		ImageView snapshot_alive_setting=(ImageView)findViewById(R.id.snapshot_alive_setting);
		
		HiLog.e(mCamera+"");
		if(mCamera!=null){
			snapshot_alive_setting.setImageBitmap(mCamera.snapshot);
			TextView nickname_alive_setting=(TextView)findViewById(R.id.nickname_alive_setting);
			nickname_alive_setting.setText(mCamera.getNikeName());
			TextView uid_alive_setting=(TextView)findViewById(R.id.uid_alive_setting);
			uid_alive_setting.setText(mCamera.getUid());
			TextView state_alive_setting=(TextView)findViewById(R.id.state_alive_setting);
			String str_state[] =getResources().getStringArray(R.array.connect_state);
			state_alive_setting.setText(str_state[mCamera.getConnectState()]);
		}


		TextView modify_password=(TextView)findViewById(R.id.modify_password);
		modify_password.setOnClickListener(this);

		TextView alarm_motion_detection=(TextView)findViewById(R.id.alarm_motion_detection);
		alarm_motion_detection.setOnClickListener(this);

		TextView action_with_alarm=(TextView)findViewById(R.id.action_with_alarm);
		action_with_alarm.setOnClickListener(this);

		TextView timing_video=(TextView)findViewById(R.id.timing_video);
		timing_video.setOnClickListener(this);

		TextView audio_setup=(TextView)findViewById(R.id.audio_setup);
		audio_setup.setOnClickListener(this);

		TextView video_settings=(TextView)findViewById(R.id.video_settings);
		video_settings.setOnClickListener(this);

		TextView wifi_settings=(TextView)findViewById(R.id.wifi_settings);
		wifi_settings.setOnClickListener(this);

		TextView sd_card_set=(TextView)findViewById(R.id.sd_card_set);
		sd_card_set.setOnClickListener(this);

		TextView equipment_time_setting=(TextView)findViewById(R.id.equipment_time_setting);
		equipment_time_setting.setOnClickListener(this);

		TextView mailbox_settings=(TextView)findViewById(R.id.mailbox_settings);
		mailbox_settings.setOnClickListener(this);

		TextView ftp_settings=(TextView)findViewById(R.id.ftp_settings);
		ftp_settings.setOnClickListener(this);

		TextView system_settings=(TextView)findViewById(R.id.system_settings);
		system_settings.setOnClickListener(this);

		TextView equipment_information=(TextView)findViewById(R.id.equipment_information);
		equipment_information.setOnClickListener(this);
		

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.modify_password:
		{
			Intent intent=new Intent(AliveSettingActivity.this,PasswordSettingActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
			startActivity(intent);
		}
		break;
		case R.id.alarm_motion_detection:
		{
			Intent intent=new Intent(AliveSettingActivity.this,AlarmSettingActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
			startActivity(intent);
		}
		break;
		case R.id.action_with_alarm:
		{
			Intent intent=new Intent(AliveSettingActivity.this,AlarmActionActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
			startActivity(intent);
		}
		break;
		case R.id.timing_video:
		{
			Intent intent=new Intent(AliveSettingActivity.this,TimeVideoActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
			startActivity(intent);
		}	
		break;
		case R.id.audio_setup:
		{
			Intent intent=new Intent(AliveSettingActivity.this,AudioSettingActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
			startActivity(intent);

		}
		break;
		case R.id.video_settings:
		{
			Intent intent=new Intent(AliveSettingActivity.this,VideoSettingActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
			startActivity(intent);
		}
		break;
		case R.id.wifi_settings:
		{
			Intent intent=new Intent(AliveSettingActivity.this,WifiSettingActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
			startActivity(intent);
		}
		break;
		case R.id.sd_card_set:
		{
			Intent intent=new Intent(AliveSettingActivity.this,SDCardSettingActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
			startActivity(intent);
		}
		break;
		case R.id.equipment_time_setting:
		{
			Intent intent=new Intent(AliveSettingActivity.this,TimeSettingActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
			startActivity(intent);
		}

		break;
		case R.id.mailbox_settings:
		{
			Intent intent=new Intent(AliveSettingActivity.this,EmailSettingActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
			startActivity(intent);
		}
		break;
		case R.id.ftp_settings:
		{
			Intent intent=new Intent(AliveSettingActivity.this,FtpSettingActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
			startActivity(intent);
		}
		break;
		case R.id.system_settings:
		{
			Intent intent=new Intent(AliveSettingActivity.this,SystemSettingActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
			startActivity(intent);
		}

		break;
		case R.id.equipment_information:
		{
			Intent intent=new Intent(AliveSettingActivity.this,DeviceInfoActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
			startActivity(intent);
		}

		break;

		}

	}

}

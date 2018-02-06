package com.thecamhi.activity.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hichip.R;
import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.content.HiChipDefines;
import com.hichip.content.HiChipDefines.HI_P2P_S_FTP_PARAM_EXT;
import com.hichip.control.HiCamera;
import com.hichip.tools.Packet;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.TitleView;
import com.thecamhi.base.TitleView.NavigationBarButtonListener;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;

public class FtpSettingActivity extends HiActivity implements ICameraIOSessionCallback{
	private MyCamera mCamera;
	private boolean isCheck=false;
	private EditText ftp_setting_server_edt,ftp_setting_port_edt,ftp_setting_username_edt,
	ftp_setting_psw_edt,ftp_setting_path_edt;
	private ToggleButton ftp_setting_mode_tgbtn;
	private HiChipDefines.HI_P2P_S_FTP_PARAM_EXT param;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ftp_setting);

		String uid =getIntent().getStringExtra(HiDataValue.EXTRAS_KEY_UID);

		for (MyCamera camera : HiDataValue.CameraList) {
			if (uid.equals(camera.getUid())) {
				mCamera = camera;
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_FTP_PARAM_EXT, null);

				break;

			}
		}

		initView();
	}
	private void initView() {
		TitleView title=(TitleView)findViewById(R.id.title_top);
		title.setTitle(getResources().getString(R.string.title_ftp_settings));
		title.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		title.setNavigationBarButtonListener(new NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				switch (which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					FtpSettingActivity.this.finish();
					break;

				}

			}
		});

		ftp_setting_server_edt=(EditText)findViewById(R.id.ftp_setting_server_edt);
		ftp_setting_port_edt=(EditText)findViewById(R.id.ftp_setting_port_edt);
		ftp_setting_username_edt=(EditText)findViewById(R.id.ftp_setting_username_edt);
		ftp_setting_psw_edt=(EditText)findViewById(R.id.ftp_setting_psw_edt);
		ftp_setting_path_edt=(EditText)findViewById(R.id.ftp_setting_path_edt);

		ftp_setting_mode_tgbtn=(ToggleButton)findViewById(R.id.ftp_setting_mode_tgbtn);

		Button testBtn=(Button)findViewById(R.id.ftp_setting_test_btn);

		testBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(param==null){
					return;
				}
				
				showLoadingProgress();
				isCheck=true;
				sendFTPSetting(isCheck);
			}
		});

		Button ftp_setting_application_btn=(Button)findViewById(R.id.ftp_setting_application_btn);
		ftp_setting_application_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(param==null){
					return;
				}
				
				showLoadingProgress();
				isCheck=false;
				sendFTPSetting(isCheck);

			}
		});

	}

	protected void sendFTPSetting(boolean check) {

	
		String server=ftp_setting_server_edt.getText().toString();

		String port=ftp_setting_port_edt.getText().toString();

		String username=ftp_setting_username_edt.getText().toString();
		String psw=ftp_setting_psw_edt.getText().toString();
		String path=ftp_setting_path_edt.getText().toString();

		param.setStrSvr(server);
		param.u32Port=Integer.valueOf(port);
		param.setStrUsernm(username);
		param.setStrPasswd(psw);
		param.setStrFilePath(path);
		param.u32Check=check?1:0;

		byte[] sendParam=param.parseContent();
		mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_FTP_PARAM_EXT, sendParam);

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
					case HiChipDefines.HI_P2P_GET_FTP_PARAM_EXT:
						param=new HI_P2P_S_FTP_PARAM_EXT(data);
						ftp_setting_server_edt.setText(Packet.getString(param.strSvr));
						ftp_setting_port_edt.setText(String.valueOf(param.u32Port));
						ftp_setting_username_edt.setText(Packet.getString(param.strUsernm));
						ftp_setting_psw_edt.setText(Packet.getString(param.strPasswd));
						ftp_setting_path_edt.setText(Packet.getString(param.strFilePath));

						ftp_setting_mode_tgbtn.setChecked(param.u32Mode==1);
						break;
					case HiChipDefines.HI_P2P_SET_FTP_PARAM_EXT:
						if(!isCheck){
							HiToast.showToast(FtpSettingActivity.this, 
									getResources().getString(R.string.ftp_setting_save_success));
							finish();
						}else{
							HiToast.showToast(FtpSettingActivity.this, 
									getResources().getString(R.string.mailbox_setting_check_success));

						}
						dismissLoadingProgress();

						break;


					}
				}else{
					switch (msg.arg1) {
					case HiChipDefines.HI_P2P_SET_FTP_PARAM_EXT:
						if(!isCheck){
							HiToast.showToast(FtpSettingActivity.this, 
									getResources().getString(R.string.ftp_setting_save_failed));
						}else{
							HiToast.showToast(FtpSettingActivity.this, 
									getResources().getString(R.string.mailbox_setting_check_failed));
						}
						dismissLoadingProgress();
						break;


					}


				}
			}
			break;


			}
		}
	};


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

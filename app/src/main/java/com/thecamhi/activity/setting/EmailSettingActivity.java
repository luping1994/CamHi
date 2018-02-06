package com.thecamhi.activity.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.hichip.R;
import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.content.HiChipDefines;
import com.hichip.control.HiCamera;
import com.hichip.tools.Packet;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.TitleView;
import com.thecamhi.base.TitleView.NavigationBarButtonListener;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;

public class EmailSettingActivity extends HiActivity implements ICameraIOSessionCallback {
	private MyCamera mCamera;
	private EditText mailbox_setting_server_edt,mailbox_setting_port_edt,
	mailbox_setting_username_edt,mailbox_setting_psw_edt,mailbox_setting_receive_address_edt,
	mailbox_setting_sending_address_edt,mailbox_setting_theme_edt,mailbox_setting_message_edt;
	private Spinner mailbox_setting_safety_spn;
	private ToggleButton mailbox_setting_check_tgbtn;
	HiChipDefines.HI_P2P_S_EMAIL_PARAM param;
	private boolean isCheck=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mailbox_setting);

		String uid =getIntent().getStringExtra(HiDataValue.EXTRAS_KEY_UID);

		for (MyCamera camera : HiDataValue.CameraList) {
			if (uid.equals(camera.getUid())) {
				mCamera = camera;

				//	HiChipDefines.HI_P2P_S_MD_PARAM mdparam = new HiChipDefines.HI_P2P_S_MD_PARAM(
				//		0,new HiChipDefines.HI_P2P_S_MD_AREA(HiChipDefines.HI_P2P_MOTION_AREA_1,0,0,0,0,0,0)
				//		);
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_EMAIL_PARAM, null);

				break;

			}
		}

		initView();
	}

	private void initView() {
		TitleView title=(TitleView)findViewById(R.id.title_top);
		title.setTitle(getResources().getString(R.string.title_mailbox_settings));
		title.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		title.setNavigationBarButtonListener(new NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				switch (which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					EmailSettingActivity.this.finish();
					break;
				}
			}
		});

		mailbox_setting_server_edt=(EditText)findViewById(R.id.mailbox_setting_server_edt);
		mailbox_setting_port_edt=(EditText)findViewById(R.id.mailbox_setting_port_edt);
		mailbox_setting_username_edt=(EditText)findViewById(R.id.mailbox_setting_username_edt);
		mailbox_setting_psw_edt=(EditText)findViewById(R.id.mailbox_setting_psw_edt);
		mailbox_setting_receive_address_edt=(EditText)findViewById(R.id.mailbox_setting_receive_address_edt);
		mailbox_setting_sending_address_edt=(EditText)findViewById(R.id.mailbox_setting_sending_address_edt);
		mailbox_setting_theme_edt=(EditText)findViewById(R.id.mailbox_setting_theme_edt);
		mailbox_setting_message_edt=(EditText)findViewById(R.id.mailbox_setting_message_edt);

		mailbox_setting_safety_spn=(Spinner)findViewById(R.id.mailbox_setting_safety_spn);
		ArrayAdapter<CharSequence> adapter_frequency = ArrayAdapter.createFromResource(this, R.array.safety_connection, android.R.layout.simple_spinner_item);
		adapter_frequency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mailbox_setting_safety_spn.setAdapter(adapter_frequency);

		mailbox_setting_check_tgbtn=(ToggleButton)findViewById(R.id.mailbox_setting_check_tgbtn);

		
		Button testBtn=(Button)findViewById(R.id.mailbox_setting_test_btn);
		
		testBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(param==null){
					return;
				}
				
				showLoadingProgress();
				isCheck=true;
				sendMailSetting(isCheck);
			}
		});
		
		
		Button mailbox_setting_application_btn=(Button)findViewById(R.id.mailbox_setting_application_btn);
		mailbox_setting_application_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(param==null){
					return;
				}
				
				showLoadingProgress();
				isCheck=false;
				sendMailSetting(isCheck);

			}
		});
	}

	protected void sendMailSetting(boolean check) {
		
		
		
		String serverStr=mailbox_setting_server_edt.getText().toString().trim();
		String portStr=mailbox_setting_port_edt.getText().toString().trim();
		String usernameStr=mailbox_setting_username_edt.getText().toString().trim();
		String pswStr=mailbox_setting_psw_edt.getText().toString().trim();
		
		String sendingStr=mailbox_setting_receive_address_edt.getText().toString().trim();
		String  receiveStr=mailbox_setting_sending_address_edt.getText().toString().trim();
		String themeStr=mailbox_setting_theme_edt.getText().toString().trim();
		String messageStr=mailbox_setting_message_edt.getText().toString().trim();

		param.setStrSvr(serverStr);

		param.u32Port=Integer.valueOf(portStr);
		param.setStrUsernm(usernameStr);
		param.setStrPasswd(pswStr);
		param.setStrFrom(receiveStr);
		param.setStrTo(sendingStr);
		param.setStrSubject(themeStr);
		param.setStrText(messageStr);
		param.u32LoginType=mailbox_setting_check_tgbtn.isChecked()?1:3;
		param.u32Auth=mailbox_setting_safety_spn.getSelectedItemPosition();
		byte[] sendParam=HiChipDefines.HI_P2P_S_EMAIL_PARAM_EXT.parseContent(param, check?1:0);

		mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_EMAIL_PARAM_EXT, sendParam);
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

					case HiChipDefines.HI_P2P_GET_EMAIL_PARAM:
						
						param=new HiChipDefines.HI_P2P_S_EMAIL_PARAM(data);
						mailbox_setting_server_edt.setText(Packet.getString(param.strSvr));
						mailbox_setting_port_edt.setText(String.valueOf(param.u32Port));
						mailbox_setting_username_edt.setText(Packet.getString(param.strUsernm));
						mailbox_setting_psw_edt.setText(Packet.getString(param.strPasswd));
						mailbox_setting_receive_address_edt.setText(Packet.getString(param.strTo[0]));
						mailbox_setting_sending_address_edt.setText(Packet.getString(param.strFrom));
						mailbox_setting_theme_edt.setText(Packet.getString(param.strSubject));
						mailbox_setting_message_edt.setText(Packet.getString(param.strText));
						if(param.u32LoginType==1){
							mailbox_setting_check_tgbtn.setChecked(true);
						}else if(param.u32LoginType==3){
							mailbox_setting_check_tgbtn.setChecked(false);
						}

						mailbox_setting_safety_spn.setSelection(param.u32Auth);

						break;

					case HiChipDefines.HI_P2P_SET_EMAIL_PARAM_EXT:
						if(!isCheck){
						HiToast.showToast(EmailSettingActivity.this, 
								getResources().getString(R.string.mailbox_setting_save_success));
						finish();
						}else{
							HiToast.showToast(EmailSettingActivity.this, 
									getResources().getString(R.string.mailbox_setting_check_success));
						}
						
						dismissLoadingProgress();
						
						break;

					}
				}else{
					switch (msg.arg1) {
					case HiChipDefines.HI_P2P_SET_EMAIL_PARAM_EXT:
						if(!isCheck){
						HiToast.showToast(EmailSettingActivity.this, 
								getResources().getString(R.string.mailbox_setting_save_failed));
						}else{
							HiToast.showToast(EmailSettingActivity.this, 
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

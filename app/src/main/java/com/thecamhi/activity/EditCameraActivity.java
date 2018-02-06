package com.thecamhi.activity;

import com.hichip.R;
import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.control.HiCamera;
import com.thecamhi.activity.setting.PasswordSettingActivity;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.TitleView;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;
import com.thecamhi.utils.EmojiFilter;
import com.thecamhi.utils.SpcialCharFilter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ToggleButton;



public class EditCameraActivity extends HiActivity implements TextWatcher {

	
	private MyCamera mCamera;
	
	private EditText edt_nikename;
	private EditText edt_uid;
	private EditText edt_username;
	private EditText edt_password;
//	private ToggleButton togbtn_delete_camera;
	
	
	private String strNikname;
	private String strUid;
	private String strUsername;
	private String strPassword;

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_camera);
		
		Bundle bundle = this.getIntent().getExtras();
		String uid = bundle.getString(HiDataValue.EXTRAS_KEY_UID);
		
		for(MyCamera camera: HiDataValue.CameraList) {
			if(camera.getUid().equals(uid)) {
				mCamera = camera;
				break;
			}
		}
		//注册摄像机状态和命令的回调
		initView();
	}

	private void initView() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		TitleView nb = (TitleView)findViewById(R.id.title_top);
		
		nb.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		nb.setButton(TitleView.NAVIGATION_BUTTON_RIGHT);
		nb.setTitle(getString(R.string.title_user_settting));
		nb.setRightBtnText(getString(R.string.finish));
		nb.setNavigationBarButtonListener(new TitleView.NavigationBarButtonListener() {
			
			@Override
			public void OnNavigationButtonClick(int which) {
				// TODO Auto-generated method stub
				switch(which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					EditCameraActivity.this.finish();
					break;
				case TitleView.NAVIGATION_BUTTON_RIGHT:
					chickDone();
					break;
				}
			}
		});
		
		
		edt_nikename = (EditText)findViewById(R.id.edt_nikename);
		edt_uid = (EditText)findViewById(R.id.edt_uid);
		edt_username = (EditText)findViewById(R.id.edt_username);
		//+++
		edt_username.setFilters(new InputFilter[]{new InputFilter.LengthFilter(31),new EmojiFilter(),new SpcialCharFilter()});
		edt_password = (EditText)findViewById(R.id.edt_password);
		edt_password.setFilters(new InputFilter[]{new InputFilter.LengthFilter(31),new EmojiFilter(),new SpcialCharFilter()});
		edt_password.addTextChangedListener(this);
		
		if(mCamera.mIsConnect){
			edt_username.setEnabled(false);
			edt_password.setEnabled(false);
		}else {
			edt_nikename.setEnabled(true);
			edt_password.setEnabled(true);
		}
		
		strNikname = mCamera.getNikeName();
		strUid = mCamera.getUid();
		strUsername = mCamera.getUsername();
		strPassword = mCamera.getPassword();
		
		edt_nikename.setText(strNikname);
		edt_uid.setText(strUid);
		edt_username.setText(strUsername);
		edt_password.setText(strPassword);
		
	/*	togbtn_delete_camera = (ToggleButton)findViewById(R.id.togbtn_delete_camera);
		togbtn_delete_camera.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteCamera();
			}
		});*/
		
	}

	private void chickDone() {
		String str_nike = edt_nikename.getText().toString();
		String str_uid = edt_uid.getText().toString().trim();
		String str_password = edt_password.getText().toString().trim();
		String str_username = edt_username.getText().toString();
		
		if (str_nike.length() == 0) {
			showAlert(getText(R.string.tips_null_nike));
			return;
		}

		if (str_uid.length() == 0) {
			showAlert(getText(R.string.tips_null_uid));
			return;
		}
		
		if(str_username.length()== 0 ) {
			showAlert(getText(R.string.tips_null_username));
			return;
		}
		
		
		strNikname = mCamera.getNikeName();
		strUid = mCamera.getUid();
		strUsername = mCamera.getUsername();
		strPassword = mCamera.getPassword();
		
//		HiLog.v(
//		"   strUid:"+strUid
//		+"   strUsername:"+strUsername
//		+"   strPassword:"+strPassword
//		+"   str_uid:"+str_uid
//		+"   str_username:"+str_username
//		+"   str_password:"+str_password
//		);
		if(!strNikname.equals(str_nike)) {
			mCamera.setNikeName(str_nike);
		}
		if( !strUid.equals(str_uid) || !strUsername.equals(str_username)|| !strPassword.equals(str_password)) {
			mCamera.setUid(str_uid);
			mCamera.setPassword(str_password);
			mCamera.setUsername(str_username);
			
			mCamera.disconnect();
			mCamera.connect();
		}		
		
		mCamera.updateInDatabase(this);
		
		Intent intent = new Intent();
		intent.setAction(HiDataValue.ACTION_CAMERA_INIT_END);
		sendBroadcast(intent);
		
		this.finish();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		if(s.toString().length()>=31){
			HiToast.showToast(EditCameraActivity.this, getString(R.string.tip_password_limit));
		}
	}
	
	
/*	private void deleteCamera() {
		new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getText(R.string.tips_warning)).setMessage(getText(R.string.tips_msg_delete_camera)).setPositiveButton(getText(R.string.btn_yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				mCamera.disconnect();
				mCamera.deleteInCameraList();
				mCamera.deleteInDatabase(EditCameraActivity.this);
				
				Intent intent = new Intent();
				intent.setAction(HiDataValue.ACTION_CAMERA_INIT_END);
				sendBroadcast(intent);
				EditCameraActivity.this.finish();
			}
		}).setNegativeButton(getText(R.string.btn_no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				togbtn_delete_camera.setChecked(false);
			}
		}).show();
	
	}*/

}

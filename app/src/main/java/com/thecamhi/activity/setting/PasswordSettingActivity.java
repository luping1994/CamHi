package com.thecamhi.activity.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.hichip.R;
import com.hichip.base.HiLog;
import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.content.HiChipDefines;
import com.hichip.control.HiCamera;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.TitleView;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;

public class PasswordSettingActivity extends HiActivity implements ICameraIOSessionCallback, TextWatcher{
	private MyCamera mCamera = null;
	private EditText edt_current_password;
	private EditText edt_new_password;
	private EditText edt_confirm_password;
	
	private String newPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_password);
		
		String uid  =getIntent().getStringExtra(HiDataValue.EXTRAS_KEY_UID);
		

		for (MyCamera camera : HiDataValue.CameraList) {
			if (uid.equals(camera.getUid())) {
				mCamera = camera;

				break;
				
			}
		}
		
		initView();
	}
	
	private void initView() {
		TitleView nb = (TitleView)findViewById(R.id.title_top);
		
		nb.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		nb.setTitle(getString(R.string.title_modify_password));
		nb.setNavigationBarButtonListener(new TitleView.NavigationBarButtonListener() {
			
			@Override
			public void OnNavigationButtonClick(int which) {
				// TODO Auto-generated method stub
				switch(which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					PasswordSettingActivity.this.finish();
					break;
			
				}
			}
		});
		
		
		edt_current_password = (EditText)findViewById(R.id.edt_current_password);
		edt_new_password = (EditText)findViewById(R.id.edt_new_password);
		edt_confirm_password = (EditText)findViewById(R.id.edt_confirm_password);
		
		edt_current_password.addTextChangedListener(this);
		edt_new_password.addTextChangedListener(this);
		edt_confirm_password.addTextChangedListener(this);
		
		CheckBox show_psw_cb=(CheckBox)findViewById(R.id.show_psw_cb);

		show_psw_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton btn, boolean check) {
				if(check){
					//显示密码
					edt_current_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					edt_new_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					edt_confirm_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				}else{
					edt_current_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
					edt_new_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
					edt_confirm_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
					
				}
			}
		});
		
		Button update_password_btn=(Button)findViewById(R.id.update_password_btn);
		update_password_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updatePassword();
				
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

	@Override
	public void receiveSessionState(HiCamera arg0, int arg1) {

	}    

	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case HiDataValue.HANDLE_MESSAGE_RECEIVE_IOCTRL:
			{
				if(msg.arg2==0) {
//					MyCamera camera = (MyCamera)msg.obj;
//					Bundle bundle = msg.getData();
//					byte[] data = bundle.getByteArray(HiDataValue.EXTRAS_KEY_DATA);
					switch (msg.arg1) {
					
					case HiChipDefines.HI_P2P_SET_USER_PARAM:
						
						mCamera.setPassword(newPassword);
						mCamera.updateInDatabase(PasswordSettingActivity.this);
						
						mCamera.disconnect();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								mCamera.connect();
							}
						}, 1000);
						
						dismissLoadingProgress();
						
						HiLog.e("     newPassword:"+newPassword);
						
						finish();
						Toast.makeText(PasswordSettingActivity.this, getString(R.string.tips_modify_security_code_ok), Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Toast.makeText(PasswordSettingActivity.this,"failed", Toast.LENGTH_SHORT).show();
					dismissLoadingProgress();
				}
			}
				break;
			}
		}
	};
	
	
	public void updatePassword(){
		String oldPwd = edt_current_password.getText().toString();
		newPassword = edt_new_password.getText().toString();
		String confirmPwd = edt_confirm_password.getText().toString();

		
		
		if(newPassword.contains("~") || newPassword.contains("=") || newPassword.contains("&") || newPassword.contains("\\") 
				 ||newPassword.contains("\"") || newPassword.contains(" ")||newPassword.contains("+")) { 
			
			Toast.makeText(PasswordSettingActivity.this, getText(R.string.tips_password_wrong_contains).toString(), Toast.LENGTH_SHORT).show();
			return;
		}

		if (!oldPwd.equalsIgnoreCase(mCamera.getPassword())) {
			Toast.makeText(PasswordSettingActivity.this, getText(R.string.tips_old_password_is_wrong).toString(), Toast.LENGTH_SHORT).show();
			return;
		}
		if (!newPassword.equalsIgnoreCase(confirmPwd)) {
			Toast.makeText(PasswordSettingActivity.this, getText(R.string.tips_new_passwords_do_not_match).toString(), Toast.LENGTH_SHORT).show();
			return;
		}
		if (mCamera != null) {
			byte[] old_auth = HiChipDefines.HI_P2P_S_AUTH.parseContent(0, mCamera.getUsername(), mCamera.getPassword());
			byte[] new_auth = HiChipDefines.HI_P2P_S_AUTH.parseContent(0, mCamera.getUsername(), newPassword);
			mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_USER_PARAM, HiChipDefines.HI_P2P_SET_AUTH.parseContent(new_auth, old_auth));
		}
		
		
		showLoadingProgress();
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
    /**
     * 单个EditText的监听
     */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		if(s.toString().length()>=31){
			HiToast.showToast(PasswordSettingActivity.this, getString(R.string.tip_password_limit));
		}
		
	}

}

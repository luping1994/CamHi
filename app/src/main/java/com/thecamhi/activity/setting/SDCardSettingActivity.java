package com.thecamhi.activity.setting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class SDCardSettingActivity extends HiActivity implements ICameraIOSessionCallback{
	private MyCamera mCamera;
	private TextView available_space_size,storage_space_size;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sd_card_setting);

		String uid = getIntent().getStringExtra(HiDataValue.EXTRAS_KEY_UID);
		for (MyCamera camera : HiDataValue.CameraList) {
			if (uid.equals(camera.getUid())) {
				mCamera = camera;
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_SD_INFO, new byte[0]);
				break;

			}
		}

		initView();
	}

	private void initView() {
		TitleView title=(TitleView)findViewById(R.id.title_top);
		title.setTitle(getResources().getString(R.string.title_sdcard_setting));
		title.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		title.setNavigationBarButtonListener(new NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				switch (which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					SDCardSettingActivity.this.finish();
					break;
				}
			}
		});

		available_space_size=(TextView)findViewById(R.id.available_space_size);
		storage_space_size=(TextView)findViewById(R.id.storage_space_size);

		Button format_sd_card=(Button)findViewById(R.id.format_sd_card);
		format_sd_card.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				formatSDCard();

			}
		});
	}

	private void formatSDCard() {

		showYesNoDialog(R.string.tips_format_sd_card, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// Yes button clicked
					mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_FORMAT_SD,null);
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					// No button clicked

					break;
				}
			}
		});
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


					case HiChipDefines.HI_P2P_GET_SD_INFO:
					{
						HiChipDefines.HI_P2P_S_SD_INFO sd_info = new HiChipDefines.HI_P2P_S_SD_INFO(data); 

						int used = sd_info.u32Space - sd_info.u32LeftSpace;
						available_space_size.setText(String.valueOf(sd_info.u32LeftSpace/1024) + " MB");
						storage_space_size.setText(String.valueOf(sd_info.u32Space/1024) + " MB");
					}

					break;
					case HiChipDefines.HI_P2P_SET_FORMAT_SD:
					{
						dismissLoadingProgress();
						HiToast.showToast(SDCardSettingActivity.this, getString(R.string.tips_format_sd_card_success));
						finish();
					}
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

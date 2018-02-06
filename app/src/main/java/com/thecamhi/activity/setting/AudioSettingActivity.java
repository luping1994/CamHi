package com.thecamhi.activity.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.hichip.R;
import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.content.HiChipDefines;
import com.hichip.control.HiCamera;
import com.hichip.data.HiDeviceInfo;
import com.hichip.sdk.HiChipP2P;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.TitleView;
import com.thecamhi.base.TitleView.NavigationBarButtonListener;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;

public class AudioSettingActivity extends HiActivity implements ICameraIOSessionCallback{

	private MyCamera mCamera = null;
	private SeekBar seekbar_audio_input,seekbar_audio_output;
	private int maxInputValue = 100;
	private int maxOutputValue = 100;
	private TextView txt_audio_output_value,txt_audio_input_value;
	private HiChipDefines.HI_P2P_S_AUDIO_ATTR audio_attr;
	private Spinner spn_input_style;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_setting);
		String uid = getIntent().getStringExtra(HiDataValue.EXTRAS_KEY_UID);

		for (MyCamera camera : HiDataValue.CameraList) {
			if (uid.equals(camera.getUid())) {
				mCamera = camera;
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_AUDIO_ATTR, null);
				break;
			}
		}
		
		initView();
	}

	private void initView() {
		TitleView title=(TitleView)findViewById(R.id.title_top);
		title.setTitle(getResources().getString(R.string.title_audio_setup));
		title.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		title.setNavigationBarButtonListener(new NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				switch (which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					AudioSettingActivity.this.finish();
					break;


				}

			}
		});
		spn_input_style=(Spinner)findViewById(R.id.spn_input_style);
		ArrayAdapter<CharSequence> adapter_sequent=ArrayAdapter.createFromResource(this, R.array.audio_input_style, android.R.layout.simple_spinner_item);
		adapter_sequent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn_input_style.setAdapter(adapter_sequent);
		
		txt_audio_output_value=(TextView)findViewById(R.id.txt_audio_output_value);
		txt_audio_input_value=(TextView)findViewById(R.id.txt_audio_input_value);

		seekbar_audio_input=(SeekBar)findViewById(R.id.seekbar_audio_input);
		
		if(mCamera.getChipVersion() == HiDeviceInfo.CHIP_VERSION_GOKE){
			maxInputValue=16;
			maxOutputValue=13;
		}

		seekbar_audio_input.setMax(maxInputValue - 1);
		seekbar_audio_input.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub

				txt_audio_input_value.setText(String.valueOf(progress+1));
			}
		});

		seekbar_audio_output=(SeekBar)findViewById(R.id.seekbar_audio_output);
		seekbar_audio_output.setMax(maxOutputValue - 1);
		seekbar_audio_output.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub

				txt_audio_output_value.setText(String.valueOf(progress+1));
			}
		});
		
		Button audio_application_btn=(Button)findViewById(R.id.audio_application_btn);
		audio_application_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(audio_attr == null)
					return;
				
				int invol = seekbar_audio_input.getProgress() + 1;
				int outvol = seekbar_audio_output.getProgress() + 1;
				int inmode = spn_input_style.getSelectedItemPosition();
				
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_AUDIO_ATTR, HiChipDefines.HI_P2P_S_AUDIO_ATTR.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN,audio_attr.u32Enable, audio_attr.u32Stream, audio_attr.u32AudioType, inmode, invol, outvol));
				
				
				
			
					
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

					case HiChipDefines.HI_P2P_GET_AUDIO_ATTR:
						audio_attr = new HiChipDefines.HI_P2P_S_AUDIO_ATTR(data);
						seekbar_audio_input.setProgress(audio_attr.u32InVol - 1);
						seekbar_audio_output.setProgress(audio_attr.u32OutVol - 1);
						
						//0：线性输入1：麦克输入
						if(audio_attr.u32InMode == 0) {
							spn_input_style.setSelection(0);
						}
						else if(audio_attr.u32InMode == 1) {
							spn_input_style.setSelection(1);
						}

						dismissLoadingProgress();
						break;
					case HiChipDefines.HI_P2P_SET_AUDIO_ATTR:
						
						HiToast.showToast(AudioSettingActivity.this,getString(R.string.tips_audio_setting));
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

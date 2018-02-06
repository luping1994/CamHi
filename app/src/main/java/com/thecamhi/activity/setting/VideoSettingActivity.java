package com.thecamhi.activity.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hichip.R;
import com.hichip.base.HiLog;
import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.content.HiChipDefines;
import com.hichip.control.HiCamera;
import com.hichip.sdk.HiChipP2P;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.TitleView;
import com.thecamhi.base.TitleView.NavigationBarButtonListener;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;

public class VideoSettingActivity extends HiActivity implements ICameraIOSessionCallback{

	private MyCamera mCamera;
	private HiChipDefines.HI_P2P_CODING_PARAM coding_param;
	private HiChipDefines.HI_P2P_S_VIDEO_PARAM video_param_hd;
	private HiChipDefines.HI_P2P_S_VIDEO_PARAM video_param_sd;
	private EditText first_code_rate_et,first_frame_rate_et,first_video_level_et,
	second_code_rate_et,second_frame_rate_et,second_video_level_et;
	private Button  video_setting_application_btn;
	private Spinner video_format_spinner;
	private TextView first_frame_rate_range,second_frame_rate_range ;
	
	
	public static int FRAME_RATE_LOW=25; 
	public static int FRAME_RATE_HIGH=30; 
	public int maxFrameRate=FRAME_RATE_LOW;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_setting);

		String uid=getIntent().getStringExtra(HiDataValue.EXTRAS_KEY_UID);


		for (MyCamera camera : HiDataValue.CameraList) {
			if (uid.equals(camera.getUid())) {
				mCamera = camera;
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_VIDEO_PARAM, HiChipDefines.HI_P2P_S_VIDEO_PARAM.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN,HiChipDefines.HI_P2P_STREAM_1,0,0,0,0,0));
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_VIDEO_PARAM, HiChipDefines.HI_P2P_S_VIDEO_PARAM.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN,HiChipDefines.HI_P2P_STREAM_2,0,0,0,0,0));
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_VIDEO_CODE, new byte[0]);

				break;

			}
		}

		initView();
		showLoadingProgress();
	}

	public void setVideoInfo(){
		if(coding_param == null || video_param_hd == null || video_param_sd == null) {
			return;
		}

		int code_rate_first = 0;
		int code_rate_second = 0;
		int frame_rate_first = 0;
		int frame_rate_second = 0;
		int video_level_first=0;
		int video_level_second=0;
		int frequency = 0;



		String first_code_rate_str=first_code_rate_et.getText().toString().trim();
		String second_code_rate_str=second_code_rate_et.getText().toString().trim();
		String first_frame_rate_str = first_frame_rate_et.getText().toString().trim();
		String second_frame_rate_str = second_frame_rate_et.getText().toString().trim();
		String first_str_level_str=first_video_level_et.getText().toString().trim();
		String second_str_level_str=second_video_level_et.getText().toString().trim();


		if(first_frame_rate_str!=null && first_frame_rate_str.length()>0) {
			frame_rate_first = Integer.valueOf(first_frame_rate_str);
		}

		if(second_frame_rate_str!=null && second_frame_rate_str.length()>0) {
			frame_rate_second = Integer.valueOf(second_frame_rate_str);
		}

		if(first_code_rate_str!=null && first_code_rate_str.length()>0) {
			code_rate_first = Integer.valueOf(first_code_rate_str);
		}

		if(second_code_rate_str!=null && second_code_rate_str.length()>0) {
			code_rate_second = Integer.valueOf(second_code_rate_str);
		}





		int max_rate_first=6144;
		if(code_rate_first < 32 || code_rate_first >max_rate_first) {
			HiToast.showToast(VideoSettingActivity.this, getText(R.string.first_tips_code_rate_range).toString());
			return;
		}
		int max_rate_second=2048;

		if(code_rate_second<32||code_rate_second>max_rate_second) {
			HiToast.showToast(VideoSettingActivity.this, getText(R.string.second_tips_code_rate_range).toString());
			return;
		}

		
		String rangStr=getResources().getString(R.string.tips_frame_rate_range);
		String rangTips=String.format(rangStr, maxFrameRate);
		if(frame_rate_first < 1 || frame_rate_first > maxFrameRate) {
			
			HiToast.showToast(VideoSettingActivity.this, rangTips);
			return;
		}
		if(frame_rate_second<1||frame_rate_second>maxFrameRate) {
			
			HiToast.showToast(VideoSettingActivity.this, rangTips);
			return;
		}



		if(first_str_level_str!=null && first_str_level_str.length()>0) {
			video_level_first = Integer.valueOf(first_str_level_str);
		}

		if(second_str_level_str!=null && second_str_level_str.length()>0) {
			video_level_second = Integer.valueOf(second_str_level_str);
		}



		int max_level=6;
		if(video_level_first < 1 || video_level_first > max_level) {
			HiToast.showToast(VideoSettingActivity.this, getText(R.string.tips_video_level_range).toString());
			return;
		}
		if(video_level_second<1||video_level_second>max_level) {
			HiToast.showToast(VideoSettingActivity.this, getText(R.string.tips_video_level_range).toString());
			return;
		}




		video_param_hd.u32BitRate =Integer.valueOf(first_code_rate_et.getText().toString());
		video_param_sd.u32BitRate =  Integer.valueOf(second_code_rate_et.getText().toString());


		video_param_hd.u32Frame = frame_rate_first;
		video_param_sd.u32Frame = frame_rate_second;

		video_param_hd.u32Quality=Integer.valueOf(first_str_level_str);
		video_param_sd.u32Quality=Integer.valueOf(second_str_level_str);



		frequency = video_format_spinner.getSelectedItemPosition() == 0?50:60;
		coding_param.u32Frequency = frequency;


		mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_VIDEO_PARAM, HiChipDefines.HI_P2P_S_VIDEO_PARAM.
				parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN,video_param_hd.u32Stream,video_param_hd.u32Cbr,
						video_param_hd.u32Frame,video_param_hd.u32BitRate,video_param_hd.u32Quality,
						video_param_hd.u32Frame*2));
		mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_VIDEO_PARAM, HiChipDefines.HI_P2P_S_VIDEO_PARAM.
				parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN,video_param_sd.u32Stream,video_param_sd.u32Cbr,
						video_param_sd.u32Frame,video_param_sd.u32BitRate,video_param_sd.u32Quality,
						video_param_sd.u32Frame*2));
		mCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_VIDEO_CODE, HiChipDefines.HI_P2P_CODING_PARAM.
				parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN, coding_param.u32Frequency, coding_param.u32Profile));

		HiLog.e("video_param_hd.u32Stream:"+video_param_hd.u32Stream+" video_param_hd.u32Cbr:"+video_param_hd.u32Cbr+
				" video_param_hd.u32Frame:"+video_param_hd.u32Frame+" video_param_hd.u32BitRate:"+video_param_hd.u32BitRate+
				" video_param_hd.u32Quality:"+video_param_hd.u32Quality+" video_param_hd.u32Stream:"+video_param_hd.u32Stream

				);

		
	}



	private void initView() {
		TitleView title=(TitleView)findViewById(R.id.title_top);
		title.setTitle(getResources().getString(R.string.title_video_settings));
		title.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		title.setNavigationBarButtonListener(new NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				switch (which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					VideoSettingActivity.this.finish();
					break;
				}
			}
		});

		first_code_rate_et=(EditText)findViewById(R.id.first_code_rate_et);
		first_frame_rate_et=(EditText)findViewById(R.id.first_frame_rate_et);
		first_video_level_et=(EditText)findViewById(R.id.first_video_level_et);
		second_code_rate_et=(EditText)findViewById(R.id.second_code_rate_et);
		second_frame_rate_et=(EditText)findViewById(R.id.second_frame_rate_et);
		second_video_level_et=(EditText)findViewById(R.id.second_video_level_et);

		first_frame_rate_range=(TextView)findViewById(R.id.first_frame_rate_range);
		second_frame_rate_range=(TextView)findViewById(R.id.second_frame_rate_range);
	
		
		video_format_spinner=(Spinner)findViewById(R.id.video_format_spinner);
		ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.video_frequency, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		video_format_spinner.setAdapter(adapter);
		
		video_format_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if(position==0){
					maxFrameRate=FRAME_RATE_LOW;
				}else{
					maxFrameRate=FRAME_RATE_HIGH;
				}
				ChangedFrameRange(maxFrameRate);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		

		video_setting_application_btn=(Button)findViewById(R.id.video_setting_application_btn);
		video_setting_application_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setVideoInfo();

			}
		});
	}
	
	private void ChangedFrameRange(int maxFrame){
		
		String strRange=getResources().getString(R.string.range_video_setting_frame_rate);
		String summary = String.format(strRange, maxFrame);
		
		first_frame_rate_range.setText(summary);
		second_frame_rate_range.setText(summary);
		
		
	}
	
	private int getBitRateValue(int stream,int level) {

		if(stream == HiChipDefines.HI_P2P_STREAM_1) {
			if(level == 0) {
				return 6144;
			}
			else if(level == 1) {
				return 3072;
			}
			else if(level == 2) {
				return 2048;
			}
			else if(level == 3) {
				return 1024;
			}
			else if(level == 4) {
				return 512;
			}
		}
		else if(stream == HiChipDefines.HI_P2P_STREAM_2) {
			if(level == 0) {
				return 2048;
			}
			else if(level == 1) {
				return 1024;
			}
			else if(level == 2) {
				return 512;
			}
			else if(level == 3) {
				return 256;
			}
			else if(level == 4) {
				return 128;
			}
		}

		return 0;
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

					case HiChipDefines.HI_P2P_GET_VIDEO_CODE:
						coding_param = new HiChipDefines.HI_P2P_CODING_PARAM(data);

						if(coding_param.u32Frequency == 50){
							video_format_spinner.setSelection(0);
							maxFrameRate=FRAME_RATE_LOW;
						}
						else if(coding_param.u32Frequency == 60) {
							video_format_spinner.setSelection(1);
							maxFrameRate=FRAME_RATE_HIGH;
						}
						ChangedFrameRange(maxFrameRate);
						
						
						dismissLoadingProgress();
						break;
					case HiChipDefines.HI_P2P_GET_VIDEO_PARAM:
						HiChipDefines.HI_P2P_S_VIDEO_PARAM video_param = new HiChipDefines.HI_P2P_S_VIDEO_PARAM(data);
						if	(video_param.u32Stream == HiChipDefines.HI_P2P_STREAM_1) {
							video_param_hd = video_param;
							first_code_rate_et.setText(video_param_hd.u32BitRate+"");
							first_frame_rate_et.setText(video_param_hd.u32Frame+"");
							first_video_level_et.setText(video_param_hd.u32Quality+"");
						}
						else if	(video_param.u32Stream == HiChipDefines.HI_P2P_STREAM_2) {
							video_param_sd = video_param;
							second_code_rate_et.setText(video_param_sd.u32BitRate+"");
							second_frame_rate_et.setText(video_param_sd.u32Frame+"");
							second_video_level_et.setText(video_param_sd.u32Quality+"");

						}
						break;
					case HiChipDefines.HI_P2P_SET_VIDEO_PARAM:
						HiToast.showToast(VideoSettingActivity.this, getString(R.string.tips_video_setting));
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

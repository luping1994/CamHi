package com.thecamhi.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hichip.R;
import com.hichip.base.HiLog;
import com.hichip.tools.HiSinVoiceData;
import com.hichip.tools.HiSmartWifiSet;
import com.thecamhi.base.HiTools;
import com.thecamhi.base.TitleView;
import com.thecamhi.base.TitleView.NavigationBarButtonListener;
import com.thecamhi.main.HiActivity;

public class WifiOneKeySettingActivity extends HiActivity{
	private EditText wifi_ssid_et;
	private EditText psw_wifi_et;
	private Button setting_wifi_btn;
	private SeekBar prs_loading;
	private TextView	wifi_rate ,wifi_rate_2;
//	private PopupWindow mPopupWindow;
	private int cur=0;
	private String ssid;
	private HiSinVoiceData sv;
	private boolean noice=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_set_one_key);

		sv = new HiSinVoiceData(WifiOneKeySettingActivity.this);

		initView();

	}

	private String getInfo()  
	{  
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);  
		WifiInfo info = wifi.getConnectionInfo();  

		 ssid = info.getSSID().trim(); 
		if(ssid!=null&&ssid.length()>1){
			ssid=ssid.substring(1, ssid.length()-1);
		}
		return ssid;
	}  



	private void initView() {
		TitleView title_top=(TitleView)
				findViewById(R.id.title_top);
		title_top.setTitle(getString(R.string.one_key_setting_wifi));
		title_top.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		title_top.setNavigationBarButtonListener(new NavigationBarButtonListener() {
			@Override
			public void OnNavigationButtonClick(int which) {
				finish();
			}
		});

		wifi_ssid_et=(EditText)findViewById(R.id.wifi_ssid_et);
		psw_wifi_et=(EditText)findViewById(R.id.psw_wifi_et);
		setting_wifi_btn=(Button)findViewById(R.id.setting_wifi_btn);

		String uuid=getInfo();

		if(uuid!=null){
			wifi_ssid_et.setText(uuid);
		}

		psw_wifi_et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

		final byte byt = 0;
		Button setting_wifi_btn=(Button)findViewById(R.id.setting_wifi_btn);
		setting_wifi_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				seekBarDialog();
			}
		});
	}

	protected void seekBarDialog() {
		showYesNoDialog(R.string.tips_wifi_one_key_set,new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					cur=0;
					showingProgressDialog();
					
					//wifi“ªº¸≈‰÷√µƒapi
					HiSmartWifiSet.HiStartSmartConnection(ssid,psw_wifi_et.getText().toString(),(byte) 4);
					sv.setValue(ssid, psw_wifi_et.getText().toString());
					sv.startSinVoice();
					noice=true;
					
					HiLog.e("ssid="+ssid+"\npsw="+psw_wifi_et.getText().toString());
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					// No button clicked
					showAlertDialog();
					
					break;
				}
			}
		});

	}
	
	private void showAlertDialog(){
		AlertDialog alertDialog = new AlertDialog.Builder(this)
		.setTitle(getString(R.string.tips_warning))
		.setMessage(getString(R.string.tips_long_press_preset_btn))
		.setNegativeButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
			
			@Override  
			public void onClick(DialogInterface dialog, int which) {  
			

			}  
		}).show();
		
	}
	
	protected void showingProgressDialog() {
		View customView = getLayoutInflater().inflate(R.layout.dialog_showing_progressbar,
				null, false);

		final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		final AlertDialog dlgBuilder = dlg.create();
		dlgBuilder.setView(customView);
		dlgBuilder.setTitle(null);
		
		dlgBuilder.setCanceledOnTouchOutside(false);
		
		/*mPopupWindow = new PopupWindow(customView);
		ColorDrawable cd = new ColorDrawable(-0000); 
		mPopupWindow.setBackgroundDrawable(cd);
		mPopupWindow.setOutsideTouchable(false);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);               
		mPopupWindow.setHeight(LayoutParams.MATCH_PARENT);    */

		dlgBuilder.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface d) {
				
				HiSmartWifiSet.HiStopSmartConnection();
				if(noice==true){
					sv.stopSinVoice();
					noice=false;
				}
				if(timer!=null){
					timer.cancel();
					cur=0;

				}
			}
		});
		
		
		// height = 80

		int offsetx = HiTools.dip2px(this, 0);
		int location[] = new int[2];

		int btnh = HiTools.dip2px(this, 50 + 80/2);


		prs_loading = (SeekBar)customView.findViewById(R.id.wifi_progressbar);
		prs_loading.setEnabled(false);
		wifi_rate=(TextView)customView.findViewById(R.id.wifi_rate);
		wifi_rate_2=(TextView)customView.findViewById(R.id.wifi_rate_2);

	/*	CheckBox cb_wifi_one_key=(CheckBox)customView.findViewById(R.id.cb_wifi_one_key);
		cb_wifi_one_key.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton btn, boolean b) {
				if(b){
					noice=false;
					sv.stopSinVoice();
				}else{
					noice=true;
					sv.startSinVoice();
				}
			}
		});*/



		dlgBuilder.show();	
		showingProgress();

	}

	private void showingProgress() {
		prs_loading.setMax(100);
		prs_loading.setProgress(0);

		timer.start();

	}
	
	CountDownTimer timer = new CountDownTimer(100000, 1000) {  

		@Override  
		public void onTick(long millisUntilFinished) {  
			cur+=1;
			prs_loading.setProgress(cur);
			wifi_rate.setText(cur+"%");
			wifi_rate_2.setText(cur+"/100");
		}  

		@Override 
		public void onFinish() {  

			handler.sendEmptyMessage(0);
		}  
	};   


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			HiSmartWifiSet.HiStopSmartConnection();
			if(noice==true){
				noice=false;
				sv.stopSinVoice();
			}
			Intent intent=new Intent();
			setResult(RESULT_OK, intent);
			finish();
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		HiSmartWifiSet.HiStopSmartConnection();
		if(noice==true){
			noice=false;
			sv.stopSinVoice();
		}
	};

}

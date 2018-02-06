package com.thecamhi.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hichip.R;
import com.hichip.base.HiLog;
import com.hichip.tools.HiSearchSDK;
import com.hichip.tools.HiSearchSDK.HiSearchResult;
import com.thecamhi.base.A2bigA;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.TitleView;
import com.thecamhi.base.TitleView.NavigationBarButtonListener;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;
import com.thecamhi.utils.EmojiFilter;
import com.thecamhi.utils.SpcialCharFilter;
import com.thecamhi.zxing.QRCodeCaptureActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddCameraActivity extends HiActivity implements OnClickListener {
	private final static int REQUEST_CODE_SCAN_RESULT = 0;
	private final static int REQUEST_SCANNIN_GREQUEST_CODE = 1;
	private final static int REQUEST_WIFI_CODE = 2;
	private final static int REQUEST_SEARCH_CAMERA_IN_WIFI = 3;
	// private ScanResultAdapter adapter;
	private EditText add_camera_uid_edt, add_camera_name_et, add_camera_username_et, add_camera_psw_et;
	private HiSearchSDK searchSDK;
	private List<HiSearchResult> list = new ArrayList<HiSearchResult>();
	private MyCamera camera;

	private boolean isSearch;// 用于记录是否正在搜索的状态
	private boolean isShow = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_camera_view);
		initView();
	}

	private void initView() {
		TitleView title = (TitleView) findViewById(R.id.title_top);
		title.setTitle(getResources().getString(R.string.add_camera));
		title.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		title.setButton(TitleView.NAVIGATION_BUTTON_RIGHT);
		title.setRightBtnText(getResources().getString(R.string.finish));
		title.setNavigationBarButtonListener(new NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				switch (which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					AddCameraActivity.this.finish();
					break;
				case TitleView.NAVIGATION_BUTTON_RIGHT:
					chickDone();
					break;

				}
			}
		});

		// ?????
		LinearLayout scanner_QRcode_ll = (LinearLayout) findViewById(R.id.scanner_QRcode_ll);
		scanner_QRcode_ll.setOnClickListener(this);
		// ?????
		LinearLayout search_in_lan_ll = (LinearLayout) findViewById(R.id.search_in_lan_ll);
		search_in_lan_ll.setOnClickListener(this);
		// wifi????
		LinearLayout one_key_setting_wifi_ll = (LinearLayout) findViewById(R.id.one_key_setting_wifi_ll);
		one_key_setting_wifi_ll.setOnClickListener(this);

		add_camera_name_et = (EditText) findViewById(R.id.add_camera_name_et);
		add_camera_username_et = (EditText) findViewById(R.id.add_camera_username_et);
		//+++
		add_camera_username_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(31),new SpcialCharFilter(),new EmojiFilter()});
		add_camera_uid_edt = (EditText) findViewById(R.id.add_camera_uid_edt);
		add_camera_uid_edt.setTransformationMethod(new A2bigA());
		add_camera_psw_et = (EditText) findViewById(R.id.add_camera_psw_et);
		add_camera_psw_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(31),new EmojiFilter(),new SpcialCharFilter()});

		setOnLoadingProgressDismissListener(new MyDismiss() {

			@Override
			public void OnDismiss() {
				isSearch = false;
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			// ???????
			case REQUEST_SCANNIN_GREQUEST_CODE: {
				Bundle extras = data.getExtras();
				String uid = extras.getString(HiDataValue.EXTRAS_KEY_UID).trim();

				add_camera_uid_edt.setText(uid.toUpperCase());
			}
				break;
			// wifi???????
			case REQUEST_WIFI_CODE:

				// showLoadingProgress();
				isSearch = true;
				Intent intent = new Intent();
				intent.setClass(AddCameraActivity.this, SearchCameraActivity.class);

				startActivityForResult(intent, REQUEST_SEARCH_CAMERA_IN_WIFI);
				// initSDK();
				break;
			case REQUEST_SEARCH_CAMERA_IN_WIFI: {
				Bundle extras = data.getExtras();
				String uid = extras.getString(HiDataValue.EXTRAS_KEY_UID).trim();

				add_camera_uid_edt.setText(uid.toUpperCase());
			}
				break;

			default:

				break;
			}
		}

	}

	private boolean checkPermission(String permission) {

		int checkCallPhonePermission = ContextCompat.checkSelfPermission(AddCameraActivity.this, permission);
		if (checkCallPhonePermission == PackageManager.PERMISSION_GRANTED) {
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.scanner_QRcode_ll: {
			// ???????android 6??,???,??????CAMERA,FLASHLIGHT?? ,?????????
			if (HiDataValue.ANDROID_VERSION >= 6 && (!checkPermission(Manifest.permission.CAMERA)
					|| !checkPermission(Manifest.permission.FLASHLIGHT))) {
				Toast.makeText(AddCameraActivity.this, getString(R.string.tips_no_permission), Toast.LENGTH_SHORT)
						.show();
				return;
			}

			// ????
			Intent intent = new Intent();
			intent.setClass(AddCameraActivity.this, QRCodeCaptureActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, REQUEST_SCANNIN_GREQUEST_CODE);
		}
			break;
		case R.id.search_in_lan_ll: {
			/*
			 * //?????UID isSearch=true; showLoadingProgress(); initSDK();
			 */
			Intent intent = new Intent();
			intent.setClass(AddCameraActivity.this, SearchCameraActivity.class);

			startActivityForResult(intent, REQUEST_SEARCH_CAMERA_IN_WIFI);
		}
			break;
		case R.id.one_key_setting_wifi_ll: {
			if (isWifiConnected(AddCameraActivity.this)) {
				Intent intent = new Intent(AddCameraActivity.this, WifiOneKeySettingActivity.class);
				startActivityForResult(intent, REQUEST_WIFI_CODE);
			} else {
				HiToast.showToast(AddCameraActivity.this, getString(R.string.connect_to_WIFI_first));
			}
		}

			break;

		}

	}

	// ??wifi????
	public boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected()) {
			return true;
		}

		return false;
	}

	/*
	 * private void initSDK() {
	 * 
	 * 
	 * 
	 * list.clear();
	 * 
	 * searchSDK= new HiSearchSDK(new HiSearchSDK.ISearchResult() {
	 * 
	 * @Override public void onReceiveSearchResult(HiSearchResult result) { //
	 * TODO Auto-generated method stub list.add(result); Message msg =
	 * handler.obtainMessage(); msg.what =
	 * HiDataValue.HANDLE_MESSAGE_SCAN_RESULT; handler.sendMessage(msg); } });
	 * searchSDK.search2();
	 * 
	 * 
	 * 
	 * 
	 * 
	 * //??searchSDK???? searchSDK = new HiSearchSDK(new
	 * HiSearchSDK.OnSearchResult() {
	 * 
	 * @Override public void searchResult(List<HiSearchResult> arg0) { // TODO
	 * Auto-generated method stub HiLog.e("list size:"+arg0.size()); list =
	 * arg0; Message msg = handler.obtainMessage(); msg.what =
	 * HiDataValue.HANDLE_MESSAGE_SCAN_RESULT; handler.sendMessage(msg);
	 * 
	 * } });
	 * 
	 * searchSDK.searchWithTime(6); }
	 */

	/*
	 * private Handler handler = new Handler() {
	 * 
	 * @Override public void handleMessage(Message msg) { switch(msg.what) {
	 * case HiDataValue.HANDLE_MESSAGE_SCAN_RESULT:
	 * 
	 * if(!isSearch) {
	 * 
	 * return; } dismissLoadingProgress();
	 * 
	 * if(isShow){ adapter.notifyDataSetChanged(); }else{
	 * showResultDialog(AddCameraActivity.this); }
	 * 
	 * 
	 * break;
	 * 
	 * } } };
	 */

	/*
	 * private void showResultDialog(Context context) {
	 * 
	 * 
	 * LayoutInflater layoutInflater = LayoutInflater.from(context); // final
	 * Dialog dialog = new Dialog(context); View popView =
	 * layoutInflater.inflate(R.layout.dialog_list_search_result, null); //
	 * dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //
	 * dialog.setContentView(popView);
	 * 
	 * LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(600, 400);
	 * popView.setLayoutParams(lp);
	 * 
	 * // dialog.setCancelable(true); ListView listView=(ListView)
	 * popView.findViewById(R.id.search_list_camera); adapter=new
	 * ScanResultAdapter(context); listView.setAdapter(adapter);
	 * 
	 * // dialog.show(); final AlertDialog alertDialog = new
	 * AlertDialog.Builder(this) .setTitle(R.string.search_in_lan)
	 * .setView(popView).setCancelable(true)
	 * .setNegativeButton(getResources().getString(R.string.refresh), new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * showLoadingProgress(); isShow=false; isSearch=true; initSDK();
	 * 
	 * } }).setPositiveButton(getResources().getString(R.string.cancel), new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * isShow=false; dialog.cancel(); } }).show(); isShow=true;
	 * dismissLoadingProgress();
	 * 
	 * listView.setOnItemClickListener(new OnItemClickListener() {
	 * 
	 * @Override public void onItemClick(AdapterView<?> arg0, View arg1, int
	 * position, long arg3) { HiSearchResult result=list.get(position);
	 * add_camera_uid_edt.setText(result.uid.toString()); alertDialog.dismiss();
	 * } });
	 * 
	 * }
	 */
	private void chickDone() {
		String str_nike = add_camera_name_et.getText().toString();
		String str_uid = add_camera_uid_edt.getText().toString().trim().toUpperCase();
		String str_password = add_camera_psw_et.getText().toString().trim();
		String str_username = add_camera_username_et.getText().toString();

		if (str_nike.length() == 0) {
			showAlert(getText(R.string.tips_null_nike));
			return;
		}

		if (str_uid.length() == 0) {
			showAlert(getText(R.string.tips_null_uid));
			return;
		}

		if (str_username.length() == 0) {
			showAlert(getText(R.string.tips_null_username));
			return;
		}

		for (int i = 0; i < HiDataValue.zifu.length; i++) {
			if (str_uid.contains(HiDataValue.zifu[i])) {
				Toast.makeText(AddCameraActivity.this, getText(R.string.tips_illegal_uid).toString(),
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

		for (MyCamera camera : HiDataValue.CameraList) {
			if (str_uid.equalsIgnoreCase(camera.getUid())) {
				showAlert(getText(R.string.tips_add_camera_exists));
				return;
			}
		}

		if (HiDataValue.CameraList != null && HiDataValue.CameraList.size() >= 64) {
			HiToast.showToast(AddCameraActivity.this, getString(R.string.tips_limit_add_camera));
			return;
		}

		camera = new MyCamera(str_nike, str_uid, str_username, str_password);
		HiLog.e("save begin");

		camera.saveInDatabase(this);
		camera.saveInCameraList();

		Intent broadcast = new Intent();
		broadcast.setAction(HiDataValue.ACTION_CAMERA_INIT_END);
		sendBroadcast(broadcast);

		Bundle extras = new Bundle();
		extras.putString(HiDataValue.EXTRAS_KEY_UID, str_uid);
		Intent intent = new Intent();
		intent.putExtras(extras);
		this.setResult(RESULT_OK, intent);
		this.finish();

	}
}
  



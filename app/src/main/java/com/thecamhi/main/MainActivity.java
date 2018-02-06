package com.thecamhi.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Timer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.hichip.R;
import com.hichip.base.HiLog;
import com.hichip.content.HiChipDefines;
import com.hichip.data.HiDeviceInfo;
import com.hichip.sdk.HiChipSDK;
import com.hichip.sdk.HiChipSDK.HiChipInitCallback;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.thecamhi.base.CrashApplication;
import com.thecamhi.base.DatabaseManager;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.HiTools;
import com.thecamhi.base.LogcatHelper;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.bean.MyCamera.OnBindPushResult;

public class MainActivity extends FragmentActivity {

	private final static int HANDLE_MESSAGE_INIT_END = 0x90000001;
	private Class<?> fraList[] = { CameraFragment.class, PictureFragment.class, VideoFragment.class,
			AboutFragment.class };
	private int drawable[] = { R.drawable.camhi_tabbar_normal_camera, R.drawable.camhi_tabbar_normal_picture,
			R.drawable.camhi_tabbar_normal_video, R.drawable.camhi_tabbar_normal_about };
	private ImageView welcom_imv;
	private long initSdkTime;
	private int count = 0;
	Timer timer;
	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (HiDataValue.isDebug) {
			LogcatHelper.getInstance(this).start();
		}

		initview();
		initTabHost();

		initSDK();
		initXGPushSDK();

	}

	// 初始化SDK
	private void initSDK() {
		initSdkTime = System.currentTimeMillis();
		HiChipSDK.init(new HiChipInitCallback() {

			@Override
			public void onSuccess() {
				Message msg = handler.obtainMessage();
				msg.what = HANDLE_MESSAGE_INIT_END;
				handler.sendMessage(msg);
				HiLog.e("SDK INIT success");
			}

			@Override
			public void onFali(int arg0, int arg1) {
				Message msg = handler.obtainMessage();
				msg.what = HANDLE_MESSAGE_INIT_END;
				handler.sendMessage(msg);
				HiLog.e("SDK INIT fail");
			}
		});

	}

	// 搭载4个fragment
	private void initTabHost() {
		String[] tabString = getResources().getStringArray(R.array.tab_name);
		FragmentTabHost tabHost = (FragmentTabHost) findViewById(R.id.main_fragment_tabhost);
		tabHost.setup(this, getSupportFragmentManager(), R.id.fragment_main_content);
		LayoutInflater inflater = LayoutInflater.from(this);
		for (int i = 0; i < fraList.length; i++) {
			View view = inflater.inflate(R.layout.fragment_tabhost_switch_image, null);
			ImageView iv = (ImageView) view.findViewById(R.id.main_tabhost_imv);
			TextView tv = (TextView) view.findViewById(R.id.main_tabhost_tv);
			iv.setImageResource(drawable[i]);
			tv.setText(tabString[i]);
			TabSpec tabItem = tabHost.newTabSpec(tabString[i]).setIndicator(view);
			tabHost.addTab(tabItem, fraList[i], null);
		}
	}

	private void initXGPushSDK() {

		// 开启logcat输出，方便debug，发布时请关闭
		XGPushConfig.enableDebug(this, false);
		// 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(),
		// XGIOperateCallback)带callback版本
		// 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
		// 具体可参考详细的开发指南
		// 传递的参数为ApplicationContext
		// Context context = getApplicationContext();
		// XGPushManager.registerPush(context);

		// 2.36（不包括）之前的版本需要调用以下2行代码
		// Intent service = new Intent(context, XGPushService.class);
		// context.startService(service);

		// 其它常用的API：
		// 绑定账号（别名）注册：registerPush(context,account)或registerPush(context,account,
		// XGIOperateCallback)，其中account为APP账号，可以为任意字符串（qq、openid或任意第三方），业务方一定要注意终端与后台保持一致。
		// 取消绑定账号（别名）：registerPush(context,"*")，即account="*"为取消绑定，解绑后，该针对该账号的推送将失效
		// 反注册（不再接收消息）：unregisterPush(context)
		// 设置标签：setTag(context, tagName)
		// 删除标签：deleteTag(context, tagName)
		XGPushManager.registerPush(this, new XGIOperateCallback() {

			@Override
			public void onSuccess(Object data, int flag) {
				HiLog.e("bruce 注册成功，设备token为：" + data);
				String token = (String) data;
				HiDataValue.XGToken = token;
			}

			@Override
			public void onFail(Object data, int errCode, String msg) {
				HiLog.e("bruce 注册失败，为：" + msg);
			}
		});
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_MESSAGE_INIT_END:
				long spendingTime = System.currentTimeMillis() - initSdkTime;
				if (spendingTime < 2000 && spendingTime > 0) {
					this.postDelayed(new Runnable() {
						@Override
						public void run() {
							// requestEnd();
							initCamera();
							welcom_imv.setVisibility(View.GONE);
						}
					}, 2000 - spendingTime);

				} else {
					// requestEnd();
					initCamera();
					welcom_imv.setVisibility(View.GONE);
				}
				break;

			}

		}
	};

	// 获取本地数据
	private void initCamera() {
		if(HiDataValue.isDebug){
			PackageManager manager=getPackageManager();
			try {
				PackageInfo info=manager.getPackageInfo(getPackageName(), 0);
				HiLog.e("-------------当前版本号为：------------"+info.versionName);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		DatabaseManager manager = new DatabaseManager(this);
		SQLiteDatabase db = manager.getReadableDatabase();
		Cursor cursor = db.query(
				DatabaseManager.TABLE_DEVICE, new String[] { "dev_nickname", "dev_uid", "view_acc", "view_pwd",
						"dev_videoQuality", "dev_alarmState", "dev_pushState", "dev_serverData" },
				null, null, null, null, null);
		HiLog.e("step1 " + "cursor is null" + (cursor == null ? "true" : "false"));
		try {
			while (cursor != null && cursor.moveToNext()) {
				if (HiDataValue.isDebug)
					HiLog.e("step2 " + "cursor is null=" + (cursor == null ? "true" : "false"));
				String dev_nickname = cursor.getString(0);
				if (HiDataValue.isDebug)
					HiLog.e("step3 " + "dev_nickname=" + dev_nickname);
				String dev_uid = cursor.getString(1);
				if (HiDataValue.isDebug)
					HiLog.e("step3 " + "dev_uid=" + dev_uid);
				String dev_name = cursor.getString(2);
				if (HiDataValue.isDebug)
					HiLog.e("step3 " + "dev_name=" + dev_name);
				String dev_pwd = cursor.getString(3);
				if (HiDataValue.isDebug)
					HiLog.e("step3 " + "dev_pwd=" + dev_pwd);
				int dev_videoQuality = cursor.getInt(4);
				if (HiDataValue.isDebug)
					HiLog.e("step3 " + "dev_videoQuality=" + dev_videoQuality);
				int dev_alarmState = cursor.getInt(5);
				if (HiDataValue.isDebug)
					HiLog.e("step3 " + "dev_alarmState=" + dev_alarmState);
				int dev_pushState = cursor.getInt(6);
				if (HiDataValue.isDebug)
					HiLog.e("step3 " + "dev_pushState=" + dev_pushState);
				String dev_serverData = cursor.getString(7);
				if (HiDataValue.isDebug)
					HiLog.e("step3 " + "dev_serverData=" + dev_serverData);
				/*
				 * String dev_nickname=cursor.getString(cursor.getColumnIndex(
				 * "dev_nickname"));
				 * HiLog.e("step3 "+"dev_nickname="+dev_nickname); String
				 * dev_uid=cursor.getString(cursor.getColumnIndex("dev_uid"));
				 * HiLog.e("step3 "+"dev_uid="+dev_uid); String
				 * dev_name=cursor.getString(cursor.getColumnIndex("view_acc"));
				 * HiLog.e("step3 "+"dev_name="+dev_name); String
				 * dev_pwd=cursor.getString(cursor.getColumnIndex("view_pwd"));
				 * HiLog.e("step3 "+"dev_pwd="+dev_pwd); int
				 * dev_videoQuality=cursor.getInt(cursor.getColumnIndex(
				 * "dev_videoQuality"));
				 * HiLog.e("step3 "+"dev_videoQuality="+dev_videoQuality); int
				 * dev_alarmState=cursor.getInt(cursor.getColumnIndex(
				 * "dev_alarmState"));
				 * HiLog.e("step3 "+"dev_alarmState="+dev_alarmState); int
				 * dev_pushState=cursor.getInt(cursor.getColumnIndex(
				 * "dev_pushState"));
				 * HiLog.e("step3 "+"dev_pushState="+dev_pushState); byte[]
				 * byteSnapshot=cursor.getBlob(cursor.getColumnIndex("snapshot")
				 * );
				 */
				MyCamera camera = new MyCamera(dev_nickname, dev_uid, dev_name, dev_pwd);
				camera.setVideoQuality(dev_videoQuality);
				camera.setAlarmState(dev_alarmState);
				camera.setPushState(dev_pushState);
				camera.snapshot = loadImageFromUrl(MainActivity.this, camera);
				camera.setServerData(dev_serverData);
				camera.saveInCameraList();
				
				
				if (camera.getPushState() == 0) {
					String pDID = camera.getUid();
					SharedPreferences setting = MainActivity.this.getSharedPreferences("Subid_" + pDID,
							MainActivity.MODE_PRIVATE);
					int subID = setting.getInt("pushon", -1);
					if (subID == 1) {
						camera.setPushState(1);
					} else {
						camera.setPushState(0);
					}
				}
			}

		} catch (Exception e) {
			// 删除snapshot数据;
			// initCamera();
		} finally {
			cursor.close();
			cursor = null;
			db.close();
		}

		requestEnd();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	public void requestEnd() {
		// 获取数据完毕，发送广播到CameraFragment界面去刷新adapter
		Intent intent = new Intent();
		intent.setAction(HiDataValue.ACTION_CAMERA_INIT_END);
		sendBroadcast(intent);
		HiLog.e("send Broadcast");
	}

	private void initview() {

		HiDataValue.ANDROID_VERSION = HiTools.getAndroidVersion();

		if (HiDataValue.ANDROID_VERSION >= 6) {
			HiTools.checkPermissionAll(MainActivity.this);
		}

		welcom_imv = (ImageView) findViewById(R.id.welcome_imv);

	}

	public boolean isFirstTime() {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean firstTime = prefs.getBoolean("first_time", true);
		if (firstTime) {

			Editor pEdit = prefs.edit();
			pEdit.putBoolean("first_time", false);
			pEdit.commit();
		}

		return firstTime;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (MyCamera camera : HiDataValue.CameraList) {
			// camera.registerIOSessionListener(MainActivity.this);
			if (camera.isSetValueWithoutSave()) {
				camera.updateInDatabase(this);
			}
			camera.disconnect();
		}

		HiChipSDK.uninit();

		if (HiDataValue.isDebug) {
			LogcatHelper.getInstance(this).stop();
		}
		int pid = android.os.Process.myPid();
		android.os.Process.killProcess(pid);
	}

	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// Yes button clicked
				finish();
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				// No button clicked
				break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:

			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

			builder.setTitle(getString(R.string.tips_warning)).setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(getString(R.string.sure_to_exit))
					.setPositiveButton(getResources().getString(R.string.btn_yes), dialogClickListener)
					.setNegativeButton(getResources().getString(R.string.btn_no), dialogClickListener).show();

			break;
		}
		return true;
	}
	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { // TODO
	 * Auto-generated method stub
	 * 
	 * switch (keyCode) { case KeyEvent.KEYCODE_BACK:
	 * 
	 * if((System.currentTimeMillis()-exitTime) > 2000){
	 * HiToast.showToast(getApplicationContext(),
	 * getString(R.string.press_again_to_exit)); exitTime =
	 * System.currentTimeMillis(); } else { finish(); System.exit(0); }
	 * moveTaskToBack(true);
	 * 
	 * break; case KeyEvent.KEYCODE_MENU: AlertDialog dialog = new
	 * AlertDialog.Builder(MainActivity.this)
	 * .setTitle(R.string.exit_the_project)
	 * 
	 * .setMessage(getResources().getString(R.string.sure_to_exit))
	 * .setPositiveButton(getString(R.string.sure), new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * //MainActivity.this.finish(); moveTaskToBack(true); } })
	 * .setNegativeButton(getString(R.string.cancel), new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * dialog.dismiss(); } }) .create(); dialog.show();
	 * 
	 * break; } return true; }
	 */

	public Bitmap loadImageFromUrl(Context context, MyCamera camera) {
		// 是否SD卡可用
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// 检查是或有保存图片的文件夹，没有就创建一个
			String FileUrl = Environment.getExternalStorageDirectory() + "/android/data/"
					+ context.getResources().getString(R.string.app_name) + "/";
			File folder = new File(FileUrl);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			File f = new File(FileUrl + camera.getUid());
			// SD卡中是否有该文件，有则直接读取返回
			if (f.exists()) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				Bitmap b = BitmapFactory.decodeStream(fis);
				return b;
			}else {
				return null;
			}
		}

		return null;

	}

}

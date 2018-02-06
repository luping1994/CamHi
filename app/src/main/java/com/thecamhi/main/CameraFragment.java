package com.thecamhi.main;

import java.io.File;
import java.nio.BufferUnderflowException;
import java.util.Calendar;
import java.util.TimeZone;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hichip.R;
import com.hichip.base.HiLog;
import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.content.HiChipDefines;
import com.hichip.control.HiCamera;
import com.hichip.data.HiDeviceInfo;
import com.hichip.tools.Packet;
import com.tencent.android.tpush.XGLocalMessage;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.thecamhi.activity.AddCameraActivity;
import com.thecamhi.activity.EditCameraActivity;
import com.thecamhi.activity.LiveViewActivity;
import com.thecamhi.activity.setting.AlarmActionActivity;
import com.thecamhi.activity.setting.AliveSettingActivity;
import com.thecamhi.base.DatabaseManager;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.HiTools;

import com.thecamhi.bean.CamHiDefines;
import com.thecamhi.bean.CamHiDefines.HI_P2P_ALARM_ADDRESS;
import com.thecamhi.bean.MyCamera.OnBindPushResult;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;

public class CameraFragment extends HiFragment implements ICameraIOSessionCallback {
	private View layoutView;
	private static final int REQUEST_CODE_CAMERA_ADD = 0;
	private static final int REQUEST_CODE_CAMERA_EDIT = 1;
	private static final int REQUEST_CODE_CAMERA_LIVE_VIEW = 2;

	private static final int MOTION_ALARM = 0; // 移动侦测
	private static final int IO_ALARM = 1; // 外置报警
	private static final int AUDIO_ALARM = 2; // 声音报警
	private static final int UART_ALARM = 3; // 外置报警

	private CameraListAdapter adapter;
	private CameraBroadcastReceiver receiver;
	private ListView mListView;

	private long lastAlarmTime = 0;
	private String str_state[];
	private boolean delModel = false;
	private Button btn_edit_camera_fragment;
	int ranNum;

	private static final String Tag = "CameraFragment";

	public interface OnButtonClickListener {
		void onButtonClick(int btnId, MyCamera camera);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (receiver == null) {
			receiver = new CameraBroadcastReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(HiDataValue.ACTION_CAMERA_INIT_END);
			getActivity().registerReceiver(receiver, filter);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		layoutView = inflater.inflate(R.layout.fragment_camera, null);
		initView();
		ranNum = (int) (Math.random() * 10000);
		return layoutView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mListView = (ListView) getListView();

		/*
		 * SwipeMenuCreator creator = new SwipeMenuCreator() {
		 * 
		 * @Override public void create(SwipeMenu menu) {
		 * 
		 * // create "delete" item SwipeMenuItem deleteItem = new
		 * SwipeMenuItem(getActivity().getApplicationContext()); // set item
		 * background deleteItem.setBackground(new
		 * ColorDrawable(Color.rgb(0xF9,0x3F, 0x25))); // set item width
		 * deleteItem.setWidth(180); // set a icon
		 * deleteItem.setIcon(R.drawable.ic_delete); // add to menu
		 * menu.addMenuItem(deleteItem); } }; // set creator
		 * listView.setMenuCreator(creator);
		 * 
		 * // step 2. listener item click event
		 * listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		 * 
		 * @Override public void onMenuItemClick(int position, SwipeMenu menu,
		 * int index) {
		 * 
		 * MyCamera camera = HiDataValue.CameraList.get(position); switch
		 * (index) { case 0: camera.disconnect(); camera.deleteInCameraList();
		 * camera.deleteInDatabase(getActivity());
		 * adapter.notifyDataSetChanged(); break; } } });
		 */
	}

	private void initView() {

		/*
		 * TitleView title_top=(TitleView)
		 * layoutView.findViewById(R.id.title_top);
		 * title_top.setTitle(getString(R.string.title_camera_fragment));
		 */

		TextView title_middle = (TextView) layoutView.findViewById(R.id.title_middle_camera_fragment);
		title_middle.setText(getString(R.string.title_camera_fragment));

		LinearLayout add_camera_ll = (LinearLayout) layoutView.findViewById(R.id.add_camera_ll);
		add_camera_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), AddCameraActivity.class);
				//startActivityForResult(intent, REQUEST_CODE_CAMERA_ADD);
				startActivity(intent);
			}
		});

		str_state = getActivity().getResources().getStringArray(R.array.connect_state);

		adapter = new CameraListAdapter(getActivity());
		this.setListAdapter(adapter);
		adapter.notifyDataSetChanged();
		if (HiDataValue.isDebug)HiLog.e("HiDataValue.CameraList.size()=" + HiDataValue.CameraList.size() + "");
		adapter.setOnButtonClickListener(new OnButtonClickListener() {

			@Override
			public void onButtonClick(int btnId, MyCamera camera) {
				switch (btnId) {
				case R.id.setting_camera_item: {
					if (delModel) {
						// ????(???????),????????camera??
						Intent intent = new Intent();
						intent.putExtra(HiDataValue.EXTRAS_KEY_UID, camera.getUid());
						intent.setClass(getActivity(), EditCameraActivity.class);
						startActivityForResult(intent, REQUEST_CODE_CAMERA_EDIT);
					} else {
						// ??????????????,?????offline
						if (camera.getConnectState() == HiCamera.CAMERA_CONNECTION_STATE_LOGIN) {
							if (HiDataValue.isDebug)
								HiLog.e("BUTTON_SETTING.setOnClickListener");
							Intent intent = new Intent();
							intent.putExtra(HiDataValue.EXTRAS_KEY_UID, camera.getUid());
							intent.setClass(getActivity(), AliveSettingActivity.class);
							startActivity(intent);
						} else {
							HiToast.showToast(getActivity(), getString(R.string.click_offline_setting));
						}
					}
				}
					break;

				case R.id.delete_icon_camera_item:

					showDeleteDialog(camera);

					break;

				default:
					break;
				}

			}
		});

		btn_edit_camera_fragment = (Button) layoutView.findViewById(R.id.btn_edit_camera_fragment);
		btn_edit_camera_fragment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				if (delModel) {
					btn_edit_camera_fragment.setText(getString(R.string.btn_edit));
				} else {
					btn_edit_camera_fragment.setText(getString(R.string.finish));
				}

				delModel = !delModel;
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	// ???????
	protected void showDeleteDialog(final MyCamera camera) {
		final Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle(R.string.tips_warning).setMessage(R.string.tips_msg_delete_camera)
				.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						showLoadingProgress();

						camera.bindPushState(false, null);

						sendUnRegister(camera, 0);
						Message msg = handler.obtainMessage();
						msg.what = HiDataValue.HANDLE_MESSAGE_DELETE_FILE;
						msg.obj = camera;
						handler.sendMessageDelayed(msg, 1000);

					}
				});
		alertDialog.show();

	}

	private void sendUnRegister(MyCamera mCamera, int enable) {
		if (mCamera.getPushState() == 1) {
			return;
		}

		if (!mCamera.getCommandFunction(CamHiDefines.HI_P2P_ALARM_TOKEN_UNREGIST)) {
			if (HiDataValue.isDebug)
				HiLog.v("UNREGIST FUCTION: false ");
			return;
		}

		byte[] info = CamHiDefines.HI_P2P_ALARM_TOKEN_INFO.parseContent(0, mCamera.getPushState(),
				(int) (System.currentTimeMillis() / 1000 / 3600), enable);

		if (HiDataValue.isDebug)
			HiLog.e("HiDataValue.XGToken:" + mCamera.getPushState() + " Time:"
					+ (int) (System.currentTimeMillis() / 1000 / 3600) + " enable:" + enable);
		if (HiDataValue.isDebug)
			HiLog.e(info + "");
		mCamera.sendIOCtrl(CamHiDefines.HI_P2P_ALARM_TOKEN_UNREGIST, info);
	}

	protected void sendRegisterToken(MyCamera mCamera) {
		if (mCamera.getPushState() == 1 || mCamera.getPushState() == 0) {// if
																			// open
																			// ,
																			// send
																			// ;
			return;
		}
		HiLog.v("bruce sendRegisterToken");

		if (!mCamera.getCommandFunction(CamHiDefines.HI_P2P_ALARM_TOKEN_REGIST)) {
			if (HiDataValue.isDebug)
				HiLog.v("REGIST FUCTION: false ");
			return;
		}

		byte[] info = CamHiDefines.HI_P2P_ALARM_TOKEN_INFO.parseContent(0, mCamera.getPushState(),
				(int) (System.currentTimeMillis() / 1000 / 3600), 1);

		mCamera.sendIOCtrl(CamHiDefines.HI_P2P_ALARM_TOKEN_REGIST, info);
	}

	OnBindPushResult bindPushResult = new OnBindPushResult() {
		@Override
		public void onBindSuccess(MyCamera camera) {

			HiLog.e(Tag + "bruce on ReBind Success");
			if(!camera.handSubXYZ()){
				camera.setServerData(HiDataValue.CAMERA_ALARM_ADDRESS);
			}else {
				camera.setServerData(HiDataValue.CAMERA_ALARM_ADDRESS_THERE);
			}
			camera.updateServerInDatabase(getActivity());
			sendServer(camera);
			sendRegisterToken(camera);
		}

		@Override
		public void onBindFail(MyCamera camera) {
			// TODO Auto-generated method stub
			HiLog.e(Tag + "bruce on ReBind Fail");
		}

		@Override
		public void onUnBindSuccess(MyCamera camera) {
			HiLog.e(Tag + "bruce on UnBind Sucess");
			camera.bindPushState(true, bindPushResult);
		}

		@Override
		public void onUnBindFail(MyCamera camera) {
			// TODO Auto-generated method stub
			HiLog.e(Tag + "bruce on UnBind Fail");
		}

	};

	protected void showSuccessDialog() {

		Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle(R.string.tips_warning).setMessage(R.string.tips_remove_success)
				.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		alertDialog.show();
	}

	// Item?????
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		if (HiDataValue.isDebug)
			HiLog.e("onListItemClick:" + position);
		MyCamera selectedCamera = HiDataValue.CameraList.get(position);

		// ?????(????????????),?????????
		if (delModel) {
			Intent intent = new Intent();
			intent.putExtra(HiDataValue.EXTRAS_KEY_UID, selectedCamera.getUid());
			intent.setClass(getActivity(), EditCameraActivity.class);
			startActivityForResult(intent, REQUEST_CODE_CAMERA_EDIT);
		} else {

			// ??????,??????,?????
			if (selectedCamera.getConnectState() == HiCamera.CAMERA_CONNECTION_STATE_LOGIN) {
				Bundle extras = new Bundle();
				extras.putString(HiDataValue.EXTRAS_KEY_UID, selectedCamera.getUid());

				Intent intent = new Intent();
				intent.putExtras(extras);
				intent.setClass(getActivity(), LiveViewActivity.class);
				startActivityForResult(intent, REQUEST_CODE_CAMERA_LIVE_VIEW);

				HiDataValue.isOnLiveView = true;
				selectedCamera.setAlarmState(0);

				adapter.notifyDataSetChanged();
			} else {
				selectedCamera.connect();
			}
		}
	}

	// ?????CameraFragment,?????????,???????,?camera????????,??????
	private class CameraBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			if (intent.getAction().equals(HiDataValue.ACTION_CAMERA_INIT_END)) {
				if (adapter != null) {
					adapter.notifyDataSetChanged();
					if (HiDataValue.isDebug)
						HiLog.e("adapte.notifyDataSetChanged");
				}
				if (HiDataValue.isDebug)
					HiLog.e("HiDataValue.CameraList.size()=" + HiDataValue.CameraList.size() + "");
				for (MyCamera camera : HiDataValue.CameraList) {
					if (HiDataValue.isDebug)
						HiLog.e(camera.getUid() + ": LOGIN register");
					camera.registerIOSessionListener(CameraFragment.this);
					camera.connect();
				}
			}
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		delToNor();

	}

	public void delToNor() {
		delModel = false;
		btn_edit_camera_fragment.setText(getString(R.string.btn_edit));
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	// camera的adapter
	public class CameraListAdapter extends BaseAdapter {
		Context context;
		private LayoutInflater mInflater;
		OnButtonClickListener mListener;
		private String strState;

		public void setOnButtonClickListener(OnButtonClickListener listener) {
			mListener = listener;
		}

		public CameraListAdapter(Context context) {

			mInflater = LayoutInflater.from(context);
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return HiDataValue.CameraList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return HiDataValue.CameraList.get(position);
		}

		@Override
		public long getItemId(int arg0) {

			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final MyCamera camera = HiDataValue.CameraList.get(position);

			if (camera == null) {
				return null;
			}
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.camera_main_item, null);
				holder.setting = (ImageView) convertView.findViewById(R.id.setting_camera_item);
				holder.img_snapshot = (ImageView) convertView.findViewById(R.id.snapshot_camera_item);
				holder.txt_nikename = (TextView) convertView.findViewById(R.id.nickname_camera_item);
				holder.txt_uid = (TextView) convertView.findViewById(R.id.uid_camera_item);
				holder.txt_state = (TextView) convertView.findViewById(R.id.state_camera_item);
				holder.img_alarm = (ImageView) convertView.findViewById(R.id.img_alarm);

				holder.delete_icon = (ImageView) convertView.findViewById(R.id.delete_icon_camera_item);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (holder != null) {
				// HiLog.e(camera.snapshot==null?"camera.snapshot is
				// null":"camera.snapshot not null");

				holder.img_snapshot.setImageBitmap(camera.snapshot);
				HiLog.e("camera.snapshot-->"+camera.snapshot);

				holder.txt_nikename.setText(camera.getNikeName());
				holder.txt_uid.setText(camera.getUid());
				int state = camera.getConnectState();
				if (state >= 0 && state <= 4) {
					strState = "(" + str_state[state] + ")";
					holder.txt_state.setText(strState);
				}

				holder.setting.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (mListener != null) {
							mListener.onButtonClick(R.id.setting_camera_item, camera);
						}

					}
				});

				holder.delete_icon.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (mListener != null) {
							mListener.onButtonClick(R.id.delete_icon_camera_item, camera);
						}

					}
				});

				if (delModel) {
					holder.delete_icon.setVisibility(View.VISIBLE);
				} else {
					holder.delete_icon.setVisibility(View.GONE);
				}

				if (camera.getAlarmState() == 0) {
					holder.img_alarm.setVisibility(View.GONE);
				} else {
					holder.img_alarm.setVisibility(View.VISIBLE);
				}
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView img_snapshot;
			public TextView txt_nikename;
			public TextView txt_uid;
			public TextView txt_state;
			public ImageView img_alarm;

			public ImageView setting;
			public ImageView delete_icon;

		}

	}

	// ???????????receiveIOCtrlData
	@Override
	public void receiveIOCtrlData(HiCamera arg0, int arg1, byte[] arg2, int arg3) {
		if (arg1 == HiChipDefines.HI_P2P_GET_SNAP && arg3 == 0) {
			MyCamera camera = (MyCamera) arg0;
			if (!camera.reciveBmpBuffer(arg2)) {
				return;
			}

		}

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

	// ??????????????
	@Override
	public void receiveSessionState(HiCamera arg0, int arg1) {

		if (HiDataValue.isDebug)
			HiLog.v("uid:" + arg0.getUid() + "  state:" + arg1);

		Message msg = handler.obtainMessage();
		msg.what = HiDataValue.HANDLE_MESSAGE_SESSION_STATE;
		msg.arg1 = arg1;
		msg.obj = arg0;
		handler.sendMessage(msg);

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// ????????
			case HiDataValue.HANDLE_MESSAGE_SESSION_STATE:
				if (adapter != null)
					adapter.notifyDataSetChanged();
				switch (msg.arg1) {
				case HiCamera.CAMERA_CONNECTION_STATE_DISCONNECTED:
					if (HiDataValue.isDebug)
						HiLog.e("disconnected");
					break;
				case HiCamera.CAMERA_CONNECTION_STATE_LOGIN:
					MyCamera camera = (MyCamera) msg.obj;
					camera.mIsConnect=true;
					if (HiDataValue.isDebug)HiLog.e("uid:" + camera.getUid() + " LOGIN");
					setTime(camera);

					setServer(camera);

					//cameraLogin((MyCamera) msg.obj);

					camera.sendIOCtrl(HiChipDefines.HI_P2P_GET_TIME_ZONE, new byte[0]);

					break;
				case HiCamera.CAMERA_CONNECTION_STATE_WRONG_PASSWORD:
					// HiToast.showToast(getActivity(), "wrong password");
					if (HiDataValue.isDebug)
						HiLog.e("wrong password");
					break;
				case HiCamera.CAMERA_CONNECTION_STATE_CONNECTING:
					// HiToast.showToast(getActivity(), "connectting");
					// HiLog.e("connectting");
					break;
				}
				break;

			case HiDataValue.HANDLE_MESSAGE_RECEIVE_IOCTRL: {
				if (msg.arg2 == 0) {

					MyCamera camera = (MyCamera) msg.obj;

					switch (msg.arg1) {
					case HiChipDefines.HI_P2P_GET_SNAP:
						// Bundle bundle = msg.getData();
						// byte[] data = bundle.getByteArray("data");
						adapter.notifyDataSetChanged();

						// DatabaseManager manager = new
						// DatabaseManager(getActivity());

						// byte[] buff = camera.getBmpBuffer();
						if (camera.snapshot != null) {

							File rootFolder = new File(
									Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
							File sargetFolder = new File(rootFolder.getAbsolutePath() + "/android/data/"
									+ getActivity().getResources().getString(R.string.app_name));

							if (!rootFolder.exists()) {
								rootFolder.mkdirs();
							}
							if (!sargetFolder.exists()) {
								sargetFolder.mkdirs();
							}
							if (HiDataValue.isDebug)
								HiLog.e(camera.getUid() + ": save  snapshot");
							HiTools.saveBitmap(camera.snapshot, sargetFolder.getAbsolutePath() + "/" + camera.getUid());
							if (HiDataValue.isDebug)
								HiLog.v(sargetFolder.getAbsolutePath() + "/" + camera.getUid());

						} else {
							if (HiDataValue.isDebug)
								HiLog.e(camera.getUid() + ":  camera.snapshot =null");
						}

						break;

					case HiChipDefines.HI_P2P_GET_TIME_ZONE: {
						Bundle bundle = msg.getData();
						byte[] data = bundle.getByteArray(HiDataValue.EXTRAS_KEY_DATA);

						HiChipDefines.HI_P2P_S_TIME_ZONE timezone = new HiChipDefines.HI_P2P_S_TIME_ZONE(data);

						if (timezone.u32DstMode == 1) {
							camera.setSummerTimer(true);
						} else {
							camera.setSummerTimer(false);
						}

					}

						break;

					case CamHiDefines.HI_P2P_ALARM_TOKEN_REGIST: {
						Bundle bundle = msg.getData();
						byte[] data = bundle.getByteArray(HiDataValue.EXTRAS_KEY_DATA);

					}
						break;
					case CamHiDefines.HI_P2P_ALARM_TOKEN_UNREGIST: {
						Bundle bundle = msg.getData();
						byte[] data = bundle.getByteArray(HiDataValue.EXTRAS_KEY_DATA);
						if (HiDataValue.isDebug)
							HiLog.e(Packet.getString(data));

					}
						break;
					case CamHiDefines.HI_P2P_ALARM_ADDRESS_SET: {
						Bundle bundle = msg.getData();
						byte[] data = bundle.getByteArray(HiDataValue.EXTRAS_KEY_DATA);
						if (HiDataValue.isDebug)
							HiLog.e(Packet.getString(data));

					}
						break;
					case CamHiDefines.HI_P2P_ALARM_ADDRESS_GET: {
						Bundle bundle = msg.getData();
						byte[] data = bundle.getByteArray(HiDataValue.EXTRAS_KEY_DATA);
					}
						break;

					// 服务器直推的回调
					case HiChipDefines.HI_P2P_ALARM_EVENT: {

						if (camera.getPushState() == 0) {
							return;
						}

						/*
						 * //相对摄像机时间的每30秒一次回调， if(System.currentTimeMillis() -
						 * camera.getLastAlarmTime() < 30000) {
						 * 
						 * HiLog.e("Time lastAlarmTime:"+(System.
						 * currentTimeMillis() - lastAlarmTime));
						 * 
						 * return; }
						 */
						camera.setLastAlarmTime(System.currentTimeMillis());

						Bundle bundle = msg.getData();
						byte[] data = bundle.getByteArray(HiDataValue.EXTRAS_KEY_DATA);
						HiChipDefines.HI_P2P_EVENT event = new HiChipDefines.HI_P2P_EVENT(data);

						// showP2PPushMessage(camera.getUid(),event.u32Event);
						showAlarmNotification(camera, event.u32Event, System.currentTimeMillis());
						// HiLog.v("alarm time:"+event.u32Time);

						saveAlarmData(camera, event.u32Event, (int) (System.currentTimeMillis() / 1000));
						camera.setAlarmState(1);
						adapter.notifyDataSetChanged();
					}
						break;

					}
				}
			}
				break;

			case HiDataValue.HANDLE_MESSAGE_DELETE_FILE: {

				MyCamera camera = (MyCamera) msg.obj;

				camera.disconnect();
				camera.deleteInCameraList();
				camera.deleteInDatabase(getActivity());
				adapter.notifyDataSetChanged();
				dismissLoadingProgress();

				showSuccessDialog();

			}
				break;
			}
		}
	};

	// 报警推送到通知栏
	@SuppressWarnings("deprecation")
	private void showAlarmNotification(MyCamera camera, int evtType, long evtTime) {

		try {

			NotificationManager manager = (NotificationManager) getActivity()
					.getSystemService(Context.NOTIFICATION_SERVICE);

			Bundle extras = new Bundle();
			extras.putString(HiDataValue.EXTRAS_KEY_UID, camera.getUid());
			extras.putInt("type", 1);

			Intent intent = new Intent(getActivity(), MainActivity.class);
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.putExtras(extras);

			PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			// Calendar cal = Calendar.getInstance();
			// cal.setTimeZone(TimeZone.getDefault());
			// cal.setTimeInMillis(evtTime);
			// cal.add(Calendar.MONTH, 0);

			Notification notification = new Notification(R.drawable.ic_launcher, camera.getNikeName(),
					System.currentTimeMillis());
			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			notification.defaults = Notification.DEFAULT_ALL;
			ranNum++;

			String[] alarmList = getResources().getStringArray(R.array.tips_alarm_list_array);

			// notification.setLatestEventInfo(this, camera.getNikeName(),
			// "baby!", pendingIntent);
			notification.setLatestEventInfo(getActivity(), camera.getUid(), alarmList[evtType], pendingIntent);

			manager.notify(ranNum, notification);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void setServer(MyCamera mCamera) {

		if (!mCamera.getCommandFunction(CamHiDefines.HI_P2P_ALARM_ADDRESS_SET)) {
			HiLog.v("uid " + mCamera.getUid() + " bruce Device not support,return ");
			HiLog.e("CamraFragment  camera.getCommandFunction(CamHiDefines.HI_P2P_ALARM_ADDRESS_SET) is "
					+ mCamera.getCommandFunction(CamHiDefines.HI_P2P_ALARM_ADDRESS_SET));

			return;
		} else {
			HiLog.e("CamraFragment  camera.getCommandFunction(CamHiDefines.HI_P2P_ALARM_ADDRESS_SET) is "
					+ mCamera.getCommandFunction(CamHiDefines.HI_P2P_ALARM_ADDRESS_SET));

		}

		// bruce add
		HiLog.v("uid " + mCamera.getUid() + "bruce getPushState  " + mCamera.getPushState());
		if (mCamera.getServerData() != null && !mCamera.getServerData().equals(HiDataValue.CAMERA_ALARM_ADDRESS)) {
			if (mCamera.getPushState() > 1) {
				HiLog.v("bruce change old addr " + mCamera.getServerData() + "new addr "
						+ HiDataValue.CAMERA_ALARM_ADDRESS);
				if (HiDataValue.XGToken == null) {

					if (HiDataValue.ANDROID_VERSION >= 6) {
						if (!HiTools.checkPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
							ActivityCompat.requestPermissions(getActivity(),
									new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
						}
					}

					HiDataValue.XGToken = XGPushConfig.getToken(getActivity());
				}
				mCamera.bindPushState(false, bindPushResult);
				return;
			}
		}

		sendServer(mCamera);
		sendRegisterToken(mCamera);

	}

	protected void sendServer(MyCamera mCamera) {
//		//测试
//		mCamera.sendIOCtrl(CamHiDefines.HI_P2P_ALARM_ADDRESS_GET, null);
		if (mCamera.getServerData() == null){
			HiLog.v("bruce save sever ");
			mCamera.setServerData(HiDataValue.CAMERA_ALARM_ADDRESS);
			mCamera.updateServerInDatabase(getActivity());
		}
		if (!mCamera.getCommandFunction(CamHiDefines.HI_P2P_ALARM_ADDRESS_SET)) {
			HiLog.v("SERVER ADDRESS SET: false ");
			return;
		}
		if(!mCamera.handSubXYZ()){
			byte[] info = HI_P2P_ALARM_ADDRESS.parseContent(HiDataValue.CAMERA_ALARM_ADDRESS);
			mCamera.sendIOCtrl(CamHiDefines.HI_P2P_ALARM_ADDRESS_SET, info);
		}else {
			byte[] info = HI_P2P_ALARM_ADDRESS.parseContent(HiDataValue.CAMERA_ALARM_ADDRESS_THERE);
			mCamera.sendIOCtrl(CamHiDefines.HI_P2P_ALARM_ADDRESS_SET, info);
		}
	}

	// ??????????
	private void saveAlarmData(MyCamera camera, int evtType, int evtTime) {

		DatabaseManager manager = new DatabaseManager(getActivity());
		manager.addAlarmEvent(camera.getUid(), evtTime, evtType);

	}

	// ?????,??????????
	private void setTime(MyCamera camera) {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTimeInMillis(System.currentTimeMillis());

		byte[] time = HiChipDefines.HI_P2P_S_TIME_PARAM.parseContent(cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

		camera.sendIOCtrl(HiChipDefines.HI_P2P_SET_TIME_PARAM, time);
	}

	private void cameraLogin(MyCamera camera) {
//		if (HiDataValue.isDebug)
//			HiLog.e("mainactivity cameraLogin:" + camera.getUid());
//		if (camera.isFirstLogin()) {
//			camera.setFirstLogin(false);
//			/*
//			 * if(camera.getDeciveInfo()!=null){ String
//			 * info=Packet.getString(camera.getDeciveInfo().aszSystemName); }
//			 */
//			if (camera.getChipVersion() == HiDeviceInfo.CHIP_VERSION_HISI) {
//				if (HiDataValue.isDebug)
//					HiLog.e("mainActivity camera getsnapshot:" + camera.getUid());
//				camera.sendIOCtrl(HiChipDefines.HI_P2P_GET_SNAP,
//						HiChipDefines.HI_P2P_S_SNAP_REQ.parseContent(0, HiChipDefines.HI_P2P_STREAM_2));
//			}
//		}

	}

	private void showP2PPushMessage(String uid, int type) {

		if (HiDataValue.isOnLiveView)
			return;

		if (HiDataValue.CameraList.size() > 0) {
			for (MyCamera camera : HiDataValue.CameraList) {
				if (camera.getUid().equals(uid)) {

					if (camera.getPushState() <= 0)
						return;

					camera.setAlarmState(1);
					adapter.notifyDataSetChanged();
					break;
				}
			}
		}

		String strAlarmType[] = getResources().getStringArray(R.array.tips_alarm_list_array);

		XGLocalMessage local_msg = new XGLocalMessage();
		// 设置本地消息类型，1:通知，2:消息
		local_msg.setType(1);
		// 设置消息标题
		local_msg.setTitle(uid);
		// 设置消息内容

		if (type < strAlarmType.length && type >= 0)
			local_msg.setContent(strAlarmType[type]);
		// 设置消息日期，格式为：20140502
		// local_msg.setDate("20140930");
		// 设置消息触发的小时(24小时制)，例如：22代表晚上10点
		// local_msg.setHour("14");
		// 获取消息触发的分钟，例如：05代表05分
		// local_msg.setMin("16");
		// 设置消息样式，默认为0或不设置
		// local_msg.setBuilderId(6);
		// 设置拉起应用页面
		// local_msg.setActivity("com.qq.xgdemo.SettingActivity");
		// 设置动作类型：1打开activity或app本身，2打开浏览器，3打开Intent ，4通过包名打开应用
		// local_msg.setAction_type(1);
		// 设置URL
		// local_msg.setUrl("http://www.baidu.com");
		// 设置Intent
		// local_msg.setIntent("intent:10086#Intent;scheme=tel;action=android.intent.action.DIAL;S.key=value;end");
		// 自定义本地通知样式
		// local_msg.setIcon_type(0);
		// local_msg.setIcon_res("right");
		// 是否覆盖原先build_id的保存设置。1覆盖，0不覆盖
		// local_msg.setStyle_id(1);
		// 设置音频资源
		// local_msg.setRing_raw("mm");
		// 设置key,value
		// HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("key", "v1");
		// map.put("key2", "v2");
		// local_msg.setCustomContent(map);
		// 设置下载应用URL
		// local_msg.setPackageDownloadUrl("http://softfile.3g.qq.com:8080/msoft/179/1105/10753/MobileQQ1.0(Android)_Build0198.apk");
		// 设置要打开的应用包名
		// local_msg.setPackageName("com.example.com.qq.feedback");
		XGPushManager.addLocalNotification(getActivity(), local_msg);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		try {
			getActivity().unregisterReceiver(receiver);
		} catch (Exception e) {
		}

	}

}

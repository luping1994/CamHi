package com.thecamhi.activity.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.hichip.R;
import com.hichip.base.HiLog;
import com.hichip.callback.ICameraIOSessionCallback;
import com.hichip.content.HiChipDefines;
import com.hichip.content.HiChipDefines.HI_P2P_GET_DEV_INFO_EXT;
import com.hichip.control.HiCamera;
import com.hichip.tools.Packet;
import com.thecamhi.base.TitleView;
import com.thecamhi.base.TitleView.NavigationBarButtonListener;
import com.thecamhi.bean.CamHiDefines;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;

public class DeviceInfoActivity extends HiActivity implements ICameraIOSessionCallback {
	private MyCamera mCamera;
	private HiChipDefines.HI_P2P_GET_DEV_INFO_EXT deviceInfo;
	private TextView device_name_tv,network_state_tv,user_connections_tv,software_version_tv,ip_address_tv,subnet_mask_tv,gateway_tv,dns_tv;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_info);

		String uid = getIntent().getStringExtra(HiDataValue.EXTRAS_KEY_UID);
		for (MyCamera camera : HiDataValue.CameraList) {
			if (uid.equals(camera.getUid())) {
				mCamera = camera;
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_DEV_INFO_EXT, new byte[0]);
				mCamera.sendIOCtrl(HiChipDefines.HI_P2P_GET_NET_PARAM, new byte[0]);
				//if(HiDataValue.isDebug)mCamera.sendIOCtrl(CamHiDefines.HI_P2P_ALARM_ADDRESS_GET, new byte[0]);
				 
				break;
			}
		}
		initView();
	}

	private void initView() {

		TitleView title=(TitleView)findViewById(R.id.title_top);
		title.setTitle(getResources().getString(R.string.title_equipment_information));
		title.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		title.setNavigationBarButtonListener(new NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				switch (which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					DeviceInfoActivity.this.finish();
					break;
				}
			}
		});





		device_name_tv=(TextView)findViewById(R.id.device_name_tv);
		network_state_tv=(TextView)findViewById(R.id.network_state_tv);
		user_connections_tv=(TextView)findViewById(R.id.user_connections_tv);
		software_version_tv=(TextView)findViewById(R.id.software_version_tv);


		ip_address_tv=(TextView)findViewById(R.id.ip_address_tv);
		subnet_mask_tv=(TextView)findViewById(R.id.subnet_mask_tv);
		gateway_tv=(TextView)findViewById(R.id.gateway_tv);
		dns_tv=(TextView)findViewById(R.id.dns_tv);

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


					case HiChipDefines.HI_P2P_GET_DEV_INFO_EXT:
						deviceInfo = new HiChipDefines.HI_P2P_GET_DEV_INFO_EXT(data);
						//						HiChipDefines.HI_P2P_S_DEV_INFO dev_info = new HiChipDefines.HI_P2P_S_DEV_INFO(data);

						device_name_tv.setText(Packet.getString(deviceInfo.aszSystemName));

						String state[]=getResources().getStringArray(R.array.net_work_style);
						network_state_tv.setText(state[deviceInfo.u32NetType]);
						user_connections_tv.setText(deviceInfo.sUserNum+"");

						software_version_tv.setText(Packet.getString(deviceInfo.aszSystemSoftVersion));


						break;

					case HiChipDefines.HI_P2P_GET_NET_PARAM:

						HiChipDefines.HI_P2P_S_NET_PARAM net_param = new HiChipDefines.HI_P2P_S_NET_PARAM(data);
						String ip = Packet.getString(net_param.strIPAddr);
						String mask=Packet.getString(net_param.strNetMask);
						String getway = Packet.getString(net_param.strGateWay);
						String dns=Packet.getString(net_param.strFDNSIP);

						ip_address_tv.setText(ip);
						subnet_mask_tv.setText(mask);
						gateway_tv.setText(getway);
						dns_tv.setText(dns);

						dismissLoadingProgress();
						break;
					case CamHiDefines.HI_P2P_ALARM_ADDRESS_GET:
						CamHiDefines.HI_P2P_ALARM_ADDRESS aa=new CamHiDefines.HI_P2P_ALARM_ADDRESS(data);
						HiLog.e( Packet.getString(aa.szAlarmAddr));
						break;
						

					}
				}
			}
			break;


			}
		}
	};


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

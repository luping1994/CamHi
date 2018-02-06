package com.thecamhi.bean;

import android.R.bool;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.AvoidXfermode.Mode;
import android.util.Log;

import com.hichip.base.HiLog;
import com.hichip.control.HiCamera;
import com.hichip.push.HiPushSDK;
import com.hichip.tools.Packet;
import com.thecamhi.base.CrashApplication;
import com.thecamhi.base.DatabaseManager;
import com.thecamhi.main.MainActivity;
import com.thecamhi.model.LiveViewModel;
import com.thecamhi.utils.SharePreUtils;

public class MyCamera extends HiCamera {

	// private long db_id;
	private String nikeName = "";
	private int videoQuality = HiDataValue.DEFAULT_VIDEO_QUALITY;
	private int alarmState = HiDataValue.DEFAULT_ALARM_STATE;
	private int pushState = HiDataValue.DEFAULT_PUSH_STATE;

	private boolean hasSummerTimer;

	private boolean isFirstLogin = true;

	private byte[] bmpBuffer = null;
	public Bitmap snapshot = null;

	private long lastAlarmTime;

	private boolean isSetValueWithoutSave = false;
	private int style;
	private int androidVersion;
	//private String limit[] = { "AAAA", "BBBB", "CCCC", "DDDD", "EEEE", "FFFF", "GGGG", "HHHH", "IIII", "JJJJ", "KKKK" };
	private String serverData;
	
	public  boolean mIsConnect=false;
	
	

	public MyCamera(String nikename, String uid, String username, String password) {
		super(uid, username, password);
		// TODO Auto-generated constructor stub
		this.nikeName = nikename;
	}

	public void saveInDatabase(Context context) {
		DatabaseManager db = new DatabaseManager(context);
		db.addDevice(nikeName, getUid(), getUsername(), getPassword(), videoQuality, alarmState, pushState);
	}

	public void setSummerTimer(boolean hasSummerTimer) {
		this.hasSummerTimer = hasSummerTimer;
	}

	public boolean getSummerTimer() {
		return this.hasSummerTimer;
	}
	
	public void setServerData(String serverData) {
		this.serverData = serverData;
	}

	public String getServerData() {
		return this.serverData;
	}

	public void saveInCameraList() {
		HiDataValue.CameraList.add(this);
	}

	public void deleteInCameraList() {
		HiDataValue.CameraList.remove(this);
		this.unregisterIOSessionListener();
		snapshot = null;
	}

	public long getLastAlarmTime() {
		return lastAlarmTime;
	}

	public void setLastAlarmTime(long lastAlarmTime) {
		this.lastAlarmTime = lastAlarmTime;
	}

	public void updateInDatabase(Context context) {
		DatabaseManager db = new DatabaseManager(context);
		db.updateDeviceByDBID(nikeName, getUid(), getUsername(), getPassword(), videoQuality,
				HiDataValue.DEFAULT_ALARM_STATE, pushState, getServerData());

		isSetValueWithoutSave = false;
	}

	public void updateServerInDatabase(Context context) {
		DatabaseManager db = new DatabaseManager(context);
		db.updateServerByUID(getUid(), getServerData());

		isSetValueWithoutSave = false;
	}

	public void deleteInDatabase(Context context) {
		DatabaseManager db = new DatabaseManager(context);
		db.removeDeviceByUID(this.getUid());
		db.removeDeviceAlartEvent(this.getUid());
	}

	public int getAlarmState() {
		return alarmState;
	}

	public void setAlarmState(int alarmState) {
		this.alarmState = alarmState;
	}

	public int getPushState() {
		return pushState;
	}

	public void setPushState(int pushState) {
		this.pushState = pushState;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public int getStyle() {

		return style;
	}

	public int getVideoQuality() {
		return videoQuality;
	}

	public void setVideoQuality(int videoQuality) {
		this.videoQuality = videoQuality;
	}

	public String getNikeName() {
		return nikeName;
	}

	public void setNikeName(String nikeName) {
		this.nikeName = nikeName;
	}

	private int curbmpPos = 0;

	public boolean reciveBmpBuffer(byte[] byt) {

		if (byt.length < 10) {
			return false;
		}

		if (bmpBuffer == null) {
			curbmpPos = 0;
			int buflen = Packet.byteArrayToInt_Little(byt, 0);
			if (buflen <= 0) {
				return false;
			}
			bmpBuffer = new byte[buflen];
			// HiLog.e(bmpBuffer==null?"bmpBuffer is null":"bmpBuffer not null"+
			// "\n"+"buflen:"+buflen+"");
		}

		// Arrays.fill(bmpBuffer, (byte) 0);

		int datalen = Packet.byteArrayToInt_Little(byt, 4);
		// HiLog.e("bytLen:"+byt.length + " datalen:"+datalen);

		if (curbmpPos + datalen <= bmpBuffer.length)
			System.arraycopy(byt, 10, bmpBuffer, curbmpPos, datalen);

		// HiLog.e("byt="+Packet.getHex(byt, byt.length)+" \n"+"bmpBuffer="+
		// Packet.getHex(bmpBuffer,bmpBuffer.length)+" ");

		curbmpPos += (datalen);

		// HiLog.e("curbmpPos="+curbmpPos+"");
		// Log.e("camera", "recivebmpbuffer");
		short flag = Packet.byteArrayToShort_Little(byt, 8);

		if (flag == 1) {
			// HiLog.e("flag=:"+1 );

			createSnapshot();
			return true;
		}

		return false;
	}

	private void createSnapshot() {
		/*
		 * ByteArrayInputStream stream=new ByteArrayInputStream(bmpBuffer);
		 * HiLog.e("stream is null:"+stream);
		 * 
		 * 
		 * //HiLog.e(Packet.getHex(bmpBuffer, bmpBuffer.length)+"");
		 * 
		 * snapshot=BitmapFactory.decodeStream(stream, null, null);
		 */

		Bitmap snapshot_temp = BitmapFactory.decodeByteArray(bmpBuffer, 0, bmpBuffer.length);

		if (snapshot_temp == null) {
			HiLog.e(snapshot_temp == null ? getUid() + "：Factory build null" : "：Factory build not null");
			HiLog.e(bmpBuffer + "");
		}

		if (snapshot_temp != null)
			snapshot = snapshot_temp;

		bmpBuffer = null;
		curbmpPos = 0;
		// HiLog.e(snapshot==null?"sure is null":"snapshot not null");

	}

	public boolean isFirstLogin() {
		return isFirstLogin;
	}

	public void setFirstLogin(boolean isFirstLogin) {
		this.isFirstLogin = isFirstLogin;
	}

	public boolean isSetValueWithoutSave() {
		return isSetValueWithoutSave;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub

		if (getUid() != null && getUid().length() > 4) {
			String temp = getUid().substring(0, 4);
			for (String str : HiDataValue.limit) {
				if (!temp.equalsIgnoreCase(str)) {
					continue;
				}
				super.connect();
				return;
			}
			return;

		} else {
			return;
		}

	}

	public interface OnBindPushResult {
		public void onBindSuccess(MyCamera camera);

		public void onBindFail(MyCamera camera);

		public void onUnBindSuccess(MyCamera camera);

		public void onUnBindFail(MyCamera camera);

	}

	private OnBindPushResult onBindPushResult;

	private HiPushSDK push;
	private HiPushSDK.OnPushResult pushResult = new HiPushSDK.OnPushResult() {

		@Override
		public void pushBindResult(int subID, int type, int result) {
			isSetValueWithoutSave = true;

			if (type == HiPushSDK.PUSH_TYPE_BIND) {
				HiLog.e("bruce bind subID " + subID + "result " + result);
				if (HiPushSDK.PUSH_RESULT_SUCESS == result) {
					pushState = subID;
					if (onBindPushResult != null)
						onBindPushResult.onBindSuccess(MyCamera.this);
				} else if (HiPushSDK.PUSH_RESULT_FAIL == result || HiPushSDK.PUSH_RESULT_NULL_TOKEN == result) {
					if (onBindPushResult != null)
						onBindPushResult.onBindFail(MyCamera.this);
				}
			} else if (type == HiPushSDK.PUSH_TYPE_UNBIND) {
				HiLog.e("bruce unbind subID " + subID + "result " + result);
				if (HiPushSDK.PUSH_RESULT_SUCESS == result) {
					if (onBindPushResult != null)
						onBindPushResult.onUnBindSuccess(MyCamera.this);
				} else if (HiPushSDK.PUSH_RESULT_FAIL == result) {
					if (onBindPushResult != null)
						onBindPushResult.onUnBindFail(MyCamera.this);
				}

			}

		}
	};

	public void bindPushState(boolean isBind, OnBindPushResult bindPushResult) {
		if (HiDataValue.XGToken == null) {
			return;
		}
		/* 地址变更 解绑时 用旧的服务器 */
		if (!isBind && this.getServerData() != null && !this.getServerData().equals(HiDataValue.CAMERA_ALARM_ADDRESS)) {
			// HiLog.e("bruce bindPushState 1");
			push = new HiPushSDK(HiDataValue.XGToken, getUid(), HiDataValue.company, pushResult, this.getServerData());
		} else if (this.getCommandFunction(CamHiDefines.HI_P2P_ALARM_ADDRESS_SET)&&!handSubXYZ()){
			// HiLog.e("bruce bindPushState 2");
			push = new HiPushSDK(HiDataValue.XGToken, getUid(), HiDataValue.company, pushResult,
					HiDataValue.CAMERA_ALARM_ADDRESS);
		}else if(this.getCommandFunction(CamHiDefines.HI_P2P_ALARM_ADDRESS_SET)&&handSubXYZ()){
			push = new HiPushSDK(HiDataValue.XGToken, getUid(), HiDataValue.company, pushResult,
					HiDataValue.CAMERA_ALARM_ADDRESS_THERE);
		} else{//old device
			// HiLog.e("bruce bindPushState 3");
			push = new HiPushSDK(HiDataValue.XGToken, getUid(), HiDataValue.company, pushResult,
					HiDataValue.CAMERA_OLD_ALARM_ADDRESS);

			// push = new HiPushSDK(HiDataValue.XGToken, getUid(),
			// HiDataValue.company, pushResult);
		}

		onBindPushResult = bindPushResult;

		if (isBind) {
			push.bind();
		} else {
			HiLog.e("getPushState():" + getPushState());
			push.unbind(getPushState());
		}
	}
	/**
	 * 处理UID前缀为XXX YYYY ZZZ
	 * @return 如果是则返回 true
	 */
	public boolean handSubXYZ(){
		String  subUid=this.getUid().substring(0,4);
		for(String str:HiDataValue.SUBUID){
			if(str.equalsIgnoreCase(subUid)){
				return true;
			}
		}
		return false;
	}
}





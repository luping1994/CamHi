package com.thecamhi.base;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.hichip.R;
import com.hichip.base.HiLog;
import com.tencent.android.tpush.XGLocalMessage;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.thecamhi.bean.HiDataValue;

@SuppressWarnings("unused")
public class PushMessageReceiver  extends XGPushBaseReceiver{

//	private Intent intent = new Intent("com.qq.xgdemo.activity.UPDATE_LISTVIEW");

//	public interface OnMessageReceiverCallback {
//	        void onNotifactionShowedResult();
//    }
//	
//	private static OnMessageReceiverCallback cbk = null;
//	public static void loadSJThread(OnMessageReceiverCallback onCallback) {
//		cbk = onCallback; 
//	}
	
	@Override
	public void onDeleteTagResult(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
//		HiLog.v("XingeDemo  onDeleteTagResult");
	}

	@Override
	public void onNotifactionClickedResult(Context arg0,
			XGPushClickedResult arg1) {
		// TODO Auto-generated method stub
//		HiLog.v("XingeDemo  onNotifactionClickedResult");
		
	}

	@Override
	public void onNotifactionShowedResult(Context arg0, XGPushShowedResult arg1) {
		// TODO Auto-generated method stub
//		HiLog.e("XingeDemo  onNotifactionShowedResult   :");
	}

	@Override
	public void onRegisterResult(Context arg0, int arg1, XGPushRegisterResult arg2) {
		// TODO Auto-generated method stub
//		HiLog.v("XingeDemo  onRegisterResult");
	}

	@Override
	public void onSetTagResult(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
//		HiLog.v("XingeDemo  onSetTagResult");
	}

	@Override
	public void onTextMessage(Context arg0, XGPushTextMessage arg1) {
		// TODO Auto-generated method stub
		HiLog.e("XingeDemo  onTextMessage:");
		
		
		String key = arg1.getCustomContent();
		String uid = null;
		int type = 0;
		int time = 0;
		if(key!= null) {
			try {
				JSONObject arrJson = new JSONObject(key);
				HiLog.v(arrJson+"");
				String jsonc = arrJson.getString("content");
				JSONObject conJson = new JSONObject(jsonc);
				uid = conJson.getString("uid");
				type = conJson.getInt("type");
				time = conJson.getInt("time");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(uid == null)
				return;
			
	
			if(HiDataValue.CameraList.size() > 0) {
				return;
			}
			
			String strAlarmType[] = arg0.getResources().getStringArray(R.array.tips_alarm_list_array);
			
			XGLocalMessage local_msg = new XGLocalMessage();
			// 设置本地消息类型，1:通知，2:消息
			local_msg.setType(1);
			// 设置消息标题
			local_msg.setTitle(uid);
			// 设置消息内容
			
			if(type < strAlarmType.length && type >=0)
				local_msg.setContent(strAlarmType[type]);
			
			HiLog.e("uid："+uid+"  type:"+type+"  time:"+time+"  local_msg："+local_msg);
			XGPushManager.addLocalNotification(arg0, local_msg);
			
		}
			
	}

	@Override
	public void onUnregisterResult(Context arg0, int arg1) {
	}

}
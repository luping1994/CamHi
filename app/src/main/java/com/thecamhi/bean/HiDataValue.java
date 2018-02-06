package com.thecamhi.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hichip.R;
import com.hichip.content.HiChipDefines;

public class HiDataValue {
	public final static boolean isDebug=false;
	
	
	public final static int DEFAULT_VIDEO_QUALITY = 1;
	public final static int DEFAULT_ALARM_STATE = 0;
	public final static int DEFAULT_PUSH_STATE = 0;//0关闭，1开启

	public final static int NOTICE_RUNNING_ID = 20001;
	public final static int NOTICE_ALARM_ID = 20002;
	public final static int REQUEST_CODE_ASK_PERMISSON=20003;
	
	public static int ANDROID_VERSION=0;

	public static int model=0;

	public final static String EXTRAS_KEY_UID = "uid";
	public final static String EXTRAS_KEY_DATA = "data";
	


	public final static String ACTION_CAMERA_INIT_END= "camera_init_end";
	public final static String CAMERA_OLD_ALARM_ADDRESS= "49.213.12.136";
	public final static String CAMERA_ALARM_ADDRESS= "47.91.149.233";
	//新增XXXX YYYY ZZZZ的服务器地址
	public static final String CAMERA_ALARM_ADDRESS_THERE = "47.90.64.173";

	public final static int HANDLE_MESSAGE_SESSION_STATE = 0x90000001;
	public final static int HANDLE_MESSAGE_RECEIVE_IOCTRL = 0x90000003;
	public final static int HANDLE_MESSAGE_SCAN_RESULT = 0x90000005;
	public final static int HANDLE_MESSAGE_SCAN_CHECK = 0x90000006;
	public final static int HANDLE_MESSAGE_DOWNLOAD_STATE=0x90000007;
	public final static int HANDLE_MESSAGE_DELETE_FILE=0X10001;
	
	public final static int HANDLE_MESSAGE_PLAY_STATE = 0x80000001;
	public final static int HANDLE_MESSAGE_PROGRESSBAR_RUN = 0x80000002;

	public static List<MyCamera> CameraList = new ArrayList<MyCamera>();
	public static String[] zifu={"&","'","~","*","(",")","/","\"","%","!",":",";",".","<",">",",","'"};

	public static boolean isOnLiveView = false;
	public static final String STYLE="style";

	public static String XGToken;
	public static final String limit[] = { "AAAA", "BBBB", "CCCC", "DDDD", "EEEE", "FFFF", "GGGG", "HHHH", "IIII", "JJJJ", "KKKK" 
			,"XXXX","YYYY","ZZZZ"};
	public static final String[] SUBUID={"XXXX","YYYY","ZZZZ"};
	

	public static final String company = "hichip";
	
	
	   

	
}

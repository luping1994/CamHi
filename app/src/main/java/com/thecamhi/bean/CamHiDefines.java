package com.thecamhi.bean;

import com.hichip.tools.Packet;

public class CamHiDefines {


	public static final int	 HI_P2P_ALARM_TOKEN_REGIST	 =	0x00004132		;/*报警推送注册，请在首次连接设置时间后调用*/
	public static final int	 HI_P2P_ALARM_TOKEN_UNREGIST =	0x00004133	;	/*报警推送注销*/
	public static final int	HI_P2P_GET_OSD_PARAM  =	0x00003109;
	public static final int	HI_P2P_SET_OSD_PARAM  =	0x00003110;
	
	public static final int HI_P2P_ALARM_ADDRESS_GET =		0x0000415e	;	/*获取报警服务器地址*/
	public static final int HI_P2P_ALARM_ADDRESS_SET =		0x0000415f	;	/*设置报警服务器地址*/

	
	
	public static final int HI_P2P_SDK_TOKEN_LEN	= 68;
	
	/******HI_P2P_ALARM_TOKEN_REGIST******/   
	public static class HI_P2P_ALARM_TOKEN_INFO {
		int u32Chn;
		byte[] szTokenId=new byte[HI_P2P_SDK_TOKEN_LEN];
		int u32UtcTime;
		byte s8Enable;
		public byte sReserved[] = new byte[3];


		public static byte[] parseContent(int u32Chn,int szTokenId,int u32UtcTime,int enable) {
			byte[] info = new byte[77];

			byte[] bChannel = Packet.intToByteArray_Little(u32Chn);
			byte[] bToken = Packet.intToByteArray_Little(szTokenId);
			byte[] bUtcTime = Packet.intToByteArray_Little(u32UtcTime);
			byte[] bEnable=Packet.intToByteArray_Little(enable);


			System.arraycopy(bChannel, 0, info, 0, 4);
			System.arraycopy(bToken, 0, info, 4, 4);
			System.arraycopy(bUtcTime, 0, info, 72, 4);
			System.arraycopy(bEnable, 0, info, 76, 1);


			return info;
		}

		public HI_P2P_ALARM_TOKEN_INFO (byte[] byt) {
			u32Chn=Packet.byteArrayToInt_Little(byt,0);	
			System.arraycopy(byt, 0, szTokenId, 4, HI_P2P_SDK_TOKEN_LEN);
			u32UtcTime=Packet.byteArrayToInt_Little(byt,72);
			s8Enable=byt[76];

		}

	}
/******HI_P2P_ALARM_TOKEN_UNREGIST******/
	///**************************HI_P2P_ALARM_TOKEN_REGIST***************************/
	//#define HI_P2P_ALARM_TOKEN_MAX	64	/*仅限设备端使用，最大token个数*/
	//typedef struct
	//{
	//	HI_U32 u32Chn;				/*ipc :0*/
	//	HI_CHAR szTokenId[68];		/*token*/
	//	HI_U32 u32UtcTime;			/*客户端当前utc时间(单位小时,即 /3600 )*/
	//	HI_S8 s8Enable;				/*app端报警推送是否打开, 1: 打开*/
	//	HI_S8 sReserved[3];
	//}HI_P2P_ALARM_TOKEN_INFO;
	///**************************HI_P2P_ALARM_TOKEN_UNREGIST***************************/


	
	/****************HI_P2P_GET_OSD_PARAM  HI_P2P_SET_OSD_PARAM*******************/
	public static class HI_P2P_S_OSD{
		int u32Chn;
		int u32EnTime;
		int u32EnName;
		int u32PlaceTime;
		int u32PlaceName;
		public byte[] strName=new byte[64];

		public static byte[] parseContent(int u32Chn,int u32EnTime,
				int u32EnName,int u32PlaceTime,int u32PlaceName,String name){
			byte[] osd = new byte[84];

			byte[] bChn = Packet.intToByteArray_Little(u32Chn);
			byte[] bEnTime = Packet.intToByteArray_Little(u32EnTime);
			byte[] bEnName = Packet.intToByteArray_Little(u32EnName);
			byte[] bPlaceTime=Packet.intToByteArray_Little(u32PlaceTime);
			byte[] bPlaceName=Packet.intToByteArray_Little(u32PlaceName);
			byte[] bName=name.getBytes();

			System.arraycopy(bChn, 0, osd, 0, 4);
			System.arraycopy(bEnTime, 0, osd, 4, 4);
			System.arraycopy(bEnName, 0, osd, 8, 4);
			System.arraycopy(bPlaceTime, 0, osd, 12, 4);
			System.arraycopy(bPlaceName, 0, osd, 16, 4);
			System.arraycopy(bName, 0, osd, 20, bName.length>64?64:bName.length);
			return osd;
		}


		public HI_P2P_S_OSD(byte[] byt){
			u32Chn=Packet.byteArrayToInt_Little(byt,0);		
			u32EnTime=Packet.byteArrayToInt_Little(byt,4);	
			u32EnName=Packet.byteArrayToInt_Little(byt,8);	
			u32PlaceTime=Packet.byteArrayToInt_Little(byt,12);	
			u32PlaceName=Packet.byteArrayToInt_Little(byt,16);	

			System.arraycopy(byt, 20, strName, 0,strName.length>64?64:strName.length);


		}


	}

	//	/****************HI_P2P_GET_OSD_PARAM  HI_P2P_SET_OSD_PARAM*******************/
	//	typedef struct 
	//	{    
	//	    HI_U32 u32Channel;/*ipc: 0*/
	//	    HI_U32 u32EnTime; /*时间,0 :close  !0 :open*/
	//	    HI_U32 u32EnName; /*名称,0 :close  !0 :open*/
	//	    HI_U32 u32PlaceTime;/*时间坐标*/
	//	    HI_U32 u32PlaceName;/*名称坐标*/
	//	    HI_CHAR strName[64];
	//	} HI_P2P_S_OSD;
	//	/****************HI_P2P_GET_OSD_PARAM  HI_P2P_SET_OSD_PARAM*******************/

	
	public static class HI_P2P_ALARM_ADDRESS{
		public byte[] szAlarmAddr=new byte[32];
		byte[] sReserved=new byte[4];
		
		public static byte[] parseContent(String szAlarmAddr){
			byte[] addr = new byte[32];
			
			byte[] bAlarmAddr = szAlarmAddr.getBytes();
			

			System.arraycopy(bAlarmAddr, 0, addr, 0, bAlarmAddr.length>32?32:bAlarmAddr.length);
			
			return addr;
		}
		
		public HI_P2P_ALARM_ADDRESS(byte[] data){
			System.arraycopy(data, 0, szAlarmAddr, 0, data.length>32?32:data.length);
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	
//	/********************HI_P2P_ALARM_ADDRESS_GET*********************/
//	typedef struct 
//	{
//		HI_CHAR szAlarmAddr[32];	/*报警服务器地址*/
//		HI_CHAR sReserved[4];
//	}HI_P2P_ALARM_ADDRESS;
//	/********************HI_P2P_ALARM_ADDRESS_SET*********************/
}

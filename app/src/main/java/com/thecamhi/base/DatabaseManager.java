package com.thecamhi.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.hichip.base.HiLog;
import com.thecamhi.bean.HiDataValue;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DatabaseManager {
	public static final String TABLE_DEVICE="device";
	public static final String TABLE_ALARM_EVENT = "alarm_event";
	//	public static final String TABLE_ALARM_EVENT = "alarm_event";
	//	public static final String TABLE_PUSH = "camera_push";
	private DatabaseHelper dbHelper;

	public DatabaseManager(Context context) {
		super();
		dbHelper=new DatabaseHelper(context);
	}

	public SQLiteDatabase getReadableDatabase(){
		return dbHelper.getReadableDatabase();
	}

	public long addDevice(String dev_nickname,String dev_uid,String dev_name,String dev_pwd,int videoQuality,int allAlarmState,int pushState){
		SQLiteDatabase db=dbHelper.getWritableDatabase();


		Cursor mCursor = db.query(TABLE_DEVICE, null,
				"dev_uid"+ " =  ? " , new String[]{dev_uid}, null, null, null);
		HiLog.e("saveing 1");
		if (mCursor != null &&
				mCursor.moveToFirst()) {
			return -1;
		}

		HiLog.e("saveing 2");

		ContentValues values=new ContentValues();
		values.put("dev_nickname", dev_nickname);
		values.put("dev_uid", dev_uid);
		values.put("dev_name", dev_name);
		values.put("dev_pwd", dev_pwd);
		values.put("view_acc", dev_name);
		values.put("view_pwd", dev_pwd);
		values.put("dev_videoQuality", videoQuality);
		//		values.put("dev_alarmState", allAlarmState);
		//		values.put("dev_pushState", pushState);
		//		+ "view_acc				VARCHAR(30) NULL, "//5
		//		+ "view_pwd				VARCHAR(30) NULL, "//6
		//		+ "event_notification 	INTEGER, " //7
		//		+ "ask_format_sdcard		INTEGER,"//8
		//		+ "camera_channel			INTEGER, "//9
		values.put("dev_alarmState", allAlarmState);
		values.put("dev_pushState", pushState);
		values.put("event_notification", 0);
		values.put("ask_format_sdcard", 0);
		values.put("camera_channel", 0);
		values.put("dev_serverData", HiDataValue.CAMERA_ALARM_ADDRESS);
		
		long ret=db.insertOrThrow(TABLE_DEVICE, null, values);
  
		return ret;

	}
	
	

	public void updateDeviceByDBID(String dev_nickname,String dev_uid,String dev_name,
			String dev_pwd,int videoQuality,int allAlarmState,int pushState,String serverData){
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("dev_nickname", dev_nickname);
		values.put("dev_uid", dev_uid);
		values.put("dev_name", dev_name);
		values.put("dev_pwd", dev_pwd);
		values.put("view_acc", dev_name);
		values.put("view_pwd", dev_pwd);
		values.put("dev_videoQuality", videoQuality);
		values.put("dev_alarmState", allAlarmState);
		values.put("dev_pushState", pushState);
		values.put("dev_serverData", serverData);
		db.update(TABLE_DEVICE, values, "dev_uid = '" + dev_uid + "'", null);

		db.close();
	}

	public void updateDeviceSnapshotByUID(String dev_uid,Bitmap snapshot){
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("snapshot",getByteArrayFromBitmap(snapshot));
		db.update(TABLE_DEVICE, values, "dev_uid='"+dev_uid+"'", null);
		db.close();
	}

	public void updateDeviceSnapshotByUID(String dev_uid,byte[] snapshot){
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("snapshot",snapshot);
		db.update(TABLE_DEVICE, values, "dev_uid='"+dev_uid+"'", null);
		db.close();
	}

	
	
	public void updateServerByUID(String dev_uid,String serverData){
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("dev_serverData",serverData);
		db.update(TABLE_DEVICE, values, "dev_uid='"+dev_uid+"'", null);
		db.close();
	}
	
	
	/*	public void getDeviceSnapshotByUID(String dev_uid){
			Bitmap bmp=null;
			SQLiteDatabase db=dbHelper.getReadableDatabase();
			Cursor cursor=db.query(TABLE_DEVICE, new String[]{"snapshot"}, 
					"dev_uid=", null, null, null, null);
			while(cursor.moveToNext()){
				byte[] snapshot=cursor.getBlob(0);
				if(snapshot!=null){
					bmp = DatabaseManager.getBitmapFromByteArray(snapshot);
				}
			}

	}*/

	public void removeDeviceByUID(String dev_uid){
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		db.delete(TABLE_DEVICE, "dev_uid='"+dev_uid+"'", null);
		db.close();
	}


	public void removeDeviceAlartEvent(String dev_uid) {

		SQLiteDatabase db =dbHelper.getWritableDatabase();
		db.delete(TABLE_ALARM_EVENT, "dev_uid = '" + dev_uid + "'", null);
		db.close();
	}

	public long addAlarmEvent(String dev_uid,int time,int type ){
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("dev_uid", dev_uid);
		values.put("time", time);
		values.put("type", type);
		long ret=db.insertOrThrow(TABLE_ALARM_EVENT, null, values);
		db.close();
		return ret;
	}


	public static Bitmap getBitmapFromByteArray(byte[] byts) {

		InputStream is = new ByteArrayInputStream(byts);
		return BitmapFactory.decodeStream(is, null, getBitmapOptions(2));
	}

	@SuppressWarnings("deprecation")
	public static BitmapFactory.Options getBitmapOptions(int scale) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inSampleSize = scale;

		return options;
	}

	public static byte[] getByteArrayFromBitmap(Bitmap bitmap){
		if(bitmap!=null&&!bitmap.isRecycled()){
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 0, bos);
			return bos.toByteArray();
		}else{
			return null;
		}
	}








	private class DatabaseHelper extends SQLiteOpenHelper{

		private static final String DB_FILE="HiChipCamera.db";
		private static final int DB_VERSION=10;
		//		private static final String SQLCMD_CREATE_TABLE_DEVICE="CREATE TABLE "+TABLE_DEVICE+" ( _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," //0 
		//				+"dev_nickname NVARCHAR(30) NULL , " //1
		//				+"dev_uid  NVARCHAR(20) NULL ," //2
		//				+"dev_name VARCHAR(30) NULL, " //3
		//				+"dev_pwd VARCHAR(30) NULL ," //4
		//				
		//				+"dev_videoQuality  INTEGER , " //5
		//				+"dev_alarmState INTEGER ," //6
		//				+"dev_pushState INTEGER ," //7
		//				+"snapshot BLOB "+" );"; //8


		private static final String SQLCMD_CREATE_TABLE_DEVICE = "CREATE TABLE " + TABLE_DEVICE + "(" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " 
				+ "dev_nickname			NVARCHAR(30) NULL, "   //1 
				+ "dev_uid				VARCHAR(20) NULL, "    //2
				+ "dev_name				VARCHAR(30) NULL, " //3
				+ "dev_pwd				VARCHAR(30) NULL, "//4
				+ "view_acc				VARCHAR(30) NULL, "//5
				+ "view_pwd				VARCHAR(30) NULL, "//6
				+ "event_notification 	INTEGER, " //7
				+ "ask_format_sdcard		INTEGER,"//8
				+ "camera_channel			INTEGER, "//9
				+ "snapshot				BLOB,"//13
				+ "dev_videoQuality				INTEGER,"//10 
				+ "dev_alarmState INTEGER,"//11
				+ "dev_pushState INTEGER,"//12
				+ "dev_serverData VARCHAR(30) NULL"//14
				+ ");";


		private static final String SQLCMD_DROP_TABLE_DEVICE="drop table if exists "+TABLE_DEVICE+";";


		//		private static final String SQLCMD_CREATE_TABLE_ALARM_EVENT = "CREATE TABLE " + TABLE_ALARM_EVENT + "(" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
		//		+ "dev_uid			VARCHAR(20) NULL, "
		//		+ "time			VARCHAR(80), "
		//		+ "type				INTEGER" + ");";
		//		09-26 15:52:56.034: E/AndroidRuntime(3313): android.database.sqlite.SQLiteException: near "exists": syntax error (code 1): , while compiling: drop table id exists  alarm_event ;

		private static final String SQLCMD_CREATE_TABLE_ALARM_EVENT="CREATE TABLE "+TABLE_ALARM_EVENT +" (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , " 
				+"dev_uid VARCHAR(20) NULL , "
				+"time INTEGER ,"
				+"type INTEGER "+" ) ";


		//		private static final String SQLCMD_CREATE_TABLE_PUSH="CREATE TABLE "+TABLE_PUSH +" (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , " 
		//				+"dev_uid VARCHAR(20) NULL ,"
		//				+ "dev_alarmState INTEGER ,"//11
		//				+ "dev_pushState INTEGER "//12
		//				+ ");";
		//		private static final String SQLCMD_DROP_TABLE_PUSH ="drop table if exists  "+ TABLE_PUSH+" ;";


		private static final String SQLCMD_ALTER_TABLE_ALARM = "ALTER TABLE "+TABLE_DEVICE+" ADD dev_alarmState INTEGER";
		private static final String SQLCMD_ALTER_TABLE_PUSH = "ALTER TABLE "+TABLE_DEVICE+" ADD dev_pushState INTEGER";
		private static final String SQLCMD_ALTER_TABLE_SERVER = "ALTER TABLE "+TABLE_DEVICE+" ADD dev_serverData VARCHAR(30) NULL";

		public DatabaseHelper(Context context) {
			super(context, DB_FILE, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			HiLog.e("DatabaseManager onCreate");

			db.execSQL(SQLCMD_CREATE_TABLE_DEVICE);
			db.execSQL(SQLCMD_CREATE_TABLE_ALARM_EVENT);
			//			db.execSQL(SQLCMD_CREATE_TABLE_PUSH);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			HiLog.e("DatabaseManager onUpgrade:"+db.getVersion());

			if(db.getVersion() <= 8){
				db.execSQL(SQLCMD_ALTER_TABLE_ALARM);
				db.execSQL(SQLCMD_ALTER_TABLE_PUSH);
			}
			if(db.getVersion() <= 9){
				db.execSQL(SQLCMD_ALTER_TABLE_SERVER);
				
			}

			
		}

	}
}

package com.thecamhi.activity.setting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hichip.R;
import com.hichip.base.HiLog;
import com.hichip.control.HiCamera;
import com.thecamhi.activity.LiveViewActivity;
import com.thecamhi.base.DatabaseManager;
import com.thecamhi.base.TitleView;
import com.thecamhi.bean.AlarmEvent;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;

public class AlarmEventActivity extends HiActivity {



	private List<AlarmEvent> event_list = new ArrayList<AlarmEvent>();
	private AlarmEventAdapter adapter;
	private ListView listView;

	private MyCamera mCamera;

	private Button btn_alarm_check;
	private Button btn_alarm_neglect;
	private LinearLayout lay_alarm_check;

	private int activity_type = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_event);

		Bundle bundle = this.getIntent().getExtras();
		String uid = bundle.getString(HiDataValue.EXTRAS_KEY_UID);
		activity_type = bundle.getInt("type");

		for(MyCamera camera: HiDataValue.CameraList) {
			if(camera.getUid().equals(uid)) {
				mCamera = camera;
				break;
			}
		}

		if(mCamera!=null){
			initAlarmData();
		}
		initView();

	}

	private void initView() {

		TitleView nb = (TitleView)findViewById(R.id.title_top);

		nb.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		nb.setTitle(getString(R.string.title_alarm_list));
		nb.setNavigationBarButtonListener(new TitleView.NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				// TODO Auto-generated method stub
				switch(which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					AlarmEventActivity.this.finish();
					break;
				}
			}
		});



		listView = (ListView) findViewById(R.id.list_scan_result);
		adapter = new AlarmEventAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

			}
		});


		btn_alarm_check = (Button)findViewById(R.id.btn_alarm_check);
		btn_alarm_neglect = (Button)findViewById(R.id.btn_alarm_neglect);
		lay_alarm_check = (LinearLayout)findViewById(R.id.lay_alarm_check);
		btn_alarm_check.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mCamera.getConnectState()==HiCamera.CAMERA_CONNECTION_STATE_LOGIN){
					Bundle extras = new Bundle();
					extras.putString(HiDataValue.EXTRAS_KEY_UID, mCamera.getUid());
					extras.putInt(HiDataValue.STYLE, mCamera.getStyle());
					Intent intent = new Intent();
					intent.putExtras(extras);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(AlarmEventActivity.this, LiveViewActivity.class);
					//					intent.setFlags(flags)
					startActivity(intent);


					HiDataValue.isOnLiveView = true;
					mCamera.setAlarmState(0);

					adapter.notifyDataSetChanged();
				}
			}
		});
		btn_alarm_neglect.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		if(activity_type == 0) {
			lay_alarm_check.setVisibility(View.GONE);
		}





	}




	private void initAlarmData() {

		event_list.clear();

		DatabaseManager manager = new DatabaseManager(this);
		SQLiteDatabase db = manager.getReadableDatabase();

		String[] args = {mCamera.getUid()};
		Cursor cursor = db.query(DatabaseManager.TABLE_ALARM_EVENT, new String[] { "_id", "dev_uid", "time","type" }, "dev_uid=?", args, null, null, "time desc");

		
		try {
			
			while (cursor.moveToNext()) {
				//			long db_id = cursor.getLong(0);
				String dev_uid = cursor.getString(1);
				int time = cursor.getInt(2);
				int type = cursor.getInt(3);

				HiLog.v( "dev_uid:"+dev_uid
						+ "  time:" + time
						+ "  type:" + type
						);

				event_list.add(new AlarmEvent(dev_uid, time, type));
			}

			
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			cursor.close();
			cursor=null;
			db.close();
		}
		
		
	}


	private class AlarmEventAdapter extends BaseAdapter {

		private LayoutInflater inflater;


		public AlarmEventAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return event_list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return event_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			AlarmEvent av = event_list.get(position);

			if(convertView == null) {
				convertView = inflater.inflate(R.layout.list_alarm_event, null);
				holder = new ViewHolder();
				holder.time = (TextView)convertView.findViewById(R.id.txt_time);
				holder.context = (TextView)convertView.findViewById(R.id.txt_context);
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder)convertView.getTag();
			}

			final SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			String time = df.format((long)av.time*1000);


			HiLog.v( 
					"getView  time:" + time  + "             av.time: "+av.time

					);

			holder.time.setText(time);
			holder.context.setText("Baby");


			return convertView;
		}

		public final class ViewHolder {
			public TextView time;
			public TextView context;
		}

	}









}

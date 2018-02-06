package com.thecamhi.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hichip.R;
import com.hichip.base.HiLog;
import com.hichip.tools.HiSearchSDK;
import com.hichip.tools.HiSearchSDK.HiSearchResult;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.TitleView;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;
import com.thecamhi.main.MainActivity;


public class SearchCameraActivity extends HiActivity{


	private Button btnBack;
	private ProgressBar prsbLoading;
	private LinearLayout layFailSearch;
	//	private Button btnFailSearch;
	private ListView listSearchResult;
	//private Button btnRefresh;
	//	private List<SearchResult> list = new ArrayList<SearchResult>();
	private SearchResultListAdapter adapter;
	private static final int HANDLE_MSG_LAN_SEARCH_END = 0;


	private HiSearchSDK searchSDK;
	private List<HiSearchResult> list = new ArrayList<HiSearchResult>();
	private static final int isCheckData=0*9995; 
	Message msg2;
	private long oldClickTime=0;
	//private String limit[]={"AAAA","BBBB","CCCC","DDDD","EEEE","FFFF","GGGG","HHHH","IIII","JJJJ","KKKK"};


	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//		setTitle(getText(R.string.dialog_AddCamera));
		setContentView(R.layout.activity_search_camera);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



		layFailSearch = (LinearLayout) findViewById(R.id.lay_fail_lan_search);
		prsbLoading = (ProgressBar) findViewById(R.id.progressBar2);
		/*	btnFailSearch = (Button) findViewById(R.id.btn_retry);
		btnFailSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(System.currentTimeMillis()-oldClickTime>=2000){
					startSearch();
					oldClickTime=System.currentTimeMillis();
				}
			}
		});


		/*		btnRefresh = (Button) findViewById(R.id.btn_refresh);
		btnRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(System.currentTimeMillis()-oldClickTime>=2000){
					startSearch();
					oldClickTime=System.currentTimeMillis();
				}


			}
		});*/


		listSearchResult = (ListView) findViewById(R.id.list_search_result);
		adapter = new SearchResultListAdapter(this);

		Log.v("hichip", "listSearchResult:"+listSearchResult+"  adapter:"+adapter);


		listSearchResult.setAdapter(adapter);

		listSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


				HiSearchResult r = list.get(position);



				Bundle extras = new Bundle();
				extras.putString(HiDataValue.EXTRAS_KEY_UID, r.uid);


				Intent intent = new Intent();
				intent.putExtras(extras);



				intent.setClass(SearchCameraActivity.this,
						AddCameraActivity.class);


				//setResult(常量，intent就把值传了过去)                
				SearchCameraActivity.this.setResult(RESULT_OK, intent);

				finish();


				//				SearchResult r = list.get(position);

				//				for (Camera camera : MainActivity.CameraList) {
				//					if (r.UID.equalsIgnoreCase(camera.getmDevUID())) {
				//						MainActivity.showAlert(AddCameraStep3Activity.this,
				//								getText(R.string.tips_warning),
				//								getText(R.string.tips_camera_exist),
				//								getText(R.string.ok));
				//
				//						return;	
				//						
				//					}
				//				}

				//				Bundle extras = new Bundle();
				//				extras.putString("dev_uid", r.UID);
				//				
				//				Intent intent = new Intent();
				//				intent.putExtras(extras);
				////				intent.setClass(AddCameraStep3Activity.this, AddCameraStep4Activity.class);
				//				startActivity(intent);


				//				
				//				Bundle extras = new Bundle();
				//				extras.putString("dev_uid", r.UID);
				//				
				//				Intent intent = new Intent();
				//				intent.putExtras(extras);
				//				intent.setClass(AddCameraStep3Activity.this, AddCameraStep7Activity.class);
				//				startActivity(intent);
				//				AddCameraStep3Activity.this.overridePendingTransition(R.anim.in_from_right,
				//						R.anim.out_to_left);

			}
		});




		TitleView nb = (TitleView)findViewById(R.id.title_top);

		nb.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		nb.setButton(TitleView.NAVIGATION_BUTTON_RIGHT);

		nb.setTitle(getString(R.string.add_camera));
		nb.setRightBtnText(getString(R.string.refresh));
		nb.setNavigationBarButtonListener(new TitleView.NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				// TODO Auto-generated method stub
				switch(which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					finish();
					break;
				case TitleView.NAVIGATION_BUTTON_RIGHT:

					if(System.currentTimeMillis()-oldClickTime>=2000){
						startSearch();
						oldClickTime=System.currentTimeMillis();
					}


					break;
				}
			}
		});


		//		Button title_btn_back = (Button) findViewById(R.id.title_btn_back);
		//		title_btn_back.setOnClickListener(new OnClickListener() {
		//			
		//			@Override
		//			public void onClick(View v) {
		//				AddCameraActivity.addCameraStep--;
		//				finish();
		//				overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
		//			}
		//		});
		//		
		//		Button title_btn_right = (Button) findViewById(R.id.title_btn_right);
		//		title_btn_right.setOnClickListener(new OnClickListener() {
		//			
		//			@Override
		//			public void onClick(View v) {
		//				Intent intent = new Intent();
		//				intent.setClass(AddCameraStep3Activity.this, MainActivity.class);
		//				startActivity(intent);
		//				finish();
		//				overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
		//				
		//			}
		//		});
		//		
		/*		TextView txtStepNum = (TextView)findViewById(R.id.text_step_num);
		txtStepNum.setText(String.valueOf(addCameraStep));*/
	}


	//	@Override
	//	public boolean onKeyDown(int keyCode, KeyEvent event) {
	//		
	//		switch (keyCode) {
	//		case KeyEvent.KEYCODE_BACK:
	//			AddCameraActivity.addCameraStep--;
	//			finish();
	//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
	//			break;
	//		}
	//		
	//		return super.onKeyDown(keyCode, event);
	//	}


	private CountDownTimer timer;

	private long oldRefreshTime;

	private void startSearch() {
		//使用searchSDK进行搜索


		/*if(msg2!=null){
			handler.dispatchMessage(msg2);
		}*/
		if(timer!=null){
			timer.cancel();
			timer=null;
		}
		oldRefreshTime=System.currentTimeMillis();
		int timeLong=20000;

		
		if(adapter!=null){
			list.clear();
			listSearchResult.requestLayout();  
			adapter.notifyDataSetChanged();
		}

		searchSDK= new HiSearchSDK(new HiSearchSDK.ISearchResult() {

			@Override
			public void onReceiveSearchResult(HiSearchResult result) {
				String temp = result.uid.substring(0, 4);
				
				for(String str:HiDataValue.limit){
					if(!temp.equalsIgnoreCase(str)){
						continue;
					}
					Message msg = handler.obtainMessage();
					msg.obj=result;
					msg.what = HiDataValue.HANDLE_MESSAGE_SCAN_RESULT;
					handler.sendMessage(msg);
				}
				
				
			}
		});
		searchSDK.search2();


		timer = new CountDownTimer(timeLong, 1000) {

			@Override
			public void onFinish() {

				if(list==null||list.size() == 0) {

					searchSDK.stop();
					layFailSearch.setVisibility(View.VISIBLE);
					prsbLoading.setVisibility(View.GONE);
				}
			}

			@Override
			public void onTick(long arg0) {

			}

		}.start();

		//timer.schedule(timerTask, 0, 300);


		/*	msg2 = handler.obtainMessage();
		msg2.what = HiDataValue.HANDLE_MESSAGE_SCAN_CHECK;
		msg2.arg1=isCheckData;
		handler.sendMessageDelayed(msg2, 6000);*/

		/*searchSDK = new HiSearchSDK(new HiSearchSDK.OnSearchResult() {
			@Override
			public void searchResult(List<HiSearchResult> arg0) {
				// TODO Auto-generated method stub
				HiLog.e("list size:"+arg0.size());
				list = arg0;
				Message msg = handler.obtainMessage();
				msg.what = HiDataValue.HANDLE_MESSAGE_SCAN_RESULT;
				handler.sendMessage(msg);

			}
		});

		searchSDK.searchWithTime(5);*/


		prsbLoading.setVisibility(View.VISIBLE);
		layFailSearch.setVisibility(View.GONE);
		list.clear();
		//	btnRefresh.setEnabled(false);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	
		searchSDK.stop();
		if(timer!=null){
			timer.cancel();
			timer=null;
		}
		
	}


	//	private void startSearch() {
	//		
	//		prsbLoading.setVisibility(View.VISIBLE);
	//		layFailSearch.setVisibility(View.GONE);
	//		btnRefresh.setEnabled(false);
	//		
	//		new Thread(new Runnable() {  
	//			  
	//            @Override  
	//            public void run() {  
	//                // TODO Auto-generated method stub  
	//            		list.clear();
	//            		
	//            		byte[] out = new byte[4096];
	//            		
	//            		SearchLib.SearchDevices(out,3);
	//            		
	//            		Log.v("result", "result:"+Packet.getHex(out, 4096));
	//            		
	//            		SearchDefines.HI_LAN_SEARCH_RESULT result = new SearchDefines.HI_LAN_SEARCH_RESULT(out);
	//            		Log.v("result", "result:"+result);
	//        			
	//        			if(result != null && result.search_info.length > 0) {
	//        				for(SearchDefines.HI_LAN_SEARCH_INFO info : result.search_info) {
	//        					list.add(new SearchResult(new String(info.uid).trim(), new String(info.ip).trim(), (int) info.port));
	//        				}
	//        			}
	//
	//        			out = null;
	//        			
	//        			Message msg = handler.obtainMessage();
	//        			msg.what = HANDLE_MSG_LAN_SEARCH_END;
	////        			msg.setData(bundle);
	//        			handler.sendMessage(msg);
	//        			
	//
	//            }  
	//        }).start(); 
	//	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		startSearch();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//			Bundle bundle = msg.getData();
			switch(msg.what) {
			case HiDataValue.HANDLE_MESSAGE_SCAN_RESULT:
			{

				HiSearchResult searchResult=(HiSearchResult)msg.obj;
				
				if(adapter!=null){
					list.add(searchResult);
					listSearchResult.requestLayout();
					adapter.notifyDataSetChanged();
					
				}
				prsbLoading.setVisibility(View.GONE);

				if(list!=null&&list.size() > 0&&layFailSearch.getVisibility()==View.VISIBLE) {
					layFailSearch.setVisibility(View.GONE);
				}

				//btnRefresh.setEnabled(true);


			}

			break;
			case HiDataValue.HANDLE_MESSAGE_SCAN_CHECK:
			{
				if(msg.arg1==isCheckData){


					if(list==null||list.size() == 0) {
						searchSDK.stop();
						layFailSearch.setVisibility(View.VISIBLE);
						prsbLoading.setVisibility(View.GONE);
					}

				}
			}
			break;
			}

		}

	};	













	//	private class SearchResult {
	//
	//		public String UID;
	//		public String IP;
	//
	//		// public int Port;
	//
	//		public SearchResult(String uid, String ip, int port) {
	//
	//			UID = uid;
	//			IP = ip;
	//			// Port = port;
	//		}
	//	}

	private class SearchResultListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public SearchResultListAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}
		//		public SearchResultListAdapter(LayoutInflater inflater) {
		//			this.mInflater = inflater;
		//		}

		public int getCount() {

			return list.size();
		}

		public Object getItem(int position) {

			return list.get(position);
		}

		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			final HiSearchResult result = (HiSearchResult) getItem(position);
			ViewHolder holder = null;

			if (convertView == null) {

				//				convertView = mInflater.inflate(R.layout.search_device_result, null);

				holder = new ViewHolder();
				//				holder.uid = (TextView) convertView.findViewById(R.id.uid);
				//				holder.ip = (TextView) convertView.findViewById(R.id.ip);





				convertView = mInflater.inflate(R.layout.list_scan_result, null);
				holder = new ViewHolder();
				holder.uid = (TextView)convertView.findViewById(R.id.txt_camera_uid);
				holder.ip=(TextView)convertView.findViewById(R.id.txt_camera_ip);

				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			holder.uid.setText(result.uid);
			holder.ip.setText(result.ip);
			holder.uid.setTextColor(Color.rgb(0x00, 0x00, 0x00));

			for (MyCamera camera : HiDataValue.CameraList) {
				if(camera.getUid().equals(result.uid)){
					holder.uid.setTextColor(Color.rgb(0x99, 0x99, 0x99));
					break;
				}
			}

			// holder.port.setText(result.Port);

			return convertView;
		}// getView()

		public final class ViewHolder {
			public TextView uid;
			public TextView ip;
		}
	}
}















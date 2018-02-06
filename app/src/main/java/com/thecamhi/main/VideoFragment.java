package com.thecamhi.main;

import com.hichip.R;
import com.thecamhi.activity.VideoLocalActivity;
import com.thecamhi.activity.VideoOnlineActivity;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.PictureFragment.PictureListAdapter;
import com.thecamhi.main.PictureFragment.PictureListAdapter.ViewHolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class VideoFragment extends Fragment implements OnClickListener{
	private View view;
	private static int LOCAL_VIDEO_MODEL=1;
	private static int ONLINE_VIDEO_MODEL=0;

	private PictureListAdapter pictureAdapter;
	private String[] state;
	private Button btn_local;
	private Button btn_online;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view=inflater.inflate(R.layout.fragment_video, null);
		initView();

		return view;
	}

	private void initView() {
		ListView picture_fragment_camera_list=(ListView)view.findViewById(R.id.video_fragment_camera_list);
		pictureAdapter=new PictureListAdapter(getActivity());
		picture_fragment_camera_list.setAdapter(pictureAdapter);
		picture_fragment_camera_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				MyCamera selectedCamera =  HiDataValue.CameraList.get(position);
				Bundle extras = new Bundle();
				extras.putString(HiDataValue.EXTRAS_KEY_UID, selectedCamera.getUid());
				Intent intent = new Intent();
				intent.putExtras(extras);
				//如果本地则跳到本地录像界面，远程则跳到online录像
				if(HiDataValue.model==ONLINE_VIDEO_MODEL){

					intent.setClass(getActivity(), VideoOnlineActivity.class);
				}else{
					intent.setClass(getActivity(), VideoLocalActivity.class);
				}
				startActivity(intent);

			}
		});

		btn_local=(Button)view.findViewById(R.id.btn_local);
		btn_local.setOnClickListener(this);
		btn_online=(Button)view.findViewById(R.id.btn_online);
		btn_online.setOnClickListener(this);


//		btn_online.setBackgroundResource(R.color.btn_bg_press);
//		btn_local.setBackgroundResource(R.color.title_middle);

		selectModel(HiDataValue.model);

		state=getResources().getStringArray(R.array.connect_state);
	}

	public void selectModel(int model){
		
		if(model==LOCAL_VIDEO_MODEL){
			btn_online.setSelected(false);
			btn_local.setSelected(true);
			pictureAdapter.notifyDataSetChanged();
			
		}else if(model==ONLINE_VIDEO_MODEL){
			btn_online.setSelected(true);
			btn_local.setSelected(false);
			pictureAdapter.notifyDataSetChanged();
			
		}


	}

	@Override
	public void onResume() {
		super.onResume();
		
	}





	protected class PictureListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		//	public VideoListAdapter(LayoutInflater layoutInflater) {
		//		this.mInflater = layoutInflater;
		//	}

		public PictureListAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);

			//		this.mContext = context;
			//		this.mInflater = layoutInflater;
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
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			 MyCamera cam = HiDataValue.CameraList.get(position);

			if (cam == null)
				return null;

			ViewHolder holder = null;

			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.list_video_camera, null);

				holder = new ViewHolder();
				//			holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.txt_video_camera_nike = (TextView) convertView.findViewById(R.id.txt_video_camera_nike);
				holder.txt_video_camera_uid = (TextView) convertView.findViewById(R.id.txt_video_camera_uid);
				holder.txt_video_camera_state=(TextView)convertView.findViewById(R.id.txt_video_camera_state);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			if(HiDataValue.model==ONLINE_VIDEO_MODEL){
				holder.txt_video_camera_state.setVisibility(View.VISIBLE);
				holder.txt_video_camera_state.setText(state[cam.getConnectState()]);
			}else{
				holder.txt_video_camera_state.setVisibility(View.GONE);
			}

			String uid=cam.getUid();

			if (holder != null) {
				
				holder.txt_video_camera_nike.setText(cam.getNikeName());
				holder.txt_video_camera_uid.setText(uid);
			}

			return convertView;

		}



		public final class ViewHolder {
			//		public ImageView img;
			public TextView txt_video_camera_nike;
			public TextView txt_video_camera_uid;
			public TextView txt_video_camera_state;
		}

	}




	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_local:
		{
			if(HiDataValue.model!=LOCAL_VIDEO_MODEL)
			{
				HiDataValue.model=LOCAL_VIDEO_MODEL;
				selectModel(HiDataValue.model);

			}
		}
		break;
		case R.id.btn_online:
		{
			if(HiDataValue.model!=ONLINE_VIDEO_MODEL)
			{

				HiDataValue.model=ONLINE_VIDEO_MODEL;
				selectModel(HiDataValue.model);
			}
		}
		break;

		}

	}

}

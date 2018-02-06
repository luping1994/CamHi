package com.thecamhi.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hichip.R;
import com.hichip.base.HiLog;
import com.thecamhi.base.HiTools;
import com.thecamhi.base.TitleView;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.bean.MyCamera;
import com.thecamhi.main.HiActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;



public class VideoLocalActivity extends HiActivity implements OnItemClickListener{


	private List<VideoInfo> video_list = new ArrayList<VideoInfo>();

	private ListView listViewVideo;
	private VideoInfoAdapter adapter;

	private String absolutePath;
	private MyCamera mCamera;

	private boolean delModel=false;
	private TitleView nb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_local);

		Bundle extras = this.getIntent().getExtras();
		String uid = extras.getString(HiDataValue.EXTRAS_KEY_UID); 




		for(MyCamera camera: HiDataValue.CameraList) {
			if(camera.getUid().equals(uid)) {
				this.mCamera=camera;
			}
		}

		String appname = getString(R.string.app_name);

		File folder = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()  + "/VideoRecoding/" + uid);


		absolutePath = folder.getAbsolutePath();

		setImagesPath(absolutePath);

		listViewVideo = (ListView) findViewById(R.id.list_video_local);
		listViewVideo.setOnItemClickListener(this);

		adapter = new VideoInfoAdapter(this);
		listViewVideo.setAdapter(adapter);
		adapter.notifyDataSetChanged();



		listViewVideo.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				deleteRecording(position);
				return true;
			}
		});





		initView();
	}

	private void initView() {
		nb = (TitleView)findViewById(R.id.title_top);

		nb.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		nb.setButton(TitleView.NAVIGATION_BUTTON_RIGHT);
		nb.setRightBtnText(getString(R.string.btn_edit));
		nb.setTitle(getString(R.string.title_local_video));

		nb.setNavigationBarButtonListener(new TitleView.NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				// TODO Auto-generated method stub
				switch(which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					finish();
					break;
				case TitleView.NAVIGATION_BUTTON_RIGHT:
					delModel=!delModel;
					if(delModel){
						nb.setRightBtnText(getString(R.string.finish));
					}else{
						nb.setRightBtnText(getString(R.string.btn_edit));
					}
					
					if(adapter!=null){
						adapter.notifyDataSetChanged();
					}
					
					break;
				}
			}
		});
	}






	public final synchronized void setImagesPath(String path) {
		video_list.clear();
		File folder = new File(path);
		String[] imageFiles = folder.list();

		if (imageFiles != null && imageFiles.length > 0) {
			Arrays.sort(imageFiles);
			for (String imageFile : imageFiles) {

				String abpath = path + "/" + imageFile;
				//				VIDEO_FILES.add(abpath);
				HiLog.v("abpath:"+abpath);

				int fileLen = 0;
				File dF = new File(abpath);
				FileInputStream fis;
				try {
					fis = new FileInputStream(dF);
					fileLen = fis.available();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long times = dF.lastModified();

				VideoInfo vi = new VideoInfo();
				vi.filename = imageFile;
				vi.fileLen = fileLen;
				vi.setTime(times);
				video_list.add(vi);
			}
			//			Collections.reverse(VIDEO_FILES);
		}
	}
	//
	private class VideoInfo {
		public String filename;
		public int fileLen;
		//		public long time;
		private String time;

		public String getTime() {
			return time;
		}
		public void setTime(long t) {
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			time = df.format(t);
		}
	}




	public class VideoInfoAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public VideoInfoAdapter(Context c) {
			// mContext = c;
			this.mInflater = LayoutInflater.from(c);

		}

		public int getCount() {
			return video_list.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		@SuppressLint("InflateParams")
		public View getView(int position, View convertView, ViewGroup parent) {

			//			String video_file = VIDEO_FILES.get(position);
			VideoInfo vi = video_list.get(position);

			if (vi == null) {
				return null;
			}

			ViewHolder holder = null;

			if (convertView == null) {

				convertView = mInflater
						.inflate(R.layout.list_video_local, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.txt_time);
				holder.size= (TextView) convertView.findViewById(R.id.txt_size);
				holder.uid=(TextView)convertView.findViewById(R.id.txt_uid);
				holder.delete_icon_local_video=(ImageView)convertView.findViewById(R.id.delete_icon_local_video);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if(delModel){
				holder.delete_icon_local_video.setVisibility(View.VISIBLE);
			}else{
				holder.delete_icon_local_video.setVisibility(View.GONE);
			}

			holder.uid.setText(mCamera.getUid());
			holder.name.setText(vi.filename);
			holder.size.setText(HiTools.formetFileSize(vi.fileLen));


			return convertView;
		}

		private final class ViewHolder {
			public TextView name;
			public TextView size;
			public TextView uid;
			public ImageView delete_icon_local_video;
		}


	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position,
			long id) {
		
		if(delModel){
			deleteRecording(position);
		}else{

			HiDataValue.ANDROID_VERSION=HiTools.getAndroidVersion();

			if(HiDataValue.ANDROID_VERSION>=7){
				HiLog.v("is  android  7");
				playbackInAndroid7(position);
			}else{
				playbackRecording(position);
			}
		}
	}
	private void playbackInAndroid7(int position){

		Intent intent = new Intent();  
		intent.setAction(android.content.Intent.ACTION_VIEW);  
		VideoInfo vi = video_list.get(position);
		String bpath = absolutePath+"/"+vi.filename;
		File file = new File(bpath); 
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);  
		Uri contentUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".FileProvider", file);  
		intent.setDataAndType(contentUri, "video/*");  
		startActivity(intent);  
	}
	private void playbackRecording(int position) {
		VideoInfo vi = video_list.get(position);
		Intent it = new Intent(Intent.ACTION_VIEW);
		String bpath = "file://" + absolutePath+"/"+vi.filename;
		Uri uri = Uri.parse(bpath);
		it.setDataAndType(uri, "video/*");

		Log.v("", "bpath:"+uri);
		startActivity(it);
	}



	private void deleteRecording(final int position){

		showYesNoDialog(R.string.tips_delete_video_local,new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// Yes button clicked
					File file = new File(absolutePath + "/" + video_list.get(position).filename);
					boolean deleted = file.delete();
					Log.v("hichip", "path:"+absolutePath + video_list.get(position).filename + "      delete:"+deleted);
					video_list.remove(position) ;
					adapter.notifyDataSetChanged();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					// No button clicked
					break;
				}
			}
		});

	}

}

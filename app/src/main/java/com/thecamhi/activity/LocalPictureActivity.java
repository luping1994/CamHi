package com.thecamhi.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.hichip.R;
import com.thecamhi.base.HiToast;
import com.thecamhi.base.TitleView;
import com.thecamhi.base.TitleView.NavigationBarButtonListener;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.main.HiActivity;
import com.thecamhi.widget.stickygridview.GridItem;
import com.thecamhi.widget.stickygridview.StickyGridAdapter;
import com.thecamhi.widget.stickygridview.YMComparator;


public class LocalPictureActivity extends HiActivity{
	private String uid;
	private static final int DEFAULT_LIST_SIZE = 20;
	final List<String> IMAGE_FILES = new ArrayList<String>(DEFAULT_LIST_SIZE);
	private List<GridItem> mGirdList = new ArrayList<GridItem>();
	private GridView gridview;
	private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
	private static int section = 1;
	private StickyGridAdapter adapter;
	private static final int ACTIVITY_RESULT_PHOTO_VIEW = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_picture);

		Bundle bundle=this.getIntent().getExtras();
		uid=bundle.getString(HiDataValue.EXTRAS_KEY_UID);
		initView();

	}
	private void initView() {
		mGirdList.clear();
		TitleView title=(TitleView)findViewById(R.id.title_top);
		title.setTitle(getResources().getString(R.string.title_local_picture));
		title.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		title.setNavigationBarButtonListener(new NavigationBarButtonListener() {
			
			@Override
			public void OnNavigationButtonClick(int which) {
				switch (which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					finish();
					break;
				}
			}
		});
		
		File folder = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/Snapshot/"+uid+"/");

		String imagesPath = folder.getAbsolutePath();
		setImagesPath(imagesPath);
		removeCorruptImage();
		
		gridview = (GridView)findViewById(R.id.asset_grid);
		Collections.sort(mGirdList, new YMComparator());

		for (ListIterator<GridItem> it = mGirdList.listIterator(); it.hasNext();) {
			GridItem mGridItem = it.next();
			String ym = mGridItem.getTime();
			if (!sectionMap.containsKey(ym)) {
				mGridItem.setSection(section);
				sectionMap.put(ym, section);
				section++;
			} else {
				mGridItem.setSection(sectionMap.get(ym));
			}
		}
		adapter = new StickyGridAdapter(LocalPictureActivity.this, mGirdList, gridview);
		gridview.setAdapter(adapter);
		
		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				 TODO Auto-generated method stub
				
				if(position<0){
					return;
				}
				
				 Intent intent = new Intent(LocalPictureActivity.this,
				 PhotoViewActivity.class);
				
				 String fileName = mGirdList.get(position).getPath();
				 String time = mGirdList.get(position).getTime();
				 intent.putExtra("filename", fileName);
				 intent.putExtra("time", time);
				 intent.putExtra("index", position);
				
				 startActivityForResult(intent,ACTIVITY_RESULT_PHOTO_VIEW);
				 
			}
		});
		
		gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				// TODO Auto-generated method stub
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							// Yes button clicked
							LocalPictureActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									File file = new File(mGirdList
											.get(position).getPath());
									file.delete();
									mGirdList.remove(position);
									adapter.notifyDataSetChanged();
								}
							});
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							// No button clicked
							break;
						}
					}
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(
						LocalPictureActivity.this);

				builder.setMessage(
						getResources().getString(
								R.string.tips_msg_delete_snapshot))
								.setTitle(R.string.tips_warning)
						.setPositiveButton(
								getResources().getString(R.string.btn_yes),
								dialogClickListener)
						.setNegativeButton(
								getResources().getString(R.string.btn_no),
								dialogClickListener).show();
				return true;
			}
		});

	}
	public final synchronized void setImagesPath(String path) {
		IMAGE_FILES.clear();
		File folder = new File(path);
		String[] imageFiles = folder.list();

		if (imageFiles != null && imageFiles.length > 0) {
			Arrays.sort(imageFiles);
			for (String imageFile : imageFiles) {
				// IMAGE_FILES.add(path+"/"+imageFile);
				File f = new File(path + "/" + imageFile);
				long times = f.lastModified() / 1000;

//				Log.v("hichip", "path:" + path + "/" + imageFile + "    time:"
//						+ times);
				GridItem mGridItem = new GridItem(path + "/" + imageFile,
						paserTimeToYM(times));
				mGirdList.add(mGridItem);

			}
			Collections.reverse(IMAGE_FILES);

		}
	}
	
	public final void removeCorruptImage() {
		Iterator<String> it = IMAGE_FILES.iterator();
		while (it.hasNext()) {
			String path = it.next();
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			// XXX: CA's hack, snapshot may fail and create corrupted bitmap
			if (bitmap == null) {
				it.remove();
			}
		}
	}

	
	private String paserTimeToYM(long time) {
		TimeZone tz = TimeZone.getDefault();
		TimeZone.setDefault(tz);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date(time * 1000L));
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ACTIVITY_RESULT_PHOTO_VIEW) {

			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				// String bundle.getString("filename");
				int position = bundle.getInt("index");
				mGirdList.remove(position);
				adapter.notifyDataSetChanged();
			}

		}
	}
	
	
}

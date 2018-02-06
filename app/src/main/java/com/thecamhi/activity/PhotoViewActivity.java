package com.thecamhi.activity;

import java.io.File;

import com.hichip.R;
import com.hichip.widget.photoview.PhotoViewAttacher;
import com.thecamhi.base.TitleView;
import com.thecamhi.main.HiActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;



public class PhotoViewActivity extends HiActivity{

	 private String mFileName;
	 
	 Button btnCancel;


	 Bitmap bitmap;


	ImageView mImageView;
	PhotoViewAttacher mAttacher;
	String time;
	int img_index;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取图片资源
		System.gc();
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		mFileName = extras.getString("filename");
		time = extras.getString("time");
		img_index = extras.getInt("index");
		
		
		bitmap = BitmapFactory.decodeFile(mFileName);// ,bfo);
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setupViewInPortraitLayout();
		}
		else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setupViewInLandscapeLayout();
		}
		
		

	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);


		Configuration cfg = getResources().getConfiguration();

		if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setupViewInLandscapeLayout();
		} else if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
			setupViewInPortraitLayout();
		}
	}
	
	
	private void setupViewInLandscapeLayout() {

		WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(wlp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		
		setContentView(R.layout.activity_photoview_landscape);
		
		// Any implementation of ImageView can be used!
	    mImageView = (ImageView) findViewById(R.id.img_photo);

	    // Set the Drawable displayed
	    mImageView.setImageBitmap(bitmap);

	    // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
	    mAttacher = new PhotoViewAttacher(mImageView);
		
	}

	private void setupViewInPortraitLayout() {
		WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(wlp);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        
        
		setContentView(R.layout.activity_photoview_portrait);
		
		
		
		TitleView nb = (TitleView)findViewById(R.id.title_top);
		nb.setTitle(time);
		nb.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		nb.setButton(TitleView.NAVIGATION_BUTTON_RIGHT);
		nb.setRightBtnText(getString(R.string.delete));
		nb.setNavigationBarButtonListener(new TitleView.NavigationBarButtonListener() {
			
			@Override
			public void OnNavigationButtonClick(int which) {
				// TODO Auto-generated method stub
				switch(which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					Intent intent = new Intent();
					setResult(RESULT_CANCELED, intent);
					finish();
					break;
				case TitleView.NAVIGATION_BUTTON_RIGHT:
					showYesNoDialog(R.string.tips_msg_delete_snapshot, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								// Yes button clicked
								PhotoViewActivity.this
										.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												File file = new File(mFileName);
												file.delete();
												Intent intent = new Intent();
												intent.putExtra("filename", mFileName);
												intent.putExtra("index", img_index);
												setResult(RESULT_OK, intent);
												finish();
											}
										});
								break;
	
							case DialogInterface.BUTTON_NEGATIVE:
								// No button clicked
								break;
							}
						}
					});
					
					break;
				}
			}
		});
		
	    mImageView = (ImageView) findViewById(R.id.img_photo);

	    mImageView.setImageBitmap(bitmap);

	    mAttacher = new PhotoViewAttacher(mImageView);
		
	}
}
package com.thecamhi.base;

import com.hichip.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleView extends RelativeLayout implements OnClickListener{
	private Context context;
	public static final int NAVIGATION_BUTTON_LEFT = 0;
	public static final int NAVIGATION_BUTTON_RIGHT = 1;
	public static final int NAVIGATION_IMAGEVIEW_RIGHT = 2;
	private NavigationBarButtonListener btnListener;

	public TitleView(Context context) {
		super(context);
		initView(context);
	}

	public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		this.context=context;
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.title_view_in_all_main_view, this,true);
	}

	public void setTitle(String txt){
		TextView tv=(TextView)findViewById(R.id.title_middle);
		tv.setVisibility(View.VISIBLE);
		tv.setText(txt);
	}

	public void setRightBtnText(String str){
		TextView tv=(TextView)findViewById(R.id.btn_finish);
		tv.setText(str);
	}
	public void setRightBtnTextSide(int sp){
		TextView tv=(TextView)findViewById(R.id.btn_finish);
		tv.setTextSize(sp);
	}
	public void setRightImageView(Drawable drawable){

		ImageView iv=(ImageView)findViewById(R.id.iv_refresh);

		iv.setImageDrawable(drawable);
	}


	public void setButton(int which){
		/*Button oldBtn=(Button)this.findViewWithTag((Integer)which);
		if(oldBtn!=null)
			this.removeView(oldBtn);
		 */

		switch (which) {
		case NAVIGATION_BUTTON_LEFT:
		{
			Button btn=(Button)findViewById(R.id.btn_return);
			btn.setTag(which);
			btn.setVisibility(View.VISIBLE);
			btn.setOnClickListener(this);
		}
		break;
		
		case NAVIGATION_BUTTON_RIGHT:
			Button btn=(Button)findViewById(R.id.btn_finish);
			btn.setTag(which);
			btn.setVisibility(View.VISIBLE);
			btn.setOnClickListener(this);
			break;
			
		case NAVIGATION_IMAGEVIEW_RIGHT:

			ImageView iv=(ImageView)findViewById(R.id.iv_refresh);
			iv.setTag(which);
			iv.setVisibility(View.VISIBLE);
			iv.setOnClickListener(this);
			break;



		}


	}

	@Override
	public void onClick(View v) {
		int which=((Integer) v.getTag()).intValue();
		if(btnListener!=null){
			btnListener.OnNavigationButtonClick(which);
		}

	}

	public interface NavigationBarButtonListener{

		public void OnNavigationButtonClick(int which);
	}

	public void setNavigationBarButtonListener(NavigationBarButtonListener listener) {
		btnListener = listener;
	}


}

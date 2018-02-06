package com.thecamhi.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;

import com.hichip.R;

public class HiActivity extends Activity{
	protected ProgressDialog progressDialog;
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			overridePendingTransition(R.anim.in_from_right,
					R.anim.out_to_left);
			break;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void startActivity(Intent intent) {
		// TODO Auto-generated method stub
		super.startActivity(intent);
		overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}
	
	public void showYesNoDialog(int msg,DialogInterface.OnClickListener listener) {
		
//		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				switch (which) {
//				case DialogInterface.BUTTON_POSITIVE:
//					// Yes button clicked
//					break;
//
//				case DialogInterface.BUTTON_NEGATIVE:
//					// No button clicked
//					break;
//				}
//			}
//		};
		AlertDialog.Builder builder = new AlertDialog.Builder(
				HiActivity.this);

		builder.setMessage(
				getResources().getString(
						msg))
				.setTitle(R.string.tips_warning)		
				.setPositiveButton(
						getResources().getString(R.string.btn_yes),
						listener)
				.setNegativeButton(
						getResources().getString(R.string.btn_no),
						listener).show();
		
	}
	
	
	
	
	public void showAlert(CharSequence message) {

		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(this);
		dlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dlgBuilder.setTitle(R.string.tips_warning);
		dlgBuilder.setMessage(message);
		dlgBuilder.setPositiveButton(getText(R.string.btn_ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
	}
	
	public void showAlert(CharSequence message,DialogInterface.OnClickListener listener,boolean cancelable) {

		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(this);
		dlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dlgBuilder.setTitle(R.string.tips_warning);
		dlgBuilder.setMessage(message);
		dlgBuilder.setCancelable(cancelable);
		dlgBuilder.setPositiveButton(getText(R.string.btn_ok), listener).show();
	}
	
	public interface MyDismiss{
		public void OnDismiss();
	}
	MyDismiss myDismiss;
	
	public void setOnLoadingProgressDismissListener(HiActivity.MyDismiss dismiss){
		this.myDismiss=dismiss;
	}
	
	public void showLoadingProgress(){
		progressDialog=new ProgressDialog(HiActivity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setMessage(getText(R.string.tips_loading));
		
		progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {  
			  
            @Override  
            public void onDismiss(DialogInterface dialog) {  
              if(myDismiss!=null){
            	  myDismiss.OnDismiss();
              }
  
            }  
        });
		progressDialog.show();
	}
	
	
	
	
	public void dismissLoadingProgress() {
		if(progressDialog!=null) {
			progressDialog.cancel();
		}
	}
}

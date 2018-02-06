package com.thecamhi.main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.app.ListFragment;

import com.hichip.R;
import com.thecamhi.main.HiActivity.MyDismiss;

public class HiFragment extends ListFragment{
	protected ProgressDialog progressDialog;
	MyDismiss myDismiss;
	
	public void showLoadingProgress(){
		progressDialog=new ProgressDialog(getActivity());
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

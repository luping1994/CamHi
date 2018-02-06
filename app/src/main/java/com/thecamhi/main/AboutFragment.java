package com.thecamhi.main;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hichip.R;
import com.hichip.content.HiChipDefines;
import com.hichip.sdk.HiChipSDK;
import com.thecamhi.base.TitleView;

public class AboutFragment extends Fragment{
	private View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view=inflater.inflate(R.layout.fragment_about, null);

		initView();
		return view;
	}

	private void initView() {
		TitleView title=(TitleView)view.findViewById(R.id.title_top);
		title.setTitle(getResources().getString(R.string.title_about_fragment));

		PackageManager manager = getActivity().getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(getActivity().getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String version=" ";
		if(info!=null){
			version = info.versionName;
		}
		
		TextView app_version_tv=(TextView)view.findViewById(R.id.app_version_tv);
		app_version_tv.setText(version);
		
		TextView txt_SDK_version=(TextView)view.findViewById(R.id.txt_SDK_version);
		txt_SDK_version.setText(HiChipSDK.getSDKVersion());
		
		
	}

}

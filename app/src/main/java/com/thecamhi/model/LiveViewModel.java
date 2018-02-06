package com.thecamhi.model;

import com.hichip.R;
import com.hichip.content.HiChipDefines;
import com.hichip.sdk.HiChipP2P;
import com.tencent.mid.a.m;
import com.thecamhi.base.HiToast;
import com.thecamhi.bean.MyCamera;

import android.content.Context;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 直播界面的model
 * @author lt
 */
public class LiveViewModel implements TextWatcher, OnClickListener {
	private static  LiveViewModel instance = null;
	private static final Object LOCK = new Object();
	private TextView tvNum;
	private LinearLayout mLlKeyBoard;
	private boolean isShowing = true;
	private String roomInput;
	private Context mContext;
	private MyCamera mMyCamera;
	public static int select_preset;
	public int mFlagPreset=0;   //1-设置  2-调用
	private static String mUid;

	private LiveViewModel() {
	}

	public  static LiveViewModel getInstance() {
		if (instance == null) {
			synchronized (LOCK) {
				instance=new LiveViewModel();
			}
		}
		return instance;
	}

	/**
	 * 预置位弹出的数字键盘
	 * 
	 * @param numKeyBoard
	 */
	public void handKeyBoard(Context context, View numKeyBoard, MyCamera myCamera) {
		this.mContext = context;
		this.mMyCamera = myCamera;
		tvNum = (TextView) numKeyBoard.findViewById(R.id.pnk_num);
		if(select_preset!=0&&mUid==myCamera.getUid()){
			tvNum.setText(select_preset+"");
		}else {
			select_preset=0;
		}
		mUid=myCamera.getUid();
		mLlKeyBoard = (LinearLayout) numKeyBoard.findViewById(R.id.ll_keyboard);
		tvNum.addTextChangedListener(this);
		tvNum.setOnClickListener(this);
		Button btnOne = (Button) numKeyBoard.findViewById(R.id.btn_one);
		Button btnTwo = (Button) numKeyBoard.findViewById(R.id.btn_two);
		Button btnThere = (Button) numKeyBoard.findViewById(R.id.btn_there);
		Button btnFour = (Button) numKeyBoard.findViewById(R.id.btn_four);
		Button btnFive = (Button) numKeyBoard.findViewById(R.id.btn_five);
		Button btnSix = (Button) numKeyBoard.findViewById(R.id.btn_six);
		Button btnSeven = (Button) numKeyBoard.findViewById(R.id.btn_seven);
		Button btnEight = (Button) numKeyBoard.findViewById(R.id.btn_eight);
		Button btnNight = (Button) numKeyBoard.findViewById(R.id.btn_nine);
		Button btnZero = (Button) numKeyBoard.findViewById(R.id.btn_zero);
		Button btnDone = (Button) numKeyBoard.findViewById(R.id.btn_done);
		Button btnDelete = (Button) numKeyBoard.findViewById(R.id.btn_delete);

		Button btn_set = (Button) numKeyBoard.findViewById(R.id.btn_preset_set);
		Button btn_call = (Button) numKeyBoard.findViewById(R.id.btn_preset_call);
		btn_set.setOnClickListener(this);
		btn_call.setOnClickListener(this);
		btnOne.setOnClickListener(this);
		btnTwo.setOnClickListener(this);
		btnThere.setOnClickListener(this);
		btnFour.setOnClickListener(this);
		btnFive.setOnClickListener(this);
		btnSix.setOnClickListener(this);
		btnSeven.setOnClickListener(this);
		btnEight.setOnClickListener(this);
		btnNight.setOnClickListener(this);
		btnZero.setOnClickListener(this);
		btnDone.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pnk_num:
			if (isShowing) {
				mLlKeyBoard.setVisibility(View.VISIBLE);
			} else {
				mLlKeyBoard.setVisibility(View.GONE);
			}
			isShowing = !isShowing;
			break;
		case R.id.btn_one:
			roomInput = tvNum.getText().toString().trim();
			tvNum.setText(roomInput + "1");
			break;
		case R.id.btn_two:
			roomInput = tvNum.getText().toString().trim();
			tvNum.setText(roomInput + "2");
			break;
		case R.id.btn_there:
			roomInput = tvNum.getText().toString().trim();
			tvNum.setText(roomInput + "3");
			break;
		case R.id.btn_four:
			roomInput = tvNum.getText().toString().trim();
			tvNum.setText(roomInput + "4");
			break;
		case R.id.btn_five:
			roomInput = tvNum.getText().toString().trim();
			tvNum.setText(roomInput + "5");
			break;
		case R.id.btn_six:
			roomInput = tvNum.getText().toString().trim();
			tvNum.setText(roomInput + "6");
			break;
		case R.id.btn_seven:
			roomInput = tvNum.getText().toString().trim();
			tvNum.setText(roomInput + "7");
			break;
		case R.id.btn_eight:
			roomInput = tvNum.getText().toString().trim();
			tvNum.setText(roomInput + "8");
			break;
		case R.id.btn_nine:
			roomInput = tvNum.getText().toString().trim();
			tvNum.setText(roomInput + "9");
			break;
		case R.id.btn_zero:
			roomInput = tvNum.getText().toString().trim();
			tvNum.setText(roomInput + "0");
			break;
		case R.id.btn_delete:
			tvNum.setText("");
			break;
		case R.id.btn_done:
			mLlKeyBoard.setVisibility(View.GONE);
			break;
		case R.id.btn_preset_call:
			if(!TextUtils.isEmpty(tvNum.getText().toString().trim())){
				select_preset=Integer.parseInt(tvNum.getText().toString().trim());
				mMyCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_PRESET, HiChipDefines.HI_P2P_S_PTZ_PRESET.parseContent(
						HiChipP2P.HI_P2P_SE_CMD_CHN, HiChipDefines.HI_P2P_PTZ_PRESET_ACT_CALL, select_preset));
				mFlagPreset=2;
			}
			break;
		case R.id.btn_preset_set:
			String str=tvNum.getText().toString().trim();
			if(TextUtils.isEmpty(str)||Integer.parseInt(str)>255||"0".equals((str.charAt(0)+""))){
				HiToast.showToast(mContext, mContext.getString(R.string.tip_perset_toast));
			}else {
				mMyCamera.sendIOCtrl(HiChipDefines.HI_P2P_SET_PTZ_PRESET,
	                        HiChipDefines.HI_P2P_S_PTZ_PRESET.parseContent(HiChipP2P.HI_P2P_SE_CMD_CHN,
	                                HiChipDefines.HI_P2P_PTZ_PRESET_ACT_SET, select_preset));
				mFlagPreset=1;
	           // HiToast.showToast(mContext, mContext.getString(R.string.tips_preset_set_btn));
			}
			break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		if (!TextUtils.isEmpty(s.toString())) {
			Integer num = Integer.parseInt(s.toString());
			select_preset=num;
			if (num == 0 || num > 255) {
				HiToast.showToast(mContext, mContext.getString(R.string.tip_perset_toast));
			}
		}

	}
}

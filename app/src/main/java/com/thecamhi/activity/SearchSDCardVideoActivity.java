package com.thecamhi.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.hichip.R;
import com.hichip.content.HiChipDefines;
import com.thecamhi.base.HiTools;
import com.thecamhi.base.TitleView;
import com.thecamhi.base.TitleView.NavigationBarButtonListener;
import com.thecamhi.bean.HiDataValue;
import com.thecamhi.main.HiActivity;

public class SearchSDCardVideoActivity extends HiActivity implements OnClickListener{
	
	private PopupWindow mPopupWindow;
	private Calendar mStartSearchCalendar;
	private Calendar mStopSearchCalendar;
	private int mSearchEventType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_sdcard_video);

		initView();
	}

	private void initView() {
		TitleView title=(TitleView)findViewById(R.id.title_top);
			title.setTitle(getResources().getString(R.string.title_search_video));
		title.setButton(TitleView.NAVIGATION_BUTTON_LEFT);
		title.setNavigationBarButtonListener(new NavigationBarButtonListener() {

			@Override
			public void OnNavigationButtonClick(int which) {
				switch (which) {
				case TitleView.NAVIGATION_BUTTON_LEFT:
					SearchSDCardVideoActivity.this.finish();
					break;
				case TitleView.NAVIGATION_BUTTON_RIGHT:

					break;

				}

			}
		});

		LinearLayout sd_card_hour_ll=(LinearLayout)findViewById(R.id.sd_card_hour_ll);
		sd_card_hour_ll.setOnClickListener(this);
		LinearLayout sd_card_half_day_ll=(LinearLayout)findViewById(R.id.sd_card_half_day_ll);
		sd_card_half_day_ll.setOnClickListener(this);
		LinearLayout sd_card_a_day_ll=(LinearLayout)findViewById(R.id.sd_card_a_day_ll);
		sd_card_a_day_ll.setOnClickListener(this);
		LinearLayout sd_card_week_ll=(LinearLayout)findViewById(R.id.sd_card_week_ll);
		sd_card_week_ll.setOnClickListener(this);
		LinearLayout sd_card_self_ll=(LinearLayout)findViewById(R.id.sd_card_self_ll);
		sd_card_self_ll.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sd_card_hour_ll:
		{	
			Bundle bundle=new Bundle();
			bundle.putLong(VideoOnlineActivity.SEARCH_ACTIVITY_START_TIME, System.currentTimeMillis()-60*60*1000);
			bundle.putLong(VideoOnlineActivity.SEARCH_ACTIVITY_END_TIME, System.currentTimeMillis());
			Intent intent=new Intent(SearchSDCardVideoActivity.this,VideoOnlineActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_DATA, bundle);
			SearchSDCardVideoActivity.this.setResult(RESULT_OK,intent);
			finish();
		}

		break;
		case R.id.sd_card_half_day_ll:
		{	
			Bundle bundle=new Bundle();
			bundle.putLong(VideoOnlineActivity.SEARCH_ACTIVITY_START_TIME, System.currentTimeMillis()-12*60*60*1000);
			bundle.putLong(VideoOnlineActivity.SEARCH_ACTIVITY_END_TIME, System.currentTimeMillis());
			Intent intent=new Intent(SearchSDCardVideoActivity.this,VideoOnlineActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_DATA, bundle);
			SearchSDCardVideoActivity.this.setResult(RESULT_OK,intent);
			finish();
		}

		break;
		case R.id.sd_card_a_day_ll:
		{	
			Bundle bundle=new Bundle();
			bundle.putLong(VideoOnlineActivity.SEARCH_ACTIVITY_START_TIME, System.currentTimeMillis()-24*60*60*1000);
			bundle.putLong(VideoOnlineActivity.SEARCH_ACTIVITY_END_TIME, System.currentTimeMillis());
			Intent intent=new Intent(SearchSDCardVideoActivity.this,VideoOnlineActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_DATA, bundle);
			SearchSDCardVideoActivity.this.setResult(RESULT_OK,intent);
			finish();
		}

		break;
		case R.id.sd_card_week_ll:
		{	
			Bundle bundle=new Bundle();
			bundle.putLong(VideoOnlineActivity.SEARCH_ACTIVITY_START_TIME, System.currentTimeMillis()-7*24*60*60*1000);
			bundle.putLong(VideoOnlineActivity.SEARCH_ACTIVITY_END_TIME, System.currentTimeMillis());
			Intent intent=new Intent(SearchSDCardVideoActivity.this,VideoOnlineActivity.class);
			intent.putExtra(HiDataValue.EXTRAS_KEY_DATA, bundle);
			SearchSDCardVideoActivity.this.setResult(RESULT_OK,intent);
			finish();
		}

		break;
		case R.id.sd_card_self_ll:
		{
			showSearchEventPopView();
			
		}
			break;

		}

	}

	private void showSearchEventPopView() {
		View customView = getLayoutInflater().inflate(R.layout.popview_search_event,
				null, false);
		
		final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		final AlertDialog dlgBuilder = dlg.create();
		dlgBuilder.setView(customView);
		dlgBuilder.setTitle(R.string.title_search_dialog);
		
		
//		final Spinner spinEventType = (Spinner) customView.findViewById(R.id.spinner_search_event_video);
		final Button btnStartDate = (Button) customView.findViewById(R.id.from_data_btn);
		final Button btnStartTime = (Button) customView.findViewById(R.id.from_time_btn);
		final Button btnStopDate = (Button) customView.findViewById(R.id.to_data_btn);
		final Button btnStopTime = (Button) customView.findViewById(R.id.to_time_btn);
		Button btnOK = (Button) customView.findViewById(R.id.seach_event_ok);
		Button btnCancel = (Button) customView.findViewById(R.id.seach_event_cancel);

		// set button
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

		SearchSDCardVideoActivity.this.mStartSearchCalendar = Calendar.getInstance();
		SearchSDCardVideoActivity.this.mStartSearchCalendar.set(Calendar.SECOND, 0);
		SearchSDCardVideoActivity.this.mStopSearchCalendar = Calendar.getInstance();
		SearchSDCardVideoActivity.this.mStopSearchCalendar.set(Calendar.SECOND, 0);

		btnStartDate.setText(dateFormat.format(SearchSDCardVideoActivity.this.mStartSearchCalendar.getTime()));
		btnStartTime.setText(timeFormat.format(SearchSDCardVideoActivity.this.mStartSearchCalendar.getTime()));
		btnStopDate.setText(dateFormat.format(SearchSDCardVideoActivity.this.mStopSearchCalendar.getTime()));
		btnStopTime.setText(timeFormat.format(SearchSDCardVideoActivity.this.mStopSearchCalendar.getTime()));

		// set spinner adapter & listener
		/*ArrayAdapter<CharSequence> adapter = ArrayAdapter
				.createFromResource(SearchSDCardVideoActivity.this, R.array.event_type, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		*/
		
	/*	spinEventType.setAdapter(adapter);
		spinEventType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				SearchSDCardVideoActivity.this.mSearchEventType = position;

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});*/

		final DatePickerDialog.OnDateSetListener startDateOnDateSetListener = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

				SearchSDCardVideoActivity.this.mStartSearchCalendar.set(year, monthOfYear, dayOfMonth,
						SearchSDCardVideoActivity.this.mStartSearchCalendar.get(Calendar.HOUR_OF_DAY),
						SearchSDCardVideoActivity.this.mStartSearchCalendar.get(Calendar.MINUTE), 0);

				btnStartDate.setText(dateFormat.format(SearchSDCardVideoActivity.this.mStartSearchCalendar.getTime()));

				// todo:
				// if start time > stop time , then stop time = start time
				//

				if (SearchSDCardVideoActivity.this.mStartSearchCalendar.after(SearchSDCardVideoActivity.this.mStopSearchCalendar)) {

					SearchSDCardVideoActivity.this.mStopSearchCalendar.setTimeInMillis(SearchSDCardVideoActivity.this.mStartSearchCalendar.getTimeInMillis());
					btnStopDate.setText(dateFormat.format(SearchSDCardVideoActivity.this.mStopSearchCalendar.getTime()));
					btnStopTime.setText(timeFormat.format(SearchSDCardVideoActivity.this.mStopSearchCalendar.getTime()));
				}
			}
		};

		final DatePickerDialog.OnDateSetListener stopDateOnDateSetListener = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

				// todo:
				// let tmp = after set stop time
				// if tmp time < start time, do nothing.
				//
				Calendar tmp = Calendar.getInstance();
				tmp.set(year, monthOfYear, dayOfMonth, SearchSDCardVideoActivity.this.mStopSearchCalendar.get(Calendar.HOUR_OF_DAY),
						SearchSDCardVideoActivity.this.mStopSearchCalendar.get(Calendar.MINUTE), 0);

				if (tmp.after(SearchSDCardVideoActivity.this.mStartSearchCalendar) || tmp.equals(SearchSDCardVideoActivity.this.mStartSearchCalendar)) {

					SearchSDCardVideoActivity.this.mStopSearchCalendar.set(year, monthOfYear, dayOfMonth,
							SearchSDCardVideoActivity.this.mStopSearchCalendar.get(Calendar.HOUR_OF_DAY),
							SearchSDCardVideoActivity.this.mStopSearchCalendar.get(Calendar.MINUTE), 0);

					btnStopDate.setText(dateFormat.format(SearchSDCardVideoActivity.this.mStopSearchCalendar.getTime()));

				}
			}
		};

		final TimePickerDialog.OnTimeSetListener startTimeOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

				SearchSDCardVideoActivity.this.mStartSearchCalendar.set(SearchSDCardVideoActivity.this.mStartSearchCalendar.get(Calendar.YEAR),
						SearchSDCardVideoActivity.this.mStartSearchCalendar.get(Calendar.MONTH),
						SearchSDCardVideoActivity.this.mStartSearchCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);

				btnStartTime.setText(timeFormat.format(SearchSDCardVideoActivity.this.mStartSearchCalendar.getTime()));

				// todo:
				// if start time > stop time , then stop time = start time
				//
				if (SearchSDCardVideoActivity.this.mStartSearchCalendar.after(SearchSDCardVideoActivity.this.mStopSearchCalendar)) {

					SearchSDCardVideoActivity.this.mStopSearchCalendar.setTimeInMillis(SearchSDCardVideoActivity.this.mStartSearchCalendar.getTimeInMillis());
					btnStopTime.setText(timeFormat.format(SearchSDCardVideoActivity.this.mStopSearchCalendar.getTime()));
				}
			}
		};

		final TimePickerDialog.OnTimeSetListener stopTimeOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

				// todo:
				// let tmp = after set stop time
				// if tmp time < start time, do nothing.
				//
				Calendar tmp = Calendar.getInstance();
				tmp.set(SearchSDCardVideoActivity.this.mStopSearchCalendar.get(Calendar.YEAR), SearchSDCardVideoActivity.this.mStopSearchCalendar.get(Calendar.MONTH),
						SearchSDCardVideoActivity.this.mStopSearchCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);

				if (tmp.after(SearchSDCardVideoActivity.this.mStartSearchCalendar) || tmp.equals(SearchSDCardVideoActivity.this.mStartSearchCalendar)) {

					SearchSDCardVideoActivity.this.mStopSearchCalendar.set(SearchSDCardVideoActivity.this.mStopSearchCalendar.get(Calendar.YEAR),
							SearchSDCardVideoActivity.this.mStopSearchCalendar.get(Calendar.MONTH),
							SearchSDCardVideoActivity.this.mStopSearchCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);

					btnStopTime.setText(timeFormat.format(SearchSDCardVideoActivity.this.mStopSearchCalendar.getTime()));
				}
			}
		};

		btnStartDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Calendar cal = Calendar.getInstance();

				new DatePickerDialog(SearchSDCardVideoActivity.this, startDateOnDateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal
						.get(Calendar.DAY_OF_MONTH)).show();
			}
		});

		btnStartTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Calendar cal = Calendar.getInstance();

				new TimePickerDialog(SearchSDCardVideoActivity.this, startTimeOnTimeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false)
						.show();
			}
		});

		btnStopDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Calendar cal = Calendar.getInstance();

				new DatePickerDialog(SearchSDCardVideoActivity.this, stopDateOnDateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal
						.get(Calendar.DAY_OF_MONTH)).show();
			}
		});

		btnStopTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Calendar cal = Calendar.getInstance();

				new TimePickerDialog(SearchSDCardVideoActivity.this, stopTimeOnTimeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false)
						.show();
			}
		});

		btnOK.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//20130725 chun 
//				DatabaseManager manager = new DatabaseManager(SearchEventActivity.this);
//				manager.addSearchHistory(SearchEventActivity.this.mDevUID, SearchEventActivity.this.mSearchEventType, mStartSearchCalendar.getTimeInMillis(),
//						mStopSearchCalendar.getTimeInMillis());

				Bundle extras = new Bundle();
			//	extras.putInt("event_type", SearchSDCardVideoActivity.this.mSearchEventType);
				extras.putLong(VideoOnlineActivity.SEARCH_ACTIVITY_START_TIME, mStartSearchCalendar.getTimeInMillis());
				extras.putLong(VideoOnlineActivity.SEARCH_ACTIVITY_END_TIME, mStopSearchCalendar.getTimeInMillis());

				Intent intent = new Intent();
				intent.putExtra(HiDataValue.EXTRAS_KEY_DATA,extras);
				setResult(RESULT_OK, intent);
				finish();

				dlgBuilder.dismiss();
				
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				dlgBuilder.dismiss();
			}
		});
		
		dlgBuilder.show();
		
	}


}

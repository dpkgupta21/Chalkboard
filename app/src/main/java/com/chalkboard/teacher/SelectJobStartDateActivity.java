package com.chalkboard.teacher;

import java.util.Calendar;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;

import com.chalkboard.GlobalClaass;
import com.chalkboard.PreferenceConnector;
import com.chalkboard.R;

public class SelectJobStartDateActivity extends Activity{
	
	Activity context = null;

	DatePicker dpDate = null;
	
	int year;
	int month;
	int day;
	Typeface font,font2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = this;
		font=Typeface.createFromAsset(context.getAssets(), "mark.ttf");
		font2=Typeface.createFromAsset(getAssets(), "marlbold.ttf");
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_select_date);
		
		
		(findViewById(R.id.close_header)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.close_header_text)).setText("Select Date");
		
		try {
			((TextView)findViewById(R.id.close_header_text)).setTypeface(font2);
		} catch (Exception e) {

		}
		
		(findViewById(R.id.close_header)).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				
				GlobalClaass.savePrefrencesfor(context, PreferenceConnector.STARTDATE, "");
				
				finish();
			}
		});
		
		dpDate = (DatePicker) findViewById(R.id.datepicker);
		
		Calendar c = Calendar.getInstance();
		
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		dpDate.init(year, month, day, new OnDateChangedListener() {
			
			@Override
			public void onDateChanged(DatePicker arg0, int arg1, int arg2, int arg3) {
				
				year = arg1;
				month = arg2;
				day = arg3;
				
				GlobalClaass.savePrefrencesfor(context, PreferenceConnector.STARTDATE, year + "-" + (month+1) + "-" + day);
				
				
				
			}
		});
		
	}
	
	@Override
	public void onBackPressed() {
		GlobalClaass.savePrefrencesfor(context, PreferenceConnector.STARTDATE, year + "-" + (month+1) + "-" + day);
		finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		overridePendingTransition(R.anim.slide_up, 0);
	}
	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(0, R.anim.slide_down);
	}
	
}
package com.chalkboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class WriteReview extends Activity{

	Activity context;
	TextView txt_feedback,txt_support,top_header_count;
	Button btn_rateapp;
	ImageView btn_back;
	Typeface font;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.write_review);

		context = this;
		font=Typeface.createFromAsset(getAssets(), "mark.ttf");

		txt_feedback = (TextView) findViewById(R.id.txt_feedback);
		txt_support = (TextView) findViewById(R.id.txt_support);
		btn_rateapp = (Button) findViewById(R.id.btn_rate);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		top_header_count = (TextView)findViewById(R.id.top_header_count);
		
		String count= GlobalClaass.getHeader_Count(context);

		if(count != null){

			if(count.length() == 1){
				top_header_count.setText("  "+count);
			}if(count.length() == 2){
				top_header_count.setText(" "+count);
			}
			else {
				top_header_count.setText(count);
			}
		}else {
			top_header_count.setVisibility(View.GONE);
		}
		
		try {
			txt_feedback.setTypeface(font);
			txt_support.setTypeface(font);
			btn_rateapp.setTypeface(font);
			top_header_count.setTypeface(font);
			
			((TextView) findViewById(R.id.writeareview_txt)).setTypeface(font);
			((TextView) findViewById(R.id.bigtext)).setTypeface(font);
			((TextView) findViewById(R.id.or_txt)).setTypeface(font);
			
		} catch (Exception e) {

		}

		txt_feedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(context, Feedback_Activity.class)
				.putExtra("Activity", "feedback")
				.putExtra("ActivityName", "WriteReview"));
				//finish();
			}
		});

		txt_support.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(context, Feedback_Activity.class)
				.putExtra("Activity", "support")
				.putExtra("ActivityName", "WriteReview"));
				//finish();
			}
		});

		btn_rateapp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
				GlobalClaass.activitySlideBackAnimation(context);
				
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		
		finish();
		GlobalClaass.activitySlideBackAnimation(context);
		
	}
}

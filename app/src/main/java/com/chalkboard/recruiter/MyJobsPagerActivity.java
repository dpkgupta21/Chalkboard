package com.chalkboard.recruiter;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.chalkboard.R;
import com.chalkboard.teacher.JobObject;

public class MyJobsPagerActivity extends FragmentActivity {

	ViewPager jobPager = null;

	int position;
	ArrayList<JobObject> dataList = null;

	Typeface font,font2;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		position = getIntent().getIntExtra("position", 0);
		dataList = (ArrayList<JobObject>) getIntent().getSerializableExtra(
				"dataList");

		setContentView(R.layout.activity_pager);

		font = Typeface.createFromAsset(getAssets(), "fonts/mark.ttf");
		font2=Typeface.createFromAsset(getAssets(), "fonts/marlbold.ttf");
		
		((ImageView)(findViewById(R.id.header_left_menu))).setImageResource(R.drawable.remove_job);
		((ImageView)(findViewById(R.id.header_right_menu))).setImageResource(R.drawable.share_icon_box);
		
		
		
		((TextView) (findViewById(R.id.top_header_count)))
		.setVisibility(View.GONE);
		
		((ImageView) (findViewById(R.id.header_logo)))
		.setVisibility(View.GONE);

       ((TextView) (findViewById(R.id.header_text))).setText("Job Preview");
       
       try {
			((TextView) (findViewById(R.id.header_text))).setTypeface(font2);
			
		} catch (Exception e) {

		}
       
		
		/*(findViewById(R.id.header_left_menu)).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				
				
				
				
				String map = "http://maps.google.co.in/maps?q=" + dataList.get(jobPager.getCurrentItem()).getJobLocation();
				
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
				startActivity(intent);		
			}
		});*/
		
		(findViewById(R.id.header_left_menu)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();				
			}
		});


		(findViewById(R.id.header_right_menu)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String shareBody = dataList.get(jobPager.getCurrentItem()).getJobName()
						+ " @ " + dataList.get(jobPager.getCurrentItem()).getJobLocation();
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Chalkboard Android");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
				startActivity(Intent.createChooser(sharingIntent, "Share via..."));

			}
		});
		
		jobPager = (ViewPager) findViewById(R.id.pager);

		jobPager.setAdapter(new SlidePagerAdapter(getSupportFragmentManager()));
		
		jobPager.setCurrentItem(position);

	}

	private class SlidePagerAdapter extends FragmentStatePagerAdapter {
		public SlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {

			return JobPreviewPage.newInstance(dataList.get(pos));

		}

		@Override
		public int getCount() {
			 return dataList.size();
			
		}
	}

}
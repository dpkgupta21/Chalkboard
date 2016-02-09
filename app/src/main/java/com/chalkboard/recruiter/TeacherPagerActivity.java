package com.chalkboard.recruiter;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.chalkboard.R;
import com.chalkboard.teacher.JobPageFragment;

public class TeacherPagerActivity extends FragmentActivity {

	ViewPager teacherPager = null;

	int position;
	ArrayList<TeacherObject> dataList = null;
	Typeface font,font2;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		position = getIntent().getIntExtra("position", 0);
		dataList = (ArrayList<TeacherObject>) getIntent().getSerializableExtra(
				"dataList");
		font=Typeface.createFromAsset(getAssets(), "mark.ttf");
		font2=Typeface.createFromAsset(getAssets(), "marlbold.ttf");

		setContentView(R.layout.activity_pager);

		((ImageView)(findViewById(R.id.header_left_menu))).setImageResource(R.drawable.pin_icon);
		((ImageView)(findViewById(R.id.header_right_menu))).setImageResource(R.drawable.cross_icon);
		
		((ImageView) (findViewById(R.id.header_logo)))
		.setVisibility(View.GONE);
		
		try {
			
			((TextView) findViewById(R.id.header_text)).setTypeface(font2);
			
		} catch (Exception e) {

		}

       ((TextView) (findViewById(R.id.header_text))).setText("Browse Teachers");
       
       
       findViewById(R.id.top_header_count).setVisibility(View.GONE);
       
       
		(findViewById(R.id.header_left_menu)).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				
				
				
				
				String map = "http://maps.google.co.in/maps?q=" + dataList.get(teacherPager.getCurrentItem()).getTeacherLocation();
				
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
				startActivity(intent);		
			}
		});
		
		(findViewById(R.id.header_right_menu)).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				finish();				
			}
		});
		
		teacherPager = (ViewPager) findViewById(R.id.pager);

		teacherPager.setAdapter(new SlidePagerAdapter(getSupportFragmentManager()));

		teacherPager.setCurrentItem(position);
		
	}

	private class SlidePagerAdapter extends FragmentStatePagerAdapter {
		public SlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {

			return TeacherPageFragment
					.newInstance(dataList.get(pos));

		}

		@Override
		public int getCount() {
			// uncomment it when data come
			 return dataList.size();
			//return 10;
		}
	}

}
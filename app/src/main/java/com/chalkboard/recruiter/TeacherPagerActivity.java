package com.chalkboard.recruiter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

import com.chalkboard.PreferenceConnector;
import com.chalkboard.R;
import com.chalkboard.model.ReadMapIdDTO;

import java.util.ArrayList;
import java.util.Map;

public class TeacherPagerActivity extends FragmentActivity {

    ViewPager teacherPager = null;
    private Activity mActivity;

    int position;
    ArrayList<TeacherObject> dataList = null;

    //Typeface font,font2;
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mActivity = this;

        position = getIntent().getIntExtra("position", 0);
        dataList = (ArrayList<TeacherObject>) getIntent().getSerializableExtra(
                "dataList");

        ReadMapIdDTO readMapIdDTO = PreferenceConnector.getObjectFromPref(mActivity,
                PreferenceConnector.READ_MAP_ID);
        readMapIdDTO.getRecruiterMapId().put(dataList.get(position).getId(), false);
        PreferenceConnector.putObjectIntoPref(mActivity, readMapIdDTO,
                PreferenceConnector.READ_MAP_ID);

        //font2=Typeface.createFromAsset(getAssets(), "fonts/marlbold.ttf");

        setContentView(R.layout.activity_pager);

//		((ImageView)(findViewById(R.id.header_left_menu))).setImageResource(R.drawable.pin_icon);
//		((ImageView)(findViewById(R.id.header_right_menu))).setImageResource(R.drawable.cross_icon);


        ImageView header_left_menu = (ImageView) findViewById(R.id.header_left_menu);
        header_left_menu.setImageResource(R.drawable.remove_job);
        header_left_menu.setOnClickListener(removeJobClickListener);

        ImageView header_right_menu = (ImageView) findViewById(R.id.header_right_menu);
        header_right_menu.setImageResource(R.drawable.group_icon);
        header_right_menu.setOnClickListener(shareClickListener);

        ((ImageView) (findViewById(R.id.header_logo)))
                .setVisibility(View.GONE);


        ((TextView) (findViewById(R.id.header_text))).setText("Browse Teachers");


        // findViewById(R.id.top_header_count).setVisibility(View.GONE);


//		(findViewById(R.id.header_left_menu)).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//
//
//
//
//				String map = "http://maps.google.co.in/maps?q=" + dataList.get(teacherPager.getCurrentItem()).getTeacherLocation();
//
//				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
//				startActivity(intent);
//			}
//		});
//
//		(findViewById(R.id.header_right_menu)).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				finish();
//			}
//		});

        teacherPager = (ViewPager) findViewById(R.id.pager);

        teacherPager.setAdapter(new SlidePagerAdapter(getSupportFragmentManager()));

        teacherPager.setCurrentItem(position);

    }

    private OnClickListener shareClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String shareBody = dataList.get(teacherPager.getCurrentItem()).getTeacherName()
                    + " @ " + dataList.get(teacherPager.getCurrentItem()).getTeacherLocation();
            Intent sharingIntent = new Intent(
                    android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(
                    android.content.Intent.EXTRA_SUBJECT,
                    "Chalkboard Android");
            sharingIntent.putExtra(
                    android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent,
                    "Share via..."));

        }
    };


    private OnClickListener locationClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String map = "http://maps.google.co.in/maps?q=" +
                    dataList.get(teacherPager.getCurrentItem()).getTeacherLocation();

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
            startActivity(intent);
        }
    };

    private OnClickListener removeJobClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };


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
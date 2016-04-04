package com.chalkboard.teacher;

import static com.chalkboard.GlobalClaass.hideProgressBar;
import static com.chalkboard.GlobalClaass.showProgressBar;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.chalkboard.GlobalClaass;
import com.chalkboard.PreferenceConnector;
import com.chalkboard.R;
import com.chalkboard.model.ReadMapIdDTO;

public class JobPagerActivity extends FragmentActivity {

    private ViewPager jobPager = null;
    private int position;
    private ArrayList<JobObject> dataList = null;
    private SlidePagerAdapter pagerAdapter;
    private Activity context = null;


    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        position = getIntent().getIntExtra("position", 0);
        dataList = (ArrayList<JobObject>) getIntent().getSerializableExtra(
                "dataList");
        context = this;


        ReadMapIdDTO readMapIdDTO = PreferenceConnector.getObjectFromPref(context,
                PreferenceConnector.READ_MAP_ID);
        readMapIdDTO.getTeacherMapId().put(dataList.get(position).getId(), false);
        PreferenceConnector.putObjectIntoPref(context, readMapIdDTO,
                PreferenceConnector.READ_MAP_ID);



        setContentView(R.layout.activity_pager);

        //((ImageView)(findViewById(R.id.header_left_menu))).setImageResource(R.drawable.pin_icon);
        //((ImageView)(findViewById(R.id.header_left_menu))).setImageResource(R.drawable.remove_job);

        ImageView header_left_menu = (ImageView) findViewById(R.id.header_left_menu);
        header_left_menu.setImageResource(R.drawable.remove_job);
        header_left_menu.setOnClickListener(removeJobClickListener);

        ImageView header_right_menu = (ImageView) findViewById(R.id.header_right_menu);
        header_right_menu.setImageResource(R.drawable.share_icon_box);
        header_right_menu.setOnClickListener(shareClickListener);

        ((ImageView) (findViewById(R.id.header_logo))).setVisibility(View.GONE);

        ((TextView) (findViewById(R.id.header_text))).setText("Job Listing");


        ((ImageView) (findViewById(R.id.header_logo)))
                .setVisibility(View.GONE);
        //findViewById(R.id.top_header_count).setVisibility(View.GONE);

        ((TextView) (findViewById(R.id.header_text))).setText(getString(R.string.job_detail_page_title));

//		(findViewById(R.id.header_left_menu)).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//
//
//
//
//				String map = "http://maps.google.co.in/maps?q=" + dataList.get(jobPager.getCurrentItem()).getJobLocation();
//
//				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
//				startActivity(intent);
//			}
//		});

//		(findViewById(R.id.header_right_menu)).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//
//				/*Log.e("Deepak", "ID:" + dataList.get(jobPager.getCurrentItem()).getId());
//
//				removeJob = new RemoveJob(dataList.get(jobPager.getCurrentItem()).getId());
//				removeJob.execute();*/
//
//				finish();
//
//			}
//		});

        jobPager = (ViewPager) findViewById(R.id.pager);

        pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager());

        jobPager.setAdapter(pagerAdapter);

        jobPager.setClipToPadding(false);
        jobPager.setPadding(20, 0, 20, 0);
        jobPager.setPageMargin(20);

        jobPager.setCurrentItem(position);

        jobPager.setOffscreenPageLimit(20);

        jobPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                ReadMapIdDTO readMapIdDTO = PreferenceConnector.getObjectFromPref(context,
                        PreferenceConnector.READ_MAP_ID);
                readMapIdDTO.getTeacherMapId().put(dataList.get(position).getId(), false);
                PreferenceConnector.putObjectIntoPref(context, readMapIdDTO,
                        PreferenceConnector.READ_MAP_ID);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private OnClickListener shareClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            String shareBody = dataList.get(jobPager.getCurrentItem()).getJobName() +
                    " @ " + dataList.get(jobPager.getCurrentItem()).getJobLocation();
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Chalkboard Android");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via..."));
        }
    };

    private OnClickListener locationClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String map = "http://maps.google.co.in/maps?q=" +
                    dataList.get(jobPager.getCurrentItem()).getJobLocation();

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

            return JobPageFragment.newInstance(dataList.get(pos));

        }

        @Override
        public int getCount() {
            return dataList.size();

        }
    }


    RemoveJob removeJob;

    class RemoveJob extends AsyncTask<String, String, String> {

        String jobId;

        public RemoveJob(String id) {
            jobId = id;
        }

        @Override
        protected void onPreExecute() {
            showProgressBar(context);
        }

        @Override
        protected String doInBackground(String... params) {

            String resultStr = null;
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("action",
                        "remove_job"));

                nameValuePairs.add(new BasicNameValuePair("job_id", jobId));
                nameValuePairs.add(new BasicNameValuePair("user_id",
                        GlobalClaass.getUserId(context)));
                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(request);

                HttpEntity entity = response.getEntity();

                resultStr = EntityUtils.toString(entity);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultStr;

        }

        @Override
        protected void onPostExecute(String result) {

            hideProgressBar(context);

            Log.e("Deepak", "result:" + result);

            dataList.remove(jobPager.getCurrentItem());

            pagerAdapter.notifyDataSetChanged();


        }

    }


}
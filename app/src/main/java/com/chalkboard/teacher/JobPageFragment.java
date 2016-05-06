package com.chalkboard.teacher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.ImageLoader11;
import com.chalkboard.R;
import com.chalkboard.customviews.CustomAlert;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.chalkboard.GlobalClaass.hideProgressBar;
import static com.chalkboard.GlobalClaass.showProgressBar;

public class JobPageFragment extends Fragment {

    View rootView = null;

    Activity context = null;
    //Typeface font,font2;
    GetJobDetail getJobDetail = null;
    public ImageLoader imageloader = null;
    private DisplayImageOptions options;

    public JobPageFragment() {
    }

    static String JOB_OBJECT = "JOB_OBJECT";

    public static JobPageFragment newInstance(JobObject jobObject) {
        JobPageFragment jobPageFragment = new JobPageFragment();

        Bundle args = new Bundle();
        args.putSerializable(JOB_OBJECT, jobObject);
        jobPageFragment.setArguments(args);

        return jobPageFragment;
    }

    JobObject jobObject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setRetainInstance(true);
        context = getActivity();

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .showImageOnLoading(R.drawable.splash)
                .showImageOnFail(R.drawable.splash)
                .showImageForEmptyUri(R.drawable.splash)
                .build();

        jobObject = (JobObject) getArguments().getSerializable(JOB_OBJECT);

        imageloader = new ImageLoader(context);
//        font = Typeface.createFromAsset(context.getAssets(), "mark.ttf");
//        font2 = Typeface.createFromAsset(context.getAssets(), "marlbold.ttf");


        rootView = inflater.inflate(R.layout.page_job, container, false);


        GlobalClaass.clearAsyncTask(getJobDetail);

        if (GlobalClaass.isInternetPresent(context)) {

            getJobDetail = new GetJobDetail(jobObject.getId());
            getJobDetail.execute();
        } else {
            GlobalClaass.showToastMessage(context, "Please check internet connection");
        }


        return rootView;
    }

	/*@Override
    public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		
		if (menuVisible) {
			GlobalClaass.clearAsyncTask(getJobDetail);
			getJobDetail = new GetJobDetail(jobObject.getId());
			getJobDetail.execute();
		}
		
	}*/

	/*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		
		if (isVisibleToUser) {

			jobObject = (JobObject) getArguments().getSerializable(JOB_OBJECT);
			
			GlobalClaass.clearAsyncTask(getJobDetail);
			getJobDetail = new GetJobDetail(jobObject.getId());
			getJobDetail.execute();	
		}
		
	}*/


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GlobalClaass.clearAsyncTask(getJobDetail);
        GlobalClaass.clearAsyncTask(addJobFavorites);
        GlobalClaass.clearAsyncTask(addJobMatch);
        GlobalClaass.clearAsyncTask(removeJobFavorites);
        GlobalClaass.clearAsyncTask(removeJobMatch);
    }

    class GetJobDetail extends AsyncTask<String, String, String> {

        String jobId;

        public GetJobDetail(String id) {
            jobId = id;
        }

        @Override
        protected void onPreExecute() {
            showProgressBar(context, rootView);
        }

        @Override
        protected String doInBackground(String... params) {

            String resultStr = null;
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs
                        .add(new BasicNameValuePair("action", "jobDetail"));

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

            hideProgressBar(context, rootView);

            setUpUi(result);
        }

    }

    public void setUpUi(String result) {

        try {

            Log.e("Dotsquares", "job detail result:" + result);

            JSONObject jObject = new JSONObject(result);

            String get_message = jObject.getString("message").trim();
            String get_replycode = jObject.getString("status").trim();
            jobObject.setJobTitle(jObject.getString("title").trim());
            String salary = jObject.getString("salary").trim();


            jobObject.setJobSalary(salary);

            String description = jObject.getString("description").trim();

            jobObject.setJobDescription(description);

            boolean is_match = jObject.getBoolean("is_match");

            jobObject.setJobMatch(is_match);

            JSONObject jObj = jObject.getJSONObject("Recruiter");

            String name = jObj.getString("name").trim();

            jobObject.setJobRecruiterName(name);

            String school_photo = jObj.getString("school_photo").trim();

            String about = jObj.getString("about").trim();

            jobObject.setJobRecruiterAbout(about);

            jobObject.setJobPhoto(school_photo);

            refineUI();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showSendMatchRequestDialog() {
        String message = "This lets " + jobObject.getJobRecruiterName() +
                " know your interested in their job. They'll view your profile, and you'll" +
                " get a notification if it's a match!";
        new CustomAlert(getActivity(), JobPageFragment.this)
                .circleTransparentDialog(
                        message,
                        getString(R.string.cancel),
                        getString(R.string.send), jobObject.getJobImage(), "dblBtnCallbackResponse", 1000);
    }

    public void dblBtnCallbackResponse(Boolean flag, int code) {
        if (flag) {
            if (jobObject.isJobMatch()) {
                removeJobMatch = new RemoveJobMatch(jobObject
                        .getId());
                removeJobMatch.execute();
            } else {
                addJobMatch = new AddJobMatch(jobObject.getId());
                addJobMatch.execute();
            }
        }

    }

    private void refineUI() {
        ((TextView) rootView.findViewById(R.id.txt_recruiter_name)).setText(jobObject
                .getJobRecruiterName());
//        ((TextView) rootView.findViewById(R.id.job_offer_by)).setText("By "
//                + jobObject.getJobRecruiterName());
        ((TextView) rootView.findViewById(R.id.txt_job_location)).setText(
                jobObject.getJobLocation());

        final Button btn_send_match_request = (Button) rootView.findViewById(R.id.btn_send_match_request);
        final ImageView img_fav_icon = (ImageView) rootView.findViewById(R.id.img_fav_icon);
        if (jobObject.isJobFavorite()) {
            img_fav_icon.setImageResource(R.drawable.like_icon_active);

        } else {
            img_fav_icon.setImageResource(R.drawable.like_icon_grey);

        }

        if (jobObject.isJobMatch()) {

            btn_send_match_request
                    .setText(getString(R.string.remove_match_request));
        } else {
            btn_send_match_request.setText(getString(R.string.send_match_request));
        }


        btn_send_match_request.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_send_match_request.getText().toString().
                        equalsIgnoreCase(getString(R.string.send_match_request))) {
                    showSendMatchRequestDialog();
                } else {
                    dblBtnCallbackResponse(true, 1001);
                }

            }
        });

        ImageLoader imageloader = new ImageLoader(context);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().
                displayImage(jobObject.getJobImage(),
                        ((ImageView) rootView.findViewById(R.id.job_image)), options);

        //ImageLoader11 imageloader11 = new ImageLoader11(context);

        imageloader.DisplayImage(jobObject.getJobPhoto(),
                ((ImageView) rootView.findViewById(R.id.img_job_icon)));


//        imageloader11.DisplayImage(jobObject.getJobImage(),
//                ((ImageView) rootView.findViewById(R.id.job_image)));

//        if (jobObject.isJobFavorite()) {
//            ((TextView) rootView.findViewById(R.id.add_to_favorite))
//                    .setText("Remove from Favorites");
//            ((TextView) rootView.findViewById(R.id.add_to_favorite))
//                    .setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View arg0) {
//                            removeJobFavorites = new RemoveJobFavorites(
//                                    jobObject.getId());
//                            removeJobFavorites.execute();
//                        }
//                    });
//        } else {
//            ((TextView) rootView.findViewById(R.id.add_to_favorite))
//                    .setText("Add to Favorites");
//            ((TextView) rootView.findViewById(R.id.add_to_favorite))
//                    .setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View arg0) {
//                            addJobFavorites = new AddJobFavorites(jobObject
//                                    .getId());
//                            addJobFavorites.execute();
//                        }
//                    });
//        }

//        if (jobObject.isJobMatch()) {
//
//            ((TextView) rootView.findViewById(R.id.match_job))
//                    .setText("Unmatch Job");
//            ((TextView) rootView.findViewById(R.id.match_job))
//                    .setOnClickListener(new OnClickListener() {
//
//                        @Override
//                        public void onClick(View arg0) {
//                            removeJobMatch = new RemoveJobMatch(jobObject
//                                    .getId());
//                            removeJobMatch.execute();
//                        }
//                    });
//        } else {
//
//            ((TextView) rootView.findViewById(R.id.match_job))
//                    .setText("Match Job");
//            ((TextView) rootView.findViewById(R.id.match_job))
//                    .setOnClickListener(new OnClickListener() {
//
//                        @Override
//                        public void onClick(View arg0) {
//                            addJobMatch = new AddJobMatch(jobObject.getId());
//                            addJobMatch.execute();
//                        }
//                    });
//
//        }

        rootView.findViewById(R.id.img_location_icon)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        String map = "http://maps.google.co.in/maps?q=" +
                                jobObject.getJobLocation();

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                        startActivity(intent);

                    }
                });

        img_fav_icon
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        if (jobObject.isJobFavorite()) {
                            img_fav_icon.setImageResource(R.drawable.like_icon_grey);
                            removeJobFavorites = new RemoveJobFavorites(
                                    jobObject.getId());
                            removeJobFavorites.execute();
                        } else {
                            img_fav_icon.setImageResource(R.drawable.like_icon_active);

                            addJobFavorites = new AddJobFavorites(jobObject
                                    .getId());
                            addJobFavorites.execute();
                        }

                    }
                });

        ((TextView) rootView.findViewById(R.id.txt_job_title))
                .setText(jobObject.getJobTitle());
        ((TextView) rootView.findViewById(R.id.txt_job_description))
                .setText(jobObject.getJobDescription());
        ((TextView) rootView.findViewById(R.id.txt_salary_val)).setText(jobObject
                .getJobSalary());
        ((TextView) rootView.findViewById(R.id.txt_start_date_val)).setText(jobObject
                .getJobDate());
        ((TextView) rootView.findViewById(R.id.txt_about_company_val))
                .setText(jobObject.getJobRecruiterAbout());
    }


    RemoveJobMatch removeJobMatch;

    class RemoveJobMatch extends AsyncTask<String, String, String> {

        String jobId;

        public RemoveJobMatch(String id) {
            jobId = id;
        }

        @Override
        protected void onPreExecute() {
            showProgressBar(context, rootView);
        }

        @Override
        protected String doInBackground(String... params) {

            String resultStr = null;
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("action",
                        "removeToMatchList"));

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

            hideProgressBar(context, rootView);

            jobObject.setJobMatch(false);
            refineUI();
        }

    }

    AddJobMatch addJobMatch;

    class AddJobMatch extends AsyncTask<String, String, String> {

        String jobId;

        public AddJobMatch(String id) {
            jobId = id;
        }

        @Override
        protected void onPreExecute() {
            showProgressBar(context, rootView);
        }

        @Override
        protected String doInBackground(String... params) {

            String resultStr = null;
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("action",
                        "addToMatchList"));

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

            hideProgressBar(context, rootView);

            jobObject.setJobMatch(true);

            refineUI();

        }

    }

    RemoveJobFavorites removeJobFavorites;

    public class RemoveJobFavorites extends AsyncTask<String, String, String> {

        String jobId;

        public RemoveJobFavorites(String id) {
            jobId = id;
        }

        @Override
        protected void onPreExecute() {
            showProgressBar(context, rootView);
        }

        @Override
        protected String doInBackground(String... params) {

            String resultStr = null;
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("action",
                        "removeToFavorite"));

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

            hideProgressBar(context, rootView);

            jobObject.setJobFavorite(false);
            refineUI();
        }

    }

    AddJobFavorites addJobFavorites;

    public class AddJobFavorites extends AsyncTask<String, String, String> {

        String jobId;

        public AddJobFavorites(String id) {
            jobId = id;
        }

        @Override
        protected void onPreExecute() {
            showProgressBar(context, rootView);
        }

        @Override
        protected String doInBackground(String... params) {

            String resultStr = null;
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("action",
                        "addToFavorite"));

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

            hideProgressBar(context, rootView);

            jobObject.setJobFavorite(true);
            refineUI();

        }

    }

}
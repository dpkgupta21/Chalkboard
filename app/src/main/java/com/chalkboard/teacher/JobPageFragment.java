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
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.ImageLoader11;
import com.chalkboard.R;
import com.chalkboard.teacher.JobNotificationFragment.GetJobNotificationItem;

public class JobPageFragment extends Fragment {

	View rootView = null;

	Activity context = null;
	Typeface font,font2;
	GetJobDetail getJobDetail = null;
	public ImageLoader imageloader = null;
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
		
		jobObject = (JobObject) getArguments().getSerializable(JOB_OBJECT);

		context = getActivity();
		imageloader = new ImageLoader(context);
		font=Typeface.createFromAsset(context.getAssets(), "mark.ttf");
		font2=Typeface.createFromAsset(context.getAssets(), "marlbold.ttf");


		rootView = inflater.inflate(R.layout.page_job, container, false);


		GlobalClaass.clearAsyncTask(getJobDetail);
		
		if(GlobalClaass.isInternetPresent(context)){

			getJobDetail = new GetJobDetail(jobObject.getId());
			getJobDetail.execute();
		}
		else {
			GlobalClaass.showToastMessage(context,"Please check internet connection");
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

	private void refineUI() {
		((TextView) rootView.findViewById(R.id.job_name)).setText(jobObject
				.getJobName());
		((TextView) rootView.findViewById(R.id.job_offer_by)).setText("By "
				+ jobObject.getJobRecruiterName());
		((TextView) rootView.findViewById(R.id.job_location)).setText("@ "
				+ jobObject.getJobLocation());
		
       try {
			
			((TextView) rootView.findViewById(R.id.job_name)).setTypeface(font2);
			((TextView) rootView.findViewById(R.id.job_offer_by)).setTypeface(font);
			((TextView) rootView.findViewById(R.id.job_location)).setTypeface(font);
			((TextView) rootView.findViewById(R.id.add_to_favorite)).setTypeface(font2);
			((TextView) rootView.findViewById(R.id.match_job)).setTypeface(font2);		
			((TextView) rootView.findViewById(R.id.job_description)).setTypeface(font);
			((TextView) rootView.findViewById(R.id.salary)).setTypeface(font);
			((TextView) rootView.findViewById(R.id.start_date)).setTypeface(font);
			((TextView) rootView.findViewById(R.id.about_company)).setTypeface(font);
			((TextView) rootView.findViewById(R.id.about_heading)).setTypeface(font2);
			((TextView) rootView.findViewById(R.id.txt_salary)).setTypeface(font2);
			((TextView) rootView.findViewById(R.id.txt_stdate)).setTypeface(font2);
			
			
			
			
		} catch (Exception e) {

		}

		ImageLoader imageloader = new ImageLoader(context);
		ImageLoader11 imageloader11 = new ImageLoader11(context);

		imageloader.DisplayImage(jobObject.getJobImage(),
				((ImageView) rootView.findViewById(R.id.job_icon)));

		

		imageloader11.DisplayImage(jobObject.getJobPhoto(),
				((ImageView) rootView.findViewById(R.id.job_image)));

		if (jobObject.isJobFavorite()) {
			((TextView) rootView.findViewById(R.id.add_to_favorite))
					.setText("Remove from Favorites");
			((TextView) rootView.findViewById(R.id.add_to_favorite))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							removeJobFavorites = new RemoveJobFavorites(
									jobObject.getId());
							removeJobFavorites.execute();
						}
					});
		} else {
			((TextView) rootView.findViewById(R.id.add_to_favorite))
					.setText("Add to Favorites");
			((TextView) rootView.findViewById(R.id.add_to_favorite))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							addJobFavorites = new AddJobFavorites(jobObject
									.getId());
							addJobFavorites.execute();
						}
					});
		}

		if (jobObject.isJobMatch()) {

			((TextView) rootView.findViewById(R.id.match_job))
					.setText("Unmatch Job");
			((TextView) rootView.findViewById(R.id.match_job))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							removeJobMatch = new RemoveJobMatch(jobObject
									.getId());
							removeJobMatch.execute();
						}
					});
		} else {

			((TextView) rootView.findViewById(R.id.match_job))
					.setText("Match Job");
			((TextView) rootView.findViewById(R.id.match_job))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							addJobMatch = new AddJobMatch(jobObject.getId());
							addJobMatch.execute();
						}
					});

		}

		rootView.findViewById(R.id.share_job)
		.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				String shareBody = jobObject.getJobName() + " @ " + jobObject.getJobLocation();
			    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
			        sharingIntent.setType("text/plain");
			        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Chalkboard Android");
			        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			        startActivity(Intent.createChooser(sharingIntent, "Share via..."));
				
			}
		});
		
		((TextView) rootView.findViewById(R.id.job_description))
				.setText(jobObject.getJobDescription());
		((TextView) rootView.findViewById(R.id.salary)).setText(jobObject
				.getJobSalary());

		((TextView) rootView.findViewById(R.id.salary)).setText(jobObject
				.getJobSalary());

		((TextView) rootView.findViewById(R.id.start_date)).setText(jobObject
				.getJobDate());

		((TextView) rootView.findViewById(R.id.about_company))
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
package com.chalkboard.recruiter;

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
import android.net.Uri;
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
import com.chalkboard.recruiter.TeacherPageFragment.RemoveJobFavorites;
import com.chalkboard.teacher.JobObject;
import com.chalkboard.teacher.Certificate_Type.CertificateList;

public class JobPreviewPage extends Fragment {

	View rootView = null;

	Activity context = null;

	GetJobDetail getJobDetail = null;
	
	

	public JobPreviewPage() {
	}

	
	
	static String JOB_OBJECT = "JOB_OBJECT";

	public static JobPreviewPage newInstance(JobObject jobObject) {
		JobPreviewPage jobPageFragment = new JobPreviewPage();

		Bundle args = new Bundle();
		args.putSerializable(JOB_OBJECT, jobObject);
		jobPageFragment.setArguments(args);

		return jobPageFragment;
	}

	JobObject jobObject;

	Typeface font,font2;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		jobObject = (JobObject) getArguments().getSerializable(JOB_OBJECT);

		context = getActivity();

		rootView = inflater.inflate(R.layout.my_job_preview, container, false);
		
		font = Typeface.createFromAsset(context.getAssets(), "mark.ttf");
		font2=Typeface.createFromAsset(context.getAssets(), "marlbold.ttf");
		
		

		
		try {

			((TextView) rootView.findViewById(R.id.job_name)).setTypeface(font2);
			((TextView) rootView.findViewById(R.id.job_offer_by)).setTypeface(font);
			((TextView) rootView.findViewById(R.id.job_location)).setTypeface(font);
			


			((TextView) rootView.findViewById(R.id.job_description)).setTypeface(font);
			
			((TextView) rootView.findViewById(R.id.salary)).setTypeface(font);
			((TextView) rootView.findViewById(R.id.start_date)).setTypeface(font);
			((TextView) rootView.findViewById(R.id.about_company)).setTypeface(font);
			
			
			((TextView)rootView.findViewById(R.id.edit_job)).setTypeface(font2);
			
			((TextView)rootView.findViewById(R.id.remove_publish_job)).setTypeface(font2);
			
			((TextView)rootView.findViewById(R.id.about_heading)).setTypeface(font2);
			((TextView)rootView.findViewById(R.id.salary_text)).setTypeface(font2);
			((TextView)rootView.findViewById(R.id.date_text)).setTypeface(font2);
			
		} catch (Exception e) {

		}
		
          if(GlobalClaass.isInternetPresent(context)){
			
        	getJobDetail = new GetJobDetail(jobObject.getId());
      		getJobDetail.execute();
			
		}
		else {
			GlobalClaass.showToastMessage(context,"Please check internet connection");
		}

		

		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		GlobalClaass.clearAsyncTask(getJobDetail);
		
	}

	public class GetJobDetail extends AsyncTask<String, String, String> {

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

			Log.e("Dotsquares", "resultttttttttttttttttttttttt:" + result);

			JSONObject jObject = new JSONObject(result);

			String get_message = jObject.getString("message").trim();
			String get_replycode = jObject.getString("status").trim();

			String salary = jObject.getString("salary").trim();

			jobObject.setJobSalary(salary);

			String description = jObject.getString("description").trim();

			jobObject.setJobDescription(description);

			boolean is_draft = jObject.getBoolean("is_draft");

			jobObject.setIs_draft(is_draft);

			JSONObject jObj = jObject.getJSONObject("Recruiter");

			String name = jObj.getString("name").trim();

			jobObject.setJobRecruiterName(name);

			String school_photo = jObj.getString("school_photo").trim();
			
			String about = jObj.getString("about").trim();

			jobObject.setJobRecruiterAbout(about);
			
			jobObject.setJobPhoto(school_photo);
			
			String logo = jObj.getString("logo").trim();

			jobObject.setJobImage(logo);
			
			
			
			jobObject.setId(jObject.getString("id").trim());
			jobObject.setJobTitle(jObject.getString("title").trim());
			jobObject.setJobCity(jObject.getString("city").trim());
			jobObject.setJobCountry(jObject.getString("country").trim());
			jobObject.setJobStartdate(jObject.getString("start_date").trim());
			jobObject.setJobIsfavourate(jObject.getString("is_favorite").trim());
			jobObject.setJobIsmatch(jObject.getString("is_match").trim());
			
			
			

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

		ImageLoader imageloader = new ImageLoader(context);

		imageloader.DisplayImage(jobObject.getJobPhoto(),
				((ImageView) rootView.findViewById(R.id.job_icon)));

		ImageLoader11 imageloader1 = new ImageLoader11(context);

		imageloader1.DisplayImage(jobObject.getJobImage(),
				((ImageView) rootView.findViewById(R.id.job_image)));
		
		if (jobObject.isIs_draft()) {
			((TextView) rootView.findViewById(R.id.remove_publish_job))
					.setText("Publish Job");
			((TextView) rootView.findViewById(R.id.remove_publish_job))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							new PublishJob(jobObject.getId()).execute();
						}
					});
		} else {
			((TextView) rootView.findViewById(R.id.remove_publish_job))
					.setText("Remove Job");
			((TextView) rootView.findViewById(R.id.remove_publish_job))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							new RemoveJob(jobObject.getId()).execute();
						}
					});
		}

		((TextView) rootView.findViewById(R.id.edit_job))
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context,
						EditJobActivity.class).putExtra("Data", jobObject));
			}
		});
		
		/*if (jobObject.isJobFavorite()) {
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

		}*/

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

		((TextView) rootView.findViewById(R.id.start_date)).setText(jobObject
				.getJobDate());

		((TextView) rootView.findViewById(R.id.about_company))
				.setText(jobObject.getJobRecruiterAbout());
		
		
		
		
		
	}

	

	class RemoveJob extends AsyncTask<String, String, String> {

		String jobId;

		public RemoveJob(String id) {
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
						"remove_job"));

				nameValuePairs.add(new BasicNameValuePair("job_id",
						jobId));
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
			Log.e("Publish job","qqqqqqqqqqqqqqqqqqq     "+result);
			JSONObject jObject, jobj;
			String get_status = "", get_message = "";

			try {

				jObject = new JSONObject(result);

				get_message = jObject.getString("message").trim();
				get_status = jObject.getString("status").trim();

				if(get_status.equalsIgnoreCase("true")){

					context.finish();
					GlobalClaass.activitySlideForwardAnimation(context);
				}
				else {
					GlobalClaass.showToastMessage(context, get_message);
				}

			} catch (Exception e) {

			}
			
			hideProgressBar(context, rootView);

		}

	}
	

	class PublishJob extends AsyncTask<String, String, String> {

		String jobId;

		public PublishJob(String id) {
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
						"publish_job"));

				nameValuePairs.add(new BasicNameValuePair("job_id",
						jobId));
				
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

			Log.e("Remove job","qqqqqqqqqqqqqqqqqqq     "+result);
			
			JSONObject jObject, jobj;
			String get_status = "", get_message = "";

			try {

				jObject = new JSONObject(result);

				get_message = jObject.getString("message").trim();
				get_status = jObject.getString("status").trim();

				if(get_status.equalsIgnoreCase("true")){

					context.finish();
					GlobalClaass.activitySlideForwardAnimation(context);
				}
				else {
					GlobalClaass.showToastMessage(context, get_message);
				}

			} catch (Exception e) {

			}
			hideProgressBar(context, rootView);

		}

	}
	

}
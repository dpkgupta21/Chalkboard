package com.chalkboard.recruiter;

import static com.chalkboard.GlobalClaass.hideProgressBar;
import static com.chalkboard.GlobalClaass.showProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.json.JSONArray;
import org.json.JSONObject;

import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.R;
import com.chalkboard.WebView_Activity;
import com.chalkboard.teacher.JobObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TeacherCreditFragment extends Fragment {

	ListView lvTeacherList = null;

	View rootView = null;

	Activity context = null;

	ArrayList<JobObject> dataList = null;

	GetTeacherItem getTeacherItem = null;

	EditText edtSearch = null;

	LinearLayout bottomlayout;

	TextView txt_addmorecredit,txt_postnewjob,txt_count;
	String geturl_link = "";

	Typeface font,font2;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		context = getActivity();

		rootView = inflater.inflate(R.layout.fragment_list, container,
				false);

		font=Typeface.createFromAsset(context.getAssets(), "fonts/mark.ttf");
		font2=Typeface.createFromAsset(context.getAssets(), "fonts/marlbold.ttf");


		ImageView notifIcon = (ImageView) context.findViewById(R.id.header_right_menu);
		notifIcon.setVisibility(View.VISIBLE);
		notifIcon.setImageResource(R.drawable.menu_notification_white_icon);
		notifIcon.setOnClickListener(notificationClick);


		//edtSearch = (EditText) rootView.findViewById(R.id.search_list);
		//edtSearch.setVisibility(View.VISIBLE);



		lvTeacherList = (ListView) rootView.findViewById(R.id.list);
		bottomlayout = (LinearLayout) rootView.findViewById(R.id.bottomlayout);
		txt_addmorecredit = (TextView) rootView.findViewById(R.id.txt_addmorecredit);
		txt_postnewjob = (TextView) rootView.findViewById(R.id.txt_postnewjob);
		txt_count = (TextView)rootView.findViewById(R.id.txt_count);
		txt_count.setVisibility(View.VISIBLE);
		
		try {
			txt_addmorecredit.setTypeface(font);
			txt_postnewjob.setTypeface(font);
			txt_count.setTypeface(font);
			((TextView)rootView. findViewById(R.id.close_header_text)).setTypeface(font2);
		} catch (Exception e) {

		}
		
		txt_postnewjob.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(context,Teacher_Create_New_Job.class));
				GlobalClaass.activitySlideForwardAnimation(context);

			}
		});

		txt_addmorecredit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				startActivity(new Intent(context,WebView_Activity.class)
				.putExtra("URL", geturl_link));
				GlobalClaass.activitySlideForwardAnimation(context);
			}
		});

		bottomlayout.setVisibility(View.VISIBLE);

		if(GlobalClaass.isInternetPresent(context)){

			getTeacherItem = new GetTeacherItem();
			getTeacherItem.execute();

		}
		else {
			GlobalClaass.showToastMessage(context,"Please check internet connection");
		}



		return rootView;
	}


	View.OnClickListener notificationClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			((ImageView) (context.findViewById(R.id.header_logo)))
					.setVisibility(View.GONE);

			((TextView) (context.findViewById(R.id.header_text))).setText("My Notifications");

			TeacherNotificationFragment fragment = new TeacherNotificationFragment();
			FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.page_container, fragment)
					.commit();
		}
	};


	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();

		GlobalClaass.clearAsyncTask(getTeacherItem);

	}



	class GetTeacherItem extends AsyncTask<String, String, String> {

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

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs
				.add(new BasicNameValuePair("action", "myCredits"));

				nameValuePairs
				.add(new BasicNameValuePair("user_id", GlobalClaass.getUserId(context)));

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

			// Toast.makeText(context, result, Toast.LENGTH_LONG).show();

			hideProgressBar(context, rootView);

			setcredit(result);
		}

	}

	public void setcredit(String result) {
		String get_message = "",get_replycode = "",credit = "",url = "";
		try {

			Log.e("Deepak", "result: " + result);

			JSONObject jObject = new JSONObject(result);

			credit = jObject.getString("credits").trim();
			geturl_link = jObject.getString("url").trim();
			get_message = jObject.getString("message").trim();
			get_replycode = jObject.getString("status").trim();


			if(credit !=null){
				txt_count.setText("Available Job Credits: "+credit);
			}
			else {
				txt_count.setText("Available Job Credits: ");
			}
			






			if(GlobalClaass.isInternetPresent(context)){

				getPostedJobs = new GetPostedJobs();
				getPostedJobs.execute();

			}

		}catch(Exception e){

		}


	}


	GetPostedJobs getPostedJobs = null;
	class GetPostedJobs extends AsyncTask<String, String, String> {

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
				nameValuePairs.add(new BasicNameValuePair("action", "postedJobs"));
				nameValuePairs.add(new BasicNameValuePair("user_id",GlobalClaass.getUserId(context)));



				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();

				resultStr = EntityUtils.toString(entity);

				Log.e("Posted Job",resultStr);

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

		Log.e("Deepak", result);
		
		try {
			JSONObject jObject = new JSONObject(result);

			String get_message = jObject.getString("message").trim();
			String get_replycode = jObject.getString("status").trim();

			JSONArray jrr = jObject.getJSONArray("jobs");

			dataList = new ArrayList<JobObject>();

			for (int i = 0; i < jrr.length(); i++) {

				JSONObject jobj = jrr.getJSONObject(i);

				JobObject itmObj = new JobObject();

				itmObj.setId(jobj.getString("id"));
				itmObj.setJobName(jobj.getString("title"));
				itmObj.setJobLocation(jobj.getString("city") + ", "
						+ jobj.getString("country"));
				
				
				itmObj.setJobDate(changeformet(jobj.getString("start_date")));

				
				
				itmObj.setJobImage(jobj.getString("image"));
				//itmObj.setJobFavorite(jobj.getBoolean("status"));


				dataList.add(itmObj);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (dataList != null) {

			if (dataList.size() > 0) {

				final JobListAdapter itmAdap = new JobListAdapter(context, dataList);

				lvTeacherList.setAdapter(itmAdap);				

			}
		}

			
	}

	
	public static String changeformet(String date) {
		String s = "";

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		Date dt = null;
		try {
			dt = format.parse(date);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SimpleDateFormat your_format = new SimpleDateFormat("dd MMM yyyy");

		s = your_format.format(dt);

		return s;
	}


	class JobListAdapter extends BaseAdapter {

		Activity mContext;
		LayoutInflater inflater;
		private List<JobObject> mainDataList = null;
		private List<JobObject> arrList = null;
		ImageLoader imageloader = null;
		Typeface font;
		public JobListAdapter(Activity context, List<JobObject> mainDataList) {

			mContext = context;
			this.mainDataList = mainDataList;
			font = Typeface.createFromAsset(mContext.getAssets(), "fonts/mark.ttf");
			arrList = new ArrayList<JobObject>();

			arrList.addAll(this.mainDataList);

			inflater = LayoutInflater.from(mContext);

			imageloader = new ImageLoader(mContext);

		}

		class ViewHolder {
			protected TextView name;
			protected ImageView image;
			protected ImageView favourite;

			protected TextView date;
			protected TextView location;

		}

		@Override
		public int getCount() {
			return mainDataList.size();
		}

		@Override
		public JobObject getItem(int position) {
			return mainDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View view, ViewGroup parent) {
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.item_job_list, null);

				holder.name = (TextView) view.findViewById(R.id.job_name);
				holder.date = (TextView) view.findViewById(R.id.job_status);
				holder.location = (TextView) view.findViewById(R.id.job_location);

				holder.image = (ImageView) view.findViewById(R.id.job_image);

				holder.favourite = (ImageView) view
						.findViewById(R.id.job_favorite_image);

				try {

					holder.name.setTypeface(font);
					holder.date.setTypeface(font);
					holder.location.setTypeface(font);
				} catch (Exception e) {

				}
				
				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.name.setText(mainDataList.get(position).getJobName());

				holder.date.setText("Published on:"
					+ mainDataList.get(position).getJobDate());
			 
			holder.location.setText(mainDataList.get(position).getJobLocation());

			/*if (mainDataList.get(position).isJobFavorite()) {
				holder.favourite.setImageResource(R.drawable.like_icon);
			} else {
				holder.favourite.setImageResource(R.drawable.unlike_icon);
			}*/

			imageloader.DisplayImage(mainDataList.get(position).getJobImage(),
					holder.image);


			return view;
		}



	}
}
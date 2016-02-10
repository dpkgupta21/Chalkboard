package com.chalkboard.teacher.matchrequest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.R;
import com.chalkboard.teacher.JobObject;
import com.chalkboard.teacher.TeacherChatBoardActivity;

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

import java.util.ArrayList;
import java.util.List;

import static com.chalkboard.GlobalClaass.hideProgressBar;
import static com.chalkboard.GlobalClaass.showProgressBar;

public class MatchRequestFragment extends Fragment {

	SwipeMenuListView lvJobList = null;

	View rootView = null;

	Activity context = null;

	ArrayList<JobObject> dataList = null;

	GetJobMatchesItem getJobItem = null;

	JobMatchListAdapter itmAdap = null;
	Typeface font2;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		context = getActivity();
		font2=Typeface.createFromAsset(context.getAssets(), "marlbold.ttf");

		rootView = inflater.inflate(R.layout.swipe_list, container, false);

		lvJobList = (SwipeMenuListView) rootView.findViewById(R.id.list);
		
		try {
			
			((TextView)rootView.findViewById(R.id.error_message)).setTypeface(font2);
			
		} catch (Exception e) {

		}

		
		dataList = new ArrayList<JobObject>();
		itmAdap = new JobMatchListAdapter(context, dataList);
		lvJobList.setAdapter(itmAdap);
		
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
			       
			    	 // create "delete" item
			        SwipeMenuItem chatItem = new SwipeMenuItem(
			                context);
			        // set item background
			        chatItem.setBackground(R.drawable.chat_back);
			        // set item width
			        chatItem.setWidth(150);
			        // set a icon
			        chatItem.setIcon(R.drawable.chat_slide);
			        // add to menu
			        menu.addMenuItem(chatItem);
			    	

			        // create "delete" item
			        SwipeMenuItem deleteItem = new SwipeMenuItem(
			                context);
			        // set item background
			        deleteItem.setBackground(R.drawable.delete_back);
			        // set item width
			        deleteItem.setWidth(150);
			        // set a icon
			        deleteItem.setIcon(R.drawable.delete_slide);
			        // add to menu
			        menu.addMenuItem(deleteItem);
			    }
		};
		
		// set creator
		lvJobList.setMenuCreator(creator);

		//lvJobList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

		lvJobList
				.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(int position,
							SwipeMenu menu, int index) {
						switch (index) {
						case 0:
							// chat
							startActivity(new Intent(context,
									TeacherChatBoardActivity.class)
									.putExtra(
											"id",
											dataList.get(position)
													.getJobRecruiterId()).putExtra("name",
															dataList.get(position).getJobRecruiterName()));
							break;
						case 1:
							// delete
							
							if(GlobalClaass.isInternetPresent(context)){

								removeJobMatch = new RemoveJobMatch(
										dataList.get(position).getId());
								removeJobMatch.execute();
							}
							else {
								GlobalClaass.showToastMessage(context,"Please check internet connection");
							}
							
							break;
						}
						// false : close the menu; true : not close the
						// menu
						return false;
					}
				});
		
		executeTask();
		
		return rootView;
	}

	

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		GlobalClaass.clearAsyncTask(getJobItem);
		GlobalClaass.clearAsyncTask(removeJobMatch);

	}

	@Override
	public void onResume() {
		super.onResume();

		executeTask();

	}

	public void executeTask() {
		GlobalClaass.clearAsyncTask(getJobItem);
		if(GlobalClaass.isInternetPresent(context)){

			getJobItem = new GetJobMatchesItem();
			getJobItem.execute();
		}
		else {
			GlobalClaass.showToastMessage(context,"Please check internet connection");
		}
		
	}

	class GetJobMatchesItem extends AsyncTask<String, String, String> {

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
				nameValuePairs.add(new BasicNameValuePair("action",
						"matchesJobs"));
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
		String get_message = "";
		dataList = new ArrayList<JobObject>();

		try {
			
			Log.e("Deepak", " job match result: " + result);
			
			if (itmAdap != null) {
				itmAdap.notifyDataSetChanged();
			}

			itmAdap = new JobMatchListAdapter(context, dataList);

			lvJobList.setAdapter(itmAdap);

			JSONObject jObject = new JSONObject(result);

			 get_message = jObject.getString("message").trim();
			String get_replycode = jObject.getString("status").trim();

			JSONArray jrr = jObject.getJSONArray("jobs");

			for (int i = 0; i < jrr.length(); i++) {

				JSONObject jobj = jrr.getJSONObject(i);

				JobObject itmObj = new JobObject();

				itmObj.setId(jobj.getString("id"));
				itmObj.setJobMatchDate(jobj.getString("match_date"));
				itmObj.setJobLocation(jobj.getString("city") + ", "
						+ jobj.getString("country"));
				itmObj.setJobImage(jobj.getString("image"));
				itmObj.setJobName(jobj.getString("title"));

				itmObj.setJobRecruiterId(jobj.getString("recruiter_id"));

				itmObj.setJobRecruiterName(jobj.getString("recruiter"));

				dataList.add(itmObj);

				/**
				 * {"id":"674","title":"Test Job for credit check",
"city":"Jaipur",
"country":"Ashmore and Cartier Islands",
"image":"","match_date":"07:35 PM",
"recruiter":"Regina Millerj","recruiter_id":"671"},
				 */
				 
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		

			if (dataList.size() > 0) {

				itmAdap = new JobMatchListAdapter(context, dataList);

				lvJobList.setAdapter(itmAdap);

				lvJobList.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {

						startActivity(new Intent(context,
								TeacherChatBoardActivity.class).putExtra("id",
								dataList.get(position).getJobRecruiterId()).putExtra("name",
										dataList.get(position).getJobRecruiterName()));

					}
				});

				

				/*
				 * lvJobList.setOnItemLongClickListener(new
				 * OnItemLongClickListener() {
				 * 
				 * @Override public boolean onItemLongClick(AdapterView<?>
				 * parent, View view, int pos, long id) {
				 * 
				 * 
				 * if (view.findViewById(R.id.overlay_layout).getVisibility() ==
				 * View.VISIBLE) {
				 * view.findViewById(R.id.overlay_layout).setVisibility
				 * (View.GONE); }else{
				 * view.findViewById(R.id.overlay_layout).setVisibility
				 * (View.VISIBLE); }
				 * 
				 * return true; }
				 * 
				 * });
				 */

			
		}
		else {
			((TextView)rootView.findViewById(R.id.error_message)).setText(get_message);
		}
		
	}

	class JobMatchListAdapter extends BaseAdapter {

		Activity mContext;
		LayoutInflater inflater;
		private List<JobObject> mainDataList = null;
		Typeface font;

		ImageLoader imageloader = null;

		public JobMatchListAdapter(Activity context,
				List<JobObject> mainDataList) {

			mContext = context;
			this.mainDataList = mainDataList;
			inflater = LayoutInflater.from(mContext);

			imageloader = new ImageLoader(mContext);
			font=Typeface.createFromAsset(mContext.getAssets(), "mark.ttf");

		}

		class ViewHolder {
			protected TextView name;
			protected ImageView image;

			protected ImageView chat;
			protected ImageView remove;

			protected TextView offer_by;

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
				view = inflater.inflate(R.layout.item_job_match_list, null);

				holder.name = (TextView) view.findViewById(R.id.job_name);
				holder.date = (TextView) view.findViewById(R.id.job_date);
				holder.location = (TextView) view
						.findViewById(R.id.job_location);

				holder.image = (ImageView) view.findViewById(R.id.job_image);

				

				holder.offer_by = (TextView) view
						.findViewById(R.id.job_offer_by);
				
				try {
					
					holder.name.setTypeface(font2);
					holder.date.setTypeface(font);
					holder.location.setTypeface(font);
					holder.offer_by.setTypeface(font);
					
				} catch (Exception e) {
					
				}

				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}
			

			holder.name.setText(mainDataList.get(position).getJobName());

			holder.date.setText(mainDataList.get(position).getJobMatchDate());

			holder.offer_by.setText(mainDataList.get(position)
					.getJobRecruiterName());

			holder.location
					.setText(mainDataList.get(position).getJobLocation());
			

			imageloader.DisplayImage(mainDataList.get(position).getJobImage(),
					holder.image);

			return view;
		}

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

			executeTask();
		}

	}

}
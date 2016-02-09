package com.chalkboard.recruiter;

import static com.chalkboard.GlobalClaass.hideProgressBar;
import static com.chalkboard.GlobalClaass.showProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.R;
import com.chalkboard.recruiter.TeacherCreditFragment.GetTeacherItem;
import com.chalkboard.recruiter.TeacherPageFragment.AddJobFavorites;
import com.chalkboard.recruiter.TeacherPageFragment.RemoveJobFavorites;
import com.chalkboard.teacher.JobObject;

public class TeacherFavoriteFragment extends Fragment {

	ListView lvTeacherList = null;

	View rootView = null;

	Activity context = null;

	ArrayList<TeacherObject> dataList = null;

	GetTeacherItem getTeacherItem = null;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		context = getActivity();

		rootView = inflater.inflate(R.layout.fragment_list, container,
				false);

		
		lvTeacherList = (ListView) rootView.findViewById(R.id.list);

		
		

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		 if(GlobalClaass.isInternetPresent(context)){
				
			 getTeacherItem = new GetTeacherItem();
				getTeacherItem.execute();
				
			}
			else {
				GlobalClaass.showToastMessage(context,"Please check internet connection");
			}
	}
	
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
						.add(new BasicNameValuePair("action", "myFavoriteTeachers"));
				
				nameValuePairs
				.add(new BasicNameValuePair("user_id", GlobalClaass.getUserId(context)));
				
				//nameValuePairs
				//.add(new BasicNameValuePair("user_id", "659"));
				
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

			setUpUi(result);
		}

	}

	public void setUpUi(String result) {
		String get_message = "";
		try {
			
			Log.e("Deepak", "result: " + result);
			
			dataList = new ArrayList<TeacherObject>();
			
			JSONObject jObject = new JSONObject(result);

			 get_message = jObject.getString("message").trim();
			String get_replycode = jObject.getString("status").trim();

			JSONArray jrr = jObject.getJSONArray("teachers");

			

			for (int i = 0; i < jrr.length(); i++) {

				JSONObject jobj = jrr.getJSONObject(i);

				TeacherObject itmObj = new TeacherObject();

				itmObj.setId(jobj.getString("id"));
				itmObj.setTeacherAbout(jobj.getString("about"));
				
				itmObj.setTeacherAge(jobj.getString("age"));
				if(jobj.has("TeacherEducation")){
					itmObj.setTeacherEducation(jobj.get("TeacherEducation").toString());
				}else{
					itmObj.setTeacherEducation("");
				}
				itmObj.setTeacherEmail(jobj.getString("email"));
				
			
				if (	jobj.has("TeacherExperience")) {
					itmObj.setTeacherExperience(jobj.get("TeacherExperience").toString());
				}else{
					itmObj.setTeacherExperience("");
				}
				
				itmObj.setTeacherGender(jobj.getString("gender"));
				itmObj.setTeacherImage(jobj.getString("image"));
				
				String city = "null";
				if(!jobj.getString("city").toString().trim().equalsIgnoreCase("null")){
					city = jobj.getString("city");
					
				}
				
				
				if(!jobj.getString("country").toString().trim().equalsIgnoreCase("null")){
					if(city.equalsIgnoreCase("null")){
						city = jobj.getString("country");
					}else {
						city = city+","+jobj.getString("country");
					}
					
				}
				
				itmObj.setTeacherLocation(city);
				
				itmObj.setTeacherName(jobj.getString("name"));
				

				itmObj.setTeacherMatch(jobj.getBoolean("is_match"));
				itmObj.setTeacherFavorite(jobj.getBoolean("is_favorite"));
				
				dataList.add(itmObj);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (dataList.size() > 0) {
			
			final TeacherListAdapter itmAdap = new TeacherListAdapter(context, dataList);

			lvTeacherList.setAdapter(itmAdap);

			lvTeacherList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {

					startActivity(new Intent(context, TeacherPagerActivity.class)
							.putExtra("dataList", dataList).putExtra("position",
									position));

				}
			});
			
			
			
		}		
		else {
			((TextView)rootView.findViewById(R.id.error_message)).setText(get_message);
		}
		

	}

	class TeacherListAdapter extends BaseAdapter {

		Activity context;
		LayoutInflater inflater;
		Typeface font,font2;
		private List<TeacherObject> mainDataList = null;

		private List<TeacherObject> arrList = null;

		ImageLoader imageloader = null;
		
		

		public TeacherListAdapter(Activity context, List<TeacherObject> mainDataList) {

		
			
			this.context = context;
			this.mainDataList = mainDataList;
			inflater = LayoutInflater.from(this.context);
			font = Typeface.createFromAsset(this.context.getAssets(), "mark.ttf");
			font2=Typeface.createFromAsset(this.context.getAssets(), "marlbold.ttf");
			arrList = new ArrayList<TeacherObject>();

			arrList.addAll(this.mainDataList);

			imageloader = new ImageLoader(this.context);

		}

		class ViewHolder {
			protected TextView name;
			protected ImageView image;
			protected ImageView favourite;

			protected TextView location;

		}

		@Override
		public int getCount() {
			return mainDataList.size();
		}

		@Override
		public TeacherObject getItem(int position) {
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
				view = inflater.inflate(R.layout.item_teacher_list, null);

				holder.name = (TextView) view.findViewById(R.id.teacher_name);
				holder.location = (TextView) view
						.findViewById(R.id.teacher_location);

				holder.image = (ImageView) view.findViewById(R.id.teacher_image);
//				holder.image.setAlpha(128);
				holder.favourite = (ImageView) view
						.findViewById(R.id.teacher_favorite_image);

				try {

					holder.name.setTypeface(font2);
					holder.location.setTypeface(font);
				} catch (Exception e) {

				}

				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}

			String name = "", location = "";

			Log.e("PrintData", mainDataList.get(position).getTeacherName());

			if (!(mainDataList.get(position).getTeacherName().toString().trim()
					.equalsIgnoreCase("null"))) {

				name = (mainDataList.get(position).getTeacherName());
			}

			if (!(mainDataList.get(position).getTeacherAge().toString().trim()
					.equalsIgnoreCase("null"))) {

				if (name.equalsIgnoreCase("null")) {
					name = mainDataList.get(position).getTeacherAge();
				} else {
					name = name + " | "
							+ mainDataList.get(position).getTeacherAge();
				}
				// age = (mainDataList.get(position).getTeacherAge());
			}
			if (!(mainDataList.get(position).getTeacherGender().toString().trim()
					.equalsIgnoreCase("null"))) {

				if (name.equalsIgnoreCase("null")) {
					name = mainDataList.get(position).getTeacherGender();
					if (name.equalsIgnoreCase("Male")) {
						name = "M";
					} else {
						name = "F";
					}

				} else {
					if (mainDataList.get(position).getTeacherGender().toString()
							.trim().equalsIgnoreCase("Male")) {
						name = name + " " + "M";
					} else {
						name = name + " " + "F";
					}

				}

			}

			if (!(mainDataList.get(position).getTeacherLocation().toString().trim()
					.equalsIgnoreCase("null"))) {
				location = (mainDataList.get(position).getTeacherLocation());
			} else {
				location = "";
			}

			holder.name.setText(name);

			holder.location.setText(location);

			if (mainDataList.get(position).isTeacherFavorite()) {
				holder.favourite.setImageResource(R.drawable.icon_like);
			} else {
				holder.favourite.setImageResource(R.drawable.unlike_icon);
			}

			holder.favourite.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View arg0) {
					
					if (mainDataList.get(position).isTeacherFavorite()) {
						//holder.favourite.setImageResource(R.drawable.icon_like);
						new RemoveJobFavorites(mainDataList.get(position).getId()).execute();
						mainDataList.get(position).setTeacherFavorite(false);
						holder.favourite.setImageResource(R.drawable.unlike_icon);
					} else {
						//holder.favourite.setImageResource(R.drawable.unlike_icon);
						new AddJobFavorites(mainDataList.get(position).getId()).execute();
						mainDataList.get(position).setTeacherFavorite(true);
						holder.favourite.setImageResource(R.drawable.icon_like);
					}
					
				}
			});
			
			imageloader.DisplayImage(mainDataList.get(position).getTeacherImage(),
					holder.image);

			return view;
		}

		public void filter(String charText) {
			charText = charText.toLowerCase(Locale.getDefault());
			mainDataList.clear();
			if (charText.length() == 0) {
				mainDataList.addAll(arrList);
			} else {
				for (TeacherObject wp : arrList) {
					if (wp.getTeacherLocation().toLowerCase(Locale.getDefault())
							.contains(charText)) {
						mainDataList.add(wp);
					}
				}
			}
			notifyDataSetChanged();
		}

		
		
	}
	
	RemoveJobFavorites removeJobFavorites;

	class RemoveJobFavorites extends AsyncTask<String, String, String> {

		String teacherId;

		public RemoveJobFavorites(String id) {
			teacherId = id;
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
						"removeFromFavoriteProfile"));

				nameValuePairs.add(new BasicNameValuePair("teacher_id",
						teacherId));
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

			 getTeacherItem = new GetTeacherItem();
				getTeacherItem.execute();

		}

	}

	AddJobFavorites addJobFavorites;

	class AddJobFavorites extends AsyncTask<String, String, String> {

		String teacherId;

		public AddJobFavorites(String id) {
			teacherId = id;
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
						"addToFavoriteProfile"));

				nameValuePairs.add(new BasicNameValuePair("teacher_id",
						teacherId));
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

			 getTeacherItem = new GetTeacherItem();
				getTeacherItem.execute();

		}

	}
	
	
}
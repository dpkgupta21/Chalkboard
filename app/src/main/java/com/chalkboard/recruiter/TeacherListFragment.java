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
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chalkboard.GlobalClaass;
import com.chalkboard.R;

public class TeacherListFragment extends Fragment {

	ListView lvTeacherList = null;

	View rootView = null;

	Activity context = null;

	ArrayList<TeacherObject> dataList = null;

	GetTeacherItem getTeacherItem = null;

	EditText edtSearch = null;
 TeacherListAdapter itmAdap;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		context = getActivity();

		rootView = inflater.inflate(R.layout.fragment_list, container,
				false);

		((ImageView) context.findViewById(R.id.header_right_menu)).setImageResource(R.drawable.filter_icon);

		edtSearch = (EditText) rootView.findViewById(R.id.search_list);
		edtSearch.setVisibility(View.VISIBLE);

		
		edtSearch.setHint("Search by location or country preference");


		lvTeacherList = (ListView) rootView.findViewById(R.id.list);

		
edtSearch.addTextChangedListener(new TextWatcher() {
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
		
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		if(GlobalClaass.isInternetPresent(context)){
		GlobalClaass.clearAsyncTask(getTeacherItem);
		
		getTeacherItem = new GetTeacherItem(s.toString());
		getTeacherItem.execute();
		}
		
		
	}
});

		return rootView;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		
		if(GlobalClaass.isInternetPresent(context)){
			edtSearch.setText("");
			getTeacherItem = new GetTeacherItem("");
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

	private String str;

	class GetTeacherItem extends AsyncTask<String, String, String> {

		String search;
		
		public GetTeacherItem(String search) {
			this.search = search;
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

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);

				nameValuePairs
				.add(new BasicNameValuePair("action", "searchTeachers"));


				if (!search.equalsIgnoreCase("")) {
					nameValuePairs
					.add(new BasicNameValuePair("keyword", search));
				}
				
				
				nameValuePairs.add(new BasicNameValuePair("user_id", GlobalClaass.getUserId(context)));

				if (!GlobalClaass.getMinValue(context).equalsIgnoreCase("")) {
					nameValuePairs
					.add(new BasicNameValuePair("salary_min", GlobalClaass.getMinValue(context)));
				}

				if (!GlobalClaass.getMaxValue(context).equalsIgnoreCase("")) {
					nameValuePairs
					.add(new BasicNameValuePair("salary_max", GlobalClaass.getMaxValue(context)));
				}

				if (!GlobalClaass.getCountriesArray(context).equalsIgnoreCase("")) {
					nameValuePairs
					.add(new BasicNameValuePair("countries", GlobalClaass.getCountriesArray(context)));
				}

				if (!GlobalClaass.getTypeArray(context).equalsIgnoreCase("")) {
					nameValuePairs.add(new BasicNameValuePair("job_id", TeachersListActivity.sb.toString()));
				}

				//nameValuePairs
				//.add(new BasicNameValuePair("user_id", "659"));
				
				 str = "keyword = "+search+","+"user_id="+GlobalClaass.getUserId(context)+","+
				"salary_min ="+GlobalClaass.getMinValue(context)+",salary_max = "+GlobalClaass.getMaxValue(context)+","+
						"location="+GlobalClaass.getCountriesArray(context)+",type="+TeachersListActivity.sb.toString();
				
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
		
//		AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		builder.setTitle("");
//		builder.setMessage(""+str + result);
		// builder.setCancelable(true);
//		builder.show();
		try {

			((TextView)rootView.findViewById(R.id.error_message)).setText("");
			dataList = new ArrayList<TeacherObject>();
			if (itmAdap != null) {
				itmAdap.notifyDataSetChanged();
			}

			itmAdap = new TeacherListAdapter(context, dataList, rootView);

			lvTeacherList.setAdapter(itmAdap);
			lvTeacherList.setAdapter(null);

			lvTeacherList.setOnItemClickListener(null);
			JSONObject jObject = new JSONObject(result);

			get_message = jObject.getString("message").trim();
			String get_replycode = jObject.getString("status").trim();

//			if(jObject.has("teacher")){
			JSONArray jrr = jObject.getJSONArray("teachers");

			for (int i = 0; i < jrr.length(); i++) {

				JSONObject jobj = jrr.getJSONObject(i);

				TeacherObject itmObj = new TeacherObject();

				itmObj.setId(jobj.getString("id"));
				itmObj.setTeacherAbout(jobj.getString("about"));

				itmObj.setTeacherAge(jobj.getString("age"));
				
				if (jobj.has("TeacherEducation")) {
					if (!jobj.get("TeacherEducation").toString().equalsIgnoreCase("")) {
				itmObj.setTeacherEducation(jobj.getString("TeacherEducation"));
					}else{
						itmObj.setTeacherEducation("");
					}
				}else{
					itmObj.setTeacherEducation("");
				}
				
				
				itmObj.setTeacherEmail(jobj.getString("email"));

				if (jobj.has("TeacherExperience")) {
					if (!jobj.get("TeacherExperience").toString().equalsIgnoreCase("")) {
				itmObj.setTeacherExperience(jobj.getString("TeacherExperience"));
					}else{
						itmObj.setTeacherExperience("");
					}
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

			itmAdap = new TeacherListAdapter(context, dataList, rootView);

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

			
		}		else {
			((TextView)rootView.findViewById(R.id.error_message)).setText(get_message);
		}


	}

}
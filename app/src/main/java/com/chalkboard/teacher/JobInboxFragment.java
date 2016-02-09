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
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.chalkboard.GlobalClaass;
import com.chalkboard.InboxListAdapter;
import com.chalkboard.InboxObject;
import com.chalkboard.R;
import com.chalkboard.teacher.JobFavoriteFragment.GetFavoriteJobItem;

public class JobInboxFragment extends Fragment {

	ListView lvJobList = null;

	View rootView = null;

	Activity context = null;

	ArrayList<InboxObject> dataList = null;

	GetInboxJobItem getJobItem = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		context = getActivity();

		rootView = inflater.inflate(R.layout.fragment_list, container, false);

		lvJobList = (ListView) rootView.findViewById(R.id.list);

		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		GlobalClaass.clearAsyncTask(getJobItem);
	}

	@Override
	public void onResume() {
		super.onResume();
		if(GlobalClaass.isInternetPresent(context)){

			getJobItem = new GetInboxJobItem();
			getJobItem.execute();
		}
		else {
			GlobalClaass.showToastMessage(context,"Please check internet connection");
		}
		
	}

	class GetInboxJobItem extends AsyncTask<String, String, String> {

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
				nameValuePairs.add(new BasicNameValuePair("action", "inbox"));

				nameValuePairs.add(new BasicNameValuePair("user_id",
						GlobalClaass.getUserId(context)));

				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();

				resultStr = EntityUtils.toString(entity);

			} catch (final Exception e) {
				
				context.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(context, "11exception in dib"+e.getMessage(), 3000).show();	
					}
				});
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
		try {

			dataList = new ArrayList<InboxObject>();

			JSONObject jObject = new JSONObject(result);

			 get_message = jObject.getString("message").trim();
			String get_replycode = jObject.getString("status").trim();

			JSONArray jrr = jObject.getJSONArray("conversions");

			for (int i = 0; i < jrr.length(); i++) {

				JSONObject jobj = jrr.getJSONObject(i);

				InboxObject itmObj = new InboxObject();

				itmObj.setMessage(jobj.getString("message"));
				itmObj.setImage(jobj.getString("image"));
				itmObj.setUnread(jobj.getString("unread"));
				itmObj.setUser(jobj.getString("user"));
				itmObj.setUserId(jobj.getString("user_id"));
				itmObj.setTimestamp(jobj.getString("timestamp"));

				dataList.add(itmObj);

			}
		} catch (Exception e) {
			
			
					// TODO Auto-generated method stub
					Toast.makeText(context, "11exception in setup"+e.getMessage(), 3000).show();	
			
			e.printStackTrace();
		}

		if (dataList.size() > 0) {
			InboxListAdapter itmAdap = new InboxListAdapter(context, dataList);

			lvJobList.setAdapter(itmAdap);

			lvJobList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					
					startActivity(new Intent(context, TeacherChatBoardActivity.class).putExtra("id", dataList.get(position).getUserId()).putExtra("name",
							dataList.get(position).getUser()));
					
				}
			});
			
		}
		else {
			((TextView)rootView.findViewById(R.id.error_message)).setText(get_message);
		}
		
	}

}
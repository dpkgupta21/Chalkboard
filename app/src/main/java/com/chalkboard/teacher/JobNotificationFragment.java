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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.chalkboard.GlobalClaass;
import com.chalkboard.NotificationListAdapter;
import com.chalkboard.NotificationObject;
import com.chalkboard.R;

public class JobNotificationFragment extends Fragment {

	ListView lvJobList = null;

	View rootView = null;

	Activity context = null;

	ArrayList<NotificationObject> dataList = null;

	GetJobNotificationItem getJobItem = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		context = getActivity();

		rootView = inflater.inflate(R.layout.fragment_list, container, false);

		lvJobList = (ListView) rootView.findViewById(R.id.list);
		
		if(GlobalClaass.isInternetPresent(context)){

			getJobItem = new GetJobNotificationItem();
			getJobItem.execute();
		}
		else {
			GlobalClaass.showToastMessage(context,"Please check internet connection");
		}
		

		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		GlobalClaass.clearAsyncTask(getJobItem);
		
	}
	
	class GetJobNotificationItem extends AsyncTask<String, String, String> {

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
						"notifications"));
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

			// Toast.makeText(context, result, Toast.LENGTH_LONG).show();

			hideProgressBar(context, rootView);

			setUpUi(result);
		}

	}

	public void setUpUi(String result) {
		String get_message = "";
		try {

			Log.e("Deepak", "notification result "+result);
			
			dataList = new ArrayList<NotificationObject>();

			JSONObject jObject = new JSONObject(result);

			 get_message = jObject.getString("message").trim();
			String get_replycode = jObject.getString("status").trim();

			JSONArray jrr = jObject.getJSONArray("notifications");

			for (int i = 0; i < jrr.length(); i++) {

				JSONObject jobj = jrr.getJSONObject(i);

				NotificationObject itmObj = new NotificationObject();

				itmObj.setHeading1(jobj.getString("heading1"));
				itmObj.setHeading2(jobj.getString("heading2"));
				itmObj.setHeading3(jobj.getString("heading3"));
				itmObj.setImage(jobj.getString("image"));
				itmObj.setTimestamp(jobj.getString("timestamp"));

				dataList.add(itmObj);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (dataList.size() > 0) {
			NotificationListAdapter itmAdap = new NotificationListAdapter(
					context, dataList);

			lvJobList.setAdapter(itmAdap);
		}
		else {
			((TextView)rootView.findViewById(R.id.error_message)).setText(get_message);
		}
		
	}

}
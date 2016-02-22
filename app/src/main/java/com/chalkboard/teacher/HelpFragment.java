package com.chalkboard.teacher;

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
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chalkboard.GlobalClaass;
import com.chalkboard.R;

public class HelpFragment extends Fragment {


	View rootView = null;
	
	TextView about_company;

	Activity context = null;
	TermService termservice = null;
	Typeface font,font2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		context = getActivity();
		
		font=Typeface.createFromAsset(context.getAssets(), "fonts/mark.ttf");
		font2=Typeface.createFromAsset(context.getAssets(), "fonts/marlbold.ttf");

		rootView = inflater.inflate(R.layout.linear_page, container,
				false);
		about_company = (TextView)rootView.findViewById(R.id.about_company);
		
		try {
			about_company.setTypeface(font);
			
		} catch (Exception e) {

		}
		
		
			if(GlobalClaass.isInternetPresent(context)){

				termservice = new TermService(context);
				termservice.execute();
			}
			else {
				GlobalClaass.showToastMessage(context,"Please check internet connection");
			}
	


		return rootView;
	}
	
	
	
	class TermService extends AsyncTask<String, String, String> {

		String responseString;
		Activity context;

		public TermService(Activity ctx) {

			context = ctx;

		}

		protected void onPreExecute() {

			GlobalClaass.showProgressBar(context);

		}

		protected String doInBackground(String... params) {


			try {

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("action","pages"));
				nameValuePairs.add(new BasicNameValuePair("type", "help"));
				
				
				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();

				responseString = EntityUtils.toString(entity);
				
				Log.e("Term result",responseString);


			} catch (Exception e) {
				e.printStackTrace();
			}

			return responseString;

		}

		protected void onPostExecute(String responseString) {

			Log.e("Term result", "responseStr : " + responseString);
			JSONObject jObject, jobj;

			String get_replycode = "", name = "";

			try {

				jObject = new JSONObject(responseString);

				
				get_replycode = jObject.getString("status").trim();
				
				if(get_replycode.equalsIgnoreCase("true")){
					
					name = jObject.getString("html").trim();
					
					about_company.setText(Html.fromHtml(name));
				}
				
				
			} catch (Exception e) {

			}
			GlobalClaass.hideProgressBar(context);

		}

	}
}
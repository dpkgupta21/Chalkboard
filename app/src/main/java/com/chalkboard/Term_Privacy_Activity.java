package com.chalkboard;

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
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class Term_Privacy_Activity extends Activity{

	TextView about_heading,about_company, about_date;

	TermService termservice = null;
	Typeface font,font2;
	Activity context;
	String get_name = "";
	ImageView btn_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.term);

		context = this;
		font=Typeface.createFromAsset(getAssets(), "fonts/mark.ttf");
		font2=Typeface.createFromAsset(getAssets(), "fonts/marlbold.ttf");

		about_company = (TextView) findViewById(R.id.about_company);
		about_date = (TextView) findViewById(R.id.about_date);
		about_heading = (TextView) findViewById(R.id.about_heading);
		btn_back = (ImageView)findViewById(R.id.btn_back);
		
		try {
			about_heading.setTypeface(font2);
			about_company.setTypeface(font);
			about_date.setTypeface(font2);
			
			
		} catch (Exception e) {

		}
		
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		get_name = getIntent().getStringExtra("Name");

		if(get_name.equalsIgnoreCase("privacy-policy")){
			about_heading.setText("Privacy Policy");
		}
		else {
			about_heading.setText("Terms of Use");
		}

		try {
			Typeface font=Typeface.createFromAsset(getAssets(), "fonts/mark.ttf");
			about_heading.setTypeface(font);
			about_company.setTypeface(font);
			about_date.setTypeface(font);
		} catch (Exception e) {
			
		}
		
		if(GlobalClaass.isInternetPresent(context)){

			termservice = new TermService(context);
			termservice.execute();

		}
		else {
			GlobalClaass.showToastMessage(context,"Please check internet connection");
		}


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
				Log.e("Deepak", get_name);
				nameValuePairs.add(new BasicNameValuePair("type", get_name));


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

			String get_replycode = "", name = "", date = "";

			try {

				jObject = new JSONObject(responseString);


				get_replycode = jObject.getString("status").trim();

				if(get_replycode.equalsIgnoreCase("true")){

					name = jObject.get("html").toString().trim();
					date = jObject.get("updated").toString().trim();

					about_company.setText(Html.fromHtml(name));
					about_date.setText(date);
				}


			} catch (Exception e) {

			}
			GlobalClaass.hideProgressBar(context);

		}

	}

	
	@Override
	public void onBackPressed() {		
		finish();
	}
}

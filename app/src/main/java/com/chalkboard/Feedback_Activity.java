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
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Feedback_Activity extends Activity{

	Activity context;
	EditText et_name,et_email,et_message;
	Button btn_send;
	FeedbackService feedbackservice = null;
	TextView txt_heading,feedback,support,top_header_count;
	ImageView btn_back;
	Typeface font,font2;
	String get_activity  = "",get_activityname = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.feedback_activity);

		context = this;
		font=Typeface.createFromAsset(getAssets(), "fonts/mark.ttf");
		font2=Typeface.createFromAsset(getAssets(), "fonts/marlbold.ttf");

		get_activity = getIntent().getStringExtra("Activity");
		get_activityname = getIntent().getStringExtra("ActivityName");

		et_name = (EditText) findViewById(R.id.et_yourname);
		et_email = (EditText) findViewById(R.id.et_emailid);
		et_message = (EditText) findViewById(R.id.et_message);
		btn_send = (Button) findViewById(R.id.btn_send);
		btn_back = (ImageView) findViewById(R.id.btn_back);

		txt_heading = (TextView) findViewById(R.id.txt_heading);
		feedback = (TextView) findViewById(R.id.feedback);
		support = (TextView) findViewById(R.id.support);
		top_header_count = (TextView)findViewById(R.id.top_header_count);

		String count= GlobalClaass.getHeader_Count(context);

		if(count != null){

			if(count.length() == 1){
				top_header_count.setText("  "+count);
			}if(count.length() == 2){
				top_header_count.setText(" "+count);
			}
			else {
				top_header_count.setText(count);
			}
		}else {
			top_header_count.setVisibility(View.GONE);
		}

		try {
			et_name.setTypeface(font);
			et_email.setTypeface(font);
			et_message.setTypeface(font);
			txt_heading.setTypeface(font2);
			feedback.setTypeface(font);
			support.setTypeface(font);
			btn_send.setTypeface(font);
			top_header_count.setTypeface(font);
		} catch (Exception e) {

		}

		if(get_activity != null){

			if(get_activity.equalsIgnoreCase("support")){
				txt_heading.setText("Support");
				support.setVisibility(View.VISIBLE);
				feedback.setVisibility(View.GONE);
			}
			else {
				txt_heading.setText("Feedback");
				support.setVisibility(View.GONE);
				feedback.setVisibility(View.VISIBLE);
			}
		}


		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				ValidationValue();
			}


		});


		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(get_activityname.equalsIgnoreCase("WriteReview")){

					startActivity(new Intent(context,WriteReview.class));
					GlobalClaass.activitySlideBackAnimation(context);
					finish();
				}
				else {
					finish();
					GlobalClaass.activitySlideBackAnimation(context);

				}


			}
		});
	}


	private void ValidationValue() {
		// TODO Auto-generated method stub

		if (et_name.getText().toString().length() < 1) {

			GlobalClaass.showToastMessage(context, "Name is required.");

			return;
		}

		if (et_email.getText().toString().length() < 1) {

			GlobalClaass.showToastMessage(context, "Email is required.");

			return;
		}

		if (et_message.getText().toString().length() < 1) {

			GlobalClaass.showToastMessage(context, "Message is required.");

			return;
		}

		if(GlobalClaass.isInternetPresent(context)){
			feedbackservice = new FeedbackService(context);
			feedbackservice.execute();
		}
		else {
			GlobalClaass.showToastMessage(context,"Please check internet connection");
		}


	}



	class FeedbackService extends AsyncTask<String, String, String> {

		String responseString;
		Activity context;

		public FeedbackService(Activity ctx) {

			context = ctx;

		}

		protected void onPreExecute() {

			GlobalClaass.showProgressBar(context);

		}

		protected String doInBackground(String... params) {



			try {

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

				nameValuePairs.add(new BasicNameValuePair("action", "contact_us"));
				nameValuePairs.add(new BasicNameValuePair("name", et_name.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("email", et_email.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("message", et_message.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("type", get_activity));

				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();

				responseString = EntityUtils.toString(entity);
				Log.e("Responce",responseString);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return responseString;

		}

		protected void onPostExecute(String responseString) {

			Log.e("Feedback", "responseStr : " + responseString);
			JSONObject jObject,jobj;


			String get_replycode = "",get_message = "",userid = "",
					name = "",email = "",address= "",role = "";

			try {

				jObject = new JSONObject(responseString);

				get_message = jObject.getString("message").trim();
				get_replycode= jObject.getString("status").trim();


				if(get_replycode.equalsIgnoreCase("true")){
					GlobalClaass.showToastMessage(context, get_message);
					finish();
					GlobalClaass.activitySlideBackAnimation(context);
				}
				else {
					GlobalClaass.showToastMessage(context, get_message);
				}
			}
			catch(Exception e){

			}
			GlobalClaass.hideProgressBar(context);


		}

	}


	@Override
	public void onBackPressed() {

		if(get_activityname.equalsIgnoreCase("WriteReview")){

			startActivity(new Intent(context,WriteReview.class));
			GlobalClaass.activitySlideBackAnimation(context);
			finish();
		}
		else {

			finish();
			GlobalClaass.activitySlideBackAnimation(context);
		}

	}
}


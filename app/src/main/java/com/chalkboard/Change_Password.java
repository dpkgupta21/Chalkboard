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
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Change_Password extends Activity{
//AIzaSyDLUreJE8wKumyS6n5vwBE1zjz9RPK-oqk
	Activity context;

	EditText et_oldpassword,et_newpassword,et_confirmpassword;
	Button btn_submit;
	ImageView btn_back;
	Typeface font,font2;
	ChangePassword changepassword = null;
	TextView top_header_count;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.change_password);
		// android:textAppearance="?android:attr/textAppearanceMedium"
		context = this;
		font=Typeface.createFromAsset(getAssets(), "mark.ttf");
		font2=Typeface.createFromAsset(getAssets(), "marlbold.ttf");

		et_oldpassword = (EditText) findViewById(R.id.et_oldpassword);
		et_newpassword = (EditText) findViewById(R.id.et_newpassword);
		et_confirmpassword = (EditText) findViewById(R.id.et_confirmpassword);
		btn_submit = (Button) findViewById(R.id.btn_Change_submit);
		btn_back = (ImageView) findViewById(R.id.btn_back);
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
			et_oldpassword.setTypeface(font);
			et_newpassword.setTypeface(font);
			et_confirmpassword.setTypeface(font);
			btn_submit.setTypeface(font);
			top_header_count.setTypeface(font);
			((TextView) findViewById(R.id.txt_heading_change)).setTypeface(font2);

		} catch (Exception e) {

		}

		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				GlobalClaass.activitySlideForwardAnimation(context);

			}
		});

		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				ValidateValue();
			}


		});
	}

	private void ValidateValue() {
		// TODO Auto-generated method stub


		String current_pass = et_oldpassword.getText().toString();
		String new_pass = et_newpassword.getText().toString();
		String confirm_pass = et_confirmpassword.getText().toString();


		if (current_pass.equals("")) {

			GlobalClaass.showToastMessage(context, "Please enter Current password.");
			return;
		}

		if (new_pass.equals("")) {
			GlobalClaass.showToastMessage(context, "Please enter new password.");
			return;
		}

		if (new_pass.length() < 4) {
			GlobalClaass.showToastMessage(context, "Minimum password length should be 4 letters.");
			return;
		}

		if (confirm_pass.equals("")) {
			GlobalClaass.showToastMessage(context, "Please enter confirm password.");

		}



		if(!new_pass.equalsIgnoreCase(confirm_pass)){

			GlobalClaass.showToastMessage(context, "Confirm password does not match the new password");
			return;
		}


		if (GlobalClaass.isInternetPresent(context)) {

			changepassword = new ChangePassword(context);
			changepassword.execute();

			InputMethodManager inputManager = (InputMethodManager)
					getSystemService(Context.INPUT_METHOD_SERVICE); 

			inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);

		}

		else {
			GlobalClaass.showToastMessage(context,"Please check internet connection");
		}

	}


	public class ChangePassword extends AsyncTask<String, String, String> {

		String responseString;

		Activity context;

		JSONObject jObject1;

		boolean remember;

		public ChangePassword(Activity ctx) {
			// TODO Auto-generated constructor stub

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

				nameValuePairs.add(new BasicNameValuePair("action", "change_password"));
				nameValuePairs.add(new BasicNameValuePair("user_id", GlobalClaass.getUserId(context)));
				nameValuePairs.add(new BasicNameValuePair("password", et_newpassword.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("confirm_password", et_confirmpassword.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("current_pass", et_oldpassword.getText().toString()));


				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();

				responseString = EntityUtils.toString(entity);
				Log.e("Responce Change Password",responseString);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return responseString;

		}



		protected void onPostExecute(String responseString) {

			//{"message":"Your Password changed successfully.","status":true}
			JSONObject jObject, jobj;
			String get_status = "", get_message = "";


			try {

				jObject = new JSONObject(responseString);

				get_message = jObject.getString("message").trim();
				get_status = jObject.getString("status").trim();


				if(get_status.equalsIgnoreCase("true")){

					finish();
					GlobalClaass.activitySlideForwardAnimation(context);


				}
				else {
					GlobalClaass.showToastMessage(context, get_message);
				}

			} catch (Exception e) {

			}

			GlobalClaass.hideProgressBar(context);

		}
	}

	@Override
	public void onBackPressed() {
		finish();
		GlobalClaass.activitySlideForwardAnimation(context);

	}
}

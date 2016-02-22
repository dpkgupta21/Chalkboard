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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ForgotPassword_Activity extends Activity{

	Button btn_forgot;
	EditText txt_forgotemail;
	ForgotPass forgotpassword = null;
	Activity context;
	Typeface font,font2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.forgotpassword);

		context = this;
		font=Typeface.createFromAsset(getAssets(), "fonts/mark.ttf");
		font2=Typeface.createFromAsset(getAssets(), "fonts/marlbold.ttf");

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		txt_forgotemail = (EditText) findViewById(R.id.txt_forgotemail);
		btn_forgot = (Button) findViewById(R.id.btn_forgot);
		
		try {
			txt_forgotemail.setTypeface(font);
			btn_forgot.setTypeface(font);
			
			((TextView) findViewById(R.id.uforgotpassword)).setTypeface(font2);
			((TextView) findViewById(R.id.emailaddress)).setTypeface(font);
			
		} catch (Exception e) {

		}

		btn_forgot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				validateValues();

			}



		});
	}

	private void validateValues() {
		// TODO Auto-generated method stub
		String get_email = txt_forgotemail.getText().toString();


		if (get_email.equals("")) {

			GlobalClaass.showToastMessage(context, "Email address is required.");
			return;
		}

		if (!GlobalClaass.isValidEmail(get_email)) {

			GlobalClaass.showToastMessage(context, "Invalid email address.");

			return;
		}

		if (GlobalClaass.isInternetPresent(context)) {


			forgotpassword = new ForgotPass(context);
			forgotpassword.execute();

		} else {

		}

	}


	public class ForgotPass extends AsyncTask<String, String, String> {

		String responseString;

		Activity context;

		JSONObject jObject1;

		boolean remember;

		public ForgotPass(Activity ctx) {
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
				
				nameValuePairs.add(new BasicNameValuePair("action", "forgot"));
				nameValuePairs.add(new BasicNameValuePair("email", txt_forgotemail.getText().toString().trim()));
				
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


		protected void onPostExecute(String responseStr) {
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
		// TODO Auto-generated method stub
		finish();
	}
}

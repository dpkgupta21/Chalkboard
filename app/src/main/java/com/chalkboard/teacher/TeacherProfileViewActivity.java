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
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.ImageLoader11;
import com.chalkboard.PreferenceConnector;
import com.chalkboard.R;

public class TeacherProfileViewActivity extends Activity {

	Activity context;
	TextView txt_profilename,txt_profileaddress,txt_profiledescription,txt_profile_job;
	public ImageLoader imageloader = null;
	public ImageLoader11 imageloader11 = null;
	ShowProfile showprofile = null;
	ImageView profile_image,profile_icon;
	LinearLayout add_education,add_exepereince;

	Typeface font,font2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_teacher_profile_view);

		context = this;
		imageloader = new ImageLoader(context);
		imageloader11 = new ImageLoader11(context);

		font=Typeface.createFromAsset(getAssets(), "mark.ttf");
		font2=Typeface.createFromAsset(getAssets(), "marlbold.ttf");
		
		txt_profileaddress = (TextView) findViewById(R.id.profile_location);
		txt_profilename = (TextView) findViewById(R.id.profile_name);
		txt_profiledescription = (TextView) findViewById(R.id.profile_description);
		
		try {
			txt_profileaddress.setTypeface(font);
			txt_profilename.setTypeface(font);
			txt_profiledescription.setTypeface(font);
			((TextView) findViewById(R.id.textView1)).setTypeface(font2);
			((TextView) findViewById(R.id.current_heading)).setTypeface(font2);
//			((TextView) findViewById(R.id.education_heading)).setTypeface(font2);
			
		} catch (Exception e) {

		}
		
		//txt_profile_job = (TextView) findViewById(R.id.current_job);
		//txt_profile_education = (TextView) findViewById(R.id.education);
		profile_icon = (ImageView)findViewById(R.id.profile_icon);
		profile_image = (ImageView)findViewById(R.id.profile_image);
		add_education = (LinearLayout)findViewById(R.id.add_education);
		add_exepereince = (LinearLayout)findViewById(R.id.add_exepereince);

		if (GlobalClaass.isInternetPresent(context)) {

			showprofile = new ShowProfile();
			showprofile.execute();

		} else {

			GlobalClaass.showToastMessage(context,
					"Please check internet connection.");
		}




		if(GlobalClaass.getName(context) != null){
			txt_profilename.setText(GlobalClaass.getName(context));
		}
		if(GlobalClaass.getCity(context) != null){
			txt_profileaddress.setText(GlobalClaass.getCity(context)+", "+GlobalClaass.getCountry(context));
		}

		(findViewById(R.id.btn_back)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//startActivity(new Intent(context, TeacherProfileEditActivity.class));
				finish();
				GlobalClaass.activitySlideForwardAnimation(context);

			}
		});

		(findViewById(R.id.edit_profile)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context, TeacherProfileEditActivity.class));
				GlobalClaass.activitySlideForwardAnimation(context);
				finish();
			}
		});


	}


	class ShowProfile extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			showProgressBar(context);
		}

		@Override
		protected String doInBackground(String... params) {

			String resultStr = null;
			try {

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "getProfile"));
				nameValuePairs.add(new BasicNameValuePair("id",GlobalClaass.getUserId(context)));

				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();

				resultStr = EntityUtils.toString(entity);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return resultStr;

		}
		//09-15 11:43:19.785: E/Profile Show(6143): {"user":{"id":"680","name":"Ashok","email":"kumar@gmail.com","city":"jaipur","country":"Iceland","country_id":"111","experience":null,"certification":true,"certification_type":"1","address":"manshrovar","is_visible":true,"about":"I am fine","age":"28","gender":"Male","job_start_date":"2015-07-16","TeacherEducation":"","TeacherExperience":"","CountryPreference":"4","image":"http:\/\/128.199.234.133\/yonder\/files\/user\/image\/680\/thumb_1436962715.jpg","school_photo":"http:\/\/128.199.234.133\/yonder\/img\/user.png"},"message":"","status":true}


		@Override
		protected void onPostExecute(String resultStr) {

			Log.e("Profile Show",resultStr);
			JSONObject jObject, jobj,obj1,obj2;
			JSONArray jarray1,jarray2;

			String get_replycode = "", get_message = "";

			try {

				jObject = new JSONObject(resultStr);

				get_message = jObject.getString("message").trim();
				get_replycode = jObject.getString("status").trim();

				if(get_replycode.equalsIgnoreCase("true")){

					jobj = jObject.getJSONObject("user");

					if(!jobj.getString("name") .equalsIgnoreCase("null")){
						txt_profilename.setText(jobj.getString("name"));
					}
					if(!jobj.getString("about") .equalsIgnoreCase("null")){
						txt_profiledescription.setText(jobj.getString("about"));
					}


					String a = "",b = "",c = "";


					if(!jobj.getString("address").equalsIgnoreCase("null")){
						a = jobj.getString("address");
					}
					if(!jobj.getString("city").equalsIgnoreCase("null")){
						b = jobj.getString("city");
					}
					if(!jobj.getString("country") .equalsIgnoreCase("null")){
						c = jobj.getString("country");
					}
					if(!jobj.getString("image").equalsIgnoreCase("null")){

						imageloader.DisplayImage(jobj.getString("image"),
								profile_icon);

						imageloader11.DisplayImage(jobj.getString("image"),
								profile_image);
					}

					txt_profileaddress.setText(a+" "+b+","+c);

					/*if(!jobj.getString("TeacherEducation") .equalsIgnoreCase("null")){
						txt_profile_education.setText(jobj.getString("TeacherEducation"));
					}*/

					if (jobj.has("TeacherEducation")) {
						if (!jobj.get("TeacherEducation").toString().equalsIgnoreCase("")) {
					
					jarray1 =jobj.getJSONArray("TeacherEducation");
					
					
					if (jarray1.length() > 0) {
					
					for (int i = 0; i < jarray1.length(); i++) {
						
						 View view = LayoutInflater.from(context).inflate(R.layout.row_profile, null);
						 
						 TextView txt_degree = (TextView) view.findViewById(R.id.txt_degree);
						 TextView txt_startdate = (TextView) view.findViewById(R.id.txt_date);
						
						obj1 = jarray1.getJSONObject(i);
						txt_degree.setText(obj1.getString("title"));
						txt_startdate.setText(obj1.getString("start_date").replace("01/", "") + " - "+obj1.getString("end_date").replace("01/", ""));
						
						try {
							txt_degree.setTypeface(font);
							txt_startdate.setTypeface(font);
						} catch (Exception e) {
							// TODO: handle exception
						}
						
						add_education.addView(view);
						
					}

					} else {
						findViewById(R.id.education_heading).setVisibility(View.GONE);
					}
					
					} else {
						findViewById(R.id.education_heading).setVisibility(View.GONE);
					}
					
				}else {
					findViewById(R.id.education_heading).setVisibility(View.GONE);
				}
					
					if (jobj.has("TeacherExperience")) {
						if (!jobj.get("TeacherExperience").toString().equalsIgnoreCase("")) {
					
					jarray2 =jobj.getJSONArray("TeacherExperience");
					
					if (jarray2.length() > 0) {
					for (int j = 0; j < jarray2.length(); j++) {

						 View view = LayoutInflater.from(context).inflate(R.layout.row_profile, null);
						 
						 TextView txt_degree = (TextView) view.findViewById(R.id.txt_degree);
						 TextView txt_startdate = (TextView) view.findViewById(R.id.txt_date);
						
						obj2 = jarray2.getJSONObject(j);
						txt_degree.setText(obj2.getString("title"));
						txt_startdate.setText(obj2.getString("start_date").replace("01/", "") + " - "+obj2.getString("end_date").replace("01/", ""));
						
						try {
							txt_degree.setTypeface(font);
							txt_startdate.setTypeface(font);
						} catch (Exception e) {
							// TODO: handle exception
						}
						
						add_exepereince.addView(view);
						
					}
					} else {
						findViewById(R.id.current_heading).setVisibility(View.GONE);
					}
					}else {
						findViewById(R.id.current_heading).setVisibility(View.GONE);
					}
				}else {
					findViewById(R.id.current_heading).setVisibility(View.GONE);
				}


					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.NAME, jobj.getString("name"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.EMAIL, jobj.getString("email"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.COUNTRY, jobj.getString("country"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.CITY, jobj.getString("city"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.ADDRESS, jobj.getString("address"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.AGE, jobj.getString("age"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.GENDER, jobj.getString("gender"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.IMAGE, jobj.getString("image"));

				}
				else {
					GlobalClaass.showToastMessage(context, get_message);

				}

			} catch (Exception e) {

			}

			hideProgressBar(context);

		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

}
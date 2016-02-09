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

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Request.GraphUserCallback;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Setting_Activity extends Activity implements  ConnectionCallbacks, OnConnectionFailedListener{
	//G+

	private static final int RC_SIGN_IN = 0;

	// Google client to communicate with Google
	private GoogleApiClient mGoogleApiClient;

	private boolean mIntentInProgress;
	private boolean signedInUser;
	private ConnectionResult mConnectionResult;
	private SignInButton signinButton;

	String Gplus_id = "",Gplus_name = "",Gplus_email = "",Gplus_image = "";

	//G+ end

	// Facebook

	private Session.StatusCallback sessionStatusCallback;
	private Session currentSession;
	private static String APP_ID = "122326904786151"; 
	//private static String APP_ID = "557569857720806";
	//private static String APP_ID = "1611696319092623";
	private Facebook facebook = new Facebook(APP_ID);
	private AsyncFacebookRunner mAsyncRunner;
	private SharedPreferences mPrefs;
	String access_token;
	Boolean Connectiontimeout = false;

	String fb_id = "", fb_firstname = "", fb_lastname = "", fb_email = "",
			fb_phone = "";

	//Facebook end
	
	// Twitter

		/* Shared preference keys */
		private static final String PREF_NAME = "sample_twitter_pref";
		private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
		private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
		private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
		private static final String PREF_USER_NAME = "twitter_user_name";

		/* Any number for uniquely distinguish your request */
		public static final int WEBVIEW_REQUEST_CODE = 100;

		private ProgressDialog pDialog;

		private static Twitter twitter;
		private static RequestToken requestToken;

		private static SharedPreferences mSharedPreferences;

		private String consumerKey = null;
		private String consumerSecret = null;
		private String callbackUrl = null;
		private String oAuthVerifier = null;
       
		// Twitter
		Typeface font,font2;
        TextView top_header_count;
	ImageView btn_back,btn_chat;
    DeleteAccount deleteaccoumy = null;
    PushMessage pushmessage = null;
    String push = "";
	View rootView = null;

	Activity context;

	TextView txt_review,txt_support,txt_feedback,txt_changepassword,txt_facebook
	,txt_twitter,txt_google,txt_showemail, txt_deleteaccount,txt_facebook11,txt_google11,txt_twitter11;

	Switch notification_onoff_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_settings);

		context = this;
		font=Typeface.createFromAsset(getAssets(), "mark.ttf");
		font2=Typeface.createFromAsset(getAssets(), "marlbold.ttf");

		txt_review = (TextView)findViewById(R.id.txt_writereview);
		txt_support = (TextView)findViewById(R.id.txt_getsupport);
		txt_feedback = (TextView)findViewById(R.id.txt_givefeedback);
		txt_changepassword = (TextView)findViewById(R.id.txt_changepassword);
		txt_facebook = (TextView) findViewById(R.id.txt_facebook);
		txt_twitter = (TextView) findViewById(R.id.txt_twitter);
		txt_google = (TextView)findViewById(R.id.txt_google);
		txt_showemail = (TextView) findViewById(R.id.txt_show_email);
		notification_onoff_button = (Switch) findViewById(R.id.notification_onoff_button);
		txt_google11 = (TextView) findViewById(R.id.txt_google11);
		txt_facebook11 = (TextView) findViewById(R.id.txt_facebook11);
		txt_twitter11 = (TextView) findViewById(R.id.txt_twitter11);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		top_header_count = (TextView)findViewById(R.id.top_header_count);
		txt_deleteaccount = (TextView)findViewById(R.id.delete_account);
		
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
		
		txt_deleteaccount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
								
				if (GlobalClaass.isInternetPresent(context)) {
					Log.e("Click","12345");
					
				deleteaccoumy = new DeleteAccount(context);
				deleteaccoumy.execute();
				}
				else {
					GlobalClaass.showToastMessage(context,"Please check internet connection");
				}
			}
		});
		
		
		try {
			txt_review.setTypeface(font);
			txt_support.setTypeface(font);
			txt_feedback.setTypeface(font);
			txt_changepassword.setTypeface(font);
			txt_facebook.setTypeface(font);
			txt_twitter.setTypeface(font);
			txt_google.setTypeface(font);
			txt_showemail.setTypeface(font);
			txt_google11.setTypeface(font);
			txt_facebook11.setTypeface(font);
			txt_twitter11.setTypeface(font);
			txt_deleteaccount.setTypeface(font);
			top_header_count.setTypeface(font);
			
			((TextView) findViewById(R.id.txt_heading_setting)).setTypeface(font2);
			((TextView) findViewById(R.id.acountdetails)).setTypeface(font2);
			((TextView) findViewById(R.id.acountconnection)).setTypeface(font2);
			((TextView) findViewById(R.id.accountextra)).setTypeface(font2);
			((TextView) findViewById(R.id.settingtext)).setTypeface(font);
			((TextView) findViewById(R.id.txtpushnotification)).setTypeface(font);
			
		} catch (Exception e) {

		}
		
		if(GlobalClaass.getFacebook_ID(context) != null){
			txt_facebook.setVisibility(View.GONE);
			txt_facebook11.setVisibility(View.VISIBLE);
		}
		
		if(GlobalClaass.getTwitter_ID(context) != null){
			txt_twitter.setVisibility(View.GONE);
			txt_twitter11.setVisibility(View.VISIBLE);
		}
		
		if(GlobalClaass.getGplus_ID(context) != null){
			txt_google.setVisibility(View.GONE);
			txt_google11.setVisibility(View.VISIBLE);
		}

		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	

		txt_facebook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				connectToFB();
			}
		});


		txt_google.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				googlePlusLogin();
			}
		});
		
		txt_twitter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loginToTwitter();
			}
		});


		
		//G+
		mGoogleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();
		//G+

		
		//Facebook
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		// create instace for sessionStatusCallback
		sessionStatusCallback = new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				onSessionStateChange(session, state, exception);

			}
		};
		
		// Facebook
		
		
		// Twitter

		/* initializing twitter parameters from string.xml */
		initTwitterConfigs();

		/* Enabling strict mode */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		/* Check if required twitter keys are set */
		if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret)) {
			Toast.makeText(this, "Twitter key and secret not configured",
					Toast.LENGTH_SHORT).show();
			return;
		}

		/* Initialize application preferences */
		mSharedPreferences = getSharedPreferences(PREF_NAME, 0);

		boolean isLoggedIn = mSharedPreferences.getBoolean(
				PREF_KEY_TWITTER_LOGIN, false);

		if (isLoggedIn) {

		} else {

			Uri uri = getIntent().getData();

			if (uri != null && uri.toString().startsWith(callbackUrl)) {

				String verifier = uri.getQueryParameter(oAuthVerifier);

				try {

					/* Getting oAuth authentication token */
					twitter4j.auth.AccessToken accessToken = twitter
							.getOAuthAccessToken(requestToken, verifier);

					/* Getting user id form access token */
					long userID = accessToken.getUserId();
					final User user = twitter.showUser(userID);
					final String username = user.getName();

					/* save updated token */
					saveTwitterInfo(accessToken);
					
					if(String.valueOf(userID) != null){
						
						GlobalClaass.savePrefrencesfor(context, PreferenceConnector.Twitter_ID, String.valueOf(userID));
						
					}

				} catch (Exception e) {
					Log.e("Failed to login Twitter!!", e.getMessage());
				}
			}

		}
		// Twitter

		notification_onoff_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					
					if (GlobalClaass.isInternetPresent(context)) {
						push = "1";
						pushmessage = new PushMessage(context);
						pushmessage.execute();
						}
						else {
							GlobalClaass.showToastMessage(context,"Please check internet connection");
						}
				} else {
					
					
					if (GlobalClaass.isInternetPresent(context)) {
						push = "0";
						pushmessage = new PushMessage(context);
						pushmessage.execute();
						}
						else {
							GlobalClaass.showToastMessage(context,"Please check internet connection");
						}
					
				}
			}
		});


		if(GlobalClaass.getEMAIL(context) != null){
			txt_showemail.setText("Email Id - "+GlobalClaass.getEMAIL(context));
		}

		txt_review.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				startActivity(new Intent(context,WriteReview.class));
				GlobalClaass.activitySlideForwardAnimation(context);

			}
		});


		txt_support.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				startActivity(new Intent(context,Feedback_Activity.class)
				.putExtra("Activity", "support")
				.putExtra("ActivityName", "Setting_Activity"));
				GlobalClaass.activitySlideForwardAnimation(context);


			}
		});


		txt_feedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(context,Feedback_Activity.class)
				.putExtra("Activity", "feedback")
				.putExtra("ActivityName", "Setting_Activity"));
				GlobalClaass.activitySlideForwardAnimation(context);


			}
		});

		txt_changepassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(context,Change_Password.class));
				GlobalClaass.activitySlideForwardAnimation(context);
				//finish();
			}
		});

	}

	/**
	 * Connects the user to facebook
	 */
	public void connectToFB() {

		List<String> permissions = new ArrayList<String>();
		permissions.add("publish_actions");
		permissions.add("email");

		currentSession = new Session.Builder(context).build();
		currentSession.addCallback(sessionStatusCallback);

		Session.OpenRequest openRequest = new Session.OpenRequest(context);
		openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
		openRequest.setRequestCode(Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE);
		openRequest.setPermissions(permissions);
		currentSession.openForPublish(openRequest);

	}

	/**
	 * this method is used by the facebook API
	 */
	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (currentSession != null) {
			currentSession.onActivityResult(context, requestCode, resultCode,
					data);
		}

		switch (requestCode) {
		case RC_SIGN_IN:
			if (resultCode == context.RESULT_OK) {
				signedInUser = false;

			}
			mIntentInProgress = false;
			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
			break;
		}
	}

	@SuppressWarnings({ "deprecation", "unused" })
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (session != currentSession) {
			return;
		}

		if (state.isOpened()) {
			// Log in just happened.
			Toast.makeText(context, "session opened",
					Toast.LENGTH_SHORT).show();

			System.out.println("Token=" + session.getAccessToken());
			Request.executeMeRequestAsync(session, new GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {

						String[] separated;
						separated = user.toString().split("=");
						String json = separated[2];
						Log.e("dfxfdsfsdf", user.toString());
						Log.e("dfxfdsfsdf", json.toString());

						try {
							// Facebook Profile JSON data
							JSONObject profile = new JSONObject(json);

							// getting name of the user
							fb_id = profile.getString("id");
							Log.e("dfxfdsfsdf", fb_id.toString());
							

							if (fb_id != null) {


								txt_facebook11.setVisibility(View.VISIBLE);
								txt_facebook.setVisibility(View.GONE);

							}

						} catch (Exception e) {
							// TODO: handle exception
						}

					}
					if (response != null) {
						
					}
				}
			});

		} else if (state.isClosed()) {
			
		}
	}

	//G+

	public void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	public void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(context, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), context, 0).show();
			return;
		}

		if (!mIntentInProgress) {
			// store mConnectionResult
			mConnectionResult = result;

			if (signedInUser) {
				resolveSignInError();
			}
		}
	}



	@Override
	public void onConnected(Bundle arg0) {
		signedInUser = false;
		//Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
		getProfileInformation();
	}



	private void getProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

				Log.e("GetInfo",currentPerson.toString());				

				Gplus_name = currentPerson.getDisplayName();
				Gplus_image = currentPerson.getImage().getUrl();
				Gplus_email = Plus.AccountApi.getAccountName(mGoogleApiClient);
				Gplus_id = currentPerson.getId();
				Log.e("GetInfoID",Gplus_id);

				if (Gplus_id != null) {

					context.runOnUiThread(new Runnable() {
						@Override
						public void run() {

							txt_google.setVisibility(View.GONE);
							txt_google11.setVisibility(View.VISIBLE);

						}
					});
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();

	}



	public void signIn(View v) {
		googlePlusLogin();
	}

	public void logout(View v) {
		googlePlusLogout();
	}

	private void googlePlusLogin() {
		if (!mGoogleApiClient.isConnecting()) {
			signedInUser = true;
			resolveSignInError();
		}
	}

	private void googlePlusLogout() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();

		}
	}

	//G+ end
	
	
	// Twiiter

		private void saveTwitterInfo(twitter4j.auth.AccessToken accessToken) {

			long userID = accessToken.getUserId();

			User user;
			try {
				user = twitter.showUser(userID);

				String username = user.getName();

				/* Storing oAuth tokens to shared preferences */
				Editor e = mSharedPreferences.edit();
				e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
				e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
				e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
				e.putString(PREF_USER_NAME, username);
				e.commit();

			} catch (TwitterException e1) {
				e1.printStackTrace();
			}
		}

		/* Reading twitter essential configuration parameters from strings.xml */
		private void initTwitterConfigs() {
			consumerKey = getString(R.string.twitter_consumer_key);
			consumerSecret = getString(R.string.twitter_consumer_secret);
			callbackUrl = getString(R.string.twitter_callback);
			oAuthVerifier = getString(R.string.twitter_oauth_verifier);
		}

		private void loginToTwitter() {
			boolean isLoggedIn = mSharedPreferences.getBoolean(
					PREF_KEY_TWITTER_LOGIN, false);

			if (!isLoggedIn) {
				final ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(consumerKey);
				builder.setOAuthConsumerSecret(consumerSecret);

				final twitter4j.conf.Configuration configuration = builder.build();
				final TwitterFactory factory = new TwitterFactory(configuration);
				twitter = factory.getInstance();

				try {
					requestToken = twitter.getOAuthRequestToken(callbackUrl);

					final Intent intent = new Intent(this, WebViewActivity.class);
					intent.putExtra(WebViewActivity.EXTRA_URL,
							requestToken.getAuthenticationURL());
					startActivityForResult(intent, WEBVIEW_REQUEST_CODE);

				} catch (TwitterException e) {
					e.printStackTrace();
				}
			} else {

			}
		}

		// Twitter
		
		
		public class DeleteAccount extends AsyncTask<String, String, String> {

			String responseString;

			Activity context;

			JSONObject jObject1;

			boolean remember;

			public DeleteAccount(Activity ctx) {
				// TODO Auto-generated constructor stub

				context = ctx;

			}

			protected void onPreExecute() {

				GlobalClaass.showProgressBar(context);

			}

			protected String doInBackground(String... params) {
				Log.e("Click","2345");
				try {

					HttpClient httpClient = new DefaultHttpClient();
					HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					
					nameValuePairs.add(new BasicNameValuePair("action", "delete_account"));
					nameValuePairs.add(new BasicNameValuePair("user_id", GlobalClaass.getUserId(context)));
					
					
					request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					HttpResponse response = httpClient.execute(request);

					HttpEntity entity = response.getEntity();

					responseString = EntityUtils.toString(entity);
					Log.e("Responce delete",responseString);

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
						
						if (currentSession != null) {
							currentSession
									.closeAndClearTokenInformation();

						}
						
						if (mGoogleApiClient.isConnected()) {
							Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
							mGoogleApiClient.disconnect();
							mGoogleApiClient.connect();
							
						}
						
						GlobalClaass.showToastMessage(context, get_message);
						GlobalClaass.removePrefrences(context);
						startActivity(new Intent(context,
								Login_Activity.class));
						GlobalClaass
								.activitySlideBackAnimation(context);
						finish();
						
						
					}
					else {
						GlobalClaass.showToastMessage(context, get_message);
					}
					
				} catch (Exception e) {

				}

				GlobalClaass.hideProgressBar(context);

			}
		}
		
		
		
		
		public class PushMessage extends AsyncTask<String, String, String> {

			String responseString;

			Activity context;

			JSONObject jObject1;

			boolean remember;

			public PushMessage(Activity ctx) {
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
					
					nameValuePairs.add(new BasicNameValuePair("action", "push_setting"));
					nameValuePairs.add(new BasicNameValuePair("user_id", GlobalClaass.getUserId(context)));
					nameValuePairs.add(new BasicNameValuePair("status", push.trim()));
					
					
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
						
						GlobalClaass.showToastMessage(context, get_message);
						
					}
					else {
						GlobalClaass.showToastMessage(context, get_message);
					}
					
				} catch (Exception e) {

				}

				GlobalClaass.hideProgressBar(context);

			}
		}

}

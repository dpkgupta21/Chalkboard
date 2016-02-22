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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class SignUp_Activity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener {

	Activity context;
	Button btn_SIGNIN, btn_SIGNUP, btn_SUBMIT;
	TextView txt_TERM, txt_PRIVACY;
	EditText et_EMAIL, et_PASSWORD, et_CONPASS;
    ImageView btn_FB, btn_TWITTER, btn_GPLUS;
	TextView txt_teacher, txt_teacher1, txt_recruiter, txt_recruiter1;

	RegisterService registerservice = null;

	String Roll_Type = "teacher", android_id = "", login_type = "";

	// G+

	private static final int RC_SIGN_IN = 0;

	// Google client to communicate with Google
	private GoogleApiClient mGoogleApiClient;

	private boolean mIntentInProgress;
	private boolean signedInUser;
	private ConnectionResult mConnectionResult;
	private SignInButton signinButton;

	String Gplus_id = "", Gplus_name = "", Gplus_email = "", Gplus_image = "";

	// G+ end
	
	
	

	// Facebook

	private Session.StatusCallback sessionStatusCallback;
	private Session currentSession;
	// private static String APP_ID = "557569857720806";
	private static String APP_ID = "1611696319092623";
	private Facebook facebook = new Facebook(APP_ID);
	private AsyncFacebookRunner mAsyncRunner;
	private SharedPreferences mPrefs;
	String access_token;
	Boolean Connectiontimeout = false;

	String fb_id = "", fb_firstname = "", fb_lastname = "", fb_email = "",
			fb_phone = "";

	FacebookLogin facebooklogin = null;

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
	Typeface font;
	String  regid = "";
	
	@SuppressLint("CutPasteId")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.signup2);

		context = this;
		android_id = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		
		font=Typeface.createFromAsset(getAssets(), "fonts/mark.ttf");

		btn_SIGNIN = (Button) findViewById(R.id.btn_SIGNIN);
		btn_SIGNUP = (Button) findViewById(R.id.btn_SIGNUP);
		btn_SUBMIT = (Button) findViewById(R.id.btn_SUBMIT);
		btn_FB = (ImageView) findViewById(R.id.btn_FB);
		btn_TWITTER = (ImageView) findViewById(R.id.btn_TWITTER);
		btn_GPLUS = (ImageView) findViewById(R.id.btn_GPLUS);
		txt_PRIVACY = (TextView) findViewById(R.id.txt_PRIVACY);
		txt_TERM = (TextView) findViewById(R.id.txt_TERM);
		et_EMAIL = (EditText) findViewById(R.id.txt_EMAIL);
		et_PASSWORD = (EditText) findViewById(R.id.txt_PASSWORD);
		et_CONPASS = (EditText) findViewById(R.id.txt_CONPASSWORD);

		txt_teacher = (TextView) findViewById(R.id.txt_TEACHER);
		txt_teacher1 = (TextView) findViewById(R.id.txt_TEACHER1);
		txt_recruiter = (TextView) findViewById(R.id.txt_RECRUITER);
		txt_recruiter1 = (TextView) findViewById(R.id.txt_RECRUITER1);
		
	
		
		
		try {
			btn_SIGNIN.setTypeface(font);
			btn_SIGNUP.setTypeface(font);
			btn_SUBMIT.setTypeface(font);
			
			txt_PRIVACY.setTypeface(font);
			txt_TERM.setTypeface(font);
			et_EMAIL.setTypeface(font);
			et_PASSWORD.setTypeface(font);
			et_CONPASS.setTypeface(font);
			
			txt_teacher.setTypeface(font);
			txt_teacher1.setTypeface(font);
			txt_recruiter.setTypeface(font);
			txt_recruiter1.setTypeface(font);
			
			
			((TextView) findViewById(R.id.login_txt)).setTypeface(font);
			((TextView) findViewById(R.id.login_nexttxt)).setTypeface(font);
			((TextView) findViewById(R.id.login_and)).setTypeface(font);
			
		} catch (Exception e) {

		}

		mAsyncRunner = new AsyncFacebookRunner(facebook);
		// create instace for sessionStatusCallback
		sessionStatusCallback = new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				onSessionStateChange(session, state, exception);

			}
		};

		btn_FB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				login_type = "facebook";

				ShowOptionDialog(login_type);

			}
		});

		btn_TWITTER.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				login_type = "Twitter";

				ShowOptionDialog(login_type);
			}
		});

		btn_GPLUS.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				login_type = "GPlus";

				ShowOptionDialog(login_type);
			}
		});

		btn_SUBMIT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (GlobalClaass.isInternetPresent(context)) {

					validateValues();

				} else {

					GlobalClaass.showToastMessage(context,
							"Please check internet connection.");
				}
			}

		});

		btn_SIGNIN.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(context, Login_Activity.class));
				GlobalClaass.activitySlideForwardAnimation(context);
				finish();
			}
		});
		
		
		txt_TERM.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				startActivity(new Intent(context, Term_Privacy_Activity.class)
						.putExtra("Name", "term-coditions"));
				GlobalClaass.activitySlideForwardAnimation(context);

			}
		});

		txt_PRIVACY.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(context, Term_Privacy_Activity.class)
						.putExtra("Name", "privacy-policy"));
				GlobalClaass.activitySlideForwardAnimation(context);
			}
		});

		

		txt_recruiter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				txt_recruiter.setVisibility(View.GONE);
				txt_recruiter1.setVisibility(View.VISIBLE);
				txt_teacher.setVisibility(View.GONE);
				txt_teacher1.setVisibility(View.VISIBLE);

				Roll_Type = "recruiter";

			}
		});

		txt_teacher1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				txt_recruiter.setVisibility(View.VISIBLE);
				txt_recruiter1.setVisibility(View.GONE);
				txt_teacher.setVisibility(View.VISIBLE);
				txt_teacher1.setVisibility(View.GONE);

				Roll_Type = "teacher";
			}
		});

		txt_recruiter1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				txt_recruiter1.setVisibility(View.GONE);
				txt_recruiter.setVisibility(View.VISIBLE);
				txt_teacher1.setVisibility(View.GONE);
				txt_teacher.setVisibility(View.VISIBLE);

				Roll_Type = "teacher";

			}
		});

		txt_teacher.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				txt_recruiter1.setVisibility(View.VISIBLE);
				txt_recruiter.setVisibility(View.GONE);
				txt_teacher1.setVisibility(View.VISIBLE);
				txt_teacher.setVisibility(View.GONE);

				Roll_Type = "recruiter";
			}
		});

		// G+
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API, Plus.PlusOptions.builder().build())
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
		// G+

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

				} catch (Exception e) {
					Log.e("Failed to login Twitter!!", e.getMessage());
				}
			}

		}
		// Twitter

	}

	private void validateValues() {
		// TODO Auto-generated method stub
		if (et_EMAIL.getText().toString().length() < 1) {

			GlobalClaass
					.showToastMessage(context, "Email address is required.");

			return;
		}

		if (!GlobalClaass.isValidEmail(et_EMAIL.getText().toString())) {

			GlobalClaass.showToastMessage(context, "Invalid email address.");

			return;
		}

		if (et_PASSWORD.getText().toString().length() < 1) {

			GlobalClaass.showToastMessage(context, "Password is required.");

			return;
		}

		if (et_PASSWORD.getText().toString().length() < 4) {
			GlobalClaass.showToastMessage(context,
					"Minimum password length should be 4 letters.");
			return;
		}

		if (et_CONPASS.getText().toString().length() < 1) {

			GlobalClaass.showToastMessage(context,
					"Confirm Password is required.");

			return;
		}

		if (!et_CONPASS.getText().toString()
				.equalsIgnoreCase(et_PASSWORD.getText().toString())) {

			GlobalClaass.showToastMessage(context,
					"Confirmation password does not match the new password");
			return;
		}
		 if(GlobalClaass.isInternetPresent(context)){
				
			 registerservice = new RegisterService(context);
				registerservice.execute();
			}
			else {
				GlobalClaass.showToastMessage(context,"Please check internet connection");
			}
		
	}

	public class RegisterService extends AsyncTask<String, String, String> {

		String responseString;
		Activity context;

		public RegisterService(Activity ctx) {
			String responseString;
			context = ctx;

		}

		protected void onPreExecute() {

			GlobalClaass.showProgressBar(context);

		}

		protected String doInBackground(String... params) {
			
			try {
				GoogleCloudMessaging   gcm = GoogleCloudMessaging.getInstance(context);

				regid = gcm.register(GlobalClaass.sender_id);


			} catch (Exception e) {
				// TODO: handle exception
			}
			Log.e("DeviceID",regid);
			try {

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						6);
				nameValuePairs
						.add(new BasicNameValuePair("action", "register"));
				nameValuePairs.add(new BasicNameValuePair("email", et_EMAIL
						.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("password",
						et_PASSWORD.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("role", Roll_Type));
				nameValuePairs.add(new BasicNameValuePair("device", "android"));
				nameValuePairs.add(new BasicNameValuePair("device_id",regid));

				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();

				responseString = EntityUtils.toString(entity);
				Log.e("Responce Sign up", responseString);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return responseString;

		}

		protected void onPostExecute(String responseString) {

			Log.e("Dotsquares Login", "responseStr : " + responseString);

			JSONObject jObject;

			String get_replycode = "", get_message = "", get_id = "";

			try {

				jObject = new JSONObject(responseString);

				get_message = jObject.getString("message").trim();
				get_replycode = jObject.getString("status").trim();
				get_id = jObject.getString("id").trim();

				if (get_replycode.equalsIgnoreCase("true")) {
					GlobalClaass.showToastMessage(context, get_message);
					startActivity(new Intent(context, Login_Activity.class));
					GlobalClaass.activitySlideBackAnimation(context);
					finish();

				} else {
					GlobalClaass.showToastMessage(context, get_message);
				}

			} catch (Exception e) {

			}

			GlobalClaass.hideProgressBar(context);

		}

	}

	private void ShowOptionDialog(String type) {

		login_type = type;

		if (login_type.equalsIgnoreCase("facebook")) {

			connectToFB();
		} else if (login_type.equalsIgnoreCase("Twitter")) {

			loginToTwitter();
		} else if (login_type.equalsIgnoreCase("GPlus")) {
			googlePlusLogin();
		}

	}

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

	/**
	 * Connects the user to facebook
	 */
	public void connectToFB() {

		List<String> permissions = new ArrayList<String>();
		permissions.add("publish_stream");

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
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (currentSession != null) {
			currentSession.onActivityResult(context, requestCode, resultCode,
					data);
		}

		switch (requestCode) {
		case RC_SIGN_IN:
			if (resultCode == RESULT_OK) {
				signedInUser = false;

			}
			mIntentInProgress = false;
			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
			break;
		}
	}

	@SuppressWarnings("deprecation")
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (session != currentSession) {
			return;
		}

		if (state.isOpened()) {
			// Log in just happened.
		//	Toast.makeText(getApplicationContext(), "session opened",
				//	Toast.LENGTH_SHORT).show();

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

								context.runOnUiThread(new Runnable() {
									@Override
									public void run() {

										login_type = "facebook";
										CallFacebookLogin(login_type, fb_id,
												fb_email);

									}
								});
							}

						} catch (Exception e) {
							// TODO: handle exception
						}

					}
					if (response != null) {
						/*System.out.println("Response=" + response);
						Toast.makeText(context, response.toString(),
								Toast.LENGTH_LONG).show();*/
					}
				}
			});

		} else if (state.isClosed()) {
			// Log out just happened. Update the UI.
			/*Toast.makeText(getApplicationContext(), "session closed",
					Toast.LENGTH_SHORT).show();*/
		}
	}

	private void CallFacebookLogin(String type, String id, String email) {

		facebooklogin = new FacebookLogin(type, id, email, context);
		facebooklogin.execute();
	}

	public class FacebookLogin extends AsyncTask<String, String, String> {

		String responseString;
		String Social_id;
		String LastName;
		String Social_Email;
		String Social_Type;
		Activity context;

		JSONObject jObject1;

		boolean remember;

		public FacebookLogin(String type, String id, String email, Activity ctx) {
			// TODO Auto-generated constructor stub

			Social_Type = type;
			Social_id = id;
			Social_Email = email;
			context = ctx;

		}

		protected void onPreExecute() {

			GlobalClaass.showProgressBar(context);

		}

		@SuppressLint("NewApi")
		protected String doInBackground(String... params) {
			

			try {
				GoogleCloudMessaging   gcm = GoogleCloudMessaging.getInstance(context);

				regid = gcm.register(GlobalClaass.sender_id);

			} catch (Exception e) {
				// TODO: handle exception
			}

			if (Social_Email.equalsIgnoreCase("")) {
				Social_Email = Social_id;
			}

			try {

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				Log.e("ID", Social_id);
				Log.e("Email", Social_Email);
				Log.e("Role", Roll_Type);
				Log.e("Social_Type", Social_Type);

				nameValuePairs.add(new BasicNameValuePair("action",
						"social_connect"));
				nameValuePairs.add(new BasicNameValuePair("social_id",
						Social_id));
				nameValuePairs
						.add(new BasicNameValuePair("email", Social_Email));
				nameValuePairs.add(new BasicNameValuePair("social_type",
						Social_Type));
				nameValuePairs.add(new BasicNameValuePair("role", Roll_Type));
				nameValuePairs.add(new BasicNameValuePair("device", "android"));
				nameValuePairs.add(new BasicNameValuePair("device_id",regid));

				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();

				responseString = EntityUtils.toString(entity);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return responseString;

		}

		protected void onPostExecute(String responseString) {

			Log.e("Login With Facebook", "responseStr : " + responseString);
			JSONObject jObject, jobj;

			String get_replycode = "", get_message = "", userid = "", name = "", email = "", address = "", role = "";

			try {

				jObject = new JSONObject(responseString);

				get_message = jObject.getString("message").trim();
				get_replycode = jObject.getString("status").trim();

				if (get_replycode.equalsIgnoreCase("true")) {

					startActivity(new Intent(context, Login_Activity.class));
					GlobalClaass.activitySlideBackAnimation(context);
					finish();

				} else {
					GlobalClaass.showToastMessage(context, get_message);
				}

			} catch (Exception e) {

			}
			GlobalClaass.hideProgressBar(context);

		}
	}

	// G+

	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
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
		//Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
		getProfileInformation();
	}

	private void getProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);

				Log.e("GetInfo", currentPerson.toString());

				Gplus_name = currentPerson.getDisplayName();
				Gplus_image = currentPerson.getImage().getUrl();
				Gplus_email = Plus.AccountApi.getAccountName(mGoogleApiClient);
				Gplus_id = currentPerson.getId();
				Log.e("GetInfoID", Gplus_id);

				if (Gplus_id != null) {

					context.runOnUiThread(new Runnable() {
						@Override
						public void run() {

							login_type = "gplus";

							CallFacebookLogin(login_type, Gplus_id, Gplus_email);

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
	
	// Gplus end
	
	

	@Override
	public void onBackPressed() {
		startActivity(new Intent(context, LandingPage_Activity.class));
		GlobalClaass.activitySlideBackAnimation(context);
		finish();
	}
}
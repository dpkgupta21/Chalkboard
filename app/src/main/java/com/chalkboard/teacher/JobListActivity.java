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
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chalkboard.CountryData;
import com.chalkboard.Feedback_Activity;
import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.ImageLoader11;
import com.chalkboard.Login_Activity;
import com.chalkboard.PreferenceConnector;
import com.chalkboard.R;
import com.chalkboard.Setting_Activity;
import com.chalkboard.teacher.Certificate_Type.MyAdap;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

public class JobListActivity extends FragmentActivity implements  ConnectionCallbacks, OnConnectionFailedListener{

	Activity context = null;
	private DrawerLayout mDrawerLayout;
	private RelativeLayout mDrawerList;
	RelativeLayout rlSlideContent = null;
	private float lastTranslate = 0.0f;
	String login_type = "";

	// Facebook
	private Session.StatusCallback sessionStatusCallback;
	private Session currentSession;
	private Button logout;
	private static String APP_ID = "557569857720806";
	private Facebook facebook = new Facebook(APP_ID);
	private AsyncFacebookRunner mAsyncRunner;
	private SharedPreferences mPrefs;
	String access_token;
	Boolean Connectiontimeout = false;
	
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
		Typeface font,font2;
		GetCount getcount = null;
		

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_job_list);
		context = this;

		login_type = getIntent().getStringExtra("LoginType");
		font=Typeface.createFromAsset(getAssets(), "mark.ttf");
		font2=Typeface.createFromAsset(getAssets(), "marlbold.ttf");
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		
		mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();

		sessionStatusCallback = new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				onSessionStateChange(session, state, exception);

			}
		};

		if (login_type != null) {

			if (login_type.equalsIgnoreCase("facebook")) {
				if(GlobalClaass.isInternetPresent(context)){

					connectToFB();
				}
				else {
					GlobalClaass.showToastMessage(context,"Please check internet connection");
				}
				
			}
		}

		tvSettings = (TextView) findViewById(R.id.tvsettings);
		tvFavourites = (TextView) findViewById(R.id.tvfavorites);
		tvMatches = (TextView) findViewById(R.id.tvmatches);
		tvNotifications = (TextView) findViewById(R.id.tvmy_notifications);
		tvInbox = (TextView) findViewById(R.id.tvinbox);
		tvHelp = (TextView) findViewById(R.id.tvhelp);
		tvSearch = (TextView) findViewById(R.id.tvsearch);
		tvLogout = (TextView) findViewById(R.id.tvlogout);
		tvnoti_count = (TextView) findViewById(R.id.noti_count);
		top_header_count = (TextView) findViewById(R.id.top_header_count);
		ivSettings = (ImageView) findViewById(R.id.ivsettings);
		ivFavourites = (ImageView) findViewById(R.id.ivfavorites);
		ivMatches = (ImageView) findViewById(R.id.ivmatches);
		ivNotifications = (ImageView) findViewById(R.id.ivmy_notifications);
		ivInbox = (ImageView) findViewById(R.id.ivinbox);
		ivHelp = (ImageView) findViewById(R.id.ivhelp);
		ivSearch = (ImageView) findViewById(R.id.ivsearch);
		ivLogout = (ImageView) findViewById(R.id.ivlogout);

		llSettings = (LinearLayout) findViewById(R.id.llsettings);
		llFavourites = (LinearLayout) findViewById(R.id.llfavorites);
		llMatches = (LinearLayout) findViewById(R.id.llmatches);
		llNotifications = (LinearLayout) findViewById(R.id.llmy_notifications);
		llInbox = (LinearLayout) findViewById(R.id.llinbox);
		llHelp = (LinearLayout) findViewById(R.id.llhelp);
		llSearch = (LinearLayout) findViewById(R.id.llsearch);
		llLogout = (LinearLayout) findViewById(R.id.lllogout);

		rlSlideContent = (RelativeLayout) findViewById(R.id.slide_content);

		findViewById(R.id.header_right_menu).setVisibility(View.GONE);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setScrimColor(Color.TRANSPARENT);
		mDrawerList = (RelativeLayout) findViewById(R.id.drawer_list);

		try {
			tvSettings.setTypeface(font2);
			tvFavourites.setTypeface(font2);
			tvMatches.setTypeface(font2);
			tvNotifications.setTypeface(font2);
			tvInbox.setTypeface(font2);
			tvHelp.setTypeface(font2);
			tvSearch.setTypeface(font2);
			tvLogout.setTypeface(font2);
			top_header_count.setTypeface(font);
			((TextView) (findViewById(R.id.header_text))).setTypeface(font2);
			
		} catch (Exception e) {

		}
		
		
		(findViewById(R.id.header_left_menu))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						mDrawerLayout.openDrawer(mDrawerList);
					}
				});

		mDrawerLayout.setDrawerListener(new DrawerListener() {

			@Override
			public void onDrawerStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@SuppressLint("NewApi")
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				float moveFactor = (mDrawerList.getWidth() * slideOffset);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					rlSlideContent.setTranslationX(moveFactor);
				} else {
					TranslateAnimation anim = new TranslateAnimation(
							lastTranslate, moveFactor, 0.0f, 0.0f);
					anim.setDuration(0);
					anim.setFillAfter(true);
					rlSlideContent.startAnimation(anim);

					lastTranslate = moveFactor;
				}
			}

			@Override
			public void onDrawerOpened(View arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDrawerClosed(View arg0) {
				// TODO Auto-generated method stub

			}
		});

		// to view profile
		setProfile();

		
		(findViewById(R.id.profile_image))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						startActivity(new Intent(context,
								TeacherProfileViewActivity.class));
						GlobalClaass.activitySlideForwardAnimation(context);
					}
				});

		// Fragment fragment = new JobListFragment();
		/*
		 * Bundle args = new Bundle();
		 * args.putString(CategoryListFragment.ARG_CATEGORY_NUMBER,
		 * dataList.get(position).getId());
		 * args.putString(CategoryListFragment.ARG_CATEGORY_NAME,
		 * dataList.get(position).getName()); fragment.setArguments(args);
		 */

		// FragmentManager fragmentManager = getSupportFragmentManager();
		// fragmentManager.beginTransaction()
		// .replace(R.id.page_container, fragment).commit();

		tvSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				nutralAll();

				tvSearch.setTextColor(Color.BLACK);

				llSearch.setBackgroundResource(R.drawable.white_gradient);

				ivSearch.setImageResource(R.drawable.search_menublackk);

				findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);

				((ImageView) (findViewById(R.id.header_logo)))
						.setVisibility(View.VISIBLE);

				((TextView) (findViewById(R.id.header_text))).setText("");

				mDrawerLayout.closeDrawer(mDrawerList);
				Fragment fragment = new JobListFragment();
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.page_container, fragment).commit();

			}
		});

		tvFavourites.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				nutralAll();

				tvFavourites.setTextColor(Color.BLACK);

				llFavourites.setBackgroundResource(R.drawable.white_gradient);

				ivFavourites.setImageResource(R.drawable.favourite_menu_black);

				(findViewById(R.id.header_logo)).setVisibility(View.GONE);

				((TextView) (findViewById(R.id.header_text)))
						.setText("Favorite Jobs");

				findViewById(R.id.bottom_bar).setVisibility(View.GONE);
				mDrawerLayout.closeDrawer(mDrawerList);
				Fragment fragment = new JobFavoriteFragment();
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.page_container, fragment).commit();

			}
		});

		tvMatches.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				nutralAll();

				tvMatches.setTextColor(Color.BLACK);

				llMatches.setBackgroundResource(R.drawable.white_gradient);

				ivMatches.setImageResource(R.drawable.match_menu_black);

				(findViewById(R.id.header_logo)).setVisibility(View.GONE);

				((TextView) (findViewById(R.id.header_text)))
						.setText("Matches");

				findViewById(R.id.bottom_bar).setVisibility(View.GONE);
				mDrawerLayout.closeDrawer(mDrawerList);
				Fragment fragment = new JobMatchesFragment();
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.page_container, fragment).commit();

			}
		});
		tvNotifications.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				nutralAll();

				tvNotifications.setTextColor(Color.BLACK);
				tvnoti_count.setTextColor(Color.BLACK);
				tvnoti_count.setBackgroundResource(R.drawable.blackring);

				llNotifications.setBackgroundResource(R.drawable.white_gradient);

				ivNotifications
						.setImageResource(R.drawable.notification_menu_black);

				(findViewById(R.id.header_logo)).setVisibility(View.GONE);

				((TextView) (findViewById(R.id.header_text)))
						.setText("My Notifications");
				findViewById(R.id.bottom_bar).setVisibility(View.GONE);
				mDrawerLayout.closeDrawer(mDrawerList);
				Fragment fragment = new JobNotificationFragment();
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.page_container, fragment).commit();

			}
		});
		tvInbox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				nutralAll();

				tvInbox.setTextColor(Color.BLACK);

				llInbox.setBackgroundResource(R.drawable.white_gradient);

				ivInbox.setImageResource(R.drawable.inbox_menu_black);

				(findViewById(R.id.header_logo)).setVisibility(View.GONE);

				((TextView) (findViewById(R.id.header_text))).setText("Inbox");
				findViewById(R.id.bottom_bar).setVisibility(View.GONE);
				mDrawerLayout.closeDrawer(mDrawerList);
				Fragment fragment = new JobInboxFragment();
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.page_container, fragment).commit();

			}
		});

		tvHelp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				nutralAll();

				tvHelp.setTextColor(Color.BLACK);

				llHelp.setBackgroundResource(R.drawable.white_gradient);

				ivHelp.setImageResource(R.drawable.help_menu_black);

				(findViewById(R.id.header_logo)).setVisibility(View.GONE);

				((TextView) (findViewById(R.id.header_text))).setText("Help");

				findViewById(R.id.bottom_bar).setVisibility(View.GONE);
				mDrawerLayout.closeDrawer(mDrawerList);

				startActivity(new Intent(context,Feedback_Activity.class)
				.putExtra("Activity", "support")
				.putExtra("ActivityName", "Setting_Activity"));
				GlobalClaass.activitySlideForwardAnimation(context);
				

			}
		});

		tvSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				
				
				
				findViewById(R.id.bottom_bar).setVisibility(View.GONE);

				mDrawerLayout.closeDrawer(mDrawerList);
				
				startActivity(new Intent(context, Setting_Activity.class));

/*
				tvSettings.setTextColor(Color.BLACK);

				llSettings.setBackgroundColor(Color.WHITE);

				ivSettings.setImageResource(R.drawable.settings_menu_black);

				findViewById(R.id.bottom_bar).setVisibility(View.GONE);

				mDrawerLayout.closeDrawer(mDrawerList);

				Fragment fragment = new FragmentSettings();
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.page_container, fragment).commit();*/

			}
		});

		tvLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				findViewById(R.id.bottom_bar).setVisibility(View.GONE);
				mDrawerLayout.closeDrawer(mDrawerList);

				final AlertDialog.Builder b = new AlertDialog.Builder(context);
				b.setIcon(android.R.drawable.ic_dialog_alert);
				b.setTitle("Logout!");
				b.setMessage("Are you sure You want to logout?");
				b.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								if (currentSession != null) {
									currentSession
											.closeAndClearTokenInformation();

								}
								
								if (mGoogleApiClient.isConnected()) {
									Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
									mGoogleApiClient.disconnect();
									mGoogleApiClient.connect();
									
								}

								GlobalClaass.removePrefrences(context);
								startActivity(new Intent(context,
										Login_Activity.class));
								GlobalClaass
										.activitySlideBackAnimation(context);
								finish();
							}
						});
				b.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						});

				b.show();

			}
		});

		(findViewById(R.id.tvsearch)).performClick();

	}

	TextView tvSettings, tvFavourites, tvMatches, tvNotifications, tvInbox,
			tvHelp, tvSearch, tvLogout,tvnoti_count,top_header_count;
	ImageView ivSettings, ivFavourites, ivMatches, ivNotifications, ivInbox,
			ivHelp, ivSearch, ivLogout;
	LinearLayout llSettings, llFavourites, llMatches, llNotifications, llInbox,
			llHelp, llSearch, llLogout;

	void nutralAll() {

		tvSettings.setTextColor(Color.WHITE);
		tvFavourites.setTextColor(Color.WHITE);
		tvMatches.setTextColor(Color.WHITE);
		tvNotifications.setTextColor(Color.WHITE);
		tvInbox.setTextColor(Color.WHITE);
		tvHelp.setTextColor(Color.WHITE);
		tvSearch.setTextColor(Color.WHITE);
		tvnoti_count.setTextColor(Color.WHITE);
		
		llSettings.setBackgroundColor(Color.TRANSPARENT);
		llFavourites.setBackgroundColor(Color.TRANSPARENT);
		llMatches.setBackgroundColor(Color.TRANSPARENT);
		llNotifications.setBackgroundColor(Color.TRANSPARENT);
		llInbox.setBackgroundColor(Color.TRANSPARENT);
		llHelp.setBackgroundColor(Color.TRANSPARENT);
		llSearch.setBackgroundColor(Color.TRANSPARENT);
		tvnoti_count.setBackgroundResource(R.drawable.whitering);
		
		ivSettings.setImageResource(R.drawable.settings_menu);
		ivFavourites.setImageResource(R.drawable.favourite_menu);
		ivMatches.setImageResource(R.drawable.match_menu);
		ivNotifications.setImageResource(R.drawable.notification_menu);
		ivInbox.setImageResource(R.drawable.inbox_menu);
		ivHelp.setImageResource(R.drawable.help_menu);
		ivSearch.setImageResource(R.drawable.search_menu);

	}
	
	void setProfile(){
		
		if (GlobalClaass.getImage(context).equalsIgnoreCase("") && GlobalClaass.getImage(context) != null) {

			((ImageView) findViewById(R.id.profile_image))
					.setImageResource(R.drawable.ic_launcher);

		} else {

			ImageLoader imageloader = new ImageLoader(context);

			imageloader.DisplayImage(GlobalClaass.getImage(context),
					((ImageView) findViewById(R.id.profile_image)));
			ImageLoader11 imageloader11 = new ImageLoader11(context);
			imageloader11.DisplayImage(GlobalClaass.getImage(context),
					((ImageView) findViewById(R.id.slider_image)));
		}
		
		   if(GlobalClaass.getName(context) != null){

			if(!GlobalClaass.getName(context) .equalsIgnoreCase("") ) {
				
				((TextView) findViewById(R.id.profile_name)).setTypeface(font);
				((TextView) findViewById(R.id.profile_name)).setText(GlobalClaass
						.getName(context));
				
			}
			}
			else {
				if(!GlobalClaass.getEMAIL(context) .equalsIgnoreCase("") ) {
					
					((TextView) findViewById(R.id.profile_name)).setTypeface(font);
					((TextView) findViewById(R.id.profile_name)).setText(GlobalClaass
							.getEMAIL(context));
					
				}
			}

		String a = "",b = "",c = "",d = "";
		if(GlobalClaass.getCity(context) != null){
			a = GlobalClaass.getCity(context);
		}
		if(GlobalClaass.getCountry(context) != null){
			b = GlobalClaass.getCountry(context);
		}
		if(GlobalClaass.getAge(context) != null){
			c = GlobalClaass.getAge(context);
		}
        if(GlobalClaass.getGender(context) != null){
        	d = GlobalClaass.getGender(context);
        }
        ((TextView) findViewById(R.id.profile_location_age)).setTypeface(font);
		((TextView) findViewById(R.id.profile_location_age))
				.setText(a+ ", "+ b	+ " | "	+ c	+ ((d
								.equalsIgnoreCase("male")) ? " M" : " F"));
		

	}

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

	

	//G+

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
				GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
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
			
		}
		@Override
		public void onConnectionSuspended(int cause) {
			mGoogleApiClient.connect();

		}

		public void signIn(View v) {
			if(GlobalClaass.isInternetPresent(context)){

				googlePlusLogin();
			}
			else {
				GlobalClaass.showToastMessage(context,"Please check internet connection");
			}
			
		}

		
		private void googlePlusLogin() {
			if (!mGoogleApiClient.isConnecting()) {
				signedInUser = true;
				resolveSignInError();
			}
		}

		//G+ end

		
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

		} else if (state.isClosed()) {
			// Back

		}
	}

	@Override
	public void onBackPressed() {

		final AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setIcon(android.R.drawable.ic_dialog_alert);
		b.setTitle("Exit !");
		b.setMessage("Are you sure you want to exit?");
		b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});
		b.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});

		b.show();

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setProfile();
		
		getcount = new GetCount(context);
		getcount.execute();
		
	}

	
	public class GetCount extends AsyncTask<String, String, String> {

		String responseString;

		Activity context;

		JSONObject jObject1;

		boolean remember;

		public GetCount(Activity ctx) {
			// TODO Auto-generated constructor stub

			context = ctx;

		}

		protected void onPreExecute() {
			
		}

		protected String doInBackground(String... params) {

			try {

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				
				nameValuePairs.add(new BasicNameValuePair("action", "userStatics"));
				nameValuePairs.add(new BasicNameValuePair("user_id", GlobalClaass.getUserId(context)));
				
				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();

				responseString = EntityUtils.toString(entity);
				Log.e("Responce count",responseString);

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return responseString;

		}



		protected void onPostExecute(String responseString) {
			JSONObject jObject,Jobj;
			JSONArray jarray;
			
			String get_replycode = "", get_message = "", noticount = "", messagecount = "",creditcount = "";

			try {

				jObject = new JSONObject(responseString);
				get_replycode = jObject.getString("status").trim();
				get_message = jObject.getString("message").trim();
				noticount = jObject.getString("notification").trim();
				messagecount = jObject.getString("msgcount").trim();
				creditcount = jObject.getString("credits").trim();
				Log.e("Count show",messagecount +"  "+noticount);
				
				tvnoti_count.setText(noticount);
				

				if(noticount != null && !noticount.equalsIgnoreCase("")  && !noticount.equalsIgnoreCase("0")){
					tvnoti_count.setText(noticount);
					tvnoti_count.setVisibility(View.VISIBLE);
				}else {
					tvnoti_count.setVisibility(View.GONE);
				}
				
				
				if(messagecount != null && !messagecount.equalsIgnoreCase("")){
					
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.Header_Count, messagecount);
					
					if(messagecount.length() == 1){
						top_header_count.setText("  "+messagecount);
					}if(messagecount.length() == 2){
						top_header_count.setText(" "+messagecount);
					}
					else {
						top_header_count.setText(messagecount);
					}


				}else {
					top_header_count.setVisibility(View.GONE);
				}
				
				
			}
			catch(Exception e){
				
			}


			GlobalClaass.hideProgressBar(context);

		}
	}
}
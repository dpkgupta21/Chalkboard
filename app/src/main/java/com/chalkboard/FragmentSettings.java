package com.chalkboard;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.chalkboard.Login_Activity.FacebookLogin;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class FragmentSettings extends Fragment implements  ConnectionCallbacks, OnConnectionFailedListener{
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
		
		

	View rootView = null;

	Activity context;

	TextView txt_review,txt_support,txt_feedback,txt_changepassword,txt_facebook
	,txt_twitter,txt_google,txt_showemail, txt_deleteaccount,txt_facebook11,txt_google11;
	
	Switch notification_onoff_button;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		context = getActivity();

		rootView = inflater.inflate(R.layout.fragment_settings, container,
				false);

		txt_review = (TextView) rootView.findViewById(R.id.txt_writereview);
		txt_support = (TextView) rootView.findViewById(R.id.txt_getsupport);
		txt_feedback = (TextView) rootView.findViewById(R.id.txt_givefeedback);
		txt_changepassword = (TextView) rootView.findViewById(R.id.txt_changepassword);
		txt_facebook = (TextView)rootView. findViewById(R.id.txt_facebook);
		txt_twitter = (TextView)rootView. findViewById(R.id.txt_twitter);
		txt_google = (TextView) rootView.findViewById(R.id.txt_google);
		txt_showemail = (TextView)rootView. findViewById(R.id.txt_show_email);
		notification_onoff_button = (Switch)rootView. findViewById(R.id.notification_onoff_button);
		txt_google11 = (TextView)rootView. findViewById(R.id.txt_google11);
		txt_facebook11 = (TextView)rootView. findViewById(R.id.txt_facebook11);
		
		txt_deleteaccount = (TextView) rootView.findViewById(R.id.delete_account);
		
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
		
		
		txt_deleteaccount.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// delete_account here
				
			}
		});
		//G+
				mGoogleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();
				//G+
		
		notification_onoff_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		            // The toggle is enabled
		        } else {
		            // The toggle is disabled
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
		

		return rootView;
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
							// getting first_name of the user
							fb_firstname = profile.getString("first_name");
							Log.e("dfxfdsfsdf", fb_firstname.toString());
							// getting last_name of the user
							fb_lastname = profile.getString("last_name");
							Log.e("dfxfdsfsdf", fb_lastname.toString());
							// getting email of the user
							if (profile.has("email")) {
								fb_email = profile.getString("email");
								Log.e("dfxfdsfsdf", fb_email.toString());
							}

							if (fb_id != null) {

								context.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										
										txt_facebook11.setVisibility(View.VISIBLE);
										txt_facebook.setVisibility(View.GONE);

									}
								});
							}

						} catch (Exception e) {
							// TODO: handle exception
						}

					}
					if (response != null) {
						System.out.println("Response=" + response);
						Toast.makeText(context, response.toString(),
								Toast.LENGTH_LONG).show();
					}
				}
			});

		} else if (state.isClosed()) {
			// Log out just happened. Update the UI.
			Toast.makeText(context, "session closed",
					Toast.LENGTH_SHORT).show();
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
			Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
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
	
}

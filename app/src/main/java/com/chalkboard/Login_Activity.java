package com.chalkboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chalkboard.recruiter.navigationdrawer.TeachersListActivity;
import com.chalkboard.teacher.navigationdrawer.JobListActivity;
import com.facebook.AccessToken;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class Login_Activity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener {

    private static SharedPreferences mSharedPreferences;

    static String TWITTER_CONSUMER_KEY = "l7bIfjvfiX0FNMQVNnFh00T4Z"; // place
    // your
    // cosumer
    // key
    // here
    static String TWITTER_CONSUMER_SECRET = "rFJ54SpZanb6YlPWyWFviFXAFtby3DLAwFzKXat6fB3nnYT2lr"; // place
    // your
    // consumer
    // secret
    // here

    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

    static final String TWITTER_CALLBACK_URL = "oauth://t4jsample2";

    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "https://api.twitter.com/oauth/authorize";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "https://api.twitter.com/oauth/access_token";

    // Twitter
    private static Twitter twitter;
    private static RequestToken requestToken;
    private AccessToken accessToken;


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

    Activity context;
    Button btn_signin, btn_singup, btn_Submit;
    TextView txt_termservice, txt_privacy, txt_forgotpass;
    EditText txt_email, txt_pass;
    LoginService loginservice = null;
    TextView txt_teacher, txt_teacher1, txt_recruiter, txt_recruiter1;
    String Roll_Type = "teacher", android_id = "", login_type = "";
    //1002086743146535
    ImageView btn_fb, btn_twitter, btn_gplus;


    private Session.StatusCallback sessionStatusCallback;
    private Session currentSession;
    //private static String APP_ID = "122326904786151";
    private static String APP_ID = "481576728669882";
    // private static String APP_ID = "1611696319092623";
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
    //	private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
//	private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
//	private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";

    /* Any number for uniquely distinguish your request */
    public static final int WEBVIEW_REQUEST_CODE = 100;

    private ProgressDialog pDialog;

//	private static Twitter twitter;
//	private static RequestToken requestToken;

//	private static SharedPreferences mSharedPreferences;

    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;
    String regid = "";

    // Twitter

    Typeface font, font2;

    private long userID;

    protected twitter4j.auth.AccessToken accessTokent;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login2);

        context = this;

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(context, "" + msg.getData().getString("USERNAME").toString(),
                        Toast.LENGTH_SHORT).show();
                CallFacebookLogin("twitter", msg.getData().getString("USERID"), "");
                super.handleMessage(msg);
            }
        };

        android_id = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
        font = Typeface.createFromAsset(getAssets(), "fonts/mark.ttf");
        font2 = Typeface.createFromAsset(getAssets(), "fonts/marlbold.ttf");

        btn_signin = (Button) findViewById(R.id.btn_signIn);
        btn_singup = (Button) findViewById(R.id.btn_signUp);
        btn_fb = (ImageView) findViewById(R.id.btn_fb);
        btn_Submit = (Button) findViewById(R.id.btn_submit);
        btn_twitter = (ImageView) findViewById(R.id.btn_twitter);
        btn_gplus = (ImageView) findViewById(R.id.btn_gplus);
        txt_email = (EditText) findViewById(R.id.txtUsername);
        txt_pass = (EditText) findViewById(R.id.txtPassword);
        txt_privacy = (TextView) findViewById(R.id.txt_privacy);
        txt_termservice = (TextView) findViewById(R.id.txt_termservice);
        txt_forgotpass = (TextView) findViewById(R.id.txt_forgotpass);

        try {
            btn_signin.setTypeface(font);
            btn_singup.setTypeface(font);
            btn_Submit.setTypeface(font);

            txt_email.setTypeface(font);
            txt_pass.setTypeface(font);
            txt_privacy.setTypeface(font);
            txt_termservice.setTypeface(font);
            txt_forgotpass.setTypeface(font);


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

        txt_forgotpass.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                startActivity(new Intent(context, ForgotPassword_Activity.class));
                GlobalClaass.activitySlideForwardAnimation(context);
            }
        });

        btn_Submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (GlobalClaass.isInternetPresent(context)) {

                    validateValues();

                } else {

                    GlobalClaass.showToastMessage(context,
                            "Please check internet connection.");
                }
            }

        });

        btn_singup.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                startActivity(new Intent(context, SignUp_Activity.class));
                GlobalClaass.activitySlideForwardAnimation(context);
                finish();
            }
        });

        btn_fb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                login_type = "facebook";

                ShowOptionDialog(login_type);

            }

        });

        btn_twitter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                login_type = "Twitter";

                ShowOptionDialog(login_type);

            }
        });

        btn_gplus.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                login_type = "GPlus";

                ShowOptionDialog(login_type);

                /**
                 * Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                 * httpIntent.setData(Uri.parse("https://plus.google.com"));
                 * startActivity(httpIntent);
                 */
            }
        });

        txt_termservice.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                startActivity(new Intent(context, Term_Privacy_Activity.class)
                        .putExtra("Name", "term-coditions"));
                GlobalClaass.activitySlideForwardAnimation(context);

            }
        });

        txt_privacy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                startActivity(new Intent(context, Term_Privacy_Activity.class)
                        .putExtra("Name", "privacy-policy"));
                GlobalClaass.activitySlideForwardAnimation(context);
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
//		initTwitterConfigs();
//
//		/* Enabling strict mode */
//		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//		.permitAll().build();
//		StrictMode.setThreadPolicy(policy);
//
//		/* Check if required twitter keys are set */
//		if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret)) {
//			Toast.makeText(this, "Twitter key and secret not configured",
//					Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		/* Initialize application preferences */
        mSharedPreferences = getSharedPreferences(PREF_NAME, 0);
//
//		boolean isLoggedIn = mSharedPreferences.getBoolean(
//				PREF_KEY_TWITTER_LOGIN, false);
//
//		if (isLoggedIn) {
//
//			Toast.makeText(Login_Activity.this, ">>logged in 1111", 3000).show();
//
//		} else {
//
//			Uri uri = getIntent().getData();
//			
//			if(uri!= null)
//				Toast.makeText(Login_Activity.this, ">> not logged in "+uri.toString(), 3000).show();
//
//
//			if (uri != null && uri.toString().startsWith(callbackUrl)) {
//
//				String verifier = uri.getQueryParameter(oAuthVerifier);
//
//				try {
//
//					/* Getting oAuth authentication token */
//					twitter4j.auth.AccessToken accessToken = twitter
//							.getOAuthAccessToken(requestToken, verifier);
//
//					/* Getting user id form access token */
//					userID = accessToken.getUserId();
//					//final User user = twitter.showUser(userID);
//					//final String username = user.getName();
//
//					Log.e("UserID",userID+"       "+String.valueOf(userID));
//					Toast.makeText(Login_Activity.this, ">> userid "+userID, 3000).show();
//
//					/* save updated token */
//					saveTwitterInfo(accessToken);
//
//					if(String.valueOf(userID) != null){
//
//						GlobalClaass.savePrefrencesfor(context, PreferenceConnector.Twitter_ID, String.valueOf(userID));
//
//					}
//
//				} catch (Exception e) {
//					Log.e("Failed to login Twitter!!", e.getMessage());
//					Toast.makeText(Login_Activity.this, "exceptionnn"+e.getMessage(), 3000).show();
//
//				}
//			}
//
//		}
//		// Twitter

        /**
         * This if conditions is tested once is redirected from twitter page.
         * Parse the uri to get oAuth Verifier
         * */
        if (!isTwitterLoggedInAlready()) {
            Uri uri = getIntent().getData();
            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                // oAuth verifier
                final String verifier = uri
                        .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

                try {

                    Toast.makeText(context, "start ", Toast.LENGTH_SHORT).show();

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Get the access token
                                Login_Activity.this.accessTokent = twitter
                                        .getOAuthAccessToken(requestToken,
                                                verifier);

                                long userID = accessTokent.getUserId();
                                User user = twitter.showUser(userID);

                                // Shared Preferences
                                Editor e = mSharedPreferences.edit();

                                Bundle b2 = new Bundle();
                                b2.putString("USERID", "" + userID);
                                b2.putString("USERNAME", "" + user.getName());

                                Message msg2 = new Message();
                                msg2.setData(b2);
                                handler.sendMessage(msg2);

                                // After getting access token, access token
                                // secret
                                // store them in application preferences
                                e.putString(
                                        PREF_KEY_OAUTH_TOKEN,
                                        Login_Activity.this.accessToken
                                                .getToken() + "");
                                e.putString(PREF_KEY_OAUTH_SECRET,
                                        Login_Activity.this.accessTokent
                                                .getTokenSecret());
                                // Store login status - true
                                e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                                e.commit(); // save changes
//								Toast.makeText(context,  accessToken.getToken() + ">>"
//										+ accessTokent.getTokenSecret() + ">>>", 3000).show();

                                try {
                                    ConfigurationBuilder builder = new ConfigurationBuilder();
                                    builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                                    builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

                                    // Access Token
                                    String access_token = mSharedPreferences.getString(
                                            PREF_KEY_OAUTH_TOKEN, "");
                                    // Access Token Secret
                                    String access_token_secret = mSharedPreferences.getString(
                                            PREF_KEY_OAUTH_SECRET, "");

                                    twitter4j.auth.AccessToken accessToken = new twitter4j.auth.AccessToken(access_token,
                                            access_token_secret);
                                    Twitter twitter = new TwitterFactory(builder.build())
                                            .getInstance(accessToken);

                                    // Update status
//									twitter4j.Status response = twitter.updateStatus(""+AppGlobalData.Login_Activitydata.get(0).Title);

//									Log.d("Status", "> " + response.getText());
                                } catch (Exception e2) {
                                    // Error in updating status
                                    Log.d("Twitter Update Error", e2.getMessage());
                                    e2.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();

                    Toast.makeText(context, "userid ", Toast.LENGTH_SHORT).show();

//					// Getting user details from twitter
//					// For now i am getting his name only
//					long userID = accessTokent.getUserId();
//					User user = twitter.showUser(userID);
//					
//					String username = user.getName();

                    // Displaying in xml ui
                } catch (Exception e) {
                    // Check log for login errors

                    Log.e("Twitter Login Error", "> " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(context, "already loggedin", Toast.LENGTH_SHORT).show();

        }


    }

    private void ShowOptionDialog(String type) {

        login_type = type;

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.activity_option);
        txt_teacher = (TextView) dialog.findViewById(R.id.txt_TEACHER);
        txt_teacher1 = (TextView) dialog.findViewById(R.id.txt_TEACHER1);
        txt_recruiter = (TextView) dialog.findViewById(R.id.txt_RECRUITER);
        txt_recruiter1 = (TextView) dialog.findViewById(R.id.txt_RECRUITER1);

        Button btn_done = (Button) dialog.findViewById(R.id.btn_done);

        txt_recruiter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                GlobalClaass.showToastMessage(context, "recruiter");
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
                GlobalClaass.showToastMessage(context, "teacher");
                txt_recruiter.setVisibility(View.VISIBLE);
                txt_recruiter1.setVisibility(View.GONE);
                txt_teacher.setVisibility(View.VISIBLE);
                txt_teacher1.setVisibility(View.GONE);

                Roll_Type = "teacher";
            }
        });

        btn_done.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (login_type.equalsIgnoreCase("facebook")) {

                    connectToFB();
                } else if (login_type.equalsIgnoreCase("Twitter")) {

                    loginToTwitter();
                } else if (login_type.equalsIgnoreCase("GPlus")) {
                    googlePlusLogin();
                }

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void validateValues() {

        if (txt_email.getText().toString().length() < 1) {

            GlobalClaass
                    .showToastMessage(context, "Email address is required.");

            return;
        }

        if (!GlobalClaass.isValidEmail(txt_email.getText().toString())) {

            GlobalClaass.showToastMessage(context, "Invalid email address.");

            return;
        }

        if (txt_pass.getText().toString().length() < 1) {

            GlobalClaass.showToastMessage(context, "Password is required.");

            return;
        }
        if (GlobalClaass.isInternetPresent(context)) {

            loginservice = new LoginService(context, txt_email.getText().toString(),
                    txt_pass.getText().toString());
            loginservice.execute();
        } else {
            GlobalClaass.showToastMessage(context, "Please check internet connection");
        }

    }

    class LoginService extends AsyncTask<String, String, String> {

        String responseString;
        Activity context;
        private String emailId;
        private String password;


        public LoginService(Activity ctx, String emailId, String password) {

            context = ctx;
            this.emailId = emailId;
            this.password = password;

        }

        protected void onPreExecute() {

            GlobalClaass.showProgressBar(context);

        }

        protected String doInBackground(String... params) {

            try {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

                regid = gcm.register(GlobalClaass.sender_id);

            } catch (Exception e) {
                // TODO: handle exception
            }

            Log.e("DeviceID", regid);
            try {
                HttpParams httpParameters = new BasicHttpParams();
                // Set the timeout in milliseconds until a connection is
                // established.
                // The default value is zero, that means the timeout is not
                // used.
                int timeoutConnection = 600000;
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        timeoutConnection);
                // Set the default socket timeout (SO_TIMEOUT)
                // in milliseconds which is the timeout for waiting for data.
                int timeoutSocket = 600000;
                HttpConnectionParams
                        .setSoTimeout(httpParameters, timeoutSocket);

                HttpClient httpClient = new DefaultHttpClient(httpParameters);

                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                nameValuePairs.add(new BasicNameValuePair("action", "login"));
                nameValuePairs.add(new BasicNameValuePair("email", emailId));
                nameValuePairs.add(new BasicNameValuePair("password", password));
                nameValuePairs.add(new BasicNameValuePair("device", "android"));
                nameValuePairs.add(new BasicNameValuePair("device_id", regid));

                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(request);

                HttpEntity entity = response.getEntity();

                responseString = EntityUtils.toString(entity);
                Log.e("Responce", responseString);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseString;

        }

        protected void onPostExecute(String responseString) {

            Log.e("Dotsquares Login", "responseStr : " + responseString);
            JSONObject jObject, jobj;
            // {"user":{"id":"663","role":"recruiter","name":"Prakash","email":"pcs5998@gmail.com","country":"India","city":"Jaipur","address":"Jaipur, 20, Station Road, Barodia Scheme, Gopalbari, Jaipur, Rajasthan, India","age":null,"gender":null,"image":"files\/user\/image\/1230\/1430450423.jpg"},"message":"User
            // login successfully.","status":true}

            String get_replycode = "", get_message = "", userid = "", name = "", email = "", address = "", role = "", age = "", gender = "", city = "", image = "";

            try {

                jObject = new JSONObject(responseString);

                get_message = jObject.getString("message").trim();
                get_replycode = jObject.getString("status").trim();

                if (get_replycode.equalsIgnoreCase("true")) {

                    jobj = jObject.getJSONObject("user");

                    userid = jobj.getString("id");
                    role = jobj.getString("role");
                    name = jobj.getString("name");
                    email = jobj.getString("email");
                    address = jobj.getString("address");

                    age = jobj.getString("age");
                    gender = jobj.getString("gender");
                    city = jobj.getString("city");
                    image = jobj.getString("image");

                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.USERID, userid);
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.ROLE, role);
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.NAME, name);
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.EMAIL, email);
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.COUNTRY, "India");
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.CITY, city);
                    GlobalClaass
                            .savePrefrencesfor(context,
                                    PreferenceConnector.ADDRESS,
                                    address);
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.AGE, age);
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.GENDER, gender);
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.IMAGE, image);

                    if (jobj.has("school_photo")) {
                        GlobalClaass.savePrefrencesfor(context,
                                PreferenceConnector.School_photo, jobj.get("school_photo").toString());
                    }


                    // change it to dynamic when data available - !! IMPORTANT
                    // !!

                    if (role.equalsIgnoreCase("teacher")) {

                        startActivity(new Intent(context, JobListActivity.class)
                                .putExtra("LoginType", "Service"));
                        GlobalClaass.activitySlideForwardAnimation(context);
                        finish();

                    } else {
                        startActivity(new Intent(context,
                                TeachersListActivity.class).putExtra(
                                "LoginType", "Service"));
                        GlobalClaass.activitySlideForwardAnimation(context);
                        finish();
                    }

                } else {
                    GlobalClaass.showToastMessage(context, get_message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            GlobalClaass.hideProgressBar(context);

        }

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
            case WEBVIEW_REQUEST_CODE:
                if (resultCode == RESULT_OK)
                    Toast.makeText(Login_Activity.this, "twitter successful" + userID, Toast.LENGTH_SHORT).show();

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
            //Toast.makeText(getApplicationContext(), "session opened",
            //Toast.LENGTH_SHORT).show();

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

                                GlobalClaass.savePrefrencesfor(context, PreferenceConnector.Facebook_ID, fb_id);

                                Log.e("First", "First");
                                login_type = "facebook";

                                CallFacebookLogin(login_type, fb_id, fb_id);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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


        Log.e("Second", "Second");
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
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

                regid = gcm.register(GlobalClaass.sender_id);

            } catch (Exception e) {
                // TODO: handle exception
            }

            if (Social_Email.equalsIgnoreCase("")) {
                Social_Email = Social_id;
            }

            if (Social_Type.equalsIgnoreCase("twitter"))
                Social_Email = "";
            Log.e("Third", "Third");
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                Log.e("ID", Social_id);
                Log.e("Email", Social_Email);
                Log.e("Role", Roll_Type);
                Log.e("Social_Type", Social_Type);

                nameValuePairs.add(new BasicNameValuePair("action", "social_connect"));
                nameValuePairs.add(new BasicNameValuePair("social_id", Social_id));
                nameValuePairs.add(new BasicNameValuePair("email", Social_Email));
                nameValuePairs.add(new BasicNameValuePair("social_type", Social_Type));
                nameValuePairs.add(new BasicNameValuePair("role", Roll_Type));
                nameValuePairs.add(new BasicNameValuePair("device", "android"));
                nameValuePairs.add(new BasicNameValuePair("device_id", regid));

                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(request);

                HttpEntity entity = response.getEntity();

                responseString = EntityUtils.toString(entity);

                Log.e("EResult", "responseString");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseString;
        }

        protected void onPostExecute(String responseString) {

//			AlertDialog.Builder builder = new AlertDialog.Builder(context);
//			builder.setMessage(""+Social_Email+" "+Social_id+" "+Roll_Type+" "+Social_Type+" "+regid);
//			
//			builder.show();
            Log.e("Login With Facebook", "responseStr : " + responseString);
            JSONObject jObject, jobj;

            String get_replycode = "", get_message = "", userid = "", name = "", email = "", address = "", role = "", age = "", gender = "", city = "", image = "";

            try {

                jObject = new JSONObject(responseString);

                get_message = jObject.getString("message").trim();
                get_replycode = jObject.getString("status").trim();

                if (get_replycode.equalsIgnoreCase("true")) {

                    jobj = jObject.getJSONObject("user");

                    userid = jobj.getString("id");
                    role = jobj.getString("role");
                    name = jobj.getString("name");
                    email = jobj.getString("email");
                    address = jobj.getString("address");

                    age = jobj.getString("age");
                    gender = jobj.getString("gender");
                    city = jobj.getString("city");
                    image = jobj.getString("image");

                    if (jobj.has("school_photo")) {
                        GlobalClaass.savePrefrencesfor(context,
                                PreferenceConnector.School_photo, jobj.get("school_photo").toString());
                    }


                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.USERID, userid);
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.ROLE, role);
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.NAME, name);
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.EMAIL, email);
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.COUNTRY, "India");
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.CITY, city);
                    GlobalClaass
                            .savePrefrencesfor(context,
                                    PreferenceConnector.ADDRESS,
                                    address);
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.AGE, age);
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.GENDER, gender);
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.IMAGE, image);

                    if (role.equalsIgnoreCase("teacher")) {

                        if (Social_Type.equalsIgnoreCase("facebook")) {

                            startActivity(new Intent(context,
                                    JobListActivity.class).putExtra(
                                    "LoginType", "facebook"));

                        } else if (Social_Type.equalsIgnoreCase("gplus")) {

                            startActivity(new Intent(context,
                                    JobListActivity.class).putExtra(
                                    "LoginType", "gplus"));

                        } else {

                            startActivity(new Intent(context,
                                    JobListActivity.class).putExtra(
                                    "LoginType", "twitter"));

                        }

                        GlobalClaass.activitySlideForwardAnimation(context);
                        finish();

                    } else {

                        if (Social_Type.equalsIgnoreCase("facebook")) {

                            startActivity(new Intent(context,
                                    TeachersListActivity.class).putExtra(
                                    "LoginType", "facebook"));

                        } else if (Social_Type.equalsIgnoreCase("gplus")) {

                            startActivity(new Intent(context,
                                    TeachersListActivity.class).putExtra(
                                    "LoginType", "gplus"));

                        } else {

                            startActivity(new Intent(context,
                                    TeachersListActivity.class).putExtra(
                                    "LoginType", "twitter"));

                        }


                        GlobalClaass.activitySlideForwardAnimation(context);
                        finish();

                    }

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
                            GlobalClaass.savePrefrencesfor(context, PreferenceConnector.Gplus_ID, Gplus_id);

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

    // G+ end
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

            Toast.makeText(Login_Activity.this, ">>saveddd", Toast.LENGTH_SHORT).show();


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

//	private void loginToTwitter() {
//		boolean isLoggedIn = mSharedPreferences.getBoolean(
//				PREF_KEY_TWITTER_LOGIN, false);
//
//		if (!isLoggedIn) {
//
//			Toast.makeText(Login_Activity.this, ">>>not login", 3000).show();
//			final ConfigurationBuilder builder = new ConfigurationBuilder();
//			builder.setOAuthConsumerKey(consumerKey);
//			builder.setOAuthConsumerSecret(consumerSecret);
//
//			final twitter4j.conf.Configuration configuration = builder.build();
//			final TwitterFactory factory = new TwitterFactory(configuration);
//			twitter = factory.getInstance();
//
//			try {
//				requestToken = twitter.getOAuthRequestToken(callbackUrl);
//
//
//				final Intent intent = new Intent(this, WebViewActivity.class);
//				intent.putExtra(WebViewActivity.EXTRA_URL,
//						requestToken.getAuthenticationURL());
//				startActivityForResult(intent, WEBVIEW_REQUEST_CODE);
//
//			} catch (TwitterException e) {
//				e.printStackTrace();
//			}
//		} else {
//			Toast.makeText(Login_Activity.this, ">>> login alreadyy", 3000).show();
//		}
//	}

    // Twitter

    @Override
    public void onBackPressed() {
//        startActivity(new Intent(context, LandingPage_Activity.class));
//        GlobalClaass.activitySlideBackAnimation(context);
        finish();
    }


    /**
     * Function to login twitter
     */
    private void loginToTwitter() {
        // Check if already logged in
        if (!isTwitterLoggedInAlready()) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            twitter4j.conf.Configuration configuration = builder.build();

            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        requestToken = twitter
                                .getOAuthRequestToken(TWITTER_CALLBACK_URL);
                        Login_Activity.this.startActivity(new Intent(
                                Intent.ACTION_VIEW, Uri.parse(requestToken
                                .getAuthenticationURL())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();


        } else {
            // user already logged into twitter
            Toast.makeText(context, "already logged in 2222", Toast.LENGTH_SHORT).show();

        }

    }

    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }
}

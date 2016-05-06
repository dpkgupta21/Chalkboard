package com.chalkboard.recruiter.navigationdrawer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.chalkboard.RangeSeekBar;
import com.chalkboard.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.chalkboard.Setting_Activity;
import com.chalkboard.menucount.MenuCountHandler;
import com.chalkboard.model.MenuCountDTO;
import com.chalkboard.recruiter.MyPostedJobs;
import com.chalkboard.recruiter.RecruiterProfileEditActivity;
import com.chalkboard.recruiter.TeacherCreditFragment;
import com.chalkboard.recruiter.TeacherFavoriteFragment;
import com.chalkboard.recruiter.TeacherInboxFragment;
import com.chalkboard.recruiter.TeacherListFragment;
import com.chalkboard.recruiter.TeacherMatchesFragment;
import com.chalkboard.recruiter.TeacherNotificationFragment;
import com.chalkboard.recruiter.Teacher_Create_New_Job;
import com.chalkboard.recruiter.matchrequest.RecruiterMatchRequestFragment;
import com.chalkboard.recruiter.navigationdrawer.adapter.RecruiterNavDrawerListAdapter;
import com.chalkboard.teacher.JobObject;
import com.chalkboard.utility.Utils;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;

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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.chalkboard.GlobalClaass.hideProgressBar;
import static com.chalkboard.GlobalClaass.showProgressBar;

public class TeachersListActivity extends FragmentActivity
        implements ConnectionCallbacks, OnConnectionFailedListener {

    private Activity context = null;

    private DrawerLayout mDrawerLayout;

    //private RelativeLayout mDrawerList;
    private RelativeLayout mFilterList;

    private RelativeLayout rlSlideContent = null;
    private float lastTranslate = 0.0f;

    //private RangeSeekBar<Integer> rangeSeekBar = null;

    private int rangeMin = 1;
    private int rangeMax = 25;
    private String login_type = "";
    //Typeface font, font2;
    // private ImageView slider_image;

    // Facebook
    private Session.StatusCallback sessionStatusCallback;
    private Session currentSession;
    //private Button logout;
    private static String APP_ID = "557569857720806";
    private Facebook facebook = new Facebook(APP_ID);
    private AsyncFacebookRunner mAsyncRunner;
    private SharedPreferences mPrefs;

    private final MenuHandler myHandler =
            new MenuHandler(TeachersListActivity.this);
    private MenuCountDTO menuDTO;
    //String access_token;
    //Boolean Connectiontimeout = false;


    //G+

    private static final int RC_SIGN_IN = 0;

    // Google client to communicate with Google
    private GoogleApiClient mGoogleApiClient;

    private boolean mIntentInProgress;
    private boolean signedInUser;
    private ConnectionResult mConnectionResult;
    //private SignInButton signinButton;

    //private String Gplus_id = "", Gplus_name = "", Gplus_email = "", Gplus_image = "";

    //G+ end

    // private GetCount getcount = null;

//	TextView tvSettings, tvFavourites, tvMatches, tvNotifications, tvInbox,
//			tvHelp, tvSearch, tvLogout, tvPostNewJob, tvCredits, tvPostedJobs,tvnoti_count,tv_credit_count,top_header_count;
//
//	ImageView ivSettings, ivFavourites, ivMatches, ivNotifications, ivInbox,
//			ivHelp, ivSearch, ivLogout, ivPostNewJob, ivCredits, ivPostedJobs;
//	LinearLayout llSettings, llFavourites, llMatches, llNotifications, llInbox,
//			llHelp, llSearch, llLogout, llPostNewJob, llCredits, llPostedJobs;

    private ListView sideMenuList;
    private RelativeLayout relative_layout;
    private RecruiterNavDrawerListAdapter adapter;


    public DrawerLayout getDrawerLayout() {
        mDrawerLayout.openDrawer(mFilterList);
        return mDrawerLayout;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_teacher_list);

        context = this;

        login_type = getIntent().getStringExtra("LoginType");

        mAsyncRunner = new AsyncFacebookRunner(facebook);

        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(Plus.API, Plus.PlusOptions.builder().build()).
                addScope(Plus.SCOPE_PLUS_LOGIN).build();

        sessionStatusCallback = new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state,
                             Exception exception) {
                onSessionStateChange(session, state, exception);

            }
        };

        if (login_type != null) {

            if (login_type.equalsIgnoreCase("facebook")) {
                if (GlobalClaass.isInternetPresent(context)) {
                    connectToFB();
                } else {
                    GlobalClaass.showToastMessage(context, "Please check internet connection");
                }
            }
        }


//		tvSettings = (TextView) findViewById(R.id.tvsettings);
//		tvFavourites = (TextView) findViewById(R.id.tvfavorites);
//		tvMatches = (TextView) findViewById(R.id.tvmatches);
//		tvNotifications = (TextView) findViewById(R.id.tvmy_notifications);
//		tvInbox = (TextView) findViewById(R.id.tvinbox);
//		tvHelp = (TextView) findViewById(R.id.tvhelp);
//		tvSearch = (TextView) findViewById(R.id.tvsearch);
//		tvLogout = (TextView) findViewById(R.id.tvlogout);
//
//		tvnoti_count = (TextView) findViewById(R.id.noti_count);
//		tv_credit_count = (TextView) findViewById(R.id.credit_count);
//		top_header_count = (TextView)findViewById(R.id.top_header_count);
//
//		tvPostedJobs = (TextView) findViewById(R.id.tvposted_jobs);
//		tvPostNewJob = (TextView) findViewById(R.id.tvpost_new_job);
//		tvCredits = (TextView) findViewById(R.id.tvcredits);
//
//		ivSettings = (ImageView) findViewById(R.id.ivsettings);
//		ivFavourites = (ImageView) findViewById(R.id.ivfavorites);
//		ivMatches = (ImageView) findViewById(R.id.ivmatches);
//		ivNotifications = (ImageView) findViewById(R.id.ivmy_notifications);
//		ivInbox = (ImageView) findViewById(R.id.ivinbox);
//		ivHelp = (ImageView) findViewById(R.id.ivhelp);
//		ivSearch = (ImageView) findViewById(R.id.ivsearch);
//		ivLogout = (ImageView) findViewById(R.id.ivlogout);
//
//		ivPostedJobs = (ImageView) findViewById(R.id.ivposted_jobs);
//		ivPostNewJob = (ImageView) findViewById(R.id.ivpost_new_job);
//		ivCredits = (ImageView) findViewById(R.id.ivcredits);
//
//		llSettings = (LinearLayout) findViewById(R.id.llsettings);
//		llFavourites = (LinearLayout) findViewById(R.id.llfavorites);
//		llMatches = (LinearLayout) findViewById(R.id.llmatches);
//		llNotifications = (LinearLayout) findViewById(R.id.llmy_notifications);
//		llInbox = (LinearLayout) findViewById(R.id.llinbox);
//		llHelp = (LinearLayout) findViewById(R.id.llhelp);
//		llSearch = (LinearLayout) findViewById(R.id.llsearch);
//		llLogout = (LinearLayout) findViewById(R.id.lllogout);
//
//		llPostedJobs = (LinearLayout) findViewById(R.id.llposted_jobs);
//		llPostNewJob = (LinearLayout) findViewById(R.id.llpost_new_job);
//		llCredits = (LinearLayout) findViewById(R.id.llcredits);
//
//		try {
//			tvSettings.setTypeface(font2);
//			tvFavourites.setTypeface(font2);
//			tvMatches.setTypeface(font2);
//			tvNotifications.setTypeface(font2);
//			tvInbox.setTypeface(font2);
//			tvHelp.setTypeface(font2);
//			tvSearch.setTypeface(font2);
//			tvLogout.setTypeface(font2);
//			tvPostedJobs.setTypeface(font2);
//			tvPostNewJob.setTypeface(font2);
//			tvCredits.setTypeface(font2);
//			tv_credit_count.setTypeface(font);
//			tvnoti_count.setTypeface(font);
//			top_header_count.setTypeface(font);
//			((TextView) (findViewById(R.id.header_text))).setTypeface(font2);
//			((TextView) findViewById(R.id.rangevalue)).setTypeface(font);
//
//
//			((TextView) findViewById(R.id.filtertxt)).setTypeface(font2);
//			((TextView) findViewById(R.id.selectjob_txt)).setTypeface(font2);
//			((TextView) findViewById(R.id.selectcompany_txt)).setTypeface(font2);
//			((TextView) findViewById(R.id.selectsalary_txt)).setTypeface(font2);
//			((TextView) findViewById(R.id.selectedno)).setTypeface(font);
//			((EditText) findViewById(R.id.country_search_list)).setTypeface(font);
//
//		} catch (Exception e) {
//
//		}


        GlobalClaass.savePrefrencesfor(context,
                PreferenceConnector.COUNTRIESARRAY, "");
        GlobalClaass.savePrefrencesfor(context, PreferenceConnector.TYPEARRAY,
                "");
        GlobalClaass.savePrefrencesfor(context, PreferenceConnector.MINVALUE,
                "");
        GlobalClaass.savePrefrencesfor(context, PreferenceConnector.MAXVALUE,
                "");

        RangeSeekBar<Integer> rangeSeekBar = (RangeSeekBar<Integer>)
                findViewById(R.id.range_seek);

        rangeSeekBar
                .setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {

                    @Override
                    public void onRangeSeekBarValuesChanged(
                            RangeSeekBar<?> bar, Integer minValue,
                            Integer maxValue) {

                        ((TextView) findViewById(R.id.rangevalue))
                                .setText(minValue + " - " + maxValue + "K");

                        rangeMin = minValue;
                        rangeMax = maxValue;

                        GlobalClaass.savePrefrencesfor(context,
                                PreferenceConnector.MINVALUE, minValue + "");
                        GlobalClaass.savePrefrencesfor(context,
                                PreferenceConnector.MAXVALUE, maxValue + "");

                    }
                });

        rlSlideContent = (RelativeLayout) findViewById(R.id.slide_content);

        String[] navMenuTitles = getResources().getStringArray(
                R.array.recruiter_drawer_items);

        // nav drawer icons from resources
        TypedArray navMenuUnSelectIcons = getResources().obtainTypedArray(
                R.array.recruiter_unselected_drawer_icons);

        TypedArray navMenuSelectIcons = getResources().obtainTypedArray(
                R.array.recruiter_selected_drawer_icons);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        sideMenuList = (ListView) findViewById(R.id.list_slidermenu);
        sideMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        relative_layout = (RelativeLayout) findViewById(R.id.relative_layout);
        int width = 3 * (getResources().getDisplayMetrics().widthPixels / 4);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) relative_layout
                .getLayoutParams();
        params.width = width;
        relative_layout.setLayoutParams(params);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);

        ArrayList<RecruiterNavDrawerItem> navDrawerItems = new ArrayList<RecruiterNavDrawerItem>();

        // adding nav drawer items to array
        // Post new Job
        navDrawerItems.add(new RecruiterNavDrawerItem(navMenuTitles[0], navMenuUnSelectIcons
                .getResourceId(0, -1), navMenuSelectIcons
                .getResourceId(0, -1)));
        // Credits
        navDrawerItems.add(new RecruiterNavDrawerItem(navMenuTitles[1], navMenuUnSelectIcons
                .getResourceId(1, -1), navMenuSelectIcons
                .getResourceId(1, -1)));
        // Search
        navDrawerItems.add(new RecruiterNavDrawerItem(navMenuTitles[2], navMenuUnSelectIcons
                .getResourceId(2, -1), navMenuSelectIcons
                .getResourceId(2, -1)));
        // Fav
        navDrawerItems.add(new RecruiterNavDrawerItem(navMenuTitles[3], navMenuUnSelectIcons
                .getResourceId(3, -1), navMenuSelectIcons
                .getResourceId(3, -1)));
        // Match Request
        navDrawerItems.add(new RecruiterNavDrawerItem(navMenuTitles[4], navMenuUnSelectIcons
                .getResourceId(4, -1), navMenuSelectIcons
                .getResourceId(4, -1)));
        // Match
        navDrawerItems.add(new RecruiterNavDrawerItem(navMenuTitles[5], navMenuUnSelectIcons
                .getResourceId(5, -1), navMenuSelectIcons
                .getResourceId(5, -1)));
        // Posted Jobs
        navDrawerItems.add(new RecruiterNavDrawerItem(navMenuTitles[6], navMenuUnSelectIcons
                .getResourceId(6, -1), navMenuSelectIcons
                .getResourceId(6, -1)));
        // Inbox
        navDrawerItems.add(new RecruiterNavDrawerItem(navMenuTitles[7], navMenuUnSelectIcons
                .getResourceId(7, -1), navMenuSelectIcons
                .getResourceId(7, -1)));
        // Help
        navDrawerItems.add(new RecruiterNavDrawerItem(navMenuTitles[8], navMenuUnSelectIcons
                .getResourceId(8, -1), navMenuSelectIcons
                .getResourceId(8, -1)));
        // Settings
        navDrawerItems.add(new RecruiterNavDrawerItem(navMenuTitles[9], navMenuUnSelectIcons
                .getResourceId(9, -1), navMenuSelectIcons
                .getResourceId(9, -1)));
        //Logout
        navDrawerItems.add(new RecruiterNavDrawerItem(navMenuTitles[10], navMenuUnSelectIcons
                .getResourceId(10, -1), navMenuSelectIcons
                .getResourceId(10, -1)));


        // Recycle the typed array
        navMenuUnSelectIcons.recycle();
        navMenuSelectIcons.recycle();

//		mDrawerList = (RelativeLayout) findViewById(R.id.drawer_list);

        sideMenuList.setOnItemClickListener(drawerListItemClickListener);
        adapter = new RecruiterNavDrawerListAdapter(
                getApplicationContext(), navDrawerItems);
        sideMenuList.setAdapter(adapter);
        sideMenuList.setItemChecked(2, true);

        mFilterList = (RelativeLayout) findViewById(R.id.drawer_filter);


        (findViewById(R.id.header_left_menu))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        mDrawerLayout.openDrawer(relative_layout);
                    }
                });

        (findViewById(R.id.header_right_menu))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        mDrawerLayout.openDrawer(mFilterList);
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
                float moveFactor;

                if (drawerView == relative_layout) {
                    moveFactor = (drawerView.getWidth() * slideOffset);
                } else {
                    moveFactor = -(drawerView.getWidth() * slideOffset);

                }

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
            public void onDrawerClosed(View drawerView) {
                // TODO Auto-generated method stub

                if (drawerView == relative_layout) {
                    // moveFactor = (drawerView.getWidth() * slideOffset);
                } else {
                    // moveFactor = -(drawerView.getWidth() * slideOffset);

                    setDrawerState(true);

                    Fragment fragment1 = new TeacherCreditFragment();

                    Fragment fragment = new TeacherListFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, fragment1).commit();
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, fragment).commit();

                }

            }
        });

        // to view profile
        (findViewById(R.id.profile_image))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        mDrawerLayout.closeDrawer(relative_layout);
                        startActivity(new Intent(context,
                                RecruiterProfileEditActivity.class));
                    }
                });

        // // // // // // // // //
        setDrawerState(true);
        //top_header_count.setVisibility(View.GONE);
        Fragment fragment = new TeacherListFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.page_container, fragment).commit();

        // // // // // // // // //

        setProfile();

//		tvPostNewJob
//		.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//
//
//
//				mDrawerLayout.closeDrawer(mDrawerList);
//
//				startActivity(new Intent(context,
//						Teacher_Create_New_Job.class));
//			}
//		});
//
//		tvCredits.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//
//				top_header_count.setVisibility(View.VISIBLE);
//				nutralAll();
//
//				tvCredits.setTextColor(Color.BLACK);
//
//				tv_credit_count.setTextColor(Color.BLACK);
//				tv_credit_count.setBackgroundResource(R.drawable.blackring);
//
//				llCredits.setBackgroundResource(R.drawable.white_gradient);
//
//				ivCredits.setImageResource(R.drawable.credit_menu_black);
//
//				setDrawerState(false);
//
//
//
//				((ImageView) (findViewById(R.id.header_logo)))
//				.setVisibility(View.GONE);
//
//				((TextView) (findViewById(R.id.header_text))).setText("Credits");
//
//
//
//				mDrawerLayout.closeDrawer(mDrawerList);
//				Fragment fragment = new TeacherCreditFragment();
//				FragmentManager fragmentManager = getSupportFragmentManager();
//				fragmentManager.beginTransaction()
//				.replace(R.id.page_container, fragment).commit();
//			}
//		});
//
//        tvSearch.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//
//                top_header_count.setVisibility(View.GONE);
//
//                nutralAll();
//
//                tvSearch.setTextColor(Color.BLACK);
//
//                llSearch.setBackgroundResource(R.drawable.white_gradient);
//
//                ivSearch.setImageResource
//                        (R.drawable.search_menublackk)
//                ;
//                ((ImageView) (findViewById(R.id.header_logo)))
//                        .setVisibility(View.VISIBLE);
//
//                ((TextView) (findViewById(R.id.header_text))).setText("");
//
//                mDrawerLayout.closeDrawer(mDrawerList);
//
//                setDrawerState(true);
//                mDrawerLayout.closeDrawer(mDrawerList);
//                Fragment fragment = new TeacherListFragment();
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.page_container, fragment).commit();
//            }
//        });
//
//        tvFavourites
//                .setOnClickListener(new OnClickListener() {
//
//                    @Override
//                    public void onClick(View arg0) {
//
//                        top_header_count.setVisibility(View.VISIBLE);
//
//                        nutralAll();
//
//                        tvFavourites.setTextColor(Color.BLACK);
//
//                        llFavourites.setBackgroundResource(R.drawable.white_gradient);
//                        ivFavourites.setImageResource
//                                (
//                                        R.drawable.favourite_menu_black);
//
//                        setDrawerState(false);
//
//                        ((ImageView) (findViewById(R.id.header_logo)))
//                                .setVisibility(View.GONE);
//
//                        ((TextView) (findViewById(R.id.header_text))).setText("My Favorites");
//
//                        mDrawerLayout.closeDrawer(mDrawerList);
//                        Fragment fragment = new TeacherFavoriteFragment();
//                        FragmentManager fragmentManager = getSupportFragmentManager();
//                        fragmentManager.beginTransaction()
//                                .replace(R.id.page_container, fragment)
//                                .commit();
//                    }
//                });
//
//        tvMatches.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                top_header_count.setVisibility(View.VISIBLE);
//                nutralAll();
//
//                tvMatches.setTextColor(Color.BLACK);
//
//                llMatches.setBackgroundResource(R.drawable.white_gradient);
//
//                ivMatches
//                        .setImageResource
//                                (
//                                        R.drawable.match_menu_black);
//
//
//                setDrawerState(false);
//
//                ((ImageView) (findViewById(R.id.header_logo)))
//                        .setVisibility(View.GONE);
//
//                ((TextView) (findViewById(R.id.header_text))).setText("Matches");
//                mDrawerLayout.closeDrawer(mDrawerList);
//                Fragment fragment = new TeacherMatchesFragment();
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.page_container, fragment).commit();
//            }
//        });

//        tvNotifications
//                .setOnClickListener(new OnClickListener() {
//
//                    @Override
//                    public void onClick(View arg0) {
//                        top_header_count.setVisibility(View.VISIBLE);
//                        nutralAll();
//
//                        tvNotifications.setTextColor(Color.BLACK);
//                        tvnoti_count.setTextColor(Color.BLACK);
//                        tvnoti_count.setBackgroundResource(R.drawable.blackring);
//
//                        llNotifications.setBackgroundResource(R.drawable.white_gradient);
//
//                        ivNotifications.setImageResource(R.drawable.notification_menu_black);
//
//                        mDrawerLayout.closeDrawer(mDrawerList);
//                        setDrawerState(false);
//
//                        ((ImageView) (findViewById(R.id.header_logo)))
//                                .setVisibility(View.GONE);
//
//                        ((TextView) (findViewById(R.id.header_text))).setText("My Notifications");
//
//                        Fragment fragment = new TeacherNotificationFragment();
//                        FragmentManager fragmentManager = getSupportFragmentManager();
//                        fragmentManager.beginTransaction()
//                                .replace(R.id.page_container, fragment)
//                                .commit();
//                    }
//                });

//        tvPostedJobs
//                .setOnClickListener(new OnClickListener() {
//
//                    @Override
//                    public void onClick(View arg0) {
//                        top_header_count.setVisibility(View.VISIBLE);
//                        nutralAll();
//
//                        tvPostedJobs.setTextColor(Color.BLACK);
//
//                        llPostedJobs.setBackgroundResource(R.drawable.white_gradient);
//
//                        ivPostedJobs.setImageResource(R.drawable.posted_job_menu_black);
//
//
//                        setDrawerState(false);
//
//                        ((ImageView) (findViewById(R.id.header_logo)))
//                                .setVisibility(View.GONE);
//
//                        ((TextView) (findViewById(R.id.header_text))).setText("Posted Jobs");
//
//                        mDrawerLayout.closeDrawer(mDrawerList);
//                        Fragment fragment = new MyPostedJobs();
//                        FragmentManager fragmentManager = getSupportFragmentManager();
//                        fragmentManager.beginTransaction()
//                                .replace(R.id.page_container, fragment)
//                                .commit();
//                    }
//                });
//
//        tvInbox.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                top_header_count.setVisibility(View.VISIBLE);
//                nutralAll();
//
//                tvInbox.setTextColor(Color.BLACK);
//
//                llInbox.setBackgroundResource(R.drawable.white_gradient);
//
//                ivInbox.setImageResource
//                        (R.drawable.inbox_menu_black)
//                ;
//
//                mDrawerLayout.closeDrawer(mDrawerList);
//                setDrawerState(false);
//
//                ((ImageView) (findViewById(R.id.header_logo)))
//                        .setVisibility(View.GONE);
//
//                ((TextView) (findViewById(R.id.header_text))).setText("Inbox");
//
//                Fragment fragment = new TeacherInboxFragment();
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.page_container, fragment).commit();
//            }
//        });

//        tvHelp.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                top_header_count.setVisibility(View.VISIBLE);
//                nutralAll();
//
//                tvHelp.setTextColor(Color.BLACK);
//
//                llHelp.setBackgroundResource(R.drawable.white_gradient);
//
//                ivHelp.setImageResource
//                        (R.drawable.help_menu_black)
//                ;
//
//                mDrawerLayout.closeDrawer(mDrawerList);
//                setDrawerState(false);
//
//                ((ImageView) (findViewById(R.id.header_logo)))
//                        .setVisibility(View.VISIBLE);
//
//                ((TextView) (findViewById(R.id.header_text))).setText("");
//
//                startActivity(new Intent(context, Feedback_Activity.class)
//                        .putExtra("Activity", "support")
//                        .putExtra("ActivityName", "Setting_Activity"));
//                GlobalClaass.activitySlideForwardAnimation(context);
//            }
//        });

//        tvSettings.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//
//
//                mDrawerLayout.closeDrawer(mDrawerList);
//                startActivity(new Intent(context, Setting_Activity.class));
//
//
//				/*	tvSettings.setTextColor(Color.BLACK);
//
//				llSettings.setBackgroundColor(Color.WHITE);
//
//				ivSettings.setImageResource
//				(
//						R.drawable.settings_menu_black);
//				mDrawerLayout.closeDrawer(mDrawerList);
//				setDrawerState(false);
//
//				((ImageView) (findViewById(R.id.header_logo)))
//				.setVisibility(View.VISIBLE);
//
//		       ((TextView) (findViewById(R.id.header_text))).setText("");
//
//				mDrawerLayout.closeDrawer(mDrawerList);
//				Fragment fragment = new FragmentSettings();
//				FragmentManager fragmentManager = getSupportFragmentManager();
//				fragmentManager.beginTransaction()
//				.replace(R.id.page_container, fragment).commit();*/
//            }
//        });


//        tvLogout.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//
//                mDrawerLayout.closeDrawer(mDrawerList);
//
//                ((TextView) (findViewById(R.id.header_text))).setText("");
//
//                mDrawerLayout.closeDrawer(mDrawerList);
//
//                final AlertDialog.Builder b = new AlertDialog.Builder(context);
//                b.setIcon(android.R.drawable.ic_dialog_alert);
//                b.setTitle("Logout!");
//                b.setMessage("Are you sure You want to logout?");
//                b.setPositiveButton("Yes",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int whichButton) {
//
//                                if (currentSession != null) {
//                                    currentSession
//                                            .closeAndClearTokenInformation();
//
//                                }
//
//                                if (mGoogleApiClient.isConnected()) {
//                                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//                                    mGoogleApiClient.disconnect();
//                                    mGoogleApiClient.connect();
//
//                                }
//
//                                GlobalClaass.removePrefrences(context);
//                                startActivity(new Intent(context,
//                                        Login_Activity.class));
//                                GlobalClaass
//                                        .activitySlideBackAnimation(context);
//                                finish();
//                            }
//                        });
//                b.setNegativeButton("No",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int whichButton) {
//
//                            }
//                        });
//
//                b.show();
//
//            }
//        });


        if (GlobalClaass.isInternetPresent(context)) {

            countrylist = new CountryList(context);
            countrylist.execute();

            getPostedJobs = new GetPostedJobs();
            getPostedJobs.execute();

        } else {
            GlobalClaass.showToastMessage(context,
                    "Please check internet connection");
        }

        posted_jobs_list = (ListView) findViewById(R.id.posted_jobs_list);

        country_list = (ListView) findViewById(R.id.country_list);
        edt_country = (EditText) findViewById(R.id.country_search_list);
        edt_country.setVisibility(View.VISIBLE);
    }


    private AdapterView.OnItemClickListener drawerListItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent intent = null;
            Fragment fragment = null;
            FragmentManager fragmentManager = null;
            // Call Menu count
            new Thread(new MenuCountHandler(myHandler,
                    TeachersListActivity.this)).start();
            switch (position) {
                case 0:
                    mDrawerLayout.closeDrawer(relative_layout);

                    startActivity(new Intent(context,
                            Teacher_Create_New_Job.class));
                    break;
                case 1:
                    setDrawerState(false);


                    ((ImageView) (findViewById(R.id.header_logo)))
                            .setVisibility(View.GONE);

                    ((TextView) (findViewById(R.id.header_text))).setText("Credits");


                    mDrawerLayout.closeDrawer(relative_layout);
                    fragment = new TeacherCreditFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, fragment).commit();

                    break;
                case 2:

                    ((ImageView) (findViewById(R.id.header_logo)))
                            .setVisibility(View.VISIBLE);

                    ((TextView) (findViewById(R.id.header_text))).setText("");

                    mDrawerLayout.closeDrawer(relative_layout);

                    setDrawerState(true);
                    fragment = new TeacherListFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, fragment).commit();

                    break;
                case 3:
                    // My Favorites
                    setDrawerState(false);

                    ((ImageView) (findViewById(R.id.header_logo)))
                            .setVisibility(View.GONE);

                    ((TextView) (findViewById(R.id.header_text))).setText("My Favorites");

                    mDrawerLayout.closeDrawer(relative_layout);
                    fragment = new TeacherFavoriteFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, fragment)
                            .commit();

                    break;


                case 4:
                    // match Request
                    setDrawerState(false);

                    ((ImageView) (findViewById(R.id.header_logo)))
                            .setVisibility(View.GONE);

                    ((TextView) (findViewById(R.id.header_text))).setText("Match Requests");
                    mDrawerLayout.closeDrawer(relative_layout);
                    fragment = new RecruiterMatchRequestFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, fragment).commit();
                    break;
                case 5:

                    //Matches

                    setDrawerState(false);

                    ((ImageView) (findViewById(R.id.header_logo)))
                            .setVisibility(View.GONE);

                    ((TextView) (findViewById(R.id.header_text))).setText("Matches");
                    mDrawerLayout.closeDrawer(relative_layout);
                    fragment = new TeacherMatchesFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, fragment).commit();
                    break;
//                case 6:
//                    mDrawerLayout.closeDrawer(relative_layout);
//                    setDrawerState(false);
//
//                    ((ImageView) (findViewById(R.id.header_logo)))
//                            .setVisibility(View.GONE);
//
//                    ((TextView) (findViewById(R.id.header_text))).setText("My Notifications");
//
//                    fragment = new TeacherNotificationFragment();
//                    fragmentManager = getSupportFragmentManager();
//                    fragmentManager.beginTransaction()
//                            .replace(R.id.page_container, fragment)
//                            .commit();
//                    break;
                case 6:

                    setDrawerState(false);

                    ((ImageView) (findViewById(R.id.header_logo)))
                            .setVisibility(View.GONE);

                    ((TextView) (findViewById(R.id.header_text))).setText("Posted Jobs");

                    mDrawerLayout.closeDrawer(relative_layout);
                    fragment = new MyPostedJobs();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, fragment)
                            .commit();
                    break;
                case 7:
                    mDrawerLayout.closeDrawer(relative_layout);
                    setDrawerState(false);

                    ((ImageView) (findViewById(R.id.header_logo)))
                            .setVisibility(View.GONE);

                    ((TextView) (findViewById(R.id.header_text))).setText("Inbox");

                    fragment = new TeacherInboxFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, fragment).commit();
                    break;
                case 8:
                    mDrawerLayout.closeDrawer(relative_layout);
                    setDrawerState(false);

                    ((ImageView) (findViewById(R.id.header_logo)))
                            .setVisibility(View.VISIBLE);

                    ((TextView) (findViewById(R.id.header_text))).setText("");

                    startActivity(new Intent(context, Feedback_Activity.class)
                            .putExtra("Activity", "support")
                            .putExtra("ActivityName", "Setting_Activity"));
                    GlobalClaass.activitySlideForwardAnimation(context);

                    break;
                case 9:
                    mDrawerLayout.closeDrawer(relative_layout);
                    startActivity(new Intent(context, Setting_Activity.class));

                    break;
                case 10:
                    ((TextView) (findViewById(R.id.header_text))).setText("");

                    mDrawerLayout.closeDrawer(relative_layout);

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
                    break;

            }
            adapter.notifyDataSetChanged();


        }
    };


    public static class MenuHandler extends Handler {


        public final WeakReference<TeachersListActivity> mActivity;

        MenuHandler(TeachersListActivity activity) {
            mActivity = new WeakReference<TeachersListActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Utils.ShowLog("TAG", "handleMessage in MenuHandler");
            TeachersListActivity activity = mActivity.get();
            activity.menuDTO = ((MenuCountDTO) msg.obj);
            activity.adapter.setMenuCountDTO(activity.menuDTO);
            activity.adapter.notifyDataSetChanged();
        }
    }

    private void setProfile() {
        if (GlobalClaass.getImage(context).equalsIgnoreCase("")) {

            ((ImageView) findViewById(R.id.profile_image))
                    .setImageResource(R.drawable.ic_launcher);

        } else {

            ImageLoader imageloader = new ImageLoader(context);

            imageloader.DisplayImage(GlobalClaass.getImage(context),
                    ((ImageView) findViewById(R.id.profile_image)));

            ImageLoader11 imageloader11 = new ImageLoader11(context);
            imageloader11.DisplayImage(GlobalClaass.getSchool_photo(context),
                    ((ImageView) findViewById(R.id.slider_image)));
        }

        if (GlobalClaass.getName(context) != null) {

            if (!GlobalClaass.getName(context).equalsIgnoreCase("")) {

                ((TextView) findViewById(R.id.profile_name)).setText(GlobalClaass
                        .getName(context));
                //((TextView) findViewById(R.id.profile_name)).setTypeface(font);
            }
        } else {
            if (!GlobalClaass.getEMAIL(context).equalsIgnoreCase("")) {

                ((TextView) findViewById(R.id.profile_name)).setText(GlobalClaass
                        .getEMAIL(context));
                // ((TextView) findViewById(R.id.profile_name)).setTypeface(font);
            }
        }

        String a = "", b = "";
        String cityCountry = null;
        if (!GlobalClaass.getCity(context).equalsIgnoreCase("")) {
            a = GlobalClaass.getCity(context);
        }
        if (!GlobalClaass.getCountry(context).equalsIgnoreCase("")) {
            b = GlobalClaass.getCountry(context);
        }

        if (!a.equalsIgnoreCase("")) {
            cityCountry = a;
            if (!b.equalsIgnoreCase("")) {
                cityCountry = a + ", " + b;
            }
        } else {
            cityCountry = b;
        }

        ((TextView) findViewById(R.id.profile_location_age)).setText(cityCountry);

        //((TextView) findViewById(R.id.profile_location_age)).setTypeface(font);
    }

//    void nutralAll() {
//
//        tvSettings.setTextColor(Color.WHITE);
//        tvFavourites.setTextColor(Color.WHITE);
//        tvMatches.setTextColor(Color.WHITE);
//        tvNotifications.setTextColor(Color.WHITE);
//        tvInbox.setTextColor(Color.WHITE);
//        tvHelp.setTextColor(Color.WHITE);
//        tvSearch.setTextColor(Color.WHITE);
//        tvnoti_count.setTextColor(Color.WHITE);
//        tv_credit_count.setTextColor(Color.WHITE);
//
//        tvPostNewJob.setTextColor(Color.WHITE);
//        tvCredits.setTextColor(Color.WHITE);
//        tvPostedJobs.setTextColor(Color.WHITE);
//
//
//        llSearch.setBackgroundColor(Color.TRANSPARENT);
//
//        llSettings.setBackgroundColor(Color.TRANSPARENT);
//        llFavourites.setBackgroundColor(Color.TRANSPARENT);
//        llMatches.setBackgroundColor(Color.TRANSPARENT);
//        llNotifications.setBackgroundColor(Color.TRANSPARENT);
//        llInbox.setBackgroundColor(Color.TRANSPARENT);
//        llHelp.setBackgroundColor(Color.TRANSPARENT);
//        tv_credit_count.setBackgroundResource(R.drawable.whitering);
//        tvnoti_count.setBackgroundResource(R.drawable.whitering);
//
//        llPostNewJob.setBackgroundColor(Color.TRANSPARENT);
//        llCredits.setBackgroundColor(Color.TRANSPARENT);
//        llPostedJobs.setBackgroundColor(Color.TRANSPARENT);
//
//        ivSearch.setImageResource(R.drawable.search_menu);
//
//        ivSettings.setImageResource(R.drawable.settings_menu);
//        ivFavourites.setImageResource(R.drawable.favourite_menu);
//        ivMatches.setImageResource(R.drawable.match_menu);
//        ivNotifications.setImageResource(R.drawable.notification_menu_black);
//        ivInbox.setImageResource(R.drawable.inbox_menu);
//        ivHelp.setImageResource(R.drawable.help_menu);
//        ivSearch.setImageResource(R.drawable.search_menu);
//
//        ivPostNewJob.setImageResource
//                (R.drawable.postjob_menu);
//        ivCredits.setImageResource
//                (R.drawable.credit_menu);
//        ivPostedJobs.setImageResource
//                (R.drawable.posted_job_menu);
//
//    }

    ArrayList<JobObject> dataList = null;
    ListView posted_jobs_list;
    GetPostedJobs getPostedJobs = null;

    CountryList countrylist = null;
    ListView country_list;
    EditText edt_country;
    MyAdap countryadapter;
    ArrayList<CountryData> array_country_list;

    class GetPostedJobs extends AsyncTask<String, String, String> {

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
                nameValuePairs.add(new BasicNameValuePair("action",
                        "postedJobs"));
                nameValuePairs.add(new BasicNameValuePair("user_id",
                        GlobalClaass.getUserId(context)));

                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(request);

                HttpEntity entity = response.getEntity();

                resultStr = EntityUtils.toString(entity);

                Log.e("Posted Job", resultStr);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultStr;

        }

        @Override
        protected void onPostExecute(String result) {

            hideProgressBar(context);

            setUpUi(result);
        }

    }

    public void setUpUi(String result) {

        try {
            JSONObject jObject = new JSONObject(result);

            String get_message = jObject.getString("message").trim();
            String get_replycode = jObject.getString("status").trim();

            JSONArray jrr = jObject.getJSONArray("jobs");

            dataList = new ArrayList<JobObject>();

            for (int i = 0; i < jrr.length(); i++) {

                JSONObject jobj = jrr.getJSONObject(i);

                JobObject itmObj = new JobObject();

                itmObj.setId(jobj.getString("job_type_id"));
                itmObj.setJobName(jobj.getString("title"));
                itmObj.setJobLocation(jobj.getString("city") + ", "
                        + jobj.getString("country"));
                // itmObj.setJobDate(jobj.getString("start_date"));

                itmObj.setJobImage(jobj.getString("image"));
                // itmObj.setJobFavorite(jobj.getBoolean("status"));
                itmObj.setSelected(false);

                dataList.add(itmObj);

            }
        } catch (Exception e) {

            Log.d("excccccccccccc1111", e.getMessage());
            e.printStackTrace();
        }

        if (dataList != null) {

            if (dataList.size() > 0) {

                final JobListAdapter itmAdap = new JobListAdapter(context,
                        dataList);

                posted_jobs_list.setAdapter(itmAdap);

                /**
                 * posted_jobs_list.setOnItemClickListener(new
                 * OnItemClickListener() {
                 *
                 * @Override public void onItemClick(AdapterView<?> arg0, View
                 *           arg1, int position, long arg3) {
                 *
                 *           startActivity(new Intent(context,
                 *           MyJobsPagerActivity.class).putExtra("dataList",
                 *           dataList).putExtra("position", position));
                 *
                 *           } });
                 */

            }
        }

    }

    class JobListAdapter extends BaseAdapter {

        Activity mContext;
        LayoutInflater inflater;
        private List<JobObject> mainDataList = null;
        private List<JobObject> arrList = null;

        public JobListAdapter(Activity context, List<JobObject> mainDataList) {

            mContext = context;
            this.mainDataList = mainDataList;

            arrList = new ArrayList<JobObject>();

            arrList.addAll(this.mainDataList);
            inflater = LayoutInflater.from(mContext);
        }

        class ViewHolder {
            protected TextView name;
            protected CheckBox check;

            protected TextView location;

        }

        @Override
        public int getCount() {
            return mainDataList.size();
        }

        @Override
        public JobObject getItem(int position) {
            return mainDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.item_job_select_list, null);

                holder.name = (TextView) view.findViewById(R.id.job_name);
                holder.location = (TextView) view
                        .findViewById(R.id.job_location);


                holder.check = (CheckBox) view.findViewById(R.id.check);


                view.setTag(holder);
                holder.check
                        .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(CompoundButton vw,
                                                         boolean isChecked) {
                                int getPosition = (Integer) vw.getTag();
                                mainDataList.get(getPosition).setSelected(
                                        vw.isChecked());
                                getSelectedJobs();

                            }
                        });

            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.check.setTag(position);

            holder.name.setText(mainDataList.get(position).getJobName());

            holder.check.setChecked(mainDataList.get(position).isSelected());

			/*
             * holder.date.setText("Start Date: " +
			 * mainDataList.get(position).getJobDate());
			 */
            holder.location
                    .setText(mainDataList.get(position).getJobLocation());

            return view;
        }

    }

    public class CountryList extends AsyncTask<String, String, String> {

        String responseString;

        Activity context;

        JSONObject jObject1;

        boolean remember;

        public CountryList(Activity ctx) {
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

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        2);

                nameValuePairs
                        .add(new BasicNameValuePair("action", "countries"));

                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(request);

                HttpEntity entity = response.getEntity();

                responseString = EntityUtils.toString(entity);
                Log.e("Responce Country", responseString);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseString;

        }

        protected void onPostExecute(String responseString) {
            JSONObject jObject, Jobj;
            JSONArray jarray;

            String get_replycode = "", get_message = "";

            array_country_list = new ArrayList<CountryData>();

            try {

                jObject = new JSONObject(responseString);
                get_replycode = jObject.getString("status").trim();
                get_message = jObject.getString("message").trim();

                if (get_replycode.equalsIgnoreCase("true")) {

                    jarray = jObject.getJSONArray("countries");
                    for (int i = 0; i < jarray.length(); i++) {

                        Jobj = jarray.getJSONObject(i);
                        Log.e("PrintCountry", Jobj.toString());

                        CountryData country = new CountryData();

                        country.setCountry_Id(Jobj.getString("id"));
                        country.setCountry_Name(Jobj.getString("name"));

                        country.setChecked(false);

                        array_country_list.add(country);

                    }
                }
            } catch (Exception e) {

                Log.d("excccccccccccc", e.getMessage());
                e.printStackTrace();
            }

            if (array_country_list.size() > 0) {

                countryadapter = new MyAdap(context, array_country_list);

                country_list.setAdapter(countryadapter);

                //				country_list.setOnItemClickListener(new OnItemClickListener() {
                //
                //					@Override
                //					public void onItemClick(AdapterView<?> arg0, View view,
                //							int position, long arg3) {
                //						CheckBox chk = (CheckBox) view.findViewById(R.id.check);
                //						CountryData bean = array_country_list.get(position);
                //
                //						if (bean.isChecked()) {
                //							bean.setChecked(false);
                //							chk.setChecked(false);
                //							Toast.makeText(context, "nchecked"+position, 3000).show();
                //						} else {
                //							bean.setChecked(true);
                //							chk.setChecked(true);
                //							Toast.makeText(context, "checked"+position, 3000).show();
                //
                //						}
                //					}
                //				});

                edt_country.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence arg0, int arg1,
                                              int arg2, int arg3) {
                        // TODO Auto-generated method stub
                        countryadapter.filter(edt_country.getText().toString()
                                .trim());
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1,
                                                  int arg2, int arg3) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub

                    }
                });

            }

            GlobalClaass.hideProgressBar(context);

        }
    }

    class MyAdap extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

        ArrayList<CountryData> event_note_data;

        ArrayList<CountryData> arr_List;

        LayoutInflater inflater;

        Context mContext = null;

        public MyAdap(Context context, ArrayList<CountryData> note_data_list) {
            // TODO Auto-generated constructor stub

            mContext = context;

            inflater = LayoutInflater.from(mContext);
            event_note_data = note_data_list;
            arr_List = new ArrayList<CountryData>();
            arr_List.addAll(event_note_data);
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return event_note_data.size();
        }

        public CountryData getItem(int position) {
            // TODO Auto-generated method stub
            return event_note_data.get(position);
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        class ViewHolder {
            TextView country_name;
            CheckBox mcheck;

        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub

            ViewHolder holder = null;
            if (convertView == null) {

                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.row_country, null);

                holder.country_name = (TextView) convertView
                        .findViewById(R.id.name);
                holder.mcheck = (CheckBox) convertView
                        .findViewById(R.id.mcheck);

                //holder.country_name.setTypeface(font);

                convertView.setTag(holder);
                convertView.setTag(R.id.name, holder.country_name);
                convertView.setTag(R.id.mcheck, holder.mcheck);


            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mcheck.setTag(position);
            /*
             * if(get_Ename != null){
			 * 
			 * if(get_Ename.equalsIgnoreCase("Country")){
			 * 
			 * holder.mcheck.setVisibility(View.GONE); } else {
			 * holder.mcheck.setVisibility(View.VISIBLE); }
			 * 
			 * }
			 */

            holder.country_name.setText(event_note_data.get(position)
                    .getCountry_Name());

            holder.mcheck.setChecked(event_note_data.get(position).isChecked());
            holder.mcheck.setOnCheckedChangeListener(this);
            /*
             * convertView.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { // TODO Auto-generated
			 * method stub String country_name =
			 * event_note_data.get(position).getCountry_Name(); String
			 * country_id = event_note_data.get(position).getCountry_Id();
			 * Intent returnIntent = new Intent();
			 * returnIntent.putExtra("name",country_name);
			 * returnIntent.putExtra("id",country_id);
			 * setResult(RESULT_OK,returnIntent); finish(); } });
			 */

            return convertView;

        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = Integer.parseInt(buttonView.getTag().toString());

            CountryData bean = array_country_list.get(position);

            if (isChecked) {
                bean.setChecked(false);
//							Toast.makeText(context, "nchecked"+position, 3000).show();
            } else {
                bean.setChecked(true);
//							Toast.makeText(context, "checked"+position, 3000).show();

            }


            //int getPosition = (Integer) vw.getTag();
            event_note_data.get(position).setChecked(
                    isChecked);
            countSelectedMethod();

//						Toast.makeText(context, "checked"+position, 3000).show();


        }


        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            event_note_data.clear();
            if (charText.length() == 0) {
                event_note_data.addAll(arr_List);
            } else {
                for (CountryData wp : arr_List) {
                    if (wp.getCountry_Name().toLowerCase(Locale.getDefault())
                            .contains(charText)) {
                        event_note_data.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }

    public static StringBuffer sb = new StringBuffer();

    void getSelectedJobs() {

        sb = new StringBuffer();

        String seperator = "";

        for (JobObject bean : dataList) {

            if (bean.isSelected()) {
                sb.append(seperator);
                seperator = ",";
                sb.append(bean.getId());

            }
        }

        String s = sb.toString().trim();

        if (TextUtils.isEmpty(s)) {
            GlobalClaass.savePrefrencesfor(context,
                    PreferenceConnector.TYPEARRAY, "");
        } else {

            GlobalClaass.savePrefrencesfor(context,
                    PreferenceConnector.TYPEARRAY, s);

        }

    }

    public static String countryids = "";

    void countSelectedMethod() {

        int count = 0;

        StringBuffer sb = new StringBuffer();

        String seperator = "";

        for (CountryData bean : array_country_list) {

            if (bean.isChecked()) {
                sb.append(seperator);
                seperator = ",";
                sb.append(bean.getCountry_Id());
                count++;

            }
        }

        countryids = sb.toString().trim();

        if (TextUtils.isEmpty(countryids)) {
            GlobalClaass.savePrefrencesfor(context,
                    PreferenceConnector.COUNTRIESARRAY, "");
        } else {

            GlobalClaass.savePrefrencesfor(context,
                    PreferenceConnector.COUNTRIESARRAY, countryids);

        }

        if (count > 0) {
            ((TextView) findViewById(R.id.selectedno)).setText(count + "");
        } else {
            ((TextView) findViewById(R.id.selectedno)).setText("");
        }

    }

    public void setDrawerState(boolean isEnabled) {
        if (isEnabled) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                    mFilterList);
            (findViewById(R.id.header_right_menu)).setVisibility(View.VISIBLE);

        } else {
            mDrawerLayout.setDrawerLockMode(
                    DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mFilterList);
            (findViewById(R.id.header_right_menu)).setVisibility(View.GONE);
        }
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
        if (GlobalClaass.isInternetPresent(context)) {

            googlePlusLogin();
        } else {
            GlobalClaass.showToastMessage(context, "Please check internet connection");
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

        GetCount getcount = new GetCount(context);
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
                Log.e("Responce count", responseString);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseString;

        }


        protected void onPostExecute(String responseString) {
//            JSONObject jObject, Jobj;
//            JSONArray jarray;
//
//            String get_replycode = "", get_message = "", noticount = "", messagecount = "", creditcount = "";

            try {

                JSONObject jObject = new JSONObject(responseString);
                MenuCountDTO menuCountDTO = new Gson().fromJson(jObject.toString(),
                        MenuCountDTO.class);
                adapter.setMenuCountDTO(menuCountDTO);
                adapter.notifyDataSetChanged();
//                get_replycode = jObject.getString("status").trim();
//                get_message = jObject.getString("message").trim();
//                noticount = jObject.getString("notification").trim();
//                messagecount = jObject.getString("msgcount").trim();
//                creditcount = jObject.getString("credits").trim();
//                Log.e("Count show", messagecount + "  " + noticount);


//                if (noticount != null && !noticount.equalsIgnoreCase("") && !noticount.equalsIgnoreCase("0")) {
//                    tvnoti_count.setText(noticount);
//                    tvnoti_count.setVisibility(View.VISIBLE);
//
//                } else {
//                    tvnoti_count.setVisibility(View.GONE);
//                }
//
//                if (creditcount != null && !creditcount.equalsIgnoreCase("") && !creditcount.equalsIgnoreCase("0")) {
//                    tv_credit_count.setText(creditcount);
//                    tv_credit_count.setVisibility(View.VISIBLE);
//
//                } else {
//                    tv_credit_count.setVisibility(View.GONE);
//                }
//
//                Log.e("GetCount", messagecount + "");
//                if (messagecount != null && !messagecount.equalsIgnoreCase("")) {
//
//                    GlobalClaass.savePrefrencesfor(context, PreferenceConnector.Header_Count, messagecount);
//
//                    if (messagecount.length() == 1) {
//                        top_header_count.setText("  " + messagecount);
//                    }
//                    if (messagecount.length() == 2) {
//                        top_header_count.setText(" " + messagecount);
//                    } else {
//                        top_header_count.setText(messagecount);
//                    }
//
//
//                } else {
//                    top_header_count.setVisibility(View.GONE);
//                }


            } catch (Exception e) {

            }


            GlobalClaass.hideProgressBar(context);

        }
    }
}
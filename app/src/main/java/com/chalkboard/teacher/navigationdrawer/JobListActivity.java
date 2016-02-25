package com.chalkboard.teacher.navigationdrawer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
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
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chalkboard.Feedback_Activity;
import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.ImageLoader11;
import com.chalkboard.Login_Activity;
import com.chalkboard.PreferenceConnector;
import com.chalkboard.R;
import com.chalkboard.Setting_Activity;
import com.chalkboard.teacher.JobFavoriteFragment;
import com.chalkboard.teacher.JobInboxFragment;
import com.chalkboard.teacher.JobListFragment;
import com.chalkboard.teacher.JobMatchesFragment;
import com.chalkboard.teacher.JobNotificationFragment;
import com.chalkboard.teacher.TeacherProfileViewActivity;
import com.chalkboard.teacher.matchrequest.MatchRequestFragment;
import com.chalkboard.teacher.navigationdrawer.adapter.NavDrawerListAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class JobListActivity extends FragmentActivity implements ConnectionCallbacks,
        OnConnectionFailedListener {

    private ActionBarDrawerToggle mDrawerToggle;
    private Activity mActivity = null;
    private DrawerLayout mDrawerLayout;
    private ListView sideMenuList;
    private RelativeLayout relative_layout;
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

    String Gplus_id = "", Gplus_name = "", Gplus_email = "", Gplus_image = "";

    //G+ end
    Typeface font, font2;
    GetCount getcount = null;
    //private TextView top_header_count;
    private NavDrawerListAdapter adapter;


//    TextView tvSettings, tvFavourites, tvMatches, tvNotifications, tvInbox,
//            tvHelp, tvSearch, tvLogout, tvnoti_count, top_header_count;
//    ImageView ivSettings, ivFavourites, ivMatches, ivNotifications, ivInbox,
//            ivHelp, ivSearch, ivLogout;
//    LinearLayout llSettings, llFavourites, llMatches, llNotifications, llInbox,
//            llHelp, llSearch, llLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_job_list);
        mActivity = this;

        ImageView header_right_menu = (ImageView) findViewById(R.id.header_right_menu);
        header_right_menu.setImageResource(R.drawable.notification_menu);

        login_type = getIntent().getStringExtra("LoginType");
        font = Typeface.createFromAsset(getAssets(), "fonts/mark.ttf");
        font2 = Typeface.createFromAsset(getAssets(), "fonts/marlbold.ttf");
        mAsyncRunner = new AsyncFacebookRunner(facebook);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();

        sessionStatusCallback = new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state,
                             Exception exception) {
                onSessionStateChange(session, state, exception);

            }
        };

        header_right_menu.setOnClickListener(notificationClick);

        if (login_type != null) {

            if (login_type.equalsIgnoreCase("facebook")) {
                if (GlobalClaass.isInternetPresent(mActivity)) {

                    connectToFB();
                } else {
                    GlobalClaass.showToastMessage(mActivity, "Please check internet connection");
                }

            }
        }

        //tvSettings = (TextView) findViewById(R.id.tvsettings);
        // tvFavourites = (TextView) findViewById(R.id.tvfavorites);
        //tvMatches = (TextView) findViewById(R.id.tvmatches);
        //tvNotifications = (TextView) findViewById(R.id.tvmy_notifications);
        //tvInbox = (TextView) findViewById(R.id.tvinbox);
        //tvHelp = (TextView) findViewById(R.id.tvhelp);
        //tvSearch = (TextView) findViewById(R.id.tvsearch);
        //tvLogout = (TextView) findViewById(R.id.tvlogout);
        //tvnoti_count = (TextView) findViewById(R.id.noti_count);
        //top_header_count = (TextView) findViewById(R.id.top_header_count);
        //ivSettings = (ImageView) findViewById(R.id.ivsettings);
        //ivFavourites = (ImageView) findViewById(R.id.ivfavorites);
        //ivMatches = (ImageView) findViewById(R.id.ivmatches);
        //ivNotifications = (ImageView) findViewById(R.id.ivmy_notifications);
        //ivInbox = (ImageView) findViewById(R.id.ivinbox);
        //ivHelp = (ImageView) findViewById(R.id.ivhelp);
        //ivSearch = (ImageView) findViewById(R.id.ivsearch);
        //ivLogout = (ImageView) findViewById(R.id.ivlogout);

//        llSettings = (LinearLayout) findViewById(R.id.llsettings);
//        llFavourites = (LinearLayout) findViewById(R.id.llfavorites);
//        llMatches = (LinearLayout) findViewById(R.id.llmatches);
//        llNotifications = (LinearLayout) findViewById(R.id.llmy_notifications);
//        llInbox = (LinearLayout) findViewById(R.id.llinbox);
//        llHelp = (LinearLayout) findViewById(R.id.llhelp);
//        llSearch = (LinearLayout) findViewById(R.id.llsearch);
//        llLogout = (LinearLayout) findViewById(R.id.lllogout);

        rlSlideContent = (RelativeLayout) findViewById(R.id.slide_content);

        findViewById(R.id.header_right_menu).setVisibility(View.VISIBLE);


        String[] navMenuTitles = getResources().getStringArray(
                R.array.teacher_drawer_items);

        // nav drawer icons from resources
        TypedArray navMenuUnSelectIcons = getResources().obtainTypedArray(
                R.array.teacher_unselected_drawer_icons);

        TypedArray navMenuSelectIcons = getResources().obtainTypedArray(
                R.array.teacher_selected_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        sideMenuList = (ListView) findViewById(R.id.list_slidermenu);
        sideMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        relative_layout = (RelativeLayout) findViewById(R.id.relative_layout);
        int width = 3 * (getResources().getDisplayMetrics().widthPixels / 4);
        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) relative_layout
                .getLayoutParams();
        params.width = width;
        relative_layout.setLayoutParams(params);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);

        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Search
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuUnSelectIcons
                .getResourceId(0, -1), navMenuSelectIcons
                .getResourceId(0, -1)));
        // Fav
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuUnSelectIcons
                .getResourceId(1, -1), navMenuSelectIcons
                .getResourceId(1, -1)));
        // Match Request
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuUnSelectIcons
                .getResourceId(2, -1), navMenuSelectIcons
                .getResourceId(2, -1)));
        // Match
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuUnSelectIcons
                .getResourceId(3, -1), navMenuSelectIcons
                .getResourceId(3, -1)));
        // Inbox
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuUnSelectIcons
                .getResourceId(4, -1), navMenuSelectIcons
                .getResourceId(4, -1)));
        // Help
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuUnSelectIcons
                .getResourceId(5, -1), navMenuSelectIcons
                .getResourceId(5, -1)));
        // Settings
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuUnSelectIcons
                .getResourceId(6, -1), navMenuSelectIcons
                .getResourceId(6, -1)));
        // Logout
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuUnSelectIcons
                .getResourceId(7, -1), navMenuSelectIcons
                .getResourceId(7, -1)));

        // Recycle the typed array
        navMenuUnSelectIcons.recycle();
        navMenuSelectIcons.recycle();

        //mDrawerList = (RelativeLayout) findViewById(R.id.drawer_list);

        try {
//            tvSettings.setTypeface(font2);
//            tvFavourites.setTypeface(font2);
//            tvMatches.setTypeface(font2);
//            tvNotifications.setTypeface(font2);
//            tvInbox.setTypeface(font2);
//            tvHelp.setTypeface(font2);
//            tvSearch.setTypeface(font2);
//            tvLogout.setTypeface(font2);
//            top_header_count.setTypeface(font);
            ((TextView) (findViewById(R.id.header_text))).setTypeface(font2);

        } catch (Exception e) {

        }
        sideMenuList.setOnItemClickListener(drawerListItemClickListener);
        adapter = new NavDrawerListAdapter(
                getApplicationContext(), navDrawerItems);
        sideMenuList.setAdapter(adapter);
        sideMenuList.setItemChecked(0, true);

        (findViewById(R.id.header_left_menu))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        mDrawerLayout.openDrawer(relative_layout);
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
                float moveFactor = (sideMenuList.getWidth() * slideOffset);

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
                        startActivity(new Intent(mActivity,
                                TeacherProfileViewActivity.class));
                        GlobalClaass.activitySlideForwardAnimation(mActivity);
                    }
                });

        Fragment fragment = new JobListFragment();
        /*
         * Bundle args = new Bundle();
		 * args.putString(CategoryListFragment.ARG_CATEGORY_NUMBER,
		 * dataList.get(position).getId());
		 * args.putString(CategoryListFragment.ARG_CATEGORY_NAME,
		 * dataList.get(position).getName()); fragment.setArguments(args);
		 */

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.page_container, fragment).commit();

//        tvSearch.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                //nutralAll();
//
//                //tvSearch.setTextColor(Color.BLACK);
//
//                //llSearch.setBackgroundResource(R.drawable.white_gradient);
//
//                //ivSearch.setImageResource(R.drawable.search_menublackk);
//
//                findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
//
//                ((ImageView) (findViewById(R.id.header_logo)))
//                        .setVisibility(View.VISIBLE);
//
//                ((TextView) (findViewById(R.id.header_text))).setText("");
//
//                mDrawerLayout.closeDrawer(mDrawerList);
//                Fragment fragment = new JobListFragment();
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.page_container, fragment).commit();
//
//            }
//        });
//
//        tvFavourites.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                nutralAll();
//
//                tvFavourites.setTextColor(Color.BLACK);
//
//                llFavourites.setBackgroundResource(R.drawable.white_gradient);
//
//                ivFavourites.setImageResource(R.drawable.favourite_menu_black);
//
//                (findViewById(R.id.header_logo)).setVisibility(View.GONE);
//
//                ((TextView) (findViewById(R.id.header_text)))
//                        .setText("Favorite Jobs");
//
//                findViewById(R.id.bottom_bar).setVisibility(View.GONE);
//                mDrawerLayout.closeDrawer(mDrawerList);
//                Fragment fragment = new JobFavoriteFragment();
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.page_container, fragment).commit();
//
//            }
//        });
//
//        tvMatches.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                nutralAll();
//
//                tvMatches.setTextColor(Color.BLACK);
//
//                llMatches.setBackgroundResource(R.drawable.white_gradient);
//
//                ivMatches.setImageResource(R.drawable.match_menu_black);
//
//                (findViewById(R.id.header_logo)).setVisibility(View.GONE);
//
//                ((TextView) (findViewById(R.id.header_text)))
//                        .setText("Matches");
//
//                findViewById(R.id.bottom_bar).setVisibility(View.GONE);
//                mDrawerLayout.closeDrawer(mDrawerList);
//                Fragment fragment = new JobMatchesFragment();
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.page_container, fragment).commit();
//
//            }
//        });
//        tvNotifications.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                nutralAll();
//
//                tvNotifications.setTextColor(Color.BLACK);
//                tvnoti_count.setTextColor(Color.BLACK);
//                tvnoti_count.setBackgroundResource(R.drawable.blackring);
//
//                llNotifications.setBackgroundResource(R.drawable.white_gradient);
//
//                ivNotifications
//                        .setImageResource(R.drawable.notification_menu_black);
//
//                (findViewById(R.id.header_logo)).setVisibility(View.GONE);
//
//                ((TextView) (findViewById(R.id.header_text)))
//                        .setText("My Notifications");
//                findViewById(R.id.bottom_bar).setVisibility(View.GONE);
//                mDrawerLayout.closeDrawer(mDrawerList);
//                Fragment fragment = new JobNotificationFragment();
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.page_container, fragment).commit();
//
//            }
//        });
//        tvInbox.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                nutralAll();
//
//                tvInbox.setTextColor(Color.BLACK);
//
//                llInbox.setBackgroundResource(R.drawable.white_gradient);
//
//                ivInbox.setImageResource(R.drawable.inbox_menu_black);
//
//                (findViewById(R.id.header_logo)).setVisibility(View.GONE);
//
//                ((TextView) (findViewById(R.id.header_text))).setText("Inbox");
//                findViewById(R.id.bottom_bar).setVisibility(View.GONE);
//                mDrawerLayout.closeDrawer(mDrawerList);
//                Fragment fragment = new JobInboxFragment();
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.page_container, fragment).commit();
//
//            }
//        });
//
//        tvHelp.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                nutralAll();
//
//                tvHelp.setTextColor(Color.BLACK);
//
//                llHelp.setBackgroundResource(R.drawable.white_gradient);
//
//                ivHelp.setImageResource(R.drawable.help_menu_black);
//
//                (findViewById(R.id.header_logo)).setVisibility(View.GONE);
//
//                ((TextView) (findViewById(R.id.header_text))).setText("Help");
//
//                findViewById(R.id.bottom_bar).setVisibility(View.GONE);
//                mDrawerLayout.closeDrawer(mDrawerList);
//
//                startActivity(new Intent(context, Feedback_Activity.class)
//                        .putExtra("Activity", "support")
//                        .putExtra("ActivityName", "Setting_Activity"));
//                GlobalClaass.activitySlideForwardAnimation(context);
//
//
//            }
//        });
//
//        tvSettings.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//
//                findViewById(R.id.bottom_bar).setVisibility(View.GONE);
//
//                mDrawerLayout.closeDrawer(mDrawerList);
//
//                startActivity(new Intent(context, Setting_Activity.class));
//
///*
//                tvSettings.setTextColor(Color.BLACK);
//
//				llSettings.setBackgroundColor(Color.WHITE);
//
//				ivSettings.setImageResource(R.drawable.settings_menu_black);
//
//				findViewById(R.id.bottom_bar).setVisibility(View.GONE);
//
//				mDrawerLayout.closeDrawer(mDrawerList);
//
//				Fragment fragment = new FragmentSettings();
//				FragmentManager fragmentManager = getSupportFragmentManager();
//				fragmentManager.beginTransaction()
//						.replace(R.id.page_container, fragment).commit();*/
//
//            }
//        });
//
//        tvLogout.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                findViewById(R.id.bottom_bar).setVisibility(View.GONE);
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

        //(findViewById(R.id.tvsearch)).performClick();

    }


    OnClickListener notificationClick = new OnClickListener() {
        @Override
        public void onClick(View v) {

            (findViewById(R.id.header_logo)).setVisibility(View.GONE);

            ((TextView) (findViewById(R.id.header_text)))
                    .setText("My Notifications");
            findViewById(R.id.bottom_bar).setVisibility(View.GONE);
            mDrawerLayout.closeDrawer(relative_layout);
            Fragment fragment = new JobNotificationFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.page_container, fragment).commit();
        }
    };

    private AdapterView.OnItemClickListener drawerListItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent intent = null;
            Fragment fragment = null;
            FragmentManager fragmentManager = null;

            switch (position) {
                case 0:
                    findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);

                    ((ImageView) (findViewById(R.id.header_logo)))
                            .setVisibility(View.VISIBLE);

                    ((TextView) (findViewById(R.id.header_text))).setText("");

                    mDrawerLayout.closeDrawer(relative_layout);
                    fragment = new JobListFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, fragment).commit();

                    break;
                case 1:
                    (findViewById(R.id.header_logo)).setVisibility(View.GONE);

                    ((TextView) (findViewById(R.id.header_text))).setText("Inbox");
                    findViewById(R.id.bottom_bar).setVisibility(View.GONE);
                    mDrawerLayout.closeDrawer(relative_layout);
                    fragment = new JobInboxFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, fragment).commit();
                    break;
                case 2:
                    (findViewById(R.id.header_logo)).setVisibility(View.GONE);

                    ((TextView) (findViewById(R.id.header_text)))
                            .setText("Favorite Jobs");

                    findViewById(R.id.bottom_bar).setVisibility(View.GONE);
                    mDrawerLayout.closeDrawer(relative_layout);
                    fragment = new JobFavoriteFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, fragment).commit();


                    break;
                case 3:
                    (findViewById(R.id.header_logo)).setVisibility(View.GONE);

                    ((TextView) (findViewById(R.id.header_text)))
                            .setText("Match Request");

                    findViewById(R.id.bottom_bar).setVisibility(View.GONE);
                    mDrawerLayout.closeDrawer(relative_layout);
                    fragment = new MatchRequestFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, fragment).commit();

                    break;
                case 4:
                    (findViewById(R.id.header_logo)).setVisibility(View.GONE);

                    ((TextView) (findViewById(R.id.header_text)))
                            .setText("Matches");

                    findViewById(R.id.bottom_bar).setVisibility(View.GONE);
                    mDrawerLayout.closeDrawer(relative_layout);
                    fragment = new JobMatchesFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, fragment).commit();

                    break;
//                case 4:
//                    (findViewById(R.id.header_logo)).setVisibility(View.GONE);
//
//                    ((TextView) (findViewById(R.id.header_text)))
//                            .setText("My Notifications");
//                    findViewById(R.id.bottom_bar).setVisibility(View.GONE);
//                    mDrawerLayout.closeDrawer(relative_layout);
//                    fragment = new JobNotificationFragment();
//                    fragmentManager = getSupportFragmentManager();
//                    fragmentManager.beginTransaction()
//                            .replace(R.id.page_container, fragment).commit();
//                    break;


                case 5:
                    (findViewById(R.id.header_logo)).setVisibility(View.GONE);

                    ((TextView) (findViewById(R.id.header_text))).setText("Help");

                    findViewById(R.id.bottom_bar).setVisibility(View.GONE);
                    mDrawerLayout.closeDrawer(relative_layout);

                    startActivity(new Intent(mActivity, Feedback_Activity.class)
                            .putExtra("Activity", "support")
                            .putExtra("ActivityName", "Setting_Activity"));
                    GlobalClaass.activitySlideForwardAnimation(mActivity);
                    break;
                case 6:
                    findViewById(R.id.bottom_bar).setVisibility(View.GONE);

                    mDrawerLayout.closeDrawer(relative_layout);

                    startActivity(new Intent(mActivity, Setting_Activity.class));

                    break;
                case 7:
                    findViewById(R.id.bottom_bar).setVisibility(View.GONE);
                    mDrawerLayout.closeDrawer(relative_layout);

                    final AlertDialog.Builder b = new AlertDialog.Builder(mActivity);
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

                                    GlobalClaass.removePrefrences(mActivity);
                                    startActivity(new Intent(mActivity,
                                            Login_Activity.class));
                                    GlobalClaass
                                            .activitySlideBackAnimation(mActivity);
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
//
//        llSettings.setBackgroundColor(Color.TRANSPARENT);
//        llFavourites.setBackgroundColor(Color.TRANSPARENT);
//        llMatches.setBackgroundColor(Color.TRANSPARENT);
//        llNotifications.setBackgroundColor(Color.TRANSPARENT);
//        llInbox.setBackgroundColor(Color.TRANSPARENT);
//        llHelp.setBackgroundColor(Color.TRANSPARENT);
//        llSearch.setBackgroundColor(Color.TRANSPARENT);
//        tvnoti_count.setBackgroundResource(R.drawable.whitering);
//
//        ivSettings.setImageResource(R.drawable.settings_menu);
//        ivFavourites.setImageResource(R.drawable.favourite_menu);
//        ivMatches.setImageResource(R.drawable.match_menu);
//        ivNotifications.setImageResource(R.drawable.notification_menu);
//        ivInbox.setImageResource(R.drawable.inbox_menu);
//        ivHelp.setImageResource(R.drawable.help_menu);
//        ivSearch.setImageResource(R.drawable.search_menu);
//
//    }

    void setProfile() {

        if (GlobalClaass.getImage(mActivity).equalsIgnoreCase("") && GlobalClaass.getImage(mActivity) != null) {

            ((ImageView) findViewById(R.id.profile_image))
                    .setImageResource(R.drawable.ic_launcher);

        } else {

            ImageLoader imageloader = new ImageLoader(mActivity);

            imageloader.DisplayImage(GlobalClaass.getImage(mActivity),
                    ((ImageView) findViewById(R.id.profile_image)));
            ImageLoader11 imageloader11 = new ImageLoader11(mActivity);
            imageloader11.DisplayImage(GlobalClaass.getImage(mActivity),
                    ((ImageView) findViewById(R.id.slider_image)));
        }

        if (GlobalClaass.getName(mActivity) != null) {

            if (!GlobalClaass.getName(mActivity).equalsIgnoreCase("")) {

                ((TextView) findViewById(R.id.profile_name)).setTypeface(font);
                ((TextView) findViewById(R.id.profile_name)).setText(GlobalClaass
                        .getName(mActivity));

            }
        } else {
            if (!GlobalClaass.getEMAIL(mActivity).equalsIgnoreCase("")) {

                ((TextView) findViewById(R.id.profile_name)).setTypeface(font);
                ((TextView) findViewById(R.id.profile_name)).setText(GlobalClaass
                        .getEMAIL(mActivity));

            }
        }

        String a = "", b = "", c = "", d = "";
        String cityCountry = null;
        if (GlobalClaass.getCity(mActivity) != null) {
            a = GlobalClaass.getCity(mActivity);
        }
        if (GlobalClaass.getCountry(mActivity) != null) {
            b = GlobalClaass.getCountry(mActivity);
        }

        if (!a.equalsIgnoreCase("")) {
            cityCountry = a;
            if (!b.equalsIgnoreCase("")) {
                cityCountry = a + ", " + b;
            }
        } else {
            cityCountry = b;
        }

        if (GlobalClaass.getAge(mActivity) != null) {
            c = GlobalClaass.getAge(mActivity);
        }
        if (GlobalClaass.getGender(mActivity) != null) {
            d = GlobalClaass.getGender(mActivity);
        }
        ((TextView) findViewById(R.id.profile_location_age)).setTypeface(font);
        ((TextView) findViewById(R.id.profile_location_age))
                .setText(cityCountry + " | " + c + ((d
                        .equalsIgnoreCase("male")) ? " M" : " F"));


    }

    public void connectToFB() {

        List<String> permissions = new ArrayList<String>();
        permissions.add("publish_actions");
        permissions.add("email");

        currentSession = new Session.Builder(mActivity).build();
        currentSession.addCallback(sessionStatusCallback);

        Session.OpenRequest openRequest = new Session.OpenRequest(mActivity);
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
        if (GlobalClaass.isInternetPresent(mActivity)) {

            googlePlusLogin();
        } else {
            GlobalClaass.showToastMessage(mActivity, "Please check internet connection");
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
            currentSession.onActivityResult(mActivity, requestCode, resultCode,
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

        final AlertDialog.Builder b = new AlertDialog.Builder(mActivity);
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

        getcount = new GetCount(mActivity);
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
            JSONObject jObject, Jobj;
            JSONArray jarray;

            String get_replycode = "", get_message = "", noticount = "", messagecount = "", creditcount = "";

            try {

                jObject = new JSONObject(responseString);
                get_replycode = jObject.getString("status").trim();
                get_message = jObject.getString("message").trim();
                noticount = jObject.getString("notification").trim();
                messagecount = jObject.getString("msgcount").trim();
                creditcount = jObject.getString("credits").trim();
                Log.e("Count show", messagecount + "  " + noticount);


                if (noticount != null && !noticount.equalsIgnoreCase("") && !noticount.equalsIgnoreCase("0")) {
                    adapter.setNotificationCount(Integer.parseInt(noticount));
                    adapter.notifyDataSetChanged();
                }


                if (messagecount != null && !messagecount.equalsIgnoreCase("")) {

                    GlobalClaass.savePrefrencesfor(context, PreferenceConnector.Header_Count, messagecount);

                    if (messagecount.length() == 1) {
                        //top_header_count.setText("  " + messagecount);
                    }
                    if (messagecount.length() == 2) {
                        //top_header_count.setText(" " + messagecount);
                    } else {
                        //top_header_count.setText(messagecount);
                    }


                } else {
                    //top_header_count.setVisibility(View.GONE);
                }


            } catch (Exception e) {

            }


            GlobalClaass.hideProgressBar(context);

        }
    }
}
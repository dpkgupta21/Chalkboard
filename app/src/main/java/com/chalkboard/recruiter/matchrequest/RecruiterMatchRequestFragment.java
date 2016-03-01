package com.chalkboard.recruiter.matchrequest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chalkboard.R;
import com.chalkboard.recruiter.TeacherNotificationFragment;


public class RecruiterMatchRequestFragment extends Fragment implements View.OnClickListener {


    private View view;
    private String TAG = "Alert Screen";
    private Activity mActivity;
    private LinearLayout ll_received, ll_sent;
    private TextView tv_received, tv_sent;


    public RecruiterMatchRequestFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_match_request, container, false);
        mActivity = getActivity();
        ImageView notifIcon = (ImageView) mActivity.findViewById(R.id.header_right_menu);
        notifIcon.setVisibility(View.VISIBLE);
        notifIcon.setImageResource(R.drawable.notification_menu);
        notifIcon.setOnClickListener(notificationClick);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        ll_received = (LinearLayout) view.findViewById(R.id.ll_received);
        ll_sent = (LinearLayout) view.findViewById(R.id.ll_sent);
        tv_received = (TextView) view.findViewById(R.id.tv_received);
        tv_sent = (TextView) view.findViewById(R.id.tv_sent);

        ll_received.setOnClickListener(this);
        ll_sent.setOnClickListener(this);
        openFragment(0);
    }

    View.OnClickListener notificationClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((ImageView) (mActivity.findViewById(R.id.header_logo)))
                    .setVisibility(View.GONE);

            ((TextView) (mActivity.findViewById(R.id.header_text))).setText("My Notifications");

            TeacherNotificationFragment fragment = new TeacherNotificationFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.page_container, fragment)
                    .commit();
        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_received:

                tv_received.setTextColor(mActivity.getResources().getColor(R.color.app_color));
                tv_sent.setTextColor(mActivity.getResources().getColor(R.color.grey));
                openFragment(0);
                break;
            case R.id.ll_sent:

                tv_received.setTextColor(mActivity.getResources().getColor(R.color.grey));
                tv_sent.setTextColor(mActivity.getResources().getColor(R.color.app_color));
                openFragment(1);
                break;

        }
    }

    private void openFragment(int flag) {
        Fragment fragment = null;

        if (flag == 0) {
            fragment = RecruiterReceivedFragment.newInstance();
        } else {
            fragment = RecruiterSentFragment.newInstance();
        }
        if (fragment != null) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentTransaction ft = fm
                    .beginTransaction();
            ft.replace(R.id.frame_layout, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_NONE);
            ft.commit();
        }

    }
}
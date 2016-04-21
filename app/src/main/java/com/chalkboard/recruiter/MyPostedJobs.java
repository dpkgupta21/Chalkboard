package com.chalkboard.recruiter;

import static com.chalkboard.GlobalClaass.hideProgressBar;
import static com.chalkboard.GlobalClaass.showProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.R;
import com.chalkboard.teacher.JobObject;

public class MyPostedJobs extends Fragment {

    View rootView;
    Activity context;


    ArrayList<JobObject> dataList = null;

    GetPostedJobs getPostedJobs = null;

    ListView lvList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        context = getActivity();

        rootView = inflater.inflate(R.layout.fragment_list, container, false);


        ImageView notifIcon = (ImageView) context.findViewById(R.id.header_right_menu);
        notifIcon.setVisibility(View.VISIBLE);
        notifIcon.setImageResource(R.drawable.menu_notification_white_icon);
        notifIcon.setOnClickListener(notificationClick);

        lvList = (ListView) rootView.findViewById(R.id.list);

        if (GlobalClaass.isInternetPresent(context)) {

            getPostedJobs = new GetPostedJobs();
            getPostedJobs.execute();

        } else {
            GlobalClaass.showToastMessage(context, "Please check internet connection");
        }


        return rootView;
    }

    View.OnClickListener notificationClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((ImageView) (context.findViewById(R.id.header_logo)))
                    .setVisibility(View.GONE);

            ((TextView) (context.findViewById(R.id.header_text))).setText("My Notifications");

            TeacherNotificationFragment fragment = new TeacherNotificationFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.page_container, fragment)
                    .commit();
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GlobalClaass.clearAsyncTask(getPostedJobs);
    }

    @Override
    public void onResume() {
        super.onResume();

        getPostedJobs = new GetPostedJobs();
        getPostedJobs.execute();

    }


    class GetPostedJobs extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            showProgressBar(context, rootView);
        }

        @Override
        protected String doInBackground(String... params) {

            String resultStr = null;
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("action", "postedJobs"));
                nameValuePairs.add(new BasicNameValuePair("user_id", GlobalClaass.getUserId(context)));


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

            hideProgressBar(context, rootView);

            setUpUi(result);
        }

    }

    public void setUpUi(String result) {

        Log.e("Deepak", result);

        try {
            JSONObject jObject = new JSONObject(result);

            String get_message = jObject.getString("message").trim();
            String get_replycode = jObject.getString("status").trim();

            JSONArray jrr = jObject.getJSONArray("jobs");

            dataList = new ArrayList<JobObject>();

            for (int i = 0; i < jrr.length(); i++) {

                JSONObject jobj = jrr.getJSONObject(i);

                JobObject itmObj = new JobObject();

                itmObj.setId(jobj.getString("id"));
                itmObj.setJobName(jobj.getString("title"));
                itmObj.setJobLocation(jobj.getString("city") + ", "
                        + jobj.getString("country"));
                //itmObj.setJobDate(jobj.getString("start_date"));

                itmObj.setJobStatus(jobj.getString("status"));

                itmObj.setJobImage(jobj.getString("image"));
                //itmObj.setJobFavorite(jobj.getBoolean("status"));


                dataList.add(itmObj);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dataList != null) {

            if (dataList.size() > 0) {

                final JobListAdapter itmAdap = new JobListAdapter(context, dataList);

                lvList.setAdapter(itmAdap);

                lvList.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long arg3) {

                        startActivity(new Intent(context,
                                MyJobsPagerActivity.class).putExtra("dataList",
                                dataList).putExtra("position", position));

                    }
                });


            } else {
                Fragment fragment = new TeacherAddFirstJobFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.page_container, fragment).commit();
            }
        } else {
            Fragment fragment = new TeacherAddFirstJobFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.page_container, fragment).commit();
        }
    }


    class JobListAdapter extends BaseAdapter {

        Activity mContext;
        LayoutInflater inflater;
        private List<JobObject> mainDataList = null;
        private List<JobObject> arrList = null;
        ImageLoader imageloader = null;

        Typeface font, font2;

        public JobListAdapter(Activity context, List<JobObject> mainDataList) {

            mContext = context;
            this.mainDataList = mainDataList;

            arrList = new ArrayList<JobObject>();

            arrList.addAll(this.mainDataList);

            inflater = LayoutInflater.from(mContext);

            font = Typeface.createFromAsset(mContext.getAssets(), "fonts/mark.ttf");
            font2 = Typeface.createFromAsset(mContext.getAssets(), "fonts/marlbold.ttf");

            imageloader = new ImageLoader(mContext);

        }

        class ViewHolder {
            protected TextView name;
            protected TextView status;
            protected ImageView image;
            protected ImageView favourite;

            protected TextView date;
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
                view = inflater.inflate(R.layout.item_job_list, null);

                holder.name = (TextView) view.findViewById(R.id.job_name);
                holder.date = (TextView) view.findViewById(R.id.job_date);
                holder.location = (TextView) view.findViewById(R.id.job_location);
                holder.status = (TextView) view.findViewById(R.id.job_status);

                try {

                    holder.name.setTypeface(font2);
                    holder.location.setTypeface(font);
                    holder.date.setTypeface(font);
                    holder.status.setTypeface(font);
                } catch (Exception e) {

                }

                holder.image = (ImageView) view.findViewById(R.id.job_image);

                holder.favourite = (ImageView) view
                        .findViewById(R.id.job_favorite_image);

                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.name.setText(mainDataList.get(position).getJobName());

		/*	holder.date.setText("Start Date: "
                    + mainDataList.get(position).getJobDate());
*/
            holder.location.setText(mainDataList.get(position).getJobLocation());

            holder.status.setText(mainDataList.get(position).getJobStatus());

			/*if (mainDataList.get(position).isJobFavorite()) {
				holder.favourite.setImageResource(R.drawable.like_icon);
			} else {
				holder.favourite.setImageResource(R.drawable.unlike_icon);
			}*/

            Log.e("Deepak", mainDataList.get(position).getJobImage());

            imageloader.DisplayImage(mainDataList.get(position).getJobImage(),
                    holder.image);


            return view;
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            mainDataList.clear();
            if (charText.length() == 0) {
                mainDataList.addAll(arrList);
            } else {
                for (JobObject wp : arrList) {
                    if (wp.getJobName().toLowerCase(Locale.getDefault())
                            .contains(charText)) {
                        mainDataList.add(wp);
                    } else if (wp.getJobLocation().toLowerCase(Locale.getDefault())
                            .contains(charText)) {
                        mainDataList.add(wp);
                    }/**
                     * else if(wp.getJobName().toLowerCase(Locale.getDefault())
                     * .contains(charText)) { mainDataList.add(wp); }
                     */
                }
            }
            notifyDataSetChanged();
        }

    }
}
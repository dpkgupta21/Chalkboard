package com.chalkboard.teacher;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.R;
import com.chalkboard.utility.Utils;

public class JobFavoriteFragment extends Fragment {

    ListView lvJobList = null;

    View rootView = null;
    //jobTypes
    Activity context = null;

    ArrayList<JobObject> dataList = null;

    GetFavoriteJobItem getJobItem = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();

        rootView = inflater.inflate(R.layout.fragment_list, container,
                false);

        lvJobList = (ListView) rootView.findViewById(R.id.list);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GlobalClaass.clearAsyncTask(getJobItem);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (GlobalClaass.isInternetPresent(context)) {

            getJobItem = new GetFavoriteJobItem();
            getJobItem.execute();
        } else {
            GlobalClaass.showToastMessage(context, "Please check internet connection");
        }


    }

    class GetFavoriteJobItem extends AsyncTask<String, String, String> {

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

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                );
                nameValuePairs
                        .add(new BasicNameValuePair("action", "favoriteJobs"));
                nameValuePairs
                        .add(new BasicNameValuePair("user_id", GlobalClaass.getUserId(context)));

				
				
				/*nameValuePairs
				.add(new BasicNameValuePair("title", ""));
				nameValuePairs
				.add(new BasicNameValuePair("countries", ""));
				nameValuePairs
				.add(new BasicNameValuePair("job_types", ""));
				nameValuePairs
				.add(new BasicNameValuePair("start_date", ""));
				nameValuePairs
				.add(new BasicNameValuePair("offset", ""));
				nameValuePairs
				.add(new BasicNameValuePair("limit", ""));*/

                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(request);

                HttpEntity entity = response.getEntity();

                resultStr = EntityUtils.toString(entity);

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
        String get_message = "";
        try {

            JSONObject jObject = new JSONObject(result);

            if(Utils.getWebServiceStatus(jObject)) {
                get_message = jObject.getString("message").trim();
                String get_replycode = jObject.getString("status").trim();


                JSONArray jrr = jObject.getJSONArray("jobs");

                dataList = new ArrayList<JobObject>();

                for (int i = 0; i < jrr.length(); i++) {

                    JSONObject jobj = jrr.getJSONObject(i);

                    JobObject itmObj = new JobObject();

                    itmObj.setId(jobj.getString("id"));
                    itmObj.setJobDate(jobj.getString("start_date"));
                    itmObj.setJobFavorite(jobj.getBoolean("is_favorite"));
                    itmObj.setJobLocation(jobj.getString("city") + ", " + jobj.getString("country"));
                    itmObj.setJobImage(jobj.getString("image"));
                    itmObj.setJobName(jobj.getString("title"));

                    dataList.add(itmObj);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dataList != null) {

            if (dataList.size() > 0) {

                JobListAdapter itmAdap = new JobListAdapter(context, dataList);

                lvJobList.setAdapter(itmAdap);

                lvJobList.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long arg3) {

                        startActivity(new Intent(context, JobPagerActivity.class)
                                .putExtra("dataList", dataList).putExtra("position",
                                        position));

                    }
                });

            }

        } else {

            ((TextView) rootView.findViewById(R.id.error_message)).setText(get_message);
        }


    }

    class JobListAdapter extends BaseAdapter {

        Typeface font, font2;//=Typeface.createFromAsset(getAssets(), "mark.ttf");
        Activity context;
        LayoutInflater inflater;
        private List<JobObject> mainDataList = null;
        private List<JobObject> arrList = null;
        public ImageLoader imageloader = null;


        public JobListAdapter(Activity context, List<JobObject> mainDataList) {


            this.context = context;
            this.mainDataList = mainDataList;
            font = Typeface.createFromAsset(this.context.getAssets(), "fonts/mark.ttf");
            font2 = Typeface.createFromAsset(this.context.getAssets(), "fonts/marlbold.ttf");
            arrList = new ArrayList<JobObject>();

            arrList.addAll(this.mainDataList);

            inflater = LayoutInflater.from(this.context);

            imageloader = new ImageLoader(this.context);

        }

        class ViewHolder {
            protected TextView name;
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

                holder.image = (ImageView) view.findViewById(R.id.job_image);

                holder.favourite = (ImageView) view
                        .findViewById(R.id.job_favorite_image);

                try {

                    holder.name.setTypeface(font2);
                    holder.date.setTypeface(font);
                    holder.location.setTypeface(font);
                } catch (Exception e) {

                }

                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }


            holder.name.setText(mainDataList.get(position).getJobName());

            holder.date.setText("Start Date: "
                    + mainDataList.get(position).getJobDate());

            holder.location.setText(mainDataList.get(position).getJobLocation());

            if (mainDataList.get(position).isJobFavorite()) {
                //holder.favourite.setImageResource(R.drawable.like_icon);
                holder.favourite.setImageResource(R.drawable.like_icon_active);
            } else {
                holder.favourite.setImageResource(R.drawable.unlike_icon);
            }

            holder.favourite.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {


                    if (mainDataList.get(position).isJobFavorite()) {
                        //holder.favourite.setImageResource(R.drawable.icon_like);

                        new RemoveJobFavorites(mainDataList.get(position).getId()).execute();
                        holder.favourite.setImageResource(R.drawable.unlike_icon);
                        mainDataList.get(position).setJobFavorite(false);

                    } else {
                        //holder.favourite.setImageResource(R.drawable.unlike_icon);

                        new AddJobFavorites(mainDataList.get(position).getId()).execute();
                        holder.favourite.setImageResource(R.drawable.like_icon_active);
                        mainDataList.get(position).setJobFavorite(true);

                    }


                }
            });

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

        RemoveJobFavorites removeJobFavorites;

        class RemoveJobFavorites extends AsyncTask<String, String, String> {

            String jobId;

            public RemoveJobFavorites(String id) {
                jobId = id;
            }

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

                    nameValuePairs.add(new BasicNameValuePair("action",
                            "removeToFavorite"));

                    nameValuePairs.add(new BasicNameValuePair("job_id", jobId));
                    nameValuePairs.add(new BasicNameValuePair("user_id",
                            GlobalClaass.getUserId(context)));
                    request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(request);

                    HttpEntity entity = response.getEntity();

                    resultStr = EntityUtils.toString(entity);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return resultStr;

            }

            @Override
            protected void onPostExecute(String result) {

                hideProgressBar(context, rootView);

                getJobItem = new GetFavoriteJobItem();
                getJobItem.execute();

            }

        }

        AddJobFavorites addJobFavorites;

        class AddJobFavorites extends AsyncTask<String, String, String> {

            String jobId;

            public AddJobFavorites(String id) {
                jobId = id;
            }

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

                    nameValuePairs.add(new BasicNameValuePair("action",
                            "addToFavorite"));

                    nameValuePairs.add(new BasicNameValuePair("job_id", jobId));
                    nameValuePairs.add(new BasicNameValuePair("user_id",
                            GlobalClaass.getUserId(context)));
                    request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(request);

                    HttpEntity entity = response.getEntity();

                    resultStr = EntityUtils.toString(entity);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return resultStr;

            }

            @Override
            protected void onPostExecute(String result) {

                hideProgressBar(context, rootView);

                getJobItem = new GetFavoriteJobItem();
                getJobItem.execute();

            }

        }

    }

}
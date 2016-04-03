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

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.PreferenceConnector;
import com.chalkboard.R;
import com.chalkboard.model.ReadMapIdDTO;

public class JobListAdapter extends BaseAdapter {

    Typeface font, font2;//=Typeface.createFromAsset(getAssets(), "mark.ttf");
    Activity context;
    LayoutInflater inflater;
    private List<JobObject> mainDataList = null;
    private List<JobObject> arrList = null;
    public ImageLoader imageloader = null;

    View rootView;

    private ReadMapIdDTO readMapIdDTO;

    public JobListAdapter(Activity context, List<JobObject> mainDataList, View rootview) {

        this.rootView = rootview;

        this.context = context;
        this.mainDataList = mainDataList;
        font = Typeface.createFromAsset(this.context.getAssets(), "fonts/mark.ttf");
        font2 = Typeface.createFromAsset(this.context.getAssets(), "fonts/marlbold.ttf");
        arrList = new ArrayList<JobObject>();

        arrList.addAll(this.mainDataList);

        inflater = LayoutInflater.from(this.context);

        imageloader = new ImageLoader(this.context);

        readMapIdDTO = PreferenceConnector.getObjectFromPref(context,
                PreferenceConnector.READ_MAP_ID);

    }

    private void refreshList(){
        readMapIdDTO = PreferenceConnector.getObjectFromPref(context,
                PreferenceConnector.READ_MAP_ID);
        notifyDataSetChanged();
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

        //        Check bold if already read
        String jobId = mainDataList.get(position).getId();
        boolean isAlreadyRead = readMapIdDTO.getTeacherMapId().get(jobId);

        if (isAlreadyRead) {
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.black));

        } else {
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.dark_grey));

        }


        if (mainDataList.get(position).isJobFavorite()) {
            //holder.favourite.setImageResource(R.drawable.like_icon);
            holder.favourite.setImageResource(R.drawable.icon_like);
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
                    holder.favourite.setImageResource(R.drawable.icon_like);
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

            ReadMapIdDTO readMapIdDTO = PreferenceConnector.getObjectFromPref(context,
                    PreferenceConnector.READ_MAP_ID);
            readMapIdDTO.getTeacherMapId().put(jobId, false);
            PreferenceConnector.putObjectIntoPref(context, readMapIdDTO,
                    PreferenceConnector.READ_MAP_ID);
            refreshList();

        }

    }

}
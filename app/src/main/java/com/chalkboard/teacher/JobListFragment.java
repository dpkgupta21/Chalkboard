package com.chalkboard.teacher;

import static com.chalkboard.GlobalClaass.hideProgressBar;
import static com.chalkboard.GlobalClaass.showProgressBar;
import static com.chalkboard.GlobalClaass.showToastMessage;

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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chalkboard.GlobalClaass;
import com.chalkboard.PreferenceConnector;
import com.chalkboard.R;

public class JobListFragment extends Fragment {

    ListView lvJobList = null;

    View rootView = null;

    Activity context = null;
    ArrayList<JobObject> dataList = null;

    EditText edtSearch = null;

    GetJobItem getJobItem = null;

    RelativeLayout rlLocation, rlType; //rlDate;
    ImageView ivLocation, ivType; //ivDate;
    TextView tvLocation, tvType; //tvDate;

    JobListAdapter itmAdap = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();



        GlobalClaass.savePrefrencesfor(context,
                PreferenceConnector.COUNTRIESARRAY, "");
        GlobalClaass.savePrefrencesfor(context, PreferenceConnector.TYPEARRAY,
                "");
        GlobalClaass.savePrefrencesfor(context, PreferenceConnector.STARTDATE,
                "");

        rootView = inflater.inflate(R.layout.fragment_list, container, false);

        edtSearch = (EditText) rootView.findViewById(R.id.search_list);
        // edtSearch.setVisibility(View.VISIBLE);

        lvJobList = (ListView) rootView.findViewById(R.id.list);

        rlLocation = (RelativeLayout) context
                .findViewById(R.id.bottom_location);
        rlType = (RelativeLayout) context.findViewById(R.id.bottom_type);
        //rlDate = (RelativeLayout) context.findViewById(R.id.bottom_date);

        ivLocation = (ImageView) context
                .findViewById(R.id.bottom_location_image);
        ivType = (ImageView) context.findViewById(R.id.bottom_type_image);
        //ivDate = (ImageView) context.findViewById(R.id.bottom_date_image);

        tvLocation = (TextView) context.findViewById(R.id.bottom_location_text);
        tvType = (TextView) context.findViewById(R.id.bottom_type_text);
        //tvDate = (TextView) context.findViewById(R.id.bottom_date_text);

        rlLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(context,
                        SelectJobLocationActivity.class));
                rlLocation.setBackgroundColor(Color.parseColor("#00c7d4"));
                ivLocation
                        .setImageResource(R.drawable.location_white_menu_icon_footer);
                tvLocation.setTextColor(Color.WHITE);
                //context.overridePendingTransition(R.anim.slide_up, 0);
            }
        });
        rlType.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(context, SelectJobTypeActivity.class));
                rlType.setBackgroundColor(Color.parseColor("#00c7d4"));
                ivType.setImageResource(R.drawable.clock_white_icon);
                tvType.setTextColor(Color.WHITE);
                //context.overridePendingTransition(R.anim.slide_up, 0);
            }
        });
//		rlDate.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				startActivity(new Intent(context,
//						SelectJobStartDateActivity.class));
//				rlDate.setBackgroundColor(Color.parseColor("#00c7d4"));
//				ivDate.setImageResource(R.drawable.calendar_white_menu_icon);
//				tvDate.setTextColor(Color.WHITE);
//				//context.overridePendingTransition(R.anim.slide_up, 0);
//			}
//		});

        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (GlobalClaass.isInternetPresent(context)) {
                    GlobalClaass.clearAsyncTask(getJobItem);

                    getJobItem = new GetJobItem(GlobalClaass.getCountriesArray(context),
                            GlobalClaass.getTypeArray(context),
                            GlobalClaass.getStartDate(context), s.toString());
                    getJobItem.execute();
                }


            }
        });


        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GlobalClaass.clearAsyncTask(getJobItem);
        GlobalClaass.removeSearchPrefrences(context);
    }

    @Override
    public void onResume() {
        super.onResume();

        rlLocation.setBackgroundColor(Color.TRANSPARENT);
        rlType.setBackgroundColor(Color.TRANSPARENT);
        //rlDate.setBackgroundColor(Color.TRANSPARENT);

        //ivDate.setImageResource(R.drawable.calendar_black_menu_icon);
        //xtvDate.setTextColor(Color.BLACK);

        ivType.setImageResource(R.drawable.clock_black_icon);
        tvType.setTextColor(Color.BLACK);

        ivLocation.setImageResource(R.drawable.location_black_menu_icon_footer);
        tvLocation.setTextColor(Color.BLACK);

        if (GlobalClaass.isInternetPresent(context)) {
            edtSearch.setText("");
            getJobItem = new GetJobItem(GlobalClaass.getCountriesArray(context),
                    GlobalClaass.getTypeArray(context),
                    GlobalClaass.getStartDate(context), "");
            getJobItem.execute();
        } else {
            GlobalClaass.showToastMessage(context, "Please check internet connection");
        }


    }

    class GetJobItem extends AsyncTask<String, String, String> {

        String countries;
        String job_types;
        String start_date;

        String search;

        public GetJobItem(String countries, String job_types, String start_date, String search) {

            this.countries = countries;
            this.job_types = job_types;
            this.start_date = start_date;
            this.search = search;

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
                nameValuePairs.add(new BasicNameValuePair("action", "jobs"));
                nameValuePairs.add(new BasicNameValuePair("user_id",
                        GlobalClaass.getUserId(context)));

                if (!search.equalsIgnoreCase("")) {
                    nameValuePairs.add(new BasicNameValuePair("title",
                            search));
                }

                if (!countries.equalsIgnoreCase("")) {
                    nameValuePairs.add(new BasicNameValuePair("countries",
                            countries));
                }

                if (!job_types.equalsIgnoreCase("")) {
                    nameValuePairs.add(new BasicNameValuePair("job_types",
                            job_types));
                }

                if (!start_date.equalsIgnoreCase("")) {
                    nameValuePairs.add(new BasicNameValuePair("start_date",
                            start_date));
                }

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

            Log.e("Deepak", "result:" + result);

            setUpUi(result);
        }

    }

    public void setUpUi(String result) {
        String get_message = "";
        try {
            ((TextView) rootView.findViewById(R.id.error_message)).setText("");
            dataList = new ArrayList<JobObject>();

            if (itmAdap != null) {
                itmAdap.notifyDataSetChanged();
            }

            itmAdap = new JobListAdapter(context, dataList, rootView);

            lvJobList.setAdapter(itmAdap);
            lvJobList.setAdapter(null);

            lvJobList.setOnItemClickListener(null);


            JSONObject jObject = new JSONObject(result);

            get_message = jObject.getString("message").trim();
            String get_replycode = jObject.getString("status").trim();

            if (get_replycode.equalsIgnoreCase("false")) {
                showToastMessage(context, get_message);
            } else {

                JSONArray jrr = jObject.getJSONArray("jobs");

                for (int i = 0; i < jrr.length(); i++) {

                    JSONObject jobj = jrr.getJSONObject(i);

                    JobObject itmObj = new JobObject();

                    itmObj.setId(jobj.getString("id"));
                    itmObj.setJobDate(jobj.getString("start_date"));
                    itmObj.setJobFavorite(jobj.getBoolean("is_favorite"));
                    itmObj.setJobLocation(jobj.getString("city") + ", "
                            + jobj.getString("country"));
                    itmObj.setJobImage(jobj.getString("image"));
                    itmObj.setJobName(jobj.getString("title"));

                    dataList.add(itmObj);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (dataList.size() > 0) {

            itmAdap = new JobListAdapter(context, dataList, rootView);

            lvJobList.setAdapter(itmAdap);

            lvJobList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {

                    startActivity(new Intent(context,
                            JobPagerActivity.class).putExtra("dataList",
                            dataList).putExtra("position", position));

                }
            });
            edtSearch.setVisibility(View.VISIBLE);


        } else {
            ((TextView) rootView.findViewById(R.id.error_message)).setText(get_message);
        }


    }

}
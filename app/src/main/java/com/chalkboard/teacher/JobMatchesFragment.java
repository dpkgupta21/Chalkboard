package com.chalkboard.teacher;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.R;
import com.chalkboard.customviews.CustomProgressDialog;
import com.chalkboard.model.MatchSentDTO;
import com.chalkboard.utility.Utils;
import com.chalkboard.webservice.WebserviceConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.volley.ApplicationController;
import com.volley.CustomJsonRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chalkboard.GlobalClaass.hideProgressBar;
import static com.chalkboard.GlobalClaass.showProgressBar;

public class JobMatchesFragment extends Fragment {

    private static final String TAG = "JobMatchesFragment";
    private SwipeMenuListView lvJobList = null;

    private View rootView = null;

    private Activity context = null;

    private RemoveJobMatch removeJobMatch;
    //ArrayList<JobObject> dataList = null;

    //GetJobMatchesItem getJobItem = null;

    private JobMatchListAdapter adapter = null;

    private List<MatchSentDTO> matchesDTOList;

    //Typeface font2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();

        rootView = inflater.inflate(R.layout.swipe_list, container, false);
        lvJobList = (SwipeMenuListView) rootView.findViewById(R.id.list);


//         dataList = new ArrayList<JobObject>();
//        itmAdap = new JobMatchListAdapter(context, matchesDTOList);
//        lvJobList.setAdapter(itmAdap);


        //lvJobList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);


        //executeTask();

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set creator
        lvJobList.setMenuCreator(creator);

        lvJobList
                .setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(int position,
                                                   SwipeMenu menu, int index) {
                        switch (index) {
                            case 0:
                                // chat
                                startActivity(new Intent(context,
                                        TeacherChatBoardActivity.class)
                                        .putExtra(
                                                "id",
                                                matchesDTOList.get(position)
                                                        .getRecruiter_id()).putExtra("name",
                                                matchesDTOList.get(position).getRecruiter().getName()));
                                break;
                            case 1:
                                // delete

                                if (GlobalClaass.isInternetPresent(context)) {

                                    removeJobMatch = new RemoveJobMatch(
                                            matchesDTOList.get(position).getId());
                                    removeJobMatch.execute();
                                } else {
                                    GlobalClaass.showToastMessage(context, "Please check internet connection");
                                }

                                break;
                        }
                        // false : close the menu; true : not close the
                        // menu
                        return false;
                    }
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //  GlobalClaass.clearAsyncTask(getJobItem);
        GlobalClaass.clearAsyncTask(removeJobMatch);

    }

    @Override
    public void onResume() {
        super.onResume();

        getMatchRequestList();
        //executeTask();

    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {

            // create "delete" item
            SwipeMenuItem chatItem = new SwipeMenuItem(
                    context);
            // set item background
            chatItem.setBackground(R.drawable.chat_back);
            // set item width
            chatItem.setWidth(150);
            // set a icon
            chatItem.setIcon(R.drawable.chat_icon_text);
            // add to menu
            menu.addMenuItem(chatItem);


//            // create "delete" item
//            SwipeMenuItem deleteItem = new SwipeMenuItem(
//                    context);
//            // set item background
//            deleteItem.setBackground(R.drawable.delete_back);
//            // set item width
//            deleteItem.setWidth(150);
//            // set a icon
//            deleteItem.setIcon(R.drawable.delete_slide);
//            // add to menu
//            menu.addMenuItem(deleteItem);
        }
    };
//
//    public void executeTask() {
//       // GlobalClaass.clearAsyncTask(getJobItem);
//        if (GlobalClaass.isInternetPresent(context)) {
//
////            getJobItem = new GetJobMatchesItem();
////            getJobItem.execute();
//        } else {
//            GlobalClaass.showToastMessage(context, "Please check internet connection");
//        }
//
//    }

    private void getMatchRequestList() {

        if (Utils.isOnline(getActivity())) {
            Map<String, String> params = new HashMap<>();
            params.put("action", WebserviceConstant.MATCHES_JOBS_REQUEST);
            params.put("user_id", GlobalClaass.getUserId(getActivity()));


            CustomProgressDialog.showProgDialog(getActivity(), null);
            CustomJsonRequest postReq = new CustomJsonRequest(Request.Method.POST, WebserviceConstant.SERVICE_BASE_URL, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Utils.ShowLog(TAG, "got some response = " + response.toString());
                                Type type = new TypeToken<ArrayList<MatchSentDTO>>() {
                                }.getType();
                                matchesDTOList = new Gson().fromJson(response.
                                        getJSONArray("jobs").toString(), type);
                                setUpUi(matchesDTOList);

                            } catch (Exception e) {
                                CustomProgressDialog.hideProgressDialog();
                                setUpUi(matchesDTOList);
                                e.printStackTrace();
                            }
                            CustomProgressDialog.hideProgressDialog();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    CustomProgressDialog.hideProgressDialog();
                    Utils.showExceptionDialog(getActivity());
                    //       CustomProgressDialog.hideProgressDialog();
                }
            });
            ApplicationController.getInstance().getRequestQueue().add(postReq);
            postReq.setRetryPolicy(new DefaultRetryPolicy(
                    30000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            CustomProgressDialog.showProgDialog(getActivity(), null);
        } else {
            Utils.showNoNetworkDialog(getActivity());
        }

    }

//    class GetJobMatchesItem extends AsyncTask<String, String, String> {
//
//        @Override
//        protected void onPreExecute() {
//            showProgressBar(context, rootView);
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            String resultStr = null;
//            try {
//
//                HttpClient httpClient = new DefaultHttpClient();
//                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);
//
//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
//                        2);
//                nameValuePairs.add(new BasicNameValuePair("action",
//                        "matchesJobs"));
//                nameValuePairs.add(new BasicNameValuePair("user_id",
//                        GlobalClaass.getUserId(context)));
//                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                HttpResponse response = httpClient.execute(request);
//
//                HttpEntity entity = response.getEntity();
//
//                resultStr = EntityUtils.toString(entity);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return resultStr;
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//            hideProgressBar(context, rootView);
//
//            // setUpUi(result);
//        }
//
//    }

    public void setUpUi(List<MatchSentDTO> matchReceivedDTOList) {
        String get_message = "";
        //dataList = new ArrayList<JobObject>();

//        try {
//
//            Log.e("Deepak", " job match result: " + result);
//
//            if (itmAdap != null) {
//                itmAdap.notifyDataSetChanged();
//            }
//
//            itmAdap = new JobMatchListAdapter(context, dataList);
//
//            lvJobList.setAdapter(itmAdap);
//
//            JSONObject jObject = new JSONObject(result);
//
//            get_message = jObject.getString("message").trim();
//            String get_replycode = jObject.getString("status").trim();
//
//            JSONArray jrr = jObject.getJSONArray("jobs");
//
//            for (int i = 0; i < jrr.length(); i++) {
//
//                JSONObject jobj = jrr.getJSONObject(i);
//
//                JobObject itmObj = new JobObject();
//
//                itmObj.setId(jobj.getString("id"));
//                itmObj.setJobMatchDate(jobj.getString("match_date"));
//                itmObj.setJobLocation(jobj.getString("city") + ", "
//                        + jobj.getString("country"));
//                itmObj.setJobImage(jobj.getString("image"));
//                itmObj.setJobName(jobj.getString("title"));
//
//                itmObj.setJobRecruiterId(jobj.getString("recruiter_id"));
//
//                itmObj.setJobRecruiterName(jobj.getString("recruiter"));
//
//                dataList.add(itmObj);
//
//                /**
//                 * {"id":"674","title":"Test Job for credit check",
//                 "city":"Jaipur",
//                 "country":"Ashmore and Cartier Islands",
//                 "image":"","match_date":"07:35 PM",
//                 "recruiter":"Regina Millerj","recruiter_id":"671"},
//                 */
//
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        if (matchesDTOList!=null && matchesDTOList.size() > 0) {

            adapter = new JobMatchListAdapter(context, matchReceivedDTOList);

            lvJobList.setAdapter(adapter);

            lvJobList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {

                    startActivity(new Intent(context,
                            TeacherChatBoardActivity.class).putExtra("id",
                            matchesDTOList.get(position).getRecruiter_id()).putExtra("name",
                            matchesDTOList.get(position).getRecruiter().getName()));

                }
            });

				

				/*
                 * lvJobList.setOnItemLongClickListener(new
				 * OnItemLongClickListener() {
				 * 
				 * @Override public boolean onItemLongClick(AdapterView<?>
				 * parent, View view, int pos, long id) {
				 * 
				 * 
				 * if (view.findViewById(R.id.overlay_layout).getVisibility() ==
				 * View.VISIBLE) {
				 * view.findViewById(R.id.overlay_layout).setVisibility
				 * (View.GONE); }else{
				 * view.findViewById(R.id.overlay_layout).setVisibility
				 * (View.VISIBLE); }
				 * 
				 * return true; }
				 * 
				 * });
				 */


        } else {
            ((TextView) rootView.findViewById(R.id.error_message)).setVisibility(View.VISIBLE);
        }

    }

    class JobMatchListAdapter extends BaseAdapter {

        Activity mContext;
        LayoutInflater inflater;
        private List<MatchSentDTO> matchReceivedDTOList = null;

        ImageLoader imageloader = null;

        public JobMatchListAdapter(Activity context,
                                   List<MatchSentDTO> matchReceivedDTOList) {

            mContext = context;
            this.matchReceivedDTOList = matchReceivedDTOList;
            inflater = LayoutInflater.from(mContext);

            imageloader = new ImageLoader(mContext);


        }

        class ViewHolder {
            protected TextView name;
            protected ImageView image;

            protected ImageView chat;
            protected ImageView remove;

            protected TextView offer_by;

            protected TextView date;
            protected TextView location;

        }

        @Override
        public int getCount() {
            return matchReceivedDTOList.size();
        }

        @Override
        public MatchSentDTO getItem(int position) {
            return matchReceivedDTOList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.item_job_match_list, null);

                holder.name = (TextView) view.findViewById(R.id.job_name);
                holder.date = (TextView) view.findViewById(R.id.job_date);
                holder.location = (TextView) view
                        .findViewById(R.id.job_location);

                holder.image = (ImageView) view.findViewById(R.id.job_image);


                holder.offer_by = (TextView) view
                        .findViewById(R.id.job_offer_by);


                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }


            holder.name.setText(matchReceivedDTOList.get(position).getTitle());

            holder.date.setText(matchReceivedDTOList.get(position).getMatch_date());

            holder.offer_by.setText(matchReceivedDTOList.get(position)
                    .getRecruiter().getName());

            holder.location
                    .setText(matchReceivedDTOList.get(position).getCity() +
                            ", " + matchReceivedDTOList.get(position).getCountry());


            imageloader.DisplayImage(matchReceivedDTOList.get(position).getImage(),
                    holder.image);

            return view;
        }

    }


    class RemoveJobMatch extends AsyncTask<String, String, String> {

        String jobId;

        public RemoveJobMatch(String id) {
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
                        "removeToMatchList"));

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
            onResume();
            //executeTask();
        }

    }

}
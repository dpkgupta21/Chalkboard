package com.chalkboard.recruiter;

import static com.chalkboard.GlobalClaass.hideProgressBar;
import static com.chalkboard.GlobalClaass.showProgressBar;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.R;
import com.chalkboard.teacher.TeacherChatBoardActivity;

public class TeacherMatchesFragment extends Fragment {

    SwipeMenuListView lvTeacherList = null;

    View rootView = null;

    Activity context = null;

    ArrayList<TeacherObject> dataList = null;

    GetTeacherItem getTeacherItem = null;

    TeacherMatchListAdapter itmAdap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();

        rootView = inflater.inflate(R.layout.swipe_list, container,
                false);

        ImageView notifIcon = (ImageView) context.findViewById(R.id.header_right_menu);
        notifIcon.setVisibility(View.VISIBLE);
        notifIcon.setImageResource(R.drawable.notification_menu);
        notifIcon.setOnClickListener(notificationClick);

        lvTeacherList = (SwipeMenuListView) rootView.findViewById(R.id.list);

        dataList = new ArrayList<TeacherObject>();

        itmAdap = new TeacherMatchListAdapter(context, dataList);

        lvTeacherList.setAdapter(itmAdap);


        lvTeacherList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // chat
                        startActivity(new Intent(context, TeacherChatBoardActivity.class).putExtra("id", dataList.get(position).getId()).putExtra("name",
                                dataList.get(position).getTeacherName()));
                        break;
                    case 1:
                        // delete
                        removeJobMatch = new RemoveJobMatch(dataList.get(position).getId());
                        removeJobMatch.execute();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

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

            }
        };

        // set creator
        lvTeacherList.setMenuCreator(creator);

        //lvTeacherList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);


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

    void executeTask() {

        if (GlobalClaass.isInternetPresent(context)) {

            getTeacherItem = new GetTeacherItem();
            getTeacherItem.execute();

        } else {
            GlobalClaass.showToastMessage(context, "Please check internet connection");
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        executeTask();
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();

        GlobalClaass.clearAsyncTask(getTeacherItem);

        GlobalClaass.clearAsyncTask(removeJobMatch);
    }


    class GetTeacherItem extends AsyncTask<String, String, String> {

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
                        2);
                nameValuePairs
                        .add(new BasicNameValuePair("action", "myMachesTeachers"));

                nameValuePairs
                        .add(new BasicNameValuePair("user_id", GlobalClaass.getUserId(context)));

                //nameValuePairs
                //.add(new BasicNameValuePair("user_id", "659"));

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

            // Toast.makeText(context, result, Toast.LENGTH_LONG).show();

            hideProgressBar(context, rootView);

            setUpUi(result);
        }

    }

    public void setUpUi(String result) {
        String get_message = "";
        try {

            Log.e("Deepak", " teacher match result: " + result);

            dataList = new ArrayList<TeacherObject>();

            if (itmAdap != null) {
                itmAdap.notifyDataSetChanged();
            }

            itmAdap = new TeacherMatchListAdapter(context, dataList);

            lvTeacherList.setAdapter(itmAdap);

            JSONObject jObject = new JSONObject(result);

            get_message = jObject.getString("message").trim();
            String get_replycode = jObject.getString("status").trim();

            JSONArray jrr = jObject.getJSONArray("teachers");


            for (int i = 0; i < jrr.length(); i++) {

                JSONObject jobj = jrr.getJSONObject(i);

                TeacherObject itmObj = new TeacherObject();

                itmObj.setId(jobj.get("id").toString());
                itmObj.setTeacherAbout(jobj.get("about").toString());

                itmObj.setTeacherAge(jobj.get("age").toString());
                //itmObj.setTeacherEducation(jobj.getString("TeacherEducation"));
                itmObj.setTeacherEmail(jobj.get("email").toString());

                //itmObj.setTeacherExperience(jobj.getString("TeacherExperience"));
                itmObj.setTeacherGender(jobj.get("gender").toString());
                itmObj.setTeacherImage(jobj.get("image").toString());

                itmObj.setTeacherLocation(jobj.get("city").toString() + ", "
                        + jobj.get("country").toString());
                itmObj.setTeacherName(jobj.get("name").toString());


                itmObj.setTeacherMatch(jobj.getBoolean("is_match"));
                itmObj.setTeacherFavorite(jobj.getBoolean("is_favorite"));

                dataList.add(itmObj);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dataList != null && dataList.size() > 0) {

            itmAdap = new TeacherMatchListAdapter(context, dataList);

            lvTeacherList.setAdapter(itmAdap);

            lvTeacherList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {

                    startActivity(new Intent(context, TeacherChatBoardActivity.class).putExtra("id", dataList.get(position).getId()).putExtra("name",
                            dataList.get(position).getTeacherName()));

                }
            });

			
			
			/*lvTeacherList.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view,
						int pos, long id) {
					
					
					if (view.findViewById(R.id.overlay_layout).getVisibility() == View.VISIBLE) {
						view.findViewById(R.id.overlay_layout).setVisibility(View.GONE);
					}else{
						view.findViewById(R.id.overlay_layout).setVisibility(View.VISIBLE);
					}
	                
	                return true;
				}
				
			});*/


        } else {
            ((TextView) rootView.findViewById(R.id.error_message)).setVisibility(View.VISIBLE);
        }

    }

    class TeacherMatchListAdapter extends BaseAdapter {

        Activity mContext;
        LayoutInflater inflater;
        private List<TeacherObject> mainDataList = null;

        private List<TeacherObject> arrList = null;
        Typeface font, font2;
        ImageLoader imageloader = null;

        public TeacherMatchListAdapter(Activity context, List<TeacherObject> mainDataList) {

            mContext = context;
            this.mainDataList = mainDataList;
            inflater = LayoutInflater.from(mContext);
            font = Typeface.createFromAsset(mContext.getAssets(), "fonts/mark.ttf");
            font2 = Typeface.createFromAsset(mContext.getAssets(), "fonts/marlbold.ttf");
            arrList = new ArrayList<TeacherObject>();

            arrList.addAll(this.mainDataList);

            imageloader = new ImageLoader(mContext);

        }

        class ViewHolder {
            protected TextView name, offer_by, date;
            protected ImageView image;
            protected ImageView favourite;


            protected ImageView chat;
            protected ImageView remove;

            protected TextView location;

        }

        @Override
        public int getCount() {
            return mainDataList.size();
        }

        @Override
        public TeacherObject getItem(int position) {
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
                view = inflater.inflate(R.layout.item_teacher_match_list, null);

                holder.name = (TextView) view.findViewById(R.id.teacher_name);
                holder.date = (TextView) view.findViewById(R.id.teacher_date);
                holder.location = (TextView) view
                        .findViewById(R.id.teacher_location);

                holder.image = (ImageView) view.findViewById(R.id.teacher_image);
                holder.offer_by = (TextView) view
                        .findViewById(R.id.teacher_offer_by);


                try {

                    holder.name.setTypeface(font2);
                    holder.date.setTypeface(font);
                    holder.location.setTypeface(font);
                    holder.offer_by.setTypeface(font);
                } catch (Exception e) {

                }

                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.name.setText(mainDataList.get(position).getTeacherName() + " | "
                    + mainDataList.get(position).getTeacherAge());

            holder.location
                    .setText(mainDataList.get(position).getTeacherLocation());


            //	holder.date.setText(mainDataList.get(position).getJobDate());

            //holder.offer_by.setText(mainDataList.get(position)
            //	.getJobRecruiterName());


            imageloader.DisplayImage(mainDataList.get(position).getTeacherImage(),
                    holder.image);

            return view;
        }


    }

    RemoveJobMatch removeJobMatch;

    class RemoveJobMatch extends AsyncTask<String, String, String> {

        String teacherId;

        public RemoveJobMatch(String id) {
            teacherId = id;
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
                        "removeFromMatchProfile"));

                nameValuePairs.add(new BasicNameValuePair("teacher_id",
                        teacherId));
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

            executeTask();

        }

    }
}
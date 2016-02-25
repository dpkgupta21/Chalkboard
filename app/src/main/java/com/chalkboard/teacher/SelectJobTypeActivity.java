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
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.chalkboard.CountryData;
import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.PreferenceConnector;
import com.chalkboard.R;

public class SelectJobTypeActivity extends Activity {

    private ListView lvJobList = null;
    private Activity context = null;
    private ArrayList<CountryData> dataList = null;
    private GetCounries getCounries = null;
    private CountryListAdapter adapter;
    //Typeface font, font2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.fragment_country_list);

        lvJobList = (ListView) findViewById(R.id.list);

        (findViewById(R.id.close_header)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.txt_header_text)).setText(getString(R.string.select_type));


        ((TextView) findViewById(R.id.txt_view_clear)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < dataList.size(); i++) {
                    dataList.get(i).setChecked(false);

                }
                adapter.setCountryList(dataList);
                adapter.notifyDataSetChanged();
            }
        });


        ((TextView) findViewById(R.id.txt_done)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (dataList != null) {
                    StringBuffer sb = new StringBuffer();

                    String seperator = "";

                    for (CountryData bean : dataList) {

                        if (bean.isChecked()) {
                            sb.append(seperator);
                            seperator = ",";
                            sb.append(bean.getCountry_Id());

                        }
                    }

                    String s = sb.toString().trim();

                    if (TextUtils.isEmpty(s)) {
                        GlobalClaass.savePrefrencesfor(context, PreferenceConnector.TYPEARRAY, "");
                    } else {
                        GlobalClaass.savePrefrencesfor(context, PreferenceConnector.TYPEARRAY, s);
                    }
                } else {
                    GlobalClaass.savePrefrencesfor(context, PreferenceConnector.TYPEARRAY, "");
                }

                finish();
            }
        });

        if (GlobalClaass.isInternetPresent(context)) {

            getCounries = new GetCounries();
            getCounries.execute();


        } else {
            GlobalClaass.showToastMessage(context, "Please check internet connection");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalClaass.clearAsyncTask(getCounries);
    }

    class GetCounries extends AsyncTask<String, String, String> {

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
                nameValuePairs
                        .add(new BasicNameValuePair("action", "jobTypes"));

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

            hideProgressBar(context);

            setUpUi(result);
        }

    }

    public void setUpUi(String result) {

        try {

            Log.e("Deepak", "result: " + result);

            JSONObject jObject = new JSONObject(result);

            String get_message = jObject.getString("message").trim();
            String get_replycode = jObject.getString("status").trim();

            JSONArray jrr = jObject.getJSONArray("jobTypes");

            dataList = new ArrayList<CountryData>();

            for (int i = 0; i < jrr.length(); i++) {

                JSONObject jobj = jrr.getJSONObject(i);

                CountryData itmObj = new CountryData();

                itmObj.setCountry_Id(jobj.getString("id"));
                itmObj.setCountry_Name(jobj.getString("name"));


                itmObj.setChecked(false);

                if (!GlobalClaass.getTypeArray(context).equalsIgnoreCase("")) {

                    String[] arr = GlobalClaass.getTypeArray(context).split(",");

                    for (int j = 0; j < arr.length; j++) {
                        if (arr[j].equalsIgnoreCase(jobj.getString("id"))) {
                            itmObj.setChecked(true);
                        }
                    }

                }

                dataList.add(itmObj);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dataList != null) {

            if (dataList.size() > 0) {

                 adapter = new CountryListAdapter(
                        context, dataList);

                lvJobList.setAdapter(adapter);

                lvJobList.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long arg3) {
                        CheckBox chk = (CheckBox) view
                                .findViewById(R.id.check);
                        CountryData bean = dataList
                                .get(position);
                        if (bean.isChecked()) {
                            bean.setChecked(false);
                            chk.setChecked(false);
                        } else {
                            bean.setChecked(true);
                            chk.setChecked(true);
                        }
                    }
                });

            }
        }
    }

    class CountryListAdapter extends BaseAdapter {

        Activity mContext;
        LayoutInflater inflater;
        private List<CountryData> mainDataList = null;
        private List<CountryData> arrList = null;
        ImageLoader imageloader = null;

        Typeface font;

        public CountryListAdapter(Activity context,
                                  List<CountryData> mainDataList) {

            mContext = context;
            this.mainDataList = mainDataList;

            arrList = new ArrayList<CountryData>();
            font = Typeface.createFromAsset(mContext.getAssets(), "fonts/mark.ttf");
            arrList.addAll(this.mainDataList);

            inflater = LayoutInflater.from(mContext);

            imageloader = new ImageLoader(mContext);

        }

        public void setCountryList(List<CountryData> mainDataList){
            this.mainDataList=mainDataList;
        }

        class ViewHolder {
            protected TextView name;
            protected CheckBox check;

        }

        @Override
        public int getCount() {
            return mainDataList.size();
        }

        @Override
        public CountryData getItem(int position) {
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
                view = inflater.inflate(R.layout.item_country_list, null);

                holder.name = (TextView) view.findViewById(R.id.country_name);
                holder.check = (CheckBox) view.findViewById(R.id.check);

                try {

                    holder.name.setTypeface(font);
                } catch (Exception e) {

                }

                view.setTag(holder);

                view.setTag(R.id.country_name, holder.name);
                view.setTag(R.id.check, holder.check);

                holder.check
                        .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(CompoundButton vw,
                                                         boolean isChecked) {

                                int getPosition = (Integer) vw.getTag();
                                mainDataList.get(getPosition).setChecked(
                                        vw.isChecked());

                            }
                        });

            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.check.setTag(position);

            holder.name.setText(mainDataList.get(position).getCountry_Name());

            holder.check.setChecked(mainDataList.get(position).isChecked());

            return view;
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            mainDataList.clear();
            if (charText.length() == 0) {
                mainDataList.addAll(arrList);
            } else {
                for (CountryData wp : arrList) {
                    if (wp.getCountry_Name().toLowerCase(Locale.getDefault())
                            .contains(charText)) {
                        mainDataList.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }

    @Override
    public void onBackPressed() {

        if (dataList != null) {
            StringBuffer sb = new StringBuffer();

            String seperator = "";

            for (CountryData bean : dataList) {

                if (bean.isChecked()) {
                    sb.append(seperator);
                    seperator = ",";
                    sb.append(bean.getCountry_Id());

                }
            }

            String s = sb.toString().trim();

            if (TextUtils.isEmpty(s)) {
                GlobalClaass.savePrefrencesfor(context, PreferenceConnector.TYPEARRAY, "");
            } else {
                GlobalClaass.savePrefrencesfor(context, PreferenceConnector.TYPEARRAY, s);
            }
        } else {
            GlobalClaass.savePrefrencesfor(context, PreferenceConnector.TYPEARRAY, "");
        }


        finish();


    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_up, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, R.anim.slide_down);
    }

}
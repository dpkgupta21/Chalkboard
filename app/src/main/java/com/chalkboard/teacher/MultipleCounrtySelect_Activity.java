package com.chalkboard.teacher;

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

import com.chalkboard.CountryData;
import com.chalkboard.GlobalClaass;
import com.chalkboard.R;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

public class MultipleCounrtySelect_Activity extends Activity {

    Activity context;
    CountryList countrylist = null;
    Button btn_country;
    ListView country_list;

    ArrayList<CountryData> array_country_list;
    String get_Ename = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.country_dialog);

        context = this;


        array_country_list = new ArrayList<CountryData>();

        country_list = (ListView) findViewById(R.id.country_list);
        btn_country = (Button) findViewById(R.id.btn_country);
        btn_country.setVisibility(View.VISIBLE);
        btn_country.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuffer sb = new StringBuffer();
                StringBuffer sb1 = new StringBuffer();

                String seperator = "";

                int count = 0;

                for (CountryData bean : array_country_list) {

                    if (bean.isChecked()) {
                        sb.append(seperator);
                        sb1.append(seperator);
                        seperator = ",";
                        sb.append(bean.getCountry_Id());
                        sb1.append(bean.getCountry_Name());
                        count++;
                    }
                }

                if (count > 5) {

                    GlobalClaass.showToastMessage(context, "Select up to 5 countries.");

                } else {
                    String s = sb.toString().trim();
                    String s1 = sb1.toString().trim();

                    if (TextUtils.isEmpty(s)) {

                    } else {

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("name", s1);
                        returnIntent.putExtra("id", s);
                        setResult(RESULT_OK, returnIntent);
                        finish();

                    }
                }


            }
        });

        if (GlobalClaass.isInternetPresent(context)) {

            countrylist = new CountryList(context);
            countrylist.execute();

        } else {
            GlobalClaass.showToastMessage(context, "Please check internet connection");
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

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                nameValuePairs.add(new BasicNameValuePair("action", "countries"));

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

                        array_country_list.add(country);

                    }
                }
            } catch (Exception e) {
            }

            country_list.setAdapter(new MyAdap(array_country_list));

            GlobalClaass.hideProgressBar(context);

        }
    }


    class MyAdap extends BaseAdapter implements CompoundButton.OnCheckedChangeListener{

        ArrayList<CountryData> event_note_data;
        Typeface font;

        public MyAdap(ArrayList<CountryData> note_data_list) {
            // TODO Auto-generated constructor stub

            event_note_data = note_data_list;
            font = Typeface.createFromAsset(context.getAssets(), "fonts/mark.ttf");

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
                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.item_country_list, null);

                holder.country_name = (TextView) convertView.findViewById(R.id.country_name);
                holder.mcheck = (CheckBox) convertView.findViewById(R.id.check);

                try {

                    holder.country_name.setTypeface(font);
                } catch (Exception e) {

                }

                convertView.setTag(holder);
                convertView.setTag(R.id.country_name, holder.country_name);
                convertView.setTag(R.id.check, holder.mcheck);



            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mcheck.setTag(position);
            holder.mcheck.setChecked(event_note_data.get(position).isChecked());
            holder.mcheck.setOnCheckedChangeListener(this);
            /*if(get_Ename != null){
				
				if(get_Ename.equalsIgnoreCase("Country")){
					
					holder.mcheck.setVisibility(View.GONE);
				}
				else {
					holder.mcheck.setVisibility(View.VISIBLE);
				}
				
			}*/

            holder.country_name.setText(event_note_data.get(position).getCountry_Name());
			
			/*convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String country_name = event_note_data.get(position).getCountry_Name();
					String country_id = event_note_data.get(position).getCountry_Id();
					Intent returnIntent = new Intent();
					returnIntent.putExtra("name",country_name);
					returnIntent.putExtra("id",country_id);
					setResult(RESULT_OK,returnIntent);
					finish();
				}
			});*/


            return convertView;

        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int tagPosition = Integer.parseInt(buttonView.getTag().toString());
            event_note_data.get(tagPosition).setChecked(isChecked);
            notifyDataSetChanged();
        }
    }


}

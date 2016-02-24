package com.chalkboard.teacher.matchrequest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chalkboard.GlobalClaass;
import com.chalkboard.R;
import com.chalkboard.customviews.CustomProgressDialog;
import com.chalkboard.model.MatchSentDTO;
import com.chalkboard.utility.Utils;
import com.chalkboard.webservice.WebserviceConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.volley.ApplicationController;
import com.volley.CustomJsonRequest;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DeepakGupta on 2/21/16.
 */
public class MatchSentDetailActivity extends Activity {

    private static final String TAG = "MatchSentDetailActivity";
    private MatchSentDTO sentDetailDTO;
    private DisplayImageOptions options;
    private Activity mActivity;
    private ImageView imgFavIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_match_sent_detail);
        mActivity = this;

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .showImageOnLoading(R.drawable.unactive_circle)
                .showImageOnFail(R.drawable.unactive_circle)
                .showImageForEmptyUri(R.drawable.unactive_circle)
                .build();
        sentDetailDTO = (MatchSentDTO) getIntent().getSerializableExtra("sentDetail");

        setUIValues(sentDetailDTO);
        ImageView img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(imgBackClickListener);

    }

    private void setUIValues(MatchSentDTO sentDetailDTO) {
        ImageView circleImage = (ImageView) findViewById(R.id.circle_img);
        ImageLoader.getInstance().displayImage(sentDetailDTO.getImage(), circleImage,
                options);
        ImageView imgFavIcon = (ImageView) findViewById(R.id.img_fav_icon);
        if (sentDetailDTO.is_favorite()) {
            imgFavIcon.setImageResource(R.drawable.like_icon);
        } else {
            imgFavIcon.setImageResource(R.drawable.unlike_icon);
        }

        ((TextView) findViewById(R.id.txt_job_location)).setText(sentDetailDTO.getCity() +
                ", " + sentDetailDTO.getCountry());
        ((TextView) findViewById(R.id.txt_job_title)).setText(sentDetailDTO.getTitle());
        ((TextView) findViewById(R.id.txt_job_description)).setText(sentDetailDTO.getDescription());
        ((TextView) findViewById(R.id.txt_salary_val)).setText(sentDetailDTO.getSalary());
        ((TextView) findViewById(R.id.txt_start_date_val)).setText(sentDetailDTO.getStart_date());
        ((TextView) findViewById(R.id.txt_about_company_val)).setText(sentDetailDTO.getRecruiter().getAbout());
        ((ImageView) findViewById(R.id.img_location_icon)).setOnClickListener(locationClickListener);
        imgFavIcon = (ImageView) findViewById(R.id.img_fav_icon);
        imgFavIcon.setOnClickListener(favClickListener);


    }

    private View.OnClickListener locationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String map = "http://maps.google.co.in/maps?q=" +
                    sentDetailDTO.getCity() + ", " + sentDetailDTO.getCountry();

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
            startActivity(intent);
        }
    };

    private View.OnClickListener favClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {

            if (sentDetailDTO.is_favorite()) {

                removeFavorites();
            } else {
               addToFavorites();
            }

        }
    };

    private void removeFavorites() {
        if (Utils.isOnline(mActivity)) {
            Map<String, String> params = new HashMap<>();
            params.put("action", WebserviceConstant.FAVORITE_REMOVE_LIST);
            params.put("user_id", GlobalClaass.getUserId(mActivity));
            params.put("job_id", sentDetailDTO.getId());

            //params.put("user_id", "2");

            CustomProgressDialog.showProgDialog(mActivity, null);
            CustomJsonRequest postReq = new CustomJsonRequest(Request.Method.POST,
                    WebserviceConstant.SERVICE_BASE_URL, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if (Utils.getWebServiceStatus(response)) {
                                    imgFavIcon.setImageResource(R.drawable.unlike_icon);
                                    CustomProgressDialog.hideProgressDialog();
                                }

                            } catch (Exception e) {
                                CustomProgressDialog.hideProgressDialog();
                                e.printStackTrace();
                            }
                            CustomProgressDialog.hideProgressDialog();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    CustomProgressDialog.hideProgressDialog();
                    Utils.showExceptionDialog(mActivity);
                    //       CustomProgressDialog.hideProgressDialog();
                }
            });
            ApplicationController.getInstance().getRequestQueue().add(postReq);
            postReq.setRetryPolicy(new DefaultRetryPolicy(
                    30000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            CustomProgressDialog.showProgDialog(mActivity, null);
        } else {
            Utils.showNoNetworkDialog(mActivity);
        }
    }


    private void addToFavorites() {
        if (Utils.isOnline(mActivity)) {
            Map<String, String> params = new HashMap<>();
            params.put("action", WebserviceConstant.FAVORITE_ADD_LIST);
            params.put("user_id", GlobalClaass.getUserId(mActivity));
            params.put("job_id", sentDetailDTO.getId());


            CustomProgressDialog.showProgDialog(mActivity, null);
            CustomJsonRequest postReq = new CustomJsonRequest(Request.Method.POST,
                    WebserviceConstant.SERVICE_BASE_URL, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if (Utils.getWebServiceStatus(response)) {
                                    imgFavIcon.setImageResource(R.drawable.like_icon);
                                    CustomProgressDialog.hideProgressDialog();
                                }

                            } catch (Exception e) {
                                CustomProgressDialog.hideProgressDialog();
                                e.printStackTrace();
                            }
                            CustomProgressDialog.hideProgressDialog();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    CustomProgressDialog.hideProgressDialog();
                    Utils.showExceptionDialog(mActivity);
                    //       CustomProgressDialog.hideProgressDialog();
                }
            });
            ApplicationController.getInstance().getRequestQueue().add(postReq);
            postReq.setRetryPolicy(new DefaultRetryPolicy(
                    30000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            CustomProgressDialog.showProgDialog(mActivity, null);
        } else {
            Utils.showNoNetworkDialog(mActivity);
        }
    }

    private View.OnClickListener imgBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();

        }
    };
}

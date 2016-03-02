package com.chalkboard.menucount;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chalkboard.GlobalClaass;
import com.chalkboard.model.MenuCountDTO;
import com.chalkboard.utility.Utils;
import com.chalkboard.webservice.WebserviceConstant;
import com.google.gson.Gson;
import com.volley.ApplicationController;
import com.volley.CustomJsonRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by deepak.gupta on 07-09-2015.
 */
public class MenuCountHandler implements Runnable {

    private static final String TAG = "MenuCountHandler";
    public static final int MENU_COUNT_HANDLER = 1001;
    private Handler handler;
    private Activity mActivity;

    public MenuCountHandler(Handler handler, Activity mActivity) {
        this.handler = handler;
        this.mActivity = mActivity;
    }

    @Override
    public void run() {
        getMenuCount();
    }

    private void getMenuCount() {

        if (Utils.isOnline(mActivity)) {
            Map<String, String> params = new HashMap<>();
            params.put("action", WebserviceConstant.USER_STATISTICS);
            params.put("user_id", GlobalClaass.getUserId(mActivity));

            //CustomProgressDialog.showProgDialog(mActivity, null);
            CustomJsonRequest postReq = new CustomJsonRequest(Request.Method.POST,
                    GlobalClaass.Webservice_Url, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //CustomProgressDialog.hideProgressDialog();
                                Utils.ShowLog(TAG, "got Menu count response = " + response.toString());
                                MenuCountDTO menuDTO = new Gson().
                                        fromJson(response.toString(), MenuCountDTO.class);
                                handleMenuCountResponse(menuDTO);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // CustomProgressDialog.hideProgressDialog();
                    Utils.showExceptionDialog(mActivity);
                    //setUpMenu();
                    //       CustomProgressDialog.hideProgressDialog();
                }
            });
            ApplicationController.getInstance().getRequestQueue().add(postReq);
            postReq.setRetryPolicy(new DefaultRetryPolicy(
                    30000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        } else {

        }


    }

    private void handleMenuCountResponse(MenuCountDTO menuDTO) {
        Utils.ShowLog(TAG, "handleMenuCountResponse");
        Message msg = handler.obtainMessage(MENU_COUNT_HANDLER, menuDTO);
        handler.sendMessage(msg);

    }

//
}

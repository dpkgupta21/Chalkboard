package com.chalkboard.recruiter.matchrequest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.chalkboard.GlobalClaass;
import com.chalkboard.R;
import com.chalkboard.customviews.CustomAlert;
import com.chalkboard.customviews.CustomProgressDialog;
import com.chalkboard.model.RecruiterMatchReceivedDTO;
import com.chalkboard.recruiter.matchrequest.adapter.RecruiterReceivedAdapter;
import com.chalkboard.teacher.TeacherChatBoardActivity;
import com.chalkboard.utility.Utils;
import com.chalkboard.webservice.WebserviceConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.volley.ApplicationController;
import com.volley.CustomJsonRequest;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecruiterReceivedFragment extends Fragment implements SwipeMenuListView.OnMenuItemClickListener {


    private View view;
    private String TAG = "Alert Screen";

    private Activity mActivity;
    //private Toolbar mToolbar;
    private SwipeMenuListView listviewReceived;
    private List<RecruiterMatchReceivedDTO> recruiterMatchReceivedDTOList;
    private RecruiterReceivedAdapter receivedAdapter;


    public RecruiterReceivedFragment() {
    }


    public static RecruiterReceivedFragment newInstance() {
        RecruiterReceivedFragment fragment = new RecruiterReceivedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_received, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        listviewReceived = (SwipeMenuListView) view.findViewById(R.id.listview_received);
    }

    @Override
    public void onResume() {
        super.onResume();
        getMatchRequestList();
    }

    private void getMatchRequestList() {

        if (Utils.isOnline(getActivity())) {
            Map<String, String> params = new HashMap<>();
            params.put("action", WebserviceConstant.RECRUITER_RECEIVED_REQUEST);
            params.put("user_id", GlobalClaass.getUserId(getActivity()));
            //params.put("user_id", "2");

            CustomProgressDialog.showProgDialog(getActivity(), null);
            CustomJsonRequest postReq = new CustomJsonRequest(Request.Method.POST, WebserviceConstant.SERVICE_BASE_URL, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (Utils.getWebServiceStatus(response)) {
                                    Utils.ShowLog(TAG, "got some response = " + response.toString());
                                    Type type = new TypeToken<ArrayList<RecruiterMatchReceivedDTO>>() {
                                    }.getType();
                                    recruiterMatchReceivedDTOList = new Gson().
                                            fromJson(response.getJSONArray("data").toString(), type);
                                    setReceivedValues();
                                } else {
                                    recruiterMatchReceivedDTOList = null;
                                    setReceivedValues();
                                }

                            } catch (Exception e) {
                                CustomProgressDialog.hideProgressDialog();
                                setReceivedValues();
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

    private void setReceivedValues() {


        if (recruiterMatchReceivedDTOList != null && recruiterMatchReceivedDTOList.size() > 0) {
            listviewReceived.setVisibility(View.VISIBLE);
            setViewVisibility(R.id.tv_no_received, view, View.GONE);
            receivedAdapter = new RecruiterReceivedAdapter(getActivity(), recruiterMatchReceivedDTOList);
            createSwipeMenu();
            listviewReceived.setAdapter(receivedAdapter);
            listviewReceived.setOnMenuItemClickListener(this);
        } else {
            listviewReceived.setVisibility(View.GONE);
            setViewVisibility(R.id.tv_no_received, view, View.VISIBLE);
        }

    }


    public void setViewVisibility(int id, View view, int flag) {
        View v = view.findViewById(id);
        v.setVisibility(flag);
    }


    private void createSwipeMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                switch (menu.getViewType()) {
                    case 0:
                        // create "check" item
                        SwipeMenuItem checkItem = new SwipeMenuItem(
                                getActivity());
                        // set item background
                        checkItem.setBackground(R.drawable.accept_green);
                        // set item width
                        checkItem.setWidth(150);
                        // set a icon
                        checkItem.setIcon(R.drawable.accept_icon_text);
                        // add to menu
                        menu.addMenuItem(checkItem);


                        // create "check" item
                        SwipeMenuItem crossItem = new SwipeMenuItem(
                                getActivity());
                        // set item background
                        crossItem.setBackground(R.drawable.orange_decline);
                        // set item width
                        crossItem.setWidth(150);
                        // set a icon
                        crossItem.setIcon(R.drawable.orange_close_icon);
                        // add to menu
                        menu.addMenuItem(crossItem);
                        break;
                    case 1:
                        // create "delete" item
                        SwipeMenuItem deleteItem = new SwipeMenuItem(
                                getActivity());
                        // set item background
                        deleteItem.setBackground(R.drawable.delete_back);
                        // set item width
                        deleteItem.setWidth(150);
                        // set a icon
                        deleteItem.setIcon(R.drawable.circle_delete_icon);
                        // add to menu
                        menu.addMenuItem(deleteItem);
                        break;


                }
            }
        };

// set creator
        listviewReceived.setMenuCreator(creator);


    }

    private int convert_dp_to_px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

        switch (menu.getViewType()) {
            case 1:
                Toast.makeText(getActivity(), "Delete Clicked", Toast.LENGTH_SHORT).show();
                break;
            case 0:

                switch (index) {
                    case 0:
                        //Toast.makeText(getActivity(), "Check Clicked", Toast.LENGTH_SHORT).show();

                        doReceivedAction(position, 1);
                        break;
                    case 1:
                        //Toast.makeText(getActivity(), "Cross Clicked", Toast.LENGTH_SHORT).show();

                        doReceivedAction(position, 0);
                        break;
                }
                break;
        }

        return false;
    }


    private void doReceivedAction(final int position, final int status) {
        if (Utils.isOnline(getActivity())) {
            Map<String, String> params = new HashMap<>();
            if (status == 1) {
                params.put("action", WebserviceConstant.RECRUITER_ACCEPT_RECEIVED_REQUEST);
            } else {
                params.put("action", WebserviceConstant.RECRUITER_REJECT_RECEIVED_REQUEST);
            }

            params.put("match_id", recruiterMatchReceivedDTOList.get(position).getMatch_id());


            CustomProgressDialog.showProgDialog(getActivity(), null);
            CustomJsonRequest postReq = new CustomJsonRequest(Request.Method.POST,
                    WebserviceConstant.SERVICE_BASE_URL, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Utils.ShowLog(TAG, "got some response = " + response.toString());
                                if (Utils.getWebServiceStatus(response)) {
                                    CustomProgressDialog.hideProgressDialog();
                                    // call send message activity
                                    if (response.getString("data").
                                            equalsIgnoreCase("Successfully accepted.")) {

                                        //getMatchRequestList();

                                        Intent intent = new Intent(mActivity,
                                                TeacherChatBoardActivity.class);
                                        intent.putExtra("id", recruiterMatchReceivedDTOList.get(position).getId());
                                        intent.putExtra("name",
                                                recruiterMatchReceivedDTOList.get(position).getName());
                                        intent.putExtra("isAfterMatch",true);
                                        startActivity(intent);
//                                        startActivity(new Intent(getActivity().getApplicationContext(),
//                                                TeacherChatBoardActivity.class).putExtra("id",
//                                                recruiterMatchReceivedDTOList.get(position).getRecruiter_id()).
//                                                putExtra("name",
//                                                        recruiterMatchReceivedDTOList.get(position).getName()));

                                    } else if (response.getString("data").
                                            equalsIgnoreCase("Successfully removed.")) {

                                        showAfterMatchRejectDialog();
                                    }

                                } else {
                                    CustomProgressDialog.hideProgressDialog();
                                    Utils.showDialog(getActivity(), "Error", Utils.getWebServiceMessage(response));
                                }
                            } catch (Exception e) {
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

    private void showAfterMatchRejectDialog() {
        String message = "Some recruiters have multiple jobs that aren't shown in their listing. Are you "
                + "sure you want to dismiss this one?";
        new CustomAlert(mActivity, RecruiterReceivedFragment.this).doubleButtonAlertDialog(
                message,
                getString(R.string.cancel),
                getString(R.string.dismiss), "dblBtnCallbackResponse", 1000);
        ;
    }

    public void dblBtnCallbackResponse(Boolean flag, int code) {
        if (flag) {
            getMatchRequestList();
        }

    }


//    private void showLogOutDialog() {
//        new CustomAlert(getActivity())
//                .singleButtonAlertDialog(
//                        "Message",
//                        "Ok",
//                        "dblBtnCallbackResponse", 1000);
//    }
//
//    public void dblBtnCallbackResponse(Boolean flag, int code) {
//        if (flag) {
//
//        }
//
//    }
}
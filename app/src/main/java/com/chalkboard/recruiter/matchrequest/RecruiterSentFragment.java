package com.chalkboard.recruiter.matchrequest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chalkboard.GlobalClaass;
import com.chalkboard.R;
import com.chalkboard.customviews.CustomProgressDialog;
import com.chalkboard.model.MatchSentDTO;
import com.chalkboard.recruiter.matchrequest.adapter.RecruiterSentAdapter;
import com.chalkboard.utility.MyOnClickListener;
import com.chalkboard.utility.RecyclerTouchListener;
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


public class RecruiterSentFragment extends Fragment {


    private View view;
    private String TAG = "Alert Screen";
    //private Activity mActivity;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<MatchSentDTO> matchSentDTOList;


    public RecruiterSentFragment() {
    }

    public static RecruiterSentFragment newInstance() {
        RecruiterSentFragment fragment = new RecruiterSentFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sent, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_sent);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);

        getMatchSentRequestList();

    }

    private void getMatchSentRequestList() {

        if (Utils.isOnline(getActivity())) {
            Map<String, String> params = new HashMap<>();
            params.put("action", WebserviceConstant.TEACHER_SENT_REQUEST);
            params.put("user_id", GlobalClaass.getUserId(getActivity()));
            //params.put("user_id", "2");

            CustomProgressDialog.showProgDialog(getActivity(), null);
            CustomJsonRequest postReq = new CustomJsonRequest(Request.Method.POST,
                    WebserviceConstant.SERVICE_BASE_URL, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                Utils.ShowLog(TAG, "got some response = " + response.toString());
                                Type type = new TypeToken<ArrayList<MatchSentDTO>>() {
                                }.getType();
                                matchSentDTOList = new Gson().
                                        fromJson(response.getJSONArray("data").
                                                toString(), type);
                                setSentValues(matchSentDTOList);

                            } catch (Exception e) {
                                CustomProgressDialog.hideProgressDialog();
                                setSentValues(matchSentDTOList);
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

    private void setSentValues(final List<MatchSentDTO> matchSentDTOList) {


        if (matchSentDTOList != null && matchSentDTOList.size() > 0) {
            setViewVisibility(R.id.tv_no_sent, view, View.GONE);
            mAdapter = new RecruiterSentAdapter(getActivity(), matchSentDTOList);
            recyclerView.setAdapter(mAdapter);

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),
                    recyclerView, new MyOnClickListener() {
                @Override
                public void onRecyclerClick(View view, int position) {
                    Intent intent = new Intent(getActivity(),
                            RecruiterMatchSentDetailActivity.class);
                    intent.putExtra("sentDetail", matchSentDTOList.get(position));
                    startActivity(intent);
                }

                @Override
                public void onRecyclerLongClick(View view, int position) {

                }

                @Override
                public void onItemClick(View view, int position) {

                }
            }));

        } else {
            recyclerView.setVisibility(View.GONE);
            setViewVisibility(R.id.tv_no_sent, view, View.VISIBLE);
        }

    }

    public void setViewVisibility(int id, View view, int flag) {
        View v = view.findViewById(id);
        v.setVisibility(flag);
    }

}
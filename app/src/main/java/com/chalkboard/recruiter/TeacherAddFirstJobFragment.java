package com.chalkboard.recruiter;

import com.chalkboard.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class TeacherAddFirstJobFragment extends Fragment{

	View rootView;
	Activity context;
	Typeface font,font2;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {


		context = getActivity();
		font=Typeface.createFromAsset(context.getAssets(), "mark.ttf");
		font2=Typeface.createFromAsset(context.getAssets(), "marlbold.ttf");
 
		rootView = inflater.inflate(R.layout.fragment_add_first_job, container, false);

		try {
			
			((TextView)rootView. findViewById(R.id.btn_create_new_job)).setTypeface(font);
			
		} catch (Exception e) {

		}
		
		rootView.findViewById(R.id.btn_create_new_job).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				
				startActivity(new Intent(context, Teacher_Create_New_Job.class));
				
			}
		});
		

		return rootView;
	}
	
}

package com.chalkboard;


import com.chalkboard.recruiter.TeachersListActivity;
import com.chalkboard.teacher.JobListActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;


public class SplashActivity extends Activity {

	Activity context = null;
//AIzaSyDMUvXxFMeXsXxUEyV2MlWtVdo7sb44-h8
	//1087365411930
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context = this;

		setContentView(R.layout.activity_splash);

		Thread thread = new Thread() {
			@Override
			public void run() {
				synchronized (this) {

					try {
						wait(2000);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						
						String id = GlobalClaass.getUserId(context);

						if (id == null) {
							startActivity(new Intent(context,LandingPage_Activity.class));
							GlobalClaass.activitySlideForwardAnimation(context);
							finish();
						} else {

							String role = GlobalClaass.getROLE(context);
							
							if (role.equalsIgnoreCase("teacher")) {
								startActivity(new Intent(context,JobListActivity.class));
								
							}else{
								startActivity(new Intent(context,TeachersListActivity.class));
								
							}
							GlobalClaass.activitySlideForwardAnimation(context);
							finish();


						}
						
					}

				}
			}
		};
		thread.start();

	}

	@Override
	public void onBackPressed() {

	}

}

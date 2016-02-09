package com.chalkboard;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class LandingPage_Activity extends Activity{

	Button btn_login,btn_singup;
	Activity context;
	Typeface font,font2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_landingpage);

		context = this;
		font=Typeface.createFromAsset(getAssets(), "mark.ttf");
		font2=Typeface.createFromAsset(getAssets(), "marlbold.ttf");

		btn_login = (Button) findViewById(R.id.btn_login);
		btn_singup = (Button) findViewById(R.id.btn_signup);
		
		try {
			btn_login.setTypeface(font);
			btn_singup.setTypeface(font);
			
			((TextView) findViewById(R.id.textView3)).setTypeface(font);
			((TextView) findViewById(R.id.textView2)).setTypeface(font);
			
		} catch (Exception e) {

		}

		btn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(context, Login_Activity.class));
				GlobalClaass.activitySlideForwardAnimation(context);
				finish();
				
			}
		});

		btn_singup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(context, SignUp_Activity.class));
				GlobalClaass.activitySlideForwardAnimation(context);
				finish();
			}
		});

	}
}

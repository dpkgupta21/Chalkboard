package com.chalkboard.recruiter;

import com.chalkboard.ImageLoader;
import com.chalkboard.R;
import com.chalkboard.teacher.JobObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


public class MyJobPreviewActivity extends Activity{

	Activity context;
	JobObject jobObject;
	TextView salary,strat_date,about_company;
	ImageView job_image, job_icon,imageView1;
	public ImageLoader imageloader = null;



	Typeface font,font2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_job_privew);

		context = this;
		imageloader = new ImageLoader(context);

		font = Typeface.createFromAsset(context.getAssets(), "mark.ttf");
		font2=Typeface.createFromAsset(getAssets(), "marlbold.ttf");

		about_company = (TextView)findViewById(R.id.about_company);
		salary = (TextView)findViewById(R.id.salary);
		strat_date = (TextView)findViewById(R.id.start_date);
		job_image = (ImageView)findViewById(R.id.job_image);	
		imageView1 = (ImageView)findViewById(R.id.imageView1);
		job_icon = (ImageView)findViewById(R.id.job_icon);	

		jobObject = (JobObject) getIntent().getSerializableExtra("data");

		if(jobObject.getJobDescription() != null){
			about_company.setText(jobObject.getJobDescription());
		}
		if(jobObject.getJobSalary() != null){
			salary.setText(jobObject.getJobSalary());
		}
		if(jobObject.getJobDate() != null){
			strat_date.setText(jobObject.getJobDate());
		}

		try {

			about_company.setTypeface(font);
			salary.setTypeface(font);
			strat_date.setTypeface(font);

			((TextView)findViewById(R.id.job_description)).setTypeface(font);


			((TextView)findViewById(R.id.edit_job)).setTypeface(font2);
			((TextView)findViewById(R.id.remove_publish_job)).setTypeface(font2);
			((TextView)findViewById(R.id.txt_heading)).setTypeface(font2);
			((TextView)findViewById(R.id.about_heading)).setTypeface(font2);
			((TextView)findViewById(R.id.job_name)).setTypeface(font2);
			((TextView)findViewById(R.id.salary_text)).setTypeface(font2);
			((TextView)findViewById(R.id.date_text)).setTypeface(font2);

		} catch (Exception e) {

		}

		if(jobObject.getJobImage() != null){

			Bitmap bitmap = BitmapFactory.decodeFile(jobObject.getJobImage());
			if(bitmap != null){
				job_image.setImageBitmap(bitmap);
				job_icon.setImageBitmap(getCircleBitmap(bitmap));
			}
		}



		findViewById(R.id.edit_job).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.remove_publish_job).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context,
						Teacher_Create_New_Job.class));
				finish();
			}
		});

		imageView1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	public static Bitmap getCircleBitmap(Bitmap bitmap) {

		// crop to circle
		Bitmap output;
		// check if its a rectangular image
		if (bitmap.getWidth() > bitmap.getHeight()) {
			output = Bitmap.createBitmap(bitmap.getHeight(),
					bitmap.getHeight(), Config.ARGB_8888);
		} else {
			output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(),
					Config.ARGB_8888);
		}
		Canvas canvas = new Canvas(output);

		float r = 0;

		if (bitmap.getWidth() > bitmap.getHeight()) {
			r = bitmap.getHeight() / 2;
		} else {
			r = bitmap.getWidth() / 2;
		}

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);

		canvas.drawCircle(r, r, r, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

}
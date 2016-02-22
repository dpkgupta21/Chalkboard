package com.chalkboard.recruiter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.chalkboard.GPSTracker;
import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader11;
import com.chalkboard.R;
import com.chalkboard.recruiter.navigationdrawer.TeachersListActivity;
import com.chalkboard.teacher.CountryList_Activity;
import com.chalkboard.teacher.JobObject;
import com.chalkboard.teacher.JobTypeListActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class EditJobActivity extends Activity {

	Activity context;
	EditText create_job_title, create_company_name, create_country_name,
	create_city_name, create_requirement, create_location,
	create_job_type, create_srart_date, create_job_description,
	create_salary;
	TextView create_citizen_required,top_header_count;
	Button create_btn_draft,  create_btn_postjob;
	Switch citizenship_swich;
	RelativeLayout image_layout;
	ImageView btn_back,teacher_photo;

	protected static final int RESULT_CAMERA = 5;
	protected static final int RESULT_LIBRARY = 6;
	String selectedImagePath = "";
	String str = "", ext = "", EncodeImage = "";
	long filebyte;

	static Dialog d;
	Dialog dialog;
	private int year;
	private int month;
	private int day;
	static final int DATE_PICKER_ID = 1111;

	double lati, longi, hlati, hlongi;
	GPSTracker gps;
	String Lat_st, Lon_st1;
	String CountryName = "";
	String Countryaddress = "",is_draft = "",eu_citizen_required = "1";
	private GoogleMap googleMap;

	LoadService loadservice = null;
	JobPosting jobposting = null;

	Typeface font,font2;
	public ImageLoader11 imageloader11 = null;
	JobObject jobObjectfromback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.editjob);


		jobObjectfromback = (JobObject) getIntent().getSerializableExtra("Data");


		context = this;
		font=Typeface.createFromAsset(getAssets(), "fonts/mark.ttf");
		font2=Typeface.createFromAsset(getAssets(), "fonts/marlbold.ttf");
		imageloader11 = new ImageLoader11(context);

		final Calendar c = Calendar.getInstance();
		year  = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day   = c.get(Calendar.DAY_OF_MONTH);

		create_job_title = (EditText) findViewById(R.id.create_job_title);
		create_company_name = (EditText) findViewById(R.id.create_company_name);
		create_country_name = (EditText) findViewById(R.id.create_country_name);
		create_city_name = (EditText) findViewById(R.id.create_city_name);
		create_requirement = (EditText) findViewById(R.id.create_requirement);
		create_location = (EditText) findViewById(R.id.create_location);
		create_job_type = (EditText) findViewById(R.id.create_job_type);
		create_srart_date = (EditText) findViewById(R.id.create_srart_date);
		create_job_description = (EditText) findViewById(R.id.create_job_description);
		create_salary = (EditText) findViewById(R.id.create_salary);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		create_citizen_required = (TextView) findViewById(R.id.create_citizen_required);
		create_btn_draft = (Button) findViewById(R.id.create_btn_draft);

		create_btn_postjob = (Button) findViewById(R.id.create_btn_postjob);
		citizenship_swich = (Switch) findViewById(R.id.citizenship_swich);
		image_layout = (RelativeLayout) findViewById(R.id.image_layout);
		teacher_photo = (ImageView)findViewById(R.id.teacher_photo);
		top_header_count = (TextView)findViewById(R.id.top_header_count);

		try {
			create_job_title.setTypeface(font);
			create_company_name.setTypeface(font);
			create_country_name.setTypeface(font);
			create_city_name.setTypeface(font);
			create_requirement.setTypeface(font);
			create_location.setTypeface(font);
			create_job_type.setTypeface(font);
			create_srart_date.setTypeface(font);
			create_job_description.setTypeface(font);
			create_salary.setTypeface(font);
			top_header_count.setTypeface(font);

			create_btn_draft.setTypeface(font);
			create_btn_postjob.setTypeface(font);

			create_citizen_required.setTypeface(font);

			((TextView) findViewById(R.id.createheader)).setTypeface(font2);

		} catch (Exception e) {

		}

		String count= GlobalClaass.getHeader_Count(context);

		if(count != null){

			if(count.length() == 1){
				top_header_count.setText("  "+count);
			}if(count.length() == 2){
				top_header_count.setText(" "+count);
			}
			else {
				top_header_count.setText(count);
			}
		}else {
			top_header_count.setVisibility(View.GONE);
		}



		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				GlobalClaass.activitySlideBackAnimation(context);
				finish();
			}
		});

		teacher_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				selectImageMethod();
			}



		});

		citizenship_swich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					eu_citizen_required = "1";
				} else {
					// The toggle is disabled
					eu_citizen_required = "0";
				}
			}
		});

		create_btn_postjob.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				is_draft = "1";
				ValidateValue();

			}
		});

		create_btn_draft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				is_draft = "0";
				ValidateValue();

			}
		});

		if (com.chalkboard.GlobalClaass.isInternetPresent(context)) {

			try {
				gps = new GPSTracker(context);

				// check if GPS enabled
				if (gps.canGetLocation()) {

					double latitude = gps.getLatitude();
					double longitude = gps.getLongitude();

					lati = latitude;
					longi = longitude;

					loadservice = new LoadService(context,
							String.valueOf(lati), String.valueOf(longi));
					loadservice.execute();

				}else{

					loadservice = new LoadService(context,
							"0", "0");
					loadservice.execute();
				}

			} catch (Exception e) {

			}
		} else {
			com.chalkboard.GlobalClaass.showToastMessage(context,
					"Please check internet connection.");
		}

		create_srart_date.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				showDialog(DATE_PICKER_ID);
			}
		});

		create_job_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(context, JobTypeListActivity.class);
				startActivityForResult(i, 2);
			}
		});

		create_country_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(context, CountryList_Activity.class);
				startActivityForResult(i, 1);
			}
		});

		try {
			create_job_title.setText(jobObjectfromback.getJobTitle());
			create_company_name.setText(jobObjectfromback.getJobRecruiterName());
			create_country_name.setText(jobObjectfromback.getJobCountry());
			create_city_name.setText(jobObjectfromback.getJobCity());
			create_location.setText(jobObjectfromback.getJobLocation());
			create_job_type.setText("Full Time");
			create_srart_date.setText(jobObjectfromback.getJobStartdate());
			create_job_description.setText(jobObjectfromback.getJobDescription());	
			create_salary.setText(jobObjectfromback.getJobSalary());
			create_requirement.setText(jobObjectfromback.getJobRecruiterAbout());

			imageloader11.DisplayImage(jobObjectfromback.getJobImage(),
					teacher_photo);

			is_draft = jobObjectfromback.isIs_draft()+"";

		} catch (Exception e) {

		}

	}


	private String gettxt(String num) {
		// TODO Auto-generated method stub
		String str="Full Time";
		if(num.equals("1"))
			str = "Volunteer";
		else if(num.equals("2")){
			str ="Part Time";
		}
		else if(num.equals("4")){
			str ="Internship";
		}
		else if(num.equals("5")){
			str ="Freelancer";
		}
		
		return str;
	}


	private void selectImageMethod() {

		Log.i("choose option", "camera or library");

		final CharSequence[] options = { "Camera", "Choose an image" };

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Chalkboard");
		// builder.setCancelable(true);
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (options[item].equals("Camera")) {

					Log.i("Camera Clcik", "camera click");
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					startActivityForResult(intent, RESULT_CAMERA);

				} else if (options[item].equals("Library")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, RESULT_LIBRARY);

				}
			}
		});
		builder.show();
	}


	private void ValidateValue1() {

		if (create_job_title.getText().toString().equals("")) {

			GlobalClaass.showToastMessage(context, "Please enter title.");
			return;
		}

		if (create_company_name.getText().toString().equals("")) {

			GlobalClaass.showToastMessage(context, "Please enter comapny name.");
			return;
		}

		if (create_country_name.getText().toString().equals("")) {

			GlobalClaass.showToastMessage(context, "Please enter country name.");
			return;
		}

		if (create_city_name.getText().toString().equals("")) {

			GlobalClaass.showToastMessage(context, "Please enter city name.");
			return;
		}

		if (create_location.getText().toString().equals("")) {

			GlobalClaass.showToastMessage(context, "Please enter address.");
			return;
		}

		if (create_job_type.getText().toString().equals("")) {

			GlobalClaass.showToastMessage(context, "Please enter job type.");
			return;
		}

		if (create_srart_date.getText().toString().equals("")) {

			GlobalClaass.showToastMessage(context, "Please enter date.");
			return;
		}




		JobObject jobObject = new JobObject();





		jobObject.setJobName(create_job_title.getText().toString());

		jobObject.setJobRecruiterName(create_company_name.getText().toString());

		jobObject.setJobLocation(create_city_name.getText().toString()+","+create_country_name.getText().toString()+","+create_location.getText().toString());

		jobObject.setJobDate(create_srart_date.getText().toString());

		jobObject.setJobDescription(create_job_description.getText().toString());

		jobObject.setJobSalary(create_salary.getText().toString());
		jobObject.setJobImage(selectedImagePath);



		startActivity(new Intent(context,
				MyJobPreviewActivity.class).putExtra("data",jobObject));

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);


		if (GlobalClaass.isInternetPresent(context)) {

			jobposting = new JobPosting(context);
			jobposting.execute();

		}

		else {
			GlobalClaass.showToastMessage(context,"Please check internet connection.");
		}

	}

	private void ValidateValue() {

		if (create_job_title.getText().toString().equals("")) {

			GlobalClaass.showToastMessage(context, "Please enter title.");
			return;
		}

		if (create_company_name.getText().toString().equals("")) {

			GlobalClaass.showToastMessage(context, "Please enter comapny name.");
			return;
		}

		if (create_country_name.getText().toString().equals("")) {

			GlobalClaass.showToastMessage(context, "Please enter country name.");
			return;
		}

		if (create_city_name.getText().toString().equals("")) {

			GlobalClaass.showToastMessage(context, "Please enter city name.");
			return;
		}

		if (create_location.getText().toString().equals("")) {

			GlobalClaass.showToastMessage(context, "Please enter address.");
			return;
		}

		if (create_job_type.getText().toString().equals("")) {

			GlobalClaass.showToastMessage(context, "Please enter job type.");
			return;
		}

		if (create_srart_date.getText().toString().equals("")) {

			GlobalClaass.showToastMessage(context, "Please enter date.");
			return;
		}

		if (GlobalClaass.isInternetPresent(context)) {
			
//			Toast.makeText(context, "b4 jobposting", 3000).show();
			
			jobposting = new JobPosting(context);
			jobposting.execute();

			InputMethodManager inputManager = (InputMethodManager)
					getSystemService(Context.INPUT_METHOD_SERVICE); 

			inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);

		}

		else {
			GlobalClaass.showToastMessage(context,"Please check internet connection.");
		}

	}


	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}


	public Uri getImageUri11(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}

	public String getRealPathFromURI11(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null); 
		cursor.moveToFirst(); 
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
		return cursor.getString(idx); 
	}


	public Uri getImageUri(Context inContext, Bitmap inImage) {

		String root = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES).toString();
		File myDir = new File(root + "/saved_images");
		myDir.mkdirs();
		Random generator = new Random();
		int n = 10000;
		n = generator.nextInt(n);
		String fname = "Image-" + n + ".jpg";
		File file = new File(myDir, fname);
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			inImage.compress(Bitmap.CompressFormat.JPEG, 90, out);

			Log.e("PAth", file.getAbsolutePath());

			Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

			teacher_photo.setImageBitmap(myBitmap);

			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		MediaScannerConnection.scanFile(context, new String[] { file.toString() },
				null, new MediaScannerConnection.OnScanCompletedListener() {
			public void onScanCompleted(String path, Uri uri) {
				Log.i("ExternalStorage", "Scanned " + path + ":");
				Log.i("ExternalStorage", "-> uri=" + uri);
				str = path;
			}
		});

		return Uri.parse(str);
	}


	private String getRealPathFromURI(Uri contentURI) {
		String result;
		Cursor cursor = context. getContentResolver().query(contentURI, null, null,
				null, null);
		if (cursor == null) { // Source is Dropbox or other similar local file
			// path
			result = contentURI.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			result = cursor.getString(idx);
			cursor.close();
		}
		return result;
	}

	public static String getFileExt(String FileName) {
		return FileName.substring((FileName.lastIndexOf(".") + 1),
				FileName.length());
	}

	public long getImageLength(String absFileName) {
		File file = new File(absFileName);
		return file.length();
	}

	public byte[] bitmapToByteArray(Bitmap b) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}



	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {

		case RESULT_CAMERA: {

			if (resultCode == context.RESULT_OK && null != data) {

				try {

					/*Bitmap photo = (Bitmap) data.getExtras().get("data");

					Uri tempUri = getImageUri(context, photo);

					selectedImagePath= getRealPathFromURI(tempUri);

					ext = getFileExt(selectedImagePath);

					filebyte = getImageLength(selectedImagePath);
					int pixel;

					if (photo.getHeight() < photo.getWidth()) {
						pixel = photo.getHeight();
					}else{
						pixel = photo.getWidth();
					}

					teacher_photo.setImageBitmap(photo);

					Log.e("BInary", selectedImagePath);*/

					Bitmap photo = (Bitmap) data.getExtras().get("data");

					// CALL THIS METHOD TO GET THE URI FROM THE BITMAP
					Uri tempUri = getImageUri11(getApplicationContext(), photo);

					selectedImagePath = getRealPathFromURI11(tempUri);

					teacher_photo.setImageBitmap(photo);
				}

				catch (Exception e) {

					e.printStackTrace();

					GlobalClaass.showToastMessage(context, e.toString());

				}

			}

			break;
		}
		case RESULT_LIBRARY: {

			if (resultCode == context.RESULT_OK && null != data) {

				try {

					Uri selectedImage = data.getData();

					String[] filePath = { MediaStore.Images.Media.DATA };

					Cursor c = context.getContentResolver().query(selectedImage,
							filePath, null, null, null);

					c.moveToFirst();

					int columnIndex = c.getColumnIndex(filePath[0]);

					selectedImagePath = c.getString(columnIndex);

					ext = getFileExt(selectedImagePath);

					c.close();

					filebyte = getImageLength(selectedImagePath);

					Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);

					int pixel;

					if (bitmap.getHeight() < bitmap.getWidth()) {
						pixel = bitmap.getHeight();
					}else{
						pixel = bitmap.getWidth();
					}


					teacher_photo.setImageBitmap(bitmap);

					Log.e("BInary", selectedImagePath);
				} catch (Exception e) {
					e.printStackTrace();

				}

			}

			break;
		}

		}



		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				
				create_country_name.setText(data.getStringExtra("name"));
				create_country_name.setTag(data.getStringExtra("id"));
//				Toast.makeText(context, ""+create_country_name.getTag(), 3000).show();
			}

		}else if (requestCode == 2) {
			if (resultCode == RESULT_OK) {

				create_job_type.setText(data.getStringExtra("name"));
				create_job_type.setTag(data.getStringExtra("id"));
//				Toast.makeText(context, ""+create_job_type.getTag(), 3000).show();

			}

		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_PICKER_ID:

			ContextThemeWrapper themedContext = new ContextThemeWrapper(context, android.R.style.Theme_DeviceDefault_Light_Dialog);
			return new DatePickerDialog(themedContext, pickerListener, year, month, day);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {

			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			create_srart_date.setText(new StringBuilder().append(year)
					.append("-").append(month + 1).append("-").append(day));

		}
	};

	public class LoadService extends AsyncTask<String, String, String> {

		String responseString;
		String latitude;
		String longitude;
		Activity context;

		JSONObject jObject1;

		boolean remember;

		public LoadService(Activity ctx, String lat, String lon) {

			context = ctx;
			latitude = lat;
			longitude = lon;

		}

		protected void onPreExecute() {

			GlobalClaass.showProgressBar(context);

		}

		protected String doInBackground(String... params) {

			try {
				// Loading map
				initilizeMap();

			} catch (Exception e) {
				e.printStackTrace();
			}

			return responseString;

		}

		protected void onPostExecute(String responseString) {
			final MarkerOptions marker;
			CameraPosition cameraPosition;
			Countryaddress = getAddress(context, Double.parseDouble(latitude),
					Double.parseDouble(longitude));

			// create marker
			marker = new MarkerOptions().position(
					new LatLng(Double.parseDouble(latitude), Double
							.parseDouble(longitude)));

			cameraPosition = new CameraPosition.Builder()
			.target(new LatLng(Double.parseDouble(latitude), Double
					.parseDouble(longitude))).zoom(12).build();

			googleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
			marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map));
			googleMap.addMarker(marker);




			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double
					.parseDouble(longitude)),
					12));

			googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

				@Override
				public View getInfoWindow(Marker arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public View getInfoContents(Marker arg0) {
					// TODO Auto-generated method stub

					// Getting view from the layout file info_window_layout
					View v =  null;

					// Getting the position from the marker
					LatLng latLng = arg0.getPosition();

					// Getting reference to the TextView to set latitude
					//TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

					// Getting reference to the TextView to set longitude
					// TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);

					String str = getAddress(context, latLng.latitude,
							latLng.longitude);

					return v;
				}
			});

			/*googleMap.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					googleMap.getParent().requestDisallowInterceptTouchEvent(
							true);
					return false;
				}
			});*/

			googleMap.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng arg0) {
					// TODO Auto-generated method stub
					googleMap.clear();

					// Creating an instance of MarkerOptions to set position
					// MarkerOptions markerOptions = new MarkerOptions();

					// Setting position on the MarkerOptions
					marker.position(arg0);

					// Animating to the currently touched position
					googleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0));

					// Adding marker on the GoogleMap
					Marker marker1 = googleMap.addMarker(marker);

					// Showing InfoWindow on the GoogleMap
					marker1.showInfoWindow();
				}
			});

			GlobalClaass.hideProgressBar(context);
		}
	}

	public String getAddress(Context ctx, double latitude, double longitude) {
		String city = "", add = "", postcode = "";
		StringBuilder result = new StringBuilder();
		try {
			Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(latitude,
					longitude, 1);
			if (addresses.size() > 0) {
				Address address = addresses.get(0);

				city = address.getLocality();
				add = address.getSubLocality();
				postcode = address.getPostalCode();

				create_location.setText(add);

				result.append(city);

			}
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}

		return result.toString();
	}

	@SuppressLint("NewApi")
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) context.getFragmentManager()
					.findFragmentById(R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(context, "Sorry! unable to create maps",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		initilizeMap();
	}




	public class JobPosting extends AsyncTask<String, String, String> {

		String responseString;
		String latitude;
		String longitude;
		Activity context;

		JSONObject jObject1;

		boolean remember;
		private String strrrr;

		public JobPosting(Activity ctx) {

			context = ctx;

		}

		protected void onPreExecute() {

			GlobalClaass.showProgressBar(context);

		}

		protected String doInBackground(String... params) {
			try {

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

//				create_job_description.setText(""+new StringBody("postJob")+","+
//						new StringBody(create_job_title.getText().toString()+",")+new StringBody(jobObjectfromback.getId())
//								+","+new StringBody(create_company_name.getText().toString())+","+
//								new StringBody(create_country_name.getTag().toString())+","+
//										new StringBody(create_city_name.getText().toString())+","+
//										new StringBody(create_location.getText().toString())+","+
////										new StringBody( create_job_type.getTag().toString())+","+
//										new StringBody(create_salary.getText().toString())+","+
//										new StringBody(create_srart_date.getText().toString())+","+
////										new StringBody(is_draft)+","+new StringBody(eu_citizen_required)+","+
////										new StringBody(GlobalClaass.getUserId(context))+","+
//										new StringBody(create_job_description.getText().toString())+","+
//										new StringBody(create_requirement.getText().toString())+","+
//										new StringBody("android")
////										)
//						);
				
				try {

					MultipartEntity entity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE);

										
					
					if(!selectedImagePath.equalsIgnoreCase("")){
						File file = new File(selectedImagePath);
						entity.addPart("image", new FileBody(file,
								"image/jpg"));
					}


//					 strrrr = "pj= "+"action = postJob, title="+
//							create_job_title.getText().toString()+",job_id="+jobObjectfromback.getId()
//									+",company="+create_company_name.getText().toString()+",company="+
//									create_country_name.getTag().toString()+",company=2, city="+
//											create_city_name.getText().toString()+",address="+
//											create_location.getText().toString()+",job_type_id="+
//											 create_job_type.getTag().toString()+",salary="+
//											create_salary.getText().toString()+",start_date="+
//											create_srart_date.getText().toString()+",is_draft="+
//											is_draft+",eu_citizen_required="+eu_citizen_required+",user_id="+
//											GlobalClaass.getUserId(context)+",description="+
//											create_job_description.getText().toString()+",requirements="+
//											create_requirement.getText().toString()+",type="+
//											"android"+responseString;
					
					entity.addPart("action",new StringBody("postJob"));
					entity.addPart("title",new StringBody(create_job_title.getText().toString()));
					entity.addPart("id",new StringBody(jobObjectfromback.getId()));
					entity.addPart("company",new StringBody(create_company_name.getText().toString()));
					try{
						entity.addPart("country_id",new StringBody(""+create_country_name.getTag().toString()));

					}
					catch(Exception e){
						entity.addPart("country_id",new StringBody(""+3));

					}
					
					
					
					entity.addPart("city",new StringBody(create_city_name.getText().toString()));
					entity.addPart("address",new StringBody(create_location.getText().toString()));
					
					try{
						entity.addPart("job_type_id",new StringBody(""+ create_job_type.getTag().toString()));

					}
					catch(Exception e){
						entity.addPart("job_type_id",new StringBody(""+ 112));

					}
					
					entity.addPart("salary",new StringBody(create_salary.getText().toString()));
					entity.addPart("start_date",new StringBody(create_srart_date.getText().toString()));
					entity.addPart("is_draft",new StringBody(is_draft));
					entity.addPart("eu_citizen_required",new StringBody(eu_citizen_required));
					entity.addPart("user_id",new StringBody(GlobalClaass.getUserId(context)));
					entity.addPart("description",new StringBody(create_job_description.getText().toString()));
					entity.addPart("requirements",new StringBody(create_requirement.getText().toString()));
					entity.addPart("type",new StringBody("android"));
					

					request.setEntity(entity);
					HttpResponse response = httpClient.execute(request);

					HttpEntity entity2 = response.getEntity();

					responseString = EntityUtils.toString(entity2);
					Log.e("Responce Edit profile",responseString);
					
					


				} catch (final Exception e1) {

					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(context, ">>>>>>>>1111"+e1.getMessage(), 3000).show();

						}
					});
					
					e1.printStackTrace();

					Log.e("Excep", e1.getMessage());

				}


			} catch (final Exception e) {
				
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(context, ">>>>>>>>2222"+e.getMessage(), 3000).show();

					}
				});
				e.printStackTrace();
			}

			return responseString;

		}

		protected void onPostExecute(String responseString) {


			// {"job_id":"6","message":"Your job added successfully.","status":true}
			JSONObject jObject, jobj;
			String get_status = "", get_message = "";

			

			try {

				jObject = new JSONObject(responseString);

				get_message = jObject.getString("message").trim();
				get_status = jObject.getString("status").trim();

				
				if(get_status.equalsIgnoreCase("true")){

//					AlertDialog.Builder builder = new AlertDialog.Builder(context);
//					builder.setMessage(""+strrrr);
//
//					builder.setTitle("Select photo from");
//					// builder.setCancelable(true);
//									builder.show();

					GlobalClaass.showToastMessage(context, get_message);
					
					startActivity(new Intent(context,TeachersListActivity.class));
					GlobalClaass.activitySlideForwardAnimation(context);
					finish();

				}
				else {
					GlobalClaass.showToastMessage(context, get_message);
				}

			} catch (Exception e) {

			}

			
//			Toast.makeText(context, responseString, 3000).show();
			GlobalClaass.hideProgressBar(context);
		}
	}

	@Override
	public void onBackPressed() {
		GlobalClaass.activitySlideBackAnimation(context);
		finish();
	}

}

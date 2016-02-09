package com.chalkboard.recruiter;

import static com.chalkboard.GlobalClaass.hideProgressBar;
import static com.chalkboard.GlobalClaass.showProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.ImageLoader11;
import com.chalkboard.PreferenceConnector;
import com.chalkboard.R;
import com.chalkboard.teacher.CountryList_Activity;
import com.chalkboard.teacher.TeacherProfileViewActivity;

public class RecruiterProfileEditActivity extends Activity{

	Activity context;

	EditText recuiter_companyname,recuiter_countryname,recuiter_city,recuiter_location,recuiter_aboutcompany,recuiter_countryid;
	Button recuiter_save;
	ImageView recuiter_profileimage,btn_back,recuiter_photo;

	protected static final int RESULT_CAMERA = 1;
	protected static final int RESULT_LIBRARY = 2;

	protected static final int RESULT_CAMERA1 = 11;
	protected static final int RESULT_LIBRARY1 = 22;

	String selectedImagePath = "";
	String selectedImagePath1 = "";
	String str = "",str1 = "",ext1 = "", ext = "", EncodeImage = "";
	long filebyte;
	ShowProfile showprofile = null;
	Recruiter_EditProfile recruiter_editprofile = null;
	public ImageLoader imageloader = null;
	public ImageLoader11 imageloader11 = null;

	Typeface font,font2;
	TextView top_header_count;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_recruiter_profile_edit);

		context = this;
		imageloader = new ImageLoader(context);
		imageloader11 = new ImageLoader11(context);

		font=Typeface.createFromAsset(getAssets(), "mark.ttf");
		font2=Typeface.createFromAsset(getAssets(), "marlbold.ttf");

		recuiter_companyname = (EditText) findViewById(R.id.recuiter_companyname);
		recuiter_countryname = (EditText) findViewById(R.id.recuiter_countryname);
		recuiter_city = (EditText) findViewById(R.id.recuiter_city);
		recuiter_location = (EditText) findViewById(R.id.recuiter_location);
		recuiter_aboutcompany = (EditText) findViewById(R.id.recuiter_aboutcompany);
		recuiter_photo = (ImageView)findViewById(R.id.recuiter_photo);
		recuiter_profileimage = (ImageView) findViewById(R.id.recuiter_profileimage);
		recuiter_save = (Button) findViewById(R.id.recuiter_save);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		recuiter_countryid = (EditText)findViewById(R.id.recuiter_countryid);
		top_header_count = (TextView)findViewById(R.id.top_header_count);

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

		try {
			recuiter_companyname.setTypeface(font);
			recuiter_countryname.setTypeface(font);
			recuiter_city.setTypeface(font);
			recuiter_save.setTypeface(font);
			recuiter_location.setTypeface(font);
			recuiter_aboutcompany.setTypeface(font);
			recuiter_countryid.setTypeface(font);
			top_header_count.setTypeface(font);
			((TextView) findViewById(R.id.recruteredir_txt)).setTypeface(font2);


		} catch (Exception e) {

		}

		if (GlobalClaass.isInternetPresent(context)) {

			showprofile = new ShowProfile();
			showprofile.execute();

		} else {

			GlobalClaass.showToastMessage(context,
					"Please check internet connection.");
		}

		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				GlobalClaass.activitySlideBackAnimation(context);
			}
		});


		recuiter_countryname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, CountryList_Activity.class);
				startActivityForResult(i, 99);
			}
		});

		recuiter_profileimage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImageMethod();
			}
		});

		recuiter_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectPhotoMethod();
			}
		});


		recuiter_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(GlobalClaass.isInternetPresent(context)){

					recruiter_editprofile = new Recruiter_EditProfile(context);
					recruiter_editprofile.execute();

				}
				else {
					GlobalClaass.showToastMessage(context,"Please check internet connection");
				}


			}
		});
	}	
	private void selectPhotoMethod() {

		Log.i("choose option", "camera or library");

		final CharSequence[] options = { "Camera", "Choose an image" };

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Select photo from");
		// builder.setCancelable(true);
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (options[item].equals("Camera")) {

					Log.i("Camera Clcik", "camera click");
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					startActivityForResult(intent, RESULT_CAMERA1);

				} else if (options[item].equals("Library")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, RESULT_LIBRARY1);

				}
			}
		});
		builder.show();
	}

	private void selectImageMethod() {

		Log.i("choose option", "camera or library");

		final CharSequence[] options = { "Camera", "Choose an image" };

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Select photo from");
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

	public Uri getImageUri1(Context inContext, Bitmap inImage) {

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

			recuiter_photo.setImageBitmap(myBitmap);

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
				str1 = path;
			}
		});

		return Uri.parse(str1);
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

			recuiter_profileimage.setImageBitmap(myBitmap);

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
					/*
					Bitmap photo = (Bitmap) data.getExtras().get("data");

					Uri tempUri = getImageUri(context, photo);

					selectedImagePath= getRealPathFromURI(tempUri);

					ext = getFileExt(selectedImagePath);

					filebyte = getImageLength(selectedImagePath);

					recuiter_profileimage.setImageBitmap(photo);

					Log.e("BInary", selectedImagePath);*/

					Bitmap photo = (Bitmap) data.getExtras().get("data");

					// CALL THIS METHOD TO GET THE URI FROM THE BITMAP
					Uri tempUri = getImageUri11(getApplicationContext(), photo);

					selectedImagePath = getRealPathFromURI11(tempUri);

					recuiter_profileimage.setImageBitmap(photo);
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

					recuiter_profileimage.setImageBitmap(bitmap);

					Log.e("BInary", selectedImagePath);
				} catch (Exception e) {
					e.printStackTrace();

				}

			}

			break;
		}


		case RESULT_CAMERA1: {

			if (resultCode == context.RESULT_OK && null != data) {

				try {

					/*Bitmap photo = (Bitmap) data.getExtras().get("data");

					Uri tempUri = getImageUri1(context, photo);

					selectedImagePath1= getRealPathFromURI(tempUri);

					ext1 = getFileExt(selectedImagePath1);

					filebyte = getImageLength(selectedImagePath1);

					recuiter_photo.setImageBitmap(photo);

					Log.e("BInary", selectedImagePath);*/
					
					Bitmap photo = (Bitmap) data.getExtras().get("data");

					// CALL THIS METHOD TO GET THE URI FROM THE BITMAP
					Uri tempUri = getImageUri11(getApplicationContext(), photo);

					selectedImagePath1 = getRealPathFromURI11(tempUri);

					recuiter_photo.setImageBitmap(photo);
				}

				catch (Exception e) {

					e.printStackTrace();

					GlobalClaass.showToastMessage(context, e.toString());

				}

			}

			break;
		}
		case RESULT_LIBRARY1: {

			if (resultCode == context.RESULT_OK && null != data) {

				try {

					Uri selectedImage = data.getData();

					String[] filePath = { MediaStore.Images.Media.DATA };

					Cursor c = context.getContentResolver().query(selectedImage,
							filePath, null, null, null);

					c.moveToFirst();

					int columnIndex = c.getColumnIndex(filePath[0]);

					selectedImagePath1 = c.getString(columnIndex);

					ext1 = getFileExt(selectedImagePath1);

					c.close();

					filebyte = getImageLength(selectedImagePath1);

					Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath1);

					recuiter_photo.setImageBitmap(bitmap);

					Log.e("BInary", selectedImagePath);
				} catch (Exception e) {
					e.printStackTrace();

				}

			}

			break;
		}
		}


		if (requestCode == 99) {
			if(resultCode == RESULT_OK){

				recuiter_countryname.setText(data.getStringExtra("name"));
				recuiter_countryid.setText(data.getStringExtra("id"));
				// String result=data.getStringExtra("result");
			}

		}

	}

	public class Recruiter_EditProfile extends AsyncTask<String, String, String> {

		String responseString;

		Activity context;

		JSONObject jObject1;

		boolean remember;

		public Recruiter_EditProfile(Activity ctx) {
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

				try {

					MultipartEntity entity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE);


					if(!selectedImagePath.equalsIgnoreCase("")){
						File file = new File(selectedImagePath);
						entity.addPart("image", new FileBody(file,
								"image/jpg"));
					}
					if(!selectedImagePath1.equalsIgnoreCase("")){
						File file = new File(selectedImagePath1);
						entity.addPart("school_photo", new FileBody(file,
								"image/jpg"));
					}


					entity.addPart("action",new StringBody("account_edit"));
					entity.addPart("user_id",new StringBody(GlobalClaass.getUserId(context)));
					entity.addPart("name",new StringBody(recuiter_companyname.getText().toString()));
					entity.addPart("country_id",new StringBody(recuiter_countryid.getText().toString()));
//					entity.addPart("city",new StringBody(recuiter_city.getText().toString()));
					entity.addPart("address",new StringBody(recuiter_location.getText().toString()));
					entity.addPart("description",new StringBody(recuiter_aboutcompany.getText().toString()));



					request.setEntity(entity);
					HttpResponse response = httpClient.execute(request);

					HttpEntity entity2 = response.getEntity();

					responseString = EntityUtils.toString(entity2);
					Log.e("Responce Edit profile",responseString);


				} catch (Exception e1) {

					e1.printStackTrace();

					Log.e("Excep", e1.getMessage());

				}



			} catch (Exception e) {
				e.printStackTrace();
			}

			return responseString;

		}


		protected void onPostExecute(String responseString) {

			//{"message":"Your Password changed successfully.","status":true}
			JSONObject jObject, jobj;
			String get_status = "", get_message = "";


			try {

				jObject = new JSONObject(responseString);

				get_message = jObject.getString("message").trim();
				get_status = jObject.getString("status").trim();

				jobj = jObject.getJSONObject("user");
				if(get_status.equalsIgnoreCase("true")){

					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.NAME, jobj.getString("name"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.EMAIL, jobj.getString("email"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.COUNTRY, jobj.getString("country"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.CITY, jobj.getString("city"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.ADDRESS, jobj.getString("address"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.AGE, jobj.getString("age"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.GENDER, jobj.getString("gender"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.IMAGE, jobj.getString("image"));

					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.School_photo, jobj.getString("school_photo"));

					GlobalClaass.showToastMessage(context, get_message);
					//startActivity(new Intent(context,Recruter_Profile_Activity.class));
					//GlobalClaass.activitySlideBackAnimation(context);
					finish();

				}
				else {
					GlobalClaass.showToastMessage(context, get_message);
				}

			} catch (Exception e) {

			}

			GlobalClaass.hideProgressBar(context);

		}
	}


	class ShowProfile extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			showProgressBar(context);
		}

		@Override
		protected String doInBackground(String... params) {

			String resultStr = null;
			try {

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "getProfile"));
				nameValuePairs.add(new BasicNameValuePair("id",GlobalClaass.getUserId(context)));

				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();

				resultStr = EntityUtils.toString(entity);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return resultStr;

		}
		//09-15 11:43:19.785: E/Profile Show(6143): {"user":{"id":"680","name":"Ashok","email":"kumar@gmail.com","city":"jaipur","country":"Iceland","country_id":"111","experience":null,"certification":true,"certification_type":"1","address":"manshrovar","is_visible":true,"about":"I am fine","age":"28","gender":"Male","job_start_date":"2015-07-16","TeacherEducation":"","TeacherExperience":"","CountryPreference":"4","image":"http:\/\/128.199.234.133\/yonder\/files\/user\/image\/680\/thumb_1436962715.jpg","school_photo":"http:\/\/128.199.234.133\/yonder\/img\/user.png"},"message":"","status":true}


		@Override
		protected void onPostExecute(String resultStr) {

			Log.e("Profile Show",resultStr);
			JSONObject jObject, jobj;

			String get_replycode = "", get_message = "";

			try {

				jObject = new JSONObject(resultStr);

				get_message = jObject.getString("message").trim();
				get_replycode = jObject.getString("status").trim();

				if(get_replycode.equalsIgnoreCase("true")){

					jobj = jObject.getJSONObject("user");

					if(!jobj.getString("name").equalsIgnoreCase("null")){
						recuiter_companyname.setText(jobj.getString("name"));
					}
					if(!jobj.getString("country").equalsIgnoreCase("null")){
						recuiter_countryname.setText(jobj.getString("country"));
					}
					if(!jobj.getString("country_id").equalsIgnoreCase("null")){
						recuiter_countryid.setText(jobj.getString("country_id"));
					}
					if(!jobj.getString("city").equalsIgnoreCase("null")){
						recuiter_city.setText(jobj.getString("city"));
					}

					if(!jobj.getString("address") .equalsIgnoreCase("null")){
						recuiter_location.setText(jobj.getString("address"));
					}
					if(!jobj.getString("about") .equalsIgnoreCase("null")){
						recuiter_aboutcompany.setText(jobj.getString("about"));
					}


					if(!jobj.getString("image") .equalsIgnoreCase("null")){

						imageloader.DisplayImage(jobj.getString("image"),
								recuiter_profileimage);
					}

					if(!jobj.getString("school_photo") .equalsIgnoreCase("null")){

						imageloader11.DisplayImage(jobj.getString("school_photo"),
								recuiter_photo);
					}



					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.NAME, jobj.getString("name"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.EMAIL, jobj.getString("email"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.COUNTRY, jobj.getString("country"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.CITY, jobj.getString("city"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.ADDRESS, jobj.getString("address"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.AGE, jobj.getString("age"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.GENDER, jobj.getString("gender"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.IMAGE, jobj.getString("image"));
					GlobalClaass.savePrefrencesfor(context, PreferenceConnector.School_photo, jobj.getString("school_photo"));

				}
				else {
					GlobalClaass.showToastMessage(context, get_message);

				}

			} catch (Exception e) {

			}

			hideProgressBar(context);

		}

	}


	@Override
	public void onBackPressed() {

		finish();
		GlobalClaass.activitySlideBackAnimation(context);

	}


}
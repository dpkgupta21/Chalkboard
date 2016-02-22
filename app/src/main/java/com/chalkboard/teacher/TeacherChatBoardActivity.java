package com.chalkboard.teacher;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chalkboard.ChatListAdapter;
import com.chalkboard.ChatObject;
import com.chalkboard.GlobalClaass;
import com.chalkboard.R;

public class TeacherChatBoardActivity extends Activity{
	
	Activity context = null;
	
	String sender_id, sender_name;
	
	ArrayList<ChatObject> dataList = null;
	
	ListView lvJobList = null;
	
	TextView tvLocation = null;
	TextView top_header_count;
	EditText edtMessage;
	protected static final int RESULT_CAMERA = 5;
	protected static final int RESULT_LIBRARY = 6;
	Typeface font,font2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = this;
		font=Typeface.createFromAsset(getAssets(), "fonts/mark.ttf");
		font2=Typeface.createFromAsset(getAssets(), "fonts/marlbold.ttf");
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_chat_board);
		
		sender_id = getIntent().getStringExtra("id");
		sender_name = getIntent().getStringExtra("name");
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
		
		((ImageView) (findViewById(R.id.header_logo)))
		.setVisibility(View.GONE);

		tvLocation = (TextView) findViewById(R.id.location);
		
       ((TextView) (findViewById(R.id.header_text))).setText(sender_name);
       
       
       
       ((ImageView)(findViewById(R.id.header_left_menu))).setVisibility(View.GONE);
       
       ((ImageView)(findViewById(R.id.chat_back))).setVisibility(View.VISIBLE);
       
       ((ImageView)(findViewById(R.id.chat_back))).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
		finish();	
		}
	});
       
		
		lvJobList = (ListView) findViewById(R.id.chatlist);
		
		edtMessage = (EditText) findViewById(R.id.message_field);
		try {
			tvLocation.setTypeface(font);
			edtMessage.setTypeface(font);
			top_header_count.setTypeface(font);
			((TextView) (findViewById(R.id.header_text))).setTypeface(font2);
			
		} catch (Exception e) {

		}
		
		(findViewById(R.id.chat_send)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
				validateMessage();
				
			}
		});

//(findViewById(R.id.chat_attachment)).setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//
//				selectImageMethod();
//
//			}
//		});
		
		
		startRepeatingTask();
		
	}
	
	String selectedImagePath = "",str = "";
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
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





	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {

		case RESULT_CAMERA: {

			if (resultCode == context.RESULT_OK && null != data) {

				try {

					Bitmap photo = (Bitmap) data.getExtras().get("data");

				    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
				    Uri tempUri = getImageUri11(getApplicationContext(), photo);

				    selectedImagePath = getRealPathFromURI11(tempUri);
				    
					sendattachment = new Sendattachment(sender_id, selectedImagePath);
					sendattachment.execute();
					
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

					sendattachment = new Sendattachment(sender_id, selectedImagePath);
					sendattachment.execute();

				} catch (Exception e) {
					e.printStackTrace();

				}

			}

			break;
		}

		}



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

	
	protected void validateMessage() {
		
		if (edtMessage.getText().toString().trim().length() > 0) {
			
			 if(GlobalClaass.isInternetPresent(context)){
					
				 sendMessage = new SendMessage(sender_id, edtMessage.getText().toString().trim());
					sendMessage.execute();
					
				}
				else {
					GlobalClaass.showToastMessage(context,"Please check internet connection");
				}
			
			
			
			edtMessage.setText("");
			
		}
		
	}

	private final static int INTERVAL = (int)(1000 * 60 * (.5)); //2 minutes
	Handler mHandler= new Handler();

	Runnable mHandlerTask = new Runnable()
	{
	     @Override 
	     public void run() {
	    	 GlobalClaass.clearAsyncTask(getJobItem);
	    	 
	    	 if(GlobalClaass.isInternetPresent(context)){
					
	    		 getJobItem = new GetJobItem(sender_id);
	 	 		getJobItem.execute();
					
				}
				else {
					GlobalClaass.showToastMessage(context,"Please check internet connection");
				}
	    	
	          
	          mHandler.postDelayed(mHandlerTask, INTERVAL);
	     }
	};

	void startRepeatingTask()
	{
	    mHandlerTask.run(); 
	}

	void stopRepeatingTask()
	{
	    mHandler.removeCallbacks(mHandlerTask);
	    GlobalClaass.clearAsyncTask(getJobItem);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		 stopRepeatingTask();
		GlobalClaass.clearAsyncTask(getJobItem);
		GlobalClaass.clearAsyncTask(sendMessage);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		 if(GlobalClaass.isInternetPresent(context)){
				
			 getJobItem = new GetJobItem(sender_id);
				getJobItem.execute();
				
			}
			else {
				GlobalClaass.showToastMessage(context,"Please check internet connection");
			}
		
	}
	
	
	Sendattachment sendattachment;
	
	class Sendattachment extends AsyncTask<String, String, String> {

		String sender_id;
		String message;
		
		public Sendattachment(String senderid, String message) {
			sender_id = senderid;
			this.message = message;
		}
		
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			String resultStr = null;
			try {

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

				MultipartEntity entity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);

			
				if(!this.message.equalsIgnoreCase("")){
					File file = new File(this.message);
					entity.addPart("attachment", new FileBody(file,
							"image/jpg"));
				}
				
				entity.addPart("action",new StringBody("sendMessage"));
				entity.addPart("sender_id",new StringBody(GlobalClaass.getUserId(context)));
				
				entity.addPart("receiver_id",new StringBody(sender_id));
				entity.addPart("message",new StringBody(""));
				
	

				request.setEntity(entity);
				HttpResponse response = httpClient.execute(request);

				HttpEntity entity2 = response.getEntity();

				resultStr = EntityUtils.toString(entity2);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return resultStr;

		}

		@Override
		protected void onPostExecute(String result) {

			

			getJobItem = new GetJobItem(sender_id);
			getJobItem.execute();
		}

	}
	
SendMessage sendMessage;
	
	class SendMessage extends AsyncTask<String, String, String> {

		String sender_id;
		String message;
		
		public SendMessage(String senderid, String message) {
			sender_id = senderid;
			this.message = message;
		}
		
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {

			String resultStr = null;
			try {

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "sendMessage"));
				nameValuePairs.add(new BasicNameValuePair("sender_id",
						GlobalClaass.getUserId(context)));
				nameValuePairs.add(new BasicNameValuePair("receiver_id",
						sender_id));
				nameValuePairs.add(new BasicNameValuePair("message",
						this.message));
				nameValuePairs.add(new BasicNameValuePair("attachment",
						""));

				/*
				 * nameValuePairs .add(new BasicNameValuePair("title", ""));
				 * nameValuePairs .add(new BasicNameValuePair("countries", ""));
				 * nameValuePairs .add(new BasicNameValuePair("job_types", ""));
				 * nameValuePairs .add(new BasicNameValuePair("start_date",
				 * "")); nameValuePairs .add(new BasicNameValuePair("offset",
				 * "")); nameValuePairs .add(new BasicNameValuePair("limit",
				 * ""));
				 */

				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();

				resultStr = EntityUtils.toString(entity);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return resultStr;

		}

		@Override
		protected void onPostExecute(String result) {

			

			getJobItem = new GetJobItem(sender_id);
			getJobItem.execute();
		}

	}
	
	
	GetJobItem getJobItem;
	
	public class GetJobItem extends AsyncTask<String, String, String> {

		String sender_id;
		
		public GetJobItem(String senderid) {
			sender_id = senderid;
		}
		
		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected String doInBackground(String... params) {

			String resultStr = null;
			try {

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "loadMessage"));
				nameValuePairs.add(new BasicNameValuePair("sender_id",
						GlobalClaass.getUserId(context)));
				nameValuePairs.add(new BasicNameValuePair("receiver_id",
						sender_id));

				

				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();

				resultStr = EntityUtils.toString(entity);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return resultStr;

		}

		@Override
		protected void onPostExecute(String result) {

			

			setUpUi(result);
		}

	}
	
	public void setUpUi(String result) {
		
		Log.e("Print Result",result);

		try {
			JSONObject jObject = new JSONObject(result);

			String get_message = jObject.getString("message").trim();
			String get_replycode = jObject.getString("status").trim();

			JSONArray jrr = jObject.getJSONArray("messageList");

			dataList = new ArrayList<ChatObject>();

			for (int i = 0; i < jrr.length(); i++) {

				JSONObject jobj = jrr.getJSONObject(i);

				ChatObject itmObj = new ChatObject();

				itmObj.setMessage(jobj.get("message").toString());
				itmObj.setUser_id(jobj.get("user_id").toString());
				itmObj.setTimestamp(jobj.get("timestamp").toString());
				itmObj.setUserImg(jobj.get("userImg").toString());
				itmObj.setAttachment(jobj.get("attachment").toString());

				dataList.add(itmObj);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (dataList != null) {

			if (dataList.size() > 0) {

				
				Collections.sort(dataList,
					       new Comparator<ChatObject>() {
					        @Override
					        public int compare(ChatObject lhs,
					        		ChatObject rhs) {

					         Long price1 = Long.parseLong(rhs
					           .getTimestamp());
					         Long price2 = Long.parseLong(lhs
					           .getTimestamp());
					         return price2.compareTo(price1);
					        }
					       });
				
				
				final ChatListAdapter itmAdap = new ChatListAdapter(context, dataList);

				lvJobList.setAdapter(itmAdap);
				
				lvJobList.post(new Runnable() {
	                @Override
	                public void run() {
	                	lvJobList.setSelection(itmAdap.getCount() - 1);
	                   
	                }
	            });

			

			}
		}
	}

}

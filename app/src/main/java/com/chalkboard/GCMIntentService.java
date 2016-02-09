package com.chalkboard;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.chalkboard.recruiter.TeachersListActivity;
import com.chalkboard.teacher.JobListActivity;
import com.google.android.gcm.GCMBaseIntentService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GCMIntentService extends GCMBaseIntentService {

	
	public GCMIntentService() {
		super(GlobalClaass.sender_id);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onMessage(Context context, Intent intent1) {

		String message;
		

		Bundle extras = intent1.getExtras();
		
	

		message = extras.getString("message");
		
		
		try {

			long when = System.currentTimeMillis();

			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);

			//@SuppressWarnings("deprecation")

//			Notification notification = new Notification(R.drawable.ic_launcher,
//					(CharSequence) message, when);

			// String title = context.getString(R.string.app_name);

			Intent notificationIntent = null;

			//notificationIntent = new Intent(context, NotificationActivity.class);
			
			String role = GlobalClaass.getROLE(context);
			
			if (role.equalsIgnoreCase("teacher")) {
				notificationIntent = new Intent(context,JobListActivity.class);
				
			}else{
				notificationIntent = new Intent(context,TeachersListActivity.class);
				
			}
			
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
	                Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 1,
					notificationIntent, 0);

//			notification.setLatestEventInfo(context, "Chalkboard",
//					(CharSequence) message, intent);

			Notification notification = null;
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				notification = new Notification();
				notification.icon = R.drawable.ic_launcher;
				try {
					Method deprecatedMethod = notification.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
					deprecatedMethod.invoke(notification, context, getString(R.string.app_name), message, intent);
				} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					Log.w(TAG, "Method not found", e);
				}
			} else {
				// Use new API
				Notification.Builder builder = new Notification.Builder(context)
						.setContentIntent(intent)
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle(getString(R.string.app_name)).setContentText(message);
				notification = builder.build();
			}
			notification.number = 1;

			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.defaults |= Notification.DEFAULT_LIGHTS;

			notificationManager.notify(1, notification);

		} catch (Exception e) {
e.printStackTrace();
		}


	}

	@Override
	protected void onError(Context context, String errorId) {

	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {

	}

}
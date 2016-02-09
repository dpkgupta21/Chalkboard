package com.chalkboard;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GlobalClaass {

	//public static String Webservice_Url = "http://128.199.208.177/webservices";

	//public static String Webservice_Url = "http://128.199.234.133/yonder/webservices";
	
	public static String Webservice_Url = "http://128.199.234.133/demo/yonder/webservices";
	
	public static String sender_id = "42648992650";
	
	public static boolean isTablet(Context context) {
		boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
		boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		return (xlarge || large);
	}

	public static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}

	public static void showToastMessage(final Activity context,
			final String message) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				try {
					Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {

				}
			}
		});

	}

	public static void activitySlideBackAnimation(Activity context) {
		context.overridePendingTransition(R.anim.slide_back_left_to_right,
				R.anim.slide_back_right_to_left);
	}

	public static void activitySlideForwardAnimation(Activity context) {
		context.overridePendingTransition(R.anim.slide_forward_left_to_right,
				R.anim.slide_forward_right_to_left);
	}

	public static void showToastMessageValue(final Activity context,
			final int message) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				try {
					Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {

				}
			}
		});

	}

	public static void showProgressBar(final Activity context) {

		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				try {
					((RelativeLayout) context.findViewById(R.id.pb_container))
							.setVisibility(View.VISIBLE);
				} catch (Exception e) {

				}
			}
		});

	}

	public static void hideProgressBar(final Activity context) {

		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				try {
					((RelativeLayout) context.findViewById(R.id.pb_container))
							.setVisibility(View.GONE);
				} catch (Exception e) {

				}
			}
		});

	}

	public static void showProgressBar(final Activity context,
			final View rootView) {

		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				try {
					((RelativeLayout) rootView.findViewById(R.id.pb_container))
							.setVisibility(View.VISIBLE);
				} catch (Exception e) {

				}
			}
		});

	}

	public static void hideProgressBar(final Activity context,
			final View rootView) {

		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				try {
					((RelativeLayout) rootView.findViewById(R.id.pb_container))
							.setVisibility(View.GONE);
				} catch (Exception e) {

				}
			}
		});

	}

	public static String CreateRequest(final JSONObject obj, String strMethod) {

		JSONObject json = new JSONObject();

		try {

			json.put("method_name", strMethod);
			json.put("is_mobile", "android");
			json.put("data", obj);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.e("Dotsquares", "requestStr : " + json.toString());

		return json.toString();
	}

	public static void clearAsyncTask(AsyncTask<?, ?, ?> asyncTask) {
		if (asyncTask != null) {
			if (!asyncTask.isCancelled()) {
				asyncTask.cancel(true);
			}
			asyncTask = null;
		}
	}

	public static boolean isInternetPresent(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	public static Object inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();
		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		// Read response until the end
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Return full string
		return total;
	}

	public static void savePrefrencesfor(final Context context,
			final String Type, final String Value) {

		PreferenceConnector.writeString(context, Type, Value);

	}

	public static void removeSearchPrefrences(final Activity context) {

		PreferenceConnector.getEditor(context)
				.remove(PreferenceConnector.COUNTRIESARRAY).commit();

		PreferenceConnector.getEditor(context)
				.remove(PreferenceConnector.TYPEARRAY).commit();

		PreferenceConnector.getEditor(context)
				.remove(PreferenceConnector.STARTDATE).commit();

	}

	public static void removePrefrences(final Activity context) {

		PreferenceConnector.getEditor(context)
				.remove(PreferenceConnector.USERID).commit();

		PreferenceConnector.getEditor(context).remove(PreferenceConnector.ROLE)
				.commit();

		PreferenceConnector.getEditor(context)
				.remove(PreferenceConnector.EMAIL).commit();

		PreferenceConnector.getEditor(context).remove(PreferenceConnector.NAME)
				.commit();

		PreferenceConnector.getEditor(context)
				.remove(PreferenceConnector.COUNTRY).commit();

		PreferenceConnector.getEditor(context).remove(PreferenceConnector.CITY)
				.commit();

		PreferenceConnector.getEditor(context)
				.remove(PreferenceConnector.ADDRESS).commit();

		PreferenceConnector.getEditor(context).remove(PreferenceConnector.AGE)
				.commit();

		PreferenceConnector.getEditor(context)
				.remove(PreferenceConnector.GENDER).commit();

		PreferenceConnector.getEditor(context)
				.remove(PreferenceConnector.IMAGE).commit();
		
		PreferenceConnector.getEditor(context)
		.remove(PreferenceConnector.School_photo).commit();
		
		PreferenceConnector.getEditor(context)
		.remove(PreferenceConnector.RadioValue).commit();
		
		PreferenceConnector.getEditor(context)
		.remove(PreferenceConnector.Facebook_ID).commit();
		
		PreferenceConnector.getEditor(context)
		.remove(PreferenceConnector.Twitter_ID).commit();
		
		PreferenceConnector.getEditor(context)
		.remove(PreferenceConnector.Gplus_ID).commit();
		
		PreferenceConnector.getEditor(context)
		.remove(PreferenceConnector.Header_Count).commit();

	}

	public static String getHeader_Count(final Activity context) {
		String Header_Count = PreferenceConnector.readString(context,
				PreferenceConnector.Header_Count, null);
		return Header_Count;
	}
	
	public static String getGplus_ID(final Activity context) {
		String Gplus_ID = PreferenceConnector.readString(context,
				PreferenceConnector.Gplus_ID, null);
		return Gplus_ID;
	}
	
	public static String getTwitter_ID(final Activity context) {
		String Twitter_ID = PreferenceConnector.readString(context,
				PreferenceConnector.Twitter_ID, null);
		return Twitter_ID;
	}
	
	public static String getFacebook_ID(final Activity context) {
		String Facebook_ID = PreferenceConnector.readString(context,
				PreferenceConnector.Facebook_ID, null);
		return Facebook_ID;
	}
	
	public static String getRadioValue(final Activity context) {
		String RadioValue = PreferenceConnector.readString(context,
				PreferenceConnector.RadioValue, null);
		return RadioValue;
	}
	
	public static String getSchool_photo(final Activity context) {
		String School_photo = PreferenceConnector.readString(context,
				PreferenceConnector.School_photo, null);
		return School_photo;
	}
	public static String getMaxValue(final Activity context) {
		String USERID = PreferenceConnector.readString(context,
				PreferenceConnector.MAXVALUE, null);
		return USERID;
	}
	
	public static String getMinValue(final Activity context) {
		String USERID = PreferenceConnector.readString(context,
				PreferenceConnector.MINVALUE, null);
		return USERID;
	}
	
	public static String getCountriesArray(final Activity context) {
		String USERID = PreferenceConnector.readString(context,
				PreferenceConnector.COUNTRIESARRAY, null);
		return USERID;
	}

	public static String getTypeArray(final Activity context) {
		String USERID = PreferenceConnector.readString(context,
				PreferenceConnector.TYPEARRAY, null);
		return USERID;
	}

	public static String getStartDate(final Activity context) {
		String USERID = PreferenceConnector.readString(context,
				PreferenceConnector.STARTDATE, null);
		return USERID;
	}

	public static String getName(final Activity context) {
		String USERID = PreferenceConnector.readString(context,
				PreferenceConnector.NAME, null);
		return USERID;
	}

	public static String getCountry(final Activity context) {
		String USERID = PreferenceConnector.readString(context,
				PreferenceConnector.COUNTRY, null);
		return USERID;
	}

	public static String getCity(final Activity context) {
		String USERID = PreferenceConnector.readString(context,
				PreferenceConnector.CITY, null);
		return USERID;
	}

	public static String getAddress(final Activity context) {
		String USERID = PreferenceConnector.readString(context,
				PreferenceConnector.ADDRESS, null);
		return USERID;
	}

	public static String getAge(final Activity context) {
		String USERID = PreferenceConnector.readString(context,
				PreferenceConnector.AGE, null);
		return USERID;
	}

	public static String getGender(final Activity context) {
		String USERID = PreferenceConnector.readString(context,
				PreferenceConnector.GENDER, null);
		return USERID;
	}

	public static String getImage(final Activity context) {
		String USERID = PreferenceConnector.readString(context,
				PreferenceConnector.IMAGE, null);
		return USERID;
	}

	
	public static String getUserId(final Activity context) {
		String USERID = PreferenceConnector.readString(context,
				PreferenceConnector.USERID, null);
		return USERID;
	}

	public static String getROLE(final Context context) {
		String ROLE = PreferenceConnector.readString(context,
				PreferenceConnector.ROLE, null);
		return ROLE;
	}

	public static String getEMAIL(final Activity context) {
		String EMAIL = PreferenceConnector.readString(context,
				PreferenceConnector.EMAIL, null);
		return EMAIL;
	}

	public static String getMonth(String date) {

		String days = "";

		if (date.equalsIgnoreCase("1")) {
			days = "JANUARY";
		} else if (date.equalsIgnoreCase("2")) {
			days = "FEBRUARY";
		} else if (date.equalsIgnoreCase("3")) {
			days = "MARCH";
		} else if (date.equalsIgnoreCase("4")) {
			days = "APRIL";
		} else if (date.equalsIgnoreCase("5")) {
			days = "MAY";
		} else if (date.equalsIgnoreCase("6")) {
			days = "JUNE";
		} else if (date.equalsIgnoreCase("7")) {
			days = "JULY";
		}

		else if (date.equalsIgnoreCase("8")) {
			days = "AUGUST";
		} else if (date.equalsIgnoreCase("9")) {
			days = "SEPTEMBER";
		} else if (date.equalsIgnoreCase("10")) {
			days = "OCTOBER";
		} else if (date.equalsIgnoreCase("11")) {
			days = "NOVEMBER";
		}

		else if (date.equalsIgnoreCase("12")) {
			days = "DECEMBER";
		}

		return days;

	}

	public static String getSortMonth(String date) {

		String days = "";

		if (date.equalsIgnoreCase("1")) {
			days = "JAN";
		} else if (date.equalsIgnoreCase("2")) {
			days = "FEB";
		} else if (date.equalsIgnoreCase("3")) {
			days = "MAR";
		} else if (date.equalsIgnoreCase("4")) {
			days = "APR";
		} else if (date.equalsIgnoreCase("5")) {
			days = "MAY";
		} else if (date.equalsIgnoreCase("6")) {
			days = "JUN";
		} else if (date.equalsIgnoreCase("7")) {
			days = "JUL";
		}

		else if (date.equalsIgnoreCase("8")) {
			days = "AUG";
		} else if (date.equalsIgnoreCase("9")) {
			days = "SEPT";
		} else if (date.equalsIgnoreCase("10")) {
			days = "OCT";
		} else if (date.equalsIgnoreCase("11")) {
			days = "NOV";
		}

		else if (date.equalsIgnoreCase("12")) {
			days = "DEC";
		}

		return days;

	}

	public static String getDayName(String date) {

		String days = "";

		if (date.equalsIgnoreCase("jan")) {
			days = "01";
		} else if (date.equalsIgnoreCase("Feb")) {
			days = "02";
		} else if (date.equalsIgnoreCase("Mar")) {
			days = "03";
		} else if (date.equalsIgnoreCase("Apr")) {
			days = "04";
		} else if (date.equalsIgnoreCase("May")) {
			days = "05";
		} else if (date.equalsIgnoreCase("Jun")) {
			days = "06";
		} else if (date.equalsIgnoreCase("Jul")) {
			days = "07";
		}

		else if (date.equalsIgnoreCase("Aug")) {
			days = "08";
		} else if (date.equalsIgnoreCase("Sept")) {
			days = "09";
		} else if (date.equalsIgnoreCase("Oct")) {
			days = "10";
		} else if (date.equalsIgnoreCase("Nov")) {
			days = "11";
		}

		else if (date.equalsIgnoreCase("Dec")) {
			days = "12";
		}

		return days;

	}

	public static String getWeekName(String date) {

		String days = "";

		if (date.equalsIgnoreCase("mon")) {
			days = "MONDAY";
		} else if (date.equalsIgnoreCase("tue")) {
			days = "TUESDAY";
		} else if (date.equalsIgnoreCase("wed")) {
			days = "WEDNESDAY";
		} else if (date.equalsIgnoreCase("thu")) {
			days = "THURSDAY";
		} else if (date.equalsIgnoreCase("fri")) {
			days = "FRIDAY";
		} else if (date.equalsIgnoreCase("sat")) {
			days = "SATURDAY";
		} else if (date.equalsIgnoreCase("sun")) {
			days = "SUNDAY";
		}

		return days;

	}

	public static String getNewMonth(String date) {

		String days = "";

		if (date.equalsIgnoreCase("Jan")) {
			days = "JANUARY";
		} else if (date.equalsIgnoreCase("Feb")) {
			days = "FEBRUARY";
		} else if (date.equalsIgnoreCase("Mar")) {
			days = "MARCH";
		} else if (date.equalsIgnoreCase("Apr")) {
			days = "APRIL";
		} else if (date.equalsIgnoreCase("May")) {
			days = "MAY";
		} else if (date.equalsIgnoreCase("Jun")) {
			days = "JUNE";
		} else if (date.equalsIgnoreCase("Jul")) {
			days = "JULY";
		}

		else if (date.equalsIgnoreCase("Aug")) {
			days = "AUGUST";
		} else if (date.equalsIgnoreCase("Sep")) {
			days = "SEPTEMBER";
		} else if (date.equalsIgnoreCase("Oct")) {
			days = "OCTOBER";
		} else if (date.equalsIgnoreCase("Nov")) {
			days = "NOVEMBER";
		}

		else if (date.equalsIgnoreCase("Dec")) {
			days = "DECEMBER";
		}

		return days;

	}

	public static String GetDayShow(String date) {
		String s = "";

		try {
			SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date date2 = inFormat.parse(date);
			SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
			s = outFormat.format(date2);

		} catch (Exception e) {

		}

		return s;

	}

	public static String convertdate(String date) {
		String s = "";

		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

		Date dt = null;
		try {
			dt = format.parse(date);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SimpleDateFormat your_format = new SimpleDateFormat("dd-MM-yyyy");

		s = your_format.format(dt);

		return s;
	}

	public static String GetSortDay(String date) {
		String s = "";

		try {
			SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date date2 = inFormat.parse(date);
			SimpleDateFormat outFormat = new SimpleDateFormat("EEE");
			s = outFormat.format(date2);

		} catch (Exception e) {

		}

		return s;

	}

	public static String GetFullDay(String date) {
		String s = "";

		try {
			SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date date2 = inFormat.parse(date);
			SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
			s = outFormat.format(date2);

		} catch (Exception e) {

		}

		return s;

	}

	public static long Calculatetimedifference(String date) {

		long days = 0;

		final Calendar compare = Calendar.getInstance();
		try {
			compare.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(String
					.valueOf(date)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final long compareTimeInMillis = compare.getTimeInMillis();

		Calendar today = Calendar.getInstance();

		long diff = compareTimeInMillis - today.getTimeInMillis();

		days = diff / (24 * 60 * 60 * 1000) + 1;

		return days;

	}

	public static String countformet(String date) {
		String s = "";

		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

		Date dt = null;
		try {
			dt = format.parse(date);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SimpleDateFormat your_format = new SimpleDateFormat("yyyy-MM-dd");

		s = your_format.format(dt);

		return s;
	}

	public static String ConvertIn24Hour(String date) {
		String s = "";

		Date now = null;
		try {
			now = new SimpleDateFormat("hh:mm aa").parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm");
		s = outFormat.format(now);

		return s;
	}

}

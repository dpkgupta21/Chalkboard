package com.chalkboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.chalkboard.preferences.ObjectSerializer;

import java.io.IOException;

public class PreferenceConnector {
	public static final String PREF_NAME = "ChalkBoardPrefrences";
	public static final int MODE = Context.MODE_PRIVATE;

	public static final String READ_MAP_ID = "READ_MAP_ID";

	public static final String USERID = "USERID";
	public static final String ROLE = "ROLE";
	public static final String EMAIL = "EMAIL";

	public static final String NAME = "NAME";
	public static final String COUNTRY = "COUNTRY";
	public static final String CITY = "CITY";
	public static final String ADDRESS = "ADDRESS";
	public static final String AGE = "AGE";
	public static final String GENDER = "GENDER";
	public static final String IMAGE = "IMAGE";
	public static final String School_photo = "School_photo";
	public static final String COUNTRIESARRAY = "COUNTRIESARRAY";
	public static final String TYPEARRAY = "TYPEARRAY";
	public static final String STARTDATE = "STARTDATE";
	public static final String RadioValue = "RadioValue";
	public static final String Header_Count = "Header_Count";
	public static final String MINVALUE = "MINVALUE";
	public static final String MAXVALUE = "MAXVALUE";
	
	public static final String Facebook_ID = "Facebook_ID";
	public static final String Gplus_ID = "Gplus_ID";
	public static final String Twitter_ID = "Twitter_ID";

	public static void writeString(Context context, String key, String value) {
		getEditor(context).putString(key, value).commit();

	}

	public static String readString(Context context, String key, String defValue) {
		return getPreferences(context).getString(key, defValue);
	}

	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREF_NAME, MODE);
	}

	public static Editor getEditor(Context context) {
		return getPreferences(context).edit();
	}



	public static <E> void putObjectIntoPref(Context context, E e, String key) {
		SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		try {
			editor.putString(key, ObjectSerializer.serialize(e));
		} catch (IOException exc) {
			exc.printStackTrace();
		}

		editor.commit();

	}

	public static <E> void removeObjectIntoPref(Context context, String key) {
		SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove(key);
		editor.commit();

	}

	/**
	 * This method is use to get your object from preference.<br>
	 * How to use<br>
	 * Bean bean = getObjectFromPref(context,key);
	 *
	 * @param context
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E> E getObjectFromPref(Context context, String key) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();

			return (E) ObjectSerializer.deserialize(context.getSharedPreferences(PREF_NAME,
					Context.MODE_PRIVATE).getString(key, null));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}

package com.ksoft.anotherworld;

import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class App extends Application {
	public static final String APP_SHARED_PREF = "app_shared_pref";
	public static final String EMAIL = "email";
	public static final String PASSWORD = "password";
	public static final String SESSIONID = "sessionid";
	public static final String SEX = "sex";
	public static final String NAME = "name";
	public static final String SECONDNAME = "secondname";
	public static final String BIRTHDATE = "birthdate";
	public static String id_session = "";
	protected String avatar = "";
	
	public void saveLoginData(String email, String passw, String[] data) {
		if (data.length >= 6) {
			SharedPreferences pref = getSharedPreferences(APP_SHARED_PREF,
					Context.MODE_PRIVATE);
			Editor ed = pref.edit();
			ed.putString(EMAIL, email);
			ed.putString(PASSWORD, passw);
			ed.putString(SESSIONID, data[1]);
			ed.putString(SEX, data[2]);
			ed.putString(NAME, data[3]);
			ed.putString(SECONDNAME, data[4]);
			ed.putString(BIRTHDATE, data[5]);
			ed.commit();
		}
	}

	public void getLoginData(List<String> data) {
		if (data != null) {
			SharedPreferences pref = getSharedPreferences(APP_SHARED_PREF,
					Context.MODE_PRIVATE);
			data.add(pref.getString(SESSIONID, ""));
			data.add(pref.getString(EMAIL, ""));
			data.add(pref.getString(PASSWORD, ""));
			data.add(pref.getString(SEX, ""));
			data.add(pref.getString(NAME, ""));
			data.add(pref.getString(SECONDNAME, ""));
			data.add(pref.getString(BIRTHDATE, ""));
		}
	}

	public void saveLoginData(String email, String passw, String id) {
		id_session = id;
		SharedPreferences pref = getSharedPreferences(APP_SHARED_PREF,
				Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putString(EMAIL, email);
		ed.putString(PASSWORD, passw);
		ed.putString(SESSIONID, id);
		ed.commit();
	}
}

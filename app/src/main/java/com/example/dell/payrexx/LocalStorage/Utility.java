package com.example.dell.payrexx.LocalStorage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.WebView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;




/*import cn.pedant.SweetAlert.SweetAlertDialog;*/

public class Utility {
	public static final String SLIDEMENURBROADCAST = "MyProjectSlideMenu";

	private static String PREFERENCES = "MyProjectPreference";

	private static Dialog dialog;

	public static void logLargeString(String str) {

		if (str.length() > 3000) {

			System.out.print(str.substring(0, 3000));
			logLargeString(str.substring(3000));
		} else {
			System.out.print(str);
		}
	}

	public static String getDeviceType(Context mContext) {
		String ua = new WebView(mContext).getSettings().getUserAgentString();

		if (ua.contains("Mobile")) {

			System.out.println("Type:Mobile");
			return "ANDROID MOBILE";
			// Your code for Mobile
		} else {
			// Your code for TAB
			System.out.println("Type:TAB");
			return "ANDROID TAB";
		}

	}

	public static Boolean write(String fname, String fcontent) {
		try {

			String fpath = "/sdcard/" + fname + ".txt";

			File file = new File(fpath);

			// If file does not exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(fcontent);
			bw.close();

			Log.d("Suceess", "Sucess");
			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public static String getFormatedDate(String strDate, String sourceFormate,
										 String destinyFormate) {
		SimpleDateFormat df;
		df = new SimpleDateFormat(sourceFormate);
		Date date = null;
		try {
			date = df.parse(strDate);

		} catch (Exception e) {
			e.printStackTrace();
		}

		df = new SimpleDateFormat(destinyFormate);
		return df.format(date);

	}






	public static void setBooleanPreferences(Context context, String key,
											 boolean isCheck) {
		SharedPreferences setting = (SharedPreferences) context
				.getSharedPreferences(PREFERENCES, 0);

		SharedPreferences.Editor editor = setting.edit();

		editor.putBoolean(key, isCheck);
		editor.commit();

	}

	public static boolean getBooleanPreferences(Context context, String key) {

		SharedPreferences setting = (SharedPreferences) context
				.getSharedPreferences(PREFERENCES, 0);
		return setting.getBoolean(key, false);

	}

	public static void setStringPreferences(Context context, String key,
											String value) {
		SharedPreferences setting = (SharedPreferences) context
				.getSharedPreferences(PREFERENCES, 0);

		SharedPreferences.Editor editor = setting.edit();

		editor.putString(key, value);
		editor.commit();

	}

	public static String getStringPreferences(Context context, String key) {

		SharedPreferences setting = (SharedPreferences) context
				.getSharedPreferences(PREFERENCES, 0);
		return setting.getString(key, null);

	}
	public static void removeStringPreferences(Context context, String key)
											 {
		SharedPreferences setting = (SharedPreferences) context
				.getSharedPreferences(PREFERENCES, 0);

		SharedPreferences.Editor editor = setting.edit();

		editor.remove(key);
		editor.commit();

	}
	public static void setIntegerPreferences(Context context, String key,
											 int value) {
		SharedPreferences setting = (SharedPreferences) context
				.getSharedPreferences(PREFERENCES, 0);

		SharedPreferences.Editor editor = setting.edit();

		editor.putInt(key, value);
		editor.commit();

	}

	public static void clearAllSharedPreferences(Context context) {
		SharedPreferences.Editor editor = context.getSharedPreferences(
				PREFERENCES, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}

	public static int getIntegerPreferences(Context context, String key) {

		SharedPreferences setting = (SharedPreferences) context
				.getSharedPreferences(PREFERENCES, 0);
		return setting.getInt(key, 0);

	}


	public static void showAlert(Context mContext, String title, String msg) {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setCancelable(false);
		builder.setTitle(title);
		builder.setMessage(msg);
		// Set behavior of negative button

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});

		AlertDialog alert = builder.create();
		try {
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showProgressHUD(Context context, String title,
									   String message) {

		try {

			if (title == null)
				title = "";
			if (message == null)
				message = "";
			dialog = ProgressDialog.show(context, title, message);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(true);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void hideProgressHud() {
		if (dialog != null)
			dialog.cancel();
	}




}

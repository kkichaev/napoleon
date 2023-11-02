package com.grsoft.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.debug.Path;

public class Updater extends AsyncTask<Object, Void, Boolean> {

	@Override
	protected Boolean doInBackground(Object... params) {
		try {
			Context context = (Context) params[0];
			StringBuilder url = new StringBuilder();
			Resources res = context.getResources();
			url.append("https://grsoft.ru/upgrade/getcurip.php");
			URL addr = new URL(url.toString());
			HttpURLConnection  conn = (HttpURLConnection)addr.openConnection();
			InputStream in = new BufferedInputStream(conn.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line;
			String retSrc = "";
			while((line = reader.readLine()) != null) {
				retSrc += line;
			}
			conn.disconnect();
			
			url = new StringBuilder();
			url.append("http://").append(retSrc).append("/").append("/upgrade/upgrade.php?").append("project=").append(res.getString(R.string.project))
					.append("&").append("version=").append(getVersion(res)).append("&").append("category=android&get");

			addr = new URL(url.toString());
			conn = (HttpURLConnection)addr.openConnection();
			in = new BufferedInputStream(conn.getInputStream());
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			retSrc = "";
			while((line = reader.readLine()) != null) {
				retSrc += line;
			}
			reader.close();
			
			int idx = retSrc.indexOf("http://");

			if (idx != -1) {
				String fileUrl = retSrc.substring(idx, retSrc.indexOf("'", idx));
				String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
				addr = new URL(fileUrl);
				InputStream input = ((HttpURLConnection)addr.openConnection()).getInputStream();
				
				String dst = Environment.getExternalStorageDirectory() + "/" + Path.SHARED_FOLDER;
				File outFile = new File(dst, fileName);

				OutputStream output = new FileOutputStream(outFile);
				int read = 0;

				byte[] bytes = new byte[1024];
				while ((read = input.read(bytes)) != -1) {
					output.write(bytes, 0, read);
				}
				output.close();

				Log.d(getClass().getName(), fileUrl);

				Intent intent = new Intent(Intent.ACTION_VIEW);

				Uri uri = null;
				if (Build.VERSION.SDK_INT >= 24) {
					try {
						uri = FileProvider.getUriForFile(context,res.getString(R.string.fileprovider_authorities), outFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					uri = Uri.fromFile(outFile);
				}
				intent.setDataAndType(uri, "application/vnd.android.package-archive");
				intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				context.startActivity(intent);
				
				return true;
			}else
				return false;
			
//			HttpClient client = new DefaultHttpClient();
//			HttpGet get = new HttpGet(url.toString());
//			HttpResponse response = client.execute(get);
//			HttpEntity entity = response.getEntity();
//			String retSrc = EntityUtils.toString(entity, "UTF-8");
//			
//			int idx = retSrc.indexOf("http://");
//
//			if (idx != -1) {
//				String fileUrl = retSrc
//						.substring(idx, retSrc.indexOf("'", idx));
//				String fileName = fileUrl
//						.substring(fileUrl.lastIndexOf('/') + 1);
//				get = new HttpGet(fileUrl);
//				response = client.execute(get);
//				entity = response.getEntity();
//				String dst = Environment.getExternalStorageDirectory() + "/"
//						+ Path.SHARED_FOLDER;
//
//				File outFile = new File(dst, fileName);
//
//				InputStream input = response.getEntity().getContent();
//				OutputStream output = new FileOutputStream(outFile);
//				int read = 0;
//
//				byte[] bytes = new byte[1024];
//				while ((read = input.read(bytes)) != -1) {
//					output.write(bytes, 0, read);
//				}
//				output.close();
//
//				Log.d(getClass().getName(), fileUrl);
//
//				Intent intent = new Intent(Intent.ACTION_VIEW);
//				intent.setDataAndType(Uri.fromFile(outFile),
//						"application/vnd.android.package-archive");
//				context.startActivity(intent);
//				
//				return true;
//			}else
//				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static String getVersion(String verStr) {
		String[] arr = verStr.split(" ");
		return (arr.length > 0) ? arr[0] : verStr;		
	}

	public static String getVersion(Resources res) {
		String fullStr = res.getString(R.string.version);
		return getVersion(fullStr);
	}

}

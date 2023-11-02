package com.ksoft.anotherworld;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

public abstract class ResponseHelper extends AsyncTask<Void, Void, String> {
	private WaitDialog waitDialog = new WaitDialog();
	private FragmentActivity fragmentActivity;
	private static final int TIMEOUT = 10000;
	
	public ResponseHelper(FragmentActivity fragmentActivity){
		this.fragmentActivity = fragmentActivity;
		waitDialog.thread = this;
	}
	
	@Override
	protected String doInBackground(Void... arg0) {
		HttpClient httpclient = new DefaultHttpClient();
		StringBuilder sb = new StringBuilder();
		sb.append(fragmentActivity.getString(R.string.serverip)).append("/")
				.append(getRequestPage());
		HttpPost httppost = new HttpPost(sb.toString());

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(
				httpParameters, TIMEOUT);
		HttpConnectionParams
				.setSoTimeout(httpParameters, TIMEOUT);
		((DefaultHttpClient) httpclient)
				.setParams(httpParameters);

		String result = "";

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			setRequestParams(nameValuePairs);
			httppost.setEntity(new UrlEncodedFormEntity(
					nameValuePairs, fragmentActivity.getString(R.string.encode)));
			HttpResponse response = httpclient
					.execute(httppost);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(is));
			result = br.readLine();
		} catch (Exception e) {
			result = e.getMessage();
		}

		return result;
	}

	protected abstract void setRequestParams(List<NameValuePair> list);
	protected abstract String getRequestPage();
	
	@Override
	protected void onPreExecute() {
		waitDialog.show(fragmentActivity.getSupportFragmentManager(), waitDialog
				.getClass().toString());
	}
	
	@Override
	protected void onPostExecute(String result) {
		waitDialog.dismiss();
	}
	
	public void execute(){
		super.execute((Void[])null);
	}
}

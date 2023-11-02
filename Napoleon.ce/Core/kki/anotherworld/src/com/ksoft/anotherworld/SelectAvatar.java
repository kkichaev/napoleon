package com.ksoft.anotherworld;

import java.io.BufferedReader;
import java.io.IOException;
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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SelectAvatar extends FragmentActivity implements OnClickListener {
	public static final String ACTION = "com.ksoft.anotherworld.action.SELECT_AVATAR";
	private List<ImageView> list = new ArrayList<ImageView>();
	private int selected = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_avatar);
		LinearLayout llPic = (LinearLayout) findViewById(R.id.llPic);

		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= 5; i++) {
			ImageView img = new ImageView(this);
			img.setPadding(10, 0, 10, 0);
			sb.setLength(0);
			sb.append("pic/awatar/").append(i).append(".jpg");

			try {
				InputStream input = getAssets().open(sb.toString());
				Bitmap bmp = BitmapFactory.decodeStream(input);
				img.setImageBitmap(bmp);
				list.add(img);
				llPic.addView(img);
				img.setTag(i);
				img.setOnClickListener(SelectAvatar.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		findViewById(R.id.btnSelect).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (selected > 0)
					new AsyncTask<Void, Void, String>() {
						private WaitDialog waitDialog = new WaitDialog();

						{
							waitDialog.thread = this;
						}

						@Override
						protected String doInBackground(Void... params) {
							HttpClient httpclient = new DefaultHttpClient();
							StringBuilder sb = new StringBuilder();
							sb.append(getString(R.string.serverip)).append("/")
									.append(getString(R.string.eduserinfo));
							HttpPost httppost = new HttpPost(sb.toString());

							HttpParams httpParameters = new BasicHttpParams();
							HttpConnectionParams.setConnectionTimeout(
									httpParameters, 10000);
							HttpConnectionParams.setSoTimeout(httpParameters,
									10000);
							((DefaultHttpClient) httpclient)
									.setParams(httpParameters);

							String result = "";

							try {
								List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
										7);
								nameValuePairs.add(new BasicNameValuePair(
										getString(R.string.id_session),
										App.id_session));
								nameValuePairs.add(new BasicNameValuePair(
										getString(R.string.avatar), Integer
												.toString(selected)));
								httppost.setEntity(new UrlEncodedFormEntity(
										nameValuePairs,
										getString(R.string.encode)));
								HttpResponse response = httpclient
										.execute(httppost);
								InputStream is = response.getEntity()
										.getContent();
								BufferedReader br = new BufferedReader(
										new InputStreamReader(is));
								result = br.readLine();
							} catch (Exception e) {
								result = e.getMessage();
							}

							return result;
						}

						@Override
						protected void onPreExecute() {
							waitDialog.show(getSupportFragmentManager(),
									waitDialog.getClass().toString());
						}

						@Override
						protected void onPostExecute(String result) {
							waitDialog.dismiss();

							if (XmlFmt.getValue(result, "id_session").length() > 0) {
								((App) SelectAvatar.this
										.getApplicationContext()).avatar = Integer
										.toString(selected);
								setResult(RESULT_OK);
								finish();
							} else {
								Toast.makeText(SelectAvatar.this,
										getString(R.string.error),
										Toast.LENGTH_SHORT).show();
							}
						}
					}.execute((Void[]) null);
			}
		});
	}

	@Override
	public void onClick(View v) {
		selected = (Integer) v.getTag();
		for (ImageView iv : list)
			iv.setBackgroundColor(Color.WHITE);

		v.setBackgroundColor(Color.RED);
	}
}

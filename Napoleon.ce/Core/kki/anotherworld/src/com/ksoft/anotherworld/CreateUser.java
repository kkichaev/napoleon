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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateUser extends FragmentActivity {
	public static void open(Context context) {
		Intent intent = new Intent(context, CreateUser.class);
		context.startActivity(intent);

	}

	private Button btnDone;
	private EditText edLogin;
	private EditText edEmail;
	private EditText edPass;
	private Spinner spSex;
	private EditText edName;
	private EditText edSecondName;
	private Spinner spBirtday;
	private EditText edCheckEmail;
	private EditText edCheckPass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createuser);

		btnDone = (Button) findViewById(R.id.btnDone);
		edLogin = (EditText) findViewById(R.id.edLogin);
		edEmail = (EditText) findViewById(R.id.edEmail);
		edPass = (EditText) findViewById(R.id.edPass);
		spSex = (Spinner) findViewById(R.id.spSex);
		edName = (EditText) findViewById(R.id.edName);
		edSecondName = (EditText) findViewById(R.id.edSecondName);
		spBirtday = (Spinner) findViewById(R.id.spBirthDay);
		edCheckEmail = (EditText) findViewById(R.id.edCheckEmail);
		edCheckPass = (EditText) findViewById(R.id.edCheckPass);

		btnDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String check = checkForm();

				if (check.length() == 0)
					send();
				else
					Toast.makeText(v.getContext(), check, Toast.LENGTH_SHORT)
							.show();
			}
		});

		ArrayList<String> items = new ArrayList<String>();
		for (int i = 1970; i < 2009; i++)
			items.add(Integer.toString(i));

		spBirtday.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, items));
		spBirtday.setSelection(17, true);
	}

	private String checkForm() {
		String result = "";

		if (edLogin.getText().toString().trim().length() == 0)
			result = getString(R.string.loginisempty);
		else if (edEmail.getText().toString().trim().length() == 0)
			result = getString(R.string.emailisempty);
		else if (!edEmail.getText().toString()
				.equals(edCheckEmail.getText().toString()))
			result = getString(R.string.emailcheck);
		else if (edPass.getText().toString().trim().length() == 0)
			result = getString(R.string.passisempty);
		else if (!edPass.getText().toString()
				.equals(edCheckPass.getText().toString()))
			result = getString(R.string.passcheck);
		else if (edName.getText().toString().trim().length() == 0)
			result = getString(R.string.nameisempty);
		else if (edSecondName.getText().toString().trim().length() == 0)
			result = getString(R.string.secondnameisempty);

		return result;
	}

	private void send() {
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
						.append(getString(R.string.adduser));
				HttpPost httppost = new HttpPost(sb.toString());

				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams
						.setConnectionTimeout(httpParameters, 10000);
				HttpConnectionParams.setSoTimeout(httpParameters, 10000);
				((DefaultHttpClient) httpclient).setParams(httpParameters);

				String result = "";

				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							7);
					nameValuePairs.add(new BasicNameValuePair(
							getString(R.string.plogin), edLogin.getText()
									.toString().trim()));
					nameValuePairs.add(new BasicNameValuePair(
							getString(R.string.pemail), edEmail.getText()
									.toString().trim()));
					nameValuePairs.add(new BasicNameValuePair(
							getString(R.string.ppas), edPass.getText()
									.toString().trim()));
					nameValuePairs
							.add(new BasicNameValuePair(
									getString(R.string.psex),
									spSex.getSelectedItemPosition() == 1 ? getString(R.string.female)
											: getString(R.string.male)));
					nameValuePairs.add(new BasicNameValuePair(
							getString(R.string.pname), edName.getText()
									.toString().trim()));
					nameValuePairs.add(new BasicNameValuePair(
							getString(R.string.psecondname), edSecondName
									.getText().toString().trim()));
					nameValuePairs.add(new BasicNameValuePair(
							getString(R.string.pbirthday), spBirtday
									.getSelectedItem().toString().trim()));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
							getString(R.string.encode)));
					HttpResponse response = httpclient.execute(httppost);
					InputStream is = response.getEntity().getContent();
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
				btnDone.setEnabled(false);
				waitDialog.show(getSupportFragmentManager(), waitDialog
						.getClass().toString());
			}

			@Override
			protected void onPostExecute(String result) {
				btnDone.setEnabled(true);
				waitDialog.dismiss();

				if (result != null) {
					if (result.trim().equals(getString(R.string.goodansw))) {
						setResult(RESULT_OK);
						finish();
					} else if (result.trim().equals("OSIBKA_login_Z"))
						Toast.makeText(CreateUser.this, R.string.loginbusy,
								Toast.LENGTH_SHORT).show();
					else if (result.trim().equals("OSIBKA_email_Z"))
						Toast.makeText(CreateUser.this, R.string.emailbusy,
								Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(CreateUser.this, result,
								Toast.LENGTH_LONG).show();
				}
			}

			protected void onCancelled() {
				btnDone.setEnabled(true);
				waitDialog.dismiss();
			};
		}.execute((Void[]) null);
	}
}

package com.ksoft.anotherworld;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends FragmentActivity {
	private TextView tvRegister;
	private Button btnLogin;
	private final static int REG_RESULT_CODE = 1;
	private EditText edEmail;
	private EditText edPass;
	private TextView tvForgotPassword;
	private TextView tvTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logindialog);
		tvRegister = (TextView) findViewById(R.id.tvRegister);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		edEmail = (EditText) findViewById(R.id.edEmail);
		edPass = (EditText) findViewById(R.id.edPass);
		tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
		tvTitle = (TextView) findViewById(R.id.tvTitle);

		Typeface face = Typeface.createFromAsset(getAssets(),
				"fonts/A_Stamper.ttf");
		tvTitle.setTypeface(face);
		edEmail.setTypeface(face);
		edPass.setTypeface(face);
		tvForgotPassword.setTypeface(face);
		tvRegister.setTypeface(face);
		btnLogin.setTypeface(face);

		ArrayList<String> data = new ArrayList<String>();
		((App) getApplicationContext()).getLoginData(data);
		edEmail.setText(data.get(1));
		edPass.setText(data.get(2));

		tvRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), CreateUser.class);
				startActivityForResult(intent, REG_RESULT_CODE);
			}
		});

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new ResponseHelper((FragmentActivity) v.getContext()) {

					@Override
					protected void setRequestParams(List<NameValuePair> list) {
						list.add(new BasicNameValuePair(
								getString(R.string.pemail), edEmail.getText()
										.toString().trim()));
						list.add(new BasicNameValuePair(
								getString(R.string.ppas), edPass.getText()
										.toString().trim()));

					}

					@Override
					protected String getRequestPage() {
						return getString(R.string.autorization);
					}

					@Override
					protected void onPreExecute() {
						super.onPreExecute();
						btnLogin.setEnabled(true);
					}

					@Override
					protected void onPostExecute(String result) {
						super.onPostExecute(result);
						btnLogin.setEnabled(true);

						if (result != null) {
							try {
								String idsession = XmlFmt.getValue(result,
										getString(R.string.id_session));
								if (idsession.length() > 1) {
									((App) getApplicationContext())
											.saveLoginData(edEmail.getText()
													.toString(), edPass
													.getText().toString(),
													idsession);
									setResult(RESULT_OK);
									finish();
								} else
									Toast.makeText(Login.this,
											getString(R.string.invaliglogin),
											Toast.LENGTH_SHORT).show();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}.execute();

				// new AsyncTask<Void, Void, String>() {
				// private WaitDialog waitDialog = new WaitDialog();
				//
				// {
				// waitDialog.thread = this;
				// }
				//
				// @Override
				// protected String doInBackground(Void... params) {
				// HttpClient httpclient = new DefaultHttpClient();
				// StringBuilder sb = new StringBuilder();
				// sb.append(getString(R.string.serverip)).append("/")
				// .append(getString(R.string.autorization));
				// HttpPost httppost = new HttpPost(sb.toString());
				//
				// HttpParams httpParameters = new BasicHttpParams();
				// HttpConnectionParams.setConnectionTimeout(
				// httpParameters, 10000);
				// HttpConnectionParams
				// .setSoTimeout(httpParameters, 10000);
				// ((DefaultHttpClient) httpclient)
				// .setParams(httpParameters);
				//
				// String result = "";
				//
				// try {
				// List<NameValuePair> nameValuePairs = new
				// ArrayList<NameValuePair>(
				// 7);
				// nameValuePairs.add(new BasicNameValuePair(
				// getString(R.string.pemail), edEmail
				// .getText().toString().trim()));
				// nameValuePairs.add(new BasicNameValuePair(
				// getString(R.string.ppas), edPass.getText()
				// .toString().trim()));
				// httppost.setEntity(new UrlEncodedFormEntity(
				// nameValuePairs, getString(R.string.encode)));
				// HttpResponse response = httpclient
				// .execute(httppost);
				// InputStream is = response.getEntity().getContent();
				// BufferedReader br = new BufferedReader(
				// new InputStreamReader(is));
				// result = br.readLine();
				// } catch (Exception e) {
				// result = e.getMessage();
				// }
				//
				// return result;
				// }
				//
				// protected void onPreExecute() {
				// btnLogin.setEnabled(false);
				//
				// waitDialog.show(getSupportFragmentManager(), waitDialog
				// .getClass().toString());
				// };
				//
				// protected void onPostExecute(String result) {
				// waitDialog.dismiss();
				// btnLogin.setEnabled(true);
				//
				// if (result != null) {
				// try {
				// String idsession = XmlFmt.getValue(result, "id_session");
				// if (idsession.length() > 1) {
				// ((App) getApplicationContext())
				// .saveLoginData(edEmail.getText()
				// .toString(), edPass
				// .getText().toString(),
				// idsession);
				// setResult(RESULT_OK);
				// finish();
				// } else
				// Toast.makeText(Login.this,
				// getString(R.string.invaliglogin),
				// Toast.LENGTH_SHORT).show();
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// };
				//
				// protected void onCancelled() {
				// waitDialog.dismiss();
				// btnLogin.setEnabled(true);
				// };
				//
				// }.execute((Void[]) null);
				//
			}
		});

		tvForgotPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "Забыли пароль?",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode != KeyEvent.KEYCODE_BACK)
			return super.onKeyDown(keyCode, event);
		else {
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}
	}
}

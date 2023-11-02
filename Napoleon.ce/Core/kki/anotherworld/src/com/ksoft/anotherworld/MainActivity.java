package com.ksoft.anotherworld;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.Window;
import android.widget.FrameLayout;

import com.ksoft.anotherworld.ui.GameModeControl;

public class MainActivity extends FragmentActivity {
	private static final int LOGIN_INFO = 1;
	private static final int AVATAR = 2;
	final String GET_USER_INFO_ACTION = "com.ksoft.anotherworld.get_user_info";
	
	BroadcastReceiver userInfoRcv;
//			new AsyncTask<Void, Void, String>() {
//				private WaitDialog waitDialog = new WaitDialog();
//				
//				{
//					waitDialog.thread = this;
//				}
//				
//				@Override
//				protected String doInBackground(Void... params) {
//					HttpClient httpclient = new DefaultHttpClient();
//					StringBuilder sb = new StringBuilder();
//					sb.append(getString(R.string.serverip)).append("/")
//							.append(getString(R.string.userinfo));
//					HttpPost httppost = new HttpPost(sb.toString());
//
//					HttpParams httpParameters = new BasicHttpParams();
//					HttpConnectionParams.setConnectionTimeout(
//							httpParameters, 10000);
//					HttpConnectionParams
//							.setSoTimeout(httpParameters, 10000);
//					((DefaultHttpClient) httpclient)
//							.setParams(httpParameters);
//
//					String result = "";
//
//					try {
//						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
//								7);
//						nameValuePairs.add(new BasicNameValuePair(
//								getString(R.string.id_session), App.id_session));
//						nameValuePairs.add(new BasicNameValuePair(
//								getString(R.string.avatar),getString(R.string.param_get)));
//						httppost.setEntity(new UrlEncodedFormEntity(
//								nameValuePairs, getString(R.string.encode)));
//						HttpResponse response = httpclient
//								.execute(httppost);
//						InputStream is = response.getEntity().getContent();
//						BufferedReader br = new BufferedReader(
//								new InputStreamReader(is));
//						result = br.readLine();
//					} catch (Exception e) {
//						result = e.getMessage();
//					}
//
//					return result;
//				}
//				
//				@Override
//				protected void onPreExecute() {
//					waitDialog.show(getSupportFragmentManager(), waitDialog
//							.getClass().toString());
//				}
//				
//				@Override
//				protected void onPostExecute(String result) {
//					waitDialog.dismiss();
//					String avatar = XmlFmt.getValue(result, "avatar");
//					
//					if(avatar.length() == 0)
//						((MainActivity)context).startActivityForResult(new Intent(SelectAvatar.ACTION), AVATAR);
//					else{
//						((App)getApplication()).avatar = avatar;
//						//((MainActivity)context).updateAvatar();
//					}
//						
//				}
//			}.execute((Void[])null);
			
//		}
		
//	};
	
	private FrameLayout fl;
	Hero hero = new Hero();
	Move move = new Move();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		GameModeControl gmc = (GameModeControl) findViewById(R.id.gamecontrol);
		fl = (FrameLayout) findViewById(R.id.fragmentLayout);
		
		gmc.onSelectMode = new GameModeControl.OnSelectMode() {
			
			@Override
			public void selectMode(int idx) {
				fl.removeAllViews();
				android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				
				if(idx == 0){
					ft.replace(R.id.fragmentLayout, hero);
				}else if(idx == 1){
					ft.replace(R.id.fragmentLayout, move);
				}
				
				ft.commit();
			}
		};
		
		showLoginDialog();
	}

	class UserInfoRcv extends BroadcastReceiver{
		FragmentActivity fragmentActivity;
		
		public UserInfoRcv(FragmentActivity fragmentActivity){
			this.fragmentActivity = fragmentActivity;
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			new ResponseHelper(fragmentActivity) {
				
				@Override
				protected void setRequestParams(List<NameValuePair> list) {
					list.add(new BasicNameValuePair(
							getString(R.string.id_session), App.id_session));
					list.add(new BasicNameValuePair(
							getString(R.string.avatar),getString(R.string.param_get)));
					
				}
				
				@Override
				protected String getRequestPage() { return getString(R.string.userinfo); }
				
				@Override
				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					
					String avatar = XmlFmt.getValue(result, "avatar");
					
					if(avatar.length() == 0)
						startActivityForResult(new Intent(SelectAvatar.ACTION), AVATAR);
					else{
						((App)getApplication()).avatar = avatar;
						//((MainActivity)context).updateAvatar();
					}
				}
			}.execute();
			
		}
		
	}
	
	private void showLoginDialog() {
		//startActivityForResult(new Intent(this, Login.class), LOGIN_INFO);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == LOGIN_INFO) {
			if (resultCode == Activity.RESULT_OK) {
				ArrayList<String> data = new ArrayList<String>();
				((App) getApplication()).getLoginData(data);
				sendBroadcast(new Intent(GET_USER_INFO_ACTION));
			} else
				finish();
		}
//		else if(requestCode == AVATAR && resultCode == Activity.RESULT_OK)
//			updateAvatar();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(userInfoRcv, new IntentFilter(GET_USER_INFO_ACTION));
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(userInfoRcv != null)
			unregisterReceiver(userInfoRcv);
	}
//	private void updateAvatar(){
//		if(((App)getApplication()).avatar.length() > 0){
//			StringBuilder sb = new StringBuilder();
//			sb.append("pic/awatar/").append(((App)getApplication()).avatar).append(".jpg");
//
//			try {
//				InputStream input = getAssets().open(sb.toString());
//				Bitmap bmp = BitmapFactory.decodeStream(input);
//				ivAvatar.setImageBitmap(bmp);
//				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
//				ivAvatar.setLayoutParams(params);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
}

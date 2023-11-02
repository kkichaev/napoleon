package com.grsoft.manager;

import com.grsoft.util.Updater;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class AboutNew extends DrawerActivity implements OnClickListener {
	
	public static void open(Context context){
		Intent i = new Intent(context, AboutNew.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnCheckUpdates).setOnClickListener(this);
	}

	@Override protected int getLayoutID() { return R.layout.about_new; }

	@Override
	protected void postSyncUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getActionBarTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if(id == R.id.btnCheckUpdates)
			checkUpdates(this);
		
	}
	
	@Override public boolean onCreateOptionsMenu(Menu menu) { return true; }

	private void checkUpdates(final Context context) {
		new Updater() {
			protected void onPreExecute() {
				Toast.makeText(context,	R.string.check_updating, Toast.LENGTH_SHORT).show();
			};

			protected void onPostExecute(Boolean result) {
				if (!result)
					Toast.makeText(context,	R.string.update_not_found, Toast.LENGTH_SHORT).show();
			};

		}.execute(context);
	}

}

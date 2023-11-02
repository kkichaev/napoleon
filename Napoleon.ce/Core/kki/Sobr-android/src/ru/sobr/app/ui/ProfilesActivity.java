package ru.sobr.app.ui;

import android.database.Cursor;
import ru.sobr.app.R;
import ru.sobr.app.provider.SobrContract;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import ru.sobr.app.utils.Constants;

public class ProfilesActivity extends SherlockListActivity {

    //public static final String TAG = "ProfilesActivity";
    //public static final boolean DEBUG = false;

    boolean onPause = false;
    boolean onActivityResult = false;

    SimpleCursorAdapter mAdapter;

    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        setResult(Activity.RESULT_CANCELED);

        mAdapter = new SimpleCursorAdapter(this, R.layout.list_item, this.getContentResolver().query(
                SobrContract.Profiles.CONTENT_URI, SobrContract.Profiles.PROJECTION_LIST, null, null, null),
                new String[]{SobrContract.Profiles.NAME, SobrContract.Profiles.SYSTEM_PHONE_NUMBER},
                new int[]{android.R.id.text1, android.R.id.text2});
        setListAdapter(mAdapter);

//	showGhangelog();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onActivityResult = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // checkAppPassword();
        Cursor cursor = this.getContentResolver().query(
                SobrContract.Profiles.CONTENT_URI,
                SobrContract.Profiles.PROJECTION_LIST,
                SobrContract.Profiles.SYSTEM_TYPE + " = '" + Constants.SOBR_GSM + "'",
                null,
                null);
        if (cursor.getCount() > 0) {
            findViewById(R.id.registration).setVisibility(View.VISIBLE);
            findViewById(R.id.registration).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfilesActivity.this, SobrAssistActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            findViewById(R.id.registration).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPause = true;
        
        if(MainActivity.isApplicationSentToBackground(this)){
        	System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(Intent.ACTION_EDIT, ContentUris.withAppendedId(
                SobrContract.Profiles.CONTENT_URI, id));
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        	.edit().putLong("profile_id_preference", id).commit();
        startActivityForResult(intent, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(R.string.profiles_add).setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Intent.ACTION_INSERT, SobrContract.Profiles.CONTENT_URI);
                startActivityForResult(intent, 0);
                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return super.onCreateOptionsMenu(menu);
    }

//    public void showGhangelog() {
//	String version = "0";
//	try {
//	    version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
//	} catch (NameNotFoundException e) {
//	}
//	SharedPreferences settings = this.getSharedPreferences("app_version_preference", 0);
//	
//	if (!version.equals(settings.getString("app_version_preference", "0"))) {
//	    showGhangelogDialog();
//	    
//	    SharedPreferences.Editor editor = settings.edit();
//	    editor.putString("app_version_preference", version);
//	    editor.commit();
//	}
//    }
//
//    public void showGhangelogDialog() {
//	String version = "0";
//	try {
//	    version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
//	} catch (NameNotFoundException e) {
//	}
//
//	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//	dialog.setIcon(R.drawable.ic_launcher);
//	dialog.setTitle("Sobr v" + version);
//	dialog.setMessage(R.string.changlog_dialog_message);
//	dialog.setPositiveButton(android.R.string.ok, null);
//	dialog.show();
//    }

}
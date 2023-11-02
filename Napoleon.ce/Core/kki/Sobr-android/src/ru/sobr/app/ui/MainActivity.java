package ru.sobr.app.ui;

import java.util.ArrayList;
import java.util.List;

import ru.sobr.app.R;
import ru.sobr.app.provider.SobrContract;
import ru.sobr.app.ui.holo.HoloAlertDialogBuilder;
import ru.sobr.app.utils.Constants;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class MainActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener {

    public static final String TAG = "MainActivity";
    //private static final boolean DEBUG = false;

    public static final String SYSTEMTYPE_KEY = "system_type";
    public static final String PHONESTATUS_KEY = "phone_status";
    public static final String PINCODE_KEY = "pincode_key";
    public static final String CMD123_KEY = "cmd_123";
    public static final String CMD456_KEY = "cmd_456";
    public static final String CMD789_KEY = "cmd_789";
    public static final String CMD777_KEY = "cmd_777";
    public static final String CMD666_KEY = "cmd_666";
    public static final String CMD999_KEY = "cmd_999";
    public static final String CMD911_KEY = "cmd_911";
    public static final String CMD09_KEY = "cmd_09";
    public static final String CMD123_KEY_TITLE = "cmd_123_title";
    public static final String CMD456_KEY_TITLE = "cmd_456_title";
    public static final String CMD789_KEY_TITLE = "cmd_789_title";
    public static final String CMD777_KEY_TITLE = "cmd_777_title";
    public static final String CMD999_KEY_TITLE = "cmd_999_title";
    public static final String CMD911_KEY_TITLE = "cmd_911_title";
    public static final String GPSRECEIVER_KEY = "gps_receiver";
    public static final String REPORTONMOVE_KEY = "report_on_move";
    public static final String IMMOBILIZER_KEY = "immobilizer";
    public static final String SHOCKSENSOR_KEY = "shock_sensor";
    public static final String SOBR_ASSIST_LOGIN = "sobr_assist_login";
    public static final String SOBR_ASSIST_PASSWORD = "sobr_assist_password";

    static String isDevice = "";
    boolean isFirstLaunch = true;
    boolean showEngineTab = true;

    TabHost mTabHost;
    ViewPager mViewPager;
    TabsAdapter mTabsAdapter;
    SimpleCursorAdapter mProfileAdapter;

	private String workMode = "0";

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        Context context = getSupportActionBar().getThemedContext();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mProfileAdapter = new SimpleCursorAdapter(context, R.layout.sherlock_spinner_dropdown_item,
                getContentResolver().query(SobrContract.Profiles.CONTENT_URI, null, null, null, null),
                new String[]{SobrContract.Profiles.NAME}, new int[]{android.R.id.text1});
        getSupportActionBar().setListNavigationCallbacks(mProfileAdapter, this);
        
        int pos = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()).getInt("profile_curr_position_preference", 0);
        
        if(pos >= 0 && pos < mProfileAdapter.getCount())
        	getSupportActionBar().setSelectedNavigationItem(pos);
        else
        	getSupportActionBar().setSelectedNavigationItem(mProfileAdapter.getCount() - 1);

        addAllTabs();
        changeEngineTabName();

        if (mProfileAdapter.getCount() == 0) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    SobrContract.Profiles.CONTENT_URI);
            startActivityForResult(intent, 0);
        }
//	else {
//	    showGhangelog();
//	}

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }

        mTabHost.setCurrentTab(PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()).getInt("tab_number_before_exit", 0));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.tab_name_settings)
                .setIcon(R.drawable.ic_action_settings)
                .setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                SobrContract.Profiles.CONTENT_URI);
                        startActivityForResult(intent, 0);

                        return true;
                    }
                })
                .setShowAsAction(
                        MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }

    @Override
    protected void onPause() {
    	if (isApplicationSentToBackground(this))
            finish();
            
        super.onPause();
    }
    
    static public boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit().putInt("tab_number_before_exit", mTabHost.getCurrentTab()).commit();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if(DEBUG)Log.d(TAG, "onActivityResult");
        if (mProfileAdapter.getCount() == 0) {
            finish();
        } else {
            mTabHost.setCurrentTab(0); // Fix API < 11
            mTabsAdapter.deleteAllTabs();
            addAllTabs();
            changeEngineTabName();
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        //if(DEBUG)Log.d(TAG, "onNavigationItemSelected");
        int currentTab = mTabHost.getCurrentTab();

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit().putLong("profile_id_preference", itemId).commit();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit().putInt("profile_curr_position_preference", itemPosition).commit();

        // refresh all tabs
        if (!isFirstLaunch) {
            mTabHost.setCurrentTab(0); // Fix API < 11
            mTabsAdapter.deleteAllTabs();
            addAllTabs();
            changeEngineTabName();
            //checkAppPassword();
        } else {
            checkAppPassword();
        }
        isFirstLaunch = false;

        mTabHost.setCurrentTab(currentTab);

        return true;
    }

    private void addAllTabs() {

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabHost.getTabWidget().setDividerDrawable(R.drawable.deliver);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTabsAdapter = new TabsAdapter(MainActivity.this, mTabHost, mViewPager);

        Cursor cur = mProfileAdapter.getCursor();
        Bundle data = new Bundle();
        
        if (cur.getCount() > 0 && cur.moveToFirst()) {
            try {
                //if (DEBUG)Log.d(TAG, "get data for tabs");
            	int pos = PreferenceManager.getDefaultSharedPreferences(
                        getApplicationContext()).getInt(
                        "profile_curr_position_preference", 0);
            	
            	if(pos >= 0 && pos < cur.getCount())
            		cur.move(pos);
            	else
            		cur.move(cur.getCount() - 1);
            	
                //if (DEBUG)Log.d(TAG, "Profile name: "+ cur.getString(cur.getColumnIndexOrThrow(SobrContract.Profiles.NAME)));

                String sysType = cur.getString(cur.getColumnIndex(SobrContract.Profiles.SYSTEM_TYPE));
                String phoneStatus = cur.getString(cur.getColumnIndex(SobrContract.Profiles.PHONE_STATUS));
                String pinCode = cur.getString(cur.getColumnIndex(SobrContract.Profiles.PIN_CODE));

                String cmd123 = cur.getString(cur.getColumnIndex(SobrContract.Profiles.COMMAND_123));
                String cmd456 = cur.getString(cur.getColumnIndex(SobrContract.Profiles.COMMAND_456));
                String cmd789 = cur.getString(cur.getColumnIndex(SobrContract.Profiles.COMMAND_789));
                String cmd666 = cur.getString(cur.getColumnIndex(SobrContract.Profiles.COMMAND_666));
                String cmd777 = cur.getString(cur.getColumnIndex(SobrContract.Profiles.COMMAND_777));
                String cmd999 = cur.getString(cur.getColumnIndex(SobrContract.Profiles.COMMAND_999));
                String cmd911 = cur.getString(cur.getColumnIndex(SobrContract.Profiles.COMMAND_911));
                String cmd09 = cur.getString(cur.getColumnIndex(SobrContract.Profiles.COMMAND_09));

                String cmd123Title = cur.getString(cur.getColumnIndex(SobrContract.Profiles.COMMAND_123_TITLE));
                String cmd456Title = cur.getString(cur.getColumnIndex(SobrContract.Profiles.COMMAND_456_TITLE));
                String cmd789Title = cur.getString(cur.getColumnIndex(SobrContract.Profiles.COMMAND_789_TITLE));
                String cmd777Title = cur.getString(cur.getColumnIndex(SobrContract.Profiles.COMMAND_777_TITLE));
                String cmd999Title = cur.getString(cur.getColumnIndex(SobrContract.Profiles.COMMAND_999_TITLE));
                String cmd911Title = cur.getString(cur.getColumnIndex(SobrContract.Profiles.COMMAND_911_TITLE));

                String shockSensor = cur.getString(cur.getColumnIndex(SobrContract.Profiles.SHOCK_SENSOR));
                String gpsReceiver = cur.getString(cur.getColumnIndex(SobrContract.Profiles.GPS_RECEIVER));
                String reportOnMove = cur.getString(cur.getColumnIndex(SobrContract.Profiles.REPORT_ON_MOVE));
                String immobilizer = cur.getString(cur.getColumnIndex(SobrContract.Profiles.IMMOBILIZER));

                String sobrAssistLogin = cur.getString(cur.getColumnIndex(SobrContract.Profiles.SOBR_ASSIST_LOGIN));
                String sobrAssistPassword = cur.getString(cur.getColumnIndex(SobrContract.Profiles.SOBR_ASSIST_PASSWORD));
                workMode = cur.getString(cur.getColumnIndex(SobrContract.Profiles.GSM510_WORK_MODE));
                // cur.close();
                data.putString(SYSTEMTYPE_KEY, sysType);
                data.putString(PHONESTATUS_KEY, phoneStatus);
                data.putString(PINCODE_KEY, pinCode);
                data.putString(CMD123_KEY, cmd123);
                data.putString(CMD456_KEY, cmd456);
                data.putString(CMD789_KEY, cmd789);
                data.putString(CMD777_KEY, cmd777);
                data.putString(CMD666_KEY, cmd666);
                data.putString(CMD999_KEY, cmd999);
                data.putString(CMD911_KEY, cmd911);
                data.putString(CMD09_KEY, cmd09);
                data.putString(CMD123_KEY_TITLE, cmd123Title);
                data.putString(CMD456_KEY_TITLE, cmd456Title);
                data.putString(CMD789_KEY_TITLE, cmd789Title);
                data.putString(CMD777_KEY_TITLE, cmd777Title);
                data.putString(CMD999_KEY_TITLE, cmd999Title);
                data.putString(CMD911_KEY_TITLE, cmd911Title);
                data.putString(GPSRECEIVER_KEY, gpsReceiver);
                data.putString(REPORTONMOVE_KEY, reportOnMove);
                data.putString(IMMOBILIZER_KEY, immobilizer);
                data.putString(SHOCKSENSOR_KEY, shockSensor);
                data.putString(SOBR_ASSIST_LOGIN, sobrAssistLogin);
                data.putString(SOBR_ASSIST_PASSWORD, sobrAssistPassword);
                data.putString(SobrContract.Profiles.GSM510_WORK_MODE,
                		cur.getString(cur.getColumnIndex(SobrContract.Profiles.GSM510_WORK_MODE)));
                data.putBoolean(SobrContract.Profiles.PREHEATER,
                		Boolean.parseBoolean(cur.getString(
                				cur.getColumnIndex(SobrContract.Profiles.PREHEATER))));
                data.putInt(SobrContract.Profiles.CHANELS,
                		cur.getInt(cur.getColumnIndex(SobrContract.Profiles.CHANELS)));
                data.putString(SobrContract.Profiles.CMD1,
                		cur.getString(cur.getColumnIndex(SobrContract.Profiles.CMD1)));
                data.putString(SobrContract.Profiles.KEY1,
                		cur.getString(cur.getColumnIndex(SobrContract.Profiles.KEY1)));
                data.putString(SobrContract.Profiles.CMD2,
                		cur.getString(cur.getColumnIndex(SobrContract.Profiles.CMD2)));
                data.putString(SobrContract.Profiles.KEY2,
                		cur.getString(cur.getColumnIndex(SobrContract.Profiles.KEY2)));
                data.putString(SobrContract.Profiles.CMD3,
                		cur.getString(cur.getColumnIndex(SobrContract.Profiles.CMD3)));
                data.putString(SobrContract.Profiles.KEY3,
                		cur.getString(cur.getColumnIndex(SobrContract.Profiles.KEY3)));
                data.putString(SobrContract.Profiles.CMD4,
                		cur.getString(cur.getColumnIndex(SobrContract.Profiles.CMD4)));
                data.putString(SobrContract.Profiles.KEY4,
                		cur.getString(cur.getColumnIndex(SobrContract.Profiles.KEY4)));
                data.putBoolean(SobrContract.Profiles.ALARM, Boolean.parseBoolean(cur.getString(
        				cur.getColumnIndex(SobrContract.Profiles.ALARM))));

                if (cmd123.equals("disable_value") & cmd456.equals("disable_value")
                        & cmd789.equals("disable_value") & cmd777.equals("disable_value")
                        & cmd999.equals("disable_value")) {
                    showEngineTab = false;
                } else {
                    showEngineTab = true;
                }

                data.putInt(SobrContract.Profiles._ID, 
                		cur.getInt(cur.getColumnIndex(SobrContract.Profiles._ID)));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
//	if(mTabHost == null) {
//	    if(DEBUG) Log.d(TAG, "mTabHost == null");
//	} else {
//	    if(DEBUG) Log.d(TAG, "mTabHost == null");
//	}
        if (showEngineTab && workMode.equals("0")) {
            mTabsAdapter.addTab(
                    mTabHost.newTabSpec("engage").setIndicator(
                            prepareTabView(mTabHost.getContext(), R.string.tab_name_engine, getResources()
                                    .getDrawable(R.drawable.ic_tab_engine))), TabEngineFragment.class, data);
        }

        mTabsAdapter.addTab(
                mTabHost.newTabSpec("info").setIndicator(
                        prepareTabView(mTabHost.getContext(), R.string.tab_name_state, getResources()
                                .getDrawable(R.drawable.ic_tab_info))), TabStateFragment.class, data);

        mTabsAdapter.addTab(
                mTabHost.newTabSpec("security").setIndicator(
                        prepareTabView(mTabHost.getContext(), R.string.tab_name_security, getResources()
                                .getDrawable(R.drawable.ic_tab_security))), TabSecurityFragment.class, data);


//	mTabsAdapter.addTab(
//		mTabHost.newTabSpec("settings").setIndicator(
//			prepareTabView(mTabHost.getContext(), "Настройки",
//				getResources().getDrawable(R.drawable.ic_tab_settings))),
//		TabSettingsFragment.class, data);

    }

    private static View prepareTabView(Context context, int resTextId, Drawable drawable) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(resTextId);
        ImageView img = (ImageView) view.findViewById(R.id.tabsImage);
        img.setImageDrawable(drawable);
        return view;
    }

    private void changeEngineTabName() {
        if (showEngineTab && workMode.equals("0")) {
            View view = mTabHost.getTabWidget().getChildAt(0);
            TextView tv = (TextView) view.findViewById(R.id.tabsText);
            Cursor cursor = mProfileAdapter.getCursor();
            try {
                String systemType = cursor.getString(cursor
                        .getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_TYPE));
                if (systemType.equals(Constants.SOBR_DOMONLINE) ||
                		systemType.equals(Constants.SOBR_GSM510)) {
                    tv.setText(R.string.tab_name_device);
                } else {
                    tv.setText(R.string.tab_name_engine);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean needShowPinCodeDialog(){
        String projection[] = {SobrContract.Profiles.PIN_CODE_ON_BOOT};
        StringBuilder whereClause = new StringBuilder(SobrContract.Profiles.PIN_CODE_ON_BOOT);
        whereClause.append(" like ").append("'").append(Constants.TRUE).append("'");
        Cursor cursor = getContentResolver().query(SobrContract.Profiles.CONTENT_URI, projection,
                whereClause.toString(), null, null);
        if(cursor==null) return false;
        int count = cursor.getCount();
        cursor.close();
        return count>0;
    }

    protected void checkAppPassword() {
        if (mProfileAdapter.getCount() != 0) {
            if (needShowPinCodeDialog()) {
                showPinCodeDialog();
            }
        }
    }

    protected void showPinCodeDialog() {

        Dialog dialog;
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.dialog_password, null, false);

        AlertDialog.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = new AlertDialog.Builder(this);
        } else {
            ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyTheme);
            builder = new HoloAlertDialogBuilder(ctw);
        }
        builder.setTitle(R.string.password_dialog_pin_code);
        builder.setView(layout);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Cursor cursor = mProfileAdapter.getCursor();
                cursor.moveToFirst();
                String password = cursor.getString(cursor.getColumnIndex(SobrContract.Profiles.PIN_CODE));
                List<String> passwords = new ArrayList<String>();
                if(password!=null) passwords.add(password);
                while( cursor.moveToNext() ){
                    password = cursor.getString(cursor.getColumnIndex(SobrContract.Profiles.PIN_CODE));
                    if(password!=null) passwords.add(password);
                }

//                String app_pass = mProfileAdapter.getCursor().getString(
//                        mProfileAdapter.getCursor().getColumnIndex(SobrContract.Profiles.PIN_CODE));
                EditText input_pass = (EditText) layout.findViewById(R.id.password_text);
                String enteredPassword = input_pass.getText().toString();
                boolean passwordIsCorrect = false;
                for(String p: passwords){
                    if(p.equals(enteredPassword)){
                        passwordIsCorrect = true;
                        break;
                    }
                }
                if (passwordIsCorrect) {
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.password_dialog_pin_code_error,
                            Toast.LENGTH_LONG).show();
                    showPinCodeDialog();
                }

            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

//    public void showGhangelog() {
//	SharedPreferences settings = this.getSharedPreferences("app_version_preference", 0);
//	String version = "0";
//	try {
//	    version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
//	} catch (NameNotFoundException e) {
//	}
//	
//	if (!version.equals(settings.getString("app_version_preference", "0"))) {
//	    
//	    showGhangelogDialog();
//	    
//	    SharedPreferences.Editor editor = settings.edit();
//	    editor.putString("app_version_preference", version);
//	    editor.commit();
//	}
//
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

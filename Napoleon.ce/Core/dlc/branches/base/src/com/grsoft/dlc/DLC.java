package com.grsoft.dlc;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.grsoft.dlc.R.string;

public class DLC extends Activity {
	protected static final int ASK_PASSWORD_DLG = 0;
	private ArrayList<ApplicationInfo> apps;
	private GridView gvApplication; 
	
	private final int ALL_APPLICATIONS = 0;
	private final int DLC_SETTING = 1;
	
	private int selection = 0;
	
	private ApplicationInfo[] dlc_activity;
	private TextView tvPhone;
	private TextView tvContact;
	private TextView tvSms;
	public static final String SETTING_PROCESS_NAME = "com.android.settings";
	public static boolean allowSetting = false;
	 
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER,
                WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);

        
        dlc_activity = createDCLAppInfo();

        apps = new ArrayList<ApplicationInfo>();
        
        gvApplication = (GridView)findViewById(R.id.gvApplication);
        gvApplication.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);
				
				if (app.isProtected()){
					if (((DLCApp)getApplication()).isFreeVersion()){
						Toast.makeText(view.getContext(), 
								string.free_version_hitn, 
								Toast.LENGTH_LONG).show();
						startActivity(app.intent);
					}else{
						selection = app.index;
						showDialog(ASK_PASSWORD_DLG);
	        		}
				}else
					startActivity(app.intent); 
			}
		});
        
        tvPhone = (TextView)findViewById(R.id.tvPhone);
        tvPhone.setCompoundDrawablesWithIntrinsicBounds(null, 
        		getResources().getDrawable(R.drawable.call_contact), null, null);
        tvPhone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_DIAL);
				startActivity(intent);
			}
		});
        
        tvContact = (TextView)findViewById(R.id.tvContact);
        tvContact.setCompoundDrawablesWithIntrinsicBounds(null, 
        		getResources().getDrawable(R.drawable.contact), null, null);
        
        tvContact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK, 
						ContactsContract.Contacts.CONTENT_URI);
				startActivity(intent);
			}
		});
        
        tvSms = (TextView)findViewById(R.id.tvSMS);
        tvSms.setCompoundDrawablesWithIntrinsicBounds(null,
        		getResources().getDrawable(R.drawable.sms), null, null);
        tvSms.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.setType("vnd.android-dir/mms-sms");
				startActivity(intent);
			}
		});
        
        ///-- settings put secure user_setup_complete 0 --- !!!!!!
    }

	private ApplicationInfo[] createDCLAppInfo(){
		return new ApplicationInfo[] {
			createAppList(),
			createPreference()};
	}
    
    private ApplicationInfo createPreference() {
    	ApplicationInfo result = new ApplicationInfo((DLCApp)getApplication());
        result.title = "DLC Настройки";
        result.setActivity(com.grsoft.dlc.Preferences.OPEN_COMMAND);
        result.icon = getResources().getDrawable(R.drawable.password);
        result.setProtected(true);
        result.index = DLC_SETTING;
		return result;
	}

	private ApplicationInfo createAppList() {
		ApplicationInfo result = new ApplicationInfo((DLCApp)getApplication());
        result.title = "Список приложений";
        result.setActivity(AppList.OPEN_COMMAND);
        result.icon = getResources().getDrawable(R.drawable.emblem);
        result.setProtected(true);
        result.index = ALL_APPLICATIONS;
		return result;
	}

	@Override
    protected Dialog onCreateDialog(int id) {
    	switch(id){
    	case ASK_PASSWORD_DLG: return createAskPasswordDlg();
    	default: return super.onCreateDialog(id);
    	}
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	switch(id){
    	case ASK_PASSWORD_DLG: prepareAskPasswordDlg(dialog); break;
    	default: super.onPrepareDialog(id, dialog);
    	}
    }
    
    private void prepareAskPasswordDlg(Dialog dialog) {
		EditText edPassword = (EditText) dialog.findViewById(R.id.edPassword);
		edPassword.setText("");
	}

	private Dialog createAskPasswordDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final View view = View.inflate(this, R.layout.password, null);
		builder.setView(view);
		view.findViewById(R.id.btnGo).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText edPassword = (EditText) view.findViewById(R.id.edPassword);
				
				if (checkPassword(edPassword.getText().toString())){
					startActivity(dlc_activity[selection].intent);
				}
				
				dismissDialog(ASK_PASSWORD_DLG);
			}
		});
		
		return builder.create();
	}

	protected boolean checkPassword(String string) {
		String password = getSharedPreferences(Preferences
				.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getString(Preferences.PASSWORD, "1234");
		
		return password.equals(string);
	}
	
	private void loadApplications() {
        apps.clear();
        allowSetting = false;
        
        SharedPreferences pref = getSharedPreferences(Preferences.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        
        if (pref.getBoolean(Preferences.SHOW_DCL_ICONS, true)){
	        apps.add(dlc_activity[ALL_APPLICATIONS]);
	        apps.add(dlc_activity[DLC_SETTING]);
        }
        
        for (ApplicationInfo info : ((DLCApp)getApplication()).getAppList().values())
        	if (info.isAllowed()){
        		apps.add(info);
        		
        		if(!allowSetting){
	        		Intent i = info.getIntent();
	        		
	        		if(i != null){
	        			if(i.getComponent().getPackageName().equals(SETTING_PROCESS_NAME))
	        				allowSetting = true;
	        		}
        		}
        	}

        tvPhone.setVisibility(View.GONE);
        tvContact.setVisibility(View.GONE);
        tvSms.setVisibility(View.GONE);
        
        boolean llFavoriteVisible = false;
        
        if (pref.getBoolean(Preferences.ALLOW_PHONE, true)){
	        tvPhone.setVisibility(View.VISIBLE);
	        tvContact.setVisibility(View.VISIBLE);
	        llFavoriteVisible = true;
        }
        
        if (pref.getBoolean(Preferences.ALLOW_MESSAGE, true)){
        	tvSms.setVisibility(View.VISIBLE);
        	llFavoriteVisible = true;
        }
        
        findViewById(R.id.llFavorite).setVisibility(
        		llFavoriteVisible ? View.VISIBLE : View.GONE);
    } 
    
    private void bindApp() {
    	gvApplication.setAdapter(new ApplicationsAdapter(this, apps));
    	gvApplication.setSelection(0); 
	}
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	loadApplications();
    	bindApp();
    	
    	ImageView iv = (ImageView)findViewById(R.id.ivBackGround);
    	iv.setImageDrawable(WallpaperManager.getInstance(this).getDrawable());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.main_opt_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	boolean result = false;
    	final int id = item.getItemId();
    	
    	if (id == R.id.itSetting){ 
    		openSetting(); 
    		result = true; 
    	} else if (id ==  R.id.itApplications){
    		openAllApplications();
    		result = true;
    	} else if (id == R.id.itWallpapper)
    		startWallpaper();
    	
    	return result;
    }

	private void startWallpaper() {
		final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
        startActivity(Intent.createChooser(pickWallpaper, getString(R.string.menu_wallpaper))); 
	}

	private void openAllApplications() {
		if (((DLCApp)getApplication()).isFreeVersion()){
			Toast.makeText(this, string.free_version_hitn, Toast.LENGTH_LONG).show();
			startActivity(dlc_activity[ALL_APPLICATIONS].intent);
		}else {
			selection = ALL_APPLICATIONS;
			showDialog(ASK_PASSWORD_DLG);
		}
	}

	private void openSetting() {
		if (((DLCApp)getApplication()).isFreeVersion()){
			Toast.makeText(this, string.free_version_hitn, Toast.LENGTH_LONG).show();
			startActivity(dlc_activity[DLC_SETTING].intent);
		}else {
			selection = DLC_SETTING;
			showDialog(ASK_PASSWORD_DLG);
		}
	} 
	
	@Override
	public void onBackPressed() {}
}

class ClippedDrawable extends Drawable {
    private final Drawable wallpaper;

    public ClippedDrawable(Drawable wallpaper) {
        this.wallpaper = wallpaper;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        wallpaper.setBounds(left, top, left + wallpaper.getIntrinsicWidth(),
                top + wallpaper.getIntrinsicHeight());
    }

    public void draw(Canvas canvas) {
        wallpaper.draw(canvas);
    }

    public void setAlpha(int alpha) {
        wallpaper.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter cf) {
        wallpaper.setColorFilter(cf);
    }

    public int getOpacity() {
        return wallpaper.getOpacity();
    }
}
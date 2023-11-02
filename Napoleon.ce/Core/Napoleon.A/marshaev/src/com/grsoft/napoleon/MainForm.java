package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.CurrentAgent;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.SendData;
import com.grsoft.util.Consts;
import com.grsoft.util.MainExceptionHandler;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.Updater;
import com.grsoft.view.BaseActivity;

public class MainForm extends BaseActivity {
	private static final int DLG_MAIN_MENU = 10;

	List<MenuHandler> menu = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler(new MainExceptionHandler(this, Path.SHARED_FOLDER));

		setContentView(R.layout.mainform);
		
		Button b = (Button)findViewById(R.id.btnOrder);
		String text = "<b>Создать заказ</b><br/>Корректировать заказ";
		b.setText(Html.fromHtml(text));
		
		b.setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { SelectMatrix.open(MainForm.this); }
		});
	
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showDialog(R.id.confirm_send); }
		});
		
		findViewById(R.id.btnCheckUpdate).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				new Updater() { 
					protected void onPreExecute() { Toast.makeText(v.getContext(), R.string.check_updating, Toast.LENGTH_SHORT).show(); };
					protected void onPostExecute(Boolean result) {
						if(!result)
							Toast.makeText(v.getContext(), R.string.update_not_found, Toast.LENGTH_SHORT).show();
					};
					
				}.execute(v.getContext());
			}
    	});
	}
	
	@SuppressLint("DefaultLocale")
	void refreshSendButton() {
		Button b = (Button)findViewById(R.id.btnSend);
		int count = OrderDoc.instance().getDirtyDocuments().getDocuments().getCount();
		b.setEnabled(count > 0);
		String text = (count == 0) ? "Все заказы отправлены" : String.format("ОТПРАВИТЬ ЗАКАЗ\nНеотправлено - %d", count); 
		b.setText(text);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		refreshSendButton();
		
		CurrentAgent ca = CurrentAgent.get(this);
		if( ca == null ) {
			Setting.open(this);
		}
	}
	
	protected List<MenuHandler> createMainMenuList() {
		List<MenuHandler> mainMenu = new ArrayList<MenuHandler>();
		
		mainMenu.add(new MenuHandler(getString(R.string.setting), new Runnable() {			
			@Override public void run() { Setting.open(MainForm.this); }
		}));
		
		mainMenu.add(new MenuHandler(getString(R.string.sync), new Runnable() {			
			@Override public void run() { UpdateDB.open(MainForm.this); }
		}));

		mainMenu.add(new MenuHandler(getString(R.string.msg_list), new Runnable() {			
			@Override public void run() { Messages.open(MainForm.this); }
		}));

		mainMenu.add(new MenuHandler(getString(R.string.about), new Runnable() {			
			@Override public void run() { showAbout(MainForm.this); }
		}));

		mainMenu.add(new MenuHandler(getString(R.string.exit), new Runnable() {	@Override public void run() { exit(); }}));
		return mainMenu;
	}
	
	
	protected void exit() {
		if (!((CfgNpl)ConfigManager.getConfig()).isAutostart) {
			Intent intent = new Intent(this, Napoleon.serviceType);
			boolean stopped = stopService(intent);
			Log.d(Consts.D_TAG, "Service has been stopped:" + Boolean.toString(stopped));
		}
		finish();
	}
	
	@SuppressLint("InflateParams")
	public static void showAbout(final Activity owner)  {
        View messageView = owner.getLayoutInflater().inflate(R.layout.about, null, false);
        TextView tvLink = (TextView) messageView.findViewById(R.id.tvLink);
        AlertDialog.Builder builder = new AlertDialog.Builder(owner);
        builder.setView(messageView);
        builder.create();
        final AlertDialog dialog = builder.show();
        
        if(Features.LINKS_DISSALLOW){
	        tvLink.setEnabled(false);
	        tvLink.setMovementMethod(null);
        }
        
        tvLink.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) { new Thread(new Runnable() { @Override public void run() { dialog.dismiss(); } }).start(); }
		});
        
        messageView.findViewById(R.id.btnCheckUpdates).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				new Updater() { 
					protected void onPreExecute() { Toast.makeText(v.getContext(), R.string.check_updating, Toast.LENGTH_SHORT).show(); };
					protected void onPostExecute(Boolean result) {
						if(!result)
							Toast.makeText(v.getContext(), R.string.update_not_found, Toast.LENGTH_SHORT).show();
					};
					
				}.execute(v.getContext());
			}
    	});
	}	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == DLG_MAIN_MENU) {
			if(menu == null)
				menu = createMainMenuList();
			return createMenuDlg(getString(R.string.menu), menu);
		} else if( id == R.id.confirm_send) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Подтвердите отправку");
			b.setMessage("Отправить документы?");
			b.setNegativeButton("Нет", null);
			b.setPositiveButton("Да", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

					DataExchange.sendDocs(MainForm.this, new SendData.Handler() {
						@Override public void onSend(boolean result) {
							runOnUiThread(new Runnable() { @Override public void run() { refreshSendButton(); } });
						} 
					});  
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected Dialog createMenuDlg(String title, final List<MenuHandler> items){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
						
		int idx = 0;
		CharSequence[] titles = new CharSequence[items.size()];
		
		for(MenuHandler mh : items)
			titles[idx++] = mh.name;
		
		builder.setItems(titles, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which){
					items.get(which).handler.run();
			}
		});
		
		return builder.create();
	}
	
	@Override
	public void onBackPressed() {
		Toast.makeText(this, R.string.ask_to_exit,  Toast.LENGTH_LONG).show();
	}
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if( keyCode == KeyEvent.KEYCODE_MENU ) {
			showDialog(DLG_MAIN_MENU);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
}

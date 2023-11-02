package com.grsoft.napoleon;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Gather;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.dataobjects.impl.GatherImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.Util;

public class NapoleonEx extends Napoleon {
	
	protected static final int REMOVE_GATHER = 0;
	
	boolean viewArchive = false;
	NapoleonAdapter adapter;
	GatherImpl rmvGather;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == REMOVE_GATHER ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Подверджение");
			b.setMessage("");
			b.setPositiveButton("Да", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if( rmvGather != null) {
						rmvGather.delete();
						rmvGather.close();
						rmvGather = null;
						adapter.notifyDataSetChanged();
					}
					dialog.dismiss();
				}
			});
			
			b.setNegativeButton("Нет", null);
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == REMOVE_GATHER && rmvGather != null ) {
			String message = "Удалить накладную №";
			message += rmvGather.getData().id;
			((AlertDialog)dialog).setMessage(message);
		}
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		cfg.allowRotateScreen = true;
		try{
			adapter = new NapoleonAdapter(this);
			lvMainOrgs.setAdapter(adapter);
			lvMainOrgs.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					GatherImpl gimpl = (GatherImpl) adapter.getItem(position);
					gimpl.open(view.getContext());
				}
			});
			
			lvMainOrgs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					GatherImpl gimpl = (GatherImpl) adapter.getItem(position);
					if( gimpl.isExported() ) {
						rmvGather = gimpl;
						showDialog(REMOVE_GATHER);
					}
					return false;
				}
			});
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//findViewById(R.id.llTop).setVisibility(View.GONE);
		findViewById(R.id.btnDocFilter).setVisibility(View.GONE);
		findViewById(R.id.btnMode).setVisibility(View.GONE);
		findViewById(R.id.btnLines).setVisibility(View.GONE);
		findViewById(R.id.btnFind).setVisibility(View.GONE);
		
		findViewById(R.id.btnSync).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new SyncProcess(v.getContext(), v).execute((Void)null);
			}
		});
		
		findViewById(R.id.btnPriceList).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PriceList.open(NapoleonEx.this);
			}
		});

		findViewById(R.id.btnChangeMode).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				viewArchive = !viewArchive;
				ImageButton ib = (ImageButton)findViewById(R.id.btnChangeMode);
				ib.setImageResource((viewArchive) ? R.drawable.view_current : R.drawable.view_archive);
				adapter.refresh(viewArchive);
			}
		});
	}
	
	@Override
	protected int getResourceID() {
		return R.layout.mainex;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
	}
	
	protected ArrayList<MenuHandler> createMainMenuList() {
		mainMenu = new ArrayList<MenuHandler>();
		
		mainMenu.add(new MenuHandler("Настройки", new Runnable() {			
			@Override public void run() { Setting.open(NapoleonEx.this); }
		}));
		
		mainMenu.add(new MenuHandler("Очистить базу", new Runnable() {			
			@Override public void run() { new AsyncTask<Void, Void, Void>(){

				@Override
				protected Void doInBackground(Void... params) {
					DataBaseManager.clearBase();
					return null;
				}
				
				protected void onPostExecute(Void result) {
					((BaseAdapter)lvMainOrgs.getAdapter()).notifyDataSetChanged();
				};
				
			}.execute((Void)null); }
		}));
		
		mainMenu.add(new MenuHandler("О программе", new Runnable() {			
			@Override public void run() { showAbout(NapoleonEx.this); }
		}));

		mainMenu.add(new MenuHandler("Выход", new Runnable() {			
			@Override 
			public void run() { 
				if (!((CfgNpl)ConfigManager.getConfig()).isAutostart) {
					Intent intent = new Intent(NapoleonEx.this, serviceType);
					boolean stopped = NapoleonEx.this.stopService(intent);
					Log.d(Consts.D_TAG, "Service has been stopped:" + Boolean.toString(stopped));
				}
				finish();
			}
		}));

		mainMenuPrepared.menuPrepared(mainMenu, NapoleonEx.this);
		return mainMenu;
	}
}

class NapoleonAdapter extends DataBaseAdapter<Gather>{

	public NapoleonAdapter(Context context)
			throws IllegalAccessException, InstantiationException {
		super(context, new GatherImpl(), 
				"(params & " + ParamState.ofExported + ") <> " + 
						ParamState.ofExported, "substr(id,4)");
	}
	
	public void refresh(boolean exported) {
		if( cursor != null )
			cursor.close();
		String condition = "(params & " + ParamState.ofExported + ") " + (exported ? "= " : "<> ") + ParamState.ofExported;
		cursor = new Cursor<Gather>(new GatherImpl(), condition, "substr(id,4)");
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if(view == null)
		view = View.inflate(context, R.layout.gather_row, null);
	
		final GatherImpl gimpl = (GatherImpl) getItem(position);
		final Gather gather = gimpl.getData();
		
		StringBuilder caption = new StringBuilder();
		caption.append("<font color=blue><b>")
			.append(Util.simpleDateFormat.format(gather.date))
			.append("&nbsp;&nbsp;&nbsp;")
			.append(gather.id)
			.append("&nbsp;&nbsp;&nbsp;")
			.append(gather.name)
			.append("</b><br><i>")
			.append(gather.address)
			.append("</i></font>");
		
		((TextView)view.findViewById(R.id.tvName)).setText(Html.fromHtml(caption.toString()));
		
		((TextView)view.findViewById(R.id.tvRing)).setText(Integer.toString(gather.krug));
		
//		gimpl.read();
//		gimpl.close();
		if(gimpl.isComplete())
			view.setBackgroundResource(R.drawable.list_grey_selector);
		else if(gimpl.isInWork())
			view.setBackgroundResource(R.drawable.list_work_selector);
		else
			view.setBackgroundResource(R.drawable.list_selector);
		return view;
	}
	
}


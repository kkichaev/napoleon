package com.grsoft.napoleon;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;

import com.grsoft.dataobjects.DlvQuery;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.impl.DlvQueryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.util.MenuHandler;

public class MainEx extends Main{
	public static final int DLVQRYDLG = 3024;
	String id = "";
//	private String alertMessage = "";
	
	@Override protected int getResourceID() { return R.layout.mainex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnOrder).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { selectedType(OrderDoc.instance()); }
		});

		findViewById(R.id.btnBalance).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { selectedType(DebtDoc.instance()); }
		});

		findViewById(R.id.btnVisit).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { selectedType(VisitDoc.instance()); }
		});

		findViewById(R.id.btnDocs).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(DLG_DOC); }
		});
		
		findViewById(R.id.btnSync).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { UpdateDB.open(MainEx.this); }
		});
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		AdapterContextMenuInfo ami = (AdapterContextMenuInfo)menuInfo; 
		
		Org o = ((BaseMainAdapter) list.getAdapter()).getOrg(ami.position);
		if(o == null)
			return;
		
		this.id = o.id; 
		menu.add(getString(R.string.askfordelivery));
//		
//		View view = ((AdapterContextMenuInfo)menuInfo).targetView;
//		Object tag =  view.getTag();
//		if( tag instanceof OrgFolders )
//			return;
//		
//		Long rowid = (Long) tag;
//		OrgImpl orgImpl = new OrgImpl();
//		orgImpl.read(rowid);
//		orgImpl.close();
//		
//		this.id = orgImpl.getData().id; 
//		menu.add(getString(R.string.askfordelivery));
	};
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle().equals(getString(R.string.askfordelivery))){
			showDialog(DLVQRYDLG);
			return true;
		}else
			return super.onContextItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DLVQRYDLG)
			return createDlvQryDlg();
//		else if( id == STOP_DLG ) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("Внимание");
//			builder.setMessage("");
//			builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {				
//				@Override
//				public void onClick(DialogInterface dialog, int which) { orgClick.resumeClick(); }
//			});
//			
//			builder.setNegativeButton("Отменить", null);
//			return builder.create();
//		}
		else
			return super.onCreateDialog(id);
	}

	private Dialog createDlvQryDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final View view = View.inflate(this, R.layout.date_interval, null);
		builder.setView(view);
		view.findViewById(R.id.btnOK).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DlvQueryImpl impl = new DlvQueryImpl();
				DlvQuery d = impl.getData();
				d.id = MainEx.this.id;
				DatePicker dpBegin = (DatePicker) view.findViewById(R.id.dpBegin);
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, dpBegin.getYear());
				c.set(Calendar.MONTH, dpBegin.getMonth());
				c.set(Calendar.DAY_OF_MONTH, dpBegin.getDayOfMonth());
				d.begin = c.getTime();
				DatePicker dpEnd = (DatePicker) view.findViewById(R.id.dpEnd);
				c.set(Calendar.YEAR, dpEnd.getYear());
				c.set(Calendar.MONTH, dpEnd.getMonth());
				c.set(Calendar.DAY_OF_MONTH, dpEnd.getDayOfMonth());
				d.end = c.getTime();
				impl.write();
				impl.close();
				dismissDialog(DLVQRYDLG);
			}
		});
		
		return builder.create();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == DLVQRYDLG){
			DlvQueryImpl impl = new DlvQueryImpl();
			impl.getData().id = this.id;
			
			if(impl.read()){
				DlvQuery d = impl.getData();
				DatePicker dpBegin = (DatePicker) dialog.findViewById(R.id.dpBegin);
				dpBegin.updateDate(d.begin.getYear() + 1900, d.begin.getMonth(), d.begin.getDate());
				
				DatePicker dpEnd = (DatePicker) dialog.findViewById(R.id.dpEnd);
				dpEnd.updateDate(d.end.getYear() + 1900, d.end.getMonth(), d.end.getDate());
			}
			
			impl.close();
//		}else if ( id == STOP_DLG ) {
//			((AlertDialog)dialog).setMessage(alertMessage);
		} else if (id == DLG_MAIN_MENU){
			ListView list = ((AlertDialog)dialog).getListView();
			ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);
			
			SharedPreferences pref = getApplication().getSharedPreferences(BehaviorSettingEx.SETING_NAME, Context.MODE_PRIVATE);
			boolean allowAddOrg = pref.getBoolean(BehaviorSettingEx.ALLOW_ADD_ORG, false);
			
			String addOrg = getString(R.string.add_org);
			
			if(!allowAddOrg){
				for(MenuHandler h :mainMenu)
					if(h.name.equals(addOrg)){
						mainMenu.remove(h);
						break;
					}
			} else {
				MenuHandler hndlAddOrg = null;
				
				for(MenuHandler h :mainMenu)
					if(h.name.equals(addOrg)){
						hndlAddOrg = h;
						break;
					}
				
				if(hndlAddOrg == null)
					mainMenu.add(3, new MenuHandler(getString(R.string.add_org), new Runnable() {			
						@Override public void run() { PotenzialOrg.open(MainEx.this); }
					}));
				
			}
			
			for(int i = 0; i < mainMenu.size(); i++){
				String name = mainMenu.get(i).name; 
				if(name.equals(addOrg)){
					if (allowAddOrg)
						a.add(name);
				}else
					a.add(name);
			}
			
			list.setAdapter(a);
		}else
			super.onPrepareDialog(id, dialog);

	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		
		DocType selected = DocType.getCurDoc();
		
		if (selected == OrderDoc.instance() || selected == ReturnDoc.instance())
			findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
		else
			findViewById(R.id.tvTotalSum).setVisibility(View.VISIBLE);
	}
}

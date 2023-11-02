package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.NapoleonTask;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.Responce;
import com.grsoft.dataobjects.impl.NapoleonTaskImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ResponceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.napoleon.documents.ResponceDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.OrgFoldersTree;
import com.grsoft.napoleon.util.PhotoClickHandler;
import com.grsoft.napoleon.util.WeekDay;
import com.grsoft.napoleon.util.PhotoClickHandler.EventHandler;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;


public class NapoleonEx extends Napoleon implements EventHandler, SendResultListener {
	private static final String PIC_PATH = "picPath";
	
	OrgImpl fakeOrg = new OrgImpl();
	NapoleonTaskImpl task = new NapoleonTaskImpl();
	ResponceImpl currentResponse;
	String picPath;
	Dialog curDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setPrefValue(LIST_MODE, ListViewMode.ROUTE_LIST.val);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		fakeOrg.close();
		task.close();
		if( currentResponse != null )
			currentResponse.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(PIC_PATH, picPath);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		picPath = savedInstanceState.getString(PIC_PATH);
	}

	private OnClickListener btnDocClick() {
		return new OnClickListener() { @Override public void onClick(View v) { openDocuments(); }};
	}
	
	@Override
	protected int getResourceID() {	return R.layout.mainex; }
	
	public void openDocuments() {
		Documents.open(this, ExtrasConst.INVALID_ROWID, true);
	}
	
	@Override
	protected void onResume() {
		DocType.setCurDoc(OrderDoc.instance());
		super.onResume();
		findViewById(R.id.btnDocFilter).setOnClickListener(btnDocClick());
		
	}
	
	@Override
	protected OnItemClickListener getItemOnClickListner() {
		return new OrglListOnClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Object tag = arg1.getTag();
				if( tag instanceof String) {
					Bundle args = new Bundle();
					args.putString(ExtrasConst.ORG_ID_STR, (String)tag);
					showDialog(R.id.responce_dlg, args);
				} else
					super.onItemClick(arg0, arg1, arg2, arg3);
			}
		};
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch(id){
		case R.id.responce_dlg:
			return createResponceDlg();
		default:
			return super.onCreateDialog(id, args);
		}
	}

	private Dialog createResponceDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.responce_dlg_title);
		builder.setView(View.inflate(this, R.layout.responcedlg, null));
//		builder.setPositiveButton(R.string.ok, onOkResponce());
//		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}
	
//	private DialogInterface.OnClickListener onOkResponce() {
//		return new DialogInterface.OnClickListener(){
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				Dialog dlg = (Dialog)dialog;
//				EditText edText = (EditText)dlg.findViewById(R.id.edText);
//				String text = edText.getText().toString().trim();
//				
//				if( currentResponse != null ) {
//					currentResponse.read(currentResponse.getRowid(), false);
//					Responce r = currentResponse.getData();
//					if(text.length() > 0 || r.items.size() > 0){
//						r.params = 0;
//						r.remark = text;
//						currentResponse.write();
//					}else
//						currentResponse.delete();
//					((BaseAdapter)lvMainOrgs.getAdapter()).notifyDataSetChanged();
//				}
//			}
//		};
//	}
	
	@Override
	protected void openUpdateActivity() {
		setTopLevelForTableHeader();
		super.openUpdateActivity();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		switch(id){
		case R.id.responce_dlg:
			prepareResponceDlg(dialog, args);
		default:
			super.onPrepareDialog(id, dialog);
		}
	}

	public ResponceImpl getResponce(String taskId) {
		ResponceImpl result = new ResponceImpl();
		
		Responce r = result.getData();
		r.date = Util.getDate();
		r.id = taskId;
		
		if(!result.read())
			result.init(this, taskId, GPSUtilNew.getLastKnownLocation());
		
		result.close();
		
		return result;
	}
	
	boolean saveCurrentResponse(Dialog dialog) {
		if( currentResponse == null )
			return false;
		
		boolean ret = false;
		EditText edText = (EditText)dialog.findViewById(R.id.edText);
		String text = edText.getText().toString().trim();
		
		currentResponse.read(currentResponse.getRowid(), false);
		if( currentResponse.isEditable() ) {
			Responce r = currentResponse.getData();
			if(text.length() > 0 || r.items.size() > 0){
				r.params = 0;
				r.remark = text;
				currentResponse.write();
				ret = true;
			}else
				currentResponse.delete();
		}
		return ret;
	}
	
	private void prepareResponceDlg(final Dialog dialog, Bundle args) {
		currentResponse =  getResponce(args.getString(ExtrasConst.ORG_ID_STR));
		EditText edText =  (EditText) dialog.findViewById(R.id.edText);
		edText.setText(currentResponse.getData().remark);
		edText.setEnabled(currentResponse.isEditable());
		
		View ph = dialog.findViewById(R.id.btnPhoto);
		if( ph != null ) {
			ph.setEnabled(currentResponse.isEditable());
			if( currentResponse.isEditable() ) {
				ph.setOnClickListener(new PhotoClickHandler(currentResponse, this, ResponceDoc.instance()) );
			}
		}
		
		dialog.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveCurrentResponse(dialog);
				((BaseAdapter)lvMainOrgs.getAdapter()).notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		
		dialog.findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( saveCurrentResponse(dialog)) {
					curDialog = dialog;
					new DocumentSender(NapoleonEx.this, dialog.findViewById(R.id.btnSend), 
						ResponceDoc.instance().getObjectName(), currentResponse, currentResponse.getRowid(), NapoleonEx.this).execute((Void[])null);
				} else
					dialog.dismiss();
			}
		});
	}
	
	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		DocType.getCurDoc().setMainView(view, linesController, oi, os);
		TextView tvOrgName = (TextView)view.findViewById(R.id.tvOrgName);
		ResponceImpl responce = new ResponceImpl();
		Responce re = responce.getData();
		re.id = oi.getData().id;
		re.date = Util.getDate();
		if (responce.read())
			tvOrgName.setTextColor(getResources().getColor(R.color.green));
		responce.close();
	}
	
	@Override
	protected OrgFoldersAdapter getOrgFoldersAdapter() {
		return new OrgFoldersAdapter(){
			@Override
			public void refresh() {
				super.refresh();
				refreshCurrentFolder();
				notifyDataSetChanged();
			}
			@Override
			protected OrgFoldersTree createOrgFoldersTree() { return new TaskTree(); }
		};
	}

	class TaskTree extends OrgFoldersTree {
		
		@Override
		public Object makeTag(int pos) {
			Object dataObject = getItem(pos);
			if (dataObject instanceof OrgFolders)
				return dataObject;
			else if (dataObject instanceof OrgFolderItem)
				return ((OrgFolderItem) dataObject).name;
			
			return null;
		}
		
		@Override
		public OrgImpl getOrg(int pos) {
			Object dataObject = getItem(pos);
			if (dataObject instanceof OrgFolderItem) {
				NapoleonTask t = task.getData();
				t.id = ((OrgFolderItem)dataObject).name;
				if( task.read() ) {
					Org o = fakeOrg.getData();
					o.name = t.task;
					o.id = t.id;
					return fakeOrg;
				}
			}
			return null;
		}
		
		@Override
		protected void loadData() {
			orgFolders.clear();
			
			DbWriter.checkDBTable(Responce.class);
			String now = Long.toString(Util.getDate().getTime());
			String tmrw = Long.toString(Util.getDate().getTime() + 24000*3600);
			String rtable = new Responce().getTableName();
			
			final HashMap<WeekDay, OrgFolders> data = new HashMap<WeekDay, OrgFolders>();
			DataTraveler.travel(NapoleonTask.class, new DataTraveler.Travel<NapoleonTask>() {

				@Override
				public boolean travel(DataTraveler<NapoleonTask> item) {
					Calendar c = Calendar.getInstance();
					c.setTime(item.data.start);
					int dw = c.get(Calendar.DAY_OF_WEEK);
					while(true) {
						WeekDay wd = WeekDay.getDayBySystemId(c.get(Calendar.DAY_OF_WEEK));
						OrgFolders od = data.get(wd);
						if(od == null) {
							od = new OrgFolders();
							od.name = wd.getCaption();
							od.items = new ArrayList<OrgFolderItem>();
							data.put(wd, od);
						}
						OrgFolderItem ofi = new OrgFolderItem();
						ofi.name = item.data.id;
						ofi.pos = od.items.size();
						od.items.add(ofi);
						
						c.add(Calendar.DAY_OF_MONTH, 1);
						if( c.get(Calendar.DAY_OF_WEEK) == dw)
							break;
					}
					return true;
				}
			}, 
			"start <= " + tmrw + " and (end >= " + now + " or not (id in (select id from " + rtable +  ")))", "start");
//			"end >= " + Long.toString(Util.getDate().getTime()), "start");
		
			orgFolders.addAll(data.values());
			Collections.sort(orgFolders, new Comparator<OrgFolders>() {

				@Override
				public int compare(OrgFolders lhs, OrgFolders rhs) {
					WeekDay lwd = WeekDay.getWeekDay(lhs.name);
					WeekDay rwd = WeekDay.getWeekDay(rhs.name);
					return WeekDay.compare(lwd, rwd);
				}
			});
		}
	}

	@Override
	public void prepareBoforeClick() {
		currentResponse.write();
	}

	@Override
	public void makePhotoFile(File newFile) {
		picPath = newFile.getAbsolutePath();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == PhotoClickHandler.CAMERA_ACTIVITY && resultCode == RESULT_OK){
			if(currentResponse != null && picPath != null && picPath.trim().length() > 0)
				((PhotoDocument)currentResponse).addPhoto(picPath.getBytes());
		}
	}

	@Override
	public void postSendExecute(boolean result) {
		if( curDialog != null ) {

			try {
				curDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			curDialog = null;
		}
	}
}


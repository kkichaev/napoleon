package com.grsoft.dataobjects.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Contract;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Target;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.ContractDoc;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.network.DocExportListener;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class ScriptImplEx extends ScriptImpl {
//	private ScrAssign scr = new ScrAssign();
	private ContractDefImpl cdef = new ContractDefImpl();
	private Map<Integer,Integer> colorMap = new HashMap<Integer, Integer>();
	public static String CURRENT_SCRIPT_ROW_ID = "CURRENT_SCRIPT_ROW_ID"; 

	@Override
	public String getDescription(Context context) {
		StringBuilder sb = new StringBuilder(super.getDescription(context));
		ScriptDefImpl sdef = new ScriptDefImpl();
		if(sdef.read("id", data.scriptId)){
			String cid = getIdContract(sdef.getData()); 
			
			if(cid != null){
				ContractDefImpl cde = new ContractDefImpl();
				if(cde.read("id", cid)){
					sb.append(" (").append(cde.getData().name).append(") ");
				}
			}
		}
		
		return sb.toString();
	}
	protected boolean isCanDo(ContractDefImpl cd, long now, ScriptDefItem i) {
		return cd.read("id", i.condParam) && cd.getData().start.getTime() <= now && cd.getData().finish.getTime() >= now;
	}
	
//	private boolean isScriptAssignet(int sid){
//		boolean result = false;
//		
//	    for(ScrAssignItem i : scr.items)
//	    	if(i.id == sid ){
//	    		result = true;
//	    		break;	
//	    	}
//		
//		return result;
//	}
	
	private String getIdContract(ScriptDef ds){
		String result = null;
		String con = ContractDoc.instance().getObjectName();
		
		for(ScriptDefItem si : ds.items)
			if(si.curType.equals(con)){
				result = si.condParam;
				break;
			}
		
		return result;
	}
	
	private boolean contractToday(String id, String defid){
		boolean result = false;
		
		Date begin = Util.getDate();
		Calendar cal = Calendar.getInstance();
		cal.setTime(begin);
		cal.add(Calendar.DATE, 1);
		Date end = cal.getTime();
		
		SQLiteDatabase db = DataBaseManager.getDataBase();
		android.database.Cursor c = null;
		
		try{
			c = db.query(DataObjectInfo.getInstance().getTableName(Contract.class), new String[]{"created"},
					"created>=? and created<? and id=? and def=?", 
					new String[]{Long.toString(begin.getTime()), Long.toString(end.getTime()), id, defid}, null, null, null);
			
			if(c.moveToFirst())
				result = true;
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(c != null)
				c.close();
		}
		
		return result;
	}
	
	protected AlertDialog.Builder createSelectVariantDlg(final Context context, final String orgId, final GpsCoord gpsCoord, CharSequence[] items) {
		
		colorMap.clear();
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle(R.string.select_variant);
		b.setAdapter(new ArrayAdapter<CharSequence>(context, android.R.layout.select_dialog_item, android.R.id.text1, items){
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						View result = super.getView(position, convertView, parent);
						int color = context.getResources().getColor(R.color.black);
						
						if(colorMap.containsKey(position))
							color = colorMap.get(position);
						else{
							ScriptDef s = scripts.get(position);
							String cid = getIdContract(s); 
							
							if(cid != null && contractToday(orgId, cdef.getData().id))
								color = context.getResources().getColor(R.color.red);
							
							colorMap.put(position, color);
						}
						
						((TextView) result.findViewById(android.R.id.text1)).setTextColor(color);
						
						return result;
					}
				}, 
				
				new DialogInterface.OnClickListener() {			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						initInternal(context, orgId, gpsCoord, scripts.get(which));
					}
				}
			);
		
		return b;
	}
	
	@Override
	public void openDoc(Context c, int index, ScriptDef defDoc) {
		if (isEditable() && index == 0){
			SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(c);
			Editor e = p.edit();
			e.putLong(CURRENT_SCRIPT_ROW_ID, getRowid());
			e.commit();
		}
			
		super.openDoc(c, index, defDoc);
	}
	
	@Override
		public List<DocExportListener> getSendedDocuments() {
			List<DocExportListener> result = super.getSendedDocuments();
			
			List<Long> ids = DbReader.readIds(DataObjectInfo.getInstance().getTableName(Target.class), 
					String.format("scriptCreated = %d", data.created.getTime()), null);
			
			if(ids.size() > 0) {
				TargetImpl tg = new TargetImpl();
				tg.read(ids.get(0));
				tg.close();
				
				result.add(new DocSendListner("Target", tg));
			}
			
			return result;
		}
}

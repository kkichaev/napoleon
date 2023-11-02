package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Contract;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ScrAssign;
import com.grsoft.dataobjects.ScrAssignItem;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.ContractDoc;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;


public class ScriptImplEx extends ScriptImpl {
	private ScrAssign scr = new ScrAssign();
	private ContractDefImpl cdef = new ContractDefImpl();
	private Map<Integer,Integer> colorMap = new HashMap<Integer, Integer>();
	
	@Override
	protected void scriptListProcess(String orgid) {
		List<ScriptDef> list = new ArrayList<ScriptDef>();
		String con = ContractDoc.instance().getObjectName();
		long now = new Date().getTime();
		
		DbReader reader = new DbReader();
		String where = "id = (select sid from org where id = '" + orgid + "')";
		if (reader.select(scr, DataObjectInfo.getInstance().getTableName(scr.getClass()), where)){
			for(ScriptDef sd : scripts)
			{
				boolean hc = false;
				
				for(ScriptDefItem i : sd.items)
				{
					if (i.curType.equals(con)){
						if(!isScriptAssignet(sd.id))
							continue;
						
						hc = true;
						
						if (isCanDo(cdef, now, i))
							list.add(sd);
						
						break;
					}
				}
				
				if(!hc)
					list.add(sd);
			}
		}
		
		scripts = list;
	}

	protected boolean isCanDo(ContractDefImpl cd, long now, ScriptDefItem i) {
		return cd.read("id", i.condParam) && cd.getData().start.getTime() <= now && cd.getData().finish.getTime() >= now;
	}
	
	private boolean isScriptAssignet(int sid){
		boolean result = false;
		
	    for(ScrAssignItem i : scr.items)
	    	if(i.id == sid ){
	    		result = true;
	    		break;	
	    	}
		
		return result;
	}
	
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
}

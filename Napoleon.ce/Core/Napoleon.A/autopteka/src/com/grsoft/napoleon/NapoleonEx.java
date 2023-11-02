package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class NapoleonEx extends Main {	
	static final int ORG_INFO = 1000;

	protected static final int DLG_WARNING_IF_ORG_IN_STOP_LIST = 0;
	
	boolean loadedDebs = false;
	private Org infoOrg = null;
	private HashMap<String, Integer> outDebs = new HashMap<String, Integer>();
	
	@Override
	protected void onResume() {
		loadedDebs = false;
		super.onResume();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == ORG_INFO )
			return createInfoDialog();
		
		if( id == DLG_WARNING_IF_ORG_IN_STOP_LIST ) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Внимание");
			if( Features.BLOCK_IN_STOP_LIST ) {
				builder.setMessage("Клиент находится в стоп-листе!");
				builder.setPositiveButton("ОК", null);			
			} else {
				builder.setMessage("Клиент находится в стоп-листе, " +
					"заявка может быть не обработана.");
				builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {				
					@Override
					public void onClick(DialogInterface dialog, int which) {  NapoleonEx.super.openOrg(infoOrg); }
				});
				
				builder.setNegativeButton("Отменить", null);
			}
			return builder.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		if( id == ORG_INFO )
			setInfo((AlertDialog)dialog);
	}
	
	@Override
	protected void drawOrg(Org oi, View view) {
		super.drawOrg(oi, view);

		int value = getOutDebs(oi);
		if( value > 0 ) {
			TextView tv = (TextView)view.findViewById(R.id.tvOrgName);
			tv.setTextColor(Color.RED);			
		}
	}
	
	/**
	 * Просроченная задолженность по организации
	 */
	private int getOutDebs(Org org) {
		if( !loadedDebs ) {
			loadedDebs = true;
			
			outDebs.clear();
			
			SQLiteDatabase db = DataBaseManager.getDataBase();
			
			String table = DataObjectInfo.getInstance().getTableName(Delivery.class);
			
			String sql = "SELECT SUM(sumD), id FROM " + table + " WHERE paydate < ? GROUP BY id";
			
			Date curDate = new Date();
			String[] args = { Long.toString(curDate.getTime()) };
	
			try {
				Cursor c = db.rawQuery(sql, args);
				while( c.moveToNext() )
					outDebs.put(c.getString(1), c.getInt(0));
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Integer v = outDebs.get(org.id);
		return ((v != null) ? v : 0);
	}
	
	private long getBalance(Org org) {
		OrgSumImpl oi = new OrgSumImpl();
		OrgSum os = oi.getData();
		os.id = org.id;
		os.type = DebtDoc.instance().getName();
		oi.read();
		oi.close();
		
		return os.sum;
	}
	
	private void setInfo(AlertDialog dialog) {
		if( infoOrg == null )
			return;
		
		OrgEx org = (OrgEx)infoOrg;
		int outDeb = getOutDebs(infoOrg);
		int plan = org.plan;
		int fact = org.fact;
		int pc = (plan <= 0 || fact < 0) ? 0 : (int)((long)fact * Consts.SUM_SCALE * Consts.SUM_SCALE / plan);
		long balance = getBalance(infoOrg);
		
		StringBuilder text = new StringBuilder(); 
		text.append("ПЛАН: ");
		text.append(Util.IntToScaleStr(plan, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		text.append(" р.\nОТГР.: ");
		text.append(Util.IntToScaleStr(fact, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		text.append(" р.\nВЫПОЛН.: ");
		text.append(Util.IntToScaleStr(pc, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		text.append(" %\n\nДОЛГ: ");
		text.append(Util.IntToScaleStr(balance, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		text.append(" р.\nПР.Д.: ");
		text.append(Util.IntToScaleStr(outDeb, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		text.append(" р.");

		   if( (org.flags & OrgEx.CHECK_REST) != 0 )
			   text.append("\n\n\nСъем остатков");
		
		dialog.setMessage(text.toString());
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "ОК", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			   if( getOutDebs(infoOrg) > 0 ) {
				   showDialog(DLG_WARNING_IF_ORG_IN_STOP_LIST);
			   } else {
				   NapoleonEx.super.openOrg(infoOrg);
			   }
			}
		});
	}
	
	private Dialog createInfoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Информация");
		builder.setMessage("");
		builder.setPositiveButton("OK", null);
		return builder.create();
	}

	@Override
	public void openOrg(Org org) {
		infoOrg = org;
		showDialog(ORG_INFO);
	}
}

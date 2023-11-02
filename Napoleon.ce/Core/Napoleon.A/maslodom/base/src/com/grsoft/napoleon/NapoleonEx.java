package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Pair;
import com.grsoft.util.Util;

public class NapoleonEx extends Napoleon {
	static final int ORG_INFO = R.id.orginfo;
	private OrgImpl infoOrg = null;
	public static boolean loadedDebs = false;
	private HashMap<String, Pair<Integer, Long>> outDebs = new HashMap<String, Pair<Integer, Long>>();

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == ORG_INFO)
			return createInfoDialog();
		else
			return super.onCreateDialog(id);

	}

	private Dialog createInfoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Èíôîðìàöèÿ");
		builder.setMessage("");
		builder.setPositiveButton("OK", null);
		return builder.create();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		if (id == ORG_INFO)
			setInfo((AlertDialog) dialog);
	}

	private void setInfo(AlertDialog dialog) {
		if (infoOrg == null)
			return;

		Pair<Integer, Long> debtInfo = getOutDebs(infoOrg); 
		int outDeb = debtInfo == null ? 0 : debtInfo.first;
		Date payDate = debtInfo == null ? new Date() : new Date(debtInfo.second);
		int balance = getBalance(infoOrg);

		StringBuilder text = new StringBuilder();
		text.append("ÄÎËÃ: ");
		text.append(Util.IntToScaleStr(balance, Consts.SUM_SCALE,
				Util.DEC_DELIM, false));
		text.append(" ð.\nÏÐ.Ä.: ");
		text.append(Util.IntToScaleStr(outDeb, Consts.SUM_SCALE,
				Util.DEC_DELIM, false));
		text.append(" ð.");
		text.append("\nËèìèò: ");
		text.append(Util.IntToScaleStr(((OrgEx)infoOrg.getData()).limit - balance, Consts.SUM_SCALE,
				Util.DEC_DELIM, false));
		text.append(" ð.");
		
		if(outDeb > 0){
			text.append("\n\n");
			if(DatePeriod.daysDiff(payDate, new Date()) > 5){
				text.append(getString(R.string.client_in_stop_list_order_rejected));
//				((OrgEx)infoOrg.getData()).blocked = 1;
//				infoOrg.write();
//				infoOrg.close();
			}else
				text.append(getString(R.string.out_deb));
				
		}
		
		dialog.setMessage(text.toString());
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "ÎÊ",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						OrgClick ocl = (OrgClick) lvMainOrgs
								.getOnItemClickListener();
						if (ocl != null)
							ocl.resume(infoOrg);
					}
				});
	}

	@Override
	protected OnItemClickListener getItemOnClickListner() {
		return new OrgClick();
	}

	class OrgClick extends OrglListOnClickListener {
		@Override
		protected void openOrg(OrgImpl oi) {
			infoOrg = oi;
			clickedOrg = oi;
			showDialog(ORG_INFO);
		}

		public void resume(OrgImpl oi) {
			super.openOrg(oi);
		}
	}

	private Pair<Integer, Long> getOutDebs(OrgImpl org) {
		if (!loadedDebs) {
			loadedDebs = true;

			outDebs.clear();

			SQLiteDatabase db = DataBaseManager.getDataBase();

			String table = DataObjectInfo.getInstance().getTableName(
					Delivery.class);

			String sql = "SELECT SUM(sumD), MAX(paydate), id FROM " + table
					+ " WHERE paydate < ? GROUP BY id";

			Date curDate = new Date();
			String[] args = { Long.toString(curDate.getTime()) };

			try {
				Cursor c = db.rawQuery(sql, args);
				while (c.moveToNext())
					outDebs.put(c.getString(2), new Pair<Integer, Long>(c.getInt(0), c.getLong(1)));
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return outDebs.get(org.getData().id);
	}

	private int getBalance(OrgImpl org) {
		OrgSumImpl oi = new OrgSumImpl();
		OrgSum os = oi.getData();
		os.id = org.getData().id;
		os.type = DebtDoc.instance().getName();
		oi.read();
		oi.close();

		return (int)os.sum;
	}
	
	protected void drawOrg(OrgImpl oi, View view) {
		DocType.getCurDoc().setMainView(view, linesController, oi, os);
	}
}

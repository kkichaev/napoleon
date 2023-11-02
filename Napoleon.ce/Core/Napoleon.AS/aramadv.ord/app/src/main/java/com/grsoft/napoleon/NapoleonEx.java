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
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class NapoleonEx extends Main {
	static final int ORG_INFO = R.id.orginfo;
	private Org infoOrg = null;
	public static HashMap<String, Integer> outDebs = new HashMap<String, Integer>();

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == ORG_INFO)
			return createInfoDialog();
		else
			return super.onCreateDialog(id);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		outDebs.clear();

		String table = DataObjectInfo.getInstance().getTableName(Delivery.class);
		String sql = "SELECT SUM(sumD), id FROM " + table + " WHERE paydate < ? GROUP BY id";

		Date curDate = new Date();
		String[] args = { Long.toString(curDate.getTime()) };
		try {
			SQLiteDatabase db = DataBaseManager.getDataBase();
			Cursor c = db.rawQuery(sql, args);
			while (c.moveToNext()) {
				int val = c.getInt(0);
				if( val > 0 )
					outDebs.put(c.getString(1), val);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		int outDeb = getOutDebs(infoOrg);
		long balance = getBalance(infoOrg);

		StringBuilder text = new StringBuilder();
		text.append("ÄÎËÃ: ");
		text.append(Util.IntToScaleStr(balance, Consts.SUM_SCALE,
				Util.DEC_DELIM, false));
		text.append(" ð.\nÏÐ.Ä.: ");
		text.append(Util.IntToScaleStr(outDeb, Consts.SUM_SCALE,
				Util.DEC_DELIM, false));
		text.append(" ð.");

		dialog.setMessage(text.toString());
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "ÎÊ",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						DocumentsW.open(NapoleonEx.this, infoOrg);
					}
				});
	}

	@Override
	public void openOrg(Org org, int pos) {
		infoOrg = org;
		showDialog(ORG_INFO);
	}

	private int getOutDebs(Org org) {
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
}

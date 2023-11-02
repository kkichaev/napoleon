package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.TextView;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.IDelivery;
import com.grsoft.dataobjects.IOrg;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {

	private static final int NOT_ALLOW_NEW_DOC = 333;
	private IOrg orgData;
	private TextView tvCredit;
	private TextView tvPeriod;
	private boolean allowCreateNewDoc = true;
	public static final String CREDIT_OST = "credit_ost";
	public static final String CREDIT_PREF_NAME = "credit_pref_name";
	private CheckBox cbACL;
	private CheckBox cbAPL;
	private int dlvSum = 0;
	public static DocType currentDocType = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences pref = getSharedPreferences(CREDIT_PREF_NAME,
				Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putInt(CREDIT_OST, 0);
		ed.commit();

		tvCredit = (TextView) findViewById(R.id.tvCredit);
		tvPeriod = (TextView) findViewById(R.id.tvPeriod);
		cbACL = (CheckBox) findViewById(R.id.cbACL);
		cbACL.setClickable(false);
		cbAPL = (CheckBox) findViewById(R.id.cbAPL);
		cbAPL.setClickable(false);
	}

	protected void refreshCreditLimit() {
		tvCredit.setBackgroundColor(Color.WHITE);
		tvPeriod.setBackgroundColor(Color.WHITE);
		allowCreateNewDoc = true;
		
		orgData = (IOrg) org.getData();
		DebtDoc dde = (DebtDoc) DebtDoc.instance(); 
		DebtDocList list = (DebtDocList)dde.docList(
				orgData.getId(), "", "");
		list.close();
		dlvSum = 0;
		Date minDate = null;
		
		int sz = list.getCount();
		
		if (sz > 0)
			for (int i = 0; i < sz; i++) {
				Document<?> d = list.get(i);

				if (d != null && d.getData() instanceof IDelivery) {
					IDelivery dlv = (IDelivery) d.getData();
	
					if (dlv.getSumD() > 0
							&& (minDate == null || minDate.compareTo(dlv.getDate()) > 0))
						minDate = dlv.getDate();
					
					dlvSum += dlv.getSumD();
				}
			}

		tvCredit.setText(makeCreditTxt(dlvSum));
		tvPeriod.setText(makePeriodTxt(minDate));
		cbAPL.setChecked(orgData.isApplyPeriodLimit());
		cbACL.setChecked(orgData.isApplyCreditLimit());
		
		if(DocType.getCurDoc() == DebtDoc.instance()){
			updateTotalSum(dlvSum, 0);
			OrgSum os = new OrgSum();
			
			os.id = org.getData().id;
			os.sum = dlvSum;
			os.type = DebtDoc.instance().getName();
			
			DbWriter w = new DbWriter();
			DbWriter.checkDBTable(OrgSum.class);
			w.insertRecord(os);
			w.close();
		}
	}

	private CharSequence makePeriodTxt(Date date) {
		StringBuilder result = new StringBuilder();

		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		long e = 0;
		long s = 0;
		
		if (date != null) {
			s = daysDiff(date.getTime(), now.getTime());
			e = s - orgData.getPeriod();
		}

		if (date == null || e <= 0){
			e = 0;
		}else {
			tvPeriod.setBackgroundColor(Color.RED);
			if (allowCreateNewDoc && orgData.isApplyPeriodLimit())
				allowCreateNewDoc = false;
		}

		result.append("Срок кредита: ")
				.append(Integer.toString(orgData.getPeriod()));
		result.append("   Срок долга: ").append(Long.toString(s));
		result.append("   Просрочка: ").append(Long.toString(e));

		return result.toString();
	}

	public static long daysDiff(long from, long to) {
		return (long)((to - from) / 86400000D); // 1000 * 60 * 60 * 24
	}

	private CharSequence makeCreditTxt(int sum) {
		StringBuilder result = new StringBuilder();

		result.append("Сумма кредита: ").append(
				Util.IntToScaleStr(orgData.getCredit(), Consts.SUM_SCALE));
		result.append(" Тек. долг: ").append(
				Util.IntToScaleStr(sum < 0 ? 0 : sum, Consts.SUM_SCALE));
		int ost = orgData.getCredit() - sum;
		result.append(" Ост: ").append(
				Util.IntToScaleStr(ost, Consts.SUM_SCALE));

		if (ost < 0) {
			tvCredit.setBackgroundColor(Color.RED);
			if (allowCreateNewDoc && orgData.isApplyCreditLimit())
				allowCreateNewDoc = false;
		}

		SharedPreferences pref = getSharedPreferences(CREDIT_PREF_NAME,
				Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putInt(CREDIT_OST, ost);
		ed.commit();

		return result.toString();
	}

	protected DocType adjustDocType(DocType docType){
		return docType;
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		docType = adjustDocType(docType);
		
		TextView tvSum = (TextView) findViewById(R.id.tvSum);
		
		if(docType instanceof DebtDoc)
			tvSum.setVisibility(View.VISIBLE);
		else
			tvSum.setVisibility(View.GONE);
		
		adapter = null;
		
		super.adjustViewForDocType(docType);
		
		refreshCreditLimit();
	}

	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		if(docType instanceof DebtDoc)
			return new DocAdaptEx(this, docType, id, "date");
		else
			return super.createAdapter(docType, id);
	}
	
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}

	@Override
	protected void doCreate() {
		if (allowCreateNewDoc || 
				DocType.getCurDoc() instanceof IncassDoc ||
				DocType.getCurDoc() instanceof VisitDoc)
			super.doCreate();
		else
			showDialog(NOT_ALLOW_NEW_DOC);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == NOT_ALLOW_NEW_DOC)
			return createNotAllowNewDocDlg();
		return super.onCreateDialog(id);
	}

	private Dialog createNotAllowNewDocDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.error);
		builder.setMessage(R.string.creatid_exceed);
		builder.setPositiveButton(R.string.ok, null);

		return builder.create();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		Document<?> doc = (Document<?>) adapter
				.getItem(((AdapterView.AdapterContextMenuInfo) menuInfo).position);

		if (doc instanceof CreatableDocument<?>
				&& (((CreatableDocument<?>) doc).isExported())){
			MenuItem mi = menu.findItem(R.id.itDelete);
			
			if(mi != null)
				mi.setVisible(false);
		}
	}
	
	class DocAdaptEx extends DocumentsAdapter{
		public DocAdaptEx(Context context, DocType docType, String orgId, String order) {
			super(context, docType, orgId, order, R.layout.docs_row_ex);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		currentDocType = DocType.getCurDoc();
	}
	
	@Override
	protected void onResume() {
		if(currentDocType != null)
			DocType.setCurDoc(currentDocType);

		super.onResume();
	}
}

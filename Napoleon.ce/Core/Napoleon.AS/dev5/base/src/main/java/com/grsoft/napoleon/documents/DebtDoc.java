/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Документ - Долги
 *
 * kki   28/02/2011   creating
 */
package com.grsoft.napoleon.documents;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Balance;
import com.grsoft.dataobjects.BalanceDoc;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.BalanceDocImpl;
import com.grsoft.aceteam.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.aceteam.R;
import com.grsoft.network.exception.RuntimeException;

public class DebtDoc extends DocType {
	static public final String DOC_NAME = "Долги";
	static protected DebtDoc instance = null;

	static String OPENING_BALANCE = "";
	static String OVERPAYMENT = "";

	public static void init(Context context) {
		OPENING_BALANCE = context.getString(R.string.start_balance);
		OVERPAYMENT = context.getString(R.string.overpayment);
	}
	
	/**
	 * При расчете баланса загружать накладные, иначе используется только таблица Payment
	 */
	static public boolean LoadDelivery = true;
	
	protected DebtDoc() {
		super(DOC_NAME, Debt.class);
	}
	
	protected DebtDoc(String name, Class<? extends Document<?>> docClass) {
		super(name, docClass);
	}
	
	@Override
	public int getViewTextColor(Context context, Document<?> doc) {
		if(Features.MARK_OVERDUE_DEBTS && doc instanceof BalanceDocImpl) {
			BalanceDoc d = (BalanceDoc) doc.getData();
			if(doc.sum() > 0 && (new Date()).compareTo(d.src.payDate) > 0)
				return Color.RED;
		}
		return super.getViewTextColor(context, doc);
	}
	
	static public DocType instance() {
		if( instance == null )
			instance = new DebtDoc();
		return instance;
	}
	
	@Override
	public DocList docList(String orgId, String order, String where) {
		String whereStr = getOrgWhere(orgId);
		
		if( where != null && where.length() > 0 ) {
			if( whereStr.length() > 0 )
				whereStr += " AND ";
			whereStr += where;
		}
		
		return createDebtDocList(whereStr, order, LoadDelivery);
	}

	protected String getOrgWhere(String orgId) {
		return (orgId == null) ? "" : "id='" + orgId + "'";
	}
	
	protected DebtDocList createDebtDocList(String where, String order, boolean LoadDelivery){
		return new DebtDocList(where, order, LoadDelivery, OPENING_BALANCE, OVERPAYMENT);
	}
	
	public static void postUpdateProcess() throws RuntimeException {
		if( instance != null )
			instance.refreshDocSum();
	}

	@Override
	public void refreshDocSum() throws RuntimeException {
		OrgSum os = new OrgSum();
		DbWriter.checkDBTable(OrgSum.class);
		try {
			String stmt = String.format("DELETE FROM %s WHERE type='%s'", os.getTableName(), name);
			DataBaseManager.getDataBase().execSQL(stmt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DbWriter w = new DbWriter();
		for(Balance b : DbReader.fetch(Balance.class)) {
			os.id = b.id;
			os.sum = b.sum;
			os.type = name;
			w.insertRecord(os);
		}
		w.close();
	}

	@Override
	public void viewClosed(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setText("Сумма");
	}

	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv;
		// main.xml
		tv = (TextView)documentsView.findViewById(R.id.tvMainDocValColTitle);
		if( tv != null )
			tv.setText("Долг");

		//documents.xml
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null ) {
			tv.setVisibility(View.VISIBLE);
			tv.setText("Долг");
		}
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.debt_doc;
	}
	
	@Override
	public int getResurce2Id() {
		return R.drawable.debt_doc_2;
	}
	
	@Override
	public int getDocTitle() {
		return R.string.debt_doc_title;
	}
}

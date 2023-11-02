package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.grsoft.dataobjects.DeliveryPrint;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class DocumentsEx extends DocumentsPrint {
	private static final int ORG_DEBT_WARNING_DLG = R.id.org_debt_warning_dlg;
//	boolean hideDebtWarningMessage = false;
	
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}
	
	@Override
	protected boolean hideMakePko() {
		return (OrderDoc.instance() == DocType.getCurDoc());
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		AdapterView.AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo) menuInfo;		
		Document<?> doc = (Document<?>) adapter.getItem(aMenuInfo.position);
		if( doc == null || !(doc instanceof SalesImpl || doc instanceof DeliveryImpl) ) {
			MenuItem item = menu.findItem(R.id.itMakePKO);
			if( item != null )
				item.setVisible(false);
		}
		
		if( !(doc instanceof OrderImpl) ) {
			MenuItem item = menu.findItem(R.id.itMakeSale);
			if( item != null )
				item.setVisible(false);
		}
	}
	
	@Override
	protected void makePKO(DeliveryImpl d, GpsCoord location) {
		IncassImpl ii = (IncassImpl) IncassDoc.instance().create();
		IncassEx dest = (IncassEx)ii.getData();
		DeliveryPrint src = (DeliveryPrint)d.getData();
		
		dest.sum = (int)src.sum();
		dest.firmCode = src.supplyercode;
		ii.init(this, src.id, location);
		ii.open(this);
	}
	
	@Override
	protected void makePKO(SalesImpl s, GpsCoord location) {
		IncassImpl ii = (IncassImpl) IncassDoc.instance().create();
		IncassEx dest = (IncassEx)ii.getData();
		Sales src = s.getData();
		
		dest.sum = (int)s.sum();
		dest.firmCode = src.supplyercode;
		ii.init(this, src.id, location);
		ii.open(this);
	}
	
	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		return new DocumentsAdapterEx(this, docType, id, getOrder(docType), 
				(docType == DebtDoc.instance()) ? R.layout.docs_row_ex : R.layout.docs_list_row );
//		if(docType instanceof DebtDoc)
//			return new DocumentsAdapter(this, docType, id, "date", R.layout.docs_row_ex);
//		else
//			return super.createAdapter(docType, id);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if(DocType.getCurDoc() == DebtDocEx.instance() || docType == DebtDocEx.instance())
			adapter = null;
		super.adjustViewForDocType(docType);
	}
	
	@Override
	protected void createNewDoc() {
		DocType dt = (DocType) DocType.getCurDoc();
//		if(!hideDebtWarningMessage &&
//		(dt.equals(OrderDoc.instance()) || dt.equals(SalesDoc.instance())) && 
//		((OrgEx)org.getData()).debt > 0)
		if((dt.equals(OrderDoc.instance()) || dt.equals(SalesDoc.instance())) && ((OrgEx)org.getData()).debt > 0)
			showDialog(ORG_DEBT_WARNING_DLG);
		else
			super.createNewDoc();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case ORG_DEBT_WARNING_DLG: return createOrgDebtWarningDlg();
		default: return super.onCreateDialog(id);
		}
	}

	private Dialog createOrgDebtWarningDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert);
		builder.setMessage(getString(R.string.org_debt_warning, 
				Util.IntToScaleStr(((OrgEx)org.getData()).debt, Consts.SUM_SCALE, Util.DEC_DELIM, false)));
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				hideDebtWarningMessage = true;
				DocumentsEx.super.createNewDoc();
			}
		});
		
		return builder.create();
	}
	
	@Override
	protected DocFilterOnClickListener createDocFilter() {
		return new DocFilterOnClickListener(this){
			@Override
			protected void initData(boolean creatableFilter) {
				super.initData(creatableFilter);
				data.remove(WSOrderDoc.instance());
			}
		};
	}
	
	@Override
	protected void onlyVisitInit() {}
}

class DocumentsAdapterEx extends DocumentsAdapter {

	public DocumentsAdapterEx(Context context, DocType docType, String orgId, String order, int id) {
		super(context, docType, orgId, order, id);
	}
	
	@Override
	protected void setData(View view, Document<?> doc, int position) {
		super.setData(view, doc, position);
		View v = view.findViewById(R.id.tvSumDoc);
		if( v == null ) {
			WindowManager wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics metrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(metrics);

			int w = metrics.widthPixels / 2;
			TextView tv = (TextView) view.findViewById(R.id.tvOther);
			if( tv != null )
				tv.setWidth(w);
			
			tv = (TextView) view.findViewById(R.id.tvDate);
			if( tv != null )
				tv.setWidth(w/2);

			tv = (TextView) view.findViewById(R.id.tvSum);
			if( tv != null )
				tv.setWidth(w/2);
		}
	}
}

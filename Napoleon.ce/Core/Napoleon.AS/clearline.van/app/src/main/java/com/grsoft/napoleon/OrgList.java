package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.SalesBanImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.gps.GPSUtilNew;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class OrgList extends MainEx {
	private SalesImpl fake = (SalesImpl) SalesDoc.instance().create();

	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, OrgList.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		fake.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		findViewById(R.id.btnMode).setVisibility(View.GONE);
		findViewById(R.id.btnDocFilter).setVisibility(View.GONE);
	}

	@Override
	public void openOrg(Org org, int pos) {
		clickOrg = org;
		orgClickPos = pos;

		if (SalesBanImpl.isOrgBanned(org.id))
			Toast.makeText(OrgList.this, R.string.sales_ban, Toast.LENGTH_SHORT).show();
		else if(((OrgEx)clickOrg).kpk > 0){
			showDialog(R.id.orgkpkdlg);
		}else{
			alertMessage = ((OrgEx)org).stopMsg;
			if( alertMessage.length() > 0 ) {
				showDialog(STOP_DLG);
			} else{
				proceedSales(org);
			}
		}
	}

		private void proceedSales(Org o) {
			SalesImpl doc = (SalesImpl) SalesDoc.instance().create();
			doc.initSilent(o.id, GPSUtilNew.getLastKnownLocation());
			Sales dst = doc.getData();
			
			((SalesEx)dst).taxType = ((OrgEx)o).taxType;
			dst.sumType = o.costype;
			dst.items.addAll(fake.getData().items);
			
			// recalc cost
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();
			CostStrategy cs = CostStrategy.getInstance(doc.getClass());
			for(OrderItem itm  : dst.items) {
				p.id = itm.id;
				pi.read();
				itm.cost = (int)cs.getItemCost(p, doc);
				((SalesItem)itm).countTax(dst, p.tax1);
			}
			pi.close();
			
			doc.initDocNumber();
			doc.write();
			doc.close();
			DocHelper.saveDocNumber(doc.getTableName(), doc.getNumber());
			
			((SalesEx)fake.getData()).buddy = doc.getData().created;
			fake.write();
			fake.close();
			
			CreateSalesEx.open(OrgList.this, doc.getRowid(), true, true);
			
			finish();
		}

	
	@Override
	public void onBackPressed() {
		finish();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.orgkpkdlg)
			return createOrgKPKDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createOrgKPKDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(View.inflate(this, R.layout.orgkpkdlg, null));
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = ((EditText)((AlertDialog)dialog).findViewById(R.id.edName)).getText().toString().trim();
				String address = ((EditText)((AlertDialog)dialog).findViewById(R.id.edAddress)).getText().toString().trim();
				
				if(address.length() > 0){
					initSalesKPK(clickOrg.id, name, address);
				}
			}
		});
		
		builder.setNegativeButton(R.string.cancel, null);
		
		return builder.create();
	}

	protected void initSalesKPK(String id, String name, String address) {
		OrgImpl oi = new OrgImpl();
		oi.read("id", id);
		if(!((OrgEx)oi.getData()).canSale()) {
			Toast.makeText(this, "Продажа запрещена ВЕТИС", Toast.LENGTH_LONG).show();
			return;
		}
		
		SalesImpl doc = (SalesImpl) SalesDoc.instance().create();
		doc.initDocNumber();
		doc.initSilent(id, GPSUtilNew.getLastKnownLocation());
		doc.getData().items.addAll(fake.getData().items);
		((SalesEx)doc.getData()).isBlack = 0; //1;
		((SalesEx)doc.getData()).orgName = name;
		((SalesEx)doc.getData()).orgAddress = address;
		((SalesEx)doc.getData()).remark = String.format("%s %s", name, address); 
		doc.write();
		doc.close();

		DocHelper.saveDocNumber(doc.getTableName(), doc.getNumber());
		((SalesEx)fake.getData()).buddy = doc.getData().created;

		fake.write();
		fake.close();
		
		Intent i = new Intent(OrgList.this, SalesDetailSM.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		OrgList.this.startActivity(i);
		
		finish();
		
	}
	
	@Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {}
	@Override public boolean onCreateOptionsMenu(Menu menu) {	return true; }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if( keyCode == KeyEvent.KEYCODE_MENU )
			return true;
		else
			return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		DocType.setCurDoc(DebtDocEx.instance());
		super.onResume();
	}
}


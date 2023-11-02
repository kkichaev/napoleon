package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Rfrg;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.util.gps.GPSUtilNew;


public class DocumentsEx extends Documents {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override protected int getContextMenuId() { return R.menu.doc_context_menuex; }
	
	void showRfregerators(boolean show) {
		OrgEx oe = (OrgEx) org.getData();
		String text = orgInfo(oe);
		
		if( show ) {
			for(Rfrg r : oe.refregerators) {
				text += "<br/>" + r.id + " " + r.name;
			}
		}
		
		tvOrgInfo.setText(Html.fromHtml(text));
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		showRfregerators(docType == VisitDoc.instance());
		
		btnNewDoc.setVisibility(docType == DebtDoc.instance() || docType == IncassDoc.instance() ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo) menuInfo;		
		Document<?> doc = (Document<?>) adapter.getItem(aMenuInfo.position);

		getMenuInflater().inflate(getContextMenuId(), menu);
		DocType ct = DocType.getCurDoc();
		if( !ct.isCreatable() )
		{
			menu.removeItem(R.id.itCopy);
			menu.removeItem(R.id.itDelete);
			menu.removeItem(R.id.itEdit);
		}
		
		if(ct != OrderDoc.instance()) {
			if(  !(doc instanceof DeliveryImpl) ) {
				MenuItem item = menu.findItem(R.id.itMakePKO);
				if( item != null )
					item.setVisible(false);
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int id = item.getItemId(); 
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		Document<?> d = (Document<?>) adapter.getItem(menuInfo.position);
		if( id == R.id.itMakePKO ) {
			makeIncass(d, false);
			return true;
		} else if( id == R.id.itMakePKONonCash ) {
			makeIncass(d, true);
			return true;
		} else 
			return super.onContextItemSelected(item);
	}

	private void makeIncass(Document<?> selectedDoc, boolean noncash) {
		long sum = selectedDoc.sum();
		if( sum == 0 )
			return;
		
		DocType.setCurDoc(IncassDoc.instance());
		
		IncassImpl ii = (IncassImpl) IncassDoc.instance().create();
		IncassEx i = (IncassEx) ii.getData();
		i.sum = (int) sum;
		i.noncash = noncash ? 1 : 0;
		
		if( selectedDoc instanceof DeliveryImpl ) {
			Delivery d = (Delivery)selectedDoc.getData();
			i.dlvNumber = d.number;
			i.dlvDate = d.date;
		}
		if( ii.init(this, org.getData().id, GPSUtilNew.getLastKnownLocation(this))) {
			ii.open(this);
		}
		ii.close();
	}
}

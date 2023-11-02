package com.grsoft.napoleon;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PaymentImpl;
import com.grsoft.dataobjects.impl.PkoImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PaDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.gps.GPSUtilNew;

public class DocumentsPrint extends Documents {
	
	protected boolean hideMakePko() {
		return SalesDoc.instance() != DocType.getCurDoc();
	}
	
	@Override protected int getContextMenuId() { return R.menu.doc_context_menu_print; }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo) menuInfo;		
		Document<?> doc = (Document<?>) adapter.getItem(aMenuInfo.position);
		
		if(doc instanceof PaymentImpl)
			return;
		
		super.onCreateContextMenu(menu, v, menuInfo);

		if( DocType.getCurDoc() == DebtDoc.instance() ) {
			getMenuInflater().inflate(getContextMenuId(), menu);
			menu.removeItem(R.id.itCopy);
			menu.removeItem(R.id.itDelete);
		}
		
		if( hideMakePko() ) {
			MenuItem item = menu.findItem(R.id.itMakePKO);
			if( item != null )
				item.setVisible(false);
		} 
		
		if( doc != null && OrderDoc.instance().create().getClass() != doc.getClass() ) {
			MenuItem item = menu.findItem(R.id.itMakeSale);
			if( item != null )
				item.setVisible(false);
		}
		
		postOnCreateContextMenu(doc, menu);
	}
	
	protected void postOnCreateContextMenu(Document<?> doc, ContextMenu menu) { }

	protected void makeSales(OrderImpl doc, GpsCoord location) {
		SalesImpl si = SalesImpl.fromOrder(doc, location);
		if( si != null ) {
			if( si.getData().items.size() > 0 ) {		
				DocType.setCurDoc(SalesDoc.instance());			
				si.open(this);
			} else {
				si.delete();
				Toast.makeText(this, "Для продажи нет свободного товара", Toast.LENGTH_SHORT).show();
			}
			si.close();
		}
	}
	
	protected void makePKO(SalesImpl s, GpsCoord location) {
		PkoImpl pko = PkoImpl.fromSales(s, location, this);
		pko.open(this);
	}
	
	protected void makePKO(DeliveryImpl d, GpsCoord location) {
		PkoImpl pko = PkoImpl.fromSales(d, location, this);
		pko.open(this);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		GpsCoord location = (allowCreateDocWhithoutGpsPos || GPSUtilNew.isGpsPosValid()) ? 
				GPSUtilNew.getLastKnownLocation() :
				null;
		
		if( item.getItemId() == R.id.itMakeSale ) {
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();		
			CreatableDocument<?> doc = (CreatableDocument<?>) adapter.getItem(menuInfo.position);
			if( doc != null && doc instanceof OrderImpl ) {
				if (location != null)
					makeSales((OrderImpl) doc, location);					
				else
					makeLocationAlert();
			}
			return false;
		} else if( item.getItemId() == R.id.itMakePKO ) {
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();		
			Document<?> doc = (Document<?>) adapter.getItem(menuInfo.position);
			if( doc != null && (doc instanceof SalesImpl || doc instanceof DeliveryImpl)) {
				if (location != null) {
					if( doc instanceof SalesImpl )
						makePKO((SalesImpl) doc, location);
					else if( doc instanceof DeliveryImpl )
						makePKO((DeliveryImpl) doc, location);
				} else
					makeLocationAlert();
			}
			return false;
		} else
			return super.onContextItemSelected(item);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		if(docType == PaDoc.instance() )
			btnNewDoc.setEnabled(false);
	}
}

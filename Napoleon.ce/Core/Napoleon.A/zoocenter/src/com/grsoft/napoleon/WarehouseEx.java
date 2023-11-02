package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.FoldersAdapter;


public class WarehouseEx extends WarehouseNew {
	boolean hideMatrix = false;
	
	@Override
	protected BaseAdapter createListAdapter() {
		hideMatrix = false;
		
		if( DocType.getCurDoc() == ReturnDoc.instance() ) {
			hideMatrix = true;
			return new ReturnAdapter(this);
		}
		
		return super.createListAdapter();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean ret = super.onPrepareOptionsMenu(menu);
		MenuItem mi = menu.findItem(R.id.itMatrix);
		if( mi != null)
			mi.setVisible(!hideMatrix);
		return ret;
	}
	
	class ReturnAdapter extends FoldersAdapter {

		
		public ReturnAdapter(WarehouseNew warehouse) {
			super(warehouse);

			FoldersAdapter.resetCache();
		}
		
		@Override
		protected void fillPriceIds(SQLiteDatabase database) {
			try {
				PriceImpl pi = new PriceImpl();
				Price p = pi.getData();
				
				fprice.clear();

				HashSet<String> items = new HashSet<String>();
				DocList dl = DeliveryDoc.instance().docList(document.getId());
				for(Document<?> d : dl) {
					Delivery dlv = ((DeliveryImpl)d).getData();
					for(DeliveryItem di : dlv.items) {
						if( items.contains(di.id))
							continue;
						
						items.add(di.id);
						p.id = di.id;
						
						if( pi.read() == false )
							continue;
						
						if(!fprice.containsKey(p.folderID))
							fprice.put(p.folderID, new ArrayList<PriceInfo>());
						
						PriceInfo pri = new PriceInfo(pi.getRowid(), p.name, p.id);
						fprice.get(p.folderID).add(pri);
					}
				}
				dl.close();
				pi.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}

package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.dataobjects.impl.WhOrderImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.PriceTextFilter;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.widget.BaseAdapter;


public class WarehouseEx extends Warehouse {
	
	@Override
	protected FoldersAdapter createAdapterInstance() {
		FoldersAdapter.resetCache();
		if (DocType.getCurDoc() == ReturnDoc.instance())
			return new ReturnAdapter(this);
		else
			return super.createAdapterInstance();
	}
	
	protected PriceTextFilter createPriceTextFilter() {
		return new PriceTextFilter() {
			protected void collectFolderID(TreeNode node, List<Integer> fids) {}
		};
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
		if(document instanceof OrderImplEx || document instanceof WhOrderImpl) {
			OrderEx oe = (OrderEx)document.getData();
			if(oe.alp != 0)
				ret.putFilter(new AltFilter());
			else
				ret.putFilter(new TabakFilter(oe.tabak == 1));
		} else if(document instanceof SalesImplEx) {
			SalesEx se = (SalesEx) document.getData();
			if(se.upd > 0) {
				ret.putFilter(new TabakFilter(se.tabak > 0));
			}
		}
		return ret;
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		return new ZeroFilter();
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			if( !(document instanceof Itemsable) )
				return super.inset(priceRowID, id);
			
			boolean result = false; 
			
			if(price.read(priceRowID))
				result = (((Itemsable)document).getItemValue(price.getData()) > 0);			
			return result;
		}
	}	
	class ReturnAdapter extends FoldersAdapter {

		HashSet<String> ids = new HashSet<String>();
		
		public ReturnAdapter(WarehouseManager warehouse) {
			super(warehouse);
			
			ConfigImpl config = new ConfigImpl();
			Config c = config.getData();
			c.key = "Организация";
			
			if( config.read()) {
				List<CharSequence> firms = new ArrayList<CharSequence>();
				DialogHelper.makeList(c.value, firms);
				String orgId = document.getId();
				Return r = (Return) document.getData();
				
				if(r.supplyer >= 0 && r.supplyer < firms.size()) {
					String firma = (String) firms.get(r.supplyer);
					
					com.grsoft.napoleon.documents.DocList dl = DeliveryDoc.instance().docList(orgId);
					
					for(Document<?> d : dl) {
						DeliveryEx dlv = (DeliveryEx) d.getData();
						
						if(dlv.firma.equals(firma))
							for(DeliveryItem di : ((DeliveryImpl)d).getData().items)
								ids.add(di.id);
					}
					
					dl.close();
				}
			}	
		}
		
		@Override public boolean inset(long rowid, String id) { return ids.contains(id); }
	}

	static class TabakFilter extends Filter {
		public TabakFilter(boolean isTabak) {
			super("TABAK" + (isTabak ? "1" : "0"));
			if(isTabak) {
				where = "tabak=1";
			} else
				where = "alp=0 and tabak=0";
		}
	}

	static class AltFilter extends Filter {
		public AltFilter() {
			super("ALT_PROD1");
			where = "alp=1";
		}
	}
}


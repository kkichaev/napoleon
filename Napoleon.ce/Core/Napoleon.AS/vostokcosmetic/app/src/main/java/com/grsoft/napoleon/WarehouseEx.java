package com.grsoft.napoleon;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgDisabledFolder;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPriceColor;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgDogovorImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Filter;
import com.grsoft.util.FolderTree;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;

import android.widget.BaseAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WarehouseEx extends Warehouse {
	static int whIndex = 0;

	PriceImpl tPrice = new PriceImpl();
	Map<String, Integer> colors = new HashMap<>();
	OrgDogovor orgDogovor;

	@Override
	protected Filter createZeroPositionFilter() {
		if(document instanceof OrderImplEx)
			return new ZeroFilter();
		return super.createZeroPositionFilter();
	}

	@Override
	protected void onDestroy() {
		tPrice.close();
		super.onDestroy();
	}

	@Override
	protected void readDocument() {
		super.readDocument();

		OrgImpl oi = new OrgImpl();
		oi.read("id", document.getId());

		DataTraveler.travel(OrgPriceColor.class, new DataTraveler.Travel<OrgPriceColor>() {
			@Override
			public boolean travel(DataTraveler<OrgPriceColor> item) {
				colors.put(item.data.id, Util.GrServerColorToSystem(item.data.color));
				return true;
			}
		}, "ido='" + ((OrgEx)oi.getData()).ido + "'");

		if(document instanceof OrderImplEx) {
			OrgDogovorImpl odi = new OrgDogovorImpl();
			orgDogovor = odi.getData();
			orgDogovor.id = ((OrderEx)document.getData()).dogovor;
			odi.read();
		}
	}

	@Override
	protected int getDefaultColor(Price p) {
		Integer clr = colors.get(p.id);
		return clr != null ? clr : super.getDefaultColor(p);
	}

	@Override
	protected BaseAdapter createListAdapter() {
		int newIndex = 0;
		if( document instanceof OrderImplEx) {
			newIndex = ((OrderEx)document.getData()).whIndex;
		}
		if(whIndex != newIndex) {
			whIndex = newIndex;
			FoldersAdapter.resetCache();
		}

		FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
		if(orgDogovor != null && orgDogovor.disabled.size() > 0) {
			ret.putFilter(new DisabledFolderFilter(orgDogovor.id, orgDogovor.disabled));
		}

		return ret;
	}

	class DisabledFolderFilter extends Filter {

		Set<Integer> disabled = new HashSet<>();

		public DisabledFolderFilter(String id, List<OrgDisabledFolder> folders) {
			super("DSBL_FLD" + id);

			FolderTree ft = new FolderTree();
			ft.load();
			for(OrgDisabledFolder odf : folders) {
				Folder f = ft.getFolder(odf.fid);
				if(f != null)
					disabled.add(f.id);
			}
		}

		@Override
		public boolean inset(long priceRowID, String id) {
			Price p = tPrice.getData();
			p.id = id;
			tPrice.read();
			return !disabled.contains(p.folderID);
		}
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
}

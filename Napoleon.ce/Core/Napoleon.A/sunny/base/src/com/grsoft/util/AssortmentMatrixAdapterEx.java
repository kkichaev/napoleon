package com.grsoft.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import android.annotation.SuppressLint;

import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.WarehouseNew;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.AssortmentMatrixAdapter.IterFunc;

public class AssortmentMatrixAdapterEx extends AssortmentMatrixAdapter {
	Set<String> priceIds = new HashSet<String>();
	List<HashMap<Integer, Integer>> sellData;
	FolderTree tree;
	
	public AssortmentMatrixAdapterEx(WarehouseNew warehouse, String id) {
		super(warehouse, id);
		
		refreshSellData();
	}
	
	public void refreshSellData() {
		DocIterator di = new DocIterator();
		
		List<MatrixItem> result = new ArrayList<MatrixItem>();
		collectItems(id, result, di);
		
		sellData = di.getSellData();

		for(MatrixItem i : result)
			if(!priceIds.contains(i.id))
				priceIds.add(i.id);
	}
	
	public boolean isIdInMatrix(String id){
		return priceIds.contains(id);
	}

	public List<Integer> getFolderSellData(int folder) {
		if(tree == null) {
			tree = new FolderTree();
			tree.load();
		}
		
		List<Integer> ret = new ArrayList<Integer>();
		for(int i=4; i>=0; i--) {
			int totalQty = 0;
			HashMap<Integer, Integer> wdata = sellData.get(i);
			
			if(folder == 0) {
				for(Integer iq : wdata.values())
					totalQty += iq;
			} else {
				int fidx = tree.findFolder(folder);
				int level = -1;
				while( fidx < tree.size() && fidx >= 0 ) {
					Folder cf = tree.get(fidx);
					if(level < 0)
						level = cf.level;
					else {
						if(cf.level <= level)
							break;
					}
					
					Integer fqty = wdata.get(cf.id);
					if(fqty != null)
						totalQty += fqty;
					
					fidx++;
				}
			}
			ret.add(totalQty);
		}
		
		return ret;
	}
}

class DocIterator extends AssortimenMatrixDocIterator {
	
	PriceImpl pi  = new PriceImpl();
	
	Date monthAgo, dataStart;
	List<Date> weekStart = new ArrayList<Date>();
	
	List<HashMap<Integer, Integer>> data = new ArrayList<HashMap<Integer,Integer>>();
	
	@SuppressLint("UseSparseArrays")
	public DocIterator() {
		Calendar c = Calendar.getInstance(Locale.getDefault());
		c.add(Calendar.MONTH, -1);
		monthAgo = Util.getDayStart(c.getTime());
		
		c.add(Calendar.MONTH, 1);
		while(c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
			c.add(Calendar.DAY_OF_MONTH, -1);
		}

		long ct = Util.getDayStart(c.getTime()).getTime();
		for(int i=0; i <4; i++) {
			// from sunday to monday
			Date d = new Date(ct);
			weekStart.add(d);
			ct -= (7 * 24 * 3600 * 1000);
		}
		dataStart = new Date(ct);
		
		for(int i=0; i<5; i++)
			data.add(new HashMap<Integer, Integer>());
	}
	
	public List<HashMap<Integer, Integer>> getSellData() {
		pi.close();
		return data;
	}
	
	@Override
	public void iterItems(Document<?> doc, IterFunc func) {
		if(doc instanceof OrderImpl) {
			Order o = (Order)doc.getData();
			Date dd = o.created;
			if(dd.compareTo(dataStart) < 0)
				return;
			
			int wi = getWeekIndex(dd);
			HashMap<Integer, Integer> accData = data.get(wi);
			if(accData != null) {
				Price p = pi.getData();
				for(OrderItem oi : o.items) {
					p.id = oi.id;
					pi.read();
					Integer val = accData.get(p.folderID);
					if(val == null)
						val = 0;
					val += oi.qty;
					accData.put(p.folderID, val);
				}
			}
			if(doc.getDate().compareTo(monthAgo) < 0)
				return;
		}
		super.iterItems(doc, func);
	}

	private int getWeekIndex(Date date) {
		int idx = 0;
		for(Date d : weekStart) {
			if(d.compareTo(date) < 0)
				break;
			idx++;
		}
		return idx;
	}
}
 
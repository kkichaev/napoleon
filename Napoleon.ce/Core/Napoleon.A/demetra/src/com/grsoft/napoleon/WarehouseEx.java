package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DistrDoc;
import com.grsoft.dataobjects.DistrItem;
import com.grsoft.dataobjects.DistribGroup;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DistribGroupImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.FolderTree;
import com.grsoft.util.AssortimenMatrixDocIterator;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;
import com.grsoft.util.AssortmentMatrixAdapter.IterFunc;

public class WarehouseEx extends WarehouseNew {
	
	@SuppressLint("UseSparseArrays")
	HashMap<Integer, Integer> folderData = new HashMap<Integer, Integer>();
	HashSet<String> topMatrix = null;
	
	HashSet<String> notExistsItems = new HashSet<String>();
	
	FolderTree folderTree = new FolderTree();
	Boolean setBackResource = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		folderTree.load();
	}
	
	@Override public boolean useInterlaceBackground() {
		if(setBackResource != null) {
			setBackResource = null;
			return false;
		}
		return super.useInterlaceBackground(); 
	}
	
	void loadAbcMatrix() {
		topMatrix = new HashSet<String>();
	
		if( document == null )
			return;
		
		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx)oi.getData();
		o.id = document.getId();
		if( oi.read() )
			for(MatrixItem mi : o.top)
				topMatrix.add(mi.id);
			
		oi.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		folderData.clear();
		if(OrderDoc.instance() == DocType.getCurDoc()) {
			final PriceImpl pi = new PriceImpl();
			final Price p = pi.getData();
			
			Date begin = Util.getDate();
			Calendar c = Calendar.getInstance();
			c.setTime(begin);
			c.add(Calendar.DAY_OF_MONTH, 1);
			DatePeriod dp = new DatePeriod(begin, c.getTime());
			dp.periodType = DatePeriod.CREATED;
			DocList dl = OrderDoc.instance().docList(null, null, dp);
			for(Document<?> doc : dl){
				for(OrderItem oi : ((OrderImpl)doc).getData().items) {
					p.id = oi.id;
					if( pi.read() ) {
						int wg = (int)((long)oi.qty * p.weight / Consts.QTY_SCALE);
						Integer fw = folderData.get(p.folderID);
						if( fw == null ) fw = wg;
						else fw += wg;
						folderData.put(p.folderID, fw);
					}
				}
			}
			
			final DistribGroupImpl dgi = new DistribGroupImpl();
			final DistribGroup dg = dgi.getData();
			
			final Set<String> assortiment = new HashSet<String>();
			
			AssortmentMatrixAdapter.collectItems(document.getId(), new ArrayList<MatrixItem>(), new AssortimenMatrixDocIterator(){
				@Override
				public void iterItems(Document<?> doc, IterFunc func) {
					super.iterItems(doc, new IterFunc() {
						@Override public void process(String id) { 
							if(!assortiment.contains(id)) {
								p.id = id;
								if(pi.read())
									assortiment.add(p.name.trim());
								
							}
							
						}
					});
				}
			});
			pi.close();
			
			String where = "id = '" + document.getId() + "' and created >= " + Long.toString(begin.getTime()) + " and created <= " + Long.toString(c.getTime().getTime());
			DataTraveler.travel(DistrDoc.class, new DataTraveler.Travel<DistrDoc>() {

				@Override
				public boolean travel(DataTraveler<DistrDoc> item) {
					for(DistrItem di : item.data.items) {
						dg.id = di.id;
						dgi.read();
						
						String name = dg.name.trim();
						if(!assortiment.contains(name))
							continue;
						
						if(di.exists == 0) {
							notExistsItems.add(name);
						} else {
							notExistsItems.remove(name);
						}
					}
					return true;
				}
			}, where);
			
			dgi.close();
		}
	}

	@Override
	protected int getDefaultColor(Price p) {
		int color = super.getDefaultColor(p); 
		if( (color & 0xffffff) == 0 ) {
			if( topMatrix == null )
				loadAbcMatrix();
			if( topMatrix.contains(p.id) )
				color = Color.MAGENTA;
		}
		return color;
	}
	
	@Override protected int getFolderLayoutId() { return R.layout.itemselectrow_ex; }	
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View view = super.getPriceView(node, convertView);
		if(notExistsItems.contains(price.getData().name.trim())) {
			setBackResource = true;
			view.setBackgroundResource(R.drawable.list_yellow_selector);
		}
		return view;
	}
	
	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		View v = super.getFolderView(node, convertView);
		
		int wg = 0;
		int index = folderTree.findFolder(node.id);
		if( index >= 0 ) {
			Folder cf = folderTree.get(index);
			int level = cf.level;
			
			Integer fw = folderData.get(node.id);
			if( fw != null )
				wg += fw;

			for( int i=index+1; i<folderTree.size(); i++) {
				cf = folderTree.get(i);
				if( cf.level <= level )
					break;
				fw = folderData.get(cf.id);
				if( fw != null )
					wg += fw;
			}
		}
		
		TextView tv = (TextView)v.findViewById(R.id.tvQty);
		tv.setText((wg != 0) ? Util.IntToScaleStr((wg +  Consts.WEIGHT_SCALE/2)/ Consts.WEIGHT_SCALE, 1) : "");

		return v;
	}
}

package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ClientCardReport extends Activity {
	private TableLayout tblFixed;
	private TableLayout tblScroll;
	private TableLayout tblHeader;
	private HorizontalScrollView headerScroll;
	private HorizontalScrollView contentScroll;
	private TableRow.LayoutParams rp = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	private TableRow.LayoutParams tvLP = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	private int fixed_width = 0;
	private int cell_width = 0;
	private int row_height = 0;
	private Drawable folderDraw;
	private List<Date> columns = new ArrayList<Date>();
	private int priceCellColor = 0;
	private int folderCellColor = 0;
	
	private Comparator<ItemData> folderNodeCmp = new Comparator<ItemData>() {
		

		@Override
		public int compare(ItemData lhs, ItemData rhs) {
			return lhs.compareTo(rhs);
		}
	};
	
	public static void open(Context context, String id) {
		Intent i = new Intent(context, ClientCardReport.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, id);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.clientcard);
		
		tblFixed = (TableLayout) findViewById(R.id.tblFixed);
		tblScroll = (TableLayout) findViewById(R.id.tblScroll);
		tblHeader = (TableLayout) findViewById(R.id.tblHeader);
		headerScroll = (HorizontalScrollView) findViewById(R.id.headerScroll);
		contentScroll = (HorizontalScrollView) findViewById(R.id.contentScroll);
		
		priceCellColor = getResources().getColor(R.color.white);
		folderCellColor = getResources().getColor(R.color.folder_color);

		contentScroll.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener() {

			@Override
			public void onScrollChanged() {
				headerScroll.scrollTo(contentScroll.getScrollX(), contentScroll.getScrollY());
			}
		});
		
		folderDraw = getResources().getDrawable(R.drawable.folder);
		tvLP.setMargins(0, 0, 0, (int)getResources().getDimension(R.dimen.del_height));
		fixed_width = (int)getResources().getDimension(R.dimen.fixed_width);
		cell_width = (int)getResources().getDimension(R.dimen.col_width);
		row_height = (int)getResources().getDimension(R.dimen.row_height);
		
		Map<String, Data> data = loadData();
		Map<Integer, FolderNode> mapRoot = makeTree(data);
		sortTree(mapRoot.get(-1));
		displayTree(mapRoot);
	}

	private void sortTree(FolderNode m) {
		Collections.sort(m.childs, folderNodeCmp);
		
		for(Object o : m.childs) {
			if (o instanceof FolderNode)
				sortTree((FolderNode)o);
		}
	}

	protected void displayTree(Map<Integer, FolderNode> mapRoot) {
		calcFixedCellWidth();
		makeHeader();
		travelTree(mapRoot.get(-1).childs);
	}

	protected void calcFixedCellWidth() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int newFixed = metrics.widthPixels - (columns.size() * cell_width);
		
		if(fixed_width < newFixed)
			fixed_width = newFixed;
	}

	protected void makeHeader() {
		TextView fc = (TextView) findViewById(R.id.fixedCell);
		fc.setWidth(fixed_width);
		
		TableRow tr = new TableRow(this);
		tr.setLayoutParams(rp);
		tblHeader.addView(tr);
		TextView tv = null;

		for (Date c : columns) {
			tv = new TextView(this);
			tv.setText(String.format("%s\no           ç", Util.simpleDateFormat.format(c)));
			tv.setWidth(cell_width);
			tr.addView(tv);
		}
	}

	protected Map<Integer, FolderNode> makeTree(Map<String, Data> data) {
		PriceImpl p = new PriceImpl();
		FolderTree ft = FolderTree.load();
		
		Map<Integer, FolderNode> mapRoot = new HashMap<Integer, FolderNode>();
		mapRoot.put(-1, new FolderNode(-1));
		
		for(Data a : data.values()) {
			p.getData().id = a.id;
			
			if(p.read()) {
				a.text = p.getData().name;
				int fid = p.getData().folderID;
				
				if (!mapRoot.containsKey(fid))
					insertIntoTree(ft, mapRoot, fid);
				
				mapRoot.get(fid).childs.add(a);
			}
		}
		
		p.close();
		
		return mapRoot;
	}

	protected Map<String, Data> loadData() {
		String id = getIntent().getStringExtra(ExtrasConst.ORG_ID_STR);
		Date d = Util.resetTime(new Date());
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date f = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, -31);
		Date s = cal.getTime();
		DatePeriod dp = new DatePeriod(s, f);
		DocList orders = OrderDoc.instance().docList(id, "created", dp);
		DocList remnants = RemnantsDoc.instance().docList(id, "created", dp);
		Map<String, Data> data = new HashMap<String, Data>();
		
		for(Document<?> t : orders) {
			Order o = (Order) t.getData();
			
			Date dt = Util.resetTime(o.created);
			
			if(!columns.contains(dt))
				columns.add(dt);
			
			for(OrderItem i : o.items) {
				if(!data.containsKey(i.id))
					data.put(i.id, new Data(i.id));
				
				Data r = data.get(i.id);
				
				if(!r.items.containsKey(dt))
					r.items.put(dt, new Item());
				
				Item c = r.items.get(dt);
				c.order += i.qty;
			}
		}
		
		for(Document<?> t : remnants) {
			Remnants o = (Remnants) t.getData();
			
			Date dt = Util.resetTime(o.created);
			
			if(!columns.contains(dt))
				columns.add(dt);
			
			for(RemnantItem i : o.items) {
				if(!data.containsKey(i.id))
					data.put(i.id, new Data(i.id));
				
				Data r = data.get(i.id);
				
				if(!r.items.containsKey(dt))
					r.items.put(dt, new Item());
				
				Item c = r.items.get(dt);
				c.remn += i.qty;
			}
		}
		return data;
	}

	protected void travelTree(List<ItemData> nodes) {
		for(Object o : nodes) {
			if(o instanceof FolderNode) {
				FolderNode f = (FolderNode)o;
				makeViewFolderNode(f);
				travelTree(f.childs);
			}else
				makeViewDataNode((Data)o);
		}
	}
	
	private void fixedTextView(String text, Drawable pic) {
		TextView tv = new TextView(this);
		tv.setCompoundDrawablesWithIntrinsicBounds(pic, null, null, null);
		tv.setText(text);
		tv.setWidth(fixed_width);
		tv.setHeight(row_height);
		tv.setBackgroundColor(pic != null ? folderCellColor : priceCellColor);
		tv.setLayoutParams(tvLP);

		TableRow tr = new TableRow(this);
		tr.setLayoutParams(rp);
		tr.addView(tv);
		tblFixed.addView(tr);
	}
	
	private void cellTextView(Map<Date, Item> items){
		TableRow tr = new TableRow(this);
		tr.setLayoutParams(rp);
		tblScroll.addView(tr);
		
		for(Date d : columns) {
			TextView tv = new TextView(this);
			tv.setWidth(cell_width);
			String text = "";
			if(items != null && items.containsKey(d)) {
				Item i = items.get(d);
				text = String.format("%1$-6s%2$7s", Util.IntToScaleStr(i.remn, Consts.QTY_SCALE),
						Util.IntToScaleStr(i.order, Consts.QTY_SCALE));
			}
				
			tv.setText(text);
			tv.setWidth(cell_width);
			tr.addView(tv);
			tv.setHeight(row_height);
			tv.setLayoutParams(tvLP);
			tv.setBackgroundColor(items == null ? folderCellColor : priceCellColor);
		}
	}
	
	private void makeViewDataNode(Data f) {
		fixedTextView(f.text, null);
		cellTextView(f.items);
	}
	
	private void makeViewFolderNode(FolderNode f) {
		fixedTextView(f.text, folderDraw);
		cellTextView(null);
	}

	private FolderNode insertIntoTree(FolderTree ft, Map<Integer, FolderNode> map, int fid) {
		FolderNode result = new FolderNode(fid);
		Folder f = ft.getFolder(fid);
		
		if (f != null)
			result.text = f.name;
		
		map.put(fid, result);
		
		Folder fp = ft.getParent(f);
		int fpd = fp == null ? -1 : fp.id;
		
		if (!map.containsKey(fpd)) 
			insertIntoTree(ft, map, fpd);
			
		map.get(fpd).childs.add(result);
		
		return result;
	}
	
	private static class ItemData implements Comparable<ItemData>{
		public String text = "";

		@Override
		public int compareTo(ItemData another) {
			return text.compareTo(another.text);
		}
	}

	private static class FolderNode extends ItemData{
		
		public List<ItemData> childs = new ArrayList<ItemData>();
		public int fid;
		
		public FolderNode(int fid) {
			this.fid = fid;
		}

		
		@Override
		public int compareTo(ItemData another) {
			if (this.getClass() != another.getClass())
				return -1;
			else
				return fid - ((FolderNode)another).fid;
		}
	}
	
	private static class Data extends ItemData{
		public String id;
		public Map<Date, Item> items = new HashMap<Date, Item>();
		
		public Data(String id) {
			this.id = id;
		}
	}
	
	private static class Item{
		public int order = 0;
		public int remn = 0;
	}
}

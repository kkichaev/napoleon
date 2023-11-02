package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FolderTree;
import com.grsoft.util.Util;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class OrderList extends DocList {
	private  TextView tvSum;
	private TextView tvQty;
	private TextView tvWeight;
	
	public static void open(Context context){
		Intent i = new Intent(context, OrderList.class);
		context.startActivity(i);
	}
	
	@Override protected int getViewID() { return R.layout.orderreport; }
	
	@SuppressLint("UseSparseArrays")
	HashMap<Long, DocRowData> docsData = new HashMap<Long, DocRowData>();
	
	private static final int FILTER_ITEM_CODE = 0x50;
	
	List<PriceRowData> dlgFilter = new ArrayList<PriceRowData>(), 
			srchFilter = new ArrayList<PriceRowData>();
	PriceAdapter priceAdapter;
	
	@Override protected int getFilterLayout() { return R.layout.date_selection_ex; }
	
	@Override
	protected void postUpdateFilterView(View view) {
		Button b = (Button)view.findViewById(R.id.btnItems);
		b.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(R.id.price_filter_list); }
		});
	}
	
	@Override
	protected void initUI() {
		super.initUI();
		
		tvSum = (TextView) findViewById(R.id.tvSum);
		tvQty = (TextView) findViewById(R.id.tvQty);
		tvWeight = (TextView) findViewById(R.id.tvWeigth);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnSend.setVisibility(View.GONE);
		btnDelete.setVisibility(View.GONE);
		btnDocFilter.setVisibility(View.GONE);
		
		DocType.setCurDoc(OrderDoc.instance());
		findViewById(R.id.tvDocSum).setVisibility(View.GONE);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.price_filter_list) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle(R.string.price_filter_list);
			View view = View.inflate(this, R.layout.price_filter_list, null);
			b.setView(view);
			ListView lv = (ListView)view.findViewById(R.id.lvItems);
			registerForContextMenu(lv);
			priceAdapter = new PriceAdapter();
			lv.setAdapter(priceAdapter);
			
			view.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { addToFilter(); }
			});

			view.findViewById(R.id.btnDelAll).setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { removeItems(); }
			});
			
			b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface arg0, int arg1) { putFilter();}
			});
			b.setNegativeButton(R.string.cancel, null);
			
			AlertDialog ad = b.create();
//			ad.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			return ad;
		}
		return super.onCreateDialog(id);
	}
	
	protected void putFilter() {
		srchFilter.clear();
		srchFilter.addAll(dlgFilter);
	}

	protected void removeItems() {
		dlgFilter.clear();
		priceAdapter.notifyDataSetChanged();
	}

	protected void addToFilter() {
		Intent i = new Intent(this, Warehouse.activity);
		i.putExtra(ExtrasConst.WAREHOUSE_ID_TAG, "");
		i.putExtra(ExtrasConst.FOLDER_SELECT_ID_TAG, 1);
		startActivityForResult(i, FILTER_ITEM_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		
		if (FILTER_ITEM_CODE == requestCode) {
			
			PriceRowData prd = null;
			Bundle b = data.getExtras();
			String id = b.getString(ExtrasConst.WAREHOUSE_ID_TAG);
			if( id != null ) {
				PriceImpl pi = new PriceImpl();
				if( pi.read("id", id) ) {
					prd = new PriceRowData();
					Price p = pi.getData();
					prd.name = p.name;
					prd.id = p.id;
					prd.isFolder = false;
				}
			} else {
				int fid = b.getInt(ExtrasConst.FOLDER_SELECT_ID_TAG);
				FolderImpl fi = new FolderImpl();
				if(fi.read("id", fid)) {
					Folder f = fi.getData();
					prd = new PriceRowData();
					prd.id = f.fid;
					prd.isFolder = true;
					prd.name = f.name;
					prd.fid = f.id;
				}
			}
			if( prd != null ) {
				dlgFilter.add(prd);
				priceAdapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId()==R.id.lvItems) {
	          MenuInflater inflater = getMenuInflater();
	          inflater.inflate(R.menu.price_filter_menu, menu);
	          menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
					dlgFilter.remove(menuInfo.position);
					priceAdapter.notifyDataSetChanged();
					return false;
				}
			});
	      }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == R.id.price_filter_list) {
			dlgFilter.clear();
			dlgFilter.addAll(srchFilter);
			priceAdapter.notifyDataSetChanged();
		} else
			super.onPrepareDialog(id, dialog);
	}
	
	class PriceAdapter extends BaseAdapter {

		@Override public int getCount() { return dlgFilter.size(); }
		@Override public Object getItem(int arg0) { return dlgFilter.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if( arg1 == null )
				arg1 = View.inflate(OrderList.this, R.layout.price_filter_row, null);
			
			PriceRowData prd = (PriceRowData)getItem(arg0);
			TextView tv = (TextView)arg1.findViewById(R.id.tvName);
			tv.setText(prd.name);
			
			ImageView iv = (ImageView)arg1.findViewById(R.id.ivFolder);
			iv.setVisibility(prd.isFolder ? View.VISIBLE : View.INVISIBLE);
			
			return arg1;
		}		
	}

	@Override
	protected DocListAdapter createListAdapter(DocType docType) {
		return new DocsAdapter(this, docType, saveDatePeriod);
	}
	
	@Override
	protected void refreshTotalSum(boolean useFilter) {
		DocRowData drd = new DocRowData();
		for(Entry<Long, DocRowData> rv : docsData.entrySet()) {
			drd.add(rv.getValue());
		}

		tvSum.setText(Util.IntToScaleStr(drd.sum, Consts.SUM_SCALE));
		tvQty.setText(Util.IntToScaleStr(drd.qty, Consts.QTY_SCALE));
		tvWeight.setText(Util.IntToScaleStr(drd.weight, Consts.WEIGHT_SCALE));
	}
	
	@Override
	protected void drawData(View view, Document<?> doc, int position) {
		if( doc != null ) {
			Org o = org.getData();
			o.id = doc.getId();
			boolean readed = org.read();
			
			int color = getDocColor(doc);
			DocRowData drd = docsData.get(doc.getRowid());
			
			TextView tv = (TextView) view.findViewById(R.id.tvName);
			String text = "";
			if( readed )
				text = getDocText(o, doc);
			tv.setText(Html.fromHtml(text));
			tv.setTextColor(color);
			
			tv = (TextView)view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(drd.sum, Consts.SUM_SCALE));
			tv.setTextColor(color);
			
			tv = (TextView)view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(drd.qty, Consts.QTY_SCALE));
			tv.setTextColor(color);
			
			tv = (TextView) view.findViewById(R.id.tvWeigth);
			tv.setText(Util.IntToScaleStr(drd.weight, Consts.WEIGHT_SCALE));
			tv.setTextColor(color);
		}
	}
	
	class DocsAdapter extends DocListAdapter {

		FolderTree ft = new FolderTree();
		HashSet<String> items = new HashSet<String>();
		
		public DocsAdapter(Context context, DocType docType, DatePeriod filter) {
			super(context, docType, filter, R.layout.orderreportrow);
		}
		
		@Override
		public void fetch(DocType docType, DatePeriod dp, String id, Price p) {
			prepareFilter();
			super.fetch(docType, dp, id, p);
		}


		@Override
		public void fetchByPeriod(DocType docType, DatePeriod dp, String orgId, Price item, HashMap<Long, Integer> values) {
			this.orgId = orgId;
			documents.close();
			documents = docType.docList(orgId, order, dp);
			
			if( values != null)
				values.clear();
			
			docsData.clear();
			
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();
			HashMap<String, Integer> wght = new HashMap<String, Integer>();
			HashMap<String, Integer> packs = new HashMap<String, Integer>();
			
			List<Long> toRemoveIds = new ArrayList<Long>();
			for (Document<?> curDoc : documents) {
				int qty = 0;
				long sum = 0;
				int weight = 0;
				int packQty = 0;
				if (curDoc instanceof OrderImplBase<?>) {
					Order o = (Order)curDoc.getData();
					for(OrderItem oi : o.items)
						if(items == null || items.size() == 0 || items.contains(oi.id)) {
							qty += oi.qty;
							sum += ((long)oi.cost * oi.qty) / Consts.QTY_SCALE;
							Integer w = wght.get(oi.id);
							if( w == null ) {
								p.id = oi.id;
								if( pi.read() ) 
									w = p.weight;
								else
									w = 0;
								wght.put(oi.id, w);
								
								packs.put(oi.id, p.qtyInPack == 0 ? Consts.QTY_SCALE : p.qtyInPack);
							}
							weight += (int)((long)w * oi.qty) / Consts.QTY_SCALE;
							int qip = packs.get(oi.id);
							packQty += (int)((long)oi.qty * Consts.QTY_SCALE / qip);
						}
				}
				if( qty == 0 ) {
					toRemoveIds.add(curDoc.getRowid());
				} else {
					DocRowData drd = new DocRowData();
					drd.qty = qty;
					drd.sum = sum;
					drd.weight = weight;
					drd.packQty = packQty;
					
					docsData.put(curDoc.getRowid(), drd);
				}
			}
			documents.removeDocuments(toRemoveIds);
			pi.close();
			
			curDocType = docType;
			datePeriod = dp;
			notifyDataSetChanged();
		}
		
		private void prepareFilter() {
			items.clear();
			if( ft.size() == 0)
				ft.load();
			
			HashSet<Integer> folders = new HashSet<Integer>();			
			for(PriceRowData prd : srchFilter) {
				if(prd.isFolder == false)
					items.add(prd.id);
				else {
					collecFolders(folders, prd.fid);
				}
			}
			addFolderItems(folders);
		}

		private void addFolderItems(HashSet<Integer> folders) {
			if(folders.size() == 0)
				return;
			
			String sql = "select id from price where folderID in (";
			for(Integer i : folders)
				sql += i.toString() + ",";
			sql = sql.substring(0, sql.length() -1) + ")";
			
			Cursor c = null;
			try {
				c = DataBaseManager.getDataBase().rawQuery(sql, null);
				while(c.moveToNext())
					items.add(c.getString(0));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if( c != null )
					c.close();
			}			
		}

		private void collecFolders(HashSet<Integer> folders, int fid) {
			int fpos = ft.findFolder(fid);
			if( fpos >= 0 ) {
				Folder f = ft.get(fpos++);
				folders.add(f.id);
				int level = f.level;
				while(fpos < ft.size()) {
					f = ft.get(fpos++);
					if( f.level <= level )
						break;
					folders.add(f.id);
				}
			}
		}
	}
}

class PriceRowData {
	boolean isFolder;
	String id = "";
	String name = "";
	int fid = 0;
}

class DocRowData {
	long sum = 0;
	int qty = 0;
	int packQty = 0;
	int weight = 0;
	
	public void add(DocRowData value) {
		sum += value.sum;
		qty += value.qty;
		packQty += value.packQty;
		weight += value.weight;		
	}
}

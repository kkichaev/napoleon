package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FolderTree;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DeliveryList extends DocList {
	DocType prevType;
	List<PriceRowData> dlgFilter = new ArrayList<PriceRowData>(), srchFilter = new ArrayList<PriceRowData>();
	PriceAdapter priceAdapter;
	private static final int FILTER_ITEM_CODE = 0x50;
	
	public static void open(Context context){
		Intent i = new Intent(context, DeliveryList.class);
		context.startActivity(i);
	}
	
	@Override protected int getViewID() { return R.layout.dlvreport; }

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		prevType = DocType.getCurDoc();
		DocType.setCurDoc(DeliveryDoc.instance());
		
		btnSend.setVisibility(View.GONE);
		btnDelete.setVisibility(View.GONE);
		btnDocFilter.setVisibility(View.GONE);
		
		findViewById(R.id.tvDocSum).setVisibility(View.GONE);
		
		TextView tvFilter = (TextView) llFilterPanel.findViewById(R.id.tvFilter);
		tvFilter.setText("");
	};
	
	@Override protected void llFilterPanelClick() {}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing()) {
			DocType.setCurDoc(prevType);
			DocType.removeType(DeliveryDoc.instance());
		}
	}
	
	static class RowItem{
		Date date;
		String number;
		String name;
		String id;
		long cost;
		long disc;
		long qty;
		long sum;
	}
	
	@Override
	protected void adapterFilter(DatePeriod dp, String id) {
		if (adapter == null) {
			adapter = new DocListAdapterEx(this, (DocType) DocType.getCurDoc(), dp);
			lvDocs.setAdapter(adapter);
		}else
			super.adapterFilter(dp, id);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
	}
	
	@Override
	protected void refreshTotalSum(boolean useFilter) {
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		btnFilter.performClick();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return true;
	}
	
	protected void setFilterText(int d1,int m1, int y1,int d2, int m2, int y2, String org){
		TextView tvFilter = (TextView) llFilterPanel.findViewById(R.id.tvFilter);
		String data = getString(R.string.date_filter2, d1,m1+1,y1,d2,m2+1,y2);
		if( org != null )  
			data += "<br>по " + org;
		tvFilter.setText(Html.fromHtml(data));
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
	
	protected void addToFilter() {
		Intent i = new Intent(this, Warehouse.activity);
		i.putExtra(ExtrasConst.WAREHOUSE_ID_TAG, "");
		i.putExtra(ExtrasConst.FOLDER_SELECT_ID_TAG, 1);
		startActivityForResult(i, FILTER_ITEM_CODE);
	}
	
	protected void putFilter() {
		srchFilter.clear();
		srchFilter.addAll(dlgFilter);
	}

	protected void removeItems() {
		dlgFilter.clear();
		priceAdapter.notifyDataSetChanged();
	}

	
	static class PriceRowData {
		boolean isFolder;
		String id = "";
		String name = "";
		int fid = 0;
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
				arg1 = View.inflate(DeliveryList.this, R.layout.price_filter_row, null);
			
			PriceRowData prd = (PriceRowData)getItem(arg0);
			TextView tv = (TextView)arg1.findViewById(R.id.tvName);
			tv.setText(prd.name);
			
			ImageView iv = (ImageView)arg1.findViewById(R.id.ivFolder);
			iv.setVisibility(prd.isFolder ? View.VISIBLE : View.INVISIBLE);
			
			return arg1;
		}		
	}
	
	@Override protected int getFilterLayout() { return R.layout.date_selection_ex; }

	@Override
	protected void postUpdateFilterView(View view) {
		Button b = (Button)view.findViewById(R.id.btnItems);
		b.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(R.id.price_filter_list); }
		});
	}
	
	class DocListAdapterEx extends DocListAdapter{
		List<RowItem> datatItems;
		HashSet<String> items = new HashSet<String>();
		FolderTree ft = new FolderTree();
		
		public DocListAdapterEx(Context context, DocType docType, DatePeriod filter) {
			super(context, docType, filter);
		}
		
		@Override
		public void fetch(DocType docType, DatePeriod dp, String id, Price p) {
			prepareFilter();
			super.fetch(docType, dp, id, p);
		}
		
		@Override public int getCount() { 
			return datatItems.size(); 
		}
		
		@Override
		public Object getItem(int position) {
			return datatItems.get(position);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = View.inflate(DeliveryList.this, R.layout.dlvreportrow, null);
				
			RowItem ri = (RowItem) getItem(position);
			
			TextView tv = (TextView) convertView.findViewById(R.id.tvDate);
			tv.setText(Util.simpleDateFormat.format(ri.date));
			
			tv = (TextView) convertView.findViewById(R.id.tvNumber);
			tv.setText(ri.number);
			
			tv = (TextView) convertView.findViewById(R.id.tvName);
			tv.setText(ri.name);
			
			tv = (TextView) convertView.findViewById(R.id.tvCost);
			tv.setText(Util.IntToScaleStr(ri.cost, Consts.SUM_SCALE));
			
			tv = (TextView) convertView.findViewById(R.id.tvDiscount);
			tv.setText(Util.IntToScaleStr(ri.disc, Consts.SUM_SCALE) + " %");
			
			tv = (TextView) convertView.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(ri.qty, Consts.QTY_SCALE));
			
			tv = (TextView) convertView.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(ri.sum, Consts.SUM_SCALE));
			
			convertView.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
			
			return convertView;
		}
		
		@Override
		protected void postFetchByPeriod() {
			if(datatItems == null)
				datatItems = new ArrayList<RowItem>();
			
			datatItems.clear();
			PriceImpl price = new PriceImpl(); 
			OrderImpl order = new OrderImpl();
			
			for(Document<?> d : documents) {
				if(d instanceof DeliveryImpl) {
					DeliveryImpl dv = (DeliveryImpl)d;
					order.getData().created = dv.getData().created; 
					
					for(DeliveryItem di : dv.getData().items) {
						if(items == null || items.size() == 0 || items.contains(di.id)) {
							RowItem ri = new RowItem();
							ri.date = dv.getData().date;
							ri.number = dv.getData().number;
							
							if(price.read("id", di.id))
								ri.name = price.getData().name;
							
							else
								ri.name = "<" + di.id + ">";
							
							ri.id = di.id;
							ri.cost = ((DeliveryItemEx)di).cost;
							ri.qty = di.qty;
							ri.sum = di.sum;
							ri.disc = ((DeliveryItemEx)di).discount;
							
							datatItems.add(ri);
							
						}
					}
				}
			}
			
			price.close();
			order.close();
			
			Collections.sort(datatItems, new Comparator<RowItem>() {

				@Override
				public int compare(RowItem lhs, RowItem rhs) {
					int result = 0;
					
					result = lhs.date.compareTo(rhs.date);
					
					if(result == 0)
						result = lhs.name.compareTo(rhs.name);
					
					return result;
				}
			});
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
	};
	
	@Override
	protected DocListAdapter createListAdapter(DocType docType) {
		return null;
	}
	
}

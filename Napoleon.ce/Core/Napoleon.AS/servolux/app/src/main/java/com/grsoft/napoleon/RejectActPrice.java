package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RejectAct;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RejectActImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class RejectActPrice extends BaseActivity {
	RejectActImpl doc;
	boolean starting = true;
	Adapter adapter;
	
	protected LinesCountController linesController;
	
	public static void open(Context context, OrderImplBase<? extends Order> doc) {
		Intent i = new Intent(context, RejectActPrice.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.reject_price);
		
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		
		doc = new RejectActImpl();
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.setFormText(this);
	
		adapter = new Adapter();
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(lv, (ImageButton)findViewById(R.id.btnLines), this, true);
		linesController = linesOnClickListener.getController();
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { finish(); }
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(starting) {
			starting = false;
		} else {
			doc.read(doc.getRowid(), false);
			adapter.refresh();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	View.OnClickListener editItem = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			ActPriceData item =  (ActPriceData) arg0.getTag();
			doc.editItem(item.rowid, RejectActPrice.this);
		}
	};
	
	class Adapter extends BaseAdapter {

		List<ActPriceData> items = new ArrayList<ActPriceData>();
		
		public Adapter() { refresh(); }
		
		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@SuppressLint("UseSparseArrays")
		public void refresh() {
			PriceImpl pi = new PriceImpl();
			PriceEx p = (PriceEx) pi.getData();
			FolderImpl fi = new FolderImpl();
			Folder f = fi.getData();

			Map<String, ActPriceData> data = new HashMap<String, ActPriceData>();
			Map<Integer, ActPriceData> folders = new HashMap<Integer, ActPriceData>();
			
			RejectAct document = doc.getData();
			String orgId = document.id;
			
			Date stExpDate = Util.getDayStart(document.getStartExpiredDate());
			Date endExpDate = Util.getDayStart(document.getEndExpiredDate());
			DocList dl = DeliveryDoc.instance().docList(orgId, "", "firm='" + document.firmCode + "'");
			for(Document<?> d : dl) {
				Delivery ddoc = (Delivery)d.getData(); 
				for(DeliveryItem item : ddoc.items) {
					Date dexp = ((DeliveryItemEx)item).expired;
					if( !data.containsKey(item.id) && dexp.compareTo(stExpDate) >= 0 && dexp.compareTo(endExpDate) < 0) {
						ActPriceData apd = new ActPriceData();
						apd.id = item.id;
						p.id = item.id;
						if(!pi.read())
							continue;
						
						apd.name = p.getName();
						apd.rowid = pi.getRowid();
						apd.qty = doc.count(item.id);
						
						data.put(item.id, apd);
						
						ActPriceData fdata = folders.get(p.folderID);
						if(fdata == null) {
							f.id = p.folderID;
							fi.read();
							fi.close();

							fdata = new ActPriceData();
							fdata.id = f.fid;
							fdata.name = f.name;
							folders.put(p.folderID, fdata);
						}
						fdata.childs.add(apd);
					}
				}
			}
			dl.close();
			fi.close();
			pi.close();
			
			List<ActPriceData> fsrc = new ArrayList<ActPriceData>(folders.values());
			Collections.sort(fsrc);
			
			items.clear();
			for(ActPriceData i : fsrc) {
				items.add(i);
				Collections.sort(i.childs);
				for(ActPriceData api : i.childs)
					items.add(api);
			}
			
			notifyDataSetChanged();
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null) {
				view = View.inflate(RejectActPrice.this, R.layout.reject_act_price_row, null);
			}
			
			ActPriceData item = (ActPriceData) getItem(arg0);
			TextView tv = (TextView) view.findViewById(R.id.tvName);
			linesController.prepareTextView(tv);
			tv.setText(item.name);
			tv.setTextColor(item.qty > 0 && item.isFolder() == false ? getResources().getColor(R.color.item_highlight) : Color.BLACK);
			
			int bkId = R.drawable.list_selector;
			tv = (TextView) view.findViewById(R.id.tvQty);
			if(item.isFolder()) {
				tv.setText("");
				view.setOnClickListener(null);
				bkId = R.drawable.lt_gray_selector;
			} else {
				tv.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE, Util.DEC_DELIM, true));
				view.setTag(item);
				view.setOnClickListener(editItem);
			}
			
			view.setBackgroundResource(bkId);
			return view;
		}
		
	}
}

class ActPriceData implements Comparable<ActPriceData> {
	public String name = "";
	public String id = "";
	public long rowid = 0;
	public int qty = 0;
	
	public List<ActPriceData> childs = new ArrayList<ActPriceData>();
	
	public boolean isFolder() { return childs.size() > 0; }

	@Override public int compareTo(ActPriceData arg0) { return name.compareTo(arg0.name); }
}

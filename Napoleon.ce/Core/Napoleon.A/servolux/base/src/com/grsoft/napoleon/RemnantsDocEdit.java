package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.grsoft.dataobjects.ActionData;
import com.grsoft.dataobjects.Brands;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.MMLFeatures;
import com.grsoft.dataobjects.MatrixItemEx;
import com.grsoft.dataobjects.OrgDog;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSalesPlace;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.SalesTypes;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgMatrixImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RemnantsDocEdit extends BaseActivity implements SendResultListener, DataItemFilterManager.DataEvent {
	
	public static final int START_PLACE_ID = 1;

	int EL_WDH = 70;
	
	RemnantsImplEx doc = new RemnantsImplEx();
	List<SalesTypes> slsPlaces;
	List<FiltrableDataItem> allData;
	Adapter adapter;
	Map<String, Brands> brands;
	DataItemFilterManager filter;
	
	public static void open(Context context, RemnantsImplEx doc) {
		Intent i = new Intent(context, RemnantsDocEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);	
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.rmnt_doc_edit);
		
		brands = Brands.get();
		
		EL_WDH = getResources().getDimensionPixelSize(R.dimen.rmnt_item);
		
		long rowid;
		if( savedInstanceState == null )
			rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		else
			rowid = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);
		doc.read(rowid);
		
		View btnSend = findViewById(R.id.btnSend); 
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { send(); }
		});
		
		if( Features.CANT_SEND_SCRIPT_PART && 
				ScriptImpl.containsDocument(RemnantsDoc.instance().getObjectName(), doc.getData().created, doc.getId()))
			btnSend.setVisibility(View.GONE);

					
		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx) oi.getData();
		org.id = doc.getId();
		
		oi.read();
		oi.close();
		
		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(org.name);
	
		Map<String, SalesTypes> st = SalesTypes.getSalesTypes();
		slsPlaces = new ArrayList<SalesTypes>();
		for(OrgSalesPlace osp : org.salesPlaces) {
			if(st.containsKey(osp.id))
				slsPlaces.add(st.get(osp.id));
		}
		Collections.sort(slsPlaces);
		
		allData = loadData(org);
		adapter = new Adapter();
		adapter.refresh(allData);
		
		final LinearLayout ll = (LinearLayout)findViewById(R.id.llRestQty);
		int index = ll.indexOfChild(ll.findViewById(R.id.tvWhQty));
		boolean first = true;
		for(SalesTypes sti : slsPlaces) {
			TextView tv = new TextView(this);
			tv.setText(sti.name);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			tv.setBackgroundResource(R.color.table_caption);
			tv.setGravity(Gravity.RIGHT);
			tv.setPadding(0, 0, 2, 0);
			tv.setTextColor(Color.BLACK);
			tv.setWidth(EL_WDH);
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
			if(first)
				first = false;
			else
				lp.leftMargin = 1;
			lp.topMargin = 1;
			
			tv.setLayoutParams(lp);
			ll.addView(tv, index++);
		}
		
		ll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
            	ll.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        		int wdh = findViewById(R.id.tvQtyTitle).getWidth();
        		if(ll.getWidth() < wdh) {
        			int diff = wdh - ll.getWidth();
        			int firstElWidth = diff + EL_WDH;
        			ll.getChildAt(0).getLayoutParams().width = firstElWidth;  
        		}
            }
        });
	
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		
		filter = new DataItemFilterManager(null, (TextView)findViewById(R.id.tvBrand), 
				(TextView)findViewById(R.id.tvName), (TextView)findViewById(R.id.tvPrefix), allData, this);
	}
	
	protected void send() {
//		saveDoc();
		
		if(doc.isEmpty()) {
			Toast.makeText(this, R.string.cant_send_empty_doc_str, Toast.LENGTH_SHORT).show();
			return;
		}
		new DocumentSender(this, null, RemnantsDoc.instance().getObjectName(), doc, doc.getRowid(), this).execute((Void[])null);		
	}

	@SuppressLint("UseSparseArrays")
	private List<FiltrableDataItem> loadData(OrgEx org) {
		List<FiltrableDataItem> ret = new ArrayList<FiltrableDataItem>();

		final Map<String, DataItem> diMap = new HashMap<String, RemnantsDocEdit.DataItem>();
		final Map<Integer, DataItem> folderItem = new HashMap<Integer, RemnantsDocEdit.DataItem>();		
		final Map<Integer, Folder> folders = new HashMap<Integer, Folder>();
//		final Map<String, PriceEx> price = new HashMap<String, PriceEx>();
		final Set<String> mustBe = new HashSet<String>();
		final Set<String> mml;
		
		DataTraveler.travel(Folder.class, new DataTraveler.Travel<Folder>(true) {

			@Override
			public boolean travel(DataTraveler<Folder> item) {
				folders.put(item.data.id, item.data);
				return true;
			}
		}, "");

		for(Entry<OrgDog, List<MatrixItemEx>> kv : OrgMatrixImpl.getItems(org).entrySet()) {
			for(MatrixItemEx mie : kv.getValue()) {
				if(mie.mustBe != 0)
					mustBe.add(mie.id);
			}
		}
		
		CostStrategyEx cs = (CostStrategyEx) CostStrategy.defaultInstance;
		
		mml = MMLFeatures.orgMML(org);
		
		Set<String> loaded = new HashSet<String>();
		PriceImpl pimpl = new PriceImpl();
		PriceEx pe = (PriceEx)pimpl.getData();
		
		Date expDate = Util.getDayStart(new Date(doc.getDate().getTime()));// + 2 * 24 * 3600 * 1000));
		DocList dl = DeliveryDoc.instance().docList(doc.getId(), "date desc", "");
		for(Document<?> d : dl) {
			DeliveryEx ddoc = (DeliveryEx)d.getData(); 
			for(DeliveryItem item : ddoc.items) {
				if(loaded.contains(item.id))
					continue;
				if( ((DeliveryItemEx)item).expired.compareTo(expDate) >= 0) {
					loaded.add(item.id);
					
					pe.id = item.id;
					if( !pimpl.read() )
						 continue;
					
					ActionData ad = cs.getActionData(doc, pe.id);
					String prefix = 
							(ad != null) ? "A" :
							(mml.contains(item.id)) ? "M":
							(mustBe.contains(item.id)) ? "!" :
							"";
						
					DataItem pi = new DataItem(pe.getName(), item.id, prefix, pe.idBrand, (ad != null));
					pi.addIncome(ddoc, item, pe);

					DataItem fi = folderItem.get(pe.folderID);
					if(fi == null) {
						Folder f = folders.get(pe.folderID);
						if( f != null) {
							fi = new DataItem(f.name, f.fid, "", "", false);
						} else {
							fi = new DataItem(Integer.toString(pe.folderID), Integer.toString(pe.folderID), "", "", false);
						}
						folderItem.put(pe.folderID, fi);
					}
					
					fi.addChild(pi);
					diMap.put(item.id, pi);
				}
			}
		}
		dl.close();
		pimpl.close();
			
//		DataTraveler.travel(PriceEx.class, new DataTraveler.Travel<PriceEx>(true) {
//
//			@Override
//			public boolean travel(DataTraveler<PriceEx> item) {
//				price.put(item.data.id, item.data);
//				String prefix = 
//					(mml.contains(item.data.id)) ? "M":
//					(mustBe.contains(item.data.id)) ? "!" :
//					"";
//				
//				DataItem pi = new DataItem(item.data.getName(), item.data.id, prefix, item.data.idBrand);
//
//				DataItem fi = folderItem.get(item.data.folderID);
//				if(fi == null) {
//					Folder f = folders.get(item.data.folderID);
//					if( f != null) {
//						fi = new DataItem(f.name, f.fid, "", "");
//					} else {
//						fi = new DataItem(Integer.toString(item.data.folderID), Integer.toString(item.data.folderID), "", "");
//					}
//					folderItem.put(item.data.folderID, fi);
//				}
//				
//				fi.addChild(pi);
//				diMap.put(item.data.id, pi);
//				return true;
//			}
//		}, "");
		
		listDataByFolders(ret, folderItem);
//		loadIncomeData(diMap, price);
//		loadRemnantData(diMap);
		
		return ret;
	}

	boolean started = true;
	@Override
	protected void onResume() {
		if(started) {
			started = false;
		} else { 
			doc.read(doc.getRowid(), false);
			adapter.notifyDataSetChanged();
		}
		super.onResume();
	}
	
//	private void loadRemnantData(final Map<String, DataItem> diMap) {
//		for(RemnantItem ri : doc.getData().items) {
//			DataItem dataItem = diMap.get(ri.id);
//			if(dataItem != null)
//				dataItem.addQty((RemnantItemEx) ri);
//		}
//	}

	private void listDataByFolders(List<FiltrableDataItem> ret, Map<Integer, DataItem> folderItem) {
		List<FiltrableDataItem> fld = new ArrayList<FiltrableDataItem>();
		fld.addAll(folderItem.values());
		Collections.sort(fld);
		
		for(FiltrableDataItem di : fld) {
			List<FiltrableDataItem> chi = di.getChilds();
			if(chi == null)
				continue;
			
			ret.add(di);
			Collections.sort(chi);
			for(FiltrableDataItem ci : chi) {
				ret.add(ci);
			}
		}
	}

//	private void loadIncomeData(final Map<String, DataItem> diMap, Map<String, PriceEx> price) {
//		String where = "id = '" + doc.getId() + 
//				"' and date = (select max(date) from delivery d2 where d2.firm = delivery.firm and d2.id = delivery.id) order by delivery.date desc";
//		
//		DeliveryEx d = new DeliveryEx();
//		DbReader r = new DbReader();
//		boolean bdo = r.select(d, "delivery", where);
//		while(bdo) {
//			for(DeliveryItem di : d.items) {
//				DataItem dataItem = diMap.get(di.id);
//				PriceEx pe = price.get(di.id);
//				if(dataItem != null && pe != null)
//					dataItem.addIncome(d, di, pe);
//			}
//			
//			bdo = r.selectNext(d);
//		}
//	}
	
//	void saveDoc() {
//		if(!doc.isEditable())
//			return;
//		
//		Remnants data = doc.getData();
//		data.items.clear();
//		
//		for(FiltrableDataItem di : allData) {
//			RemnantItemEx ri = ((DataItem)di).createItem();
//			if( ri != null )
//				data.items.add(ri);
//		}
//		
//		if(!doc.isEmpty())
//			doc.write();
//	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		saveDoc();
		if( isFinishing() ) {
			if(doc.isEmpty())
				doc.delete();
		}
	}

	@Override
	protected void onDestroy() {
		doc.close();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		super.onSaveInstanceState(outState);
	}
	
	class DataItem extends FiltrableDataItem implements View.OnClickListener {

		int incomeQty;
		Date incomeDate = null;
		boolean inAction;
		
//		Map<String, Integer> qty;
		
		public DataItem(String name, String id, String prefix, String brand, boolean inAction) {
			this.name = name;
			this.id = id;
			this.prefix = prefix;
			this.brand = brand;
			this.inAction = inAction;
		}
		
//		public void addQty(RemnantItemEx item) {
//			if(qty == null)
//				qty = new HashMap<String, Integer>();
//			for(RmntSalesPlaceQty i : item.items)
//				qty.put(i.id, i.qty);
//		}
		
		public void addIncome(DeliveryEx d, DeliveryItem item, PriceEx price) {
//			int qip = Consts.QTY_SCALE;
//			if(price != null)
//				qip = price.qtyInPack;
//			incomeQty = (int)((long)item.qty * Consts.QTY_SCALE / qip);
			
			incomeQty = item.qty;
			incomeDate = d.date;
		}
		
		@Override public int getQty() { return doc.countQty(id); }
		
		int countQty(String salesId) {
			if(childs != null) {
				int ret = 0;
				for(FiltrableDataItem di : childs) {
					ret += ((DataItem)di).countQty(salesId);
				}
				return ret;
			}
			
			return doc.getQty(this.id, salesId);
//			if(qty != null) {
//				Integer val = qty.get(id);
//				return (val == null) ? 0 : val;
//			}
//			return 0;
		}
		
		@Override public FiltrableDataItem createFolderItem() { return new DataItem(name, id, prefix, brand, false); }		
		
		public void drawData(View v) {
			
			int bkColor = (childs == null) ? Color.WHITE : Color.LTGRAY;
			int totalBkColor = Color.LTGRAY;
			int bkQtyId = (childs == null) ? R.drawable.list_selector : R.drawable.lt_gray_selector;
			int color = (inAction) ? Color.RED : Color.BLACK;
			
			String text = "";
			TextView tv;
			
			List<SalesTypes> src = new ArrayList<SalesTypes>();
			src.add(new SalesTypes());
			src.addAll(slsPlaces);
			int totalQty = countTotals(src);
			
			tv = (TextView)v.findViewById(R.id.tvMark);
			tv.setText(prefix);
			tv.setBackgroundColor(bkColor);
			tv.setTextColor(color);

			tv = (TextView)v.findViewById(R.id.tvName);
			tv.setText(name);
			tv.setBackgroundColor(bkColor);
			tv.setTextColor(color);
			
			text = "";
			if(childs == null && brand.length() > 0) {
				Brands b = brands.get(brand);
				if(b != null)
					text = b.name;
			}
			tv = (TextView)v.findViewById(R.id.tvBrand);
			tv.setText(text);
			tv.setBackgroundColor(bkColor);
			tv.setTextColor(color);
			
			text = "";
			if(incomeDate != null) {
				text = Util.IntToScaleStr(incomeQty, Consts.QTY_SCALE) + "\n" + Util.simpleDateFormat.format(incomeDate);
				if(totalQty == 0) {
					bkQtyId = R.drawable.red_selector;
					totalBkColor = Color.RED;
				}
			}
			tv = (TextView)v.findViewById(R.id.tvDlv);
			tv.setText(text);
			tv.setBackgroundColor(bkColor);
			tv.setTextColor(color);
			
			
			int id = R.id.tvWhQty;
			for(SalesTypes sti : src) {
				int qi = countQty(sti.id);
				if(sti.id.length() == 0 && qi != 0 && qi == totalQty)
					bkQtyId = R.drawable.orange_selector;

				text = "";
				if(qi != 0)
					text = Util.IntToScaleStr(qi, Consts.QTY_SCALE); 
				
				tv = (TextView)v.findViewById(id); 
				tv.setText(text);
				tv.setTag(sti);
				tv.setTextColor(color);
				if(childs == null) {
					tv.setOnClickListener(this);
					tv.setBackgroundResource(bkQtyId);
				} else
					tv.setBackgroundColor(bkColor);
				
				if(id == R.id.tvWhQty)
					id = START_PLACE_ID;
				else
					id++;
			}
			
			text = "";
			if(totalQty != 0)
				text = Util.IntToScaleStr(totalQty, Consts.QTY_SCALE);
			tv = (TextView)v.findViewById(R.id.tvTotalQty); 
			tv.setText(text);
			tv.setBackgroundColor(totalBkColor);
			tv.setTextColor(color);

//			v.setBackgroundResource((childs != null) ? R.drawable.lt_gray_selector : R.drawable.list_selector);
		}

		private int countTotals(List<SalesTypes> src) {
			int ret = 0;
			for(SalesTypes st : src)
				ret += countQty(st.id);
			return ret;
		}

		@Override
		public void onClick(View v) {
			if(!doc.isEditable())
				return;
			
			final SalesTypes st = (SalesTypes)v.getTag();
			RemnantsPriceCount.open(RemnantsDocEdit.this, doc, id, st);
			
//			if(qty == null)
//				qty = new HashMap<String, Integer>();
//			final Integer val = qty.get(st.id);
//			InputNumberDlg.open(v.getContext(), new InputNumber() {
//				
//				@Override public int getValue() { return val == null ? 0 : val; }
//				
//				@Override
//				public void applayInput(int value, Object... params) {
//					qty.put(st.id, value);
//					adapter.notifyDataSetChanged();
//				}
//			}, Consts.QTY_SCALE, true, "¬ведите остаток");
		}

//		public RemnantItemEx createItem() {
//			if( childs != null || qty == null)
//				return null;
//			
//			RemnantItemEx ret = new RemnantItemEx();
//			ret.id = id;
//			ret.qty = 0;
//			ret.uid = UUID.randomUUID().toString().replace("-", "");
//			
//			for(java.util.Map.Entry<String, Integer> kv : qty.entrySet()) {
//				RmntSalesPlaceQty rqi = new RmntSalesPlaceQty();
//				rqi.id = kv.getKey();
//				rqi.qty = kv.getValue();
//				ret.qty += rqi.qty;
//				ret.items.add(rqi);
//			}
//			return ret.qty == 0 ? null : ret;
//		}
	}
	
	class Adapter extends BaseAdapter {

		List<FiltrableDataItem> items;
		
		public Adapter() {}
		
		public void refresh(List<FiltrableDataItem> items) { 
			this.items = items;
			notifyDataSetChanged();
		}
		
		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View v, ViewGroup arg2) {
			if(v == null) {
				v = View.inflate(RemnantsDocEdit.this, R.layout.rmnt_item, null);
				int id = START_PLACE_ID;
				int index = ((ViewGroup)v).indexOfChild(v.findViewById(R.id.tvWhQty));
				for(@SuppressWarnings("unused") SalesTypes sti : slsPlaces) {
					TextView tv = new TextView(RemnantsDocEdit.this);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					tv.setGravity(Gravity.CENTER_HORIZONTAL);
					tv.setTextColor(Color.BLACK);
					tv.setWidth(EL_WDH);
					tv.setBackgroundColor(Color.WHITE);
					
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
					if(id != START_PLACE_ID)
						lp.leftMargin = 1;
					
					tv.setId(id++);
					tv.setLayoutParams(lp);
					((ViewGroup)v).addView(tv, index++);
				}
			}
			
			DataItem di = (DataItem) getItem(pos);
			di.drawData(v);
			return v;
		}
		
	}

	@Override
	public void postSendExecute(boolean result) {
		if(result)
			doc.read(doc.getRowid(), false);
	}

	@Override
	public void dataFiltred(List<FiltrableDataItem> newList) {
		adapter.refresh(newList);
	}

	@Override
	public void askShowDialog(int id) {
		Dialog ret = filter.createDialog(id);
		if(ret != null)
			ret.show();
	}
}

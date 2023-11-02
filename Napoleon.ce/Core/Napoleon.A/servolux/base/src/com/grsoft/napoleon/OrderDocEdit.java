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
import java.util.UUID;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.ActionData;
import com.grsoft.dataobjects.Brands;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.MMLFeatures;
import com.grsoft.dataobjects.MatrixItemEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgDog;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PlanQtyData;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.impl.AgentPlanNewImpl;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgMatrixImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.OffTakeHistory.OffTakeInflator;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.DisabledFirms;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderDocEdit extends BaseActivity implements DataItemFilterManager.DataEvent, DisabledFirms.Handler {
	static final String LINKED_ID = "linkedId";

	protected static final int DIALOG_DATE_PICKER_ID = 0x124;
	
	Map<String, Brands> brands;

	Map<String, Integer> prevRest = new HashMap<String, Integer>();
	Map<String, Integer> prevDelivery = new HashMap<String, Integer>();
	Map<String, Integer> dailyOrder = new HashMap<String, Integer>();
	Map<String, Integer> autoOrder = new HashMap<String, Integer>();
	Map<String, Integer> planData;
	Map<String, List<DataItem>> itemsByFirm = new HashMap<String, List<DataItem>>();
	Set<String> showedPlanItems = new HashSet<String>();

	Map<String, Integer> priceInpack = new HashMap<String, Integer>();
//	OrderBundleImpl bundleDoc;
	
	protected LinesCountController linesController;
	
	
	Map<String, FirmEx> firms;
	
	List<FiltrableDataItem> allItems;
	Set<String> selectedPriceItems = new HashSet<String>();
	DataItemFilterManager filter;
	
	
	RemnantsImpl remnantsDoc = new RemnantsImpl();
	Adapter adapter;
	
	Date prevVisit = null;
	Date docDate = null;
	Date dlvDate = null;
	long linked = 0;
	String orgId = "";
	
	Map<String, OrderImplEx> docs = new HashMap<String, OrderImplEx>();

//	boolean saved = false;
//	Map<String, Long> weights = new HashMap<String, Long>();
	
	static public void open(Context context, long linked) {
		Intent i = new Intent(context, OrderDocEdit.class);
//		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(LINKED_ID, linked);
		context.startActivity(i);	
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_doc_edit);

		CfgNplW cfg = (CfgNplW) ConfigManager.getConfig();
		if(cfg.keepAwayInOrder)
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		brands = Brands.get();
		firms = FirmEx.get();
		
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		linked = b.getLong(LINKED_ID, 0);
		if(linked == 0) {
			Toast.makeText(getApplicationContext(), "Ошибка в параметре", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
//		bundleDoc = new OrderBundleImpl();
//		bundleDoc.read(linked);
		
		refreshDocs();
		if(docs.size() == 0) {
			Toast.makeText(getApplicationContext(), "Нет документов", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		OrderImplEx oe = getFirstDoc();
		OrderEx ord = (OrderEx)oe.getData();
		
		orgId = ord.id;
		docDate = Util.getDayStart(ord.date);
		dlvDate = ord.dlvDate;
		
		View btnSend = findViewById(R.id.btnSend); 
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { send(); }
		});
//		if(ScriptImpl.containsDocument(OrderBundleDoc.instance().getObjectName(), new Date(linked), orgId))
//			btnSend.setVisibility(View.GONE);
		
		findViewById(R.id.btnQtyFilter).setOnClickListener(new View.OnClickListener() {
			@Override 
			public void onClick(View arg0) {
				filter.setQtyFilter(!filter.isQtyFilterSet());
			}
		});
		
		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx) oi.getData();
		org.id = orgId;
		oi.read();
		oi.close();
		
		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(org.name);
	
		CostStrategyEx cs = (CostStrategyEx) CostStrategy.defaultInstance;
		allItems = loadData(org, cs, oe);
		adapter = new Adapter(allItems);
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				DataItem di = (DataItem) arg0.getItemAtPosition(arg2);
				if(di != null)
					di.onClick(arg1);
			}
		});

		ImageView btnLines = (ImageButton) findViewById(R.id.btnLines);
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(lv, btnLines, this, true);
		linesController = linesOnClickListener.getController();
		
		
		filter = new DataItemFilterManager((TextView)findViewById(R.id.tvFirm), (TextView)findViewById(R.id.tvBrand), 
				(TextView)findViewById(R.id.tvName), (TextView)findViewById(R.id.tvPrefix), allItems, this);
		filter.setFirms(itemsByFirm.keySet());
		
		refreshDate();
		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!isEditable())
					return;
				Intent i = new Intent(arg0.getContext(), CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, dlvDate);
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});
	}

	private OrderImplEx getFirstDoc() {
		OrderImplEx oe = null;
		for(String key : docs.keySet()) {
			oe = docs.get(key);
			break;
		}
		return oe;
	}
	
	boolean isEditable() {
		boolean ret = true;
		for(OrderImplEx doc : docs.values())
			if(!doc.isEditable()) {
				ret = false;
				break;
			}
		return ret;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		for(OrderImplEx doc : docs.values()) {
			if(doc.isEmpty())
				doc.delete();
			doc.close();
		}
//		bundleDoc.refreshDocs();
//		if(bundleDoc.isEmpty())
//			bundleDoc.delete();
	}
	
	@Override
	protected void onPause() {
//		bundleDoc.refreshDocs();
		super.onPause();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null ) {
			if( requestCode == DIALOG_DATE_PICKER_ID ) {
				Date curDate = new Date();
				long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
				Date checkDate = Util.getNextDay(null);
				if(ct < checkDate.getTime()) {
					Toast.makeText(this, "Дата должна быть больше текущей", Toast.LENGTH_SHORT).show();
					return;
				}
				dlvDate = new Date(ct);
				docDate = new Date(ct - 24 * 2600 * 1000);
				changeDocsDate();
				refreshDate();
				refreshCurrentData(false);
			}
		}
	}
	
	private void changeDocsDate() {
		OrderImplEx oe = null;
		for(OrderImplEx doc : docs.values()) {
			if(oe == null)
				oe = doc;
			OrderEx d = (OrderEx)doc.getData();
			d.dlvDate = dlvDate;
			d.date = docDate;
			doc.write();
		}
		
		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx) oi.getData();
		org.id = orgId;
		oi.read();
		oi.close();
		
		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(org.name);
	
		CostStrategyEx cs = (CostStrategyEx) CostStrategy.defaultInstance;
		allItems = loadData(org, cs, oe);
		adapter.setItems(allItems);
		
		filter = new DataItemFilterManager((TextView)findViewById(R.id.tvFirm), (TextView)findViewById(R.id.tvBrand), 
				(TextView)findViewById(R.id.tvName), (TextView)findViewById(R.id.tvPrefix), allItems, this);
		filter.setFirms(itemsByFirm.keySet());
	}

	private void refreshDate() {
		TextView tv = (TextView) findViewById(R.id.tvDate);
		String text = "Доставка <u><font color='blue'>" + Util.simpleDateFormat.format(dlvDate) + "</font></u> Дата плана " +
				Util.simpleDateFormat.format(docDate);
		tv.setText(Html.fromHtml(text));
	}

	void refreshDocs() {
		docs.clear();
		
		OrderImplEx oe = null;
		OrderEx ord = null;
		List<Long> ids = DbReader.readIds((new OrderEx()).getTableName(), "linked=" + Long.toString(linked), "");		
		for(Long id : ids) {
			oe = new OrderImplEx();
			oe.read(id);
			ord = (OrderEx)oe.getData();
			docs.put(ord.firmCode, oe);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(LINKED_ID, linked);
	}
	
	void checkMatrix(Map<OrgDog, List<MatrixItemEx>> matrix, Map<String, Map<String, OrderItem>> curQty) {
		for(Entry<String, Map<String, OrderItem>> kv : curQty.entrySet()) {
			String firm = kv.getKey();
			OrgDog dog = null;
			for(OrgDog od : matrix.keySet()) {
				if(od.firm.equals(kv.getKey())) {
					dog = od;
					break;
				}
			}
			if(dog == null) {
				dog = new OrgDog();
				dog.id = UUID.randomUUID().toString();
				dog.firm = firm;
				matrix.put(dog, new ArrayList<MatrixItemEx>());
			}
			
			for(String id : kv.getValue().keySet()) {
				List<MatrixItemEx> ml = matrix.get(dog);
				boolean finded = false;
				for(MatrixItemEx mie : ml) {
					if(mie.id.equals(id)) {
						finded = true;
						break;
					}
				}
				if(!finded) {
					MatrixItemEx mie = new MatrixItemEx();
					mie.id = id;
					ml.add(mie);
				}
			}
		}
	}	

	@SuppressLint("UseSparseArrays")
	private List<FiltrableDataItem> loadData(OrgEx org, CostStrategyEx cs, OrderImplEx refDoc) {
		List<FiltrableDataItem> ret = new ArrayList<FiltrableDataItem>();
		Set<String> mml = MMLFeatures.orgMML(org);
		
		showedPlanItems.clear();

		final Map<String, Map<String, OrderItem>> curQty = new HashMap<String, Map<String,OrderItem>>();
		DataTraveler.travel(OrderEx.class, new DataTraveler.Travel<OrderEx>() {

			@Override
			public boolean travel(DataTraveler<OrderEx> item) {
				Map<String, OrderItem> moi = curQty.get(item.data.firmCode);
				if(moi == null) {
					moi = new HashMap<String, OrderItem>();
					curQty.put(item.data.firmCode, moi);
				}
				
				for(OrderItem oi : item.data.items) {
					moi.put(oi.id, oi);
				}
				return true;
			}
		}, "id='" + orgId + "' and linked=" + Long.toString(linked));
		
		itemsByFirm.clear();
		
		Map<Integer, DataItem> folders = new HashMap<Integer, OrderDocEdit.DataItem>();
		FolderImpl fi = new FolderImpl();
		Folder f = fi.getData();
		
		PriceImpl pi = new PriceImpl();
		PriceEx pe = (PriceEx)pi.getData();
		Map<OrgDog, List<MatrixItemEx>> matrix = OrgMatrixImpl.getItems(org);
		Map<OrgDog, List<MatrixItemEx>> matrixPlan = AgentPlanNewImpl.getPlansAsMatrix(docDate, org);
		if(matrix == null || matrix.size() == 0)
			matrix = matrixPlan;
		else
			removeNotInPlan(matrix, matrixPlan);
		checkMatrix(matrix, curQty);
		
		for(Entry<OrgDog, List<MatrixItemEx>> kv : matrix.entrySet()) {
			List<DataItem> orgItems = new ArrayList<OrderDocEdit.DataItem>();
			String cfirm = kv.getKey().firm;
			itemsByFirm.put(cfirm, orgItems);
			for(MatrixItemEx mie : kv.getValue()) {
				pe.id = mie.id;
				if( !pi.read() )
					continue;
				
				priceInpack.put(pe.id, pe.qtyInPack);
				
				showedPlanItems.add(mie.id);
				
				ActionData ad = cs.getActionData(refDoc, pe.id);
				String prefix = 
						(ad != null) ? "A" :
						(mml.contains(mie.id)) ? "M":
						(mie.mustBe > 0) ? "!" :
						"";
				
				int cost = (ad != null) ? ad.cost : cs.getItemCost(pe, refDoc);
				DataItem di = new DataItem(pe.getName(), pe.id, prefix, cfirm, pe.idBrand, pe.qtyInPack, cost, (ad != null));
				orgItems.add(di);
				
//				Map<String, OrderItem> moi = curQty.get(cfirm);
//				if(moi != null) {
//					OrderItem val = moi.get(pe.id);
//					if(val != null)
//						di.setQty(val.qty, val.inPack());
//				}
				
				DataItem fitem = folders.get(pe.folderID);
				if(fitem == null) {
					f.id = pe.folderID;
					fi.read();
					fitem = new DataItem(f.name, f.fid, "", "", "", Consts.QTY_SCALE, 0, false);
					folders.put(pe.folderID, fitem);
				}
				fitem.addChild(di);
			}
		}
		pi.close();
		fi.close();
		
		List<DataItem> fldItems = new ArrayList<OrderDocEdit.DataItem>();
		fldItems.addAll(folders.values());
		Collections.sort(fldItems);
		for(DataItem fldItem : fldItems) {
			ret.add(fldItem);
			fldItem.sort();
			for(FiltrableDataItem chi : fldItem.childs)
				ret.add((DataItem)chi);
		}
		return ret;
	}

	private void removeNotInPlan(Map<OrgDog, List<MatrixItemEx>> matrix, Map<OrgDog, List<MatrixItemEx>> matrixPlan) {
		
		List<OrgDog> rmvDog = new ArrayList<OrgDog>();
		for(Entry<OrgDog, List<MatrixItemEx>> kv : matrix.entrySet()) {
			List<MatrixItemEx> src = matrixPlan.get(kv.getKey());
			if(src == null) {
				rmvDog.add(kv.getKey());
				continue;
			}
			List<MatrixItemEx> needRmv = new ArrayList<MatrixItemEx>();
			for(MatrixItemEx mie : kv.getValue()) {
				boolean find = false;
				for(MatrixItemEx srcI : src) {
					if(srcI.id.equals(mie.id)) {
						find = true;
						break;
					}
				}
				if(!find)
					needRmv.add(mie);
			}
			
			kv.getValue().removeAll(needRmv);
			if(kv.getValue().size() == 0)
				rmvDog.add(kv.getKey());
		}
		
		for(OrgDog rd : rmvDog)
			matrix.remove(rd);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		refreshCurrentData(true);

		TextView tv = (TextView)findViewById(R.id.tvPrevDate);
		String text = "";		
		if( prevVisit != null ) {
			text = Util.simpleDateFormat.format(prevVisit);
		} else
			text = "не определена";
		tv.setText(text);
	}

//	@Override
//	protected void onPause() {
//		if(!saved)
//			saveData(isFinishing());
//		saved = true;
//		super.onPause();
//	}
	
	ProgressDialog pd = null;
	private void checkFirmDisable() {
		pd = ProgressDialog.show(this, "Подождите, пожалуйста", "Проверка запрета отправки");
		DisabledFirms.loadDisabledFirms(this, this);
	}

	void closeWaitDialog() {
		if( pd != null ) {
			pd.dismiss();
			pd = null;
		}
	}

	void send() {
		checkFirmDisable();
	}
	
	OrderImplEx getOrCreate(String firm) {
		OrderImplEx doc = docs.get(firm);
		if(doc == null) {
			doc = new OrderImplEx();
			doc.copyFrom(getFirstDoc(), Util.getDateTime().getTime());
			((OrderEx)doc.getData()).firmCode = firm;
			doc.write();
			docs.put(firm, doc);
		}
		return doc;
	}
	
//	private List<OrderImplEx> saveData(boolean finishing) {
//		if(refOrderRID == ExtrasConst.INVALID_ROWID)
//			return null;
//		
//		OrderImplEx ref = new OrderImplEx();
//		ref.read(refOrderRID);
//		OrderEx oe = (OrderEx)ref.getData();
//		long created = oe.created.getTime();
//		
//		Map<String, OrderImplEx> orders = new HashMap<String, OrderImplEx>();
//		orders.put(oe.firmCode, ref);
//		if(ref.isEditable()) {
//			oe.params = 0;
//			oe.items.clear();
//		}
//		
//		String where = "id='" + orgId + "' and linked=" + Long.toString(linked);
//		List<Long> rids = DbReader.readIds(ref.getTableName(), where, "");
//		for(Long rid : rids) {
//			if(rid == refOrderRID) continue;
//			
//			OrderImplEx oie = new OrderImplEx();
//			oie.read(rid);
//			OrderEx oedoc = (OrderEx)oie.getData();
//			if(oie.isEditable()) {
//				oedoc.params = 0;
//				oedoc.items.clear();
//			}
//			
//			if(oedoc.created.getTime() > created)
//				created = oedoc.created.getTime();
//			orders.put(oedoc.firmCode, oie);
//			oie.write();
//		}
//		
//		PriceImpl pi = new PriceImpl();
//		PriceEx pe = (PriceEx)pi.getData();
//		CostStrategy cs = CostStrategy.getInstance(ref.getClass());
//		for(Entry<String, List<DataItem>> kv : itemsByFirm.entrySet()) {
//			OrderImplEx srcOrder = orders.get(kv.getKey());
//			if(srcOrder == null) {
//				srcOrder = new OrderImplEx();
//				created += Consts.ONE_SECOND;
//				srcOrder.copyFrom(ref, created);
//				((OrderEx)srcOrder.getData()).firmCode = kv.getKey();
//				orders.put(kv.getKey(), srcOrder);
//			}
//			if(srcOrder.isEditable() == false)
//				continue;
//			
//			for(DataItem di : kv.getValue()) {
//				int qty = di.getQty();
//				if(qty == 0)
//					continue;
//				pe.id = di.id;
//				pi.read();
//				srcOrder.addItem(pe, qty, cs.getItemCost(pe, srcOrder), di.inPack);
//			}
//			srcOrder.write();
//		}
//		
//		if(finishing) {
//			List<String> deleted = new ArrayList<String>();
//			for(OrderImplEx oie : orders.values()) {
//				if(oie.isEditable() && oie.getData().items.size() == 0) {
//					oie.delete();
//					deleted.add(((OrderEx)oie.getData()).firmCode);
//					oie.close();
//				}
//			}
//			for(String key : deleted)
//				orders.remove(key);
//		}
//		List<OrderImplEx> ret = new ArrayList<OrderImplEx>();
//		ret.addAll(orders.values());
//		for(OrderImplEx ord : ret)
//			ord.close();
//		return ret;
//	}
	

	private void refreshCurrentData(boolean refreshDocs) {
		if(refreshDocs)
			refreshDocs();
		
		planData = new HashMap<String, Integer>(); 
		Map<String, PlanQtyData> pqd = AgentPlanNewImpl.getPlans(null, docDate);
		for(Entry<String, PlanQtyData> kv : pqd.entrySet()) {
			if(showedPlanItems.contains(kv.getKey()) == false)
				continue;
			
			PlanQtyData pq = kv.getValue();
			int val = pq.qty; //(int)((long)pq.qty * pq.inPack / Consts.QTY_SCALE);
			planData.put(kv.getKey(), val);
		}
		
		prevRest.clear();
		prevDelivery.clear();		
		dailyOrder.clear();
		autoOrder.clear();
		prevVisit = null;
				
		loadPrevRest();
		loadDailyOrder();
		loadPrevDelivery(prevVisit);
		countAutoOrder();
		
		updateTotals();
		
		if(adapter != null)
			adapter.notifyDataSetChanged();
	}

	void loadPrevRest() {
		String where = "created < " + Long.toString(docDate.getTime());
		DocList dl = RemnantsDoc.instance().docList(orgId, "created desc", where);
		for(Document<?> d : dl) {
			Remnants rdoc = (Remnants) d.getData();
			prevVisit = Util.getDayStart(rdoc.created);
			for(RemnantItem ri : rdoc.items) {
				Integer qip = priceInpack.get(ri.id);
				if(qip == null || qip == 0)
					qip = Consts.QTY_SCALE;
				
				int qty = (int)((long)ri.qty * Consts.QTY_SCALE / qip);;
				prevRest.put(ri.id, qty);
			}
			break;
		}
		dl.close();
	}

	void loadPrevDelivery(Date prevDay) {
		if(prevDay == null)
			return;
		
		String where = "date >= " + Long.toString(Util.getDayStart(prevDay).getTime()) + " and date < " + Long.toString(docDate.getTime());
		DocList dl = DeliveryDoc.instance().docList(orgId, null, where);
		for(Document<?> d : dl) {
			for(DeliveryItem di : ((Delivery)d.getData()).items) {
				Integer qty = prevDelivery.get(di.id);
				if( qty == null )
					qty = 0;
				Integer qip = priceInpack.get(di.id);
				if(qip == null || qip == 0)
					qip = Consts.QTY_SCALE;
				
				qty += (int)((long)di.qty * Consts.QTY_SCALE / qip);;
				prevDelivery.put(di.id, qty);
			}
		}
		dl.close();
	}

	private void loadDailyOrder() {
		String where = "created >= " + Long.toString(Util.getDayStart(docDate).getTime()) + 
				" and created <= " + Long.toString(Util.getDayEnd(docDate).getTime()) + 
				" and linked <> " + Long.toString(linked);
		DocList dl = OrderDoc.instance().docList(null, null, where);
		for(Document<?> d : dl) {
			for(OrderItem oi : ((Order)d.getData()).items) {
				Integer qty = dailyOrder.get(oi.id);
				if( qty == null )
					qty = 0;
				Integer qip = priceInpack.get(oi.id);
				if(qip == null || qip == 0)
					qip = Consts.QTY_SCALE;
				
				qty += (int)((long)oi.qty * Consts.QTY_SCALE / qip);;
				dailyOrder.put(oi.id, qty);
			}
		}
		dl.close();
	}

	void countAutoOrder() {
		HashMap<String, Integer> curRest = new HashMap<String, Integer>();
		long rdoc = RemnantsImpl.find(orgId, docDate);
		if(rdoc != ExtrasConst.INVALID_ROWID) {
			remnantsDoc.read(rdoc);

			for(RemnantItem ri : remnantsDoc.getData().items)
				curRest.put(ri.id, ri.qty);
		}
		
		HashMap<String, Integer> prevData = new HashMap<String, Integer>();
		for(Entry<String, Integer> kv : prevRest.entrySet())
			prevData.put(kv.getKey(), kv.getValue());
		for(Entry<String, Integer> kv : prevDelivery.entrySet()) {
			Integer rest = prevData.get(kv.getKey());
			if( rest == null)
				rest = 0;
			prevData.put(kv.getKey(), kv.getValue() + rest);
		}
		
		double coef = (double)OffTakeInflator.OFF_TAKE_COEF / 100.0;
		
		for(Entry<String, Integer> kv : prevData.entrySet()) {
			int value = kv.getValue();
			
			Integer cv = curRest.get(kv.getKey());
			if( cv == null)
				cv = 0;
			value -= cv;
			value *= coef;
			value -= cv;
			if( value < 0 )
				value = 0;
			autoOrder.put(kv.getKey(), value);
		}
	}

	private void updateTotals() {
		TextView tv;
		
		tv = (TextView)findViewById(R.id.tvPrevRest);
		tv.setText(Util.IntToScaleStr(countTotals(prevRest), 0));
		
		tv = (TextView)findViewById(R.id.tvPrevSales);
		tv.setText(Util.IntToScaleStr(countTotals(prevDelivery), 0));
		
		tv = (TextView)findViewById(R.id.tvCurRest);
		tv.setText(Util.IntToScaleStr(remnantsDoc == null ? 0 : remnantsDoc.qty(), 0));

		tv = (TextView)findViewById(R.id.tvAutoOrder);
		tv.setText(Util.IntToScaleStr(countTotals(autoOrder), 0));

		SumQty qty = countQty();
		tv = (TextView)findViewById(R.id.tvCurOrder);
		tv.setText(Util.IntToScaleStr(qty.qty, Consts.QTY_SCALE));

		tv = (TextView)findViewById(R.id.tvSum);
		tv.setText(Util.IntToScaleStr(qty.sum, Consts.SUM_SCALE));

		tv = (TextView)findViewById(R.id.tvPlan);
		tv.setText(Util.IntToScaleStr(countTotals(planData), 0));
				
		tv = (TextView)findViewById(R.id.tvFact);
		tv.setText(Util.IntToScaleStr(countTotals(dailyOrder) + qty.qty / Consts.QTY_SCALE, 0));
		
		tv = (TextView)findViewById(R.id.tvTotals);
		tv.setText("Вес " + Util.IntToScaleStr(countWeight(), Consts.WEIGHT_SCALE, Util.DEC_DELIM, true) + " кг");
	}
	
	int countWeight() {
		int w = 0;
		for(Entry<String, OrderImplEx> kv : docs.entrySet()) {
			if(!filter.isFirmChoosed(kv.getKey()))
				continue;
			w += kv.getValue().weight();
		}
		
		return w;
	}

	int countRemnants() {
		int qty = 0;
		for(RemnantItem oi : remnantsDoc.getData().items)
			if(selectedPriceItems.size() == 0 || selectedPriceItems.contains(oi.id))
				qty += oi.qty;
		return qty / Consts.QTY_SCALE;
	}
	
	SumQty countQty() {
		SumQty ret = new SumQty();

		for(Entry<String,List<DataItem>> kv : itemsByFirm.entrySet()) {
			if(!filter.isFirmChoosed(kv.getKey()))
				continue;
			for(DataItem di : kv.getValue()) {
				if(selectedPriceItems.size() == 0 || selectedPriceItems.contains(di.id)) {
					int cq = di.getQty();
					long cs = (long)cq * di.cost / Consts.QTY_SCALE * di.qtyInPack / Consts.QTY_SCALE;
					ret.qty += cq;
					ret.sum += cs;
				}
			}
		}
		
		return ret;
	}
	
	private int countTotals(Map<String, Integer> data) {
		int ret = 0;
		for(Entry<String, Integer> kv : data.entrySet()) {
			if(selectedPriceItems.size() == 0 || selectedPriceItems.contains(kv.getKey()))
				ret += kv.getValue();
		}
		return (ret + Consts.QTY_SCALE / 2) / Consts.QTY_SCALE;
	}

	class DataItem extends FiltrableDataItem implements View.OnClickListener {
//		int qty = 0;
		int cost;
		int qtyInPack = Consts.QTY_SCALE;
		boolean inPack = true;
		boolean inAction = false;

		public DataItem(String name, String id, String prefix, String firm, String brand, int qip, int cost, boolean inAction) {
			this.name = name;
			this.id = id;
			this.prefix = prefix;
			this.firm = firm;
			this.brand = brand;
			this.qtyInPack = qip;
			this.cost = cost;
			this.inAction = inAction;
		}
		
//		public void setQty(int newQty, boolean inPack) { 
//			qty = newQty;
//			this.inPack = inPack;
//		}
		/**
		 * Возвращает кол-во в упаковках
		 * @return
		 */
		@Override
		public int getQty() {
			OrderImplEx doc = docs.get(firm);
			return doc == null ? 0 : (int)((long)doc.getQty(id) * Consts.QTY_SCALE / qtyInPack);
//			return (inPack) ? (int)((long)qty * qtyInPack / Consts.QTY_SCALE) : qty;
		}
				
		public void drawData(View v) {
//			int bkColor = (childs == null) ? Color.WHITE : Color.LTGRAY;
//			int totalBkColor = Color.LTGRAY;
			int bkQtyId = (childs == null) ? R.drawable.list_selector : R.drawable.lt_gray_selector;
			int color = (inAction) ? Color.RED : Color.BLACK;
			
			String text = "";
			TextView tv;

//			if(childs == null)
//				v.setBackgroundResource(bkQtyId);
//			else
//				v.setBackgroundColor(bkColor);
			
			text = "";
			if(childs == null && brand.length() > 0) {
				Brands b = brands.get(brand);
				if(b != null)
					text = b.name;
			}
			tv = (TextView)v.findViewById(R.id.tvBrand);
			tv.setText(text);
//			tv.setBackgroundColor(bkColor);
			tv.setBackgroundResource(bkQtyId);
			tv.setTextColor(color);

			text = "";
			FirmEx fe = firms.get(firm);
			if(fe != null)
				text = fe.shortName;
			tv = (TextView)v.findViewById(R.id.tvFirm);
			tv.setText(text);
//			tv.setBackgroundColor(bkColor);
			tv.setBackgroundResource(bkQtyId);
			tv.setTextColor(color);
			
			tv = (TextView)v.findViewById(R.id.tvMark);
			tv.setText(prefix);
//			tv.setBackgroundColor(bkColor);
			tv.setBackgroundResource(bkQtyId);
			tv.setTextColor(color);

			tv = (TextView)v.findViewById(R.id.tvName);
			linesController.prepareTextView(tv);
			tv.setText(name);			
//			tv.setBackgroundColor(bkColor);
			tv.setBackgroundResource(bkQtyId);
			tv.setTextColor(color);

			tv = (TextView)v.findViewById(R.id.tvCost);
			tv.setText(cost == 0 ? "" : Util.IntToScaleStr(cost, Consts.SUM_SCALE));
//			tv.setBackgroundColor(bkColor);			
			tv.setBackgroundResource(bkQtyId);
			tv.setTextColor(color);
			
			int val;
			val = count(prevRest);
			text = (val == 0) ? "" : Util.IntToScaleStr(val, Consts.QTY_SCALE);
			tv = (TextView)v.findViewById(R.id.tvPrevRestQty);
			tv.setText(text);
//			tv.setBackgroundColor(bkColor);
			tv.setBackgroundResource(bkQtyId);
			tv.setTextColor(color);
			
			val = count(prevDelivery);
			text = (val == 0) ? "" : Util.IntToScaleStr(val, Consts.QTY_SCALE);
			tv = (TextView)v.findViewById(R.id.tvPrevOrdQty);
			tv.setText(text);
//			tv.setBackgroundColor(bkColor);
			tv.setBackgroundResource(bkQtyId);
			tv.setTextColor(color);
			
			val = count(remnantsDoc);
			text = (val == 0) ? "" : Util.IntToScaleStr(val, Consts.QTY_SCALE);
			tv = (TextView)v.findViewById(R.id.tvRestQty);
			tv.setText(text);
//			tv.setBackgroundColor(bkColor); 
			tv.setBackgroundResource(bkQtyId);
			tv.setTextColor(color);

			val = count(autoOrder);
			text = (val == 0) ? "" : Util.IntToScaleStr(val, Consts.QTY_SCALE);
			tv = (TextView)v.findViewById(R.id.tvAutoOrdQty);
			tv.setText(text);
//			tv.setBackgroundColor(bkColor);
			tv.setBackgroundResource(bkQtyId);
			tv.setTextColor(color);
			
			int qty = countQty();
			text = (qty == 0) ? "" : Util.IntToScaleStr(qty, Consts.QTY_SCALE);
			tv = (TextView)v.findViewById(R.id.tvOrdQty);
			tv.setText(text);
//			tv.setBackgroundColor(bkColor);
			tv.setBackgroundResource(bkQtyId);
			tv.setTextColor(color);
			if(childs == null) {
				tv.setTag(id);
//				tv.setOnClickListener(this);
//				tv.setBackgroundResource(bkQtyId);
			} else {
//				tv.setOnClickListener(null);
//				tv.setBackgroundColor(bkColor);
			}
			
			val = count(dailyOrder) + qty;
			text = (val == 0) ? "" : Util.IntToScaleStr(val, Consts.QTY_SCALE);
			tv = (TextView)v.findViewById(R.id.tvFactQty);
			tv.setText(text);
//			tv.setBackgroundColor(bkColor);
			tv.setBackgroundResource(bkQtyId);
			tv.setTextColor(color);

			val = count(planData);
			text = (val == 0) ? "" : Util.IntToScaleStr(val, Consts.QTY_SCALE);
			tv = (TextView)v.findViewById(R.id.tvPlanQty);
			tv.setText(text);
//			tv.setBackgroundColor(bkColor);
			tv.setBackgroundResource(bkQtyId);
			tv.setTextColor(color);
		}

		int countQty() {
			if(childs == null) return (int)((long)getQty());
			
			int sum = 0;
			for(FiltrableDataItem di : childs) {
				sum += ((DataItem)di).countQty();
			}
			return sum;
		}
		
		int count(RemnantsImpl doc) {
			if(childs == null) {
				RemnantItem ri = (RemnantItem)doc.findItem(id);
				return ri == null ? 0 : ri.qty;
			}
			
			int sum = 0;
			for(FiltrableDataItem di : childs) {
				sum += ((DataItem)di).count(doc);
			}
			return sum;
		}
		
		int count(Map<String, Integer> vals) {
			if(childs == null) {
				Integer val = vals.get(id);
				return val == null ? 0 : val;
			}
			
			int sum = 0;
			for(FiltrableDataItem di : childs) {
				sum += ((DataItem)di).count(vals);
			}
			return sum;
		}
		
		@Override public void onClick(View v) {
			if(childs != null)
				return;
			PriceImpl pi = new PriceImpl();
			PriceEx p = (PriceEx) pi.getData();
			p.id = id;
			pi.read();
			pi.close();
			OrderImplEx doc = getOrCreate(firm);
			doc.editItem(pi.getRowid(), v.getContext());
		}

		@Override public FiltrableDataItem createFolderItem() { return new DataItem(name, id, prefix, firm, brand, qtyInPack, cost, false); }		
	}
	
	class Adapter extends BaseAdapter {
		List<FiltrableDataItem> items;
		
		public Adapter(List<FiltrableDataItem> items) { this.items = items; }
		
		public void setItems(List<FiltrableDataItem> items) {
			this.items = items;
			notifyDataSetChanged();
		}
		
		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View v, ViewGroup arg2) {
			if(v == null) {
				v = View.inflate(OrderDocEdit.this, R.layout.order_item, null);
			}
			
			DataItem di = (DataItem) getItem(pos);
			di.drawData(v);
			return v;
		}
	}

	@Override
	public void dataFiltred(List<FiltrableDataItem> newList) {
		selectedPriceItems.clear();
		for(FiltrableDataItem di : newList) {
			List<FiltrableDataItem> childs = di.getChilds();
			if(childs == null)
				continue;			
			for(FiltrableDataItem chi : childs)
				selectedPriceItems.add(chi.id);
		}
		adapter.setItems(newList);
		updateTotals();	
	}

	@Override
	public void askShowDialog(int id) {
		Dialog ret = filter.createDialog(id);
		if(ret != null)
			ret.show();
	}

	@Override
	public void firmsLoaded(final HashSet<String> disabledFirms) {
		runOnUiThread(new Runnable() {
			@Override public void run() { 
				closeWaitDialog();
				if( disabledFirms.size() > 0 )
					Toast.makeText(OrderDocEdit.this, "Включена блокировка передачи, заявки могут не отправиться", Toast.LENGTH_SHORT).show();
}
		});
		
		List<Long> ids = new ArrayList<Long>();
		for(Entry<String, OrderImplEx> kv : docs.entrySet()) {
			if(disabledFirms.contains(kv.getKey()) || kv.getValue().isEmpty())
				continue;
			ids.add(kv.getValue().getRowid());
		}
		
		DocList dl = new DocList(OrderImplEx.class, ids);
		final List<DocExportListener> snd = new ArrayList<DocExportListener>();
		snd.add(new DocSendListner(OrderDoc.instance().getObjectName(), dl));
		
		new DocumentSender(OrderDocEdit.this, findViewById(R.id.btnSend), snd).execute((Void[])null);
	}

	@Override
	public void error(final String message) {
		runOnUiThread(new Runnable() {
			@Override public void run() { 
				closeWaitDialog();
				String err = "Ошибка проверки\n" + message;
				Toast.makeText(OrderDocEdit.this, err, Toast.LENGTH_SHORT).show();
			}
		});
	}
}

class SumQty {
	public long qty = 0;
	public long sum = 0;
}

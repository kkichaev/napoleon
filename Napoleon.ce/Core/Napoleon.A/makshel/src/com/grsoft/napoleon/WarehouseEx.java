package com.grsoft.napoleon;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.ActionFile;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Gift;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceFilterMask;
import com.grsoft.dataobjects.impl.ActionImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.DiscountInputDlg.Type;
import com.grsoft.napoleon.InputNumberDlg.Decorator;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.view.KeypadHelper;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WarehouseEx extends WarehouseNew {
	public static final String ATTACH_RCVD_ACTION = "com.grsoft.napoleon.WarehouseEx.ATTACH_RCVD_ACTION";
	public static final String ATTACH = "ATTACH"; 
	private TextView tvActionDescr;
	private View btnAttach;
	private boolean orderMode = false;
	public static final String CTRL_ID = "ctrl_id";
	private final static String ATTACH_FOLDER = "attachment";
	private boolean discInited = false;
	
	GiftHelper giftHelper;
	int valueForGift;
	
	PriceImpl priceEdit = new PriceImpl();
	
	private int[] controls = {R.id.tvMfr, R.id.tvAct, R.id.tvNew, R.id.tvDraft, R.id.tvHit};
	private int curMode;
	private int priceFilterMask = 0; 
	List<PriceFilterMask> priceFilterItems;
	static String PRICE_FILTER_MODE = "PRICE_FILTER_MODE";
	
	public Map<String, String> giftitem = new HashMap<String, String>(); // id - товара : id - подарка
	
	@Override protected int getLayoutId() { return R.layout.warehouseex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences p = getPreferences(MODE_PRIVATE);
		priceFilterMask = p.getInt(PRICE_FILTER_MODE, 0);
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void adapterInit() {
		if(DocType.getCurDoc() == OrderDoc.instance())
			initForOrder();

		super.adapterInit();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(attachrcvd, new IntentFilter(ATTACH_RCVD_ACTION));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(isFinishing())
			unregisterReceiver(attachrcvd);
	}
	
	@Override
	public void afterBuildSet() {
		super.afterBuildSet();
		
		if(DocType.getCurDoc() == OrderDoc.instance() &&  !discInited){
			DiscountHelper.init(document.getId());
			discInited = true;
		}
	}
	
	@Override
	protected void postInitUI() {
		super.postInitUI();
		tvActionDescr = (TextView) findViewById(R.id.tvActionDescr);
		btnAttach = findViewById(R.id.btnAttach);
		btnAttach.setOnClickListener(attachClick());
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (!editMode && document != null && document.getRowid() != ExtrasConst.INVALID_ID)
					document.open(WarehouseEx.this);
				finish();
			}
		});
		
		findViewById(R.id.btnExpand).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { expandingPrice(); }
		});
		
		refreshExpandButton();
		if(ivFilter == null)
			ivFilter = (ImageView) findViewById(R.id.ivFilter);
		ivFilter.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(R.id.price_filter_dlg); }
		});
	}
	
//	class UpdateQty implements View.OnClickListener {
//
//		String id;
//		public UpdateQty(String id) {
//			this.id = id;
//		}
//		
//		@Override
//		public void onClick(View v) {
//			Price prc = priceEdit.getData();
//			prc.id = id;
//			priceEdit.read();
//
//			setOrderQty();
//		}
//	}
	
	void setOrderQty() {
		final Price prc = priceEdit.getData();
		final OrderImpl oi = (OrderImpl)document;
		final int qty = oi.getItemQty(prc);
		OrderItem oitem = (OrderItem) oi.findItem(prc.id);
		final boolean isInPack = oitem != null ? oitem.inPack() : false;

		OrderItemEx oie = (OrderItemEx) oi.findItem(prc.id);
		if(oie != null && oie.IsActionItem())
			return;
		
		giftHelper = new GiftHelper((OrderImplEx) document);
		if( !giftHelper.loadGift(prc.id, null))
			giftHelper = null;
		
		InputNumberDlg.open(WarehouseEx.this, new InputNumber() {
			
			@Override public int getValue() { return isInPack ? (int)((long)qty *Consts.QTY_SCALE / prc.qtyInPack) : qty; }			
			@Override public boolean isInpack() { return isInPack; }
			
			@Override
			public void applayInput(int value, Object... params) {
				boolean inPack = (Boolean)params[0];
				CostStrategy cs = CostStrategy.getInstance(oi.getClass());
				int cost = cs.getItemCost(prc, oi);

				if( inPack )
					value = (int)((long)value * prc.qtyInPack / Consts.QTY_SCALE);
				oi.updateQty(priceEdit, value, cost, inPack);				
				notifyDataSetChanged();
				
				if( giftHelper != null) {
					if(giftHelper.needShowGiftDialog(value)) {
						valueForGift = value;
						showDialog(giftHelper.giftDialogId());
					} else
						giftHelper.updateGift(value);
				}
			}
		}, Consts.QTY_SCALE, true, "Заказ", true, new Decorator() {
			@Override public int getContentView() { return R.layout.inputnumberdlgex;	}
			
			@Override
			public void adjustView(AlertDialog dialog, View view, KeypadHelper nh) {
				TextView tv = (TextView) view.findViewById(R.id.tvQtyInPack);
				tv.setText(getString(R.string.inpack_count, Util.IntToScaleStr(prc.qtyInPack, Consts.QTY_SCALE)));
			}
		});
	}		

	@Override
	public void editItem(long rowid) {
		if(DocType.getCurDoc() == OrderDoc.instance()){
			priceEdit.read(rowid);
			setOrderQty();
		}else
			super.editItem(rowid);
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		if( document instanceof OrderImplEx) {
			price.read(node.getRowid(), false);
			Price p = price.getData();
			
			OrderItemEx oie = (OrderItemEx) ((OrderImplEx)document).findItem(p.id);
			boolean hasAction = (oie != null && oie.IsActionItem());

			View view;
			int id = R.layout.wh_order_row;
			if (convertView != null && convertView.getTag(id) != null)
				view = convertView;
			else {
				view = View.inflate(this, id, null);
				view.setTag(id, true);
			}
			
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvPriceItemName);
			tv.setText(getItemName(p));
			setColor(tv, p);
			tv.setTag(node);

			Itemsable idoc = (Itemsable)document;
			tv = (TextView)view.findViewById(R.id.tvCost);
			int cost = (hasAction) ? oie.cost : getCost(p);
			tv.setText(Util.IntToScaleStr(cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			tv = (TextView)view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(getWhQty(idoc, p), Consts.QTY_SCALE, Util.DEC_DELIM, true));
			tv = (TextView)view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(idoc.getItemSum(p), Consts.SUM_SCALE, Util.DEC_DELIM, false));
			tv = (TextView)view.findViewById(R.id.tvOrder);
			tv.setText(Util.IntToScaleStr(idoc.getItemQty(p), Consts.QTY_SCALE, Util.DEC_DELIM, true));
//			tv.setOnClickListener(new UpdateQty(p.id));
			
			tv = (TextView) view.findViewById(R.id.tvDiscount);
			tv.setOnClickListener(hasAction ? null : updateDiscountClick);
			int dsc = hasAction ? oie.disc : ((OrderImplEx)document).getDisc(p);
			tv.setText(Util.IntToScaleStr(dsc, Consts.SUM_SCALE, Util.DEC_DELIM, true));
			tv.setTag(node.getRowid());
//			tv.setEnabled(idoc.findItem(p.id) != null);
			
			updateChildPriceView(view, p);
			return view;
		}
		return super.getPriceView(node, convertView);
	}
	
	OnClickListener updateDiscountClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final PriceImpl p = new PriceImpl();
			p.read((Long)v.getTag());
			p.close();
			final PriceEx price = (PriceEx) p.getData();
			
			DiscountInputDlg.open(v.getContext(), new InputNumber() {
				@Override
				public int getValue() {
					return ((OrderImplEx)document).getDisc(price);
				}

				@Override
				public void applayInput(int value, Object... params) {
					value = Math.abs(value);
					
					int maxdiscount = DiscountHelper.getMaxDiscount(document.getId(), price);
					
					if(value <= maxdiscount){
						((OrderImplEx)document).setDisc(price, value, maxdiscount);
						notifyDataSetChanged();
					}else
						Toast.makeText(WarehouseEx.this, getString(R.string.min_price_exceed, 
								Util.IntToScaleStr(maxdiscount, Consts.SUM_SCALE, Util.DEC_DELIM, true)), Toast.LENGTH_SHORT).show();
				}
			}, Consts.SUM_SCALE, false, getString(R.string.cost_changing), Type.OnlyDiscount, new DiscountInputDlg.Helper(){
				public int getLayoutId() { return R.layout.discount_inputex;}
				public void adjustView(View view) {
					final EditText ed = (EditText) view.findViewById(R.id.edCount);
					final TextView tvCost = (TextView)view.findViewById(R.id.tvCost);
					@SuppressWarnings("unchecked")
					final int baseCost = ((CostStrategyEx)CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass())).getBaseItemCost(p.getData(), document);
					final int maxdiscount = DiscountHelper.getMaxDiscount(document.getId(), price);
					
					ed.addTextChangedListener(new TextWatcher() {
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
							try{
								int newDisc = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);
								int newCost = DiscountHelper.calcDisc(baseCost, newDisc);
								tvCost.setText(getString(R.string.cost_val, Util.IntToScaleStr(newCost, Consts.SUM_SCALE, Util.DEC_DELIM, false)));
								
								if (newDisc > maxdiscount){
									Toast.makeText(WarehouseEx.this, getString(R.string.min_price_exceed, 
											Util.IntToScaleStr(maxdiscount, Consts.SUM_SCALE, Util.DEC_DELIM, true)), Toast.LENGTH_SHORT).show();
								}
							}catch(Exception e){
								e.printStackTrace();
							}
						}
						
						@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
						@Override public void afterTextChanged(Editable s) {}
					});
					
					
					tvCost.setText(getString(R.string.cost_val, Util.IntToScaleStr(getCost(p.getData()), Consts.SUM_SCALE, Util.DEC_DELIM, false)));
				};
			});
		}
	};
	
	@Override
	protected void readDocument() {
		super.readDocument();

		if(document != null && document.getId().length() > 0) {
			OrgImpl oi = new OrgImpl();
			Org o = oi.getData();
			o.id = document.getId();
			oi.read();
			oi.close();
			
			TextView tv = (TextView)findViewById(R.id.tvOrg);
			tv.setText(o.name);
			findViewById(R.id.llOrgName).setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void expandingPrice() {
		super.expandingPrice();
		refreshExpandButton();
	}
	
	protected void refreshExpandButton() {
		((ImageButton)findViewById(R.id.btnExpand)).setImageResource(adapter != null && adapter.isExpanded() ? 
				R.drawable.view_list : R.drawable.view_tree);
	}

	@Override
	protected int getItemLayoutId() { return R.layout.priceitemrowex; }
	
	private Map<String,String> attach = new HashMap<String,String>(); // имя файла - имя файла на сервере с путем
	
	private OnClickListener attachClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FolderTreeNode n = adapter.getFolderTop();
				
				if(n instanceof ActionNode){
					ActionNode an = (ActionNode) n; 
					ActionImpl a = new ActionImpl();
					
					if (a.read("id", an.actionid)){
						attach.clear();
						for(ActionFile af : a.getData().files)
							try{
								attach.put(af.file.substring(af.file.lastIndexOf("\\") + 1), af.file);
							}catch(Exception e){
								e.printStackTrace();
							}
						
						if(attach.size() == 1)
							openAttach(attach.get(0));
						else if (attach.size() > 1)
							showDialog(R.id.sel_attach_dlg);
					}
				}
			}
		};
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		
		if( id == R.id.price_filter_dlg)
			return createPriceFilterDialog();

		
		Dialog d = (giftHelper == null) ? null : giftHelper.createSelectGiftDialog(this, id, 
				new Runnable() { @Override public void run() { giftHelper.updateGift(valueForGift); }});
		
		if( d != null )
			return d;
		
		if(id == R.id.sel_attach_dlg)
			return createAttachDlg();
		else
			return super.onCreateDialog(id);
	}
	
	Dialog createPriceFilterDialog() {
		if(priceFilterItems == null) {
			priceFilterItems = new ArrayList<PriceFilterMask>();
			DataTraveler.travel(PriceFilterMask.class, new DataTraveler.Travel<PriceFilterMask>(true) {

				@Override
				public boolean travel(DataTraveler<PriceFilterMask> item) {
					priceFilterItems.add(item.data);
					return true;
				}
			}, "", "name");
		}
		
		CharSequence items[] = new CharSequence[priceFilterItems.size()];
		int idx = 0;
		for(PriceFilterMask pfm : priceFilterItems)
			items[idx++] = pfm.name;
		
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setMultiChoiceItems(items, null, null);
		b.setNegativeButton(android.R.string.cancel, null);
		b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				priceFilterMask = 0;
				
				ListView lv = ((AlertDialog)dialog).getListView();
				SparseBooleanArray data = lv.getCheckedItemPositions(); 
				for(int i=0; i<priceFilterItems.size(); i++) {
					if( data.get(i) )
						priceFilterMask |= priceFilterItems.get(i).id;
				}
				
				SharedPreferences.Editor ed = getPreferences(MODE_PRIVATE).edit();
				ed.putInt(PRICE_FILTER_MODE, priceFilterMask);
				ed.commit();
				
				FoldersAdapter.resetCache();
				adapter.putFilter(new PriceMaskFilter(priceFilterMask));
				WarehouseAdapter wa = getModeAdapter();
				applayAdapter(wa);
			}
		});
		return b.create();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == R.id.sel_attach_dlg)
			prepareAttachDlg(dialog);
		else if(id == R.id.price_filter_dlg)
			preparePriceFilterDlg((AlertDialog)dialog);
		else
			super.onPrepareDialog(id, dialog);
	}
	
	private void preparePriceFilterDlg(AlertDialog dialog) {
		ListView lv = dialog.getListView();
		for(int i=0; i<priceFilterItems.size(); i++ ) {
			PriceFilterMask pfm = priceFilterItems.get(i);
			boolean isChecked = (priceFilterMask & pfm.id) == pfm.id;
			lv.setItemChecked(i, isChecked);
		}
	}

	private void prepareAttachDlg(Dialog dialog) {
		ListView lv = (ListView) dialog.findViewById(R.id.list);
		
		if(lv != null){
			List<String> d = new ArrayList<String>();
			d.addAll(attach.keySet());
			ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, d);
			lv.setAdapter(aa);
		}
	}

	private Dialog createAttachDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		builder.setTitle(R.string.sel_attach);
		ListView lv = new ListView(this);
		lv.setId(R.id.list);
		lv.setBackgroundColor(getResources().getColor(R.color.white));
		
		builder.setView(lv);
		lv.setOnItemClickListener(fileItemSelect());
		return builder.create();
	}

	private OnItemClickListener fileItemSelect() {
		return new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String s = (String) parent.getItemAtPosition(position);
				openAttach(s);
				dismissDialog(R.id.sel_attach_dlg);
			}};
	}

	public static String getAttachPath(){
		return Environment.getExternalStorageDirectory() + "/" + Path.SHARED_FOLDER + "/" + ATTACH_FOLDER + "/";
	}
	
	protected void openAttach(String name) {
		if(attach.containsKey(name)){
			File f = new File(getAttachPath().trim(),name.trim());
			if (!f.exists())
				load(attach.get(name));
			else
				openfile(f.getAbsolutePath());
		}
	}

	private void openfile(String name) {
		try{
			Intent myIntent = new Intent(Intent.ACTION_VIEW);
			String mime = URLConnection.guessContentTypeFromName("file://" + name);
			
			if(mime != null){
				myIntent.setDataAndType(Uri.fromFile(new File(name)), mime);
				startActivity(myIntent);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	private void load(String name) {
		new RcvAttach(this, name).execute((Void[])null);
	}

	private void initForOrder(){
		orderMode = true;
		
		findViewById(R.id.llOrder).setVisibility(View.VISIBLE);
		
		for(int c : controls){
			View v = findViewById(c);
			v.setOnClickListener(controlClick);
		}
		
		curMode = getIntent().getIntExtra(CTRL_ID, -1);
		
		if (curMode == -1)
			curMode = R.id.tvMfr;
		else{
			for(int c : controls){
				View v = findViewById(c);
				
				if(v != null)
					v.setVisibility(View.GONE);
			}
			
			View v = findViewById(curMode);
			
			if(v != null)
				v.setVisibility(View.VISIBLE);
		}
		
		selectCtrl(curMode);
		
		OrderEx o = (OrderEx) document.getData();
		
		if(o.mfr.length() == 0){
			TextView tv = (TextView)findViewById(R.id.tvMfr);
			if(tv != null)
				tv.setText(R.string.full_price);
		}
		
		giftitem.clear();
		
		StringBuilder where = new StringBuilder();
		long now = new Date().getTime();
		
		where.append("start <= ").append(now).append(" and finish >= ").append(now).append(" and (id='' or id='").append(document.getId()).append("')");
		
		DataTraveler.travel(Gift.class, new DataTraveler.Travel<Gift>(){

			@Override
			public boolean travel(DataTraveler<Gift> item) {
				if(!giftitem.containsKey(item.data.id_i))
					giftitem.put(item.data.id_i, item.data.giftid);
				return true;
			}}

			, where.toString());
	}
	
	private OnClickListener controlClick = new OnClickListener(){
		@Override public void onClick(View v) {selectCtrl(v.getId());	}
	};
	
	protected void selectCtrl(int id) {
		for(int c : controls){
			TextView tv = (TextView)findViewById(c);
			
			if(tv != null)
				tv.setTypeface(null, Typeface.NORMAL);
		}
		
		TextView tv = (TextView)findViewById(id);
		
		if(tv != null)
			tv.setTypeface(null, Typeface.BOLD);
		
		curMode = id;
		FoldersAdapter.resetCache();
		
		if (adapter != null && !adapter.isTop())
			adapter.setFolder(-1);
		
		
		if(id == R.id.tvAct)
			applyAdapter(getModeAdapter(), false);
		else
			applayAdapter(getModeAdapter());
		
			
	}
	
	@Override
	protected void postAdapterInit() {
		if(orderMode) {
			WarehouseAdapter wa = getModeAdapter();
			if(curMode == R.id.tvMfr) {
				adapter.putFilter(new PriceMaskFilter(priceFilterMask));
				wa.putFilter(new PriceMaskFilter(priceFilterMask));
			}
			applayAdapter(wa);
		}
		else
			super.postAdapterInit();
	}
	
	protected void updateChildPriceView(View view, Price p) {
		View v = view.findViewById(R.id.ivGift);
		v.setVisibility(giftitem.containsKey(p.id) ? View.VISIBLE : View.INVISIBLE);
	};
	
	WarehouseAdapter getModeAdapter(){
		WarehouseAdapter result = (WarehouseAdapter) createListAdapter();
		OrderEx o = (OrderEx) document.getData();
		
		if(curMode == R.id.tvMfr && o.mfr.length() > 0 )
			result = new MfrAdapter(this, o.mfr);
		else if(curMode == R.id.tvNew)
			result = new NewstAdapter(this);
		else if (curMode == R.id.tvHit)
			result = new HitAdaper(this);
		else if (curMode == R.id.tvDraft)
			result = new AssortmentMatrixAdapter(this, o.id);
		else if (curMode == R.id.tvAct)
			result = new ActionAdapter(this);
		
		return result;
	}
	
	@Override
	protected void postAdapterChange() {
		super.postAdapterChange();
		
		tvActionDescr.setVisibility(View.GONE);
		btnAttach.setVisibility(View.GONE);
		
		if(orderMode && curMode == R.id.tvAct){
			FolderTreeNode n = adapter.getFolderTop();
			
			if(n instanceof ActionNode){
				tvActionDescr.setVisibility(View.VISIBLE);
				btnAttach.setVisibility(View.VISIBLE);
				
				ActionNode an = (ActionNode)n;
				tvActionDescr.setText(an.descr);
			}
		}
		
		ivFilter.setVisibility(curMode == R.id.tvMfr ? View.VISIBLE : View.GONE);
	}
	
	BroadcastReceiver attachrcvd = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String file = intent.getStringExtra(ATTACH);
			
			if(file != null){
				File f = new File(getAttachPath().trim(),file.trim());
				openfile(f.getAbsolutePath());
			}
		}
	};
}

class PriceMaskFilter extends Filter {
	static String NAME = "PriceMaskFilter"; 
		
	public PriceMaskFilter(int mask) {
		super(NAME);
		
		if(mask != 0) {
			String strMask = Integer.toString(mask);
			where = "(([filterMask] & " + strMask + ")=" + strMask + ")";
		}
	}
}


package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.impl.MatrixImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.WSOrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.modules.print.DataSource;
import com.grsoft.napoleon.modules.print.DataSourceGroup;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.util.BTPrinterHelper;
import com.grsoft.napoleon.modules.print.util.BTPrinterSettings;
import com.grsoft.napoleon.printsources.SilentReflector;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FPOperation;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixBaseAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class WarehouseEx extends WarehouseNew {
	private static final int WAIT_FOR_PRINT_DLG = R.id.wait_for_print_dlg;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	protected String fileName = "";
	private static final int REQUEST_ENABLE_BT = 1;

	final String MATRIX_NAME = "<Матрица контрагента>";

	List<MatrixItem> orgMatrix = null;
	private boolean matrixInited = false;
	private ImageButton btnPrint;
	protected static SelectPrinFormDlg selectPrintFormDlg;
	
	@Override
	protected int getItemLayoutId() { return R.layout.priceitemrowex; }
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
		    String action = intent.getAction();

	        if (NPrinter.SEND_TXT_FILE_ACTION.equals(action)){
				if (bluetoothAdapter == null) 
				   Toast.makeText(context, "Bluetooth недоступен", Toast.LENGTH_LONG).show();
				else{
					fileName  = intent.getStringExtra("file");
					if (!bluetoothAdapter.isEnabled()) {
					    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
					} else {
						printing();						
					}
				}
	        }
		}
	};
	
	protected void printing() {
		BTPrinterSettings cfg = BTPrinterHelper.getSettings(this);
		if( cfg.address.length() > 0 )
			BTPrinterHelper.printing(cfg.address, cfg.copies, fileName, this);
		else {
			Toast.makeText(this, "Настройте, пожалуйста, принтер", Toast.LENGTH_SHORT).show();
			Setting.open(this, TextPrinterSetting.class);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NPrinter.SEND_TXT_FILE_ACTION);
		registerReceiver(receiver, intentFilter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View ret = super.getPriceView(node, convertView); 
		TextView tv = (TextView) ret.findViewById(R.id.tvVanQty);
		if( document instanceof WSOrderImpl ) {
			tv.setVisibility(View.VISIBLE);
			PricePrint p = (PricePrint) price.getData();
			String text = Util.IntToScaleStr(p.vanQty, Consts.QTY_SCALE);
			tv.setText(text);
		} else
			tv.setVisibility(View.GONE);
		return ret;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		selectPrintFormDlg = new SelectPrinFormDlg(this, WAIT_FOR_PRINT_DLG){
			@Override
			protected void onItemSelect(String[] captions, int which) {
				PriceSource source = (PriceSource) dataSource;
				
				if(source != null)
					source.fill(which == 1);
				
				super.onItemSelect(captions, which);
			}
		};
		
		btnPrint = (ImageButton) findViewById(R.id.btnPrint);
		
		if(docRowId == ExtrasConst.INVALID_ROWID)
			btnPrint.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Dialog d = selectPrintFormDlg.createDialog(new String[]{getString(R.string.print_price_onboard), getString(R.string.print_price)});
						selectPrintFormDlg.setDataSource(new PriceSource( document, (FoldersAdapter) lvItemSelect.getAdapter()));
						d.show();
					} catch (Exception e) {
						e.printStackTrace();
					}				
				}
			});
		else
			btnPrint.setVisibility(View.GONE);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case WAIT_FOR_PRINT_DLG:
			return SelectPrinFormDlg.createWaitDlg(this);
		default:
			return super.onCreateDialog(id);
		}
	}
	
	@Override
	protected int getLayoutId() { return R.layout.warehouseex; }

	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter.resetCache();
		FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
		DocType cd = DocType.getCurDoc(); 
		if( cd != ReturnDoc.instance() )
			ret.putFilter(new ZeroPositionFilter());
		return ret;
	}

	@Override
	protected void adapterInit() {
		if (document != null && !matrixInited) {
			OrgImpl oi = new OrgImpl();
			Org org = oi.getData();
			org.id = document.getId();
			oi.read();
			oi.close();

			OrgEx oe = (OrgEx) org;

			if (oe.matrixName.size() > 0) {
				MatrixImpl mi = new MatrixImpl();
				Matrix m = mi.getData();
				m.name = oe.matrixName.get(0).name;
				if( mi.read())
					orgMatrix = m.items;
				mi.close();
			}
			
			matrixInited = true;

			if (orgMatrix != null){
				applayAdapter(new OrgMatrixAdapter(this, orgMatrix));
				matrixName = MATRIX_NAME;
			}else{
				if(folderID != -1)
					adapter.buildSet(folderID);
				else
					adapter.buildSet();
			}
		}
	}
	
	@Override
	protected void applayMatrix(String matrixName) {
		if( matrixName.equals(MATRIX_NAME)) {
			applayAdapter(new OrgMatrixAdapter(this, orgMatrix));
		} else
			super.applayMatrix(matrixName);
		
		this.matrixName = matrixName;
	}

	@Override
	protected void updateTotalSum() {
		if (document instanceof OrderImplBase<?>)
			updateTotalSum(document.sum(),
					((OrderImplBase<?>) document).weight(),
					((OrderImplBase<?>) document).count());
		else
			super.updateTotalSum();
	}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		int pos = 1;
		if( orgMatrix != null && orgMatrix.size() > 0) {
			items.add(pos++, MATRIX_NAME);
		}
		
		return items;
	}
	
	protected void loadDailySales() {
		if (!(document instanceof OrderImplBase))
			return;
		
		currentOrders.clear();
		
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		
		HashMap<String, Integer> weightCach = new HashMap<String, Integer>();
			
		OrderImplBase<?> oi = (OrderImplBase<?>)document;
		for(OrderItem item : oi.getData().items) {
			long sum = (int)((long)item.cost * item.qty / Consts.QTY_SCALE);
			Integer baseWeight = weightCach.get(item.id);
			if( baseWeight == null ) {
				p.id = item.id;
				pi.read();
				baseWeight = p.weight;
				weightCach.put(p.id, baseWeight);
			}
			//long weight = item.qty * baseWeight; /// (Consts.QTY_SCALE * Consts.WEIGHT_SCALE);
			long weight = FPOperation.itemMul(item.qty, baseWeight, Consts.QTY_SCALE);
			Integer folder = foldersCache.get(item.id);
			if( folder == null ) {
				if( p.id.equals(item.id) == false ){
					p.id = item.id;
					pi.read();
				}
				folder = p.folderID;
				foldersCache.put(p.id, folder);
			}
			
			WarehouseCurrentOrderData val = currentOrders.get(folder);
			if( val == null ) {
				val = new WarehouseCurrentOrderData();
				currentOrders.put(folder, val);
			}
			val.sum += sum;
			val.weight += weight;
		}
		
		if( folderTree.size() == 0 )
			folderTree.load();
	}
}

class OrgMatrixAdapter extends MatrixBaseAdapter {
	List<MatrixItem> matrix;

	public OrgMatrixAdapter(WarehouseNew warehouse, List<MatrixItem> matrix) {
		super(warehouse);
		this.matrix = matrix;
	}

	public String getName() {
		return "OrgMatrixAdapter";
	}

	@Override
	protected List<MatrixItem> getMatrixItems() {
		return matrix;
	}
}

/***
 * Товар
 * @author kkichaev
 *
 */
class PriceSourceItemItem {
	public String name;
	public String cost;
	public String unit;
	public String qty;
	
	@Override
	public synchronized String toString() {
		return "item: " + name;
	}
}

/***
 * Папки
 * @author kkichaev
 */

class PriceSourceItem extends DataSourceGroup{
	
	List<PriceSourceItemItem> items = new ArrayList<PriceSourceItemItem>();
	
	public int index = -1;
	public String name;
	
	public int size() { return items.size(); }
	
	public void sort() {
		Collections.sort(items, new Comparator<PriceSourceItemItem>() {
			@Override
			public int compare(PriceSourceItemItem lhs, PriceSourceItemItem rhs) {
				return lhs.name.compareTo(rhs.name);
			}});
	}
	
	public boolean moveNext() {
		index++;
		
		if (index >= items.size())
			return false;
		else{
			return true;
		}
	}
	
	public void add(PriceSourceItemItem item) { items.add(item); }
	
	@Override public void init(Context context, int resId) { index = -1;	}

	@Override
	public boolean getValue(StringBuilder value, String name, String format) {
		return SilentReflector.getFieldValue(value, name, items.get(index), format);
	}

	@Override
	public DataSource getObject(String name) {	return null; }

	@Override
	public boolean haveMoreData() {	return index < items.size();	}

	@Override
	public synchronized String toString() { return "group: " + name; }

	@Override
	public boolean isGroup() { return index < 0; }
}

class PriceSourceItems extends DataSourceGroup{
	
	List<PriceSourceItem> items = new ArrayList<PriceSourceItem>();
	
	public int index = 0;
	
	public void add(PriceSourceItem item) { items.add(item); }
	
	public void clear() { items.clear(); }
	
	@Override
	public void startPage() {}

	@Override
	public boolean getValue(StringBuilder value, String name, String format) {
		if(isGroup())
			return SilentReflector.getFieldValue(value, name, items.get(index), format);
		else
			return items.get(index).getValue(value, name, format);
	}

	@Override
	public DataSource getObject(String name) { return null;	}

	@Override
	public boolean haveMoreData() { return index < items.size(); }

	@Override
	public void calculate() {}

	@Override
	public boolean moveNext() {
		PriceSourceItem current = items.get(index);
		
		if (!current.moveNext()){
			index++;
			return index < items.size();
		}
		
		return true;
	}

	@Override
	public boolean isGroup() {
		if(index < items.size()){
			PriceSourceItem psi = items.get(index);
			return psi.isGroup();
		}
		 
		return false;
	}
}

class PriceSource extends DataSource{

	PriceSourceItems items = new PriceSourceItems();
	PriceImpl price = new PriceImpl();
	Document<?> document;
	CostStrategy costStrategy;
	public String date;
 	
	@SuppressWarnings({ "static-access", "unchecked" })
	public PriceSource(Document<?> document, FoldersAdapter folder) {
		this.document = document;
		this.costStrategy = costStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
		this.date = Util.simpleDateFormat.format(Util.getDate());
	}
	
	@Override
	public void init(Context context, int resId) {
		items.init(context, resId);
	}
	
	public void fill(final boolean zero) {
		items.clear();
		DataTraveler.travel(Folder.class, new DataTraveler.Travel<Folder>(){

			@Override
			public boolean travel(DataTraveler<Folder> item) {
				final PriceSourceItem psi = new PriceSourceItem();
				psi.name = item.data.name.toUpperCase();
				
				StringBuilder where = new StringBuilder();
				where.append("folderID=").append(item.data.id);
				
				if(zero)
					where.append(" and qty > 0");
				else 
					where.append(" and vanqty > 0");
				
				DataTraveler.travel(PricePrint.class, new DataTraveler.Travel<PricePrint>() {
					@Override
					public boolean travel(DataTraveler<PricePrint> item) {
						PriceSourceItemItem psii = new PriceSourceItemItem();
						psii.name = item.data.name;
						psii.cost = Util.IntToScaleStr(
								costStrategy.getItemCost(item.data, (Document<?>) document), Consts.SUM_SCALE, Util.DEC_DELIM, false);
						psii.qty = Util.IntToScaleStr(((PricePrint)item.data).vanQty, Consts.QTY_SCALE);
						psii.unit = ((PricePrint)item.data).unit;
						psi.add(psii);
						return true;
					}}, where.toString());
				
				if(psi.size() > 0) {
					items.add(psi);
					psi.sort();
				}
				
				return true;
			}} , null);
		
	}

	@Override
	public void startPage() {}

	@Override
	public boolean getValue(StringBuilder value, String name, String format) {
		return SilentReflector.getFieldValue(value, name, this, format); 
	}

	@Override
	public DataSource getObject(String name) { return items; }

	@Override
	public boolean haveMoreData() {	return false; }

	@Override
	public void calculate() { }

	@Override
	public boolean moveNext() {	return false; }
	
}

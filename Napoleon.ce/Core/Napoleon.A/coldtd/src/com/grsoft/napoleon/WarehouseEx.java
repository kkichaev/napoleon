package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.OrgDogItem;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgDogovorImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.dataobjects.impl.WSOrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.utl.PricePrintHelper;
import com.grsoft.napoleon.utl.ScannerHelper;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixItemsAdapter;
import com.grsoft.util.TreeNodeCmp;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class WarehouseEx extends WarehouseNew {
	
	private static final int CHOOSE_PRICE_VARIANT = 0x43;
	private static final int WAIT_PRINT_DIALOG = 0x44;
	
	PriceImpl pi = new PriceImpl();
	AtomicBoolean isScanning=new AtomicBoolean(false);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if( document instanceof SalesImplEx ) {
			int st = document.getSumType();
			if(st >= 0) {
		        ConfigImpl config = new ConfigImpl();
	//			DialogHelper.loadSpinnerFromConfig(config, "¬ид÷ены", priceType, spPrices, o.sumType);
		        Config c = config.getData();
		        c.key = "¬ид÷ены";
		        config.read();
		        config.close();
		        List<CharSequence> values = new ArrayList<CharSequence>();
				DialogHelper.makeList(c.value, values, "");
				if( values.size() > st ) {
					TextView tv = (TextView)findViewById(R.id.tvHome);
					tv.setText(values.get(st));
				}
			}
		}
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter.resetCache();
		
		if(document instanceof WSOrderImpl)
			return super.createListAdapter();
		
		BaseAdapter ret = null;
		
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = document.getId();
		oi.read();
		oi.close();
		
		if( oe.matrix.size() > 0 )
			ret = new MatrixItemsAdapter(this, oe.matrix); 
		else
			ret = super.createListAdapter();
		if( ((FoldersAdapter)ret).getFilter(ZeroCostFilter.NAME) == null )
			((FoldersAdapter)ret).putFilter(new ZeroCostFilter());

		if( document instanceof SalesImplEx ) {
			
			String dogId = ((SalesEx)document.getData()).dogId;
			if( dogId != null && dogId.length() > 0) {
				OrgDogovorImpl di = new OrgDogovorImpl();
				OrgDogovor od = di.getData();
				od.id = dogId;
				HashSet<String> mi = new HashSet<String>();
				if( di.read() ) {
					for(OrgDogItem odi : od.items)
						if( odi.id.length() > 0 )
							mi.add(odi.id);
				}
				di.close();
				if( mi.size() > 0 )
					((FoldersAdapter)ret).putFilter(new DogFilter(mi)); 
			}
		}
		
		return ret;
	}
	
	@SuppressLint("UseSparseArrays")
	HashMap<Long, Integer> priceOrders = new HashMap<Long, Integer>();
	int getPriceOrder(long rid) {
		Integer r = priceOrders.get(rid);
		if( r == null ) {
			pi.read(rid);
			r = ((PriceEx)pi.getData()).priceOrder;
			priceOrders.put(rid, r);
		}
		return r;
	}
	
	
	@Override
	public void sortingPriceList(ArrayList<TreeNode> price) {
		Collections.sort(price, new TreeNodeCmp() {

			@Override
			public int compare(TreeNode lhs, TreeNode rhs) {
				if (!(lhs instanceof PriceTreeNode) || !(rhs instanceof PriceTreeNode))
					return super.compare(lhs, rhs);
				
				int l = getPriceOrder(lhs.getRowid());
				int r = getPriceOrder(rhs.getRowid());
				if(l == r)
					return super.compare(lhs, rhs);
				return l - r;
			}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == CHOOSE_PRICE_VARIANT ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("¬ариант печати");
			b.setSingleChoiceItems(new CharSequence[] { 
					"—ортировка по позици€м в прайс-листе", 
					"—ортировка по ассортиментным группам",
					"ќстатки на борту"
					}, -1, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) { 
					printPrice(which);
					dialog.dismiss();
				}
			});
			return b.create();
		}
		if( id == WAIT_PRINT_DIALOG ) {
			return SelectPrinFormDlg.createWaitDlg(this);
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == WAIT_PRINT_DIALOG ) {
			ProgressBar p = (ProgressBar) dialog.findViewById(android.R.id.progress);
			if( p != null ) {
		        p.setVisibility(View.GONE);
		        p.setVisibility(View.VISIBLE);
			}
	        return;
		}
		super.onPrepareDialog(id, dialog);
	}
	
	protected void printPrice(int which) {
		new AsyncTask<Integer, Void, File>(){
			protected void onPreExecute() { showDialog(WAIT_PRINT_DIALOG); };
			
			@Override
			protected File doInBackground(Integer... params) {
				File result = null;
				if (params.length > 0) {
					int which = params[0];
					result = PricePrintHelper.printPrice(WarehouseEx.this, which, document);
				}
				
				return result;
			}
			
			protected void onPostExecute(File output) {
				try {
					dismissDialog(WAIT_PRINT_DIALOG);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.execute(which);
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		if( document instanceof WSOrderImpl )
			return new WSOrderFilter();
		return super.createZeroPositionFilter();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isScanning.set(false);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case 132:
			NapoleonEx.moveTo(this);
			break;
		case 212:
		case 221:
			if( isScanning.compareAndSet(false, true) ) {
				scan();
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	void scan() {
		Thread scanThread = new Thread() {
			public void run() {
				ScannerHelper.doScan(WarehouseEx.this, document);
				isScanning.set(false);
			}
		};
		scanThread.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		pi.close();
	}
	
	@Override
	protected int getOptionsMenuId() {
		return R.menu.wh_opt_menu_ex;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if( item.getItemId() == R.id.itPrint ) {
			showDialog(CHOOSE_PRICE_VARIANT);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
		
	class ZeroCostFilter extends Filter {
		
		public static final String NAME = "CostFilter";
		
		CostStrategy cs;
		
		@SuppressWarnings("unchecked")
		public ZeroCostFilter() {
			super(NAME);
			cs = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
		}
		
		@Override
		public boolean inset(long priceRowID, String id) {
			pi.read(priceRowID);
			int cost = cs.getItemCost(pi.getData(), document);
			return cost > 0;
		}
	}
}

class DogFilter extends Filter {
	private static final String NAME = "DogovorFilter";
	HashSet<String> ids;
	
	public DogFilter(HashSet<String> ids) {
		super(NAME);
		this.ids = ids;
	}
	
	@Override
	public boolean inset(long priceRowID, String id) {
		return ids.contains(id);
	}
}

class WSOrderFilter extends ZeroPositionFilter {
	public WSOrderFilter() {
		where = "vanQty>0";
	}
}


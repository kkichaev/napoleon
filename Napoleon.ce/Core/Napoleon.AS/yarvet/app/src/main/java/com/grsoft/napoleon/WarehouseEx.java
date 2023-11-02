package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DistribMatrixImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OffTakeHistory;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarehouseEx extends Warehouse {
	private TextView tvOrgOrdSum;
	private int skladIdx = 0;
	static String idStore = "";
	PriceImpl pi = new PriceImpl();
	private BaseAdapter listAdapter;
	private View llMatrixOrder;
	private View ibNextPrice;
	private boolean core = false;
	private static Map<String, Integer> offtakeMap = new HashMap<String, Integer>();

	public static void resetCache() { idStore = ""; }

	@Override protected int getLayoutId() { return R.layout.warehouse_ex;	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		offtakeMap.clear();
		OffTakeHistory.inflator = new OffTakeHistory.OffTakeInflator();
		
		OrgImpl org = new OrgImpl();
		if(org.read("id", document.getId()))
		{
			DistribMatrixImpl dmi = new DistribMatrixImpl();
			if(dmi.read("id", ((OrgEx)org.getData()).fmtx)){
				for(OrgMatrixItem i : dmi.getData().items)
					if(!offtakeMap.containsKey(i.id))
						offtakeMap.put(i.id, i.offtake);
				
				OffTakeHistory.inflator = new OffTakeHistory.OffTakeInflator(){
					public int getOffTake(String id) {
						return offtakeMap.containsKey(id) ? offtakeMap.get(id) : getOffTake(); 
					};
				};
			}
		}
	}
	
	@Override
	protected void postInitUI() {
		super.postInitUI();
		llMatrixOrder = findViewById(R.id.llMatrixOrder);
		ibNextPrice = findViewById(R.id.ibNextPrice);
		ibNextPrice.setOnClickListener(ibNextPriceClick);
	}
	
	private OnClickListener ibNextPriceClick = new OnClickListener() {
		@Override public void onClick(View v) {
			listAdapter = null;
			core = true;
			llMatrixOrder.setVisibility(View.GONE);
			applayAdapter((WarehouseAdapter) createListAdapter());
		}
	}; 
	
	@Override
	protected BaseAdapter createListAdapter() {
		if( listAdapter == null ) {
			if (docRowId != ExtrasConst.INVALID_ID){
				if(document.getRowid() == ExtrasConst.INVALID_ID)
					document.read(docRowId);
				
				DocType dt = DocType.getCurDoc();
				DistribMatrixImpl dmi = new DistribMatrixImpl();
				OrgImpl org = new OrgImpl();
				if(!core && (dt == OrderDoc.instance() || dt == RemnantsDoc.instance()) &&
						org.read("id", document.getId()) && dmi.read("id", ((OrgEx)org.getData()).fmtx)){
					llMatrixOrder.setVisibility(View.VISIBLE);
					listAdapter = new OrgTypeMatrix(this, dmi); 
				}
			}
			
			if( listAdapter == null )
				listAdapter = super.createListAdapter();
		}
		
		return listAdapter;
	}

	@Override
	protected Filter createZeroPositionFilter() {
		if(document instanceof OrderImplEx) {
			OrderEx o = (OrderEx) document.getData();
			if(idStore == null || idStore.equals(o.whCode) == false ) {
				FoldersAdapter.resetCache();
				idStore = o.whCode;
			}
			return new WarehouseEx.ZeroFilter();
		}
		return super.createZeroPositionFilter();
	}

	class ZeroFilter extends ZeroPositionFilter {

		@Override public String getWhereStr() { return ""; }

		@Override
		public boolean inset(long priceRowID, String id) {
			boolean result = false;

			Price p = pi.getData();
			p.id = id;
			pi.read();
			result = (((OrderImplEx)document).getItemValue(p) > 0);
			return result;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pi.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if( !super.onCreateOptionsMenu(menu) )
			return false;

		if(document.getRowid() == ExtrasConst.INVALID_ROWID ){
			menu.add(Menu.NONE, R.id.select_sklad_dlg, Menu.NONE, getString(R.string.select_sklad));
		}

		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.select_sklad_dlg)
			showDialog(R.id.select_sklad_dlg);
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.select_sklad_dlg)
			return createSkadDialog();
		return super.onCreateDialog(id);
	}

	private Dialog createSkadDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Склады");

		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();

		c.key = "Склады";
		if( ci.read() ) {
			final List<KeyValue> list = new ArrayList<KeyValue>();
			DialogHelper.makeListWithKey(c.value, list, null);
			if( list.size() > 0 ) {
				CharSequence[] csa = new CharSequence[list.size()];

				for(int i = 0; i <csa.length; i++)
					csa[i] = list.get(i).value;

				b.setSingleChoiceItems(csa, skladIdx, new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) {
						selectSkald(which);
						dialog.dismiss();
					}
				});
			}
		}

		ci.close();
		return b.create();
	}

	protected void selectSkald(int which) {
		skladIdx = which;
		((OrderEx)document.getData()).whIndex = skladIdx;
		FoldersAdapter.resetCache();
		adapter.buildSet();
	}

	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		super.setTextColumnValue(textView, type, price);

		if (textView.getId() == R.id.tvClmn2 && DocType.getCurDoc() == OrderDoc.instance()) {
			long ren = ((OrderImplEx)document).getRentability(price.id);

			if (ren != 0) {
				String text = textView.getText().toString();
				text += "<br>" + Util.IntToScaleStr(ren, Consts.SUM_SCALE);
				textView.setText(Html.fromHtml(text));
			}
		}
	}
}

class OrgTypeMatrix extends MatrixAdapter{
	private DistribMatrixImpl matrix;
	public OrgTypeMatrix(Warehouse warehouse, DistribMatrixImpl matrix) {
		super(warehouse, "");
		this.matrix = matrix;
	}
	
	@Override
	protected List<MatrixItem> getMatrixItems() {
		List<MatrixItem> result = new ArrayList<MatrixItem>();
		
		for(OrgMatrixItem i : matrix.getData().items){
			MatrixItem mi = new MatrixItem();
			mi.id = i.id;
			result.add(mi);
		}
		
		return result;
	}
}


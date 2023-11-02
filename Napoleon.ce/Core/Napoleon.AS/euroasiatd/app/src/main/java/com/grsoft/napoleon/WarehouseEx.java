package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.grsoft.database.DbReader;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgActionCost;
import com.grsoft.dataobjects.OrgDistribItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgRetPrc;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DistribImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DistribDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.util.ZeroPositionFilter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class WarehouseEx extends Warehouse implements OnClickListener, OnItemLongClickListener, android.content.DialogInterface.OnClickListener {
	
	HashSet<String> actions = new HashSet<String>();
	List<OrgRetPrc> retPrc = new ArrayList<OrgRetPrc>();
	
	static String idStore = ""; 
	PriceImpl pi = new PriceImpl();
	private OrgMatrix orgMatrix;

	@Override
	protected int getLayoutId() {
		return R.layout.warehouseex;
	}
	
	public static void resetCache() { idStore = ""; }
	@Override protected int getItemLayoutId() {
		if (DocType.getCurDoc().equals(DistribDoc.instance()))
			return R.layout.distrpriceitemrowex;
		else
			return R.layout.priceitemrowex; 
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null) {
		     String bc = scanResult.getContents();
		     List<Long> ids = DbReader.readIds(DataObjectInfo.getInstance().getTableName(PriceEx.class), "barcode LIKE '%" + bc + "%'", null);
		     if( ids.size() > 0 ) {
		    	 ((Itemsable)document).editItem(ids.get(0), this);
		     }
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnScanBC).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				IntentIntegrator ii = new IntentIntegrator(WarehouseEx.this);
				ii.initiateScan();
			}
		});


		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = document.getId();
		oi.read();
		oi.close();
		
		for(OrgActionCost oac : oe.actions)
			actions.add(oac.id);
		
		retPrc = oe.retPrc;
		
		if (DocType.getCurDoc() == DistribDoc.instance())
			lvItemSelect.setOnItemLongClickListener(this);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		
		if(menuInfo instanceof AdapterView.AdapterContextMenuInfo && document instanceof ReturnImplEx) {
			AdapterView.AdapterContextMenuInfo mi = (AdapterContextMenuInfo)menuInfo;
			TreeNode tn = (TreeNode) adapter.getItem(mi.position);
			if(tn instanceof PriceTreeNode) {
				getMenuInflater().inflate(R.menu.wh_context_menu, menu);
				return;
			}
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.itAddNew) {
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();		
			PriceTreeNode ptn = (PriceTreeNode) adapter.getItem(menuInfo.position);
			ReturnPriceCount.open(this, ptn.getRowid(), (ReturnImplEx)document, true);
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		if(document instanceof OrderImpl) {
			OrderEx o = (OrderEx) document.getData();
			if(idStore == null || idStore.equals(o.whCode) == false ) {
				FoldersAdapter.resetCache();
				idStore = o.whCode;
			}
			return new ZeroFilter();
		}
		return super.createZeroPositionFilter();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pi.close();
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			boolean result = false; 
			
			Price p = pi.getData();
			p.id = id;
			pi.read();
			result = (((OrderImpl)document).getItemValue(p) > 0);
			return result;
		}
	}
	
	@Override
	protected void setName(View view, Price p, int linesCount, PriceTreeNode node) {
		super.setName(view, p, linesCount, node);

		String text = "";
		for(OrgRetPrc orp : retPrc)
			if(orp.id.equals(p.id)) {
				text =  Util.IntToScaleStr(orp.prc, 1, Util.DEC_DELIM, true) + "%";
				break;
			}
		
		TextView tv = (TextView)view.findViewById(R.id.tvRetPrc);
		if(tv != null)
			tv.setText(text);
		
		ImageView iv = (ImageView)view.findViewById(R.id.iAction);
		if( iv != null ) {
			iv.setImageResource( (actions.contains(p.id)) ? R.drawable.action : R.drawable.empty );
		}
	}
	
	@Override
	protected void postAdapterInit() {
		orgMatrix = new OrgMatrix(this, document == null ? "" : document.getId());
		
		if(DocType.getCurDoc() == DistribDoc.instance())
			applayAdapter(orgMatrix);
		else
			super.postAdapterInit();
	}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		if (orgMatrix.hasItems())
			items.add(OrgMatrix.NAME);
		
		return super.prepareMatrixList(items);
	}
	
	@Override
	protected void applayMatrix(String matrixName) {
		this.matrixName = matrixName;

		if(matrixName.equals(OrgMatrix.NAME)) {
			applayAdapter(orgMatrix);
		} else
			super.applayMatrix(matrixName);
	}
	
	@Override
	protected int getDefaultColor(Price p) {
		if (orgMatrix.contains(p.id))
			return getResources().getColor(R.color.blue);
		else
			return super.getDefaultColor(p);
	}
	
	@Override
	protected void updateChildPriceView(View view, Price p) {
		super.updateChildPriceView(view, p);
		
		if(DocType.getCurDoc().equals(DistribDoc.instance())) {
			view.findViewById(R.id.tvClmn1).setVisibility(View.GONE);
			view.findViewById(R.id.tvClmn2).setVisibility(View.GONE);
			
			 
			CheckBox cb = (CheckBox) view.findViewById(R.id.cbVal);
			cb.setChecked(((Itemsable) document).getItemQty(p) != 0);
			cb.setTag(p.id);
			cb.setOnClickListener(this);
			
			OrgDistribItem item = (OrgDistribItem) ((DistribImpl)document).findItem(p.id);
			
			cb = (CheckBox) view.findViewById(R.id.cbAction);
			cb.setChecked(item != null && item.action != 0);
			cb.setTag(p.id);
			cb.setOnClickListener(this);
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.cbVal) {
			String id = v.getTag().toString();
			CheckBox cb = (CheckBox)v;
			price.read("id", id);
			
			boolean ch = ((Itemsable) document).getItemQty(price.getData()) == 0;
			cb.setChecked(ch);
			int cost = 0;
			DistribImpl d = ((DistribImpl)document);
			OrgDistribItem i = (OrgDistribItem) d.findItem(id);
			
			if (i != null)
				cost = i.cost;
			
			d.updateQty(price, ch ? 1 * Consts.QTY_SCALE : 0, cost, false);
		}else if (v.getId() == R.id.cbAction) {
			String id = v.getTag().toString();
			price.read("id", id);
			
			OrgDistribItem i = (OrgDistribItem) ((DistribImpl)document).findItem(id);
			
			if (i == null) 
				((DistribImpl)document).updateQty(price, 0 , 0, false);
			
			i = (OrgDistribItem) ((DistribImpl)document).findItem(id);
			
			if ( i != null)
				i.action = ((CheckBox)v).isChecked() ? 1 : 0;
			
			document.write();
			document.close();
		}
			
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Object item = adapter.getItem(position);
		
		if (item instanceof PriceTreeNode)
		{
			Bundle b = new Bundle();
			b.putString(ExtrasConst.ORG_ID_STR, ((PriceTreeNode)item).getId());
			showDialog(R.id.remark_dlg, b);
		}
		
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		
		if(id == R.id.remark_dlg)
			return createRemarkDlg();
		
		return super.onCreateDialog(id, args);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		if(id == R.id.remark_dlg)
			prepareRemarkDlg(dialog, args);
		super.onPrepareDialog(id, dialog, args);
	}

	private void prepareRemarkDlg(Dialog dialog, Bundle args) {
		String id = args.getString(ExtrasConst.ORG_ID_STR);
		EditText ed = ((EditText)dialog.findViewById(R.id.edRemark));
		ed.setTag(id);
		OrgDistribItem i = (OrgDistribItem) ((DistribImpl)document).findItem(id);
		
		if (i != null)
			ed.setText(i.remark);
		else
			ed.setText("");
	}

	private Dialog createRemarkDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(View.inflate(this, R.layout.remarkdlg, null));
		builder.setTitle(R.string.remark_dlg_title);
		builder.setPositiveButton(R.string.ok, this);
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (((CreatableDocument<?>)document).isEditable()){
			EditText ed = (EditText) ((Dialog)dialog).findViewById(R.id.edRemark);
			String id = ed.getTag().toString();
			price.read("id", id);
			OrgDistribItem i = (OrgDistribItem) ((DistribImpl)document).findItem(id);
			
			if (i == null) 
				((DistribImpl)document).updateQty(price, 0 , 0, false);
			
			i = (OrgDistribItem) ((DistribImpl)document).findItem(id);
			
			if ( i != null)
				i.remark = ed.getText().toString().trim();
			
			document.write();
			document.close();
		}
	}
	
	@Override
	protected void adapterInit() {
		super.adapterInit();
		
		if (DocType.getCurDoc() == OrderDoc.instance())
			adapter.putFilter(new OrderFilter());
	}
	
	private static class OrderFilter extends Filter{
		public static final String NAME = "orderfilter";
		
		public OrderFilter() {
			super(NAME);
		}
		
		@Override
		public String getWhereStr() {
			return "outStock = 0";
		}
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		WarehouseAdapter a = (WarehouseAdapter) super.createListAdapter();
		
		if (DocType.getCurDoc() == OrderDoc.instance() || DocType.getCurDoc() == RemnantsDoc.instance())
			a.putFilter(new Filter("CS_Filter") {
				PriceImpl p = new PriceImpl();
				@Override
				public boolean inset(long priceRowID, String id) {
					p.read(priceRowID);
					
					return ((CostStrategyEx)CostStrategy.getInstance((Class<? extends Document<?>>)document.getClass())).hasPriceCost(p.getData(), document);
				}
			});
		
		return a;
	}
}

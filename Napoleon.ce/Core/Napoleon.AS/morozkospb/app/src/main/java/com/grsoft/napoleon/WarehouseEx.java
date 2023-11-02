package com.grsoft.napoleon;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.grsoft.database.DbReader;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.MerchFolder;
import com.grsoft.dataobjects.MerchItem;
import com.grsoft.dataobjects.MonitoringItemMSPB;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RetailCode;
import com.grsoft.dataobjects.RivalPrice;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.MerchImpl;
import com.grsoft.dataobjects.impl.MonitoringImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RivalPriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.MerchDoc;
import com.grsoft.napoleon.documents.MonitoringDocMSPB;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RivalMntrDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.util.ZeroPositionFilter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class WarehouseEx extends Warehouse implements OnClickListener {
	private boolean adapterInited = false;
	static String idStore = "";
	private View btnPrice;
	private TextView tvMatrixName;
	
	Map<String, String> retCodes = new HashMap<String, String>();

	public static void resetCache() {
		idStore = "";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		btnPrice = findViewById(R.id.btnPrice);
		tvMatrixName = (TextView) findViewById(R.id.tvMatrixName);
		
		DocType dt = DocType.getCurDoc();
		
		if (dt == MerchDoc.instance() || dt == MonitoringDocMSPB.instance() || DocType.getCurDoc() == RivalMntrDoc.instance()) {
			btnPrice.setVisibility(View.GONE);
			tvMatrixName.setVisibility(View.GONE);
		}else {
			btnPrice.setVisibility(View.VISIBLE);
			tvMatrixName.setVisibility(View.VISIBLE);
			btnPrice.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					FoldersAdapter.resetCache();
					matrixName = PRICE_WITHOUT_MATRIX;
					applyAdapter((WarehouseAdapter) createListAdapter(), adapter.isExpanded(), false);
				}
			});
		}

		if (dt == MerchDoc.instance())
			findViewById(R.id.merch_header).setVisibility(View.VISIBLE);
		else if (dt == MonitoringDocMSPB.instance() || DocType.getCurDoc() == RivalMntrDoc.instance())
			findViewById(R.id.monitoring_header).setVisibility(View.VISIBLE);
		
		findViewById(R.id.btnScanBC).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				IntentIntegrator ii = new IntentIntegrator(WarehouseEx.this);
				ii.initiateScan();
			}
		});

	}

	@Override
	protected Filter createZeroPositionFilter() {
		if (document instanceof OrderImplEx) {
			OrderEx o = (OrderEx) document.getData();
			if (idStore.equals(o.idStore) == false) {
				FoldersAdapter.resetCache();
				idStore = o.idStore;
			}
			return new ZeroFilter();
		}
		return super.createZeroPositionFilter();
	}

	@Override
	protected String getItemId(Price p) {
		return ((PriceEx) p).article;
	}

	class ZeroFilter extends ZeroPositionFilter {

		@Override
		public String getWhereStr() {
			return "";
		}

		@Override
		public boolean inset(long priceRowID, String id) {
			boolean result = false;

			Price p = new Price();
			p.id = id;
			result = (((OrderImplEx) document).getItemValue(p) > 0);
			return result;
		}
	}

	@Override
	protected String getItemName(Price p) {
		String ret = super.getItemName(p);
		String code = retCodes.get(p.id);
		if(code != null)
			ret = code + " " + ret;
		return ret;
	}
	
	@Override
	protected void postAdapterInit() {
		retCodes.clear();
		if (adapterInited)
			super.postAdapterInit();
		else {
			adapterInited = true;
			DocType dt = DocType.getCurDoc();
			if (document.getRowid() != ExtrasConst.INVALID_ROWID && (dt == MerchDoc.instance()
					|| dt == MonitoringDocMSPB.instance() || dt == RivalMntrDoc.instance())) {
				if(dt == RivalMntrDoc.instance())
					price = new PriceImplRival();
				OrgMatrixAdapter.NAME = getString(R.string.org_matrix_name);
				applayMatrix(OrgMatrixAdapter.NAME);
				loadRetailCodes();
			} else if (document.getRowid() != ExtrasConst.INVALID_ROWID
					&& AssortmentMatrixAdapter.hasAssortiment(document.getId()))
				applayMatrix(AssortmentMatrixAdapter.TITLE);
			else
				super.postAdapterInit();
		}
	}

	private void loadRetailCodes() {
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx) oi.getData();
		oe.id = document.getId();
		oi.read();
		oi.close();
		
		DataTraveler.travel(RetailCode.class, new DataTraveler.Travel<RetailCode>() {

			@Override
			public boolean travel(DataTraveler<RetailCode> item) {
				retCodes.put(item.data.id_i, item.data.code);
				return true;
			}
			
		}, "id='" + oe.idCompany + "'");
	}

	@Override
	protected boolean inheritedApplayMatrix(String matrixName) {
		FoldersAdapter.resetCache();
		
		boolean res = super.inheritedApplayMatrix(matrixName);

		if (matrixName.equals(OrgMatrixAdapter.NAME) && document instanceof ISuppl) {
			String suppl = ((ISuppl)document).getSuppl();
			applayAdapter(new OrgMatrixAdapter(WarehouseEx.this, document.getId(),
					(DocType.getCurDoc() != RivalMntrDoc.instance()), suppl));
			res = true;
		}

		return res;
	}

	@Override
	protected void postAdapterChange() {
		super.postAdapterChange();
		tvMatrixName.setText(matrixName);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.warehouseex;
	}

	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		items.add(OrgMatrixAdapter.NAME);
		return super.prepareMatrixList(items);
	}

	@Override
	protected int getFolderLayoutId() {
		if (DocType.getCurDoc() == MerchDoc.instance())
			return R.layout.itemselectrowex;
		else
			return super.getFolderLayoutId();
	}

	@Override
	protected int getItemLayoutId() {
		if (DocType.getCurDoc() == MerchDoc.instance())
			return R.layout.priceitemrowex;
		else if (DocType.getCurDoc() == MonitoringDocMSPB.instance() || DocType.getCurDoc() == RivalMntrDoc.instance())
			return R.layout.monitoringtemrowex;
		else if (DocType.getCurDoc() == OrderDoc.instance())
			return R.layout.orderpriceitemrow;
		else
			return super.getItemLayoutId();
	}

	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View res = super.getPriceView(node, convertView);
		
		ImageView iAction = (ImageView) res.findViewById(R.id.iAction);
		
		if (iAction != null)
			iAction.setImageDrawable(getResources().getDrawable(R.drawable.empty));
		
		if (DocType.getCurDoc() == OrderDoc.instance()) {
			if (iAction != null && CostStrategyEx.hasOrgText(document, node.getId())) {
				iAction.setImageDrawable(getResources().getDrawable(R.drawable.action));
			}
		}

		if (DocType.getCurDoc() == MerchDoc.instance()) {
			res.findViewById(R.id.llQuant).setVisibility(View.GONE);
			TextView tv = (TextView) res.findViewById(R.id.tvQty);
			tv.setVisibility(View.VISIBLE);
			TextView tvSystem = (TextView) res.findViewById(R.id.tvSystem);
			tvSystem.setVisibility(View.VISIBLE);

			String str = "";
			String s2 = "";
			MerchItem i = (MerchItem) ((MerchImpl) document).findItem(node.getId());

			if (i != null) {
				str = Util.IntToScaleStr(i.qty, Consts.QTY_SCALE);
				s2 = Util.IntToScaleStr(i.system, Consts.QTY_SCALE);
			}

			tv.setText(str);
			tvSystem.setText(s2);
		} else if (DocType.getCurDoc() == MonitoringDocMSPB.instance() || DocType.getCurDoc() == RivalMntrDoc.instance()) {
			res.findViewById(R.id.llQuant).setVisibility(View.GONE);
			TextView tvCost = (TextView) res.findViewById(R.id.tvCost);
			tvCost.setVisibility(View.VISIBLE);
			TextView tvCost1 = (TextView) res.findViewById(R.id.tvCost1);
			tvCost1.setVisibility(View.VISIBLE);
			TextView tvCost2 = (TextView) res.findViewById(R.id.tvCost2);
			tvCost2.setVisibility(View.VISIBLE);

			String c1 = "", c2 = "", c3 = "";

			MonitoringItemMSPB i = (MonitoringItemMSPB) ((MonitoringImplBase<?>) document).findItem(node.getId());

			if (i != null) {
				c1 = Util.IntToScaleStr(i.cost, Consts.SUM_SCALE);
				c2 = Util.IntToScaleStr(i.cost1, Consts.SUM_SCALE);
				c3 = Util.IntToScaleStr(i.cost2, Consts.SUM_SCALE);
			}

			tvCost.setText(c1);
			tvCost1.setText(c2);
			tvCost2.setText(c3);
		}

		return res;
	}

	private FolderImpl fld = new FolderImpl();

	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		View res = super.getFolderView(node, convertView);
		fld.read("id", node.id);

		if (DocType.getCurDoc() == MerchDoc.instance()) {
			String fldID = fld.getData().fid;
			MerchFolder f = ((MerchImpl) document).findFolder(fldID);

			initFolderButton(res, R.id.tvMine, f != null ? f.mine : null);
			initFolderButton(res, R.id.tvTheir, f != null ? f.their : null);
		}

		return res;
	}

	private void initFolderButton(View view, int tvID, Integer val) {
		TextView tv = (TextView) view.findViewById(tvID);
		tv.setTag(fld.getData().fid);
		tv.setOnClickListener(this);

		String str = val != null ? Util.IntToScaleStr(val, Consts.QTY_SCALE) : "";
		tv.setText(str);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.tvMine || id == R.id.tvTheir) {
			((MerchImpl) document).editFolder(this, v.getTag().toString(), id);
		}
	}
	
	@Override
	protected void updateTotalSum() {
		if (DocType.getCurDoc() == MerchDoc.instance() || DocType.getCurDoc() == MonitoringDocMSPB.instance() ||
				DocType.getCurDoc() == RivalMntrDoc.instance()) {
			View v = findViewById(R.id.tvTotalSum);
			
			if (v != null)
				v.setVisibility(View.GONE);
		}else
			super.updateTotalSum();
	}
	
	@Override
	protected boolean isAllowAllPrice() {
		DocType dt = DocType.getCurDoc();
		return !(dt == MerchDoc.instance() || dt == MonitoringDocMSPB.instance() || DocType.getCurDoc() == RivalMntrDoc.instance());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null) {
		     String bc = scanResult.getContents();
		     
		     Class<? extends DataObject> type  = PriceEx.class;
		     
		     if (DocType.getCurDoc() == RivalMntrDoc.instance())
		    	 type = RivalPrice.class; 
		     
		     List<Long> ids = DbReader.readIds(DataObjectInfo.getInstance().getTableName(type), "barcode LIKE '%" + bc + "%'", null);
		     
		     if( ids.size() > 0 ) {
		    	 ((Itemsable)document).editItem(ids.get(0), this);
		     }
		}
	}

}

class PriceImplRival extends PriceImpl {
	RivalPriceImpl src = new RivalPriceImpl();
	
	@Override
	public boolean read(long rowid, boolean useCache) {
		if(src.read(rowid, useCache)) {
			RivalPrice sd = src.getData();
			
			Field[] fld = data.getClass().getFields();
			for(Field f : fld) {
				try {
					Field sf = sd.getClass().getField(f.getName());
					f.set(data, sf.get(sd));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			return true;
		}
		return false;
	}
	
	@Override
	public void close() {
		src.close();
	}
}

package com.grsoft.napoleon;

import com.grsoft.database.DbReader;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixAdapter;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehosueEx extends Warehouse {

	static long lastOrder = ExtrasConst.INVALID_ROWID;
	static int curMatrix = 0;
	MatrixOrder matrixOrder = null;
	boolean hideMatrix = false;

	private String LAST_DOC_TYPE = "last_doc_type";

	@Override protected int getLayoutId() { return R.layout.warehouse_ex; }
	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.ibNextPrice).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				curMatrix++;
				resetMatrix();
			}
		});
	}

	@Override
	protected void updateTotalSum() {
		super.updateTotalSum();
		if(document instanceof ReturnImpl) {
			OrgImpl oi = new OrgImpl();
			oi.read("id", document.getId());
			OrgEx oe = (OrgEx) oi.getData();
			TextView tv = findViewById(R.id.tvTotalSum);
			String text = tv.getText().toString();
			String limit = Util.IntToScaleStr(oe.limit, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			text += String.format(getString(R.string.return_limit), limit);
			tv.setText(Html.fromHtml(text));
		}
	}

	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View v = super.getPriceView(node, convertView); 
		TextView tv;
		
		Price p = price.getData();
		if(p.qtyInPack == 0)
			p.qtyInPack = Consts.QTY_SCALE;
		if(p.weight == 0)
			p.weight = Consts.WEIGHT_SCALE;
		
		long cost = CostStrategy.defaultInstance.getItemCost(p, document);
		
		int costPack = (int)((long)cost * p.qtyInPack / Consts.QTY_SCALE);  
		tv = (TextView)v.findViewById(R.id.tvPrc1);
		tv.setText(Util.IntToScaleStr(costPack, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		
		int costW = (int)((long)cost * Consts.WEIGHT_SCALE / p.weight);
		tv = (TextView)v.findViewById(R.id.tvPrc2);
		tv.setText(Util.IntToScaleStr(costW, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		
		return v;
	}

	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		AssortmentMatrixAdapter.MATRIX_DOC = (DocType.getCurDoc() == ReturnDoc.instance()) ? DeliveryDoc.instance() : OrderDoc.instance();
		return super.createAssortementMatrixAdapter();
	}

	@Override
	protected BaseAdapter createListAdapter() {
		if( DocType.getCurDoc() == ReturnDoc.instance())
			return createAssortementMatrixAdapter();

		String lastDocType = getPreferences(Context.MODE_PRIVATE).getString(LAST_DOC_TYPE, "");
		String curDocName = DocType.getCurDoc().getName();

		if(!curDocName.equals(lastDocType)){
			SharedPreferences.Editor ed = getPreferences(Context.MODE_PRIVATE).edit();
			ed.putString(LAST_DOC_TYPE, curDocName);
			ed.commit();

			FoldersAdapter.resetCache();
		}

		hideMatrix = false;
		if( DocType.getCurDoc() == OrderDoc.instance() && document.getRowid() != ExtrasConst.INVALID_ROWID ) {
			if( lastOrder != document.getRowid() ) {
				lastOrder = document.getRowid();
				curMatrix = 0;
			}

			if( matrixOrder == null ) {
				matrixOrder = new MatrixOrder();
				DbReader r = new DbReader();
				r.select(matrixOrder, matrixOrder.getTableName(), null);
				r.close();
			}

			if( curMatrix < matrixOrder.items.size() ) {
				hideMatrix = true;
				findViewById(R.id.llMatrixOrder).setVisibility(View.VISIBLE);
				String name = matrixOrder.items.get(curMatrix).name;
				TextView tv = (TextView)findViewById(R.id.tvMatrixName);
				tv.setText(name);
				String actitle = getString(R.string.active_assortiment);
				if(name.equals(actitle)) {
					return createAssortementMatrixAdapter();
				}
				return new MatrixAdapter(this, name);
			}
		}
		findViewById(R.id.llMatrixOrder).setVisibility(View.GONE);
		return super.createListAdapter();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean ret = super.onPrepareOptionsMenu(menu);
		MenuItem mi = menu.findItem(R.id.itMatrix);
		if( mi != null)
			mi.setVisible(!hideMatrix);
		return ret;
	}
}

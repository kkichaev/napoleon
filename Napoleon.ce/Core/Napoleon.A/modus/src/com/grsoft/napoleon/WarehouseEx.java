package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.ActionPrice;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.MerchItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Recommend;
import com.grsoft.dataobjects.impl.ActionPriceImpl;
import com.grsoft.dataobjects.impl.MerchImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MerchBeginDoc;
import com.grsoft.napoleon.documents.MerchEndDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.MatrixBaseAdapter;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class WarehouseEx extends WarehouseNew {
	MerchMatrix merchMatrix;
	private List<String> recommend = new ArrayList<String>();
	private Map<String, ActionPrice> actions = new HashMap<String, ActionPrice>();
	ActionPriceImpl actionPriceImpl = new ActionPriceImpl();
	
	
	@Override
	protected void updateChildPriceView(View view, Price p) {
		super.updateChildPriceView(view, p);
		
		ActionPrice ap = actions.get(p.id);
		
		if (ap != null) {
			actionPriceImpl.getData().orgid = ap.orgid;
			actionPriceImpl.getData().priceid = ap.priceid;
			actionPriceImpl.read();
			actionPriceImpl.close();
			
			ap = actionPriceImpl.getData();
		}
		
		ImageView iv = (ImageView) view.findViewById(R.id.ivAction);
		iv.setVisibility(ap != null ? View.VISIBLE : View.INVISIBLE);
		
		iv = (ImageView) view.findViewById(R.id.ivRecommend);
		iv.setVisibility(recommend.contains(p.id) ? View.VISIBLE : View.INVISIBLE);
		
		TextView tv = (TextView) view.findViewById(R.id.tvACost);
		tv.setText(ap != null ? Util.IntToScaleStr(ap.cost, Consts.SUM_SCALE) : "0");
		
		tv = (TextView) view.findViewById(R.id.tvAQty);
		tv.setText(ap != null ? Util.IntToScaleStr(ap.qty, Consts.QTY_SCALE) : "0");
	}
	
	@Override
	protected int getItemLayoutId() {
		return R.layout.priceitemrowex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String where = String.format("orgid='%s'", document.getId());
		
		DataTraveler.travel(Recommend.class, new DataTraveler.Travel<Recommend>() {

			@Override
			public boolean travel(DataTraveler<Recommend> item) {
				recommend.add(item.data.priceid);
				return true;
			}
		}, where);
		
		DataTraveler.travel(ActionPrice.class, new DataTraveler.Travel<ActionPrice>(true) {

			@Override
			public boolean travel(DataTraveler<ActionPrice> item) {
				actions.put(item.data.priceid, item.data);
				return true;
			}
		}, where);
	}
	
	@Override
	protected void postAdapterInit() {
		if(DocType.getCurDoc() == MerchEndDoc.instance()){
			merchMatrix = new MerchMatrix(this, (MerchImpl) document);
			matrixName = getString(R.string.MerchMatrix);
			applayAdapter(merchMatrix);
		}else
			super.postAdapterInit();
	}

	@Override
	protected boolean inheritedApplayMatrix(String matrixName) {
		if(matrixName.equals(getString(R.string.MerchMatrix))){
			matrixName = getString(R.string.MerchMatrix);
			applayAdapter(merchMatrix);
			return true;
		}else
			return super.inheritedApplayMatrix(matrixName);
	}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		items.add(1, getString(R.string.MerchMatrix));
		
		return items;
	}
	
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		DocType cur = DocType.getCurDoc();
		
		if (cur == MerchBeginDoc.instance() || cur == MerchEndDoc.instance()){
			int val = 0;
			
			MerchImpl merch = (MerchImpl) document;
			MerchItem mi = (MerchItem) merch.findItem(price.id);
			
			if(mi != null)
				switch(textView.getId()){
				case R.id.tvClmn1:
					val = mi.start;
					break;
				case R.id.tvClmn2:
					val = mi.finish;
					break;
				}
			
			textView.setText(Util.IntToScaleStr(val, Consts.QTY_SCALE));
		}else
			super.setTextColumnValue(textView, type, price);
	}
}

class MerchMatrix extends MatrixBaseAdapter{
	MerchImpl doc;
	
	public MerchMatrix(WarehouseNew warehouse, MerchImpl doc) {
		super(warehouse);
		this.doc = doc; 
	}

	@Override
	protected List<? extends MatrixItem> getMatrixItems() {
		List<MatrixItem> result = new ArrayList<MatrixItem>();
		
		for(MerchItem mi : doc.getData().items){
			MatrixItem i = new MatrixItem();
			i.id = mi.id;
			result.add(i);
		}
		
		return result;
	}
	
	@Override
	public String getName() { return warehouse.getString(R.string.MerchMatrix); }
}
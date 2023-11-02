package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrixItem;
import com.grsoft.dataobjects.impl.DistribMatrixImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OffTakeHistory;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.MatrixAdapter;
import com.grsoft.util.WarehouseAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;

public class WarehouseEx extends WarehouseNew {
	private BaseAdapter listAdapter;
	private View llMatrixOrder;
	private View ibNextPrice;
	private boolean core = false;
	private static Map<String, Integer> offtakeMap = new HashMap<String, Integer>();
	
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
}

class OrgTypeMatrix extends MatrixAdapter{
	private DistribMatrixImpl matrix;
	public OrgTypeMatrix(WarehouseNewW warehouse, DistribMatrixImpl matrix) {
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


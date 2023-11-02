package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.BaseAdapter;
import com.grsoft.dataobjects.Contract;
import com.grsoft.dataobjects.ContractDef;
import com.grsoft.dataobjects.ContractDefItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.impl.ContractDefImpl;
import com.grsoft.dataobjects.impl.ContractImpl;
import com.grsoft.util.MatrixBaseAdapter;


public class WarehouseEx extends WarehouseNew {
	private ContractDefImpl contractDef = new ContractDefImpl();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		if(document instanceof ContractImpl){
			Contract c = (Contract) document.getData();
			contractDef.read("id", c.def);
			
			return new MatrixBaseAdapter(this) {
				@Override
				protected List<? extends MatrixItem> getMatrixItems() {
					List<MatrixItem> result = new ArrayList<MatrixItem>();
					
					ContractDef cd = contractDef.getData();
					for(ContractDefItem i : cd.items){
						MatrixItem mi = new MatrixItem();
						mi.id = i.id;
						
						result.add(mi);
					}
						
					return result;
				}
			};
		}else
			return super.createListAdapter();
	}
	
	@Override
	public boolean isPriceExpand() { return true; }
}

package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	boolean loadNewItems = false;
	
	@Override protected int getLayoutId() { return R.layout.warehouseex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		loadNewItems = ((CfgNplEx)ConfigManager.getConfig()).showNewMatrix;
		
		super.onCreate(savedInstanceState);
		
		if(document instanceof OrderImplEx ) {
			View v = findViewById(R.id.btnWhIndex);
			v.setVisibility(View.VISIBLE);
			v.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { changeWhIndex(); }
			});
			if(loadNewItems ) {
				View np = findViewById(R.id.ibNextPrice); 
				np.setVisibility(View.VISIBLE);
				np.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						loadNewItems = false;
						resetMatrix();
						v.setVisibility(View.GONE);
					}
				});
			}
		}
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		if(adapter != null)
			adapter.deleteFilter(NewMatrixFilter.NAME);
		
		FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
		if( loadNewItems )
			ret.putFilter(new NewMatrixFilter());
		return ret;
	}
	
	protected void changeWhIndex() {
		OrderEx oe = (OrderEx) document.getData();
		oe.whIndex = (oe.whIndex == 0) ? 1 : 0;
		ImageButton ib = (ImageButton)findViewById(R.id.btnWhIndex);
		ib.setImageResource(oe.whIndex == 0 ? R.drawable.pack_off : R.drawable.pack_on);
		
		notifyDataSetChanged();
	}

	@Override
	protected Filter createZeroPositionFilter() {
		if( document instanceof OrderImplEx && ((OrderEx)document.getData()).whIndex == 1 )
			return new ZeroFilterQty2();
		return super.createZeroPositionFilter();
	}

	@Override
	protected void readDocument() {
		if( document instanceof OrderImplEx ) {
			OrderEx oe = (OrderEx) document.getData();
			int wi = oe.whIndex;
			super.readDocument();
			oe.whIndex = wi;
		} else
			super.readDocument();			
	}
}

class NewMatrixFilter extends Filter {
	public static String NAME = "NewMatrixFilter";
	
	public NewMatrixFilter() {
		super(NAME);
		where = "newItem=1";
	}
}

class ZeroFilterQty2 extends ZeroPositionFilter {
	public ZeroFilterQty2() {
		where = "qty2>0";
		FoldersAdapter.resetCache();
	}
}

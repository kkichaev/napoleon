package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.MatrixImpl;
import com.grsoft.util.MatrixAdapter;

import android.widget.BaseAdapter;

public class WarehouseEx extends WarehouseNew {
	boolean fullPrice = false;
	
	boolean canUseFullPrice() {
		fullPrice = false;
		ConfigImpl ci = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		if( ci.getValue(sb, "РаботаСПолнымПрайсом") ) {
			try {
				int val = Integer.parseInt(ci.getData().value);
				fullPrice = val != 0;
			} catch(Exception e ) {
				e.printStackTrace();
			}
		}
		return fullPrice;
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		if( !canUseFullPrice() ) {

			List<String> matrixes = MatrixImpl.getNames();
			if( matrixes != null && matrixes.size() > 0 )  {
				String matrix = matrixes.get(0);
				matrixName = matrix;
				return new MatrixAdapter(this, matrix);
			}
			fullPrice = true;
		}
		return super.createListAdapter();
	}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		ArrayList<String> ret = super.prepareMatrixList(items);
		if( !fullPrice )
			ret.remove(PRICE_WITHOUT_MATRIX);
		return ret;
	}
}

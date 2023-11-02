package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.AssortmentMatrixAdapterEx;

import android.os.Bundle;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {
	AssortmentMatrixAdapterEx assortmentMatrixAdapter;
	List<MatrixItem> ami = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createAssortementMatrixAdapter();
	}
	
	@Override
	public void setColor(TextView textView, Price price) {
		if(assortmentMatrixAdapter != null && assortmentMatrixAdapter.isIdInMatrix(price.id))
			textView.setTextColor(getResources().getColor(R.color.red));
		super.setColor(textView, price);
	};
	
	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		if (assortmentMatrixAdapter == null)
			assortmentMatrixAdapter =  new AssortmentMatrixAdapterEx(this, document.getId());
		
		return assortmentMatrixAdapter;
	}
}

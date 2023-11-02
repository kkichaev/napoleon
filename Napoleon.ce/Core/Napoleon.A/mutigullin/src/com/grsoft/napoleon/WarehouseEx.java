package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.MatrixAdapter;
import com.grsoft.util.Util;
import android.os.Bundle;

public class WarehouseEx extends WarehouseNew{
	DeliveryMatrix dlvmtx;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	class DeliveryMatrix extends MatrixAdapter{
		
		public DeliveryMatrix(WarehouseNewW warehouse) {
			super(warehouse, "DeliveryMatrix");
			
			final int DAY_RANGE = 1000;
			Calendar c =  Calendar.getInstance();
			c.setTime(Util.getDate());
			c.add(Calendar.DATE, -DAY_RANGE);
			String where = String.format("date > %d", c.getTime().getTime());
			
			final List<String> ids = new ArrayList<String>();
			DataTraveler.travel(Delivery.class, 
					new DataTraveler.Travel<Delivery>() {

						@Override
						public boolean travel(DataTraveler<Delivery> item) {
							for(DeliveryItem i : item.data.items)
								if(!ids.contains(i.id))
									ids.add(i.id);
							return true;
						}}, 
			where);
			
			for(String i : ids){
				MatrixItem m = new MatrixItem();
				m.id = i;
				matrix.getData().items.add(m);
			}
		}
		
		@Override public String getName() { return getString(R.string.distrmtxname); }
	}
	
	@Override
	protected void postAdapterInit() {
		super.postAdapterInit();
		
		if(DocType.getCurDoc() == RemnantsDoc.instance()){
			dlvmtx = new DeliveryMatrix(this);
			matrixName = dlvmtx.getName();
			applayAdapter(dlvmtx);
		}
	}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		if(DocType.getCurDoc() == RemnantsDoc.instance() && dlvmtx != null)
			items.add(dlvmtx.getName());
		
		return super.prepareMatrixList(items);
	}
	
	@Override
	protected void applayMatrix(String matrixName) {
		if (dlvmtx != null && matrixName.equals(dlvmtx.getName())){
			matrixName = dlvmtx.getName();
			applayAdapter(dlvmtx);
		}else
			super.applayMatrix(matrixName);
	}
}

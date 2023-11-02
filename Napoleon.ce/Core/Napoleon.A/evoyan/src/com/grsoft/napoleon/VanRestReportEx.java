package com.grsoft.napoleon;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.util.VanRestData;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class VanRestReportEx extends VanRestReport {

	int totalPack;
	long totalSum;
	
	@Override
	protected int getContentID() { return R.layout.van_rest_report_ex; }  
	
	@Override
	protected void onResume() {
		super.onResume();
		WSOrderDoc.instance().updateTotalSum(this, totalSum, 0, totalPack / Consts.QTY_SCALE);
	}
	
	@Override
	protected void buildData() {
		totalPack = 0;
		totalSum = 0;
		
		DataTraveler.travel(PricePrint.class, new DataTraveler.Travel<PricePrint>() {

			@Override
			public boolean travel(DataTraveler<PricePrint> item) {
				VanRestDataEx d = new VanRestDataEx();
				d.name = item.data.name;
				int qip = item.data.qtyInPack;
//				int qip = 0; //item.data.qtyInPack;
				if( qip == 0 )
					qip = Consts.QTY_SCALE;
				int qty = (int)((long)item.data.vanQty * Consts.QTY_SCALE / qip);
				d.qty = qty;
				
				if( item.data.cost.size() > 0 )
					d.sum = ((long)item.data.cost.get(0).cost * item.data.vanQty / Consts.QTY_SCALE);
				
				totalPack += qty;
				totalSum += d.sum;

				data.add(d);
				return true;
			}
		}, "vanQty>0", "name");
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(new RestAdapterEx());
	}
	class RestAdapterEx extends RestAdapter {
		@Override
		public View getView(int position, View v, ViewGroup parent) {
			if( v == null )
				v = View.inflate(VanRestReportEx.this, R.layout.van_rest_row_ex, null);
			
			VanRestDataEx d = (VanRestDataEx) getItem(position);
			
			if( d != null ) {
				TextView tv;
				
				tv = (TextView)v.findViewById(R.id.tvName);
				tv.setText(d.name);

				tv = (TextView)v.findViewById(R.id.tvQty);
				setQty(tv, d);

				tv = (TextView)v.findViewById(R.id.tvSum);
				tv.setText(Util.IntToScaleStr(d.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			}
			return v;
		}
	}
}

class VanRestDataEx extends VanRestData {
	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public long sum = 0;
}

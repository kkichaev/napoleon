package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceSalesQty;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItemPrintRest;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.util.VanRestData;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.napoleon.printsources.SalesSource;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class VanRestReportEx extends VanRestReport {

	int totalPack;
	long totalSum;
	
	SelectPrintFormDlgNew dlg = null;
	
	@Override
	protected int getContentID() { return R.layout.van_rest_report_ex; }  
	
	@Override
	protected void onResume() {
		super.onResume();
		WSOrderDoc.instance().updateTotalSum(this, totalSum, 0, totalPack / Consts.QTY_SCALE);
	}
	
	@Override
	protected void printing() {
		if( dlg == null ) {
			dlg = new SelectPrintFormDlgNew(this, WAIT_FOR_PRINT_DLG);
//			VanRestSource ss = new VanRestSource(data);
			AgentPrefix ap = AgentPrefix.get();
			SalesPrintEx pe = new SalesPrintEx(makeSales(data));
			pe.dogovor = "Основной договор";
			pe.name = ap == null ? "" : ap.fullname.length() > 0 ? ap.fullname : ap.name;
			SalesSource ss = new SalesSource(pe);
			dlg.setDataSource(ss);
		}
		dlg.createDialog(new String[] {"Остатки", "ТТН на остаток"}).show();
	}
	
	private Sales makeSales(ArrayList<VanRestData> data) {
		SalesEx ret = new SalesEx();
		ret.created = Util.getDateTime();
		ret.date = ret.created;
		ret.number = "4811";
		ret.supplyercode = "000000001";
		
		ret.items = new ArrayList<OrderItem>();
		
		for(VanRestData i : data) {
			VanRestDataEx ve = (VanRestDataEx)i;
			
			SalesItemPrintRest si = new SalesItemPrintRest();
			si.id = ve.id;
			si.cost = ve.cost;
			
			si.date = ve.date;
			si.qty = i.qty;
			
			si.countTax(ret, ve.tax);
			ret.items.add(si);
		}			
		return ret;
	}

	@Override
	protected void buildData() {
		totalPack = 0;
		totalSum = 0;
		
		DataTraveler.travel(PriceEx.class, new DataTraveler.Travel<PriceEx>() {

			@Override
			public boolean travel(DataTraveler<PriceEx> item) {
				
				int qip = item.data.qtyInPack;
				if( qip == 0 )
					qip = Consts.QTY_SCALE;

				int cost = 0;
				if( item.data.cost.size() > 0 ) {
					cost = (int)((long)item.data.cost.get(0).cost * qip / Consts.QTY_SCALE);
				}
				
				boolean assignedName = false;
				for(PriceSalesQty psq : item.data.party) {
					VanRestDataEx d = new VanRestDataEx();
					
					d.name = assignedName ? "" : item.data.name;
					int qty = (int)((long)psq.qty * Consts.QTY_SCALE / qip);
					d.qty = qty;
					
					d.date = psq.date;
					
					d.id = item.data.id;
					d.tax = item.data.tax1;
					
					d.cost = cost;
					d.sum = ((long)d.cost * qty / Consts.QTY_SCALE);
					
					totalPack += qty;
					totalSum += d.sum;

					data.add(d);
					assignedName = true;
				}
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

				tv = (TextView)v.findViewById(R.id.tvDate);
				tv.setText(Util.simpleDateFormat.format(d.date));

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
	
	public int tax = 0;
	public int cost = 0; 
	
	public String id = "";
	public Date date = new Date();
}

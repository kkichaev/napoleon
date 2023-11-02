package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.BaseDataSource;
import com.grsoft.napoleon.modules.print.DataSource;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;

public class WSOrderDetail extends OrderDetail {

	private static final int WAIT_FOR_PRINT_DLG = 0;
	
	static public void open(Context context, OrderImplBase<? extends Order> order) {
		Intent i = new Intent(context, WSOrderDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);		
	}
	
	
	private DocType docType = OrderDoc.instance();
	protected void onCreate(android.os.Bundle savedInstanceState) {
		docType = DocType.getCurDoc();
		DocType.setCurDoc(WSOrderDoc.instance());
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnPrint).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { print(); }
		});
	};
	
	@Override
	protected void updateTotalSum(){
		updateTotalSum(doc.sum(), doc.weight(), doc.countPack());
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == WAIT_FOR_PRINT_DLG )
			return SelectPrinFormDlg.createWaitDlg(this);
		return super.onCreateDialog(id);
	}
	
//	@Override
//	protected void updateTotalSum() {
//		updateTotalSum(doc.sum(), doc.weight(), doc.count());
//	}

	protected void print() {
		DataSource ds = new BaseDataSource(new PrnData((WSOrder) doc.getData()));
		SelectPrinFormDlg.createPrintForm(this, ds, WAIT_FOR_PRINT_DLG, "ws_order",null);
	}


	@Override
	protected void onDestroy() {
		DocType.setCurDoc(docType);
		super.onDestroy();
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderItemsAdapter(){
			@Override
			int getResourceID() {
				return R.layout.wsorderdetail_list_row;
			}
		});
	}
	
	@Override
	protected boolean haveFocusedGroup() {
		return false;
	}
	
	protected void setContentView(){
		setContentView(R.layout.wsorderdetail);
	}
}

class PrnData {
	public Date date;

	@Scale(value=Consts.QTY_SCALE, hideRest=true)
	public int qty;

	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public long sum;
	
	public List<WSItem> items = new ArrayList<WSItem>();
	
	public PrnData(WSOrder doc) {
		qty = 0;
		sum = 0;
		date = doc.date;
		PriceImpl pi = new PriceImpl();
		int num = 1;
		for (OrderItem oi : doc.items) {
			WSItem i = new WSItem(pi, oi, num++);
			items.add(i);

			qty += i.qty;
			sum += i.sum;
		}
	}
}

class WSItem {
	public int num;
	
	public String name;
	
	@Scale(value=Consts.QTY_SCALE, hideRest=true)
	public int qty;

	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public long sum;
	
	public WSItem(PriceImpl pi, OrderItem item, int num) {
		Price p = pi.getData();
		p.id = item.id;
		pi.read();
		name = p.name;

		int inPack = p.qtyInPack;
//		int inPack = 0; //p.qtyInPack;
		if( inPack == 0 )
			inPack = Consts.QTY_SCALE;
		
		qty = (int)((long)item.qty * Consts.QTY_SCALE / inPack);
		this.num = num;
		sum = (long)item.cost * item.qty / Consts.QTY_SCALE;
	}
}

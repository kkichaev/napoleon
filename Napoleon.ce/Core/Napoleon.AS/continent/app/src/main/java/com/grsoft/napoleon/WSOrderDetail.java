package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.grsoft.database.DocHandleResultHitching;
import com.grsoft.dataobjects.MovementAnswer;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.dataobjects.impl.MovementAnswerImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.WSOrderImpl;
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
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;

public class WSOrderDetail extends OrderDetail {

	private static final int WAIT_FOR_PRINT_DLG = 0;

	MovementAnswer refDoc = new MovementAnswer();

	Adapter adapter;
	SelectPrinFormDlg printDlg;

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
		printDlg = new SelectPrintFormDlgNew(this, WAIT_FOR_PRINT_DLG);
		printDlg.setPostExec(new Runnable() {
			@Override
			public void run() {
				((WSOrderImpl)doc).markPrinted();
				doc.write();
			}
		});

		findViewById(R.id.btnPrint).setVisibility(View.GONE);
//		findViewById(R.id.btnPrint).setOnClickListener(new View.OnClickListener() {
//			@Override public void onClick(View v) { print(); }
//		});
	};

	@Override
	protected void onResume() {
		super.onResume();

		refreshRefDoc();
	}

	void  refreshRefDoc() {
		MovementAnswerImpl mi = new MovementAnswerImpl();
		if(mi.read("created", doc.getData().created)) {
			refDoc = mi.getData();
			if(refDoc.noedit > 0) {
				((WSOrderImpl)doc).markPrinted();
				doc.write();
			}
		}
		showRefDoc();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void postSendExecute(boolean result) {
		super.postSendExecute(result);
		if(result) {
			refreshRefDoc();
			if(!refDoc.isEmpty()) {
				doc.getData().number = refDoc.number;
				doc.write();
			}

			if(DocHandleResultHitching.Result.isFail()) {
				MessageBox.show(WSOrderDetail.this, "Ошибка при передаче", DocHandleResultHitching.Result.message);
			}
		}
	}

	void showRefDoc() {
		View v = findViewById(R.id.UnloadTitle);
		if(refDoc.isEmpty()) {
			v.setVisibility(View.GONE);
		} else {
			v.setVisibility(View.VISIBLE);
		}
		TextView tv = findViewById(R.id.tvAnswerInfo);
		if(refDoc.remark.length() > 0) {
			tv.setVisibility(View.VISIBLE);
			tv.setText(refDoc.remark);
		} else {
			tv.setVisibility(View.GONE);
		}
	}

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
		String[] frm = new String[] { "Перемещение на борт" };
		DataSource ds = new BaseDataSource(new PrnData((WSOrder) doc.getData()));
		Dialog d = printDlg.createDialog(frm);
		printDlg.setDataSource(ds);
		d.show();
//
//		SelectPrinFormDlg.createPrintForm(this, ds, WAIT_FOR_PRINT_DLG, "ws_order", new Runnable() {
//			@Override
//			public void run() {
//				((WSOrderImpl)doc).markPrinted();
//				doc.write();
//			}
//		});
	}


	@Override
	protected void onDestroy() {
		DocType.setCurDoc(docType);
		super.onDestroy();
	}
	
	@Override
	protected void setAdapter() {
		adapter = new Adapter();
		lvItems.setAdapter(adapter);
	}
	
	@Override
	protected boolean haveFocusedGroup() {
		return false;
	}
	
	protected void setContentView(){
		setContentView(R.layout.wsorderdetail);
	}

	class Adapter extends OrderItemsAdapter {
		public Adapter() {
		}

		@Override
		int getResourceID() {
			return R.layout.wsorderdetail_list_row;
		}

		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
			super.drawInternal(view, name, color, item, pos);

			TextView tv = view.findViewById(R.id.tvAnswerQty);
			if(!refDoc.isEmpty()) {
				OrderItem ref = findRef(item);
				int qty = ref == null ? 0 : ref.qty;
				tv.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
				tv.setTextColor(color);
				tv.setVisibility(View.VISIBLE);
			} else {
				tv.setVisibility(View.GONE);
			}
		}

		@Override
		protected int getItemColor(int pos) {
			if(!refDoc.isEmpty()) {
				OrderItem oi = (OrderItem) getItem(pos);
				OrderItem ref = findRef(oi);

				if(ref != null && oi.qty != ref.qty)
					return Color.RED;
			}

			return super.getItemColor(pos);
		}

		OrderItem findRef(OrderItem src) {
			for(OrderItem oi : refDoc.items) {
				if(oi.id.equals(src.id))
					return oi;
			}
			return null;
		}
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

package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.WSOrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class WSOrderDetail extends OrderDetail {
//	private static final int REQ_DATE = 123;
//	private static final int TIME_DIALOG = 0;
	Adapter adapter;

	static public void open(Context context, OrderImplBase<? extends Order> order) {
		Intent i = new Intent(context, WSOrderDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);		
	}
	
	@Override
	protected void updateTotalSum() {
		TextView tv = (TextView)findViewById(R.id.tvTotalSum);
		if(tv != null) {
			int packCount = ((WSOrderImpl)doc).packCount();
			tv.setText(Util.IntToScaleStr(packCount, Consts.QTY_SCALE) + " ÿש.");
		}
	}
	
//	TimeHandler timeHandler; 
//	Date loadTime;
	
	private DocType docType = OrderDoc.instance();
	protected void onCreate(android.os.Bundle savedInstanceState) {
		docType = DocType.getCurDoc();
		DocType.setCurDoc(WSOrderDoc.instance());
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnEditOrder).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { WSOrderProperty.open(WSOrderDetail.this, doc); }
		});
		
//		findViewById(R.id.tvLoadDate).setOnClickListener(new View.OnClickListener() {
//			@Override public void onClick(View v) { changeLoadDate(); }
//		});
//
//		Calendar c = Calendar.getInstance();
//		c.set(Calendar.HOUR_OF_DAY, ((WSOrder)doc.getData()).loadTime);
//		c.set(Calendar.MINUTE, 0);
//		
//		loadTime = c.getTime();
//		
//		timeHandler = new TimeHandler((TextView)findViewById(R.id.tvLoadTime), loadTime, TIME_DIALOG) {
//			@Override
//			public void updateDate() {
//				super.updateDate();
//				
//				Calendar cdr = Calendar.getInstance();
//				cdr.setTime(date);
//				((WSOrder)doc.getData()).loadTime = cdr.get(Calendar.HOUR_OF_DAY);
//				doc.write();
//			}
//			
//			@Override protected String displayFormat() { return "HH"; }
//		};
//		
//		refreshDate();
	}
	
//	@Override
//	protected Dialog onCreateDialog(int id) {
//		if( id == TIME_DIALOG )
//			return timeHandler.createDialog();
//		
//		return super.onCreateDialog(id);
//	}
//	
//	protected void changeLoadDate() {
//		Intent i = new Intent(this, CalendarActivity.class);
//		i.putExtra(ExtrasConst.DATE_TAG, ((WSOrder)doc.getData()).loadDate);
//		startActivityForResult(i, REQ_DATE);
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if( data != null && requestCode == REQ_DATE ) {
//			Date curDate = new Date();
//			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
//			Date newDate = new Date(ct);
//			((WSOrder)doc.getData()).loadDate = newDate;
//			doc.write();
//			
//			refreshDate();
//		}
//	}
//
//	private void refreshDate() {
//		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
//		((TextView)findViewById(R.id.tvLoadDate)).setText(sd.format(((WSOrder)doc.getData()).loadDate));		
//	}
//	
	@Override
	protected void onDestroy() {
		DocType.setCurDoc(docType);
		super.onDestroy();
	}
	
	@Override
	protected void setAdapter() {
		adapter = new Adapter();
		lvItems.setAdapter(adapter);
		lvItems.setDividerHeight(0);
	}
	
	@Override
	protected boolean haveFocusedGroup() {
		return false;
	}
	
	protected void setContentView(){
		setContentView(R.layout.wsorderdetail);
	}

	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		boolean showPack = ((CfgNpl)ConfigManager.getConfig()).isPackView;
		String qtyText;
		if( !showPack )
			qtyText = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
		else {
			String packName = "רע.";
			int scale = Consts.QTY_SCALE;
			int qty = item.qty;
			if( item.inPack() ) {
				Price p = price.getData();
				int inPack = p.qtyInPack;
				if( inPack == 0 )
					inPack = Consts.QTY_SCALE;
				packName = getString(R.string.pack_lbl);
				qty = ((int)((long)item.qty * Consts.QTY_SCALE / inPack) + 50) / 100;
				scale = 10;
			}
			qtyText = Util.IntToScaleStr(qty, scale) + " " + packName;
		}
		tvQty.setText(qtyText);
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setTextColor(color);
	}
	
	class Adapter extends OrderItemsAdapter {
		@Override int getResourceID() { return R.layout.wsorderdetail_list_row; }
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			super.drawInternal(view, name, color, item);
			
			int index = doc.getData().items.indexOf(item);
			view.setBackgroundResource((index % 2) != 0 ? R.drawable.even_row_selector
					: R.drawable.list_selector);
		}
	}
}

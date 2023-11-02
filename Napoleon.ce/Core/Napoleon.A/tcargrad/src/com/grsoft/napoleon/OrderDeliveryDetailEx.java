package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.HashSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.CostTypes;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.FocusedItemsTCImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.util.DocItemsRemover;
import com.grsoft.util.Consts;

public class OrderDeliveryDetailEx extends OrderDeliveryDetail {

	private static final int ASK_FOR_SEND = 0x1000;
	private static final int SEND_CHANGES = 0x1001;
	
	DocItemsRemover remover;

	public static OrderChecker checker = new OrderChecker();

	@Override protected boolean haveFocusedGroup() { return true; }
	
	@Override protected boolean disableSendWithoutFocusedGroup() { return false; }
	
	@Override
	protected void editItem(OrderItem orderItem) {
		if( !remover.itemClicked(orderItem) )
			super.editItem(orderItem);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdeliverydetailex);
	}
	
	@Override
	protected void init() {
		super.init();

		checker.set(doc.getData());

		View btnProp = findViewById(R.id.btnProps);
		btnProp.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { OrderProps.open(OrderDeliveryDetailEx.this, doc); }
		});
	}
	
	void syncCost(Order src, OrderImplBase<? extends Order> dest) {
		boolean costChanged = false;
		for(OrderItem oi : src.items) {
			OrderItem di = (OrderItem) dest.findItem(oi.id);
			if( di != null ) {
				if( di.cost != oi.cost ) {
					costChanged = true;
					di.cost = oi.cost;
				}
			}
		}
		
		if( costChanged ) {
			dest.write();
			doc.getDocumentType().refreshDocSum(doc.getId());
		}
	}
	
	@Override
	protected void checkObjectSendResult(OrderImplBase<? extends Order> object, String response, int result) {
		syncCost(object.getData(), doc);
		checker.set(doc.getData());
		super.checkObjectSendResult(object, response, result);
	}

	@Override
	protected String getOrgText(Org o) {
		String text = super.getOrgText(o);
		Order ord = doc.getData();
		if( ord.number.length() > 0 )
			text += "<br>№ накладной <i>" + ord.number + "</i>";
		return text;
	}
	
	Dialog makeSendDialog(int title, int send, DialogInterface.OnClickListener onNo) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(title);
		b.setMessage(send);
		b.setNegativeButton(R.string.no, onNo);
		
		b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				OrderDeliveryDetailEx.super.send();
				arg0.dismiss();
			}
		});
		return b.create();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case ASK_FOR_SEND:
			return makeSendDialog(R.string.confirm_title, R.string.confirm_send, null);
		case SEND_CHANGES:
			return makeSendDialog(R.string.send_title, R.string.send_message, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					checker.restoreOrder(doc);
					arg0.dismiss();
					finish();
				}
			});
		}
		return super.onCreateDialog(id);
	}
	
	boolean isChangedItem(OrderItem i) {
		return checker.isChanged((OrderItemEx)i);
//		Integer qty = checker.o(i.id);
//		return ( qty == null || i.qty != qty );
	}
	
	protected long getItemSum(OrderItem i) {
		if( isChangedItem(i) )
			return (int)((long)i.cost * i.qty / Consts.QTY_SCALE);
		
		DeliveryItem dlvitem = getDlvItem(i.id);
		return (dlvitem == null) ? 0 : dlvitem.sum;
	}
	
	@Override
	protected void updateTotalSum() {
		updateTotalSum(doc.sum(), delivery.weight());
	}
	
	
	@Override public void send() { showDialog(ASK_FOR_SEND); }
	
	@Override
	protected boolean haveUnsettedFocusedGroups() {
		HashSet<String> recommend = new HashSet<String>();
		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx)oi.getData();
		o.id = doc.getId();
		oi.read();

		FocusedItemsTCImpl.loadItems(recommend, o.orgType, true);
		
		if( doc.getData().items != null )
			for(OrderItem i : doc.getData().items ) {
				if(recommend.contains(i.id) )
					recommend.remove(i.id);
			}
		
		return recommend.size() > 0;
	}
	
	@Override
	protected void setAdapter() {
		Adapter a = new Adapter();
		remover = new DocItemsRemover(a, (ImageButton) findViewById(R.id.btnDel), doc);
		lvItems.setAdapter(a);
	}

	@Override
	protected void openFocusItemEditor() {
		MissingFocusItem.open(this, doc);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(checker.isChanged(doc.getData())) {
				showDialog(SEND_CHANGES);
				return true;
			}
			checker.clear();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	class Adapter extends OrderDeliveryItemsAdapter {
		HashMap<String, String> costTypes = new HashMap<String, String>();
		
		public Adapter() {
			loadCostTypes();
		}
		
		@Override
		int getResourceID() { return R.layout.orderdeliverydetail_list_rowex; }

		void loadCostTypes() {
			CostTypes ct = new CostTypes();
			DbReader r = new DbReader();
			String table = DataObjectInfo.getInstance().getTableName(ct.getClass());
			boolean bdo = r.select(ct, table, "");
			while( bdo ) {
				costTypes.put(ct.id, ct.name);
				bdo = r.selectNext(ct);
			}
			r.close();
		}
		
		@Override
		protected long getItemSum(OrderItem item) {
			return OrderDeliveryDetailEx.this.getItemSum(item);
		}
		
		@Override
		protected int getItemColor(OrderItem item, int defaultColor) {
			if( isChangedItem(item))
				return Color.GREEN;
			return super.getItemColor(item, defaultColor);
		}
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			OrderItemEx ie = (OrderItemEx)item;
			String taxName = costTypes.get(ie.taxType);
			if( taxName != null )
				name += "\n" + taxName;
			
			super.drawInternal(view, name, color, item);

			view.setBackgroundResource(remover.inSet(item) ? R.drawable.below_zero_row_selector : R.drawable.list_selector);
		}
	}
}

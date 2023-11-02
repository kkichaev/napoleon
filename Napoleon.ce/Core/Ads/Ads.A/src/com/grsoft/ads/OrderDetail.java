package com.grsoft.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.ads.database.OrderItem;
import com.grsoft.ads.dataobjects.impl.PriceImpl;
import com.grsoft.ads.documents.OrderItemsDocument;
import com.grsoft.ads.utils.ItemsBaseAdapter;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class OrderDetail extends BaseActivity {
	public static final String TAB_NAME = "order_items";
	public static final String TAB_CAPTION = "Материалы";
	private OrderItemsDocument<? extends CreateDocDataObject> orderItemsDocument;
	private ItemsAdapter adapter;
	private LinesCountController linesController;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_detail);
		orderItemsDocument = ((OrderItemsDocument<? extends CreateDocDataObject>) 
				DocType.getCurDoc().create());
		
		long rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, 
				ExtrasConst.INVALID_ID);
		
		if (orderItemsDocument.read(rowid)){
			ListView lvOrderItems =((ListView)findViewById(R.id.lvOrderItems));
			ImageButton btnLines = (ImageButton) findViewById(R.id.btnLines);
			LinesOnClickListener linesOnClickListener = new LinesOnClickListener(
					lvOrderItems, btnLines, this);
			linesController = linesOnClickListener.getController();
			adapter = new ItemsAdapter(this, orderItemsDocument, linesController);
			lvOrderItems.setAdapter(adapter);
			lvOrderItems.setOnItemClickListener(adapter);
			registerForContextMenu(lvOrderItems);
		
			ImageButton btnAdd = (ImageButton) findViewById(R.id.btnAdd);
			
			if (btnAdd != null){
				
				if (orderItemsDocument.isEditable())
					btnAdd.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Warehouse.open(v.getContext(), orderItemsDocument.getRowid());
						}
					});
				else
					btnAdd.setEnabled(false);
			}
		}
		
		orderItemsDocument.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.order_detail_menu, menu);
		return true; 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itAdd)
			Warehouse.open(this, orderItemsDocument.getRowid());

		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		menu.findItem(R.id.itAdd).setVisible(orderItemsDocument.isEditable());
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.order_detail_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int position = ((AdapterContextMenuInfo)
				item.getMenuInfo()).position;
		int itemId = item.getItemId(); 
		if (itemId == R.id.itAdd)
			Warehouse.open(this, orderItemsDocument.getRowid());
		else if (itemId == R.id.itEdit)
			adapter.onItemClick(null, null, position, 0);
		else if (itemId == R.id.itDel)
			adapter.delteItem(position);
		
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		orderItemsDocument.read();
		orderItemsDocument.close();
		
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (orderItemsDocument.isEditable())
			orderItemsDocument.write();
	}
}

class ItemsAdapter extends ItemsBaseAdapter implements OnItemClickListener{

	private  OrderItemsDocument<? extends CreateDocDataObject> orderItemsDocument;

	public ItemsAdapter(Context context, OrderItemsDocument<? extends CreateDocDataObject> orderItemsDocument, LinesCountController linesController){
			super(context, linesController);
			this.orderItemsDocument = orderItemsDocument;
	}
	
	public void delteItem(int position) {
		OrderItem orderItem = (OrderItem) getItem(position);
		
		if (orderItem != null){
			orderItemsDocument.getOrderItems().remove(orderItem);
			orderItemsDocument.write();
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return orderItemsDocument.getOrderItems().size();
	}

	@Override
	public Object getItem(int position) {
		return orderItemsDocument.getOrderItems().get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(getContext(), R.layout.ads_list_row, null);
		
		OrderItem item = (OrderItem) getItem(position);
		convertView.setTag(item);
		
		if(item != null){
			TextView tvClmn1 = (TextView)convertView.findViewById(R.id.tvClmn1);
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.getData().id = item.priceid;
			
			if (priceImpl.read())
				tvClmn1.setText(priceImpl.getData().name);
			else
				tvClmn1.setText("неизвестно");
			
			priceImpl.close();
			
			TextView tvClmn2 = (TextView) convertView.findViewById(R.id.tvClmn2);
			tvClmn2.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE));
			
			applyLineController(tvClmn1);
		}
		
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, 
			View arg1, final int pos, long arg3) {
		InputNumberDlg.open(getContext(), new InputNumber() {
			
			@Override
			public int getValue() {
				OrderItem item = (OrderItem) getItem(pos);
				return item == null ? 0 : item.qty;
			}
			
			@Override
			public void applayInput(int value, Object... params) {
				OrderItem item = (OrderItem) getItem(pos);
				if (item != null)
					item.qty = value;
				orderItemsDocument.write();
				notifyDataSetChanged();
			}
		});
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		
		TextView tvInfo = (TextView)((Activity)getContext()).findViewById(R.id.tvInfo);
		
		int sum = 0;
		for(OrderItem item : orderItemsDocument.getOrderItems())
			sum += item.qty;
		
		tvInfo.setText(String.format("Всего: %s", Util.IntToScaleStr(sum, Consts.QTY_SCALE)));
	}
}
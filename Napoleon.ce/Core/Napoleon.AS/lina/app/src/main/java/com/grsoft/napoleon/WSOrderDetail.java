package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.AgentPrefix;
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
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.util.BTPrinterHelper;
import com.grsoft.napoleon.modules.print.util.BTPrinterSettings;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.Toast;

public class WSOrderDetail extends OrderDetail {
	private static final int WAIT_FOR_PRINT_DLG = R.id.wait_for_print_dlg;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	protected String fileName = "";
	private static final int REQUEST_ENABLE_BT = 1;
	
	static public void open(Context context, OrderImplBase<? extends Order> order) {
		Intent i = new Intent(context, WSOrderDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);		
	}
	
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
		    String action = intent.getAction();

	        if (NPrinter.SEND_TXT_FILE_ACTION.equals(action)){
				if (bluetoothAdapter == null) 
				   Toast.makeText(context, "Bluetooth недоступен", Toast.LENGTH_LONG).show();
				else{
					fileName  = intent.getStringExtra("file");
					if (!bluetoothAdapter.isEnabled()) {
					    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
					} else {
						printing();						
					}
				}
	        }
		}
	};
	
	protected void printing() {
		BTPrinterSettings cfg = BTPrinterHelper.getSettings(this);
		if( cfg.address.length() > 0 )
			BTPrinterHelper.printing(cfg.address, cfg.copies, fileName, this);
		else {
			Toast.makeText(this, "Настройте, пожалуйста, принтер", Toast.LENGTH_SHORT).show();
			Setting.open(this, TextPrinterSetting.class);
		}
	}

	private DocType docType = OrderDoc.instance();
	protected void onCreate(android.os.Bundle savedInstanceState) {
		docType = DocType.getCurDoc();
		DocType.setCurDoc(WSOrderDoc.instance());
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnPrint).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				PrintSource ps = new PrintSource((WSOrder)doc.getData());
				BaseDataSource ds = new BaseDataSource(ps);
				SelectPrinFormDlg.createPrintForm(WSOrderDetail.this, ds, WAIT_FOR_PRINT_DLG, "rest", null);
			}
		});
	};
	

	@Override
	protected void onResume() {
		super.onResume();
	
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NPrinter.SEND_TXT_FILE_ACTION);
		registerReceiver(receiver, intentFilter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case WAIT_FOR_PRINT_DLG:
			return SelectPrinFormDlg.createWaitDlg(this);
		default:
			return super.onCreateDialog(id);
		}
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

class PrintSource {
	public String agent = "";
	public String date;
	
	public List<Item> items = new ArrayList<Item>();
	
	public PrintSource(WSOrder doc) {
		AgentPrefix ap = AgentPrefix.get();
		if( ap != null )
			agent = ap.name;
		
		//date = Util.simpleDateFormat.format(new Date());
		date = Util.simpleDateFormat.format(doc.date);
		
		int order = 1;
		
		PriceImpl p = new PriceImpl();
		for(OrderItem item : doc.items) {
			items.add(new Item(item, p, order++));
		}
		
		p.close();
	}
}

class Item {
	public String name;
	public int order;
	@Scale(value=Consts.QTY_SCALE, hideRest=true)
	public int qty;
	public String unit;
	
	public Item(OrderItem item, PriceImpl p, int order) {
		qty = item.qty;
		
		Price pp = p.getData();
		pp.id = item.id;
		p.read();
		
		if( item.inPack() ) {
			unit = pp.packName;
			qty = (int)((long)qty * Consts.QTY_SCALE / pp.qtyInPack);
		} else {
			unit = pp.unit;
		}
		
		name = pp.name;
		
		this.order = order;
	}
}

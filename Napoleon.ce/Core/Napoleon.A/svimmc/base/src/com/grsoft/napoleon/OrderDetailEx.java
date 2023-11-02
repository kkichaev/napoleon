package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.modules.print.DataSource;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.util.BTPrinterHelper;
import com.grsoft.napoleon.modules.print.util.BTPrinterSettings;
import com.grsoft.napoleon.modules.print.util.Dig2Str;
import com.grsoft.napoleon.printsources.SilentReflector;
import com.grsoft.napoleon.printsources.SupplSource;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.Util;
import com.grsoft.util.WaitDlg;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class OrderDetailEx extends OrderDetail {
	private ImageButton btnPrint;
	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	protected String fileName = "";
	

	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (NPrinter.SEND_TXT_FILE_ACTION.equals(action)) {
				if (bluetoothAdapter == null)
					Toast.makeText(context, "Bluetooth недоступен",
							Toast.LENGTH_LONG).show();
				else {
					fileName = intent.getStringExtra("file");
					if (!bluetoothAdapter.isEnabled()) {
						Intent enableBtIntent = new Intent(
								BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableBtIntent,
								REQUEST_ENABLE_BT);
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
			Setting.open(this, PrinterSetting.class);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(receiver);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK){
			printing();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NPrinter.SEND_TXT_FILE_ACTION);
		registerReceiver(receiver, intentFilter);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnPrint = (ImageButton) findViewById(R.id.btnPrint);
		btnPrint.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, File>() {

					@Override
					protected File doInBackground(Void... params) {
						return NPrinter.print(OrderDetailEx.this, "order",
								new OrderDataPrint((OrderEx) doc.getData()));
					}

					protected void onPreExecute() {
						showDialog(R.id.wait_dlg);
					};

					protected void onPostExecute(File result) {
						dismissDialog(R.id.wait_dlg);

						if (result != null)
							NPrinter.sendPrintTask(OrderDetailEx.this, result);
					};

				}.execute((Void[]) null);
			}
		});
		btnSend.setVisibility(View.GONE);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.wait_dlg)
			return WaitDlg.createDialog(this);
		return super.onCreateDialog(id);
	}

	@Override
	protected void updateTotalSum() {
		super.updateTotalSum();
		
		OrderEx oe = (OrderEx) doc.getData();
		OrgDogovor dog = DocHelper.getDogovor((OrgEx) org.getData(), oe.iddog);
		
		PriceImpl pi = new PriceImpl();
		int sum = 0;
		@SuppressWarnings("unchecked")
		CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) doc.getClass());
		for (OrderItem i : doc.getData().items) {
			pi.read("id", i.id);
			sum +=  FPOperation.itemMul(cs.getItemCost(pi.getData(), doc), i.qty, Consts.QTY_SCALE);  
		}
		pi.close();
		
		int d = sum - doc.sum();
		TextView tvDiscountInfo = (TextView) findViewById(R.id.tvDiscountInfo);
		tvDiscountInfo.setText (getResources().getString(R.string.sum_no_discount)+" = "+
		Util.IntToScaleStr(sum, Consts.SUM_SCALE) + " " + getResources().getString(R.string.discount)+
		" = "+	Util.IntToScaleStr(d, Consts.SUM_SCALE) );
		
		
//		TextView tvTotalSum = (TextView)findViewById(R.id.tvTotalSum);
//		String s=tvTotalSum.getText().toString();
		
//	tvTotalSum.setText(s + "("+getResources().getString(R.string.discount)+"="+ oe.discval+")");
	
		// int sum = doc.sum();
		// sum -= (int) (((long) sum * oe.getDiscval() + Consts.SUM_SCALE
		// * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));

		TextView tv;
		// tv = (TextView) findViewById(R.id.tvInfo);
		// tv.setText(String.format("%s(%s%%)",
		// Util.IntToScaleStr(sum, Consts.SUM_SCALE),
		// Util.IntToScaleStr(oe.getDiscval(), Consts.SUM_SCALE)));

		if (dog != null) {
			tv = (TextView) findViewById(R.id.tvMinSum);
			tv.setText(getString(R.string.min_order_cost,
					Util.IntToScaleStr(dog.minOrder, Consts.SUM_SCALE)));
		}
		
			
	}
}

class OrderDataPrintItem {
	public String barcode = "";
	public String name = "";
	public String qty = "";
	public String cost = "";
	public int isum = 0;
	public String sum = "";
}

@SuppressWarnings("serial")
class OrderDataPrintItems extends Vector<OrderDataPrintItem> implements
		DataSource {
	public int index = 0;

	@Override
	public void startPage() {
	}

	@Override
	public boolean getValue(StringBuilder value, String name, String format) {
		return SilentReflector.getFieldValue(value, name, get(index), format)
				|| SilentReflector.getFieldValue(value, name, this, format);
	}

	@Override
	public DataSource getObject(String name) { return this;	}

	@Override
	public boolean haveMoreData() {	return (index + 1 < size()); }

	@Override
	public void calculate() { }

	@Override
	public boolean moveNext() {
		index++;

		if (index >= size())
			return false;
		else 
			return true;
	}
}

class OrderDataPrint implements DataSource {
	public static String SUM_TEXT_FORMAT = "%s, %02d";
	public String date = "";
	public String number = "";
	public String sklad = "";
	public String dogovor = "";
	public String name = "";
	public String torg = "";
	public OrderDataPrintItems items = new OrderDataPrintItems();
	protected SupplSource supplSource = new SupplSource();
	int totalSum = 0;
	@Scale(value = Consts.SUM_SCALE, hideRest = false)
	public int sumWDisc = 0;
	@Scale(value = Consts.SUM_SCALE, hideRest = false)
	public int disc = 0;
	public String sum = "";
	@Scale(value = Consts.QTY_SCALE, hideRest = true)
	public int totalQty = 0;
	public String sumText = "";

	public OrderDataPrint(Order doc) {
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		cfg.getValue(sb, "Организация");
		if (sb.length() > 0) {
			String[] firms = sb.toString().split(";");

			if (firms.length > doc.supplyer)
				supplSource.supl_name = firms[doc.supplyer];
		}

		ArrayList<KeyValue> list = new ArrayList<KeyValue>();

		sb.setLength(0);
		cfg.getValue(sb, "Склады");
		String sklad = ""; 
		try{
			sklad = (String) doc.getClass().getField("sklad").get(doc);
		}catch(Exception e){}
		
		int idx = DialogHelper.makeListWithKey(sb.toString(), list, sklad);
		if (idx != - 1 && idx < list.size())
			sklad = (String) list.get(idx).value;

		OrgImpl impl = new OrgImpl();
		impl.getData().id = doc.id;
		impl.read();
		impl.close();

		OrgEx org = (OrgEx) impl.getData();
		String iddog = "";
		
		try{
			iddog = (String) doc.getClass().getField("iddog").get(doc);
		}catch(Exception e){}
			
		for (OrgDogovor od : org.dogovors) {
			if (od.id.equals(iddog)) {
				dogovor = od.name;
				break;
			}
		}

		date = Util.simpleDateFormat.format(doc.created);
		
		try{
			number = (String) doc.getClass().getField("ordnumber").get(doc);
		}catch(Exception e){}
		
		name = org.name;

		AgentPrefix pref = AgentPrefix.get();
		torg = pref.name;

		PriceImpl priceImpl = new PriceImpl();

		for (OrderItem item : doc.items) {
			OrderDataPrintItem pi = new OrderDataPrintItem();
			priceImpl.getData().id = item.id;
			priceImpl.read();
			PriceEx price = (PriceEx) priceImpl.getData();

			pi.barcode = price.barcode;
			pi.name = price.name;
			pi.qty = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
			pi.cost = Util.IntToScaleStr(item.cost, Consts.SUM_SCALE,
					Util.DEC_DELIM, false);
			pi.isum = FPOperation
					.itemMul(item.cost, item.qty, Consts.QTY_SCALE);
			pi.sum = Util.IntToScaleStr(item.cost, Consts.SUM_SCALE,
					Util.DEC_DELIM, false);
			totalSum += pi.isum;
			totalQty += item.qty;

			items.add(pi);
		}

		priceImpl.close();
		
		disc = 0;
		
		try{
			disc = doc.getClass().getField("discval").getInt(doc);
		}catch(Exception e){}
		
		sumWDisc = totalSum;
		totalSum -= (int) (((long) totalSum * disc + Consts.SUM_SCALE
				* Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
		sum = Util.IntToScaleStr(totalSum, Consts.SUM_SCALE, Util.DEC_DELIM,
				false);
		sumText = String.format(SUM_TEXT_FORMAT,
				Dig2Str.digToText(totalSum / Consts.SUM_SCALE), totalSum
						% Consts.SUM_SCALE);
	}

	@Override
	public boolean moveNext() {	return false; }

	@Override
	public boolean haveMoreData() { return true; }

	@Override
	public boolean getValue(StringBuilder value, String name, String format) {
		if (value != null && name != null) {
			value.setLength(0);
			ConfigImpl config = new ConfigImpl();

			return config.getValue(value, name)
					|| supplSource.getValue(value, name, format)
					|| SilentReflector
							.getFieldValue(value, name, items, format)
					|| SilentReflector.getFieldValue(value, name, this, format);
		} else
			return false;
	}

	@Override
	public DataSource getObject(String name) {	return items; }

	@Override
	public void calculate() {}

	@Override
	public void startPage() {}
}
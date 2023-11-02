package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.Contact;
import com.grsoft.dataobjects.DeliveryAddress;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.TimeHandler;

public class MainPage extends Activity {

	private static final int DIALOG_DATE_PICKER_ID = 0;
	private static final int DIALOG_TIME_FROM_PICKER_ID = 1;
	private static final int DIALOG_TIME_TILL_PICKER_ID = 2;

//	private ArrayList<KeyValue> priceType = new ArrayList<KeyValue>();
	
	DateHandler dateHandler;
	TimeHandler timeFrom;
	TimeHandler timeTill;
	
	Date from, till;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createorder);
		init();
	}
	
	Date toDate(String timeStr) {
		int h = 0, m = 0;
		Calendar c = Calendar.getInstance();
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		if(timeStr.length() > 0) {
			try {
				String[] parts = timeStr.split(":");
				h = Integer.parseInt(parts[0]);
				if(parts.length > 1)
					m = Integer.parseInt(parts[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		c.set(Calendar.HOUR_OF_DAY, h);
		c.set(Calendar.MINUTE, m);
		
		return c.getTime();
	}

	private void init() {
		OrderImpl order = CreateOrder.currentOrder();
		final OrderEx o = (OrderEx)order.getData();
		
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx) oi.getData();
		oe.id = o.id;
		oi.read();
		String text = oe.name;
		text += "<br/> œœ: " + oe.kpp;
        ((TextView) findViewById(R.id.tvOrgName)).setText(Html.fromHtml(text));

        ConfigImpl config = new ConfigImpl();
		
//		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
//		DialogHelper.loadSpinnerWithKey(config, "¬Ë‰÷ÂÌ˚", priceType, spPrices, o.costCode);

		config.close();
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		remark.setText(o.remark);
		
		from = toDate(o.dlvFrom);
		till = toDate(o.dlvTill);

		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
		timeFrom = new TimeHandler((TextView)findViewById(R.id.tvTimeFrom), from, DIALOG_TIME_FROM_PICKER_ID);
		timeTill = new TimeHandler((TextView)findViewById(R.id.tvTimeTill), till, DIALOG_TIME_TILL_PICKER_ID);
		
		
		int sel = 0;
		List<ContactEx> values = new ArrayList<ContactEx>();
		values.add(new ContactEx(new Contact()));
		for(Contact c : oe.contacts) {
			if(c.name.length() == 0)
				continue;
			
			if(c.name.equals(o.contact))
				sel = values.size();
			ContactEx contEx = new ContactEx(c);
			values.add(contEx);
		}
		Spinner sp = (Spinner)findViewById(R.id.spContact);

		ArrayAdapter<ContactEx> aa = new ArrayAdapter<ContactEx>(this, R.layout.simple_spinner_layout, values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		sp.setAdapter(aa);
		sp.setSelection(sel);
		
		Spinner spDlv = (Spinner)findViewById(R.id.spDlvAdr);
		DialogHelper.loadSpinnerFromDataObject(spDlv, DeliveryAddress.class, new DialogHelper.Selected<DeliveryAddress>() {
			@Override public boolean isSelected(DeliveryAddress da) { return o.dlvAddress.equals(da.address); }
		}, true, "address", "id='" + o.id + "'");
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_DATE_PICKER_ID:
				return dateHandler.createDialog();
			case DIALOG_TIME_FROM_PICKER_ID:
				return timeFrom.createDialog();
			case DIALOG_TIME_TILL_PICKER_ID:
				return timeTill.createDialog();
		}
		return super.onCreateDialog(id);
	}
	
	void update(OrderImpl order) {
		OrderEx o = (OrderEx)order.getData();
		o.date = dateHandler.getDate();
		
		o.dlvFrom = ((TextView)findViewById(R.id.tvTimeFrom)).getText().toString();
		o.dlvTill = ((TextView)findViewById(R.id.tvTimeTill)).getText().toString();
		
		if (o.created == null)
			o.created = new Date();
		
		ContactEx sel = (ContactEx)((Spinner)findViewById(R.id.spContact)).getSelectedItem();
		if(sel != null)
			o.contact = sel.getContact().name;
		
//		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
//		int costType = spPrices.getSelectedItemPosition();
//		o.sumType = costType;
//		o.costCode = priceType.get(costType).key.toString();
		
		EditText remark = (EditText)findViewById(R.id.edCreateOrderNotes);
		o.remark = remark.getText().toString();
		
		DeliveryAddress da = (DeliveryAddress)((Spinner)findViewById(R.id.spDlvAdr)).getSelectedItem();
		if(da != null)
			o.dlvAddress = da.address;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			CreateOrder.checkEmptyOrder();
			finish();
		}
		
		return true;
	}
}

class ContactEx {
	Contact contact;
	public ContactEx(Contact c) {
		contact = c;
	}
	
	@Override
	public String toString() {
		String ret = contact.name;
		if(ret.length() > 0)
			ret += " ÚÂÎ.: " + contact.phone;
		return ret;
	}
	
	public Contact getContact() { return contact; } 
}

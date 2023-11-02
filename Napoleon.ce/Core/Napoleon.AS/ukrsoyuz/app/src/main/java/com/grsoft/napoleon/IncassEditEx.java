package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassDebDistrItem;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class IncassEditEx extends IncassDebDistrEdit {

	Spinner spPayType, spAgents;

	int spInited = 2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnSend.setVisibility(View.GONE);
	}
	
	@Override
	protected int getContentViewID() { return R.layout.incassex; }

	@Override
	protected void childInit(Incass incass, final Org org) {
		super.childInit(incass, org);

		IncassEx ie = (IncassEx) incass;

		ConfigImpl c = new ConfigImpl();
		spPayType = findViewById(R.id.spPayType);
		DialogHelper.loadSpinnerFromConfig(c, "‘ормаќплаты»нкассаци€", new ArrayList<CharSequence>(), spPayType, ie.payType);
		spPayType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(spInited > 0) {
					inited = false;
					spInited--;
				}
				loadDeliveries(org);
			}

			@Override public void onNothingSelected(AdapterView<?> parent) { }
		});

		DbReader r = new DbReader();
		List<DeliveryEx> docs = r.fetch(DeliveryEx.class, "id='" + org.id + "'", "agent");

		ArrayList<CharSequence> data = new ArrayList<CharSequence>();
		data.add("");
		Config cfg = c.getData();
		cfg.key = "ѕриемƒенег";
		c.read();
		DialogHelper.makeList(cfg.value, data);
		c.close();

		for (DeliveryEx de : docs) {
			if(data.contains(de.agent) == false) {
				data.add(de.agent);
			}
		}

		int sel = 0, curI = 0;
		for(CharSequence cs : data) {
			if(cs.equals(ie.agent)) {
				sel = curI;
				break;
			}
			curI++;
		}

		spAgents = findViewById(R.id.spAgent);
		ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(this, R.layout.simple_spinner_layout, data);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spAgents.setAdapter(aa);
		spAgents.setSelection(sel);
		spAgents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(spInited > 0) {
					inited = false;
					spInited--;
				}
				loadDeliveries(org);
			}

			@Override public void onNothingSelected(AdapterView<?> parent) { }
		});
//		DialogHelper.loadSpinnerFromConfig(c, "ѕриемƒенег", data,
//				(Spinner)findViewById(R.id.spAgent), ie.agent);
	}

	@Override
	protected void loadDeliveries(Org o) {
		super.loadDeliveries(o);
		((EditText)findViewById(R.id.edCount)).selectAll();
	}

	@Override
	protected String makeDeliveryWhere(Org o) {
		String ret = super.makeDeliveryWhere(o);
		String pt = spPayType.getSelectedItem().toString();
		String agent = spAgents.getSelectedItem().toString();

		if(pt.length() > 7)
			pt = pt.substring(0, 7);

		ret += " and payType='" + pt + "'";
		if(agent.length() > 0) {
			ret += " and agent='" + agent + "'";
		}
		return ret;
	}

	@Override
	protected void onResume() {
		super.onResume();
		inited = false;
	}

	@Override
	protected ItemsAdapter createAdapter() {
		return new Adapter();
	}

	@Override
	protected Item createItem(Delivery d) {
		return new ItemEx(d);
	}

	@Override
	protected void setDocument() {
		super.setDocument();
		
		CharSequence value = (CharSequence) spPayType.getSelectedItem();
		if( value != null )
			((IncassEx) doc.getData()).payType = value.toString();
		
		value = (CharSequence) spAgents.getSelectedItem();
		
		if( value != null )
			((IncassEx) doc.getData()).agent = value.toString();
	}

	@Override
	protected int getRowLayoutID() {
		return R.layout.incass_row_ex;
	}

	class Adapter extends ItemsAdapter {
		@Override
		protected void childDraw(View view, Item item, int color) {
			TextView tv = view.findViewById(R.id.tvAgent);
			tv.setText(((ItemEx)item).agent);
			tv.setTextColor(color);
		}
	}

	class  ItemEx extends  Item {
		public String agent;

		public ItemEx(Delivery d) {
			super(d);
			agent = ((DeliveryEx)d).agent;
		}
	}
}

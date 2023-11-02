package com.grsoft.napoleon;

import java.util.ArrayList;

import android.os.Bundle;
import android.widget.Spinner;

import com.grsoft.dataobjects.DeliveryMan;
import com.grsoft.dataobjects.DiscountItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class CreateReturnEx extends CreateReturn {

	@Override int getContentViewID() { return R.layout.createreturnex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ReturnEx r = (ReturnEx) doc.getData();
		Org oe = oi.getData();

		ConfigImpl config = new ConfigImpl();

		ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
		ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();

		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "ќрганизаци€", firms,
				spFirma, r.supplyer);

		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		DialogHelper.loadSpinnerFromConfig(config, "¬ид÷ены", priceType,
				spPrices, r.sumType);

		config.getData().key = "ћожно»змен€ть÷ену";
		try {
			if (config.read() && Integer.parseInt(config.getData().value) == 0)
				spPrices.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		config.close();

		DocHelper.loadSpinner(r.dlvman, (Spinner) findViewById(R.id.spDlvMan), DeliveryMan.class);
		DocHelper.prepareSpinners((Spinner) findViewById(R.id.spDogovor),
				(Spinner) findViewById(R.id.spDiscount), 
				((OrgEx)oe).dogovors, r.iddog, r.discid);
	}
	
	@Override
	protected void updateReturn(Return r) {
		super.updateReturn(r);
		
		ReturnEx re = (ReturnEx)r;
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		int suppl = spFirma.getSelectedItemPosition();
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		int costType = spPrices.getSelectedItemPosition();

		if (suppl >= 0)
			re.supplyer = suppl;
		if (costType >= 0)
			re.sumType = costType;

		Spinner sp;
		sp = (Spinner)findViewById(R.id.spDogovor);
		OrgDogovor dg = (OrgDogovor) sp.getSelectedItem();
		if (dg != null)
			re.iddog = dg.id;

		sp = (Spinner) findViewById(R.id.spDlvMan);
		KeyValue val = (KeyValue)sp.getSelectedItem();
		if (val != null)
			re.dlvman = val.key.toString();

		sp = (Spinner)findViewById(R.id.spDiscount);
		DiscountItem di = (DiscountItem)sp.getSelectedItem();
		if (di != null) {
			re.discid = di.id;
			re.discval = di.val;
		}
	}
}

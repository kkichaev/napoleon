package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.DeliveryMan;
import com.grsoft.dataobjects.DiscountItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class CreateSalesEx extends CreateSales {
	private final static int ASK_NUMBER_DLG = R.id.ask_number_dlg_id;

	@Override
	protected int getSalesLayoutId() {
		return R.layout.createsalesex;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		edNumber.setInputType(InputType.TYPE_NULL);

		findViewById(R.id.tvDocNumber).setOnLongClickListener(
				new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						showDialog(ASK_NUMBER_DLG);
						return true;
					}
				});
		Sales sls = salesImpl.getData();
		Org oe = oi.getData();
		SalesEx ob = (SalesEx) sls;

		DocHelper.loadSpinner(ob.dlvman, (Spinner) findViewById(R.id.spDlvMan),
				DeliveryMan.class);

		DocHelper.prepareSpinners((Spinner) findViewById(R.id.spDogovor),
				(Spinner) findViewById(R.id.spDiscount), ((OrgEx) oe).dogovors,
				ob.iddog, ob.discid);

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == ASK_NUMBER_DLG)
			return createAskNumberDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createAskNumberDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.question);
		builder.setView(View.inflate(this, R.layout.ask_number_dlg, null));
		builder.setPositiveButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String n = ((TextView) ((Dialog) dialog)
						.findViewById(R.id.edInput)).getText().toString();

				if (n.length() > 0)
					edNumber.setText(n);
			}
		});

		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == ASK_NUMBER_DLG)
			prepareAskNumberDlg(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}

	private void prepareAskNumberDlg(Dialog dialog) {
		((TextView) dialog.findViewById(R.id.edInput)).setText(edNumber
				.getText());
	}

	@Override
	protected void postOkDone(Sales sales) {
		SalesEx ob = (SalesEx) sales;

		Spinner sp;
		sp = (Spinner) findViewById(R.id.spDogovor);
		OrgDogovor dg = (OrgDogovor) sp.getSelectedItem();
		if (dg != null)
			ob.iddog = dg.id;

		sp = (Spinner) findViewById(R.id.spDlvMan);
		KeyValue val = (KeyValue) sp.getSelectedItem();
		if (val != null)
			ob.dlvman = val.key.toString();

		sp = (Spinner) findViewById(R.id.spDiscount);
		DiscountItem di = (DiscountItem) sp.getSelectedItem();
		if (di != null) {
			ob.discid = di.id;
			ob.discval = di.val;
		}
	}
}

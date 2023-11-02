package com.grsoft.napoleon.dostavka;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DWaybillDocument;
import com.grsoft.dataobjects.DWaybillDocumentItem;
import com.grsoft.dataobjects.DispatchReturnsInfo;
import com.grsoft.dataobjects.DispatchReturnsItem;
import com.grsoft.dataobjects.DriverRouteActions;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DispatchReturnsInfoImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.network.DriverRouteActionsExport;
import com.grsoft.network.ObjectListener;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.KeypadHelper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DShipmentEditEx extends DShipmentEdit {
	public String remark = "";
	TextView tvRemark;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		boolean canEdit = doc.getData().routeItemId.equals(DriverRouteActions.getActiveItemId());
		findViewById(R.id.btnOK).setEnabled(canEdit);
		findViewById(R.id.btnReject).setEnabled(canEdit);
		View v = findViewById(R.id.btnReturnComment);
		v.setEnabled(canEdit);
		v.setOnClickListener(this);
		
		remark = "";
		tvRemark = (TextView)findViewById(R.id.tvReturnRemark);
		tvRemark.setOnClickListener(this);
		DispatchReturnsInfo retdoc = DispatchReturnsInfoImpl.find(doc.getData());
		if(retdoc != null)
			remark = retdoc.remark;
		tvRemark.setText(remark);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v.getId() == R.id.btnReturnComment || v.getId() == R.id.tvReturnRemark) {
			RemarkDlg dlg = new RemarkDlg();
			dlg.show(getFragmentManager(), RemarkDlg.class.getCanonicalName());
		}
	}
	
	static class RemarkDlg extends DialogFragment{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.returncomment);
			final EditText edRemark = new EditText(getActivity());
			edRemark.setHint(R.string.returncommenthint);
			edRemark.setText(((DShipmentEditEx)getActivity()).remark);
			builder.setView(edRemark);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DShipmentEditEx act = (DShipmentEditEx)getActivity(); 
					act.remark = edRemark.getText().toString();
					act.tvRemark.setText(act.remark);
				}
			});
			
			builder.setNegativeButton(R.string.cancel, null);
			return builder.create();
		}
	}
	
	@Override
	protected int getLayoutID() {
		return R.layout.dshipmentedit_ex;
	}
	
	void sendStatus() {
		if(MainEx.MAIN_SERVICE != null) {
			List<ObjectListener> toSend = new ArrayList<ObjectListener>();
			toSend.add(new DriverRouteActionsExport());
			//toSend.add(new OrderProceededExport());
			try {
				MainEx.MAIN_SERVICE.send(toSend, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onAccept() {
		DWaybillDocument wb = doc.getData();
		boolean haveRet = makeAgentReturnDoc();
		DriverRouteActions.setStatus(wb.routeItemId, wb.number, haveRet ? DriverRouteActions.STAUS_DONE_WIITH_RETURNS : DriverRouteActions.STAUS_FINISHED, "");
		sendStatus();
		super.onAccept();
	}
	
	@Override
	public void doReject(String remark) {
		DWaybillDocument wb = doc.getData();
		DriverRouteActions.setStatus(wb.routeItemId, wb.number, DriverRouteActions.STAUS_REJECT, remark);
		makeAgentReturnDoc();
		sendStatus();
		super.doReject(remark);
	}
	
	boolean makeAgentReturnDoc() {
		boolean ret = false;
		DWaybillDocument wb = doc.getData();
		DispatchReturnsInfoImpl retDoc = DispatchReturnsInfoImpl.create(wb, GPSUtilNew.getLastKnownLocation());
		
		for(DWaybillDocumentItem di : wb.items) {
			if(di.outqty < di.inqty) {
				DispatchReturnsItem item = new DispatchReturnsItem();
				item.id = di.id;
				item.qty = di.inqty - di.outqty;
				item.cause = di.cause;
				item.cost = di.cost;
				
				retDoc.getData().items.add(item);
			}
		}
		
		if(retDoc.getData().items.size() > 0 || remark.trim().length() > 0){
			retDoc.getData().remark = remark.trim();
			retDoc.write();
			retDoc.close();
			ret = true;
		} else {
			retDoc.delete();
		}
		
		return ret;
	}
	
	@Override
	protected void onReject() {
		new WBRejectDialog().show(getFragmentManager(), "");
	}
	
	@Override
	protected void changeItemQty(final DWaybillDocumentItem item) {
		final QtyDecorator decorator = new QtyDecorator(item);
		
		InputNumberDlg.open(DShipmentEditEx.this, new InputNumber() {
			
			@Override
			public boolean isValid(int value, Object... params) {
				if(value > item.inqty) {
					Toast.makeText(DShipmentEditEx.this, "Количество больше чем в накладной", Toast.LENGTH_LONG).show();
					return true;
				}
				if(value< item.inqty && decorator.selectedValue().length() == 0) {
					Toast.makeText(DShipmentEditEx.this, "Выберите причину вовзрата", Toast.LENGTH_LONG).show();
					return false;
				}
				return super.isValid(value, params);
			}
			
			@Override
			public void applayInput(int value, Object... params) {
				if(value >= item.inqty) {
					item.outqty = value;
					adapter.notifyDataSetChanged();
					return;
				}
				if(value < item.inqty) {
					item.outqty = value;
					item.cause = item.outqty == item.inqty ? "" : decorator.selectedValue();
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public int getValue() {				
				return item == null ? 0 : item.outqty;
			}
		}, Consts.QTY_SCALE, true, "Ввести количество принятого товара", false, decorator);
	}

	class QtyDecorator implements InputNumberDlg.Decorator {
		DWaybillDocumentItem item;
		Spinner causeSp;
		
		public QtyDecorator(DWaybillDocumentItem item) {
			this.item = item;
		}
		
		@Override public int getContentView() { return R.layout.input_wb_qty; }

		public String selectedValue() {
			KeyValue kv = (KeyValue) causeSp.getSelectedItem();
			return kv == null ? "" : kv.key.toString();
		}
		
		@Override
		public void adjustView(AlertDialog dialog, View view, KeypadHelper nh) {
			causeSp = (Spinner)view.findViewById(R.id.spRetCause);
			ConfigImpl ci = new ConfigImpl();
			DialogHelper.loadSpinnerWithKeyW(ci, "ПричиныВозвратовДоставка", new ArrayList<KeyValue>(), causeSp, item.cause, true);
			ci.close();
			
			PriceImpl price = new PriceImpl();
			
			price.read("id", item.id);
			TextView tv = (TextView) view.findViewById(R.id.tvDiscountInfo);
			tv.setVisibility(View.VISIBLE);
			tv.setText(price.getData().name);
			
			tv = (TextView) view.findViewById(R.id.tvBeforeQty);
			tv.setText(Util.IntToScaleStr(item.inqty, Consts.QTY_SCALE));
		}
		
	}
	
	class WBRejectDialog extends BaseDialogFragment {
		@SuppressLint("InflateParams")
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			final View v = inflater.inflate(R.layout.rej_wb_dialog, null);
			final Spinner sp = (Spinner)v.findViewById(R.id.spRetCause);
			ConfigImpl ci = new ConfigImpl();
			DialogHelper.loadSpinnerWithKeyW(ci, "ПричиныВозвратовДоставка", new ArrayList<KeyValue>(), sp, "", true);
			v.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View arg0) { dismiss(); }
			});
			
			v.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					KeyValue sel = (KeyValue) sp.getSelectedItem();
					if(sel == null || sel.key.toString().length() == 0) {
						Toast.makeText(DShipmentEditEx.this, "Выберите причину вовзарат", Toast.LENGTH_LONG).show();
						return;
					}
					
					dismiss();
					for(DWaybillDocumentItem di : doc.getData().items) {
						di.outqty = 0;
						di.cause = sel.key.toString();
					}
					doReject(sel.value.toString());
					finish();
				}
			});
			return v;
		}
	}
}

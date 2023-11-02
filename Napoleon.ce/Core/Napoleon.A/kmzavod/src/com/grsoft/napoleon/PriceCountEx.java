package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	TextView tvQuant;
	QuantHelper quantHelper = new QuantHelper();
	int error_code = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		edCount.requestFocus();

		tvQuant = (TextView) findViewById(R.id.tvQuant);

		tvQuant.setText(Util.IntToScaleStr(((PriceEx) price.getData()).quant, Consts.QTY_SCALE));

		if (DocType.getCurDoc() == OrderDoc.instance())
			btnOK.setOnClickListener(new BtnOKClickListenet() {
				@Override
				public void onClick(View v) {
					boolean inPack = cbPackets.isChecked();
					int qty = qtyItems;
					qty = fixOrderQty(inPack, qty, price.getData());

					int quant = ((PriceEx) price.getData()).quant;

					if (quant != 0 && (qty % quant) != 0) {
						showDialog(R.id.quant_error_dialog);
						return;
					}

					List<MatrixItem> matrix = AssortmentMatrixAdapterEx.collect(document.getId());
					int sdp = 0;
					Map<String, OrderImplEx.DebugInt> sdpMap = ((OrderImplEx) document).SDP(matrix);

					if (sdpMap.containsKey(price.getData().id)) {
						sdp = sdpMap.get(price.getData().id).value;

						if (qty > sdp * ((PriceEx) price.getData()).godn) {
							showDialog(R.id.sdp_error_dialog);
							return;
						}

						int kdv = ((OrderImplEx) document).KDV().value;
						long rowid = RemnantsImpl.find(document.getId(), Calendar.getInstance().getTime());

						if (rowid != ExtrasConst.INVALID_ROWID) {
							RemnantsImpl rmn = new RemnantsImpl();
							rmn.read(rowid);
							rmn.close();

							int rm = 0;

							for (RemnantItem i : rmn.getData().items)
								if (i.id.equals(price.getData().id)) {
									rm = i.qty;
									break;
								}

							if (qty < sdp * kdv - rm) {
								showDialog(R.id.remn_error_dialog);
								return;
							}
						}
					}

					super.onClick(v);
				}
			});
	}

	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.quant_error_dialog)
			return createQuantErrorDLG();
		else if (id == R.id.sdp_error_dialog)
			return createSDPErrorDLG();
		else if (id == R.id.remn_error_dialog)
			return createREMNErrorDLG();
		else
		return super.onCreateDialog(id);
	}

	private Dialog createREMNErrorDLG() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.warning);
		builder.setTitle(R.string.rmn_error);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new BtnOKClickListenet().onClick(((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE));
			}
		});
		
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		return builder.create();
	}

	private Dialog createSDPErrorDLG() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.warning);
		builder.setTitle(R.string.sdp_error);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new BtnOKClickListenet().onClick(((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE));
			}
		});
		
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		return builder.create();
	}

	private Dialog createQuantErrorDLG() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.warning);
		builder.setTitle(R.string.quant_not_valid);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		return builder.create();
	}

	@Override
	protected void createComplexHistoryView(Price p, LinearLayout ll) {
		final int MONTH_PERIOD = 2;
		Calendar c = Calendar.getInstance();
		Date finish = c.getTime();
		c.add(Calendar.MONTH, -MONTH_PERIOD);
		Date start = c.getTime();
		DatePeriod dp = new DatePeriod(start, finish);

		Map<Date, Map<String, Integer>> deliveries = new HashMap<Date, Map<String, Integer>>();
		Map<Date, Map<String, Integer>> returns = new HashMap<Date, Map<String, Integer>>();
		Map<Date, Map<String, Integer>> remnants = new HashMap<Date, Map<String, Integer>>();

		for (Object d : DeliveryDoc.instance().docList(document.getId(), null, dp)) {
			if (d instanceof DeliveryImpl) {
				DeliveryImpl dlv = (DeliveryImpl) d;
				Date dt = Util.resetTime(dlv.getDate());

				if (!deliveries.containsKey(dt))
					deliveries.put(dt, new HashMap<String, Integer>());

				for (DeliveryItem i : dlv.getData().items) {
					if (i.id.equals(p.id)) {
						Map<String, Integer> val = deliveries.get(dt);

						if (!val.containsKey(i.id))
							val.put(i.id, 0);

						val.put(i.id, val.get(i.id) + i.qty);
					}
				}
			}
		}

		for (Object d : ReturnDoc.instance().docList(document.getId(), null, dp)) {
			if (d instanceof ReturnImpl) {
				ReturnImpl ret = (ReturnImpl) d;
				Date dt = Util.resetTime(ret.getData().created);

				if (!returns.containsKey(dt))
					returns.put(dt, new HashMap<String, Integer>());

				for (OrderItem i : ret.getData().items) {
					if (i.id.equals(p.id)) {
						Map<String, Integer> val = returns.get(dt);

						if (!val.containsKey(i.id))
							val.put(i.id, 0);

						val.put(i.id, val.get(i.id) + i.qty);
					}
				}
			}
		}

		for (Object d : RemnantsDoc.instance().docList(document.getId(), null, dp)) {
			if (d instanceof RemnantsImpl) {
				RemnantsImpl rem = (RemnantsImpl) d;
				Date dt = Util.resetTime(rem.getData().created);

				if (!remnants.containsKey(dt))
					remnants.put(dt, new HashMap<String, Integer>());

				for (RemnantItem i : rem.getData().items) {
					if (i.id.equals(p.id)) {
						Map<String, Integer> val = remnants.get(dt);

						if (!val.containsKey(i.id))
							val.put(i.id, 0);

						val.put(i.id, val.get(i.id) + i.qty);
					}
				}
			}
		}

		Map<Date, Item> map = new HashMap<Date, Item>();

		for (Map.Entry<Date, Map<String, Integer>> en : deliveries.entrySet()) {
			if (!map.containsKey(en.getKey()))
				map.put(en.getKey(), new Item(en.getKey()));

			Item i = map.get(en.getKey());
			i.dlv = "0";

			if (deliveries.get(en.getKey()).containsKey(p.id))
				i.dlv = Util.IntToScaleStr(deliveries.get(en.getKey()).get(p.id), Consts.QTY_SCALE);
		}

		for (Map.Entry<Date, Map<String, Integer>> en : returns.entrySet()) {
			if (!map.containsKey(en.getKey()))
				map.put(en.getKey(), new Item(en.getKey()));

			Item i = map.get(en.getKey());
			i.ret = "0";

			if (returns.get(en.getKey()).containsKey(p.id))
				i.ret = Util.IntToScaleStr(returns.get(en.getKey()).get(p.id), Consts.QTY_SCALE);
		}

		for (Map.Entry<Date, Map<String, Integer>> en : remnants.entrySet()) {
			if (!map.containsKey(en.getKey()))
				map.put(en.getKey(), new Item(en.getKey()));

			Item i = map.get(en.getKey());
			i.remn = "0";

			if (remnants.get(en.getKey()).containsKey(p.id))
				i.remn = Util.IntToScaleStr(remnants.get(en.getKey()).get(p.id), Consts.QTY_SCALE);
		}

		List<Item> list = new ArrayList<Item>(map.values());
		Collections.sort(list, new Comparator<Item>() {

			@Override
			public int compare(Item lhs, Item rhs) {
				return rhs.date.compareTo(lhs.date);
			}
		});

		SimpleDateFormat sf = new SimpleDateFormat("dd.MM", Locale.getDefault());

		for (Item i : list) {
			String text = sf.format(i.date);
			TextView tv = new TextView(this);

			tv.setGravity(Gravity.RIGHT);
			tv.setTextColor(Color.BLACK);
			tv.setPadding(5, 3, 5, 3);

			text += "<br>" + i.remn + "<br>" + i.ret + "<br>" + i.dlv;
			tv.setText(Html.fromHtml(text));

			tv.setLines(getHistoryLines());
			ll.addView(tv);
		}
	}

	private static class Item {
		Date date;
		String remn = "-";
		String ret = "-";
		String dlv = "-";

		public Item(Date date) {
			this.date = date;
		}
	}
}

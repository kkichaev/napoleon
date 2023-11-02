package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.FolderDiscount;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceMaxDiscount;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.FolderDiscountImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceMaxDiscountImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount implements OrderImpl.UpdateQtyHandler {
	int discount = 0;
	int costWOD = 0;
	int orgCost = 0;
	int maxDiscount = -1;
	String selDsc = "";
	int minQty;

	boolean useAddDiscount = false;
	int addDiscount = 0;
	int usePriceCost = 0;
	String selAddDsc = "";

	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}

	List<KeyValue> loadDiscount(ConfigImpl config, String key, Spinner sp, String selItem) {
		List<KeyValue> ret = new ArrayList<KeyValue>();

		Config c = config.getData();
		c.key = key;
		int sel = -1;
		if (config.read()) {
			String split = new String(new char[] { DialogHelper.SEP_SYMBOL });
			for (String kvStr : c.value.split(split)) {
				KeyValue kv = new KeyValue(kvStr);

				if (maxDiscount >= 0) {
					int curDsc = Util.StrToScale(kv.value.toString(), Consts.SUM_SCALE);
					if (curDsc > maxDiscount)
						continue;
				}

				if (kv.key.equals(selItem))
					sel = ret.size();
				ret.add(kv);
			}
		}
		config.close();

		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(sp.getContext(), R.layout.simple_spinner_layout, ret);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		sp.setAdapter(aa);
		if (sel >= 0)
			sp.setSelection(sel);

		return ret;
	}

	@Override
	protected void refreshData() {
		super.refreshData();

		if (document != null) {
			OrderEx oe = (OrderEx) document.getData();
			if (oe != null) {
				PriceEx pe = (PriceEx) price.getData();
				OrgImpl org = new OrgImpl();
				org.read("id", oe.id);

				if (!(pe.nodisc != 0 || ((OrgEx) org.getData()).nodisc != 0)) {
					findViewById(R.id.trDsc).setVisibility(View.VISIBLE);

					((OrderImpl) document).setUpdateQtyHandler(this);

					OrderItemEx oie = (OrderItemEx) oe.findItem(price.getData().id);
					if (oie != null) {
						selDsc = oie.discount;
						discount = oie.dscValue;
						costWOD = oie.costWOD;
						addDiscount = oie.addDscValue;
						usePriceCost = oie.usePriceCost;
						selAddDsc = oie.addDiscount;

						orgCost = oie.costOrg;
						onChangeCost(oie.cost);
					} else {
						costWOD = ((CostStrategyEx) CostStrategy.defaultInstance).getNativeCost(price.getData(),
								document);
						discount = oe.dscValue;
						orgCost = CostStrategy.costWithDiscount(costWOD, discount, Consts.SUM_SCALE);
						selDsc = oe.discount;
						if (discount != 0) {
							onChangeCost(orgCost);
						}
					}

					PriceMaxDiscountImpl pmi = new PriceMaxDiscountImpl();
					PriceMaxDiscount pm = pmi.getData();
					pm.id = price.getData().id;
					if (pmi.read()) {
						maxDiscount = pm.discount;
					} else {
						FolderDiscountImpl fdi = new FolderDiscountImpl();
						FolderDiscount fdsc = fdi.getData();
						fdsc.folderID = price.getData().folderID;
						if (fdi.read()) {
							maxDiscount = fdsc.discount;
						}
						fdi.close();
					}
					pmi.close();

					ConfigImpl config = new ConfigImpl();

					Spinner spDsc = (Spinner) findViewById(R.id.spDsc);
					final List<KeyValue> discounts = loadDiscount(config, "Скидки", spDsc, selDsc);
					spDsc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
							KeyValue sel = discounts.get(arg2);
							discount = Util.StrToScale(sel.value.toString(), Consts.SUM_SCALE);
							selDsc = sel.key.toString();
							setNewCost();
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
						}
					});

					spDsc = (Spinner) findViewById(R.id.spAddDsc);
					final List<KeyValue> add_d = loadDiscount(config, "ДопСкидки", spDsc, selAddDsc);
					if (add_d.size() > 0) {
						useAddDiscount = true;
						findViewById(R.id.llAddDsc).setVisibility(View.VISIBLE);

						final CheckBox cb = (CheckBox) findViewById(R.id.cbCostType);
						cb.setChecked((usePriceCost > 0));
						cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
								setNewCost();
							}
						});

						spDsc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
								KeyValue sel = add_d.get(arg2);
								addDiscount = Util.StrToScale(sel.value.toString(), Consts.SUM_SCALE);
								selAddDsc = sel.key.toString();
								setNewCost();
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
							}
						});

					}

					config.close();
				}
			}
		}
	}

	@Override
	protected void postOnCreate() {
		super.postOnCreate();

		minQty = ((PriceEx) price.getData()).minQty;
	}

	protected void setNewCost() {
		orgCost = CostStrategy.costWithDiscount(costWOD, discount, Consts.SUM_SCALE);
		;
		int newCost = orgCost;
		if (useAddDiscount) {
			boolean usePC = ((CheckBox) findViewById(R.id.cbCostType)).isChecked();
			if (usePC)
				newCost = CostStrategy.costWithDiscount(costWOD, addDiscount, Consts.SUM_SCALE);
			usePriceCost = usePC ? 1 : 0;
		}

		onChangeCost(newCost);
	}

	@Override
	protected void updateCost() {
		if (useAddDiscount) {
			String value = Util.IntToScaleStr(getInputCost(price.getData()), Consts.SUM_SCALE, Util.DEC_DELIM, false);
			TextView tv = (TextView) findViewById(R.id.tvTotalPrice);
			tv.setText(value);

			tv = (TextView) findViewById(R.id.tvPrice);
			tv.setText(Util.IntToScaleStr(orgCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		} else
			super.updateCost();
	}

	@Override
	protected void updateSumTextView() {
		if (useAddDiscount) {
			long sumItems = getSumValue();
			TextView tv = (TextView) findViewById(R.id.tvTotalSum);
			tv.setText(Util.IntToScaleStr(sumItems, Consts.SUM_SCALE, Util.DEC_DELIM, false));

			long count = getCountValue();
			if (cbPackets.isChecked())
				count = count * qtyInPack / Consts.QTY_SCALE;
			sumItems = count * orgCost / Consts.QTY_SCALE;
			tvSum.setText(Util.IntToScaleStr(sumItems, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		} else
			super.updateSumTextView();
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oie = (OrderItemEx) item;
		oie.dscValue = discount;
		oie.costWOD = costWOD;
		oie.discount = selDsc;
		oie.costOrg = orgCost;

		if (useAddDiscount) {
			oie.addDiscount = selAddDsc;
			oie.addDscValue = addDiscount;
			oie.usePriceCost = usePriceCost;
		}
	}

	@Override
	protected void invalidInputValueHandler() {
		Toast.makeText(this, getString(R.string.order_min_qty, Util.IntToScaleStr(minQty, Consts.QTY_SCALE)),
				Toast.LENGTH_SHORT).show();
		edCount.setText(Util.IntToScaleStr((int) minQty, Consts.QTY_SCALE));
		edCount.selectAll();
	}

	@Override
	protected boolean isInputValid(Runnable r) {
		boolean result = true;
		int qty = qtyItems;
		qty = fixOrderQty(cbPackets.isChecked(), qty, price.getData());

		if (minQty > 0)
			result = minQty <= qty;

		return result;
	}
}

package com.grsoft.napoleon;

import java.util.HashMap;
import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.BonusDefImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	
	private static final int LIMIT_QTY = 10;
	int limit;
	int dsc = 0;
	int maxDsc = 0;
	int priceCost = 0;
	int actionQty = 0;
	int giftQty = 0;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void refreshData() {
		super.refreshData();

		int bonusVisible = View.GONE;
		if( document instanceof OrderImpl ) {
			HashMap<String, BonusDef> bonuses = BonusDefImpl.getActiveBonuses(document.getDate());
			BonusDef bd = bonuses.get(price.getData().id);
			if( bd != null ) {
				TextView tvBonus = (TextView)findViewById(R.id.tvBonus);
				String text = Util.IntToScaleStr(bd.qty, Consts.QTY_SCALE);
				tvBonus.setText(text);
				bonusVisible = View.VISIBLE;				
			}

			((OrderImpl) document).setUpdateQtyHandler(this);

			PriceEx p = (PriceEx) price.getData();
			OrderItemEx oie = (OrderItemEx) ((OrderImpl) document).findItem(p.id);
			int sumType = document.getSumType();
			priceCost = oie != null ? oie.costWODsc :
					(p.cost.size() > sumType && sumType >= 0) ? p.cost.get(sumType).cost :
							0;

			boolean editable = document.isEditable();
			if(p.maxDsc > 0 || (oie != null && oie.discount != 0)) {
				setupDiscount(p, oie, editable);
			}

			if(p.actionQty > 0 || (oie !=null && oie.actionGift != 0)) {
				setupAction(p, oie, editable);
			}
			updateSumTextView();
		}
		findViewById(R.id.trBonus).setVisibility(bonusVisible);
		
		limit = ((PriceEx)price.getData()).limit;
		((TextView)findViewById(R.id.tvLimit)).setText(Util.IntToScaleStr(limit, Consts.QTY_SCALE));
	}

	private void setupAction(PriceEx p, OrderItemEx oie, boolean editable) {
		findViewById(R.id.llAction).setVisibility(View.VISIBLE);
		actionQty = p.actionQty;

		giftQty = oie == null ? 0 : oie.actionGift;

		String text = String.format("Возьми %d шт. и получи одну в подарок", actionQty);
		((TextView)findViewById(R.id.action_text)).setText(text);

		EditText ed = findViewById(R.id.action_qty);
		ed.setText(Integer.toString(giftQty));
		ed.setInputType(InputType.TYPE_NULL);

		ed.setEnabled(editable);
		ed.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() != 0) {
					try {
						giftQty = Integer.parseInt(ed.getText().toString());
						String val = String.format("%d", giftQty * (actionQty + 1));
						((EditText)findViewById(R.id.edCount)).setText(val);
						int ndsc = Consts.SUM_SCALE * Consts.SUM_SCALE / (actionQty + 1);
						onDiscountChange(ndsc);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					giftQty = 0;
				}
			}

			@Override
			public void afterTextChanged(Editable s) {}
		});

		CheckBox cb = (CheckBox)findViewById(R.id.apply_action);
		cb.setChecked(giftQty > 0);
		cb.setEnabled(editable);
		enableActionFields(giftQty > 0);
		cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
			onActionChecked(ed, isChecked);
		});
	}

	void enableActionFields(boolean enable) {
		findViewById(R.id.edCount).setEnabled(!enable);
		findViewById(R.id.cbPackets).setEnabled(!enable);
		findViewById(R.id.tvDiscount).setEnabled(!enable);

		findViewById(R.id.action_qty).setEnabled(enable);
	}

	private void onActionChecked(EditText ed, boolean isChecked) {
		enableActionFields(isChecked);

		if(isChecked) {
			((CheckBox)findViewById(R.id.cbPackets)).setChecked(false);
			keypadHelper.setTargetID(R.id.action_qty);
			ed.selectAll();
		} else {
			ed.setText("");
			keypadHelper.setTargetID(R.id.edCount);
			onDiscountChange(0);
		}
	}

	private void setupDiscount(PriceEx p, OrderItemEx oie, boolean editable) {
		maxDsc = p.maxDsc;

		dsc = oie == null ? 0 : oie.discount;
		findViewById(R.id.trDiscount).setVisibility(View.VISIBLE);
		if(editable) {
			findViewById(R.id.tvDiscount).setOnClickListener(v -> {
				changeDiscount();
			});
		}
		updateDiscount();
		if(oie != null && priceVal != oie.cost)
			onChangeCost(oie.cost);
	}

	void onDiscountChange(int newDsc) {
		dsc = newDsc;
		int cost = (int) CostStrategy.costWithDiscount(priceCost, dsc, Consts.SUM_SCALE);
		onChangeCost(cost);
		updateDiscount();
	}

	@Override
	protected long getSum(int count) {
		if(cbPackets != null && cbPackets.isChecked())
			count = (int)((long)count * qtyInPack / Consts.QTY_SCALE);
		return countSum(count);
	}

	long countSum(int qty) {
		long sum = (long) priceCost * qty / Consts.QTY_SCALE;
		if(dsc != 0)
			sum = CostStrategy.costWithDiscount(sum, dsc, Consts.SUM_SCALE);
		return sum;
	}

	void changeDiscount() {
		InputNumberDlg.open(this, new InputNumber() {
			@Override
			public void applayInput(int value, Object... params) {
				if(value < maxDsc) {
					onDiscountChange(value);
				} else {
					Toast.makeText(PriceCountEx.this, "Скидка больше максимальной", Toast.LENGTH_SHORT).show();
				}
			}

			@Override public long getValue() {return dsc;}
		}, Consts.SUM_SCALE, false, "Введите скидку");
	}

	void updateDiscount() {
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(Util.IntToScaleStr(dsc, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setText(ss);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == LIMIT_QTY) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Ошибка");
			b.setMessage("Вы не можете отгрузить меньше лимита");
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		if(document instanceof OrderImpl && ((OrderEx)document.getData()).bonus == 0) {
			int qty = fixOrderQty(cbPackets.isChecked(), qtyItems, price.getData());
			if(qty < limit) {
				showDialog(LIMIT_QTY);
				return false;
			}
		}
		
		return super.isInputValid(r);
	}
	
	@Override
	protected boolean getStartInPack() {
		return ((PriceEx)price.getData()).boxed != 0;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(document instanceof OrderImpl && ((OrderEx)document.getData()).bonus == 0) {
			cbPackets.setEnabled(((PriceEx)price.getData()).boxed == 0);
		}
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oie = (OrderItemEx) item;
		oie.discount = dsc;
		oie.sum = countSum(item.qty);
		oie.costWODsc = priceCost;
		oie.actionGift = giftQty;
	}
}

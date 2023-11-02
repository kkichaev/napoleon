package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.UnitEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	
	ArrayList<UnitEx> units = new ArrayList<UnitEx>();
	UnitEx selected = null;

	int sumDD, prcDD;
	int maxPrcDD = 5;
	int maxDiscount;
	int priceCost;
	int discount;
	int limit;
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected int getQtyInPack(Price p) {
		
		if( selected == null ) {
			String scode = "";
			
			List<UnitItem> units = ((PriceEx)p).units;
			if( document != null && document instanceof OrderImpl ) {
				OrderItemEx oi = (OrderItemEx) ((OrderImpl)document).findItem(p.id);
				if( oi != null )
					scode = oi.unit;
			} else if( units.size() > 0 ) {
				scode = units.get(0).id;
			}
			
			for(UnitItem ui : units ) {
				UnitEx uex = new UnitEx(ui);
				if(ui.id.compareTo(scode) == 0 )
					selected = uex;
	
				this.units.add(uex);
			}
		}
		
		if( selected != null )
			return selected.inpack;

		return super.getQtyInPack(p);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		StringBuilder sb = new StringBuilder();
		ConfigImpl cfg = new ConfigImpl();
		if( cfg.getValue(sb, "МаксСкидка") )
			maxDiscount = Util.StrToScale(sb.toString(), Consts.SUM_SCALE);
		if( maxDiscount == 0 )
			maxDiscount = 5 * Consts.SUM_SCALE;
		
		priceCost = priceVal;

		TextView tv;
		tv = (TextView)findViewById(R.id.tvCost);
		tv.setText(Util.IntToScaleStr(priceCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

		tv = (TextView)findViewById(R.id.tvDiscount);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				DiscountInputDlg.open(PriceCountEx.this, new InputNumber() {
					@Override public int getValue() { return -discount; }
					@Override
					public void applayInput(int value, Object... params) {
						if( value < 0 && value > maxDiscount ) {
							MessageBox.show(PriceCountEx.this, "Ошибка", "Скидка выше максимальной");
							return;
						}
						discount = -value;
						priceVal = priceCost - (int)(((long)priceCost * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
						updateCost();
						updateDicsount();
						updateSumTextView();
					}});
			}
		});
		
		ArrayAdapter<UnitEx> adapter = new ArrayAdapter<UnitEx>(this, R.layout.simple_spinner_layout, units);
		Spinner s = (Spinner)findViewById(R.id.spUnits);
		s.setAdapter(adapter);
		if( selected != null ) {
			s.setSelection(units.indexOf(selected));
			onUnitChanged(selected);
		}

		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { 
				onUnitChanged(units.get(pos));
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		if(document != null && document instanceof OrderImpl && document.getRowid() != ExtrasConst.INVALID_ID ) {
			OrgImpl org = new OrgImpl(); 
			OrderImpl o = (OrderImpl)document;
			OrderItemEx oe = (OrderItemEx) o.findItem(price.getData().id);
			if( oe != null ) {
				sumDD = oe.sumDD;
				prcDD = oe.prcDD;
				discount = oe.discount;
				if( priceVal != oe.cost ) {
					priceVal = oe.cost;
					updateCost();
					updateSumTextView();
				}
			}
			
			OrderEx ord = (OrderEx)o.getData();
			org.getData().id = o.getId();
			org.read();
			for(OrgDogovor od : ((OrgEx)org.getData()).dogovors) {
				if( od.id.equals(ord.dogCode) ) {
					limit = od.limit;
					break;
				}
			}
			org.close();
			
			findViewById(R.id.trSumDD).setVisibility(View.VISIBLE);
			findViewById(R.id.trPrcDD).setVisibility(View.VISIBLE);

			tv = (TextView)findViewById(R.id.tvSumDD);
			tv.setOnClickListener(new View.OnClickListener() {					
				@Override public void onClick(View v) { setSumDD(); }
			});

			tv = (TextView)findViewById(R.id.tvPrcDD);
			tv.setOnClickListener(new View.OnClickListener() {					
				@Override public void onClick(View v) { setPrcDD(); }
			});

			refreshSumDD();
			refreshPrcDD();
		}
		updateDicsount();
	}
	
	@Override
	protected void onChangeCost(int newCost) {
		int checkDiscount = (int)((long)(priceCost - newCost) * 10000 / priceCost);
		if( checkDiscount <= maxDiscount ) {
			discount = checkDiscount;
			updateDicsount();
			super.onChangeCost(newCost);
		} else {
			MessageBox.show(this, "Ошибка", "Скидка больше максимальной");
		}
	}
	
	private void updateDicsount() {
		int val = discount;
		String label = "скидка,%";
		if( val < 0 ) {
			label = "наценка,%";
			val = -val;
		}
		((TextView)findViewById(R.id.tvDiscountLabel)).setText(label);
		
		String value = Util.IntToScaleStr(val, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(value);
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setTextColor(Color.BLUE);
		tv.setText(ss);
	}

	@Override protected boolean getStartInPack() { return true; }
	
	void onUnitChanged(UnitEx newUnit) {
		selected = newUnit;		
		qtyInPack = newUnit.inpack;
		if( qtyInPack == 0 )
			qtyInPack = Consts.QTY_SCALE;

		TextView tvQtyInPack = (TextView) findViewById(R.id.tvQtyInPack);
		tvQtyInPack.setText(Util.IntToScaleStr(qtyInPack, Consts.QTY_SCALE));

		updateSumTextView();
	}
	
	void makePrcAlert(String message, final Runnable run ) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Вопрос");
		b.setMessage(message);
		b.setPositiveButton("Да", new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) { run.run(); }
		});
		b.setNegativeButton("Нет", null);
		b.create().show();		
	}
	
	void inputPrcDD() {
		InputNumberDlg.open(PriceCountEx.this, new InputNumber() {
			
			@Override public int getValue() { return prcDD; }
			
			@Override
			public void applayInput(final int value, Object... params) {
				if( value <= maxPrcDD * Consts.SUM_SCALE ) {
					prcDD = value;
					refreshSumDD();
					refreshPrcDD();
				} else {
					makePrcAlert("Процент превышает максимальный. Продолжать?", new Runnable() {
						
						@Override
						public void run() {
							prcDD = value;
							refreshSumDD();
							refreshPrcDD();							
						}
					});
				}
			}
		}, Consts.SUM_SCALE, false, "Введите процент ДД");
	}
	
	protected void setPrcDD() {
		if( sumDD != 0 ) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Очистить сумму ДД?");
			builder.setTitle("Вопрос");
			builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {				
				@Override public void onClick(DialogInterface dialog, int which) { 
					sumDD = 0;
					inputPrcDD();
				}
			});
			builder.setNegativeButton("Нет", null);
			builder.create().show();
		} else
			inputPrcDD();
	}
	
	void inputSumDD() {
		InputNumberDlg.open(PriceCountEx.this, new InputNumber() {
			
			@Override public int getValue() { return sumDD; }
			
			@Override
			public void applayInput(final int value, Object... params) {
				int sv = value * Consts.SUM_SCALE / (int)getSumValue();
				if( sv <= maxPrcDD ) {
					sumDD = value;
					refreshSumDD();
					refreshPrcDD();
				} else {
					makePrcAlert("Сумма превышает максимально возможную. Продолжать?", new Runnable() {
						
						@Override
						public void run() {
							sumDD = value;
							refreshSumDD();
							refreshPrcDD();							
						}
					});
				}
			}
		}, Consts.SUM_SCALE, false, "Введите сумму ДД");
	}

	protected void setSumDD() {
		if( prcDD != 0 ) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Очистить % ДД?");
			builder.setTitle("Вопрос");
			builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {				
				@Override public void onClick(DialogInterface dialog, int which) { 
					prcDD = 0;
					inputSumDD();
				}
			});
			builder.setNegativeButton("Нет", null);
			builder.create().show();
		} else
			inputSumDD();
	}

	void refreshSumDD() {
		TextView tv = (TextView)findViewById(R.id.tvSumDD);
		SpannableString ss = new SpannableString(Util.IntToScaleStr(sumDD, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setText(ss);
	}
	
	void refreshPrcDD() {
		TextView tv = (TextView)findViewById(R.id.tvPrcDD);
		SpannableString ss = new SpannableString(Util.IntToScaleStr(prcDD, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setText(ss);
	}

//	@Override
//	protected boolean isInputValid(final Runnable r) {
//		if( document == null || !(document instanceof OrderImpl) )
//			return super.isInputValid(r);
//		
//		int sv = getSumValue();
//		int sum = ((OrderImpl)document).sum() + sv;
//		if( sum > limit && sv > 0 ) {
//			AlertDialog.Builder b = new AlertDialog.Builder(this);
//			b.setTitle("Ошибка");
//			b.setMessage("Превышен лимит отгрузки для клиента. Продолжать?");
//			b.setNegativeButton("Нет", null);
//			b.setPositiveButton("Да", new DialogInterface.OnClickListener() {
//				@Override public void onClick(DialogInterface dialog, int which) { r.run(); }
//			});
//			b.create().show();
//			return false;
//		}
//		return true;
//	}
	
	@Override
	protected boolean updateOrder() {
		boolean ret = super.updateOrder();
		
		if( document instanceof OrderImpl ) {
			OrderItemEx oi = (OrderItemEx) ((OrderImpl)document).findItem(price.getData().id);
			if( oi != null ) {
				if(selected != null)
					oi.unit = selected.id;
				oi.sumDD = sumDD;
				oi.prcDD = prcDD;
				oi.discount = discount;
				document.write();
			}
		}

		return ret;
	}
}

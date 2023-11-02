package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Locale;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import com.grsoft.dataobjects.ActCost;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.OffTakeHistory;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	public static final int BASE_COST_TYPE = 0;
	public static final int PROTOCOL_COST_TYPE = 1;
	public static final int ACTION_COST_TYPE = 2;
	
	RestRefresh rr = new RestRefresh();
	
	private int costidx = BASE_COST_TYPE;      ///индекс типа цены с акцией 
	private int costidxorg = BASE_COST_TYPE;   ///индекс типа цены без акции
	
	private CheckBox cbUseAction;
	private TextView tvActioninfo;
	private TextView tvCostType;
	
	private boolean actTimeNow = false; // Есть ли сейчас акция 
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(document != null && isComplexSalesHistory()) {
			View tr = findViewById(R.id.trRestAdd);
			tr.setVisibility(View.VISIBLE);
			
			EditText ed;
			ed = (EditText)findViewById(R.id.edRestWh);
			ed.addTextChangedListener(rr);
			
			ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {			
				@Override public void onFocusChange(View v, boolean hasFocus) {
					if( hasFocus ) {
						keypadHelper.setTargetID(v.getId());
						((EditText)v).selectAll();
					}
				}
			});
			
			ed.setInputType(InputType.TYPE_NULL);
		}
		
		if (document != null && document instanceof OrderImpl){
			((OrderImpl)document).setUpdateQtyHandler(new UpdateQtyHandler() {

				@Override
				public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
					OrderItemEx iex = (OrderItemEx) item;
					iex.costidx = costidx;
					iex.useact = cbUseAction.isChecked() ? 1 : 0;
				}});
		}
		
		updateSumTextView();
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		if(document instanceof OrderImpl) {
			PriceEx pe = (PriceEx) price.getData();
			
			if( pe.pack > Consts.QTY_SCALE) {
				int qty = qtyItems;
				qty = fixOrderQty(cbPackets.isChecked(), qty, price.getData());
	
				if( (qty % pe.pack) != 0 ) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	protected void invalidInputValueHandler() { showDialog(R.id.invalid_pack_value_dlg); }
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case R.id.invalid_pack_value_dlg:
			return createInvalidValueDlg();
		default:
			return super.onCreateDialog(id);
		}
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case R.id.invalid_pack_value_dlg:
			prepareInvalidValueDlg(dialog);
			break;
		default:
		super.onPrepareDialog(id, dialog);
		}
	}
	
	private void prepareInvalidValueDlg(Dialog dialog) {
		PriceEx pe = (PriceEx) price.getData();
		int qty = fixOrderQty(cbPackets.isChecked(), qtyItems, price.getData());
		qty = qty - (qty % pe.pack);
		
		if(dialog instanceof AlertDialog){
			AlertDialog dlg = (AlertDialog)dialog;
			Button btnDec = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
			btnDec.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
			Button btnInc = dlg.getButton(DialogInterface.BUTTON_NEGATIVE);
			btnInc.setText(Util.IntToScaleStr(qty + pe.pack, Consts.QTY_SCALE));
			dlg.setMessage(Html.fromHtml(getString(R.string.PackBreacked, Util.IntToScaleStr(pe.pack, Consts.QTY_SCALE), Util.IntToScaleStr(qtyItems, Consts.QTY_SCALE))));
			
			if(qty <= 0){
				btnDec.setEnabled(false);
			}
		}
	}

	private Dialog createInvalidValueDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle(R.string.Caution);
		builder.setMessage("");
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.IncBntTitle, decPack());
		builder.setNegativeButton(R.string.DecBtnTitle, incPack());
		
		return builder.create();
	}

	private OnClickListener decPack() {
		return new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PriceEx pe = (PriceEx) price.getData();
				int qty = fixOrderQty(cbPackets.isChecked(), qtyItems, price.getData());
				qtyItems = qtyItems - (qty % pe.pack);
				btnOK.performClick();
			}
		};
	}

	private OnClickListener incPack() {
		return new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PriceEx pe = (PriceEx) price.getData();
				int qty = fixOrderQty(cbPackets.isChecked(), qtyItems, price.getData());
				qtyItems = qtyItems - (qty % pe.pack) + pe.pack;
				btnOK.performClick();
			}
		};
	}

	@Override
	protected int getInputCost(Price p) {
		if(document instanceof OrderImpl){
			CostStrategyEx cs= (CostStrategyEx)CostStrategy.defaultInstance;
			
			switch (costidx) {
			case BASE_COST_TYPE:
				return cs.getStdCost(price.getData());
			case PROTOCOL_COST_TYPE:
				return cs.getProtocolCost(price.getData(), document);
			case ACTION_COST_TYPE:
				ActCost ac = cs.getActCost(price.getData(), document); 
				return ac == null ? 0 : ac.cost;
			}
		}
			
		return super.getInputCost(p);
	}
	
	@Override
	protected String getRestText(long rest, RemnantItem ri) {
		if( ri  == null )
			return "";
		
		RemnantItemEx re = (RemnantItemEx)ri;
		
		EditText ed;
		ed = (EditText)findViewById(R.id.edRestWh);
		ed.setText(Util.IntToScaleStr(re.qtyWh, Consts.QTY_SCALE));
		return Util.IntToScaleStr(re.qtyBoard, Consts.QTY_SCALE);
	}
	
	@Override
	protected void updateRest(boolean inPack, int rest, Editable txt) {
			
		int qtyBoard = 0;
		if( txt != null && txt.length() != 0 )
			qtyBoard = Util.StrToScale(txt.toString(), Consts.QTY_SCALE);

		int qtyWh = 0;
		EditText ed = (EditText)findViewById(R.id.edRestWh);
		txt = ed.getText();
		if( txt != null && txt.length() != 0 )
			qtyWh= Util.StrToScale(txt.toString(), Consts.QTY_SCALE);
		
		((RemnantsImplEx)rdoc).updateQty2(price, qtyBoard, qtyWh);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		cbUseAction = (CheckBox) findViewById(R.id.cbUseAction);
		cbUseAction.setVisibility(View.VISIBLE);
		tvActioninfo = (TextView) findViewById(R.id.tvActionInfo);
		tvCostType = (TextView) findViewById(R.id.tvCostType);
				
		if(document instanceof OrderImpl){
			
			PriceEx pe = (PriceEx) price.getData();
			
			if( pe.pack > Consts.QTY_SCALE) {
				findViewById(R.id.trPack).setVisibility(View.VISIBLE);
				TextView tv = (TextView)findViewById(R.id.tvPack);
				tv.setText(Util.IntToScaleStr(pe.pack, Consts.QTY_SCALE));
			}
			
			CostStrategyEx cs= (CostStrategyEx)CostStrategy.defaultInstance;
			ActCost actCost = cs.getActCost(price.getData(), document);
			int orgCost = cs.getProtocolCost(price.getData(), document);
			
			OrderItemEx item = (OrderItemEx)((OrderImpl)document).findItem(price.getData().id);
			
			costidxorg = orgCost > 0 ? PROTOCOL_COST_TYPE : BASE_COST_TYPE;
					
			if(item != null)
				costidx = item.costidx;
			else{
				if(actCost != null){
					costidx = ACTION_COST_TYPE;
					actTimeNow = true;
				}else if ( orgCost > 0 ) 
					costidx = PROTOCOL_COST_TYPE;
				else 
					costidx = BASE_COST_TYPE;
			}
			
			cbUseAction.setVisibility(View.VISIBLE);
			cbUseAction.setChecked(item == null || item.useact == 1);
			cbUseAction.setOnCheckedChangeListener(enableActionCost);
			
			String text = getString(R.string.action_missing);
			
			if(actCost != null)
				text = getString(R.string.action_info, Util.simpleDateFormat.format(actCost.start), Util.simpleDateFormat.format(actCost.end));
			
			tvActioninfo.setText(Html.fromHtml(text));
			updateCost();
		}
	}
	
	
	@Override
	protected void updateCost() {
		updateCostType();
		super.updateCost();
		TextView tv = (TextView)findViewById(R.id.tvPrice);
		tv.setTextColor(getResources().getColor(R.color.blue));
	}

	protected void updateCostType() {
		if(tvCostType != null){
			String cta[] =  getResources().getStringArray(R.array.cost_types);
			String ct = "";
			if(costidx >= 0 && costidx < cta.length)
				ct = cta[costidx];
			
			String cti = getString(R.string.cost_type_info,ct);
			tvCostType.setText(Html.fromHtml(cti));
		}
	}
	
	private OnCheckedChangeListener enableActionCost = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked && actTimeNow)
				costidx = ACTION_COST_TYPE;
			else 
				costidx = costidxorg;
			
			updateCost();
			updateSumTextView();
		}
	};
	
	@Override
	protected TextWatcher getRestUpdateHandler() {
		return rr;
	}
	
	class RestRefresh implements TextWatcher {

		@Override
		public void afterTextChanged(Editable arg0) {
			int rest = 0;
			EditText ed;
			Editable txt;
			
			ed = (EditText)findViewById(R.id.edRest);
			txt = ed.getText();
			if( txt != null && txt.length() != 0 ) {
				rest += Util.StrToScale(txt.toString(), Consts.QTY_SCALE);
			}
			ed = (EditText)findViewById(R.id.edRestWh);
			txt = ed.getText();
			if( txt != null && txt.length() != 0 ) {
				rest += Util.StrToScale(txt.toString(), Consts.QTY_SCALE);
			}

			if( Features.PUT_REST_BEFORE_QTY )
				edCount.setEnabled((txt != null && txt.length() != 0));
			
			if( firstView != null ) {
				OffTakeHistory.Item item = history.updateRest(price.getData().id, rest, null);
				
				SimpleDateFormat sf = new SimpleDateFormat("dd.MM", Locale.getDefault());
				String text = sf.format(item.date);
				text += "<br>" + item.makeText(true);			
				firstView.setText(Html.fromHtml(text));
			}
		}

		@Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

		@Override public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
	}
}

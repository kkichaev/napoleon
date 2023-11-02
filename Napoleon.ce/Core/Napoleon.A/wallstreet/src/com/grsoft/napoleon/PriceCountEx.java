package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
import com.grsoft.dataobjects.MpcyItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgProtocolCost;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.OffTakeHistory;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class PriceCountEx extends PriceCount {
	public static final int BASE_COST_TYPE = 0;
	public static final int PROTOCOL_COST_TYPE = 1;
	public static final int ACTION_COST_TYPE = 2;
	
	RestRefresh rr = new RestRefresh();
	
	private int costidx = BASE_COST_TYPE;      ///индекс типа цены с акцией 
	private int costidxorg = BASE_COST_TYPE;   ///индекс типа цены без акции
	
	OrgProtocolCost orgCost;
	ActCost actCost;
	String actName = "";
	
	private CheckBox cbUseAction;
	private TextView tvActioninfo;
	private TextView tvCostType;
	
	int itemPack = 0;
	
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
			
			
			if( itemPack > Consts.QTY_SCALE) {
				int qty = qtyItems;
				qty = fixOrderQty(cbPackets.isChecked(), qty, price.getData());
	
				if( (qty % itemPack) != 0 ) {
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
		int qty = fixOrderQty(cbPackets.isChecked(), qtyItems, price.getData());
		qty = qty - (qty % itemPack);
		
		if(dialog instanceof AlertDialog){
			AlertDialog dlg = (AlertDialog)dialog;
			Button btnDec = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
			btnDec.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
			Button btnInc = dlg.getButton(DialogInterface.BUTTON_NEGATIVE);
			btnInc.setText(Util.IntToScaleStr(qty + itemPack, Consts.QTY_SCALE));
			dlg.setMessage(Html.fromHtml(getString(R.string.PackBreacked, Util.IntToScaleStr(itemPack, Consts.QTY_SCALE), Util.IntToScaleStr(qtyItems, Consts.QTY_SCALE))));
			
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
				int qty = fixOrderQty(cbPackets.isChecked(), qtyItems, pe);
				qtyItems = qtyItems - (qty % itemPack);
				btnOK.performClick();
			}
		};
	}

	private OnClickListener incPack() {
		return new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PriceEx pe = (PriceEx) price.getData();
				int qty = fixOrderQty(cbPackets.isChecked(), qtyItems, pe);
				qtyItems = qtyItems - (qty % itemPack) + itemPack;
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
				OrgProtocolCost opc = cs.getProtocolCost(price.getData(), document); 
				return (opc == null) ? 0 : opc.cost;
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

			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx) oi.getData(); 
			oe.id = document.getId();
			oi.read();
			oi.close();

			itemPack = pe.pack;
			for(MpcyItem mi : pe.mult)
				if(mi.category.equals(oe.category)) {
					itemPack = mi.pack;
					break;
				}
			
			if( itemPack > Consts.QTY_SCALE) {
				findViewById(R.id.trPack).setVisibility(View.VISIBLE);
				TextView tv = (TextView)findViewById(R.id.tvPack);
				tv.setText(Util.IntToScaleStr(itemPack, Consts.QTY_SCALE));
			}
			
			CostStrategyEx cs= (CostStrategyEx)CostStrategy.defaultInstance;
			actCost = cs.getActCost(pe, document);
			orgCost = cs.getProtocolCost(pe, document);
			
			OrderItemEx item = (OrderItemEx)((OrderImpl)document).findItem(pe.id);
			
			costidxorg = orgCost != null && orgCost.cost > 0 ? PROTOCOL_COST_TYPE : BASE_COST_TYPE;
					
			if(item != null)
				costidx = item.costidx;
			else{
				if(actCost != null){
					costidx = ACTION_COST_TYPE;
					actTimeNow = true;
				}else if ( orgCost != null && orgCost.cost > 0 ) 
					costidx = PROTOCOL_COST_TYPE;
				else 
					costidx = BASE_COST_TYPE;
			}
			
			cbUseAction.setVisibility(View.VISIBLE);
			cbUseAction.setChecked(item == null || item.useact == 1);
			cbUseAction.setOnCheckedChangeListener(enableActionCost);
			
			String text = getString(R.string.action_missing);
			
			if(actCost != null) {
				StringBuilder sb = new StringBuilder();
				ConfigImpl ci = new ConfigImpl();
				ci.getValue(sb, "АкционныеЦены");
				List<KeyValue> values = new ArrayList<KeyValue>();
				int sel = DialogHelper.makeListWithKey(sb.toString(), values, actCost.idCost);
				
				if(sel >= 0)
					actName = values.get(sel).value.toString();
				text = getString(R.string.action_info, 
						Util.simpleDateFormat.format(actCost.start), 
						Util.simpleDateFormat.format(actCost.end),
						actName);
			}
			
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

	static long NO_DATE = 48 * 3600 *  1000;
	protected void updateCostType() {
		if(tvCostType != null){
			String ct = "";
			String period = "";
			if(costidx == PROTOCOL_COST_TYPE && orgCost != null) {
				StringBuilder sb = new StringBuilder();
				ConfigImpl ci = new ConfigImpl();
				ci.getValue(sb, "ПротокольныеЦены");
				List<KeyValue> values = new ArrayList<KeyValue>();
				int sel = DialogHelper.makeListWithKey(sb.toString(), values, orgCost.idCost);
				String costName = "";
				if(sel >= 0)
					costName = values.get(sel).value.toString();
				ct = costName;
				if(orgCost.start.getTime() > NO_DATE)
					period += "<br/>Действует: <font color='blue'>с " + Util.simpleDateFormat.format(orgCost.start);
				if(orgCost.end.getTime() > NO_DATE)
					period += " по " + Util.simpleDateFormat.format(orgCost.end) + "</font>";
			} else if(costidx == ACTION_COST_TYPE && actCost != null) { 
				ct = actName;
				period += "<br/>Действует: <font color='blue'>с " + Util.simpleDateFormat.format(actCost.start);
				period += " по " + Util.simpleDateFormat.format(actCost.end) + "</font>";
			} else if(costidx == BASE_COST_TYPE) {
				ct = "Прейскурант";
			} else {
				String cta[] =  getResources().getStringArray(R.array.cost_types);
				if(costidx >= 0 && costidx < cta.length)
					ct = cta[costidx];
			}
			String cti = getString(R.string.cost_type_info,ct) + period;
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

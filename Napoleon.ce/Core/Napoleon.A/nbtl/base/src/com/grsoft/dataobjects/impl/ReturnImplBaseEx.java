package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnCause;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.InputNumberDlg.Decorator;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.InputNumber;
import com.grsoft.view.KeypadHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public abstract class ReturnImplBaseEx<T extends Return> extends ReturnImplBase<T>{
	private String comment = "";
	private Date expdate;
	private String causeid = ""; 

	public ReturnImplBaseEx() {
		updateQtyHandler = new UpdateQtyHandler() {

			@Override
			public void itemUpdated(OrderItem item, Order order,
					boolean isNewItem) {
				ReturnItemEx itemex = (ReturnItemEx) item;
				itemex.comment = comment;
				itemex.expdate = expdate;
				itemex.causeid = causeid;
			}
		};
	}

	@Override
	public void editItem(final long itemRowid, final Context context) {
		if (!isEditable())
			return;

		final PriceImpl priceImpl = new PriceImpl();
		priceImpl.read(itemRowid);
		priceImpl.close();

		Decorator olddecor = InputNumberDlg.decorator;

		InputNumberDlg.decorator = new Decorator() {

			@Override
			public int getContentView() {
				return R.layout.inputreturndlgex;
			}

			@Override
			public void adjustView(AlertDialog dialog, final View view,
					KeypadHelper nh) {
				CheckBox cbPack = (CheckBox) view.findViewById(R.id.cbPack);
				cbPack.setText(context.getString(R.string.kg));

				Spinner spReturnCause = (Spinner) view.findViewById(R.id.spReturnCause);
				spReturnCause.setAdapter(new SpinnerAdapter() {
					final List<ReturnCause> data = new ArrayList<ReturnCause>();
					{
						DataTraveler.travel(ReturnCause.class, new DataTraveler.Travel<ReturnCause>(){
	
							@Override
							public boolean travel(DataTraveler<ReturnCause> item) {
								data.add(item.data);
								item.data = new ReturnCause();
								return true;
							}}, null);
						
						data.add(0, new ReturnCause());
					}
					
					@Override public void unregisterDataSetObserver(DataSetObserver observer) {}
					
					@Override public void registerDataSetObserver(DataSetObserver observer) {}
					
					@Override public boolean isEmpty() { return false; }
					
					@Override
					public boolean hasStableIds() { return false; }
					
					@Override public int getViewTypeCount() { return 0;	}
					
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						return getDropDownView(position, convertView, parent);
					}
					
					@Override
					public int getItemViewType(int position) { return 0; }
					
					@Override
					public long getItemId(int position) { return 0;	}
					
					@Override public Object getItem(int position) { return data.get(position); }
					
					@Override
					public int getCount() {	return data.size(); }
					
					@Override
					public View getDropDownView(int position, View cnv, ViewGroup parent) {
						if (cnv == null)
							cnv = View.inflate(view.getContext(),  android.R.layout.simple_spinner_item, null);
						
						ReturnCause cause = (ReturnCause)getItem(position);
						((TextView) cnv).setText(cause.agent);
						return cnv;
					}
				});

				ReturnItemEx item = (ReturnItemEx) findItem(priceImpl.data.id);
				DatePicker picker = (DatePicker) view.findViewById(R.id.datePicker);
				Date date = new Date();
				
				if (item != null) {
					EditText edComment = (EditText) view
							.findViewById(R.id.edComment);
					edComment.setText(item.comment);
					date = item.expdate;
					
					int pos = 0;
					
					SpinnerAdapter adapter = spReturnCause.getAdapter(); 
					for(;pos < adapter.getCount(); pos++){
						ReturnCause cause = (ReturnCause) adapter.getItem(pos);
						
						if(cause.id.equals(item.causeid))
							break;
					}
					
					if(pos < adapter.getCount())
						spReturnCause.setSelection(pos, true);
				}
				
				try{
					picker.init(date.getYear() + 1900 , date.getMonth(), date.getDate(), null);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		};

		InputNumberDlg.open(context, new InputNumber() {

			@Override
			public void applayInput(int value, Object... params) {

				if (!isEditable())
					return;

				AlertDialog dlg = (AlertDialog) params[1];
				comment = ((EditText) dlg.findViewById(R.id.edComment))
						.getText().toString();

				int cost = 0;

				if (Features.USE_COST_IN_RETURNS) {
					CostStrategy cs = CostStrategy
							.getInstance(ReturnImpl.class);
					cost = cs.getItemCost(priceImpl.getData(),ReturnImplBaseEx.this);
				}
				
				Spinner spReturnCause = (Spinner) dlg.findViewById(R.id.spReturnCause);
				causeid = ((ReturnCause)spReturnCause.getSelectedItem()).id;
				
				DatePicker picker = (DatePicker) dlg.findViewById(R.id.datePicker);
				expdate = new Date(picker.getYear()-1900, picker.getMonth(), picker.getDayOfMonth());

				if (updateQty(priceImpl, value, cost, (Boolean) params[0])
						&& context instanceof DataSetNotify)
					((DataSetNotify) context).notifyDataSetChanged();

				ReturnDoc.instance().refreshDocSum(data.id);
			}

			@Override
			public int getValue() {
				OrderItem ri = (OrderItem) findItem(priceImpl.data.id);
				return ri == null ? 0 : ri.qty;
			}

			@Override
			public boolean isInpack() {
				OrderItem item = (OrderItem) findItem(priceImpl.getData().id);
				return item == null ? false : item.inPack();
			}

		}, Consts.QTY_SCALE, true, context.getString(R.string.value), true);

		InputNumberDlg.decorator = olddecor;
	}
}

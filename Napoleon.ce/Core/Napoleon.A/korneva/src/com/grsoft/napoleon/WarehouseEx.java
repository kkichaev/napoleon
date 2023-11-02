package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.util.QtyEditor;
import com.grsoft.util.Consts;
import com.grsoft.util.MessageBox;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;

public class WarehouseEx extends WarehouseNew {
	OnClickListener opencard = new OnClickListener() {
		@Override public void onClick(View v) { editItem((Long) v.getTag()); }
	};

	OnClickListener openfolder = new OnClickListener() {
		@Override public void onClick(View v) { adapter.setFolder((Integer) v.getTag()); }
	};

	OnClickListener openqty = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (document instanceof OrderImpl || document instanceof ReturnImpl) {
				@SuppressWarnings("unchecked")
				final OrderImplBase<? extends Order> cdoc = (OrderImplBase<? extends Order>) document;
				if (cdoc.isEditable()) {
					final String priceid = (String) v.getTag();
					InputNumberDlg.Decorator sv = InputNumberDlg.decorator;
					QtyEditor qe = new QtyEditor(cdoc, priceid, adapter); 
					
					PriceImpl pi = new PriceImpl();
					Price p = pi.getData();
					p.id = (String)v.getTag();
					pi.read();
					pi.close();
					
					InputNumberDlg.decorator = new INQDecorator(p.qtyInPack, qe);
					InputNumberDlg.open(WarehouseEx.this, qe, Consts.QTY_SCALE, true, getString(R.string.input_new_qty), true);
					InputNumberDlg.decorator = sv;
				}
			}
		}
	};

	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View result = super.getPriceView(node, convertView);

		TextView tv = (TextView) result.findViewById(R.id.tvPriceItemName);
		tv.setTag(node.getRowid());
		tv.setOnClickListener(opencard);

		price.read(node.getRowid());
		Price p = price.getData();

		tv = (TextView) result.findViewById(R.id.tvClmn1);
		if (tv != null) {
			tv.setTag(p.id);
			tv.setOnClickListener(openqty);
		}

		tv = (TextView) result.findViewById(R.id.tvClmn2);
		if (tv != null) {
			tv.setTag(p.id);
			tv.setOnClickListener(openqty);
		}

		return result;
	}
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if( type == COLUMN_QTY_WH || type == COLUMN_QTY_WH_ORD || type == COLUMN_QTY_ORD) {
			
			int value = (type == COLUMN_QTY_WH_ORD || type== COLUMN_QTY_ORD) ? ((Itemsable)document).getItemQty(price) :
				((Itemsable)document).getItemValue(price);
			if( type == COLUMN_QTY_WH_ORD && value == 0 )
				value = ((Itemsable)document).getItemValue(price);
				
			int qip = price.qtyInPack == 0 ? Consts.QTY_SCALE : price.qtyInPack;
			
			int qf = (value / qip)  * Consts.QTY_SCALE;
			int qr = (int)(value % qip);
			String text = Util.IntToScaleStr(qf, Consts.QTY_SCALE) + " + " + Util.IntToScaleStr(qr, Consts.QTY_SCALE);
			textView.setText(text);
			return;
		}
		super.setTextColumnValue(textView, type, price);
	}

	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		View result = super.getFolderView(node, convertView);
		result.setOnClickListener(openfolder);
		result.setTag(node.id);
		return result;
	}
}

class INQDecorator implements InputNumberDlg.Decorator {

	int inPack;
	QtyEditor editor;
	
	public INQDecorator(int inPack, QtyEditor qe) { this.inPack = inPack; editor = qe; }

	@Override public int getContentView() { return R.layout.inputnumberdlgex; }

	@Override
	public void adjustView(final AlertDialog dialog, final View view, final KeypadHelper nh) {
		view.findViewById(R.id.cbPack).setVisibility(View.GONE);
		
		final EditText edRest = (EditText)view.findViewById(R.id.edRest);
		edRest.setText(Util.IntToScaleStr(editor.getRest(), Consts.QTY_SCALE));
		edRest.setInputType(InputType.TYPE_NULL);
		edRest.setOnFocusChangeListener(new View.OnFocusChangeListener() {			
			@Override public void onFocusChange(View v, boolean hasFocus) {
				if( hasFocus ) {
					nh.setTargetID(R.id.edRest);
					edRest.selectAll();
					view.findViewById(R.id.btnComma).setEnabled(true);
				}
			}
		});

		final EditText edCount = (EditText) view.findViewById(R.id.edCount);
		edCount.selectAll();
		edCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {			
			@Override public void onFocusChange(View v, boolean hasFocus) {
				if( hasFocus ) {
					nh.setTargetID(R.id.edCount);
					edCount.selectAll();
					view.findViewById(R.id.btnComma).setEnabled(false);
				}
			}
		});
		
		view.findViewById(R.id.btnOK).setOnClickListener(new OnClickListenerToNotify() {
			
			@Override
			public void onClick(View arg0) {
				super.onClick(arg0);
				try{
					int value = Util.StrToScale(edCount.getText().toString(), Consts.QTY_SCALE);
					value = (int)((long)value * inPack / Consts.QTY_SCALE);
					value += Util.StrToScale(edRest.getText().toString(), Consts.QTY_SCALE);
					editor.applayInput(value, false);
					dialog.dismiss();
				}
				catch(Exception e){
					edCount.selectAll();
					Context c = arg0.getContext();
					String message = e.getMessage();
					if( message == null )
						message = c.getString(R.string.check_input);
					MessageBox.show(c, c.getString(R.string.error), message);
				}
			}
		});
	}
}

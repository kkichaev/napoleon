package com.grsoft.dataobjects.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
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

public class ReturnImplEx extends ReturnImpl {

	private String comment = "";

	public ReturnImplEx() {
		updateQtyHandler = new UpdateQtyHandler() {

			@Override
			public void itemUpdated(OrderItem item, Order order,
					boolean isNewItem) {
				ReturnItemEx itemex = (ReturnItemEx) item;
				itemex.comment = comment;
			}
		};
	}
	
	@Override
	public void postInit() {
		data.sumType = 0;
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
			public void adjustView(AlertDialog dialog, View view,
					KeypadHelper nh) {
				CheckBox cbPack = (CheckBox) view.findViewById(R.id.cbPack);
				cbPack.setText(context.getString(R.string.kg));

				ReturnItemEx item = (ReturnItemEx) findItem(priceImpl.data.id);
				if (item != null) {
					EditText edComment = (EditText) view
							.findViewById(R.id.edComment);
					edComment.setText(item.comment);
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

				CostStrategy cs = CostStrategy.getInstance(ReturnImpl.class);
				int cost = cs.getItemCost(priceImpl.getData(), ReturnImplEx.this);

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

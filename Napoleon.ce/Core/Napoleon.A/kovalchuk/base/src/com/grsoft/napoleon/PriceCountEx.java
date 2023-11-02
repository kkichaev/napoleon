package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	private EditText edCount2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		edCount2 = (EditText) findViewById(R.id.edCount2);

		edCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					keypadHelper.setTargetID(R.id.edCount);
					edCount.selectAll();
				}
			}
		});

		edCount2.setInputType(InputType.TYPE_NULL);
		edCount2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					keypadHelper.setTargetID(R.id.edCount2);
					edCount2.selectAll();
				}
			}
		});

		if (document instanceof OrderImpl) {
			OrderItemEx item = (OrderItemEx) ((OrderImpl) document)
					.findItem(price.getData().id);
			if (item != null)
				edCount2.setText(Util
						.IntToScaleStr(item.qty2, Consts.QTY_SCALE));

			((OrderImpl) document).setUpdateQtyHandler(new UpdateQtyHandler() {

				@Override
				public void itemUpdated(OrderItem item, Order order,
						boolean isNewItem) {
					try {
						
						String text = edCount2.getText().toString();

						int count = text.length() == 0 
							? 0 
							: Util.StrToScale(text, Consts.QTY_SCALE);
						
						((OrderItemEx) item).qty2 = count;
					} catch (Exception e) {
					}

				}
			});
		}
	}

	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
}

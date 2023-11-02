package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItemEx;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class OrderDetailEx extends OrderDetail {
	
	OrderItemEx curItem = null;
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		View v;
		v = findViewById(R.id.btnOK);
		v.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { applyRemark(); }
		});

		v = findViewById(R.id.btnCancel);
		v.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { removeRemark(); }
		});

		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if( position < doc.getData().items.size() ) {
					View v = findViewById(R.id.llRemark);
					v.setVisibility(View.VISIBLE);

					curItem = (OrderItemEx)doc.getData().items.get(position);
					EditText ed = (EditText)findViewById(R.id.edRemark);
					ed.setText(curItem.remark);
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		curItem = null;
		View v = findViewById(R.id.llRemark);
		v.setVisibility(View.GONE);
		super.onResume();
	}
	
	protected void removeRemark() {
		if( curItem != null ) {
			curItem.remark = "";
			doc.write();
		}
	}

	protected void applyRemark() {
		if( curItem != null ) {
			EditText ed = (EditText)findViewById(R.id.edRemark);
			curItem.remark = ed.getText().toString();
			doc.write();
		}
	}
}

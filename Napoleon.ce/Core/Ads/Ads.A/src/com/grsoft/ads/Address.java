package com.grsoft.ads;

import android.os.Bundle;
import android.widget.EditText;

import com.grsoft.ads.documents.Addressable;
import com.grsoft.ads.documents.OrderItemsDocument;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;

public class Address extends BaseActivity {
	protected OrderItemsDocument<? extends CreateDocDataObject> orderItemsDocument;
	private long rowid;
	public static final String TAB_NAME = "address";
	public static final String TAB_CAPTION = "Адрес";
	private EditText edCity;
	private EditText edStreet;
	private EditText edHouse;
	private EditText edFlat;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address);
		
		orderItemsDocument = ((OrderItemsDocument<? extends CreateDocDataObject>) 
				DocType.getCurDoc().create());
		
		rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, 
				ExtrasConst.INVALID_ID);
		
		edCity = ((EditText)findViewById(R.id.edCity));
		edStreet = ((EditText)findViewById(R.id.edStreet));
		edHouse = ((EditText)findViewById(R.id.edHouse));
		edFlat = ((EditText)findViewById(R.id.edFlat));
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		if (orderItemsDocument != null && orderItemsDocument.read(rowid, false)){
			Addressable address = (Addressable)orderItemsDocument;
			edCity.setText(address.getCity());
			edStreet.setText(address.getStreet());
			edHouse.setText(address.getHouse());
			edFlat.setText(address.getFlat());
		}
		
		if (!orderItemsDocument.isEditable()){
			edCity.setEnabled(false);
			edStreet.setEnabled(false);
			edHouse.setEnabled(false);
			edFlat.setEnabled(false);
		}
			
		orderItemsDocument.close();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (orderItemsDocument != null){
			Addressable address = (Addressable)orderItemsDocument;
			address.setCity(edCity.getText().toString());
			address.setStreet(edStreet.getText().toString());
			address.setHouse(edHouse.getText().toString());
			address.setFlat(edFlat.getText().toString());
		}
		
		orderItemsDocument.write();
		orderItemsDocument.close();
	}
}

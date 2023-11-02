package com.grsoft.napoleon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.grsoft.dataobjects.IOrder;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.DeliveryType.UpdateOrder;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.ExtrasConst;


public class SelfDelivery extends Fragment implements UpdateOrder {
	private EditText edAddress;
	protected CreatableDocument<?> order = null;
	private OrgImpl org = new OrgImpl(); 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result = inflater.inflate(getLayoutID(), container, false);
		
		order = (CreatableDocument<?>) DocType.getCurDoc().create();
		
		inflateView(result);
		initData();
		initView();
		return result;
	}
	
	protected int getLayoutID(){
		return R.layout.self_delivery;
	}
	
	protected void initView() {
		IOrder o = (IOrder) order.getData();
		String address = o.getDlvAddress().trim().length() == 0 ? org.getData().address : o.getDlvAddress().trim();
		edAddress.setText(address);
	}

	protected void initData() {
		order.read(getArguments().getLong(ExtrasConst.DOC_ROW_ID_STR));
		order.close();
		org.read("id", order.getId());
	}

	protected void inflateView(View view){
		edAddress = (EditText) view.findViewById(R.id.edAddress);
	}
	
	@Override
	public boolean checkAndUpdate(IOrder order) {
		boolean result = false;
		String address = edAddress.getText().toString().trim();
		
		if(address.length() > 0){
			order.setDlvAddress(address);
			result = true;
		}
		
		return result;
	}
}

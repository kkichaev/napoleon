package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import com.grsoft.dataobjects.Carrier;
import com.grsoft.dataobjects.IOrder;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.CarrierDelivery.CarrierObserver;
import com.grsoft.napoleon.DeliveryType.UpdateOrder;


public class CarrierDeliveryByAddress extends ClientDelivery implements CarrierObserver, 
	UpdateOrder {
	private CharSequence address[];
	private ImageButton btnCarrierAddr;
	private EditText edCarAdr;
	private ImageButton btnDel;
	
	private DialogInterface.OnClickListener applyCurrierAddr = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) { edCarAdr.setText(address[which]); }
	};
	
	protected int getLayoutID() { return R.layout.carrier_delivery_by_address; }

	@Override
	protected void initData() {
		super.initData();
		
		Bundle bundle = getArguments();
		if(bundle != null){
			CarrierBasket cb = bundle.getParcelable(CarrierDelivery.CARRIER);
			initAddress(cb.carrier);
		}
	}
	
	private void initAddress(Carrier carrier){
		if(carrier != null && carrier.items != null && carrier.items.size() > 0){
			List<String> adr = new ArrayList<String>();
			for(int i = 0; i < carrier.items.size(); i++){
				String a = carrier.items.get(i).address.trim();
				if(a.length() > 0)
					adr.add(a);  
			}
			
			address = new String[adr.size()];
			
			for(int i = 0; i < adr.size(); i++)
				address[i] = adr.get(i);
		}
	}

	@Override
	protected void initView() {
		super.initView();
		btnCarrierAddr.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new DialogFragment(){
					public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setItems(address, applyCurrierAddr);
						return builder.create();
					};
				}.show(getChildFragmentManager(), btnCarrierAddr.getClass().toString());
				
			}
		});
		
		btnDel.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { edCarAdr.setText("");	} });
		
		IOrder o = (IOrder)order.getData();
		String caraddr = o.getCarAddress().trim();
		
		if(caraddr.length() > 0)
			edCarAdr.setText(caraddr);
		else if(address.length > 0)
			edCarAdr.setText(address[0]);
		else{
			
			OrgImpl org = new OrgImpl();
			org.read("id", order.getId());
			edCarAdr.setText(org.getData().address);
		}
	}

	@Override
	protected void inflateView(View view) {
		super.inflateView(view);
		btnCarrierAddr = (ImageButton) view.findViewById(R.id.btnCarrierAddr);
		edCarAdr = (EditText) view.findViewById(R.id.edCarAddr);
		btnDel = (ImageButton) view.findViewById(R.id.btnDel);
	}

	@Override
	public void onChangeCarrier(Carrier carrier) { initAddress(carrier); }

	@Override
	public boolean checkAndUpdate(IOrder order) {
		boolean result = super.checkAndUpdate(order);
		
		String caradr = edCarAdr.getText().toString().trim();
		
		if(caradr.length() > 0){
			order.setCarAddress(caradr);
			result = true;
		}
			
		return result;
	}
}

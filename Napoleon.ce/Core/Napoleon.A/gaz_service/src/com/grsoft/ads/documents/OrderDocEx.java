package com.grsoft.ads.documents;

import android.app.Activity;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;

import com.grsoft.ads.R;
import com.grsoft.ads.SpinnerDataBaseAdapter;
import com.grsoft.ads.dataobjects.OrderEx;
import com.grsoft.ads.dataobjects.impl.CertificateImpl;
import com.grsoft.ads.dataobjects.impl.CounterImpl;
import com.grsoft.ads.dataobjects.impl.OrderImpl;
import com.grsoft.ads.dataobjects.impl.OrderImplEx;
import com.grsoft.ads.dataobjects.impl.ProtocolImpl;
import com.grsoft.dataobjects.CreateDocDataObject;

public class OrderDocEx extends OrderDoc 
implements OrderDataDoc{

	public OrderDocEx(){
		docClass = OrderImplEx.class;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void updateView(Activity activity,
			OrderItemsDocument<? extends CreateDocDataObject> doc) {
		if (activity != null && doc != null){
			Spinner spCounter = ((Spinner) activity.findViewById(R.id.spCounter));
			Spinner spProtocol = ((Spinner) activity.findViewById(R.id.spProtocol));
			Spinner spCeritficate = ((Spinner) activity.findViewById(R.id.spCert));
			EditText edNumCounter = (EditText)activity.findViewById(R.id.edNumCounter);
			EditText edDataCounter = (EditText)activity.findViewById(R.id.edDataCounter);
			EditText edProtocol = (EditText)activity.findViewById(R.id.edProtocol);
			EditText edCert = (EditText)activity.findViewById(R.id.edCert);
			TableRow trProtokolEdit = (TableRow)activity.findViewById(R.id.trProtokolEdit);
			TableRow trProtokolShow = (TableRow)activity.findViewById(R.id.trProtokolShow);
			TableRow trCertEdit = (TableRow)activity.findViewById(R.id.trCertEdit);
			TableRow trCertShow = (TableRow)activity.findViewById(R.id.trCertShow);
			
			boolean editable = ((OrderImpl)doc).isEditable();
			
			OrderEx order = (OrderEx) doc.getData();
			
			spCounter.setAdapter(SpinnerDataBaseAdapter
					.create(activity, new CounterImpl(), "name", "name", ""));
			
			if (editable){
				trProtokolShow.setVisibility(View.GONE);
				trCertShow.setVisibility(View.GONE);
				trProtokolEdit.setVisibility(View.VISIBLE);
				trCertEdit.setVisibility(View.VISIBLE);
				
				spCeritficate.setAdapter(SpinnerDataBaseAdapter
						.create(activity, new CertificateImpl(), "number", "number", "writeof=0"));
				
				spProtocol.setAdapter(SpinnerDataBaseAdapter
						.create(activity, new ProtocolImpl(), "number", "number", "writeof=0"));
				
				spProtocol.setSelection(((SpinnerDataBaseAdapter)spProtocol.getAdapter())
						.getItemPosition(order.protocol), true);
				spCeritficate.setSelection(((SpinnerDataBaseAdapter)spCeritficate.getAdapter())
						.getItemPosition(order.certificate), true);
				
			}else{
				trProtokolEdit.setVisibility(View.GONE);
				trCertEdit.setVisibility(View.GONE);
				trProtokolShow.setVisibility(View.VISIBLE);
				trCertShow.setVisibility(View.VISIBLE);
				edProtocol.setText(order.protocol);
				edCert.setText(order.certificate);
				edProtocol.setEnabled(false);
				edCert.setEnabled(false);
				
				spCounter.setEnabled(false);
				spProtocol.setEnabled(false);
				spCeritficate.setEnabled(false);
				edNumCounter.setEnabled(false);
				edDataCounter.setEnabled(false);
			}
			
			spCounter.setSelection(((SpinnerDataBaseAdapter)spCounter.getAdapter())
					.getItemPosition(order.counter), true);
			edNumCounter.setText(order.numctr);
			edDataCounter.setText(Integer.toString(order.datactr));
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean updateDoc(Activity activity,
			OrderItemsDocument<? extends CreateDocDataObject> doc) {
		boolean result = false;
		
		if (activity != null && doc != null 
				&& ((OrderImpl)doc).isEditable()){
			Spinner spCounter = ((Spinner) activity.findViewById(R.id.spCounter));
			Spinner spProtocol = ((Spinner) activity.findViewById(R.id.spProtocol));
			Spinner spCeritficate = ((Spinner) activity.findViewById(R.id.spCert));
			
			OrderEx order = (OrderEx) doc.getData();
			
			order.counter = (((SpinnerDataBaseAdapter)spCounter.getAdapter())
				.getData(spCounter.getSelectedItemPosition()));
			order.numctr = ((EditText)activity.findViewById(R.id.edNumCounter))
					.getText().toString();
			order.protocol = (((SpinnerDataBaseAdapter)spProtocol.getAdapter())
					.getData(spProtocol.getSelectedItemPosition()));
			order.certificate = (((SpinnerDataBaseAdapter)spCeritficate.getAdapter())
					.getData(spCeritficate.getSelectedItemPosition()));
			order.datactr = Integer.parseInt(((EditText)activity.findViewById(R.id.edDataCounter))
					.getText().toString());
			
			result = true;
		}
		
		return result;
	}

	@Override
	public int getDataLayout() {
		return R.layout.orderdata;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void closeAdapters(Activity activity) {
		Spinner spCounter = ((Spinner) activity.findViewById(R.id.spCounter));
		Spinner spProtocol = ((Spinner) activity.findViewById(R.id.spProtocol));
		Spinner spCeritficate = ((Spinner) activity.findViewById(R.id.spCert));
		
		((SpinnerDataBaseAdapter)spCounter.getAdapter()).close();
		
		Adapter a = spProtocol.getAdapter();
		
		if (a != null)
			((SpinnerDataBaseAdapter)a).close();
		
		a = spCeritficate.getAdapter();
		
		if (a != null)
			((SpinnerDataBaseAdapter)a).close();
	}

}

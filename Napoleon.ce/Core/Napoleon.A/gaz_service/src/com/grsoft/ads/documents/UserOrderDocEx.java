package com.grsoft.ads.documents;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;

import com.grsoft.ads.R;
import com.grsoft.ads.SpinnerDataBaseAdapter;
import com.grsoft.ads.UserOrderEditEx;
import com.grsoft.ads.dataobjects.Order;
import com.grsoft.ads.dataobjects.UserOrderEx;
import com.grsoft.ads.dataobjects.impl.CertificateImpl;
import com.grsoft.ads.dataobjects.impl.CounterImpl;
import com.grsoft.ads.dataobjects.impl.ProtocolImpl;
import com.grsoft.ads.dataobjects.impl.UserOrderImpl;
import com.grsoft.ads.dataobjects.impl.UserOrderImplEx;
import com.grsoft.ads.dataobjects.impl.WorkTypeImpl;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.DataBaseAdapter;

public class UserOrderDocEx extends UserOrderDoc 
	implements OrderDataDoc{
	
	public UserOrderDocEx(){
		docClass = UserOrderImplEx.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateView(Activity activity, OrderItemsDocument<? extends CreateDocDataObject> doc) {
		if (activity != null && doc != null){
			Spinner spWorkType = ((Spinner) activity.findViewById(R.id.spWorkType));
			Spinner spCounter = ((Spinner) activity.findViewById(R.id.spCounter));
			Spinner spProtocol = ((Spinner) activity.findViewById(R.id.spProtocol));
			Spinner spCeritficate = ((Spinner) activity.findViewById(R.id.spCert));
			EditText edContact = (EditText)activity.findViewById(R.id.edContact);
			EditText edNumCounter = (EditText)activity.findViewById(R.id.edNumCounter);
			EditText edDataCounter = (EditText)activity.findViewById(R.id.edDataCounter);
			EditText edPhone = (EditText)activity.findViewById(R.id.edPhone);
			EditText edProtocol = (EditText)activity.findViewById(R.id.edProtocol);
			EditText edCert = (EditText)activity.findViewById(R.id.edCert);
			TableRow trProtokolEdit = (TableRow)activity.findViewById(R.id.trProtokolEdit);
			TableRow trProtokolShow = (TableRow)activity.findViewById(R.id.trProtokolShow);
			TableRow trCertEdit = (TableRow)activity.findViewById(R.id.trCertEdit);
			TableRow trCertShow = (TableRow)activity.findViewById(R.id.trCertShow);
			
			boolean editable = ((UserOrderImpl)doc).isEditable();
			
			UserOrderEx order = (UserOrderEx) doc.getData();
			
			spWorkType.setAdapter(SpinnerDataBaseAdapter
					.create(activity, new WorkTypeImpl(), "name", "id", ""));
			spWorkType.setSelection(((SpinnerDataBaseAdapter)spWorkType.getAdapter())
					.getItemPosition(order.worktype), true);
			
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
				
				spWorkType.setEnabled(false);
				spCounter.setEnabled(false);
				spProtocol.setEnabled(false);
				spCeritficate.setEnabled(false);
				edContact.setEnabled(false);
				edNumCounter.setEnabled(false);
				edDataCounter.setEnabled(false);
				edPhone.setEnabled(false);
			}
			
			
			edContact.setText(order.contact);
			spCounter.setSelection(((SpinnerDataBaseAdapter)spCounter.getAdapter())
					.getItemPosition(order.counter), true);
			edNumCounter.setText(order.numctr);
			edDataCounter.setText(Integer.toString(order.datactr));
			edPhone.setText(order.phone);
		}
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean updateDoc(Activity activity, 
			OrderItemsDocument<? extends CreateDocDataObject> doc) {
		boolean result = false;
		
		if (activity != null && doc != null && ((UserOrderImpl)doc).isEditable()){
			Spinner spWorkType = ((Spinner) activity.findViewById(R.id.spWorkType));
			Spinner spCounter = ((Spinner) activity.findViewById(R.id.spCounter));
			Spinner spProtocol = ((Spinner) activity.findViewById(R.id.spProtocol));
			Spinner spCeritficate = ((Spinner) activity.findViewById(R.id.spCert));
			
			UserOrderEx order = (UserOrderEx) doc.getData();
			
			order.worktype = (((SpinnerDataBaseAdapter)spWorkType.getAdapter())
					.getData(spWorkType.getSelectedItemPosition())); 
			order.contact = (((EditText)activity.findViewById(R.id.edContact))
					.getText().toString());
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
			order.phone = ((EditText)activity.findViewById(R.id.edPhone))
					.getText().toString();
			
			result = true;
		}
		
		return result;
	}

	@Override
	public int getDataLayout() {
		return R.layout.userorderdata;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void closeAdapters(Activity activity) {
		Spinner spWorkType = ((Spinner) activity.findViewById(R.id.spWorkType));
		Spinner spCounter = ((Spinner) activity.findViewById(R.id.spCounter));
		Spinner spProtocol = ((Spinner) activity.findViewById(R.id.spProtocol));
		Spinner spCeritficate = ((Spinner) activity.findViewById(R.id.spCert));
		
		((SpinnerDataBaseAdapter)spWorkType.getAdapter()).close();
		((SpinnerDataBaseAdapter)spCounter.getAdapter()).close();
		
		Adapter a = spCeritficate.getAdapter();
		
		if (a != null)
			((SpinnerDataBaseAdapter)a).close();
		
		a = spProtocol.getAdapter();
		
		if (a != null)
			((SpinnerDataBaseAdapter)a).close();
	}
	
	@Override
	public Class<?> getSummary() {
		return UserOrderEditEx.class;
	}
	
	@Override
	public DataBaseAdapter<? extends DataObject> createAdapter(Context context, 
			LinesCountController linesControlles) {
		UserOrderAdapter result = null;
		try{
			result = new UserOrderAdapterEx(context, linesControlles);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DocExportListener getDirtyDocuments() {
		CreatableDocument<?> d = (CreatableDocument<?>)create();
		
		String where = "(([params] & " + Order.DONE_PARAMS + " ) = " + Order.DONE_PARAMS +") and " + 
				"(([params] & " + ParamState.ofExported + " ) = 0)";
		
		DocList docList =  new DocList((Class<? extends CreatableDocument<?>>)d.getClass(), where, null);
		return new DocSendListner(OBJ_NAME, docList);
	}
}

class UserOrderAdapterEx extends UserOrderAdapter{

	public UserOrderAdapterEx(Context context,
			LinesCountController linesController)
			throws IllegalAccessException, InstantiationException {
		super(context, linesController);
	}
	
	@Override
	protected void setBackground(View convertView, UserOrderImpl userOrderImpl) {
		if(convertView != null && userOrderImpl != null){
			UserOrderImplEx implex = (UserOrderImplEx)userOrderImpl;
			
			if (implex.isDone())
				convertView.setBackgroundResource(R.drawable.list_grey_selector);
			else if (implex.isDoing())
				convertView.setBackgroundResource(R.drawable.list_yellow_selector);
		}
	}
	
}

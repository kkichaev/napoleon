package com.grsoft.napoleon;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.network.DocExportListener;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class IncassDebDistrEditEx extends IncassDebDistrEdit {
	protected int getRowLayoutID() { return R.layout.incass_deb_distr_row_ex; }
	
	protected ItemsAdapter createAdapter() { return new ItemsAdapter(){
		private DeliveryImpl delivery = new DeliveryImpl();
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			view = super.getView(position, view, parent);
			
			Item i = (Item) getItem(position);
			delivery.getData().number = i.dlv.number;
			delivery.getData().id = doc.getId();
			delivery.read();
			delivery.close();
			
			TextView tv = (TextView) view.findViewById(R.id.tvAgent);
			tv.setText(((DeliveryEx)delivery.getData()).agent);
			tv.setTextColor(i.dlv.color);
			
			return view;
		}
		
	}; }

	@Override
	protected void send() {
		setDocument();
		if(save()) {
			List<DocExportListener> sends = DocType.getDocuments(true, true);
			new DocumentSender(this, findViewById(R.id.btnSend), sends, this).execute((Void[]) null);
		}
	}
}

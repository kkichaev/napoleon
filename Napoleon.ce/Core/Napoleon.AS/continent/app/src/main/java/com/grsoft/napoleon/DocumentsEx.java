package com.grsoft.napoleon;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Requestdoc;
import com.grsoft.dataobjects.impl.RequestdocImpl;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.GpsCoord;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

public class DocumentsEx extends Documents {
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (DocType.getCurDoc() == DebtDocEx.instance())
			getMenuInflater().inflate(R.menu.delivery_menu, menu);
		else
			super.onCreateContextMenu(menu, v, menuInfo);
		
		if (DocType.getCurDoc() == OrderDoc.instance()) {
			menu.add(android.view.Menu.NONE, R.id.itReqUPD, android.view.Menu.NONE, R.string.request_upd);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itReqUPD) {
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
			Document<?> doc = (Document<?>) adapter.getItem(menuInfo.position);
			String number = "";
			
			if (doc.getData() instanceof Delivery) {
				number = requestUPD((Delivery) doc.getData());
				
			}else if (doc.getData() instanceof Order) {
				number = requestUPD((Order) doc.getData());
			}
			
			if (number.length() > 0)
				Toast.makeText(this, String.format("Запрос УПД с номером: %s создан", number), Toast.LENGTH_SHORT).show();
				
			return true;
		}else
			return super.onContextItemSelected(item);
	}

	private String requestUPD(Delivery dlv) {
		RequestdocImpl request = new RequestdocImpl();
		request.init(this, dlv.id, GpsCoord.empty);
		request.getData().number = dlv.number;
		request.getData().type = Requestdoc.UPD_TYPE;
		request.write();
		request.close();
		
		return dlv.number;
	}
	
	private String requestUPD(Order ord) {
		RequestdocImpl request = new RequestdocImpl();
		request.init(this, ord.id, GpsCoord.empty);
		request.getData().number = ord.number;
		request.getData().type = Requestdoc.UPD_TYPE;
		request.write();
		request.close();
		
		return ord.number;
	}
}

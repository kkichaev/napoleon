package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.DocHandleStatus;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItemDlv;
import com.grsoft.dataobjects.ReturnItemEx;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class OrderHelper {
	static public void updateOrderInfo(TextView tv, OrderEx order) {
		String text = "";
		
		if(order.docStatus == DocHandleStatus.FAIL) {
			text = "Ошибка при записи " + order.docMessage;
		} else {
			if( order.ordNumber.length() > 0 ) {
				text += "№ заказа <b>" + order.ordNumber + "</b>";
				if( order.docStatus == DocHandleStatus.SAVED ) { 
					text += " <i>заказ не проведен!</i>";// + order.docMessage + "<br/>";
				} 
				text += "<br/>";
			}
			if( order.docStatus == DocHandleStatus.HANDLED) {
				if( order.number.length() > 0 ) {
					text += "№ накладной <b>" + order.number + "</b>";			
				} else {
					text += "<i>накладная не проведена!</i>";// + order.docMessage;
				}
				text += "<br/>";
			}
		}
		
		if( text.length() > 0 ) {
			tv.setVisibility(View.VISIBLE);
			tv.setText(Html.fromHtml(text));
		} else
			tv.setVisibility(View.GONE);
	}
	
	static int countRetQty(Delivery d, DeliveryItemEx item, List<Return> retDocs) {
		int qty = item.retQty;
		for(Return doc : retDocs) {
			for(OrderItem docitem : doc.items) {
				ReturnItemEx reitem = (ReturnItemEx)docitem;
				for(ReturnItemDlv rditem : reitem.items) {
					if( rditem.number.equals(d.number)) {
						qty -= rditem.qty;
					}
				}
			}
		}
		return qty;
	}
	
	static List<Return> loadRetDocs(String orgId, final Return excluded) {
		final List<Return> rdocs = new ArrayList<Return>();
		DataTraveler.travel(Return.class, new DataTraveler.Travel<Return>() {

			@Override
			public boolean travel(DataTraveler<Return> item) {
				if(excluded == null || !item.data.created.equals(excluded.created)) {
					rdocs.add(item.data);
					item.data = new Return(); 
				}
				return true;
			}
		}, "id='"+orgId+"' and params=0");
		
		return rdocs;
	}
}

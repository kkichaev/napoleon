package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.ExtrasConst;

import android.text.Html;
import android.widget.TextView;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void updateTotalSum() {
		
		if (doc instanceof OrderImplEx) {
			String s = DocType.getCurDoc().getTotalSumStr(this, doc.sum(), doc.weight(), 
					((CfgNplW)ConfigManager.getConfig()).isPackView ? doc.countPack() : doc.count());
			
			StringBuilder sb = new StringBuilder();
			sb.append("<i>");
			sb.append(getString(R.string.sku_qty, ((OrderImplEx)doc).SkuCount()));
			sb.append(",</i> ");
			sb.append(s);
			
			TextView tv = (TextView) findViewById(R.id.tvTotalSum);
			tv.setText(Html.fromHtml(sb.toString()));
		}else
			super.updateTotalSum();
	}
	
	@Override
	public void send() {
		List<DocExportListener> docs = new ArrayList<DocExportListener>();
		
		docs.add(new DocSendListner(docType.getObjectName(), doc, doc.getRowid()));
		
		List<Long> ids = findIncass(doc.getId());
		
		for(long rid : ids)
			if( rid != ExtrasConst.INVALID_ROWID) {
				IncassImpl ri = new IncassImpl();
				ri.read(rid);
				if(ri.isExported() == false)
					docs.add(new DocSendListner(IncassDoc.instance().getObjectName(), ri, ri.getRowid()));
				ri.close();
			}
		
		new DocumentSender(OrderDetailEx.this, btnSend, docs).execute((Void[])null);
	}
	
	List<Long> findIncass(String id){
		String tn = DataObjectInfo.getInstance().getTableName(Incass.class);
		String condition = String.format("id='%s'", id);
		DbWriter.checkDBTable(Incass.class);
		
		return DbReader.readIds(tn, condition, null);
	}
}

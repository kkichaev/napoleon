package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.napoleon.ReturnDetail;
import com.grsoft.napoleon.ReturnPriceCount;
import com.grsoft.napoleon.ReturnProperties;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.GpsCoord;

public class ReturnImplEx extends OrderImplBase<ReturnEx> {

	@Override public CreatableDocument<ReturnEx> createInstance() { return new ReturnImplEx(); }

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		ReturnProperties.open(ctx, this, isOldOrder);
	}
	
	@Override protected DocType getDocumentType() { return ReturnDoc.instance(); }

	@Override
	public void open(Context context) {
		ReturnDetail.open(context, this);
	}

	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this);
	}

	@Override protected boolean checkPriceQty() { return false; }
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		data.retNum = makeDocNumber();
		return super.init(context, orgId, coord);
	}
	
	private String makeDocNumber() {
		String prefix = "";
		
		Config config = ConfigManager.getConfig();
		AgentPrefix ap = new AgentPrefix();
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(ap.getClass());
		boolean bdo = r.select(ap, table, "login='" + config.login + "' and password='" + config.passw + "'" );
		if( bdo )
			prefix = ap.prefix;
		r.close();

		int num = 1;
		ReturnEx ri = new ReturnEx();
		table = DataObjectInfo.getInstance().getTableName(ri.getClass());
		bdo = r.select(ri, table, null, "created desc");
		while( bdo ) {
			if( ri.retNum.length() == 0 ) {
				bdo = r.selectNext(ri);
				continue;
			}
			try {
				StringBuilder lastnum = new StringBuilder();
				for(char sym : ri.retNum.toCharArray()) {
					if( Character.isDigit(sym) )
						lastnum.append(sym);
				}
				
				if( lastnum.length() > 0 )
					num = Integer.parseInt(lastnum.toString()) + 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		r.close();
		return String.format("%s%04d", prefix, num);
	}
}

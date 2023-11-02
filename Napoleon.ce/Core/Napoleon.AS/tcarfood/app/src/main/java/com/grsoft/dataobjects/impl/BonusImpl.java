package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Bonus;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.napoleon.BonusDetail;
import com.grsoft.napoleon.BonusPriceCount;
import com.grsoft.napoleon.BonusProperties;
import com.grsoft.napoleon.documents.BonusDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.GpsCoord;

public class BonusImpl extends OrderImplBase<Bonus> {

	@Override
	public CreatableDocument<Bonus> createInstance() {
		return new BonusImpl();
	}

	@Override
	public DocType getDocumentType() {
		return BonusDoc.instance();
	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		BonusProperties.open(ctx, this, isOldOrder);
	}

	@Override
	public void open(Context context) {
		BonusDetail.open(context, this);
	}

	@Override
	public void editItem(long itemRowid, Context context) {
		BonusPriceCount.open(context, itemRowid, this);
	}

	public static BonusImpl initFromOrder(OrderImpl doc) {
		Order order = doc.getData();

		BonusImpl result = new BonusImpl();
		Bonus bonus = result.getData();

		DbReader reader = new DbReader();
		StringBuilder sql = new StringBuilder();
		sql.append("[order]=").append(order.created.getTime());
		
		if (!reader.select(bonus, 
				DataObjectInfo.getInstance().getTableName(Bonus.class), sql.toString())) {
			result.initSilent(order.id, new GpsCoord(order.latitude, order.longitude, order.stltime));
			bonus.order = order.created;
			bonus.sumType = order.sumType;
			bonus.order = order.created;
			bonus.ordersum = (int)doc.sum();
			result.write();
			result.close();
		}
		else
			result.rowid = result.getData().created.getTime();

		reader.close();
		
		return result;
	}

}

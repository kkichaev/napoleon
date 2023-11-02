/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Объект данных Delivery для работы с базой
 *
 * kki   29/11/2010   creating
 */

package com.grsoft.dataobjects.impl;
import com.grsoft.aceteam.R;

import java.util.Date;

import android.content.Context;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.napoleon.DeliveryDetail;
import com.grsoft.util.Consts;

public class DeliveryImpl extends DeliveryImplBase<Delivery>
{
	public interface DataCreator { public Delivery create(); }
	
	@Override
	public long sum() { return data.sum(); }

	@Override
	public Date getDate() { return data.date; }

	@Override
	public String getDescription(Context context) { return data.number; }

	@Override
	public String getId() { return data.id;	}

	@Override
	public void open(Context context) { 
		DeliveryDetail.open(context, this); 
	}
	
	public int qty(){
		int result = 0;
		
		if( data.items != null ) {
			for(DeliveryItem item : data.items)
				result += item.qty;
		}
		return result / Consts.QTY_SCALE;
	}
}

package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.dataobjects.Order;
import com.grsoft.napoleon.documents.CreatableDocument;

public class OrderImplEx extends OrderImpl {
	@Override
	protected void postCopyProcess(CreatableDocument<Order> copy) {
		super.postCopyProcess(copy);
		copy.getData().date = new Date(copy.getData().date.getTime() + 24 * 3600 * 1000);
	}
}

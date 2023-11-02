/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Объект данных Payment(Оплаты)
 * для работы с базой
 *
 * kki   28/02/2011   creating
 */
package com.grsoft.dataobjects.impl;

import java.util.Date;

import android.content.Context;

import com.grsoft.dataobjects.Payment;
import com.grsoft.napoleon.documents.Document;

public class PaymentImpl extends Document<Payment>
{
	@Override
	public long sum() { return data.sum;	}

	@Override
	public Date getDate() { return data.date; }

	@Override public String getNumber() { return data.number; }

	@Override
	public String getDescription(Context context) { return data.number; }

	@Override
	public String getId() { return data.id;	}

	@Override
	public void open(Context context) {}
}

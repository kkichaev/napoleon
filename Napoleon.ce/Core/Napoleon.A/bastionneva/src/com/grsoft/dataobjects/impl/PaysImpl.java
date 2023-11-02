package com.grsoft.dataobjects.impl;

import java.util.Date;

import android.content.Context;

import com.grsoft.dataobjects.Pays;
import com.grsoft.napoleon.documents.Document;

public class PaysImpl extends Document<Pays> {

	@Override
	public long sum() { return data.sum;	}

	@Override
	public Date getDate() { return data.date; }

	@Override
	public String getDescription(Context context) { return data.number; }

	@Override
	public String getId() { return data.id;	}

	@Override
	public void open(Context context) {}

}

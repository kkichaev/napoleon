package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.dataobjects.PKO1c;
import com.grsoft.napoleon.documents.Document;

import android.content.Context;

public class PKO1cImpl extends Document<PKO1c> {
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

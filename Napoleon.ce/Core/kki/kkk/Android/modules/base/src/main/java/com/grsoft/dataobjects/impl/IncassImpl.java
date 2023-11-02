package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Incass;
import com.grsoft.napoleon.IncassEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class IncassImpl extends CreatableDocument<Incass> {

	@Override public void open(Context context) { IncassEdit.open(context, this); }

	@Override public long sum() { return data.sum;	}
}

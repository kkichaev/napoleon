package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Facing;
import com.grsoft.napoleon.FacingEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import android.content.Context;


public class FacingImpl extends CreatableDocument<Facing> {

	@Override public void open(Context context) { FacingEdit.open(context, getRowid()); }

}

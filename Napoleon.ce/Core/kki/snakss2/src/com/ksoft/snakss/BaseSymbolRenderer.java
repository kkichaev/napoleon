package com.ksoft.snakss;

import android.content.Context;

public abstract class BaseSymbolRenderer extends CellRenderer {
	
	public BaseSymbolRenderer(Context context) {
		super(context);
	}

	@Override protected float getFontSize() { return 20; }
}

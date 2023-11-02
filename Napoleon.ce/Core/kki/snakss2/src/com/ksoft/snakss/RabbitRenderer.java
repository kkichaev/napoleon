package com.ksoft.snakss;

import android.content.Context;

public class RabbitRenderer extends BaseSymbolRenderer{
	private RabbitPool rabbit;
	private final String SYMBOL = "R";
	
	public RabbitRenderer(Context context, RabbitPool rabbit) {
		super(context);
		this.rabbit = rabbit;
	}

	@Override protected String getSymbol(int pos) { return SYMBOL; }
	@Override protected Cell[] getCells() {	return rabbit.getRabbitCells(); }
}

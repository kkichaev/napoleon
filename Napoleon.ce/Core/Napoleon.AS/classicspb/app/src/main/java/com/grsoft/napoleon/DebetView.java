package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.grsoft.util.ExtrasConst;

public class DebetView extends DocumentsBase {
	public static void open(Context ctx, String org) {
		Intent i = new Intent(ctx, DebetView.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, org);
		ctx.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.debet_view);
	}
}

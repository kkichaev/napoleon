package com.grsoft.napoleon;

import android.widget.ListAdapter;
import com.grsoft.util.view.dialog_helper.KeyValue;


public class RegFrg extends SelFrg {

	@Override
	protected ListAdapter createAdapter() {	return ((CreateOrder)getActivity()).createRegionAdapter(); }

	@Override
	protected void initTitle() { getDialog().setTitle(R.string.select_region); }

	@Override
	protected void onItemSelect(KeyValue kv) { ((CreateOrder)getActivity()).onRegionSelect(kv); }

}

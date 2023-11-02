package com.grsoft.napoleon;

import android.widget.ListAdapter;
import com.grsoft.util.view.dialog_helper.KeyValue;


public class CityFrg extends SelFrg {
	@Override
	protected ListAdapter createAdapter() {	return ((CreateOrder)getActivity()).createCityAdapter(); }

	@Override
	protected void initTitle() { getDialog().setTitle(R.string.select_city); }

	@Override
	protected void onItemSelect(KeyValue kv) { ((CreateOrder)getActivity()).onCitySelect(kv); }
}

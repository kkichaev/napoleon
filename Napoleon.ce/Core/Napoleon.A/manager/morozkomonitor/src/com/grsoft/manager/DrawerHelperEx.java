package com.grsoft.manager;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

public class DrawerHelperEx extends DrawerHelper {
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
		MenuItem item = (MenuItem) parent.getItemAtPosition(position);
		int id = item.getItemId();
		Context context = view.getContext();

		if (id == R.id.works)
			ManagerNew.open(context);
		else if (id == R.id.setting)
			ManagerConfigurationNew.open(context);
		else if (id == R.id.about)
			AboutNew.open(context);

		childItemClick(context, id);
	}
}

package com.grsoft.napoleon;

import android.widget.BaseAdapter;
import com.grsoft.dataobjects.Org;


public abstract class BaseMainAdapter extends BaseAdapter {
	abstract void reload();
	public abstract Org getOrg(int pos);
	public int getPos(String id) {
		return -1;
	};
}

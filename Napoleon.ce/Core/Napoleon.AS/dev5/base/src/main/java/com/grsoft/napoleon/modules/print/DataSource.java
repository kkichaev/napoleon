package com.grsoft.napoleon.modules.print;
import com.grsoft.aceteam.R;

import android.content.Context;

public abstract class DataSource {
	
	public abstract boolean getValue(StringBuilder value, String name, String format);
	
	public byte[] getImage(String name) { return null; }
	public int getImageHeight(String name) { return 0; }
	
	public void init(Context context, int resId) {}
	
	public DataSource getObject(String name) { return null; }
	
	public void startPage() {}
	public void calculate() {}
	
	public boolean haveMoreData() { return false; }
	public boolean moveNext() { return false; }
}

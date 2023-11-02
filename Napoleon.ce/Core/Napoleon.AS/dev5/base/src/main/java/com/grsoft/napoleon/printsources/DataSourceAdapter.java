package com.grsoft.napoleon.printsources;
import com.grsoft.aceteam.R;

import com.grsoft.napoleon.modules.print.DataSource;

public class DataSourceAdapter extends DataSource {

	@Override
	public boolean getValue(StringBuilder value, String name, String format) {
		return SilentReflector.getFieldValue(value, name, this, format);
	}
}

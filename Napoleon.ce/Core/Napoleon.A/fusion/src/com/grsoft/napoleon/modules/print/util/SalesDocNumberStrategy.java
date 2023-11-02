package com.grsoft.napoleon.modules.print.util;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.SalesImpl;

public class SalesDocNumberStrategy extends BaseDocNumberStrategy {
	@Override
	protected long getStartNumber(DbObject<?> obj) {
		if( obj instanceof SalesImpl) {
			ConfigImpl ci = new ConfigImpl();
			StringBuilder value = new StringBuilder();
			if( ci.getValue(value, "НомерПоследнейНакладной") ) {
				long res = 0;
				try {
					res = Integer.parseInt(value.toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				return res + 1;
			}
		}
		return super.getStartNumber(obj);
	}
}

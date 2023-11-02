package com.grsoft.napoleon;

import com.grsoft.database.DbReader;
import com.grsoft.dataobject.AgentPrefixEx;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.modules.print.util.BaseDocNumberStrategy;

public class MakeDocNumberEx extends BaseDocNumberStrategy {
	@Override
	protected String makePrefix(DbReader r, DbObject<?> doc) {
		if(doc instanceof SalesImpl) {
			AgentPrefixEx ap = (AgentPrefixEx) AgentPrefix.get();
			if( ap != null )
				return ((((SalesImpl)doc).getData().params & ParamState.ofCash) != 0) ? ap.prefix : ap.prefixAdd;
			
		}
		return super.makePrefix(r, doc);
	}
}

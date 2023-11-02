package com.grsoft.util;

import android.content.Context;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.napoleon.RWServiceFactoryNapoleon;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.RWServiceFactory;

public class FirstRunInit
{	
	/***
	 * Метод должен вызываться первыйм в Application.onCreate().
	 * @param context
	 */
	static public void init(Context context)
	{
		ConfigManager.tryInitConfig(new CfgNpl());
		Path.init(context);
			
		DataBaseManager.init();
		DocTypeBase.checkTables();
		DbWriter.checkDBTable(OrgSum.class);

		ConfigManager.load(context);
		SrcDataCounter.init(context);

		if(RWServiceFactory.instance.getClass() == RWServiceFactory.class )
			RWServiceFactory.instance = new RWServiceFactoryNapoleon();
	}
}

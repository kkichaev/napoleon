/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.GPSGatherDoc;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.network.ServerCommand;


public class NapoleonApp extends NapoleonAppBaseEx{
	@Override
	protected void initDocTypes() {
		Print.init(false);
		GPSGatherDoc.OBJ = "OrgLocation";
		super.initDocTypes();
		Features.RECIEVE_REMNANTS_IN_MAIN_MENU = false;
		
		PriceCount.activity = PriceCountEx.class;
		ServerCommand.Category = "pda";
		PotenzialOrg.activity = PotenzialOrgEx.class;
		
		GPSGatherEdit.BEST_ACC = 50;
		Documents.activity = Documents2Ex.class;
	}
}

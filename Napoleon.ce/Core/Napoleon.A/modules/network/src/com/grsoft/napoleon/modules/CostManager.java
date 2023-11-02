package com.grsoft.napoleon.modules;

import android.content.Context;

import com.grsoft.database.Hitching;

public interface CostManager {

	public class CostType {
		public String id;
		public String name;
		
		public CostType() {}
		
		public CostType(String id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	Hitching getReceiveHitching(Context context);
	
	/**
	 * Чтение файла цен в память. Файл читается только если он не был прочитан ранее. 
	 * @param context
	 */
	void initCost(Context context);
	
	int getCost(String id, int costType);
	int getCost(String id, String costType);
	int getCostIndex(String costType);
	
	CostType[] getCostTypes();
}

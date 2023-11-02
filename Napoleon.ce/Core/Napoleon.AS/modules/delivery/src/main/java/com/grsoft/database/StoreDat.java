package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

public class StoreDat {
	public String name  = "";
	public String crc = "";
	public List<String> items = new ArrayList<String>();
	private final static String ITEM_DLM = ",";
	private final static String LEFT_BRK = "{";
	private final static String RIGHT_BRK = "}";
	
	public static StoreDat parse(String line) {
		
		final int ITEM_SZ = 2;
		
		StoreDat result = null;
		String a[] = line.substring(0, line.indexOf(LEFT_BRK)).split(ITEM_DLM);
		
		if (a.length == ITEM_SZ){
			result = new StoreDat();
			result.name = a[0];
			result.crc = a[1];
			
			
		}
		
		String ai[] = line.substring(line.indexOf(LEFT_BRK) + 1, line.length() - 1).split(ITEM_DLM);
		
		for(int i = 0; i < ai.length; i++)
			result.items.add(ai[i]);
		
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(ITEM_DLM);
		sb.append(crc).append(ITEM_DLM);
		sb.append(LEFT_BRK);
		
		StringBuilder is = new StringBuilder();
		
		for(String i : items){
			if(is.length() > 0)
				is.append(ITEM_DLM);
			is.append(i);
		}
			
		sb.append(is.toString());
		sb.append(RIGHT_BRK);
		
		return sb.toString();	
	}
	
}

package com.grsoft.dataobjects;

import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.types.FieldOrder;

public class PlanRouteItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	public String spectask = "";
	
	@FieldOrder(order=2)
	public int order = 1;
	
	@FieldOrder(order=3)
	public int incass = 1;
	
	@FieldOrder(order=4)
	public int returns = 1;
	
	@FieldOrder(order=5)
	public int visit = 1;
	
	public int getDocCount(){
		int result = 0;
		
		if(order == 1)
			result++;
		if(incass == 1)
			result++;
		if(returns == 1)
			result++;
		if(visit == 1)
			result++;
		
		return result;
	}
	
	public ScriptDef createScriptDef(){
		ScriptDefImpl impl = new ScriptDefImpl();
		impl.getData().id = ("" + order + incass + returns + visit).hashCode(); 
		ScriptDef result = impl.getData();
		
		if(!impl.read()){
			if(order == 1){
				ScriptDefItem i = new ScriptDefItem();
				i.curType = "Order";
				result.items.add(i);
			}
			
			if(incass == 1){
				ScriptDefItem i = new ScriptDefItem();
				i.curType = "Incass";
				result.items.add(i);
			}
			
			if(returns == 1){
				ScriptDefItem i = new ScriptDefItem();
				i.curType = "Returns";
				result.items.add(i);
			}
			
			if(visit == 1){
				ScriptDefItem i = new ScriptDefItem();
				i.curType = "Visit";
				result.items.add(i);
			}
			
			impl.write();
		}
		
		impl.close();
		
		return result;
	}
}

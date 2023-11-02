package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

@TableInfo(name="ServikoAction", keyFields="idOrg,id")
@ServerInfo(name="ServikoAction")
public class ServikoAction extends DataObject implements Comparable<ServikoAction> {
    public String id = "";
    public String name = "";
    public String idOrg = "";
    public String condition = "";
    
    @Scale(value=Consts.QTY_SCALE)
    public int value = 0;
    
    public int isFix = 0;
    public int isAuto = 0;
    public int isBase = 0;
    public int isFloat = 0;
    public int valueIsCost = 0;
    public int applyOnBase = 0;
	
    public int displace = 0;
    public int displaceDoc = 0;
    public int displacePriority = 0;
    public int priority = 0;
    public int exPriority = 0;
    public Date start = new Date();
    
    public List<ActionSklad> sklads = new ArrayList<ActionSklad>();
    
    public ServikoAction() {}
    
    public ServikoAction(ServikoAction src) {
    	makeCopy(this, src);
    }
    
	public int discountValue(int priceCost, int baseCost) {
		if(valueIsCost > 0)
			return value / 10;
		
		double cd = (double)(applyOnBase > 0 ? baseCost : priceCost) / Consts.SUM_SCALE;
		double dsc = (double)value / (Consts.QTY_SCALE * 100.0);
		double sum = (cd * dsc * Consts.SUM_SCALE) + 0.5;
		return (int)sum;
	}

    public int getDisplaceValue() {
    	return 	displace == 0 ? 0 : 				//не вытесняет ничего
    			displacePriority > 0 ? priority :	//вытесняет все по приоритету
    			displaceDoc > 0 ? -2 :				//вытесняет все по документу
    			-1;									//вытесняет все по номенклатуре не смотря на приоритет
    }
    
    public boolean sameValue(int displaceValue, int displaceDocValue) {
    	return (getDisplaceValue() == displaceValue) && (displaceDocValue < 0 || displaceDoc == displaceDocValue);
    }
    
    @Override
	public int compareTo(ServikoAction arg0) {
		int cmp = arg0.displace - displace;
		if(cmp != 0) return cmp;

		int val1 = ((displacePriority == 0) && (displace > 0)) ? 1 : 0; 
		int val2 = ((arg0.displacePriority == 0) && (arg0.displace > 0)) ? 1 : 0; 
		cmp = val2 - val1;
		if(cmp != 0) return cmp;

		cmp = arg0.displaceDoc - displaceDoc;
		if(cmp != 0) return cmp;
		
		cmp = displacePriority - arg0.displacePriority;
		if(cmp != 0) return cmp;
		
		cmp = priority - arg0.priority;
		if(cmp != 0) return cmp;

		cmp = exPriority - arg0.exPriority;
		if(cmp != 0) return cmp;

		return start.compareTo(arg0.start);
	}

	public boolean isBaseAction() {
		return isAuto> 0 || isBase > 0;
	}
   
	
	public String actionText(int cost) {
		String text;
		if(valueIsCost > 0) {
			text = Util.IntToScaleStr(value / 10, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р.";
		} else {
			text = Util.IntToScaleStr(value, Consts.QTY_SCALE, Util.DEC_DELIM, isFix == 0 ? true : false);
			if(isFix == 0) {
				text += "% = " + Util.IntToScaleStr(cost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			}
		}
		if(name.length() > 0)
			text += "<br/>" + name;
		if(condition.length() > 0)
			text += "<br/>" + condition;
		return text;
	}
}

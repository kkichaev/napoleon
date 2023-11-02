package com.grsoft.napoleon.printsources;

@SuppressWarnings("serial")
public class SalesPrintItemsEx extends SalesPrintItems {

	public SalesPrintItemsEx(SalesPrint owner) {
		super(owner);
	}

	public boolean hasnds;
	
	@Override
	public boolean getValue(StringBuilder value, String name) {
		if (!hasnds){
			if(name.equals("tax")){
				value.append("Áåç ÍÄÑ");
				return true;
			}else if (name.equals("sumwtax") || name.equals("cost"))
				return super.getValue(value, "sum");
			else if (name.equals("sumtax")){
				value.append("");
				return true;
			}else
				return super.getValue(value, name);
		}else{
			if(name.equals("tax")){
				super.getValue(value, name);
				value.append("%");
				return true;
			}else
				return super.getValue(value, name);
		}
	}

}

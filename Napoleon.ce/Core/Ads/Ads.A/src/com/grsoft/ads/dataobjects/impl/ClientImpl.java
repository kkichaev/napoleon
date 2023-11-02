package com.grsoft.ads.dataobjects.impl;

import com.grsoft.ads.dataobjects.Client;
import com.grsoft.dataobjects.impl.DbObject;

public class ClientImpl extends DbObject<Client>{
	public String getHtmlNameAddress(String number){
		StringBuilder sb = new StringBuilder("<b>");
		sb.append(data.name).append("</b><br>");
		
		if (number != null && number.length() > 0)
			sb.append("<i>").append(number).append("</i><br>");
		
		sb.append(getData().address);
		 
		return  sb.toString();
	}
	
	public String getHtmlNameAddress(){
		return getHtmlNameAddress("");
	}
}

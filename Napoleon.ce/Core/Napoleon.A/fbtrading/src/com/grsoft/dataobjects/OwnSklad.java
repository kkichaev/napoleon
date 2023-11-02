package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="OwnSklad", keyFields="id")
@ServerInfo(name="OwnSklad")
public class OwnSklad extends DataObject {
	public String id="";
	
	public static String getOwnSklad() {
		final OwnSklad data = new OwnSklad();
		DataTraveler.travel(OwnSklad.class, new DataTraveler.Travel<OwnSklad>() {

			@Override
			public boolean travel(DataTraveler<OwnSklad> item) {
				data.id = item.data.id;
				return false;
			}
		}, "");
		return data.id;
	}
}

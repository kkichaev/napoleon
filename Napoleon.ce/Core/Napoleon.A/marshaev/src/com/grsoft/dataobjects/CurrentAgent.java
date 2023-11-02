package com.grsoft.dataobjects;

import android.content.Context;

import com.grsoft.napoleon.util.ProgID;

public class CurrentAgent extends AgentPrefix {
	public String progid = "";

	static CurrentAgent retAgent = null;
	
	public static CurrentAgent get(Context context) {
		if( retAgent != null )
			return retAgent;
		
		DataTraveler.travel(CurrentAgent.class, new DataTraveler.Travel<CurrentAgent>() {

			@Override
			public boolean travel(DataTraveler<CurrentAgent> item) {
				retAgent = item.data;
				return false;
			}
		}, "progid='" + ProgID.getPrgID(context) + "'" );
		return retAgent;
	}
}

package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitPreview;


public class SyncPhoto {
	
	public static void sync(Context context, UpdateCtrl pown, String userid, Date created){
		List<Hitching> ret = new ArrayList<Hitching>();
		ret.add(new VisitHitch(VisitPreview.class, userid, created));

		UpdateProcess upp = new UpdateProcess((Activity) context, pown, ret);
		upp.execute((Void[]) null);
	}
}

class SPParam extends DataObject{
	public String userid;
	public Date created;
}
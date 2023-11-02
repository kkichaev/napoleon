package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.grsoft.database.FullPrice;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.CurrentAgent;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.RefreshData;
import com.grsoft.network.SendData;

public class DataExchange {

	public static void receiveData(Context context, RefreshData.Handler handler) {
		List<Hitching> data = new ArrayList<Hitching>();
		data.add(new RcvNewHitching(Matrix.class, "CommonMatrix"));
		data.add(new RcvNewHitching(DbObject.getDataType(Folder.class), "Folder"));
		data.add(new FullPrice());

		data.add(new RcvNewHitching(CurrentAgent.class, "Agents"));
		data.add(new RcvNewHitching(Org.class, "CommonOrgs"));
		
		RefreshData rd = new RefreshData(context, handler, data);
		rd.execute((Void[])null);
	}
	
	public static void sendDocs(Context context, SendData.Handler handler) {
		DocExportListener dl = OrderDoc.instance().getDirtyDocuments();
		SendData sd = new SendData(context, dl, handler);
		sd.execute((Void[])null);
	}
}

package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.List;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Dispatch;
import com.grsoft.dataobjects.DispatchItem;
import com.grsoft.dataobjects.DispatchPhoto;
import com.grsoft.dataobjects.DispatchTime;
import com.grsoft.dataobjects.ItemDef;
import com.grsoft.dataobjects.RouteItem;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DShipmentDoc;
import com.grsoft.napoleon.documents.DVisitDoc;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.gps.GPSUtilNew;
import android.content.Context;

public class DispatchImpl extends CreatableDocument<Dispatch> {

	public static Class<? extends DispatchImpl> DISPATCH = DispatchImpl.class;
	
	protected DispatchImpl() {}
	
	public static DispatchImpl create() {
		DispatchImpl ret = null;
		
		try {
			ret = DISPATCH.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	@Override
	public void open(Context context) {
	}

	public boolean init(Context context, RouteItem ri, GpsCoord gpsCoord) {
		for (ItemDef d : ri.docs) {
			DispatchItem i = new DispatchItem();
			i.number = d.number;
			i.type = d.type;
			i.remark = d.remark;
			i.itemid = d.id;
			data.items.add(i);
		}

		data.itemid = ri.itemid;

		return super.init(context, ri.id, gpsCoord);
	}

	@Override
	public void postInit() {
		DispatchTime dt = new DispatchTime();
		dt.start = data.created;
		data.times.add(dt);
		data.params |= Dispatch.NOT_READY_TO_SEND;
		data.visit = data.created;
	}

	public boolean readFromId(String id) {
		boolean result = false;
		String condition = "itemid='" + id + "'";
		DbWriter.checkDBTable(Dispatch.class);
		List<Long> ids = DbReader.readIds(getTableName(), condition, null);

		if (ids.size() > 0) {
			result = read(ids.get(0), false);
			close();
		}

		return result;
	}

	public boolean isInWork() {
		boolean result = false;

		if (data.times.size() > 0)
			result = data.times.get(data.times.size() - 1).finish.equals(new Date(0));

		return result;
	}

	public DispatchItem findItem(String itemid) {
		DispatchItem result = null;

		for (DispatchItem i : data.items)
			if (i.itemid.equals(itemid)) {
				result = i;
				break;
			}

		return result;
	}

	@Override
	public boolean isEditable() {
		return true;
		/* !isReadyToSend() && super.isEditable(); */}

	protected boolean isReadyToSend() {
		return !((data.params & Dispatch.NOT_READY_TO_SEND) == Dispatch.NOT_READY_TO_SEND);
	}

	public void setReadyToSend() {
		data.params &= ~Dispatch.NOT_READY_TO_SEND;
		setExported(false);
	}

	public void finish() {
		boolean f = true;

		if (f) {
			setReadyToSend();
			setChildDocReadyToSend();
		}

		write();
		close();
	}

	private void setChildDocReadyToSend() {
		for (int i = 0; i < data.items.size(); i++) {
			DispatchItem di = data.items.get(i);

			if (di.state == DispatchItem.DOC_INITED) {
				DocTypeBase dt = DocTypeBase.getDocType(di.type);
				DispatchDocImpl<?> doc = (DispatchDocImpl<?>) dt.create();
				if (dt != null) {
					doc.getData().created = di.date;
					doc.read();
					doc.setReadyToSend();
					doc.write();
					doc.close();
				}
			}
		}

		DVisitImpl visit = (DVisitImpl) DVisitDoc.instance().create();

		if (visit != null) {
			visit.getData().created = data.visit;

			if (visit.read()) {
				visit.setReadyToSend();
				visit.write();
			}

			visit.close();
		}
	}

	public DispatchPhoto findPhoto(String path) {
		DispatchPhoto result = null;

		for (DispatchPhoto i : data.photos)
			if (new String(i.id).equals(path)) {
				result = i;
				break;
			}

		return result;
	}

	public boolean removePhoto(String path) {
		boolean result = false;
		DispatchPhoto i = findPhoto(path);

		if (i != null) {
			data.photos.remove(i);
			result = write() != ExtrasConst.INVALID_ROWID;
			close();
		}

		return result;
	}

	public boolean openDoc(Context context, int pos) {
		boolean result = false;

		if (pos >= 0 && data.items.size() > pos) {
			DispatchItem i = data.items.get(pos);
			DocTypeBase dt = DocTypeBase.getDocType(i.type);

			if (dt != null) {
				DispatchDocImpl<?> doc = (DispatchDocImpl<?>) dt.create();

				if (doc != null) {
					if (i.state == DispatchItem.DOC_INITED) {
						doc.getData().created = i.date;
						result = doc.read();
					} else {
						doc.init(context, this, i, GPSUtilNew.getLastKnownLocation());
						i.date = doc.getData().created;
						i.state = DispatchItem.DOC_INITED;

						if (isEditable()) {
							write();
							close();
						}
						result = true;
					}

					doc.close();
					doc.open(context);
				}
			}
		}

		return result;
	}

	public boolean isDocFinished(Context context) {
		for(int i=0; i<data.items.size(); i++) {
			DispatchItem di = data.items.get(i);
			if(di.type.equals(DShipmentDoc.instance().getObjectName()))
				return isItemFinished(context, i);
		}

//		for(int i=0; i<data.items.size(); i++) {
//			if(!isItemFinished(context, i))
//				return false;
//		}
		
		return true;
	}
	
	public boolean isItemFinished(Context context, int pos) {
		boolean res = false;
		
		if(pos >= 0 && data.items.size() > pos){
			DispatchItem i = data.items.get(pos);
			
			if (i.state == DispatchItem.DOC_INITED) {
				DocTypeBase dt = DocTypeBase.getDocType(i.type);
				
				if(dt != null){
					DispatchDocImpl<?> doc = (DispatchDocImpl<?>) dt.create();
					doc.getData().created = i.date;
					doc.read();
					doc.close();
					
					res = doc.isReadyToSend();
				}
			}
		}
		
		return res;
	}
}

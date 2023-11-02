package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.List;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Dispatch;
import com.grsoft.dataobjects.DispatchItem;
import com.grsoft.dataobjects.DispatchPhoto;
import com.grsoft.dataobjects.DispatchTime;
import com.grsoft.dataobjects.PointDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.ExtrasConst;
import android.content.Context;


public class DispathImpl extends CreatableDocument<Dispatch> {

	@Override
	public void open(Context context) {
	}

	@Override
	public void postInit() {
		DispatchTime dt = new DispatchTime();
		dt.start = data.created;
		data.times.add(dt);
		
		RoutePointImpl rpi = new RoutePointImpl();
		rpi.read("id", getId());
		
		for(PointDoc d : rpi.getData().docs){
			DispatchItem i = new DispatchItem();
			i.number = d.number;
			data.items.add(i);
		}
		
		data.params |= Dispatch.NOT_READY_TO_SEND;
	}

	public boolean readFromId(String id) {
		boolean result = false;
		String condition = "id='" + id + "'";
		DbWriter.checkDBTable(Dispatch.class);
		List<Long> ids = DbReader.readIds(getTableName(), condition, null);
		
		if(ids.size() > 0){
			result = read(ids.get(0), false);
			close();
		}
		
		return result;
	}
	
	public boolean isInWork(){
		boolean result = false;
		
		if (data.times.size() > 0)
			result = data.times.get(data.times.size()-1).finish.equals(new Date(0));
		
		return result;
	}

	public DispatchItem findItem(String number) {
		DispatchItem result = null;
		
		for(DispatchItem i : data.items)
			if(i.number.equals(number)){
				result = i;
				break;
			}
		
		return result;
	}
	
	@Override
	public boolean isEditable() { return !isReadyToSend() && super.isEditable(); }

	protected boolean isReadyToSend() {
		return !((data.params & Dispatch.NOT_READY_TO_SEND) == Dispatch.NOT_READY_TO_SEND);
	}
	
	public void setReadyToSend(){
		data.params &= ~Dispatch.NOT_READY_TO_SEND;
	}

	public void finish() {
		boolean f = true;
		
		for(DispatchItem i : data.items)
			if(i.state == DispatchItem.WAITING){
				f = false;
				break;
			}
		
		if(f)
			setReadyToSend();
		
		write();
		close();
	}

	public DispatchPhoto findPhoto(String path) {
		DispatchPhoto result = null;
		
		for(DispatchPhoto i : data.photos)
			if(new String(i.id).equals(path)){
				result = i;
				break;
			}
		
		return result;
	}

	public boolean removePhoto(String path) {
		boolean result = false;
		DispatchPhoto i = findPhoto(path);
		
		if(i != null){
			data.photos.remove(i);
			result = write() != ExtrasConst.INVALID_ROWID;
			close();
		}
		
		return result;
	}
}

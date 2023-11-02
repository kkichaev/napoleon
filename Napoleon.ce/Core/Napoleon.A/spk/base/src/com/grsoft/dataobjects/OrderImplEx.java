package com.grsoft.dataobjects;

import java.util.Calendar;

import android.content.Context;

import com.grsoft.dataobjects.impl.OrderImpl;

public class OrderImplEx extends OrderImpl {
	static public final int MARKDELETE = 0x80000;
	static public final int DELETED = 0x100000;
	static public final int APPROVED = 0x200000;
	
	public boolean isMarkToDel(){
		return (data.params & MARKDELETE) == MARKDELETE;
	}
	
	public void setMarkToDel(){
		data.params |= MARKDELETE;
	}
	
	public boolean isDeleted(){
		return (data.params & DELETED) == DELETED;
	}
	
	public boolean isApproved(){
		return (data.params & APPROVED) == APPROVED;
	}
	
	@Override
	public String getDescription(Context ctx) {
		if (isMarkToDel())
			return "помечен на удаление";
		else
			return super.getDescription(ctx);
	}
	
	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		if (!isOldOrder){
			Calendar c = Calendar.getInstance();
			c.setTime(data.date);
			c.add(Calendar.DATE, 1);
			data.date = c.getTime();
			write();
			close();
		}
		
		super.editProperties(ctx, isOldOrder);
	}
}

package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.ChatData;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ChatImpl;
import com.grsoft.network.ObjectExportListener;


public class ChatSndHitching extends Hitching implements ObjectExportListener{

	List<Long> list;
	public ChatSndHitching() {
		super(ChatData.class, "ChatData");
		
		DbWriter.checkDBTable(dataObject);
		list = new ArrayList<Long>();
		String where = "(([params] & " + ParamState.ofExported + " ) == 0)";
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(dataObject), where, "");
	}

	@Override
	public int size() {
		return list.size();
	}
	
	@Override
	public void onEnd() {
		for( int i=0; i<list.size(); i++ ) {
			ChatImpl impl = new ChatImpl();
			impl.read(list.get(i));
			impl.getData().params |=  ParamState.ofExported;
			impl.write();
			impl.close();
		}
	}

	@Override
	public DataObject get(int i) {
		ChatImpl impl = new ChatImpl();
		impl.read(list.get(i));
		impl.close();
		return impl.getData();
	}
	
	
}

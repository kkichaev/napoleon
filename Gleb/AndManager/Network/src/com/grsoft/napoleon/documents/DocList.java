package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;

public class DocList implements Iterable<Document<?>> {
	protected Document<?> document;
	protected List<Long> ids;
	
	public DocList(Class<? extends Document<?>> docType, String where, String order) {
		try {
			document = docType.newInstance();
			DbWriter.checkDBTable(document.getData().getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String table = DataObjectInfo.getInstance().getTableName(document.getData().getClass());
		ids = DbReader.readIds(table, where, order);
	}
	
	protected DocList() {}
	
	/**
	 * —пециальный случай - список из одного документа
	 * @param doc
	 * @param rowid
	 */
	public DocList(Document<?> doc, long rowid) {
		document = doc;
		ids = new ArrayList<Long>();
		ids.add(rowid);
	}
	
	public int getCount() { return (ids != null) ? ids.size() : 0; }
	
	public Document<?> get(int index) {
		if( ids != null && index >= 0 && index < ids.size() && document.read(ids.get(index), false) )
			return document;		
		return null;
	}

	public long getId(int index) { return (ids != null && index >= 0 && index < ids.size()) ? ids.get(index) : 0; }
	
	public void close() {
		if( document != null )
			document.close();
	}

	public class DocIterator implements Iterator<Document<?>> {

		int current = 0;
		
		@Override public boolean hasNext() { return (current < getCount()); }
		@Override public Document<?> next() { return get(current++); }
		@Override public void remove() { }		
	}
	
	@Override
	public Iterator<Document<?>> iterator() {
		return new DocIterator();
	}
}

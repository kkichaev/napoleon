package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;

public class DocList implements Iterable<Document<?>> {
	protected Document<?> document;
	protected List<Long> ids;
	protected Class<? extends Document<?>> docType;
	
	public DocList(){}
	public DocList(Class<? extends Document<?>> docType, String where, String order) {
		this(docType);
		
		document = docInstance();
		
		if(document != null && document.getData() != null){
			DbWriter.checkDBTable(document.getData().getClass());
			String table = DataObjectInfo.getInstance().getTableName(document.getData().getClass());
			ids = DbReader.readIds(table, where, order);
		}
	}
	public DocList(Class<? extends Document<?>> docType, List<Long> ids) {
		this(docType);
		
		document = docInstance();
		this.ids = ids;
	}
	
	private Document<?> docInstance(){
		Document<?> result = null;
		
		try {
			result = docType.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	protected DocList(Class<? extends Document<?>> docType) {
		this.docType = docType;
	}
	
	/**
	 * Специальный случай - список из одного документа
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
	
	/**
	 * Удаляет только из списка, не из базы 
	 * @param documents
	 */
	public void removeDocuments(List<Long> documents) {
		if (ids != null)
			ids.removeAll(documents);
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
	
	public void sort(Comparator<Long> cmp){
		Collections.sort(ids, cmp);
	}
	
	public List<Document<?>> instanceCollections(){
		List<Document<?>> result = new ArrayList<Document<?>>();
		
		for(long i : ids){
			Document<?> d = docInstance();
			if(d.read(i))
				result.add(d);
			d.close();
		}
		
		return result;
	}
}

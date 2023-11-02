/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Объет данных, который может содержит список Memeber
 *
 * kki   14/09/2010   creating
 */

package com.grsoft.network;


import java.lang.reflect.Field;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.napoleon.util.ConvertConstants;
import com.grsoft.network.exception.MemberTypeNotImplemented;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.exception.UnexpectedCharInStream;

public class RawObject
{
	private Format format;
	//private Map<String, Member> members = new HashMap<String, Member>();
	
	private List<ObjectField> members = new ArrayList<ObjectField>();
	
	class ObjectField {
		public MemberFormat format;
		public Member member; 
		
		public ObjectField(MemberFormat f) throws RuntimeException {
			format = f;
			member = createMember(f);
		}
		
		public void clean(){
			member.setValue(null);
		}
	}
	
	private static Member createMember(MemberFormat memberFormat) throws RuntimeException {
		Class<?> memberClass = memberFormat.getMemberType();
		
		if (memberClass == ObjectList.class)
			return new ObjectListMember();
		
		if (memberClass == byte[].class)
			return new BytesMember();
		
		if (memberClass == Date.class)
			return new DateMember();
		
		if (memberClass == int.class || memberClass == long.class)
			return new NumberMember();
		
		if (memberClass == String.class)
			return new StringMember();
		
		if (memberClass == Time.class)
			return new TimeMember();
		
		throw new RuntimeException(new MemberTypeNotImplemented(memberFormat));
	}
		
	public RawObject(Format format) throws RuntimeException {
		this.format = format;

		for (MemberFormat mf : format)
			members.add(new ObjectField(mf));
//			members.put(mf.getName(), createMember(mf));
	}

	public Member getMember(String fieldName) {
		for( ObjectField f : members )
			if( f.format.getName().compareTo(fieldName) == 0 )
				return f.member;
		return null;
//		return members.get(fieldName); 
	}

	public boolean read(ByteStream stream) throws RuntimeException {
		RawObjectReader reader = new RawObjectReader(this);
		clean();
		return reader.read(stream);
    }

	private void clean(){
		for(ObjectField of : members){
			of.clean();
		}
	}
	
	public Format getFormat() { return format; }

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		
		for(ObjectField entry : members)
			result.append(entry.format.getName() + 
					ConvertConstants.SPACE + ConvertConstants.COLON + 
					ConvertConstants.SPACE + entry.member.toString() + 
					ConvertConstants.SPACE + ConvertConstants.COMMA);
		
//		for(Map.Entry<String, Member> entry : members.entrySet())
//			result.append(entry.getKey() + 
//					ConvertConstants.SPACE + ConvertConstants.COLON + 
//					ConvertConstants.SPACE + entry.getValue().toString() + 
//					ConvertConstants.SPACE + ConvertConstants.COMMA);
		
		result.deleteCharAt(result.length()-1);
		
		return result.toString();
	}
	
	public void setDataObject(DataObject result) {
		Class<? extends DataObject> dataObjectClass = result.getClass();
		try {
			for(ObjectField entry: members ) {
				Field field = null; 
				
				try {
					String fieldName = entry.format.getName();
					if (!DataObjectInfo.getInstance().ifFieldPresent(dataObjectClass, fieldName))
						continue;
						
					field = dataObjectClass.getField(fieldName);
					entry.format.setField(field, entry.member, dataObjectClass, result);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
	
	public DataObject createDataObject(Class<? extends DataObject> dataObjectClass) throws RuntimeException	{
		try {
			DataObject result = dataObjectClass.newInstance();
			setDataObject(result);
			return result;
		}
		catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}

//	private ArrayList<DataObject> makeComplexObject(Class<? extends DataObject> listType, ObjectList obectList)
//			throws ListShouldParameterezed, RuntimeException
//	{
//		ArrayList<DataObject> complexObject = new ArrayList<DataObject>();
//				
//		for (RawObject rawObject : obectList)
//		{
//			DataObject dataObject = rawObject.createDataObject((Class<? extends DataObject>) listType);
//			complexObject.add(dataObject);
//		}
//		
//		return complexObject;
//	}

	class RawObjectReader {
		private RawObject rawObject;
	
		public RawObjectReader(RawObject rawObject) { this.rawObject = rawObject; }
	
		public boolean read(ByteStream stream) throws RuntimeException {			
			if ( stream.current() == ConvertConstants.LEFT_BRACKET )
		        return readFullyMembers(stream);
			return false;
		}
		
		private boolean readFullyMembers(ByteStream stream) throws RuntimeException {
			if (stream.next() == ConvertConstants.RIGHT_BRACKET) {
				// move to next object
				stream.moveNext();
		        stream.moveNext();
	        	return false;
			}
	        else {
	        	readMembers(stream);
	        	return true;
	        }
		}
		
		private void readMembers(ByteStream stream) throws RuntimeException {
			for (MemberFormat mf : rawObject.getFormat()) {
	           stream.moveNext();
	           Member m = rawObject.getMember(mf.getName());
	
	           if (!mf.readMember(m, stream))
	              break;
	
	           char sym = stream.current();
	           
	           if (sym == ConvertConstants.RIGHT_BRACKET) {
	              stream.moveNext();
	              break;
	           }
	           
	           else if (sym != ConvertConstants.COMMA)
	              throw new RuntimeException(new UnexpectedCharInStream(stream));
	        }
	
		}
	}
}

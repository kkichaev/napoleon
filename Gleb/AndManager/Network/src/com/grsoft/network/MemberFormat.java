/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 *
 *
 * kki   14/09/2010   creating
 */

package com.grsoft.network;


import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import com.grsoft.database.DataObjectUtils;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.util.ConvertConstants;
import com.grsoft.network.exception.CreateMemberFormatNotImplemented;
import com.grsoft.network.exception.RuntimeException;

public abstract class MemberFormat 
{
	private String name;
	private Class<?> memberType;
	private String formatString;
	
	public MemberFormat(String name, Class<?> memberType, String formatString ){
		this.name = name;
		this.memberType = memberType;
		this.formatString = formatString;
	}
	
	public Class<?> getMemberType(){
		return memberType;
	}
	
    public String toFormatString(){
    	return formatString;
    }
    
    public boolean read(ByteStream stream){
    	return true;
    }
    
    public abstract boolean readMember(Member m, ByteStream stream) throws RuntimeException;
    
    public static MemberFormat createFormat(ReadMemberContext context) throws RuntimeException {
    	MemberFormat result = createMemberFormat(context);
    	result.read(context.getStream());
        return result;
    }
    
	public static MemberFormat createMemberFormat(ReadMemberContext context) throws RuntimeException {
		final char STRING_FORMAT_CODE = 's';
		final char NUMBER_FORMAT_CODE = 'n';
		final char BINARY_FORMAT_CODE = 'b';
		final char DATE_FORMAT_CODE = 'd';
		final char TIME_FORMAT_CODE = 't';
		
		if (context.getCurSymbol() == ConvertConstants.COLON) {
	       if (context.moveNext())
	       {
	          switch (context.getCurSymbol())
	          {
	             case STRING_FORMAT_CODE:
	                return  new StringFormat(context.getMemberName());
	
	             case NUMBER_FORMAT_CODE:
	                return  new NumberFormat(context.getMemberName());
	                
	             case BINARY_FORMAT_CODE:
	                return new BinaryFormat(context.getMemberName());
	                
	             case DATE_FORMAT_CODE:
	                if (context.getNextChar() == TIME_FORMAT_CODE) {
	                   context.moveNext();
	                   return new StampFormat(context.getMemberName());
	                }
	                else
	                   return new DateFormat(context.getMemberName());
	                
	             case TIME_FORMAT_CODE:
	                return new TimeFormat(context.getMemberName());
	          }
	       }
	    } else if (context.getCurSymbol() == ConvertConstants.LEFT_BRACKET) {
	       return new ObjectFormat(context.getMemberName(), context.getFormatName());
	    }
		
		throw new RuntimeException(new CreateMemberFormatNotImplemented(context));
	}

	public static MemberFormat createFormat(Field field, Class<? extends DataObject> dataType) {
		Class<?> type = field.getType();
		String name = field.getName();
		
		if (type == String.class)
			return new StringFormat(name);
		
		if (type == int.class) {
			int scale = DataObjectInfo.getInstance().getScale(dataType, field.getName());
			return new NumberFormat(name, scale);
		}

		if (type == Date.class)
			return new StampFormat(name);
		
		if (type == List.class) {
			ListFormat result = new ListFormat(field.getName());
			
			Class<? extends DataObject> paramType = DataObjectInfo.getInstance().getListType(dataType, field.getName()); 
//				(Class<? extends DataObject>) 
//				((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
			
			paramType = DbObject.getDataType(paramType);
			
			for (Field listField: DataObjectInfo.getInstance().getFields(paramType))
				result.add(createFormat(listField, paramType));
			
			return result;
			
		}
		
		if (type == byte[].class)
			if (DataObjectUtils.hasSource(field))
				return new SrcBinaryFormat(name);
			else
				return new BinaryFormat(name);
		
		return null;
	}
    
	public String getName()	{
		return name;
	}
	
	@Override
	public String toString() {
		return getName() + toFormatString();
	}

	public void setField(Field f, Member m,
			Class<? extends DataObject> dataObjectClass, DataObject object)
			throws Exception {
		f.set(object, m.getValue()); 
	}
}

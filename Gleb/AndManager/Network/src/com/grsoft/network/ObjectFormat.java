/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 *
 *
 * kki   10/10/2010   creating
 */

package com.grsoft.network;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.network.exception.RuntimeException;

public class ObjectFormat extends MemberFormat
{
	private String parent;
	private final char DOLLAR = '$';
	
	public ObjectFormat(String name, String parent)
	{
		super(name, ObjectList.class, "");
		this.parent = parent;
	}
	
	@Override
	public void setField(Field f, Member m, Class<? extends DataObject> dataObjectClass, 
			DataObject object) throws Exception {
		Class<? extends DataObject> listClass = 
			DataObjectInfo.getInstance().getListType(dataObjectClass, f.getName());
		ArrayList<DataObject> res = makeComplexObject(listClass, (ObjectList)m.getValue());
		f.set(object, res);
	}

	private ArrayList<DataObject> makeComplexObject(Class<? extends DataObject> listType, ObjectList obectList)	throws Exception {
		ArrayList<DataObject> complexObject = new ArrayList<DataObject>();

		//Class<? extends DataObject> itemType =  //DbObject.getDataType(listType);
		for (RawObject rawObject : obectList) {
			DataObject dataObject = rawObject.createDataObject((Class<? extends DataObject>) listType);
			complexObject.add(dataObject);
		}
		
		return complexObject;
	}
	
	@Override
	public boolean read(ByteStream stream)
	{
		try{
			if (stream.moveNext())
	        {
	           String fname = parent + "$" + getName();
	
	           Format f = new Format(fname);
	           f.addMembers(stream);
	           Format.add(f);
	           return true;
	        }
			else
				return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean readMember(Member m, ByteStream stream) throws RuntimeException
	{
		Format f = Format.find(parent + DOLLAR + getName());
        if (f == null) return false;

        ObjectList ol = new ObjectList(f);

        while(true)
        {
        	RawObject rawObject = new RawObject(f);
        	
        	if (rawObject.read(stream))
        		ol.add(rawObject);
        	else
        		break;
        }
        
        m.setValue(ol);
        
        return true;
	}

	@Override
	public String toFormatString()
	{
		try
		{
			Format format = Format.find(parent + DOLLAR + getName());
			return Format.membersToString(format);
		}
		catch(Exception e)
		{
			return new String();
		}
	}
}

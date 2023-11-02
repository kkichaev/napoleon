package com.grsoft.network;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.FormatObjectList;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.util.UnicodUtils;
import com.grsoft.util.Util;

public class ListFormat extends MemberFormat
	implements BinaryFormatValue
{
	private Format members = new Format("");
	
	public ListFormat(String name)
	{
		super(name, null, "");
	}

	@Override
	public boolean readMember(Member m, ByteStream stream) throws RuntimeException {
		return false;
	}
	
	@Override
	public void setField(Field f, Member m, 
			Class<? extends DataObject> dataObjectClass, DataObject object) throws Exception {
		throw new RuntimeException(new IllegalAccessException("Can't set ListField"));
	}

	@Override
	public String toFormatString()
	{
		try
		{
			return Format.membersToString(members);
		} catch (RuntimeException e)
		{
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public byte[] valueToBinary(Object value)
	{
		ArrayList<byte[]> result = new ArrayList<byte[]>();
		
		List<?> listValue = (List<?>) value;
		
		for(int i=0; i < listValue.size(); i++)
		{
			DataObject item = (DataObject) listValue.get(i);
			FormatObjectList.appendObjectToArray(item, members, result);
		}
		
		if (listValue.size() == 0)
			try
			{
				result.add(UnicodUtils.toBytes("[]"));
			} catch (RuntimeException e)
			{
				e.printStackTrace();
			}
			
		return Util.ArrayListToBytes(result);
	}
	
	public void add(MemberFormat memberFormat)
	{
		members.add(memberFormat);
	}
}

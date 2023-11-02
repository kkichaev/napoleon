package com.grsoft.dataobjects;


import java.lang.reflect.Field;
import java.util.ArrayList;

import com.grsoft.napoleon.util.ConvertConstants;
import com.grsoft.network.BinaryFormatValue;
import com.grsoft.network.Format;
import com.grsoft.network.MemberFormat;
import com.grsoft.network.StringFormatValue;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.util.UnicodUtils;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class FormatObjectList
{
	protected Format format;
	private ArrayList<byte[]> value = new ArrayList<byte[]>();
	
	public FormatObjectList(DataObject dataObject) throws RuntimeException
	{
		this.format = createFormat(dataObject);
		appendObject(dataObject);
	}
	
	protected FormatObjectList() {}
	
	public void appendObject(DataObject dataObject) {
		appendObjectToArray(dataObject, format, value);
	}

	public byte[] getData()
	{
		try {
			value.add(0, UnicodUtils.toBytes(format.toString()));
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return Util.ArrayListToBytes(value);
	}

	protected Format createFormat(DataObject dataObject) throws RuntimeException {
		try
		{
			DataObjectInfo dataObjectInfo = DataObjectInfo.getInstance();
			Format result = new Format(dataObjectInfo.getTableName(dataObject.getClass())); 
			
			for (Field field : dataObjectInfo.getFields(dataObject.getClass()))
			{
				if (field.getName().equals(ExtrasConst.ROW_ID_FIELD))
					continue;
				
				MemberFormat memberFormat = MemberFormat.createFormat(field, dataObject.getClass());
				
				result.add(memberFormat);
			}
			
			return result;
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}
	
	public String getValues()
	{
		return value.toString();
	}
	
	public static void appendObjectToArray(DataObject dataObject, Format format,
			ArrayList<byte[]> value)
	{
		StringBuilder stringBuilder = new StringBuilder();
		Class<? extends DataObject> objClass = dataObject.getClass();
		try {
			stringBuilder.append(ConvertConstants.LEFT_BRACKET);
			
			for (MemberFormat memberFormat : format) {
				Field src = null;
				try {
					src = objClass.getField(memberFormat.getName());
				} catch (Exception e) { }
				
				if( src == null ) {
					// этот случай для онлайн проведения нужны фиктинвные поля 
					if( memberFormat.getMemberType() == String.class ) {
						stringBuilder.append("\"\"");
					} else if(memberFormat.getMemberType() == int.class ) {
						stringBuilder.append("0");
					}
					stringBuilder.append(ConvertConstants.COMMA);
					continue;
				}
				Object fieldValue = src.get(dataObject);
				
				if (memberFormat instanceof BinaryFormatValue)
				{
					value.add(UnicodUtils.toBytes(stringBuilder.toString()));
					stringBuilder.setLength(0);
					value.add(((BinaryFormatValue)memberFormat).valueToBinary(fieldValue));
					stringBuilder.append(ConvertConstants.COMMA);
				}
				else
					stringBuilder.append(
							((StringFormatValue)memberFormat)
							.valueToFormatString(fieldValue)).append(ConvertConstants.COMMA);
			}
			
			stringBuilder.deleteCharAt(stringBuilder.length()-1);
			stringBuilder.append(ConvertConstants.RIGHT_BRACKET);
			
			value.add(UnicodUtils.toBytes(stringBuilder.toString()));
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
		
	}
	
	@Override
	public String toString()
	{
		return format.toString() + getValues();
	}
}

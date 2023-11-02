package com.grsoft.dataobjects;
import com.grsoft.database.UploadSource;
import com.grsoft.aceteam.R;


import java.lang.reflect.Field;
import java.util.ArrayList;

import com.grsoft.napoleon.util.ConvertConstants;
import com.grsoft.network.BinaryFormatValue;
import com.grsoft.network.DataUploader;
import com.grsoft.network.Format;
import com.grsoft.network.MemberFormat;
import com.grsoft.network.StringFormatValue;
import com.grsoft.network.UploadContext;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.exception.UploadException;
import com.grsoft.network.util.UnicodUtils;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class FormatObjectList {
	protected Format format;
	private ArrayList<byte[]> value = new ArrayList<byte[]>();
	
	public FormatObjectList(DataObject dataObject, UploadContext context) throws RuntimeException, UploadException {
		this.format = createFormat(dataObject, context);
		appendObject(dataObject, context);
	}
	
	protected FormatObjectList() {}
	
	public void appendObject(DataObject dataObject, UploadContext context) throws UploadException {
		appendObjectToArray(dataObject, format, value, context);
	}

	public byte[] getData() {
		try {
			value.add(0, UnicodUtils.toBytes(format.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Util.ArrayListToBytes(value);
	}

	protected Format createFormat(DataObject dataObject, UploadContext context) throws RuntimeException {
		try {
			Format result = new Format(dataObject.getTableName()); 
			
			for (Field field : dataObject.getFields()) {
				if (field.getName().equals(ExtrasConst.ROW_ID_FIELD))
					continue;
				
				MemberFormat memberFormat = MemberFormat.createFormat(field, dataObject.getClass(), context);
				if(memberFormat != null) {
					result.add(memberFormat);
				}
			}
			
			return result;
		}
		catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}
	
	public String getValues()
	{
		return value.toString();
	}
	
	public static void appendObjectToArray(DataObject dataObject, Format format, ArrayList<byte[]> value, UploadContext context) throws UploadException {
		if( dataObject instanceof DataUploader) {
			((DataUploader)dataObject).upload(context);
		}

		StringBuilder stringBuilder = new StringBuilder();
//		Class<? extends DataObject> objClass = dataObject.getClass();
		try {
			stringBuilder.append(ConvertConstants.LEFT_BRACKET);
			
			for (MemberFormat memberFormat : format) {
				Field src = dataObject.getField(memberFormat.getName());
				if(src.getAnnotation(UploadSource.class) != null) {
					continue;
				}

//				try {
//					src = objClass.getField(memberFormat.getName());
//				} catch (Exception e) { }
				
				if( src == null ) {
					Class<?> memberType = memberFormat.getMemberType(); 
					// этот случай для онлайн проведения нужны фиктинвные поля 
					if( memberType == String.class ) {
						stringBuilder.append("\"\"");
					} else if(memberType == int.class || memberType == long.class ) {
						stringBuilder.append("0");
					}
					stringBuilder.append(ConvertConstants.COMMA);
					continue;
				}
				Object fieldValue = src.get(dataObject);
				
				if (memberFormat instanceof BinaryFormatValue) {
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
		catch(Exception exception) {
			exception.printStackTrace();
		}
		
	}
	
	@Override
	public String toString()
	{
		return format.toString() + getValues();
	}
}

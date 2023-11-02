package com.grsoft.database;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.os.Debug;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.util.ConvertConstants;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.exception.TypeNotImplemented;
import com.grsoft.types.Feature;
import com.grsoft.types.FieldOrder;

public class DataObjectUtils {
	
	static boolean IsFeaturePresents(Field field) {
		Feature ftr = field.getAnnotation(Feature.class);
		if( ftr == null )
			return true;
		
		boolean ret = true;
		Field f = null;
		try {
			f = Class.forName("com.grsoft.napoleon.Features").getField(ftr.feature());
			if( f != null ) {
				Object val = f.get(field);
				ret = (Boolean)val;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static Field[] getUpdatebleFields(Class<? extends DataObject> dataObjectType) {
		Field[] fields = dataObjectType.getFields();
		ArrayList<Field> f = new ArrayList<Field>();
		boolean checkOrder = false, first = true;
		for(Field field : fields) {
			int mdf = field.getModifiers();
			if( (mdf & (Modifier.FINAL | Modifier.STATIC)) != 0 || (mdf & Modifier.PUBLIC) == 0 ) continue;
			
			if( first ) {
				checkOrder = (field.getAnnotation(FieldOrder.class) != null);
				first = false;
			}
			
			if( IsFeaturePresents(field))
				f.add(field);
		}
		
		if( checkOrder ) {
			Collections.sort(f, new Comparator<Field>() {
				@Override public int compare(Field lhs, Field rhs) {
					int lo = fieldOrder(lhs);
					int ro = fieldOrder(rhs);
					return lo - ro;
				}
			});
		}
		Field[] ret = {};
		return f.toArray(ret);
	}
	
	static private int fieldOrder(Field f) {
		FieldOrder fo = f.getAnnotation(FieldOrder.class);
		return fo.order();
	}
	
	/**
	 * Создает список полей без rowid
	 * @param dataObjectType
	 * @return
	 */
	static String getFields(Class<? extends DataObject> dataObjectType)
	{
		StringBuilder result = new StringBuilder();
		
		for(Field field : DataObjectInfo.getInstance().getFields(dataObjectType))
		{
			String name = field.getName();
			//
			//if( name.compareTo(ExtrasConst.ROW_ID_FIELD) == 0 ) continue;
			
			result.append("\"" + name + "\",");
		}
			
		result.deleteCharAt(result.length() - 1);
		
		return result.toString();
	}

	@SuppressWarnings("unchecked")
	static public <T extends DataObject> List<T> readList(Class<T> dataType, ByteBuffer stream) throws RuntimeException {
		Debug.startMethodTracing("read_list1");
		
		ArrayList<T> list = new ArrayList<T>();
		Class<? extends T> itemType = (Class<? extends T>) DbObject.getDataType(dataType);
		try {
			while (stream != null && stream.remaining() > 0) {
				T instance = itemType.newInstance();
				assignFieldsByType(stream, instance);
				list.add(instance);
			}
			
			Debug.stopMethodTracing();

			return list;
		}
		catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	static private void assignFieldsByType(ByteBuffer stream, DataObject instance) throws IllegalAccessException, TypeNotImplemented {
		for(Field field: DataObjectInfo.getInstance().getFields(instance.getClass())) {
			Class<?> fieldType = field.getType();
			if (fieldType == int.class) {
				int val = stream.getInt();
				field.set(instance, val);
			} else if (fieldType == byte[].class) {
				int size = stream.getInt();
				byte val[] = new byte[size];
				stream.get(val);
				field.set(instance, val);
			} else if (fieldType == String.class) {
				StringBuilder sb = new StringBuilder();
				while( true ) {
					char c = (char) (stream.get() | (stream.get() << 8));
					if( c == ConvertConstants.EOS ) break;
					sb.append(c);
				}
				field.set(instance, sb.toString());
			} else if(fieldType == Date.class) {
				Date val = new Date(stream.getLong());
				field.set(instance, val);
			} else if(fieldType == List.class) {
				try {
					Class<? extends DataObject> paramType = DataObjectInfo.getInstance().getListType(instance.getClass(), field.getName()); 
					List<? extends DataObject> list = readList(paramType, stream);
					field.set(instance, list);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else
				throw new TypeNotImplemented(field.getType()); 
		}
	}
	
	static public <T extends DataObject> List<T> readList(Class<T> dataType, byte[] stream) throws RuntimeException
	{
		return readList(dataType, stream, new StreamPos());
	}

	@SuppressWarnings("unchecked")
	static public <T extends DataObject> List<T> readList(Class<T> dataType, byte[] stream, StreamPos sp) throws RuntimeException
	{
//		Debug.startMethodTracing("read_list_bytes3");
		int elemCount = -1;
		
		if (listHasCounter(stream, sp)){
			elemCount = getInt(stream, sp.getPos());
			sp.addPos(4); // Counter
			sp.addPos(4); // Version 
		}
		
		ArrayList<T> list = new ArrayList<T>();
		Class<? extends T> itemType = (Class<? extends T>) DbObject.getDataType(dataType);
		Field[] fields = DataObjectInfo.getInstance().getFields(itemType);
		try
		{
			while (stream != null && stream.length > sp.getPos())
			{
				if (elemCount != -1 && elemCount-- <= 0)
					break;

				T instance = itemType.newInstance();
				assignFieldsByType(stream, instance, sp, fields);
				list.add(instance);
				
			}
			
//			Debug.stopMethodTracing();
			return list;
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}
	
	public static byte[] counterMarker = {0x12, 0x14, 0x67, 0x44, 0x76, 0x23};
	
	static private boolean listHasCounter(byte[] stream, StreamPos sp){
		boolean result = true;
		
		if (stream != null && sp != null){
			int index = sp.getPos();
			
			for (int i = 0; i < counterMarker.length; i ++) {
				if(stream.length <= index || counterMarker[i] != stream[index++]){
					result = false;
					break;
				}
			}	
			
			if (result)
				sp.addPos(counterMarker.length);
		}else
			result = false;
		
		return result;
	}
	
	static private int getInt(byte[] stream, int idx){
		return (stream[idx++] << 24) | 
				((stream[idx++] << 16) & 0xFF0000) | 
				((stream[idx++] << 8) & 0xFF00) | 
				(stream[idx++] & 0xFF);
	}
	
	static private int assignFieldsByType(byte[] stream, DataObject instance, StreamPos sp, Field[] fields) throws IllegalAccessException, TypeNotImplemented
	{
		int idx = sp.getPos();
		for(Field field: fields)
		{
			Class<?> fieldType = field.getType();
			if (fieldType == int.class) {
				int val = getInt(stream, idx);
				idx += 4;
				field.set(instance, val);
			} else if (fieldType == byte[].class) {
				int val = getInt(stream, idx);//((stream[idx++] << 8) & 0xFF00) | (stream[idx++] & 0xFF);
				idx += 4;		
				byte[] buf = new byte[val];
				System.arraycopy(stream, idx, buf, 0, val);
				idx += val;
				field.set(instance, buf);
				
			} else if (fieldType == String.class) {
				StringBuilder sb = new StringBuilder();
				while( true ) {
					char c = (char) (stream[idx++] | (stream[idx++] << 8));
					if( c == ConvertConstants.EOS ) break;
					sb.append(c);
				}
				field.set(instance, sb.toString());
		
			} else if(fieldType == Date.class) {
				int valHigh = (stream[idx++] << 24) | ((stream[idx++] << 16) & 0xFF0000) | ((stream[idx++] << 8) & 0xFF00) | (stream[idx++] & 0xFF);
				int valLow = (stream[idx++] << 24) | ((stream[idx++] << 16) & 0xFF0000) | ((stream[idx++] << 8) & 0xFF00) | (stream[idx++] & 0xFF);
				long val = ((long)valLow & 0xFFFFFFFFl) | ((long)valHigh << 32);
				Date d = new Date(val);
				field.set(instance, d);
				
			} else if(fieldType == List.class) {
				try {
					sp.setPos(idx);
					Class<? extends DataObject> paramType = DataObjectInfo.getInstance().getListType(instance.getClass(), field.getName()); 
					List<? extends DataObject> list = readList(paramType, stream, sp);
					field.set(instance, list);
					idx = sp.getPos();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
				throw new TypeNotImplemented(field.getType()); 
		}
	
		sp.setPos(idx);
		return idx;
	}
	
//	static private int assignDateField(byte[] buf, StreamPos pos, DataObject instance, Field field) throws IllegalAccessException
//	{
//		long ms = Util.bytesToLong(buf, pos.getPos());
//		Date val = new Date(ms);
//		field.set(instance, val);
//		pos.addPos(Util.LONG_SIZE);
//		return pos.getPos();
//	}
//
//	static private int assignArrayField(byte[] buf, StreamPos pos, DataObject instance, Field field) throws IllegalAccessException
//	{
//		int size = Util.bytesToInt(buf, pos.getPos());
//		pos.addPos(Util.INT_SIZE);
//		byte val[] = new byte[size];
//		System.arraycopy(buf, pos.getPos(), val, 0, size);
//		field.set(instance, val);
//		pos.addPos(size);
//		return pos.getPos();
//	}

	static public boolean hasSource(Field field){
		BlobSource blobSource = field.getAnnotation(BlobSource.class);
		
		return blobSource != null;
	}
}

class StreamPos {
	int pos;
	
	StreamPos() { pos = 0; }
	
	StreamPos(int pos) {
		this.pos = pos;
	}
	
	int getPos() { return pos; }
	void setPos(int np) { pos = np; }
	void addPos(int addPos) { pos += addPos; }
}
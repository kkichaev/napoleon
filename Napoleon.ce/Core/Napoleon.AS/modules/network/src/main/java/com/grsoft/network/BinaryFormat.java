package com.grsoft.network;

import com.grsoft.database.DataObjectUtils;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.napoleon.util.ConvertConstants;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.util.UnicodUtils;
import com.grsoft.util.SrcDataCounter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;


public class BinaryFormat extends MemberFormat
	implements BinaryFormatValue
{

	public interface Reader {
		void read(Member m, ByteStream stream);
	}
	
	public static Reader BinaryReader = null;
	
	public BinaryFormat(String name){
		super(name, byte[].class, ":b");
	}

	@Override
	public boolean readMember(Member m, ByteStream stream)
	{
		if( BinaryReader != null ) {
			BinaryReader.read(m, stream);
		} else {
			byte[] bytes = toBytes(stream);
			if (bytes != null)
				m.setValue(bytes);
		}
		return true;
	}

	protected byte[] toBytes(ByteStream stream) {
		StringBuilder size = new StringBuilder();
		if (!stream.copyUntill(size, ConvertConstants.COLON))
			return null;
		
		byte[] result = new byte[0];
		int len = Integer.parseInt(size.toString());
		if(len == 0) {
			 stream.moveNext();
			 return result;
		}
		
		result = new byte[len];
		if (!stream.copyBytes(result)) // add moveNex to copyBytes
			result = new byte[0];
		
		return result;
	}

	@Override
	public void setField(Field f, Member m, Class<? extends DataObject> dataObjectClass, DataObject object) throws Exception {
		if(DataObjectUtils.hasSource(f)) {
			Object val = m.getValue();
			if(val instanceof byte[]) {
				try{
					byte[] data = (byte[]) val;
					File file = new File(Path.getDataDir(), Integer.toString(SrcDataCounter.getValue()));
					OutputStream fos = new BufferedOutputStream(new FileOutputStream(file));
					fos.write(data);
					fos.close();
					f.set(object, file.getAbsolutePath().toString().getBytes());
					return;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		super.setField(f, m, dataObjectClass, object);
	}

	@Override
	public byte[] valueToBinary(Object value)
	{
		if(value == null || (((byte[])value).length == 0))
			return UnicodUtils.toBytes("0:");
		
		String header = String.format("%d:",((byte[])value).length); 
		byte[] headerBytes = UnicodUtils.toBytes(header);		
		byte[] bodyBytes = (byte[]) value;
		
		int len = headerBytes.length + bodyBytes.length;
		
		if ((len % 2) != 0)
			len++;
		
		byte[] result = new byte[len];
		
		System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
		
		if (bodyBytes != null)
			System.arraycopy(bodyBytes, 0, result, headerBytes.length, bodyBytes.length);
		
		return result;
	}

}

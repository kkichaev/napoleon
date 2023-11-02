package com.grsoft.network;

import com.grsoft.napoleon.util.ConvertConstants;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.util.UnicodUtils;


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
		if (!stream.copyUntill(size, ConvertConstants.COLON) || !stream.moveNext())
			return null;
		
		byte[] result = null;
		int len = Integer.parseInt(size.toString());
		
		if(len > 0) {
			result = new byte[len];
			if (!stream.copyBytes(result))
				return null;
		}
		return result;
	}
	
	@Override
	public byte[] valueToBinary(Object value)
	{
		String header = value == null ? "0:"
				: String.format("%d:",((byte[])value).length); 
		
		byte[] headerBytes;
		
		try
		{
			headerBytes = UnicodUtils.toBytes(header);
		} catch (RuntimeException e)
		{
			e.printStackTrace();
			return null;
		}
		
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

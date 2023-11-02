package com.grsoft.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class PacketOperator
{
	private static PacketOperator instance;
	
	private PacketOperator(){}
	
	private static PacketOperator getPacketOperator()
	{
		if (instance == null)
			instance = new PacketOperator();
		
		return instance;
	}
	
	class Operator
	{
		public String name;
		public Decoder decoder;
		public Encoder encoder;
		
		public Operator(String name, Decoder decoder, Encoder encoder)
		{
			this.name = name;
			this.decoder = decoder;
			this.encoder = encoder;
		}
	}
	
	private Operator[] operators = new Operator[]{
		new Operator(ByteStream.GZIP_TAG, new Decompress(), new Comprees()),
		new Operator(ByteStream.CRC_TAG, new CheckCRC32(), new SetCRC32())
	};
	
	public static EncodeResult encode(byte[] src, String opName)
	{
		String param = "";
		byte[] dest = null;
		
		for(Operator op : getPacketOperator().operators)
		{
			if(op.name.equals(opName))
			{
				String val = "";
				EncodeResult encodeResult = op.encoder.encode(src, val);
				val = encodeResult.getParam();
				dest = encodeResult.getBuf();
				param = opName + val + ";";
				break;
			}
		}
		
		return new EncodeResult(param, dest);
	}

	public static byte[] decode(byte[] src, PacketOptions option) {
		byte[] dest = null;
		Operator[] operators = getPacketOperator().operators; 
		for(Operator op : operators)
		{
			if(op.name.equals(option.getName()))
			{
				dest = op.decoder.decode(src, option.getValue());
				break;
			}
		}
		
		return dest;
	}
	
	
}

class Decompress implements Decoder
{

	@Override
	public byte[] decode(byte[] src, String value)
	{
		final int BUF_SIZE = Integer.parseInt(value);
		byte[] dest = new byte[BUF_SIZE];
		
		ByteArrayInputStream ms = new ByteArrayInputStream(src);
		InflaterInputStream decompress = new InflaterInputStream(ms);
		
		
		try
		{
			int offset = 0;
			int bytes_read = 0;
			
			while(offset < BUF_SIZE)
			{
				bytes_read = decompress.read(dest, offset, BUF_SIZE - offset);
				
				offset += bytes_read;
			}
			
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		
		return dest;
	}
	
}

class Comprees implements Encoder
{

	@Override
	public EncodeResult encode(byte[] src, String value)
	{
		int len = src.length;
		if (len < 1024) len = 1024;
		byte[] dest = null;
		ByteArrayOutputStream ms = new ByteArrayOutputStream();
		DeflaterOutputStream compress = new DeflaterOutputStream(ms);

		try
		{
			compress.write(src, 0, src.length);
			compress.finish();
			dest = ms.toByteArray();
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		
		value = "(" + Integer.toString(src.length) + ")";
		
		return new EncodeResult(value, dest);
	}
}

class CheckCRC32 implements Decoder
{

	@Override
	public byte[] decode(byte[] src, String value)
	{
		CRC32 crc = new CRC32();
		crc.update(src);
		int c1 = (int)crc.getValue();
		int c2 = Integer.parseInt(value);
		
		if (c1 != c2)
			return null;
		
		return src;
	}
	
}

class SetCRC32 implements Encoder
{

	@Override
	public EncodeResult encode(byte[] src, String value)
	{
		CRC32 crc = new CRC32();
        crc.update(src);
        int crcVal = (int)crc.getValue();
        value = "(" + Integer.toString(crcVal) + ")";

        return new EncodeResult(value, src);
	}
	
}
/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 *
 *
 * kki   14/09/2010   creating
 */

package com.grsoft.network;

import static com.grsoft.util.Debug.dbgPrint;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.napoleon.util.ConvertConstants;
import com.grsoft.network.exception.EndOfStream;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.exception.UnexpectedCharInStream;
import com.grsoft.network.util.UnicodUtils;

public class ByteStream
{
	public static final String PACKET_STR_TAG = "GRPACKET";
	public static final String DATA_TAG = "DATA";
	public static final String GZIP_TAG = "GZIP";
	public static final String CRC_TAG = "CRC";
	
	private int curSym = 0;
	public Buffer bytes;
	private int received = 0;
	
	private final char END_OF_STRING = '\0';
	
	public ByteStream(byte[] bytes)
	{
		this.bytes = new Buffer(bytes);
	}

	public int getReceived() { return received; }
	
	public char current()
	{
		return getCharAt(curSym);
	}

	public char next()
	{
		return getCharAt(curSym + 2);
	}
	
	public static ByteStream createStreamFromDataObjects(List<? extends DataObject> dataObjects )
	{
		return null;
	}
	
	private char getCharAt(int pos)
	{
		return !isEOS()
			? readCharFromBuffer(pos)
			: END_OF_STRING;
	}

	private char readCharFromBuffer(int pos)
	{
		return (pos <= bytes.getLength() - 2)
			? UnicodUtils.readChar(bytes.getData(), pos, END_OF_STRING)
			: (char)bytes.getData()[pos];
	}

	public boolean moveNext()
	{
		if (isEOS()) return false;
		
		if (curSym < bytes.getLength() - 2)
			curSym += 2;
		else
			curSym = bytes.getLength();
		
		return true;
	}
	
	public boolean eatWhite()
	{
		while(!isEOS())
		{
			char sym = current();
			
			if(Character.isWhitespace(sym))
				return true;
			
			moveNext();
		}
		
		return false;
	}
	
	public boolean copyUntill(StringBuilder outRes, char stopSym )
	{
		while(!isEOS())
		{
			char sym = current();
			
			if (sym == stopSym) 
				break;
			
			outRes.append(sym);
			moveNext();
		}
		
		return !isEOS();
	}
	
	public String readStringTillChar(char stopSym) throws RuntimeException
	{
		StringBuilder result = new StringBuilder();
		
		if (!copyUntill(result, stopSym))
			throw new RuntimeException(new EndOfStream());
		
		return result.toString();
	}
	
	public boolean writeBytes(FileOutputStream fos, int size) throws IOException {
		if (bytes.getLength() - curSym < size )
			return false;
		
		fos.write(bytes.getData(), curSym, size);

		curSym += size;		
		if(size % 2 != 0) 
			curSym++;
		
		return true;
	}
	
	public boolean copyBytes(byte[] dest)
	{
		int sz = dest.length;
		
		if (bytes.getLength() - curSym < sz )
			return false;
		
		System.arraycopy(bytes.getData(), curSym, dest, 0, sz);
		
		curSym += sz;
		
		if(sz % 2 != 0) 
			curSym++;
		
		return true;
	}
	
	private static boolean waitPacket(InputStream stream) throws RuntimeException
	{
		byte[] buf = new byte[UnicodUtils.toBytes(PACKET_STR_TAG).length];
		
		while(true)
		{
			int i = 0;
			try
			{
				int len = stream.read(buf, 0, buf.length);
				if(len < buf.length)
					return false;
				
				for(i = UnicodUtils.toBytes(PACKET_STR_TAG).length - 1; i >= 0; i--)
					if (buf[i] != UnicodUtils.toBytes(PACKET_STR_TAG)[i])
						break;
				
				if(i < 0)
					break;
			} catch (IOException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
	}
	
	static boolean readTill(StringBuilder outStr, InputStream stream, char stopSym)
	{
		byte[] buf = new byte[2];
		
		while(true)
		{
			try
			{
				if(stream.read(buf,0,2) != 2)
					return false;
				
				ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
				InputStreamReader reader = new InputStreamReader(inputStream);
				char sym = (char)reader.read();
				
				if (sym == stopSym)
					break;
				
				outStr.append(sym);
				
			} catch (IOException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
	}
	
	private static int readOptions(List<PacketOptions> ops, InputStream stream) 
		throws RuntimeException
	{
		int receivedBytes = 0;
		if (!waitPacket(stream))
			return receivedBytes;
		
		while(true)
		{
			StringBuilder sb = new StringBuilder();
			
			if (!readTill(sb, stream, ';')) 
				return 0;
			
			if (sb.toString().equals(DATA_TAG))
				break;	
			
			PacketOptions op = new PacketOptions(sb.toString());
			receivedBytes += (sb.length() * 2);
			ops.add(op);
		}
		
		return (ops.size() != 0) ? receivedBytes : 0;
	}
	
	public boolean isEOS()
	{
		return (bytes == null ) ? true  : curSym >= bytes.getLength();
	}

	public int send(OutputStream stream, String operations) throws RuntimeException
	{
		String head = DATA_TAG + ";";
		if(operations.indexOf(GZIP_TAG) < 0
				&& operations.indexOf(CRC_TAG) < 0)
			operations += CRC_TAG;
		
		int sp = 0;
		int len = operations.length();
		int sendSize = 0;
		
//		try {
//			final String FILE_LOG = "test" + Integer.toString(SrcDataCounter.getValue()) + ".log";
//			File f = new File( Environment.getExternalStorageDirectory().getPath(), FILE_LOG);
//			OutputStream os = new FileOutputStream(f);
//			os.write(bytes.getData());
//			os.close();
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		
		while(true)
		{
			int ep = operations.indexOf(':',sp);
			String op = operations.substring(sp, (ep < 0) ? len - sp : ep - sp);
			
			EncodeResult encodeResult = PacketOperator.encode(bytes.getData(), op);
			String param = encodeResult.getParam();
			bytes = new Buffer(encodeResult.getBuf());
			
			head = param + head;
			
			if (ep < 0 )
				break;
			
			sp = ep + 1;
		}
		
		String pktTag = PACKET_STR_TAG + "(" + Integer.toString(bytes.getLength()) + ");";
		head = pktTag + head;
		
		dbgPrint("head >>> %s", head);
		
//		dbgWriteFileLn(head);
//		dbgWriteFileLn(bytes.getData(), true);
		
		byte[] headBytes = new byte[]{};
		headBytes = UnicodUtils.toBytes(head);
		
		try
		{
			stream.write(headBytes, 0, headBytes.length);
			stream.write(bytes.getData(), 0, bytes.getLength());
			sendSize = headBytes.length + bytes.getLength();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return sendSize;
	}

	public static ByteStream receive(InputStream stream) throws RuntimeException
	{
		ArrayList<PacketOptions> ops = new ArrayList<PacketOptions>();
		
		int rcvd = readOptions(ops, stream);
		if( rcvd == 0 )
			return null;
		
		int packetSize = Integer.parseInt(ops.get(0).getValue());
		ops.remove(0);
		
		rcvd += packetSize;
		byte[] packet = readFully(stream, packetSize);
		
		ByteStream res = decodePacket(packet, ops);
		if( res != null )
			res.received = rcvd;
		
		return res;
	}
	
	private static byte[] readFully(InputStream stream, int size) throws RuntimeException
	{
		try
		{
			byte[] result = new byte[size];
			
			DataInputStream dataInputStream = new DataInputStream(stream);
			dataInputStream.readFully(result);
		
			return result;
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
		
	}
	private static ByteStream decodePacket(byte[] packet, ArrayList<PacketOptions> ops)
	{
		for (PacketOptions op : ops)
		{
			packet = PacketOperator.decode(packet, op);
			if (packet == null)
				break;
		}
		
		/*------------------------------ DEBUG SECTION ---------------------------*/
//		try{
//			File f = new File(Environment.getExternalStorageDirectory(), "receive.txt");
//			FileOutputStream fos = new FileOutputStream(f);
//			fos.write(packet);
//			fos.close();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		/*------------------------------ DEBUG SECTION ---------------------------*/
		
		return (packet != null) ? new ByteStream(packet) : null;
	}
	
	@Override
	public String toString()
	{
		try
		{
			return new String(bytes.getData(), "UTF_16LE");
		}
		catch(Exception exception)
		{
			return new String("ByteStream can't representation as String" + exception.getMessage());
		}	
	}
	
	public String readString() throws UnexpectedCharInStream
	{
		ByteStreamReader byteStreamReader = new ByteStreamReader(this);
		return byteStreamReader.readString();
	}
	
	public int getSize()
	{
		return bytes.getLength();
	}
	
	public int getPosition()
	{
		return curSym;
	}
	
	public boolean isContinues(){
		final byte[] CONTINUE_FLAG = new byte[]{83, 0, 116, 0, 114, 0, 101, 0, 97, 0, 109, 0, 
				67, 0, 111, 0, 110, 0, 116, 0, 105, 0, 110, 0, 117, 0, 101, 0, 91, 0, 99, 0, 
				111, 0, 110, 0, 116, 0, 105, 0, 110, 0, 117, 0, 101, 0, 58, 0, 110, 0, 93, 0, 
				91, 0, 49, 0, 93, 0};
		final int CONTINUE_FLAG_LENGH = CONTINUE_FLAG.length;
		
		try{
			if (bytes.getLength() > CONTINUE_FLAG_LENGH){
				byte[] test = new byte[CONTINUE_FLAG_LENGH];
				
				System.arraycopy(bytes.getData(), bytes.getLength() - CONTINUE_FLAG_LENGH, 
						test, 0, CONTINUE_FLAG_LENGH);
				
				return Arrays.equals(test, CONTINUE_FLAG);
			}
			
			return false;
		}catch(Exception e){
			return false;
		}
	}
}

class Buffer
{
	private byte[] data;
	private int length; 
	
	public Buffer(byte[] data)
	{
		this.data = data;
		this.length = data.length;
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	public int getLength()
	{
		return length;
	}
}

class ByteStreamReader
{
	ByteStream context;
	
	public ByteStreamReader(ByteStream context)
	{
		this.context = context;
	}
	
	public String readString() throws UnexpectedCharInStream
	{
		if (context.current() != ConvertConstants.QUOTE)
	           throw new UnexpectedCharInStream(context);

	    context.moveNext();

	    String result = readBodyString();
	        
	    context.moveNext();
	        
	    return result.toString();
	}
	
	private String readBodyString() throws UnexpectedCharInStream
	{
		StringBuilder result = new StringBuilder(32);
		
		while (context.current() != ConvertConstants.QUOTE)
        {
        	if (context.isEOS())
        	   throw new UnexpectedCharInStream(context);
           
        	char sym = context.current();
        	
        	if (sym == ConvertConstants.SLASH)
        	{
        		if (!context.moveNext())
        			throw new UnexpectedCharInStream(context);
        		
        		sym = convertStreamChar();
        	}
          
        	result.append(sym);
        	context.moveNext();
        }
		
		return result.toString();
	}
	
	private char convertStreamChar() throws UnexpectedCharInStream
	{
		switch (context.current())
        {
			case '\\': 
        	case '/': 
        	case '"': 
        	   return context.current();
        	   
        	case 'b': 
        	   return '\b';
        	   
        	case 'f': 
        	   return '\f';
        	   
        	case 'n': 
        	   return '\n';
        	   
        	case 'r': 
        	   return '\r'; 
        	   
        	case 't': 
        	   return '\t';
        	
        	default:
        	   throw new UnexpectedCharInStream(context);
         }
	}
	
	
}

package com.grsoft.napoleon.modules;
import com.grsoft.aceteam.R;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;

import android.content.Context;

import com.grsoft.database.Hitching;

public class CostManagerImpl implements CostManager {

	public static final String FILE_NAME = "Costs.data";
		
	private CostType[] costTypes = null;
	private String fileName;
	private HashMap<String, Integer> price = new HashMap<String, Integer>();
	
	int dataSize;
	
	public CostManagerImpl() {
		dataSize = 4;
	}
	
	protected CostManagerImpl(int dataSize) {
		this.dataSize = dataSize;
	}
	
	/**
	 * проверяем принимались ли цены при синхронизации
	 */
	protected boolean costReaded = false;
	
	@Override public Hitching getReceiveHitching(Context context) { 
		costTypes = null;
		price.clear();
		
		costReaded = false;
		return new CostHitching(context, this);
	}
	
	public void setCostReaded(boolean readed) { costReaded = readed; }
	public boolean isCostReaded() { return costReaded; }

	@Override
	public int getCost(String id, int costType) {
		int c = 0;
		
		try {
			if( costTypes != null && costType < costTypes.length && costType >= 0 ) {
				Integer offset = price.get(id);
				if( offset != null ) {
					byte[] buf = new byte[dataSize];
					RandomAccessFile file = new RandomAccessFile(fileName, "r");
					file.seek(offset + costType * dataSize);
					file.read(buf);
					file.close();
					c = readData(buf);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return c;
	}
	
	protected int readData(byte[] buf) {
		return (buf[3]) << 24 | (buf[2]&0xff) << 16 | (buf[1]&0xff) <<  8 | (buf[0]&0xff);
	}
	
	private String readString(byte[] buf, int offset) throws Exception {
		
		int i = offset;
		for( ; buf[i] != 0; i++ )
			;
		
		return new String(buf, offset, i - offset, "windows-1251");		
	}

	private byte[] _bbuf = new byte[4];
	private int readUnsignedShort(DataInputStream dis) throws Exception {
		dis.readFully(_bbuf, 0, 2);
		return ((_bbuf[1]&0xff) << 8 | (_bbuf[0]&0xff));
	}
	
	private int readInteger(DataInputStream dis) throws Exception {
		dis.readFully(_bbuf, 0, 4);
		 return ((_bbuf[3]) << 24 | (_bbuf[2]&0xff) << 16 | (_bbuf[1]&0xff) <<  8 | (_bbuf[0]&0xff));
}
	
	@Override
	public void initCost(Context context) {
		final int HEAD_SIZE = 2 + 2 + 4 + 4;
		
		if( costTypes != null )
			return;
		
		fileName = context.getFilesDir().getAbsolutePath() + "/" + FILE_NAME;
		File file = new File(fileName);
		if( file.exists() ) {
			try {
				int priceCount;
				int costCount;
				int costTypeOffset;
				int priceOffset;
				
				FileInputStream stream = new FileInputStream(file);
				DataInputStream dis = new DataInputStream(stream);

				priceCount = readUnsignedShort(dis);
				costCount = readUnsignedShort(dis);
				costTypeOffset = readInteger(dis);
				priceOffset = readInteger(dis);
				
				int len = costTypeOffset - HEAD_SIZE;
				byte[] buf = new byte[len];
				dis.readFully(buf);
				
				// read price item name
				int offset = 0;
				int curOffset = priceOffset;
				while(priceCount-- > 0) {
					String pv = readString(buf, offset);
					offset += pv.length() + 1;
					price.put(pv, curOffset);
					curOffset += costCount * dataSize;
				}

				// read cost types
				len = priceOffset - costTypeOffset;
				buf = new byte[len];
				dis.readFully(buf);
				int i = 0;
				offset = 0;
				costTypes = new CostType[costCount];
				while(costCount-- > 0) {
					String id = readString(buf, offset);
					offset += id.length() + 1;
					String name = readString(buf, offset);
					offset += name.length() + 1;
					
					costTypes[i++] = new CostType(id, name);
				}
								
				stream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public int getCost(String id, String costType) { return getCost(id, getCostIndex(costType)); }

	@Override
	public int getCostIndex(String costType) {
		if( costTypes != null ) {
			for( int i=0; i<costTypes.length; i++ ) {
				if( costTypes[i].id.compareTo(costType) == 0)
					return i;
			}
		}
		return -1;
	}

	@Override public CostType[] getCostTypes() { return costTypes; }

}

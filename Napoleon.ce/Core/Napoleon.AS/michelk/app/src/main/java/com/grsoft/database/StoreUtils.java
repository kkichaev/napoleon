package com.grsoft.database;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PresentEx;
import com.grsoft.dataobjects.impl.CRCDatInfoImpl;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class StoreUtils {
	public static final String CRC_DAT="crc.dat";
	public static final String PREF_NAME = "PresentUtils.PREFERENCES";
	public static final String HASHCRCDAT = "crcdat"; 
	
	public static void initPresentation(){
		String path = ((CfgNplW)ConfigManager.getConfig()).presentpath;
		
		CRCDatInfoImpl impl = new CRCDatInfoImpl();
		long crc = impl.readCRC();
		
		if (path.length() > 0) {
			File file = new File(path, CRC_DAT);

			if (file.isFile() && (crc == -1 || getCRC32(file) != crc))
				if (recreateBaseData(file))
					impl.writeCRC(getCRC32(file));
		}
	}

	private static long getCRC32(File file){
		CRC32 crc = new CRC32();
		
			crc.reset();
			InputStream is = null;
			
			try{
				is = new BufferedInputStream(new FileInputStream(file));
				
				byte[] data = new byte[(int)file.length()];
				is.read(data);
				crc.update(data);
			}catch(Exception e) {e.printStackTrace();
			}finally{
				if(is != null)
					try{
						is.close();
					}catch(Exception e){}
			}
			
		return 	crc.getValue();
	}
	
	private static boolean recreateBaseData(File src) {
		boolean result = false;
		try{
			if (src != null){
				SQLiteDatabase database = DataBaseManager.getDataBase();
				SQLiteStatement statement = database.compileStatement("SELECT folderid from price WHERE id=?");
				DbWriter.checkDBTable(Present.class);
				database.execSQL("delete from " + DataObjectInfo.getInstance().getTableName(Present.class));
				
				BufferedReader r = new BufferedReader(new FileReader(src));
				
				String line= null;
				DbWriter dbr = new DbWriter();
				String path = ((CfgNplW)ConfigManager.getConfig()).presentpath;
				
				while((line = r.readLine()) != null){
					StoreDat d = StoreDat.parse(line);
					
					if (d != null)
						for(String id : d.items){
							PresentEx p = new PresentEx();
							
							try{
								p.id = id;
								p.name = d.name;
								p.crc = d.crc;
								statement.bindString(1, id);
								
								try{
									p.folderId = statement.simpleQueryForString();
								}catch(Exception e){e.printStackTrace();}
								
								p.photoPath = path + "/" + d.name;
								dbr.insertRecord(p);
							}catch(Exception e){
								continue;
							}
						}
				}
				
				r.close();
				dbr.close();
				statement.close();
				result = true;
			}
		}catch(Exception e){ e.printStackTrace(); }
		
		return result;
	}

	public static void commitCRCChanges() {
		Collection<StoreDat> data = readFromDB();
		String path = ((CfgNplW)ConfigManager.getConfig()).presentpath;
		File src = new File(path, CRC_DAT);
		
		try{
			BufferedWriter w = new BufferedWriter(new FileWriter(src, false));
			
			for (StoreDat d : data){
				w.write(d.toString());
				w.write('\n');
			}
			
			w.close();
			CRCDatInfoImpl impl = new CRCDatInfoImpl();
			impl.writeCRC(getCRC32(src));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static Collection<StoreDat> readFromDB() {
		final Map<String, StoreDat> data = new HashMap<String, StoreDat>();
		
		DataTraveler.travel(PresentEx.class, new DataTraveler.Travel<PresentEx>(){
			@Override
			public boolean travel(DataTraveler<PresentEx> item) {
				if (!data.containsKey(item.data.name)){
					StoreDat crc = new StoreDat();
					crc.name = item.data.name;
					crc.crc = item.data.crc;
					
					data.put(item.data.name, crc);
				}
				
				StoreDat d =  data.get(item.data.name);
				d.items.add(item.data.id);
					
				return true;
			}}, null);
		
		return data.values();
	}

}

package com.grsoft.napoleon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.impl.PresentImpl;
import com.grsoft.napoleon.util.PresentSdcard.InitStrategy;

public class PresentSdcard {
	public static final String PREF_NAME = "com.grsoft.napoleon.util.PresentSdcard";
	public static final String UPDTATE_PRESENT_TIME = "com.grsoft.napoleon.util.updtate_present_time"; 
	public static final String PREZENT_PATH = "com.grsoft.napoleon.util.PREZENT_PATH";
	public static final String fileName = "prezent";
	
	public static InitStrategy initStrategy = new DefaultInitStrategy();
	
	public interface InitStrategy {
		public void init(Context context);
	}
	
	public static void init(Context context) {
		initStrategy.init(context);
	}
	
	private static final String FOLDER = "descr";

	public static String readDscr(Context ctx, String id) {
		StringBuilder result = new StringBuilder();

		try {
			String srcPath = ((CfgNplW)ConfigManager.getConfig()).presentpath;
			File file = new File(new File(srcPath, FOLDER), id);
			if (file.exists()) {
				InputStream is = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = null;
				try {
					br = new BufferedReader(isr);
					String s = br.readLine();

					while (s != null) {
						result.append(s);
						s = br.readLine();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (br != null)
						br.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result.toString();
	}
}

class DefaultInitStrategy implements InitStrategy{

	@Override
	public void init(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PresentSdcard.PREF_NAME, Context.MODE_PRIVATE);
		String path = ((CfgNplW)ConfigManager.getConfig()).presentpath;
		long time = pref.getLong(PresentSdcard.UPDTATE_PRESENT_TIME, -1);

		if (path.length() > 0) {
			File txt = new File(path, PresentSdcard.fileName);

			if (txt.isFile() && (time == -1 || 
					txt.lastModified() != time)) {
				try {
					SQLiteDatabase database = DataBaseManager.getDataBase();
					SQLiteStatement statement = database
							.compileStatement("SELECT folderid from price WHERE id=?");
					DbWriter.checkDBTable(Present.class);
					database.execSQL("delete from " + DataObjectInfo.getInstance().getTableName(Present.class));
					InputStream is = new FileInputStream(txt);
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);

					String s = br.readLine();
					final String DELIMITER = ";";
					DbWriter writer = new DbWriter();
					
					while (s != null) {
						String[] data = s.split(DELIMITER);
						
						if(data.length == 2) {
							PresentImpl pimpl = new PresentImpl();
							Present p = pimpl.getData();
							
							statement.bindString(1, data[0]);
							
							try{
								p.folderId = statement.simpleQueryForString();
							}catch(Exception e){
								s = br.readLine();
								continue;
							}
							
							p.id = data[0];
							p.photoPath = path + "//" + data[1];
							
							update(p);
							
							writer.insertRecord(p);
						}
						s = br.readLine();
					}

					br.close();
					writer.close();
					
					Editor edit = pref.edit();
					edit.putLong(PresentSdcard.UPDTATE_PRESENT_TIME, txt.lastModified());
					edit.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void update(Present p) {}
}



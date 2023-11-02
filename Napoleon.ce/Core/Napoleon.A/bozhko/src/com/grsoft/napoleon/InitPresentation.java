package com.grsoft.napoleon;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PresentItem;
import com.grsoft.dataobjects.PresentItemPrice;
import com.grsoft.dataobjects.PresentList;
import com.grsoft.dataobjects.impl.PresentListImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.PresentSdcard;
import com.grsoft.napoleon.util.PresentSdcard.InitStrategy;

public class InitPresentation implements InitStrategy {

	@Override
	public void init(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
				PresentSdcard.PREF_NAME, Context.MODE_PRIVATE);
		String path = ((CfgNpl) ConfigManager.getConfig()).presentpath;
		long time = pref.getLong(PresentSdcard.UPDTATE_PRESENT_TIME, -1);

		if (path.length() > 0) {
			File txt = new File(path, PresentSdcard.fileName + ".xml");

			if (txt.isFile() && (time == -1 || txt.lastModified() != time)) {
				try {
					SQLiteDatabase database = DataBaseManager.getDataBase();
					SQLiteStatement statement = database.compileStatement("SELECT folderid from price WHERE id=?");
					DbWriter.checkDBTable(PresentList.class);
					database.execSQL("delete from " + DataObjectInfo.getInstance().getTableName(PresentList.class) + " ");

					DbWriter.checkDBTable(Present.class);
					database.execSQL("delete from " + DataObjectInfo.getInstance().getTableName(Present.class));

					InputStream is = new FileInputStream(txt);
					XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
					factory.setNamespaceAware(false);
					XmlPullParser xpp = factory.newPullParser();
					xpp.setInput(is, "utf-8");

					PresentListImpl presentList = null;
					PresentItem item = null;
					DbWriter writer = new DbWriter();
					int eventType = xpp.getEventType();
					int id = 0;
					while (eventType != XmlPullParser.END_DOCUMENT) {
						switch (xpp.getEventType()) {
						case XmlPullParser.START_TAG:
							if (xpp.getName().equals("list")) {
								if (presentList != null) {
									presentList.write();
									presentList.close();
								}

								presentList = new PresentListImpl();
								PresentList pl = presentList.getData();
								pl.id = ++id;
								pl.col = Integer.parseInt(xpp
										.getAttributeValue("", "col"));
								pl.row = Integer.parseInt(xpp
										.getAttributeValue("", "row"));
								pl.items.clear();
							} else if (xpp.getName().equals("item")) {
								if (presentList != null) {
									item = new PresentItem();
									item.col = Integer.parseInt(xpp
											.getAttributeValue("", "col"));
									item.row = Integer.parseInt(xpp
											.getAttributeValue("", "row"));
									item.path = xpp.getAttributeValue("",
											"path");
									item.desc = xpp.getAttributeValue("",
											"desc");
									PresentList pl = presentList.getData();
									pl.items.add(item);
								}
							} else if (xpp.getName().equals("price")) {
								if (item != null) {
									PresentItemPrice pip = new PresentItemPrice();
									pip.id = xpp.getAttributeValue("", "id");
									item.ids.add(pip);
									
									Present p = new Present();
									
									statement.bindString(1, pip.id);
									
									try{
										p.folderId = statement.simpleQueryForString();
										p.id = pip.id;
										
										String itempath = item.path;

										if (itempath.trim().length() > 0) {
											itempath = itempath.replace("\\", "/");
											File file = new File(
													((CfgNpl) ConfigManager.getConfig()).presentpath,
													itempath);
											p.photoPath = file.getAbsolutePath();
											writer.insertRecord(p);
										}
										
									}catch(Exception e){}
								}
							}
							break;

						default:
							break;
						}
						eventType = xpp.next();
					}

					presentList.write();
					presentList.close();
					
					Editor edit = pref.edit();
					edit.putLong(PresentSdcard.UPDTATE_PRESENT_TIME, txt.lastModified());
					edit.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}

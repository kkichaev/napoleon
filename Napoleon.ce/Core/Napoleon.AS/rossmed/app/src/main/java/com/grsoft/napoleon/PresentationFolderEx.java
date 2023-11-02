package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;

public class PresentationFolderEx extends PresentationFolder {
	@Override protected FoldersAdapter createAdapter() { return new Adapter(this); }
	
	class Adapter extends PhotoFolder {
		public Adapter(WarehouseManager manager) {
			super(manager);
		}
	
		@SuppressLint("DefaultLocale")
		@Override
		protected void fillPriceIds(SQLiteDatabase database) {
			try{
				fprice.clear();
				items.clear();
				
				PriceImpl pi = new PriceImpl();
				Price p = pi.getData();
				String path = ((CfgNpl)ConfigManager.getConfig()).presentpath;
				File f = new File(path);
				if( f.isDirectory() ) {
					String[] files = f.list();
					for(String name : files) {
						if(name.toLowerCase().startsWith("img_")) {
							int idx = name.lastIndexOf('.');
							String id = name.substring(4, idx);
							
							p.id = id;
							if( pi.read() ) {
								long rowid = pi.getRowid();
								if( !inset(rowid, id) )
									continue;
								
								int folderid = p.folderID;
								if(!fprice.containsKey(folderid))
									fprice.put(folderid, new ArrayList<PriceInfo>());
								
								PriceInfo pri = new PriceInfo(rowid, p.name, id);								
								if(items.add(new PresentationData(rowid, folderid, p.name, path + "/" + name, p.id)))
									fprice.get(folderid).add(pri);
							}
						}
					}
				}
				
				pi.close();
//				items.sort();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}

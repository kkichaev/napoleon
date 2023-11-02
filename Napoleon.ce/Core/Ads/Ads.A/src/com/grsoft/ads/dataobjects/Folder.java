package com.grsoft.ads.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="folders", keyFields = "id")
public class Folder extends DataObject {
	
	/***
	 * Ключ
	 */
	public String id = "";
	
	/***
	 * id родителя
	 */
    public String parent = "";
    
    /***
     * Тип
     */
    public int type;
    
    /***
     * Наименование
     */
    public String name = "";
}

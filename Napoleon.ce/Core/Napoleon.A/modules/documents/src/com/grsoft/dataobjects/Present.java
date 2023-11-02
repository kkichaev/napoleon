package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="presentation", keyFields = "id")
public class Present extends DataObject {
	/**
	 * id - прайс
	 */
	public String id = "";
	
	/**
	 * Папка товара
	 */
	public String folderId = "";
	
	/**
	 * Путь к файлу фотографии
	 */
	public String photoPath = "";
}

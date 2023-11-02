package com.grsoft.napoleon.documents;


public interface PhotoDocument {
	void addPhoto(byte[] photo);
	boolean read(long rowid);
	long getRowid();
	long size();
	
	/***
	 * Количество фотографий
	 * @return
	 */
	int count();
}

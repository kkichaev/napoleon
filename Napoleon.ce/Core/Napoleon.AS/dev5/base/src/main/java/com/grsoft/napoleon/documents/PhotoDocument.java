package com.grsoft.napoleon.documents;
import com.grsoft.aceteam.R;


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

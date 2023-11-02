package com.grsoft.dataobjects;

import java.util.ArrayList;
import com.grsoft.dataobjects.DataObject;

public class UpdatePrezent extends DataObject{
	public String name;
	public byte[] file;
	public long crc;
	public ArrayList<UpdatePresentItem> items = new ArrayList<UpdatePresentItem>(); 
}
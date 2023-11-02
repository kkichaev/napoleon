package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Distrib", keyFields="created")
public class Distrib extends CreateDocDataObject {
	public int ourFaces;
	public int theirFaces;
}

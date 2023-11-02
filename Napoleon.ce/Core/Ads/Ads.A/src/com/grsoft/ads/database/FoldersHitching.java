package com.grsoft.ads.database;

import com.grsoft.ads.dataobjects.Folder;
import com.grsoft.database.RcvNewHitching;

public class FoldersHitching extends RcvNewHitching {

	public FoldersHitching() {
		super(Folder.class, "Folders");
	}
}

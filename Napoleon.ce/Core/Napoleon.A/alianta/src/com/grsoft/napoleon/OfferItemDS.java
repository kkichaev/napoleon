package com.grsoft.napoleon;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.impl.PresentImpl;
import com.grsoft.napoleon.modules.print.BaseDataSource;

public class OfferItemDS extends BaseDataSource {
	static final int MAX_HEIGHT = 150;
	
	public OfferItemDS(OfferItemPrintData object) {
		super(object);
	}
	
	@Override
	public int getImageHeight(String name) {
		return MAX_HEIGHT;
	}
	
	@Override
	public byte[] getImage(String name) {
		byte[] ret = null;
		
		PresentImpl pi = new PresentImpl();
		Present pr = pi.getData();
		pr.id = ((OfferItemPrintData)object).id; 
		if(pi.read()) {
			File f = new File(pr.photoPath);
			int len = (int) f.length();
			ret = new byte[len];
		    try {
				BufferedInputStream buf = new BufferedInputStream(new FileInputStream(f));
				buf.read(ret, 0, ret.length);
			    buf.close();
			} catch (Exception e) {
				e.printStackTrace();
				ret = null;
			}
		}
		pi.close();
		return ret;
	}

}

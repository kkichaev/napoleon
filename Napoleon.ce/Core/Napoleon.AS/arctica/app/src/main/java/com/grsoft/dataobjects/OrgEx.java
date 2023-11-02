package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	public int delay;
	
	public int refregerators;
	public String ido = "";

	public List<OrgParam> params = new ArrayList<OrgParam>();
	
	public List<Agreement> agree = new ArrayList<Agreement>();
	public List<Segment> segments = new ArrayList<Segment>();
	
	public boolean haveSegment(String idSeg) {
		for(Segment s : segments) {
			if(s.id.equals(idSeg))
				return true;
		}
		return false;
	}
}

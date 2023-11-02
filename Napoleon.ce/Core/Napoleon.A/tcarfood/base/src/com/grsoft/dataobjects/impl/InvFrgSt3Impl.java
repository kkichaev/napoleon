package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.InvFrgSt3;


public class InvFrgSt3Impl extends InvFrgStImplBase<InvFrgSt3> {
	@Override
	public boolean delete() {
		InvFrgImpl parent = new InvFrgImpl();
		parent.read(data.invfrg.getTime());
		parent.getData().st3_state = 0;
		parent.write();
		parent.close();
		
		return super.delete();
	}
	
}

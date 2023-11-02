package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.InvFrgSt1;


public class InvFrgSt1Impl extends InvFrgStImplBase<InvFrgSt1> {
	@Override
	public boolean delete() {
		InvFrgImpl parent = new InvFrgImpl();
		parent.read(data.invfrg.getTime());
		parent.getData().st1_state = 0;
		parent.write();
		parent.close();
		
		return super.delete();
	}

}

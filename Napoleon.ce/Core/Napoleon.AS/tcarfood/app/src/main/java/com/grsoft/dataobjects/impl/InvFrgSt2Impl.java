package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.InvFrgSt2;


public class InvFrgSt2Impl extends InvFrgStImplBase<InvFrgSt2> {
	@Override
	public boolean delete() {
		InvFrgImpl parent = new InvFrgImpl();
		parent.read(data.invfrg.getTime());
		parent.getData().st2_state = 0;
		parent.write();
		parent.close();
		
		return super.delete();
	}
}

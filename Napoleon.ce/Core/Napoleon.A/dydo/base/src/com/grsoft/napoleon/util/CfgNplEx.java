package com.grsoft.napoleon.util;

public class CfgNplEx extends CfgNpl {

	private static final long serialVersionUID = 1L;

	@Override
	public void resetToDefault() {
		super.resetToDefault();

		address = "service.k-cloud.ru";
		port = 20102;
	}
}

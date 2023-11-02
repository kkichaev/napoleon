package com.grsoft.napoleon;

import java.util.UUID;


public class PotenzialOrgEx extends PotenzialOrg {
	@Override
	protected OKListener createOKListener() {
		return new OKListener(){
			@Override protected String genOrgId() { return  UUID.randomUUID().toString().replace("-", ""); }
		};
	}
}

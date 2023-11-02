package com.grsoft.napoleon.documents;

class EmptyDocList extends DocList {
	public EmptyDocList(Class<? extends Document<?>> docType) {
		try {
			document = docType.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

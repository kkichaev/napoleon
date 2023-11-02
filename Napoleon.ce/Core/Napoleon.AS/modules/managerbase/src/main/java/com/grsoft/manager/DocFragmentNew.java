package com.grsoft.manager;

public class DocFragmentNew extends DocFragment {
	public static Class<?> docListFragment = DocListFragmentNew.class;
	@Override protected Class<?> getDocListType() { return docListFragment; }
}

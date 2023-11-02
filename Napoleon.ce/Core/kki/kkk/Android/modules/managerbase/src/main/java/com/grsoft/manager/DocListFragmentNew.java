package com.grsoft.manager;

public class DocListFragmentNew extends DocListFragment {
	
	@Override
	protected void openOrgTaskList(DocRow row) {
		OrgTaskListNew.open(getActivity(), row.getDocument().getId(), ((SelParam)getActivity()).getUserid());
	}
}

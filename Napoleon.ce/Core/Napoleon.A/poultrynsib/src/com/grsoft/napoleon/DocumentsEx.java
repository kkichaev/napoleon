package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.DocFilterOnClickListener;

public class DocumentsEx extends Documents {
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);

		if (adapter != null && DocType.getCurDoc() instanceof OrderDoc) {
			int sum = 0;
			int weight = 0;

			for (int i = 0; i < adapter.getCount(); i++) {
				Document<?> d = (Document<?>) adapter.getItem(i);
				sum += d.sum();
				weight += ((OrderImplBase<?>) d).weight();
			}

			DocType.getCurDoc().updateTotalSum(this, sum, weight, 0,
					R.id.tvTotalSum);
		}
	}

	@Override
	protected void onlyVisitInit() {
		List<DocTypeBase> filter = ((NapoleonApp) getApplication()).potenzialOrgDocFilter;

		if (filter != null && filter.size() > 0
				&& !filter.contains(DocType.getCurDoc()))
			DocType.setCurDoc(filter.get(0));

		btnDocFilter.setOnClickListener(new DocFilterOnClickListener(this, false, true,  filter));
	}
}

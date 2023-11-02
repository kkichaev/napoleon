package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.MovementDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.util.DocFilterOnClickListener;

public class Documents2Ex extends DocumentsEx {
	@Override
	protected DocType adjustDocType(DocType docType) {
		DocType result = docType;

		if (docType instanceof MovementDoc)
			result = SalesDoc.instance();

		return result;
	}

	@Override
	protected DocFilterOnClickListener createDocFilter() {
		ArrayList<DocTypeBase> list = new ArrayList<DocTypeBase>();
		for (DocTypeBase dt : DocType.docTypes)
			if (!(dt instanceof MovementDoc))
				list.add((DocType) dt);

		return new DocFilterOnClickListener(this, false, false, list);
	}

}

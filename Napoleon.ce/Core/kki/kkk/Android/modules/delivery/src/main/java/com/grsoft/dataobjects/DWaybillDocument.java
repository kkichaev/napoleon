package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

abstract public class DWaybillDocument extends DispatchDocDataObject{
	public List<DWaybillDocumentItem> items = new ArrayList<DWaybillDocumentItem>();
}

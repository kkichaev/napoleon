package com.grsoft.network;

import com.grsoft.dataobjects.CommonIncass;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.impl.CommonIncassImplBase;
import com.grsoft.napoleon.documents.DocumentUtils;
import com.grsoft.network.exception.RuntimeException;


public class CommonIncassExportHitching implements ObjectExportListener {
	CommonIncassImplBase<? extends CommonIncass> data;
	String objectName;
	public CommonIncassExportHitching(CommonIncassImplBase<? extends CommonIncass> data, String objectName) {
		this.data = data;
		this.objectName = objectName;
	}

	@Override
	public void onStart() { }

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException { }

	@Override
	public void onSave() { }

	@Override
	public void onEnd() {
		DocumentUtils.setExported(data, data.getData().params, true);
		data.write();
		data.close();
	}

	@Override
	public String getObjectName() { return objectName; }

	@Override
	public int size() { return 1; }

	@Override
	public DataObject get(int i) { return data.getData(); }

}

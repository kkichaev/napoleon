package com.grsoft.util;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.napoleon.R;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class PrjUtil {
	public static void loadSpinner(Context context, String key, Spinner sp,
			Class<? extends DataObject> dataType) {
		try {
			ArrayList<KeyValue> values = new ArrayList<KeyValue>();
			DbReader reader = new DbReader();
			DataObject data = dataType.newInstance();

			boolean bdo = reader.select(data, DataObjectInfo.getInstance()
					.getTableName(dataType), null);

			Field fid = dataType.getField("id");
			Field fname = dataType.getField("name");

			while (bdo) {
				KeyValue kv = new KeyValue(fid.get(data).toString(), fname.get(
						data).toString());
				values.add(kv);
				bdo = reader.selectNext(data);
			}

			reader.close();

			int selected = -1;
			for (int i = 0; i < values.size(); i++)
				if (values.get(i).key.equals(key)) {
					selected = i;
					break;
				}

			ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(context,
					R.layout.simple_spinner_layout, values);

			sp.setAdapter(aa);

			if (selected >= 0 && selected < sp.getCount())
				sp.setSelection(selected);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

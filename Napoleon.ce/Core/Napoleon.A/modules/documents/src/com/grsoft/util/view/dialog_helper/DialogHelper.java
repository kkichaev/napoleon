package com.grsoft.util.view.dialog_helper;

import java.util.ArrayList;
import java.util.List;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.R;

public class DialogHelper {
	
	public static final char SEP_SYMBOL = ';';
	
	public interface Selected<T extends DataObject> {
		boolean isSelected(T object);
	}
	
	public static void loadSpinnerFromConfig(ConfigImpl config, String key, List<CharSequence> values, Spinner s, int selected) {
		Config c = config.getData();
		c.key = key;
		if( config.read())
			makeList(c.value, values);
		
		ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(s.getContext(), R.layout.simple_spinner_layout, values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		s.setAdapter(aa);
		if( selected >= 0 && selected < s.getCount())
			s.setSelection(selected);
	}

	public static void loadSpinnerWithKey(ConfigImpl config, String key, List<KeyValue> values, Spinner s, String selected) {
		loadSpinnerWithKeyW(config, key, values, s, selected, false);
	}

	public static <T extends DataObject> void loadSpinnerFromDataObject(Spinner s, Class<T> dataClass, Selected<T> selected, boolean addEmpty) {
		loadSpinnerFromDataObject(s, dataClass, selected, addEmpty, null);
	}

	public static <T extends DataObject> void loadSpinnerFromDataObject(Spinner s, Class<T> dataClass, Selected<T> selected, boolean addEmpty, String order) {
		loadSpinnerFromDataObject(s, dataClass, selected, addEmpty, order, null);
	}
	
	public static <T extends DataObject> void loadSpinnerFromDataObject(Spinner s, final Class<T> dataClass, final Selected<T> selected, boolean addEmpty, String order, String where) {
		final List<T> values = new ArrayList<T>();

		if(addEmpty) {
			T val = null;
			try {
				val = dataClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if( val != null )
				values.add(val);
		}
		
		final SelectHolder sh = new SelectHolder();
		DataTraveler.travel(dataClass, new DataTraveler.Travel<T>(){

			@Override
			public boolean travel(DataTraveler<T> item) {
				if(selected != null && selected.isSelected(item.data))
					sh.setSelected(values.size());
				
				values.add(item.data);
				try {
					item.data = dataClass.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		}, where, order);

		ArrayAdapter<T> aa = new ArrayAdapter<T>(s.getContext(), R.layout.simple_spinner_layout, values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		s.setAdapter(aa);
		if( sh.assigned() )
			s.setSelection(sh.getSelected());
	}

	public static void loadSpinnerWithKeyW(ConfigImpl config, String key,
			List<KeyValue> values, Spinner s, String selected, boolean space) {
		Config c = config.getData();
		c.key = key;
		int sel = -1;
		if( config.read())
			sel = makeListWithKey(c.value, values, selected);
		
		if(space){
			values.add(0, new KeyValue("",""));
			sel++;
		}
		
		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(s.getContext(), R.layout.simple_spinner_layout, values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		s.setAdapter(aa);
		if( sel >= 0 && sel < s.getCount())
			s.setSelection(sel);
	}

	public static void loadSpinnerFromConfig(ConfigImpl config, String key, List<CharSequence> values, Spinner s, String selected) {
		Config c = config.getData();
		c.key = key;
		int sel = -1;
		if( config.read())
			sel = makeList(c.value, values, selected);
		
		ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(s.getContext(), R.layout.simple_spinner_layout, values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		s.setAdapter(aa);
		if( sel >= 0 && sel < s.getCount())
			s.setSelection(sel);
	}

	public static void makeList(String value, List<CharSequence> values) {
		makeList(value, values, null);
	}
	
	public static int makeListWithKey(String value, List<KeyValue> values, String selected) {
		int sel = -1;
		int pos = value.indexOf(SEP_SYMBOL); 
		
		while(pos != -1) {
			String f = value.substring(0,pos);
			
			KeyValue kv = new KeyValue(f);
			if( selected != null && kv.key.equals(selected))
				sel = values.size();
			
			value = value.substring(pos+1);
			values.add(kv);
			pos = value.indexOf(SEP_SYMBOL); 
		}

		if(pos == -1 && value.length() > 0) {
			KeyValue kv = new KeyValue(value);
			if( selected != null && kv.key.equals(selected))
				sel = values.size();
			values.add(kv);
		}
		return sel;
	}

	public static int makeList(String value, List<CharSequence> values, String selected) {
		int sel = -1;
		int pos = value.indexOf(SEP_SYMBOL); 
		
		while(pos != -1) {
			String f = value.substring(0,pos);
			
			if( selected != null && f.equals(selected))
				sel = values.size();
			
			value = value.substring(pos+1);
			values.add(f);
			pos = value.indexOf(SEP_SYMBOL); 
		}

		if(pos == -1 && value.length() > 0) {
			if( selected != null && value.equals(selected))
				sel = values.size();
			values.add(value);
		}
		return sel;
	}
}

class SelectHolder {
	int selected;
	public SelectHolder() { selected = -1; }
	
	public boolean assigned() { return selected >= 0; }
	public int getSelected() { return selected; }
	public void setSelected(int sel) { selected = sel; }
}

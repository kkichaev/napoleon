package com.grsoft.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.GuidDataObject;
import com.grsoft.dataobjects.impl.EditorObject;
import com.grsoft.dataobjects.impl.EditorObjectData;
import com.grsoft.util.ExtrasConst;

public class EditorsItems extends Activity {
	public static Class<? extends Activity> activity = EditorsItems.class;
	private static final String EDITOR_OBJECT = "editor_object";

	private ImageButton btnAdd;
	private ListView list;

	private Class<? extends EditorObject<?>> editor;
	private Method openEditActivity;
	private Class<?> editorActivity;

	public static void open(Context context,
			Class<? extends EditorObject<?>> editor) {
		Intent intent = new Intent(context, activity);
		intent.putExtra(EDITOR_OBJECT, editor);
		context.startActivity(intent);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editors_items);

		btnAdd = (ImageButton) findViewById(R.id.btnAdd);
		list = (ListView) findViewById(R.id.list);

		editor = (Class<? extends EditorObject<?>>) getIntent().getExtras()
				.get(EDITOR_OBJECT);
		EditorObjectData data = editor.getAnnotation(EditorObjectData.class);
		editorActivity = data.activity();

		try {
			openEditActivity = editorActivity.getMethod("open", Context.class,
					long.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edit(v.getContext(), ExtrasConst.INVALID_ID);
			}
		});

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int pos, long arg3) {
				EditorObject<?> data = (EditorObject<?>) adapterView
						.getAdapter().getItem(pos);
				edit(view.getContext(), data.getRowid());
			}
		});

		registerForContextMenu(list);
	}

	protected void edit(Context context, long rowid) {
		try {
			openEditActivity.invoke(editorActivity, context, rowid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		list.setAdapter(new EditorItemsAdapter(this, editor));
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.edititems_context_menu, menu);
	};

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		EditorObject<?> eo = ((EditorObject<?>) (((BaseAdapter) list
				.getAdapter()).getItem(info.position)));
		
		int id = item.getItemId();
		if (id == R.id.itDelete){
			eo.delete();
			((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
			return true;
		} else if (id == R.id.itEdit){
			edit(this, eo.getRowid());
			return true;
		} else
			return super.onContextItemSelected(item);
	}
}

class EditorItemsAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<EditorObject<?>> data = new ArrayList<EditorObject<?>>();
	private Class<? extends EditorObject<?>> editorType;
	
	public EditorItemsAdapter(Context context,
			Class<? extends EditorObject<?>> editorType) {
		this.context = context;
		this.editorType = editorType;
		refreshData();
	}
	
	@Override
	public void notifyDataSetChanged() {
		refreshData();
		super.notifyDataSetChanged();
	}

	public void refreshData() {
		try {
			data.clear();
			EditorObject<? extends DataObject> instance = editorType
					.newInstance();
			GuidDataObject data = instance.getData();
			DbWriter.checkDBTable(data.getClass());
			List<Long> ids = DbReader.readIds(instance.getTableName(), null,
					null);

			for (Long id : ids) {
				EditorObject<? extends DataObject> obj = editorType
						.newInstance();
				obj.read(id);
				obj.close();
				this.data.add(obj);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.editors_items_row, null);
		EditorObject<?> item = (EditorObject<?>) getItem(position);
		((TextView) view.findViewById(R.id.tvName))
				.setText(item.getData().name);
		return view;
	}

}
package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.napoleon.util.SyncShedulHelper;
import com.grsoft.network.RefreshData;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class SelectMatrix extends BaseActivity {
	int selected = -1;
	
	public static void open(Context c) {
		Intent i = new Intent(c, SelectMatrix.class);
		c.startActivity(i);
	}
	
	HashSet<String> usedMatrix = new HashSet<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.createorder);
		
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { finish(); }
		});

		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if( selected < 0 )
					Toast.makeText(SelectMatrix.this, "Необходимо указать матрицу для заказа", Toast.LENGTH_SHORT).show();
				else {
					ListView lv = (ListView)findViewById(R.id.lvMatrix);
					Matrix m = (Matrix) lv.getAdapter().getItem(selected);
					WarehouseEx.open(SelectMatrix.this, m.name);
					finish(); 
				}
			}
		});
		
		if( SyncShedulHelper.needSync(this) ) {
			DataExchange.receiveData(this, new RefreshData.Handler() {
				@Override public void onRead(boolean result) {
					SyncShedulHelper.markSync(SelectMatrix.this);
					runOnUiThread(new Runnable() {
						@Override public void run() { 
							loadMatrix();
						}
					});
				}
			});
		} else {
			loadMatrix();
		}
	}
	
	private void loadMatrix() {
		DataTraveler.travel(OrderEx.class, new DataTraveler.Travel<OrderEx>() {
			@Override
			public boolean travel(DataTraveler<OrderEx> item) {
				usedMatrix.add(item.data.matrix);
				return true;
			}
		}, "created >= " + Long.toString(Util.getDate().getTime()));
		
		Adapter a = new Adapter();
		ListView lv = (ListView)findViewById(R.id.lvMatrix);
		lv.setAdapter(a);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selected = arg2;
				((Adapter)arg0.getAdapter()).notifyDataSetChanged();
			}
		});
	}

	class Adapter extends BaseAdapter {

		List<Matrix> matrix = new ArrayList<Matrix>();
		
		public Adapter() {
			DataTraveler.travel(Matrix.class, new DataTraveler.Travel<Matrix>() {

				@Override
				public boolean travel(DataTraveler<Matrix> item) {
					matrix.add(item.data);
					item.data = new Matrix();
					return true;
				}
			}, "");
		}
		
		@Override public int getCount() { return matrix.size(); }
		@Override public Object getItem(int arg0) { return arg0 < matrix.size() ? matrix.get(arg0) : null; }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if(arg1 == null)
				arg1 = View.inflate(SelectMatrix.this, R.layout.matrix_row, null);
			Matrix m = (Matrix) getItem(arg0);
			if( m != null ) {
				TextView tv = (TextView) arg1.findViewById(R.id.tvName);
				tv.setText(m.name);
			}
			int drawRes =  (arg0 == selected ) ? R.drawable.sel_matrix : 
				m != null && usedMatrix.contains(m.name) ? R.drawable.used_matrix : 
				R.drawable.list_selector; 
			arg1.setBackgroundResource(drawRes);
			return arg1;
		}
		
	}
}

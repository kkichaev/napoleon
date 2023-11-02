package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.PresentList;
import com.grsoft.util.ExtrasConst;

public class PresentationView extends FragmentActivity {

	private static final int PAGE_SELECT = 0;

	ViewPager pager;

	private long docRowId;
	private PagerAdapter adapter;

	public static void open(Context context, long rowid) {
		Intent intent = new Intent(context, PresentationView.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.presentationfolderex);

		pager = (ViewPager) findViewById(R.id.pager);

		Bundle bundle = getIntent().getExtras();
		docRowId = bundle.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);

		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new PagerAdapter(getSupportFragmentManager(), docRowId);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override public void onPageSelected(int arg0) { }
			@Override public void onPageScrolled(int arg0, float arg1, int arg2) { }
			@Override public void onPageScrollStateChanged(int arg0) {}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == PAGE_SELECT ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Выберите страницу");
			String[] pages = new String[adapter.getCount()];

			for( int i=0; i<adapter.getCount(); i++ ) {
				pages[i] = Integer.toString(i+1);
			}
			
			b.setSingleChoiceItems(pages, pager.getCurrentItem(), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					pager.setCurrentItem(arg1, true);					
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.prezent_menu, menu);
		//showDialog(PAGE_SELECT);
		return true;
//		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.itSelectPage) {
			showDialog(PAGE_SELECT);
		}
		return super.onOptionsItemSelected(item);
	}
}

class PagerAdapter extends FragmentStatePagerAdapter {
	List<Integer> data = new ArrayList<Integer>();
	long docrowid;

	public PagerAdapter(FragmentManager fm, long docrowid) {
		super(fm);
		this.docrowid = docrowid;
		
		DbWriter.checkDBTable(PresentList.class);
		Cursor c = null;
		SQLiteDatabase db = DataBaseManager.getDataBase();

		try {
			c = db.query(DataObjectInfo.getInstance().getTableName(PresentList.class), new String[] { "id" }, null, null, null, null, null);

			while (c.moveToNext())
				data.add(c.getInt(c.getColumnIndex("id")));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null)
				c.close();
		}

	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Fragment getItem(int pos) {
		Fragment f = new PresentPage();
		Bundle b = new Bundle();
		b.putInt(PresentPage.PAGE_ID, data.get(pos));
		b.putLong(ExtrasConst.DOC_ROW_ID_STR, docrowid);
		f.setArguments(b);
		return f;
	}
}

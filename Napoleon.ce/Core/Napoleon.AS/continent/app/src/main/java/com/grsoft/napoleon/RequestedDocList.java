package com.grsoft.napoleon;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.Attachment;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Requestdoc;
import com.grsoft.dataobjects.ResponseAttach;
import com.grsoft.dataobjects.impl.AttachmentImpl;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocDeleteHelper;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentUtils;
import com.grsoft.napoleon.documents.RequestdocDoc;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.UpdateProcess;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BackgroudProcess;
import com.grsoft.view.RunnableProcess;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

public class RequestedDocList extends Activity implements OnClickListener {
	ListView list;
	
	public static void open(Context context) {
		Intent intent = new Intent(context, RequestedDocList.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.requested_doc_list);
		
		list = (ListView)findViewById(R.id.list);
		list.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String i = ((Adapter)parent.getAdapter()).getAttachID(position);
				
				if (i != null) {
					final AttachmentImpl impl = new AttachmentImpl();

					if (!impl.read("id", i)) 
						loadAttachment(i, impl);
					else
						preview(impl.getData().path);
				}
			}
		});
		
		removeOldDocs();
		list.setAdapter(new Adapter(this));
		
		findViewById(R.id.btnSend).setOnClickListener(this);
		findViewById(R.id.btnDel).setOnClickListener(this);
	}

	void removeOldDocs() {
		String minDate = Long.toString(getStartDocDate().getTime());
		String attTbl = (new Attachment()).getTableName();
		String rspTbl = (new ResponseAttach()).getTableName();
		String rqTbl = (new Requestdoc()).getTableName();
		String stmt = "select [path] from " + attTbl + " a, " + rspTbl + " r where a.id = r.id and r.created < " + minDate;
		try {
			Cursor c = DataBaseManager.getDataBase().rawQuery(stmt, null);
			while (c.moveToNext()) {
				String path = c.getString(0);
				File f = new File(path);
				if (f.exists())
					f.delete();
			}

			stmt = "delete from " + attTbl + " where id in (select id from " + rspTbl + " where created < " + minDate + ")";
			DataBaseManager.getDataBase().execSQL(stmt);

			stmt = "delete from " + rspTbl + " created < " + minDate + "";
			DataBaseManager.getDataBase().execSQL(stmt);

			stmt = "delete from " + rqTbl + " created < " + minDate + "";
			DataBaseManager.getDataBase().execSQL(stmt);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private Date getStartDocDate() {
		int days = 20;
		StringBuilder sb = new StringBuilder();
		ConfigImpl ci = new ConfigImpl();
		if(ci.getValue(sb, "ХранитьДокументыДней") && sb.length() > 0) {
			days = Integer.parseInt(sb.toString());
		}
		return new Date(Util.getDate().getTime() - (long)days * 24 * 3600 * 1000);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.wait_dlg)
			return createWaitDlg();
		return super.onCreateDialog(id);
	}

	private Dialog createWaitDlg() {
		ProgressDialog dlg = new ProgressDialog(this);
		dlg.setMessage(getString(R.string.please_wait));
		return dlg;
	}
	
	protected void preview(String file) {
		try {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_VIEW);
			String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file));
			
			Uri uri = null;
			
			if (Build.VERSION.SDK_INT >= 24) {
				uri = FileProvider.getUriForFile(this, getString(R.string.fileprovider_authorities), new File(file));
			}else
				uri = Uri.fromFile(new File(file));
			
			i.setDataAndType(uri, mime);
			i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			startActivity(i);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

	protected void loadAttachment(final String id, final AttachmentImpl impl) {
		UpdateProcess p = new UpdateProcess(this) {
			@Override
			protected void onPreExecute() {
				showDialog(R.id.wait_dlg);
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					dismissDialog(R.id.wait_dlg);
					if (impl.read("id", id)) 
						preview(impl.getData().path);
				}
			}
		};
		
		Config cfg = ConfigManager.getConfig();
		UpdateProcess.Params arg = new UpdateProcess.Params();
		arg.login = cfg.login;
		arg.pass = cfg.passw;
		arg.ip1 = cfg.address;
		arg.ip2 = cfg.address2;
		arg.port1 = cfg.port;
		
		arg.indata.add(new AttachmentHitching(id));
		p.execute(arg);
	}

	private static class Adapter extends BaseAdapter{
		HashMap<Date, ResponseAttach> responce = new HashMap<Date, ResponseAttach>();
		
		
		List<CreateDocDataObject> data = new ArrayList<CreateDocDataObject>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
		Context context;
		
		public Adapter(Context context) {
			this.context = context;
			
			reload();
		}
		
		private void reload() {
			data.clear();
			responce.clear();
			
			List<Class<? extends CreateDocDataObject>> types = new ArrayList<Class<? extends CreateDocDataObject>>();
			types.add(Requestdoc.class);
			
			final DbReader reader = new DbReader();
			
			for (Class<? extends CreateDocDataObject> t : types) {
				DataTraveler.travel(t, new DataTraveler.Travel<CreateDocDataObject>(true) {
					
					@Override
					public boolean travel(DataTraveler<CreateDocDataObject> item) {
						data.add(item.data);
						
						ResponseAttach a = new ResponseAttach();
						a.created = item.data.created;
						
						if (reader.read(a, DataObjectInfo.getInstance().getTableName(a.getClass())) != -1) {
							responce.put(a.created, a);
						}
						
						return true;
					}
					
				}, null);
			}
			
			reader.close();
			
			Collections.sort(data, new Comparator<CreateDocDataObject>() {

				@Override
				public int compare(CreateDocDataObject lhs, CreateDocDataObject rhs) {
					return lhs.created.compareTo(rhs.created) * -1;
				}
			});
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

		OrgImpl orgImpl = new OrgImpl();
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = View.inflate(context, R.layout.requested_doc_list_row, null);
			
			Requestdoc item = (Requestdoc) getItem(position);
			
			TextView tv = (TextView) convertView.findViewById(R.id.tvDate);
			tv.setText(sdf.format(item.created));
			
			TextView tvOrg = (TextView) convertView.findViewById(R.id.tvOrg);
			tvOrg.setVisibility(View.GONE);
			
			String name = "";
			if (item.type == Requestdoc.FA_TYPE)
				name = "Сличительная ведомомсть";
			else if (item.type == Requestdoc.UPD_TYPE) {
				name = "УПД";
				tvOrg.setVisibility(View.VISIBLE);
				orgImpl.read("id", item.id);
				tvOrg.setText(orgImpl.getData().name);
			}
			else if (item.type == Requestdoc.MOVING_TYPE)
				name = "Перемещение";
			
			tv = (TextView) convertView.findViewById(R.id.tvName);
			tv.setText(name);
			
			ImageView iv = (ImageView) convertView.findViewById(R.id.ivStatus);
			
			if (getAttachID(position) != null)
				iv.setImageResource(R.drawable.pcd);
			else if (DocumentUtils.isExported(item.params))
				iv.setImageResource(R.drawable.sent);
			
			return convertView;
		}
		
		public String getAttachID(int index){
			String result = null;
			
			CreateDocDataObject item = (CreateDocDataObject) getItem(index);
			if (responce.containsKey(item.created))
				result = responce.get(item.created).id;
			
			return result;
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnSend)
			send();
		else if (v.getId() == R.id.btnDel) {
			deleteDlg();
		}
			
	}

	private void deleteDlg() {
		Intent i = new Intent(this, CalendarActivity.class);
		i.putExtra(ExtrasConst.DATE_TAG, Calendar.getInstance().getTime().getTime());
		startActivityForResult(i, R.id.delete_dlg);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == R.id.delete_dlg) {
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, new Date().getTime());
			Date newDate = new Date(ct);
			
			removeDocs(newDate.getYear() + 1900, newDate.getMonth(), newDate.getDate());
		}
	}
	
	private void removeDocs(final int year, final int month, final int date) {
		BackgroudProcess backgroudProcess = new BackgroudProcess(this, 
				new RunnableProcess()
				{
					
					@Override
					public void run()
					{
						Calendar calendar = Calendar.getInstance();
						calendar.set(year,month,date,23, 59, 0);
						calendar.set(Calendar.MILLISECOND, 0);
						
						Date date = calendar.getTime();
						
						for (DocType dt : listDocTypes()) 
							DocDeleteHelper.deleteTill(date, dt.create().getTableName(), "created");
						
						Cursor c = DataBaseManager.getDataBase().rawQuery("select a.id, a.path from attachment as a left join responseattach as r on a.id = r.id \r\n" + 
								"where r.created < ?", new String[] {Long.toString(date.getTime())}); 
						
						List<String> idlist = new ArrayList<String>();
						
						while(c.moveToNext()) {
							idlist.add(c.getString(c.getColumnIndex("id")));
							new File(c.getString(c.getColumnIndex("path"))).delete();
						}
						
						c.close();
						
						DocDeleteHelper.deleteTill(date, DataObjectInfo.getInstance().getTableName(ResponseAttach.class), "created");
						
						for(String id : idlist)
							DataBaseManager.getDataBase().rawQuery("delete from attachment where id = ?", new String [] {id});
					}
					
					@Override
					public void onPreExecute(){}
					
					@Override
					public void onPostExecute() { 
						refreshContent(); 
					}
				});
		
		backgroudProcess.execute((Void[])null);
	}

	protected void refreshContent() {
		((Adapter)list.getAdapter()).reload();
		((Adapter)list.getAdapter()).notifyDataSetChanged();
	}

	private void send() {
		UpdateProcess p = new UpdateProcess(this) {
			@Override
			protected void onPreExecute() {
				showDialog(R.id.wait_dlg);
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					dismissDialog(R.id.wait_dlg);
					((Adapter)list.getAdapter()).notifyDataSetChanged();
				}
			}
		};
		
		Config cfg = ConfigManager.getConfig();
		UpdateProcess.Params arg = new UpdateProcess.Params();
		arg.login = cfg.login;
		arg.pass = cfg.passw;
		arg.ip1 = cfg.address;
		arg.ip2 = cfg.address2;
		arg.port1 = cfg.port;
		
		arg.indata.add(new Hitching(ResponseAttach.class));
		
		List<ObjectListener> outdata = new ArrayList<ObjectListener>();
		
		for(DocType dt : listDocTypes())
			outdata.add(dt.getDirtyDocuments());
		
		arg.outdata = outdata;
		
		p.execute(arg);
	}

	protected DocType[] listDocTypes() {
		return new DocType[] {RequestdocDoc.instance()};
	}
}

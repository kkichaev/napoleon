package com.grsoft.napoleon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.NewClient;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.NewClientImpl;
import com.grsoft.napoleon.documents.NewClientDoc;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.napoleon.util.HorizontalListView;
import com.grsoft.napoleon.util.ImagesItemsAdapter;
import com.grsoft.napoleon.util.PhotoClickHandler;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.exception.InstanceNotInit;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.SrcDataCounter;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewClientEdit extends Activity
		implements LocationListener, PhotoClickHandler.EventHandler, OnMenuItemClickListener {
	public static final int PICK_IMAGE = 1;
	static final String key = "89f4536ed2b4a3946c18e232f6034739c0481a7a";

	private static final String PIC_PATH = "PicPath";

	NewClientImpl doc;

	protected String picPath;
	ImagesItemsAdapter adapter;
	VisitItem selectedItem;

	public static void open(Context context, NewClientImpl client) {
		Intent i = new Intent(context, NewClientEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, client != null ? client.getRowid() : ExtrasConst.INVALID_ROWID);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_client_edit);

		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		long rowid = b.getLong(ExtrasConst.DOC_ROW_ID_STR);

		doc = new NewClientImpl();
		if (rowid == ExtrasConst.INVALID_ROWID || !doc.read(rowid)) {
			doc.init(this, "", GPSUtilNew.getLastKnownLocation());
		}

		NewClient c = doc.getData();
		EditText ed;

		ed = (EditText) findViewById(R.id.edINN);
		ed.setText(c.inn);
		ed.addTextChangedListener(twatch);

		ed = (EditText) findViewById(R.id.edAddress);
		ed.setText(c.address);

		ConfigImpl ci = new ConfigImpl();
		Config cfg = ci.getData();
		ci.read("key", "ОтсрочкаНовыйКлиент");
		List<CharSequence> values = new ArrayList<CharSequence>();
		DialogHelper.makeList(cfg.value, values);

		int sel = -1;
		Iterator<CharSequence> i = values.iterator();
		while (i.hasNext()) {
			CharSequence obj = i.next();
			String val = obj.toString();
			try {
				int ival = Integer.parseInt(val);
				if (ival == c.delay) {
					sel = values.indexOf(obj);
				}
			} catch (Exception e) {
				i.remove();
				e.printStackTrace();
			}
		}

		Spinner s = (Spinner) findViewById(R.id.spDelay);
		ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(s.getContext(), R.layout.simple_spinner_layout,
				values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		s.setAdapter(aa);
		if (sel >= 0 && sel < s.getCount())
			s.setSelection(sel);

		ed = (EditText) findViewById(R.id.edPhone);
		ed.setText(c.phone);

		ed = (EditText) findViewById(R.id.edEmail);
		ed.setText(c.email);

		ed = (EditText) findViewById(R.id.edAccount);
		ed.setText(c.account);

		CheckBox cb = (CheckBox) findViewById(R.id.cbBlack);
		cb.setChecked(c.isBlack > 0);

		findViewById(R.id.btnGetInfo).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				requestInfo();
			}
		});

		if (doc.isEditable())
			findViewById(R.id.btnPhoto).setOnClickListener(new PhotoClickHandler(doc, this, NewClientDoc.instance()));

		findViewById(R.id.btnGallery).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
			}
		});

		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onOK();
			}
		});

		updateButtons();
		showInfo();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (isFinishing()) {
			if (!doc.isCompleete()) {
				doc.delete();
			}
		}
	}

	protected void onOK() {
		if (!doc.isEditable())
			return;

		NewClient c = doc.getData();
		EditText ed;
		ed = (EditText) findViewById(R.id.edINN);
		c.inn = ed.getText().toString();

		ed = (EditText) findViewById(R.id.edAddress);
		c.address = ed.getText().toString();

		ed = (EditText) findViewById(R.id.edPhone);
		c.phone = ed.getText().toString();

		ed = (EditText) findViewById(R.id.edEmail);
		c.email = ed.getText().toString();

		ed = (EditText) findViewById(R.id.edAccount);
		c.account = ed.getText().toString();

		CheckBox cb = (CheckBox) findViewById(R.id.cbBlack);
		c.isBlack = cb.isChecked() ? 1 : 0;

		Spinner s = (Spinner) findViewById(R.id.spDelay);
		CharSequence sel = (CharSequence) s.getSelectedItem();
		if (sel != null)
			c.delay = Integer.parseInt(sel.toString());

		if (!doc.isValid()) {
			Toast.makeText(this, "Не вся ифнормация заполнена", Toast.LENGTH_LONG).show();
			return;
		}

		doc.write();
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		if (!doc.isCompleete()) {
			doc.delete();
		}
	}

	class ClientSetter implements Runnable {
		JsonObject src;

		public ClientSetter(JsonObject src) {
			this.src = src;
		}

		@Override
		public void run() {
			NewClient c = doc.getData();
			c.name = "";
			c.kpp = "";
			c.ogrn = "";
			c.legalAdr = "";
			c.fio = "";
			c.post = "";

			if (src != null) {
				JsonElement el;
				el = src.get("value");
				if (el != null)
					c.name = el.getAsString();
				JsonElement dataEl = src.get("data");
				if (dataEl != null && dataEl.isJsonObject()) {
					JsonObject data = dataEl.getAsJsonObject();
					el = data.get("kpp");
					if (el != null)
						c.kpp = el.getAsString();

					el = data.get("inn");
					if (el != null)
						c.inn = el.getAsString();

					el = data.get("ogrn");
					if (el != null)
						c.ogrn = el.getAsString();

					JsonElement addrEl = data.get("address");
					if (addrEl != null && addrEl.isJsonObject()) {
						JsonObject obj = addrEl.getAsJsonObject();
						el = obj.get("unrestricted_value");
						if (el != null)
							c.legalAdr = el.getAsString();
					}

					JsonElement mgmEl = data.get("management");
					if (mgmEl != null && mgmEl.isJsonObject()) {
						JsonObject obj = mgmEl.getAsJsonObject();
						el = obj.get("name");
						if (el != null)
							c.fio = el.getAsString();
						el = obj.get("post");
						if (el != null)
							c.post = el.getAsString();
					}
				}
			}

			showInfo();
		}
	}

	void doDaData(final String queryStr) {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					String url = "https://suggestions.dadata.ru/suggestions/api/4_1/rs/suggest/party";
					URL u = new URL(url);
					HttpURLConnection conn = (HttpURLConnection) u.openConnection();
					conn.setRequestProperty("Content-Type", "application/json");
					conn.setRequestProperty("Accept", "application/json");
					conn.setRequestProperty("Authorization", "Token 89f4536ed2b4a3946c18e232f6034739c0481a7a");
					conn.setRequestMethod("POST");

					conn.setDoOutput(true);
					conn.setChunkedStreamingMode(0);
					OutputStream out = conn.getOutputStream();
					String query = String.format("{ \"query\": \"%s\", \"count\":1 }", queryStr.trim());
					out.write(query.getBytes("UTF-8"));

					InputStream in = new BufferedInputStream(conn.getInputStream());
					BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
					String line;
					String retSrc = "";
					while ((line = reader.readLine()) != null) {
						retSrc += line;
					}
					conn.disconnect();

					JsonObject client = null;
					JsonElement root = new JsonParser().parse(retSrc);
					if (root.isJsonObject()) {
						JsonElement el = root.getAsJsonObject().get("suggestions");
						if (el != null && el.isJsonArray()) {
							JsonArray data = el.getAsJsonArray();
							if (data.size() > 0) {
								el = data.get(0);
								if (el != null && el.isJsonObject()) {
									client = el.getAsJsonObject();
								}
							}
						}
					}
					NewClientEdit.this.runOnUiThread(new ClientSetter(client));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		new Thread(r).start();
	}

	public void showInfo() {
		NewClient c = doc.getData();
		String info = "";

		if (c.name.length() > 0)
			info += c.name + "<br/>ОГРН: " + c.ogrn;
		((TextView) findViewById(R.id.tvInfo)).setText(Html.fromHtml(info));

		info = "<br/>";
		if (c.legalAdr.length() > 0)
			info = "Юр.Адрес: " + c.legalAdr + "<br/>Руководитель: " + c.fio + " " + c.post;
		((TextView) findViewById(R.id.tvInfo1)).setText(Html.fromHtml(info));

		EditText ed = (EditText) findViewById(R.id.edINN);
		String curinn = ed.getText().toString();
		if (!curinn.equals(c.inn)) {
			ed.setText(c.inn);
		}
	}

	protected void requestInfo() {
		String inn = "";
		String addr = "";

		EditText ed;
		ed = (EditText) findViewById(R.id.edINN);
		inn = ed.getText().toString();

		// ed = (EditText) findViewById(R.id.edAddress);
		// addr = ed.getText().toString();

		if (inn.length() == 0) {
			return;
		}

		doDaData(inn + " " + addr);
	}

	TextWatcher twatch = new TextWatcher() {
		@Override public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
		@Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}@Override public void afterTextChanged(Editable arg0){updateButtons();}};

	void updateButtons() {
		boolean enabled = true;
		EditText ed;
		ed = (EditText) findViewById(R.id.edINN);
		if (ed.getText().length() == 0)
			enabled = false;
		// else {
		// ed = (EditText) findViewById(R.id.edAddress);
		// if(ed.getText().length() == 0)
		// enabled = false;
		// }

		findViewById(R.id.btnGetInfo).setEnabled(enabled);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doc.close();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		outState.putString(PIC_PATH, picPath);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		picPath = savedInstanceState.getString(PIC_PATH);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
			String img = savefile(data.getData());
			
			if (img.length() > 0)
				((PhotoDocument) doc).addPhoto(img.getBytes());
		}else if (requestCode == PhotoClickHandler.CAMERA_ACTIVITY && resultCode == RESULT_OK) {
			if (picPath != null && picPath.trim().length() > 0) {
				((PhotoDocument) doc).addPhoto(picPath.getBytes());
			}
		}
	}

	String savefile(Uri sourceuri) {
		String res = "";
		try {
			File file = new File(Path.getDataDir(), Integer.toString(SrcDataCounter.getValue()));
			res = file.getAbsolutePath();
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			
			try {
				bis = new BufferedInputStream(getContentResolver().openInputStream(sourceuri));
				bos = new BufferedOutputStream(new FileOutputStream(res, false));
				byte[] buf = new byte[1024];
				bis.read(buf);
				do {
					bos.write(buf);
				} while (bis.read(buf) != -1);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (bis != null)
						bis.close();
					if (bos != null)
						bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return res;
	}

	@Override
	protected void onResume() {
		super.onResume();

		doc.read(doc.getRowid(), false);

		adapter = new ImagesItemsAdapter(this, doc.getData().items);
		HorizontalListView g = (HorizontalListView) findViewById(R.id.gvItems);
		g.setAdapter(adapter);
		g.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectedItem = (VisitItem) adapter.getItem(arg2);
				PopupMenu menu = new PopupMenu(NewClientEdit.this, arg1);
				menu.setOnMenuItemClickListener(NewClientEdit.this);
				menu.inflate(R.menu.new_client_photo);
				menu.show();
				return true;
			}
		});
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepareBoforeClick() {
		doc.write();
	}

	@Override
	public void makePhotoFile(File newFile) {
		picPath = newFile.getAbsolutePath();
	}

	@Override
	public boolean onMenuItemClick(MenuItem arg0) {
		if (arg0.getItemId() == R.id.itShow) {
			String photo = new String(selectedItem.id);
			preview(photo);
		} else if (arg0.getItemId() == R.id.itDelete) {
			doc.getData().items.remove(selectedItem);
			doc.write();
			adapter.notifyDataSetChanged();
		}
		return false;
	}

	private void preview(String path) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View dialogView = View.inflate(this, R.layout.image_show, null);
		ImageView preview = (ImageView) dialogView.findViewById(R.id.imageView1);
		Bitmap bm = BitmapFactory.decodeFile(path);
		preview.setImageBitmap(bm);
		builder.setView(dialogView);
		builder.create().show();
	}
}

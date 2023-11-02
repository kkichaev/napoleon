package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.grsoft.dataobjects.BankIncass;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.PicStore;
import com.grsoft.dataobjects.impl.BankIncassImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PicStoreImpl;
import com.grsoft.napoleon.documents.BankDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;

import java.io.File;
import java.util.UUID;

public class BankIncassEdit extends Activity implements PopupMenu.OnMenuItemClickListener {

    BankIncassImpl doc = new BankIncassImpl();
    DocType docType;
    PicStoreImpl pict = new PicStoreImpl();

    KeypadHelper kh;

    String storePath = "";
    static int CAMERA_ACTIVITY = 1;

    static public void open(Context context, BankIncassImpl order) {
        Intent i = new Intent(context, BankIncassEdit.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_inscass_edit);

        docType = DocType.getCurDoc();
        DocType.setCurDoc(BankDoc.instance());

        kh = new KeypadHelper(this, R.id.sum);

        long orderRowId;
        if( savedInstanceState == null )
            orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
        else
            orderRowId = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);
        doc.read(orderRowId);

        BankIncass src = doc.getData();

        PicStore photo = pict.getData();
        photo.id = src.picture;
        pict.read();

        EditText ed = findViewById(R.id.sum);
        ed.setText(Util.IntToScaleStr(src.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
        ed.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus)
                ed.selectAll();
        });
        ed.selectAll();

        View ok = findViewById(R.id.btnOK);
        ok.setEnabled(doc.isEditable());
        ok.setOnClickListener(view -> save());

        ImageView iv = findViewById(R.id.photo);
        Drawable d = photo.getDrawable(this);
        if(d != null)
            iv.setImageDrawable(d);
        if(doc.isEditable())
            iv.setOnClickListener(view -> takePhoto());
    }

    private void takePhoto() {
        if(pict.isEmpty()) {
            takePhotoInt();
        } else {
            PopupMenu m = new PopupMenu(this, findViewById(R.id.photo));
            MenuInflater inf = m.getMenuInflater();
            m.setOnMenuItemClickListener(this);
            inf.inflate(R.menu.photo_options, m.getMenu());
            m.show();
        }
    }

    void takePhotoInt() {
        try {
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File path = new File(Path.getDataDir());
                path.mkdir();

                String id = UUID.randomUUID().toString().replace("-", "") + ".jpeg";
                File file = new File(getExternalFilesDir(null), id + ".jpg");
                storePath = file.getAbsolutePath();

                com.grsoft.napoleon.util.CfgNplW cfg = (com.grsoft.napoleon.util.CfgNplW) ConfigManager.getConfig();
                if (cfg.androidPhoto) {
                    Uri uri = null;

                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            uri = FileProvider.getUriForFile(this,getString(R.string.fileprovider_authorities), file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(uri == null)
                        uri = Uri.fromFile(file);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, CAMERA_ACTIVITY);
                } else {
                    CameraPreview.takePhoto(this, storePath, CAMERA_ACTIVITY);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_ACTIVITY && resultCode == Activity.RESULT_OK && storePath.trim().length() > 0) {

            BankIncass dsrc = doc.getData();
            PicStore src = pict.getData();

            src.id = UUID.randomUUID().toString().replace("-", "");
            src.picture = storePath.getBytes();
            src.date = dsrc.created;
            src.created = Util.getDateTime();

            dsrc.picture = src.id;
            pict.write();
            doc.write();

            ImageView iv = findViewById(R.id.photo);
            Drawable d = src.getDrawable(this);
            if(d != null)
                iv.setImageDrawable(d);

            storePath = "";
        }
    }

    private void save() {
        if(doc.getData().picture.length() == 0) {
            Toast.makeText(this, "Необходимо сделать фото чека", Toast.LENGTH_LONG).show();
            return;
        }
        BankIncass dsrc = doc.getData();
        long sum = Util.StrToScale(((EditText)findViewById(R.id.sum)).getText().toString(), Consts.SUM_SCALE);
        if(sum == 0) {
            Toast.makeText(this, "Введите сумму", Toast.LENGTH_LONG).show();
            return;
        }
        dsrc.sum = sum;
        doc.write();

        finish();
    }

    @Override
    public void onBackPressed() {
        if(doc.isEmpty() && doc.isEditable()) {
            doc.delete();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DocType.setCurDoc(docType);
        doc.close();
        pict.close();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.itShow) {
            pict.getData().preview(this);
        } else if(id == R.id.itTakePhoto) {
            takePhotoInt();
        }
        return false;
    }
}

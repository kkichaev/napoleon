package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.PathUtils;

import java.io.File;

public class WarehouseSettingEx extends WarehouseSetting{

    static final int PICKFILE_REQUEST_CODE = 10;

    @Override
    protected int getContentViewID() {
        return R.layout.warehouse_setting_ex;
    }

    @Override
    protected void init() {
        super.init();

        CfgNplEx ce = (CfgNplEx) config;
        ((TextView)findViewById(R.id.tvPhotoFolder)).setText(ce.pricePhotoIndex);

        findViewById(R.id.btnSelectFolder).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            String[] types = new String[] {
                    "text/xml",
                    "application/xml"
            };
            intent.putExtra(Intent.EXTRA_MIME_TYPES, types);
            startActivityForResult(intent, PICKFILE_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            boolean ret = false;String path = "";
            try {
                Uri uri = data.getData();
                path = PathUtils.getPathFromUri(this, uri);
                ret = PresentationUpdater.update(new File(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(ret) {
                ((CfgNplEx)config).pricePhotoIndex = path;
                ((TextView)findViewById(R.id.tvPhotoFolder)).setText(path);
            } else {
                Toast.makeText(this, "Ошибка при чтении файла, возможно выбран не правильный файл", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

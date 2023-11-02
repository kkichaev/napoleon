package com.grsoft.napoleon;

import android.os.Bundle;

import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;

import java.io.File;

public class PresentationFolderEx extends PresentationFolder{
    @Override
    protected int getLayoutId() {
        return R.layout.presentationfolder_ex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btnReload).setOnClickListener(v -> {
            try {
                String path = ((CfgNplEx) ConfigManager.getConfig()).pricePhotoIndex;
                if (PresentationUpdater.update(new File(path))) {
                    rebuildAdapter();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

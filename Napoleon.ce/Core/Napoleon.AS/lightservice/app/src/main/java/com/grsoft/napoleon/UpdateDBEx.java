package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.PriceDescription;
import com.grsoft.network.exception.RuntimeException;

import java.util.ArrayList;
import java.util.List;

public class UpdateDBEx extends UpdateDB {
    @Override protected int getContentView() { return R.layout.updatedbex; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.cbRemains).setVisibility(View.GONE);
    }

    @Override
    protected List<Hitching> getGenDataHitchings() throws RuntimeException {
        List<Hitching> ret = super.getGenDataHitchings();

        if(((CheckBox)findViewById(R.id.cbDescr)).isChecked()) {
            ret.add(new RcvNewHitching(PriceDescription.class));
        }

        if (!((CheckBox)findViewById(R.id.cbPrice)).isChecked()){
            List<Hitching> rem = new ArrayList<>();

            for(Hitching h : ret){
                if (h.getObjectName().equals("Folder") || h.getObjectName().equals("Price"))
                    rem.add(h);
            }

            ret.removeAll(rem);
        }

        return ret;
    }

    @Override
    protected boolean receiveGenData() {
        return super.receiveGenData() || ((CheckBox)findViewById(R.id.cbDescr)).isChecked();
    }

    @Override
    protected void postSync(Boolean result) {
        super.postSync(result);

        WarehouseEx.masterOrder = "";
    }
}

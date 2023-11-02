package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.grsoft.database.SalesResultHitching;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.PkoImpl;
import com.grsoft.dataobjects.impl.SalesBaseImpl;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.util.MessageBox;
import com.grsoft.util.gps.GPSUtilNew;

public class SalesDetailEx extends SalesDetail {
    @Override
    protected String[] createPrintCaption() {
		SalesEx se = (SalesEx)doc.getData();
        boolean isBlack = se.black != 0;
        return isBlack ? new String[] { "Накладная" } :
                new String[] {
                        NPrinter.TORG_12_CAPTION, NPrinter.SCHET_FACT_CAPTION,
                        NPrinter.UPD_CAPTION,
                        "Удостоверение качества"};
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.salesdetailex);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MNU_PKO_ID) {
            PkoImpl pko = PkoImpl.fromSales((SalesBaseImpl<?>)doc, GPSUtilNew.getLastKnownLocation(), this);
            pko.getData().number = doc.getData().number;
            pko.write();
            pko.open(this);

            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btnGetNumber).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                SalesResultHitching.result = 0;
                SalesResultHitching.message = "";
                send();
            }
        });
    }

    void updateButtons() {
        findViewById(R.id.btnPrint).setEnabled(!doc.isEditable());
        findViewById(R.id.btnGetNumber).setEnabled(doc.isEditable());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtons();
    }

    @Override
    public void postSendExecute(boolean result) {
        if(result) {
            doc.read(doc.getRowid(), false);
            if(doc.getData().number.length() == 0) {
                doc.getData().params = 0;
                doc.write();
            }
            updateButtons();
            if(SalesResultHitching.result == 0) {
                MessageBox.show(this, getString(R.string.error), SalesResultHitching.message);
            }
        }
    }
}

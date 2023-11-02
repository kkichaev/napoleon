package com.grsoft.napoleon;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.printsources.OrderSource;

import java.io.File;

public class OrderDetailEx extends OrderDetail{
    protected static final int WAIT_FOR_PRINT_DLG = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnSend.setOnClickListener((v)->doPrint());
        btnSend.setImageDrawable(getResources().getDrawable(R.drawable.print));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id){
            case WAIT_FOR_PRINT_DLG:
                return SelectPrinFormDlg.createWaitDlg(this);
            default:
                return super.onCreateDialog(id);
        }
    }

    private void doPrint() {
        OrderEx ex = (OrderEx) doc.getData();

        if(ex.pnum.trim().length() == 0){
            ex.pnum = DocHelper.makeDocNumber(doc);
            doc.write();
            doc.close();
        }

        new AsyncTask<String, Void, File>(){
            protected void onPreExecute() { OrderDetailEx.this.showDialog(WAIT_FOR_PRINT_DLG); };

            @Override
            protected File doInBackground(String... params) {
                File result = null;

                try {
                    if (params.length > 0)
                        result = NPrinter.print(OrderDetailEx.this, params[0], new OrderSource(doc.getData()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            protected void onPostExecute(File output) {
                OrderDetailEx.this.dismissDialog(WAIT_FOR_PRINT_DLG);
                if(output != null){
                    NPrinter.sendPrintTask(OrderDetailEx.this, output);
                }
            };
        }.execute("Заказ");
    }
}

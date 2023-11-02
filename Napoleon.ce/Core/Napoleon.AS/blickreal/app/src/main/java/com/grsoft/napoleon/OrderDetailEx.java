package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;

public class OrderDetailEx extends OrderDetail {
    public static final String ALERT_SUM_VAL = "alert_sum_val";

    @Override
    protected void onResume() {
        super.onResume();

        if (DocType.getCurDoc() == OrderDoc.instance()){
            ConfigImpl cfg = new ConfigImpl();
            StringBuilder sb = new StringBuilder();

            if (cfg.getValue(sb, "МинимальнаяСуммаЗаказа")){
                String minSumVal = sb.toString();
                try {
                    Integer val = Integer.parseInt(minSumVal);

                    if  (((double)doc.sum() / Consts.SUM_SCALE) < val){
                        DialogFragment dlg = new OrderDetailEx.AlertSumDlg();
                        Bundle args = new Bundle();
                        args.putString(ALERT_SUM_VAL, minSumVal);
                        dlg.setArguments(args);
                        dlg.show(getFragmentManager(), OrderDetailEx.AlertSumDlg.class.getCanonicalName());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static class AlertSumDlg extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String msg = getString(R.string.min_sum_alert, getArguments().getString(ALERT_SUM_VAL));
            builder.setMessage(msg);
            builder.setPositiveButton(R.string.ok, null);
            return builder.create();
        }
    }
}

package com.grsoft.napoleon.memo;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.dataobjects.MemoType;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.text.SimpleDateFormat;

public class Email extends BaseFragment {
    @Override
    protected int getLayoutID() {
        return R.layout.memo_email;
    }

    View v;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);

        model.setDisabled(false);

        EditText ed = v.findViewById(R.id.edEmail);
        ed.setText(model.doc.email.length() == 0 ? model.org.email : model.doc.email);

        v.findViewById(R.id.tvOrderInfo).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                OrderImpl ord = new OrderImpl();
                if(ord.read("created", model.doc.till)) {
                    ord.open(arg0.getContext());
                }
            }
        });

        MemoType mt = model.getType(model.doc.topic);
        int orderVisible = View.GONE;
        if(mt.sendingInvoice()) {
            orderVisible = View.VISIBLE;

            OrderImpl ord = new OrderImpl();
            String text = "";
            if(ord.read("created", model.doc.till)) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");
                text = "<font color='blue'><u>Заявка от " + sdf.format(ord.getData().created) + " сумма: " +
                        Util.IntToScaleStr(ord.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) + " руб.";
                text += "</u></font>";
            }
            ((TextView)v.findViewById(R.id.tvOrderInfo)).setText(Html.fromHtml(text));
        }
        v.findViewById(R.id.llOrder).setVisibility(orderVisible);
        return v;
    }

    @Override
    public void save() {
        EditText ed = (EditText)v.findViewById(R.id.edEmail);
        model.doc.email = ed.getText().toString();
    }
}

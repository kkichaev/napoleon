package com.grsoft.napoleon.memo;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.dataobjects.SyncInfo;
import com.grsoft.napoleon.CalendarActivity;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.UpdateDBEx;
import com.grsoft.napoleon.debet_data.DogovorData;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Unlock extends BaseFragment {
    protected static final int DIALOG_DATE_PICKER_ID = 0;

    View v;
    int selDog;

    ActivityResultLauncher<String[]> takePhoto;
    ActivityResultLauncher<Intent> changeDate;

    @Override
    protected int getLayoutID() {
        return R.layout.memo_unlock;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);

        Date dt = SyncInfo.getLastSync(SyncInfo.DEBT);
        boolean disable = (dt != null && ((new Date()).getTime() - dt.getTime()) > 24 * 3600 * 1000);
        model.setDisabled(disable);
        model.loadPicture(getContext());

        ImageView iv = v.findViewById(R.id.photo);
        iv.setOnClickListener(view -> { takePhoto.launch(new String[]{"image/*"}); });
        iv.setOnLongClickListener(view -> {
            if(model.picture == null)
                return false;
            showPopupMenu(iv);
            return true;
        });
        setImage(iv);


        EditText ed = v.findViewById(R.id.edRemark);
        ed.setText(model.doc.remark);

        ed = v.findViewById(R.id.edEmail);
        ed.setText(model.doc.email.length() == 0 ? model.org.email : model.doc.email);

        v.findViewById(R.id.tvSum).setOnClickListener(arg0 -> InputNumberDlg.open(getContext(), new InputNumber() {

            @Override public int getValue() { return (int)model.doc.sum; }

            @Override
            public void applayInput(int value, Object... params) {
                if(model.doc.isEditable()) {
                    model.doc.sum = value;
                    refreshSum();
                }

            }
        }, Consts.SUM_SCALE, false, "Введите сумму"));

        v.findViewById(R.id.tvSumPay).setOnClickListener(arg0 -> InputNumberDlg.open(getContext(), new InputNumber() {

            @Override public int getValue() { return (int)model.doc.sumPay; }

            @Override
            public void applayInput(int value, Object... params) {
                if(model.doc.isEditable()) {
                    model.doc.sumPay = value;
                    refreshSum();
                }

            }
        }, Consts.SUM_SCALE, false, "Введите сумму"));

        refreshSum();
        refreshDate();

        changeDate = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent b = result.getData();
            if(b != null) {
                Date curDate = Util.getDate();
                long ct = b.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
                if(ct < curDate.getTime())
                    ct = curDate.getTime();
                model.doc.till = new Date(ct);
            }
            refreshDate();
        });

        takePhoto = registerForActivityResult(new ActivityResultContracts.OpenDocument(), result -> {
            if(result != null) {
                model.updatePicture(result, getContext());
                setImage(iv);
            }
        });

        v.findViewById(R.id.btnSync).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View arg0) { UpdateDBEx.makeDebtSync(getContext());}
        });

        v.findViewById(R.id.llSync).setVisibility(disable ? View.VISIBLE : View.GONE);

        v.findViewById(R.id.tvUnblockTill).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getContext(), CalendarActivity.class);
                i.putExtra(ExtrasConst.DATE_TAG, model.doc.till.getTime());
                changeDate.launch(i);
            }
        });
        loadDogovors(model.org.ido, model.doc.id, model.doc.idDog);

        v.findViewById(R.id.deliveries).setOnClickListener(view ->{
            showDeliveries();
        });

        KeyValue sel = (KeyValue) ((Spinner)v.findViewById(R.id.spDogovor)).getSelectedItem();
        if(sel != null)
            model.doc.idDog = sel.key.toString();

        return v;
    }

    private void showDeliveries() {
        Spinner sp = (Spinner)v.findViewById(R.id.spDogovor);
        KeyValue sel = (KeyValue) sp.getSelectedItem();
        if(sel == null || sel.key.toString().length() == 0) {
            Toast.makeText(getContext(), R.string.need_select_dogovor, Toast.LENGTH_LONG).show();
        } else {
            model.setDogovor(sel.key.toString());
            DeliveriesSelector ds = new DeliveriesSelector();
            ds.show(getParentFragmentManager(), "");
        }
    }

    @Override
    public void save() {
        EditText ed = (EditText)v.findViewById(R.id.edRemark);
        model.doc.remark = ed.getText().toString();

        ed = (EditText)v.findViewById(R.id.edEmail);
        model.doc.email = ed.getText().toString();

        Spinner sp = (Spinner)v.findViewById(R.id.spDogovor);
        if(sp.getSelectedItem() != null) {
            model.setDogovor(((KeyValue)sp.getSelectedItem()).key.toString());
        }
    }

    private void showPopupMenu(ImageView iv) {
        PopupMenu pm = new PopupMenu(getContext(), iv);
        pm.inflate(R.menu.picture_handler);
        pm.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            if(id == R.id.delete) {
                model.deletePicture();
                iv.setImageResource(R.drawable.add_photo);
            } else if(id == R.id.preview) {
                model.picture.preview(getContext());
            }
            return true;
        });
        pm.show();
    }


    private void setImage(ImageView iv) {
        if (model.picture != null) {
            Drawable src = model.picture.getDrawable(getContext());
            if (src != null) {
                iv.setImageDrawable(src);
            }
        }
    }

    private void refreshDate() {
        SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String text = "<u>" + sd.format(model.doc.till) + "</u>";
        ((TextView)v.findViewById(R.id.tvUnblockTill)).setText(Html.fromHtml(text));
    }

    private void refreshSum() {
        String text = "<u>" + Util.IntToScaleStr(model.doc.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</u>";
        ((TextView)v.findViewById(R.id.tvSum)).setText(Html.fromHtml(text));

        text = "<u>" + Util.IntToScaleStr(model.doc.sumPay, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</u>";
        ((TextView)v.findViewById(R.id.tvSumPay)).setText(Html.fromHtml(text));
    }

    void loadDogovors(String ido, String id, final String selected) {
        final Date dueDate = Util.getDayEnd(Util.getDate());

        model.dogData.load(id);

        Spinner sp = (Spinner)v.findViewById(R.id.spDogovor);
        final List<KeyValue> values = new ArrayList<KeyValue>();

        values.add(new KeyValue("", ""));
        selDog = -1;
        for(DogovorData dd : model.dogData) {
            if(dd.id.equals(selected)) {
                selDog = model.dogData.indexOf(dd) + 1;
            }

            String value = dd.name + " " + Integer.toString(dd.dueDays) + "к/д/ " +
                    Util.IntToScaleStr(dd.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);

            value += "\nДолг: " + Util.IntToScaleStr(dd.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " / " +
                    Util.IntToScaleStr(dd.overdueSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " / " +
                    Integer.toString(dd.overdueDays);

            values.add(new KeyValue(dd.id, value));
        }

        ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(getContext(), R.layout.simple_spinner_layout, values);
        aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
        sp.setAdapter(aa);
        if( selDog >= 0)
            sp.setSelection(selDog);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                KeyValue kv = (KeyValue) adapterView.getAdapter().getItem(i);
                if(!kv.key.toString().equals(model.doc.idDog))
                    model.doc.deliveries = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }
}

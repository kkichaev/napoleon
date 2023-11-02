package com.grsoft.napoleon.script_wizard;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.napoleon.R;
import com.grsoft.napoleon.views.RoundedDialog;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputDlg extends RoundedDialog {

    public static final String RESULT_KEY = "InputDlgResult";

    static final char FRACTION_SEPARATOR = ',';
    private static final String ARG_KEY = "argKey";
    static Map<Integer, String> keys = new HashMap<>();

    static Pair<Integer, Pair<Integer, Integer>>[] pairs = new Pair[]{
            new Pair(R.id.row1, new Pair<>(R.id.item1, R.id.value1)),
            new Pair(R.id.row2, new Pair<>(R.id.item2, R.id.value2))
    };

    static {
        keys.put(R.id.btn0_click, "0");
        keys.put(R.id.btn1_click, "1");
        keys.put(R.id.btn2_click, "2");
        keys.put(R.id.btn3_click, "3");
        keys.put(R.id.btn4_click, "4");
        keys.put(R.id.btn5_click, "5");
        keys.put(R.id.btn6_click, "6");
        keys.put(R.id.btn7_click, "7");
        keys.put(R.id.btn8_click, "8");
        keys.put(R.id.btn9_click, "9");
    }

    public InputDlg(InputDlgParam[] params) {
        Bundle args = new Bundle();
        args.putParcelableArray(ARG_KEY, params);
        setArguments(args);
    }

    List<RowData> rows = new ArrayList<>();
    int selected = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.input_dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        for (Map.Entry<Integer, String> kv : keys.entrySet()) {
            v.findViewById(kv.getKey()).setOnClickListener(view -> onDigitPressed(kv.getValue()));
        }
        v.findViewById(R.id.ok).setOnClickListener(view -> onOK());

        v.findViewById(R.id.btn_comma_click).setOnClickListener(view -> onComma());
        v.findViewById(R.id.btn_del_click).setOnClickListener(view -> onDel());

        Bundle b = getArguments();
        Parcelable[] params = b.getParcelableArray(ARG_KEY);

        int index = 0;
        for (Pair<Integer, Pair<Integer, Integer>> i : pairs) {
            if (index >= params.length)
                break;

            RowData rd = new RowData();
            rd.row = v.findViewById(i.first);

            InputDlgParam param = (InputDlgParam) params[index];
            TextView label = v.findViewById(i.second.first);
            label.setText(param.label);

            rd.input = v.findViewById(i.second.second);
            rd.scale = param.scale;
            rd.fracLen = fracLen(rd.scale);

            rd.value = param.value == 0 ? "" : Util.IntToScaleStr(param.value, param.scale);
            rd.input.setText(formatValue(rd.value));

            rd.row.setOnClickListener(view -> selectRow(rows.indexOf(rd)));
            rows.add(rd);

            index++;
        }

        v.findViewById(R.id.btnComma).setVisibility(View.GONE);
        return v;
    }

    private void onOK() {
        long[] values = new long[rows.size()];
        int i = 0;
        for (RowData rd : rows) {
            long val = Util.StrToScale(rd.value, rd.scale);
            if (val == 0) {
                Toast.makeText(getContext(), R.string.input_value, Toast.LENGTH_LONG).show();
                selectRow(i);
                return;
            }

            values[i++] = val;
        }
        Bundle res = new Bundle();
        res.putLongArray(RESULT_KEY, values);
        getParentFragmentManager().setFragmentResult(RESULT_KEY, res);
        dismiss();
    }

    int fracLen(int scale) {
        int i = 0;
        while (scale > 1) {
            scale /= 10;
            i++;
        }
        return i;
    }

    private void onDel() {
        RowData rd = rows.get(selected);
        if (rd.value.length() > 0) {
            rd.value = rd.value.substring(0, rd.value.length() - 1);
            rd.input.setText(formatValue(rd.value));
        }
    }

    private void selectRow(int i) {
        if (i != selected) {
            selected = i;

            int idx = 0;
            for (RowData v : rows) {
                v.row.setBackgroundResource(idx++ == i ? R.color.white : R.color.purchase_back);
            }
        }
    }

    private void onDigitPressed(String value) {
        RowData rd = rows.get(selected);

        if (value.equals("0")) {
            if (rd.value.length() == 1 && rd.value.charAt(0) == '0')
                return;
        }

        if (rd.value.contains(String.valueOf(FRACTION_SEPARATOR))) {
            String[] vals = rd.value.split(String.valueOf(FRACTION_SEPARATOR));
            if (vals.length > 1 && vals[1].length() >= rd.fracLen)
                return;
        }

        rd.value += value;
        rd.input.setText(formatValue(rd.value));
    }

    public static String formatValue(String value) {
        String res = "";
        String intpart = value;
        int fs = value.indexOf(FRACTION_SEPARATOR);
        if (fs >= 0) {
            intpart = value.substring(0, fs);
        }

        for (int i = intpart.length() - 1; i >= 0; i--) {
            if (((intpart.length() - i - 1) % 3) == 0) {
                res = " " + res;
            }
            res = intpart.charAt(i) + res;
        }

        if (fs >= 0)
            res += value.substring(fs);
        return res;
    }

    private void onComma() {
        RowData rd = rows.get(selected);

        if (rd.value.contains(String.valueOf(FRACTION_SEPARATOR))) {
            return;
        }

        rd.value += FRACTION_SEPARATOR;
        rd.input.setText(formatValue(rd.value));
    }

    static class RowData {
        View row;
        TextView input;
        String value;
        int scale;
        int fracLen;
    }
}

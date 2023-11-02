package com.ksoft.dms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.text.DecimalFormat;

public class Calculator extends FragmentActivity implements View.OnClickListener, View.OnLongClickListener {
    public static final String DIGIT_RESULT = "DIGIT_RESULT";
    TextView tvRes;
    TextView tvRadInfo;
    TextView tvRad;
    EditText input;
    TextView tvDegree;
    TextView tvRadRes;
    DMSSymbols dmsSymbols = new DMSSymbols();

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.tvDMS)
            showPopupDMS();

        if (v instanceof  StateButton) {
            onClick(((StateButton) v).getTopView());
            return  true;
        }

        return false;
    }

    private View.OnClickListener popupClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((PopupWindow)((View)v.getParent()).getTag()).dismiss();
            Calculator.this.onClick(v);
        }
    };

    private void showPopupDMS() {
        View view = View.inflate(this, R.layout.dmspoup, null );

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        view.setTag(popupWindow);

        view.findViewById(R.id.tvPDegree).setOnClickListener(popupClick);
        view.findViewById(R.id.tvPMin).setOnClickListener(popupClick);;
        view.findViewById(R.id.tvPSec).setOnClickListener(popupClick);;

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    public static class DMSSymbols{
        char data[] = new char[]{'\u00b0', '\u2032', '\u2033'};
        int pos = 0;

        public char getSymbol(){
            char res = data[pos++];

            if (pos == data.length)
                pos = 0;

            return res;
        }

        public void reset(){
            pos = 0;
        };

        public boolean isDMSSymbol(char c){
            for (char d : data)
                if (c == d)
                    return true;

            return false;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calculator);
        tvRes = findViewById(R.id.tvRes);
        input = findViewById(R.id.input);
        tvDegree = findViewById(R.id.tvDegree);
        tvRadInfo = findViewById(R.id.tvRadInfo);
        tvRad = findViewById(R.id.tvRad);
        tvRadRes = findViewById(R.id.tvRadRes);

        findViewById(R.id.tv0).setOnClickListener(this);
        findViewById(R.id.tv1).setOnClickListener(this);
        findViewById(R.id.tv2).setOnClickListener(this);
        findViewById(R.id.tv3).setOnClickListener(this);
        findViewById(R.id.tv4).setOnClickListener(this);
        findViewById(R.id.tv5).setOnClickListener(this);
        findViewById(R.id.tv6).setOnClickListener(this);
        findViewById(R.id.tv7).setOnClickListener(this);
        findViewById(R.id.tv8).setOnClickListener(this);
        findViewById(R.id.tv9).setOnClickListener(this);
        findViewById(R.id.tvComma).setOnClickListener(this);
        findViewById(R.id.tvPlus).setOnClickListener(this);
        findViewById(R.id.tvMinus).setOnClickListener(this);
        findViewById(R.id.tvMul).setOnClickListener(this);
        findViewById(R.id.tvDiv).setOnClickListener(this);
        findViewById(R.id.tvDMS).setOnClickListener(this);
        findViewById(R.id.tvC).setOnClickListener(this);
        findViewById(R.id.tvRem).setOnClickListener(this);
        findViewById(R.id.tvScope).setOnClickListener(this);
        findViewById(R.id.tvMr).setOnClickListener(this);
        findViewById(R.id.tvSin).setOnClickListener(this);
        findViewById(R.id.tvCos).setOnClickListener(this);
        findViewById(R.id.tvTan).setOnClickListener(this);
        findViewById(R.id.tvMrd).setOnClickListener(this);
        findViewById(R.id.tvPow).setOnClickListener(this);

        findViewById(R.id.tvDMS).setOnLongClickListener(this);
        findViewById(R.id.tvSin).setOnLongClickListener(this);
        findViewById(R.id.tvCos).setOnLongClickListener(this);
        findViewById(R.id.tvTan).setOnLongClickListener(this);

        tvRad.setOnClickListener(this);

        input.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);   // handle the event first
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  // hide the soft keyboard
                }
                return true;
            }
        });

        View v = findViewById(R.id.tvResult);

        if (getCallingActivity() == null)
            v.setVisibility(View.GONE);
        else
            v.setOnClickListener((x->{
                Intent i = new Intent();
                i.putExtra(DIGIT_RESULT, tvRes.getText().toString());
                setResult(RESULT_OK, i);
                finish();
            }));

//        String res = "360" + dmsSymbols.getSymbol() + "11" + dmsSymbols.getSymbol() + "11" + dmsSymbols.getSymbol() + "/2";
//                + dmsSymbols.getSymbol() + "0" + dmsSymbols.getSymbol();
//        input.setText(res);
//        doCalc(res);
    }

    private void doCalc(String val) {
        double res = DMSParser.parse(val);
        DecimalFormat df = new DecimalFormat("###.#########");
        tvRes.setText(String.format("= %s",df.format(res).replace(",",".")));

        double deg = res;
        double rad = res;


        if (!DMSParser.degree)
            deg = Math.toDegrees(res);
        else
            rad = Math.toRadians(rad);

        deg = Math.abs(deg);
        rad = Math.abs(rad);

        tvDegree.setText(String.format("%s", DMSConvert.toDMSString(this, deg)).replace(",","."));
        tvRadRes.setText(String.format("%s %s", df.format(rad), getString(R.string.rad)).replace(",","."));
    }

    @Override
    public void onClick(View v) {
        if (v instanceof  StateButton)
            v = ((StateButton) v).getMainView();

        if (v.getId() == R.id.tvDMS) {
            DMSParser.degree = true;
            updateDegree();
            addExpression(Character.toString(dmsSymbols.getSymbol()));
            return;

        }

        if (v.getId() == R.id.tvC) {
            tvRes.setText("");
            input.setText("");
            tvDegree.setText("");
            tvRadRes.setText("");
            dmsSymbols.reset();
            return;
        }

        if (v.getId() == R.id.tvRem){
            int s = input.getSelectionStart();

            if (s > 0) {
                input.setText(input.getText().delete(s - 1, s));
                input.setSelection(s-1);
                doCalc(input.getText().toString());
            }

            return;
        }

        if (v.getId() == R.id.tvScope) {
            dmsSymbols.reset();
            String c = "(";
            String s = input.getText().toString().substring(0, input.getSelectionStart());

            if (!(s.indexOf('(') == -1)){
                char cc = s.charAt(s.length() - 1);
                if (Character.isDigit(cc) || dmsSymbols.isDMSSymbol(cc) )
                    c = ")";
            }

            addExpression(c);

            return;
        }

        if (v.getId() == R.id.tvMr ){
            if (tvRes.getText().toString().trim().length() > 0) {
                input.setText(tvRes.getText().toString().substring(1, tvRes.getText().length()).trim());
                input.setSelection(input.getText().length());
                tvRes.setText("");
                tvDegree.setText("");
                tvRadRes.setText("");
                dmsSymbols.reset();
            }

            return;
        }

        if (v.getId() == R.id.tvMrd ){
            if (tvRes.getText().toString().trim().length() > 0) {
                input.setText(tvDegree.getText().toString());
                input.setSelection(input.getText().length());
                tvRes.setText("");
                tvDegree.setText("");
                tvRadRes.setText("");
                dmsSymbols.reset();
            }

            return;
        }

        if (v.getId() == R.id.tvRad){
            DMSParser.degree = !DMSParser.degree;

            updateDegree();
            doCalc(input.getText().toString());

            return;
        }

        String expr = v.getTag().toString();

        if (expr.contains("+") || expr.contains("-") || expr.contains("*") || expr.contains("/"))
            dmsSymbols.reset();

        addExpression(expr);
    }

    private void updateDegree() {
        tvRadInfo.setText(DMSParser.degree ? R.string.deg : R.string.rad);
        tvRad.setText(!DMSParser.degree ? R.string.deg : R.string.rad);
    }

    public void addExpression(String expr) {
        input.getText().insert(input.getSelectionStart(),expr);
        doCalc(input.getText().toString());
    }
}

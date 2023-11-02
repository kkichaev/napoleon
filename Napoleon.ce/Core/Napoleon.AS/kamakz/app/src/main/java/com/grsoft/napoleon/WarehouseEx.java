package com.grsoft.napoleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Price;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;
import com.grsoft.util.InputNumber;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class WarehouseEx extends Warehouse {
    FilterData fd = new FilterData();
    List<String> seasons = new ArrayList<>();
    List<String> axes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        seasons = loadList("season");
        axes = loadList("axe");
    }

    private List<String> loadList(String column) {
        List<String> ret = new ArrayList<>();
        String stmt = "select distinct " + column + " from " + new Price().getTableName();

        try {
            Cursor c = DataBaseManager.getDataBase().rawQuery(stmt, null);
            while (c.moveToNext()) {
                ret.add(c.getString(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean res = super.onCreateOptionsMenu(menu);
        MenuItem item = menu.add(Menu.NONE, R.id.itPriceFilter, Menu.NONE, R.string.filter);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setIcon(getResources().getDrawable(R.drawable.filter2));
        return res;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itPriceFilter) {
            showFilterDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        final View view = View.inflate(this, R.layout.filter_dlg, null);

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setView(view);
        final AlertDialog p = b.create();

//        final PopupWindow p = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        loadSpinner((Spinner) view.findViewById(R.id.spAxe), axes);
        loadSpinner((Spinner) view.findViewById(R.id.spSeason), seasons);
        fd.putToView(this, view);

        view.findViewById(R.id.btnFilter).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                fd.setFromView(view);
                adapter.deleteFilter(PriceFilter.NAME);
                adapter.putFilter(new PriceFilter(fd));
                adapter.buildSet();
                p.dismiss();
            }
        });

        view.findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                adapter.deleteFilter(PriceFilter.NAME);
                adapter.buildSet();
                p.dismiss();
            }
        });

        p.show();
//        p.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    void loadSpinner(Spinner sp, List<String> values) {
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, values);
        aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
        sp.setAdapter(aa);
    }

    static class FilterData {
        public String season = "";
        public String axe = "";
        public int studded = 0;
        public int width = 0;
        public int height = 0;
        public int diameter = 0;

        public void setFromView(View v) {
            axe = (String) ((Spinner) v.findViewById(R.id.spAxe)).getSelectedItem();
            season = (String) ((Spinner) v.findViewById(R.id.spSeason)).getSelectedItem();
            studded = ((CheckBox) v.findViewById(R.id.cbStudded)).isChecked() ? 1 : 0;
        }

        public void putToView(final Activity context, View v) {
            final TextView tvWidth = ((TextView) v.findViewById(R.id.tvWidth));
            setText(tvWidth, width);
            tvWidth.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    InputNumberDlg.open(context, new InputNumber() {
                        @Override
                        public void applayInput(int value, Object... params) {
                            width = value;
                            setText(tvWidth, width);
                        }
                        @Override public int getValue() { return width; }
                    }, Consts.SUM_SCALE, true, "������� ");
                }
            });

            final TextView tvHeight = ((TextView) v.findViewById(R.id.tvHeight));
            setText(tvHeight, height);
            tvHeight.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    InputNumberDlg.open(context, new InputNumber() {
                        @Override
                        public void applayInput(int value, Object... params) {
                            height = value;
                            setText(tvHeight, height);
                        }
                        @Override public int getValue() { return height; }
                    }, Consts.SUM_SCALE, true, "������� ");
                }
            });

            final TextView tvDiameter = ((TextView) v.findViewById(R.id.tvDiameter));
            setText(tvDiameter, diameter);
            tvDiameter.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    InputNumberDlg.open(context, new InputNumber() {
                        @Override
                        public void applayInput(int value, Object... params) {
                            diameter = value;
                            setText(tvDiameter, diameter);
                        }
                        @Override public int getValue() { return diameter; }
                    }, Consts.SUM_SCALE, true, "������� ");
                }
            });

            ((CheckBox) v.findViewById(R.id.cbStudded)).setChecked(studded == 1);

            selectValue((Spinner) v.findViewById(R.id.spSeason), season);
            selectValue((Spinner) v.findViewById(R.id.spAxe), axe);
        }

        void setText(TextView tv, int number) {
            String text;
            if(number == 0) {
                text = "<u>������� ��������</u>";
            } else
                text = "<u>" + Integer.toString(number) + "</u>";
            tv.setText(Html.fromHtml(text));
        }

        void selectValue(Spinner sp, String value) {
            for (int i = sp.getCount() - 1; i >= 0; i--) {
                String ci = (String) sp.getItemAtPosition(i);
                if (ci.equals(value)) {
                    sp.setSelection(i);
                    break;
                }
            }
        }

        public String makeWhere() {
            String where = "";
            if(width != 0) {
                where += "width=" + Integer.toString(width);
            }
            if(height != 0) {
                if(where.length() > 0) where += " and ";
                where += "height="+ Integer.toString(height);
            }
            if(diameter != 0) {
                if(where.length() > 0) where += " and ";
                where += "dia="+ Integer.toString(diameter);
            }
            if(studded != 0) {
                if(where.length() > 0) where += " and ";
                where += "studded=1";
            }
            if(season.length() > 0) {
                if(where.length() > 0) where += " and ";
                where += "season='" + season + "'";
            }
            if(axe.length() > 0) {
                if(where.length() > 0) where += " and ";
                where += "axe='" + axe + "'";
            }
            return where;
        }
    }

    static class PriceFilter extends Filter {
        public static final String NAME = "PriceFilter";

        public PriceFilter(FilterData data) {
            super(NAME);
            where = data.makeWhere();
        }

    }
}

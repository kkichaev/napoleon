package com.grsoft.napoleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WhFilter extends Filter {
    public interface Events {
        void editFinished(boolean clear);
    }

    List<BaseFieldFilter> filters = new ArrayList<>();
    String name = "";

    public static String NAME = "WhFilter";
    public WhFilter() {
        super(NAME);

        filters.add(new BaseFieldFilter("width", "Ширина"));
        filters.add(new BaseFieldFilter("wall", "Высота"));
        filters.add(new BaseFieldFilter("diameter", "Диаметр"));
        filters.add(new BaseFieldFilter("brand", "Брэнд"));
        filters.add(new BaseFieldFilter("subbrand", "Суббрэнд"));
        filters.add(new BaseFieldFilter("autoType", "Тип шины"));

        Map<Object, String> seasons = new HashMap<>();
        seasons.put(1, "Зимние");
        seasons.put(2, "Летние");
        seasons.put(3, "Всесезонные");
        filters.add(new MappedFieldFilter("season", "Сезон", seasons));

        Map<Object, String> studded = new HashMap<>();
        studded.put(1, "Шипованные");
        studded.put(0, "Нет");
        filters.add(new MappedFieldFilter("studded", "Шипованные", studded));
    }

    public void add(PriceEx item) {
        for(BaseFieldFilter bf : filters) {
            bf.update(item);
        }
    }

    void show(Activity context, Events handler) {
        FilterDialog fd = new FilterDialog();
        fd.setFilters(filters, name, context);
        fd.setDoneHandler((text, clear) -> {
            name = text;
            handler.editFinished(clear);
        });
        fd.show(context.getFragmentManager(), "");
    }

    @Override
    public boolean inset(long priceRowID, String id) {
        return super.inset(priceRowID, id);
    }

    @Override
    public String getWhereStr() {
        String where = "";
        for(BaseFieldFilter f : filters) {
            String fw = f.filter();
            if(fw.length() > 0) {
                if(where.length() > 0) {
                    where += " AND ";
                }
                where += "(" + fw + ")";
            }
        }
        if(name.length() > 0) {
            String fw = "srchName LIKE '%" + name.toUpperCase().replace(' ', '%') + "%'";
            if(where.length() > 0) {
                where += " AND ";
            }
            where += "(" + fw + ")";
        }
        return where;
    }

    static class BaseFieldFilter {
        String fieldName;
        public Set<Object> values;
        public List<Object> selected;
        public String title;
        Field srcField;

        public BaseFieldFilter(String fieldName, String title) {
            this.fieldName = fieldName;
            this.title = title;

            values = new HashSet<>();
            selected = new ArrayList<>();
        }

        public void clear() { selected.clear(); }

        public void checking(Object el, boolean checked) {
            if(checked) {
                if(!selected.contains(el)) {
                    selected.add(el);
                }
            } else {
                selected.remove(el);
            }
        }

        @Override public String toString() { return title; }

        public String filter() {
            if(selected.size() > 0) {
                String in = "";
                for(Object v : selected) {
                    if(v instanceof String) {
                        in += String.format("'%s',", v);
                    } else {
                        in += v.toString() + ",";
                    }
                }
                return String.format("%s in (%s)", fieldName, in.substring(0, in.length()-1));
            }
            return "";
        }

        public boolean update(PriceEx src) {
            if(srcField == null) {
                try {
                    srcField = src.getClass().getField(fieldName);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
            if(srcField == null) {
                return false;
            }

            try {
                Object v = srcField.get(src);
                if(v.toString().length() > 0)
                    values.add(v);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return true;
        }

        public String valueToName(Object val) { return val.toString(); }
    }

    static class MappedFieldFilter extends BaseFieldFilter {
        Map<Object, String> map;
        public MappedFieldFilter(String fieldName, String title, Map<Object, String> map) {
            super(fieldName, title);
            this.map = map;
        }

        @Override
        public String valueToName(Object val) {
            String v = map.get(val);
            return v == null ? "" : v;
        }
    }

    static public class FilterDialog extends DialogFragment {

        public interface DoneEvent {
            void done(String text, boolean clear);
        }

        List<BaseFieldFilter> filters;
        DoneEvent handler;
        String text;
        Activity context;
        public void setFilters(List<BaseFieldFilter> filters, String text, Activity context) {
            this.filters = filters;
            this.text = text;
            this.context = context;
        }

        public void setDoneHandler(DoneEvent handler) {
            this.handler = handler;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog d = super.onCreateDialog(savedInstanceState);
            d.setTitle("Фильтр номенклатуры");
            return d;
        }

        class Adapter extends BaseAdapter {

            @Override public int getCount() {return filters.size();}

            @Override public Object getItem(int position) {return filters.get(position);}

            @Override public long getItemId(int position) {return position;}

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if(view == null) {
                    view = View.inflate(context, R.layout.filter_dlg_row, null);
                }
                BaseFieldFilter item = (BaseFieldFilter) getItem(position);
                TextView tv = view.findViewById(R.id.tvName);
                tv.setText(item.toString());
                int color = item.selected.size() > 0 ? Color.RED : Color.BLACK;
                tv.setTextColor(color);
                return view;
            }
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.filter_dlg, null);
            ListView lv = v.findViewById(R.id.lvItems);
            v.findViewById(R.id.btnClear).setOnClickListener(v1 -> {
                for (BaseFieldFilter bf : filters) {
                    bf.clear();
                }
                if(handler != null)
                    handler.done("", true);
                dismiss();
            });

            EditText ed = v.findViewById(R.id.edFind);
            ed.setText(text);

            v.findViewById(R.id.btnFilter).setOnClickListener(v1 -> {
                if(handler != null) {
                    String name = ed.getText().toString();
                    handler.done(name, false);
                }
                dismiss();
            });

            Adapter a = new Adapter();
            lv.setAdapter(a);
            lv.setOnItemClickListener((parent, view, position, id) -> {
                BaseFieldFilter bf = (BaseFieldFilter) a.getItem(position);
                ItemSelectDlg dlg = new ItemSelectDlg();
                dlg.setSrc(bf, context);
                dlg.show(getFragmentManager(), "");
                dlg.setHandler(() -> a.notifyDataSetChanged());
            });
            Display display = context.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            v.setMinimumWidth(size.x * 7 / 8);
            return v;
        }
    }

    public static class ItemSelectDlg extends DialogFragment {

        public interface Event {
            void onDismiss();
        }

        Event handler;
        BaseFieldFilter src;
        Context context;
        public void setSrc(BaseFieldFilter src, Context context) {
            this.src = src;
            this.context = context;
        }

        public void setHandler(Event handler) {this.handler = handler;}

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle(src.title);

            int count = src.values.size();
            CharSequence[] values = new CharSequence[count];
            boolean[] checked = new boolean[count];
            Object[] els = new Object[count];

            List<ElValue> data = new ArrayList<>();
            for(Object el : src.values) {
                data.add(new ElValue(el, src));
            }
            Collections.sort(data);

            int i = 0;
            for(ElValue el : data) {
                values[i] = el.name;
                els[i] = el.el;
                checked[i] = src.selected.contains(el.el);
                i++;
            }

            b.setMultiChoiceItems(values, checked, (dialog, which, isChecked) -> {
                src.checking(els[which], isChecked);
            });
            b.setPositiveButton(android.R.string.ok, (dialog, which) -> {
               dismiss();
               if(handler != null) {
                   handler.onDismiss();
               }
            });
            return b.create();
        }
    }
    static class ElValue implements Comparable<ElValue> {
        public String name;
        public Object el;
        public ElValue(Object el, BaseFieldFilter src) {
            this.el = el;
            name = src.valueToName(el);
        }

        @Override
        public int compareTo(ElValue o) {
            if(el instanceof Integer) {
                return (int)el - (int)o.el;
            }
            return name.compareTo(o.name);
        }
    }
}

package com.grsoft.napoleon.price;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.BSellingImpl;
import com.grsoft.dataobjects.impl.SellingImpl;
import com.grsoft.napoleon.BaseFragment;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.InputDlg;
import com.grsoft.napoleon.InputDlgParam;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.script_wizard.Selling;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

public class Price extends BaseFragment implements InputDlg.InputDlgControl{
    static final String TAG = Price.class.toString();

    Model priceModel;
    CostStrategy cs;
    Adapter adapter;

    @Override
    protected int getLayoutID() {
        return R.layout.price_view;
    }

    @Override
    public String TAG() {
        return TAG;
    }

    @Override
    public String getTitle() {
        if(model != null && model.getCurrentOrg() != null) {
            return model.getCurrentOrg().getValue().name;
        }
        return super.getTitle();
    }

//    @Override
//    public int getOptionMenu() {
//        return R.menu.price;
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.zero) {
            adapter.zeroFilter();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        priceModel = new ViewModelProvider(getActivity()).get(Model.class);
        cs = CostStrategy.defaultInstance;

        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.findViewById(R.id.btnOK).setOnClickListener(w -> getParentFragmentManager().popBackStack());
        RecyclerView rv = v.findViewById(R.id.items);
        adapter = new Adapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        getParentFragmentManager().setFragmentResultListener(InputDlg.RESULT_KEY,
            getViewLifecycleOwner(), (requestKey, result) -> {
                long[] values = result.getLongArray(InputDlg.RESULT_KEY);
                OrderItem oi = new OrderItem();
                oi.id = priceModel.editItem.id;
                oi.cost = (int)values[1];
                oi.qty = (int)values[0];

                ((SellingImpl)priceModel.doc).updateDoc(oi);
                adapter.reload();
                adapter.notifyDataSetChanged();
        });

        SearchView sv = v.findViewById(R.id.search);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null) adapter.filter(query);
                model.searchText = query;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    if (adapter != null) adapter.filter("");
                    model.searchText = "";
                }
                return false;
            }
        });

        return v;
    }

    @Override
    public boolean valid(InputDlg.RowData data) {
        return Selling.isInputValid(getContext(), priceModel.editItem, data, (SellingImpl) priceModel.doc);
    }

    @Override
    public void adjust(View view, InputDlg.RowData row) {
        Selling.adjustComma(view, row);
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        List<com.grsoft.dataobjects.PriceEx> items = new ArrayList<>();
        List<com.grsoft.dataobjects.PriceEx> allItems = new ArrayList<>();
        private String filter = "";
        private boolean zero = true;

        public Adapter() {
            reload();
        }

        private void reload() {
            StringBuilder where = new StringBuilder("hidden=0");

//            if (zero) {
//                if(priceModel.doc instanceof BSellingImpl) {
//                    where.append(" and bqty > 0");
//                } else {
//                    where.append(" and qty > 0");
//                }
//            }

            items = DbReader.fetch(com.grsoft.dataobjects.PriceEx.class, where.toString(), "name");
            allItems.clear();
            if(zero) {
                for(com.grsoft.dataobjects.PriceEx p : items) {
                    if(priceModel.doc.getItemValue(p) > 0) {
                        allItems.add(p);
                    }
                }
            }
//            allItems.addAll(items);
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.price_row, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.update(items.get(position), position);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void filter(String text) {
            this.filter = text;
            if(text == null || text.length() == 0) {
                items = allItems;
            } else {
                String[] srch = text.toUpperCase(Locale.ROOT).split(" ");

                items = new ArrayList<>();
                for (com.grsoft.dataobjects.PriceEx p : allItems) {
                    boolean contains = true;
                    for (String si : srch) {
                        if (!p.name.toUpperCase(Locale.ROOT).contains(si)) {
                            contains = false;
                            break;
                        }
                    }
                    if (contains) {
                        items.add(p);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public void zeroFilter() {
            zero = !zero;
            reload();
            notifyDataSetChanged();
        }

        public boolean isZero(){
            return zero;
        }

        class Holder extends RecyclerView.ViewHolder {

            public Holder(@NonNull View itemView) {
                super(itemView);
            }

            public void update(com.grsoft.dataobjects.PriceEx item, int pos) {
                int textColor = Color.BLACK;

                OrderItem oi = null;
                if(priceModel.doc != null &&  (oi = (OrderItem) priceModel.doc.findItem(item.id)) != null) {
                    textColor = getContext().getColor(R.color.green);
                }
                TextView tv = itemView.findViewById(R.id.name);
                tv.setText(item.name);
                tv.setTextColor(textColor);

                long cost =  oi != null ? oi.cost : cs.getItemCost(item, priceModel.doc);
                tv = itemView.findViewById(R.id.cost);
                tv.setText(InputDlg.formatValue(cost, Consts.SUM_SCALE, false));
                tv.setTextColor(textColor);

                tv = itemView.findViewById(R.id.qty);

                int qty = priceModel.doc.getItemValue(item);
                tv.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
                tv.setTextColor(textColor);

                itemView.setBackgroundResource((pos % 2) != 0 ? R.drawable.odd_row_back : R.drawable.even_row_back);
                itemView.setOnClickListener(view -> {
                    editItem(item);
                });
            }
        }
    }

    private void editItem(com.grsoft.dataobjects.PriceEx price) {
        OrderItem item = priceModel.doc == null ? null : (OrderItem) priceModel.doc.findItem(price.id);
        int qty = item == null ? 0 : item.qty;
        long cost = item == null ? cs.getItemCost(price, priceModel.doc) : item.cost;

        priceModel.editItem = price;

        InputDlgParam[] params = new InputDlgParam[] {
                new InputDlgParam(R.string.qty, qty, Consts.QTY_SCALE),
                new InputDlgParam(R.string.cost, cost, Consts.SUM_SCALE),
        };
        InputDlg dlg = new InputDlg(params, price.name);
        dlg.control = this;
        dlg.show(getParentFragmentManager(), "");
    }
}

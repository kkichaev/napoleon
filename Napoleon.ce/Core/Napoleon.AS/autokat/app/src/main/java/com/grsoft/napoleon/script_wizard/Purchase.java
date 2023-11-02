package com.grsoft.napoleon.script_wizard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PurchaseItem;
import com.grsoft.dataobjects.impl.PurchaseImpl;
import com.grsoft.napoleon.InputDlg;
import com.grsoft.napoleon.InputDlgParam;
import com.grsoft.napoleon.PriceHolder;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class Purchase extends BaseFragment implements InputDlg.InputDlgControl {
    PurchaseImpl doc;
    static final String TAG = Purchase.class.toString();

    @Override
    protected int getLayoutID() {
        return R.layout.purchase_script_view;
    }

    @Override
    public String TAG() {
        return TAG;
    }

    @Override
    public boolean validate(boolean moveBack) {
        return true;
    }

    View v;
    PurchaseItem editItem;
    Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        Log.d("Purchase", "onCreateView");

        doc = (PurchaseImpl) getCurDoc(getContext());

        RecyclerView rv = v.findViewById(R.id.items);
        adapter = new Adapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        getParentFragmentManager().setFragmentResultListener(InputDlg.RESULT_KEY,
                getViewLifecycleOwner(), (requestKey, result) -> {
                    long[] values = result.getLongArray(InputDlg.RESULT_KEY);
                    editItem.weight = (int)values[0];
                    editItem.qty = Consts.QTY_SCALE;
                    editItem.cost = (int)values[1];
                    adapter.notifyItemChanged(doc.getData().items.indexOf(editItem), editItem);
                    updateTotal();
                });

        updateTotal();
        return v;
    }

    private void updateTotal() {
        TextView tv = v.findViewById(R.id.total_wight);
        String text = InputDlg.formatValue(Util.IntToScaleStr(doc.weight(), Consts.QTY_SCALE));
        tv.setText(text);

        tv = v.findViewById(R.id.total_sum);
        text = InputDlg.formatValue(Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE));
        tv.setText(text);
    }

    void editItem(PurchaseItem item) {
        editItem = item;
        InputDlgParam[] params = new InputDlgParam[]{
                new InputDlgParam(R.string.weight, item.weight, Consts.WEIGHT_SCALE),
                new InputDlgParam(R.string.worth, item.cost, Consts.SUM_SCALE),
        };
        InputDlg dlg = new InputDlg(params, PriceHolder.get(item.id).name);
        dlg.control = this;
        dlg.show(getParentFragmentManager(), "");
    }

    boolean onItemMenu(PurchaseItem item, View view) {
        if (!item.inited())
            return true;
        PopupMenu pm = new PopupMenu(getContext(), view);
        pm.inflate(R.menu.doc_item_context_menu);
        pm.setOnMenuItemClickListener(item1 -> {
            if (item1.getItemId() == R.id.delete) {
                item.weight = 0;
                item.qty = 0;
                item.cost = 0;
                adapter.notifyItemChanged(doc.getData().items.indexOf(item), item);
                updateTotal();
            }
            return true;
        });
        pm.show();
        return true;
    }

    @Override
    public boolean valid(InputDlg.RowData data) {
        return true;
    }

    @Override
    public void adjust(View view, InputDlg.RowData row) {
        View v = view.findViewById(R.id.btn_comma_click);
        v.setEnabled(row.label != R.string.worth);
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.purchase_row_item, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            PurchaseItem oi = (PurchaseItem) doc.getData().items.get(position);
            holder.update(oi);
        }

        @Override
        public int getItemCount() {
            return doc.getData().items.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            public Holder(@NonNull View itemView) {
                super(itemView);
            }

            public void update(PurchaseItem item) {
                TextView tv;
                Price p = PriceHolder.get(item.id);
                tv = itemView.findViewById(R.id.name);
                tv.setText(p.name);

                String text;
                tv = itemView.findViewById(R.id.weight);
                text = item.weight == 0 ? "" : InputDlg.formatValue(Util.IntToScaleStr(item.weight, Consts.WEIGHT_SCALE, Util.DEC_DELIM, false));
                tv.setText(text);

                tv = itemView.findViewById(R.id.cost);
                text = item.qty == 0 ? "" : InputDlg.formatValue(Util.IntToScaleStr(item.cost, Consts.SUM_SCALE));
                tv.setText(text);

                itemView.setOnClickListener(view -> editItem(item));
                itemView.setOnLongClickListener(view -> onItemMenu(item, view));
            }
        }
    }
}

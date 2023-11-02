package com.grsoft.napoleon.script_wizard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.StoreData;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.dataobjects.impl.SellingImpl;
import com.grsoft.napoleon.InputDlg;
import com.grsoft.napoleon.InputDlgParam;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.PriceHolder;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.main.PurchaseRequiredDlg;
import com.grsoft.napoleon.price.Model;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class Selling extends BaseFragment implements InputDlg.InputDlgControl {
    static final String TAG = Selling.class.toString();

    View v;
    Model priceModel;
    SellingImpl doc;
    RecyclerView rv;

    OrderItem editItem;

    @Override
    protected int getLayoutID() {
        return R.layout.selling_script_view;
    }

    @Override
    public String TAG() {
        return TAG;
    }

    @Override
    public boolean validate(boolean moveBack) {
        return true;
    }



    @Override
    public int getOptionMenu() {
        return R.menu.selling;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        doc = (SellingImpl) getCurDoc(getContext());

        if (doc == null){
            getParentFragmentManager().popBackStack();
        }

        priceModel = new ViewModelProvider(getActivity()).get(Model.class);
        priceModel.doc = doc;
        priceModel.getOrderItem().observe(getViewLifecycleOwner(), orderItem -> {
            updateDoc(orderItem);
        });

        getParentFragmentManager().setFragmentResultListener(InputDlg.RESULT_KEY,
                getViewLifecycleOwner(), (requestKey, result) -> {
                    long[] values = result.getLongArray(InputDlg.RESULT_KEY);
                    editItem.qty = (int)values[0];
                    editItem.cost = (int)values[1];
                    updateDoc(editItem);
                });

        rv = v.findViewById(R.id.items);
        rv.setAdapter(new Adapter());
        rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        updateTotal();

        String title = doc.getData().title;
        if(title.length() > 0) {
            TextView tv = (TextView)v.findViewById(R.id.sales_title);
            tv.setText(title);
            tv.setVisibility(View.VISIBLE);
        }

        return v;
    }

    private void updateDoc(OrderItem src) {
        doc.updateDoc(src);
        model.saveCurrentDoc();
        refreshAdapter();
    }

    private void refreshAdapter() {
        int pos = ((LinearLayoutManager)rv.getLayoutManager()).findFirstVisibleItemPosition();
        rv.setAdapter(new Adapter());
        ((LinearLayoutManager)rv.getLayoutManager()).scrollToPosition(pos);
        updateTotal();
    }

    private void updateTotal() {
        TextView tv = v.findViewById(R.id.total_wight);
        String text = InputDlg.formatValue(Util.IntToScaleStr(doc.count(), 0));
        tv.setText(text);

        tv = v.findViewById(R.id.total_sum);
        text = InputDlg.formatValue(Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE));
        tv.setText(text);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add) {
            ((MainActivity)getActivity()).requestPrice();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean onItemMenu(OrderItem item, View view) {
        PopupMenu pm = new PopupMenu(getContext(), view);
        pm.inflate(R.menu.doc_item_context_menu);
        pm.setOnMenuItemClickListener(item1 -> {
            if(item1.getItemId() == R.id.delete) {
                doc.removeItem(item);
                refreshAdapter();
            }
            return true;
        });
        pm.show();
        return true;
    }

    public static boolean isInputValid(Context context, PriceEx item, InputDlg.RowData data, SellingImpl doc){
        int itemQty = doc.getItemValue(item);
        int val = itemQty - (int)Util.StrToScale(data.value, Consts.QTY_SCALE);
        if (data.label == R.string.qty && val < 0){
            Toast.makeText(context, context.getString(R.string.qty_error, Util.IntToScaleStr(itemQty, Consts.QTY_SCALE)), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean valid(InputDlg.RowData data) {
        return isInputValid(getContext(), priceModel.editItem, data, doc);
    }

    public static void adjustComma(View view, InputDlg.RowData row){
        View v = view.findViewById(R.id.btn_comma_click);
        v.setEnabled(row.label != R.string.cost);
    }

    @Override
    public void adjust(View view, InputDlg.RowData row) {
        adjustComma(view, row);
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
            OrderItem oi = (OrderItem) doc.getData().items.get(position);
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

            public void update(OrderItem item) {
                TextView tv;
                Price p = PriceHolder.get(item.id);
                tv = itemView.findViewById(R.id.name);
                tv.setText(p.name);

                String text;
                tv = itemView.findViewById(R.id.weight);
                text = InputDlg.formatValue(Util.IntToScaleStr(item.qty, Consts.WEIGHT_SCALE));
                tv.setText(text);

                long sum = (long)((long)item.cost * item.qty / Consts.QTY_SCALE);
                tv = itemView.findViewById(R.id.cost);
                text = InputDlg.formatValue(Util.IntToScaleStr(sum, Consts.SUM_SCALE));
                tv.setText(text);
                itemView.setOnClickListener(view -> editItem(item));
                itemView.setOnLongClickListener(view -> onItemMenu(item, view));
            }
        }
    }

    private void editItem(OrderItem item) {
        editItem = item;
        InputDlgParam[] params = new InputDlgParam[] {
                new InputDlgParam(R.string.qty, item.qty, Consts.QTY_SCALE),
                new InputDlgParam(R.string.cost, item.cost, Consts.SUM_SCALE),
        };
        InputDlg dlg = new InputDlg(params, PriceHolder.get(item.id).name);
        dlg.control = this;
        dlg.show(getParentFragmentManager(), "");
    }

    @Override
    public void onPause() {
        super.onPause();

        priceModel.setOrderItem(null);
    }
}

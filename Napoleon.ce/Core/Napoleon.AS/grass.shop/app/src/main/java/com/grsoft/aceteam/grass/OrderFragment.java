package com.grsoft.aceteam.grass;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.grsoft.camera.BarcodeHandler;
import com.grsoft.camera.CameraActivity;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napmobile.R;
import com.grsoft.napoleon.util.Config;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderFragment extends BaseFragment {
    private RecyclerView list;
    OrderAdapter adapter;

    SwipeHelper swipeHelper;

    @Override protected int getLayoutID() {return R.layout.order;}

    @Override public String TAG() {return "Order";}

    @Override
    public String getTitle() {return getString(R.string.order);}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    int bcidx = 0;
    String[] bcs = new String[] {
            "4670113605223"
            ,"4650067522203"
            ,"4630037510973"
            ,"4670113605230"
            ,"4630097265332"
            ,"4630097267534"
            ,"4670042904961"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        model.prepareOrder();

        boolean editable = model.order.isEditable();

        list = v.findViewById(R.id.items);
        adapter = new OrderAdapter(getContext(), (view, position) -> {
            if(editable)
                model.onItemSelected(position);
        });

        v.findViewById(R.id.new_order).setOnClickListener(view -> {
            model.setOrder(null);
            model.prepareOrder();
        });

        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        swipeHelper = new SwipeHelper();
        new ItemTouchHelper(swipeHelper).attachToRecyclerView(list);

        model.onOrderChanged().observe(getViewLifecycleOwner(), integer -> {
            refreshView(v, model.order);
        });

        model.currentItem().observe(getViewLifecycleOwner(), priceEx -> {
            if(priceEx != null) {
                DialogFragment df = new ItemEditDlg();
                df.show(getChildFragmentManager(), "");
            }
        });

        v.findViewById(R.id.finish_order).setOnClickListener(view -> {
            if(editable && !model.order.isEmpty()) {
                sendOrder(v);
            }
        });

        v.findViewById(R.id.scan).setOnClickListener(view -> {
            if(Model.TEST) {
                if(bcidx >= bcs.length) {
                    bcidx = 0;
                }
                model.onScanBC(bcs[bcidx++]);
                return;
            }
            CameraActivity.openBCScanner(getContext(), new BarcodeHandler() {
                @Override
                public boolean onReadBarcode(Activity owner, String barcode, int type, long elapsesMs) {
                    model.onScanBC(barcode);
                    return false;
                }
                @Override public void initActivity(Activity owner) {}
            });
        });

        return v;
    }

    private void sendOrder(View v) {
//        if(Model.TEST) {
//            model.order.getData().number = "14";
//            ((Main)getActivity()).openOrderNumber(model.order);
//            return;
//        }
        v.findViewById(R.id.wait).setVisibility(View.VISIBLE);
        model.sendOrder(() -> {
            getActivity().runOnUiThread(() -> {
                v.findViewById(R.id.wait).setVisibility(View.GONE);
                ((Main)getActivity()).openOrderNumber(model.order);
            });
        });
    }

    class SwipeHelper extends ItemTouchHelper.Callback {

        boolean enableSwipe;

        public void enableSwipe(boolean enable) { enableSwipe = enable; }

        @Override public boolean isItemViewSwipeEnabled() {return enableSwipe;}

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int swipeflag = ItemTouchHelper.RIGHT;
            return makeMovementFlags(0, swipeflag);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {return false;}

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getAdapterPosition();
            OrderItemEx oie = adapter.removeItem(pos);
            model.removeIem(oie);

            Snackbar.make(list, R.string.delete_item, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undu, view -> {
                        adapter.insertItem(oie, pos);
                        model.insetItem(oie, pos);
                    })
                    .show();
        }
    }
    private void refreshView(View v, OrderImpl order) {
        adapter.refresh(order.getData().items);
        int vsbl = order.isEditable() ? View.VISIBLE : View.INVISIBLE;
        int invv = !order.isEditable() ? View.VISIBLE : View.INVISIBLE;
        v.findViewById(R.id.finish_order).setVisibility(vsbl);
        v.findViewById(R.id.scan).setVisibility(vsbl);
        v.findViewById(R.id.new_order).setVisibility(invv);

        String sum = Util.IntToScaleStr(order.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false);
        ((TextView)v.findViewById(R.id.orderSum)).setText(sum);

        swipeHelper.enableSwipe(order.isEditable());
    }
}

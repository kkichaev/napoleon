package com.grsoft.manager.memo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.grsoft.manager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDialog extends DialogFragment {

    static Map<OrderField.Type, Pair<Integer, Integer>> views = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle(R.string.ordering);

        initViews();
        final Model model = new ViewModelProvider(getActivity()).get(Model.class);

        final Ordering ordering = new Ordering(model.order.getValue());

        final View v = LayoutInflater.from(getContext()).inflate(R.layout.memo_order, null);

        refreshOrder(v, ordering);

        for(final Map.Entry<OrderField.Type, Pair<Integer, Integer>> kv : views.entrySet()) {
            View tv = v.findViewById(kv.getValue().first);
            tv.setOnClickListener(view -> {
                ordering.update(kv.getKey());
                refreshOrder(v, ordering);
            });
        }

        v.findViewById(R.id.ok).setOnClickListener(view -> {
            model.updateOrdering(ordering);
            dismiss();
        });

        return v;
    }

    static void initViews() {
        if(views.size() == 0) {
            views.put(OrderField.Type.Org, new Pair<>(R.id.org, R.id.org_order));
            views.put(OrderField.Type.Created, new Pair<>(R.id.created, R.id.created_order));
            views.put(OrderField.Type.Topic, new Pair<>(R.id.topic, R.id.topic_order));
            views.put(OrderField.Type.Status, new Pair<>(R.id.status, R.id.status_order));
        }
    }

    private void refreshOrder(View v, Ordering ordering) {
        List<OrderField.Type> unused = new ArrayList<>(views.keySet());

        int idx = 1;
        for(OrderField of :ordering.fields) {
            unused.remove(of.type);
            Pair<Integer, Integer>  p = views.get(of.type);
            TextView tv = v.findViewById(p.second);
            String text = Integer.toString(idx);
            tv.setText(text);
            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    (of.direction == OrderField.ORDER_UP ? R.drawable.sort_up : R.drawable.sort_down),
                    0);
            idx++;
        }

        for(OrderField.Type t : unused) {
            TextView tv = v.findViewById(views.get(t).second);
            tv.setText("");
            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }
}

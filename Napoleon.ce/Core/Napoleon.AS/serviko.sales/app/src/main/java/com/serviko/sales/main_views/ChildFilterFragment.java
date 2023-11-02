package com.serviko.sales.main_views;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.serviko.sales.R;
import com.serviko.sales.main_views.order_filter.OrderFilter;

public abstract class ChildFilterFragment extends Fragment {
    protected Model model;

    public interface Handler {
        void backing();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater ti = TransitionInflater.from(requireContext());
        setEnterTransition(ti.inflateTransition(R.transition.slide_right));
        setExitTransition(ti.inflateTransition(R.transition.fade));
    }

    protected abstract int getResourceId();
    protected abstract Filter getFilter();

    protected Pair<Integer,String>[] bindings() { return new Pair[]{}; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(getActivity()).get(Model.class);

        View v = inflater.inflate(getResourceId(), container, false);
        View back = v.findViewById(R.id.back);
        if(getParentFragment() instanceof Handler && back != null) {
            back.setOnClickListener(view -> {
                ((Handler) getParentFragment()).backing();
            });
        }

        Filter of = getFilter();
        for(Pair<Integer,String> p : bindings()) {
            ViewGroup bv = v.findViewById(p.first);
            if(bv != null) {
                ImageView ico = findIcon(bv);
                if(ico != null) {
                    setImage(ico, of.getValue(p.second));
                    bv.setOnClickListener(clicked -> {
                        onClicked(bv, ico, p.second);
                    });
                }
            }
        }
        return v;
    }

    protected void onClicked(ViewGroup bv, ImageView ico, String filedName) {
        Filter of = getFilter();
        boolean tval = !of.getValue(filedName);
        setImage(ico, tval);
        of.setValue(filedName, tval);
    }

    protected void setImage(ImageView ico, boolean value) {
        ico.setImageResource(value ? R.drawable.ic_select_on :
                R.drawable.ic_select_off);
    }

    protected ImageView findIcon(ViewGroup parent) {
        for(int i=0; i<parent.getChildCount(); i++ ) {
            View v = parent.getChildAt(i);
            if(v instanceof ImageView) {
                return (ImageView) v;
            }
        }

        return null;
    }
}

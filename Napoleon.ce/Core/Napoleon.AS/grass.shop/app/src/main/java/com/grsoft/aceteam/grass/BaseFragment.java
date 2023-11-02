package com.grsoft.aceteam.grass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public abstract class BaseFragment extends Fragment {
    protected abstract int getLayoutID();

    public int getOptionMenu() { return 0;}

    public abstract String TAG();

    protected Model model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(getActivity()).get(Model.class);

        View v = inflater.inflate(getLayoutID(), container, false);
        return v;
    }

    public String getTitle() { return ""; }

    @Override
    public void onResume() {
        super.onResume();

        ((Main)getActivity()).fragmentResumed(this);
    }
}

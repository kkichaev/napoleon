package com.grsoft.napoleon.memo;

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

    protected Model model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutID(), container, false);
        model = new ViewModelProvider(getActivity()).get(Model.class);
        return v;
    }

    public abstract void save();
}

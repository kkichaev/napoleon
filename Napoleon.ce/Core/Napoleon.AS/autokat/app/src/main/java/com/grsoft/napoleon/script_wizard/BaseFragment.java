package com.grsoft.napoleon.script_wizard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.grsoft.napoleon.documents.CreatableDocument;

public abstract class BaseFragment extends Fragment {

    Model model;

    protected abstract int getLayoutID();
    public abstract String TAG();

    public abstract boolean validate(boolean moveBack);

    public int getOptionMenu() { return 0; }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewModelStoreOwner owner = getParentFragment() != null ? getParentFragment() : getActivity();
        model = new ViewModelProvider(owner).get(Model.class);
        View v = inflater.inflate(getLayoutID(), container, false);

        return v;
    }

    public CreatableDocument getCurDoc(Context context){
        return model.getCurDoc(context);
    }
}

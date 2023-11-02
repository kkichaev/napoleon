package com.grsoft.napoleon.main;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.script_wizard.QuestEdit;

public class QuestEditPreview extends QuestEdit {
    @Override
    public CreatableDocument getCurDoc(Context context) {
        Model m = new ViewModelProvider(getActivity()).get(Model.class);;

        return m.currentDoc;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}

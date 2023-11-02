package com.grsoft.napoleon.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.grsoft.napoleon.BaseFragment;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.NapoleonApp;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.script_wizard.Scriptable;

public class ScriptWizard extends BaseFragment {
    final static String STEP = "step";
    final static String BUNDLE = "bundle";
    static final String TAG = ScriptWizard.class.toString();
    int curStep = -1;

    com.grsoft.napoleon.script_wizard.Model scriptModel;
    com.grsoft.napoleon.script_wizard.BaseFragment currentFragment;

    @Override
    protected int getLayoutID() {
        return R.layout.script_view;
    }

    @Override
    public int getOptionMenu() {
        return currentFragment != null ? currentFragment.getOptionMenu() : 0;
    }

    public ScriptWizard(){
        this(0);
    }

    public ScriptWizard(int step){
        Bundle arg = new Bundle();
        arg.putInt(STEP, step);
        setArguments(arg);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(currentFragment != null) {
            return currentFragment.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        curStep = -1;

        Log.d("ScriptWizard", "onCreate");

        if (savedInstanceState != null) {
            curStep = savedInstanceState.getInt(STEP);
            Log.d("ScriptWizard", "set step " + curStep);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STEP, scriptModel.getCurStep().getValue());
        Log.d("ScriptWizard", "onSaveInstanceState " + curStep);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        scriptModel = new ViewModelProvider(this).get(com.grsoft.napoleon.script_wizard.Model.class);

        int step = getArguments().getInt(STEP);

        if (curStep != -1)
            step = curStep;

        if(scriptModel.doc != model.getCurrentScript())
            scriptModel.init(model.getCurrentScript(), step);

        TextView ok = v.findViewById(R.id.btnOK);
        ok.setOnClickListener(view -> nextStep());

        v.findViewById(R.id.btnBack).setOnClickListener(view -> {
            if(validateCurrentDocument(true)) {
                if (scriptModel.isFirstDoc())
                    getParentFragmentManager().popBackStack();
                else
                    scriptModel.move(false);
            }
        });

        scriptModel.getCurStep().observe(getViewLifecycleOwner(), cs -> onNewStep(ok));
        return v;
    }

    public void nextStep() {
        if(validateCurrentDocument(false)) {
            if(scriptModel.isLastDoc()) {
                scriptModel.closeScript();
                ((NapoleonApp)getActivity().getApplication()).need_sync = true;
                getParentFragmentManager().popBackStack();
            } else {
                scriptModel.move(true);
            }
        }
    }

    void onNewStep(TextView ok) {
        Log.d("ScriptWizard", "onNewStep");
        ok.setText(scriptModel.isLastDoc() ? R.string.finish : R.string.next_script);
        int drw = scriptModel.isLastDoc() ? R.drawable.ic_ok : R.drawable.ic_fast_forward;
        ok.setCompoundDrawablesWithIntrinsicBounds(0, 0, drw, 0);
        ok.setVisibility(View.VISIBLE);
        openDoc();
    }

    private boolean validateCurrentDocument(boolean moveBack) {
        if(!currentFragment.validate(moveBack))
            return false;

        if(!scriptModel.saveCurrentDoc() && !moveBack) {
            Toast.makeText(getContext(), R.string.need_fill_document, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    void openDocFragment(com.grsoft.napoleon.script_wizard.BaseFragment cf) {
        currentFragment = cf;
        ((MainActivity)getActivity()).setOptionMenu(currentFragment.getOptionMenu());
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment, cf)
                .commit();
    }

    private void openDoc() {
        CreatableDocument<?> currentDoc = scriptModel.getCurDoc(getContext());
        Log.d("ScriptWizard", "openDioc: " + currentDoc);
        if(currentDoc instanceof Scriptable) {
            com.grsoft.napoleon.script_wizard.BaseFragment cf = ((Scriptable) currentDoc).getView();
            if(cf != null)
                openDocFragment(cf);
        }
    }
}

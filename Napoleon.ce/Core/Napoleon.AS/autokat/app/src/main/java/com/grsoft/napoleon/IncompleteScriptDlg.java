package com.grsoft.napoleon;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.views.RoundedDialog;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.ExtrasConst;

public class IncompleteScriptDlg extends RoundedDialog {
    public final static String KEY = "incompletescriptdlg_key";
    public final static String ACTION = "action";
    public final static int CLOSE_ACTION = 0;
    public final static int DELETE_ACTION = 1;
    public final static String SYN_AFTER_DEL = "syn_after_del";

    private long docRowID = ExtrasConst.INVALID_ROWID;

    public IncompleteScriptDlg(){
        long rowid = -1;

        Activity activity = getActivity();

        if (activity instanceof MainActivity){
            ScriptImpl script = ((MainActivity)activity).getIncompleteScript();
            if (script != null) rowid = script.getRowid();
        }

        Bundle args = new Bundle();
        args.putLong(ExtrasConst.DOC_ROW_ID_STR, rowid);
        setArguments(args);
    }

    public IncompleteScriptDlg(long rowid){
        Bundle args = new Bundle();
        args.putLong(ExtrasConst.DOC_ROW_ID_STR, rowid);
        setArguments(args);
    }

    @Override
    protected int getLayoutId(){
        return R.layout.incompletescript_dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        docRowID = getArguments().getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
        ScriptImplEx doc = new ScriptImplEx();
        doc.read(docRowID);
        doc.close();

        OrgImpl org = new OrgImpl();
        org.read("id", doc.getId());

        TextView tv = v.findViewById(R.id.org);
        tv.setText(Html.fromHtml(String.format("%s<br>%s", org.getData().name, org.getData().address)));

        v.findViewById(R.id.close).setOnClickListener(w->close(CLOSE_ACTION));
        v.findViewById(R.id.delete).setOnClickListener(w->close(DELETE_ACTION));

        return v;
    }

    private void close(int action) {
        Bundle res = new Bundle();
        res.putLong(ExtrasConst.DOC_ROW_ID_STR, docRowID);
        res.putInt(ACTION, action);
        res.putBoolean(SYN_AFTER_DEL, getSyncFlag());
        getParentFragmentManager().setFragmentResult(KEY, res);
        dismiss();
    }

    public boolean getSyncFlag() {
        return true;
    }
}

package com.grsoft.napoleon.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.grsoft.PdfViewerNotFoundDlg;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DocsToSign;
import com.grsoft.dataobjects.DocsToSignItem;
import com.grsoft.dataobjects.SignDocResponse;
import com.grsoft.napoleon.BaseFragment;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.SignEditor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewDocs extends BaseFragment {
    String signFile;
    DocsToSign doc;

    static final String SIGN_NAM = "sign.png";

    @Override protected int getLayoutID() {return R.layout.view_docs;}
    @Override public String TAG() {return "ViewDocs";}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.findViewById(R.id.reject).setOnClickListener(v1 -> {
            if(doc != null) {
                SignDocResponse sdr = SignDocResponse.makeResponse(doc, null);
                sendResponse(sdr);
            }
        });

        v.findViewById(R.id.sign).setOnClickListener(v1 -> {signing();});

        File dir = getContext().getExternalFilesDir(null);
        File signF = new File(dir, SIGN_NAM);
        signFile = signF.getAbsolutePath();
        getParentFragmentManager().setFragmentResultListener(SignEditor.KEY,
                getViewLifecycleOwner(), (requestKey, result) -> {
            String path = result.getString(SignEditor.FILE_NAME);
            if (path.length() > 0) {
                SignDocResponse sdr = SignDocResponse.makeResponse(doc, path);
                sendResponse(sdr);
            }
        });
        ListView lv = v.findViewById(R.id.docs);
        lv.setAdapter(new Adapter());
        return v;
    }

    void sendResponse(SignDocResponse resp) {
        model.signDocResponse = resp;
        ((MainActivity)getActivity()).showSignResponse();
    }

    private void signing() {
        if(doc != null)
            ((MainActivity)getActivity()).signEditor(signFile, false);
    }

    class Adapter extends BaseAdapter {

        List<DocsToSignItem> data = new ArrayList<>();
        public Adapter() {
            for(DocsToSign d : DbReader.fetch(DocsToSign.class, "", "created desc")) {
                doc = d;
                for(DocsToSignItem i : d.documents) {
                    data.add(i);
                }
                break;
            }
        }

        @Override public int getCount() {return data.size();}
        @Override public Object getItem(int position) {return data.get(position);}
        @Override public long getItemId(int position) {return position;}

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = View.inflate(getContext(), R.layout.view_doc_row, null);
            }
            DocsToSignItem item = (DocsToSignItem) getItem(position);
            TextView tv;
            tv = view.findViewById(R.id.name);
            tv.setText(item.name);

            view.setOnClickListener(v -> {viewDocument(item.file);});
            return view;
        }
    }

    private void viewDocument(String fileName) {
        File file = new File(fileName);

        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(getContext(), getString(R.string.fileprovider_authorities), file);
        } else
            uri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .setDataAndType(uri, "application/pdf")
                .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        try {
            getContext().startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();

            DialogFragment dlg = new PdfViewerNotFoundDlg();
            dlg.show(getParentFragmentManager(), "");
        }

    }
}

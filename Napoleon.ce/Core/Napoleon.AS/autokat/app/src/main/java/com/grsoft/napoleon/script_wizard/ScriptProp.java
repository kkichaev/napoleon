package com.grsoft.napoleon.script_wizard;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.grsoft.PdfViewerNotFoundDlg;
import com.grsoft.camera.CameraActivity;
import com.grsoft.camera.TakePhotoHandler;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.BNOper;
import com.grsoft.dataobjects.ClientType;
import com.grsoft.dataobjects.DayOrder;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PayType;
import com.grsoft.dataobjects.ScriptEx;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.ClientDocsImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PassportPhotosImpl;
import com.grsoft.dataobjects.impl.ScriptPropImpl;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.PassportPhotoErrorDlg;
import com.grsoft.napoleon.PrintData;
import com.grsoft.napoleon.PrintDataSource;
import com.grsoft.napoleon.PromptToGPSEx;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.SignEditor;
import com.grsoft.napoleon.VisitPhotoErrorDlg;
import com.grsoft.napoleon.main.AddressAdapter;
import com.grsoft.napoleon.modules.print.GraphicPrinter;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.util.DaData;
import com.grsoft.napoleon.util.HorizontalListView;
import com.grsoft.napoleon.util.ImagesItemsAdapter;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.NapoleonServiceW;
import com.grsoft.util.SrcDataCounter;
import com.grsoft.util.gps.GPSUtilNew;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public class ScriptProp extends BaseFragment {
    public static final String PASSPORT_TAG = "";
    public static final String VISIT_TAG = "v";
    public static final String SIGN_TAG = "s";
    private static final int RF_PASSPORT_PHOTO_COUNT = 2;
    private static final int FGN_PASSPORT_PHOTO_COUNT = 1;

    VisitImplEx refVisit;
    ActivityResultLauncher<String[]> takePhoto;
    ImagesItemsAdapter adapterPassport;
    ImagesItemsAdapter adapterVisit;
    VisitItem selectedItem;
    String photoTag = "";
    ClientDocsImpl clientDocs;
    View docsLayout;
    View passportPhotoLayout;
    View addPassportPhotoLayout;
    View passportPhotoDone;
    View visitCauseLayout;
    TextInputLayout passportSeria;
    TextInputLayout passportNumber;
    TextInputLayout birthday;
    TextInputLayout issue_date;
    TextInputLayout issue_code;
    View questLayout;
    PayType selectedPayType;
    View pcaLayout;
    View pkoLayout;
    TextView signDocs;
    View signDocsDone;
    TextView tvPassportError;
    boolean containsPurchase = false;
    View validateClicker = null;
    TextInputLayout payType;

    static final String TAG = ScriptProp.class.toString();

    static final Map<Integer, String> controls = new HashMap<>();
    private boolean passportOnServer = false;
    private PassportPhotosImpl passportPhotos = new PassportPhotosImpl();
    boolean not_check_sign = false;
    private String noncashTime = "";
    private boolean noncashReject = false;

    {
        controls.put(R.id.address, "address");
        controls.put(R.id.pay_type, "payType");
        controls.put(R.id.client, "fio");
        controls.put(R.id.client_type, "clientType");
        controls.put(R.id.phone, "phone");
        controls.put(R.id.passport_seria, "passportSeria");
        controls.put(R.id.passport_number, "passportNumber");
        controls.put(R.id.issue_org, "issueOrg");
        controls.put(R.id.issue_date, "passportIssue");
        controls.put(R.id.issue_code, "issueCode");
        controls.put(R.id.passport_type, "passportType");
        controls.put(R.id.birthday, "birthday");
    }

    boolean needValidatePassport = true;

    int[] psid = new int[]{
            R.id.issue_org,
            R.id.issue_date,
            R.id.passport_seria,
            R.id.passport_number,
            R.id.issue_code,
            R.id.passport_type,
            R.id.birthday,
    };

    ScriptPropImpl doc;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    TextInputLayout tilIssueOrg;
    View v;
    boolean needPassportCheck = false;

    @Override
    protected int getLayoutID() {
        return R.layout.script_prop;
    }

    @Override
    public String TAG() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        tilIssueOrg = v.findViewById(R.id.issue_org);
        docsLayout = v.findViewById(R.id.docsLayout);
        passportPhotoLayout = v.findViewById(R.id.passportPhotoLayout);
        passportPhotoDone = v.findViewById(R.id.passportPhotoDone);
        addPassportPhotoLayout = v.findViewById(R.id.addPassportPhotoLayout);
        visitCauseLayout = v.findViewById(R.id.visitCauseLayout);
        passportSeria = v.findViewById(R.id.passport_seria);
        passportNumber = v.findViewById(R.id.passport_number);
        questLayout = v.findViewById(R.id.questLayout);
        pcaLayout = v.findViewById(R.id.pcaLayout);
        pkoLayout = v.findViewById(R.id.pkoLayout);
        signDocs = v.findViewById(R.id.signDocs);
        signDocsDone = v.findViewById(R.id.signDocsDone);
        birthday = v.findViewById(R.id.birthday);
        issue_date = v.findViewById(R.id.issue_date);
        issue_code = v.findViewById(R.id.issue_code);
        tvPassportError = v.findViewById(R.id.tvPassportError);

        docsLayout.setVisibility(View.GONE);
        passportPhotoLayout.setVisibility(View.GONE);
        tvPassportError.setVisibility(View.GONE);

        passportSeria.getEditText().addTextChangedListener(new TextWatcher(){
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count)  { updateDocuments(); }
            @Override public void afterTextChanged(Editable s) { }
        });

        passportNumber.getEditText().addTextChangedListener(new TextWatcher(){
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count)  { updateDocuments(); }
            @Override public void afterTextChanged(Editable s) { }
        });

//        passportSeria.getEditText().setOnFocusChangeListener((view, focus)->{if (!focus)startCheckingPassport();});
//        passportNumber.getEditText().setOnFocusChangeListener((view, focus)->{if (!focus)startCheckingPassport();});

        OrgImpl oi = new OrgImpl();

        if (model.doc == null){
            getParentFragmentManager().popBackStack();
            return v;
        }

        oi.read("id", model.doc.getId());
        OrgEx oe = (OrgEx) oi.getData();
        final boolean PRIVATE_CLIENT = oi.getData().name.equals(getString(R.string.private_client));

        doc = (ScriptPropImpl) model.getCurDoc(getContext());
        if (doc.getData() != model.doc.getData()) {
            doc.initDoc(getContext(), GPSUtilNew.getLastKnownLocation(), model.doc, model.getCurScriptDef());
        }

        ScriptEx src = doc.getData();

        TextInputLayout til = v.findViewById(R.id.address);
        EditText ed = til.getEditText();
        ed.setText(src.address);
        if (oe.isPerson()) {
            ed.setEnabled(true);
        } else {
            til.setHelperTextEnabled(true);
            int ht = oe.locationValid() ? R.string.location_valid : R.string.location_invalid;
            til.setHelperText(getString(ht));

            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_enabled}
            };
            int[] colors = new int[]{
                    getContext().getColor(oe.locationValid() ? R.color.green : R.color.red)
            };
            til.setHelperTextColor(new ColorStateList(states, colors));
        }

        payType = v.findViewById(R.id.pay_type);
        containsPurchase = model.containsPurchase();

        if (containsPurchase) {
            List<PayType> types = DbReader.fetch(PayType.class, "", "pos");
            ArrayAdapter<PayType> aa = new ArrayAdapter<>(getContext(), R.layout.setting_list_item, types);
            AutoCompleteTextView av = (AutoCompleteTextView) payType.getEditText();
            av.setAdapter(aa);
            for (PayType pt : types) {
                if (pt.name.equals(src.payType)) {
                    selectedPayType = pt;

                    if(pt.isPSA2())
                        visitCauseLayout.setVisibility(View.GONE);

                    av.setText(pt.name, false);
                    updatePassport(!pt.isPSA2());
                    updateDocuments();
                    v.findViewById(R.id.birthday).setEnabled(!pt.isPSA2());

                }
            }

            av.setOnItemClickListener((parent, view, position, id) -> {
                PayType pt = types.get(position);
                setPaytypeSelected(av, pt);
            });
        } else {
            til.setEnabled(false);
            updatePassport(false);
        }

        List<ClientData> clients = !PRIVATE_CLIENT ?  collectClients() : new ArrayList<>();

        til = v.findViewById(R.id.client_type);
        List<ClientType> ctypes;
        if (oe.isPerson()) {
            ClientType ct = new ClientType();
            ct.name = oe.orgType;
            ctypes = new ArrayList<>();
            ctypes.add(ct);
        } else {
            ctypes = DbReader.fetch(ClientType.class, "", "pos");
        }

        ArrayAdapter<ClientType> caa = new ArrayAdapter<>(getContext(), R.layout.setting_list_item, ctypes);
        AutoCompleteTextView cav = (AutoCompleteTextView) til.getEditText();
        cav.setAdapter(caa);
        for (ClientType ct : ctypes) {
            if (ct.name.equals(src.clientType)) {
                cav.setText(ct.name, false);
            }
        }
        til.setEnabled(!oe.isPerson());

        til = v.findViewById(R.id.client);
        Adapter ac = new Adapter(getContext(), R.layout.setting_list_item, clients);
        AutoCompleteTextView cv = (AutoCompleteTextView) til.getEditText();
        cv.setText(src.fio, false);
        final EditText edFio = cv;
        cv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                edFio.removeTextChangedListener(this);
                String t = s.toString();
                t = t.replaceAll("Ё", "E").replaceAll("ё","e");
                s.replace(0, s.length(), t);
                edFio.addTextChangedListener(this);
                doc.clearPassportStatus();
            }
        });
        cv.setAdapter(ac);
        AutoCompleteTextView finalCv = cv;
        cv.setOnItemClickListener((parent, view, position, id) -> {
            doc.clearPassportStatus();
            ClientData cd = clients.get(position);
            finalCv.setText(cd.fio, false);
            setFieldsFrom(cd);

            passportPhotos = new PassportPhotosImpl();

            if(passportPhotos.hasPassport(cd.passportSeria, cd.passportNumber)) {
                addPassportPhotoLayout.setVisibility(View.GONE);
                passportPhotoDone.setVisibility(View.VISIBLE);
            }else{
                addPassportPhotoLayout.setVisibility(View.VISIBLE);
                passportPhotoDone.setVisibility(View.GONE);
            }
        });

        String phone = getEditablePhone(src.phone);

        til = v.findViewById(R.id.phone);
        til.getEditText().setText(phone);

        til = v.findViewById(R.id.issue_org);
        til.getEditText().setText(src.issueOrg);

        til = v.findViewById(R.id.passport_seria);
        til.getEditText().setText(src.passportSeria);

        til = v.findViewById(R.id.passport_number);
        til.getEditText().setText(src.passportNumber);

        til = v.findViewById(R.id.issue_code);
        til.getEditText().setText(src.issueCode);

        til = v.findViewById(R.id.passport_type);

        ArrayAdapter<String> apt = new ArrayAdapter<>(getContext(), R.layout.setting_list_item, getResources().getStringArray(R.array.passport_type));
        cv = (AutoCompleteTextView) til.getEditText();
        cv.setText(getResources().getStringArray(R.array.passport_type)[src.passportType], false);
        cv.setAdapter(apt);
        cv.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                needValidatePassport = true;
                setPassportRestrict();
                ((TextInputLayout) v.findViewById(R.id.passport_seria)).getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
                ((TextInputLayout) v.findViewById(R.id.passport_number)).getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
                src.passportType = ScriptEx.PASSPORT_RF;
                visitCauseLayout.setVisibility(View.GONE);
            } else {
                clearPassportRestrict();
                needValidatePassport = false;
                ((TextInputLayout) v.findViewById(R.id.passport_seria)).getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
                ((TextInputLayout) v.findViewById(R.id.passport_number)).getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
                src.passportType = ScriptEx.PASSPORT_FGN;
                visitCauseLayout.setVisibility(View.VISIBLE);
            }
        });

        setIssueDate(src.passportIssue);
        setBirthday(src.birthday);

        if (src.passportType == ScriptEx.PASSPORT_RF) {
            setPassportRestrict();
            visitCauseLayout.setVisibility(View.GONE);
        }

        AutoCompleteTextView address = ((AutoCompleteTextView) ((TextInputLayout) v.findViewById(R.id.address)).getEditText());

        if (PRIVATE_CLIENT) {
            address.setAdapter(new AddressAdapter(getContext()));
        } else {
            address.setEnabled(false);
            address.setTextColor(getResources().getColor(R.color.black));
        }

        v.findViewById(R.id.PCAReport).setOnClickListener((w) -> printPCA(w));
        v.findViewById(R.id.quest).setOnClickListener((w) -> printQuest(w));
        v.findViewById(R.id.pko).setOnClickListener((w) -> printPKO(w));
        signDocs.setOnClickListener((w) -> signDocs());
        signDocsDone.setOnClickListener((w) -> signDocs());

        refVisit = new VisitImplEx();
        refVisit.getData().created = src.visitDoc;

        if (!refVisit.read()) {
            refVisit.init(getContext(), src.id, new GpsCoord(src.latitude, src.longitude, src.stltime));
            doc.getData().visitDoc = refVisit.getData().created;
            doc.write();
            doc.close();
        }

        if (refVisit.hasSignature()){
            signDocs.setVisibility(View.GONE);
            signDocsDone.setVisibility(View.VISIBLE);
        }

        clientDocs = new ClientDocsImpl();
        clientDocs.getData().created = src.clientDoc;

        if (!clientDocs.read()) {
            clientDocs.init(getContext(), src.id, new GpsCoord(src.latitude, src.longitude, src.stltime));
            doc.getData().clientDoc = clientDocs.getData().created;
            doc.write();
            doc.close();
        }

        File path = new File(Path.getDataDir());
        path.mkdirs();

        v.findViewById(R.id.photoPassport).setOnClickListener(w -> takePicture(path, PASSPORT_TAG));
        v.findViewById(R.id.photoVisit).setOnClickListener(w -> takePicture(path, VISIT_TAG));

        adapterPassport = new ImagesItemsAdapter(getContext(), filterByTag(refVisit.getData().items, PASSPORT_TAG));
        adapterVisit = new ImagesItemsAdapter(getContext(), filterByTag(refVisit.getData().items, VISIT_TAG));

        HorizontalListView g = (HorizontalListView) v.findViewById(R.id.gwItems);
        g.setAdapter(adapterPassport);
        g.setOnItemLongClickListener(imagePopup);

        g = (HorizontalListView) v.findViewById(R.id.gwVisitItems);
        g.setAdapter(adapterVisit);
        g.setOnItemLongClickListener(imagePopup);

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        getParentFragment().getParentFragmentManager().setFragmentResultListener(SignEditor.KEY,
            getViewLifecycleOwner(), (requestKey, result) -> {
                GpsCoord coord = GPSUtilNew.getLastKnownLocation();
                doc.getData().latitude = coord.latitude;
                doc.getData().longitude = coord.longitude;

                doc.write();
                doc.close();

                refVisit.setSignature(result.getString(SignEditor.FILE_NAME));

                refVisit.getData().latitude = coord.latitude;
                refVisit.getData().longitude = coord.longitude;

                refVisit.write();
                refVisit.close();
        });

        if (refVisit.hasSignature()){
            setEnableControls((ViewGroup)v, false);
            address.setTextColor(getResources().getColor(R.color.grey));
            Fragment f = getParentFragment();
            f.getView().findViewById(R.id.btnBack).setEnabled(false);

            v.findViewById(R.id.PCAReport).setEnabled(true);
            v.findViewById(R.id.quest).setEnabled(true);
            v.findViewById(R.id.pko).setEnabled(true);
        }

        getParentFragmentManager().setFragmentResultListener(PassportInvalidDlg.RESULT_KEY,
                getViewLifecycleOwner(), (requestKey, result) -> {
                    if (requestKey.equals(PassportInvalidDlg.RESULT_KEY)) {
                        int sel = result.getInt(PassportInvalidDlg.RESULT_KEY);

                        if (sel == PassportInvalidDlg.PCA_SELECT) {
                            setPCAMode();
                        } else if (sel == PassportInvalidDlg.REPEAT_SELECT) {
                            needPassportCheck = true;
                            passportSeria.requestFocus();
                            v.scrollTo(0, passportSeria.getTop());
                        }
                    }
                });

        getParentFragmentManager().setFragmentResultListener(PassportInvalidDlg2.RESULT_KEY,
                getViewLifecycleOwner(), (requestKey, result) -> {
                    if (requestKey == PassportInvalidDlg2.RESULT_KEY){
                        int sel = result.getInt(PassportInvalidDlg2.RESULT_KEY);

                        if (sel == PassportInvalidDlg.REPEAT_SELECT){
                            needPassportCheck = true;
                            if (validateClicker != null)
                                validateClicker.performClick();
                        }else if (sel == PassportInvalidDlg2.OPERATOR){
                            doc.setPassportOperatorStatus();
                            needPassportCheck = false;

                            if (validateClicker != null)
                                validateClicker.performClick();
                        }else if (sel == PassportInvalidDlg.PCA_SELECT){
                            setPCAMode();
                        }
                    }
                });

        sdf.setLenient(false);

        initBeznalAllow();
        needPassportCheck = true;
        return v;
    }

    private void setPCAMode() {
        AutoCompleteTextView av = (AutoCompleteTextView) payType.getEditText();
        ListAdapter a = av.getAdapter();
        for (int i = 0; i < a.getCount(); i++) {
            PayType pt = (PayType) a.getItem(i);

            if (pt.isPSA2()) {
                av.setText(pt.name, false);
                setPaytypeSelected(av, pt);
            }
        }
    }

    private void setPaytypeSelected(AutoCompleteTextView av, PayType pt) {
        if (noncashReject && pt.isNonCash()) {
            av.setText("");
            Toast.makeText(this.getContext(), getString(R.string.noncash_time_exceed, noncashTime), Toast.LENGTH_SHORT).show();
            return;
        }

        selectedPayType = pt;

        if(pt.isPSA2()) {
            visitCauseLayout.setVisibility(View.GONE);
            passportSeria.getEditText().setText("");
            passportNumber.getEditText().setText("");
            issue_date.getEditText().setText("");
            issue_code.getEditText().setText("");
            tilIssueOrg.getEditText().setText("");
            birthday.getEditText().setText("");

            ScriptEx ex = doc.getData();
            ex.passportSeria = "";
            ex.passportNumber = "";
            ex.passportIssue = ScriptEx.NULL_DATE;
            ex.issueCode = "";
            ex.issueOrg = "";
            ex.birthday = ScriptEx.NULL_DATE;

            doc.write();
            doc.close();
        }

        updatePassport(!pt.isPSA2());
        updateDocuments();
    }

    @NonNull
    private String getEditablePhone(String phone) {
        if (phone.startsWith("+7"))
            phone = phone.substring(2);
        else if (phone.startsWith("8"))
            phone = phone.substring(1);
        return phone;
    }

    private void setEnableControls(ViewGroup parent, boolean enabled){
        for (int i = 0; i < parent.getChildCount(); i++){
            View v = parent.getChildAt(i);

            if (v instanceof  ViewGroup)
                setEnableControls((ViewGroup) v, enabled);
            v.setEnabled(enabled);
        }
    }

    private void updateDocuments() {
        if (selectedPayType == null)
            return;

        if ( selectedPayType.isPSA2())
            docsLayout.setVisibility(View.GONE);
        else{
            docsLayout.setVisibility(View.VISIBLE);

            if (!passportPhotos.hasPassport(passportSeria.getEditText().getText().toString().trim(),
                    passportNumber.getEditText().getText().toString())){
                questLayout.setVisibility(View.VISIBLE);
                passportOnServer = false;
                passportPhotoDone.setVisibility(View.GONE);
                addPassportPhotoLayout.setVisibility(View.VISIBLE);
            }else {
                questLayout.setVisibility(View.GONE);
                passportPhotoDone.setVisibility(View.VISIBLE);
                addPassportPhotoLayout.setVisibility(View.GONE);
                passportOnServer = true;
            }

            if (selectedPayType.isCash())
                pkoLayout.setVisibility(View.GONE);
            else
                pkoLayout.setVisibility(View.GONE);

            pcaLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initBeznalAllow() {
        noncashReject = true;
        List<?> list = DbReader.fetch(BNOper.class);
        if (list.size() > 0){
            Calendar calendar = Calendar.getInstance();
            int wd = calendar.get(Calendar.DAY_OF_WEEK);

            try{
                for (Field f : BNOper.class.getFields()){
                    DayOrder d = f.getAnnotation(DayOrder.class);

                    if (d != null && wd == d.order()){
                        noncashTime = f.get(list.get(0)).toString();
                    }
                }

                if (noncashTime.length() < 2)
                    return;

                String[] time = noncashTime.split(":");

                if (time.length > 1){
                    int min = Integer.parseInt(time[0]) * 60 + Integer.parseInt(time[1]);
                    int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);

                    if (now < min)
                        noncashReject = false;
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void reloadAdapters() {
        adapterPassport.setData(filterByTag(refVisit.getData().items, PASSPORT_TAG));
        adapterPassport.notifyDataSetChanged();
        adapterVisit.setData(filterByTag(refVisit.getData().items, VISIT_TAG));
        adapterVisit.notifyDataSetChanged();
    }

    AdapterView.OnItemLongClickListener imagePopup = new AdapterView.OnItemLongClickListener(){
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (refVisit.hasSignature())
                return  true;

            selectedItem = (VisitItem) arg0.getItemAtPosition(arg2);
            PopupMenu menu = new PopupMenu(getContext(), arg1);
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.itShow) {
                        String photo = new String(selectedItem.id);
                        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getString(R.string.fileprovider_authorities), new File(photo));
                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "image/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        startActivity(intent);
                    } else if (item.getItemId() == R.id.itDelete) {
                        refVisit.getData().items.remove(selectedItem);
                        refVisit.write();
                        doc.write();
                        doc.close();
                        reloadAdapters();
                    }
                    return false;

                }
            });
            menu.inflate(R.menu.photo_menu);
            menu.show();
            return true;
        }
    };

    private List<VisitItem> filterByTag(List<VisitItem> items, String tag) {
        List<VisitItem> res = new ArrayList<>();

        for (VisitItem i : items)
            if (((VisitItemEx)i).tag.equals(tag))
                res.add(i);

        return res;
    }

    private void takePicture(File path, String tag) {
        photoTag = tag;
        CameraActivity.openCamera(getContext(), new TakePhotoHandler() {
            @Override
            public File getPhotoFile() {
                File file = new File(path, String.format("%d.jpg", SrcDataCounter.getValue()));
                photoPath = file.getAbsolutePath();
                return file;
            }

            @Override
            public boolean photoSaved(File file, Uri savedUri) {
                refVisit.addPhoto(photoPath.getBytes(), photoTag);
                reloadAdapters();
                return true;
            }
        });
    }

    private void printPKO(View w) {
        printDoc("pko", "pko", w);
    }

    String photoPath = "";

    private void printQuest(View w) {
        printDoc("quest", "quest", w);
    }

    private void printDoc(String docType, String report, View invoker) {
        not_check_sign = true;
        validateClicker = invoker;
        if (validate(false)) {
            doc.write();
            doc.close();

            String docFile = clientDocs.getFile(docType);

            PrintData prntObj = new PrintData(getContext());
            prntObj.setPurchase(model.getPurchase());
            prntObj.setScript(doc.getData());

            File tmp = NPrinter.print(getContext(), report, new PrintDataSource(prntObj));
            File file = new File(tmp.getParent(), docFile);
            tmp.renameTo(file);

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

            clientDocs.addItem(docType, docFile);
            clientDocs.write();
            clientDocs.close();
        }

        not_check_sign = false;
    }

    private void signDocs() {
        not_check_sign = true;
        validateClicker = signDocs;

        if (validate(false)) {
            doc.write();
            doc.close();

            if (NapoleonServiceW.isTracking() && ! ((MainActivity) getActivity()).isGPSTurnOn()){
                PromptToGPSEx dlg = new PromptToGPSEx();
                dlg.show(getParentFragmentManager(), "");

            }else
                ((MainActivity) getActivity()).signEditor(refVisit.getSignPath(), false);
        }
        not_check_sign = false;
    }

    private void printPCA(View w) {
        printDoc("pca", "pca" + GraphicPrinter.FORM_DELIM_SIM + "pca2", w);
    }

    private void clearPassportRestrict() {
        TextInputLayout t = v.findViewById(R.id.passport_seria);
        t.getEditText().setFilters(new InputFilter[]{});
        t = v.findViewById(R.id.passport_number);
        t.getEditText().setFilters(new InputFilter[]{});
        t = v.findViewById(R.id.issue_code);
        t.getEditText().removeTextChangedListener(istw);
    }

    private IssueCodeTextWatcher istw = new IssueCodeTextWatcher();

    private class IssueCodeTextWatcher implements TextWatcher {
        boolean skipChanged = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            skipChanged = count > 0;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!skipChanged)
                return;

            if (s.length() == 3)
                s.append("-");

            final int MAX_LENGTH = 7;

            if (s.length() > MAX_LENGTH)
                s.replace(0, s.length(), s.subSequence(0, MAX_LENGTH));

            if (s.length() == MAX_LENGTH) {
                DaData.getIssueOrg(s.toString(), new DaData.Action() {
                    @Override
                    public void run(String val) {
                        if (val.length() > 0)
                            getActivity().runOnUiThread(() ->{
                                try {
                                    v.scrollTo(0, v.findViewById(R.id.issue_code).getTop());
                                    tilIssueOrg.getEditText().setText(val);
                                    tilIssueOrg.requestFocus();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            });
                    }
                });

            }
        }
    }

    private void setPassportRestrict() {
        TextInputLayout t = v.findViewById(R.id.passport_seria);
        t.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        t = v.findViewById(R.id.passport_number);
        t.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

        t = v.findViewById(R.id.issue_code);
        t.getEditText().addTextChangedListener(istw);
    }

    private void updatePassport(boolean enablePssport) {
        for (int id : psid) {
            v.findViewById(id).setEnabled(enablePssport);
        }

        docsLayout.setVisibility(enablePssport ? View.VISIBLE : View.GONE);
        needValidatePassport = enablePssport && doc.getData().passportType == 0;
        passportPhotoLayout.setVisibility(enablePssport ? View.VISIBLE : View.GONE);
    }

    private void setIssueDate(Date date) {
        EditText ed = issue_date.getEditText();
        String text = "";

        if (date.compareTo(ScriptEx.NULL_DATE) > 0) {
            text = sdf.format(date);
        }

        ed.setText(text);
    }

    private void setBirthday(Date date) {
        if (containsPurchase) {
            EditText ed = birthday.getEditText();
            String text = "";

            if (date.compareTo(ScriptEx.NULL_DATE) > 0) {
                text = sdf.format(date);
            }

            ed.setText(text);
        }else
            birthday.setEnabled(false);
    }

    private void setFieldsFrom(ClientData src) {
        TextInputLayout til = v.findViewById(R.id.phone);
        til.getEditText().setText(getEditablePhone(src.phone));

        til = v.findViewById(R.id.issue_org);
        til.getEditText().setText(src.issueOrg);

        til = v.findViewById(R.id.passport_seria);
        til.getEditText().setText(src.passportSeria);

        til = v.findViewById(R.id.passport_number);
        til.getEditText().setText(src.passportNumber);

        til = v.findViewById(R.id.issue_code);
        til.getEditText().setText(src.issueCode);

        til = v.findViewById(R.id.client_type);
        til.getEditText().setText(src.clientType);

        doc.getData().birthday = src.birthday;
        doc.getData().passportIssue = src.passportIssue;

        setIssueDate(src.passportIssue);
        setBirthday(src.birthday);

        til = v.findViewById(R.id.passport_type);
        ((AutoCompleteTextView) til.getEditText()).setText(getResources().getStringArray(R.array.passport_type)[src.passportType], false);

        doc.getData().passportType = src.passportType;

        if (doc.getData().passportType > 0){
            needValidatePassport = false;
            visitCauseLayout.setVisibility(View.VISIBLE);
        }else
            visitCauseLayout.setVisibility(View.GONE);
    }

    private void selectDate(EditText dil) {
        ScriptEx src = doc.getData();
        long selTime = src.issueValid() ? src.passportIssue.getTime() : (new Date().getTime());

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setEnd(Calendar.getInstance().getTimeInMillis())
                .build();


        MaterialDatePicker dp = MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraints)
                .setSelection(selTime)
                .setTitleText(R.string.select_issue_date)
                .build();

        dp.addOnPositiveButtonClickListener(selection -> {
            src.passportIssue = new Date((long) selection);
            dil.setText(sdf.format(src.passportIssue));
        });

        dp.show(getChildFragmentManager(), "");
    }

    private void selectBirthdayDate(EditText dil) {
        ScriptEx src = doc.getData();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -18);

        long selTime = src.birthdayValid() ? src.birthday.getTime() : cal.getTimeInMillis();

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setEnd(cal.getTimeInMillis())
                .build();

        MaterialDatePicker dp = MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraints)
                .setSelection(selTime)
                .setTitleText(R.string.date_birthday)
                .build();

        dp.addOnPositiveButtonClickListener(selection -> {
            src.birthday = new Date((long) selection);
            dil.setText(sdf.format(src.birthday));
        });

        dp.show(getChildFragmentManager(), "");
    }

    List<ClientData> collectClients() {
        Map<String, ClientData> ret = new HashMap<>();
        Map<String, ClientData> retPCA = new HashMap<>();
        Map<String, Long> users = new HashMap<>();
        Map<String, Long> pca2 = new HashMap<>();

        String filter = "id='" + doc.getId() + "'";
        for (ScriptEx sc : DbReader.fetch(ScriptEx.class, filter)) {
            ClientData cd = new ClientData(sc);
            if (!cd.empty()) {
                if (sc.payType.equals(PayType.PSA2)){
                    if (pca2.containsKey(cd.fio)) {
                        long created = pca2.get(cd.fio);
                        if (sc.created.getTime() > created) {
                            retPCA.put(cd.fio, cd);
                            pca2.put(cd.fio, sc.created.getTime());
                        }
                    } else {
                        retPCA.put(cd.fio, cd);
                        pca2.put(cd.fio, sc.created.getTime());
                    }
                }else {
                    if (users.containsKey(cd.fio)) {
                        long created = users.get(cd.fio);
                        if (sc.created.getTime() > created && sc.passportNumber.length() > 0) {
                            ret.put(cd.fio, cd);
                            users.put(cd.fio, sc.created.getTime());
                        }
                    } else {
                        ret.put(cd.fio, cd);
                        users.put(cd.fio, sc.created.getTime());
                    }
                }
            }
        }

        List<ClientData> res = new ArrayList<>(ret.values());
        res.addAll(retPCA.values());
        Collections.sort(res);
        res.add(0, new ClientData());

        return res;
    }

    @Override
    public boolean validate(boolean moveBack) {
        boolean noError = true;
        ScriptEx src = doc.getData();

        for (Map.Entry<Integer, String> kv : controls.entrySet()) {
            boolean allowEmpty = false;
            int kid = kv.getKey();
            if (kid == R.id.pay_type && !model.containsPurchase()) {
                continue;
            }
            if (!needValidatePassport) {
                for (int id : psid) {
                    if (id == kid) {
                        allowEmpty = true;
                        break;
                    }
                }
            }

            TextInputLayout til = v.findViewById(kv.getKey());
            if (til.isEnabled() == false)
                continue;

            String err = "";
            String value = "";

            if (kv.getValue().equals("phone"))
                value = ((MaskedEditText)til.getEditText()).getRawText();
            else
                value = til.getEditText().getText().toString();

            if (value.length() == 0 && !allowEmpty) {
                if (!moveBack) {
                    err = getContext().getString(R.string.need_fill_field);
                    noError = false;
                }
            } else {
                if (kv.getValue().equals("phone"))
                    value = ((MaskedEditText)til.getEditText()).getText().toString();

                Field f = src.getField(kv.getValue());
                if (f != null && f.getType() == String.class) {
                    try {
                        f.set(src, value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (f != null && f.getType() == Date.class)
                    setDateControl(src, til, kv.getValue());
            }
            til.setError(err);
        }

        if (noError && !moveBack) {
            if (model.containsPurchase() && (selectedPayType == null || !selectedPayType.isPSA2())) {
                if (!setDateControl(src, birthday, "birthday")) return false;

                if ((new Date().getYear() - src.birthday.getYear()) < 18){
                    birthday.setError(getString(R.string.teens_reject));
                    birthday.requestFocus();
                    return false;
                }
            }

            noError = checkPhoneNumber();

            if (noError && needValidatePassport) {
                if (!setDateControl(src, issue_date, "passportIssue")) return false;

                if (src.passportType == 0) {
                    noError = checkFieldCond(v.findViewById(R.id.passport_seria), 4) &&
                            checkFieldCond(v.findViewById(R.id.passport_number), 6);

                    if (noError) {
                        TextInputLayout til = v.findViewById(R.id.issue_code);
                        String text = til.getEditText().getText().toString().trim();

                        noError = Pattern.matches("\\d{3}-\\d{3}", text);

                        if (!noError) {
                            til.setError(getString(R.string.check_field));
                            til.requestFocus();

                            return false;
                        }
                    }

                    if (noError){
                        if (needPassportCheck && !refVisit.hasSignature()){
                            needPassportCheck = false;
                            passportChecking(src);

                            Toast.makeText(getContext(), R.string.passport_validating, Toast.LENGTH_SHORT).show();
                            noError = false;
                            return false;
                        }
                    }
                }
            }
        }

        if (!noError && !moveBack){
            DialogFragment dlg = new VisitPhotoErrorDlg();
            Bundle bundle = new Bundle();
            bundle.putString(VisitPhotoErrorDlg.TEXT, getString(R.string.field_need_value));
            dlg.setArguments(bundle);
            dlg.show(getParentFragmentManager(),"");
            noError = false;
            return false;
        }

        if (noError && !moveBack) {
            if (!not_check_sign && selectedPayType != null && !selectedPayType.isPSA2() && !refVisit.hasSignature()){
                DialogFragment dlg = new VisitPhotoErrorDlg();
                Bundle bundle = new Bundle();
                bundle.putString(VisitPhotoErrorDlg.TEXT, getString(R.string.docs_not_signed));
                dlg.setArguments(bundle);
                dlg.show(getParentFragmentManager(),"");
                noError = false;
                return false;
            }
        }

        if (!moveBack && !passportOnServer && selectedPayType != null && !selectedPayType.isPSA2()){
            int p = 0;
            int v = 0;

            for(VisitItem i : refVisit.getData().items){
                if (((VisitItemEx)i).tag.equals(PASSPORT_TAG))
                    p++;
                if (((VisitItemEx)i).tag.equals(VISIT_TAG))
                    v++;
            }

            if (src.passportType == ScriptEx.PASSPORT_RF && p < RF_PASSPORT_PHOTO_COUNT ||
                    src.passportType == ScriptEx.PASSPORT_FGN && p < FGN_PASSPORT_PHOTO_COUNT){
                DialogFragment dlg = new PassportPhotoErrorDlg();
                Bundle arg = new Bundle();
                arg.putInt(PassportPhotoErrorDlg.PHOTO_COUNT, src.passportType);
                dlg.setArguments(arg);
                dlg.show(getParentFragmentManager(),"");
                noError = false;
                return false;
            }

            if (v == 0 && src.passportType == ScriptEx.PASSPORT_FGN){
                DialogFragment dlg = new VisitPhotoErrorDlg();
                dlg.show(getParentFragmentManager(),"");
                noError = false;
                return false;
            }
        }

        return noError || moveBack;
    }

    private void passportChecking(ScriptEx src) {
        DaData.checkPassport(String.format("%s %s", src.passportSeria, src.passportNumber),
                new DaData.TAction() {
                    @Override
                    public void resolve(DaData.PassportResponce data) {
                        getActivity().runOnUiThread(()->{
                            tvPassportError.setVisibility(View.VISIBLE);

                            if (data == DaData.PassportResponce.PASSPORT_OK) {
                                doc.setPassportOKStatus();
                                tvPassportError.setText(R.string.passport_ok);
                                tvPassportError.setTextColor(getContext().getColor(R.color.green));
                                Toast.makeText(getContext(), R.string.passport_ok, Toast.LENGTH_SHORT).show();

                                if (validateClicker != null)
                                    validateClicker.performClick();

                            }else if (data == DaData.PassportResponce.PASSPORT_INVALID ||
                                data == DaData.PassportResponce.PASSPORT_INPUT_EMPTY ||
                                data == DaData.PassportResponce.PASSPORT_INVALID_FORMAT){
                                tvPassportError.setText(R.string.passport_invalid);
                                tvPassportError.setTextColor(getContext().getColor(R.color.red));

                                PassportInvalidDlg dlg = new PassportInvalidDlg();
                                dlg.show(getParentFragmentManager(), dlg.getClass().toString());
                            }else{
                                tvPassportError.setText(R.string.passport_service_is_off);
                                tvPassportError.setTextColor(getContext().getColor(R.color.grey));
                                needPassportCheck = true;

                                PassportInvalidDlg2 dlg = new PassportInvalidDlg2();
                                dlg.show(getParentFragmentManager(), dlg.getClass().toString());
                            }
                        });
                    }

                    @Override
                    public void reject(String data) {
                        getActivity().runOnUiThread(()-> {
                            tvPassportError.setVisibility(View.VISIBLE);
                            tvPassportError.setText(R.string.passport_service_is_off);
                            tvPassportError.setTextColor(getContext().getColor(R.color.grey));
                            needPassportCheck = true;

                            PassportInvalidDlg2 dlg = new PassportInvalidDlg2();
                            dlg.show(getParentFragmentManager(), dlg.getClass().toString());
                        });
                    }
                });
    }

    private boolean setDateControl(ScriptEx scr, TextInputLayout input, String name) {
        try {
            Field f = scr.getField(name);
            String text = input.getEditText().getText().toString().trim();
            Date date = sdf.parse(text);
            f.set(scr, date);
            if (!checkValidDate(date, input))
                return false;

            return true;
        }catch (Exception e){
            e.printStackTrace();
            input.setError(getString(R.string.invalid_date_format));
            input.requestFocus();

            return false;
        }
    }

    private boolean checkValidDate(Date date, TextInputLayout til) {
        final long MIN = -2208988800000l; // 01/01/1900

        if (date.getTime() < MIN) {
            til.setError(getString(R.string.data_is_min));
            til.requestFocus();

            return false;
        }

        if (date.getTime() > new Date().getTime()) {
            til.setError(getString(R.string.data_is_big));
            til.requestFocus();

            return false;
        }

        return true;
    }

    private boolean checkPhoneNumber() {
        TextInputLayout tv = v.findViewById(R.id.phone);
        MaskedEditText mer = (MaskedEditText) tv.getEditText();
        String text = mer.getRawText().toString();

        if (text.equals(" "))
            return true;

        if (text.length() != 10) {
            tv.setError(getString(R.string.check_field));
            tv.requestFocus();
            return false;
        }

        return true;
    }

    private boolean checkFieldCond(TextInputLayout til, int lim) {
        boolean res = true;
        String text = til.getEditText().getText().toString().trim();

        if (text.length() != lim) {
            til.setError(getString(R.string.check_field));
            til.requestFocus();
            res = false;
        }

        return res;
    }

    class Adapter extends ArrayAdapter<ClientData> {
        List<ClientData> src;
        List<ClientData> origValues;

        public Adapter(@NonNull Context context, int resource, List<ClientData> src) {
            super(context, resource);
            this.src = src;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
            if (view == null) {
                view = View.inflate(getContext(), R.layout.fio_row, null);
            }
            ClientData cd = getItem(position);
            ((TextView) view.findViewById(R.id.name)).setText(cd.fio);
            ((TextView) view.findViewById(R.id.passport)).setText(cd.passport());
            return view;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return filter;
        }

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (origValues == null) {
                    origValues = new ArrayList<>(src);
                }
                FilterResults res = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    res.count = origValues.size();
                    res.values = new ArrayList<>(origValues);
                } else {
                    List<ClientData> flist = new ArrayList<>();
                    String srch = constraint.toString().toLowerCase(Locale.ROOT);
                    for (ClientData cd : origValues) {
                        if (cd.toString().toLowerCase(Locale.ROOT).contains(srch)) {
                            flist.add(cd);
                        }
                    }
                    res.count = flist.size();
                    res.values = flist;
                }
                return res;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<ClientData> cd = (List<ClientData>) results.values;
                if (cd == null) {
                    src = new ArrayList<>();
                    notifyDataSetInvalidated();
                    return;
                }
                src = cd;
                if (cd.size() > 0)
                    notifyDataSetChanged();
                else
                    notifyDataSetInvalidated();
            }
        };

        @Override
        public int getCount() {
            return src.size();
        }

        @Nullable
        @Override
        public ClientData getItem(int position) {
            return src.get(position);
        }
    }

    static class ClientData implements Comparable<ClientData> {
        public Date passportIssue = ScriptEx.NULL_DATE;
        private String issueDate = "";
        public String fio = "";
        public String phone = "";
        public String passportSeria = "";
        public String passportNumber = "";
        public String issueOrg = "";
        public String issueCode = "";
        public String clientType = "";
        public int passportType = 0;
        public Date birthday = ScriptEx.NULL_DATE;

        public String passport() {
            if (fio.length() == 0) return "";
            String text = "";

            if (passportNumber.length() == 0) {
                text += "Без паспорта";
            } else {
                text += String.format("паспорт %s № %s", passportSeria, passportNumber);
            }

            return text;
        }

        @Override
        public String toString() {
            return fio;
        }

        public ClientData() {
        }

        public ClientData(ScriptEx src) {
            fio = src.fio.trim();
            phone = src.phone.trim();
            passportSeria = src.passportSeria.trim();
            passportNumber = src.passportNumber.trim();
            issueOrg = src.issueOrg;
            issueCode = src.issueCode;
            clientType = src.clientType;
            passportType = src.passportType;
            passportIssue = src.passportIssue;
            birthday = src.birthday;
        }

        boolean empty() {
            return fio.length() == 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClientData that = (ClientData) o;
            boolean b = Objects.equals(fio, that.fio) && Objects.equals(phone, that.phone) && Objects.equals(passportSeria, that.passportSeria) && Objects.equals(passportNumber, that.passportNumber);
            return b;
        }

        @Override
        public int hashCode() {
            return Objects.hash(fio, phone, passportSeria, passportNumber);
        }

        @Override
        public int compareTo(ClientData o) {
            int res = fio.compareTo(o.fio);

            if (res == 0)
                res= o.passportNumber.compareTo(passportNumber);

            return res;
        }
    }
}

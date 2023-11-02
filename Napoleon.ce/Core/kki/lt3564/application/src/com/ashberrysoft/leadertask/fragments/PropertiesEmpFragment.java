package com.ashberrysoft.leadertask.fragments;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ByMeTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ForMeTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.PropertiesEmpHeaderView;

public class PropertiesEmpFragment extends BaseFeaturesFragment {

    private static final String CLASS_PATH = PropertiesEmpFragment.class.getName();
    private static final String ETRA_EMP = CLASS_PATH + "ETRA_EMP";
    private static final String ETRA_EMP_NEW = CLASS_PATH + "ETRA_EMP_NEW";
    private static final String EXTRA_ALL_COMM = "EXTRA_ALL_COMM";
    private static final String ACTION_MEDIA_RESULT_EMP = "ACTION_MEDIA_RESULT_EMP";
    private static final String EXTRA_EMP_FILE = "EXTRA_EMP_FILE";

    // VIEW
    private PropertiesEmpHeaderView mHeaderView;

    // VALUE's
    private Emp mEmp;
    private boolean mEmpNew;
    private boolean mShowKeyBoard;
    private static boolean mAddNewEmpFromEmail;
    private int mPosition;
    private File mTempFile;
    private ProgressDialog mProgress;

    public static PropertiesEmpFragment newInstance(Emp emp, boolean addNewEmpFromEmail) {
        final Bundle b = new Bundle(1);
        if (emp != null) {
            b.putSerializable(ETRA_EMP, emp);
        }
        mAddNewEmpFromEmail = addNewEmpFromEmail;
        final PropertiesEmpFragment f = new PropertiesEmpFragment();
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        final Bundle bundle = b == null ? getArguments() : b;
        if (bundle.containsKey(ETRA_EMP)) {
            mEmp = (Emp) bundle.getSerializable(ETRA_EMP);
            mEmpNew = bundle.getBoolean(ETRA_EMP_NEW);
            mShowKeyBoard = false;

        } else {
            mEmp = new Emp();
            mEmpNew = true;
            mShowKeyBoard = true;
        }

        mHeaderView = new PropertiesEmpHeaderView(getActivity(), mEmp, mEmpNew, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        mListView = (ListView) inflater.inflate(R.layout.edit_features_fragment, container, false);
        if (getListViewHeader() != null) {
            mListView.addHeaderView(getListViewHeader());
        }

        mListView.setBackgroundColor(getResources().getColor(R.color.login_background));
        mListView.setAdapter(getAdapter());
        registerForContextMenu(mListView);

        return mListView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mShowKeyBoard) {
            showKeyboard(mHeaderView.getEditText());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            if (mTempFile != null) {
                mTempFile.delete();
                clearCacheFoto(mEmp.getLogin());
                mTempFile = null;
            }
            return;
        }

        if (requestCode == 1) {
            if (data != null) {
                setBlocking(true);
                // скопировать с темп именем
                boolean error = false;
                final Intent intent = new Intent(ACTION_MEDIA_RESULT_EMP);
                final String path = Utils.getRealPathFromURI(mApp, data.getData());

                try {
                    if (path == null) {
                        return;
                    }

                    final File file = Utils.FileWorker.copyEmpFotoFile(path, mApp.getAppFolder());

                    intent.putExtra(EXTRA_EMP_FILE, file);

                } catch (Exception e) {
                    error = true;
                } finally {
                    LocalBroadcastManager.getInstance(mApp).sendBroadcast(intent);
                    if (error) {
                        mHeaderView.resetFoto(mEmp.getLogin());
                    } else {
                        mHeaderView.resetFoto(Utils.TMP_FOTO_FILE_NAME); // и брать ресур фотки с него
                    }
                    setBlocking(false);
                }
            }
        }
    }

    private void setBlocking(boolean value) {
        if (value) {
            if (mProgress == null) {
                mProgress = new ProgressDialog(getActivity());
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.setMessage(getString(R.string.blocking_process));
            }
            mProgress.show();

        } else {
            mHandler.post(mSetBlockFalse);
        }
    }

    private final Runnable mSetBlockFalse = new Runnable() {
        @Override
        public void run() {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        mHeaderView.getData(mEmp, mEmpNew);
        b.putSerializable(ETRA_EMP, mEmp);
        b.putBoolean(ETRA_EMP_NEW, mEmpNew);
    }

    @Override
    protected View getListViewHeader() {
        return mHeaderView;
    }

    @Override
    protected int getActionBarTitle() {
        return mEmpNew ? R.string.emp_new : R.string.emp_properties;
    }

    @Override
    protected int getActionBarIcon() {
        return R.drawable.employee_white;
    }

    @Override
    protected boolean getVisibilitySwitchMode() {
        return false;
    }

    @Override
    protected boolean onAddFeatureClick() {
        return false;
    }

    @Override
    protected boolean onSaveFeatureClick() {
        mHeaderView.getData(mEmp, mEmpNew);
        inputHide(mHeaderView);
        if (TextUtils.isEmpty(mEmp.getTitle()) || TextUtils.isEmpty(mEmp.getLogin())) {
            Utils.showToast(getActivity(), R.string.t_error_not_all_information);
            return true;
        }

        new Thread(getSaveRunnable()).start();
        return true;
    }

    private Runnable getSaveRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (mEmpNew || mAddNewEmpFromEmail) {
                    mEmp.setUid(UUID.randomUUID());
                    mEmp.setOrder(getMaxPosition() + 1);
                    mEmp.setUsnFieldFoto(0);
                    mApp.getContentResolver().insert(EmpContract.CONTENT_URI, mEmp.getContentValues(null));
//                  Emp.setEmpSortToTaskCustomer(mApp);

                } else {
                    final ContentValues cv = new ContentValues();
                    cv.put(EmpContract.USN_ENTITY, 0);
                    cv.put(EmpContract.TITLE, mEmp.getTitle());
                    cv.put(EmpContract.USN_FIELD_TITLE, mEmp.getUsnFieldTitle() + 1);
                    cv.put(EmpContract.COMMENT, mEmp.getComment());
                    cv.put(EmpContract.USN_FIELD_COMMENT, mEmp.getUsnFieldComment() + 1);

                    if (mEmp.getLogin().equals(LTSettings.getInstance().getUserName())) {
                        cv.put(EmpContract.LAST_NAME, mEmp.getLastName());
                        cv.put(EmpContract.USN_FIELD_LASTNAME, mEmp.getUsnFieldLastName() + 1);
                        cv.put(EmpContract.FIRST_NAME, mEmp.getFirstName());
                        cv.put(EmpContract.USN_FIELD_FIRSTNAME, mEmp.getUsnFieldFirstName() + 1);
                        cv.put(EmpContract.MIDDLE_NAME, mEmp.getMiddleName());
                        cv.put(EmpContract.USN_FIELD_MIDDLENAME, mEmp.getUsnFieldMiddleName() + 1);
                        cv.put(EmpContract.GENDER, mEmp.getGender());
                        cv.put(EmpContract.USN_FIELD_GENDER, mEmp.getUsnFieldGender() + 1);
                        cv.put(EmpContract.DETAILS, mEmp.getDetails());
                        cv.put(EmpContract.USN_FIELD_DETAILS, mEmp.getUsnFieldDetails() + 1);
                        cv.put(EmpContract.BIRTHDAY, mEmp.getBirthday() == null ? "" : mEmp.getBirthday().toString());
                        cv.put(EmpContract.USN_FIELD_BIRTHDAY, mEmp.getUsnFieldBirthday() + 1);
                        cv.put(EmpContract.COUNTRY, mEmp.getCountry());
                        cv.put(EmpContract.USN_FIELD_COUNTRY, mEmp.getUsnFieldCountry() + 1);
                        cv.put(EmpContract.PROVINCE, mEmp.getProvince());
                        cv.put(EmpContract.USN_FIELD_PROVINCE, mEmp.getUsnFieldProvince() + 1);
                        cv.put(EmpContract.STREET, mEmp.getStreet());
                        cv.put(EmpContract.USN_FIELD_STREET, mEmp.getUsnFieldStreet() + 1);
                        cv.put(EmpContract.CITY, mEmp.getCity());
                        cv.put(EmpContract.USN_FIELD_CITY, mEmp.getUsnFieldCity() + 1);
                        cv.put(EmpContract.POSTAL_CODE, mEmp.getPostalCode());
                        cv.put(EmpContract.USN_FIELD_POSTALCODE, mEmp.getUsnFieldPostalCode() + 1);

                        //save new foto
                        final File src = new File(mApp.getAppFolder(), mEmp.getLogin());
                        final File dst = new File(mApp.getAppFolder(), Utils.TMP_FOTO_FILE_NAME);

                        if (dst != null && dst.exists()) {
                            try {
                                Utils.FileWorker.copyFile(dst, src);
                                cv.put(EmpContract.USN_FIELD_FOTO, mEmp.getUsnFieldFoto() + 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                clearCacheFoto(mEmp.getLogin());
                                clearCacheFoto(dst.getName());
                                dst.delete();
                            }
                        }
                        //
                        MenuLoader.getInstance(getActivity()).resetMyFoto();

                        //
                    }

                    mApp.getContentResolver()//
                            .update(EmpContract.CONTENT_URI, cv, EmpContract.selectionUid(mEmp.getUid()), null);
                }

                EmployeeCache.getInstance(mApp).refreshCache();
                mApp.getContentResolver().notifyChange(ByMeTotalLinkContract.CONTENT_URI, null);
                mApp.getContentResolver().notifyChange(ForMeTotalLinkContract.CONTENT_URI, null);
                mApp.getContentResolver().notifyChange(LTaskContract.CONTENT_URI, null);
                Utils.hideInput(mApp, mHeaderView);

            }
        };
    }

    public void deletePhoto() {
        boolean error = false;
        try {
            final File dst = new File(mApp.getAppFolder(), Utils.TMP_FOTO_FILE_NAME);
            if (dst.exists()) {
                dst.delete();
            }
            dst.createNewFile();
            clearCacheFoto(mEmp.getLogin());
            clearCacheFoto(dst.getName());
        } catch (IOException e) {
            error = true;
            e.printStackTrace();
        } finally {
            if (error) {
                mHeaderView.resetFoto(mEmp.getLogin());
            } else {
                mHeaderView.resetFoto(Utils.TMP_FOTO_FILE_NAME);
            }
        }

    }

    private void clearCacheFoto(String fileName) {
        try {
            File cacheImgFile = new File(mApp.getAppFolder() + "/cache_" + fileName);
            if (cacheImgFile.exists()) {
                cacheImgFile.delete();
            }
        } catch (Exception e) {

        }
    }

    private int getMaxPosition() {
        Cursor c = null;
        try {
            c = mApp.getContentResolver().query(EmpContract.CONTENT_URI, null, null, null, null);
            return c.getCount();
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    @Override
    protected boolean onOtherFeatureClick(MenuItem item) {
        return false;
    }

    @Override
    protected void onDialogPositiveButton() {

    }

    @Override
    protected ListAdapter getAdapter() {
        return null;
    }

    @Override
    protected boolean runOperationInBackground(Operation operation) {
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        final File dst = new File(mApp.getAppFolder(), Utils.TMP_FOTO_FILE_NAME);
        if (dst.exists()) {
            dst.delete();
        }
        clearCacheFoto(mEmp.getLogin());
        clearCacheFoto(dst.getName());
    }

    private List<String> getCommunications(String base64String) {
        List <String> allComm = new ArrayList<String>();
        int lastIndexFounded = 0;
        byte[] data = Base64.decode(base64String, Base64.DEFAULT);
        String utf8Text ="";
        try {
            utf8Text = new String(data, "UTF-8");
            for(int i=0; i<utf8Text.length(); i++)
            {
                int indexMain = utf8Text.indexOf("\n", i);
                if (indexMain != -1) {
                    String subString = utf8Text.substring(lastIndexFounded == 0 ? lastIndexFounded : lastIndexFounded+1, indexMain);
                    allComm.add(subString);
                    lastIndexFounded = indexMain;
                    i = indexMain;
                }
                else {
                    String subString = utf8Text.substring(i, utf8Text.length());
                    allComm.add(subString);
                    break;
                }

            }
        } catch (UnsupportedEncodingException e) {
        }
        return allComm;
    }

}
package com.ashberrysoft.leadertask.modern.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.dialogs.SetColorDialogBuilder;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.fragment.TasksFragment;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.utils.Utils;

import yuku.ambilwarna.AmbilWarnaDialog;


public class ChangeCategoryDialog extends BaseDialog implements AmbilWarnaDialog.OnAmbilWarnaListener {

    public static final int CODE = R.id.dialog_change_category;
    private static final String CLASS_PATH = ChangeCategoryDialog.class.getSimpleName();
    private static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";

    // ADAPTER
    private EditText editText1;
    private View mBg;
    private static Context mContext;
    private Category mCategory;


    public static ChangeCategoryDialog newInstance(Fragment target, Category category) {
        final Bundle b = new Bundle(1);
        if (category != null) {
            b.putSerializable(EXTRA_CATEGORY, category);
        }
        final ChangeCategoryDialog d = new ChangeCategoryDialog();
        mContext = target.getActivity().getApplicationContext();
        d.setTargetFragment(target, CODE);
        d.setArguments(b);
        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        final Bundle bundle = b != null ? b : getArguments();
        mCategory = (Category) bundle.getSerializable(EXTRA_CATEGORY);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.add_marker_category, null);
        editText1 = (EditText) v.findViewById((R.id.editText1));
        editText1.setText(mCategory.getName());
        editText1.setSelection(editText1.length());
        mBg = (View) v.findViewById(R.id.backColor);
        mBg.setBackgroundColor(Color.parseColor( mCategory.getColor()));
        mBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectColor();
            }
        });

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(editText1.getText().toString().trim())) {
                    new Thread(mSaveCategoryRunnable).start();
                } else {
                    Utils.showToast(getActivity(), R.string.t_error_feature_name);
                }
                /*Category category = new Category();
                setCategoryParams(category);
                category.setName(editText1.getText().toString());
                saveCategory(category);
                if (getTargetFragment() instanceof BaseSyncStatusFragment) {
                    ((BaseSyncStatusFragment) getTargetFragment()).onFragmentResult(category, CODE);
                }*/
                getDialog().dismiss();
            }
        });
        ad.setNegativeButton(R.string.btn_cancel, null);

        Dialog d = ad.create();
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        d.show();
        return d;
    }

    private Runnable mSaveCategoryRunnable = new Runnable() {
        @Override
        public void run() {
            mCategory.setName(editText1.getText().toString().trim());
            mCategory.setUsn(0);
            mCategory.setUsnName(mCategory.getUsnName() + 1);
            mCategory.setUsnComment(mCategory.getUsnComment() + 1);
            mCategory.setUsnColor(mCategory.getUsnColor() + 1);

            try {
                DbHelper.getInstance(getContext()).getCategoryDao().update(mCategory);

            } catch (Exception e) {
                Utils.toLog(e);
            }
            final UpdateFeatureLinkHelper linkHelper = new UpdateFeatureLinkHelper(getApp());
            linkHelper.updateTotalLink(mCategory);
            getApp().getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);
            try {
                ((TasksFragment) getTargetFragment()).getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((SlidingActivity) ((TasksFragment) getTargetFragment()).getActivity()).setActionBarTitle(mCategory.getName(), false, null);

                    }
                });
            } catch (Exception e) {

            }
        }
    };

    private void selectColor() {
        final int color;
        if (mCategory.getColor() == null || Marker.NO_COLOR.equals(mCategory.getColor())) {
            color = Color.BLACK;

        } else {
            color = Color.parseColor(mCategory.getColor());
        }

        new SetColorDialogBuilder(getContext(), color, ChangeCategoryDialog.this).show();
    }

    @Override
    public void onCancel(AmbilWarnaDialog dialog) {}

    @Override
    public void onOk(AmbilWarnaDialog dialog, int color) {
        final String hexColor = SetColorDialogBuilder.NO_COLOR == color ? Marker.NO_COLOR : Utils.getColor(color);

        mCategory.setColor(hexColor);
        mBg.setBackgroundColor(Color.parseColor( mCategory.getColor()));
    }
}
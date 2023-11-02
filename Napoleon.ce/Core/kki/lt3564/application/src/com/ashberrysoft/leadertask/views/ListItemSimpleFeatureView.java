package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.FeaturesActivity.FeatureType;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ListItemSimpleFeatureView<DATA> extends LinearLayout//
        implements View.OnClickListener, View.OnLongClickListener, IDataView<DATA> {

    public interface OnSimpleFeatureViewListener<DATA> {
        public void onSimpleFeatureViewClick(DATA data);

        public void onSimpleFeatureViewLongClick(View v, DATA data, int position, DATA dataPrev, DATA dataPost);
    }

    // VIEW's
    private TextView mName;
    private TextView mSubName;
    private ImageView mImage;
    private ImageView mCircleImage;
    private View mLayout;

    // VALUE's
    private FeatureType mFeatureType;
    private LTSettings mSettings;
    private int mUnivPadding;
    private DATA mData;
    private DATA mDataPrev;
    private DATA mDataPost;
    private int mPosition;
    private LTApplication mApp;

    // LISTENER
    private OnSimpleFeatureViewListener<DATA> mListener;

    public ListItemSimpleFeatureView(Context context) {
        super(context);
        initialization(context);
    }

    public ListItemSimpleFeatureView(Context context, final FeatureType type, OnSimpleFeatureViewListener<DATA> listener) {
        super(context);

        setFeatureType(type);
        initialization(context);
        setCustomListener(listener);
    }

    private void initialization(Context context) {
        mApp = (LTApplication) context.getApplicationContext();
        inflate(getContext(), R.layout.list_item_view_feature, this);
        this.setOrientation(VERTICAL);
        this.setOnClickListener(this);
        this.setOnLongClickListener(this);

        mSettings = LTSettings.getInstance(getContext());
        mUnivPadding = getResources().getDimensionPixelSize(R.dimen.univ_padding_big_feature);

        mLayout = findViewById(R.id.linear_layout);
        mName = (TextView) findViewById(R.id.tv_feature_name);
        mSubName = (TextView) findViewById(R.id.tv_feature_sub_name);
        mImage = ((ImageView) findViewById(R.id.iv_feature_img));
        mCircleImage = ((ImageView) findViewById(R.id.iv_feature_img_custom));

        mName.setTextColor(mSettings.isThemeDark() ? Color.WHITE : Color.BLACK);
        mSubName.setTextColor(mSettings.isThemeDark() ? Color.WHITE : Color.BLACK);
        final int featureImgId;
        switch (mFeatureType) {
        case CATEGORY:
            featureImgId = R.drawable.category;
            break;

        case PROJECT:
            featureImgId = R.drawable.project;
            break;

        case MARKER:
            featureImgId = R.drawable.marker_black;
            break;

        case EMP:
            featureImgId = R.drawable.emp_simple;
            break;

        case CONTACT_GROUPS:
            featureImgId = R.drawable.groups;
            break;

        case CONTACTS:
            featureImgId = R.drawable.employee;
            break;

        default:
            return;
        }

        mImage.setImageResource(featureImgId);
    }

    public void setData(DATA data, DATA dataPrev, DATA dataPost) {
        mData = data;
        mDataPrev = dataPrev;
        mDataPost = dataPost;

        switch (mFeatureType) {
        case CATEGORY:
            setCategoryData((Category) mData);
            break;

        case PROJECT:
            setProjectData((Project) mData);
            break;

        case CONTACT_GROUPS:
            setContactGroupsData((ContactsGroup) mData);
            break;

        case CONTACTS:
            setContactData((Contact) mData);
            break;

        case MARKER:
            setMarkerData((Marker) mData);
            break;

        case EMP:
            setEmpData((Emp) mData);
            break;

        default:
            break;
        }
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    private void setCategoryData(Category category) {
        if(category.getColor() != null && !category.getColor().equals(Category.NO_COLOR) ) {
            mImage.setImageBitmap(Utils.getCategoryDrawable(mApp, category.getColor()));
        }
        else {
            //mImage.setImageResource(R.drawable.category);
            mImage.setImageBitmap(Utils.getCategoryDrawable(mApp, null));
        }
        mName.setText(category.getName());
        setPadding(category.getIndent());
    }

    private void setProjectData(Project project) {
        mName.setText(project.getName());
        setPadding(project.getIndent());
        if (project.getSharedUsers() != null) {
            mImage.setImageResource(R.drawable.project_shared);
        }
        else {
            mImage.setImageResource(R.drawable.project);
        }
    }

    private void setContactGroupsData(ContactsGroup contactsGroup) {
        mName.setText(contactsGroup.getName());
        setPadding(contactsGroup.getIndent());
        if (contactsGroup.getCreator().equals(LTSettings.getInstance().getUserName())) {
            if (contactsGroup.getSharedUsers() != null) {
                mImage.setImageResource(R.drawable.cg_shared);
            }
            else {
                mImage.setImageResource(R.drawable.cg_my);
            }
        }
        else {
            mImage.setImageResource(R.drawable.cg_avalibale);
        }
    }

    private void setContactData(Contact contact) {
        LTApplication mApp = (LTApplication) getContext().getApplicationContext();
        mName.setText(contact.getTitle());
        setPadding(contact.getIndent());

        RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder(mApp, contact.getId().toString());
        if(roundedBitmapDrawable != null) {
            mImage.setImageDrawable(roundedBitmapDrawable);
        }
        else {

            if (contact.getEmailCreator().equals(LTSettings.getInstance().getUserName())) {
                switch (contact.getGender()) {
                    case 1:
                        mImage.setImageResource(R.drawable.c_men);
                        break;
                    case 2:
                        mImage.setImageResource(R.drawable.c_women);
                        break;
                    case 3:
                        mImage.setImageResource(R.drawable.c_org);
                        break;
                    default:
                        mImage.setImageResource(R.drawable.c_nobody);
                        return;
                }
            }
            else {
                switch (contact.getGender()) {
                    case 1:
                        mImage.setImageResource(R.drawable.c_men_avaleble);
                        break;
                    case 2:
                        mImage.setImageResource(R.drawable.c_women_avaleble);
                        break;
                    case 3:
                        mImage.setImageResource(R.drawable.c_org_avaleble);
                        break;
                    default:
                        mImage.setImageResource(R.drawable.c_nobody_avaleble);
                        return;
                }
            }
        }
    }

    private void setMarkerData(Marker marker) {
        try {
            mName.setText(marker.getName());
            setPadding(0);
            mName.setTextColor((marker.getTextColor() == null || Marker.NO_COLOR.equals(marker.getTextColor())) == true ? Color.BLACK : Color.parseColor(marker.getTextColor()));
        } catch (Exception e) {}

        try {
            this.setBackgroundColor((marker.getBackColor() == null || Marker.NO_COLOR.equals(marker.getBackColor())) == true ? Color.WHITE : Color.parseColor(marker.getBackColor()));
        } catch (Exception e) {}
    }

    private void setEmpData(Emp emp) {
        String name;
        LTApplication mApp = (LTApplication) getContext().getApplicationContext();
        try {
            name = emp.getTitle();
            if(name.equals(null)) {
                name = emp.getLogin();
            }
        } catch (Exception e) {
            name = emp.getLogin();
        }

        if(emp.getLogin().equals(mSettings.getUserName())){
            name = EmployeeCache.getInstance(getContext()).find(mSettings.getUserName()).toString();
            mName.setText(name);
            mSubName.setText(emp.getLogin());
            mSubName.setVisibility(VISIBLE);
        } else {
            mName.setText(name);
            mSubName.setText(emp.getLogin());
            mSubName.setVisibility(VISIBLE);
        }

        RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder(mApp, emp.getLogin());
        if(roundedBitmapDrawable != null) {
            mImage.setImageDrawable(roundedBitmapDrawable);
            mImage.setPadding(5, 5, 5, 5);
            mCircleImage.setPadding(2, 2, 2, 2);
            mCircleImage.setVisibility(VISIBLE);
            mCircleImage.setImageResource(R.drawable.emp_circle_simple);
        }
        else {
            mImage.setPadding(1, 1, 1, 1);
            mImage.setImageResource(R.drawable.emp_simple);
            mCircleImage.setVisibility(GONE);
        }
        setPadding(0);
    }

    private void setPadding(int indent) {
        mLayout.setPadding(mUnivPadding * indent, 0, 0, 0);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onSimpleFeatureViewClick(mData);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mListener != null) {
            mListener.onSimpleFeatureViewLongClick(v, mData, mPosition, mDataPrev, mDataPost);
            return true;
        }
        return false;
    }

    @Override
    public void setData(DATA data) {

    }

    @Override
    public DATA getData() {
        return mData;
    }

    public void setCustomListener(OnSimpleFeatureViewListener<DATA> listener) {
        mListener = listener;
    }

    public void setFeatureType(FeatureType featureType) {
        mFeatureType = featureType;
    }
}
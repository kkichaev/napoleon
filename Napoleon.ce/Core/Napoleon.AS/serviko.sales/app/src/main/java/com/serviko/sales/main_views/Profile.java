package com.serviko.sales.main_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.serviko.sales.MainActivity;
import com.serviko.sales.R;
import com.serviko.sales.SelectPartner;

public class Profile extends BaseView{

        public static String TAG = Profile.class.toString();

        @Override
        int getResourceId() {
            return R.layout.profile_view;
        }

        @Override
        public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        v.findViewById(R.id.myOrders).setOnClickListener((view) -> openOrders());
        v.findViewById(R.id.myShops).setOnClickListener((view) -> openPartners());
        v.findViewById(R.id.feedback).setOnClickListener((view) -> openFeedback());
        v.findViewById(R.id.exit).setOnClickListener((view) -> logout());
        return v;
    }

    void logout() {
        ((MainActivity)getActivity()).logout();
    }

    void openOrders() {
        ((MainActivity)getActivity()).loadFragment(new Orders(), true);
    }

    void openPartners() {
        SelectPartner.open(getActivity(), true);
    }

    void openFeedback() { ((MainActivity)getActivity()).loadFragment(new Feedback(), true); }
}

package com.novotek.sales.main_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.novotek.sales.BuildConfig;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;
import com.novotek.sales.SelectPartner;

public class Profile extends BaseView{

        public static String TAG = Profile.class.toString();

        @Override
        protected int getResourceId() {
            return R.layout.profile_view;
        }

        @Override
        public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        model.getPartner().observe(getViewLifecycleOwner(), partner -> {
            TextView tv = v.findViewById(R.id.name);
            tv.setText(partner.name);

            tv = v.findViewById(R.id.phone);
            tv.setText(com.novotek.sales.login_views.Model.phoneNumber(getContext()));
        });

        v.findViewById(R.id.title).setOnClickListener(view ->{
            SelectPartner.open(getActivity(), true);
        });

        v.findViewById(R.id.favorites).setOnClickListener((view) -> ((MainActivity)getActivity()).openFavorites());
        v.findViewById(R.id.company).setOnClickListener((view) -> ((MainActivity)getActivity()).openCompanyCard());
        v.findViewById(R.id.feedback).setOnClickListener((view) -> ((MainActivity)getActivity()).openFeedback());

        v.findViewById(R.id.exit).setOnClickListener((view) -> logout());

        ((TextView)v.findViewById(R.id.version)).setText(getString(R.string.version, BuildConfig.VERSION_NAME));
        return v;
    }

    void logout() {
        ((MainActivity)getActivity()).logout();
    }

}

package com.ashberrysoft.leadertask.modern.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.SettingsActivity;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.InputPasFragment;

/**
 * Created by Антон on 21.03.2018.
 */

public class PreferencesFragment extends PreferenceFragment {

    public static PreferencesFragment newInstance() {
        // если надо чет передать во фрагмент
        //final Bundle b = new Bundle();
        //b.putSerializable(EXTRA, extra);

        final PreferencesFragment f = new PreferencesFragment();
        //f.setArguments(b); // передаем параметры

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        return inflater.inflate(R.layout.preferense_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((SettingsActivity) getActivity()).setToolbarTitle(getResources().getString(R.string.menu_settings));
    }

    @Override
    public void onViewCreated(View v, Bundle b) {
        super.onViewCreated(v, b);
        // тут уже все присваиваем

        LinearLayout mMainPref = (LinearLayout) v.findViewById(R.id.main_pref);
        mMainPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFragment(MainPreferencesFragment.newInstance() , true);
            }
        });

        LinearLayout mNavPref = (LinearLayout) v.findViewById(R.id.nav_pref);
        mNavPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFragment(NavPreferencesFragment.newInstance() , true);
            }
        });

        LinearLayout mNotifyPref = (LinearLayout) v.findViewById(R.id.notif_pref);
        mNotifyPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFragment(NotifyPreferencesFragment.newInstance() , true);
            }
        });

        LinearLayout mDataPref = (LinearLayout) v.findViewById(R.id.data_pref);
        mDataPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFragment(DataPreferencesFragment.newInstance() , true);
            }
        });

        LinearLayout mInfoPref = (LinearLayout) v.findViewById(R.id.info_pref);
        mInfoPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFragment(AboutPreferencesFragment.newInstance() , true);
            }
        });

        LinearLayout mInputPref = (LinearLayout) v.findViewById(R.id.input_pref);
        mInputPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFragment(InputPasFragment.newInstance() , true);
            }
        });
    }

    private void startFragment(android.app.Fragment fragment, boolean toBackStack) {
        final android.app.FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();

        ft.replace(SettingsActivity.FRAGMENT_CONTAINER, fragment);
        // добавлять или нет в стек.
        if (toBackStack) {
            ft.addToBackStack(fragment.getClass().getSimpleName());
        }

        ft.commit();
    }

}

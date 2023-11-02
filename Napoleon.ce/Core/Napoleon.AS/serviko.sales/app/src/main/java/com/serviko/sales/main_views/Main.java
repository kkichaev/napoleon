package com.serviko.sales.main_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.serviko.sales.R;

public class Main extends Actions {

    @Override
    int getResourceId() {
        return R.layout.main_view;
    }

    @Override
    protected int getActionCount() {  return 5; }
}

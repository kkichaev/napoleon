package com.serviko.sales.main_views.order_filter;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.serviko.sales.R;
import com.serviko.sales.main_views.ChildFilterFragment;
import com.serviko.sales.main_views.Filter;
import com.serviko.sales.main_views.Model;
import com.serviko.sales.main_views.Orders;

import java.util.HashMap;
import java.util.Map;

public abstract class OrderChildFilter extends ChildFilterFragment {

    @Override
    protected Filter getFilter() { return model.orderFilter; }
}

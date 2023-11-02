package com.grsoft.napoleon.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.napoleon.R;
import com.grsoft.napoleon.views.RoundedDialog;

import java.util.Timer;
import java.util.TimerTask;

public class SuccessExchange extends RoundedDialog {
    static final int SHOW_TIME = 4000;

    Timer t = null;

    @Override
    protected int getLayoutId() { return R.layout.success_exchange_dialog; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    v.animate()
                        .alpha(0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                dismiss();
                            }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, SHOW_TIME);
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(t != null)
            t.cancel();
    }
}

package com.grsoft.napoleon.main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.grsoft.napoleon.R;
import com.grsoft.napoleon.views.RoundedDialog;
import com.grsoft.util.Updater;

public class AboutDialog extends RoundedDialog {
    @Override
    protected int getLayoutId() { return R.layout.about_dialog; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        v.findViewById(R.id.link).setOnClickListener(view -> {
            new Thread(this::dismiss).start();
        });

        v.findViewById(R.id.check_for_updates).setOnClickListener(view -> {
            new Upd(new Upd.Events() {
                @Override
                public void preExecute(Upd updater) {
                    Toast.makeText(getContext(), R.string.check_updating, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void postExecut(Boolean result, Upd updater) {
                    if(!result)
                        Toast.makeText(getContext(), R.string.update_not_found, Toast.LENGTH_SHORT).show();
                }
            }).execute(getContext());
        });
        return v;
    }

    static class Upd extends Updater {
        public interface Events {
            void preExecute(Upd updater);
            void postExecut(Boolean result, Upd updater);
        }

        Events handler;
        public Upd(Events handler) {
            this.handler = handler;
        }

        protected void onPreExecute() {
            if(handler != null)
                handler.preExecute(this);
        };

        protected void onPostExecute(Boolean result) {
            if(handler != null)
                handler.postExecut(result, this);
        };
    }
}

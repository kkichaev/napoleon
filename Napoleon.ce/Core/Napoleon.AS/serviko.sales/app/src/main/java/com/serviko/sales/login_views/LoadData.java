package com.serviko.sales.login_views;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.serviko.sales.R;

public class LoadData extends Fragment {

    static int IMAGE_DELAY = 2000;

    Model model;

    Handler imageHandler = new Handler();

    ImageView[] images = new ImageView[3];
    int[] drawables = new int[] {
            R.drawable.ic_white_2,
            R.drawable.ic_white_1,
            R.drawable.ic_white,
            R.drawable.ic_white_none,
    };
    int current_image = 0;
    Runnable updateImages = new Runnable() {
        @Override
        public void run() {
            current_image++;
            if(current_image >= images.length) {
                current_image = 0;
            }
            images[current_image].setImageResource(R.drawable.ic_white);
            int i = 0;
            for(; i <current_image; i++) {
                images[i].setImageResource(drawables[i]);
            }
            for( ++i; i<images.length; i++) {
                images[i].setImageResource(R.drawable.ic_white_none);
            }
            imageHandler.postDelayed(updateImages, IMAGE_DELAY);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(getActivity()).get(Model.class);

        View v =  inflater.inflate(R.layout.load_data_frame, container, false);

        ImageView iv = v.findViewById(R.id.serviko_text);
        Animation a = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        a.setDuration(6000);
        a.setRepeatCount(Animation.INFINITE);
        a.setInterpolator(new LinearInterpolator(getContext(), null));

        iv.startAnimation(a);

        images[0] = v.findViewById(R.id.wait1);
        images[1] = v.findViewById(R.id.wait2);
        images[2] = v.findViewById(R.id.wait3);

        imageHandler.postDelayed(updateImages, IMAGE_DELAY);

        model.loadData(getActivity());
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        imageHandler.removeCallbacks(updateImages);
    }
}

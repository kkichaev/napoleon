package com.ksoft.anotherworld;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class Hero extends Fragment {
	int pos;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View result = inflater.inflate(R.layout.hero, null);

		LinearLayout layout = (LinearLayout) result.findViewById(R.id.layout9);
		float denst = getActivity().getResources().getDisplayMetrics().density;
		final int SIZE = (int) (60 * denst);
		for (int i = 0; i < 10; i++) {
			ImageView iv = new ImageView(getActivity());
			iv.setScaleType(ScaleType.FIT_XY);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					SIZE, SIZE);
			iv.setLayoutParams(params);
			iv.setImageDrawable(getResources()
					.getDrawable(R.drawable.bottonpnl));
			layout.addView(iv);
		}

		pos = SIZE;

		final HorizontalScrollView scrollView = (HorizontalScrollView) result
				.findViewById(R.id.layout10);

		result.findViewById(R.id.imageView19).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(final View v) {
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {

								v.postDelayed(new Runnable() {
									public void run() {
										scrollView.smoothScrollTo(
												scrollView.getScrollX() + SIZE,
												0);
									}
								}, 80L);
							}
						});
					}
				});
		
		result.findViewById(R.id.imageView18).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(final View v) {
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {

								v.postDelayed(new Runnable() {
									public void run() {
										scrollView.smoothScrollTo(
												scrollView.getScrollX() - SIZE,
												0);
									}
								}, 80L);
							}
						});
					}
				});
		
		return result;
	}
}

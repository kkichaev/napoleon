package com.grsoft.manager;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.util.Updater;

public class About extends DialogFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AboutDialog);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.about, container);
		TextView tvLink = (TextView) result.findViewById(R.id.tvLink);

		if (Features.LINKS_DISSALLOW) {
			tvLink.setEnabled(false);
			tvLink.setMovementMethod(null);
		}

		tvLink.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		result.findViewById(R.id.btnCheckUpdates).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(final View v) {
						new Updater() {
							protected void onPreExecute() {
								Toast.makeText(v.getContext(),
										R.string.check_updating,
										Toast.LENGTH_SHORT).show();
							};

							protected void onPostExecute(Boolean result) {
								if (!result)
									Toast.makeText(v.getContext(),
											R.string.update_not_found,
											Toast.LENGTH_SHORT).show();
							};

						}.execute(v.getContext());
					}
				});
		
		getDialog().setTitle(getString(R.string.action_showabout));
		return result;
	}
}

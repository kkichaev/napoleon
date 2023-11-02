package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.impl.RoutePointImpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.gps.GPSUtilNew;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SettingGPS extends BasePreferenceFragment {

	@Override
	protected int getPreferenceResource() {	return R.xml.gps_pref; }
	
	@Override
	public void onStart() {
		super.onStart();
		
		Config cfg = ConfigManager.getConfig();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Editor ed = pref.edit();
		ed.putString(getString(R.string.gps_dist_pref), Integer.toString(cfg.gpsDistance));
		ed.putString(getString(R.string.gps_freq_pref), Integer.toString(cfg.gpsFrequience / Consts.ONE_SECOND));
		ed.commit();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		Config cfg = ConfigManager.getConfig();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		try{
			cfg.gpsDistance = Integer.parseInt(pref.getString(getString(R.string.gps_dist_pref), getString(R.string.def_gps_dist_val)));
			cfg.gpsFrequience = Integer.parseInt(pref.getString(getString(R.string.gps_freq_pref), getString(R.string.def_gps_freq_val))) * Consts.ONE_SECOND;
			ConfigManager.save();
			
			GPSUtilNew.stop(getActivity());
			
			if(!RoutePointImpl.isRouteComplete())
				GPSUtilNew.start(getActivity());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}

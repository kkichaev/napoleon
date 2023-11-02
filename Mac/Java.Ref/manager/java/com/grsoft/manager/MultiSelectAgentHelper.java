package com.grsoft.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grsoft.dataobjects.ManagerAgent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MultiSelectAgentHelper extends SelectAgentHelper implements OnMultiChoiceClickListener {
	private boolean[] selIdx;
	private Context context;
	private static String PREF_NAME = "MultiSelectAgentHelper.SharedPreferences";
	private final String DEL = ";";
	
	private MultiAgentSelectedListener multiAgentSelectedListener;
	
	public interface MultiAgentSelectedListener{
		void onMultiAgentSelect(List<ManagerAgent> sel);
	}
	
	public void setMultiAgentSelectedListener(MultiAgentSelectedListener listener){
		multiAgentSelectedListener = listener;
	}
	
	public MultiSelectAgentHelper(Context context) {
		this.context = context;
	}
	
	@Override
	public void init() {
		super.init();
		selIdx = new boolean[agents.size()];
		readSelection();
	}
	
	private void readSelection() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String s = pref.getString(PREF_NAME, "");
		
		String[] idx = s.split(DEL);
		
		if(idx.length > 0){
			Set<String> set = new HashSet<String>();
			
			for(String i : idx)
				if (!set.contains(i))
					set.add(i);
			
			if (selIdx.length < agents.size())
				selIdx = new boolean[agents.size()];
			
			for(int i = 0; i < agents.size(); i ++ ){
				ManagerAgent a = agents.get(i);
				selIdx[i] = set.contains(a.id);
			}
		}
	}
	
	private void saveSelection(){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = pref.edit();
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < agents.size() && i < selIdx.length; i++)
			if (selIdx[i]){
				if(sb.length() > 0)
					sb.append(DEL);
				
				sb.append(agents.get(i).id);
			}
		
		e.putString(PREF_NAME, sb.toString());
		e.commit();
	};

	public Dialog createDialog(Context context){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		String data[] = new String[agents.size()];
		
		for(int i = 0; i < agents.size(); i ++)
			data[i] = agents.get(i).name;
		
		builder.setMultiChoiceItems(data, selIdx, this);
		builder.setPositiveButton(R.string.ok, this);
		
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		selIdx[which] = isChecked;
	}
	
	@Override
	protected void applySelect(int which) {
		saveSelection();
		fireMultiAgentSelected(collectSelected());
	}
	
	private void fireMultiAgentSelected(List<ManagerAgent> arr) {
		if(multiAgentSelectedListener != null)
			multiAgentSelectedListener.onMultiAgentSelect(arr);
	}
	
	public List<ManagerAgent> collectSelected() {
		List<ManagerAgent> result = new ArrayList<ManagerAgent>();
		
		for(int i = 0; i < agents.size() && i < selIdx.length; i++)
			if (selIdx[i])
				result.add(agents.get(i));
		
		return result;
	}
	
	
}

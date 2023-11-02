package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.util.MenuHandler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

public class MainEx extends Main {
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.choose_plan_dlg) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Выберите план");

			CharSequence[] cs = new CharSequence[] {
				"Общий план продаж",
				"Фокусный план",
				"План ПДЗ",
			};
			
			builder.setSingleChoiceItems(cs, -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					
					if(arg1 != 1) {
						MonthlyPlans.open(MainEx.this, arg1 == 0 ? MonthlyPlans.PlanType.SalesPlan : MonthlyPlans.PlanType.PDZPlan);
					} else {
						AgentPlanView.open(MainEx.this);
					}
				}
			});
			
			return builder.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected ArrayList<MenuHandler> createDocMenuList() {
		ArrayList<MenuHandler> ret = super.createDocMenuList();
		ret.add(new MenuHandler("Планы", new Runnable() {
			@Override public void run() { showDialog(R.id.choose_plan_dlg); }
		}));
		return ret;
	}
}

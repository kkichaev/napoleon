package com.grsoft.manager;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class AgentRoute extends DrawerActivity implements SelParam {
	public static Class<? extends AgentRoute> activity = AgentRoute.class;
	public static final String VISIT = "visit";
	public static final String USERID = "userid";
	public static final String DATE = "date";
	
	private ImageButton btnMap;
	private ImageButton btnOrder;
	private ImageButton btnSync;
	
	protected String userid = "";
	
	public static void open(Context context, String id, Date date) {
		Intent intent = new Intent(context, activity);
		intent.putExtra(USERID, id);
		intent.putExtra(DATE, date.getTime());
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		Intent intent = getIntent();
		
		if(intent != null){
			userid = intent.getStringExtra(USERID);
			setDate(new Date(intent.getLongExtra(DATE, new Date().getTime())));
		}
		
		initView();
		
		ButtonController ctrl = new ButtonController(this);
		
		if (btnMap != null)
			btnMap.setOnClickListener(ctrl);
		
		if(btnOrder != null)
			btnOrder.setOnClickListener(ctrl);
		
		if(btnSync != null)
			btnSync.setOnClickListener(createSyncClick());
	}


	private OnClickListener createSyncClick() {
		return new OnClickListener() { @Override public void onClick(View v) { doSync();}	};
	}
	
	protected void doSync(){
		GPSChecker.check(this, new Runnable() {
			
			@Override
			public void run() {
				SyncDetail.sync(AgentRoute.this, new UpdateCtrl() {
					@Override
					public void updateCtrl(boolean enabled) {
						for(View v : new View[]{btnMap, btnOrder, btnSync})
							if (v != null)
								v.setEnabled(enabled);
					}
					
					@Override
					public void onFinish(boolean result) {
						if( result ) { updateChildFragment(); }
					}
				}, getUserid(), getDate(), false);
			}
		});
	};

	private void initView() {
		btnMap = (ImageButton) findViewById(R.id.btnMap);
		btnOrder = (ImageButton) findViewById(R.id.btnOrder);
		btnSync = (ImageButton) findViewById(R.id.btnSync);
	}

	@Override
	public String getUserid() { return userid; }

	@Override protected int getLayoutID() { return R.layout.route;	}

	@Override
	protected void postSyncUpdate() {
		// TODO Auto-generated method stub
	}

	@Override
	protected String getActionBarTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void updateChildFragment() {
		DocListFragment f = (DocListFragment) getSupportFragmentManager().findFragmentByTag(DocListFragment.class.getName());
		f.refresh();
		
		MapFragment mf = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.mapFragment);
		mf.refresh();
	}
}

class ButtonController implements OnClickListener{
	Fragment frTop;
	Fragment frBottom;
	int marker = 1;
	private FragmentManager fragmentManager;
	ImageButton ctrls[] = new ImageButton[2];
	
	ButtonController(FragmentActivity fa){
		this.frTop = fa.getSupportFragmentManager().findFragmentById(R.id.mapFragment);
		this.frBottom = fa.getSupportFragmentManager().findFragmentById(R.id.orderFragment);
		fragmentManager = fa.getSupportFragmentManager();
		ctrls[0] = (ImageButton) fa.findViewById(R.id.btnOrder);
		ctrls[1] = (ImageButton) fa.findViewById(R.id.btnMap);
	}

	@Override
	public void onClick(View v) {
		ctrls[0].setEnabled(true);
		ctrls[1].setEnabled(true);
		
		int id = v.getId();
		
		if (id == R.id.btnOrder && marker < 2)
			marker ++;
		else if (id == R.id.btnMap && marker > 0)
			marker--;
		
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		
		if(marker == 0){
			transaction.hide(frTop);
			transaction.show(frBottom);
			v.setEnabled(false);
		}else if(marker == 1){
			transaction.show(frTop);
			transaction.show(frBottom);
		}else if(marker == 2){
			transaction.show(frTop);
			transaction.hide(frBottom);
			v.setEnabled(false);
		}
			
		transaction.commit();
	}
}

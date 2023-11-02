package com.grsoft.napoleon;

public class MainEx extends Main {
//	private static final int GET_REPORT_LIST = 0x145;
//	ReportList adapter = new ReportList();
//
//
//	@Override
//	protected ArrayList<MenuHandler> createDocMenuList() {
//		ArrayList<MenuHandler> ret = super.createDocMenuList();
//		
//		ret.add(new MenuHandler(getString(R.string.reports), new Runnable() {
//			@Override public void run() { showDialog(GET_REPORT_LIST);; }
//		}));
//		return ret;
//	}
//
//	@Override
//	protected void onPrepareDialog(int id, Dialog dialog) {
//		if( id == GET_REPORT_LIST) {
//			AlertDialog ad = (AlertDialog)dialog;
//			((ReportList)ad.getListView().getAdapter()).refresh();
//		}
//		super.onPrepareDialog(id, dialog);
//	}
//	
//	@Override
//	protected Dialog onCreateDialog(int id) {
//		if( id == GET_REPORT_LIST ) {
//			AlertDialog.Builder b = new AlertDialog.Builder(this);
//			b.setTitle("ֲûבונטעו מעקוע");
//			b.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					Report r = (Report) adapter.getItem(which);
//					if( r != null )
//						ReportWebView.open(MainEx.this, r.name);
//				}
//			});
//			return b.create();
//		}
//		return super.onCreateDialog(id);
//	}
//
//	class ReportList extends BaseAdapter {
//		
//		List<Report> reports = new ArrayList<Report>();
//
//		public void refresh() {
//			reports.clear();
//			
//			DataTraveler.travel(Report.class, new DataTraveler.Travel<Report>() {
//
//				@Override
//				public boolean travel(DataTraveler<Report> item) {
//					reports.add(item.data);
//					item.data = new Report();
//					return true;
//				}
//			}, null);
//			notifyDataSetChanged();
//		}
//		
//		@Override
//		public int getCount() {
//			return reports.size();
//		}
//
//		@Override
//		public Object getItem(int arg0) {
//			return arg0 < getCount() ? reports.get(arg0) : null;
//		}
//
//		@Override
//		public long getItemId(int arg0) {
//			return arg0;
//		}
//
//		@Override
//		public View getView(int arg0, View v, ViewGroup arg2) {
//			if( v == null )
//				v = View.inflate(MainEx.this, R.layout.report_row, null);
//			TextView tv = (TextView)v.findViewById(R.id.tvName);
//			Report r = (Report)getItem(arg0);
//			if( r != null )
//				tv.setText(r.name);
//			return v;
//		}
//		
//	}

	
}

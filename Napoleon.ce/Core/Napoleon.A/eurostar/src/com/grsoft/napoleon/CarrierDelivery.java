package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Carrier;
import com.grsoft.dataobjects.CarrierItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.IOrder;
import com.grsoft.napoleon.DeliveryType.UpdateOrder;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.ExtrasConst;

public class CarrierDelivery extends Fragment implements UpdateOrder {
	public static String CARRIER = "carrier";
	private EditText edCarrier;
	private FrameLayout holder;
	private RadioButton rbFromWarehouse;
	private RadioButton rbByAddress;
	private List<Carrier> carriers = new ArrayList<Carrier>();
	private CharSequence crrnames[];
	private ImageButton btnCarrier;
	private ImageButton btnDel;
	public static String CHANGE_CARRIER_CATION = "com.grsoft.napoleon.CHANGE_CARRIER_CATION";
	private CreatableDocument<?> order = null;
	
	public interface CarrierObserver {
		void onChangeCarrier(Carrier carrier);
	}
	
	private DialogInterface.OnClickListener applyCurrier = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			edCarrier.setText(crrnames[which]);
			Carrier carrier = carriers.get(which);
			edCarrier.setTag(carrier);
			
			fireChangeCarrier(carrier);
		}
	};

	protected void fireChangeCarrier(Carrier carrier) {
		FragmentManager fm = getChildFragmentManager();
		List<Fragment> list = fm.getFragments();
		
		for(Fragment f : list)
			if(f instanceof CarrierObserver)
				((CarrierObserver)f).onChangeCarrier(carrier);
	}
	
	private OnCheckedChangeListener onCheckedRB = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(edCarrier.getTag() != null){
				try {
					holder.removeAllViews();
					String name = buttonView.getTag().toString();
					Class<?> type = Class.forName(name);
					Fragment fragment = (Fragment) type.newInstance();
					FragmentManager fm = getChildFragmentManager();
					FragmentTransaction transact = fm.beginTransaction();
					
					Bundle bundle = new Bundle();
					bundle.putParcelable(CARRIER, new CarrierBasket((Carrier) edCarrier.getTag()));
					bundle.putLong(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
					fragment.setArguments(bundle);
	
					if (isChecked)
						transact.add(R.id.holder, fragment);
					else
						transact.remove(fragment);
	
					transact.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			}else{
				Toast.makeText(buttonView.getContext(), R.string.select_carrier, Toast.LENGTH_SHORT).show();
				buttonView.setChecked(false);
			}
		}
		
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.carrier_delivery, container, false);
		
		order = (CreatableDocument<?>) DocType.getCurDoc().create();
		
		inflateView(result);
		initData();
		initView();
		return result;
	}
	
	private void initData() {
		DbReader reader = new DbReader();
		Carrier data = new Carrier();
		boolean bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(data.getClass()), null);
		
		while(bdo){
			carriers.add((Carrier) data.clone());
			bdo = reader.selectNext(data);
		}
		
		crrnames = new CharSequence[carriers.size()];
		
		for(int i = 0; i < carriers.size(); i++)
			crrnames[i] = carriers.get(i).name;
		
		order.read(getArguments().getLong(ExtrasConst.DOC_ROW_ID_STR));
		order.close();
 	}

	private void initView() {
		rbByAddress.setOnCheckedChangeListener(onCheckedRB);
		rbFromWarehouse.setOnCheckedChangeListener(onCheckedRB);
		
		btnCarrier.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new DialogFragment(){
					public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setItems(crrnames,applyCurrier);
						return builder.create();
					};
					
				}.show(getChildFragmentManager(),btnCarrier.getClass().toString());
			}
		});
		
		btnDel.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { 
			edCarrier.setText("");
			edCarrier.setTag(null);
			fireChangeCarrier(null);
		} });
		
		IOrder o = (IOrder) order.getData();
		
		for(Carrier c : carriers)
			if(c.name.equals(o.getCarrier())){
				edCarrier.setText(c.name);
				edCarrier.setTag(c);
				break;
			}
		
		if(o.getPlace() == 1) rbFromWarehouse.setChecked(true);
		else if (o.getPlace() == 2) rbByAddress.setChecked(true);
	}

	protected void inflateView(View view) {
		holder = (FrameLayout) view.findViewById(R.id.holder);
		rbFromWarehouse = (RadioButton) view.findViewById(R.id.rbFromWarehouse);
		rbByAddress = (RadioButton) view.findViewById(R.id.rbByAddress);
		btnCarrier = (ImageButton) view.findViewById(R.id.btnCarrier);
		edCarrier = (EditText) view.findViewById(R.id.edCarrier);
		btnDel = (ImageButton) view.findViewById(R.id.btnDel);
	}

	@Override
	public boolean checkAndUpdate(IOrder order) {
		boolean result = false;
		String c = edCarrier.getText().toString().trim();
		
		int place = 0;
		
		if (rbFromWarehouse.isChecked())
			place = 1;
		else if (rbByAddress.isChecked())
			place = 2;
		
		
		if(c.length() > 0 && place > 0){
			order.setCarrier(c);
			order.setPlace(place);
			
			result = true;
		}
		
		if(result){
			FragmentManager fm = getChildFragmentManager();
			List<Fragment> list = fm.getFragments();
			
			if(list != null)
				for(Fragment f : list)
					if(f instanceof UpdateOrder){
						result = ((UpdateOrder)f).checkAndUpdate(order);
						if (!result)
							break;
					}
		}
		
		return result;
	}
}

class CarrierBasket implements Parcelable{
	public Carrier carrier = new  Carrier();
	
	public CarrierBasket(Carrier carrier){
		this.carrier = carrier;
	}
	
	public CarrierBasket(Parcel p){
		String[] arr = new String[2];
		p.readStringArray(arr);
		carrier.id = arr[0];
		carrier.name = arr[2];
		p.readList(carrier.items, CarrierItem.class.getClassLoader());
	}
	
	@Override
	public int describeContents() {return 0; }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { carrier.id, carrier.name, });
		dest.writeList(carrier.items);
	}
	
	public static final Parcelable.Creator<CarrierBasket> CREATOR = new Parcelable.Creator<CarrierBasket>() {
		public CarrierBasket createFromParcel(Parcel in) {
			return new CarrierBasket(in);
		}

		public CarrierBasket[] newArray(int size) {
			return new CarrierBasket[size];
		}
	};
}

package com.grsoft.napoleon.modules;

import java.io.FileOutputStream;

import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.napoleon.modules.dataobjects.orgCost;
import com.grsoft.napoleon.util.ConvertConstants;
import com.grsoft.network.BinaryFormat;
import com.grsoft.network.ByteStream;
import com.grsoft.network.Member;
import com.grsoft.network.RawObject;

public class CostHitching extends Hitching {
	
//	private static final String DATA_FIELD_NAME = "data";
	
	public static String COST_OBJECT = "Cost"; 

	private Context context;
	CostManagerImpl costManager;
	
	public CostHitching(Context context, CostManagerImpl costManager) {
		super(orgCost.class, COST_OBJECT);
		this.context = context;
		this.costManager = costManager;
	}
	
	public void setCostReaded(boolean readed) { 
		if( costManager != null )
			costManager.setCostReaded(readed);
	}
	
	@Override
	public void onStart() {
		BinaryFormat.BinaryReader = new CostReader(context, this);
		super.onStart();
	}
	
	@Override
	public void onEnd() {
		BinaryFormat.BinaryReader = null;
		super.onEnd();
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
//		BytesMember data = (BytesMember)rawObject.getMember(DATA_FIELD_NAME);
//		
//		if( data != null ) {
//			try {
//				FileOutputStream fos = context.openFileOutput(CostManagerImpl.FILE_NAME, Context.MODE_PRIVATE);
//				byte[] bytes = data.toBytes();
//				
//				fos.write(bytes);
//				
//				fos.flush();
//				fos.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}			
//		}		
	}
}

class CostReader implements BinaryFormat.Reader {
	private Context context;
	CostHitching costHitching;
	
	public CostReader(Context ctx, CostHitching costHitching) { 
		context = ctx;
		this.costHitching = costHitching;
	}

	@Override
	public void read(Member m, ByteStream stream) {
		boolean readed = false;
		StringBuilder size = new StringBuilder();
		if (stream.copyUntill(size, ConvertConstants.COLON)) {
			try {
				int len = Integer.parseInt(size.toString());
				
				FileOutputStream fos = context.openFileOutput(CostManagerImpl.FILE_NAME, Context.MODE_PRIVATE);
				stream.writeBytes(fos, len); // put moveNext to writeBytes
				fos.flush();
				fos.close();
				readed = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if( costHitching != null )
			costHitching.setCostReaded(readed);
	}
}
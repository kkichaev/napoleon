package com.grsoft.napoleon.modules.print;
import com.grsoft.aceteam.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.grsoft.napoleon.Features;
import com.grsoft.aceteam.R;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

public class GraphicPrinter {	
	final static String resType = "raw";
	
	private static final String TAG = "GraphicPrinter";
	
	public GraphicPrinter(){
		
	}
	
	public void sendPrintTask(Context context, File file) {
		String ps = ((CfgNpl)ConfigManager.getConfig()).printSource;
		String action = "android.intent.action.VIEW";
		
		if (ps.equals("HP"))
			action = "org.androidprinting.intent.action.PRINT";
		
		Uri uri = null;
		
		if (Build.VERSION.SDK_INT >= 24) {
			uri = FileProvider.getUriForFile(context,context.getString(R.string.fileprovider_authorities), file);
		}else
			uri = Uri.fromFile(file);
		
		Intent intent = new Intent(action)
			.addCategory(Intent.CATEGORY_DEFAULT)
			.setDataAndType(uri,"application/pdf")
			.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		
		try{
			context.startActivity(intent);
		}catch(ActivityNotFoundException e){
			Toast.makeText(context, R.string.print_activity_not_found, Toast.LENGTH_SHORT).show();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public File print(Context context, String repName, DataSource source) {
		ArrayList<DataSource> list = new ArrayList<DataSource>();
		list.add(source);
		return print(context, repName, list);
	}
	
	public File print(Context context, String repName, List<DataSource> source) {
		
		File result = null;
		try{
			Log.d(TAG, "begin print");
			List<Integer> report = getFormidByName(repName, context);
			
			if (report.size() > 0 && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				result = makePrintFile(context, source, report);
				Log.d(TAG, "end print");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}

	protected File makePrintFile(Context context, List<DataSource> source, List<Integer> report) {
		List<File> imageList = null;
		File result;
		int width = PrintForm.PAGE_WIDTH_PX / NPrinter.ZOOM_SCALE;
		int height = PrintForm.PAGE_HEIGHT_PX / NPrinter.ZOOM_SCALE;
		
		File cacheDir = new File(Environment.getExternalStorageDirectory(), 
				"Android/data/" + context.getPackageName() +"/files/");
		if(!cacheDir.exists())
			cacheDir.mkdirs();
		
		
		PrintForm pf = new PrintForm(source, new Dimension(width, height));
		
		if (Features.PRINT_THROW_PDF) {
			File res = pf.drawPdf(context, cacheDir, report);
			if( res != null )
				return res;
			
			return null;
		}
		
		boolean buldResult = pf.build(context, report.get(0));
		Log.d(TAG, String.format("PrintForm builded: %s",  Boolean.toString(buldResult)));
		imageList = pf.draw(context, cacheDir);		
		result = new File(cacheDir, "output.pdf");
		
		if (imageList != null && imageList.size() > 0){
			try{
				com.itextpdf.text.Document document = new com.itextpdf.text.Document();
				Rectangle r = new Rectangle(0, 0, pf.getWidth(), pf.getHeight());
		        PdfWriter.getInstance(document,new FileOutputStream(result));
		        document.setPageSize(r);
		        
		        document.open();
		        
		        for(File im : imageList){
		        	Image image = Image.getInstance (im.getAbsolutePath());
		        	image.scaleToFit(r.getWidth() - 30, r.getHeight() - 30);
		        	document.add(image);
		        }
		        
		        document.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static final String FORM_DELIM_SIM = "\t";

	protected List<Integer> getFormidByName(String names, Context context){
		List<Integer> result = new ArrayList<Integer>();
		
		String[] nar = names.split(FORM_DELIM_SIM);
		for(String name : nar)
			if (NPrinter.forms.containsKey(name)){
				String resnames = NPrinter.forms.get(name);
				String[] parts = resnames.split(",");
				for(String resname : parts) {
					int resId = context.getResources().getIdentifier(wrapResourceName(resname), "raw", context.getPackageName());
					if( resId > 0 )
						result.add(resId);
				}
			}
		
		return result;	 
	}
	
	protected String wrapResourceName(String name) { return name; }

	protected String getResourceName(String name) {
		return wrapResourceName(NPrinter.forms.get(name));
	}
}

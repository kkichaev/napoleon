package com.grsoft.napoleon.documents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import android.os.Environment;

import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.modules.print.util.MakeDocNumberStartegy;
import com.grsoft.napoleon.util.debug.Path;

public class DocNumberStrategy implements MakeDocNumberStartegy {
	private static final String DOC_NUMBER_FILE = "docnumber.txt";
	private static final String DELIMETER = ";";
	
	@Override
	public String makeNextDocNumber(String table) {
		int num = 1;
		String prefix = DocHelper.getAgentPrefix();
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File f = new File(new File(Environment.getExternalStorageDirectory(),
					Path.SHARED_FOLDER), DOC_NUMBER_FILE);
			
			if (f.exists())
				num = getNumberFromFile(table, f);
		}
		
		return String.format("%s%04d", prefix, num);
	}

	private int getNumberFromFile(String table, File file){
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int result = 1;
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			
			while((line = reader.readLine()) != null){
				DocNumberData dnd = parse(line);
				
				if (dnd.table.equals(table) && dnd.year == year){
					result = dnd.number + 1;
					break;
				}
			}
			
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	public DocNumberData parse(String line){
		DocNumberData result = new DocNumberData();
		String[] data = line.split(DELIMETER);
		
		if (data.length >= 3){
			result.table = data[0];
			result.number = Integer.parseInt(data[1]);
			result.year = Integer.parseInt(data[2]);
		}
		
		return result;
	}
	
	class DocNumberData{
		String table;
		int number;
		int year;
	}
	
	public void saveDocNumber(String table, String number){
		ArrayList<DocNumberData> data = new ArrayList<DocNumberStrategy.DocNumberData>();
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File f = new File(new File(Environment.getExternalStorageDirectory(),
					Path.SHARED_FOLDER), DOC_NUMBER_FILE);
			
			try{
				if(f.exists()){
					BufferedReader reader = new BufferedReader(new FileReader(f));
					String line = null;
					
					while((line = reader.readLine()) != null){
						DocNumberData dnd = parse(line);
						data.add(dnd);
					}
				
					reader.close();
				}
				
				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				int num = DocHelper.parseDocNumber(DocHelper.getAgentPrefix(), number);
				Iterator<DocNumberData> iter = data.iterator();
				BufferedWriter writer = new BufferedWriter(new FileWriter(f));
				boolean dndInList = false;
				
				while(iter.hasNext()){
					DocNumberData dnd = iter.next();
					
					if(dnd.table.equals(table) && dnd.year == year){
						dndInList = true;
						
						if (dnd.number < num)
							dnd.number = num;
					}
					
					writer.write(encodeDnd(dnd) + '\n');
				}

				if(!dndInList){
					DocNumberData dnd = new DocNumberData();
					dnd.number = num;
					dnd.table = table;
					dnd.year = year;
					writer.write(encodeDnd(dnd) + '\n');
				}
				writer.close();
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private String encodeDnd(DocNumberData dnd) {
		return String.format("%s;%d;%d", dnd.table, dnd.number, dnd.year);
	}
}

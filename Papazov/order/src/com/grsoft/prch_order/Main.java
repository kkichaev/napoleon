package com.grsoft.prch_order;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Main extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
//		findViewById(R.id.btnTest).setOnClickListener(new View.OnClickListener() {
//			@Override public void onClick(View arg0) { saveXls(); }
//		});
//		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId(); 
		if(id == R.id.itSettings) {
			Config.show(this);
			return true;
		}
		if(id == R.id.itAdd) {
			GateEditor.open(this, null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	void readXls() {
		HSSFWorkbook wb;
		try {
			File file = new File(getExternalFilesDir(null), "test.xls");
			InputStream fs = new FileInputStream(file);
			wb = new HSSFWorkbook(fs);
			Sheet sheet=wb.getSheetAt(0);
			Row row; 
			Cell cell;

			Iterator<Row> rows = sheet.rowIterator();

			while (rows.hasNext())
			{
				row= rows.next();
				Iterator<Cell> cells = row.cellIterator();
				
				while (cells.hasNext())
				{
					cell= cells.next();
			
					if (cell.getCellTypeEnum() == CellType.STRING)
					{
						System.out.print(cell.getStringCellValue()+" ");
					}
					else if(cell.getCellTypeEnum() == CellType.NUMERIC)
					{
						System.out.print(cell.getNumericCellValue()+" ");
					}
					else
					{
						//U Can Handel Boolean, Formula, Errors
					}
				}
				System.out.println();
			}
			wb.close();
			fs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void saveXls() {
		try {
			HSSFWorkbook wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet("test1") ;
		
			for (int r=0;r < 5; r++ )
			{
				Row row = sheet.createRow(r);
		
				//iterating c number of columns
				for (int c=0;c < 5; c++ )
				{
					Cell cell = row.createCell(c);
					
					cell.setCellValue("Cell "+r+" "+c);
				}
			}
			File file = new File(getExternalFilesDir(null), "test1.xls");
			wb.write(file);
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

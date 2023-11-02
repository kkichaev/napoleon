package com.grsoft.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.PresentEx;

public class PrezentHitching extends ReportHitching {
	public PrezentHitching() {
		super("update_prezent", new ReportParam(),
				Arrays.asList(
						new SendPhotoCountHitching(),
						new UpdatePrezentHitching())
				);
	}

	public static class ReportParam extends DataObject{
		public List<Item> items;

		public ReportParam() {
			DbWriter.checkDBTable(PresentEx.class);

			String stmt = "select p.id, pp.crc, pp.name from Price p left join presentation pp on p.id = pp.id";
			items = DbReader.fetchStmt(Item.class, stmt);
		}
	}

	public static class Item extends DataObject {
		public String name = "";
		public String crc = "";
		public String id = "";
	}
}

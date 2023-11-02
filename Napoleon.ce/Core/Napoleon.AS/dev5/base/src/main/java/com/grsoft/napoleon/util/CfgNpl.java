package com.grsoft.napoleon.util;
import com.grsoft.aceteam.R;


public class CfgNpl extends CfgNplW {
	public static final int SHOW_NEWEST = 1;
	public static final int SHOW_ALL = 0;

	public static final int IMAGE_LOCATION_TOP = 0;
	public static final int IMAGE_LOCATION_CENTER = 1;

	private static final long serialVersionUID = 1L;
	
	public boolean showImageInPriceCount = true;
	public int imagePosInPriceCount = IMAGE_LOCATION_CENTER;
	public boolean showDailySales = true;
	public int onlyNewstItems = SHOW_NEWEST;
	public int linesCount = 2;
	public int chartPeriod = 0;
	public int chartAKB = 0;
	public boolean overlay = false;
	public int distance_start = 9;
	public int distance_end = 18;
	public boolean usePriceMover = true;
	public int priceQty = 0;
	public boolean loadPresentationByWiFi = true;

	@Override
	public void resetToDefault() {
		super.resetToDefault();
		loadPresentationByWiFi = true;
	}
}

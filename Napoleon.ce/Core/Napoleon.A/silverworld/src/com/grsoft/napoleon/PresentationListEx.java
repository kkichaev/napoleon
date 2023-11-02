package com.grsoft.napoleon;

public class PresentationListEx extends PresentationList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private boolean isImagePresent(PresentationData object){
		boolean result = false;
		
		for(PresentationData d : this){
			if (d.image.equals(object.image) && d.folder == object.folder){
				result = true;
				break;
			}
					
		}
		
		return result;
	}
	
	@Override
	public boolean add(PresentationData object) {
		if(!isImagePresent(object))
			return super.add(object);
		else
			return false;
	}

}

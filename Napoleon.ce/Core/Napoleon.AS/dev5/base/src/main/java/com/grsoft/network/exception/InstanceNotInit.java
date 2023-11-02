package com.grsoft.network.exception;
import com.grsoft.aceteam.R;

@SuppressWarnings("serial")
public class InstanceNotInit extends Exception {
	private Class<?> type;
	
	public InstanceNotInit(Class<?> type) {
		this.type = type; }
	
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder("Object: ");
		sb.append(type.toString()).append(" have to initilized.");
		
		return sb.toString();
	}
}

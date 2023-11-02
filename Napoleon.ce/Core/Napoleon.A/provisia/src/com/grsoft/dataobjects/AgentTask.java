package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="AgentTask", keyFields="created")
public class AgentTask extends CreateDocDataObject {
	public static int DONE = 1;

	public Date appointDate;
	
	public String category;
	
	public String text;
	
	public int flags;
	
	public boolean IsDone() { return (flags & DONE) != 0; }
	
	public void SetDone(boolean newDone) {
		if(IsDone())
			flags &= ~DONE;
		else
			flags |= DONE;
		
		params &= ~ParamState.ofExported;
	}
}

package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.script.dataobjects.Script;
import com.grsoft.types.Scale;

public class ScriptEx extends Script {
	public Date dateEnd;
	@Scale(value=0)
	public int flags = 0;
}

package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="atask",keyFields="taskid")
@ServerInfo(name="MTask")
public class MTask extends ATask {
}

package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.ContractMatrixImpl;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="ContractMatrix", keyFields = "name")
@ServerInfo(name="ContractMatrix")
public class ContractMatrix extends DataObject {
    public String name = "";
    public List<MatrixItem> items = new ArrayList<>();

    public static List<MatrixItem> read(String name) {
        ContractMatrixImpl cmi = new ContractMatrixImpl();
        return cmi.read("name", name) ? cmi.getData().items : null;
    }
}

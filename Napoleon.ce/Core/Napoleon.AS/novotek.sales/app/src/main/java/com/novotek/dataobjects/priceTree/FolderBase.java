package com.novotek.dataobjects.priceTree;

import com.novotek.dataobjects.Catalog;
import com.novotek.dataobjects.NameObj;

public class FolderBase implements Comparable<FolderBase> {
    public NameObj name = new NameObj();
    public String url = "";

    public FolderBase(Catalog src) {
        name = src.name;
        url = src.url;
    }

    @Override
    public int compareTo(FolderBase folderBase) {
        return name.compareTo(folderBase.name);
    }

    @Override
    public String toString() {
        return name.toString();
    }
}

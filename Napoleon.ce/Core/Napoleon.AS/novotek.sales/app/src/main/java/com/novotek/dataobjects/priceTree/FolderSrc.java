package com.novotek.dataobjects.priceTree;

import com.novotek.dataobjects.Catalog;
import com.novotek.dataobjects.NameObj;

import java.util.ArrayList;
import java.util.List;

public class FolderSrc extends FolderBase{

    public List<SubFolder> folders = new ArrayList<>();

    public FolderSrc(Catalog src) {
        super(src);
    }
}

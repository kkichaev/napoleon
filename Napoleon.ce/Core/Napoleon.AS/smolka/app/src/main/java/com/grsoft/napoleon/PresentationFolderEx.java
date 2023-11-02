package com.grsoft.napoleon;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.TreeNodeCmp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PresentationFolderEx extends PresentationFolder {
    public static Comparator<TreeNode> CMP_BY_COST = new TreeNodeCmp() {
        PriceImpl price = new PriceImpl();
        PriceImpl price2 = new PriceImpl();

        @Override
        public int compare(TreeNode x, TreeNode y) {
            Class<? extends Document<?>> curDocType = DocType.getCurDoc().getDocClass();

            if ((x instanceof PriceTreeNode) && (y instanceof PriceTreeNode)) {
                price.read(x.getRowid(), false);
                price2.read(y.getRowid(), false);

                int c1 = CostStrategy.getInstance(curDocType).getItemCost(price.getData(), null);
                int c2 = CostStrategy.getInstance(curDocType).getItemCost(price2.getData(), null);

                return Integer.compare(c1, c2);
            } else
                return super.compare(x, y);
        }
    };

    protected FoldersAdapter createAdapter() {
        return new PhotoFolder(this) {
            @Override
            protected Comparator<TreeNode> getComparator() {return CMP_BY_COST;}

            @Override
            protected FolderTreeNode createFoldersTreeNode(FolderTreeNode parent) {
                return new FolderTreeNode(this, parent){
                    @Override
                    public Comparator<TreeNode> getComparator() {
                        return CMP_BY_COST;
                    }
                };
            }
        };
    }

    @Override
    public void sortingPriceList(ArrayList<TreeNode> childs) {
        Collections.sort(childs, CMP_BY_COST);
    }
}

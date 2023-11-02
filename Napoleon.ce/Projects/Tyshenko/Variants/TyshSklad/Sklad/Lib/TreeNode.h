/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Окно с папками
 *
 *  ert   29/10/2007   creating
 */
#ifndef __TREE_NODE_H
#define __TREE_NODE_H

#include <Reflection.h>

class TreeNode
{
public:
   TreeNode(TreeNode* parent = NULL);
   ~TreeNode();

   TreeNode* Find(const ROWID &node) const; // recursion
   TreeNode* NextWithLeafs(bool next, int level) const;

   TreeNode* NextFromLeafs(int startWith = 0) const;
   TreeNode* PrevFromLeafs(int startWith = 0) const;
   TreeNode* NextFromChild(const TreeNode *child, bool next) const;

   void CopyChildID(std::vector<ROWID> *ids) const;
   int  IndexOf(const TreeNode *node) const;

   void NodeWithLeafs(std::vector<ROWID> *ids) const;

   bool HaveChild() const { return (haveLeafs || childs.size() > 0); }

   void Clear();

   int Level() const;

   typedef std::vector<TreeNode*> ChildList;

   ROWID id;
   bool haveLeafs;

   TreeNode *parent;
   ChildList childs;
};

#endif

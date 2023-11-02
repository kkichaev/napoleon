/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Дерево
 *
 *  ert   29/10/2007   creating
 */
#include "stdafx.h"
#include "TreeNode.h"

using namespace std;

//
//------------------------------------------ TreeNode ------------------------------------
//
TreeNode::TreeNode(TreeNode* _parent) : parent(_parent), haveLeafs(false), id(NO_ROWID)
{
}

TreeNode::~TreeNode()
{
   Clear();
}

void TreeNode::Clear()
{
   id = NO_ROWID;
   haveLeafs = 0;

   vector<TreeNode*>::iterator i;
   for( i = childs.begin(); i != childs.end(); i++ )
      delete (*i);

   childs.clear();
}

TreeNode* TreeNode::Find(const ROWID &nodeID) const
{
   if( id == nodeID ) return (TreeNode*)this;

   vector<TreeNode*>::const_iterator i = childs.begin();
   for( ; i != childs.end(); i++ )
   {
      TreeNode *node = (*i)->Find(nodeID);
      if( node != NULL ) return (TreeNode*)node;
   }

   return NULL;
}

TreeNode* TreeNode::NextWithLeafs(bool next, int level) const
{
   if( level < 0 && next )
   {
      TreeNode *n = NextFromLeafs();
      if( n != NULL ) return n;
   }
   if( parent == NULL ) return NULL;

   TreeNode* tn = parent->NextFromChild(this, next);
   if( level < 0 || tn == NULL ) return (TreeNode*)tn;

   int cl = tn->Level();
   while( cl-- > level ) tn = tn->parent;
   return (TreeNode*)tn;
}

TreeNode* TreeNode::NextFromChild(const TreeNode *child, bool next) const
{
   int childIndex = IndexOf(child);
   ATLASSERT( childIndex >= 0 );

   TreeNode *n = NULL;
   if( next )
   {
      n = NextFromLeafs(childIndex+1);
   } else
   {
      if( childIndex > 0 )
         n = PrevFromLeafs(childs.size() - childIndex);
      else
         if( haveLeafs ) return (TreeNode*)this;
   }

   if( n != NULL ) return n;
   if( parent == NULL ) return NULL;
   return parent->NextFromChild(this, next);
}

TreeNode* TreeNode::PrevFromLeafs(int startWith) const
{
   vector<TreeNode*>::const_reverse_iterator i = childs.rbegin();
   advance(i, startWith);

   for( ; i != childs.rend(); i++ )
   {
      TreeNode *node = (*i);
      if( node->haveLeafs ) return (TreeNode*)node;

      node = node->PrevFromLeafs();
      if( node != NULL ) return (TreeNode*)node;
   }

   return NULL;
}

TreeNode* TreeNode::NextFromLeafs(int startWith) const
{
   vector<TreeNode*>::const_iterator i = childs.begin();
   advance(i, startWith);

   for( ; i != childs.end(); i++ )
   {
      TreeNode *node = (*i);
      if( node->haveLeafs ) return (TreeNode*)node;

      node = node->NextFromLeafs();
      if( node != NULL ) return (TreeNode*)node;
   }

   return NULL;
}

void TreeNode::CopyChildID(std::vector<ROWID> *ids) const
{
   for( vector<TreeNode*>::const_iterator i = childs.begin(); i != childs.end(); i++ )
      ids->push_back((*i)->id);
}

int TreeNode::IndexOf(const TreeNode *node) const
{
   int index = 0;
   vector<TreeNode*>::const_iterator i;
   for( i = childs.begin(); i != childs.end(); i++, index++ )
   {
      if( (*i) == node )
         return index;
   }

   return -1;
}

void TreeNode::NodeWithLeafs(std::vector<ROWID> *ids) const
{
   if( haveLeafs ) ids->push_back(id);

   vector<TreeNode*>::const_iterator i;
   for( i = childs.begin(); i != childs.end(); i++ )
      (*i)->NodeWithLeafs(ids);
}

int TreeNode::Level() const
{
   int level = 0;
   TreeNode *tn = parent;
   while( tn != NULL )
   {
      level++;
      tn = tn->parent;
   }

   return level;
}
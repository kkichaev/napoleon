/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Окно с папками
 *
 *  ert   29/10/2007   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <SQLTable.h>
#include "SQLFolderForm.h"

using namespace std;
//
//------------------------------------------ SQLFolderFormData ------------------------------------
//
static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Название", L"name", 100 },
};

const ListFormData::Header *SQLFolderFormData::GetHeader() const
{
   return header; 
}

SQLFolderFormData::SQLFolderFormData() : current(NULL), root(NULL), expandLevel(-1)
{
}

int SQLFolderFormData::ColumnsCount() const
{
   return sizeof(header)/sizeof(header[0]); 
}

bool SQLFolderFormData::Selecting(int index)
{
   int fsz = folders.size();
   if( index >= fsz )
      return SelectLeaf(index - fsz);

   SelectFolder(folders[index]);
   return true;
}

void SQLFolderFormData::Refresh()
{
   ROWID id = root.id;

   root.Clear();
   current = NULL;

   SelectFolder(id);
}

void SQLFolderFormData::SelectFolder(const ROWID& folder, bool forceReload)
{
   if( current == NULL )
   {
      LoadTree();
      current = &root;
   }

   if( folder == NO_ROWID )
      SetCurrent(&root);
   else if( current->id != folder || forceReload )
      SetCurrent(root.Find(folder));
}

void SQLFolderFormData::LoadWithChilds(const TreeNode &node)
{
   TreeNode::ChildList::const_iterator i = node.childs.begin();
   for( ; i != node.childs.end(); i++ )
      LoadWithChilds(*(*i));

   LoadFolderData(node);
}

void SQLFolderFormData::SetCurrent(TreeNode *node)
{
   if( node == NULL ) return;

   current = node;
   folders.clear();
   leafs.clear();

   bool expand = (expandLevel >=0 && node->Level() >= expandLevel);
   if( expand )
   {
      LoadWithChilds(*current);
   } else
   {
      current->CopyChildID(&folders);
      LoadFolderData(*current);
   }
}

bool SQLFolderFormData::NextWithLeafs(bool next)
{
   TreeNode *fnd = current->NextWithLeafs(next, expandLevel);
   if( fnd == NULL ) return false;

   SetCurrent(fnd);
   return true;
}

int SQLFolderFormData::Up()
{
   if( current == &root ) return -1;

   TreeNode* sv = current;
   SetCurrent(current->parent);
   return current->IndexOf(sv);
}

//
//------------------------------------------ SQLFolderForm ------------------------------------
//
SQLFolderForm::SQLFolderForm() : enableUp(FALSE)
{
}

LRESULT SQLFolderForm::MoveUp(LPNMHDR hdr)
{
   BOOL bHandled = TRUE;
   NMLISTVIEW *lv = (NMLISTVIEW*)hdr;
   if( lv->iSubItem == 0 )
      Backing(0, 0, NULL, bHandled);

   return 0;
}

int SQLFolderForm::ImageIndex(int index) const
{
   return (((SQLFolderFormData*)data)->IsFolder(index)) ? 0 : -1;
}

LRESULT SQLFolderForm::SetCellInfo(LPNMHDR hdr)
{
   if( ListForm::SetCellInfo(hdr) == FALSE )
      return FALSE;

   NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
   if( di->item.mask & LVIF_IMAGE )
      di->item.iImage = ImageIndex(di->item.iItem);

   return TRUE;
}

int SQLFolderForm::ImageListID(ListViewMultiLine *list) const
{
   if( GetSystemMetrics(SM_CXSMICON) == 16 )
      return IDB_FOLDER;
   else
      return IDB_FOLDER32;
}

bool SQLFolderForm::SetDataEx(IFormData *_data, int scale)
{
   ((SQLFolderFormData*)_data)->InitData();

   if( ListForm::SetDataEx(_data, scale) == false ) return false;

   listCtrl.ModifyStyle(LVS_SHOWSELALWAYS, 0);

   enableUp = (((SQLFolderFormData*)data)->IsTopLevel()) ? FALSE : TRUE;//FALSE;
   UpdateLayout(false);

   SetTitle(((SQLFolderFormData*)data)->GetTitle());
   return true;
}

LRESULT SQLFolderForm::ItemSelected(LPNMHDR hdr)
{
   int index = ((NMLISTVIEW*)hdr)->iItem;
   if( index >= 0 )
   {
      bool isFolder = ((SQLFolderFormData*)data)->IsFolder(index);
      listCtrl.SetItemState(index, 0, LVIS_SELECTED);

      if( ((SQLFolderFormData*)data)->Selecting(index) )
      {
         if( isFolder )
         {
            listCtrl.SetItemState(index, 0, LVIS_FOCUSED);
            Refresh();
         
            enableUp = TRUE;
            //menuBar.EnableButton(IDC_BACK, enableUp);

            SetTitle(((SQLFolderFormData*)data)->GetTitle());
         } else
         {
            curIndex = -1;

            // не могу по другому решить вопрос с перемещением по прайсу в диалоге кол-ва
            if( ((SQLFolderFormData*)data)->RefreshAfterSelect() )
               Refresh();
            else
               listCtrl.RedrawItems(index, index);
            sumLabel.SetSum(((SQLFolderFormData*)data)->Sum());
         }
      }
   }

   return 0;
}

void SQLFolderForm::SetTitle(const wchar_t *title)
{
   CHeaderCtrl h = listCtrl.GetHeader();
   HDITEM item;
   item.mask = HDI_TEXT;
   item.pszText = (wchar_t*)title;
   h.SetItem(0, &item);
}

void SQLFolderForm::ChangeFolder(bool next)
{
   bool done = ((SQLFolderFormData*)data)->NextWithLeafs(next);
   if( done )
   {
      int index = listCtrl.GetSelectedIndex();
      if( index >= 0 )
         listCtrl.SetItemState(index, 0, LVIS_SELECTED);

      index = listCtrl.GetNextItem(-1, LVNI_FOCUSED);
      if( index >= 0 )
         listCtrl.SetItemState(index, 0, LVIS_FOCUSED);

      Refresh();
      SetTitle(((SQLFolderFormData*)data)->GetTitle());
   }
}

LRESULT SQLFolderForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   int selIndex = ((SQLFolderFormData*)data)->Up();
   if( selIndex >= 0 )
   {
      int index = listCtrl.GetSelectedIndex();
      if( index >= 0 ) listCtrl.SetItemState(index, 0, LVIS_SELECTED);

      index = listCtrl.GetNextItem(-1, LVNI_FOCUSED);
      if( index >= 0 ) listCtrl.SetItemState(index, 0, LVIS_FOCUSED);

      Refresh();

      if( selIndex >= 0 ) listCtrl.EnsureVisible(selIndex, FALSE);
   }

   return 0;
}

LRESULT SQLFolderForm::OnKeyDown(LPNMHDR hdr)
{
   NMLVKEYDOWN *pKey = (NMLVKEYDOWN*)hdr;
   if( pKey->wVKey == VK_LEFT || pKey->wVKey == VK_RIGHT )
   {
      ChangeFolder(pKey->wVKey==VK_RIGHT);
      return TRUE;
   }
   SetMsgHandled(FALSE);
   return FALSE;
}

void SQLFolderForm::Refresh()
{
   //menuBar.EnableButton(IDC_BACK, (((SQLFolderFormData*)data)->IsTopLevel()) ? FALSE : TRUE);
   SetTitle(((SQLFolderFormData*)data)->GetTitle());

   BOOL enable = (((SQLFolderFormData*)data)->HaveLeaf()) ? TRUE : FALSE;
   menuBar.EnableButton(IDC_NEXT, enable);
   menuBar.EnableButton(IDC_PREV, enable);

   sumLabel.SetSum(((SQLFolderFormData*)data)->Sum());

   ListForm::Refresh();
}


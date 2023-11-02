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
#include "FolderForm.h"

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Название", L"name", 100 },
   //{ ListFormData::Header::Left, L"Кол-во", L"qty", 50 },
   //{ ListFormData::Header::Right, L"Сумма", L"sum", 50 }
};

const ListFormData::Header *FolderFormData::GetHeader() const
{
   return header; 
}

int FolderFormData::ColumnsCount() const
{
   return sizeof(header)/sizeof(header[0]); 
}

bool FolderFormData::Selecting(int index)
{
   int fsz = folders.size();
   if( index >= fsz )
      return SelectLeaf(index - fsz);

   CEOID upFolder = folders[index];
   upFolders.push_back(upFolder);
   LoadFolder(upFolder);
   return true;
}

int FolderFormData::IndexOf(CEOID oid) const
{
   std::vector<CEOID>::const_iterator ctr = folders.begin();
   for( int i=0; ctr != folders.end(); i++, ctr++ )
      if( *ctr == oid ) return i;

   return -1;
}

CEOID FolderFormData::Backing()
{
   if( upFolders.size() < 2 ) return 0;

   CEOID retVal = upFolders.back();
   upFolders.pop_back();
   CEOID upFolder = upFolders.back();
   LoadFolder(upFolder);
   return retVal;
}

void FolderFormData::MakeUpFolders(CEOID cur, CETable *folderTable)
{
   const DataReflector &reflector = folderTable->DataType();
   IReflectableData *item = reflector.Create();
   const MemberType &levelT = reflector.Type(L"level");
   upFolders.clear();

   upFolders.insert(upFolders.begin(), cur);
   folderTable->GetCurrent(item);

   WORD checkLevel = *(WORD*)levelT.GetValue(*item);
   while( (cur=folderTable->MoveNext(false)) != 0 )
   {
      folderTable->GetCurrent(item);
      WORD level = *(WORD*)levelT.GetValue(*item);
      if( level < checkLevel )
      {
         checkLevel = level;
         upFolders.insert(upFolders.begin(), cur);
      }
   }
   upFolders.insert(upFolders.begin(), 0);
   delete item;
}

//
// Post : true, item == paramItem || item->level > paramItem->level
//        false *oid == 0, item->level <= paramItem->level
//
bool FolderFormData::HaveLeafItems(CETable *folderTable, IReflectableData *item, CEOID *oid)
{
   const DataReflector& reflector = folderTable->DataType();
   const MemberType &levelT = reflector.Type(L"level");
   const MemberType &sizeT = reflector.Type(L"size");

   WORD checkLevel = *(WORD*)levelT.GetValue(*item);
   while(true)
   {
      WORD size = *(WORD*)sizeT.GetValue(*item);
      if( size > 0 ) return true;

      *oid = folderTable->MoveNext(true);
      if( *oid == 0 ) return false;
    
      folderTable->GetCurrent(item);
      WORD level = *(WORD*)levelT.GetValue(*item);
      if( level <= checkLevel ) return false; 
   }
}


void FolderFormData::LoadFolders(const SyncFormat &syncFolder, CEOID upFolder)
{
   CEDBFormat folderFormat(syncFolder);
   CETable folderTable(folderFormat);

   folders.clear();
   leafs.clear();

   if( !folderTable.Open(syncFolder.FileName()) || !folderTable.SetTag(L"sort", true) || folderTable.Count() == 0 )
      return;

   const DataReflector& reflector = folderTable.DataType();
   IReflectableData *fItem = reflector.Create();
   const MemberType &levelT = reflector.Type(L"level");
   const MemberType &sizeT = reflector.Type(L"size");
   const MemberType &nameT = reflector.Type(L"name");
   const MemberType &firstIDT = reflector.Type(L"firstID");

   CEOID oid;
   WORD curLevel, priceSize;
   DWORD priceItem = -1;
   if( upFolder == 0 ) // создаем верхний массив
   {
      oid = folderTable.SetPos(0);
      folderTable.GetCurrent(fItem);
      curLevel = *(WORD*)levelT.GetValue(*fItem);

      upFolders.clear();
      upFolders.push_back(0);

      title = L"";
   } else
   {
      folderTable.Seek(upFolder);
      folderTable.GetCurrent(fItem);
      curLevel = *(WORD*)levelT.GetValue(*fItem);
      
      title = *(const wchar_t**)nameT.GetValue(*fItem);
      WORD size = *(WORD*)sizeT.GetValue(*fItem);
      if( size != 0 )
      {
         priceItem = *(DWORD*)firstIDT.GetValue(*fItem);
         priceSize = size;
      }
      // проверить сл. запись относится ли она к той же группе
      oid = folderTable.MoveNext(true);
      if( oid != 0 )
      {
         folderTable.GetCurrent(fItem);
         WORD level = *(WORD*)levelT.GetValue(*fItem);
         if( level <= curLevel ) oid = 0;
         else curLevel = level;
      }
   }
   
   // load folders
   LoadFolders(folderTable, fItem, oid, curLevel, &priceSize, &priceItem);
   delete fItem;

   // load price
   if( priceItem != -1 )
      LoadLeafs(priceItem, priceSize);
}


void FolderFormData::LoadFolders(CETable &folderTable, IReflectableData *fItem, CEOID oid, WORD curLevel, WORD *priceSize, DWORD *priceItem)
{
   const DataReflector& reflector = folderTable.DataType();
   const MemberType &levelT = reflector.Type(L"level");
   const MemberType &sizeT = reflector.Type(L"size");

   while( oid != 0 )
   {
      WORD level = *(WORD*)levelT.GetValue(*fItem);
      if( level == curLevel )
      {
         CEOID curOID;
         if( HaveLeafItems(&folderTable, fItem, &curOID) )
         {
            folders.push_back(oid);
            curOID = folderTable.MoveNext(true);
            if( curOID == 0 )
               break;
            folderTable.GetCurrent(fItem);
            level = *(WORD*)levelT.GetValue(*fItem);
         }
         oid = curOID;
      }

      if( level < curLevel )
         break;

      if( level > curLevel )
      {
         oid = folderTable.MoveNext(true);
         if( oid == 0 ) break;

         folderTable.GetCurrent(fItem);
         //level = *(WORD*)levelT.GetValue(*fItem);
      }
   }
}

//
//--------------------------- Folder Form -------------------
//
FolderForm::FolderForm() : enableUp(FALSE)
{
}

LRESULT FolderForm::MoveUp(LPNMHDR hdr)
{
   BOOL bHandled = TRUE;
   NMLISTVIEW *lv = (NMLISTVIEW*)hdr;
   if( lv->iSubItem == 0 )
      Backing(0, 0, NULL, bHandled);

   return 0;
}

int FolderForm::ImageIndex(int index) const
{
   return (((FolderFormData*)data)->IsFolder(index)) ? 0 : -1;
}

LRESULT FolderForm::SetCellInfo(LPNMHDR hdr)
{
   if( ListForm::SetCellInfo(hdr) == FALSE )
      return FALSE;

   NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
   if( di->item.mask & LVIF_IMAGE )
      di->item.iImage = ImageIndex(di->item.iItem);

   return TRUE;
}

int FolderForm::ImageListID(ListViewMultiLine *list) const
{
   if( GetSystemMetrics(SM_CXSMICON) == 16 )
      return IDB_FOLDER;
   else
      return IDB_FOLDER32;
}

bool FolderForm::SetDataEx(IFormData *_data, int scale)
{
   if( ListForm::SetDataEx(_data, scale) == false ) return false;

   listCtrl.ModifyStyle(LVS_SHOWSELALWAYS, 0);

   enableUp = (((FolderFormData*)data)->IsTopLevel()) ? FALSE : TRUE;//FALSE;
   UpdateLayout(false);

   SetTitle(((FolderFormData*)data)->GetTitle());
   return true;
}

LRESULT FolderForm::ItemSelected(LPNMHDR hdr)
{
   int index = ((NMLISTVIEW*)hdr)->iItem;
   if( index >= 0 )
   {
      bool isFolder = ((FolderFormData*)data)->IsFolder(index);
      listCtrl.SetItemState(index, 0, LVIS_SELECTED);

      if( ((FolderFormData*)data)->Selecting(index) )
      {
         if( isFolder )
         {
            listCtrl.SetItemState(index, 0, LVIS_FOCUSED);
            Refresh();
         
            enableUp = TRUE;
            menuBar.EnableButton(IDC_BACK, enableUp);

            SetTitle(((FolderFormData*)data)->GetTitle());
         } else
         {
            curIndex = -1;
            listCtrl.RedrawItems(index, index);
            sumLabel.SetSum(((FolderFormData*)data)->Sum());
         }
      }
   }

   return 0;
}

void FolderForm::SetTitle(const wchar_t *title)
{
   CHeaderCtrl h = listCtrl.GetHeader();
   HDITEM item;
   item.mask = HDI_TEXT;
   item.pszText = (wchar_t*)title;
   h.SetItem(0, &item);
}

void FolderForm::ChangeFolder(bool next)
{
   bool done = ((FolderFormData*)data)->MoveToFolder(next);
   if( done )
   {
      int index = listCtrl.GetSelectedIndex();
      if( index >= 0 )
         listCtrl.SetItemState(index, 0, LVIS_SELECTED);

      index = listCtrl.GetNextItem(-1, LVNI_FOCUSED);
      if( index >= 0 )
         listCtrl.SetItemState(index, 0, LVIS_FOCUSED);

      Refresh();
      SetTitle(((FolderFormData*)data)->GetTitle());
   }
}

LRESULT FolderForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   CEOID oid = ((FolderFormData*)data)->Backing();
   if( oid != 0 )
   {
      int index = listCtrl.GetSelectedIndex();
      if( index >= 0 )
         listCtrl.SetItemState(index, 0, LVIS_SELECTED);

      index = listCtrl.GetNextItem(-1, LVNI_FOCUSED);
      if( index >= 0 )
         listCtrl.SetItemState(index, 0, LVIS_FOCUSED);

      //listCtrl.SetItemCount(data->Count());
      Refresh();

      index = ((FolderFormData*)data)->IndexOf(oid);
      if( index >= 0 ) listCtrl.EnsureVisible(index, FALSE);
   }

   return 0;
}

LRESULT FolderForm::OnKeyDown(LPNMHDR hdr)
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

void FolderForm::Refresh()
{
   menuBar.EnableButton(IDC_BACK, (((FolderFormData*)data)->IsTopLevel()) ? FALSE : TRUE);
   SetTitle(((FolderFormData*)data)->GetTitle());

   BOOL enable = (((FolderFormData*)data)->HaveLeaf()) ? TRUE : FALSE;
   menuBar.EnableButton(IDC_NEXT, enable);
   menuBar.EnableButton(IDC_PREV, enable);

   sumLabel.SetSum(((FolderFormData*)data)->Sum());

   ListForm::Refresh();
}


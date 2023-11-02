/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Приходы
 *
 *  ert   26/03/2010   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "OrgList.h"
#include "FldOrgs.h"
#include <DocType.h>
#include <SQLFolderForm.h>

#include <Add.h>

struct IncomeView : public FolderFormItem
{
   DWORD qty; // QTY_SCALE
   wchar_t* text;
   DECLARE_TYPE_REFLECTION(IncomeView)
};

class IncomeData : public SQLFolderFormData
{
public:
   IncomeData(const wchar_t* docType) { this->docType = docType; }

   const wchar_t* BackType() { return docType; }

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const { return 3; }
   virtual const DataReflector& DataType() const { return IncomeView().GetType(); }

   virtual bool Get(IReflectableData* data, int index) const;
   virtual bool SelectLeaf(int index) { return false; }

   virtual DWORD Sum() const { return 0; }

   virtual const wchar_t *GetTitle() const;

   void ChangeViewType(bool expand)
   {
      leafData.clear();
      SetCurrent(current, expand);
   }

   void Collapse()
   {
      expandLevel = -1;
   }

protected:
   virtual void LoadTree();
   virtual void LoadFolderData(const TreeNode& folder);

   const wchar_t* GetTopFolderName(const ROWID& id) const;

   const wchar_t* docType;

   mutable PriceImpl price;
   mutable FolderImpl folder;

   mutable wchar_t buf[50];

   struct PData
   {
      std::wstring id;
      DWORD qty;
      std::wstring text;
   };

   struct LeafData
   {
      DWORD qty;
      wchar_t *text;
   };

   typedef std::vector<PData> PriceData;

   std::map<ROWID, ROWID> foldersData;
   std::map<ROWID, PriceData> priceData;

   std::vector<LeafData> leafData;
};

class IncomeForm : public SQLFolderForm
{
public:
   IncomeForm() :reportView(false) {}

   virtual bool SetData(IFormData *_data)
   {
      if( SQLFolderForm::SetDataEx(_data, 3) == false )
         return false;

      LoadMenuBar(true);
      return true;
   }

   BEGIN_MSG_MAP(IncomeForm)
      COMMAND_ID_HANDLER(IDC_CLOSE, Closing)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_SET_VIEW_TYPE, SetViewType)
      CHAIN_MSG_MAP(SQLFolderForm)
   END_MSG_MAP()

   virtual DWORD GetResourceID() const { return IDD_INCOME; }
   virtual DWORD GetMenuBarID() const { return IDD_INCOME; }

   DECLARE_FORM(IncomeForm, IDD_INCOME)

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      reportView = false;
      ((IncomeData*)data)->Collapse();

      LRESULT res = SQLFolderForm::Backing(nCode, id, hWnd, bHandled);
      SetViewTypeButton();
      return res;
   }

   LRESULT SetViewType(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      reportView = !reportView;

      ((IncomeData*)data)->ChangeViewType(reportView);
      Refresh();
      SetViewTypeButton();
      return 0;
   }

   LRESULT Closing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      OpenOrgList(((IncomeData*)data)->BackType());
      return 0;
   }

   virtual bool CanSetColumn(int rowIndex, int colIndex) const
   {
      if( colIndex == 0 ) return true;
      if( ((IncomeData*)data)->IsFolder(rowIndex) ) return false;

      return true;
   }

   virtual void LoadMenuBar(bool hideSIP)
   {
      ListForm::LoadMenuBar(hideSIP);
      SetViewTypeButton();
   }

   void SetViewTypeButton()
   {
      TBBUTTONINFO bi = {0};
      bi.cbSize = sizeof(bi);
      bi.dwMask = TBIF_IMAGE;
      bi.iImage = (reportView) ? 21 : 20;
      menuBar.SetButtonInfo(IDC_SET_VIEW_TYPE, &bi);

      BOOL enable = (reportView && ((IncomeData*)data)->IsTopLevel() ||  !((IncomeData*)data)->HaveLeaf()) ? FALSE : TRUE;

      menuBar.EnableButton(IDC_NEXT, enable);
      menuBar.EnableButton(IDC_PREV, enable);
   }

   bool reportView;
};

BEGIN_TYPE_REFLECTION(IncomeView)
   CHAIN_REFLECTION(IncomeView, FolderFormItem)
   REGISTER_ULONG_SCALE_MEMBER2(IncomeView, qty, QTY_SCALE, true)
   REGISTER_STRING_MEMBER(IncomeView, text)
END_TYPE_REFLECTION(IncomeView)

IMPLEMENT_FORM(IncomeForm)

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left,  L"Название", L"name", 100 },
   { ListFormData::Header::Right, L"Кол-во",   L"qty", 20 },
   { ListFormData::Header::Right, L"",   L"text", 30 },
};

const ListFormData::Header *IncomeData::GetHeader() const
{ 
   return header; 
}

const wchar_t* IncomeData::GetTopFolderName(const ROWID& id) const
{
   SYSTEMTIME st;
   FILETIME ft;
   *(__int64*)&ft = id;

   FileTimeToSystemTime(&ft, &st);
   GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));

   return buf;
}

bool IncomeData::Get(IReflectableData* data, int index) const
{
   bool ret = false;
   if( leafs.size() > 0 )
   {
      const ROWID& id = leafs[index];
      if( id != NO_ROWID && price.Read(id) )
      {
         ((FolderFormItem*)data)->name = price.name;
      } else
         ((FolderFormItem*)data)->name = L"?";

      ((IncomeView*)data)->qty = leafData[index].qty;
      ((IncomeView*)data)->text = leafData[index].text;

      ret = true;
   } else if( IsTopLevel() )
   {
      if( index < (int)folders.size() )
      {
         ((FolderFormItem*)data)->name = GetTopFolderName(folders[index]);
         ((IncomeView*)data)->qty = 0;
         ((IncomeView*)data)->text = L"";

         ret = true;
      } 
   } else if( current->parent == &root ) 
   {
      if( index < (int)folders.size() )
      {
         std::map<ROWID, ROWID>::const_iterator fnd = foldersData.find(folders[index]);
         ((IncomeView*)data)->qty = 0;
         ((IncomeView*)data)->text = L"";
         if( folder.Read(fnd->second) )
         {
            ((FolderFormItem*)data)->name = folder.name;
         } else
            ((FolderFormItem*)data)->name = L"?";

         ret = true;
      }
   }

   return ret;
}

ROWID FindFolder(const std::map<ROWID, ROWID> &folders, const ROWID& data)
{
   std::map<ROWID, ROWID>::const_iterator i = folders.begin();
   for( ; i != folders.end(); i++ )
      if( i->second == data ) return i->first;

   return (__int64)-1;
}

void IncomeData::LoadTree()
{
   IncomeImpl ii;
   SQLTable table(ii.Name());

   bool bdo = table.Select(&ii);
   ROWID index = 1, priceIndex = 0;
   while( bdo )
   {
      TreeNode *current = new TreeNode(&root);
      current->id = *(__int64*)&ii.date;
      current->haveLeafs = false;

      DWORD curFolder;
      PriceData fpdata;
      vector_t<IncomeItem>::const_iterator i = ii.items.begin();
      for( ; i != ii.items.end(); i++ )
      {
         if( i == ii.items.begin() || curFolder != i->folderID )
         {
            curFolder = i->folderID;

            if( i != ii.items.begin() )
               priceData[priceIndex] = fpdata;

            folder.id = curFolder;
            folder.Read();

            priceIndex = FindFolder(foldersData, folder.RID());
            if( priceIndex != (__int64)-1 )
            {
               fpdata = priceData[priceIndex];
            } else
            {
               TreeNode *leaf = new TreeNode(current);
               leaf->id = index;
               leaf->haveLeafs = true;
               current->childs.push_back(leaf);

               foldersData[index] = folder.RID();
               fpdata.clear();

               priceIndex = index;
               index++;
            }
         }

         PData pdata;
         pdata.id = i->id;
         pdata.qty = i->qty;
         pdata.text = i->remark;
         fpdata.push_back(pdata);
      }

      if( fpdata.size() )
         priceData[priceIndex] = fpdata;

      root.childs.push_back(current);

      bdo = table.SelectNext(&ii);
   }
}

const wchar_t* IncomeData::GetTitle() const
{
   if( IsTopLevel() )
      return L"Приходы";

   if( current->parent == &root ) 
      return GetTopFolderName(current->id);

   std::map<ROWID, ROWID>::const_iterator fnd = foldersData.find(current->id);
   if( folder.Read(fnd->second) )
      return folder.name;

   return L"?";
}

void IncomeData::LoadFolderData(const TreeNode& folder)
{
   if( expandLevel < 0 ) leafData.clear();

   if( &folder == &root  )
   {
   } else if( folder.parent == &root ) 
   {
      title = GetTopFolderName(folder.id);
   } else
   {
      std::map<ROWID, PriceData>::const_iterator fnd = priceData.find(folder.id);

      PriceData::const_iterator pi = fnd->second.begin();
      for( ; pi != fnd->second.end(); pi++ )
      {
         ROWID rid = NO_ROWID;
         LeafData ld;
         ld.qty = pi->qty;
         ld.text = (wchar_t*)pi->text.c_str();

         price.id = (wchar_t*)pi->id.c_str();
         if( price.Read() )
            rid = price.RID();

         leafs.push_back(rid);
         leafData.push_back(ld);
      }
   }
}

void OpenIncomes(const wchar_t* docType)
{
   SQLCheckTable(IncomeImpl());
   _Module.GetFrame()->Load(IDD_INCOME, new IncomeData(docType));
}

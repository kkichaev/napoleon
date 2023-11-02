/*
* Copyright (C), 2007 - 2010, Денис Мосягин
*
* WhDoc List
*
*  ert   03/09/2010   creating
*/
#include "stdafx.h"

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <StdFuncs.h>
#include <ListForm.h>

#include "Preference.h"
#include <Password.h>
#include <PrfDlg.h>
#include "FormEntries.h"
#include "ObjImpl.h"
#include <DateDialog.h>

struct ListDocItem : public IReflectableData
{
   FILETIME date;
   DWORD qty;

   DECLARE_TYPE_REFLECTION(ListDocItem)
};

BEGIN_TYPE_REFLECTION(ListDocItem)
   REGISTER_TIMESTAMP_MEMBER(ListDocItem, date)
   REGISTER_ULONG_MEMBER(ListDocItem, qty)
END_TYPE_REFLECTION(ListDocItem)

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Дата", L"date", 80 },
   { ListFormData::Header::Right, L"Кол-во", L"qty", 50 }
};

class DocData : public ListFormData
{
public:
   DocData();

   virtual const Header *GetHeader() const { return header; }
   virtual int ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }

   virtual const DataReflector& DataType() const { return ListDocItem().GetType(); }
   virtual int Count() const { return rids.size(); }
   virtual bool Get(IReflectableData* data, int index) const;

   virtual bool Removing(int index);
   virtual bool Selecting(int index) { return Editing(index); }
   virtual bool Editing(int index);

   bool Checked(int index) const;

   bool RemoveTill(const SYSTEMTIME& check);

protected:
   void Load();

   std::vector<ROWID> rids;
   mutable WhDocImpl doc;
};

class DocList : public ListForm
{
public:
   virtual bool SetData(IFormData* _data);

   DECLARE_FORM(DocList, IDC_DOC_LIST)

   virtual DWORD GetResourceID() const { return IDC_DOC_LIST; }
   virtual DWORD GetMenuBarID() const { return IDC_DOC_LIST; }
   virtual DWORD GetMenuID() const { return IDC_DEL; }

   BEGIN_MSG_MAP(DocList)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_REMOVE_ORDERS, Remove)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   virtual LRESULT SetCellInfo(LPNMHDR hdr);

   LRESULT Remove(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      OpenMainForm();
      return 0;
   }

   virtual int ImageListID(ListViewMultiLine *list) const
   {
      return ( GetSystemMetrics(SM_CXSMICON) == 16 ) ? IDB_CHECKED : IDB_CHECKED32;
   }
};

IMPLEMENT_FORM(DocList)

DocData::DocData()
{
   Load();
}

void DocData::Load()
{
   SQLTable t(doc.Name());

   rids.clear();
   t.RIDList(&rids, L"ORDER BY created");
}

bool DocData::Editing(int index)
{
   if( (unsigned) index < rids.size() )
   {
      WhDocImpl *di = new WhDocImpl();
      di->Read(rids[index]);

      OpenDoc(di, true);
   }
   return false;
}

bool DocData::RemoveTill(const SYSTEMTIME& check)
{
   FILETIME ft;
   SystemTimeToFileTime(&check, &ft);

     wchar_t buf[200];
   __int64 val = ft.dwLowDateTime | (((__int64)ft.dwHighDateTime) << 32);
   wsprintf(buf,  L"created <= %d%09d", val / 1000000000, val % 1000000000);
   std::wstring sql(L"DELETE FROM '");
   sql += doc.Name();
   sql += L"' WHERE ";
   sql += buf;

   bool res = false;
   if( SQLTable::Execute(sql.c_str()) )
   {
      Load();
      res = true;
   }
   return res; 
} 

LRESULT DocList::Remove(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   RemoveDateDialog dlg;
   if( dlg.DoModal() == IDOK )
   {
      if( ((DocData*)data)->RemoveTill(dlg.date) )
         Refresh();
   }
   return 0;
}

bool DocData::Removing(int index)
{
   bool res = false;
   if( (unsigned) index < rids.size() )
   {
      if( MessageBox(GetActiveWindow(), L"Удалить документ?", L"Вопрос", MB_YESNO|MB_ICONQUESTION) == IDYES)
      {
         doc.Read(rids[index]);
         if( doc.Remove() )
         {
            Load();
            res = true;
         }
      }
   }

   return res;
}

bool DocData::Checked(int index) const
{
   if( (unsigned)index > rids.size() ) return false;
   doc.Read(rids[index]);

   return ((doc.params & ofExported) != 0);
}

bool DocList::SetData(IFormData* _data)
{
   if( !ListForm::SetData(_data) )
      return false;

   LoadMenuBar(false);
   UpdateLayout(false);
   return true;
}

bool DocData::Get(IReflectableData* data, int index) const
{
   if( (unsigned)index >= rids.size() )
      return false;

   doc.Read(rids[index]);

   ((ListDocItem*)data)->date = doc.created;
   ((ListDocItem*)data)->qty = doc.items.size();

   return true;
}

LRESULT DocList::SetCellInfo(LPNMHDR hdr)
{
   NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
   if( ListForm::SetCellInfo(hdr) == FALSE )
   {
      if( di->item.mask & LVIF_IMAGE )
         di->item.iImage = 0;
      return FALSE;
   }

   if( di->item.mask & LVIF_IMAGE )
      di->item.iImage = (((DocData*)data)->Checked(di->item.iItem)) ? 1 : 0;

   return ListForm::SetCellInfo(hdr);
}

void OpenDocList()
{
   _Module.GetFrame()->Load(IDC_DOC_LIST, new DocData());
}

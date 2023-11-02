/*
 * Copyright (C), 2006-2013, Денис Мосягин
 *
 * Список заявок
 *
 *  ert   22/04/2013   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "DocImpl.h"
#include "FormEntries.h"
#include "PictButton.h"

#include <Preference.h>
#include "AppBaseForm.h"
#include "BaseDialog.h"

#include "MainFrame.h"
#include "Qty.h"

//#include "syslib.h"

#include "DocType.h"

struct CtrlDocListItem : public IReflectableData
{
   wchar_t* item;
   long qty;

   DECLARE_TYPE_REFLECTION(CtrlDocListItem)
};

BEGIN_TYPE_REFLECTION(CtrlDocListItem)
   REGISTER_STRING_MEMBER(CtrlDocListItem, item)
   REGISTER_LONG_SCALE_MEMBER2(CtrlDocListItem, qty, QTY_SCALE, false)
END_TYPE_REFLECTION(CtrlDocListItem)

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left,  /*L"Название"*/IDS_PRICE, NULL, L"item", 150 },
   { ListFormData::Header::Right, /*L"Кол-во"*/IDS_QTY_HEAD, NULL,   L"qty", 50 },
};

struct DocCtrlItem
{
   std::wstring name;
   DWORD qty;
};

class CtrlDocListData : public ListFormData
{
public:
   CtrlDocListData();
   ~CtrlDocListData();

   virtual const Header *GetHeader() const { return header; }
   virtual int ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }

   virtual const DataReflector& DataType() const { return CtrlDocListItem().GetType(); }
   virtual int Count() const { return items.size(); }

   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }
   virtual void Clear() {}

   virtual bool Editing(int index) { return Selecting(index); }

   virtual bool Selecting(int index);

   virtual bool Get(IReflectableData* data, int index) const;
   void RefreshItems();

   bool Sending();

protected:
   std::map<std::wstring, DocCtrlItem> items;
   ControlDocImpl doc;
};

class CtrlDocListForm : public ListForm
{
public:
   CtrlDocListForm() {}

   virtual bool SetData(IFormData *_data) { return SetDataEx(_data, 2); }

   BEGIN_MSG_MAP(DocumentForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
      COMMAND_ID_HANDLER(IDOK, OnReturn)
      COMMAND_ID_HANDLER(IDC_F4, OnF4)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   virtual DWORD GetResourceID() const { return IDD_CTRL_DOC_LIST; }
   virtual DWORD GetMenuID() const { return -1; }

   virtual void UpdateLayout(const RECT& bounds, bool forceRecalc);

   DECLARE_FORM(CtrlDocListForm, IDD_CTRL_DOC_LIST)

   LRESULT OnF4(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      OpenOrderList();
      return 0;
   }

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnReturn(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      int index = listCtrl.GetSelectedIndex();
      if( index >= 0 && ((ListFormData*)data)->Selecting(index) )
         Refresh();
      return 0;
   }

   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      ((CtrlDocListData*)data)->Sending();
      return 0;
   }

protected:
   bool SetDataEx(IFormData *_data, int scale);
};

IMPLEMENT_FORM(CtrlDocListForm);

//
//----------------------------------- CtrlDocListData ----------------------------------
//
CtrlDocListData::CtrlDocListData()
{
   std::vector<ROWID> rids;
   SQLTable t(doc.Name());
   t.RIDList(&rids, L"WHERE params=0");

   if( rids.size() > 0 )
   {
      doc.Read(rids.front());

      RefreshItems();
   }
}

CtrlDocListData::~CtrlDocListData()
{
}

void CtrlDocListData::RefreshItems()
{
   items.clear();

   PriceImpl price;
   vector_t<DocItem>::const_iterator i = doc.items.begin();
   for( ; i != doc.items.end(); i++ )
   {
      std::map<std::wstring, DocCtrlItem>::iterator fnd = items.find(i->id);
      if( fnd == items.end() )
      {
         DocCtrlItem di;
         price.id = i->id;
         if( price.Read(false) )
            di.name = price.name;
         else
         {
            di.name = L"Код ";
            di.name += i->id;
         }
         di.qty = i->qty;
         items[i->id] = di;
      } else
         fnd->second.qty += i->qty;
   }
}

bool CtrlDocListData::Sending()
{
	Log("Try send doc with %d items", doc.items.size());

   const class DocType* dt = docTypeManager.GetDocType(dtOrder);
   int rc = _Module.SendDocument(&doc, dt);
   
   doc.Read(doc.rid, false);
   return (rc > 0);
}

bool CtrlDocListData::Selecting(int index)
{
   if( index < 0 || !doc.IsDirty() )
      return false;

   std::map<std::wstring, DocCtrlItem>::const_iterator i = items.begin();
   while( i != items.end() )
   {
      if( index == 0 )
         break;
      index--;
      i++;
   }

   if( i == items.end() )
      return false;

   bool ret = false;
   HWND hWnd = GetFocus();
   CQTYDialog dlg(i->second.name.c_str(), i->second.qty);
   if( dlg.DoModal() == IDOK )
   {
      int qty = dlg.GetQty();
      if( qty != 0 || MessageBox(NULL, L"Удалить товар из документа?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
      {
         doc.RemoveItems(i->first.c_str());
         if( qty > 0 )
         {
            DocItem di;
            di.id = (wchar_t*)doc.holder.Add(i->first.c_str());
            di.qty = qty;
            di.order = 10000;
            doc.items.push_back(di);
         }
         doc.Write();
         RefreshItems();
         ret = true;
      }
   }
   SetFocus(hWnd);

   return ret;
}

bool CtrlDocListData::Get(IReflectableData* data, int index) const
{
   if( index < 0 )
      return false;

   std::map<std::wstring, DocCtrlItem>::const_iterator i = items.begin();
   while( i != items.end() )
   {
      if( index == 0 )
         break;
      index--;
      i++;
   }

   if( i == items.end() )
      return false;

   ((CtrlDocListItem*)data)->item = (wchar_t*)i->second.name.c_str();
   ((CtrlDocListItem*)data)->qty = i->second.qty;

   return true;
}

//
//-------------------------------- CtrlDocListForm --------------------------------
//
bool CtrlDocListForm::SetDataEx(IFormData *_data, int scale)
{
   if( !ListForm::SetDataEx(_data, scale) )
      return false;
   
   SetFKey(VK_F4, FALSE);
   return true;
}

LRESULT CtrlDocListForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   SetFKey(VK_F4, TRUE);
   OpenMainForm(); 
   return 0;
}

void CtrlDocListForm::UpdateLayout(const RECT& bounds, bool forceRecalc)
{
   CRect rc;

   CWindow btn(GetDlgItem(IDC_SEND));
   btn.GetWindowRect(rc);
   ScreenToClient(rc);

   int diff = bounds.right - rc.right - 2;
   rc.OffsetRect(diff, 0);
   btn.MoveWindow(rc);

   SetListLayout(forceRecalc, rc.bottom + 2, bounds.bottom - rc.bottom + 3);
}

void OpenCtrlDocList()
{
   CtrlDocListData *pfd = new CtrlDocListData();
   _Module.GetFrame()->Load(IDD_CTRL_DOC_LIST, pfd);
}
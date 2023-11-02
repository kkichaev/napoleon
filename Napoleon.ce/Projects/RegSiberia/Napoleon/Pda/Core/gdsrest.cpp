/*
 * Copyright (C), 2006-2011, Денис Мосягин
 *
 * Регион Сибирь add-in
 *
 *  ert   17/03/2011   creating
 */ 
#include "stdafx.h"
#include <Exchange.h>
#include <ObjImpl.h>
#include <Module.h>

#include "Add.h"
#include <NapoleonRes.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <PriceForm.h>
#include <FormEntries.h>

struct GoodsFormItem : public IReflectableData
{
   wchar_t *name;

   DECLARE_TYPE_REFLECTION(GoodsFormItem)
};

struct GoodsFormData : public ListFormData 
{
   GoodsFormData(GoodsRestImpl* doc, bool retToDocList);
   ~GoodsFormData();

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;

   virtual int Count() const { return items.size(); }
   virtual bool Get(IReflectableData* data, int index) const;
   virtual const DataReflector& DataType() const;
   virtual bool Selecting(int index);
   virtual bool Editing(int index) { return Selecting(index); }

   void RefreshItems();

   GoodsRestImpl* doc;
   bool retToDocList;

   std::vector<std::wstring> items;
   mutable PriceImpl price;
};

class GoodsForm : public ListForm
{
public:

   virtual bool SetData(IFormData *_data);
   virtual void LoadMenuBar(bool hideSIP);
   virtual void UpdateLayout(bool forceRecalc);

   virtual DWORD GetResourceID() const { return IDD_GOODS_REST; }
   virtual DWORD GetMenuBarID() const { return IDD_GOODS_REST; }

   DECLARE_FORM(GoodsForm, IDD_GOODS_REST)

   BEGIN_MSG_MAP(GoodsForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_ADD, Adding)
      COMMAND_ID_HANDLER(IDC_SEND, SendDoc)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Adding(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT SendDoc(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
};

IMPLEMENT_FORM(GoodsForm)

BEGIN_TYPE_REFLECTION(GoodsFormItem)
   REGISTER_STRING_MEMBER(GoodsFormItem, name)
END_TYPE_REFLECTION(GoodsFormItem)

BEGIN_TYPE_REFLECTION(GoodsRestItem)
   REGISTER_STRING_MEMBER(GoodsRestItem, id)
   REGISTER_USHORT_MEMBER(GoodsRestItem, party)
   REGISTER_ULONG_SCALE_MEMBER2(GoodsRestItem, qty, QTY_SCALE, false)
END_TYPE_REFLECTION(GoodsRestItem)

BEGIN_TYPE_REFLECTION(GoodsRest)
   REGISTER_STRING_MEMBER(GoodsRest, id)
   REGISTER_TIMESTAMP_MEMBER(GoodsRest, date)
   REGISTER_ULONG_MEMBER(GoodsRest, params)
   REGISTER_STRING_MEMBER(GoodsRest, remark)
   REGISTER_COLLECTION_MEMBER(GoodsRest, items, GoodsRestItem)
END_TYPE_REFLECTION(GoodsRest)

const wchar_t* GoodsRestImpl::Description() const
{
   return (params & ofProceeded) ? L"в обработке" : (params & ofExported) ? L"отправлен" : L"";
}

bool GoodsRestImpl::CreateDocument(const ROWID &orgID)
{
   if( !Init(orgID) ) return false;

   OpenGoodsPrice(this, true);
   return true;
}

WORD GetNearestDeliveryDateOffset(const SYSTEMTIME& st)
{
   switch(st.wDayOfWeek)
   {
   case 0:
      return 2;
   case 1:
      return 3;
   case 2:
      return 1;
   case 3:
      return 2;
   case 4:
      return 1;
   case 5:
      return 2;
   case 6:
      return 1;
   }
   return 1; //?
}

void GoodsRestImpl::GetItemInfo(std::vector<GoodsItemInfo> *res, const wchar_t* id)
{
   res->clear();

   SYSTEMTIME st;
   FileTimeToSystemTime(&date, &st);

   WORD party = 0;
   for( int i=0; i < 6; i++ )
   {
      GoodsItemInfo item;
      WORD d = GetNearestDeliveryDateOffset(st);
      party += d;
      
      item.party = party;
      item.qty = GetQty(id, party);
      SystemTimeToFileTime(&st, &item.date);

      *((__int64*)&item.date) -= (__int64)d * 24 * 3600 * 10000000;

      res->push_back(item);
      FileTimeToSystemTime(&item.date, &st);
   }
}

void GoodsRestImpl::UpdateItems(const std::vector<GoodsItemInfo> &res, const wchar_t* id)
{
   // find element
   vector_t<GoodsRestItem>::iterator i = items.begin();
   for( ; i != items.end(); i++ )
      if( wcscmp(i->id, id) == 0 )
         break;

   // remove previous
   while( i != items.end() && wcscmp(i->id, id) == 0 )
      i = items.erase(i);

   // add new
   std::vector<GoodsItemInfo>::const_iterator ii = res.begin();
   id = holder.Add(id);
   for( ; ii != res.end(); ii++ )
   {
      if( ii->qty == 0 ) continue;

      GoodsRestItem item;
      item.id = (wchar_t*)id;
      item.party = ii->party;
      item.qty = ii->qty;

      i = items.insert(i, item);
      i++;
   }

   Write();
}

DWORD GoodsRestImpl::GetQty(const wchar_t* id, WORD party) const
{
   vector_t<GoodsRestItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      if( i->party == party && wcscmp(i->id, id) == 0 )
         return i->qty;
   }

   return 0;
}

bool GoodsRestImpl::EditItemData(const wchar_t* id)
{
   EditGoodsItem dlg(this, id);
   return (dlg.DoModal() == IDOK);
}

bool GoodsRestImpl::CanRemove() const
{
   bool needDelete = false;
   if( !IsDirty() )
   {
      int id = MessageBox(GetActiveWindow(), L"Удалить документ?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION);
      if( id == IDYES )
         needDelete = true;
   } else
   {
      int id = MessageBox(GetActiveWindow(), L"ВНИМАНИЕ!\nДокумент не передан на компьютер\nУдалить документ?", 
         L"Подтверждение", MB_YESNO|MB_ICONQUESTION);

      if( id == IDYES )
         needDelete = true;
   }

   return needDelete;
}

bool GoodsRestImpl::ClearDirty(SQLTable *updateTable, bool reverse)
{
   const wchar_t *updStr = L"params";
   if( reverse )
   {
      if( params & ofExported ) params &= (~ofExported);
      else params |= ofExported;
   } else
   {
      params |= ofExported;
   }

   return (updateTable == NULL) ? true : updateTable->Update(*this, updStr, rid);
}

bool GoodsRestImpl::Init(const ROWID &orgID)
{
   OrgImpl org;
   org.Read(orgID);

   _Module.GetLocalTime(&date);

   params = 0;
   remark = L"";

   id = holder.Add(org.id);
   rid = NO_ROWID;

   return true;
}

void GoodsRestImpl::EditDocument(UINT retForm)
{
   OpenGoods(this, (retForm != IDD_ORDER_LIST));
}

bool GoodsRestImpl::HaveItem(const wchar_t* id) const
{
   vector_t<GoodsRestItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
      if( wcscmp(i->id, id) == 0 )
         return true;

   return false;
}

GoodsFormData::GoodsFormData(GoodsRestImpl* doc, bool retToDocList)
{
   this->doc = doc;
   this->retToDocList = retToDocList;

   RefreshItems();
}

bool GoodsFormData::Selecting(int index)
{
   if( index >= 0 && index < (int)items.size() )
   {
      EditGoodsItem dlg(doc, items[index].c_str());
      if( dlg.DoModal() == IDOK )
      {
         RefreshItems();
         return true;
      }
   }

   return false;
}

void GoodsFormData::RefreshItems()
{
   std::set<std::wstring> ti;
   vector_t<GoodsRestItem>::const_iterator i = doc->items.begin();
   
   items.clear();
   for( ; i != doc->items.end(); i++ )
   {
      std::wstring id(i->id);
      if( ti.find(i->id) != ti.end() )
         continue;

      items.push_back(id);
      ti.insert(id);
   }
}

GoodsFormData::~GoodsFormData()
{
   delete doc;
}

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Название", L"name", 100 },
};

const ListFormData::Header *GoodsFormData::GetHeader() const
{
   return header;
}

int GoodsFormData::ColumnsCount() const
{
   return sizeof(header)/sizeof(header[0]);
}

bool GoodsFormData::Get(IReflectableData* data, int index) const
{
   if( index < 0 || index >= (int)items.size() ) return false;

   price.id = (wchar_t*)items[index].c_str();
   price.Read();
   ((GoodsFormItem*)data)->name = price.name;

   return true;
}

const DataReflector& GoodsFormData::DataType() const
{
   return GoodsFormItem().GetType();
}

bool GoodsForm::SetData(IFormData *_data)
{
   if( ListForm::SetDataEx(_data, 2) == false )
      return false;

   LoadMenuBar(true);
   
   OrgImpl org;
   org.id = (wchar_t*)((GoodsFormData*)_data)->doc->id;
   org.Read();
   GetDlgItem(IDC_ORG_TITLE).SetWindowTextW(org.name);

   UpdateLayout(false);

   if( ((GoodsFormData*)data)->doc->params & ofExported )
      menuBar.EnableButton(IDC_ADD, FALSE);

   return true;
}

void GoodsForm::UpdateLayout(bool forceRecalc)
{
   CRect bounds, rcTitle;
   CStatic title(GetDlgItem(IDC_ORG_TITLE));

   title.GetWindowRect(&rcTitle);
   GetClientRect(bounds);
   ScreenToClient(rcTitle);

   int listHeight = bounds.Height() - rcTitle.bottom;

   SetListLayout(forceRecalc, rcTitle.bottom, listHeight);   
   title.SetWindowPos(NULL, 0, 0, bounds.right, rcTitle.Height(), SWP_NOZORDER|SWP_NOOWNERZORDER);
}

void GoodsForm::LoadMenuBar(bool hideSIP)
{
   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, (hideSIP) ? SHCMBF_HIDESIPBUTTON : 0));
}

LRESULT GoodsForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( !CreateNextDoc(((GoodsFormData*)data)->doc->id) )
   {
      if( ((GoodsFormData*)data)->retToDocList )
         OpenOrgDocs(((GoodsFormData*)data)->doc->id, dtGoodsRest);
      else
         OpenListDoc(dtGoodsRest);
   }

   return 0;
}

LRESULT GoodsForm::Adding(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   GoodsRestImpl* doc = ((GoodsFormData*)data)->doc;
   ((GoodsFormData*)data)->doc = NULL;
   OpenGoodsPrice(doc, ((GoodsFormData*)data)->retToDocList);
   return 0;
}

LRESULT GoodsForm::SendDoc(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( SendDocument(((GoodsFormData*)data)->doc, docTypeManager.GetDocType(dtGoodsRest), L"Документа отправлен") )
   {
      ((GoodsFormData*)data)->doc->ClearDirty(NULL, false);
      menuBar.EnableButton(IDC_ADD, FALSE);
   }

   return 0;
}

void OpenGoods(GoodsRestImpl* doc, bool retToDocList)
{
   _Module.GetFrame()->Load(IDD_GOODS_REST, new GoodsFormData(doc, retToDocList));
}

/*
* Copyright (C), 2007-2012, Денис Мосягин
*
* Остатки
*
*  ert   28/04/2012   creating
*/
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include <Exchange.h>

#include "FormEntries.h"
#include <NapoleonRes.h>

#include <ListForm.h>
#include "ObjImpl.h"
#include <NplConfig.h>
#include <BaseDialog.h>
#include <EnterNumber.h>
#include "RestOut.h"

typedef EnterNumberT<IDD_ENTER_VALUE, 1, true> EnterQty;

struct RfrItem : public IReflectableData
{
   wchar_t *name;
   int qty;
   int plan;
   int order;
   DECLARE_TYPE_REFLECTION(RfrItem)
};

BEGIN_TYPE_REFLECTION(RestInItem)
   REGISTER_ULONG_MEMBER(RestInItem, plan)
   REGISTER_STRING_MEMBER(RestInItem, id)
END_TYPE_REFLECTION(RestInItem)

BEGIN_TYPE_REFLECTION(RestIn)
   REGISTER_STRING_MEMBER(RestIn, id)
   REGISTER_COLLECTION_MEMBER(RestIn, items, RestInItem)
END_TYPE_REFLECTION(RestIn)

BEGIN_TYPE_REFLECTION(RestOutItem)
   REGISTER_ULONG_MEMBER(RestOutItem, plan)
   REGISTER_ULONG_MEMBER(RestOutItem, qty)
   REGISTER_ULONG_MEMBER(RestOutItem, order)
   REGISTER_STRING_MEMBER(RestOutItem, id)
END_TYPE_REFLECTION(RestOutItem)

BEGIN_TYPE_REFLECTION(RestOut)
   REGISTER_STRING_MEMBER(RestOut, id)
   REGISTER_TIMESTAMP_MEMBER(RestOut, created)
   REGISTER_TIMESTAMP_MEMBER(RestOut, date)
   REGISTER_ULONG_MEMBER(RestOut, flags)
   REGISTER_COLLECTION_MEMBER(RestOut, items, RestOutItem)
END_TYPE_REFLECTION(RestOut)

BEGIN_TYPE_REFLECTION(RfrItem)
   REGISTER_STRING_MEMBER(RfrItem, name)
   REGISTER_LONG_SCALE_MEMBER2(RfrItem, plan, 1, true)
   REGISTER_LONG_SCALE_MEMBER2(RfrItem, qty, 1, true)
   REGISTER_LONG_SCALE_MEMBER2(RfrItem, order, 1, true)
END_TYPE_REFLECTION(RfrItem)

static ListFormData::Header docHeader[] = 
{
   { ListFormData::Header::Left, L"Папка", L"name", 50 },
   { ListFormData::Header::Right, L"План", L"plan", 15 },
   { ListFormData::Header::Right, L"Кол-во", L"qty", 15 },
   { ListFormData::Header::Right, L"Заказ", L"order", 15 },
};

struct DocData : public ListFormData
{
   DocData(RestOutImpl *doc, bool rdl) { this->doc = doc; retToDocList = rdl; }
   ~DocData() { delete doc; }

   virtual int Count() const { return doc->items.size(); }
   virtual bool Get(IReflectableData* data, int index) const;
   virtual const Header *GetHeader() const { return docHeader; }
   virtual int ColumnsCount() const { return sizeof(docHeader)/sizeof(docHeader[0]); }

   virtual const DataReflector& DataType() const { return RfrItem().GetType(); }

   void Send();

   bool SetQty(int index);
   bool SetOrder(int index);

   RestOutImpl *doc;
   bool retToDocList;

   mutable std::wstring text;
};

class DocForm : public ListForm
{
public:
   DocForm() {}

   virtual bool SetData(IFormData *_data);

   BEGIN_MSG_MAP(DocForm)
      NOTIFY_CODE_HANDLER_EX(NM_CLICK, DoSelect)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   DECLARE_FORM(DocForm, IDD_REST_OUT)

   virtual DWORD GetMenuID() const { return -1; }
   virtual DWORD GetResourceID() const { return IDD_REST_OUT; }
   virtual DWORD GetMenuBarID() const { return IDD_REST_OUT; }

protected:
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
   LRESULT DoSelect(LPNMHDR hdr);

protected:
};

IMPLEMENT_FORM(DocForm);

bool DocData::Get(IReflectableData* data, int index) const
{
   if( index >= (int)doc->items.size() )
      return false;

   const RestOutItem& i = doc->items.at(index);

   FolderImpl fi;
   fi.fid = i.id;

   SQLTable table(fi.Name());
   std::wstring sql = L"where fid='"; sql += i.id; sql += L"'";
   
   text.clear();
   if( table.Select(&fi, sql.c_str()) )
      text = fi.name;
   
   ((RfrItem*)data)->name = (wchar_t*)text.c_str();
   ((RfrItem*)data)->plan = i.plan;
   ((RfrItem*)data)->qty = i.qty;
   ((RfrItem*)data)->order = i.order;

   return true;
}

void DocData::Send()
{
   if(SendDocument(doc, docTypeManager.GetDocType(dtRestOut)))
   {
      doc->flags |= ofExported;
      doc->Write();
   }
}

bool DocData::SetQty(int index)
{
   if( doc->IsDirty() == false )
      return false;

   if( index >= (int)doc->items.size() )
      return false;

   RestOutItem& i = doc->items.at(index);

   EnterQty eq;
   eq.title = L"Введите количество";
   eq.value = i.qty;
   if( eq.DoModal() == IDOK )
   {
      i.qty = eq.value;
      doc->Write();
   }
   return true;
}

bool DocData::SetOrder(int index)
{
   if( doc->IsDirty() == false )
      return false;

   if( index >= (int)doc->items.size() )
      return false;

   RestOutItem& i = doc->items.at(index);

   EnterQty eq;
   eq.title = L"Введите заказ";
   eq.value = i.order;
   if( eq.DoModal() == IDOK )
   {
      i.order = eq.value;
      doc->Write();
   }
   return true;
}

bool DocForm::SetData(IFormData *_data)
{
  if( ListForm::SetDataEx(_data, 2) == false )
      return false;

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID()));

   SetListLayout(false);
   return true;
}

LRESULT DocForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   if( !CreateNextDoc(((DocData*)data)->doc->id) )
   {
      if( ((DocData*)data)->retToDocList )
         OpenListDoc();
      else
         OpenOrgDocs(((DocData*)data)->doc->id, dtRestOut);
   }
   return 0;
}

LRESULT DocForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   ((DocData*)data)->Send();
   return 0;
}

LRESULT DocForm::DoSelect(LPNMHDR hdr)
{
   bool invalidate = false;
   NMLISTVIEW* lh = (NMLISTVIEW*)hdr;
   if( lh->iSubItem == 2 )
      invalidate = ((DocData*)data)->SetQty(lh->iItem);
   else if( lh->iSubItem == 3 )
      invalidate = ((DocData*)data)->SetOrder(lh->iItem);
   
   if( invalidate )
      Refresh();

   return 0;
}

void OpenRestOut(RestOutImpl *doc, bool retToDocList)
{
   DocData *data = new DocData(doc, retToDocList);
   _Module.GetFrame()->Load(IDD_REST_OUT, data);
}

//
//------------------- RestOutImpl --------------------------
//
const wchar_t* RestOutImpl::Description() const
{
   return (flags & ofExported) ? L"отправлен" : L"";
}

void RestOutImpl::EditDocument(UINT retForm)
{
   OpenRestOut(this, (retForm == IDD_ORDER_LIST));
}

bool RestOutImpl::Init(const ROWID &orgID)
{
   OrgImpl org;
   org.Read(orgID);
   id = holder.Add(org.id);

   RestInImpl ri;
   ri.id = id;
   if( !ri.Read() )
      return false;

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &created);
   date = created;
   flags = 0;

   vector_t<RestInItem>::const_iterator i = ri.items.begin();
   for( ; i != ri.items.end(); i++ )
   {
      RestOutItem ro;
      ro.id = holder.Add(i->id);
      ro.plan = i->plan;
      ro.qty = 0;
      ro.order = 0;

      items.push_back(ro);
   }
   Write();
   return true;
}

bool RestOutImpl::CreateDocument(const ROWID &orgID)
{
   if( !Init(orgID) )
      return false;

   OpenRestOut(this, false);
   return true;
}

bool RestOutImpl::ClearDirty(SQLTable *updateTable, bool reverse)
{
   //if( rid == NO_ROWID ) return false;

   if( reverse )
   {
      if( flags & ofExported ) flags &= (~ofExported);
      else flags |= ofExported;
   } else
      flags |= ofExported;
   return (updateTable == NULL) ? true : updateTable->Update(*this, L"flags", rid);
}

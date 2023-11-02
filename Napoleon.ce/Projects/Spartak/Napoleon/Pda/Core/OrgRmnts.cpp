/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Загрузчик типов докуменов дополнительный
 *
 *  ert   15/03/2008   creating
 */
#include "stdafx.h"
#include <Exchange.h>

#include <DocType.h>
#include <OrgRmnts.h>
#include "OrgDocs.h"
#include "PriceForm.h"
#include "FormEntries.h"
#include "Qty.h"
#include <OrgRmnts.h>
#include <StdFuncs.h>

#include <set>

class RemnantsQty : public CQTYDialog
{
public:
   RemnantsQty(QTYData *_data) : CQTYDialog(_data) {}

   virtual void CheckQty() {}
};

void OpenRemnantsPrice(OrgRemnantsImpl *remnants);
bool SetRemnantsQty(OrgRemnantsImpl *remnants, const PriceImpl &priceItem);

struct OrgRemnantsListItem : public IReflectableData
{
   const wchar_t *name;
   DWORD qty;

   DECLARE_TYPE_REFLECTION(OrgRemnantsListItem)
};

struct RemnantsPriceData : public PriceFormData
{
   RemnantsPriceData(OrgRemnantsImpl *remnants);
   RemnantsPriceData(OrgRemnantsImpl *remnants, const ROWID& upFolder);
   ~RemnantsPriceData() { delete remnants; }

   virtual PriceBaseData* Clone()
   {
      OrgRemnantsImpl *rm =  UnbindRemnants();
      return new RemnantsPriceData(rm);
   }

   virtual int ColumnsCount() const { return 1; }
   virtual bool IsItemMarked(int index) const;
   virtual bool SelectLeaf(int index);

   virtual COLORREF GetItemColor(int index) const
   {
      if( index < (int)leafs.size() )
      {
         priceItem.Read(leafs[index]);
         if( remnants->FindItem(priceItem.id) != NULL)
            return selectColor;
      }
      return textColor;
   }

   virtual void LoadTree();
   virtual void LoadFolderData(const TreeNode& folder);

   const wchar_t* OrgID() const { return remnants->id; }
   const ROWID& OID() const { return remnants->RID(); }

   OrgRemnantsImpl* UnbindRemnants() { OrgRemnantsImpl *r = remnants; remnants = NULL; return r; }

protected:
   OrgRemnantsImpl *remnants;
};

class RemnantsPriceForm : public PriceForm
{
public:
   RemnantsPriceForm();

   DWORD GetMenuBarID() const { return IDD_PRICE_LIST; }
   DWORD GetMenuID() const { return IDD_PRICE_LIST; }

   DECLARE_FORM(RemnantsPriceForm, IDD_REMNANTS_PRICE)

   virtual bool SetData(IFormData *_data);

   virtual LRESULT Closing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
};

class OrgRemnantsForm : public ListForm
{
public:
   OrgRemnantsForm();
   virtual bool SetData(IFormData *_data);
   virtual void UpdateLayout(bool forceRecalc);

   virtual DWORD GetMenuID() const { return IDR_ADD_REMOVE; }
   virtual DWORD GetResourceID() const { return IDD_INVOICE; }
   virtual DWORD GetMenuBarID() const { return IDD_ORG_REMNANTS; }

   BEGIN_MSG_MAP(OrgRemnantsForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   DECLARE_FORM(OrgRemnantsForm, IDD_ORG_REMNANTS)

protected:
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
};

class OrgRemnantsData : public ListFormData
{
public:
   OrgRemnantsData(OrgRemnantsImpl *r, bool retToOrgDocs);
   ~OrgRemnantsData() { delete remnants; }

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;

   virtual const DataReflector& DataType() const { return OrgRemnantsListItem().GetType(); }
   virtual int Count() const { return remnants->items.size(); }
   virtual bool Get(IReflectableData* data, int index) const;

   virtual bool Adding();
   virtual bool Removing(int index);
   virtual bool Selecting(int index);
   virtual bool Editing(int index) { return Selecting(index); }

   void SetFlags(WORD flags);

   const wchar_t *ID() const { return remnants->id; }

   bool retToOrgDocs;
protected:
   mutable PriceImpl p;
   OrgRemnantsImpl *remnants;
};

int DateDiff(const FILETIME &ft1, const FILETIME &ft2)
{
   __int64 val1 = ft1.dwLowDateTime | (((__int64)ft1.dwHighDateTime) << 32);
   __int64 val2 = ft2.dwLowDateTime | (((__int64)ft2.dwHighDateTime) << 32);

   int val = (int)((val1 - val2) / ((__int64)1000000 * 24 * 3600));
   return val;
}

void OrgRemnantsImpl::EditDocument(UINT retForm)
{
   OpenOrgRemnantsForm(this, (retForm != IDD_ORDER_LIST));
}

bool OrgRemnantsImpl::CreateDocument(const ROWID &orgID)
{
   OrgImpl o;
   o.Read(orgID);

   id = holder.Add(o.id);

   SYSTEMTIME st;
   GetLocalTime(&st);
   ResetTime(&st);

   SystemTimeToFileTime(&st, &date);

   flags = 0;

   OpenRemnantsPrice(this);
   return true;
}

IDocument* OrgRemnantsImpl::Copy()
{
   if( rid == NO_ROWID ) return NULL;

   OrgRemnantsImpl *rmts = new OrgRemnantsImpl();

   rmts->Read(rid);

   SYSTEMTIME st;
   GetLocalTime(&st);
   ResetTime(&st);
   SystemTimeToFileTime(&st, &rmts->date);

   rmts->flags = 0;
   rmts->rid = NO_ROWID;
   rmts->Write();

   return rmts;
}

const wchar_t* OrgRemnantsImpl::Description() const
{
   return (flags & orfDirty) ? L"" : L"отправлен";
}

bool OrgRemnantsImpl::CanRemove() const
{
   return (MessageBox(GetActiveWindow(), L"Удалить остатки?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION) == IDYES);
}

bool OrgRemnantsImpl::ClearDirty(SQLTable *updateTable, bool reverse)
{
   //if( rid == NO_ROWID ) return false;

   if( reverse )
   {
      if( flags & orfDirty ) flags &= (~orfDirty);
      else flags |= orfDirty;
   } else
      flags &= (~orfDirty);

   return (updateTable == NULL) ? true : updateTable->Update(*this, L"flags", rid);
}

void OrgRemnantsImpl::Update(const wchar_t* id, DWORD qty)
{
   bool finded = false;
   vector_t<OrgRemnantsItem>::iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      if( wcscmp(i->id, id) == 0 )
      {
         if( qty != 0 ) i->qty = qty;
         else items.erase(i);
         finded = true;
         break;
      }
   }

   if( !finded && qty )
   {
      OrgRemnantsItem item;
      item.id = holder.Add(id);
      item.qty = qty;

      items.push_back(item);
   }

   flags |= orfDirty;
   Write();
}

void OrgRemnantsImpl::GetItemData(DWORD *qty, DWORD *vistDataBefore, const wchar_t *orgID, const wchar_t* itemID)
{
   SYSTEMTIME st;
   FILETIME ft;
   GetLocalTime(&st);

   ResetTime(&st);
   SystemTimeToFileTime(&st, &ft);

   *vistDataBefore = 0;
   *qty = 0;

   DocumentList *dl;
   const DocType *doctype = docTypeManager.GetDocType(dtRemnants);
   if( doctype->GetDocuments(orgID, &dl) )
   {
      unsigned i = 0;
      for( ; i != dl->Count(); i++ )
      {
         const OrgRemnantsImpl &ri = *(const OrgRemnantsImpl*)(dl->Get(i)->Data());
         if( CompareFileTime(&ft, &ri.date) > 0 )
         {
            *vistDataBefore = DateDiff(ft, ri.date);

            OrgRemnantsItem *item = ri.FindItem(itemID);
            if( item == NULL ) *qty = 0;
            else *qty = item->qty;
            break;
         }
      }
      delete dl;
   }
}

OrgRemnantsItem* OrgRemnantsImpl::FindItem(const wchar_t* id) const
{
   vector_t<OrgRemnantsItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      if( wcscmp(i->id, id) == 0 )
         return (OrgRemnantsItem*)&(*i);
   }
   return NULL;
}

IMPLEMENT_FORM(OrgRemnantsForm)

BEGIN_TYPE_REFLECTION(OrgRemnantsListItem)
   REGISTER_STRING_MEMBER(OrgRemnantsListItem, name)
   REGISTER_ULONG_SCALE_MEMBER2(OrgRemnantsListItem, qty, QTY_SCALE, true)
END_TYPE_REFLECTION(OrgRemnantsListItem)

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Название", L"name", 90 },
   { ListFormData::Header::Right, L"Кол-во", L"qty", 10 },
};

OrgRemnantsData::OrgRemnantsData(OrgRemnantsImpl *r, bool rod)
{
   retToOrgDocs = rod;
   remnants = r;
}

const ListFormData::Header *OrgRemnantsData::GetHeader() const { return header; }
int OrgRemnantsData::ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }

bool OrgRemnantsData::Adding()
{
   OrgRemnantsImpl *r = remnants;
   remnants = NULL;
   OpenRemnantsPrice(r);
   return false;
}

bool OrgRemnantsData::Removing(int index)
{
   if( (unsigned)index >= remnants->items.size() )
      return false;

   if( MessageBox(GetActiveWindow(), L"Удалить позицию из остатков?", L"Вопрос", MB_YESNO|MB_ICONQUESTION) == IDYES )
   {
      remnants->Update(remnants->items[index].id, 0);
      return true;
   }

   return false;
}

bool OrgRemnantsData::Selecting(int index)
{
   if( (unsigned)index >= remnants->items.size() )
      return false;

   PriceImpl p;
   p.id = remnants->items[index].id;
   p.Read();
   return SetRemnantsQty(remnants, p);
}

void OrgRemnantsData::SetFlags(WORD flags)
{
   remnants->flags = flags;
   remnants->Write();
}

bool OrgRemnantsData::Get(IReflectableData* data, int index) const
{
   if( (unsigned)index >= remnants->items.size() )
      return false;

   const OrgRemnantsItem &item = remnants->items[index];
   p.id = item.id;
   p.Read();

   ((OrgRemnantsListItem*)data)->name = p.name;
   ((OrgRemnantsListItem*)data)->qty = item.qty;

   return true;
}

OrgRemnantsForm::OrgRemnantsForm()
{
}

LRESULT OrgRemnantsForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   if( ((OrgRemnantsData*)data)->retToOrgDocs )
      OpenOrgDocs(((OrgRemnantsData*)data)->ID(), dtRemnants);
   else
      OpenListDoc(dtRemnants);
   return 0;
}

bool OrgRemnantsForm::SetData(IFormData *_data)
{
   if( ListForm::SetData(_data) == false )
      return false;

   OrgImpl org;
   org.id = (wchar_t*)((OrgRemnantsData*)_data)->ID();
   org.Read();
   SetDlgItemText(IDC_ORG_TITLE, org.name);

   LoadMenuBar(true);
   return true;
}

void OrgRemnantsForm::UpdateLayout(bool forceRecalc)
{
   CWindow title(GetDlgItem(IDC_ORG_TITLE));

   CRect rc, bounds;
   GetClientRect(rc);
   bounds.left = 2;
   bounds.top = 2;
   bounds.bottom = 2;
   bounds.right = rc.Width() - 4;

   CalcTextHeight(title.m_hWnd, &bounds);
   title.MoveWindow(bounds, FALSE);

   SetListLayout(forceRecalc, bounds.bottom + 2);
}

void OpenOrgRemnantsForm(OrgRemnantsImpl* r, bool retToOrgDocs)
{
   _Module.GetFrame()->Load(IDD_ORG_REMNANTS, new OrgRemnantsData(r, retToOrgDocs));
}

IMPLEMENT_FORM(RemnantsPriceForm)

RemnantsPriceData::RemnantsPriceData(OrgRemnantsImpl *remnants) : PriceFormData(NULL)
{
   this->remnants = remnants;
}

RemnantsPriceData::RemnantsPriceData(OrgRemnantsImpl *remnants, const ROWID& upFolder) : PriceFormData(NULL)
{
   this->remnants = remnants;
}

void RemnantsPriceData::LoadTree()
{
}


void RemnantsPriceData::LoadFolderData(const TreeNode& folder)
{
   if( &folder == &root )
   {
      SQLTable t(priceItem.Name());
      t.RIDList(&leafs, L"WHERE (flags & 1) <>  0 ORDER BY name");
   }
}

bool RemnantsPriceData::SelectLeaf(int index)
{
   if( index >= (int)leafs.size() )
      return false;

   priceItem.Read(leafs[index]);
   return SetRemnantsQty(remnants, priceItem);
}

bool RemnantsPriceData::IsItemMarked(int index) const
{
   index -= folders.size();
   if( index < 0 || index >= (int)leafs.size() ) return false;

   priceItem.Read(leafs[index]);
   return (remnants->FindItem(priceItem.id) != NULL);
}

RemnantsPriceForm::RemnantsPriceForm()
{
}

bool RemnantsPriceForm::SetData(IFormData *_data)
{
   if( !PriceForm::SetData(_data) ) return false;

   return true;
}

LRESULT RemnantsPriceForm::Closing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenOrgRemnantsForm(((RemnantsPriceData*)data)->UnbindRemnants(), true);
   return 0;
}

bool SetRemnantsQty(OrgRemnantsImpl *remnants, const PriceImpl &pItem)
{
   QTYData qd;
   qd.id = pItem.id;
   qd.qty =  1 * QTY_SCALE;
   qd.flags = 0;
   qd.sum = 0;
   qd.cost = pItem.cost[0];
   qd.canChange = true;

   const OrgRemnantsItem *item = remnants->FindItem(qd.id.c_str());
   if( item != NULL )
      qd.qty = item->qty;

   HWND oldFocus = GetFocus();
   RemnantsQty dlg(&qd);
   int code = dlg.DoModal();
   SetFocus(oldFocus);

   bool retVal = (code == IDOK);
   if( retVal )
      remnants->Update(qd.id.c_str(), qd.qty);

   return retVal;
}

void OpenRemnantsPrice(OrgRemnantsImpl *remnants)
{
   _Module.GetFrame()->Load(IDD_REMNANTS_PRICE, 
      new RemnantsPriceData(remnants));
}

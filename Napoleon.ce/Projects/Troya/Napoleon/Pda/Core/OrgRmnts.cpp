/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Загрузчик типов докуменов дополнительный
 *
 *  ert   15/03/2008   creating
 */
#include "stdafx.h"
#include <CEInt.h>
#include <Exchange.h>
#include <Sync.h>
#include <Table.h>

#include <DocType.h>
#include <OrgRmnts.h>
#include "OrgDocs.h"
#include "Price.h"
#include "PriceForm.h"
#include "FormEntries.h"
#include "Qty.h"
#include "AutoOrder.h"

class RemnantsQty : public CQTYDialog
{
public:
   RemnantsQty(QTYData *_data) : CQTYDialog(_data) {}

   virtual void CheckQty() {}
};

bool SetRemnantsQTY(QTYData *_data);
void OpenRemnantsPrice(OrgRemnantsImpl *remnants, OrderImpl *order);
bool SetRemnantsQty(OrgRemnantsImpl *remnants, CEOID id);

struct OrgRemnantsListItem : public IReflectableData
{
   const wchar_t *name;
   DWORD qty;

   DECLARE_TYPE_REFLECTION(OrgRemnantsListItem)
};

struct RemnantsPriceData : public PriceFormData
{
   RemnantsPriceData(OrgRemnantsImpl *remnants, OrderImpl *order);
   RemnantsPriceData(OrgRemnantsImpl *remnants, OrderImpl *order, CEOID upFolder);
   ~RemnantsPriceData() { delete remnants; }

   virtual int ColumnsCount() const { return 1; }
   virtual bool IsItemMarked(int index) const;
   virtual bool SelectLeaf(int index);

   CEOID OrgID() const { return remnants->id; }
   OrderImpl *GetOrder() const { return order; }

   CEOID UpFolder() const { return (upFolders.size()) ? upFolders.back() : 0; }

   OrgRemnantsImpl* UnbindRemnants() { OrgRemnantsImpl *r = remnants; remnants = NULL; return r; }

   OrderImpl* UnbindRemnantsOrder() { OrderImpl *o = order; order = NULL; return o; }

protected:
   OrgRemnantsImpl *remnants;
   OrderImpl *order;
};

class RemnantsPriceForm : public PriceForm
{
public:
   RemnantsPriceForm();

   DWORD GetMenuBarID() const { return IDD_PRICE_LIST; }
   DWORD GetMenuID() const { return IDD_PRICE_LIST; }

   DECLARE_FORM(RemnantsPriceForm, IDD_REMNANTS_PRICE)

   virtual bool PrepareData(bool forceCreate, IProgressIndicator *pc) { return true; }

   virtual bool SetData(IFormData *_data);

   BEGIN_MSG_MAP(RemnantsPriceForm)
      COMMAND_ID_HANDLER(IDC_SHOW_2_ROW, ChangeRows)
      CHAIN_MSG_MAP(PriceForm)
   END_MSG_MAP()

   virtual LRESULT Closing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   LRESULT ChangeRows(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
};

class OrgRemnantsData : public ListFormData
{
public:
   OrgRemnantsData(CEOID oid);
   OrgRemnantsData(OrderImpl *order);

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;

   virtual const DataReflector& DataType() const { return item.GetType(); }
   virtual int Count() const { return remnants.items.size(); }
   virtual bool Get(IReflectableData* data, int index) const;

   CEOID OrgID() const { return remnants.id; }

   virtual bool Adding();
   virtual bool Removing(int index);
   virtual bool Selecting(int index);
   virtual bool Editing(int index) { return Selecting(index); }

   OrderImpl* GetOrder() const { return order; }

   void SetFlags(WORD flags);

protected:
   OrgRemnantsListItem item;
   OrgRemnantsImpl remnants;

   OrderImpl *order;
   mutable std::wstring name;
};


bool SetRemnantsQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   RemnantsQty dlg(data);
   int code = dlg.DoModal();

   SetFocus(oldFocus);
   return (code == IDOK);
}

OrgRemnantsImpl::OrgRemnantsImpl(CEOID orgID)
{
   id = orgID;

   SYSTEMTIME st;
   GetLocalTime(&st);
   ResetTime(&st);

   SystemTimeToFileTime(&st, &date);

   flags = 0;

   rmid = 0;
}

bool OrgRemnantsImpl::Read()
{
   SyncOrgRemnants so;
   CEDBFormat format(so);
   CETable table(format);

   if( table.Open(so.FileName()) || table.Create(so.FileName()) )
   {
      table.SetTag(L"id");

      CEPROPVAL prop = { 0 };
      prop.propid = format.CEType(L"id");
      prop.val.ulVal = id;

      if( (rmid = table.Seek(prop)) != 0 )
         table.GetCurrent(this);
   } else
      return false;
   
   return true;
}

void OrgRemnantsImpl::Update(CEOID id, DWORD qty)
{
   bool finded = false;
   vector_t<OrgRemnantsItem>::iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      if( i->id == id )
      {
         if( qty != 0 )
            i->qty = qty;
         else
            items.erase(i);
         finded = true;
         break;
      }
   }

   if( !finded && qty )
   {
      OrgRemnantsItem item;
      item.id = id;
      item.qty = qty;

      items.push_back(item);
   }

   flags |= orfDirty;
   Save();
}

void OrgRemnantsImpl::Save()
{
   SyncOrgRemnants sr;
   CEDBFormat format(sr);
   CETable table(format);

   if( !table.Open(sr.FileName()) && !table.Create(sr.FileName()) )
      return;

   table.SetTag(L"id");

   CEPROPVAL prop;
   prop.propid = format.CEType(L"id");
   prop.val.ulVal = id;

   CEOID oid = table.Seek(prop);
   if( items.size() == 0 )
   {
      if( oid != 0 )
      {
         if( (flags & orfDirty) == 0 )
         {
            table.RemoveRecord(oid);
            oid = 0;
         }
      } else
         rmid = table.WriteRecord(*this, oid);
   } else
      rmid = table.WriteRecord(*this, oid);
}


OrgRemnantsItem* OrgRemnantsImpl::FindItem(CEOID id) const
{
   vector_t<OrgRemnantsItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      if( i->id == id )
         return (OrgRemnantsItem*)&(*i);
   }

   return NULL;
}

bool SyncOrgRemnants::Deserialize(IReflectableData *data, const StreamReader &reader) const
{
   return false;
}

bool SyncOrgRemnants::Serialize(StreamWriter *writer, const IReflectableData &data) const
{
   CEDBFormat orgFormat(ORG_OBJ, ORG_KEY), priceFormat(PRICE_OBJ, PRICE_KEY);
   CETable orgTable(orgFormat), priceTable(priceFormat);
   if(orgTable.Open(ORG_DB) == false || priceTable.Open(PRICE_DB) == false)
      return false;

   OrgRemnantsSend dest;
   StringHolder sh;
   const OrgRemnants& src = (const OrgRemnants&)data;
   if( orgTable.Seek(src.id) )
   {
      Org org;
      orgTable.GetCurrent(&org);

      dest.id = sh.Add(org.id);
      dest.date = src.date;
      dest.flags = src.flags;

      vector_t<OrgRemnantsItem>::const_iterator i = src.items.begin();
      for( ; i!= src.items.end(); i++ )
      {
         if( priceTable.Seek(i->id) )
         {
            Price prc;
            OrgRemnantsItemSend item;

            priceTable.GetCurrent(&prc);
            item.qty = i->qty;
            item.id = sh.Add(prc.id);

            dest.items.push_back(item);
         }
      }
   } else
      return false;

   dest.GetType().Serialize(writer, dest);
   return true;
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

OrgRemnantsData::OrgRemnantsData(CEOID oid) : remnants(oid), order(NULL)
{
   remnants.Read();
}

OrgRemnantsData::OrgRemnantsData(OrderImpl *order) : remnants(order->id)
{
   this->order = order;

   if( remnants.Read() )
   {
      SYSTEMTIME st;
      FILETIME check;
      FileTimeToSystemTime(&order->created, &st);
      ResetTime(&st);
      SystemTimeToFileTime(&st, &check);

      if( CompareFileTime(&check, &remnants.date) != 0 )
      {
         remnants.date = check;
         remnants.flags = 0;
         remnants.items.clear();
         remnants.Save();
      }
   }
}

const ListFormData::Header *OrgRemnantsData::GetHeader() const { return header; }
int OrgRemnantsData::ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }

bool OrgRemnantsData::Adding()
{
   OrgRemnantsImpl *r = new OrgRemnantsImpl(remnants.id);
   *r = remnants;
   OpenRemnantsPrice(r, order);
   return false;
}

bool OrgRemnantsData::Removing(int index)
{
   if( (unsigned)index >= remnants.items.size() )
      return false;

   if( MessageBox(GetActiveWindow(), L"Удалить позицию из остатков?", L"Вопрос", MB_YESNO|MB_ICONQUESTION) == IDYES )
   {
      remnants.Update(remnants.items[index].id, 0);
      return true;
   }

   return false;
}

bool OrgRemnantsData::Selecting(int index)
{
   if( (unsigned)index >= remnants.items.size() )
      return false;

   return SetRemnantsQty(&remnants, remnants.items[index].id);
}

void OrgRemnantsData::SetFlags(WORD flags)
{
   remnants.flags = flags;
   remnants.Save();
}

bool OrgRemnantsData::Get(IReflectableData* data, int index) const
{
   if( (unsigned)index >= remnants.items.size() )
      return false;

   SyncPrice sp;
   CEDBFormat format(sp);
   CETable table(format);

   table.Open(sp.FileName());

   const OrgRemnantsItem &item = remnants.items[index];
   if( !table.Seek(item.id) )
      return false;

   Price prc;
   table.GetCurrent(&prc);
   name = prc.name;

   ((OrgRemnantsListItem*)data)->name = name.c_str();
   ((OrgRemnantsListItem*)data)->qty = item.qty;

   return true;
}

OrgRemnantsForm::OrgRemnantsForm()
{
}

LRESULT OrgRemnantsForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   OrderImpl *order = ((OrgRemnantsData*)data)->GetOrder();
   if( order == NULL )
      OpenOrgList(dtRemnants);
   else
   {
      bool openOrder = (((OrgRemnantsData*)data)->Count() != 0);
      if( !openOrder )
      {
         if( MessageBox(L"Остатки не заполнены. Продолжить?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
         {
            int ctr = 1;
            HMENU hm = CreatePopupMenu();
            wchar_t *cause[] = { L"&Внеплановый звонок", L"В&неплановый заказ", L"&Иное" };

            for( ; ctr < 4; ctr ++ )
               AppendMenu(hm, MF_STRING, ctr, cause[ctr-1]);

            CRect rc;
            GetClientRect(rc);
            ClientToScreen(rc);
            int res = TrackPopupMenuEx(hm,  TPM_RETURNCMD | TPM_BOTTOMALIGN, 
               rc.left, rc.bottom, m_hWnd, NULL);
            DestroyMenu(hm);

            if( res > 0 )
            {
               ((OrgRemnantsData*)data)->SetFlags(orfDirty|(res * 2));
               openOrder = true;
            }
         }
      }
      if( openOrder )
      {
         if( order->EditDetail() )
            order->AddFromPriceList();
         else
            OpenOrgDocs(order->id, dtOrder);
      }
   }
   return 0;
}

LRESULT OrgRemnantsForm::SetViewType(int id, LPNMHDR header, BOOL &handled)
{
   if( ((NMTOOLBAR*)header)->iItem != IDC_VIEW_TYPE ) return 0;

   const DocType *dt = SelectDocType(&menuBar, m_hWnd);
   if( dt != NULL && dt->type != dtRemnants )
      dt->OpenListForm(((OrgRemnantsData*)data)->OrgID());

   return 0;
}

DWORD OrgRemnantsForm::GetMenuBarID() const
{
   return (((OrgRemnantsData*)data)->GetOrder() == NULL) ? IDD_ORG_REMNANTS : IDD_ORG_REMNANTS_BAR; 
}

void OrgRemnantsForm::LoadMenuBar(bool hideSIP)
{
   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));

   const DocType* dt = docTypeManager.GetDocType(dtRemnants);

   TBBUTTONINFO bi;
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_TEXT;
   bi.pszText = (LPWSTR)dt->name;
   menuBar.SetButtonInfo(IDC_VIEW_TYPE, &bi);
}

bool OrgRemnantsForm::SetData(IFormData *_data)
{
   if( ListForm::SetData(_data) == false )
      return false;

   std::wstring name;
   OrgInt::GetName(&name, ((OrgRemnantsData*)_data)->OrgID());
   CStatic title(::GetDlgItem(m_hWnd, IDC_ORG_TITLE));
   title.SetWindowTextW(name.c_str());

   LoadMenuBar(true);

   RECT rc, rcTitle;
   GetParent().GetClientRect(&rc);
   title.GetWindowRect(&rcTitle);
   WORD titleHeight = (WORD)(rcTitle.bottom - rcTitle.top);

   SetWindowPos(NULL, 0, 0, rc.right, rc.bottom-rc.top, SWP_NOZORDER|SWP_NOOWNERZORDER);
   title.SetWindowPos(NULL, 0, 0, rc.right, titleHeight, SWP_NOZORDER|SWP_NOOWNERZORDER);

   SetListLayout(false, titleHeight);
   
   return true;
}

LRESULT OrgRemnantsForm::AutoOrder(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   MakeAutoOrder(((OrgRemnantsData*)data)->OrgID(), true);
   return 0;
}

void OpenOrgRemnantsForm(CEOID oid)
{
   _Module.GetFrame()->Load(IDD_ORG_REMNANTS, new OrgRemnantsData(oid));
}

void OpenOrgRemnantsForm(OrderImpl *order)
{
   _Module.GetFrame()->Load(IDD_ORG_REMNANTS, new OrgRemnantsData(order));
}

IMPLEMENT_FORM(RemnantsPriceForm)

RemnantsPriceData::RemnantsPriceData(OrgRemnantsImpl *remnants, OrderImpl *order) : PriceFormData(NULL)
{
   this->remnants = remnants;
   this->order = order;
}

RemnantsPriceData::RemnantsPriceData(OrgRemnantsImpl *remnants, OrderImpl *order, CEOID upFolder) : PriceFormData(NULL)
{
   this->remnants = remnants;
   this->order = order;


   if( upFolder != NULL )
   {
      CETable folderTable(folderFormat);
      folderTable.Open(syncFolder.FileName());
      folderTable.SetTag(L"sort", true);

      folderTable.Seek(upFolder);
      MakeUpFolders(upFolder, &folderTable);
      LoadFolder(upFolder);
   }
}

bool RemnantsPriceData::SelectLeaf(int index)
{
   if( index >= (int)leafs.size() )
      return false;

   return SetRemnantsQty(remnants, leafs[index]);
}

bool RemnantsPriceData::IsItemMarked(int index) const
{
   index -= folders.size();

   if( index < 0 || index > (int)leafs.size() ) return false;
   CEOID oid = leafs[index];

   return (remnants->FindItem(oid) != NULL);
}

RemnantsPriceForm::RemnantsPriceForm()
{
}

bool RemnantsPriceForm::SetData(IFormData *_data)
{
   if( !PriceForm::SetData(_data) ) return false;

   Preference p;
   p.Load();

   TBBUTTONINFO bi = {0};
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_IMAGE;
   bi.iImage = (p.flags & ppfPriceRow2) ? 13 : 12;
   menuBar.SetButtonInfo(IDC_SHOW_2_ROW, &bi);

   return true;
}

LRESULT RemnantsPriceForm::ChangeRows(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   Preference p;
   p.Load();

   bool orgRow2 = !((p.flags & ppfPriceRow2) != 0);
   if( orgRow2 ) p.flags |= ppfPriceRow2;
   else p.flags &= (~ppfPriceRow2);

   p.Save();

   OrderImpl *order = ((RemnantsPriceData*)data)->UnbindRemnantsOrder();
   OrgRemnantsImpl *r = ((RemnantsPriceData*)data)->UnbindRemnants();
   CEOID upFolder = ((RemnantsPriceData*)data)->UpFolder();

   RemnantsPriceData *pfd = new RemnantsPriceData(r, order, upFolder);
   _Module.GetFrame()->Load(IDD_REMNANTS_PRICE, pfd);

   return 0;
}

LRESULT RemnantsPriceForm::Closing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OrderImpl *order = ((RemnantsPriceData*)data)->GetOrder();
   if( order != NULL )
      OpenOrgRemnantsForm(order);
   else
      OpenOrgRemnantsForm(((RemnantsPriceData*)data)->OrgID());
   return 0;
}

bool SetRemnantsQty(OrgRemnantsImpl *remnants, CEOID id)
{
   Price pItem;
   SyncPrice syncPrice;
   CEDBFormat priceFormat(syncPrice);
   CETable priceTable(priceFormat);
   priceTable.Open(syncPrice.FileName());
   priceTable.Seek(id);
   priceTable.GetCurrent(&pItem);

   QTYData qd;
   qd.id = id;
   qd.qty =  1 * QTY_SCALE;
   qd.flags = 0;
   qd.sum = 0;
   qd.cost = pItem.cost[0];
   qd.canChange = true;

   const OrgRemnantsItem *item = remnants->FindItem(id);
   if( item != NULL )
      qd.qty = item->qty;

   bool retVal = SetRemnantsQTY(&qd);
   if( retVal )
      remnants->Update(id, qd.qty);

   return retVal;
}

void OpenRemnantsPrice(OrgRemnantsImpl *remnants, OrderImpl *order)
{
   _Module.GetFrame()->Load(IDD_REMNANTS_PRICE, 
      new RemnantsPriceData(remnants, order));
}

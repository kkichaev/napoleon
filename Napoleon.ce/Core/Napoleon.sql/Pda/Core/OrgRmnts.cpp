/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Загрузчик типов докуменов дополнительный
 *
 *  ert   15/03/2008   creating
 */
#include "stdafx.h"
#include "OrgRmnts.h"

#include <DocType.h>
#include "OrgDocs.h"
#include "PriceForm.h"
#include "FormEntries.h"
#include "Qty.h"
#include "OrgRmnts.h"
#include "InitDoc.h"
#include <StdFuncs.h>

#include <set>

wchar_t dtRemnants[] = L"Остатки";

struct RmntsFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new OrgRemnantsImpl(); }
   virtual void Free(IDocument* document) const { delete (OrgRemnantsImpl*)document; }
} rmntsFactory;

class RemnantsQty : public CSimpleDialog<IDD_REMNANTS_QTY, TRUE>
{
public:
   RemnantsQty(DWORD value, bool sku);
   ~RemnantsQty();

   DWORD value;
   bool sku;

   typedef CSimpleDialog<IDD_REMNANTS_QTY, TRUE> BaseClass;

   BEGIN_MSG_MAP(RemnantsQty)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_ID_HANDLER(IDOK, Close)
      COMMAND_ID_HANDLER(IDC_REST, ChangeRest)
      COMMAND_RANGE_HANDLER(IDC_DIG_0, IDC_DIG_BS, OnDigPressed)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT , WPARAM , LPARAM , BOOL& bHandled);
   LRESULT Close(WORD code, WORD id, HWND hWnd, BOOL& bHandled);
   LRESULT OnDigPressed(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
   LRESULT ChangeRest(WORD code, WORD id, HWND hWnd, BOOL& bHandled);

protected:
   void SetScalingValue(int id, int value, DWORD scale, bool hideRest);
   DWORD GetValue(int id, DWORD scale);

   void SetChildFont();
   HFONT childFont;
}; 

void OpenRemnantsPrice(OrgRemnantsImpl *remnants);
bool SetRemnantsQty(OrgRemnantsImpl *remnants, const PriceImpl &priceItem);

struct OrgRemnantsListItem : public IReflectableData
{
   const wchar_t *name;
   DWORD qty;

   DECLARE_TYPE_REFLECTION(OrgRemnantsListItem)
};

static ListFormData::Header p_header[] = 
{
   { ListFormData::Header::Left,  L"Название", L"name", 100 },
   { ListFormData::Header::Right, L"Кол-во",   L"qty", 25 },
};

struct RestPriceItem : public PriceFormItem
{
   DWORD qty;
   DECLARE_TYPE_REFLECTION(RestPriceItem)
};

BEGIN_TYPE_REFLECTION(RestPriceItem)
   REGISTER_ULONG_SCALE_MEMBER2(RestPriceItem, qty, QTY_SCALE, true)
   CHAIN_REFLECTION(RestPriceItem, PriceFormItem)
END_TYPE_REFLECTION(RestPriceItem)

struct RemnantsPriceData : public PriceFormData
{
   RemnantsPriceData(OrgRemnantsImpl *remnants);
   RemnantsPriceData(OrgRemnantsImpl *remnants, const ROWID& upFolder);

   ~RemnantsPriceData()
   {
      //ListFormData::Header* hdr = (ListFormData::Header*)GetHeader();
      //hdr[0].curWidth = 0;
      delete remnants;
   }

   virtual PriceBaseData* Clone()
   {
      OrgRemnantsImpl *rm =  UnbindRemnants();
      return new RemnantsPriceData(rm);
   }

   virtual int ColumnsCount() const { return 2; }
   virtual const Header *GetHeader() const { return p_header; }
   virtual const DataReflector& DataType() const { return RestPriceItem().GetType(); }

   virtual bool IsItemMarked(int index) const;
   virtual bool SelectLeaf(int index);

   virtual COLORREF GetItemColor(int index) const
   {
      index -= folders.size();
      if( index >= 0 && index < (int)leafs.size() )
      {
         priceItem.Read(leafs[index]);
         if( remnants->FindItem(priceItem.id) != NULL)
            return selectColor;
      }
      return textColor;
   }

   virtual bool Get(IReflectableData* data, int index) const
   {
      if( !PriceFormData::Get(data, index) )
         return false;

      int qty = 0;
      if( index >= (int)folders.size() )
      {
         OrgRemnantsItem *item = remnants->FindItem(priceItem.id);
         if( item != NULL )
            qty = item->qty;
      }
      ((RestPriceItem*)data)->qty = qty;
      return true;
   }

   virtual void UpdateHeadTitle() { }

#ifdef Kolbiko
   bool openMatrix;
   virtual void LoadMatrix()
   {
      PriceFormData::LoadMatrix();

      if( remnants == NULL )
         return;

      OrgImpl o;
      o.id = remnants->id;
      o.Read();

      if( o.matrix.size() > 0 )
      {
         Matrix m;
         m.name = L"<Ассортимент>";

         PriceImpl p;
         vector_t<MatrixItem>::const_iterator i = o.matrix.begin();
         for( ; i != o.matrix.end(); i++ )
         {
            p.id = i->id;
            p.Read();

            m.items.push_back(p.rid);
         }

         matrixes.insert(matrixes.begin()+1, m);
         openMatrix = true;
      }
   }

   virtual void SetDataDone()
   {
      if( openMatrix )
      {
         SetMatrix(1);
         owner->Refresh();
      }
   }
#endif

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
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   DECLARE_FORM(OrgRemnantsForm, IDD_ORG_REMNANTS)

protected:
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
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
   OrgRemnantsImpl* Doc() { return remnants;}

   bool retToOrgDocs;
protected:
   mutable PriceImpl p;
   OrgRemnantsImpl *remnants;
};

RemnantsQty::RemnantsQty(DWORD value, bool sku) : childFont(NULL)
{ 
   this->value = value;
   this->sku = sku;
}

RemnantsQty::~RemnantsQty()
{
   if( childFont != NULL )
      DeleteObject(childFont);
}

void RemnantsQty::SetChildFont()
{
   LOGFONT lf;
   if( GetObject(GetStockObject(SYSTEM_FONT), sizeof(lf), &lf) )
   {
      if( lf.lfHeight < 0 ) lf.lfHeight++;
      else lf.lfHeight--;
      lf.lfWeight = FW_BOLD;

      childFont = CreateFontIndirect(&lf);
      if( childFont != NULL )
      {
         CWindow child(GetWindow(GW_CHILD));

         while( child.m_hWnd != NULL )
         {
            child.SetFont(childFont);
            child = child.GetWindow(GW_HWNDNEXT);
         }
      }
   }
}

LRESULT RemnantsQty::OnInitDialog(UINT , WPARAM , LPARAM , BOOL& bHandled)
{
   bHandled = FALSE;

   SetChildFont();

   SetScalingValue(IDC_QTY, value, QTY_SCALE, true);
   if( sku )
      CheckDlgButton(IDC_REST, BST_CHECKED);

   CWindow ok(GetDlgItem(IDOK));
   CRect rc1, rc2;
   ok.GetWindowRect(rc1);
   GetDlgItem(IDC_DIG_0).GetWindowRect(rc2);
   rc1.bottom = rc2.bottom;
   ScreenToClient(rc1);
   ok.MoveWindow(rc1);

   CEdit edit(GetDlgItem(IDC_QTY));
   edit.SetSelAll(TRUE);
   return 0;
}

DWORD RemnantsQty::GetValue(int id, DWORD scale)
{
   CWindow wnd(GetDlgItem(id));

   int len = wnd.GetWindowTextLength();
   wchar_t *buf = (wchar_t*)alloca((len+1) * sizeof(buf[0]));
   wnd.GetWindowText(buf, len+1);

   wchar_t decBuf[4], sepBuf[4];
   int cch = GetLocaleInfoW(LOCALE_USER_DEFAULT, LOCALE_SDECIMAL, decBuf, sizeof(decBuf)/sizeof(decBuf[0]));
   decBuf[cch] = L'\0';
   cch = GetLocaleInfoW(LOCALE_USER_DEFAULT, LOCALE_STHOUSAND, sepBuf, sizeof(sepBuf)/sizeof(sepBuf[0]));
   sepBuf[cch] = L'\0';

   DWORD val = 0;
   DWORD sign = 1;

   if( *buf == '-' )
   {
      sign = (DWORD)-1;
      buf++;
   }

   while( *buf != L'\0' && *buf != *decBuf && *buf != L'.' )
   {
      if( *buf != *sepBuf )
         val = val * 10 + *buf - L'0';
      buf++;
   }

   val *= scale;
   if( *buf == *decBuf || *buf == L'.' )
   {
      while( *(++buf) && scale > 1 )
      {
         scale /= 10;
         val += (*buf - L'0') * scale;
      }
   }
   return val * sign;
}

void RemnantsQty::SetScalingValue(int id, int value, DWORD scale, bool hideRest)
{
   wchar_t buf[20], src[20];

   ConvertScaling(src, (long)value, scale);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % scale, scale, hideRest);
   SetDlgItemText(id, buf);
}

LRESULT RemnantsQty::Close(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
{
   bHandled = FALSE;
   value = GetValue(IDC_QTY, QTY_SCALE);
   return 0;
}

LRESULT RemnantsQty::ChangeRest(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
{
   sku = !sku;
   EndDialog(m_hWnd, IDOK);
   return 0;
}

LRESULT RemnantsQty::OnDigPressed(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   CEdit qty(GetDlgItem(IDC_QTY));
 
   int len = qty.GetWindowTextLength();
   wchar_t *buf = (wchar_t*)alloca((len+1) * sizeof(buf[0]));
   
   qty.GetWindowText(buf, len+1);
      
   if( wID == IDC_DIG_BS || wID == IDC_DIG_PT )
   { 
      int start, end;
      qty.GetSel(start, end);

      buf[len] = L'\0';
      if( wID == IDC_DIG_PT )
      {
         bool canReplace = false;
         if( wcschr(buf, L'.') == NULL ) canReplace = true;
         else
         {
            while( start < end ) if( buf[start++] == L'.' )
            {
               canReplace = true;
               break;
            }
         }

         if( canReplace ) qty.ReplaceSel(L".");
      } else
      {
         if( start != end )
            qty.ReplaceSel(L"");
         else
         {
            if( len > 0 )
            {
               wcscpy(buf+len-1, buf+len);
               qty.SetWindowText(buf);
               qty.SetSel(len,len);
            }
         }
      }
      return 0;
   }

   if( len == 1 && *buf == L'0' ) qty.SetSel(0, len);

   wchar_t dbuf[2];
   dbuf[1] = L'\0';
   switch(wID)
   {
      case IDC_DIG_0:
         *dbuf = L'0';
         break;
      case IDC_DIG_1:
         *dbuf = L'1';
         break;
      case IDC_DIG_2:
         *dbuf = L'2';
         break;
      case IDC_DIG_3:
         *dbuf = L'3';
         break;
      case IDC_DIG_4:
         *dbuf = L'4';
         break;
      case IDC_DIG_5:
         *dbuf = L'5';
         break;
      case IDC_DIG_6:
         *dbuf = L'6';
         break;
      case IDC_DIG_7:
         *dbuf = L'7';
         break;
      case IDC_DIG_8:
         *dbuf = L'8';
         break;
      case IDC_DIG_9:
         *dbuf = L'9';
         break;
   }
   qty.ReplaceSel(dbuf);
   return 0;
} 

void OrgRemnantsImpl::EditDocument(UINT retForm)
{
   if( items.size() == 0 )
      OpenRemnantsPrice(this);
   else
      OpenOrgRemnantsForm(this, (retForm != IDD_ORDER_LIST));
}

bool OrgRemnantsImpl::Init(const ROWID &orgID)
{
#ifdef GPS_POS
   if( !CheckGPSPos(L"Получить координаты?") )
      return false;

   latitude = gCurrentGPSPos.latitude;
   longitude = gCurrentGPSPos.longitude;
#endif

   OrgImpl o;
   o.Read(orgID);

   id = holder.Add(o.id);

#ifdef Kolbiko
   _Module.GetLocalTime(&date);
#else
   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &date);
#endif
   
   created = date;
   flags = 0;
   return true;
}

bool OrgRemnantsImpl::CreateDocument(const ROWID &orgID)
{
   if( !Init(orgID) )
      return false;

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
   st.wMilliseconds = 0;
   SystemTimeToFileTime(&st, &rmts->date);

   rmts->flags = 0;
   rmts->rid = NO_ROWID;
   rmts->Write();

   return rmts;
}

const wchar_t* OrgRemnantsImpl::Description() const
{
   return (flags & orfDirty || items.size() == 0) ? L"" : L"отправлен";
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

void OrgRemnantsImpl::Update(const wchar_t* id, DWORD qty, bool sku)
{
   if( !IsDirty() && items.size() )
      return;

   bool finded = false;
   bool changed = true;
   vector_t<OrgRemnantsItem>::iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      if( wcscmp(i->id, id) == 0 )
      {
         if( sku )
         {
            if( (i->flags & oriSKU) != 0 ) changed = false;
            else i->flags |= oriSKU;
         } else
         {
            if( (i->flags & oriSKU) == 0 ) changed = false;
            else i->flags &= (~oriSKU);
         }

         if( i->qty != qty )
         {
            changed = true;
            i->qty = qty;
         }

         if( qty == 0 && !sku )
         {
            changed = true;
            items.erase(i);
         }

         finded = true;
         break;
      }
   }

   if( finded && !changed )
      return;

   if( !finded && (qty || sku) )
   {
      OrgRemnantsItem item;
      item.id = holder.Add(id);
      item.qty = qty;
      item.flags = (sku) ? oriSKU : 0;

      items.push_back(item);
   }

   flags |= orfDirty;
   Write();
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

DWORD OrgRemnantsImpl::GetItemQty(const wchar_t* id)
{
   vector_t<OrgRemnantsItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      if( wcscmp(i->id, id) == 0 )
         return i->qty;
   }

   return 0;
}

void OrgRemnantsImpl::Load(const OrderImpl& order)
{
   if( Load(order.id, order.created) )
      return;

   rid = NO_ROWID;
   flags = 0;

   id = holder.Add(order.id);

   items.clear();

   date = order.created;
   //SYSTEMTIME st;
   //GetLocalTime(&st);
   //SystemTimeToFileTime(&st, &date);

#ifdef GPS_POS
   latitude = order.latitude;
   longitude = order.longitude;
#endif

#ifdef SHOW_OFF_TAKE
   Write(); // надо записать документ для корректной работы OFF_TAKE
#endif
}

bool OrgRemnantsImpl::Load(const wchar_t* id, const FILETIME& refDate)
{
   FILETIME from, till;
   SYSTEMTIME st;

   FileTimeToSystemTime(&refDate, &st);
   ResetTime(&st);
   SystemTimeToFileTime(&st, &from);

   st.wHour = 23;
   st.wMinute = 59;
   st.wSecond = 59;
   SystemTimeToFileTime(&st, &till);

   wchar_t buf[200];
   wsprintf(buf, L"WHERE id = '%s' and date >= %d%09d and date <= %d%09d", id, 
      (DWORD)((*(__int64*)&from) / 1000000000), (DWORD)((*(__int64*)&from) % 1000000000),
      (DWORD)((*(__int64*)&till) / 1000000000), (DWORD)((*(__int64*)&till) % 1000000000));

   std::vector<ROWID> rids;
   SQLTable t(Name());
   t.RIDList(&rids, buf);
   if( rids.size() > 0 )
   {
      Read(rids[0]);
      return true;
   }
   return false;
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
      remnants->Update(remnants->items[index].id, 0, false);
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
   if( !CreateNextDoc(((OrgRemnantsData*)data)->ID()) )
   {
      if( ((OrgRemnantsData*)data)->retToOrgDocs )
         OpenOrgDocs(((OrgRemnantsData*)data)->ID(), dtRemnants);
      else
         OpenListDoc(dtRemnants);
   }
   return 0;
}

LRESULT OrgRemnantsForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   OrgRemnantsImpl* doc = ((OrgRemnantsData*)data)->Doc();
   if( doc->items.size() > 0 )
   {
      if( SendDocument(doc, docTypeManager.GetDocType(dtRemnants), L"Остатки отправлены") )
      {
         doc->ClearDirty(NULL, false);
         menuBar.EnableButton(IDC_ADD, FALSE);
      }
   }
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
#ifdef Kolbiko
   openMatrix = false;
#endif
   this->remnants = remnants;
}

RemnantsPriceData::RemnantsPriceData(OrgRemnantsImpl *remnants, const ROWID& upFolder) : PriceFormData(NULL)
{
#ifdef Kolbiko
   openMatrix = false;
#endif
   this->remnants = remnants;
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

   ListFormData::Header* hdr = (ListFormData::Header*)((ListFormData*)data)->GetHeader();
   hdr[0].curWidth = 0;

   return true;
}

LRESULT RemnantsPriceForm::Closing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenOrgRemnantsForm(((RemnantsPriceData*)data)->UnbindRemnants(), true);
   return 0;
}

bool SetRemnantsQty(OrgRemnantsImpl *remnants, const PriceImpl &pItem)
{
   DWORD value = 0;
   bool sku = false;
   std::wstring id(pItem.id);
   const OrgRemnantsItem *item = remnants->FindItem(id.c_str());
   if( item != NULL )
   {
      value = item->qty;
      sku = ((item->flags & oriSKU) != 0);
   }

   HWND oldFocus = GetFocus();
   RemnantsQty dlg(value, sku);
   int code = dlg.DoModal();
   SetFocus(oldFocus);

   bool retVal = (code == IDOK && (remnants->items.size() == 0 || remnants->IsDirty()));
   if( retVal )
   {
      value = dlg.value;
      sku = dlg.sku;

      remnants->Update(id.c_str(), value, sku);
   }

   return retVal;
}

void OpenRemnantsPrice(OrgRemnantsImpl *remnants)
{
   _Module.GetFrame()->Load(IDD_REMNANTS_PRICE, 
      new RemnantsPriceData(remnants));
}

RemnantsType::RemnantsType() : DocType(dtRemnants, &rmntsFactory, 0)
{
}

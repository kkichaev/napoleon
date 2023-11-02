/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Окно прайс-листа
 *
 *  ert   18/08/2007   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "PriceForm.h"
#include "FormEntries.h"

#include <PicWindow.h>
#include <StdFuncs.h>

#include <algorithm>

#ifdef COST_MANAGER
#include "Costs.h"
#endif

IMPLEMENT_FORM(PriceForm)

//
// ------------------- Price Form Data ----------------------
//
static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left,  L"Название", L"name", 100 },
   { ListFormData::Header::Right, L"Кол-во",   L"column2", 50 },
   { ListFormData::Header::Right, L"Сумма",    L"column3", 50 }
};

const ListFormData::Header *PriceFormData::GetHeader() const
{ 
   return header; 
}

const DataReflector& PriceFormData::DataType() const { return PriceFormItem().GetType(); }
DWORD PriceFormData::Sum() const { return (order != NULL ) ? order->Sum() : 0; }

PriceFormData::PriceFormData(OrderImpl *_order, IPriceSelect* selector) : PriceBaseData(NO_ROWID), order(_order)
{
   this->selector = selector;
}

PriceFormData::PriceFormData(OrderImpl* _order, const ROWID& upFolder, IPriceSelect* selector) : PriceBaseData(upFolder), order(_order)
{
   this->selector = selector;
}

PriceFormData::~PriceFormData()
{
   if( selector != NULL )
      delete selector;

   delete order;
}

bool PriceFormData::Closed()
{
   bool res = false;
   if( selector != NULL && selector->CanBacking() )
   {
      selector->Backing();
      res = true;
   }
   return res;
}

PriceBaseData* PriceFormData::Clone()
{
   PriceBaseData *pfd = new PriceFormData(UnbindOrder(), UpFolder());
   return pfd;
}

void PriceFormData::Init()
{
#ifdef PRICE_MOVER
   flags |= flRefresh;
#endif

   MakeHeader();

   Preference pref;
   pref.Load();

   if( (pref.flags & ppfSelectLastOrder) && order != NULL )
      LoadLastSales(&lastSaledItems, *order, SALES_FROM_ORDERS);

   searchHelper.SetData(priceItem.Name(), L"name");

#ifdef PRICE_MATRIX
   LoadMatrix();
#endif

#if defined(MULTI_WH) || defined(FIRMS_REST) || defined(WH_QTY)
   if( order != NULL )
      currentWh = order->WarehouseIndex();
#endif
#ifdef SHOW_OFF_TAKE
   if( order )
      remnants.Load(*order);
#endif
}


bool PriceFormData::IsItemMarked(const wchar_t *id) const
{
   if( selector != NULL && selector->CanSelect() )
      return selector->IsSelected(id);

   return (order != NULL ) ? order->FindItem(id) != order->items.end() : false;
}

struct PricePhotoData : public IReflectableData
{
   ROWID rowid;
   wchar_t* photo;

   DECLARE_TYPE_REFLECTION(PricePhotoData)
};

BEGIN_TYPE_REFLECTION(PricePhotoData)
   REGISTER_INT64_MEMBER(PricePhotoData, rowid)
   REGISTER_STRING_MEMBER(PricePhotoData, photo)
END_TYPE_REFLECTION(PricePhotoData)

void PriceFormData::LoadFolderData(const TreeNode& folder)
{
   PricePhotoImpl photo;
   if( SQLTable::IsTableExist(photo.Name()) )
   {
      if( !folderItem.Read(folder.id) ) return;

      wchar_t buf[20];
      PricePhotoData data;
      SQLTable table(photo.Name());

      title = folderItem.name;
      photos.clear();

      _itow(folderItem.id, buf, 10);

      std::wstring stmt(L"SELECT a.rowid, b.photo FROM ");
      stmt += priceItem.Name();
      stmt += L" AS a LEFT JOIN ";
      stmt += photo.Name();
      stmt += L" AS b ON a.id = b.id WHERE a.folderID=";
      stmt += buf;

      if( filtred )
         stmt += L" AND a.qty <> 0";

      stmt += L" ORDER BY a.name COLLATE RUSS";

      bool bdo = table.Select(stmt.c_str(), &data);
      while( bdo )
      {
         leafs.push_back(data.rowid);
         if( data.photo != NULL && *data.photo != L'\0' )
            photos[data.rowid] = data.photo;

         bdo = table.SelectNext(&data);
      }
   } else
      PriceBaseData::LoadFolderData(folder);
}

COLORREF PriceFormData::GetItemColor(int index) const
{
   index -= folders.size();
  
   if( index >= 0 && index < (int)leafs.size() )
   {
      priceItem.Read(leafs[index]);

      if( IsItemMarked(priceItem.id) )
         return selectColor;

#ifdef SHOW_OFF_TAKE
      if( remnants.FindItem(priceItem.id) != NULL )
         return rmntsColor;
#endif

      if( lastSaledItems.find(priceItem.id) != lastSaledItems.end() )
        return lastColor;

#ifdef PRICE_COLOR
      return (priceItem.color != 0) ? priceItem.color : textColor;
#endif
   }
   return textColor;
}

void PriceFormData::UpdateHeadTitle()
{
   Preference pref;
   pref.Load();

   UpdateHeadTitle(pref.priceColumn2, pref.priceColumn3);
}


void PriceFormData::UpdateHeadTitle(PriceColumnField column2, PriceColumnField column3)
{
   Preference pref;
   pref.Load();

   ListFormData::Header *hdr = (ListFormData::Header*)GetHeader();
   int shift = ColumnsCount() - 3;
   if( pref.priceScale > 0 ) shift++;
   if( shift < 0 ) return;

   //ListFormData::Header *hdr = header;
   for( int i=1; i<=2; i++ )
   {
      PriceColumnField fmt = (i==1) ? column2 : column3;

      switch( fmt )
      {
      case pcfQty:
      case pcfOrderQty:
      case pcfPriceOrderQty:
         hdr[i + shift].title = L"Кол-во";
         break;
      case pcfCost:
         hdr[i + shift].title = L"Цена";
         break;
      case pcfCostSum:
         hdr[i + shift].title = L"Сумма";
         break;
      }
   }
   if( pref.priceScale > 0 )
   {
      static wchar_t buf[50];
      wsprintf(buf, L"%s/%s", hdr[1 + shift].title, hdr[2 + shift].title);
      hdr[1 + shift].title = buf;
   }
}

LongScaleType* PriceFormData::CreateColumnFormat(const wchar_t *name, WORD offset, PriceColumnField fmt, const Preference &pref)
{
   ULongScaleType::ScaleFormat format;
   int scale;

   format.rest = L"";

   switch( fmt )
   {
   case pcfNone:
      return NULL;

   case pcfQty:
   case pcfOrderQty:
   case pcfPriceOrderQty:
      format.hideRest = true;
      if(pref.flags & ppfBoxQty)
      {
         format.flag |= ULongScaleType::ScaleFormat::SF_ROUND;
         format.rest =  L"у";
      }
      scale = QTY_SCALE;
      break;

   case pcfCost:
      scale = SUM_SCALE;
      format.hideRest = false;
      break;
   case pcfCostSum:
      scale = SUM_SCALE;
      format.hideRest = false;
      break;
   }

   format.scale = scale;

   LongScaleType *lst = new LongScaleType(name, offset, scale);
   lst->SetFormat(format);

   return lst;
}

DWORD PriceFormData::ItemCost(const Price &price, WORD ct) const
{
#ifdef COST_MANAGER
   if( (short)ct >= 0 )
      return CostManager::GetCost(price.id, ct);
   ct = 0;
#else
   if( (short)ct < 0 )
      ct = 0;
#endif

   int sz = price.cost.size();
   return (sz==0) ? 0 : (ct>= sz) ? price.cost.back() : price.cost[ct];
}

struct TwiceType : public MemberType
{
public:
   TwiceType(MemberType *type1, MemberType *type2, const wchar_t *name) : MemberType(MemberType::String, name, 1, 0)
   {
      this->type1 = type1;
      this->type2 = type2;
   }

   virtual void  SetValue(IReflectableData *data, const void *src) const {}

   virtual void* GetValue(const IReflectableData &data) const { return NULL; }

   virtual void SetFormat(const TypeFormat &format) {}

   virtual void ToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
      std::wstring text;
      type1->ToString(data, buf, cch);
      buf[cch-1] = L'\0';

      text = buf;
      text += L'\n';

      type2->ToString(data, buf, cch);
      buf[cch-1] = L'\0';
      text += buf;

      wcsncpy(buf, text.c_str(), cch);
   }

   virtual bool Serialize(StreamWriter *streamer, const IReflectableData &data) const { return true; }
   virtual bool Deserialize(IReflectableData *data, const StreamReader &streamer) const{ return true; }

protected:
   MemberType *type1, *type2;
};

#ifdef ORD_SURVAY
struct ShadowText : public MemberType
{
public:
   ShadowText(MemberType *type1, const wchar_t *name) : MemberType(MemberType::String, name, 1, 0)
   {
      shadow = false;
      this->type1 = type1;
   }

   virtual void  SetValue(IReflectableData *data, const void *src) const {}

   virtual void* GetValue(const IReflectableData &data) const { return NULL; }

   virtual void SetFormat(const TypeFormat &format) {}

   virtual void ToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
      if( !shadow )
         type1->ToString(data, buf, cch);
      else
         wcsncpy(buf, text.c_str(), cch);

      buf[cch-1] = L'\0';
   }

   void SetShadowText(const wchar_t *text)
   {
      shadow = true;
      this->text = text;
   }

   void ClearShadowText()
   {
      shadow = false;
      text.clear();
   }

   virtual bool Serialize(StreamWriter *streamer, const IReflectableData &data) const { return true; }
   virtual bool Deserialize(IReflectableData *data, const StreamReader &streamer) const{ return true; }

protected:
   MemberType *type1;
   bool shadow;
   std::wstring text;
};

bool PriceFormData::HaveSurvay(int index, int column) const
{
   return (column == columnCount - 1 && order != NULL && IsFolder(index));
}

const wchar_t* PriceFormData::GetSurvay(int index) const
{
   if( order != NULL && IsFolder(index) )
   {
      folderItem.Read(folders[index]);
      return order->GetSurvay(folderItem.id);
   }
   return L"";
}

void PriceFormData::SetSurvay(int index, const wchar_t *choice)
{
   if( order != NULL && IsFolder(index) )
   {
      folderItem.Read(folders[index]);
      order->SetSurvay(folderItem.id, folderItem.fid, choice);
   }
}
#endif

void PriceFormData::MakeHeader()
{
   Preference pref;
   pref.Load();

   DataReflector *reflector = new DataReflector(PriceFormItem::Creator, L"PriceFormItem");

   RemoveTypeReflector(L"PriceFormItem");
   RegisterTypeReflector(reflector);
   reflector->AddMember(new StringType(L"name", offsetof(PriceFormItem, name)));

   columnCount = 1;

#ifdef ORD_SURVAY
   groupFaultType = NULL;
   if( pref.priceColumn2 == pcfNone) pref.priceColumn2 = pcfQty;
#endif

   LongScaleType *lst = CreateColumnFormat(L"column2", offsetof(PriceFormItem, column2), pref.priceColumn2, pref);
   LongScaleType *lst1 = CreateColumnFormat(L"column3", offsetof(PriceFormItem, column3), pref.priceColumn3, pref);

   if( lst != NULL && lst1 != NULL )
   {
      if( pref.priceScale > 0 )
      {
         TwiceType *tt = new TwiceType(lst, lst1, L"column2");

#ifdef ORD_SURVAY
         if( order != NULL )
         {
            groupFaultType = new ShadowText(tt, L"column2");
            reflector->AddMember(groupFaultType);
         } else
            reflector->AddMember(tt);
#else
         reflector->AddMember(tt);
#endif
         
         columnCount++;
      } else
      {
         if( header[columnCount].curWidth == 0 ) header[0].curWidth = 0;
         reflector->AddMember(lst);
         columnCount++;

         if( header[columnCount].curWidth == 0 ) header[0].curWidth = 0;
#ifdef ORD_SURVAY
         if( order != NULL )
         {
            groupFaultType = new ShadowText(lst1, L"column3");
            reflector->AddMember(groupFaultType);
         } else
            reflector->AddMember(lst1);
#else
         reflector->AddMember(lst1);
#endif
         columnCount++;
      }
   } else
   {
      if( lst1 != NULL ) delete lst1;
      if( lst == NULL )
         return;

      if( header[columnCount].curWidth == 0 ) header[0].curWidth = 0;

#ifdef ORD_SURVAY
      if( order != NULL )
      {
         groupFaultType = new ShadowText(lst, L"column2");
         reflector->AddMember(groupFaultType);
      } else
         reflector->AddMember(lst);
#else
      reflector->AddMember(lst);
#endif
      columnCount++;
   }
}

void PriceFormData::UpdateOrder(const QTYData &qd, std::vector<OrderItem>::iterator item)
{
   order->UpdateOrder(item, qd);
}

#ifdef PRICE_MOVER
bool PMover::LoadNext(ROWID *id, bool next)
{
   if( next )
   {
      if( selected < data->LeafCount() - 1 ) selected++;
      else
      {
         if( !data->NextWithLeafs(next) ) return false;
         selected = 0;
      }
   } else
   {
      if( selected > 0 ) selected --;
      else
      {
         if( !data->NextWithLeafs(next) ) return false;
         selected = data->LeafCount()-1;
      }
   }

   (*id) = data->LeafID(selected);
   return true;
}

bool PMover::Move(QTYData *data, bool next)
{
   if( order != NULL )
   {
      std::vector<OrderItem>::iterator fnd = order->FindItem(data->id.c_str());
      if( fnd != order->items.end() || data->qty != 0 )
      {
         order->UpdateOrder(fnd, *data);
         if( data->sumLabel != NULL ) data->sumLabel->SetSum(order->Sum());
      }
   }

   ROWID r;
   if( !LoadNext(&r, next) ) return false;
   PriceImpl p;
   p.Read(r);

   data->id = p.id;
   data->sales.clear();

   if( order != NULL )
   {
      std::vector<OrderItem>::iterator fnd = order->FindItem(data->id.c_str());
      if( fnd == order->items.end() )
      {
         int sumType = ((int)order->sumType < 0) ? 0 : order->sumType;
         data->cost = p.cost[sumType];
         data->qty = 0;
      } else
      {
         data->cost = fnd->cost;
         data->qty = fnd->qty;
         data->flags = fnd->flags;
      }
      LoadItemSales(&data->sales, SALES_FROM_ORDERS, order->id, data->id.c_str(), 0);
   }
   else
      data->cost = p.cost[0];

   data->sum = 0;
   return true;
}
#endif


/*
bool PriceFormData::GetPriceItem(Price *pi, int index) const
{
   if( index >= (int)leafs.size() )
      return false;

   CETable priceTable(priceFormat);
   priceTable.Open(syncPrice.FileName());
   priceTable.Seek(leafs[index]);
   priceTable.GetCurrent(pi);

   return true;
}
*/

const wchar_t* PriceFormData::Photo(int index) const
{
   static std::wstring photo;

   if( index < (int)folders.size() ) return L"";

   index -= folders.size();
   if( index >= (int)leafs.size() ) return L"";

   std::map<ROWID, std::wstring>::const_iterator fnd = photos.find(leafs[index]);
   if( fnd != photos.end() )
      return fnd->second.c_str();
   return L"";
   //priceItem.Read(leafs[index]);
   //return priceItem.photo;
}

void PriceFormData::SetPhoto(const wchar_t *photo, int index)
{
   index -= folders.size();

   PriceImpl pi;
   pi.Read(leafs[index]);
   PricePhotoImpl ppi;
   ppi.id = pi.id;
   ppi.photo = (wchar_t*)photo;
   ppi.Write();

   photos[pi.rid] = photo;

   //SQLTable table(priceItem.Name());
   //priceItem.photo = (wchar_t*)photo;
   //table.Update(priceItem, L"photo", leafs[index]);
   //priceItem.photo = L"";
}

void PriceFormData::SetColumnData(int index, DWORD *data, PriceColumnField field) const
{
   *data = 0;
   if( field == pcfNone ) return;

   Preference pref;
   pref.Load();

   if( field == pcfQty || field == pcfPriceOrderQty || field == pcfOrderQty )
   {
      int pQty = PriceQty(priceItem);
      if( field == pcfQty )
      {
         *data = (pref.flags & ppfBoxQty) ? DivideInPack(pQty, priceItem.qtyInPack, QTY_SCALE) : pQty;
         return;
      } else if( field == pcfPriceOrderQty )
         *data = pQty;

      if( order != NULL )
      {
         std::vector<OrderItem>::const_iterator fnd = order->FindItem(priceItem.id);
         if( fnd != order->items.end() )
            *data = fnd->qty;

         if(pref.flags & ppfBoxQty)
            *data = DivideInPack(*data, priceItem.qtyInPack, QTY_SCALE); 
      }
   } else
   {
      DWORD curCost;

      if( order != NULL )
      {
#ifdef Provisia
         curCost = (order->params & ofNetCost) ? priceItem.cost[2] : priceItem.cost[0];
         curCost -= (curCost * order->discount / DISCOUNT_SCALE) / 100;
#else // Provisia
         curCost = ItemCost(priceItem, order->sumType);
#ifdef ORDER_DISCOUNT
         curCost += ((int)curCost * order->discount / DISCOUNT_SCALE) / SUM_SCALE;
#endif
#endif // Provisia
      } else
         curCost = ItemCost(priceItem, 0);

#ifdef ORD_ITEM_DISCOUNT
      int discount = ItemDiscount(priceItem);
      if( discount != 0 )
      {
         // добавим 1/2 для округления
         //curCost = (((int)curCost * 2 + 1) * DISCOUNT_SCALE * SUM_SCALE + ((int)curCost * discount * 2)) / (2 * DISCOUNT_SCALE * SUM_SCALE);
			int sign = (discount < 0) ? -1 : 1;
			curCost += (int)(((__int64)curCost * discount  + sign * DISCOUNT_SCALE * SUM_SCALE / 2) / (DISCOUNT_SCALE * SUM_SCALE));

         //curCost += ((int)curCost * discount / DISCOUNT_SCALE) / SUM_SCALE;
      }
#endif

      *data = curCost;
      if( field != pcfCost && order != NULL )
      {
         std::vector<OrderItem>::const_iterator fnd = order->FindItem(priceItem.id);
         if( fnd != order->items.end() )
            *data = ItemSum(fnd->cost, fnd->qty);
      }
   }
}

bool PriceFormData::Get(IReflectableData* data, int index) const
{
   if( !PriceBaseData::Get(data, index) ) return false;

   int fsize = folders.size();
   if( index < fsize )
   {
#ifdef ORD_SURVAY
      if( groupFaultType != NULL && order != NULL )
         ((ShadowText*)groupFaultType)->SetShadowText(order->GetSurvay(folderItem.id));
#endif
      AfterLoadFolder();

      ((PriceFormItem*)data)->column2 = 0;
      ((PriceFormItem*)data)->column3 = 0;
   } else
   {
#ifdef ORD_SURVAY
      if( groupFaultType != NULL )
         ((ShadowText*)groupFaultType)->ClearShadowText();
#endif

      AfterLoadPrice();

      Preference pref;
      pref.Load();
      SetColumnData(index - fsize, &((PriceFormItem*)data)->column2, pref.priceColumn2);
      SetColumnData(index - fsize, &((PriceFormItem*)data)->column3, pref.priceColumn3);
   }
   return true;
}

bool PriceFormData::ColumnHaveData(int rowIndex, PriceColumnField field) const
{
   switch(field)
   {
   case pcfNone:
      return false;

   case pcfOrderQty:
      {
         /*
         if( curReaded != rowIndex )
         {
            GetPriceItem(pItem, rowIndex - folders.size());
            curReaded = rowIndex;
         }
         */
         if( order != NULL )
         {
            std::vector<OrderItem>::const_iterator fnd = order->FindItem(priceItem.id);
            return (fnd != order->items.end());
         } else
            return false;
      }
   }
   return true;
}

bool PriceFormData::CanSetColumn(int rowIndex, int colIndex) const
{
   if( colIndex == 0 ) return true;
   if( IsFolder(rowIndex) )
   {
#ifdef ORD_SURVAY
      if( order != NULL && colIndex == columnCount - 1 )
      {
         //TreeNode *f = current->Find(folders[rowIndex]);
         //if( f != NULL && f->haveLeafs )
            return true;
      }
#endif
      return false;
   }

   Preference pref;
   pref.Load();

   if( leafs.size() <= rowIndex - folders.size() ) return false;

   priceItem.Read(leafs[rowIndex - folders.size()]);
   if( pref.priceScale > 0 )
      return ColumnHaveData(rowIndex, pref.priceColumn2) || ColumnHaveData(rowIndex, pref.priceColumn3);

   if( colIndex == 1 )
      return ColumnHaveData(rowIndex, pref.priceColumn2);

   return ColumnHaveData(rowIndex, pref.priceColumn3);
}

//
// ----------------------- PriceForm ------------------------------
//
PriceForm::PriceForm() : picWindow(NULL)
{
}

LRESULT PriceForm::SetCellInfo(LPNMHDR hdr)
{
   if( PriceBaseForm::SetCellInfo(hdr) == FALSE )
      return FALSE;

   NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
   if( di->item.mask & LVIF_IMAGE )
   {
      int index = di->item.iItem;
      if( ((PriceFormData*)data)->IsFolder(index) == false )
      {
         const wchar_t *photo = ((PriceFormData*)data)->Photo(index);
         if( *photo != 0 )
            di->item.iImage = 1;
      }
   }

   return TRUE;
}

bool PriceForm::CanSetColumn(int rowIndex, int colIndex) const
{
   return ((PriceFormData*)data)->CanSetColumn(rowIndex, colIndex);
}

void PriceForm::LoadMenuBar()
{
   PriceBaseForm::LoadMenuBar();

   if( ((PriceFormData*)data)->HaveOrder() )
   {
      sumLabel.CreateLabel(menuBar.m_hWnd, SumLabel::STD_WIDTH, SumLabelOffset());
      sumLabel.SetSum(((PriceFormData*)data)->Sum());
   }

#ifdef PRICE_MATRIX
   TBBUTTON mbutton;
   mbutton.iBitmap = I_IMAGENONE;
   mbutton.idCommand = IDC_MATRIX;
   mbutton.fsState = TBSTATE_ENABLED;
   mbutton.fsStyle = TBSTYLE_DROPDOWN | TBSTYLE_AUTOSIZE;
   mbutton.dwData = 0;
   mbutton.iString = (DWORD)L"M";

   menuBar.AddButtons(1, &mbutton);
#endif
#if defined(MULTI_WH) || defined(FIRMS_REST)
   TBBUTTON wbutton;
   wbutton.iBitmap = I_IMAGENONE;
   wbutton.idCommand = IDC_MULTIWH;
   wbutton.fsState = TBSTATE_ENABLED;
   wbutton.fsStyle = TBSTYLE_DROPDOWN | TBSTYLE_AUTOSIZE;
   wbutton.dwData = 0;
   wbutton.iString = (DWORD)L"C";

   menuBar.AddButtons(1, &wbutton);
#endif
}

#if defined(MULTI_WH) || defined(FIRMS_REST)
#include <NplConfig.h>
void PriceFormData::SetWarehous(short newWh)
{
   currentWh = newWh;
#ifdef Agama
   SelectFolder(current->id, true);
#else
   if( filtred )
      SelectFolder(current->id, true);
#endif
}

LRESULT PriceForm::SetWarehouse(int id, LPNMHDR header, BOOL &handled)
{
   NapoleonConfig config;
   std::wstring tvalue;
#ifdef MULTI_WH
   if( !config.ReadValue(&tvalue, WAREHOUSES) ) return 0;
#elif FIRMS_REST
   if( !config.ReadValue(&tvalue, SUPPL_TYPE) ) return 0;
#endif

   HMENU hm = CreatePopupMenu();

   int ctr = 1;
   int off = 0, nextOff, codePos;
   int curWh = ((PriceFormData*)data)->CurWarehous() + 1;
   while( true )
   {
      nextOff = tvalue.find(SEP_SYM, off);
      std::wstring value = tvalue.substr(off, (nextOff != std::wstring::npos) ? 
         nextOff - off : std::wstring::npos);

      codePos = value.find(L'\t');
      UINT flag = MF_STRING;
      if( ctr == curWh )
         flag |= MF_CHECKED;
      std::wstring name(L"&");
      name += value.substr(0, codePos);
      AppendMenu(hm, flag, ctr, name.c_str());

      if( nextOff == std::wstring::npos )
         break;
      off = nextOff + 1;
      ctr++;
   }

   CRect menuBounds;
   menuBar.GetRect(IDC_MULTIWH, menuBounds);
   menuBar.ClientToScreen(menuBounds);

   int res = TrackPopupMenuEx(hm,  TPM_RETURNCMD | TPM_BOTTOMALIGN,  menuBounds.left, menuBounds.top, m_hWnd, NULL);
   DestroyMenu(hm);

   if( res > 0 )
   {
      res--;
      ((PriceFormData*)data)->SetWarehous(res);
      Refresh();
   }

   return 0;
}
#endif

#if defined(PRICE_MATRIX) || defined(MULTI_WH) || defined(FIRMS_REST)
LRESULT PriceForm::ShowList(int id, LPNMHDR header, BOOL &handled)
{
#ifdef PRICE_MATRIX
   if( ((NMTOOLBAR*)header)->iItem == IDC_MATRIX )
      return SetMatrix(id, header, handled);
#endif
#if defined(MULTI_WH) || defined(FIRMS_REST)
   if( ((NMTOOLBAR*)header)->iItem == IDC_MULTIWH )
      return SetWarehouse(id, header, handled);
#endif

   handled = FALSE;
   return 0;
}
#endif

#ifdef PRICE_MATRIX
LRESULT PriceForm::SetMatrix(int id, LPNMHDR header, BOOL &handled)
{
   HMENU hm = CreatePopupMenu();

   std::vector<wchar_t*> mtx;
   ((PriceFormData*)data)->Matrixes(&mtx);

   int ctr = 1;
   int curMtx = ((PriceFormData*)data)->CurMatrix() + 1;
   std::vector<wchar_t*>::const_iterator i = mtx.begin();
   for( ; i!= mtx.end(); i++ )
   {
      UINT flag = MF_STRING;
      if( ctr == curMtx )
         flag |= MF_CHECKED;
      std::wstring name(L"&");
      name += (*i);
      AppendMenu(hm, flag, ctr++, name.c_str());
   }

   CRect menuBounds;
   menuBar.GetRect(IDC_MATRIX, menuBounds);
   menuBar.ClientToScreen(menuBounds);

   int res = TrackPopupMenuEx(hm,  TPM_RETURNCMD | TPM_BOTTOMALIGN,  menuBounds.left, menuBounds.top, m_hWnd, NULL);
   DestroyMenu(hm);

   if( res > 0 )
   {
      res--;
      ((PriceFormData*)data)->SetMatrix(res);
      menuBar.HideButton(IDC_PRICE_FILTER, (res==0) ? FALSE : TRUE);
      Refresh();

      //TBBUTTONINFO bi = {0};
      //bi.cbSize = sizeof(bi);
      //bi.dwMask = TBIF_TEXT;
      //bi.pszText = L"М"; //(LPWSTR)((PriceFormData*)data)->GetMatrix();
      //menuBar.SetButtonInfo(IDC_VIEW_TYPE, &bi);  
   }

   return 0;
}
#endif


bool PriceForm::SetData(IFormData *_data)
{
   Preference p;
   p.Load();
   return SetDataEx(_data, p.priceScale+1);
}

bool PriceForm::SetDataEx(IFormData *_data, int scale)
{
   if( prevScale != scale )
   {
      prevScale = scale;
      ListFormData::Header *h = (ListFormData::Header*)((PriceFormData*)_data)->GetHeader();
      h[0].curWidth = 0;
   }

   ((PriceFormData*)_data)->Init();
   ((PriceFormData*)_data)->UpdateHeadTitle();
   if( PriceBaseForm::SetDataEx(_data, scale) == false )
      return false;

#ifdef PRICE_MOVER
   ((PriceFormData*)data)->sumLabel = &sumLabel;
#endif

   ((PriceFormData*)_data)->SetDataDone();
   return true;
}

#ifdef ORD_SURVAY
#include <NplConfig.h>
int PriceForm::PopSurvayMenu(std::vector<std::wstring> *choices, int index)
{
   NapoleonConfig config;
   std::wstring value;
   if( config.ReadValue(&value, ORDER_SURVEY) )
   {
      int ctr = 1;
      const wchar_t *curChoice = ((PriceFormData*)data)->GetSurvay(index);
      HMENU hm = CreatePopupMenu();

      DWORD flag = MF_STRING;
      if( *curChoice == L'\0' ) flag |= MF_CHECKED;
      AppendMenu(hm, flag, ctr++, L"<нет>");
      choices->push_back(L"");

      std::wstring::size_type off = 0, nextOff = 0;
      while( true )
      {
         nextOff = value.find(SEP_SYM, off);
         std::wstring tval(L"&");

         choices->push_back(value.substr(off, (nextOff != std::wstring::npos) ?  nextOff - off : std::wstring::npos));
         tval += choices->back();

         flag = MF_STRING;
         if( choices->back().compare(curChoice) == 0 )
            flag |= MF_CHECKED;

         AppendMenu(hm, flag, ctr++, tval.c_str());

         if( nextOff == std::wstring::npos ) break;
         off = nextOff + 1;
      }

      CRect menuBounds;
      int top;
      listCtrl.GetWindowRect(menuBounds);
      top = menuBounds.top;
      listCtrl.GetItemRect(index, menuBounds, LVIR_BOUNDS);
      menuBounds.left = menuBounds.right - listCtrl.GetColumnWidth(listCtrl.GetHeader().GetItemCount());
      menuBounds.bottom += top;

      int res = TrackPopupMenuEx(hm,  TPM_RETURNCMD | TPM_BOTTOMALIGN,  menuBounds.left, menuBounds.bottom, m_hWnd, NULL);
      DestroyMenu(hm);

      return res - 1;
   }

   return -1;
}
#endif // ORD_SURVAY

LRESULT PriceForm::ItemSelected(LPNMHDR hdr)
{
   int index = ((NMLISTVIEW*)hdr)->iItem;

   curIndex = -1;

   if( index >= 0 )
   {
      bool isFolder = ((PriceFormData*)data)->IsFolder(index);
      if( isFolder == false )
      {
         const wchar_t *photo = ((PriceFormData*)data)->Photo(index);
         if( *photo != 0 )
         {
            CRect bounds;
            listCtrl.GetItemRect(index, bounds, LVIR_BOUNDS);
            bounds.right = bounds.left + GetSystemMetrics(SM_CXSMICON);
            if( bounds.PtInRect(((NMLISTVIEW*)hdr)->ptAction) )
            {
               ShowPhoto(photo);
               return 0;
            }
         }
      }
#ifdef ORD_SURVAY
      if( ((PriceFormData*)data)->HaveSurvay(index, ((NMLISTVIEW*)hdr)->iSubItem) )
      {
         std::vector<std::wstring> choices;
         int ichoice = PopSurvayMenu(&choices, index);
         if( ichoice >= 0 )
         {
            ((PriceFormData*)data)->SetSurvay(index, choices[ichoice].c_str());

            listCtrl.RedrawItems(index, index);

         }
         return 0;
      }
#endif
   }
   return PriceBaseForm::ItemSelected(hdr);
}

LRESULT PriceForm::Closing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   if( !((PriceFormData*)data)->Closed() )
   {
      OrderImpl *o = ((PriceFormData*)data)->UnbindOrder();
      if( o ) OpenInvoice(o);
      else OpenOrgList(dtOrder);
   }
   return 0;
}

DWORD PriceForm::OnItemPrePaint(int idCtrl, LPNMCUSTOMDRAW lpNMCustomDraw)   
{
   DWORD res = PriceBaseForm::OnItemPrePaint(idCtrl, lpNMCustomDraw);

#ifdef ORD_SURVAY
   NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
   DWORD item = lvcd->nmcd.dwItemSpec;

   if( ((PriceFormData*)data)->HaveSurvay(item, lvcd->iSubItem) )
   {
      HDC dc = lvcd->nmcd.hdc;

      int wdh;
      GetCharWidth32(dc, L'н', L'н', &wdh);
      lvcd->rcText.right -= wdh + 4;

      //return CDRF_SKIPDEFAULT;
   }
#endif
   return res;
}

#ifdef ORD_SURVAY
DWORD PriceForm::OnItemPostPaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
{
   NMLVCUSTOMDRAW *cd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
   DWORD item = cd->nmcd.dwItemSpec;
   if( ((PriceFormData*)data)->HaveSurvay(item, cd->iSubItem) )
   {
      HDC dc = cd->nmcd.hdc;
      RECT &rc = cd->rcText;
      HPEN svPen = (HPEN)SelectObject(dc, GetStockObject(BLACK_PEN));
      HBRUSH svBrush = (HBRUSH)SelectObject(dc, GetStockObject(BLACK_BRUSH));
      
      TEXTMETRIC tm;
      GetTextMetrics(dc, &tm);
      int height;
      GetCharWidth32(dc, L'н', L'н', &height);

      POINT pt[3];
      pt[0].x = rc.right - height - 2;
      pt[0].y = rc.top + tm.tmHeight /2 + 2;
      height /= 2;
      pt[1].x = pt[0].x + height;
      pt[1].y = pt[0].y - height;
      pt[2].x = pt[1].x + height;
      pt[2].y = pt[0].y;

      Polygon(dc, pt, 3);

      SelectObject(dc, svPen);
      SelectObject(dc, svBrush);
   }
   return CDRF_NOTIFYITEMDRAW;
}
#endif

LRESULT PriceForm::OnCommand(WORD msg, WPARAM, LPARAM, BOOL &bHandled)
{
   if( picWindow )
   {
      picWindow->Cancel();
      delete picWindow;
      picWindow = NULL;
   } else
      bHandled = FALSE;
   return 0;
}

void PriceForm::ShowPhoto(const wchar_t *fileName)
{
#ifdef WIN32_PLATFORM_PSPC
   HBITMAP hBmp = ::SHLoadImageFile(fileName);
   if( hBmp  == NULL ) return;

   if( picWindow != NULL )
   {
      picWindow->Cancel();
      delete picWindow;
   }
   picWindow = new PicWindow(hBmp);
   picWindow->Show(m_hWnd);
   delete picWindow;
   picWindow = NULL;
#endif
}

LRESULT PriceForm::DelPhoto(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   int index = listCtrl.GetSelectedIndex();
   ((PriceFormData*)data)->SetPhoto(L"", index);
   listCtrl.RedrawItems(index, index);
   return 0;
}

#ifndef WIN32_PLATFORM_PSPC
//++++++
//
// GetOpenFileNameEx
//
//  This function extends the GetOpenFileName provided by WinCE to support our Thumbnail view and provide support
//  for other extensoins (i.e. hide DRM content) not supported by defauly in WinCE

//
// Sort order
//
typedef enum tagOFN_SORTORDER
{
   OFN_SORTORDER_AUTO,
   OFN_SORTORDER_DATE,
   OFN_SORTORDER_NAME,
   OFN_SORTORDER_SIZE,
   OFN_SORTORDER_ASCENDING = 0x00008000

} OFN_SORTORDER;

//
// Extended Flags  
//
typedef enum tagOFN_EXFLAG
{
    OFN_EXFLAG_EXPLORERVIEW                      = 0x00000000,
    OFN_EXFLAG_DETAILSVIEW                       = 0x00000001,
    OFN_EXFLAG_THUMBNAILVIEW                     = 0x00000002,
    OFN_EXFLAG_MESSAGING_FILE_CREATE             = 0x00000004,
    OFN_EXFLAG_CAMERACAPTURE_MODE_VIDEOONLY      = 0x00000008,
    OFN_EXFLAG_CAMERACAPTURE_MODE_VIDEOWITHAUDIO = 0x00000010,
    OFN_EXFLAG_CAMERACAPTURE_MODE_VIDEODEFAULT   = 0x00000020,
    OFN_EXFLAG_LOCKDIRECTORY                     = 0x00000100,
    OFN_EXFLAG_NOFILECREATE                      = 0x00000200,
    OFN_EXFLAG_HIDEDRMPROTECTED                  = 0x00010000,     //If this flag is set and the DRM engine is installed - the PicturePicker will not show ANY DRM content
    OFN_EXFLAG_HIDEDRMFORWARDLOCKED              = 0x00020000     //If this flag is set and the DRM engine is installed - the PicturePicker will not show ANY DRM FORWARD LOCK content
} OFN_EXFLAG;


typedef struct tagOPENFILENAMEEX
{
    // Fields which map to OPENFILENAME
   DWORD        lStructSize;
   HWND         hwndOwner;
   HINSTANCE    hInstance;
   LPCTSTR      lpstrFilter;
   LPTSTR       lpstrCustomFilter;
   DWORD        nMaxCustFilter;
   DWORD        nFilterIndex;
   LPTSTR       lpstrFile;
   DWORD        nMaxFile;
   LPTSTR       lpstrFileTitle;
   DWORD        nMaxFileTitle;
   LPCTSTR      lpstrInitialDir;
   LPCTSTR      lpstrTitle;
   DWORD        Flags;
   WORD         nFileOffset;
   WORD         nFileExtension;
   LPCTSTR      lpstrDefExt;
   LPARAM       lCustData;
   LPOFNHOOKPROC lpfnHook;
   LPCTSTR      lpTemplateName;

   // Extended fields
   DWORD       dwSortOrder;
   DWORD       ExFlags;
}OPENFILENAMEEX, *LPOPENFILENAMEEX ;
#endif
typedef BOOL (*TGetOpenFileNameEx)(LPOPENFILENAMEEX lpofnex);


LRESULT PriceForm::SetPhoto(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   wchar_t buf[MAX_PATH];
   int index = listCtrl.GetSelectedIndex();
   const wchar_t *photo = ((PriceFormData*)data)->Photo(index);
   
   OSVERSIONINFO osvi;
   osvi.dwOSVersionInfoSize = sizeof(osvi);
   GetVersionEx(&osvi);

   wcscpy(buf, photo);

   HMODULE module = LoadLibrary(L"AYGSHELL.dll");
   TGetOpenFileNameEx gf = (TGetOpenFileNameEx)GetProcAddress(module, L"GetOpenFileNameEx");

   if( gf != NULL )
   {
      OPENFILENAMEEX ofn = {0};

      ofn.lStructSize = sizeof(ofn);
      ofn.hwndOwner = hWnd;
      ofn.hInstance = _Module.GetModuleInstance();
      ofn.lpstrFilter = L"All Files (*.*)\0*.*\0";//Изображения\0*.jpg;*.jpeg\0";
      ofn.nFilterIndex = 0;
      ofn.lpstrFile = buf;
      ofn.nMaxFile = sizeof(buf)/sizeof(buf[0]);
      ofn.lpstrTitle = L"Укажите изображение";
      ofn.Flags = OFN_FILEMUSTEXIST;
      ofn.ExFlags = OFN_EXFLAG_THUMBNAILVIEW;
      //ofn.dwSortOrder = OFN_SORTORDER_NAME;

      if( gf(&ofn) == TRUE )
      {
         ((PriceFormData*)data)->SetPhoto(buf, index);
         listCtrl.RedrawItems(index, index);
      }
      return 0;
   }

   OPENFILENAME ofn;
   memset(&ofn, 0, sizeof(ofn));

   ofn.lStructSize = sizeof(ofn);
   ofn.hwndOwner = hWnd;
   ofn.hInstance = _Module.GetModuleInstance();
   ofn.lpstrFilter = L"Изображения\0*.jpg;*.jpeg\0";
   ofn.nFilterIndex = 0;
   ofn.lpstrFile = buf;
   ofn.nMaxFile = sizeof(buf)/sizeof(buf[0]) - 1;
   ofn.lpstrTitle = L"Укажите изображение";
   ofn.Flags = OFN_FILEMUSTEXIST;

   if( GetOpenFileName(&ofn) == TRUE )
   {
      ((PriceFormData*)data)->SetPhoto(buf, index);
      listCtrl.RedrawItems(index, index);
   }
   return 0;
}

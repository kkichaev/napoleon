/*
 * Copyright (C), 2007, Денис Мосягин
 *
 *  Процедура открытия прайс-листа
 *
 *  ert   06/02/2008   creating
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
#include "NplConfig.h"
#include "Preference.h"
#include <DocType.h>

static ListFormData::Header priceHeader[] = 
{
   { ListFormData::Header::Left,  L"Название", L"name", 100 },
   { ListFormData::Header::Right,  L"Продано", L"qty", 25 },
   { ListFormData::Header::Right, L"Кол-во",   L"column2", 50 },
   { ListFormData::Header::Right, L"Сумма",    L"column3", 50 },
};

struct PFItemAdd : PriceFormItem
{
   DWORD qty;   
   DECLARE_TYPE_REFLECTION(PFItemAdd)
};

//
// ----------------------------------- Price Add --------------------------------------------
//
struct PriceFormDataAdd : public PriceFormData
{
   PriceFormDataAdd(OrderImpl* _order);
   PriceFormDataAdd(OrderImpl* _order, const ROWID &upFolder);
   
   virtual const Header *GetHeader() const
   { 
      return priceHeader; 
   }

   virtual const DataReflector& DataType() const { return PFItemAdd().GetType(); }

   virtual PriceBaseData* Clone()
   {
      OrderImpl *order = UnbindOrder();
      PriceBaseData *pfd = new PriceFormDataAdd(order, UpFolder());
      return pfd;
   }

   virtual bool CanSetColumn(int rowIndex, int colIndex) const
   {
      if( colIndex == 0 ) return true;
      if( rowIndex < (int)folders.size() && colIndex == 1 ) return false;
      return PriceFormData::CanSetColumn(rowIndex, colIndex); 
   }

   virtual bool Get(IReflectableData* data, int index) const
   {
      bool ret = PriceFormData::Get(data, index);
      if( !ret ) return false;
      
      ((PFItemAdd*)data)->qty = 0;

      index -= folders.size();
      if( index >= 0 && index < (int)leafs.size() )
      {
         std::map<std::wstring, DWORD>::const_iterator fnd = sales.find(priceItem.id);
         if( fnd != sales.end() )
            ((PFItemAdd*)data)->qty = fnd->second;
      }

      return true;
   }

protected:
   virtual void Init();

   std::map<std::wstring, DWORD> sales;
};


class PriceFormAdd : public PriceForm
{
 public:
   PriceFormAdd();

   BEGIN_MSG_MAP(PriceFormAdd)
      COMMAND_ID_HANDLER(IDC_FLAT_PRICE, ChangePrice)
      CHAIN_MSG_MAP(PriceForm)
   END_MSG_MAP()

   virtual DWORD GetResourceID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuID() const { return IDD_PRICE_LIST; }

   DECLARE_FORM(PriceFormAdd, IDD_PRICE_LIST_ADD)

   virtual void LoadMenuBar();

   bool IsReportView() const { return reportView; }

 protected:
   LRESULT ChangePrice(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   bool reportView;
};

IMPLEMENT_FORM(PriceFormAdd)

PriceFormAdd::PriceFormAdd() : reportView(false)
{
}

void PriceFormAdd::LoadMenuBar()
{
   PriceForm::LoadMenuBar();

   TBBUTTON mbutton;
   mbutton.iBitmap = (reportView) ? 21 : 20;
   mbutton.idCommand = IDC_FLAT_PRICE;
   mbutton.fsState = TBSTATE_ENABLED;
   mbutton.fsStyle = TBSTYLE_BUTTON | TBSTYLE_AUTOSIZE;
   mbutton.dwData = 0;
   mbutton.iString = 0;

   menuBar.AddButtons(1, &mbutton);
}


LRESULT PriceFormAdd::ChangePrice(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   reportView = !reportView;

   ((PriceFormData*)data)->SetFlatPrice(reportView);
   Refresh();

   TBBUTTONINFO bi = {0};
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_IMAGE;
   bi.iImage = (reportView) ? 21 : 20;
   menuBar.SetButtonInfo(IDC_FLAT_PRICE, &bi);

   return 0;
}

PriceFormDataAdd::PriceFormDataAdd(OrderImpl* _order) : PriceFormData(_order)
{
}

PriceFormDataAdd::PriceFormDataAdd(OrderImpl* _order, const ROWID &upFolder) : PriceFormData(_order, upFolder)
{
}

void PriceFormDataAdd::Init()
{
   PriceFormData::Init();

   DataReflector *reflector = new DataReflector(PFItemAdd::Creator, L"PFItemAdd");
   RemoveTypeReflector(L"PFItemAdd");
   RegisterTypeReflector(reflector);

   reflector->AddMember(new ULongScaleType(L"qty", offsetof(PFItemAdd, qty), QTY_SCALE, true));
   reflector->AddMember(new ParentType(L"PriceFormItem"));
      
   columnCount++; 

   if( order != NULL )
   {
      DocumentList *dl;
      if( docTypeManager.GetDocType(dtOrder)->GetDocuments(order->id, &dl) )
      {
         for( unsigned i=0; i<dl->Count(); i++ )
         {
            IDocument *d = dl->Get(i);
            if( d != NULL )
            {
               OrderImpl *doc = (OrderImpl*)d->Data();
               vector_t<OrderItem>::const_iterator item = doc->items.begin();
               for( ; item != doc->items.end(); item++ )
                 sales[item->id] += item->qty;
            }
         }
      }

      delete dl;
   }
}

void OpenPriceList(OrderImpl* order)
{
   PriceFormData *pfd = new PriceFormDataAdd(order);
   _Module.GetFrame()->Load(IDD_PRICE_LIST_ADD, pfd);
}

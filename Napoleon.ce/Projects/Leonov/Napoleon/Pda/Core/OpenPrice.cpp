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
#include <DocImpl.h>

struct PriceFormDataAdd : public PriceFormData
{
   PriceFormDataAdd(OrderImpl* _order);
   PriceFormDataAdd(OrderImpl* _order, const ROWID &upFolder);
   
   virtual PriceBaseData* Clone()
   {
      OrderImpl *order = UnbindOrder();

      PriceBaseData *pfd = new PriceFormDataAdd(order, UpFolder());
      return pfd;
   }

   void SetShowQty(bool v) { showPriceQty = v; }

   virtual bool Get(IReflectableData* data, int index) const
   {
      if( !PriceFormData::Get(data, index) ) return false;

      index -= folders.size();
      if( index >= 0 )
      {
         Preference pref;
         pref.Load();

         if( pref.priceColumn2 == pcfPriceOrderQty || pref.priceColumn3 == pcfPriceOrderQty )
         {
            DWORD qdata;

            SetColumnData(index, &qdata, (showPriceQty) ? pcfQty : pcfOrderQty );

            if( pref.priceColumn2 == pcfPriceOrderQty )
               ((PriceFormItem*)data)->column2 = qdata;
            if( pref.priceColumn3 == pcfPriceOrderQty )
               ((PriceFormItem*)data)->column3 = qdata;
         }
      }

      return true;
   }

   bool showPriceQty;
};

class PriceFormAdd : public PriceForm
{
 public:
   PriceFormAdd() {}

   virtual DWORD GetResourceID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_PRICE_LIST_ADD; }
   virtual DWORD GetMenuID() const { return IDD_PRICE_LIST; }

   BEGIN_MSG_MAP(PriceFormAdd)
      COMMAND_ID_HANDLER(IDC_QTY, ChangeQty)
      CHAIN_MSG_MAP(PriceForm)
   END_MSG_MAP()

   LRESULT ChangeQty(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      bool v = !((PriceFormDataAdd*)data)->showPriceQty;

      ((PriceFormDataAdd*)data)->SetShowQty(v);

      if( menuBar.m_hWnd != NULL )
      {
         TBBUTTONINFO bi = {0};
         bi.cbSize = sizeof(bi);
         bi.dwMask = TBIF_IMAGE;
         bi.iImage = (v) ? 20 : 21;
         menuBar.SetButtonInfo(IDC_QTY, &bi);
      }

      Refresh();
      return 0;
   }

   virtual void LoadMenuBar()
   {
      PriceForm::LoadMenuBar();
      
      Preference p;
      p.Load();

      if( p.priceColumn2 == pcfPriceOrderQty || p.priceColumn3 == pcfPriceOrderQty )
      {
         TBBUTTON button;
         button.iBitmap = 20; // 21
         button.idCommand = IDC_QTY;
         button.fsState = TBSTATE_ENABLED;
         button.fsStyle = TBSTYLE_BUTTON | TBSTYLE_AUTOSIZE;
         button.dwData = 0;
         button.iString = IDC_NOTES;

         menuBar.AddButtons(1, &button);
      }
   }

   DECLARE_FORM(PriceFormAdd, IDD_PRICE_LIST_ADD)
};

IMPLEMENT_FORM(PriceFormAdd);

PriceFormDataAdd::PriceFormDataAdd(OrderImpl* _order) : PriceFormData(_order), showPriceQty(true)
{
}

PriceFormDataAdd::PriceFormDataAdd(OrderImpl* _order, const ROWID &upFolder) : PriceFormData(_order, upFolder), showPriceQty(true)
{
}

void OpenPriceList(OrderImpl* order)
{
   PriceFormData *pfd = new PriceFormDataAdd(order);
   _Module.GetFrame()->Load(IDD_PRICE_LIST_ADD, pfd);
}


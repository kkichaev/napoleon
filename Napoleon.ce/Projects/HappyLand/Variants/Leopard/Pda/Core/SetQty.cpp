/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Диалог количества, custom
 *
 *  ert   17/08/2007   creating
 */
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include "Preference.h"
#include <NapoleonRes.h>

#include "Qty.h"

#include <MainFrame.h>
#include <Table.h>
#include "Add.h"

class QTYDialog : public CQTYDialog
{
public:
   QTYDialog(QTYData *_data) : CQTYDialog(_data) {}

   BEGIN_MSG_MAP(QTYDialog)
      COMMAND_HANDLER(IDC_INPACK_LABEL, CBN_SELCHANGE, CheckInpack)
      CHAIN_MSG_MAP(CQTYDialog)
   END_MSG_MAP()

   virtual DWORD PriceQty(const Price &item) const
   {
      return CountQTY(item, data->whCode.c_str(), data->pack.c_str());
   }

protected:
   virtual void SetData(const PriceImpl& price);
   virtual DWORD QTYInPack(const PriceImpl& price);

   LRESULT CheckInpack(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled);

   CComboBox packs;

   struct PackData
   {
      std::wstring id;
      DWORD qty;
   };
   std::vector<PackData> packQty;
};

void QTYDialog::SetData(const PriceImpl& price)
{
   CQTYDialog::SetData(price);

   CButton pack(GetDlgItem(IDC_PACK));
   pack.ShowWindow(SW_HIDE);
   CheckDlgButton(IDC_PACK, BST_CHECKED);

   bool checkInpack = false;
   packs = GetDlgItem(IDC_INPACK_LABEL);
   vector_t<PackItem>::const_iterator i = price.packs.begin();
   for( ; i != price.packs.end(); i++ )
   {
      int index = packs.AddString(i->pack);
      if( wcscmp(i->pack, data->pack.c_str()) == 0 )
         packs.SetCurSel(index);

      if( data->pack.empty() )
      {
         data->pack = i->pack;
         packs.SetCurSel(index);
         checkInpack = true;
      }

      PackData pd;
      pd.qty = i->inPack;
      pd.id = i->pack;
      packQty.push_back(pd);
   }
   if( checkInpack )
   {
      BOOL bHandled = TRUE;
      CheckInpack(0, 0, packs.m_hWnd, bHandled);
   }
}

LRESULT QTYDialog::CheckInpack(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled)
{
   int cs = packs.GetCurSel();
   DWORD ip = 0;
   if( cs >= 0 && cs < (int)packQty.size() )
   {
      const PackData& pd = packQty[cs];
      data->pack = pd.id;
      ip = pd.qty;
   }

   if( qtyInPack != ip )
   {
      qtyInPack = ip;

      SetScalingValue(IDC_INPACK, qtyInPack, QTY_SCALE, true);
      SetSum(0, 0, m_hWnd, bHandled);

      PriceImpl prc;
      prc.id = (wchar_t*)data->id.c_str();
      prc.Read();
      priceQty = PriceQty(prc);
      SetScalingValue(IDC_REST, priceQty, QTY_SCALE, true);
   }
   return 0;
}

DWORD QTYDialog::QTYInPack(const PriceImpl& price)
{
   return GetInPack(price, data->whCode.c_str(), data->pack.c_str());
}

bool SetQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   QTYDialog dlg(data);
   int code = dlg.DoModal();

   SetFocus(oldFocus);
   return (code == IDOK);
}

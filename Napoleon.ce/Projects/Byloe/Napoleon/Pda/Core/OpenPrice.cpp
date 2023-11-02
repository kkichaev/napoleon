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
#include "Add.h"  
#include <NplConfig.h>

struct PriceFormDataAdd : public PriceFormData
{
   PriceFormDataAdd(OrderImpl *order) : PriceFormData(order)
   {
      whIndex = SetCurrentWH(order);
      costIndex = (order) ? order->sumType : 0;
   }

   virtual void LoadFolderData(const TreeNode& folder);
   virtual void UpdateOrder(const QTYData &qd, std::vector<OrderItem>::iterator item);

   int WhIndex() const { return whIndex; }
   void SetWhIndex(int i) { whIndex = i; }

   virtual DWORD ItemCost(const Price &price, WORD ct) const
   {
      return PriceFormData::ItemCost(price, costIndex);
   }
   int CostIndex() const { return costIndex; }
   void SetCostIndex(int i) { costIndex = i; }

protected:
   int whIndex;
   int costIndex;
   bool IsBlw15() const;

   virtual DWORD PriceQty(const Price &price) const
   {
      if( /*order == NULL || */whIndex == 0 || whIndex > (int)price.qtys.size() ) return price.qty;
      return price.qtys[whIndex-1].qty;
   }
};

class PriceFormAdd : public PriceForm
{
public:
   PriceFormAdd() {}

   DECLARE_FORM(PriceFormAdd, IDD_PRICE_LIST_ADD)

   BEGIN_MSG_MAP(PriceFormAdd)
      NOTIFY_CODE_HANDLER(TBN_ENDDRAG, SetWarehouse)
      CHAIN_MSG_MAP(PriceForm)
   END_MSG_MAP()

   virtual DWORD GetMenuID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetResourceID() const { return IDD_PRICE_LIST; }

   LRESULT SetWarehouse(int id, LPNMHDR header, BOOL &handled);

protected:
   virtual void LoadMenuBar();
   int MakeContextMenu(int id);
};

IMPLEMENT_FORM(PriceFormAdd)

void PriceFormDataAdd::UpdateOrder(const QTYData &qd, std::vector<OrderItem>::iterator item)
{
   if( IsBlw15() )
   {
      priceItem.id = (wchar_t*)qd.id.c_str();;
      priceItem.Read();
      if( (priceItem.flags & pfBlw15) == 0 )
      {
         MessageBox(GetActiveWindow(), L"В заказ нельзя выбирать товар с крепостью больше 15", L"Ошибка", MB_OK|MB_ICONSTOP);
         return;
      }
   }
   PriceFormData::UpdateOrder(qd, item);
}

bool PriceFormDataAdd::IsBlw15() const
{
   if( order != NULL )
   {
      OrgImpl org;
      org.id = order->id;
      org.Read();
      if( (org.flags & ofBlw15) != 0 )
         return true;
   }

   return false;
}

void PriceFormDataAdd::LoadFolderData(const TreeNode& folder)
{
   if( folder.id == NO_ROWID || !folderItem.Read(folder.id) ) return;

   title = folderItem.name;

   wchar_t buf[200];
   wsprintf(buf, L"WHERE folderID=%d", folderItem.id);

   if( IsBlw15() )
      wsprintf(buf+wcslen(buf), L" AND (flags & %d) <> 0", pfBlw15);

   if( filtred )
   {
#ifdef MULTI_WH
      wsprintf(buf+wcslen(buf), L" AND collectionValue(qty, 'QtyItem', %d, 'qty') <> 0", currentWh);
#else
      wcscat(buf, L" AND qty <> 0");
#endif
   }

   Preference p;
   p.Load();
   wcscat(buf, L" ORDER BY name COLLATE " );
   wcscat(buf, (p.flags & apfSortNoCase) ? L"RUSS_NOCASE" : L"RUSS" );
   SQLTable table(priceItem.Name());
   table.RIDList(&leafs, buf);
}

void PriceFormAdd::LoadMenuBar()
{
   PriceForm::LoadMenuBar();

   TBBUTTON wbutton[2];
   wbutton[0].iBitmap = I_IMAGENONE;
   wbutton[0].idCommand = IDC_MULTIWH;
   wbutton[0].fsState = TBSTATE_ENABLED;
   wbutton[0].fsStyle = TBSTYLE_DROPDOWN | TBSTYLE_AUTOSIZE;
   wbutton[0].dwData = 0;
   wbutton[0].iString = (DWORD)L"C";

   wbutton[1].iBitmap = I_IMAGENONE;
   wbutton[1].idCommand = IDC_COST_TYPE;
   wbutton[1].fsState = TBSTATE_ENABLED;
   wbutton[1].fsStyle = TBSTYLE_DROPDOWN | TBSTYLE_AUTOSIZE;
   wbutton[1].dwData = 0;
   wbutton[1].iString = (DWORD)L"Ц";

   menuBar.AddButtons(2, wbutton);
}

int PriceFormAdd::MakeContextMenu(int id)
{
   NapoleonConfig config;
   std::wstring tvalue;

   if( !config.ReadValue(&tvalue, (id == IDC_MULTIWH) ? L"Склады" : L"ТипЦен") ) return 0;

   HMENU hm = CreatePopupMenu();

   int ctr = 1;
   int off = 0, nextOff, codePos;
   int curWh = (id == IDC_MULTIWH) ? ((PriceFormDataAdd*)data)->WhIndex() : ((PriceFormDataAdd*)data)->CostIndex();
   curWh++;

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
   
   return res;
}

LRESULT PriceFormAdd::SetWarehouse(int id, LPNMHDR header, BOOL &handled)
{
   int iid = ((NMTOOLBAR*)header)->iItem;
   if( iid != IDC_MULTIWH && iid != IDC_COST_TYPE )
   {
      handled = FALSE;
      return 0;
   }

   int res = MakeContextMenu(iid);
   if( res > 0 )
   {
      res--;
      if( iid == IDC_MULTIWH )
         ((PriceFormDataAdd*)data)->SetWhIndex(res);
      else
         ((PriceFormDataAdd*)data)->SetCostIndex(res);
      Refresh();
   }

   return 0;
}

void OpenPriceList(OrderImpl* order)
{
   PriceFormData *pfd = new PriceFormDataAdd(order);
   _Module.GetFrame()->Load(IDD_PRICE_LIST_ADD, pfd);
}


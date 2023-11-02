/*
* Copyright (C), 2007, Денис Мосягин
*
* Детали заказа
*
* Order.supplyer - порядковый номер договора
*
*  ert   09/09/2007   creating
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

#include "PropDialog.h"

#include <NplConfig.h>
#include <Add.h>
#include <StdFuncs.h>
#include <NumInput.h>
#include <ObjImpl.h>

//BOOL CALLBACK DisableChildsProc(HWND hwnd, LPARAM lParam);

class OrderPage : public PropPage
{
public:
   OrderPage(WORD wID, ATL::_U_STRINGorID title = (LPCTSTR)NULL) : PropPage(wID, title) {}
   virtual void Save(OrderImpl *order) = 0;

   //void DisableChilds()
   //{
   //   EnumChildWindows(m_hWnd, DisableChildsProc, (LPARAM)((HWND)GetDlgItem(IDCANCEL)));
   //}

   void ResizeWindow(UINT id, WORD width)
   {
      CWindow wnd(GetDlgItem(id));
      CRect rc;

      wnd.GetWindowRect(rc);
      ScreenToClient(rc);
      rc.right = width - offset;
      wnd.MoveWindow(rc);
   }

   virtual bool Valid() const = 0;
};

class OrderDetailDialog : public PropDialog
{
public:
   OrderDetailDialog(OrderImpl *_order);

   ~OrderDetailDialog() {}

   bool OnOK()
   {
      std::vector<PropPage*>::iterator i = pages.begin();
      for( ;i != pages.end(); i++ )
      {
         if( ((OrderPage*)(*i))->Valid() == false )
            return false;

         ((OrderPage*)(*i))->Save(order);
      }

      return true; 
   }

   OrderImpl* Order() const { return order; }

protected:
   OrderImpl *order;
   std::wstring title;
};

class OrderPage1 : public OrderPage
{
public:
   OrderPage1() : OrderPage(IDD_ORDER_DETAIL, L"Основная"), numInput(IDC_SUM) {}

   void LoadComboBox(UINT id, wchar_t *str[], int sel)
   {
      CComboBox cb(GetDlgItem(id));
      while( *str )
      {
         cb.AddString(*str);
         str++;
      }
      cb.SetCurSel(sel);
   }

   BEGIN_MSG_MAP(OrderPage1)
      COMMAND_HANDLER(IDC_PAY, CBN_SELCHANGE, CheckPay)
      COMMAND_HANDLER(IDC_DOGOVORS, CBN_SELCHANGE, ChangeDog)
      NUM_INPUT_HANDLER(numInput)
      CHAIN_MSG_MAP(OrderPage)
   END_MSG_MAP()

   LRESULT CheckPay(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled)
   {
      CComboBox pay(GetDlgItem(IDC_PAY));
      UINT show;
      if(pay.GetCurSel() == ptPaySum)
      {
         show = SW_SHOW;
         ((CEdit)GetDlgItem(IDC_SUM)).SetSel(0, -1);
      } else
      {
         show = SW_HIDE;
      }

      GetDlgItem(IDC_SUM).ShowWindow(show);
      numInput.Show(*this, show);

      return 0;
   }

   LRESULT ChangeDog(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled)
   {
      OnChangeDogovor();
      return 0;
   }

   struct CItem
   {
      const char *name;
      const char *type;

      bool operator<(const CItem &item) const { return strcmp(name, item.name) < 0; }
   };
   std::set<CItem> costSet;

   virtual void Init()
   {
      OrderImpl *o = ((OrderDetailDialog*)owner)->Order();
      if( o->IsExported() )
         DisableChilds();

      org.id = o->id;
      org.Read();

      SYSTEMTIME st;
      FileTimeToSystemTime(&o->date, &st);
      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);
      FileTimeToSystemTime(&o->supplDate, &st);
      ((CDateTimePickerCtrl)GetDlgItem(IDC_DELIVERY_DATE)).SetSystemTime(GDT_VALID, &st);

      wchar_t name[300];
      CComboBox costs(GetDlgItem(IDC_COST_TYPE));
      const CostManager::CostList &clist = CostManager::CostTypes();
      CostManager::CostList::const_iterator ci = clist.begin();

      // выделим текущую
      std::set<CItem>::const_iterator ctsel = costSet.end();
      const char *selType = NULL;
      if( o->sumType != (WORD)-1 )
         selType = clist[o->sumType].id.c_str();
      
      for( ; ci != clist.end(); ci++ )
      {
         CItem item;
         item.name = ci->name.c_str();
         item.type = ci->id.c_str();
         std::pair<std::set<CItem>::iterator, bool> res = costSet.insert(item);
         if( selType != NULL && ci->id.compare(selType) == 0 )
            ctsel = res.first;
      }

      std::set<CItem>::const_iterator cti = costSet.begin();
      for( ; cti != costSet.end(); cti++ )
      {
         mbstowcs(name, cti->name, strlen(cti->name) + 1);
         costs.AddString(name);
      }

      CComboBox dog(GetDlgItem(IDC_DOGOVORS));
      vector_t<Dogovor>::const_iterator dogI = org.dogovors.begin();
      for( ; dogI != org.dogovors.end(); dogI++ )
         dog.AddString(dogI->name);
      dog.SetCurSel(o->supplyer);
      if( o->sumType == (WORD)-1 )
      {
         ((CComboBox)GetDlgItem(IDC_COST_TYPE)).SetCurSel(0);
         OnChangeDogovor();
      }
      else
         ((CComboBox)GetDlgItem(IDC_COST_TYPE)).SetCurSel(distance((std::set<CItem>::const_iterator)costSet.begin(), ctsel));

      wchar_t *docs[] = { L"полный", L"вложить чек", L"минимальный", NULL };
      LoadComboBox(IDC_DOCUMENTS, docs, ((o->params & ofDocsMask) >> 2));

      wchar_t *pay[] = { L"по накладной", L"безнал", L"суммы", L"без оплаты", NULL };
      LoadComboBox(IDC_PAY, pay, ((o->params & ofPayMask) >> 4));

      BOOL bHandled = TRUE;
      CheckPay(0, 0, 0, bHandled);
      SetScalingValue(IDC_SUM, o->paySum, SUM_SCALE, false);
   }

   void OnChangeDogovor()
   {
      CComboBox dog(GetDlgItem(IDC_DOGOVORS));
      int cs = dog.GetCurSel();
      if( cs < 0 || cs >= (int)org.dogovors.size() ) return;

      const Dogovor &dg = org.dogovors[cs];
      int len = wcslen(dg.costType) + 1;
      if( len <= 1 ) return;

      char *buf = (char*)alloca(len * sizeof(char));
      wcstombs(buf, dg.costType, len);
      buf[len-1] = '\0';

      std::set<CItem>::const_iterator cti = costSet.begin();
      for( ; cti != costSet.end(); cti++ )
      {
         if( strcmp(cti->type, buf) == 0 )
         {
            ((CComboBox)GetDlgItem(IDC_COST_TYPE)).SetCurSel(distance((std::set<CItem>::const_iterator)costSet.begin(), cti));
            break;
         }
      }
   }

   virtual void Sizing(WORD width, WORD height)
   {
      ResizeWindow(IDC_DOGOVORS, width);
      ResizeWindow(IDC_COST_TYPE, width);
      ResizeWindow(IDC_DOCUMENTS, width);
      ResizeWindow(IDC_PAY, width);

      OrderPage::Sizing(width, height);
   }

   virtual bool Valid() const
   {
      int cs = ((CComboBox)GetDlgItem(IDC_DOGOVORS)).GetCurSel();
      if( cs < 0 )
      {
         ::MessageBox(m_hWnd, L"Ошибка", L"Укажите договор", MB_OK | MB_ICONSTOP);
         return false;
      }

      return true;
   }


   virtual void Save(OrderImpl *order)
   {
      SYSTEMTIME st;
      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
      ResetTime(&st);
      SystemTimeToFileTime(&st, &order->date);

      ((CDateTimePickerCtrl)GetDlgItem(IDC_DELIVERY_DATE)).GetSystemTime(&st);
      ResetTime(&st);
      SystemTimeToFileTime(&st, &order->supplDate);

      int cs = ((CComboBox)GetDlgItem(IDC_DOGOVORS)).GetCurSel();
      if( cs >= 0 )
      {
         order->supplyer = cs;
         order->dogNum = order->holder.Add(org.dogovors[cs].number);
      }

      cs = ((CComboBox)GetDlgItem(IDC_COST_TYPE)).GetCurSel();
      if( cs >= 0 )
      {
         std::set<CItem>::const_iterator i = costSet.begin();
         advance(i, cs);

         const CostManager::CostList &clist = CostManager::CostTypes();
         CostManager::CostList::const_iterator ci = clist.begin();
         for( ; ci != clist.end(); ci++ )
         {
            if( ci->id.compare(i->type) == 0 )
            {
               cs = distance(clist.begin(), ci);
               break;
            }
         }
         if( cs != order->sumType )
         {
            if( order->sumType != (WORD)-1 &&
               MessageBox(L"Пересчитать цены в заявке ?", L"Вопрос", MB_YESNO|MB_ICONQUESTION) == IDYES )
            {
               order->ChangeCost(cs);
            }
            order->sumType = cs;
            order->sumTypeID = order->holder.Add(i->type);
         }
      }

      wchar_t dbuf[100];
      *dbuf = L'\0';
      GetDlgItemText(IDC_SUM, dbuf, sizeof(dbuf)/sizeof(dbuf[0]));
      order->paySum = (WORD)GetValue(dbuf, SUM_SCALE);

      cs = ((CComboBox)GetDlgItem(IDC_DOCUMENTS)).GetCurSel();
      if( cs >= 0 )
      {
         order->params &= (~ofDocsMask);
         order->params |= ((cs << 2) & ofDocsMask);
      }

      cs = ((CComboBox)GetDlgItem(IDC_PAY)).GetCurSel();
      if( cs >= 0 )
      {
         order->params &= (~ofPayMask);
         order->params |= ((cs << 4) & ofPayMask);
      }
   }

   NumInput numInput;
   OrgImpl org;
};

class OrderPage2 : public OrderPage
{
public:
   OrderPage2() : OrderPage(IDD_DETAIL_PAGE2, L"Дополнительно") {}

   virtual bool Valid() const { return true; }

   virtual void Init()
   {
      OrderImpl *o = ((OrderDetailDialog*)owner)->Order();

      if( o->IsExported() )
         DisableChilds();

      if( o->params & ofInvoice )
         CheckDlgButton(IDD_ACCOUNT, BST_CHECKED);

      if( o->params & ofReestr )
         CheckDlgButton(IDC_REGISTRY, BST_CHECKED);

      if( o->params & ofSert )
         CheckDlgButton(IDC_SERTIFICAT, BST_CHECKED);

      if( o->params & ofOther )
         CheckDlgButton(IDC_OTHER, BST_CHECKED);

      GetDlgItem(IDC_REMARK).SetWindowText(o->remark);
   }

   virtual void Sizing(WORD width, WORD height)
   {
      OrderPage::Sizing(width, height);

      CRect rc1, rc2;
      GetDlgItem(IDCANCEL).GetWindowRect(rc1);
      ScreenToClient(rc1);

      CWindow rem(GetDlgItem(IDC_REMARK));
      rem.GetWindowRect(rc2);
      ScreenToClient(rc2);
      rc2.bottom = rc1.top - offset;
      rc2.right = width - offset;
      rc2.left = offset;
      rem.MoveWindow(rc2);
   }

   virtual void Save(OrderImpl *order)
   {
      if( m_hWnd == NULL ) return;
      CWindow wnd(GetDlgItem(IDC_REMARK));

      int len = wnd.GetWindowTextLength() + 1;
      wchar_t *buf = (wchar_t*)alloca(len* sizeof(wchar_t));
      wnd.GetWindowText(buf, len);
      order->AssignRemark(buf);

      if( IsDlgButtonChecked(IDD_ACCOUNT) == BST_CHECKED ) order->params |= ofInvoice;
      else order->params &= (~ofInvoice);

      if( IsDlgButtonChecked(IDC_REGISTRY) == BST_CHECKED ) order->params |= ofReestr;
      else order->params &= (~ofReestr);

      if( IsDlgButtonChecked(IDC_SERTIFICAT) == BST_CHECKED ) order->params |= ofSert;
      else order->params &= (~ofSert);

      if( IsDlgButtonChecked(IDC_OTHER) == BST_CHECKED ) order->params |= ofOther;
      else order->params &= (~ofOther);
   }
};

OrderDetailDialog::OrderDetailDialog(OrderImpl *_order) : order(_order)
{
   OrgImpl o;
   o.id = _order->id;
   o.Read();
   SetTitle(o.name);

   AddPage(new OrderPage1());
   AddPage(new OrderPage2());
}

bool EditOrderDetail(OrderImpl *order)
{
   OrderDetailDialog dlg(order);
   return (dlg.DoModal() == IDOK);
}

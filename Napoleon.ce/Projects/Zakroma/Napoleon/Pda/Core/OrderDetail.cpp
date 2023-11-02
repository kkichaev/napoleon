/*
* Copyright (C), 2007, Денис Мосягин
*
* Детали заказа
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

#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>

#include "BaseDialog.h"
#include "NplConfig.h"

#include "PropDialog.h"
#include "SAnchor.h"
#include <StdFuncs.h>
#include <ObjImpl.h>
#include "AddRes.h"

const wchar_t REMARK_TEXT[] = L"Общее примечание...";

class OrderPage : public PropPage
{
public:
   OrderPage(WORD wID, ATL::_U_STRINGorID title = (LPCTSTR)NULL) : PropPage(wID, title) {}
   virtual void Save(OrderImpl *order) = 0;

   void SetChildWidth(UINT id, WORD width)
   {
      CWindow wnd(GetDlgItem(id));
      CRect rc;

      wnd.GetWindowRect(rc);
      ScreenToClient(rc);
      rc.right = width - offset;
      wnd.MoveWindow(rc);
   }

   bool GetDlgItemRect(LPRECT r, int id)
   {
      CWindow w(GetDlgItem(id));
      
      if( w.m_hWnd == NULL ) return false;

      w.GetWindowRect(r);
      ScreenToClient(r);
      return true;
   }
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
         ((OrderPage*)(*i))->Save(order);

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
   OrderPage1() : OrderPage(IDD_ORDER_DETAIL, L"Основная") {}

   BEGIN_MSG_MAP(OrderPage1)
      COMMAND_HANDLER(IDC_PHONE, STN_CLICKED, DoCall)
      COMMAND_HANDLER(IDC_PHONE1, STN_CLICKED, DoCall)
      CHAIN_MSG_MAP(OrderPage)
   END_MSG_MAP()

   std::wstring phone1, phone2;
   LRESULT DoCall(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled)
   {
      MakeCall((idc == IDC_PHONE) ? phone1.c_str() : phone2.c_str());
      return 0;
   }

protected:
   virtual void Init()
   {
      OrderImpl *order = ((OrderDetailDialog*)owner)->Order();
      if( order->IsExported() )
         DisableChilds();

      SYSTEMTIME st;
      FileTimeToSystemTime(&order->date, &st);

      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

      CComboBox fact(GetDlgItem(IDC_DOCUMENTS));
      fact.AddString(L"Отсрочка");
      fact.AddString(L"Нал/Факт");

      if( (order->params & ofFact) != NULL ) fact.SetCurSel(1);
      else fact.SetCurSel(0);

      OrgImpl org;
      org.id = order->id;
      org.Read();
      SetWindowText(org.name);

      SetDlgItemText(IDC_REMARK, order->logistic);
      SetDlgItemText(IDC_REMARK2, order->fcontrol);

      NapoleonConfig ncfg;
      ncfg.ReadValue(&phone1, L"Логист");
      ncfg.ReadValue(&phone2, L"ФинКонтроль");

      sphone1.SubclassWindow(GetDlgItem(IDC_PHONE));
      sphone2.SubclassWindow(GetDlgItem(IDC_PHONE1));

      if( phone1.empty() ) sphone1.ShowWindow(SW_HIDE);
      else sphone1.SetWindowText(phone1.c_str());

      if( phone2.empty() ) sphone2.ShowWindow(SW_HIDE);
      else sphone2.SetWindowText(phone2.c_str());

      if( (order->params & ofPayBefore) != 0 )
         CheckDlgButton(IDC_PAY_GUARANTY, BST_CHECKED);
   }

   virtual void Save(OrderImpl *order)
   {
      SYSTEMTIME st;
      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
      SystemTimeToFileTime(&st, &order->date);

      if( IsDlgButtonChecked(IDC_PAY_GUARANTY) == BST_CHECKED )
         order->params |= ofPayBefore;
      else
         order->params &= (~ofPayBefore);

      wchar_t buf[300];
      GetDlgItemText(IDC_REMARK, buf, sizeof(buf)/sizeof(buf[0]));
      order->logistic = order->holder.Add(buf);

      GetDlgItemText(IDC_REMARK2, buf, sizeof(buf)/sizeof(buf[0]));
      order->fcontrol = order->holder.Add(buf);

      CComboBox fact(GetDlgItem(IDC_DOCUMENTS));
      if( fact.GetCurSel() == 0 ) order->params &= (~ofFact);
      else order->params |= ofFact;
   }

   void SetRemarkSize(UINT id, WORD width)
   {
      CRect rc;
      CWindow wnd(GetDlgItem(id));
      wnd.GetWindowRect(rc);
      ScreenToClient(rc);
      rc.left = 2;
      rc.right = width - 4;
      wnd.MoveWindow(rc, FALSE);
   }

   virtual void Sizing(WORD width, WORD height)
   {
      OrderPage::Sizing(width, height);

      SetRemarkSize(IDC_REMARK, width);
      SetRemarkSize(IDC_REMARK2, width);
   }

   StaticAnchor sphone1;
   StaticAnchor sphone2;
};

class OrderPage3 : public CSimpleDialog<IDD_DETAIL_PAGE3, TRUE>
{
public:
   OrderPage3(OrderImpl* order)
   {
      this->order = order;
   }

   typedef CSimpleDialog<IDD_DETAIL_PAGE3, TRUE> BaseClass;
 
   BEGIN_MSG_MAP(OrderPage3)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_ID_HANDLER(IDOK, Close)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT , WPARAM , LPARAM , BOOL& bHandled)
   {
      if( (order->params & ofSert) != 0 )
         CheckDlgButton(IDC_SERTIFICAT, BST_CHECKED);
      if( (order->params & ofQuality) != 0 )
         CheckDlgButton(IDC_OTHER, BST_CHECKED);
      if( (order->params & ofDate) != 0 )
         CheckDlgButton(IDC_DOCUMENTS, BST_CHECKED);

      bHandled = FALSE;
      return 0;
   }

   LRESULT Close(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
   {
      if( IsDlgButtonChecked(IDC_SERTIFICAT) == BST_CHECKED )
         order->params |= ofSert;
      else
         order->params &= (~ofSert);
      if( IsDlgButtonChecked(IDC_OTHER) == BST_CHECKED )
         order->params |= ofQuality;
      else
         order->params &= (~ofQuality);
      if( IsDlgButtonChecked(IDC_DOCUMENTS) == BST_CHECKED )
         order->params |= ofDate;
      else
         order->params &= (~ofDate);

      bHandled = FALSE;
      return 0;
   }
 
protected:
   OrderImpl *order;
};

class OrderPage2 : public OrderPage
{
public:
   OrderPage2() : OrderPage(IDD_DETAIL_PAGE2, L"Дополнительно") {}

   BEGIN_MSG_MAP(OrderPage2)
      COMMAND_ID_HANDLER(IDC_DOCUMENTS, ShowDocuments)
      CHAIN_MSG_MAP(OrderPage)
   END_MSG_MAP()

   virtual void Init()
   {
      order = ((OrderDetailDialog*)owner)->Order();

      SetScalingValue(IDC_SUM, order->collectSum, SUM_SCALE, false);
      SetDlgItemText(IDC_DOC_NUMBER, order->collectNum);

      if( order->IsExported() )
         DisableChilds();
   }

   virtual void Save(OrderImpl *order)
   {
      if( m_hWnd == NULL ) return;

      wchar_t dbuf[100];
      *dbuf = L'\0';
      GetDlgItemText(IDC_SUM, dbuf, sizeof(dbuf)/sizeof(dbuf[0]));
      order->collectSum = (DWORD)GetValue(dbuf, SUM_SCALE);

      wchar_t buf[1000];
      GetDlgItemText(IDC_DOC_NUMBER, buf, sizeof(buf)/sizeof(buf[0]));
      order->collectNum = order->holder.Add(buf);
  }

   LRESULT ShowDocuments(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      if( MessageBox(L"Ненужная выписка документов задерживает работу склада!!!\nВы хотите выписать документы?", L"Предепреждение", MB_YESNO | MB_ICONQUESTION) == IDYES )
      {
         OrderPage3 dlg(order);
         dlg.DoModal();
      }
      return 0;
   }

   OrderImpl *order;
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

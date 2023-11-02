/*
 * Copyright (C), 2006-2013, Денис Мосягин
 *
 * Список заявок
 *
 *  ert   22/04/2013   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "DocImpl.h"
#include "FormEntries.h"
#include "PictButton.h"

#include <Preference.h>
#include "AppBaseForm.h"
#include "BaseDialog.h"

#include "MainFrame.h"
#include "Qty.h"

#include "syslib.h"

#ifdef ZEBEX
typedef BOOL (*pZBCRSetPower)(BOOL dwState);
typedef BOOL (*pGetLastNotifyEvent) (PDWORD lpNotifyEvent);
typedef BOOL (*pGetLastBarcode) (LPTSTR lpszBarcode);
typedef BOOL (*pZBCRStartScan) (void);
typedef BOOL (*pZBCRStopScan) (void);
typedef BOOL (*pZBCRSetOutputMode) (BYTE dwMode);

typedef BOOL (*pSysSetFxKeyState)(DWORD dwVKCode,BOOL dwEnableState);
#endif

class OrderListData : public IFormData
{
public:
   OrderListData();
   ~OrderListData();

   ControlDocImpl doc;
protected:
};

#ifdef ZEBEX
class OrderListForm : public AppBaseForm, public BarcodeHandler
#else
class OrderListForm : public AppBaseForm
#endif
{
public:
   OrderListForm() : receivingPrice(false) {}
   ~OrderListForm() { delete data; }

   virtual bool SetData(IFormData *_data);

   BEGIN_MSG_MAP(OrderListForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDOK, OnReturn)
      COMMAND_ID_HANDLER(IDC_FIND, OnInputBC)
      COMMAND_ID_HANDLER(IDC_SAVE, OnSave)
		MESSAGE_HANDLER(WM_SCAN_DATA, ScanData)
      CHAIN_MSG_MAP(AppBaseForm)
   END_MSG_MAP()

   virtual DWORD GetResourceID() const { return IDD_ORDER_LIST; }
   virtual DWORD GetMenuID() const { return -1; }

   virtual void UpdateLayout(const RECT& bounds, bool forceRecalc);

   DECLARE_FORM(OrderListForm, IDD_ORDER_LIST)

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnInputBC(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnReturn(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

   LRESULT OnSave(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

protected:
   OrderListData* data;

   void OnBarcode(const wchar_t* barcode);
   void SaveItem(int qty);

#ifdef ZEBEX
   virtual void HandleEvent();
   HMODULE hLib;
   pGetLastNotifyEvent GetBCEvent;
   pGetLastBarcode GetBarcode;
#endif

	std::wstring id;

   bool receivingPrice;
};

IMPLEMENT_FORM(OrderListForm);

//
//----------------------------------- OrderListData ----------------------------------
//
OrderListData::OrderListData()
{
   std::vector<ROWID> rids;
   SQLTable t(doc.Name());
   t.RIDList(&rids, L"WHERE params=0");

   if( rids.size() > 0 )
      doc.Read(rids.front());
   else
   {
      SYSTEMTIME st;
      GetLocalTime(&st);
      SystemTimeToFileTime(&st, &doc.created);

      //SQLTable::DropTable(PriceImpl().Name());
   }
}

OrderListData::~OrderListData()
{
}

//
//-------------------------------- OrderListForm --------------------------------
//
bool OrderListForm::SetData(IFormData *_data)
{
   data = (OrderListData*)_data;

#ifdef ZEBEX
   hLib = LoadLibrary(L"zbcrlib.dll");
   pZBCRSetPower fn = (pZBCRSetPower)GetProcAddress(hLib, L"ZBCRSetPower");
   fn(TRUE);

   pZBCRSetOutputMode outMode = (pZBCRSetOutputMode)GetProcAddress(hLib, L"ZBCRSetOutputMode");
   outMode(BCR_DISABLE_OUTPUT);

   GetBCEvent = (pGetLastNotifyEvent)GetProcAddress(hLib, L"ZBCRGetLastNotifyEvent");
   GetBarcode = (pGetLastBarcode)GetProcAddress(hLib, L"ZBCRGetLastBarcode");
   ((MainFrame*)_Module.GetFrame())->SetBarcodeHandler(this);
#else
   StartScan(m_hWnd);
#endif

   //SetFKey(VK_F1, FALSE);

   GetDlgItem(IDC_BARCODE).SetFocus();
   GetDlgItem(IDC_QTY).EnableWindow(FALSE);
   GetDlgItem(IDC_SAVE).EnableWindow(FALSE);

   CWindow info(GetDlgItem(IDC_INFO));
   CreateFont(&font, 18, true);
   info.SetFont(font);

   return true;
}

LRESULT OrderListForm::OnInputBC(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   std::wstring buf;
   GetString(&buf, GetDlgItem(IDC_BARCODE));
   OnBarcode(buf.c_str());

   return 0;
}

LRESULT OrderListForm::OnReturn(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   HWND hf = GetFocus();
   if( hf == GetDlgItem(IDC_BARCODE).m_hWnd )
      OnInputBC(nCode, id, hWnd, bHandled);
   else if( hf == GetDlgItem(IDC_QTY).m_hWnd )
      OnSave(nCode, id, hWnd, bHandled);
   return 0;
}

static wchar_t* DoConvert(long value, int scale, bool hideRest)
{
   static wchar_t buf[20];
   wchar_t src[20];
   ConvertScaling(src, (long)value, scale);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % scale, scale, hideRest);
   return buf;
}

void OrderListForm::OnBarcode(const wchar_t* barcode)
{
   if( receivingPrice )
      return;

   receivingPrice = true;
   CWindow find(GetDlgItem(IDC_FIND));
   find.EnableWindow(FALSE);

   CEdit qty(GetDlgItem(IDC_QTY));

   std::wstring nameBuf;
   PriceImpl p;
   bool bdo = p.Get(barcode, false);
   //SQLTable tp(p.Name());
   //std::wstring whereStr = L"WHERE barcode like '%|";
   //whereStr += barcode; whereStr += L"|%'";
   //bool bdo = tp.Select(&p, whereStr.c_str());
   if( !bdo )
   {
      //std::wstring answer;
      //if( _Module.ReceivePrice(barcode, &answer) != 0 )
      //{
      //   ::MessageBox(NULL, (answer.empty() ? L"Ошибка при приеме" : answer.c_str()), L"Ошибка", MB_OK | MB_ICONSTOP);
      //   qty.EnableWindow(FALSE);
      //   GetDlgItem(IDC_SAVE).EnableWindow(FALSE);

      //   receivingPrice = false;
      //   find.EnableWindow(TRUE);
      //   return;
      //}

      //bdo = p.Get(barcode, false);
      ////bdo = tp.Select(&p, whereStr.c_str());
      if( !bdo )
      {
         p.id = (wchar_t*)barcode;
         p.cost = 0;
         nameBuf = L"Код товара ";
         nameBuf += barcode;
         p.name = (wchar_t*)nameBuf.c_str();
      }
   }
   receivingPrice = false;
   find.EnableWindow(TRUE);

   CEdit bc(GetDlgItem(IDC_BARCODE));
   bc.SetWindowText(barcode);
   bc.SetSelAll();
   id = p.id;

   int iqty = p.IsWeight() ? p.GetWeight() : QTY_SCALE;

   wchar_t *buf = DoConvert(iqty, QTY_SCALE, false);

   qty.SetWindowText(buf);
   qty.EnableWindow(TRUE);
   qty.SetSelAll();
   qty.SetFocus();

   GetDlgItem(IDC_SAVE).EnableWindow(TRUE);

   std::wstring text;
   text = p.name;
   text += L"\nЦена: ";

   buf = DoConvert(p.cost, SUM_SCALE, false);
   text += buf;
   GetDlgItem(IDC_INFO).SetWindowText(text.c_str());

   text = L"Код: ";
   text += p.id;
   GetDlgItem(IDC_ID).SetWindowText(text.c_str());

   text = L"ШК: ";
   text += barcode;
   GetDlgItem(IDC_BAR).SetWindowText(text.c_str());

   int tqty = data->doc.TotalQty(p.id);
   buf = DoConvert(tqty, QTY_SCALE, false);
   text = L"Общее кол-во: ";
   text += buf;
   GetDlgItem(IDC_ORDER_QTY).SetWindowText(text.c_str());

   if( IsDlgButtonChecked(IDC_AUTO_SAVE) == BST_CHECKED )
   {
      SaveItem(iqty);
   }
}

void OrderListForm::SaveItem(int qty)
{
   if( id.empty() )
   {
      return;
   }

   data->doc.Update(id.c_str(), qty);
   GetDlgItem(IDC_QTY).EnableWindow(FALSE);
   GetDlgItem(IDC_SAVE).EnableWindow(FALSE);
   GetDlgItem(IDC_BARCODE).SetFocus();

   int tqty = data->doc.TotalQty(id.c_str());
   const wchar_t* buf = DoConvert(tqty, QTY_SCALE, false);
   std::wstring text = L"Общее кол-во: ";
   text += buf;
   GetDlgItem(IDC_ORDER_QTY).SetWindowText(text.c_str());

   id.clear();
}

LRESULT OrderListForm::OnSave(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   int iqty = GetValue(GetDlgItem(IDC_QTY), QTY_SCALE);
   SaveItem(iqty);

   return 0;
}

LRESULT OrderListForm::ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   std::wstring data;
   if( GetScanData(&data, lParam) )
		OnBarcode(data.c_str());

	return 1;
}


#ifdef ZEBEX
void OrderListForm::HandleEvent()
{
   DWORD eventCode;

   if( GetBCEvent(&eventCode) && eventCode == BCR_NOTIFY_RECEIVE_BARCODE )
   {
      wchar_t buf[MAX_PATH], *cp;
      GetBarcode(buf);

      cp = wcschr(buf, L'\r');
      if( cp ) *cp = L'\0';
      cp = wcschr(buf, L'\n');
      if( cp ) *cp = L'\0';
      OnBarcode(buf);
   }
}
#endif

LRESULT OrderListForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
#ifdef ZEBEX
   ((MainFrame*)_Module.GetFrame())->SetBarcodeHandler(NULL);

   pZBCRSetPower fn = (pZBCRSetPower)GetProcAddress(hLib, L"ZBCRSetPower");
   fn(FALSE);

   FreeLibrary(hLib);

   //SetFKey(VK_F1, TRUE);
#else
   StopScan();
#endif

   OpenCtrlDocList(); 
   return 0;
}

void OrderListForm::UpdateLayout(const RECT& bounds, bool forceRecalc)
{
   CRect rc, rc2;

   CWindow wnd(GetDlgItem(IDC_FIND));
   wnd.GetWindowRect(rc);
   ScreenToClient(rc);

   CWindow wnd2(GetDlgItem(IDC_BARCODE));
   wnd2.GetWindowRect(rc2);
   ScreenToClient(rc2);

   int offset = rc2.left;
   int diff = bounds.right - rc.right - offset;
   rc.OffsetRect(diff, 0);
   wnd.MoveWindow(rc);
   wnd2.MoveWindow(rc2.left, rc2.top, rc.left - rc2.left - offset, rc2.Height());

   CWindow wnd3(GetDlgItem(IDC_SAVE));
   wnd3.GetWindowRect(rc2);
   ScreenToClient(rc2);
   rc2.OffsetRect(diff, 0);
   wnd3.MoveWindow(rc2);

   CWindow ord(GetDlgItem(IDC_ORDER_QTY));
   ord.GetWindowRect(rc2);
   ScreenToClient(rc2);
   diff = bounds.bottom - rc2.bottom - offset;
   rc2.OffsetRect(0, diff);
   ord.MoveWindow(rc2);

   CWindow code(GetDlgItem(IDC_ID));
   code.GetWindowRect(rc2);
   ScreenToClient(rc2);
   rc2.OffsetRect(0, diff);
   code.MoveWindow(rc2);

   CWindow bc(GetDlgItem(IDC_BAR));
   bc.GetWindowRect(rc2);
   ScreenToClient(rc2);
   rc2.right = bounds.right - offset;
   rc2.OffsetRect(0, diff);
   bc.MoveWindow(rc2);

   CWindow info(GetDlgItem(IDC_INFO));
   info.GetWindowRect(rc2);
   ScreenToClient(rc2);
   rc2.bottom += diff;
   rc2.right = bounds.right - offset;
   info.MoveWindow(rc2);

   //int top = rc.top;
   //int right = bounds.right - rc.left - rc2.Width() - 2;
   //wnd.MoveWindow(rc.left, rc.top, right, rc.Height());

   //CWindow wnd1(GetDlgItem(IDC_RACK));
   //wnd1.GetWindowRect(rc);
   //ScreenToClient(rc);
   //wnd1.MoveWindow(rc.left, rc.top, right, rc.Height());

   //btn.MoveWindow(rc.left + right+1, top + (rc.bottom - rc2.Height()) / 2, rc2.Width(), rc2.Height());
}

void OpenOrderList()
{
   OrderListData *pfd = new OrderListData();
   _Module.GetFrame()->Load(IDD_ORDER_LIST, pfd);
}
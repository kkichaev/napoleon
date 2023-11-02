/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Функционал для окна организаций
 *
 *  ert   30/10/2007   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "OrgList.h"
#include "OrgFuncs.h"
#include "Progress.h"
#include "OrgDocs.h"

#include "DocImpl.h"
#include "FormEntries.h"

#include <StdFuncs.h>

#ifdef GPS_POS
#include <Apps.h>
#include <atlcrack.h>

typedef void (*GetStatesT)(ModuleStates* states);

class IcoView : public CWindowImpl<IcoView, CStatic>
{
   typedef CWindowImpl<IcoView, CStatic> TBase;

public:
   IcoView();
   ~IcoView();

   void SetState(const ModuleStates* newState);

   bool Create(HWND parent, ATL::_U_RECT bounds, UINT id);
   void OnPaint(HDC );

   BEGIN_MSG_MAP(IcoView)
      MSG_WM_PAINT(OnPaint)
   END_MSG_MAP()

   DWORD ico;
};

IcoView::IcoView() : ico(IDC_NONE)
{
}

IcoView::~IcoView()
{
}

void IcoView::SetState(const ModuleStates* newState)
{
   if( newState->gps == Module::stWork ) ico = IDC_GPS_TRACK;
   else if( newState->gsm == Module::stWork ) ico = IDC_GSM_TRACK;
   else if( newState->gps == Module::stInit || newState->gsm == Module::stInit ) ico = 0;
   else ico = IDC_NONE;
}

bool IcoView::Create(HWND parent, ATL::_U_RECT bounds, UINT id)
{
   TBase::Create(parent, bounds, NULL, WS_CHILD|SS_NOTIFY|WS_VISIBLE, 0, id);
   return (m_hWnd != NULL);
}

void IcoView::OnPaint(HDC)
{
   CRect rc;
   PAINTSTRUCT pPaint;

   HDC hdc = BeginPaint(&pPaint);

   GetClientRect(rc);

   SelectObject(hdc, GetStockObject(BLACK_PEN));
   SelectObject(hdc, GetStockObject(WHITE_BRUSH));
   Rectangle(hdc, rc.left, rc.top, rc.right, rc.bottom);

   if( ico != 0 )
   {
      int wdh = GetSystemMetrics(SM_CXSMICON);
      HICON hIco = (HICON)LoadImage(_Module.GetModuleInstance(), MAKEINTRESOURCE(ico), IMAGE_ICON, wdh, wdh, 0);
      DrawIconEx(hdc, (rc.right - wdh) / 2, (rc.bottom - wdh) / 2, hIco, wdh, wdh, 0, NULL, DI_NORMAL);
      DestroyIcon(hIco);
   }

   EndPaint(&pPaint);
}

IcoView *icoView;

void RefreshIco(const ModuleStates* newState)
{
   if( icoView )
   {
      icoView->SetState(newState);
      icoView->Invalidate();
      icoView->UpdateWindow();
   }
}

#endif

OrgData::OrgData()
{
   docType = docTypeManager.GetDocType(dtOrder);
}

void OrgData::DestroyData()
{
#ifdef GPS_POS
   IcoView *svI = icoView;
   icoView = NULL;
   delete svI;
#endif
}

#if defined(ORG_COLOR) && defined(STOP_LIST)
COLORREF OrgData::GetItemColor(int index, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd) const
{
   OrgImpl org;
   if( !org.Read(GetOID(index)) )
      return defaultColor;

   if( (org.flags & ofStopList) != 0 ) return RGB(192,192,192);

   if( org.color != 0 )
      return org.color;
   return (orgIds.find(org.id) == orgIds.end()) ? defaultColor : RGB(0, 192, 0);
}
#elif ORG_COLOR
COLORREF OrgData::GetItemColor(int index, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd) const
{
   OrgImpl org;
   if( !org.Read(GetOID(index)) )
      return defaultColor;

   if( org.color != 0 )
      return org.color;
   return (orgIds.find(org.id) == orgIds.end()) ? defaultColor : RGB(0, 192, 0);
}
#elif STOP_LIST
COLORREF OrgData::GetItemColor(int index, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd) const
{
   OrgImpl org;
   if( !org.Read(GetOID(index)) )
      return defaultColor;

   if( (org.flags & ofStopList) != 0 ) return RGB(192,192,192);
   return (orgIds.find(org.id) == orgIds.end()) ? defaultColor : RGB(0, 192, 0);
}
#else
COLORREF OrgData::GetItemColor(int index, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd) const
{
   OrgImpl org;
   if( !org.Read(GetOID(index)) )
      return defaultColor;

   return (orgIds.find(org.id) == orgIds.end()) ? defaultColor : RGB(0, 192, 0);
}
#endif

void OrgData::SetDocType(const wchar_t *type)
{
   orgIds.clear();
   const DocType *dt = docTypeManager.GetDocType(type);
   if( dt != NULL )
   {
      docType = dt;

      if( dt->IsCreatable() )
      {
         DocumentList *orgDocs = NULL;
         SYSTEMTIME st;
         __int64 from, till;
         GetLocalTime(&st);
         ResetTime(&st);
         SystemTimeToFileTime(&st, (FILETIME*)&from);
         till = from + (__int64)3600 * 24 * 10000000;
         wchar_t buf[200];
         const wchar_t *field = ( type == dtOrder ) ? L"created" : L"date";
         wsprintf(buf,  L"%s >= %d%09d and %s < %d%09d", field, (DWORD)(from / 1000000000), (DWORD)(from % 1000000000),
            field, (DWORD)(till / 1000000000), (DWORD)(till % 1000000000));
         if( dt->GetDocuments(L"", &orgDocs, buf, L"") )
         {
            for( unsigned i=0; i<orgDocs->Count(); i++ )
            {
               IDocument *d = orgDocs->Get(i);
               orgIds.insert(d->ID());
            }
         }
         delete orgDocs;
      }
   }
}

OrgFuncs::OrgFuncs(ListForm *_form) : 
   form(_form), orgData(NULL), search(IDC_FIND, IDC_SEARCH_ORG)
{
}

void OrgFuncs::SetOrgData(OrgData *orgData, SearchControl::ISearchEvent* handler)
{
   this->orgData = orgData;
   search.SetHandler(form->m_hWnd, handler);
}

void OrgFuncs::UpdateLayout(const CRect& bounds)
{
   if( search.m_hWnd == NULL ) return;

   CRect rc;
   form->GetParent().GetClientRect(rc);
   int itemHeight = bounds.Height();

#ifdef GPS_POS
   search.UpdateLayout(rc.Width() - itemHeight, itemHeight, form->listCtrl.GetFont());

   rc = bounds;
   search.GetWindowRect(rc);
   form->ScreenToClient(rc);

   rc.left = rc.right;
   rc.right += itemHeight;

   if( icoView == NULL )
   {
      icoView = new IcoView();
      icoView->Create(form->m_hWnd, rc, IDC_GPS_TRACK);
   } else
   {
      icoView->MoveWindow(rc, TRUE);
   }
   HANDLE hApps = _Module.AppsIntance();
   if( hApps )
   {
      GetStatesT GetAppsState = (GetStatesT)GetProcAddress((HMODULE)hApps, L"GetStates");
      ModuleStates states;
      GetAppsState(&states);

      RefreshIco(&states);
   }
#else
   search.UpdateLayout(rc.Width(), itemHeight, form->listCtrl.GetFont());
#endif
}

LRESULT OrgFuncs::CreateOrder(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   int sel = form->GetSelected();
   if( sel >= 0 )
      AddNewDocument(form, orgData->GetDocType(), orgData->GetOID(sel));

   return 0;
}

void OrgFuncs::LoadMenuBar(bool hideSIP)
{
   form->LoadMenuBar(hideSIP);

   TBBUTTONINFO bi;
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_TEXT;
   bi.pszText = (LPWSTR)orgData->GetDocType()->Type();
   form->menuBar.SetButtonInfo(IDC_VIEW_TYPE, &bi);

   //if( hideSIP )
   {
      form->sumLabel.CreateLabel(form->menuBar.m_hWnd, SumLabel::STD_WIDTH, GetSystemMetrics(SM_CXSMICON) * 9 / 4);
      form->sumLabel.SetSum(orgData->GetSum());

      form->listCtrl.SetFocus();
   }

   HMENU hMenu = (HMENU)SendMessage(form->menuBar.m_hWnd, SHCMBM_GETMENU, 0, 0);
   if( hMenu != NULL )
   {
      HMENU hSubMenu = GetSubMenu(hMenu, 1);
      if( hSubMenu != NULL )
         InsertMenu(hSubMenu, 0, MF_BYPOSITION | MF_OWNERDRAW, IDC_POWER, L"");
   }
}

LRESULT OrgFuncs::SetViewType(int id, LPNMHDR header, BOOL &handled)
{
   if( ((NMTOOLBAR*)header)->iItem != IDC_VIEW_TYPE ) return 0;

   const DocType *dt = SelectDocType(&form->menuBar, form->m_hWnd);
   if( dt != NULL )
   {
      orgData->SetDocType(dt->Type());
      form->Refresh();
      form->sumLabel.SetSum(orgData->GetSum());
   }
   return 0;
}

LRESULT OrgFuncs::ReceivePrice(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   HWND activeWindow = GetActiveWindow();
   ProgressWindow pw;
   pw.CreateSTDWindow(activeWindow);
   std::wstring answer;

   PriceImpl p;
   SQLTable table(p.Name());
   bool havePrice = table.Select(&p);

   long ec = (havePrice) ? _Module.ReceiveRemnants(&answer, &pw) : 
      _Module.ReceivePrice(&answer, &pw, false, false);
   
   pw.DestroyWindow();

   if( ec )
   {
      _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при приеме:\n");
   } else
   {
      if( havePrice == false )
      {
         form->Refresh();
      }
      ::MessageBox(activeWindow, L"Новый прайс принят", L"Подтверждение", MB_OK|MB_ICONINFORMATION);
   }

#ifdef RCV_MESSAGE
      _Module.ShowMessage();
#endif
   return 0;
}

LRESULT OrgFuncs::EditPreference(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   LoadMenuBar(false);
   _Module.ChangePreference();
   LoadMenuBar(false);
   return 0;
}

LRESULT OrgFuncs::OpenListDoc(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   ::OpenListDoc();
   return 0;
}

LRESULT OrgFuncs::PriceList(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenPriceList((OrderImpl*)NULL);
   return 0;
}

BOOL OrgFuncs::EventHandler(UINT uMsg, HWND hWnd, WPARAM wParam, LPARAM lParam, BOOL &bHandled, LRESULT &lResult)
{
   NOTIFY_CODE_HANDLER(TBN_ENDDRAG, SetViewType)
   COMMAND_ID_HANDLER(IDC_ADD, CreateOrder)
   COMMAND_ID_HANDLER(IDC_RCV_PRICE, ReceivePrice)
   COMMAND_ID_HANDLER(IDC_PREFERENCE, EditPreference)
   COMMAND_ID_HANDLER(IDD_ORDER_LIST, OpenListDoc)
   COMMAND_ID_HANDLER(IDD_PRICE_LIST, PriceList)

   MESSAGE_HANDLER(WM_MEASUREITEM, OnMeasureItem)
   MESSAGE_HANDLER(WM_DRAWITEM, OnDrawItem)

#ifdef RCV_MESSAGE
   if( uMsg == WM_COMMAND && IDD_MESSAGE_LIST == LOWORD(wParam) )
   {
      OpenMessageList();
      return 0;
   }
#endif

   if( uMsg == WM_NOTIFY && NM_CUSTOMDRAW == ((LPNMHDR)lParam)->code )
   {
      LPNMCUSTOMDRAW lpNMCustomDraw = (LPNMCUSTOMDRAW)lParam;
      switch(lpNMCustomDraw->dwDrawStage)
      {
         case CDDS_PREPAINT:
            lResult = CDRF_NOTIFYITEMDRAW;
            return TRUE;
            
         case CDDS_ITEMPREPAINT:
            lResult = OnItemPrePaint(wParam, lpNMCustomDraw);
            return TRUE;

         case CDDS_POSTPAINT:
         case CDDS_PREERASE:
         case CDDS_POSTERASE:
         case CDDS_ITEMPOSTPAINT:
         case CDDS_ITEMPREERASE:
         case CDDS_ITEMPOSTERASE:
            return CDRF_DODEFAULT;
      }
   }

   bHandled = FALSE;
   return FALSE;
}

DWORD OrgFuncs::OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
{
   NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
   lvcd->clrText = orgData->GetItemColor(lvcd->nmcd.dwItemSpec, form->listCtrl.GetTextColor(), lvcd);
   return CDRF_NOTIFYITEMDRAW;
}

#define MulDiv(a,b,c)       (((a)*(b))/(c))

HFONT CreateMenuFont(HDC hDC)
{
   HKEY hKey;
   LOGFONT lf = {0};
   if( RegOpenKeyEx(HKEY_LOCAL_MACHINE, L"System\\GWE\\Menu\\PopFnt", 0, 0, &hKey) == ERROR_SUCCESS )
//   if( false )
   {
      wchar_t buf[50];
      DWORD val;
      DWORD sz = sizeof(val);

      RegQueryValueEx(hKey, L"Ht", NULL, NULL, (BYTE*)&val, &sz);
      int pt = val;
      RegQueryValueEx(hKey, L"HtInPts", NULL, NULL, (BYTE*)&val, &sz);
      if( val != 0 )
      {
         //int lpy = GetDeviceCaps(hDC, LOGPIXELSY);
         lf.lfHeight = 17 * pt / 1000;//MulDiv((int)val, lpy, 72);
      } else
         lf.lfHeight = pt;

      RegQueryValueEx(hKey, L"Wt", NULL, NULL, (BYTE*)&val, &sz);
      lf.lfWeight = val;

      RegQueryValueEx(hKey, L"It", NULL, NULL, (BYTE*)&val, &sz);
      if( val != 0 )
         lf.lfItalic = TRUE;

      sz = sizeof(buf);
      RegQueryValueEx(hKey, L"Nm", NULL, NULL, (BYTE*)buf, &sz);
      wcscpy(lf.lfFaceName, buf);

      RegCloseKey(hKey);
   } else
   {
      if( GetObject(GetStockObject(SYSTEM_FONT), sizeof(lf), &lf) == FALSE ) return NULL;

      if( lf.lfHeight < 0 ) lf.lfHeight++;
      else lf.lfHeight--;
      lf.lfWeight = FW_BOLD;
   }

   return CreateFontIndirect(&lf);
}

LRESULT OrgFuncs::OnMeasureItem(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
   LPMEASUREITEMSTRUCT ms = (LPMEASUREITEMSTRUCT)lParam;
   if( ms->CtlType != ODT_MENU && ms->itemID != IDC_POWER )
   {
      bHandled = FALSE;
      return FALSE;
   }

   ms->itemHeight = GetSystemMetrics(SM_CYMENU);
   //ms->itemHeight = GetSystemMetrics(SM_CYSMICON) + 2;
   HDC dc = GetDC(form->m_hWnd);
   HFONT hf = CreateMenuFont(dc);
   if( hf != NULL )
   {
      RECT bounds = {0};
      HGDIOBJ svFont = SelectObject(dc, hf);
      DrawText(dc, L"Батарея 100%", -1, &bounds, DT_CALCRECT | DT_SINGLELINE);
      SelectObject(dc, svFont);
      DeleteObject(hf);

      ms->itemWidth = bounds.right + ms->itemHeight * 2;
   } else
      ms->itemWidth = 10;

   ReleaseDC(form->m_hWnd, dc);
   return TRUE;
}

LRESULT OrgFuncs::OnDrawItem(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
   LPDRAWITEMSTRUCT ds = (LPDRAWITEMSTRUCT)lParam;

   if( ds->CtlType != ODT_MENU && ds->itemID != IDC_POWER )
   {
      bHandled = FALSE;
      return FALSE;
   }

   int wdh = GetSystemMetrics(SM_CXSMICON);

   SYSTEM_POWER_STATUS_EX pwr;
   pwr.BatteryLifePercent = BATTERY_FLAG_UNKNOWN;
#ifdef WIN32_PLATFORM_PSPC // Pocket PC code
   GetSystemPowerStatusEx(&pwr, FALSE);
#else
   pwr.BatteryLifePercent = 100;
#endif

   int id;

   if( pwr.BatteryLifePercent == BATTERY_FLAG_UNKNOWN || pwr.BatteryLifePercent > 75 ) id = IDC_POWER;
   else if( pwr.BatteryLifePercent > 50 ) id = IDC_POWER_GOOD;
   else if( pwr.BatteryLifePercent > 25 ) id = IDC_POWER_HALF;
   else id = IDC_POWER_LOW;

   HICON hIco = (HICON)LoadImage(_Module.GetModuleInstance(), MAKEINTRESOURCE(id), IMAGE_ICON, wdh, wdh, 0);

   COLORREF menuColor = GetSysColor(COLOR_MENU);
   HBRUSH brsh = CreateSolidBrush(menuColor);
   FillRect(ds->hDC, &ds->rcItem, brsh);
   DeleteObject(brsh);

   DrawIconEx(ds->hDC, ds->rcItem.right - (wdh + wdh/2), (ds->rcItem.bottom + ds->rcItem.top - wdh) / 2, hIco, wdh, wdh, 0, NULL, DI_NORMAL);
   DestroyIcon(hIco);

   if( pwr.BatteryLifePercent != BATTERY_FLAG_UNKNOWN )
   {
      wchar_t buf[50];
      wsprintf(buf, L"Батарея %d%%", pwr.BatteryLifePercent);

      RECT rc(ds->rcItem);
      rc.right -= (wdh + wdh/2);
      rc.left = wdh - wdh/16;

      COLORREF svBkColor = SetBkColor(ds->hDC, menuColor);
      COLORREF svTxtColor = SetTextColor(ds->hDC, GetSysColor(COLOR_MENUTEXT));

      HFONT font = CreateMenuFont(ds->hDC), svFont= NULL;
      if( font != NULL )
         svFont = (HFONT)SelectObject(ds->hDC, font);

      DrawText(ds->hDC, buf, -1, &rc, DT_SINGLELINE | DT_VCENTER); 

      if( font != NULL )
      {
         SelectObject(ds->hDC, svFont);
         DeleteObject(font);
      }

      //SetBkColor(ds->hDC, svBkColor);
      SetTextColor(ds->hDC, svTxtColor);
      SelectObject(ds->hDC, svFont);
   }

   return TRUE;
}

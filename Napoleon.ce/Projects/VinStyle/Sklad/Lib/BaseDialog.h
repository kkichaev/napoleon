/*
* Copyright (C), 2007, Денис Мосягин
*
* Базовый диалог
*
*  ert   24/08/2007   creating
*/ 
#ifndef __BASE_DIALOG_H
#define __BASE_DIALOG_H

#define DEFAULT_FLAGS 0xFFFFFFFF

#include <StdFuncs.h>

BOOL CALLBACK DisableChildsProc(HWND hwnd, LPARAM lParam);

template <class T, bool t_bModal = true>
class CPocketDialogBase
{
public:
   // Pocket PC only Dialog title handling
#ifdef WIN32_PLATFORM_PSPC
   const int nTitleHeight;
   
   CPocketDialogBase(UINT _shidiFlags) : shidiFlags(_shidiFlags), nTitleHeight(24)
   {
      if( _shidiFlags == DEFAULT_FLAGS )
         shidiFlags = (IsSquareScreen()) ? SHIDIF_SIZEDLGFULLSCREEN/*|SHIDIF_DONEBUTTON*/ : SHIDIF_SIZEDLG;
      //shidiFlags = SHIDIF_SIZEDLGFULLSCREEN;
   }

   // Title painting
   LRESULT OnPaintTitle(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      T* pT = static_cast<T*>(this);
      TCHAR sTitle[40];

      // Preparation
      CPaintDC dc(pT->m_hWnd);
      CFont fontTitle = AtlCreateBoldFont();
      CFontHandle fontOld = dc.SelectFont(fontTitle);
      dc.SetTextColor(RGB(0, 0, 156));
      int nLen = pT->GetWindowText(sTitle, 40);
      int nWidth = dc.GetDeviceCaps(HORZRES);

      // Display title text
      RECT rTitle = { 8, 0, nWidth, nTitleHeight };
      dc.DrawText(sTitle, nLen, &rTitle, DT_VCENTER | DT_SINGLELINE);
      dc.SelectFont(fontOld);

      // Draw bottom line
      CPenHandle penOld = dc.SelectStockPen(BLACK_PEN);
      POINT line[2] = { { 0, nTitleHeight }, { nWidth, nTitleHeight } };
      dc.Polyline(line, 2);
      dc.SelectPen(penOld);

      return bHandled = FALSE;
   }

   // Title preparation: move the dialog controls down to make room for title
   void DialogTitleInit()
   {
      T* pT = static_cast<T*>(this);
      ATLASSERT(::IsWindow(pT->m_hWnd));

      ATL::CWindow wCtl = pT->GetWindow(GW_CHILD);
      while (wCtl.IsWindow())
      {
         RECT rCtl = { 0 };
         wCtl.GetWindowRect(&rCtl);
         ::MapWindowPoints(NULL, pT->m_hWnd, (LPPOINT)&rCtl, 2);
         ::OffsetRect(&rCtl, 0, nTitleHeight);
         wCtl.MoveWindow(&rCtl, FALSE);
         wCtl = wCtl.GetWindow(GW_HWNDNEXT);
      }
   }
#else
   CPocketDialogBase(UINT _shidiFlags) : shidiFlags(_shidiFlags)
   {
   }
#endif // WIN32_PLATFORM_PSPC

#ifdef WIN32_PLATFORM_WFSP
   // SmartPhone VK_TBACK key standard management
   LRESULT OnHotKey(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
   {
      T* pT = static_cast<T*>(this);
      UINT uModif = (UINT)LOWORD(lParam);
      UINT uVirtKey = (UINT)HIWORD(lParam);

      if(uVirtKey == VK_TBACK)
      {
         ATL::CWindow wCtrl = GetFocus();
         if (wCtrl.IsWindow())
         {
            TCHAR szClassName[8] = {0};
            ATLVERIFY(::GetClassName(wCtrl.m_hWnd, szClassName, 8));
            if (!_tcscmp(szClassName, _T("Edit")) || !_tcscmp(szClassName, WC_CAPEDIT))
            {
               ::SHSendBackToFocusWindow(uMsg, wParam, lParam);
            }
            else
            {
               if (uModif & MOD_KEYUP)
                  pT->PostMessage(WM_COMMAND, IDCANCEL, 0);
            }
         }
      }
      return 1;
   }

   // Menu dialog ending
   LRESULT OnMenuClose(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
   {
      T* pT = static_cast<T*>(this);
      pT->PostMessage(WM_COMMAND, wID == ID_MENU_CANCEL ? IDCANCEL : IDOK);
      return 0;
   }

   void SetStaticBold()
   {
      T* pT = static_cast<T*>(this);
      ATLASSERT(::IsWindow(pT->m_hWnd));

      CFontHandle fontBold = AtlCreateBoldFont(pT->GetFont());

      ATL::CWindow wCtl = pT->GetWindow(GW_CHILD);

      while (wCtl.IsWindow())
      {
         if ((short int)wCtl.GetDlgCtrlID() == IDC_STATIC)
            wCtl.SetFont(fontBold);
         wCtl = wCtl.GetWindow(GW_HWNDNEXT);
      }
   }
#endif // WIN32_PLATFORM_WFSP

   // Platform dependant initialization
   void StdPlatformInit()
   {
#ifdef WIN32_PLATFORM_PSPC // Pocket PC title initialization
      DialogTitleInit();
#elif defined(WIN32_PLATFORM_WFSP) // SmartPhone MenuBar and VK_TBACK key initialization
      T* pT = static_cast<T*>(this);
      HWND hMenuBar = NULL;

      if (shidiFlags & SHIDIF_DONEBUTTON)
         hMenuBar = AtlCreateMenuBar(pT->m_hWnd, ATL_IDM_MENU_DONE, SHCMBF_HMENU);
      else
         hMenuBar = ::SHFindMenuBar(pT->m_hWnd);

      if(hMenuBar != NULL)
         ::SendMessage(hMenuBar, SHCMBM_OVERRIDEKEY, VK_TBACK,
         MAKELPARAM(SHMBOF_NODEFAULT | SHMBOF_NOTIFY, SHMBOF_NODEFAULT | SHMBOF_NOTIFY));

      SetStaticBold();
#endif
   }

   // Shell dialog layout initialization
   void StdShidInit()
   {
#ifdef ZEBEX_AYG
      T* pT = static_cast<T*>(this);
      SHINITDLGINFO shidi = { SHIDIM_FLAGS, pT->m_hWnd, shidiFlags };
      ::SHInitDialog(&shidi);
#endif

      //DWORD style = GetWindowLong(pT->m_hWnd, GWL_STYLE);
      //SetWindowLong(pT->m_hWnd, GWL_STYLE, style | WS_NONAVDONEBUTTON);
   }

   // IDC_INFOSTATIC background setting
   LRESULT OnColorStatic(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
   {
      if (::GetDlgCtrlID((HWND)lParam) == IDC_INFOSTATIC)
      {
         ::SetBkMode((HDC)wParam, TRANSPARENT);
         return (LRESULT)::GetSysColorBrush(COLOR_INFOBK);
      }
      return bHandled = FALSE;
   }

   // Standard dialog ending: may be used with any command
   LRESULT OnCloseCmd(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
   {
      T* pT = static_cast<T*>(this);
      if (t_bModal)
         ::EndDialog(pT->m_hWnd, wID);
      else
         pT->DestroyWindow();
      return 0;
   }

protected:
   UINT shidiFlags;
};

LPDLGTEMPLATE MakeDlgTemplate(WORD dlgTemplateID);
struct ResourceString
{
   WORD    size;  // длина строки
   const wchar_t*  str;  //  указатель на строку (м.б. не NULL-terminated)

   std::wstring MakeString() const { return std::wstring(str, size); }
};

ResourceString ReadResourceString(BYTE *src, BYTE **endPtr);

template <class T>
class CBaseDlg : public CDialogImplBase, public CPocketDialogBase< T >
{
public:
   CBaseDlg(WORD _templateID, UINT _shidiFlags) : 
      CPocketDialogBase<T>(_shidiFlags), dlgTemplateID(_templateID)
      {
      }

      INT_PTR DoModal(HWND hWndParent = ::GetActiveWindow())
      {
         ATLASSUME(m_hWnd == NULL);
         _AtlWinModule.AddCreateWndData(&m_thunk.cd, (CDialogImplBase*)this);
         LPDLGTEMPLATE tmpl = MakeDlgTemplate(dlgTemplateID);
         INT_PTR nRet = ::DialogBoxIndirect(_AtlBaseModule.GetResourceInstance(),
            tmpl, hWndParent, StartDialogProc);
         delete tmpl;

         //INT_PTR nRet = ::DialogBox(_AtlBaseModule.GetResourceInstance(),
         //   MAKEINTRESOURCE(dlgTemplateID), hWndParent, StartDialogProc);
         m_hWnd = NULL;
         return nRet;
      }

      BEGIN_MSG_MAP(CBaseDlg)
#ifdef WIN32_PLATFORM_PSPC // Pocket PC title
         MESSAGE_HANDLER(WM_PAINT, OnPaintTitle)
#elif defined(WIN32_PLATFORM_WFSP) // SmartPhone VK_TBACK key
         MESSAGE_HANDLER(WM_HOTKEY, OnHotKey)
         COMMAND_RANGE_HANDLER(ID_MENU_OK, ID_MENU_CANCEL, OnMenuClose)
#endif
         MESSAGE_HANDLER(WM_CTLCOLORSTATIC, OnColorStatic)

         MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
         COMMAND_RANGE_HANDLER(IDOK, IDNO, OnCloseCmd)
      END_MSG_MAP()

      LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
      {
         StdPlatformInit();
         StdShidInit();

         return TRUE;
      }

      LRESULT OnCloseCmd(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
      {
         ::EndDialog(m_hWnd, wID);
         return 0;
      }

protected:
   WORD dlgTemplateID;
};

class BaseDialog : public CBaseDlg<BaseDialog>
{
public:
   enum Flags { ShowSIP = 1, };

   BaseDialog(WORD _templateID, UINT _shidiFlags = DEFAULT_FLAGS) : 
      CBaseDlg<BaseDialog> (_templateID, _shidiFlags)
      { 
         flags = 0; //( GetSystemMetrics(SM_CXSCREEN) < GetSystemMetrics(SM_CYSCREEN) ) ? ShowSIP : 0;
      }

      //typedef CStdSimpleDialog<t_DlgTempl, t_shidFlags> BaseClass;   
      typedef CBaseDlg<BaseDialog> BaseClass;

      BEGIN_MSG_MAP(BaseDialog)
         MESSAGE_HANDLER(WM_HOTKEY, OnHotKey)
         MESSAGE_HANDLER(WM_DESTROY, OnDestroy)
         MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
         COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
         MESSAGE_HANDLER(WM_SETTINGCHANGE, CheckSIP)
         CHAIN_MSG_MAP(BaseClass)
      END_MSG_MAP()

protected:
   static const int offset = 2;
   WORD flags;

   LRESULT OnHotKey(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
   {
      if(wParam == PK1 || wParam == PK2 )
      {
         PostMessage(WM_COMMAND, (wParam == PK1) ? IDOK : IDCANCEL, 0);
      }
      return 0;
   }

   LRESULT OnDestroy(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
   {
      bHandled = FALSE;
      UnregisterHotKeys(m_hWnd, true);
      return 0;
   }

   LRESULT OnInitDialog(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
   {
      bHandled = FALSE;
      if( (flags & ShowSIP) != 0 )
      {
         CreateWindowEx(0,WC_SIPPREF,L"",WS_CHILD, 0, 0, 1, 1, m_hWnd, 
            (HMENU)IDC_SIP1, _Module.GetResourceInstance(),NULL);
      }
#ifdef ZEBEX_AYG
      else
         SHSipPreference(m_hWnd, SIP_FORCEDOWN/*SIP_DOWN*/);
#endif

      RegisterHotKeys(m_hWnd, true);
      return TRUE;
   }

   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      bHandled = FALSE;
#ifdef ZEBEX_AYG
      SHSipPreference(m_hWnd, SIP_DOWN);
#endif
      return FALSE;
   }

   LRESULT CheckSIP(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
   {
      if( IsSquareScreen() == false && wParam == SPI_SETSIPINFO )
      {
         SIPINFO si;
         memset (&si, 0, sizeof (si));
         si.cbSize = sizeof (si);

         if (SipGetInfo(&si)) 
         {
            MoveWindow(si.rcVisibleDesktop.left,
               si.rcVisibleDesktop.top, si.rcVisibleDesktop.right - si.rcVisibleDesktop.left,
               si.rcVisibleDesktop.bottom - si.rcVisibleDesktop.top, TRUE);
         }
      }
      return 0;
   }

   // disable all childs except IDCANCEL
   void DisableChilds()
   {
      EnumChildWindows(m_hWnd, DisableChildsProc, (LPARAM)((HWND)GetDlgItem(IDCANCEL)));
   }

   void MoveButtons(WORD wdh, WORD hgh)
   {
      int bwdh = 0;
      CRect bounds;
      if( GetDlgItemRect(bounds, IDCANCEL) )
      {
         bwdh = bounds.Width();
         GetDlgItem(IDCANCEL).MoveWindow(offset, hgh - bounds.Height() - offset, bwdh, bounds.Height(), FALSE);      
      }

      if( GetDlgItemRect(bounds, IDOK) )
      {
         GetDlgItem(IDOK).MoveWindow(bwdh + 3*offset, hgh - bounds.Height() - offset, 
            bounds.Width(), bounds.Height(), FALSE);
      }
   }

   bool GetDlgItemRect(LPRECT r, int id)
   {
      CWindow w(GetDlgItem(id));
      
      if( w.m_hWnd == NULL ) return false;

      w.GetWindowRect(r);
      ScreenToClient(r);
      return true;
   }

   void ShiftXDlgItem(int x, int id, bool shift = true)
   {
      CRect bounds;
      CWindow w(GetDlgItem(id));
      w.GetWindowRect(bounds);
      ScreenToClient(bounds);

      //GetDlgItemRect(bounds, id);
      if( shift ) bounds.OffsetRect(x, 0);
      else bounds.MoveToX(x);
      w.MoveWindow(bounds, FALSE);
   }

   DWORD GetValue(const wchar_t *buf, DWORD scale)
   {
      return ::GetValue(buf, scale);
   }

   void SetScalingValue(int id, int value, DWORD scale, bool hideRest)
   {
      wchar_t buf[20], src[20];

      ConvertScaling(src, (long)value, scale);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % scale, scale, hideRest);
      SetDlgItemText(id, buf);
   }

   void LoadCombobox(const std::wstring &val, int id, int value, int start = 0)
   {
      CComboBox cbBox(GetDlgItem(id));

      std::wstring::size_type sp = 0;
      for( int i=start; ; i++ )
      {
         std::wstring::size_type ep = val.find_first_of(SEP_SYM, sp);
         if( ep == std::wstring::npos )
            cbBox.InsertString(i, val.substr(sp, ep).c_str());
         else
            cbBox.InsertString(i, val.substr(sp, ep - sp).c_str());

         if( ep == std::wstring::npos ) break;
         sp = ep + 1;
      }
      cbBox.SetCurSel(value);
   }

   void LoadComboboxWithCode(const std::wstring &val, int id, int value, wchar_t sepCodeSym, int start = 0)
   {
      CComboBox cbBox(GetDlgItem(id));

      std::wstring::size_type sp = 0;
      for( int i=start; ; i++ )
      {
         std::wstring::size_type ep = val.find_first_of(SEP_SYM, sp);
         std::wstring tval = val.substr(sp, (ep != std::wstring::npos) ? ep - sp : std::wstring::npos);

         std::wstring::size_type sepSym = tval.find(sepCodeSym);

         int index = cbBox.AddString(tval.substr(0, sepSym).c_str());
        
         if( sepSym != std::wstring::npos )
         {
            int ivalue = _wtoi(tval.substr(sepSym + 1).c_str());
            cbBox.SetItemData(index, ivalue);
            if( ivalue  == value )
               cbBox.SetCurSel(index);
         }

         if( ep == std::wstring::npos ) break;
         sp = ep + 1;
      }
   }

   void LoadComboboxWithCode(const std::wstring &val, int id, wchar_t* value, wchar_t sepCodeSym, StringHolder *sh, int start = 0)
   {
      CComboBox cbBox(GetDlgItem(id));

      std::wstring::size_type sp = 0;
      for( int i=start; ; i++ )
      {
         std::wstring::size_type ep = val.find_first_of(SEP_SYM, sp);
         std::wstring tval = val.substr(sp, (ep != std::wstring::npos) ? ep - sp : std::wstring::npos);

         std::wstring::size_type sepSym = tval.find(sepCodeSym);

         int index = cbBox.AddString(tval.substr(0, sepSym).c_str());
        
         if( sepSym != std::wstring::npos )
         {
            const wchar_t *code = sh->Add(tval.substr(sepSym + 1).c_str());
            cbBox.SetItemData(index, (DWORD)code);
            if( wcscmp(value, code) == 0 )
               cbBox.SetCurSel(index);
         }

         if( ep == std::wstring::npos ) break;
         sp = ep + 1;
      }
   }
};


#endif

/*
* Copyright (C), 2007-2008, Денис Мосягин
*
* Синхронизация
* 
*  ert   21/07/2008   creating
*/ 
#include "stdafx.h"
#include <Module.h>
#include "MainFrame.h"
#include <map>

#include "Progress.h"
#include "BaseDialog.h"
#include "About.h"
#include "FormEntries.h"
#include <NapoleonRes.h>
#include "Progress.h"

#include <projects.h>
#include <StdFuncs.h>

#include "TopApp.h"

#include <NplConfig.h>
#include <DateDialog.h>

#ifdef Alians_sp
#include <Password.h>
#endif

const wchar_t MAIN_STORAGE[] = L"Основная память";

class SyncDialog : public CStdSimpleDialog<IDD_SYNC, SHIDIF_SIPDOWN | SHIDIF_SIZEDLG>
{
public:
   struct Sync
   {
      bool clearBase : 1;
      bool rcvPhoto : 1;
      bool rcvBalance : 1;
      bool rcvPrice : 1;
      bool sndOrders : 1;
#ifdef GPS_POS
      bool sndGPS : 1; 
      WORD gpsDayInterval; 
#endif
   } sync;

   SyncDialog() {}
   ~SyncDialog() {}

   typedef CStdSimpleDialog<IDD_SYNC, SHIDIF_SIPDOWN | SHIDIF_SIZEDLG> BaseClass;

   BEGIN_MSG_MAP(SyncDialog)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_HANDLER(IDC_CLEAR_BASE, BN_CLICKED, OnClearClick)
      COMMAND_ID_HANDLER(IDC_DEL, OnRemove)
      COMMAND_ID_HANDLER(IDC_PREFERENCE, Preference)
      COMMAND_ID_HANDLER(IDOK, OnOK)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      CComboBox cbx(GetDlgItem(IDC_PHOTO_FOLDER));
      cbx.AddString(MAIN_STORAGE);

      WIN32_FIND_DATA fnd;
      HANDLE hFile = FindFirstFlashCard(&fnd);
      if( hFile )
      {
         BOOL done;
         do
         {
            cbx.AddString(fnd.cFileName);
            done = FindNextFlashCard(hFile, &fnd);
         } while(done == TRUE);
         FindClose(hFile);
      }

      struct Preference p;
      int selected = 0;
      if( p.Load() )
      {
         if( p.photoInMainMemory  == false )
         {
            wchar_t buf[MAX_PATH];
            mbstowcs(buf, p.photoFolder, MAX_PATH);
            selected = cbx.FindString(0, buf);
         }
         if( selected < 0 ) selected = 0;
      }
      cbx.SetCurSel(selected);

      bHandled = false;


      CheckDlgButton(IDC_RCV_PRICE, BST_CHECKED);
      CheckDlgButton(IDC_RCV_BALANCE, BST_CHECKED);
      CheckDlgButton(IDC_SEND, BST_CHECKED);
#ifdef GPS_POS
      SetDlgItemInt(IDC_DAY_INTERVAL, 7, FALSE);
#endif
      //CheckDlgButton(IDC_RCV_PHOTO, BST_CHECKED);
      return TRUE;
   }

   LRESULT OnClearClick(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      if( IsDlgButtonChecked(IDC_CLEAR_BASE) == BST_CHECKED )
      {
#ifdef Alians_sp
         NapoleonConfig cfg;
         std::wstring val;
         if( cfg.ReadValue(&val, L"SUPWD") )
         {
            Password dlg;
            if( dlg.DoModal() != IDOK || (val.compare(dlg.password) != 0) )
               CheckDlgButton(IDC_CLEAR_BASE, BST_UNCHECKED);

         } else
         {
            if( MessageBox(L"ВНИМАНИЕ! Во время синхронизации будет удалена вся информация из компьютера. Продолжить?",
                           L"Предупреждение", MB_YESNO|MB_ICONQUESTION) != IDYES )
               CheckDlgButton(IDC_CLEAR_BASE, BST_UNCHECKED);
         }

#else
         if( MessageBox(L"ВНИМАНИЕ! Во время синхронизации будет удалена вся информация из компьютера. Продолжить?",
            L"Предупреждение", MB_YESNO|MB_ICONQUESTION) != IDYES )
         {
            CheckDlgButton(IDC_CLEAR_BASE, BST_UNCHECKED);
         }
#endif
      }
      return 0;
   }

   LRESULT OnRemove(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      HMENU hMenu = LoadMenu(_Module.GetModuleInstance(), MAKEINTRESOURCE(IDD_SYNC));
      HMENU hPopup = GetSubMenu(hMenu, 0);

      CRect rc;
      GetDlgItem(IDC_DEL).GetWindowRect(rc);
      ScreenToClient(rc);
      int ret = ::TrackPopupMenuEx(hPopup, TPM_RIGHTALIGN | TPM_RETURNCMD, rc.left, rc.top, m_hWnd, NULL);

      if( ret > 0 )
         EndDialog(m_hWnd, ret);
      return 0;
   }

   LRESULT OnOK(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      bHandled = false;

      wchar_t buf[MAX_PATH];
      struct Preference p;
      char fbuf[MAX_PATH];

      CComboBox cbx(GetDlgItem(IDC_PHOTO_FOLDER));

      p.Load();
      if( cbx.GetCurSel() == 0 )
      {
         p.photoInMainMemory = true;
         *p.photoFolder = L'\0';
      } else
      {
         p.photoInMainMemory = false;

         GetDlgItemText(IDC_PHOTO_FOLDER, buf, MAX_PATH);
         wcstombs(fbuf, buf, MAX_PATH);
         strncpy(p.photoFolder, fbuf, MAX_PATH);
      }
      p.Save();

      sync.clearBase = (IsDlgButtonChecked(IDC_CLEAR_BASE) == BST_CHECKED);
      sync.rcvBalance = (IsDlgButtonChecked(IDC_RCV_BALANCE) == BST_CHECKED);
      sync.rcvPhoto = (IsDlgButtonChecked(IDC_RCV_PHOTO) == BST_CHECKED);
      sync.rcvPrice = (IsDlgButtonChecked(IDC_RCV_PRICE) == BST_CHECKED);
      sync.sndOrders = (IsDlgButtonChecked(IDC_SEND) == BST_CHECKED);

#ifdef GPS_POS
      sync.sndGPS = (IsDlgButtonChecked(IDC_GPS_TRACK) == BST_CHECKED);
      sync.gpsDayInterval = GetDlgItemInt(IDC_DAY_INTERVAL, NULL, FALSE);
#endif
      return 0;
   }

   LRESULT Preference(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      _Module.ChangePreference();
      return 0;
   }

   bool rcvPhoto, rcvBalance, rcvPrice;
};

LRESULT MainFrame::Sync(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
   SyncDialog syncDlg;
   int res = syncDlg.DoModal();
   HINSTANCE hl;
   if( (hl = GetModuleHandle(L"shellres.dll")) != NULL ) FreeLibrary(hl);

   switch( res )
   {
      case IDOK:
         break;
      case IDC_REMOVE_EMPTY_ORGS:
         RemoveEmptyOrgs();
         return 0;
      case IDC_REMOVE_ORDERS:
      case IDC_DEL:
      {
         RemoveDateDialog dlg;
         if( dlg.DoModal() == IDOK )
         {
            OrderImpl o;
            o.RemoveOrdersTill(dlg.date);

            if( res == IDC_DEL )
               RemoveEmptyOrgs();
         }
         return 0;
      }
      default:
         return 0;
   }
   if( res != IDOK ) return 0;

   std::wstring answer;
   ProgressWindow pw;
   pw.CreateSTDWindow(m_hWnd);

   long ec = 0;
   if( syncDlg.sync.sndOrders )
   {
      ec = _Module.ExportDocuments(&answer, &pw);
      if( ec )
         _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при передаче:\n");
   }

#ifdef GPS_POS
   if( syncDlg.sync.sndGPS )
   {
      ec = _Module.ExportGPS(&answer, &pw, syncDlg.sync.gpsDayInterval );
      if( ec )
      {
         _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при передаче:\n");
         ec = 0;
      }
   }
#endif

   // sepcial case - remove only
   if( ec == 0 && syncDlg.sync.clearBase && !syncDlg.sync.rcvPrice )
   {
      void BaseRemove();
      BaseRemove();
   }

   if( ec == 0 && syncDlg.sync.rcvPrice )
   {
      ec = _Module.ReceivePrice(&answer, &pw, syncDlg.sync.clearBase);
      if( ec )
         _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при приеме:\n");
   }

#ifdef Alians
   if( ec == 0 && syncDlg.sync.clearBase )
   {
      LONG ReceiveOrders(std::wstring *answer, IProgressIndicator *pi);
      ec = ReceiveOrders(&answer, &pw);
      if( ec )
         _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при приеме:\n");
   }
#endif

   if( ec == 0 && syncDlg.sync.rcvBalance )
   {
      ec = _Module.ReceiveBalance(&answer, &pw);
      if( ec )
         _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при приеме:\n");
   }

   if( ec == 0 && syncDlg.sync.rcvPhoto )
   {
      ec = _Module.ReceivePhoto(&answer, &pw);
      if( ec )
         _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при приеме:\n");
   }

   pw.DestroyWindow();
   if( ec == 0 )
   {
      //PrepareData();
      if( current ) current->Refresh();
      MessageBox(L"Синхронизация завершена", L"Сообщение", MB_OK);
   }

#ifdef RCV_MESSAGE
   _Module.ShowMessage();
#endif
    return 0;
}


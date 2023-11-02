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

#include <StdFuncs.h>

#include "TopApp.h"

#include <NplConfig.h>
#include <DateDialog.h>
#include <PhotoFolder.h>

#ifdef Alians_sp
#include <Password.h>
#endif

#ifdef MARK_SYNCED
#include <DoSync.h>
#endif

#ifdef COST_MANAGER
#include "Costs.h"
#endif

#ifdef Agama
HWND hButton;
void SetWaitCursor(bool on);
void SetButtonText()
{
   SetWaitCursor(false);
   if( IsWindow(hButton) == FALSE )
      return;

   CWindow w(hButton);
   w.SetWindowText((IsPhoneOn()) ? L"Выкл тел." : L"Вкл тел.");
   hButton = NULL;
}
#endif

#ifdef VinStyle
void GetSyncDate(SYSTEMTIME* st)
{
   GetLocalTime(st);

   std::wstring fn;
   _Module.MakeFileName(&fn, SYNC_STAMP);
   FILE *f = _wfopen(fn.c_str(), L"rb");
   if( f )
   {
      __int64 ft;
      fread(&ft, sizeof(DWORD), 1, f);
      ft *= ((__int64)10000000 * 3600 * 24);
      FileTimeToSystemTime((FILETIME*)&ft, st);
      fclose(f);
   }
}
#endif

class SyncDialog : public CStdSimpleDialog<IDD_SYNC, SHIDIF_SIPDOWN | SHIDIF_SIZEDLG>
{
public:
   struct Sync
   {
      bool clearBase : 1;
      bool rcvPhoto : 1;
      bool rcvBalance : 1;
      bool rcvPrice : 1;
      bool rcvFullPrice : 1;
      bool sndOrders : 1;
      bool sndVisits : 1;
      bool rcvOrders : 1;
#ifdef COST_MANAGER
      bool rcvCosts : 1; 
#endif
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
#ifdef Agama
      COMMAND_ID_HANDLER(IDC_PHONE_ONOFF, PhoneOnOff)
#endif
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      //LoadFolderData(*this, IDC_PHOTO_FOLDER);
      bHandled = FALSE;

      CheckDlgButton(IDC_RCV_PRICE, BST_CHECKED);
      CheckDlgButton(IDC_SEND, BST_CHECKED);

      CComboBox prc(GetDlgItem(IDC_PRICE_FILTER));
      prc.AddString(L"с остатком > 0");
      prc.AddString(L"полный");
#if defined(Autopteka) || defined(Kolbiko)
      prc.SetCurSel(1);
#elif Voshod // всегда принимаем полный прайс
      prc.SetCurSel(1);
      prc.ShowWindow(SW_HIDE);
#elif defined(Agama) || defined(Michailova_O) || defined (Byloe) || defined (Byloe2) || defined(Lytasov)
      prc.SetCurSel(1);
      prc.ShowWindow(SW_HIDE);
#else
      prc.SetCurSel(0);
#endif

#ifdef GPS_POS
      SetDlgItemInt(IDC_DAY_INTERVAL, 0, FALSE); 
#endif

#ifdef VAN_SELLING
      GetDlgItem(IDC_SEND).SetWindowText(L"Передача документов");

      GetDlgItem(IDC_RCV_BALANCE).ShowWindow(SW_HIDE);
      GetDlgItem(IDD_ORDER_LIST).ShowWindow(SW_HIDE);
#ifdef VISIT_DOC
      GetDlgItem(IDD_VISIT).ShowWindow(SW_HIDE);
#endif
#endif

#ifdef Abdullin
      CheckDlgButton(IDC_RCV_BALANCE, BST_CHECKED);
      CheckDlgButton(IDC_RCV_PRICE, BST_CHECKED);
      CheckDlgButton(IDD_VISIT, BST_CHECKED);
      CheckDlgButton(IDC_SEND, BST_CHECKED);
#endif

#ifdef Autopteka
      CheckDlgButton(IDC_GPS_TRACK, BST_CHECKED);
#endif

#ifdef TKSibir
      CheckDlgButton(IDC_SEND, BST_UNCHECKED);
      CheckDlgButton(IDC_GPS_TRACK, BST_CHECKED);
#endif
      //CheckDlgButton(IDC_RCV_PRICE, BST_CHECKED);
      //CheckDlgButton(IDC_RCV_PHOTO, BST_CHECKED);

#ifdef Agama
      hButton = GetDlgItem(IDC_PHONE_ONOFF);
      SetButtonText();
#endif

#ifdef Vkk
      CheckDlgButton(IDC_RCV_BALANCE, BST_CHECKED);
#endif

#ifdef VinStyle
      wchar_t buf[50];
      NapoleonConfig cfg;
      std::wstring tstr(L"Дата/время синхронизации на КПК:\n"), value;
      SYSTEMTIME st;

      GetSyncDate(&st);
      wsprintf(buf, L"%02d.%02d.%d %02d:%02d\n", st.wDay, st.wMonth, st.wYear, st.wHour, st.wMinute);
      tstr += buf;

      if( cfg.ReadValue(&value, L"ДатаСинхрониазацииОстатков") )
      {
         tstr += L"\nАктуальность остатков:\n";
         tstr += value;
      }

      if( cfg.ReadValue(&value, L"ДатаСинхрониазацииДолгов") )
      {
         tstr += L"\nАктуальность взаиморасчётов:\n";
         tstr += value;
      }
   
      SetDlgItemText(IDC_BASE_VERSION, tstr.c_str());
#endif
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

#ifdef Agama
   LRESULT PhoneOnOff(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      SetWaitCursor(true);
      hButton = GetDlgItem(IDC_PHONE_ONOFF);

      if( IsPhoneOn() )
         ClosePhoneLine(SetButtonText);
      else
         OpenPhoneLine(SetButtonText);

      return 0;
   }
#endif

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

      //StoreFolderData(*this, IDC_PHOTO_FOLDER, NULL);

      sync.clearBase = (IsDlgButtonChecked(IDC_CLEAR_BASE) == BST_CHECKED);
      sync.rcvBalance = (IsDlgButtonChecked(IDC_RCV_BALANCE) == BST_CHECKED);
      sync.rcvPhoto = (IsDlgButtonChecked(IDC_RCV_PHOTO) == BST_CHECKED);
      sync.rcvPrice = (IsDlgButtonChecked(IDC_RCV_PRICE) == BST_CHECKED);
      sync.sndOrders = (IsDlgButtonChecked(IDC_SEND) == BST_CHECKED);
      sync.sndVisits = (IsDlgButtonChecked(IDD_VISIT) == BST_CHECKED);
      sync.rcvOrders = (IsDlgButtonChecked(IDD_ORDER_LIST) == BST_CHECKED);
#ifdef COST_MANAGER
      sync.rcvCosts = (IsDlgButtonChecked(IDC_COST) == BST_CHECKED);
#endif

#ifdef GPS_POS
      sync.sndGPS = (IsDlgButtonChecked(IDC_GPS_TRACK) == BST_CHECKED);
      sync.gpsDayInterval = GetDlgItemInt(IDC_DAY_INTERVAL, NULL, FALSE);
#endif
      CComboBox prc(GetDlgItem(IDC_PRICE_FILTER));
      sync.rcvFullPrice = (prc.GetCurSel() == 1);
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
#ifdef GPS_POS
   if( syncDlg.sync.sndOrders || syncDlg.sync.sndGPS|| syncDlg.sync.sndVisits )
   {
      DWORD flags = 0;
      if( syncDlg.sync.sndOrders ) flags |= NapoleonApp::efDocs;
      if( syncDlg.sync.sndVisits ) flags |= NapoleonApp::efVisits;
      if( syncDlg.sync.sndGPS ) flags |= NapoleonApp::efGPS;

      ec = _Module.ExportDocuments(&answer, &pw, flags, syncDlg.sync.gpsDayInterval);
      if( ec )
      {
         if( ec == neNoDocuments && (syncDlg.sync.rcvPrice || syncDlg.sync.rcvBalance || syncDlg.sync.rcvPhoto) )
            ec = 0;
         else
            _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при передаче:\n");
      }
   }
#else
   if( syncDlg.sync.sndOrders || syncDlg.sync.sndVisits )
   {
      DWORD flags = 0;
      if( syncDlg.sync.sndOrders ) flags |= NapoleonApp::efDocs;
      if( syncDlg.sync.sndVisits ) flags |= NapoleonApp::efVisits;

      ec = _Module.ExportDocuments(&answer, &pw, flags, 0);
      if( ec )
      {
         if( ec == neNoDocuments && (syncDlg.sync.rcvPrice || syncDlg.sync.rcvBalance || syncDlg.sync.rcvPhoto) )
            ec = 0;
         else
            _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при передаче:\n");
      }
   }
#endif

   // special case - remove only
   if( ec == 0 && syncDlg.sync.clearBase && !syncDlg.sync.rcvPrice )
   {
      void BaseRemove();
      BaseRemove();
   }

   if( ec == 0 && syncDlg.sync.rcvPrice )
   {
      ec = _Module.ReceivePrice(&answer, &pw, syncDlg.sync.clearBase, syncDlg.sync.rcvFullPrice);
      if( ec )
         _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при приеме:\n");
#ifdef MARK_SYNCED
      else
         MarkSynced(); 
#endif
   }


   if( ec == 0 && (syncDlg.sync.rcvBalance || syncDlg.sync.rcvOrders) )
   {
      DWORD flags = 0;
      if(syncDlg.sync.rcvBalance) flags |=  NapoleonApp::efBalance;
      if(syncDlg.sync.rcvOrders) flags |=  NapoleonApp::efOrders;
      ec = _Module.ReceiveDocs(&answer, &pw, flags);
      if( ec )
         _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при приеме:\n");
   }

#ifdef COST_MANAGER
   if( ec == 0 && syncDlg.sync.rcvCosts )
   {
      ec = CostManager::ReceiveCosts(&answer, &pw);
      if( ec )
         _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при передаче:\n");
      else
         CostManager::Clear();
   }
#endif

   if( ec == 0 && syncDlg.sync.rcvPhoto )
   {
      ec = _Module.ReceivePhoto(&answer, &pw);
      if( ec )
         _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при приеме:\n");
   }

   pw.DestroyWindow();
   if( ec == 0 )
   {
      if( current ) current->Refresh();
      MessageBox(L"Синхронизация завершена", L"Сообщение", MB_OK);
   }

#ifdef RCV_MESSAGE
   _Module.ShowMessage();
#endif
    return 0;
}

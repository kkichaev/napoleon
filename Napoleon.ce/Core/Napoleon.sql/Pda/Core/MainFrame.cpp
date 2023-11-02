/*
* Copyright (C), 2007, Денис Мосягин
*
* Фрейм
* 
*  ert   13/08/2007   creating
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

//#include <projects.h>
#include <StdFuncs.h>

#include "TopApp.h"

#include <NplConfig.h>
#include <SQLTable.h>

#include "ObjImpl.h"

const wchar_t* ConfigImpl::IP1 = L"ServerIPName1";
const wchar_t* ConfigImpl::IP2 = L"ServerIPName2";

#ifdef GPS_POS
#include <Apps.h>
#include <GPSArchive.h>

void Log(const char* msg, ... );

typedef void (*AddCallbackT)(StateCallback scbk, LocationCallback lcbk);

static void NewLocation(const Location* location)
{
   GPSArchive::AddCurrent(*location);
}

static void NewState(const ModuleStates* newState)
{
   RefreshIco(newState);
}

#endif

//
//--------------------- Main Frame -------------------------
//
MainFrame::~MainFrame()
{
   StopTopApp();
}

struct TableName : public IReflectableData
{
   wchar_t *name;
   DECLARE_TYPE_REFLECTION(TableName)
};

BEGIN_TYPE_REFLECTION(TableName)
   REGISTER_STRING_MEMBER(TableName, name)
END_TYPE_REFLECTION(TableName)

void BaseRemove()
{
   SQLTable table(L"sqlite_master");
   TableName tn;
   std::vector<std::wstring> tableNames;

   ConfigImpl ci;

   bool bdo = table.Select(&tn, L"WHERE type='table'");
   while(bdo)
   {
      if( wcscmp(ci.Name(), tn.name) !=  0 )
         tableNames.push_back(tn.name);
      bdo = table.SelectNext(&tn);
   }

   std::wstring stmt(L"DELETE FROM '"); stmt += ci.Name(); stmt += L"' WHERE NOT key LIKE 'ServerIPName%'";
   SQLTable::Execute(stmt.c_str());

   std::vector<std::wstring>::const_iterator i = tableNames.begin();
   for(; i != tableNames.end(); i++ )
      SQLTable::DropTable(i->c_str());
}


LRESULT MainFrame::OnCreate(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   DWORD deviceID = GetDeviceID();

   Preference p;
   p.Load();

   if( (p.flags & apfTopApp) != 0 )
      StartTopApp();

   if( *p.dbName == '\0' )
      strcpy(p.dbName, DEFAULT_BASE);

   p.Save();

   DataInit(p.dbName);

   InitDocTypeSet();

#ifdef GPS_POS
   _Module.StartApps();
   HANDLE hApps = _Module.AppsIntance();
   if( hApps )
   {
      AddCallbackT AddCallback = (AddCallbackT)GetProcAddress((HMODULE)hApps, L"AddCallback");
      AddCallback(NewState, NewLocation);
   }
#endif

   //MessageBox(L"!", L"!", MB_OK);

   OpenOrgList();

   bHandled = FALSE;

   _Module.SetPreferenceChangeHandler(this);
   return 0;
}

LRESULT MainFrame::OnActivate(UINT /*uMsg*/, WPARAM wParam, LPARAM /*lParam*/, BOOL& bHandled)
{
   int fActive = LOWORD(wParam);
   if( fActive == WA_ACTIVE && cameraActive )
   {
      PostMessage(WM_ACTIVATE_CAMERAVIEW, 0, 0);
      return 0;
   }

   if( fActive == WA_INACTIVE )
   {
      Preference p;
      p.Load();

      DWORD ct = (GetTickCount() - _Module.GetStartTick()) / 1000;
      p.worked += ct;
      p.Save();
   } else
   {
      _Module.SetStartTick();
   }
   return 0;
}

//#include "RegHash.h"
//class RegisterDlg : public BaseDialog//<IDD_REGISTER>
//{
//public:
//   RegisterDlg() : BaseDialog(IDD_REGISTER) {}
//
//   //typedef BaseDialog<IDD_REGISTER> BaseClass;
//   typedef BaseDialog BaseClass;
//
//   BEGIN_MSG_MAP(RegisterDlg)
//      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
//      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
//      COMMAND_ID_HANDLER(IDOK, CheckResponse)
//      CHAIN_MSG_MAP(BaseClass)
//   END_MSG_MAP()
//
//protected:
//   LRESULT CheckResponse(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
//   {
//      bHandled = FALSE;
//
//      DWORD check = MakeAnswer(GetDlgItemInt(IDC_ANSWER, NULL, FALSE));
//      DWORD current = GetDlgItemInt(IDC_RESPONSE, NULL, FALSE);
//      if( check != current )
//      {
//         MessageBox(L"К сожалению,\nвведен неверный код регистрации,\nпожалуйста,\nвведите код повторно", 
//            L"Ошибка в коде", MB_OK|MB_ICONINFORMATION);
//
//         bHandled = TRUE;
//      } else
//      {
//         Preference p;
//         p.Load();
//         p.answer = current;
//         p.Save();
//
//         MessageBox(L"Приложение успешно зарегистрировано!", L"Поздравляем!", MB_OK|MB_ICONINFORMATION);
//      }
//
//      return FALSE;
//   }
//
//   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
//   {
//      bHandled = FALSE;
//
//      Preference p;
//      CEdit response = (CEdit)GetDlgItem(IDC_RESPONSE);
//      response.SetLimitText(10);
//      if( p.Load() )
//      {
//         wchar_t buf[20];
//         CEdit answ = (CEdit)GetDlgItem(IDC_ANSWER);
//         answ.SetLimitText(10);
//
//         wsprintf(buf, L"%010u", p.code);
//         answ.SetWindowText(buf);
//         answ.EnableWindow(FALSE);
//
//         if( p.answer )
//         {
//            wsprintf(buf, L"%010u", p.answer);
//            response.SetWindowText(buf);
//         }
//      }
//
//      response.SetFocus();
//      return TRUE;
//   }
//
//   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
//   {
//      WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);
//
//      MoveButtons(wdh, hgh);
//
//      CRect bounds;
//      GetDlgItemRect(bounds, IDC_ANSWER);
//      GetDlgItem(IDC_ANSWER).MoveWindow(offset, bounds.top, wdh - 2*offset, bounds.Height());
//
//      GetDlgItemRect(bounds, IDC_RESPONSE);
//      GetDlgItem(IDC_RESPONSE).MoveWindow(offset, bounds.top, wdh - 2*offset, bounds.Height());
//
//      return 0;
//   }
//};
//
LRESULT MainFrame::CheckSIP(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
   if( current != NULL )
      current->SendMessage(WM_SETTINGCHANGE, wParam, lParam);
   return 0;
}

//LRESULT MainFrame::Register(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
//{
//   RegisterDlg dlg;
//   dlg.DoModal();
//
//   return 0;
//}

LRESULT MainFrame::About(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
   CAboutDlg dlg;

   TopApp::EnableDoneButton(true);
#ifdef WIN32_PLATFORM_PSPC // Pocket PC code
   LRESULT res = FSDoModal(dlg);
#else
   LRESULT res = dlg.DoModal();
#endif
   TopApp::EnableDoneButton(false);

   return res;
}

static void MakeRemoveStmt(std::wstring *sql, const wchar_t *tblname, const wchar_t *ordname)
{
   sql->assign(L"DELETE FROM ");
   sql->append(tblname);
   sql->append(L" WHERE id NOT IN (SELECT id FROM ");
   sql->append(ordname);
   sql->append(L")");
}

//LRESULT MainFrame::RemoveEmptyOrgs(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
int MainFrame::RemoveEmptyOrgs()
{
   OrderImpl oi;
   OrgImpl org;
   if( !SQLTable::IsTableExist(oi.Name()) )
   {
      if( MessageBox(L"ВНИМАНИЕ!!! В базе нет заявок.\nУдалить все организации?", L"Вопрос", MB_YESNO|MB_ICONQUESTION) != IDYES )
         return 0;

      SQLTable::DropTable(org.Name());
   }
   else
   {
      if( MessageBox(L"Удалить все организации без заказов?", L"Вопрос", MB_YESNO|MB_ICONQUESTION) != IDYES )
         return 0;

      OrgSumImpl os;
      std::wstring sql;
      MakeRemoveStmt(&sql, org.Name(), oi.Name());
      SQLTable::Execute(sql.c_str());

      MakeRemoveStmt(&sql, os.Name(), oi.Name());
      SQLTable::Execute(sql.c_str());

      //L"delete from orgs where id not in (select id from orders)";
      //L"delete from org_sums where id not in (select id from orders)";
   }

   if( current != NULL )
      current->Refresh();
   return 0;
}

void MainFrame::PreferenceChanged()
{
   Preference p;
   p.Load();

   if( (p.flags & apfLandscape) != 0 ) TopApp::ChangeOrientation(true);
   else if( (p.flags & apfPortrait) != 0 ) TopApp::ChangeOrientation(false);


   if( (p.flags & apfTopApp) != 0 )
   {
      StartTopApp();
      TopApp::MakeAutorun();
   }
   else
   {
      StopTopApp();
      TopApp::RemoveAutorun();
   }

#ifdef GPS_POS
   _Module.UpdateApps();
#endif

   if( current ) current->Refresh();
}

LRESULT MainFrame::OnQuit(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
   Preference p;
   p.Load();

   if( (p.flags & apfTopApp) == 0 )
   {
#ifdef GPS_POS
   _Module.StopApps();
#endif
      DestroyDocTypeSet();

      Quit();
   }
   return 1;
}

LRESULT MainFrame::OnNewInstance(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
   COPYDATASTRUCT *cd = (COPYDATASTRUCT*)lParam;
#ifdef GPS_POS
   _Module.DoApps((wchar_t*)cd->lpData);
#endif
   return TRUE;
}

void MainFrame::StartTopApp()
{
   TopApp::Start(m_hWnd);
}

void MainFrame::StopTopApp()
{
   TopApp::Stop();
}

void MainFrame::CameraActive(bool active)
{
   cameraActive = active;
}

// ***************************************************************************
// Function Name: IsOwned
//
// Purpose: Determine if hwnd is owned by hwndOwner.
//
// Arguments:
//   hwndOwner - Handle to the owner window
//   hwnd      - Handle to the owned window
//
// Return Values:
//   TRUE if hwnd is owned by hwndOwner.
//   FALSE if hwnd isn't owned by hwndOwner.

static BOOL IsOwned(HWND hwndOwner, HWND hwnd)
{
    BOOL bOwned = FALSE;

    while (NULL != (hwnd = GetWindow(hwnd, GW_OWNER)))
    {
        if (hwnd == hwndOwner)
        {
            bOwned = TRUE;
            break;
        }
    }

    return bOwned;
}


// ***************************************************************************
// Function Name: EnumLastActiveWindowProc
//
// Purpose: Get the topmost, visible, enabled window who is owned by the
//          window which specified by the application-defined value given
//          in EnumWindows.
//
// Arguments:
//   hwnd   - Handle to a top-level window
//   lParam - Handle to the window which specified by the application-defined
//            value given in EnumWindows
//
// Return Values:
//   TRUE continues enumeration. FALSE stops enumeration.

static BOOL CALLBACK EnumLastActiveWindowProc(HWND hwnd, LPARAM lParam)
{
    BOOL bContinue = TRUE;
    HWND hOwner = *((HWND *)lParam);

    // Ignore windows which are invisible, disabled or cannot be activated.
    if (!IsWindowVisible(hwnd) ||
        !IsWindowEnabled(hwnd) ||
        (WS_EX_NOACTIVATE & GetWindowLong(hwnd, GWL_EXSTYLE)))
    {
        // Continue enumeration.
        goto Exit;
    }

    // If this is the owner window, there are no owned windows because
    // all owned windows are always above its owner in the z-order.
    if (hwnd == hOwner)
    {
        // Not found the owned window. Stop enumeration.
        bContinue = FALSE;
        goto Exit;
    }

    // Is this window owned by hwndOwner?
    if (IsOwned(hOwner, hwnd))
    {
        // Found the last owned window. Stop enumeration.
        bContinue = FALSE;
        *((HWND *)lParam) = hwnd;
        goto Exit;
    }

Exit:
    return bContinue;
}

// ***************************************************************************
// Function Name: GetLastActiveWindow
//
// Purpose: Retrieves the last active window owned by hwndOwner.
//          The return value is the same as the hwndOwner parameter
//          if hwndOwner does not own any windows.
//
// Arguments:
//   hwndOwner - Handle to the owner window
//
// Return Values:
//   Handle to the last active window.

static HWND GetLastActiveWindow(HWND hwndOwner)
{
    HWND hwndLastActive = hwndOwner;
    EnumWindows(EnumLastActiveWindowProc, (LPARAM)&hwndLastActive);
    return hwndLastActive;
}

LRESULT MainFrame::ActivateCamera(UINT /*uMsg*/, WPARAM wParam, LPARAM /*lParam*/, BOOL& bHandled)
{
   if( cameraActive )
   {
      HWND hwndCameraView = FindWindow(L"Camera View", NULL);
      if (NULL != hwndCameraView)
         SetForegroundWindow(GetLastActiveWindow(hwndCameraView));
   }
   return 0;
}

//#include "PhotoFolder.h"
bool MainFrame::MakePhoto(HWND hWnd, std::wstring* photoVar)
{
#ifdef WIN32_PLATFORM_PSPC
   SHCAMERACAPTURE shcc = {0};

   std::wstring folder;
   Preference p;
   p.Load();
   if( p.photoInMainMemory )
      _Module.MakeFileName(&folder, PHOTO_FOLDER);
   else
   {
      wchar_t buf[MAX_PATH];

      folder = L'\\';
      mbstowcs(buf, p.photoFolder, MAX_PATH);
      folder += buf;
      folder += PHOTO_FOLDER;
   }
   CreateDirectory(folder.c_str(), NULL);

   shcc.cbSize             = sizeof(shcc);
   shcc.hwndOwner          = hWnd;
   shcc.pszInitialDir      = folder.c_str();
   shcc.pszDefaultFileName = NULL;
   shcc.pszTitle           = L"Фото";
   shcc.StillQuality       = CAMERACAPTURE_STILLQUALITY_NORMAL;//(CAMERACAPTURE_STILLQUALITY)p.photoQuality;
   shcc.VideoTypes         = CAMERACAPTURE_VIDEOTYPE_ALL;
#ifdef VISIT_DOC
   switch( p.photoQuality )
   {
   //case 0:
   //case 1:
   //   shcc.nResolutionWidth   = 176;
   //   shcc.nResolutionHeight  = 132;
   //   break;
   case 0:
      shcc.nResolutionWidth   = 320;
      shcc.nResolutionHeight  = 240;
      break;
   case 2:
      shcc.nResolutionWidth   = p.picWidth;
      shcc.nResolutionHeight  = p.picHeight;
      break;
   default:
      shcc.nResolutionWidth   = 640;
      shcc.nResolutionHeight  = 480;
      break;
   }
#else
   shcc.nResolutionWidth   = 320;
   shcc.nResolutionHeight  = 240;
#endif
   shcc.nVideoTimeLimit    = 0;
   shcc.Mode               = CAMERACAPTURE_MODE_STILL;

   ((MainFrame*)_Module.GetFrame())->CameraActive(true);
   HRESULT hr = SHCameraCapture(&shcc);
   ((MainFrame*)_Module.GetFrame())->CameraActive(false);
   if( hr == S_OK )
   {
      photoVar->assign(shcc.szFile);
      return true;
   }

   return false;
#else
   return false;
#endif
}
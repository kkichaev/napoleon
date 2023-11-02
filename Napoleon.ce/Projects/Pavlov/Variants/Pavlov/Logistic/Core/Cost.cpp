/*
* Copyright (C), 2007 - 2010, Денис Мосягин
*
* Показ цен
*
*  ert   05/09/2010   creating
*/
#include "stdafx.h"
#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <StdFuncs.h>
#include <ListForm.h>
#include "FormEntries.h"

#include "ObjImpl.h"
#include <NumInput.h>

class CostData : public IFormData
{
public:
   CostData(const wchar_t *orgID) { this->orgID = orgID; }
   ~CostData() {}

   const std::wstring OrgID() const { return orgID; }

protected:
   std::wstring orgID;
};

class CostForm : public BaseForm
{
public:
   CostForm() : numInput(IDC_ITEM_CODE) {}
   ~CostForm() { delete data; }

   DECLARE_FORM(CostForm, IDC_COST)

   virtual DWORD GetResourceID() const { return IDC_COST; }
   virtual DWORD GetMenuBarID() const { return IDC_COST; }
   virtual DWORD GetMenuID() const { return -1; }

   BEGIN_MSG_MAP(CostForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDOK, ScanDataByKey)
      MESSAGE_HANDLER(WM_SCAN_DATA, ScanData)
      NUM_INPUT_HANDLER(numInput)
      CHAIN_MSG_MAP(BaseForm)
   END_MSG_MAP()

   virtual bool SetData(IFormData *_data);

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   LRESULT ScanDataByKey(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

protected:
   void GetData(const wchar_t* id);

protected:
   CostData *data;
   NumInput numInput;
};

IMPLEMENT_FORM(CostForm)

bool CostForm::SetData(IFormData *_data)
{
   data = (CostData*)_data;

   SHSipPreference(m_hWnd, SIP_DOWN);
   StartScan(m_hWnd, WM_SCAN_DATA);
   return true;
}

LRESULT CostForm::ScanDataByKey(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   CWindow wnd(GetDlgItem(IDC_ITEM_CODE));
   int len = wnd.GetWindowTextLength() + 1;
   if( len > 1 )
   {
      wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
      wnd.GetWindowText(buf, len);

      GetData(buf);
   }
   return 0;
}

LRESULT CostForm::ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   std::wstring data;
   if( GetScan(&data) )
   {
      GetDlgItem(IDC_ITEM_CODE).SetWindowText(data.c_str());
      GetData(data.c_str());
   }
   return 0;
}

LRESULT CostForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   StopScan();
   OpenPartnerList();
   return 0;
}

class Cost : public SkladImpl
{
public:
   wchar_t* id_o;

   DECLARE_TYPE_REFLECTION(Cost);
};

BEGIN_TYPE_REFLECTION(Cost)
   REGISTER_STRING_MEMBER(Cost, id_o)
   CHAIN_REFLECTION(Cost, Sklad)
END_TYPE_REFLECTION(Cost)

void CostForm::GetData(const wchar_t* id)
{
   static bool inNet = false;

   GetDlgItem(IDC_ITEM_CODE).SetWindowText(L"");
   if( inNet )
      return;

   inNet = true;
   std::wstring answer;
   ServObject<Cost> so;

   ProgressWindow pw;
   pw.CreateSTDWindow(m_hWnd);

   so.id = so.holder.Add(id);
   so.id_o = so.holder.Add(data->OrgID().c_str());
   long ec = _Module.ObjectExchange(&so, READ_OBJECTS, &answer, &pw);
   pw.DestroyWindow();

   if( ec != 0 || so.servResult == 0 )
   {
      if( ec == 0 )
         answer = so.servResponse;
      _Module.ShowErrorBox(ec, answer,  L"Ошибка при приеме:\n");
   } else
   {
      GetDlgItem(IDC_TEXT).SetWindowText(((Sklad&)so).name);
      SetScalingValue(GetDlgItem(IDC_COST), so.cost, SUM_SCALE, false);
      SetScalingValue(GetDlgItem(IDC_QTY), so.qty, QTY_SCALE, true);
   }

   inNet = false;
}

void OpenCost(const wchar_t *orgID)
{
   _Module.GetFrame()->Load(IDC_COST, new CostData(orgID));
}
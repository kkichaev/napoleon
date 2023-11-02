/*
 * Copyright (C), 2006-2017, Денис Мосягин
 *
 * Документ ДКА1
 *
 *  ert   01/11/2017   creating
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

typedef BOOL (*pSysSetFxKeyState)(DWORD dwVKCode,BOOL dwEnableState);


#ifdef ZEBEX
typedef BOOL (*pZBCRSetPower)(BOOL dwState);
typedef BOOL (*pGetLastNotifyEvent) (PDWORD lpNotifyEvent);
typedef BOOL (*pGetLastBarcode) (LPTSTR lpszBarcode);
typedef BOOL (*pZBCRStartScan) (void);
typedef BOOL (*pZBCRStopScan) (void);
typedef BOOL (*pZBCRSetOutputMode) (BYTE dwMode);
#endif


class ScanDocData : public IFormData
{
public:
   ScanDocData(const wchar_t* doc);
   ~ScanDocData();

   void GetDocTitle(std::wstring* text) const;
	void GetDocBC(std::wstring* text) const { 
		if(current != NULL)
		{
			int ctr = 1;
			wchar_t buf[10];
			text->clear();
			vector_t<ScanDocItem>::const_iterator i = current->items.begin();
			for( ; i != current->items.end(); i++ )
			{
				wsprintf(buf, L"%d", ctr++);
				text->append(buf).append(L" ").append(i->barCode).append(L"\r\n");
			}
		}
	}

   void RemoveDoc();

	void OnBarcode(const wchar_t* bc)
	{
		if(current != NULL)
		{
			bool finded = false;
			vector_t<ScanDocItem>::iterator i = current->items.begin();
			for( ; i != current->items.end(); i++ )
			{
				if( wcscmp(i->barCode, bc) == 0 )
				{
					finded = true;
					break;
				}
			}

			if(!finded)
			{
				ScanDocItem sdi;
				sdi.barCode = current->holder.Add(bc);
				current->items.push_back(sdi);

				current->Write();
			}
		}
	}

protected:

	bool NewDoc(const wchar_t* doc);

	ScanDocImpl *current;
};

#if ZEBEX
class ScanDocForm : public BaseForm, public BarcodeHandler//, public CCustomDraw<MovmentDocForm>
#else
class ScanDocForm : public BaseForm//, public CCustomDraw<DKA1DocForm>
#endif
{
public:
	ScanDocForm() {}

   virtual bool SetData(IFormData *_data) { 
	   data = _data;

		wchar_t text[100];
		std::wstring tstr;
		((ScanDocData*)data)->GetDocTitle(&tstr);
		wsprintf(text, L"Документ: %s", tstr.c_str());
		SetDlgItemText(IDC_DOC, text);

		((ScanDocData*)data)->GetDocBC(&tstr);
		SetDlgItemText(IDC_BARCODE, tstr.c_str());

		return true;
	}

   BEGIN_MSG_MAP(DKA1DocForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_REMOVE, Remove)
      COMMAND_ID_HANDLER(IDC_INPUT_BARCODE, OnInputBC)
      MESSAGE_HANDLER(WM_SCAN_DATA, ScanData)
      CHAIN_MSG_MAP(BaseForm)
   END_MSG_MAP()

   virtual DWORD GetResourceID() const { return IDD_SCAN_DOC; }
   virtual DWORD GetMenuID() const { return IDD_ORDER_LIST; }

   virtual void UpdateLayout(const RECT& bounds, bool forceRecalc);

#if ZEBEX
   virtual void HandleEvent();

	HMODULE hLib;
   pGetLastNotifyEvent GetBCEvent;
   pGetLastBarcode GetBarcode;
#endif

   DECLARE_FORM(ScanDocForm, IDD_SCAN_DOC)

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Remove(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   LRESULT OnInputBC(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

protected:
	IFormData* data;
   void OnBarcode(const wchar_t* barcode);
};

IMPLEMENT_FORM(ScanDocForm);

//
//----------------------------------- MovmentDocData ----------------------------------
//
ScanDocData::ScanDocData(const wchar_t *doc) : current(NULL)
{
   //SQLTable tb(WhAgentsImpl().Name());
   //tb.Select(&agent, L"where id=userid");

	NewDoc(doc);
}

ScanDocData::~ScanDocData()
{
   if( current )
   {
		if( current->items.size() == 0 )
      {
         current->Remove();
      }
      delete current;
      current = NULL;
   }
}

void ScanDocData::RemoveDoc()
{
   if( current != NULL )
   {
      current->Remove();

      delete current;
      current = NULL;
   }
}


bool ScanDocData::NewDoc(const wchar_t* doc)
{
   if( current == NULL )
   {
      current = new ScanDocImpl();
   } else
   {
      if( wcscmp(current->id, doc) == 0 )
         return true;
   }

   current->id = (wchar_t*)doc;
   if( !current->Read() )
   {
      SYSTEMTIME st;
      GetLocalTime(&st);
      SystemTimeToFileTime(&st, &current->created);
      current->params = 0;
      current->id = current->holder.Add(doc);
      current->Write();
   }
   return true;
}

void ScanDocData::GetDocTitle(std::wstring* text) const
{
   text->clear();
   if( current != NULL )
      DecodeNumber(text, current->id, true);
}

//
//-------------------------------- MovmentDocForm --------------------------------
//

LRESULT ScanDocForm::OnInputBC(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   BCDialog dlg;
   if( dlg.DoModal() == IDOK && *dlg.GetText() != L'\0' )
   {
      wchar_t buf[MAX_PATH];
      wcscpy(buf, dlg.GetText());
      OnBarcode(buf);
   }
   return 0;
}

static bool readBarcode = false;
void ScanDocForm::OnBarcode(const wchar_t* _barcode)
{
	if( readBarcode )
		return;
	
	readBarcode = true;

   wchar_t text[MAX_PATH];
	wchar_t *barcode = (wchar_t*)alloca((wcslen(_barcode) + 1) * sizeof(wchar_t));
	wcscpy(barcode, _barcode);

   wchar_t *p = wcschr(barcode, L'\n');
   if( p ) *p = L'\0';
   p = wcschr(barcode, L'\r');
   if( p ) *p = L'\0';
	int bcLen = wcslen(barcode);
   bool isItem = (bcLen == 13 || bcLen > 30);
   bool refresh = false;

	((ScanDocData*)data)->OnBarcode(barcode);
	std::wstring tstr;
	((ScanDocData*)data)->GetDocTitle(&tstr);
	wsprintf(text, L"Документ: %s", tstr.c_str());
	SetDlgItemText(IDC_DOC, text);

	((ScanDocData*)data)->GetDocBC(&tstr);
	SetDlgItemText(IDC_BARCODE, tstr.c_str());
	
	readBarcode = false;
}

LRESULT ScanDocForm::ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   std::wstring data;
   if( GetScanData(&data, lParam) )
		OnBarcode(data.c_str());

	return 1;
}

#ifdef ZEBEX
void ScanDocForm::HandleEvent()
{
   DWORD eventCode;

   if( GetBCEvent(&eventCode) && eventCode == BCR_NOTIFY_RECEIVE_BARCODE )
   {
      wchar_t buf[MAX_PATH];
      GetBarcode(buf);
      OnBarcode(buf);
   }
}
#endif

LRESULT ScanDocForm::Remove(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( MessageBox(L"Удалить документ?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
   {
      ((ScanDocData*)data)->RemoveDoc();
		Backing(nCode, id, hWnd, bHandled);
   }
   return 0;
}

LRESULT ScanDocForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
#ifdef ZEBEX
   ((MainFrame*)_Module.GetFrame())->SetBarcodeHandler(NULL);

   pZBCRSetPower fn = (pZBCRSetPower)GetProcAddress(hLib, L"ZBCRSetPower");
   fn(FALSE);

   FreeLibrary(hLib);
#else
   StopScan();
#endif
	OpenOrderList(); 
   return 0;
}

void ScanDocForm::UpdateLayout(const RECT& bounds, bool forceRecalc)
{
   CRect rc, rc2;

   CWindow btn(GetDlgItem(IDC_REMOVE));
   btn.GetWindowRect(rc2);

   CWindow wnd(GetDlgItem(IDC_DOC));
   wnd.GetWindowRect(rc);
   ScreenToClient(rc);
   int top = rc.top;
   int right = bounds.right - rc.left - rc2.Width() - 2;
   wnd.MoveWindow(rc.left, rc.top, right, rc.Height());

	btn.MoveWindow(rc.left + right+1, top + (rc.bottom - rc2.Height()) / 2, rc2.Width(), rc2.Height());

   CWindow wnd1(GetDlgItem(IDC_BARCODE));
   wnd1.GetWindowRect(rc);
   ScreenToClient(rc);
	wnd1.MoveWindow(rc.left, rc.top, right, bounds.bottom - 3 - rc.top);

}

void OpenScanDoc(const wchar_t* doc)
{
   ScanDocData *pfd = new ScanDocData(doc);
	_Module.GetFrame()->Load(IDD_SCAN_DOC, pfd);
}

bool ScanDocImpl::Remove()
{
   if( rid == NO_ROWID ) return true;

   if( !table.Remove(rid) ) return false;

   rid = NO_ROWID;
   return true;
}

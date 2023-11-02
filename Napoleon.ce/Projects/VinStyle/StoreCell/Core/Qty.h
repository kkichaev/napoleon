/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Диалог количества
 *
 *  ert   17/08/2007   creating
 */
#ifndef __QTY_H
#define __QTY_H

#include <Form.h>
#include "FormEntries.h"
#include "BaseDialog.h"
#include "SAnchor.h"
#include "NumInput.h"
#include "ObjImpl.h"
#include "RADrawer.h"
#include <ZBCRLib.h>

#include <atlcrack.h>

const int NEW_ITEM = WM_SCAN_DATA + 0x20;
const int UPDATE_ITEM = WM_SCAN_DATA + 0x21;
const int CHOOSE_ITEM = WM_SCAN_DATA + 0x22;
const int CANCEL_ITEM = WM_SCAN_DATA + 0x23;

class EditImpl : public CWindowImpl<EditImpl>
{
public:
	DECLARE_WND_CLASS(L"EditImpl")

	BEGIN_MSG_MAP(EditImpl)
		MESSAGE_HANDLER(WM_KEYDOWN, OnKeyDown)
	END_MSG_MAP()

   LRESULT OnKeyDown(WORD msg, WPARAM wParam, LPARAM, BOOL &bHandled)
   {
		if( wParam == VK_RETURN )
		{
			GetParent().PostMessage(WM_COMMAND, MAKELONG(IDOK, BM_CLICK), (LPARAM)m_hWnd);
		} else if ( wParam == VK_ESCAPE )
		{
			GetParent().PostMessage(WM_COMMAND, MAKELONG(IDCANCEL, BM_CLICK), (LPARAM)m_hWnd);
		} else
			bHandled = false;
      return 0;
   }

};

class CQTYDialog : public BaseDialog
{
public:
   CQTYDialog(const wchar_t* name, int qty = QTY_SCALE, int rc = IDD_QTY);

   BEGIN_MSG_MAP(CQTYDialog)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
		COMMAND_HANDLER(IDC_QTY, EN_CHANGE, OnEditChange)
      MESSAGE_HANDLER(WM_BCR_NOTIFY, OnBarcodeNotify)
      CHAIN_MSG_MAP(BaseDialog)

   END_MSG_MAP()

   int GetQty();

	void SetData(const wchar_t* name, int qty);
	void SetMixData(int inPack);
	void AddQty(int added);

	HWND Create(HWND hWndParent, LPARAM dwInitParam = NULL);

protected:
   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT OnEditChange(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
   LRESULT OnBarcodeNotify(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled);   

protected:
   int qty;
   const wchar_t* name;
	EditImpl edit;
	HWND hParent;

	int inPack;
};

class CMoveQtyDialog : public CQTYDialog
{
public:
	CMoveQtyDialog(const wchar_t* name, int qty = QTY_SCALE, const wchar_t* destRack = L"");

   BEGIN_MSG_MAP(CMoveQtyDialog)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      CHAIN_MSG_MAP(CQTYDialog)
   END_MSG_MAP()

	void SetDestRack(const wchar_t* rack);

	void EnableInput(bool enable);

protected:
   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);

	void UpdateRackText();

	std::wstring rack;
};

#endif

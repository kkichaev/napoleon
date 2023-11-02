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
#include "Exchange.h"

#include <atlcrack.h>

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
   CQTYDialog();

   BEGIN_MSG_MAP(CQTYDialog)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
		COMMAND_HANDLER(IDC_QTY, EN_CHANGE, OnEditChange)
      CHAIN_MSG_MAP(BaseDialog)

   END_MSG_MAP()

	void SetData(WhOutDocItem *item);

	//HWND Create(HWND hWndParent, LPARAM dwInitParam = NULL);

protected:
   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT OnEditChange(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);

	void Init();
protected:
   WhOutDocItem *item;
	EditImpl edit;
	PriceImpl price;

	//HWND hParent;
};

#endif

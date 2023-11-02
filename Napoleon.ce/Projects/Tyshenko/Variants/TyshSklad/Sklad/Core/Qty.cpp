/*
* Copyright (C), 2006-2008, Денис Мосягин
*
* Диалог количества
*
*  ert   17/08/2007   creating
*/
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include "Preference.h"
#include <NapoleonRes.h>

#include "Qty.h"

#include <MainFrame.h>
#include <Table.h>

const DWORD QTY_FLAGS = DEFAULT_FLAGS;

CQTYDialog::CQTYDialog() : BaseDialog(IDD_QTY, QTY_FLAGS)
{
	item = NULL;
}

void CQTYDialog::SetData(WhOutDocItem *item)
{
	this->item = item;
	price.id = item->id;
	price.Read();
}

LRESULT CQTYDialog::OnEditChange(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
	return 0;
}

//HWND CQTYDialog::Create(HWND hWndParent, LPARAM dwInitParam)
//{
//	BOOL result;
//
//	ATLASSUME(m_hWnd == NULL);
//
//	// Allocate the thunk structure here, where we can fail
//	// gracefully.
//
//	result = m_thunk.Init(NULL,NULL);
//	if (result == FALSE) 
//	{
//		SetLastError(ERROR_OUTOFMEMORY);
//		return NULL;
//	}
//
//	_AtlWinModule.AddCreateWndData(&m_thunk.cd, this);
//	hParent = hWndParent;
//	HWND hWnd = ::CreateDialogParam(_AtlBaseModule.GetResourceInstance(), MAKEINTRESOURCE(dlgTemplateID),
//		hWndParent, StartDialogProc, dwInitParam);
//	ATLASSUME(m_hWnd == hWnd);
//	return hWnd;
//}

LRESULT CQTYDialog::Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
	bHandled = FALSE;
	if( wID == IDOK )
	{
		int qty = GetDlgItemInt(IDC_QTY, NULL, TRUE) * QTY_SCALE;
		if(qty > (int)item->qty)
		{
			bHandled = TRUE;
			MessageBox(L"Количество больше чем в накладной", L"Ошибка", MB_OK|MB_ICONSTOP);
			return 0;
		} else 
		{
			item->inputQty = qty;
		}
	} 

	return 0;
}

void CQTYDialog::Init()
{
	SetDlgItemInt(IDC_QTY, item->inputQty / QTY_SCALE);

	CRect rc;
	CWindow title(GetDlgItem(IDC_ITEM_NAME));
	title.SetWindowText(price.name);
	GetClientRect(&rc);

   CalcTextHeight(title.m_hWnd, &rc);
	title.MoveWindow(rc);


	CEdit edit = (CEdit)GetDlgItem(IDC_QTY);
	edit.SetFocus();
	edit.SetSelAll();

	CWindow tq(GetDlgItem(IDC_TOTAL_QTY));
	std::wstring textBuf;
	QtyToText(&textBuf, item->qty, item->isBaseUnit, price);
	textBuf.insert(0, L"Всего: ");
	tq.SetWindowText(textBuf.c_str());
}




LRESULT CQTYDialog::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
	bHandled = FALSE;
	CenterWindow();

	Init();

	edit.SubclassWindow(GetDlgItem(IDC_QTY).m_hWnd);

	return TRUE;
}

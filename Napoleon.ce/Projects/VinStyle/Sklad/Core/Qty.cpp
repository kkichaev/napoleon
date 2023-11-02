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

#ifdef Urovo
static CFont gvFont;

static bool CreateFont(CFont* font, int fontSize, bool bold)
{
   LOGFONT lf = {0};
   lf.lfHeight = fontSize;
   lf.lfWeight = (bold) ? FW_BOLD : FW_NORMAL;
   lf.lfCharSet = ANSI_CHARSET;
   lf.lfPitchAndFamily = DEFAULT_PITCH;
   wcscpy(lf.lfFaceName, L"Tahoma"); //L"Arial");

   return (font->CreateFontIndirect(&lf) != NULL);
}
#endif

CQTYDialog::CQTYDialog(const wchar_t *name, int qty, int rc) : BaseDialog(rc, QTY_FLAGS)
{
	flags &= (~ShowSIP);
	this->name = name;
	this->qty = qty / QTY_SCALE;
	inPack = 0;
}

void CQTYDialog::SetMixData(int inPack)
{
	GetDlgItem(IDC_TOTAL_QTY).ShowWindow(SW_SHOW);
	SetDlgItemText(IDC_QTY, L"");
	AddQty(0);
	this->inPack = inPack;
}

void CQTYDialog::AddQty(int added)
{
	wchar_t buf[30];
	qty += (added * QTY_SCALE);
	wsprintf(buf, L"Всего: %d", qty / QTY_SCALE);
	SetDlgItemText(IDC_TOTAL_QTY, buf);
}

LRESULT CQTYDialog::OnEditChange(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
	if( inPack != 0 )
	{
		qty = GetDlgItemInt(IDC_QTY, NULL, TRUE) * inPack;
		AddQty(0);
	}
	return 0;
}

LRESULT CQTYDialog::OnBarcodeNotify(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)   
{
	if( inPack != 0 )
		((MainFrame*)_Module.GetFrame())->OnBarcodeNotify(uMsg, wParam, lParam, bHandled);
	return 1;
}

HWND CQTYDialog::Create(HWND hWndParent, LPARAM dwInitParam)
{
	BOOL result;

	ATLASSUME(m_hWnd == NULL);

	// Allocate the thunk structure here, where we can fail
	// gracefully.

	result = m_thunk.Init(NULL,NULL);
	if (result == FALSE) 
	{
		SetLastError(ERROR_OUTOFMEMORY);
		return NULL;
	}

	_AtlWinModule.AddCreateWndData(&m_thunk.cd, this);
	hParent = hWndParent;
	HWND hWnd = ::CreateDialogParam(_AtlBaseModule.GetResourceInstance(), MAKEINTRESOURCE(dlgTemplateID),
		hWndParent, StartDialogProc, dwInitParam);
	ATLASSUME(m_hWnd == hWnd);
	return hWnd;
}

LRESULT CQTYDialog::Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
	if( wID == IDOK )
	{
		if( inPack == 0 )
			qty = GetDlgItemInt(IDC_QTY, NULL, TRUE) * QTY_SCALE;
		::PostMessage(hParent, UPDATE_ITEM, 0, 0);
	} else {
		::PostMessage(hParent, CANCEL_ITEM, 0, 0);
		ShowWindow(SW_HIDE);
	}

	bHandled = FALSE;
	return 0;
}


int CQTYDialog::GetQty()
{
	if( m_hWnd != 0 && inPack != 0 )
		qty = GetDlgItemInt(IDC_QTY, NULL, TRUE) * QTY_SCALE;

	return qty;
}

void CQTYDialog::SetData(const wchar_t* _name, int _qty)
{
	SetDlgItemInt(IDC_QTY, _qty / QTY_SCALE);
	SetDlgItemText(IDC_ITEM_NAME, _name);

	CEdit edit = (CEdit)GetDlgItem(IDC_QTY);
	edit.SetFocus();
	edit.SetSelAll();

	this->qty = _qty;

	GetDlgItem(IDC_TOTAL_QTY).ShowWindow(SW_SHOW);
	inPack = 0;
}


#ifdef Urovo
static BOOL CALLBACK SetDlgChildFonts(HWND hwnd, LPARAM lParam)
{
	SendMessage(hwnd, WM_SETFONT, (WPARAM)gvFont.m_hFont, MAKELPARAM(TRUE, 0));

   return TRUE;
}
#endif

LRESULT CQTYDialog::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
	bHandled = FALSE;
	CenterWindow();

	if( *name != L'\0' )
	{
		SetData(name, qty * QTY_SCALE);
		name = L"";
	}

#ifdef Urovo
	if(gvFont.m_hFont == NULL)
		CreateFont(&gvFont, 13, false);
	EnumChildWindows(m_hWnd, (WNDENUMPROC)SetDlgChildFonts, 0);
#endif

	edit.SubclassWindow(GetDlgItem(IDC_QTY).m_hWnd);

	return TRUE;
}

CMoveQtyDialog::CMoveQtyDialog(const wchar_t* name, int qty, const wchar_t* destRack) :
	CQTYDialog(name, qty, IDD_MOVE_QTY)
{
	rack = destRack;	
}

LRESULT CMoveQtyDialog::OnInitDialog(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
	bHandled = FALSE;
	UpdateRackText();
	return TRUE;
}

void CMoveQtyDialog::UpdateRackText()
{
	std::wstring text(L"Полка куда: ");
	text += rack;
	SetDlgItemText(IDC_DEST_RACK, text.c_str());
}

void CMoveQtyDialog::SetDestRack(const wchar_t* newRack)
{
	rack = newRack;
	UpdateRackText();
}

LRESULT CMoveQtyDialog::OnBarcodeNotify(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
	((MainFrame*)_Module.GetFrame())->OnBarcodeNotify(uMsg, wParam, lParam, bHandled);
	return 1;
}

void CMoveQtyDialog::EnableInput(bool enable)
{
	GetDlgItem(IDOK).EnableWindow(enable ? TRUE : FALSE);
}

bool SetQTY(QTYData *data)
{
	return false;
}
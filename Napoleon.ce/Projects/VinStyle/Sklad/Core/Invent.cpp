/*
 * Copyright (C), 2006-2015, Денис Мосягин
 *
 * Инвентаризация
 *
 *  ert   16/09/2015   creating
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

struct ItemView : public IReflectableData
{
   wchar_t* item;
   DWORD qty;
   DECLARE_TYPE_REFLECTION(ItemView)
};

BEGIN_TYPE_REFLECTION(ItemView)
   REGISTER_STRING_MEMBER(ItemView, item)
   REGISTER_ULONG_SCALE_MEMBER2(ItemView, qty, QTY_SCALE, true)
END_TYPE_REFLECTION(ItemView)

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left,  /*L"Название"*/IDS_PRICE, NULL, L"item", 200 },
   { ListFormData::Header::Right, /*L"Кол-во"*/IDS_KOL_VO, NULL,   L"qty", 50 },
};

class InventData : public ListFormData
{
public:
	InventData();

	void SetNewSklad(const std::wstring& whId);
	void OnBarcode(const std::wstring& rack);

	void GetRackTitle(std::wstring* text) const;

   virtual const Header *GetHeader() const { return header; }
   virtual int ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }

   virtual const DataReflector& DataType() const { return ItemView().GetType(); }
	virtual int Count() const { return items.size(); }

   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }
   virtual bool Editing(int index) { return Selecting(index); }
	virtual bool Selecting(int index) { return false; }

   virtual bool Get(IReflectableData* data, int index) const;

protected:
	struct Item 
	{
		std::wstring text;
		int qty;
	};

	std::wstring curRack, curWh;
	std::vector<Item> items;
   WhAgents agent;
};

InventData::InventData()
{
	SQLTable tb(WhAgentsImpl().Name());
   tb.Select(&agent, L"where id=userid");
}

void InventData::SetNewSklad(const std::wstring& whId)
{
	curWh = whId;
	curRack.clear();
	items.clear();
}

void InventData::OnBarcode(const std::wstring& bc)
{
	items.clear();

	const wchar_t *barcode = bc.c_str();
   bool isItem = (bc.size()== 13);
	std::wstring whereStr(L"where idWh='"); whereStr += curWh; whereStr += L"' and ";
	if ( !isItem && *barcode == L'7' && *(barcode+1) == L'7' )
	{
		// is rack
		whereStr += L"idRack='"; whereStr += bc; whereStr += L"'";

		curRack = L"Полка:";
		DecodeNumber(&curRack, bc.c_str(), false);
	} else
	{
		PalletsImpl pli;
		pli.barcode = (wchar_t*)bc.c_str();
		if( pli.Read() )
		{
			curRack = L"Паллет:";

			vector_t<PalletItem>::const_iterator i = pli.items.begin();
			for( ; i != pli.items.end(); i++ )
			{
				PriceImpl prc;
				prc.id = i->id;
				prc.Read();
				Item data;
				data.text = prc.name;
				data.qty = i->qty;
				items.push_back(data);
			}
			return;
		}

		//is item
		isItem = true;
		PriceImpl pi;
		pi.ReadBarcode(bc.c_str());

		curRack = pi.name;

		whereStr += L"barcode='"; whereStr += bc; whereStr += L"' or bcPack='"; whereStr += bc; whereStr += L"'";
	}
	WhSkaldRestImpl skl;
	SQLTable t(skl.Name());
	bool bdo = t.Select(&skl, whereStr.c_str());
	while( bdo )
	{
		Item data;
		data.qty = skl.qty;
		if( agent.canInputInPack != 0 && skl.inPack > 0 )
			data.qty = (DWORD)((__int64)data.qty * skl.inPack / QTY_SCALE);

		wchar_t buf[200];
		SYSTEMTIME st;
		FileTimeToSystemTime(&skl.bottling, &st);
		wsprintf(buf, L" %02d.%02d.%d", st.wDay, st.wMonth, st.wYear);
		if( isItem )
		{
			DecodeNumber(&data.text, skl.idRack, false);
			
			data.text += buf;
		} else
		{
			data.text = skl.name; data.text += buf;
		}
		items.push_back(data);

		bdo = t.SelectNext(&skl);
	}
}

bool InventData::Get(IReflectableData* data, int index) const
{
	if( index < 0 || index >= (int)items.size() )
		return false;

	const Item& i = items[index];
	((ItemView*)data)->item = (wchar_t*)i.text.c_str();
	((ItemView*)data)->qty = i.qty;
	return true;
}

#if ZEBEX
class InventForm : public ListForm, public BarcodeHandler, public CCustomDraw<InventForm>
#else
class InventForm : public ListForm, public CCustomDraw<InventForm>
#endif
{
public:
	InventForm() {}

   virtual bool SetData(IFormData *_data) { return SetDataEx(_data, 3); }

   BEGIN_MSG_MAP(InventForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_INPUT_BARCODE, OnInputBC)
		COMMAND_HANDLER(IDC_WH, CBN_SELCHANGE, OnWhChanged)
      MESSAGE_HANDLER(WM_SCAN_DATA, ScanData)
      NOTIFY_CODE_HANDLER_EX(LVN_KEYDOWN, OnKeyDown)
		NOTIFY_CODE_HANDLER_EX(NM_CLICK, ItemSelected)
      CHAIN_MSG_MAP(CCustomDraw<InventForm>)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   virtual DWORD GetResourceID() const { return IDD_INVENT; }
   virtual DWORD GetMenuID() const { return IDD_ORDER_LIST; }

   virtual void UpdateLayout(const RECT& bounds, bool forceRecalc);

   DECLARE_FORM(InventForm, IDD_INVENT)

   LRESULT OnInputBC(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnWhChanged(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

	DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/) { return CDRF_NOTIFYITEMDRAW; }
   DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/) { return CDRF_NOTIFYITEMDRAW; }

   bool SetDataEx(IFormData *_data, int scale);
   void OnBarcode(const wchar_t* barcode);

	std::vector<std::wstring> sklads;

#if ZEBEX
   virtual void HandleEvent();

	HMODULE hLib;
   pGetLastNotifyEvent GetBCEvent;
   pGetLastBarcode GetBarcode;
#endif
};

IMPLEMENT_FORM(InventForm)

//
//-------------------------------- InventData --------------------------------
//
void InventData::GetRackTitle(std::wstring* text) const
{
   text->clear();
	text->assign(curRack);
}


//
//-------------------------------- InventForm --------------------------------
//
bool InventForm::SetDataEx(IFormData *_data, int scale)
{
   if( !ListForm::SetDataEx(_data, scale) )
      return false;

	CComboBox cb = (CComboBox)GetDlgItem(IDC_WH);

	WhSkladImpl skl;
	SQLTable t(skl.Name());
	bool bdo = t.Select(&skl, L"ORDER BY name");
	while(bdo)
	{
		sklads.push_back(skl.id);
		cb.AddString(skl.name);

		bdo = t.SelectNext(&skl);
	}
	if( sklads.size() > 0 )
	{
		cb.SetCurSel(0);
		BOOL bRet = TRUE;
		OnWhChanged(CBN_SELCHANGE, IDC_WH, GetDlgItem(IDC_WH), bRet);;
	}
  
#ifdef ZEBEX
   hLib = LoadLibrary(L"zbcrlib.dll");
   pZBCRSetPower fn = (pZBCRSetPower)GetProcAddress(hLib, L"ZBCRSetPower");
   fn(TRUE);

   pZBCRSetOutputMode outMode = (pZBCRSetOutputMode)GetProcAddress(hLib, L"ZBCRSetOutputMode");
   outMode(BCR_DISABLE_OUTPUT);

   GetBCEvent = (pGetLastNotifyEvent)GetProcAddress(hLib, L"ZBCRGetLastNotifyEvent");
   GetBarcode = (pGetLastBarcode)GetProcAddress(hLib, L"ZBCRGetLastBarcode");
   ((MainFrame*)_Module.GetFrame())->SetBarcodeHandler(this);
#else
   StartScan(m_hWnd);
#endif

   return true;
}

LRESULT InventForm::OnInputBC(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
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

LRESULT InventForm::OnWhChanged(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
	int sel = ::SendMessage(hWnd, CB_GETCURSEL, 0, 0);
	if( sel >= 0 && sel < (int)sklads.size() )
	{
		std::wstring tstr;
		((InventData*)data)->SetNewSklad(sklads.at(sel));
		((InventData*)data)->GetRackTitle(&tstr);
		SetDlgItemText(IDC_RACK, tstr.c_str());
		Refresh();
	}
	return 0;
}

static bool readBarcode = false;
void InventForm::OnBarcode(const wchar_t* _barcode)
{
	if( readBarcode )
		return;
	
	readBarcode = true;
	std::wstring tstr;

	wchar_t *barcode = (wchar_t*)alloca((wcslen(_barcode) + 1) * sizeof(wchar_t));
	wcscpy(barcode, _barcode);

   wchar_t *p = wcschr(barcode, L'\n');
   if( p ) *p = L'\0';
   p = wcschr(barcode, L'\r');
   if( p ) *p = L'\0';

	((InventData*)data)->OnBarcode(barcode);
	((InventData*)data)->GetRackTitle(&tstr);
	SetDlgItemText(IDC_RACK, tstr.c_str());

	Refresh();
	readBarcode = false;
}

LRESULT InventForm::ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   std::wstring data;
   if( GetScanData(&data, lParam) )
		OnBarcode(data.c_str());

	return 1;
}

#ifdef ZEBEX
void InventForm::HandleEvent()
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


LRESULT InventForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
#ifdef ZEBEX
   ((MainFrame*)_Module.GetFrame())->SetBarcodeHandler(NULL);

   pZBCRSetPower fn = (pZBCRSetPower)GetProcAddress(hLib, L"ZBCRSetPower");
   fn(FALSE);

   FreeLibrary(hLib);
#else
   StopScan();
#endif
	OpenMainForm(); 
   return 0;
}

void InventForm::UpdateLayout(const RECT& bounds, bool forceRecalc)
{
   CRect rc, rc2;

   CWindow wnd(GetDlgItem(IDC_WH));
   wnd.GetWindowRect(rc);
   ScreenToClient(rc);

	int right = bounds.right - rc.left - 2;
   wnd.MoveWindow(rc.left, rc.top, right, rc.Height());

   CWindow wnd1(GetDlgItem(IDC_RACK));
   wnd1.GetWindowRect(rc);
   ScreenToClient(rc);
   wnd1.MoveWindow(rc.left, rc.top, right, rc.Height());

   SetListLayout(forceRecalc, rc.bottom + 2, bounds.bottom - rc.bottom + 3);
}

void OpenInvent()
{
   InventData *pfd = new InventData();
	_Module.GetFrame()->Load(IDD_INVENT, pfd);
}
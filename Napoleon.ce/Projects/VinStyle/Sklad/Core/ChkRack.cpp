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

struct ChkRackItemView : public IReflectableData
{
   wchar_t* item;
   DWORD qty;
   DECLARE_TYPE_REFLECTION(ChkRackItemView)
};

BEGIN_TYPE_REFLECTION(ChkRackItemView)
   REGISTER_STRING_MEMBER(ChkRackItemView, item)
   REGISTER_ULONG_SCALE_MEMBER2(ChkRackItemView, qty, QTY_SCALE, true)
END_TYPE_REFLECTION(ChkRackItemView)

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left,  /*L"Название"*/IDS_PRICE, NULL, L"item", 200 },
   { ListFormData::Header::Right, /*L"Кол-во"*/IDS_KOL_VO, NULL,   L"qty", 50 },
};


class ChkRackData : public ListFormData
{
public:
	enum RowState
	{
		Inited = 0,
		Scanned,
		NotInDoc,
	};

	ChkRackData();

	void SetNewSklad(const std::wstring& whId);
	void OnBarcode(const std::wstring& rack);

	void GetRackTitle(std::wstring* text) const;

   virtual const Header *GetHeader() const { return header; }
   virtual int ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }

   virtual const DataReflector& DataType() const { return ChkRackItemView().GetType(); }
	virtual int Count() const { return items.size(); }

   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }
   virtual bool Editing(int index) { return Selecting(index); }
	virtual bool Selecting(int index) { return false; }

   virtual bool Get(IReflectableData* data, int index) const;

	void PlaySound(bool goodItem);
	void AddNewItem(const PriceImpl& price, int qty);

	RowState RowState(DWORD idx)
	{
		return (items.size() > idx) ? items[idx].state : Inited;
	}

protected:
	struct Item 
	{
		std::wstring text;
		std::wstring bc;
		int qty;
		enum RowState state;
	};

	std::wstring curRack, curWh, rackBc;
	std::vector<Item> items;
   WhAgents agent;

	std::wstring goodSnd, badSnd;

	std::vector<Item>::iterator FindItem(const wchar_t* bc);
};

ChkRackData::ChkRackData()
{
	SQLTable tb(WhAgentsImpl().Name());
   tb.Select(&agent, L"where id=userid");

   Preference p;
   p.Load();

	_Module.MakeFileName(&goodSnd, L"Sounds\\");
	badSnd = goodSnd;

	goodSnd += p.goodItem;
	goodSnd += L".wav";

	badSnd += p.badItem;
	badSnd += L".wav";
}

void ChkRackData::PlaySound(bool goodItem)
{
	const wchar_t* snd = (goodItem) ? goodSnd.c_str() : badSnd.c_str();
	if(*snd != L'\0')
		::PlaySound(snd, NULL, SND_SYNC);
}

void ChkRackData::AddNewItem(const PriceImpl& price, int qty)
{
	Item item;
	item.text = price.name;
	item.qty = qty;
	item.bc = price.barcode;
	item.state = NotInDoc;
	items.push_back(item);
}

void ChkRackData::SetNewSklad(const std::wstring& whId)
{
	curWh = whId;
	curRack.clear();
	rackBc.clear();
	items.clear();
}

std::vector<ChkRackData::Item>::iterator ChkRackData::FindItem(const wchar_t* bc)
{
	std::vector<ChkRackData::Item>::iterator i = items.begin();
	for( ; i != items.end(); i++ )
	{
		if(i->bc.compare(bc) == 0)
			break;
	}
	return i;
}

void ChkRackData::OnBarcode(const std::wstring& bc)
{
	const wchar_t *barcode = bc.c_str();
   bool isItem = (bc.size()== 13);
	std::wstring whereStr(L"where idWh='"); whereStr += curWh; whereStr += L"' and ";
	if ( !isItem && *barcode == L'7' && *(barcode+1) == L'7' )
	{
		// is rack
		rackBc = bc;
		whereStr += L"idRack='"; whereStr += bc; whereStr += L"'";

		curRack = L"Полка:";
		DecodeNumber(&curRack, bc.c_str(), false);
		
		items.clear();

		WhSkaldRestImpl skl;
		SQLTable t(skl.Name());
		bool bdo = t.Select(&skl, whereStr.c_str());
		while( bdo )
		{
			std::vector<ChkRackData::Item>::iterator i = FindItem(skl.barcode);
			if(i == items.end())
			{
				Item data;
				data.qty = skl.qty;
				data.bc = skl.barcode;
				data.state = Inited;

				wchar_t buf[200];
				SYSTEMTIME st;
				FileTimeToSystemTime(&skl.bottling, &st);
				wsprintf(buf, L" %02d.%02d.%d", st.wDay, st.wMonth, st.wYear);
				data.text = skl.name; data.text += buf;

				items.push_back(data);
			} else
			{
				i->qty += skl.qty;
			}
			bdo = t.SelectNext(&skl);
		}
		return;
	} 

	bool haveItem = true;

	PalletsImpl pli;
	pli.barcode = (wchar_t*)bc.c_str();
	if( pli.Read() )
	{
		vector_t<PalletItem>::const_iterator i = pli.items.begin();
		for( ; i != pli.items.end(); i++ )
		{
			PriceImpl prc;
			prc.id = i->id;
			if(prc.Read())
			{
				std::vector<ChkRackData::Item>::iterator fndi = FindItem(prc.barcode);
				if(fndi == items.end())
				{
					haveItem = false;
					AddNewItem(prc, i->qty);
				} else
				{
					if(fndi->state == NotInDoc)
					{
						fndi->qty += i->qty;
						haveItem = false;
					} else
					{
						fndi->state = Scanned;
						//fndi->qty -= i->qty;
						//if(fndi->qty >= 0)
						//{
						//	int newQty = -fndi->qty;
						//	items.erase(fndi);
						//	if(fndi->qty < 0)
						//	{
						//		haveItem = false;
						//		AddNewItem(prc, newQty);
						//	}
						//}
					}
				}
			}
		}
		PlaySound(haveItem);
		return;
	}

	int cqty = QTY_SCALE;
	std::vector<ChkRackData::Item>::iterator fndi = FindItem(bc.c_str());
	if(fndi != items.end())
	{
		if(fndi->state == NotInDoc)
		{
			fndi->qty += cqty;
			haveItem = false;
		} else
		{
			fndi->state = Scanned;
			//fndi->qty -= cqty;
			//if(fndi->qty == 0)
			//	items.erase(fndi);
		}
	} else
	{
		PriceImpl pi;
		if(pi.ReadBarcode(bc.c_str()))
		{
			haveItem = false;
			AddNewItem(pi, cqty);
		} else
		{
			return;
		}
	}
	PlaySound(haveItem);
}

bool ChkRackData::Get(IReflectableData* data, int index) const
{
	if( index < 0 || index >= (int)items.size() )
		return false;

	const Item& i = items[index];
	((ChkRackItemView*)data)->item = (wchar_t*)i.text.c_str();
	((ChkRackItemView*)data)->qty = i.qty;
	return true;
}

#if ZEBEX
class ChkRackForm : public ListForm, public BarcodeHandler, public CCustomDraw<ChkRackForm>
#else
class ChkRackForm : public ListForm, public CCustomDraw<ChkRackForm>
#endif
{
public:
	ChkRackForm() {}

   virtual bool SetData(IFormData *_data) { return SetDataEx(_data, 3); }

   BEGIN_MSG_MAP(ChkRackForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_INPUT_BARCODE, OnInputBC)
		COMMAND_HANDLER(IDC_WH, CBN_SELCHANGE, OnWhChanged)
      MESSAGE_HANDLER(WM_SCAN_DATA, ScanData)
      NOTIFY_CODE_HANDLER_EX(LVN_KEYDOWN, OnKeyDown)
		NOTIFY_CODE_HANDLER_EX(NM_CLICK, ItemSelected)
      CHAIN_MSG_MAP(CCustomDraw<ChkRackForm>)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   virtual DWORD GetResourceID() const { return IDD_INVENT; }
   virtual DWORD GetMenuID() const { return IDD_ORDER_LIST; }

   virtual void UpdateLayout(const RECT& bounds, bool forceRecalc);

   DECLARE_FORM(ChkRackForm, IDD_CHK_RACK)

   LRESULT OnInputBC(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnWhChanged(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

	DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/) { return CDRF_NOTIFYITEMDRAW; }
   DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);

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

IMPLEMENT_FORM(ChkRackForm)

//
//-------------------------------- ChkRackData --------------------------------
//
void ChkRackData::GetRackTitle(std::wstring* text) const
{
   text->clear();
	text->assign(curRack);
}


//
//-------------------------------- ChkRackForm --------------------------------
//
bool ChkRackForm::SetDataEx(IFormData *_data, int scale)
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

LRESULT ChkRackForm::OnInputBC(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
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

DWORD ChkRackForm::OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
{
   NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
	enum ChkRackData::RowState state = ((ChkRackData *)data)->RowState(lvcd->nmcd.dwItemSpec);
   if( state == ChkRackData::NotInDoc )
   {
      lvcd->clrTextBk = RGB(0xf0, 0, 0);
	} else if(state == ChkRackData::Scanned )
	{
      lvcd->clrTextBk = RGB(0, 0xf0, 0);
	}
   return CDRF_NOTIFYITEMDRAW;
}

LRESULT ChkRackForm::OnWhChanged(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
	int sel = ::SendMessage(hWnd, CB_GETCURSEL, 0, 0);
	if( sel >= 0 && sel < (int)sklads.size() )
	{
		std::wstring tstr;
		((ChkRackData*)data)->SetNewSklad(sklads.at(sel));
		((ChkRackData*)data)->GetRackTitle(&tstr);
		SetDlgItemText(IDC_RACK, tstr.c_str());
		Refresh();
	}
	return 0;
}

static bool readBarcode = false;
void ChkRackForm::OnBarcode(const wchar_t* _barcode)
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

	((ChkRackData*)data)->OnBarcode(barcode);
	((ChkRackData*)data)->GetRackTitle(&tstr);
	SetDlgItemText(IDC_RACK, tstr.c_str());

	int topI = listCtrl.GetLastVisibleItem();
	Refresh();
	if(topI >= 0)
		listCtrl.EnsureVisible(topI, FALSE);
	readBarcode = false;
}

LRESULT ChkRackForm::ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   std::wstring data;
   if( GetScanData(&data, lParam) )
		OnBarcode(data.c_str());

	return 1;
}

#ifdef ZEBEX
void ChkRackForm::HandleEvent()
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


LRESULT ChkRackForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
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

void ChkRackForm::UpdateLayout(const RECT& bounds, bool forceRecalc)
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

void OpenChkRack()
{
   ChkRackData *pfd = new ChkRackData();
	_Module.GetFrame()->Load(IDD_CHK_RACK, pfd);
}
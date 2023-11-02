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

struct DKA1DocItem : public IReflectableData
{
   wchar_t* item;
   DWORD qty;
   DECLARE_TYPE_REFLECTION(DKA1DocItem)
};

BEGIN_TYPE_REFLECTION(DKA1DocItem)
   REGISTER_STRING_MEMBER(DKA1DocItem, item)
   REGISTER_ULONG_SCALE_MEMBER2(DKA1DocItem, qty, QTY_SCALE, true)
END_TYPE_REFLECTION(DKA1DocItem)

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left,  /*L"Название"*/IDS_PRICE, NULL, L"item", 150 },
   { ListFormData::Header::Right, /*L"Кол-во"*/IDS_QTY_HEAD, NULL,   L"qty", 50 },
};

class DKA1DocData : public ListFormData
{
public:
   enum ItemType { Document, Rack, Item, Qty, ErrorType };
   //enum ControlType { CanInputWORack = -1, None = 0, Warning, Error };

   DKA1DocData(const wchar_t* doc);
   ~DKA1DocData();

   virtual const Header *GetHeader() const { return header; }
   virtual int ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }

   virtual const DataReflector& DataType() const { return DKA1DocItem().GetType(); }
	virtual int Count() const { return items.size(); }

   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }
   virtual void Clear() {}

   virtual bool Editing(int index) { return false; }
	virtual bool Selecting(int index) { return false; }
   virtual bool Get(IReflectableData* data, int index) const;

   bool New(const wchar_t* doc, ItemType type);

   void GetDocTitle(std::wstring* text) const;
   void GetPackTitle(std::wstring* text) const;

   void RemoveDoc();

protected:

	DKA1Impl *current;
	
	//DocTypeImpl docType;
   //WhAgents agent;

	mutable PriceImpl price;

	struct Data
	{
		wchar_t* id;
		int qty;
	};

	std::vector<Data> items;

protected:
   void RefreshItems();

   bool NewDoc(const wchar_t* doc);
   bool NewItem(const wchar_t* id);
};

#if ZEBEX
class DKA1DocForm : public ListForm, public BarcodeHandler//, public CCustomDraw<MovmentDocForm>
#else
class DKA1DocForm : public ListForm//, public CCustomDraw<DKA1DocForm>
#endif
{
public:
	DKA1DocForm() {}

   virtual bool SetData(IFormData *_data) { return SetDataEx(_data, 2); }

   BEGIN_MSG_MAP(DKA1DocForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_REMOVE, Remove)
      COMMAND_ID_HANDLER(IDC_INPUT_BARCODE, OnInputBC)
      MESSAGE_HANDLER(WM_SCAN_DATA, ScanData)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   virtual DWORD GetResourceID() const { return IDD_DKA1_DOC; }
   virtual DWORD GetMenuID() const { return IDD_ORDER_LIST; }

   virtual void UpdateLayout(const RECT& bounds, bool forceRecalc);

#if ZEBEX
   virtual void HandleEvent();

	HMODULE hLib;
   pGetLastNotifyEvent GetBCEvent;
   pGetLastBarcode GetBarcode;
#endif

   DECLARE_FORM(DKA1DocForm, IDD_DKA1_DOC)

   LRESULT OnKeyDown(LPNMHDR hdr);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Remove(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   LRESULT OnInputBC(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

protected:
   bool SetDataEx(IFormData *_data, int scale);
   void OnBarcode(const wchar_t* barcode);
};

IMPLEMENT_FORM(DKA1DocForm);

//
//----------------------------------- MovmentDocData ----------------------------------
//
DKA1DocData::DKA1DocData(const wchar_t *doc) : current(NULL)
{
   //SQLTable tb(WhAgentsImpl().Name());
   //tb.Select(&agent, L"where id=userid");

	NewDoc(doc);
}

DKA1DocData::~DKA1DocData()
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

void DKA1DocData::RefreshItems()
{
   items.clear();
   if( current == NULL )
      return;

	std::map<std::wstring, Data*> imap;
	std::vector<OrderItem>::iterator i = current->items.begin();
   for( ; i != current->items.end(); i++ )
   {
		std::map<std::wstring, Data*>::iterator fnd = imap.find(i->id);
		if( fnd == imap.end() )
		{
			Data d;
			d.id = i->id;
			d.qty = i->qty;
			items.push_back(d);
			Data* src = &items.at(items.size() - 1);
			imap.insert(std::map<std::wstring, Data*>::value_type(i->id, src));
		} else
			fnd->second->qty += i->qty;
   }
}

void DKA1DocData::RemoveDoc()
{
   if( current != NULL )
   {
      current->Remove();

      delete current;
      current = NULL;

		RefreshItems();
   }
}

bool DKA1DocData::New(const wchar_t* doc, DKA1DocData::ItemType type)
{
   bool ret = false;

	switch(type)
	{
	case Document:
		ret = NewDoc(doc);
		break;
	case Item:
		ret = NewItem(doc);
		break;
	//case Qty:
	//   ret = NewDoc(doc);
	//   break;
	}
   return ret;
}

bool DKA1DocData::NewDoc(const wchar_t* doc)
{
   if( current == NULL )
   {
      current = new DKA1Impl();
   } else
   {
      if( wcscmp(current->id, doc) == 0 )
         return true;
   }

   current->items.clear();
   current->id = L"";
   current->packBC = L"";

 //  wchar_t buf[3], *dest;
 //  dest = buf;
 //  *dest++ = doc[2];
 //  *dest++ = doc[3];
 //  *dest = L'\0';

 //  docType.id = buf;
	//docType.Read() ;

   current->id = (wchar_t*)doc;
   if( !current->Read() )
   {
      SYSTEMTIME st;
      GetLocalTime(&st);
      SystemTimeToFileTime(&st, &current->created);
      current->params = 0;
      current->id = current->holder.Add(doc);
		current->packBC = L"";
      current->items.clear();
      current->Write();
   } else
   {
      if( !current->IsDirty() )
      {
         current->ClearDirty(NULL, true);
         current->Write();
      }
   }

	RefreshItems();
   return true;
}

bool DKA1DocData::NewItem(const wchar_t* code)
{
	vector_t<OrderItem>::iterator i = current->items.begin();
	for(; i != current->items.end(); i++ )
	{
		if( wcscmp(i->mark, code) == 0 )
		{
			MessageBox(NULL, L"Товар уже введен в документ", L"Ошибка", MB_OK | MB_ICONSTOP);
			return false;
		}
	}

	if(wcslen(code) <= 30)
	{
		current->packBC = current->holder.Add(code);
		current->Write();
		return true;
	}

	std::wstring mark;
	PartCodeImpl::MakePartyCode(&mark, code);

	PartCodeImpl pci;
	pci.code = (wchar_t*)mark.c_str();
	if(pci.Read())
	{
		OrderItem oi;
		oi.id = current->holder.Add(pci.id);
		oi.mark = current->holder.Add(code);
		oi.qty = QTY_SCALE;
		current->items.push_back(oi);
		current->Write();
	}

	RefreshItems();
	return true;
}

void DKA1DocData::GetDocTitle(std::wstring* text) const
{
   text->clear();
   if( current != NULL )
      DecodeNumber(text, current->id, true);
}

void DKA1DocData::GetPackTitle(std::wstring* text) const
{
   text->clear();
   if( current != NULL )
		text->assign(current->packBC);
}

bool DKA1DocData::Get(IReflectableData* data, int index) const
{
	if( index < 0 || (unsigned)index >= items.size() )
      return false;

	const Data* oi = &items.at(index);
	price.id = (wchar_t*)oi->id;
	price.Read();

	((DKA1DocItem*)data)->item = price.name;
   ((DKA1DocItem*)data)->qty = oi->qty;

   return true;
}

//
//-------------------------------- MovmentDocForm --------------------------------
//
bool DKA1DocForm::SetDataEx(IFormData *_data, int scale)
{
   if( !ListForm::SetDataEx(_data, scale) )
      return false;
  
	std::wstring tstr;
   wchar_t text[MAX_PATH];
	((DKA1DocData*)data)->GetDocTitle(&tstr);
	wsprintf(text, L"Документ: %s", tstr.c_str());
	SetDlgItemText(IDC_DOC, text);

	((DKA1DocData*)data)->GetPackTitle(&tstr);
	SetDlgItemText(IDC_RACK, tstr.c_str());

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

LRESULT DKA1DocForm::OnInputBC(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
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
void DKA1DocForm::OnBarcode(const wchar_t* _barcode)
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

	if( bcLen >= 200 )
	{
		MessageBox(L"Отсканирован неправильный код", L"Ошибка", MB_OK | MB_ICONSTOP);
		SetFocus();
	} else
	{
		DKA1DocData::ItemType itemType = DKA1DocData::Item;
		if( !isItem && *barcode == L'9' && *(barcode+1) == L'1' )
		{
			itemType = DKA1DocData::Document;
		}

		if( ((DKA1DocData*)data)->New(barcode, itemType) )
		{
			std::wstring tstr;
			((DKA1DocData*)data)->GetDocTitle(&tstr);
			wsprintf(text, L"Документ: %s", tstr.c_str());
			SetDlgItemText(IDC_DOC, text);

			((DKA1DocData*)data)->GetPackTitle(&tstr);
			SetDlgItemText(IDC_RACK, tstr.c_str());
			Refresh();
		}
	}
	
	readBarcode = false;
}

LRESULT DKA1DocForm::ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   std::wstring data;
   if( GetScanData(&data, lParam) )
		OnBarcode(data.c_str());

	return 1;
}

#ifdef ZEBEX
void MovmentDocForm::HandleEvent()
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

LRESULT DKA1DocForm::Remove(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( data->Count() > 0 )
   {
      if( MessageBox(L"Удалить документ?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
      {
         ((DKA1DocData*)data)->RemoveDoc();

         SetDlgItemText(IDC_DOC, L"Документ:");
         Refresh();
      }
   }
   return 0;
}

LRESULT DKA1DocForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
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

void DKA1DocForm::UpdateLayout(const RECT& bounds, bool forceRecalc)
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

   CWindow wnd1(GetDlgItem(IDC_RACK));
   wnd1.GetWindowRect(rc);
   ScreenToClient(rc);
	wnd1.MoveWindow(rc.left, rc.top, right, rc.Height());

   btn.MoveWindow(rc.left + right+1, top + (rc.bottom - rc2.Height()) / 2, rc2.Width(), rc2.Height());

   SetListLayout(forceRecalc, rc.bottom + 2, bounds.bottom - rc.bottom + 3);
}

void OpenDKA1Doc(const wchar_t* doc)
{
   DKA1DocData *pfd = new DKA1DocData(doc);
	_Module.GetFrame()->Load(IDD_DKA1_DOC, pfd);
}
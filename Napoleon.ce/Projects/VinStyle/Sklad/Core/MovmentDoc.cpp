/*
 * Copyright (C), 2006-2013, Денис Мосягин
 *
 * Список заявок
 *
 *  ert   22/04/2013   creating
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

static const wchar_t _encode[]=L"1234567890АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ       ABCDEFGHIJKLMNOPQRSTUVWXYZ    _-+=/*";

#ifdef ZEBEX
typedef BOOL (*pZBCRSetPower)(BOOL dwState);
typedef BOOL (*pGetLastNotifyEvent) (PDWORD lpNotifyEvent);
typedef BOOL (*pGetLastBarcode) (LPTSTR lpszBarcode);
typedef BOOL (*pZBCRStartScan) (void);
typedef BOOL (*pZBCRStopScan) (void);
typedef BOOL (*pZBCRSetOutputMode) (BYTE dwMode);
#endif

struct MovmentDocItem : public IReflectableData
{
   wchar_t* item;
   DWORD qty;
   DECLARE_TYPE_REFLECTION(MovmentDocItem)
};

BEGIN_TYPE_REFLECTION(MovmentDocItem)
   REGISTER_STRING_MEMBER(MovmentDocItem, item)
   REGISTER_ULONG_SCALE_MEMBER2(MovmentDocItem, qty, QTY_SCALE, true)
END_TYPE_REFLECTION(MovmentDocItem)

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left,  /*L"Название"*/IDS_PRICE, NULL, L"item", 150 },
   { ListFormData::Header::Right, /*L"Кол-во"*/IDS_QTY_HEAD, NULL,   L"qty", 50 },
};

class MovmentDocData : public ListFormData
{
public:
   enum ItemType { Document, Rack, Item, Qty, ErrorType };
   enum ControlType { CanInputWORack = -1, None = 0, Warning, Error };

   MovmentDocData(const wchar_t* doc);
   ~MovmentDocData();

   virtual const Header *GetHeader() const { return header; }
   virtual int ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }

   virtual const DataReflector& DataType() const { return MovmentDocItem().GetType(); }
	virtual int Count() const { return items.size(); }

   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }
   virtual void Clear() {}

   virtual bool Editing(int index) { return Selecting(index); }

   virtual bool Selecting(int index);

   virtual bool Get(IReflectableData* data, int index) const;

   bool New(const wchar_t* doc, ItemType type);

   bool CanChange(ItemType type) const;

   bool CheckCurItem() const;

   void GetDocTitle(std::wstring* text) const;
   void GetRackTitle(std::wstring* text) const;

   void RemoveDoc();
	bool NewItem();
	void UpdateOrderItem();
	void CancelItem();
	bool CanExit();

	void SetSelectedItem(const PriceImpl& pi);

	const std::vector<ROWID>& ChooseItems() const { return barcodeItems; }

protected:
	int selectedItem;
	CMoveQtyDialog *qtyDlg;
   OrderImpl *current;
   std::wstring curRack;
	DocTypeImpl docType;

   WhAgents agent;
   PriceImpl curItem;
	mutable PriceImpl price;

   bool inputRack, blocked, waitDestRack;
	std::wstring curMark;
	std::wstring curBc;
	std::wstring curCode;
	std::wstring destRack;

	std::vector<OrderItem*> items;
	std::wstring scannedBarcode;
	std::vector<ROWID> barcodeItems;

protected:
   void RefreshItems();

   ControlType CheckType(ItemType type) const;

   bool NewDoc(const wchar_t* doc);
   bool NewRack(const wchar_t* rack);
   bool NewItem(const wchar_t* id);

	void PrepareQtyDlg(const PriceImpl& price, const std::wstring& mark, const std::wstring &bc);
};

#if ZEBEX
class MovmentDocForm : public ListForm, public BarcodeHandler, public CCustomDraw<MovmentDocForm>
#else
class MovmentDocForm : public ListForm, public CCustomDraw<MovmentDocForm>
#endif
{
public:
	MovmentDocForm() : chooseItem(false) {}

   virtual bool SetData(IFormData *_data) { return SetDataEx(_data, 2); }

   BEGIN_MSG_MAP(DocumentForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_REMOVE, Remove)
      COMMAND_ID_HANDLER(IDC_INPUT_BARCODE, OnInputBC)
      MESSAGE_HANDLER(WM_SCAN_DATA, ScanData)
      MESSAGE_HANDLER(NEW_ITEM, OnNewItem)
      NOTIFY_CODE_HANDLER_EX(LVN_KEYDOWN, OnKeyDown)
		NOTIFY_CODE_HANDLER_EX(NM_CLICK, ItemSelected)
      MESSAGE_HANDLER(UPDATE_ITEM, OnUpdateItem)
      MESSAGE_HANDLER(CANCEL_ITEM, OnCancelItem)
      MESSAGE_HANDLER(CHOOSE_ITEM, OnChooseItem)
      CHAIN_MSG_MAP(CCustomDraw<MovmentDocForm>)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   virtual DWORD GetResourceID() const { return IDD_MOVE_LIST; }
   virtual DWORD GetMenuID() const { return IDD_ORDER_LIST; }

   virtual void UpdateLayout(const RECT& bounds, bool forceRecalc);

#if ZEBEX
   virtual void HandleEvent();

	HMODULE hLib;
   pGetLastNotifyEvent GetBCEvent;
   pGetLastBarcode GetBarcode;
#endif

	virtual LRESULT SetCellInfo(LPNMHDR hdr);
   LRESULT ItemSelected(LPNMHDR hdr);

   DECLARE_FORM(MovmentDocForm, IDD_MOVE_QTY)

   LRESULT OnKeyDown(LPNMHDR hdr);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Remove(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   LRESULT OnInputBC(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/) { return CDRF_NOTIFYITEMDRAW; }
   DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);
   LRESULT ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
   LRESULT OnNewItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
   LRESULT OnChooseItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
	LRESULT OnUpdateItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
	{
		((MovmentDocData*)data)->UpdateOrderItem();
		Refresh();
		return 0;
	}

	LRESULT OnCancelItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
	{
		((MovmentDocData*)data)->CancelItem();
		return 0;
	}

protected:
   bool SetDataEx(IFormData *_data, int scale);
   void OnBarcode(const wchar_t* barcode);
	void ItemChoosed(int index);

   //HMODULE hLib;
	bool chooseItem;
	ListViewMultiLine choose;
	ChoosItemData chooseData;
};

IMPLEMENT_FORM(MovmentDocForm);

//
//----------------------------------- MovmentDocData ----------------------------------
//
MovmentDocData::MovmentDocData(const wchar_t *doc) : current(NULL), inputRack(false), blocked(false), qtyDlg(NULL), selectedItem(-1), waitDestRack(false)
{
   SQLTable tb(WhAgentsImpl().Name());
   tb.Select(&agent, L"where id=userid");

	NewDoc(doc);
}

MovmentDocData::~MovmentDocData()
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
	if( qtyDlg )
	{
		qtyDlg->DestroyWindow();
		qtyDlg = NULL;
	}
}

bool MovmentDocData::CanExit()
{
	if( qtyDlg && qtyDlg->IsWindowVisible() )
	{
		UpdateOrderItem();
		qtyDlg->ShowWindow(SW_HIDE);
		return false;
	}

	return true;
}

//static DocCheckItem* FindItem(const std::vector<DocCheckItem>& items, const wchar_t* id, const wchar_t* rack)
//{
//   std::vector<DocCheckItem>::const_iterator i = items.begin();
//   for( ; i != items.end(); i++ )
//   {
//      if( (*rack == L'\0' || i->rack.compare(rack) == 0) && i->id.compare(id) == 0 )
//         return (DocCheckItem*)(&(*i));
//   }
//
//   return NULL;
//}
//
//inline DocCheckItem* FindItem(const std::vector<DocCheckItem>& items, const OrderItem& item, bool checkRack)
//{
//   return FindItem(items, item.id, (checkRack) ? item.rack : L"");
//}

void MovmentDocData::RefreshItems()
{
   items.clear();
   if( current == NULL )
      return;

	std::vector<OrderItem>::iterator i = current->items.begin();
   for( ; i != current->items.end(); i++ )
   {
		if( curRack.empty() || wcscmp(curRack.c_str(), i->rack) == 0 )
			items.push_back(&(*i));
   }
}

void MovmentDocData::RemoveDoc()
{
   if( current != NULL )
   {
      current->Remove();

      curRack.clear();
      curItem.id = L"";

      delete current;
      current = NULL;

		RefreshItems();
   }
}

MovmentDocData::ControlType MovmentDocData::CheckType(MovmentDocData::ItemType type) const
{
   //switch( type )
   //{
   //case Document:
   //   return (ControlType)docType.controlDoc;
   //case Rack:
   //   return (ControlType)docType.controlRack;
   //case Item:
   //   return (ControlType)docType.controlItem;
   //case Qty:
   //   return (ControlType)docType.controlQty;
   //}

   return None;
}

bool MovmentDocData::CanChange(MovmentDocData::ItemType type) const
{
   //if( type == Document )
   //{
   //   if( !needControlDoc || docType.controlDoc == 0 )
   //      return true;
   //   return CheckCurItem();
   //}
   return true;
}

bool MovmentDocData::New(const wchar_t* doc, MovmentDocData::ItemType type)
{
   bool ret = false;

	if( !blocked )
	{

		switch(type)
		{
		case Document:
			selectedItem = -1;
			ret = NewDoc(doc);
			break;
		case Rack:
			if( !waitDestRack )
				selectedItem = -1;
			ret = NewRack(doc);
			break;
		case Item:
			selectedItem = -1;
			ret = NewItem(doc);
			break;
		//case Qty:
		//   ret = NewDoc(doc);
		//   break;
		}
	}
   return ret;
}

bool MovmentDocData::NewDoc(const wchar_t* doc)
{
   if( current == NULL )
   {
      current = new OrderImpl();
   } else
   {
		if( qtyDlg )
		{
			UpdateOrderItem();
			qtyDlg->ShowWindow(SW_HIDE);
		}

      if( wcscmp(current->id, doc) == 0 )
      {
         curRack.clear();
         return true;
      }
   }

   current->items.clear();
   current->id = L"";
   curRack.clear();
   curItem.id = L"";

   wchar_t buf[3], *dest;
   dest = buf;
   *dest++ = doc[2];
   *dest++ = doc[3];
   *dest = L'\0';

   bool fail = false;
   docType.id = buf;
	if( !docType.Read() || !docType.isMovement )
   {
      MessageBox(NULL, L"Документ не является перемещением", L"Ошибка", MB_OK | MB_ICONSTOP);
      fail = true;
   }

   if( !fail )
   {
      current->id = (wchar_t*)doc;
      if( !current->Read() )
      {
         SYSTEMTIME st;
         GetLocalTime(&st);
         SystemTimeToFileTime(&st, &current->created);
         current->params = 0;
         current->id = current->holder.Add(doc);
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
   }

	RefreshItems();
   return true;
}

bool MovmentDocData::NewRack(const wchar_t* rack)
{
   if( !CheckCurItem() )
      return false;

   if( current == NULL )
   {
      MessageBox(NULL, L"Введите, пожалуйста, документ", L"Ошибка", MB_OK | MB_ICONSTOP);
      return false;
   }

	if( waitDestRack )
	{
		destRack = rack;

		qtyDlg->EnableInput(true);

		std::wstring text;
      DecodeNumber(&text, destRack.c_str(), false);
		qtyDlg->SetDestRack(text.c_str());

		return false;
	}

	if( qtyDlg )
	{
		UpdateOrderItem();
		qtyDlg->ShowWindow(SW_HIDE);
	}

   curRack = rack;
   inputRack = true;

   curItem.id = L"";
	RefreshItems();
   return true;
}

void MovmentDocData::CancelItem()
{
	waitDestRack = false;
	destRack.clear();
}

void MovmentDocData::UpdateOrderItem()
{
	if( *curItem.id == L'\0' || qtyDlg == NULL || destRack.empty() )
		return;

	waitDestRack = false;
	int qty = qtyDlg->GetQty();

	OrderItem* item = (selectedItem < 0 ) ? NULL : items.at(selectedItem); //(OrderItem*)current->FindItem(curRack.c_str(), curItem.id); 
	if( item == NULL && qty != NULL )
	{
      OrderItem oi;
      oi.id = current->holder.Add(curItem.id);
      oi.qty = qty;
      oi.flags = 0;
		oi.mark = current->holder.Add(curMark.c_str());
		oi.barcode = current->holder.Add(curBc.c_str());
      oi.rack = current->holder.Add(curRack.c_str());
		oi.rackDest = current->holder.Add(destRack.c_str());
		oi.palletBarcode = L"";

      current->items.push_back(oi);
	} else 
	{
		if( qty == 0 )
		{
         if( MessageBox(NULL, L"Удалить товар из документа?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
         {
            std::vector<OrderItem>::iterator i = current->items.begin();
            for( ; i != current->items.end(); i++ )
               if( &(*i) == item )
               {
                  current->items.erase(i);
                  break;
               }
			}
		} else
		{
			item->qty = qty;
			if( destRack.empty() == false )
				item->rackDest = current->holder.Add(destRack.c_str());
		}
	}
	current->Write();
	curItem.id = L"";
	selectedItem = -1;

	RefreshItems();
}

bool MovmentDocData::NewItem(const wchar_t* code)
{
	curCode = code;
	//StopScan();
	owner->PostMessage(NEW_ITEM, 0, 0);
	return true;
}

bool MovmentDocData::NewItem()
{
	if( curCode.empty() )
		return false;

	const wchar_t* code = curCode.c_str();
	if( curRack.empty() )
   {
      MessageBox(NULL, L"Введите, пожалуйста, полку", L"Ошибка", MB_OK | MB_ICONSTOP);
		curCode.clear();
      return false;
   }

	if( waitDestRack && destRack.empty() )
	{
		if( MessageBox(NULL, L"Не введена полка назначения. Отменить предыдущий товар?", L"Ошибка", MB_YESNO | MB_ICONQUESTION) == IDNO )
		{
			waitDestRack = false;
			curCode.clear();
			return false;
		}
	}
	waitDestRack = false;

	std::wstring mark;
	std::wstring bc;
	if( price.ReadMark(code) )
	{
		MakeMarkCode(&mark, code);
	} else
	{
		if( !PriceImpl::GetItems(&barcodeItems, code) )
		{
			wchar_t buf[200];
			wsprintf(buf, L"Нет товара с кодом '%s'", code);
			MessageBox(NULL, buf, L"Ошибка", MB_OK | MB_ICONSTOP);
			curCode.clear();
			return false;
		}
		if( barcodeItems.size() == 1 )
		{
			price.Read(barcodeItems.front(), false);
			bc = code;
		} else
		{
			scannedBarcode = code;
			owner->PostMessage(CHOOSE_ITEM, 0, 0);
			return true;
		}
	}
	//blocked = true;

	PrepareQtyDlg(price, mark, bc);

	//blocked = false;
	curCode.clear();
   return true;
}

void MovmentDocData::PrepareQtyDlg(const PriceImpl& price, const std::wstring& mark, const std::wstring &bc)
{
	if( qtyDlg == NULL )
	{
		qtyDlg = new CMoveQtyDialog(L"");
		qtyDlg->Create(owner->m_hWnd);
	} else
	{
		UpdateOrderItem();
	}

	curItem.id = price.id;
	curItem.name = price.name;
	curItem.inPack = price.inPack;

	curItem.UnbindStrings();
	curMark = mark;
	curBc = bc;
	
	destRack.clear();
	waitDestRack = true;
	qtyDlg->EnableInput(!destRack.empty());

   DWORD qty = QTY_SCALE;

	qtyDlg->ShowWindow(SW_SHOW);
	qtyDlg->SetData(curItem.name, qty);
	qtyDlg->SetDestRack(destRack.c_str());
}


bool MovmentDocData::CheckCurItem() const
{
   //if( current == NULL || *curItem.id = L'\0' )
   //   return true;

   //ControlType qtyType = CheckType(Qty);
   //if( qtyType != Warning && qtyType != Error )
   //   return true;

   //DocCheckItem* item = FindItem(items, curItem.c_str(), (CheckType(Rack) == CanInputWORack) ? L"" : curRack.c_str());
   //if( item == NULL || item->qty == item->checkQty )
   //   return true;

   bool ret = true;
   //if( item->qty != item->checkQty )
   //{
   //   wchar_t num[10];
   //   wsprintf(num, L"%d", item->checkQty / QTY_SCALE);

   //   std::wstring buf;
   //   buf = L"Для товар '";
   //   buf += item->name;
   //   buf += L"' кол-во должно быть ";
   //   buf += num;
   //   MessageBox(NULL, buf.c_str(), L"Ошибка", MB_OK | 
   //      (qtyType == Error) ? MB_ICONSTOP : MB_ICONWARNING );

   //   ret = (qtyType != Error);
   //}

   return ret;
}

void MovmentDocData::GetDocTitle(std::wstring* text) const
{
   text->clear();
   if( current != NULL )
      DecodeNumber(text, current->id, true);
}

void MovmentDocData::SetSelectedItem(const PriceImpl& pi)
{
	PrepareQtyDlg(pi, L"", scannedBarcode);
	barcodeItems.clear();
	scannedBarcode.clear();
}

void MovmentDocData::GetRackTitle(std::wstring* text) const
{
   text->clear();
   if( !curRack.empty() )
      DecodeNumber(text, curRack.c_str(), false);
}

bool MovmentDocData::Get(IReflectableData* data, int index) const
{
	if( index < 0 || (unsigned)index >= items.size() )
      return false;

	OrderItem* oi = items.at(index);
	price.id = (wchar_t*)oi->id;
	price.Read();

	((MovmentDocItem*)data)->item = price.name;
   ((MovmentDocItem*)data)->qty = oi->qty;

   return true;
}

bool MovmentDocData::Selecting(int index)
{
	if( index < 0 || (unsigned)index >= items.size() || blocked )
      return false;

	blocked = true;

	OrderItem* oi = items.at(index);
	price.id = oi->id;
	price.Read();

	curItem.id = price.id;
	curItem.name = price.name;
	curItem.inPack = price.inPack;

	bool ret = false;

	if( qtyDlg == NULL )
	{
		qtyDlg = new CMoveQtyDialog(L"");
		qtyDlg->Create(owner->m_hWnd);
	}
	qtyDlg->ShowWindow(SW_SHOW);
	qtyDlg->SetData(curItem.name, oi->qty);

	destRack = oi->rackDest;
	waitDestRack = true;
	std::wstring text;
	DecodeNumber(&text, destRack.c_str(), false);
	qtyDlg->SetDestRack(text.c_str());

	selectedItem = index;


	blocked = false;
	return ret;
}

//
//-------------------------------- MovmentDocForm --------------------------------
//
bool MovmentDocForm::SetDataEx(IFormData *_data, int scale)
{
   if( !ListForm::SetDataEx(_data, scale) )
      return false;
  
	choose.Set(this);
	choose.SubclassWindow(GetDlgItem(IDC_SELECT_ITEM));
	SetupListCtrl(&choose, 2, &chooseData);

	std::wstring tstr;
   wchar_t text[MAX_PATH];
	((MovmentDocData*)data)->GetDocTitle(&tstr);
	wsprintf(text, L"Документ: %s", tstr.c_str());
	SetDlgItemText(IDC_DOC, text);

	((MovmentDocData*)data)->GetRackTitle(&tstr);
	wsprintf(text, L"Полка: %s", tstr.c_str());
	SetDlgItemText(IDC_RACK, text);

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

LRESULT MovmentDocForm::OnInputBC(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
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
void MovmentDocForm::OnBarcode(const wchar_t* _barcode)
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
   bool isItem = (bcLen == 13);
   bool refresh = false;

	if( bcLen >= 200 )
	{
		MessageBox(L"Отсканирован неправильный код", L"Ошибка", MB_OK | MB_ICONSTOP);
		SetFocus();
	} else
	{
		MovmentDocData::ItemType itemType = MovmentDocData::Item;
		if( !isItem && *barcode == L'9' && *(barcode+1) == L'1' )
		{
			itemType = MovmentDocData::Document;
		} else if ( !isItem && *barcode == L'7' && *(barcode+1) == L'7' )
		{
			itemType = MovmentDocData::Rack;
		}

		if( ((MovmentDocData*)data)->CanChange(itemType) && ((MovmentDocData*)data)->New(barcode, itemType) )
		{
			std::wstring tstr;
			((MovmentDocData*)data)->GetDocTitle(&tstr);
			wsprintf(text, L"Документ: %s", tstr.c_str());
			SetDlgItemText(IDC_DOC, text);

			((MovmentDocData*)data)->GetRackTitle(&tstr);
			wsprintf(text, L"Полка: %s", tstr.c_str());
			SetDlgItemText(IDC_RACK, text);
			Refresh();
		}
	}
	
	readBarcode = false;
}

LRESULT MovmentDocForm::OnChooseItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
	chooseItem = true;
	chooseData.items = &((MovmentDocData*)data)->ChooseItems();
	
	GetDlgItem(IDC_SELECT_LABEL).ShowWindow(SW_SHOW);
	choose.ShowWindow(SW_SHOW);
	choose.SetFocus();

	int count = chooseData.Count();
	choose.SetItemCount(count);
	if( count )
		choose.RedrawItems(choose.GetTopIndex(), count);
	return 1;
}

LRESULT MovmentDocForm::SetCellInfo(LPNMHDR hdr)
{
	if( hdr->hwndFrom != choose.m_hWnd )
		return ListForm::SetCellInfo(hdr);

	NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
	if( (di->item.mask & LVIF_IMAGE) != 0 )
		di->item.iImage = 4;
 
	if( !(di->item.mask & LVIF_TEXT) )
		return TRUE;

	int index = di->item.iItem;
	const DataReflector& reflector = chooseData.DataType();
	IReflectableData *rd = reflector.Create();
	if( chooseData.Get(rd, index))
	{
		const MemberType &tp = reflector.Type(chooseData.GetHeader()[di->item.iSubItem].field);
		tp.ToString(*rd, di->item.pszText, di->item.cchTextMax);
	} else
		*di->item.pszText = L'\0';

	delete rd;		

	return TRUE;
}

LRESULT MovmentDocForm::OnKeyDown(LPNMHDR hdr)
{
	if( hdr->hwndFrom != choose.m_hWnd  || !chooseItem )
		return ListForm::ItemSelected(hdr);

   NMLVKEYDOWN *kd = (NMLVKEYDOWN*)hdr;
   if( kd->wVKey == VK_RETURN )
   {
      int index = choose.GetSelectedIndex();
		if( index >= 0 )
			ItemChoosed(index);

		return TRUE;
	}
	return FALSE;
}

void MovmentDocForm::ItemChoosed(int index)
{
	chooseItem = false;

	choose.ShowWindow(SW_HIDE);
	GetDlgItem(IDC_SELECT_LABEL).ShowWindow(SW_HIDE);

	PriceImpl pi;
	pi.Read(((MovmentDocData*)data)->ChooseItems().at(index));
	((MovmentDocData*)data)->SetSelectedItem(pi);
}

LRESULT MovmentDocForm::ItemSelected(LPNMHDR hdr)
{
	if( hdr->hwndFrom != choose.m_hWnd  || !chooseItem )
		return ListForm::ItemSelected(hdr);

   int index = ((NMLISTVIEW*)hdr)->iItem;
	if( index < 0 || index >= chooseData.Count() )
		return FALSE;

	ItemChoosed(index);
	return TRUE;
}

LRESULT MovmentDocForm::OnNewItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   //CMessageLoop *ml = _Module.GetMessageLoop();

   //while( true )
   //{
   //   MSG msg;
   //   if( ::PeekMessage(&msg, m_hWnd, 0, 0, PM_REMOVE) == FALSE ) break;
   //   if( msg.message == WM_QUIT ) break;
   //   if( ml && ml->PreTranslateMessage(&msg) ) continue;

   //   ::TranslateMessage(&msg);
   //   ::DispatchMessage(&msg);
   //}

	//BringWindowToTop();

	if( ((MovmentDocData*)data)->NewItem() )
	{
		Refresh();
	}
	return 0;
}

LRESULT MovmentDocForm::ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
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

DWORD MovmentDocForm::OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
{
   NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
   //bool isHelded = ((MovmentDocData*)data)->HaveChanges(lvcd->nmcd.dwItemSpec);
   //if( isHelded )
   //{
   //   lvcd->clrTextBk = RGB(192, 192, 192);
   //}
   return CDRF_NOTIFYITEMDRAW;
}

LRESULT MovmentDocForm::Remove(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( data->Count() > 0 )
   {
      if( MessageBox(L"Удалить документ?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
      {
         ((MovmentDocData*)data)->RemoveDoc();

         SetDlgItemText(IDC_DOC, L"Документ:");
         SetDlgItemText(IDC_RACK, L"Полка:");
         Refresh();
      }
   }
   return 0;
}

//LRESULT MovmentDocForm::OnKeyDown(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
//{
//	if( chooseItem && wParam == VK_RETURN )
//	{
//		CListBox lb(GetDlgItem(IDC_SELECT_ITEM));
//
//		GetDlgItem(IDC_SELECT_LABEL).ShowWindow(SW_HIDE);
//		lb.ShowWindow(SW_HIDE);
//
//		int i = lb.GetCurSel();
//		if( i >= 0 )
//		{
//			ROWID* rid = (ROWID*) lb.GetItemDataPtr(i);
//			PriceImpl pi;
//			pi.Read(*rid);
//
//			((MovmentDocData*)data)->SetSelectedItem(pi);
//		}
//
//		chooseItem = false;
//	}
//	return 0;
//}

LRESULT MovmentDocForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
	if( chooseItem )
	{
		chooseItem = false;
		GetDlgItem(IDC_SELECT_LABEL).ShowWindow(SW_HIDE);
		choose.ShowWindow(SW_HIDE);
		return 0;
	}
   if( !((MovmentDocData*)data)->CanExit() )
      return 0;

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

void MovmentDocForm::UpdateLayout(const RECT& bounds, bool forceRecalc)
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

	CWindow label(GetDlgItem(IDC_SELECT_LABEL));

	label.GetWindowRect(rc);
	ScreenToClient(rc);
	label.SetWindowPos(HWND_TOP, bounds.left, bounds.top, bounds.right - bounds.left, rc.Height(), 0);
	label.ShowWindow(SW_HIDE);

	SetListLayout(forceRecalc, rc.Height(), 0, &choose, &chooseData);
	choose.SetWindowPos(HWND_TOP, 0, 0, 0, 0, SWP_NOMOVE | SWP_NOSIZE | SWP_HIDEWINDOW);
}

void OpenMovmentDoc(const wchar_t* doc)
{
   MovmentDocData *pfd = new MovmentDocData(doc);
	_Module.GetFrame()->Load(IDD_MOVE_QTY, pfd);
}
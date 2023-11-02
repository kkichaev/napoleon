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

void DecodeNumber(std::wstring* res, const wchar_t* src, bool isDoc)
{
   wchar_t buf[3];
   wchar_t *dest;
   buf[2] = L'\0';

   src += 2;
   if( isDoc )
   {
      dest = buf;
      *dest++ = *src++;
      *dest++ = *src++;
      src++;
      DocTypeImpl doc;
      doc.id = buf;
      if( doc.Read() )
      {
         res->append(doc.name);
         res->append(1, L' ');
      }
   }

   while( *src )
   {
      dest = buf;
      *dest++ = *src++;
      *dest++ = *src++;
      int pos = _wtoi(buf);
      if( pos <= 0 )
         break;

      if( pos <= sizeof(_encode)/sizeof(_encode[0]) )
         res->append(1, _encode[pos-1]);
      else
         res->append(1, L'?');

   }
}

struct DocCheckItem
{
   DWORD qty;
   DWORD checkQty;
   std::wstring rack;
   std::wstring id;
   std::wstring name;
   std::wstring docRack;
	std::wstring docId;

   bool ContainsInRack(const std::wstring& curRack) const
   {
      return (curRack.empty() || rack.compare(curRack) == 0 || docRack.compare(curRack) == 0);
   }

   void Set(const OrderItem& item, bool isControl, PriceImpl& price)
   {
		id = (isControl) ? item.id : item.barcode;
      qty = 0;
      checkQty = 0;

      if( isControl )
      {
         rack = item.rack;
         checkQty = item.qty;
      }
      else
      {
         docRack = item.rack;
			docId = item.id;
         qty = item.qty;
      }

      //price.id = item.id;
		if( price.ReadBarcode(id.c_str()) )
         name = price.name;
      else
      {
         name = L"ШК '";
         name += id;
         name += L"'";
      }
   }
};

struct OrderListItem : public IReflectableData
{
   wchar_t* item;
   DWORD qty;
   DWORD cQty;
   DECLARE_TYPE_REFLECTION(OrderListItem)
};

BEGIN_TYPE_REFLECTION(OrderListItem)
   REGISTER_STRING_MEMBER(OrderListItem, item)
   REGISTER_ULONG_SCALE_MEMBER2(OrderListItem, qty, QTY_SCALE, true)
   REGISTER_ULONG_SCALE_MEMBER2(OrderListItem, cQty, QTY_SCALE, true)
END_TYPE_REFLECTION(OrderListItem)

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left,  /*L"Название"*/IDS_PRICE, NULL, L"item", 150 },
   { ListFormData::Header::Right, /*L"Кол-во"*/IDS_QTY_HEAD, NULL,   L"qty", 50 },
   { ListFormData::Header::Right, /*L"Кол-во"*/IDS_CTRL_QTY, NULL,   L"cQty", 50 },
};

class OrderListData : public ListFormData
{
public:
   enum ItemType { Document, Rack, Item, Qty, ErrorType };
   enum ControlType { CanInputWORack = -1, None = 0, Warning, Error };

   OrderListData();
   ~OrderListData();

   virtual const Header *GetHeader() const { return header; }
   virtual int ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }

   virtual const DataReflector& DataType() const { return OrderListItem().GetType(); }
	virtual int Count() const;

   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }
   virtual void Clear() {}

   virtual bool Editing(int index) { return Selecting(index); }

   virtual bool Selecting(int index);

   virtual bool Get(IReflectableData* data, int index) const;

   bool New(const wchar_t* doc, ItemType type);

   bool CanChange(ItemType type) const;
	bool HaveChanges(int index) const;

   bool CheckCurItem() const;

   void GetDocTitle(std::wstring* text) const;
   void GetRackTitle(std::wstring* text) const;

	bool CheckPallet(const std::wstring& barCode);
	bool IsHavePallet(const std::wstring& barCode) const;

	void RemoveDoc();
	bool NewItem();
	void UpdateOrderItem();
	void CancelItem() { curItem.id = L""; }
	bool CanExit();

	void SetSelectedItem(const PriceImpl& pi);

	const std::vector<ROWID>& ChooseItems() const { return barcodeItems; }

protected:
	//int selectedItem;
	CQTYDialog *qtyDlg;
   OrderImpl *current;
   std::wstring curRack;
	DocTypeImpl docType;

   WhAgents agent;
   PriceImpl curItem;
	mutable PriceImpl price;

   bool inputRack, blocked, needControlDoc;
	std::wstring curMark;
	std::wstring curBc;
	std::wstring curCode;

	ControlDocImpl ctrlDoc;

   std::vector<DocCheckItem> items;
	//std::vector<OrderItem*> items;
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

static ListFormData::Header chooseHeader[] =
{
   { ListFormData::Header::Left, IDS_PRICE, NULL, L"name", 80 },
};
const ListFormData::Header *ChoosItemData::GetHeader() const { return chooseHeader; }
int ChoosItemData::ColumnsCount() const { return sizeof(chooseHeader) / sizeof(chooseHeader[0]); }

BEGIN_TYPE_REFLECTION(ChooseItem)
   REGISTER_STRING_MEMBER(ChooseItem, name)
END_TYPE_REFLECTION(ChooseItem)

#if ZEBEX
class OrderListForm : public ListForm, public BarcodeHandler, public CCustomDraw<OrderListForm>
#else
class OrderListForm : public ListForm, public CCustomDraw<OrderListForm>
#endif
{
public:
	OrderListForm() : chooseItem(false) {}

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
      CHAIN_MSG_MAP(CCustomDraw<OrderListForm>)
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

   DECLARE_FORM(OrderListForm, IDD_ORDER_LIST)

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
		((OrderListData*)data)->UpdateOrderItem();
		Refresh();
		return 0;
	}

	LRESULT OnCancelItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
	{
		((OrderListData*)data)->CancelItem();
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

IMPLEMENT_FORM(OrderListForm);

//
//----------------------------------- PriceImpl ----------------------------------
//
void MakeMarkCode(std::wstring* val, const wchar_t*barcode)
{
	const wchar_t *p = wcschr(barcode, L'-');
	if( p != NULL ) p++;
	else p = barcode;

	val->assign(p, 3);
	p += 3;
	while( *p == '0' )
		p++;
	if( *p != '\0' ) {
		val->append(L" ");
		val->append(p);
	}
}

bool PriceImpl::ReadMark(const wchar_t* barcode)
{
	std::wstring val;
	std::wstring whereStr;
	MakeMarkCode(&val, barcode);
	whereStr = L"WHERE markBegin <= '"; whereStr += val; whereStr += L"' and markEnd >= '"; whereStr += val; whereStr += L"'";

	SkMarksImpl smi;
	SQLTable st(smi.Name());
	if( st.Select(&smi, whereStr.c_str()) )
	{
		id = smi.id;
		return Read();
	}
	return false;
}

bool PriceImpl::GetItems(std::vector<ROWID> *ridList, const wchar_t* barcode)
{
	ridList->clear();

	PriceImpl pp;
   SQLTable t(pp.Name());
	std::wstring whereStr = L"WHERE barcode='"; whereStr += barcode; whereStr += L"' or bcPack='"; whereStr += barcode; whereStr += L"'";

	t.RIDList(ridList, whereStr.c_str());
	return (ridList->size() > 0);
}

bool PriceImpl::ReadBarcode(const wchar_t* barcode)
{
	std::vector<ROWID> rid;
	if( GetItems(&rid, barcode) )
	{
		Read(rid.front(), false);
		return true;
	}

	//SQLTable t(Name());
	//std::wstring whereStr = L"WHERE barcode='"; whereStr += barcode; whereStr += L"'";
	//if( t.Select(this, whereStr.c_str()) )
	//	return true;

	id = (wchar_t*)barcode;
	return Read();
}

//
//----------------------------------- OrderListData ----------------------------------
//
OrderListData::OrderListData() : current(NULL), needControlDoc(false), inputRack(false), blocked(false), qtyDlg(NULL)
{
   SQLTable tb(WhAgentsImpl().Name());
   tb.Select(&agent, L"where id=userid");
}

OrderListData::~OrderListData()
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

int OrderListData::Count() const
{
   if( current == NULL )
      return 0;

   int count = 0;
   std::vector<DocCheckItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
      if( i->ContainsInRack(curRack) )
         count++;

   return count;
}

bool OrderListData::HaveChanges(int index) const
{
   if( !needControlDoc )
      return false;

   std::vector<DocCheckItem>::const_iterator i = items.begin();
   while( i != items.end() )
   {
      if( i->ContainsInRack(curRack) )
      {
         if( index == 0 )
            break;
         index--;
      }
      i++;
   }

   if( i == items.end() )
      return false;

   return (i->qty != i->checkQty);
}

bool OrderListData::CanExit()
{
	if( qtyDlg && qtyDlg->IsWindowVisible() )
	{
		UpdateOrderItem();
		qtyDlg->ShowWindow(SW_HIDE);
		return false;
	}

   if( !current || !needControlDoc )
      return true;

   bool res = true;

   ControlType qt = CheckType(Qty);
   if( qt == Warning || qt == Error )
   {
      std::vector<DocCheckItem>::const_iterator i = items.begin();
      for( ; i != items.end(); i++ )
      {
         if( i->qty != i->checkQty )
         {
            std::wstring buf;
            buf = L"Ошибка в товаре '";
            buf += i->name;
            buf += L"'";
            MessageBox(NULL, buf.c_str(), L"Ошибка", MB_OK | (qt == Error) ? MB_ICONSTOP : MB_ICONWARNING );

            res = (qt != Error);
            break;
         }
      }
   }
   return res;
}

static DocCheckItem* FindItem(const std::vector<DocCheckItem>& items, const wchar_t* barcode, const wchar_t* rack)
{
   std::vector<DocCheckItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      if( (*rack == L'\0' || i->rack.compare(rack) == 0) && i->id.compare(barcode) == 0 )
         return (DocCheckItem*)(&(*i));
   }

   return NULL;
}

inline DocCheckItem* FindItem(const std::vector<DocCheckItem>& items, const OrderItem& item, bool checkRack)
{
   return FindItem(items, item.barcode, (checkRack) ? item.rack : L"");
}

void OrderListData::RefreshItems()
{
   items.clear();
   if( current == NULL )
      return;

   PriceImpl price;
   if( needControlDoc )
   {
      std::vector<OrderItem>::const_iterator i = ctrlDoc.items.begin();
      for( ; i != ctrlDoc.items.end(); i++ )
      {
         DocCheckItem item;
         item.Set(*i, true, price);
         items.push_back(item);
      }
   }

   std::vector<OrderItem>::const_iterator i = current->items.begin();
   for( ; i != current->items.end(); i++ )
   {
      DocCheckItem* item = FindItem(items, *i, true);
      if( item == NULL )
      {
         DocCheckItem ditem;
         ditem.Set(*i, false, price);
         items.push_back(ditem);
      }
      else
      {
			if( item->docId.empty() )
			{
				item->qty = i->qty;
				item->docRack = i->rack;
				item->docId = i->id;
			} else
			{
				item->qty += i->qty;
				//DocCheckItem ditem;
				//ditem.Set(*i, false, price);
				//items.push_back(ditem);
			}
      }
   }
	
 //  items.clear();
 //  if( current == NULL )
 //     return;

	//std::vector<OrderItem>::iterator i = current->items.begin();
 //  for( ; i != current->items.end(); i++ )
 //  {
	//	if( curRack.empty() || wcscmp(curRack.c_str(), i->rack) == 0 )
	//		items.push_back(&(*i));
 //  }
}

void OrderListData::RemoveDoc()
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

OrderListData::ControlType OrderListData::CheckType(OrderListData::ItemType type) const
{
   switch( type )
   {
   case Document:
      return (ControlType)docType.controlDoc;
   case Rack:
      return (ControlType)docType.controlRack;
   case Item:
      return (ControlType)docType.controlItem;
   case Qty:
      return (ControlType)docType.controlQty;
   }

   return None;
}

bool OrderListData::CanChange(OrderListData::ItemType type) const
{
   if( type == Document )
   {
      if( !needControlDoc || docType.controlDoc == 0 )
         return true;
      return CheckCurItem();
   }
   return true;
}

bool OrderListData::New(const wchar_t* doc, OrderListData::ItemType type)
{
   bool ret = false;

	if( !blocked )
	{
		switch(type)
		{
		case Document:
			ret = NewDoc(doc);
			break;
		case Rack:
			ret = NewRack(doc);
			break;
		case Item:
			if( CheckPallet(doc) )
				ret = true;
			else
				ret = NewItem(doc);
			break;
		//case Qty:
		//   ret = NewDoc(doc);
		//   break;
		}
	}
   return ret;
}

bool OrderListData::NewDoc(const wchar_t* doc)
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
   if( !docType.Read() )
   {
      MessageBox(NULL, L"Не определен тип документа", L"Ошибка", MB_OK | MB_ICONSTOP);
      fail = true;
   }

	if( docType.isRouteList )
   {
      OpenRouteList(doc);
      return true;
   }

	if( docType.isMovement )
	{
		OpenMovmentDoc(doc);
		return true;
	}

	if( docType.isDKA1 )
	{
		//OpenDKA1Doc(doc);
		return true;
	}

	if( docType.isDKA2 )
	{
		//OpenDKA2Doc(doc);
		return true;
	}

	if( docType.isScanDoc )
	{
		OpenScanDoc(doc);
		return true;
	}

	if( CheckType(Document) != None )
   {
      ctrlDoc.id = (wchar_t*)doc;
      needControlDoc = ctrlDoc.Read();
      if( !needControlDoc )
      {
         fail = true;
         delete current;
         current = NULL;
         MessageBox(NULL, L"Нет данных для контроля", L"Ошибка", MB_OK | MB_ICONSTOP );
      }
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

bool OrderListData::NewRack(const wchar_t* rack)
{
   if( !CheckCurItem() )
      return false;

   if( current == NULL )
   {
      MessageBox(NULL, L"Введите, пожалуйста, документ", L"Ошибка", MB_OK | MB_ICONSTOP);
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

void OrderListData::UpdateOrderItem()
{
	if( *curItem.id == L'\0' || qtyDlg == NULL || !CheckCurItem()  )
		return;

	qtyDlg->ShowWindow(SW_HIDE);

	int qty = qtyDlg->GetQty();

	if( agent.canInputInPack && curItem.inPack > 0 )
      qty = (DWORD)((__int64)qty * curItem.inPack / QTY_SCALE);

	//OrderItem* item = (selectedItem < 0 ) ? NULL : items.at(selectedItem); //(OrderItem*)current->FindItem(curRack.c_str(), curItem.id); 
	OrderItem* item = (OrderItem*)current->FindBC(curRack.c_str(), curItem.barcode); 
	if( item == NULL && qty != NULL )
	{
      OrderItem oi;
      oi.id = current->holder.Add(curItem.id);
      oi.qty = qty;
      oi.flags = 0;
		oi.mark = current->holder.Add(curMark.c_str());
		oi.barcode = current->holder.Add(curBc.c_str());
      oi.rack = current->holder.Add(curRack.c_str());
		oi.rackDest = L"";
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
			item->qty = qty;
	}
	current->Write();
	curItem.id = L"";

	RefreshItems();
}

bool OrderListData::NewItem(const wchar_t* code)
{
	curCode = code;
	//StopScan();
	owner->PostMessage(NEW_ITEM, 0, 0);
	return true;
}

bool OrderListData::NewItem()
{
	if( curCode.empty() )
		return false;

	const wchar_t* code = curCode.c_str();
	if( wcscmp(curItem.barcode, code) != 0 && wcscmp(curItem.bcPack, code) != 0 && !CheckCurItem() )
      return false;

   ControlType rackType = CheckType(Rack);
   bool checkRack = rackType != CanInputWORack;   
   if( ((curRack.empty() || current == NULL) && checkRack) || (checkRack && agent.inputRack != 0 && !inputRack) )
	//if( curRack.empty() )
   {
      MessageBox(NULL, L"Введите, пожалуйста, полку", L"Ошибка", MB_OK | MB_ICONSTOP);
		curCode.clear();
      return false;
   }

	std::wstring mark;
	std::wstring bc;
	if( price.ReadMark(code) )
	{
		MakeMarkCode(&mark, code);
	} else
	{
		PartCodeImpl::MakePartyCode(&mark, code);

		PartCodeImpl pci;
		pci.code = (wchar_t*)mark.c_str();
		if(pci.Read())
		{
			if(current->FindMark(code) != NULL)
			{
				MessageBox(NULL, L"Товар уже присутствует в документе", L"Ошибка", MB_OK | MB_ICONSTOP);
				return false;
			}

			price.id = pci.id;
			if( price.Read() )
			{
				OrderItem oi;
				oi.id = current->holder.Add(price.id);
				oi.qty = QTY_SCALE;
				oi.flags = 0;
				oi.mark = current->holder.Add(code);
				oi.barcode = current->holder.Add(price.barcode);
				oi.rack = current->holder.Add(curRack.c_str());
				oi.rackDest = L"";
				oi.palletBarcode = L"";

				current->items.push_back(oi);
				current->Write();
				curItem.id = L"";

				RefreshItems();

				return true;
			}
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
	}
	//blocked = true;

	PrepareQtyDlg(price, mark, bc);

	//blocked = false;
	curCode.clear();
   return true;
}

bool OrderListData::IsHavePallet(const std::wstring& barCode) const
{
	vector_t<OrderItem>::const_iterator i = current->items.begin();
	for( ; i != current->items.end(); i++ )
	{
		if( wcscmp(i->palletBarcode, barCode.c_str()) == 0 )
			return true;
	}

	return false;
}

bool OrderListData::CheckPallet(const std::wstring& barCode)
{
	PalletsImpl pi;
	pi.barcode = (wchar_t*)barCode.c_str();
	
	bool ret = pi.Read();
	if(ret)
	{
		if(IsHavePallet(barCode))
		{
			ret = false;
		} else
		{
			vector_t<PalletItem>::const_iterator i = pi.items.begin();
			for( ; i != pi.items.end(); i++ )
			{
				OrderItem* item = (OrderItem*)current->FindBC(curRack.c_str(), i->barcode);
				if( item != NULL )
				{
					item->qty += i->qty;
				} else
				{
					OrderItem oi;
					oi.id = current->holder.Add(i->id);
					oi.qty = i->qty;
					oi.flags = 0;
					oi.mark = L"";
					oi.barcode = current->holder.Add(i->barcode);
					oi.palletBarcode = current->holder.Add(barCode.c_str());
					oi.rack = current->holder.Add(curRack.c_str());;
					oi.rackDest = L"";

					current->items.push_back(oi);
				}
			}
			current->Write();
			RefreshItems();
		}
	}

	return ret;
}

void OrderListData::PrepareQtyDlg(const PriceImpl& price, const std::wstring& mark, const std::wstring &bc)
{
   ControlType rackType = CheckType(Rack);
   bool inputWORack = (rackType != Error);
   if( needControlDoc )
   {
      ControlType itemType = CheckType(Item);
      //checkItem = ctrlDoc.FindItem((rackType != CanInputWORack) ? curRack.c_str() : L"", code);
		if( ctrlDoc.FindItem(L"", price.barcode) == NULL )
      {
         if( (itemType == Warning || itemType == Error))
         {
            MessageBox(NULL, L"В контрольном документе нет такого товара", L"Ошибка", MB_OK | 
               (itemType == Warning) ? MB_ICONWARNING : MB_ICONSTOP);
            if( itemType == Error )
            {
					curItem.id = L"";
               return;
            }
         }
      } else 
      {
         if( (rackType == Error || rackType == Warning) && ctrlDoc.FindItem(curRack.c_str(), price.barcode) == NULL )
         {
            MessageBox(NULL, L"В контрольном документе нет такого товара на этой полке", L"Ошибка", MB_OK | 
               (rackType == Warning) ? MB_ICONWARNING : MB_ICONSTOP);
            if( rackType == Error )
            {
					curItem.id = L"";
               return;
            }
         }
      }
   }

	if( qtyDlg == NULL )
	{
		qtyDlg = new CQTYDialog(L"");
		qtyDlg->Create(owner->m_hWnd);
	} else
	{
		if( agent.canMixInput == 0 || wcscmp(curItem.id, price.id) != 0 )
			UpdateOrderItem();
	}

	if( agent.canMixInput != 0 && wcscmp(curItem.id, price.id) == 0 )
		qtyDlg->AddQty(1);
	else 
	{	
		curItem.id = price.id;
		curItem.name = price.name;
		curItem.barcode = price.barcode;
		curItem.bcPack = price.bcPack;
		curItem.inPack = price.inPack;

		curItem.UnbindStrings();
		curMark = mark;
		curBc = bc;

		int qty = QTY_SCALE;
		if( bc.compare(price.bcPack) == 0 && agent.canInputInPack == 0 && price.inPack > 0 )
			qty = price.inPack;

		if( current != NULL )
		{
			OrderItem* oi = (OrderItem*)current->FindItem(curRack.c_str(), price.id);
			if( oi != NULL )
				qty = oi->qty;
		}

		qtyDlg->SetData(curItem.name, qty);
		if( agent.canMixInput != 0 )
			qtyDlg->SetMixData(price.inPack);
	}

	if( agent.canInputQty != 0 || agent.canMixInput != 0 )
   {
		qtyDlg->ShowWindow(SW_SHOW);
	} else
	{
		UpdateOrderItem();
	}
}


bool OrderListData::CheckCurItem() const
{
   if( current == NULL || *curItem.id == L'\0' || qtyDlg == NULL )
      return true;

   ControlType qtyType = CheckType(Qty);
   if( qtyType != Warning && qtyType != Error )
      return true;

	int qty = qtyDlg->GetQty();
	if( agent.canInputInPack && curItem.inPack > 0 )
      qty = (DWORD)((__int64)qty * curItem.inPack / QTY_SCALE);

	DocCheckItem* item = FindItem(items, curItem.barcode, (CheckType(Rack) == CanInputWORack) ? L"" : curRack.c_str());
   if( item == NULL || qty == item->checkQty )
      return true;

   bool ret = true;
   if( qty != item->checkQty && qty != 0 ) // дадим возможность удалить товар
   {
      wchar_t num[10];
      wsprintf(num, L"%d", item->checkQty / QTY_SCALE);

      std::wstring buf;
      buf = L"Для товар '";
      buf += item->name;
      buf += L"' кол-во должно быть ";
      buf += num;
      MessageBox(NULL, buf.c_str(), L"Ошибка", MB_OK | 
         (qtyType == Error) ? MB_ICONSTOP : MB_ICONWARNING );

      ret = (qtyType != Error);
   }

   return ret;
}

void OrderListData::GetDocTitle(std::wstring* text) const
{
   text->clear();
   if( current != NULL )
      DecodeNumber(text, current->id, true);
}

void OrderListData::SetSelectedItem(const PriceImpl& pi)
{
	PrepareQtyDlg(pi, L"", scannedBarcode);
	barcodeItems.clear();
	scannedBarcode.clear();
}

void OrderListData::GetRackTitle(std::wstring* text) const
{
   text->clear();
   if( !curRack.empty() )
      DecodeNumber(text, curRack.c_str(), false);
}

bool OrderListData::Get(IReflectableData* data, int index) const
{
	if( index < 0 || (unsigned)index >= items.size() )
      return false;

   std::vector<DocCheckItem>::const_iterator i = items.begin();
   while( i != items.end() )
   {
      if( i->ContainsInRack(curRack) )
      {
         if( index == 0 )
            break;
         index--;
      }
      i++;
   }

   if( i == items.end() )
      return false;

   ((OrderListItem*)data)->item = (wchar_t*)i->name.c_str();
   ((OrderListItem*)data)->qty = i->qty;
   ((OrderListItem*)data)->cQty = i->checkQty;


   return true;
}

bool OrderListData::Selecting(int index)
{
	if(agent.canInputQty == 0 && agent.canMixInput == 0 )
		return false;

   if( index < 0 )
      return false;

   std::vector<DocCheckItem>::const_iterator i = items.begin();
   while( i != items.end() )
   {
      if( i->ContainsInRack(curRack) )
      {
         if( index == 0 )
            break;
         index--;
      }
      i++;
   }

   if( i == items.end() )
      return false;

	//blocked = true;

   bool ret = false;
   const DocCheckItem& srcI = (*i);
	OrderItem* oi = (OrderItem*)current->FindItem(srcI.docRack.c_str(), srcI.docId.c_str());
	if( oi == NULL )
		return ret;

	//OrderItem* oi = items.at(index);
	price.id = oi->id;
	price.Read();

	PrepareQtyDlg(price, L"", price.barcode);

	//blocked = false;
	return ret;
}

//
//-------------------------------- ChoosItemData --------------------------------
//
bool ChoosItemData::Get(IReflectableData* data, int index) const
{
	if( !items || index < 0 || index >= (int)items->size() )
		return false;

	price.Read(items->at(index));
	((ChooseItem*)data)->name = price.name;
	return true;
}

//
//-------------------------------- OrderListForm --------------------------------
//
bool OrderListForm::SetDataEx(IFormData *_data, int scale)
{
   if( !ListForm::SetDataEx(_data, scale) )
      return false;
  
	choose.Set(this);
	choose.SubclassWindow(GetDlgItem(IDC_SELECT_ITEM));
	SetupListCtrl(&choose, 2, &chooseData);

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

LRESULT OrderListForm::OnInputBC(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
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
void OrderListForm::OnBarcode(const wchar_t* _barcode)
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
		OrderListData::ItemType itemType = OrderListData::Item;
		if( !isItem && *barcode == L'9' && *(barcode+1) == L'1' )
		{
			itemType = OrderListData::Document;
		} else if ( !isItem && *barcode == L'7' && *(barcode+1) == L'7' )
		{
			itemType = OrderListData::Rack;
		}

		if( ((OrderListData*)data)->CanChange(itemType) && ((OrderListData*)data)->New(barcode, itemType) )
		{
			std::wstring tstr;
			((OrderListData*)data)->GetDocTitle(&tstr);
			wsprintf(text, L"Документ: %s", tstr.c_str());
			SetDlgItemText(IDC_DOC, text);

			((OrderListData*)data)->GetRackTitle(&tstr);
			wsprintf(text, L"Полка: %s", tstr.c_str());
			SetDlgItemText(IDC_RACK, text);
			Refresh();
		}
	}
	
	readBarcode = false;
}

LRESULT OrderListForm::OnChooseItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
	chooseItem = true;
	chooseData.items = &((OrderListData*)data)->ChooseItems();
	
	GetDlgItem(IDC_SELECT_LABEL).ShowWindow(SW_SHOW);
	choose.ShowWindow(SW_SHOW);
	choose.SetFocus();

	int count = chooseData.Count();
	choose.SetItemCount(count);
	if( count )
		choose.RedrawItems(choose.GetTopIndex(), count);
	return 1;
}

LRESULT OrderListForm::SetCellInfo(LPNMHDR hdr)
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

LRESULT OrderListForm::OnKeyDown(LPNMHDR hdr)
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

void OrderListForm::ItemChoosed(int index)
{
	chooseItem = false;

	choose.ShowWindow(SW_HIDE);
	GetDlgItem(IDC_SELECT_LABEL).ShowWindow(SW_HIDE);

	PriceImpl pi;
	pi.Read(((OrderListData*)data)->ChooseItems().at(index));
	((OrderListData*)data)->SetSelectedItem(pi);
}

LRESULT OrderListForm::ItemSelected(LPNMHDR hdr)
{
	if( hdr->hwndFrom != choose.m_hWnd  || !chooseItem )
		return ListForm::ItemSelected(hdr);

   int index = ((NMLISTVIEW*)hdr)->iItem;
	if( index < 0 || index >= chooseData.Count() )
		return FALSE;

	ItemChoosed(index);
	return TRUE;
}

LRESULT OrderListForm::OnNewItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
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

	if( ((OrderListData*)data)->NewItem() )
	{
		Refresh();
	}
	return 0;
}

LRESULT OrderListForm::ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   std::wstring data;
   if( GetScanData(&data, lParam) )
		OnBarcode(data.c_str());

	return 1;
}

#ifdef ZEBEX
void OrderListForm::HandleEvent()
{
   DWORD eventCode;

   if( GetBCEvent(&eventCode) && eventCode == BCR_NOTIFY_RECEIVE_BARCODE )
   {
      wchar_t buf[MAX_PATH];
      GetBarcode(buf);
      OnBarcode(buf);
		//MessageBox(buf, L"Штрихкод", MB_OK);
   }
}
#endif

DWORD OrderListForm::OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
{
   NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
   bool isHelded = ((OrderListData*)data)->HaveChanges(lvcd->nmcd.dwItemSpec);
   if( isHelded )
   {
      lvcd->clrTextBk = RGB(192, 192, 192);
   }
   return CDRF_NOTIFYITEMDRAW;
}

LRESULT OrderListForm::Remove(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( data->Count() > 0 )
   {
      if( MessageBox(L"Удалить документ?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
      {
         ((OrderListData*)data)->RemoveDoc();

         //SetDlgItemText(IDC_DOC, L"Документ:");
         //SetDlgItemText(IDC_RACK, L"Полка:");
         //Refresh();

#ifdef ZEBEX
			((MainFrame*)_Module.GetFrame())->SetBarcodeHandler(NULL);

			pZBCRSetPower fn = (pZBCRSetPower)GetProcAddress(hLib, L"ZBCRSetPower");
			fn(FALSE);

			FreeLibrary(hLib);
#else
			StopScan();
#endif
			OpenMainForm(); 
		}
   }
   return 0;
}

//LRESULT OrderListForm::OnKeyDown(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
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
//			((OrderListData*)data)->SetSelectedItem(pi);
//		}
//
//		chooseItem = false;
//	}
//	return 0;
//}

LRESULT OrderListForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
	if( chooseItem )
	{
		chooseItem = false;
		GetDlgItem(IDC_SELECT_LABEL).ShowWindow(SW_HIDE);
		choose.ShowWindow(SW_HIDE);
		return 0;
	}
   if( !((OrderListData*)data)->CanExit() )
      return 0;

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

void OrderListForm::UpdateLayout(const RECT& bounds, bool forceRecalc)
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

void OpenOrderList()
{
   OrderListData *pfd = new OrderListData();
   _Module.GetFrame()->Load(IDD_ORDER_LIST, pfd);
}
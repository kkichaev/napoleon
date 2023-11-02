/*
* Copyright (C), 2007-2021, Денис Мосягин
*
* Складской документ 
*
*  ert   30/07/2021   creating
*/
#include "stdafx.h"

#include <Module.h>

#include "ObjImpl.h"
#include "DocImpl.h"
#include "FormEntries.h"
#include <StdFuncs.h>
#include <NapoleonRes.h>

#include "Preference.h"
#include <Doctype.h>
#include "Qty.h"

const UINT WM_ON_BC = WM_USER + 100;
struct BCPriceData : IReflectableData
{
	wchar_t* id;
	wchar_t* packName;
	wchar_t* name;

	DWORD qty;
	WORD boxed;

	DECLARE_TYPE_REFLECTION(BCPriceData);
};

BEGIN_TYPE_REFLECTION(BCPriceData)
   REGISTER_STRING_MEMBER(BCPriceData, id)
   REGISTER_STRING_MEMBER(BCPriceData, packName)
   REGISTER_STRING_MEMBER(BCPriceData, name)
   REGISTER_USHORT_MEMBER(BCPriceData, boxed)
   REGISTER_ULONG_SCALE_MEMBER2(BCPriceData, qty, QTY_SCALE, true)
END_TYPE_REFLECTION(BCPriceData)

struct WhDocFormItem : public IReflectableData
{
	const wchar_t *name;
	const wchar_t *qty;
	const wchar_t *inputQty;

	DECLARE_TYPE_REFLECTION(WhDocFormItem)
};

BEGIN_TYPE_REFLECTION(WhDocFormItem)
	REGISTER_STRING_MEMBER(WhDocFormItem, name)
	REGISTER_STRING_MEMBER(WhDocFormItem, qty)
	REGISTER_STRING_MEMBER(WhDocFormItem, inputQty)
END_TYPE_REFLECTION(WhDocFormItem)

static ListFormData::Header header[] = 
{
	{ ListFormData::Header::Left, -1, L"Название", L"name", 100 },
	{ ListFormData::Header::Right, -1, L"Кол-во", L"qty", 35 },
	{ ListFormData::Header::Right, -1, L"Скан.", L"inputQty", 35 },
};

struct WhDocData : public ListFormData
{
	WhDocData(WhOutDocImpl* doc) {this->doc = doc;; }
	~WhDocData() { delete doc; }

	virtual const Header *GetHeader() const { return header; }
	virtual int ColumnsCount() const { return sizeof(::header)/sizeof(::header[0]); }

	virtual COLORREF GetItemColor(const NMCUSTOMDRAW& nmcdr) const;
	virtual COLORREF GetBackColor(const NMCUSTOMDRAW& nmcdr) const;
	virtual const DataReflector& DataType() const { return WhDocFormItem().GetType(); }

	virtual int Count() const { return doc->items.size(); }

	virtual bool Get(IReflectableData* data, int index) const;

	bool ReadPrice(const wchar_t* id) const;

   virtual bool Editing(int index);
	virtual bool Selecting(int index) { return Editing(index); }

	virtual int OnBarcode(const wchar_t* bc);

	mutable PriceImpl price;
	COLORREF textColor;

	WhOutDocImpl* doc;
	mutable std::wstring priceId, qtyBuf;
	mutable bool priceReaded;
};

class WhDocForm : public ListForm, public CCustomDraw<WhDocForm>
{
public:
	WhDocForm() : keyDownHandled(false), newItemSelected(-1) {}

	virtual bool SetData(IFormData *_data) { return SetDataEx(_data, 3); }
	virtual void LoadMenuBar(bool hideSIP);

	virtual void UpdateLayout(const RECT& bounds, bool forceRecalc);

	BEGIN_MSG_MAP(WhDocForm)
		NOTIFY_HANDLER(IDC_TABLE, LVN_KEYDOWN, OnKeyDown)
		COMMAND_ID_HANDLER(IDC_BACK, Backing)
		MESSAGE_HANDLER(WM_ON_BC, OnBC);
		MESSAGE_HANDLER(WM_SCAN_DATA, ScanData)
		CHAIN_MSG_MAP(CCustomDraw<WhDocForm>)
		CHAIN_MSG_MAP(ListForm)
	END_MSG_MAP()

	DECLARE_FORM(WhDocForm, IDD_OUT_DOCUMENT);

	DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/)
	{
		return CDRF_NOTIFYITEMDRAW;
	}

	DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);

	virtual DWORD GetResourceID() const { return IDD_DOCUMENT; }
   LRESULT OnKeyDown(int id, LPNMHDR hdr, BOOL &bHandled);
	LRESULT OnBC(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
	LRESULT OnRefreshItem(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
	LRESULT ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

	virtual bool OnChar(ListViewMultiLine* ctrl, UINT charSym);
	void SetErrorInBC(const wchar_t* msg) { bcError = msg; }

protected:
	CStatic bcLabel;

	bool SetDataEx(IFormData *_data, int scale);

	LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
	std::wstring barcode;
	std::wstring bcError;
	boolean keyDownHandled;
	int newItemSelected;
};

IMPLEMENT_FORM(WhDocForm);

static ListFormData::Header header1[] = 
{
	{ ListFormData::Header::Left, -1, L"Название", L"name", 100 },
//	{ ListFormData::Header::Right, -1, L"Кол-во", L"qty", 35 },
	{ ListFormData::Header::Right, -1, L"Скан.", L"inputQty", 35 },
};

LRESULT WhDocForm::ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   if( GetScanData(&barcode, lParam) )
		OnBC(0, 0, 0, bHandled);

	return 1;
}

struct WhInvDocData : public WhDocData
{
	WhInvDocData(WhOutDocImpl* doc) : WhDocData(doc) {}

	virtual const Header *GetHeader() const { return header1; }
	virtual int ColumnsCount() const { return sizeof(::header1)/sizeof(::header1[0]); }

	virtual int OnBarcode(const wchar_t* bc);
};

class WhInvForm : public WhDocForm
{
public:
	DECLARE_FORM(WhInvForm, IDD_INVENT_DOCUMENT);

};
IMPLEMENT_FORM(WhInvForm);

COLORREF WhDocData::GetItemColor(const NMCUSTOMDRAW& nmcdr) const 
{ 
	bool selected = ((nmcdr.uItemState & CDIS_SELECTED) != 0);
	return selected ? RGB(255,255,255) : textColor; 
}

COLORREF WhDocData::GetBackColor(const NMCUSTOMDRAW& nmcdr) const
{
	bool selected = ((nmcdr.uItemState & CDIS_SELECTED) != 0);
	unsigned index = nmcdr.dwItemSpec;
   if( index < doc->items.size() )
	{
		const WhOutDocItem &oi = doc->items[index];
		if(ReadPrice(oi.id))
		{
			if(*price.barcode == 0 && *price.boxBarcode == 0)
			{
				return selected ? RGB(0,0,150) : RGB(212,212,212);
			}
		}
	}
	return selected ? RGB(0,0,150) : RGB(255,255,255);
}

int WhDocData::OnBarcode(const wchar_t* bc)
{
	std::wstring wstr;
	wstr.append(L"select qtyInPack as qty, '' as packName, 0 as boxed, id, name from price where barcode = '").append(bc).append(L"'")
		 .append(L" union all select qtyInPack as qty, packName, 1 as boxed, id, name  from price where boxBarcode = '").append(bc).append(L"'");

	SQLTable tbl(PriceImpl().Name());

	BCPriceData bcData;
	if(tbl.Select(wstr.c_str(), &bcData))
	{
		int index = 0;
		vector_t<WhOutDocItem>::iterator i = doc->items.begin();
		for( ; i != doc->items.end(); i++, index++ )
		{
			if(wcscmp(i->id, bcData.id) == 0)
			{
				if( ((i->isBaseUnit + bcData.boxed) == 1) || bcData.qty == QTY_SCALE )
				{
					if(i->inputQty < i->qty)
					{
						i->inputQty += QTY_SCALE;
						doc->Write();
					} else
					{
						((WhDocForm*)owner)->SetErrorInBC(L"Товар уже набран");
					}
				} else
				{
					((WhDocForm*)owner)->SetErrorInBC(L"Упаковка не соответствует накладной");
				}
				return index;
			}
		}
	}

	return -1;
}

bool WhDocData::ReadPrice(const wchar_t* id) const
{
	if(priceId.compare(id) != 0)
	{
		priceId = id;
		price.id = (wchar_t*)priceId.c_str();
		priceReaded = price.Read();
	}
		
	return priceReaded;
}

bool WhDocData::Get(IReflectableData* data, int index) const
{
   if( (unsigned)index >= doc->items.size() )
      return false;

   const WhOutDocItem &oi = doc->items[index];
   if( ReadPrice(oi.id) )
      ((WhDocFormItem*)data)->name = price.name;
   else
      ((WhDocFormItem*)data)->name = L"?";

	((WhDocFormItem*)data)->qty = QtyToText(&qtyBuf, oi.qty, oi.isBaseUnit, price);
	((WhDocFormItem*)data)->inputQty = QtyToText(&qtyBuf, oi.inputQty, oi.isBaseUnit, price);
   return true;
}

bool WhDocData::Editing(int index)
{
   if( (unsigned)index >= doc->items.size() || !doc->IsDirty())
      return false;

   WhOutDocItem &oi = doc->items[index];

	CQTYDialog dlg;
	dlg.SetData(&oi);
	bool changed = (dlg.DoModal() == IDOK);
	if(changed)
	{
		doc->Write();
	}
	owner->listCtrl.SetFocus();

	return changed;
}

bool WhDocForm::SetDataEx(IFormData *_data, int scale)
{
   if( ListForm::SetDataEx(_data, scale) == false )
      return false;

   ((WhDocData*)data)->textColor = listCtrl.GetTextColor();
   LoadMenuBar(true);

   CStatic title(::GetDlgItem(m_hWnd, IDC_ORG_TITLE));
	title.SetWindowTextW(((WhDocData*)_data)->doc->name);

	listCtrl.SetFocus();
	StartScan(m_hWnd);
   return true;
}

bool WhDocForm::OnChar(ListViewMultiLine* ctrl, UINT charSym)
{ 
	if(keyDownHandled)
	{
		keyDownHandled = false;
		return false;
	}

	bool ret = false;
	if(charSym == 0xd && !barcode.empty())
	{
		PostMessage(WM_ON_BC, 0, 0);
		ret = true;
	} else if(charSym >= 0x30 && charSym <= 0x39)
	{
		barcode.append(1, charSym);
		ret = true;
	}
	bcLabel.SetWindowText(barcode.c_str());
	return ret; 
}

LRESULT WhDocForm::OnBC(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
	if(!((WhDocData*)data)->doc->IsDirty())
	{
		barcode.clear();
		bcLabel.SetWindowText(barcode.c_str());
		return 0;
	}

	bcError.clear();

	int count = data->Count();
	int selected = ((WhDocData*)data)->OnBarcode(barcode.c_str());
	barcode.clear();
	bcLabel.SetWindowText(barcode.c_str());

	if(selected >= 0)
	{
		curIndex = -1;
		if(count != data->Count())
		{
			Refresh();
			listCtrl.EnsureVisible(selected, FALSE);
			listCtrl.SetItemState(selected, LVIS_SELECTED, LVIS_SELECTED);
//			newItemSelected = selected;
		} else
		{
			listCtrl.EnsureVisible(selected, FALSE);
			listCtrl.SetItemState(selected, LVIS_SELECTED, LVIS_SELECTED);
			listCtrl.RedrawItems(selected, selected);
		}
		return 1;
	}
	if(bcError.empty() == false)
	{
		MessageBox(bcError.c_str(), L"Ошибка", MB_ICONERROR|MB_OK);
		bcError.clear();
		listCtrl.SetFocus();
	}

	return 0;
}

LRESULT WhDocForm::OnKeyDown(int id, LPNMHDR hdr, BOOL &bHandled)
{
	bHandled = FALSE;

	LPNMLVKEYDOWN kh = (LPNMLVKEYDOWN)hdr;
	WORD key = kh->wVKey;
	
	if(key >= 0x30 && key <= 0x39)
	{
		keyDownHandled = true;
		barcode.append(1, key);
	} else if(key == VK_RETURN && !barcode.empty())
	{
		keyDownHandled = true;
		PostMessage(WM_ON_BC, 0, 0);
		bHandled = TRUE;
	} 
	bcLabel.SetWindowText(barcode.c_str());

	return bHandled;
}


LRESULT WhDocForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
	StopScan();
	if(wcscmp(((WhDocData*)data)->doc->type, L"Invent") == 0)
	{
		OpenMainForm();
	} else 
		OpenDocList(StringToType(((WhDocData*)data)->doc->type));
	return 0;
}

DWORD WhDocForm::OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
{
   NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
   lvcd->clrText = ((WhDocData*)data)->GetItemColor(lvcd->nmcd);
	lvcd->clrTextBk = ((WhDocData*)data)->GetBackColor(lvcd->nmcd);
	return CDRF_NOTIFYITEMDRAW;
}

void WhDocForm::LoadMenuBar(bool hideSIP)
{
   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, (hideSIP) ? SHCMBF_HIDESIPBUTTON : 0));
}

void WhDocForm::UpdateLayout(const RECT& bounds, bool forceRecalc)
{
   CRect rc, rcl;
   CStatic title(GetDlgItem(IDC_ORG_TITLE));
	
	if(bcLabel.IsWindow() == FALSE)
		bcLabel.Attach(GetDlgItem(IDC_BC));
	
	CButton gd(GetDlgItem(IDC_GET_DOC));	
	gd.ShowWindow(SW_HIDE);

	bcLabel.ShowWindow(SW_SHOW);
	bcLabel.GetWindowRect(rcl);

	int r = bounds.right - 1;
   rc.top = 2;
   rc.bottom = 2;
   rc.left = 2;
	rc.right = r;
   CalcTextHeight(title.m_hWnd, &rc);

   rc.right = r;
   title.MoveWindow(rc, FALSE);
	
	int bclH = rcl.Height();
	rcl.left = bounds.left;
	rcl.bottom = bounds.bottom;
	rcl.top = rcl.bottom - bclH;
	rcl.right = bounds.right;
	bcLabel.MoveWindow(rcl, FALSE);

	SetListLayout(forceRecalc, rc.bottom, bounds.bottom - rc.bottom - bclH);
}

int WhInvDocData::OnBarcode(const wchar_t* bc)
{
	std::wstring wstr;
	wstr.append(L"select qtyInPack as qty, '' as packName, 0 as boxed, id, name from price where barcode = '").append(bc).append(L"'")
		 .append(L" union all select qtyInPack as qty, packName, 1 as boxed, id, name  from price where boxBarcode = '").append(bc).append(L"'");

	SQLTable tbl(PriceImpl().Name());

	BCPriceData bcData;
	if(tbl.Select(wstr.c_str(), &bcData))
	{
		int index = 0;
		vector_t<WhOutDocItem>::iterator i = doc->items.begin();
		for( ; i != doc->items.end(); i++, index++ )
		{
			if(wcscmp(i->id, bcData.id) == 0 && i->isBaseUnit == bcData.boxed)
			{
				i->inputQty += QTY_SCALE;
				i->qty = i->inputQty;
				doc->Write();
				return index;
			}
		}
		WhOutDocItem item;
		item.id = doc->holder.Add(bcData.id);
		item.isBaseUnit = bcData.boxed;
		item.packCoef = bcData.qty;
		item.inputQty = QTY_SCALE;
		item.qty = QTY_SCALE;

		doc->items.push_back(item);
		doc->Write();
		return index;
	}

	return -1;
}

const wchar_t* WhOutDocImpl::Description() const
{
	return ((flags & WFSended) != 0) ? L"отправлен" : L"";
}

bool WhOutDocImpl::CanRemove() const
{
   return (MessageBox(GetActiveWindow(), L"Удалить документ?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION) == IDYES);
}

bool WhOutDocImpl::Create(const WHDocs& src)
{
   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &created);

	date = src.date;

	id = holder.Add(src.id);
	type = holder.Add(src.type);
	number = holder.Add(src.number);
	name = holder.Add(src.name);

	flags = 0;
	vector_t<WHDocItem>::const_iterator i = src.items.begin();
	for( ; i != src.items.end(); i++ )
	{
		WhOutDocItem dest;

		dest.id = holder.Add(i->id);
		dest.qty = i->qty;
		dest.isBaseUnit = i->isBaseUnit;
		dest.packCoef = i->packCoef;
		dest.inputQty = 0;

		items.push_back(dest);
	}

	return Write();
}

WhOutDocImpl* WhOutDocImpl::Find(const WHDocs& src)
{
	WhOutDocImpl* ret = new WhOutDocImpl();
	SQLTable t(ret->Name());
	std::wstring wh;
	wchar_t buf[50];
	wsprintf(buf, L"%d%09d", (DWORD)((*(__int64*)&src.date) / 1000000000), (DWORD)((*(__int64*)&src.date) % 1000000000));

	wh.append(L"where type='").append(src.type).append(L"' and id='").append(src.id).append(L"' and number='").append(src.number).append(L"' and date=").append(buf);

	std::vector<ROWID> rows;
	t.RIDList(&rows, wh.c_str());
	if(rows.size())
	{
		ret->Read(rows.at(0));
		return ret;
	}
	delete ret;
	return NULL;
}

void WhOutDocImpl::EditDocument(UINT retForm)
{
	if(wcscmp(type, L"Invent") == 0)
	{
		_Module.GetFrame()->Load(IDD_INVENT_DOCUMENT, new WhInvDocData(this));
	} else
	{
		_Module.GetFrame()->Load(IDD_OUT_DOCUMENT, new WhDocData(this));
	}
}

bool WhOutDocImpl::ClearDirty(SQLTable *table, bool reverse)
{
   const wchar_t *updStr = L"flags";
   if( reverse )
   {
      if(flags & WFSended) flags &= (~WFSended);
      else flags |= WFSended;
   } else
   {
      flags |= WFSended;
   }

   return (table == NULL) ? true : table->Update(*this, updStr, rid);
}

void WhOutDocImpl::InitInvent()
{

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &created);

	date = created;

	id = L"";
	type = holder.Add(L"Invent");
	number = holder.Add(L"");
	name = holder.Add(L"");

	flags = 0;

	Write();
}
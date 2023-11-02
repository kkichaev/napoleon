/*
* Copyright (C), 2007-2009, Денис Мосягин
*
* Реализация функций заказа
*
*  ert   20/08/2007   creating
*  ert   17/06/2008   modifying (SQL impl)
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
#include "DataReader.h"
#include "NetExchange.h"

struct DocumentFormItem : public IReflectableData
{
	const wchar_t *name;
	const wchar_t* qty;
	//DWORD inputQty;

	DECLARE_TYPE_REFLECTION(DocumentFormItem)
};

BEGIN_TYPE_REFLECTION(DocumentFormItem)
	REGISTER_STRING_MEMBER(DocumentFormItem, name)
	REGISTER_STRING_MEMBER(DocumentFormItem, qty, QTY_SCALE, true)
	//REGISTER_ULONG_SCALE_MEMBER2(DocumentFormItem, inputQty, QTY_SCALE, true)
END_TYPE_REFLECTION(DocumentFormItem)

static ListFormData::Header header[] = 
{
	{ ListFormData::Header::Left, -1, L"Название", L"name", 100 },
	{ ListFormData::Header::Right, -1, L"Кол-во", L"qty", 35 },
	//{ ListFormData::Header::Right, -1, L"Скан.", L"inputQty", 35 },
};

struct DocumentData : public ListFormData
{
	DocumentData(WHDocsImpl* doc) {this->doc = doc; }
	~DocumentData() { delete doc; }

	virtual const Header *GetHeader() const { return header; }
	virtual int ColumnsCount() const { return sizeof(::header)/sizeof(::header[0]); }

	virtual COLORREF GetItemColor(int index) const { return textColor; }
	virtual const DataReflector& DataType() const { return DocumentFormItem().GetType(); }

	virtual int Count() const { return doc->items.size(); }

	virtual bool Get(IReflectableData* data, int index) const;

	mutable PriceImpl price;
	COLORREF textColor;

	WHDocsImpl* doc;
	mutable std::wstring qtyBuf;
};

class DocumentForm : public ListForm, public CCustomDraw<DocumentForm>
{
public:
	DocumentForm() {}

	virtual bool SetData(IFormData *_data) { return SetDataEx(_data, 3); }
	virtual void LoadMenuBar(bool hideSIP);

	virtual void UpdateLayout(const RECT& bounds, bool forceRecalc);

	BEGIN_MSG_MAP(DocumentForm)
		COMMAND_ID_HANDLER(IDC_BACK, Backing)
		COMMAND_ID_HANDLER(IDC_GET_DOC, GetDoc)
		CHAIN_MSG_MAP(CCustomDraw<DocumentForm>)
		CHAIN_MSG_MAP(ListForm)
	END_MSG_MAP()

	DECLARE_FORM(DocumentForm, IDD_DOCUMENT);

	DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/)
	{
		return CDRF_NOTIFYITEMDRAW;
	}

	DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);

	virtual DWORD GetResourceID() const { return IDD_DOCUMENT; }

protected:
	bool SetDataEx(IFormData *_data, int scale);

	LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
	LRESULT GetDoc(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
};

IMPLEMENT_FORM(DocumentForm);

bool DocumentData::Get(IReflectableData* data, int index) const
{
   if( (unsigned)index >= doc->items.size() )
      return false;

   const WHDocItem &oi = doc->items[index];

   //((DocumentFormItem*)data)->inputQty = 0;

   price.id = oi.id;
   if( price.Read() )
      ((DocumentFormItem*)data)->name = price.name;
   else
      ((DocumentFormItem*)data)->name = L"?";

	((DocumentFormItem*)data)->qty = QtyToText(&qtyBuf, oi.qty, oi.isBaseUnit, price);
   return true;
}

bool DocumentForm::SetDataEx(IFormData *_data, int scale)
{
   if( ListForm::SetDataEx(_data, scale) == false )
      return false;

   ((DocumentData*)data)->textColor = listCtrl.GetTextColor();
   LoadMenuBar(true);

   CStatic title(::GetDlgItem(m_hWnd, IDC_ORG_TITLE));
	title.SetWindowTextW(((DocumentData*)_data)->doc->name);

   return true;
}

LRESULT DocumentForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
	OpenDocList(StringToType(((DocumentData*)data)->doc->type));
	return 0;
}

DWORD DocumentForm::OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
{
   NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
   lvcd->clrText = ((DocumentData*)data)->GetItemColor(lvcd->nmcd.dwItemSpec);
	return CDRF_NOTIFYITEMDRAW;
}

void DocumentForm::LoadMenuBar(bool hideSIP)
{
   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, (hideSIP) ? SHCMBF_HIDESIPBUTTON : 0));
}

void DocumentForm::UpdateLayout(const RECT& bounds, bool forceRecalc)
{
   CRect rc, getDoc;
   CStatic title(GetDlgItem(IDC_ORG_TITLE));
	CButton gd(GetDlgItem(IDC_GET_DOC));
	
	gd.GetWindowRect(getDoc);
	ScreenToClient(getDoc);

	int r = bounds.right - 1 - getDoc.Width();
   rc.top = 2;
   rc.bottom = 2;
   rc.left = 2;
	rc.right = r;
   CalcTextHeight(title.m_hWnd, &rc);

   rc.right = r;
   title.MoveWindow(rc, FALSE);
	
	getDoc.MoveToXY(r, 1);
	gd.MoveWindow(getDoc);

	SetListLayout(forceRecalc, rc.bottom, bounds.bottom - rc.bottom);
}

LRESULT DocumentForm::GetDoc(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
	ReportRequestParam prm;
	prm.reportName = L"req_doc";

	WHDocsImpl *src = ((DocumentData*)data)->doc;

	ReqDocParam rd;
	rd.date = src->date;
	rd.id = src->id;
	rd.number = src->number;
	rd.status = 0;
	rd.type = src->type;

	ArrayRcvr<ReqDocAnswerImpl> rcvAnsw(IDS_REQ_ANSW_PROGRESS);
	prm.objects.push_back(&rcvAnsw);
	prm.param = &rd;

	int ec = _Module.RequestReport(prm);
	if(ec)
		_Module.ShowErrorBox(ec, prm.answer.c_str(), IDS_ERROR_IN_SYNC);
	else
	{
		if(rcvAnsw.list.size())
		{
			ReqDocAnswer* answ = rcvAnsw.list.at(0);
			if(answ->status == 1)
			{
				WhOutDocImpl* dest = new WhOutDocImpl();

				if(dest->Create(*src))
				{
					dest->EditDocument(0);
				} else 
				{
					::MessageBox(GetActiveWindow(), L"Ошибка при создании документа", L"Ошибка", MB_OK|MB_ICONERROR);
				}
			} else 
			{
				::MessageBox(GetActiveWindow(), answ->message, L"Ошибка", MB_OK|MB_ICONERROR);
				src->Remove();

				BOOL b = FALSE;
				Backing(0, 0, 0, b);
			}
		}
	}

	return 0;
}

void WHDocsImpl::EditDocument(UINT retForm)
{
	WhOutDocImpl* doc = WhOutDocImpl::Find(*this);
	if(doc != NULL)
	{
		doc->EditDocument(retForm);
		delete this;
		return ;
	}
	_Module.GetFrame()->Load(IDD_DOCUMENT, new DocumentData(this));
}

const wchar_t* QtyToText(std::wstring* dest, DWORD qty, WORD isBaseUnit, const PriceImpl& item)
{
	if(qty == 0) 
	{
		return L"";
	}

	wchar_t buf[20], buf2[20];
	ConvertScaling(buf, (long)qty, QTY_SCALE);
	FormatScaling(buf, buf2, sizeof(buf2)/sizeof(buf2[0]), qty % QTY_SCALE, QTY_SCALE, true);

	dest->assign(buf2);
	if(!isBaseUnit)
	{
		dest->append(L" ").append(item.packName);
	}
	return dest->c_str();
}
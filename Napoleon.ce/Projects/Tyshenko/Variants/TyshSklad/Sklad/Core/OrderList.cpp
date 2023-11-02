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
#include "DocType.h"

struct ListDocItem : public IReflectableData
{
   const wchar_t *name;
   FILETIME date;
   const wchar_t *number;

   DECLARE_TYPE_REFLECTION(ListDocItem)
};

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, -1, L"", L"name", 100 },
   { ListFormData::Header::Left, -1, L"Номер", L"number", 40 },
   { ListFormData::Header::Left, -1, L"Дата", L"date", 40 },
};

const wchar_t* TypeToWhere(DocListTypes type)
{
	return type == DtDeliveries ? L"type='Delivery'" :
		type == DtIncomes ? L"type='Income'" :
		L"type='Invent'";
}

DocListTypes StringToType(const wchar_t* type)
{
	if(wcscmp(type, L"Delivery") == 0) return DtDeliveries;
	if(wcscmp(type, L"Income") == 0) return DtIncomes;
	return DtInvent;
}

const wchar_t* TypeToTitle(DocListTypes type)
{
	return type == DtDeliveries ? L"Отгрузки" :
		type == DtIncomes ? L"Приходы" :
		L"Инвентаризация";
}

class ListDocData : public ListFormData
{
public:
   ListDocData(DocListTypes type);
   ~ListDocData();

	virtual const Header *GetHeader() const 
	{ 
		header[0].wTitle = TypeToTitle(type);
		return header; 
	}

	virtual int ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }

   virtual const DataReflector& DataType() const { return ListDocItem().GetType(); }
   virtual int Count() const { return docList->Count(); }
   virtual bool Get(IReflectableData* data, int index) const;
   
   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }

   virtual bool Selecting(int index);
	virtual bool Editing(int index) { return Selecting(index); }

protected:
	DocListTypes type;
   const DocType *dt;
   DocumentList *docList;
};

class ListDoc : public ListForm
{
public:
   ListDoc();

   virtual bool SetData(IFormData *_data);

   BEGIN_MSG_MAP(ListDoc)
      //NOTIFY_CODE_HANDLER_EX(NM_CLICK, DoSelect)
      //NOTIFY_CODE_HANDLER_EX(LVN_GETDISPINFO, SetCellInfo)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   DECLARE_FORM(ListDoc, IDD_ORDER_LIST)

   //DWORD GetHitTest() const { return hitFlags; }

	virtual DWORD GetResourceID() const { return IDD_DOCS_LIST; }

protected:
   //LRESULT DoSelect(LPNMHDR hdr);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
   //virtual LRESULT SetCellInfo(LPNMHDR hdr);

//protected:
//   DWORD hitFlags;
};

BEGIN_TYPE_REFLECTION(ListDocItem)
   REGISTER_STRING_MEMBER(ListDocItem, name)
   REGISTER_FILETIME_MEMBER(ListDocItem, date)
   REGISTER_STRING_MEMBER(ListDocItem, number)
END_TYPE_REFLECTION(ListDocItem)

ListDocData::ListDocData(DocListTypes type)
{
	this->type = type;
	dt = docTypeManager.GetDocType(dtWHDoc);

	dt->GetDocuments(L"", &docList, TypeToWhere(type), L"date");
}

ListDocData::~ListDocData()
{
	delete docList;
}

bool ListDocData::Selecting(int index)
{
   IDocument *d = docList->Get(index);
   if( d == NULL ) return false;
   docList->Unbind(d);
   d->EditDocument(0);
   return false;
}

bool ListDocData::Get(IReflectableData* data, int index) const
{
   IDocument *d = docList->Get(index);
   if( d == NULL ) return false;

	((ListDocItem*)data)->name = ((WHDocsImpl*)d)->name;
   ((ListDocItem*)data)->date = d->Date();
	((ListDocItem*)data)->number = ((WHDocsImpl*)d)->number;

   return true;
}

IMPLEMENT_FORM(ListDoc);

ListDoc::ListDoc()
{
}

LRESULT ListDoc::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   OpenMainForm();
   return 0;
}

bool ListDoc::SetData(IFormData *_data)
{
   if( ListForm::SetDataEx(_data, 3) == false )
      return false;

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID()));
   SetListLayout(false);

   //int count = listCtrl.GetItemCount();
   //if( count > 0 )
   //   listCtrl.EnsureVisible(count-1, FALSE);

   return true;
}

void OpenDocList(DocListTypes type)
{
	if(type == DtInvent)
	{
		WhOutDocImpl *doc = new WhOutDocImpl();
		SQLTable table(doc->Name());
		std::vector<ROWID> ids;
		std::wstring where(L"where ");
		where.append(TypeToWhere(type)).append(L" and flags=0");
		table.RIDList(&ids, where.c_str());
		if(ids.size())
		{
			doc->Read(ids.front());
		} else 
		{
			doc->InitInvent();
		}
		doc->EditDocument(0);
	} else
	{
		ListDocData *ldd = new ListDocData(type);
		_Module.GetFrame()->Load(IDD_ORDER_LIST, ldd);
	}
}
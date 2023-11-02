/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Список документов
 * 
 *  ert   14/08/2007   creating
 */ 
#ifndef __ORG_DOCS_H
#define __ORG_DOCS_H

#include <ListForm.h>
#include <NapoleonRes.h>
#include <atlcrack.h>
#include "SumLabel.h"
#include "Contacts.h"

struct OrderDocListItem : public IReflectableData
{
   FILETIME date;
   const wchar_t *flags;
   long sum;

   DECLARE_TYPE_REFLECTION(OrderDocListItem)
};

class DocType; 
class OrderImpl;
struct OrgDocsListData : public ListFormData
{
   OrgDocsListData(const wchar_t *org, const wchar_t* type);
   ~OrgDocsListData() { delete docList; }

   void SetDocType(const wchar_t* type);
   const DocType* GetDocType() const { return docType; }

   virtual DWORD GetSum() const;
   const ROWID& OrgID() const { return orgID; }
   const wchar_t* ID() const { return id.c_str(); }

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;

   virtual const DataReflector& DataType() const;
   virtual int Count() const;
   virtual bool Get(IReflectableData* data, int index) const;
   
   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }

   virtual bool Adding();
   virtual bool Selecting(int index);
   virtual bool Removing(int index);
   virtual bool Editing(int index) { return Selecting(index); }

   virtual void GetTitle(const Org &org, std::wstring *title);

   IDocument* CopyDoc(int index);

   bool CanCreateDocument() const { return docType->IsCreatable(); }

   virtual const wchar_t* DocOrderField(const wchar_t* type) const { return L"date"; }

protected:
   ROWID orgID;
   std::wstring id;
   const wchar_t* svDocType;

   DocumentList *docList;

   const DocType *docType;

   mutable std::wstring docNumber;
};

class OrgDocsList : public ListForm
{
public:
   OrgDocsList();

   virtual bool SetData(IFormData *_data) { return SetDataEx(_data, 1); }

   DECLARE_FORM(OrgDocsList, IDD_ORG_DOCS)

   BEGIN_MSG_MAP(OrgDocsList)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
#ifdef ORG_TASK
      COMMAND_ID_HANDLER(IDC_WARNING, Task)
#endif
#ifdef ORG_NOTE
      COMMAND_ID_HANDLER(IDC_NOTES, Notes)
#endif
      MSG_WM_PAINT(Paint)
      COMMAND_ID_HANDLER(IDC_COPY, Copying)
      MSG_WM_LBUTTONDOWN(MouseDown)
      NOTIFY_CODE_HANDLER(TBN_ENDDRAG, SetViewType)
      NOTIFY_CODE_HANDLER_EX(NM_CLICK, ItemSelected)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   virtual void LoadMenuBar(bool hideSIP);

   virtual DWORD GetResourceID() const { return IDD_ORG_DOCS; }
   virtual DWORD GetMenuID() const;

   virtual void SetViewType(const DocType *newDT);

protected:
   bool SetDataEx(IFormData *_data, int scale);

   LRESULT SetViewType(int id, LPNMHDR header, BOOL &handled);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
   LRESULT Copying(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);

#ifdef ORG_TASK
   LRESULT Task(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
#endif

#ifdef ORG_NOTE
   LRESULT Notes(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
#endif

   virtual LRESULT SetCellInfo(LPNMHDR hdr);
   void Paint(HDC dc);
   void MouseDown(UINT flags, const CPoint &pt);
   virtual int ImageListID(ListViewMultiLine *list) const;
   LRESULT ItemSelected(LPNMHDR hdr);

   virtual BOOL EnableAddButton() const;

   virtual void Refresh();
   virtual void UpdateLayout(bool forceRecalc);

protected:
   OrgInfo orgInfo;
};
//
// выбор типа документа из меню
//
const DocType* SelectDocType(CMenuBarCtrl *menuBar, HWND hWnd, bool creatable = false);

#endif

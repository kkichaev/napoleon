/*
 * Copyright (C), 2006-2010, Денис Мосягин
 *
 * Посещения
 *
 *  ert   08/12/2008   creating
 *  ert   23/06/2009   update
 *  ert   19/05/2010   update
 */
#ifndef __VISIT_DELC_H
#define __VISIT_DELC_H

#include "ObjImpl.h"
#include "DocImpl.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>
#include <atlmisc.h>
#include <atlscrl.h>
#include <MainFrame.h>
#include <ListForm.h>
#include <PicWindow.h>

struct VisitItem : public IReflectableData
{
   wchar_t *id;

   DECLARE_TYPE_REFLECTION(VisitItem)
};

struct Visit : public IReflectableData
{
   FILETIME created;
   FILETIME date;
   wchar_t *id;
   wchar_t *remark;
   DWORD flags;

#ifdef VISIT_CAUSE
   wchar_t *cause;
#endif

#ifdef Agama
   DWORD    unitCode;
#endif

   vector_t<VisitItem> items;

#ifdef GPS_POS
   int   latitude;
   int   longitude;
#endif

   DECLARE_TYPE_REFLECTION(Visit)
};

class VisitImpl : public DBImpl<Visit>, public IDocument, public ICreatableDocument
{
public:
// ------------------------------- DBImpl -----------------------------------

   VisitImpl() : DBImpl(L"visits") {}

   virtual const wchar_t*  KeyFields() const { return L"id,date"; }
   virtual const wchar_t** Indexes() const { static const wchar_t *index[] = { NULL }; return index; }
   
// ------------------------------- IDocument functions -----------------------------------
   virtual const wchar_t* ID() const { return id; }
   virtual const FILETIME& Date() const { return date; }
   virtual const wchar_t* Description() const;

   virtual IReflectableData* Data() { return this; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // return null if is not creatable
   virtual ICreatableDocument* Creatable() { return (ICreatableDocument*)this; }
   
   virtual DWORD Sum() const { return 0; }
   virtual const ROWID& RowID() const { return rid; }

// ------------------------------- ICreatableDocument functions -----------------------------------

   virtual IDocument* Copy() { return NULL; }

   virtual bool CreateDocument(const ROWID &orgID);
   virtual bool Init(const ROWID &orgID);
   virtual bool CanRemove() const;
   virtual bool RemoveDocument();
   virtual bool WriteDocument() { return Write(); }
   virtual void EditDocument(UINT retForm);


   virtual const ROWID& Serialize(StreamWriter* writer) const
   { 
      GetType().Serialize(writer, *this);
      return rid;
   }

   //virtual const char* CMD() const { return SND_VISIT; }
   //virtual const wchar_t* SendText(int count) const { return L"Передача посещений"; }

   virtual bool IsDirty() const { return (flags & ofExported) == 0; }
   virtual bool ClearDirty(SQLTable *updateTable, bool reverse);

   virtual const FILETIME& UID() const { return date; }

   // ------------------------------- Instance functions -----------------------------------
   
};

struct VisitData : public IFormData
{
   VisitData(VisitImpl *visit, bool rdl) : retToDocList(rdl) { this->visit = visit; }
   ~VisitData() { delete visit; }

   VisitImpl *visit;
   bool retToDocList;
};


class VisitForm : public BaseForm
{
public:
   VisitForm() : data(NULL), picWindow(NULL) {}
   ~VisitForm();

   virtual DWORD GetResourceID() const { return IDD_VISIT; }
   virtual DWORD GetMenuBarID() const { return IDD_VISIT; }

   virtual bool SetData(IFormData *_data);
   virtual void UpdateLayout(bool forceRecalc);

   virtual void WriteChanges();

   DECLARE_FORM(VisitForm, IDD_VISIT)

   BEGIN_MSG_MAP(VisitForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_PHOTO, MakePhoto)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
      COMMAND_ID_HANDLER(IDC_EDIT, ShowPicture)
      COMMAND_ID_HANDLER(IDC_DEL, RemovePicture)
      MESSAGE_HANDLER(WM_COMMAND, OnCommand)
      MSG_WM_CONTEXTMENU(ShowContextMenu)
   END_MSG_MAP()

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT MakePhoto(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ShowPicture(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT RemovePicture(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ShowContextMenu(HWND hWnd, const CPoint &org);

   LRESULT OnCommand(WORD msg, WPARAM, LPARAM, BOOL &bHandled)
   {
      if( picWindow )
      {
         picWindow->Cancel();
         delete picWindow;
         picWindow = NULL;
      } else
         bHandled = FALSE;
      return 0;
   }

protected:
   virtual void UpdateData();
   void RefreshPhoto();
   void AddPhoto(const wchar_t* file);
   void RemovePhoto(int index);

protected:
   VisitData *data;
   CMenuBarCtrl menuBar;
   CImageList images;
   CListViewCtrl photos;
   PicWindow *picWindow;
};

extern wchar_t dtVisit[];
void OpenVisit(VisitImpl *visit, bool retToDocList);


#endif
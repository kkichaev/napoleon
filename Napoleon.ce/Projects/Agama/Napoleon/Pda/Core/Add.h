/*
 * Copyright (C), 2007 - 2011, Денис Мосягин
 *
 * Дополнения Агама
 *
 *  ert   01/06/2011   creating
 */
#ifndef __ADD_AGAMA_H
#define __ADD_AGAMA_H

#include "SAnchor.h"
#include <SearchCtrl.h>

#include <Visit.h>

class AddressList : public CWindowImpl<CListViewCtrl, CListViewCtrl>
{
public:
   struct ISetData
   {
      virtual bool SetData(NMLVDISPINFO* info) = 0;
   };

   AddressList() : handler(NULL), dataHandler(NULL) { }

   DECLARE_WND_CLASS(L"ADDR_LIST")

   BEGIN_MSG_MAP(AddressList)
      MESSAGE_HANDLER(OCM_DRAWITEM, DrawItem)
      MESSAGE_HANDLER(OCM_NOTIFY, OnNotify)
   END_MSG_MAP()

   void Init();
   void UpdateLayout(int top = 0);

   LRESULT DrawItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/);
   LRESULT OnNotify(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/);

   void SetHandler(StaticAnchor::IClickHandler* h) { handler = h; }
   void ClearHandler() { handler = NULL; }

   void SetDataHandler(ISetData* h) { dataHandler = h; }
   void ClearDataHandler() { dataHandler = NULL; }

   void Refresh(DWORD count);

protected:
   StaticAnchor::IClickHandler *handler;
   ISetData* dataHandler;
};

class UnitList : public StaticAnchor::IClickHandler, public AddressList::ISetData, public SearchControl::ISearchEvent
{
public:
   UnitList();

   bool Init(CWindow owner, UINT addrListID, UINT unitLabelID, UINT unitTextID, const Org& org, int code);

   void UpdateLayout(WORD wdh, WORD hgh);
   int GetSelectedItemCode();

protected:
   int selCode;

   CWindow owner;
   AddressList addres;
   StaticAnchor unitLabel;
   CStatic unitText;

   SearchControl search;
   bool inSearch;

   struct UnitData
   {
      std::wstring text;
      std::wstring textLower;
      DWORD id;
   };

   typedef std::vector<UnitData> UnitArray;
   typedef std::vector<WORD> SearchArray;

   UnitArray units;
   SearchArray searching;

   virtual void Click(void* source);
   virtual bool SetData(NMLVDISPINFO* info);

   virtual void SearchClear(); // нажали на кнопку новый
   virtual void SearchDo(const wchar_t *text); 
};

struct DisplayItem : public IReflectableData
{
   wchar_t* id;
   wchar_t* folder;

   DECLARE_TYPE_REFLECTION(DisplayItem)
};

// выкладка товара
struct Display : public IReflectableData
{
   FILETIME date;
   wchar_t *id;
   DWORD flags;

   DWORD    unitCode;

   vector_t<DisplayItem> items;

#ifdef GPS_POS
   int   latitude;
   int   longitude;
#endif

   DECLARE_TYPE_REFLECTION(Display)
};

class DisplayImpl : public DBImpl<Display>, public IDocument, public ICreatableDocument
{
public:
// ------------------------------- DBImpl -----------------------------------

   DisplayImpl() : DBImpl(L"display") {}
   ~DisplayImpl();

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

   virtual bool IsDirty() const { return (flags & ofExported) == 0; }
   virtual bool ClearDirty(SQLTable *updateTable, bool reverse);

   virtual const FILETIME& UID() const { return date; }

   // ------------------------------- Instance functions -----------------------------------
  
   bool EditDetail();
};

extern wchar_t dtDisplay[];

#endif
/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Add
 *
 *  ert   26/07/2010   creating
 */
#ifndef __ADD_B_H
#define __ADD_B_H

struct IncassItem : public IReflectableData
{
   wchar_t* number;
   FILETIME date;
   DWORD sum;     // SUM_SCALE

   DECLARE_TYPE_REFLECTION(IncassItem)
};

struct Incass : public IReflectableData
{
   wchar_t *id;

   FILETIME date;
   FILETIME created;
   DWORD sum; // SUM_SCALE

   wchar_t *remark;
   DWORD flags;

	FILETIME dovDate;
	wchar_t *dovNumber;

   vector_t<IncassItem> items;

   DECLARE_TYPE_REFLECTION(Incass)
};


class IncassImpl : public DBImpl<Incass>, public IDocument, public ICreatableDocument
{
public:
   IncassImpl() : DBImpl(L"Incass") {}

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
   
   virtual DWORD Sum() const { return sum; }
   virtual const ROWID& RowID() const { return rid; }

// ------------------------------- ICreatableDocument functions -----------------------------------

   virtual IDocument* Copy() { return NULL; }

   virtual void EditDocument(UINT retForm);
   virtual bool CreateDocument(const ROWID &orgID);
   virtual bool Init(const ROWID &orgID);
   virtual bool CanRemove() const;
   virtual bool RemoveDocument() { return Remove(); } 
   virtual bool WriteDocument() { return Write(); }


   virtual const ROWID& Serialize(StreamWriter* writer) const
   { 
      GetType().Serialize(writer, *this);
      return rid;
   }

   //virtual const char* CMD() const { return SND_PROXY; }
   virtual const wchar_t* SendText(int count) const { return L"Передача документов"; }

   virtual bool IsProceeded() const { return (flags & ofProceeded) != 0; }
   virtual bool IsDirty() const { return (flags & ofExported) == 0; }
   virtual bool ClearDirty(SQLTable *updateTable, bool reverse);

   virtual const FILETIME& UID() const { return date; }

   DWORD PaymentSum() const;
   DWORD GetPayment(const PaymentImpl& p) const;

   bool SetPayment(const PaymentImpl& p, DWORD sum, std::wstring* alert); 
};

extern wchar_t dtIncass[];

int SetCurrentWH(OrderImpl* o);

#endif
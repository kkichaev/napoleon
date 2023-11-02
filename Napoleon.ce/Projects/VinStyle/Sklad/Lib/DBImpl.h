/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * реализация работы с БД
 *
 *  ert   12/06/2009   creating
 */ 
#ifndef __NAPOLEON_DB_IMPL_H
#define __NAPOLEON_DB_IMPL_H

#include <SQLTable.h>

struct IDBData
{
   virtual const wchar_t*  Name() const = 0;
   virtual const wchar_t*  KeyFields() const = 0;
   virtual const wchar_t** Indexes() const = 0;
   virtual const DataReflector& Type() const = 0;
};

void UnbindingItem(IReflectableData *data, StringHolder *holder);
void UnbindCollectionMember(IReflectableData *cdata, const MemberType &m, StringHolder *holder);
void ClearMembers(IReflectableData *cdata);
void CopyData(IReflectableData* dest, const IReflectableData& src);

bool SQLCheckTable(const IDBData &dbdata);

template <class Base>
class DBImpl : public Base, public IDBData
{
public:
   DBImpl(const wchar_t *tn) : rid(NO_ROWID), table(tn) { ClearMembers(this); }

   virtual const wchar_t* Name() const { return table.TableName(); }
   virtual const DataReflector& Type() const { return Base::GetType(); }

   bool CreateTable()
   {
      if( SQLTable::IsTableExist(Name()) ) return true;
      if( !table.Create(GetType(), KeyFields()) ) return false;

      const wchar_t **indexes = Indexes();
      if( indexes )
      {
         while( *indexes )
         {
            table.CreateIndex(*indexes);
            indexes++;
         }
      }

      return true;
   }

   bool Read(const ROWID& id, bool checkCache = true, bool unbindString = true)
   {
      if( checkCache && rid == id ) return true;

      if( !table.Read(this, id) ) return false;
      rid = id;
      if( unbindString ) UnbindStrings();
      return true;
   }

   // fill key fields, before
   bool Read(bool unbindString = true)
   {
      rid = table.Read(this, KeyFields());
      if( rid != NO_ROWID && unbindString )
         UnbindStrings();
      return (rid != NO_ROWID);
   }

   bool Remove()
   {
      if( rid == NO_ROWID ) return true;
      if( table.Remove(rid) )
      {
         rid = NO_ROWID;
         return true;
      }
      return false;
   }

   bool RemoveByKey(const wchar_t *key)
   {
      if( table.Remove(*this, key) )
      {
         rid = NO_ROWID;
         return true;
      }
      return false;
   }

   virtual bool Write()
   {
      if( rid == NO_ROWID )
      {
         SQLCheckTable(*this); 

         rid = table.Write(*this);
         return (rid != NO_ROWID);
      }
      return table.Write(*this, rid);
   }

   bool Update(const wchar_t *fields)
   {
      if( rid == NO_ROWID ) return false;
      return table.Update(*this, fields, rid);
   }

   bool Update(const wchar_t *fields, const ROWID &oid)
   {
      return table.Update(*this, fields, oid);
   }

   const ROWID& RID() const { return rid; }
   void ClearCache() { rid = NO_ROWID; }

   void UnbindStrings()
   {
      holder.Clear();
      UnbindingItem(this, &holder);
   }

   StringHolder holder;
   SQLTable table;
   ROWID rid;
};

#endif

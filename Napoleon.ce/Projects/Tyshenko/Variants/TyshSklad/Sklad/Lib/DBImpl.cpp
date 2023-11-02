/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * реализация работы с БД
 *
 *  ert   16/06/2009   creating
 */ 
#include "stdafx.h"
#include "DBImpl.h"
#include <StdFuncs.h>

void UnbindingItem(IReflectableData *data, StringHolder *holder)
{
   const DataReflector &r = data->GetType();
   
   for( int i = r.Count() - 1; i >= 0; i-- )
   {
      const MemberType &m = r.Type(i);

      if( m.type == MemberType::String )
      {
         wchar_t *p = *(wchar_t**)m.GetValue(*data);
         wchar_t *dest = (p == NULL) ? L"" : holder->Add(p);
         m.SetValue(data, &dest);
      } else if( m.type == MemberType::Collection )
         UnbindCollectionMember(data, m, holder);

   }
}

void UnbindCollectionMember(IReflectableData *cdata, const MemberType &m, StringHolder *holder)
{
   IDataCollection* data = (IDataCollection*)m.GetValue(*cdata);
   for( int i = data->Count() - 1; i >= 0; i-- )
   {
      IReflectableData* element = data->GetItem(i);
      if( element != NULL )
         UnbindingItem(element, holder);
   }
}

void ClearMembers(IReflectableData *data)
{
   const DataReflector &r = data->GetType();
   
   __int64 ivalue = 0;
   wchar_t* value = L"";
   for( int i = r.Count() - 1; i >= 0; i-- )
   {
      const MemberType &m = r.Type(i);
      switch( m.type )
      {
      case MemberType::String:
         m.SetValue(data, &value);
         break;
      case MemberType::Parent:
      case MemberType::Collection:
         break;
      default:
         m.SetValue(data, &ivalue);
         break;
      }
   }
}

void CopyData(IReflectableData* dest, const IReflectableData& src)
{
   const DataReflector& srcT = src.GetType();
   const DataReflector& dstT = dest->GetType();

   int i = srcT.Count() - 1;
   while( i >= 0 )
   {
      const MemberType& srcF = srcT.Type(i);

      int destI = dstT.Find(srcF.name);
      if( destI >= 0 )
      {
         const MemberType& dstF = dstT.Type(destI);
         if( dstF.type == srcF.type )
            dstF.SetValue(dest, srcF.GetValue(src));
      }
      i--;
   }
}

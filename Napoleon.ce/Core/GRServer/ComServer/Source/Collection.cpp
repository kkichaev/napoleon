// Collection.cpp: реализация CCollection

#include "stdafx.h"
#include "Collection.h"
#include "Field.h"
#include "ObjCol.h"

// CCollection


STDMETHODIMP CCollection::get_Count(double* pVal)
{
   if( objects.size() > 0 )
   {
      *pVal = (double)objects.size();
      return S_OK;
   }


   *pVal = (double)fields.size();
   return S_OK;
}

STDMETHODIMP CCollection::Get(ULONG index, IDispatch** value)
{
   if( objects.size() > 0 )
   {
      if( index < objects.size() )
      {
         *value = objects.at(index);
         (*value)->AddRef();
         return S_OK;
      }
      return S_FALSE;
   }

   if( index >= fields.size() )
      return S_FALSE;

   HRESULT res = CField::CreateInstance(value);
   if( SUCCEEDED(res) )
      ((CField*)*value)->SetData(fields.at(index), objName, formats);

   return res;
}

void CCollection::FinalRelease()
{
   std::vector<CObjCol*>::iterator i = objects.begin();
   for( ; i != objects.end(); i++ )
      (*i)->Release();
}

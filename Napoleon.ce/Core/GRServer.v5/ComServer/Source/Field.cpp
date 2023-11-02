// Field.cpp: реализация CField

#include "stdafx.h"
#include "Field.h"
#include "Collection.h"

// CField

using namespace GRServer;

STDMETHODIMP CField::get_Name(BSTR* pVal)
{
   *pVal = SysAllocString(data.name.c_str());
   return S_OK;
}

STDMETHODIMP CField::get_Type(double* pVal)
{
   *pVal = (SHORT)data.type;
   return S_OK;
}

STDMETHODIMP CField::get_ChildObject(IDispatch** pVal)
{
   HRESULT hres = S_OK;
   *pVal = NULL;
   if( formats != NULL && data.type == MemberFormat::mtObject )
   {
      std::wstring object(objName);
      object += L"$"; object += data.name.c_str();
      Format* f = formats->GetFormat(object);
      if( f )
      {
         hres = CCollection::CreateInstance(pVal);
         if( hres == S_OK )
            ((CCollection*)*pVal)->SetData(*f, object.c_str(), formats);
      }
   }
   return hres;
}

/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * ServObjDispatch
 *
 * ert   17/09/2010   creating
 */

#include "stdafx.h"
#include "SODispatch.h"

using namespace GRServer;

//const LONG PROP_BASE = 0x8000;
union FieldDispID
{
   DISPID id;
   struct
   {
      short object;
      short index;
   } field;
};

static ServObjDispatch::DispData resultMethods[] =
{
   { L"Размер", (ServObjDispatch::TInvoke)(&ServObjDispatch::Count) },
   { L"УстановитьСтроку", (ServObjDispatch::TInvoke)(&ServObjDispatch::SetRow) },
   { L"Следующий", (ServObjDispatch::TInvoke)(&ServObjDispatch::MoveNext) },
   { L"Новый", (ServObjDispatch::TInvoke)(&ServObjDispatch::NewObject) },
   { L"НоваяСтрока", (ServObjDispatch::TInvoke)(&ServObjDispatch::NewSubItem) },
};

ServObjDispatch::ServObjDispatch() : rc(0), data(NULL)
{
   for( int i=0; i<sizeof(resultMethods)/sizeof(resultMethods[0]); i++ )
      methods.push_back(resultMethods[i]);
}

ServObjDispatch::~ServObjDispatch()
{
   ObjectList::iterator i = objects.begin();
   for( ; i != objects.end(); i++ )
      delete i->object;
}

HRESULT ServObjDispatch::MoveNext(VARIANT *res, const DISPPARAMS& params)
{
   V_VT(res) = VT_I4;
   V_I4(res) = 0;

   if( data != NULL && curObject < (int)data->size() - 1 )
   {
      V_I4(res) = 1;
   
      curObject++;

      Object *o = data->at(curObject);
      ObjectList::iterator i = objects.begin();
      for( ; i != objects.end(); i++ )
      {
         Member& m = o->at(i->fieldIndex);
         i->object->Init(m.object);
      }
   }
   return S_OK;
}

HRESULT ServObjDispatch::SetRow(VARIANT *res, const DISPPARAMS& params)
{
   HRESULT hres = S_FALSE;

   V_VT(res) = VT_I4;
   V_I4(res) = 0;
   int index = 0;
   if( params.cArgs == 1 )
   {
      hres = S_OK;

      const VARIANT &var = params.rgvarg[0];
      if( var.vt == VT_R8 )
         index = (int)var.dblVal;
      else
      {
         CComVariant v;
         if( VariantChangeType(&v, &var, 0, VT_R8) == S_OK )
            index = (int)v.dblVal;
      }

      if( data && index >= 0 && index < (int)data->size() )
      {
         curObject = index;
         V_I4(res) = 1;
      }
   }
   return hres;
}

HRESULT ServObjDispatch::Count(VARIANT *res, const DISPPARAMS& params)
{
   V_VT(res) = VT_I4;
   V_I4(res) = (data) ? data->size() : 0;
   return S_OK;
}

HRESULT ServObjDispatch::NewSubItem(VARIANT *res, const DISPPARAMS& params)
{
   ObjectList::iterator oi = objects.begin();
   if( params.cArgs > 0 )
   {
      bool clear = false;
      VARIANT v(*params.rgvarg);
      if( v.vt != VT_BSTR )
      {
         VariantChangeType(&v, &v, 0, VT_BSTR);
         clear = true;
      }

      for( ; oi != objects.end(); oi++ )
      {
         if( _wcsicmp(oi->name, v.bstrVal) == 0 )
            break;
      }

      if( clear )
         VariantClear(&v);
   }

   if( oi != objects.end() )
      return oi->object->NewObject(res, params);

   return S_FALSE;
}

HRESULT ServObjDispatch::NewObject(VARIANT *res, const DISPPARAMS& params)
{
   curObject = data->size();

   Object* o = data->AddObject();
   ObjectList::iterator i = objects.begin();
   for( ; i != objects.end(); i++ )
   {
      Member& m = o->at(i->fieldIndex);
      m.object = new ServObject(i->object->objFormat);
      i->object->Init(m.object);
   }

   V_VT(res) = VT_DISPATCH;
   V_DISPATCH(res) = this;
   AddRef();

   return S_OK;
}

void ServObjDispatch::Init(Format* format, FormatList* list)
{
   objFormat = format;

   int index = 0;
   Format::const_iterator fi = format->begin();
   for( ; fi != format->end(); fi++, index++ )
   {
      if( fi->type == MemberFormat::mtObject )
      {
         std::wstring fname = format->name + L"$" + fi->name;
         Format *child = list->GetFormat(fname);

         if( child )
         {
            ObjectData od;
            od.fieldIndex = index;
            od.name = fi->name.c_str();
            od.object = new ServObjDispatch();
            od.object->Init(child, list);

            objects.push_back(od);
         }
      }
   }
}

void ServObjDispatch::Init(ServObject* obj)
{
   curObject = -1;
   data = obj;

   ObjectList::iterator i = objects.begin();
   for( ; i != objects.end(); i++ )
      i->object->Init(NULL);
}

ULONG ServObjDispatch::AddRef()
{
   return InterlockedIncrement(&rc);
}

ULONG ServObjDispatch::Release()
{
	ULONG l = InterlockedDecrement(&rc);
	//if (l == 0)
	//	delete this;
	return l;
}

HRESULT ServObjDispatch::QueryInterface(REFIID iid, void ** ppvObject)
{
   if( ppvObject == NULL )
      return E_POINTER;

   if( iid == IID_IUnknown || iid == IID_IDispatch )
   {
      AddRef();
      *ppvObject = this;

      return S_OK;
   }

   return E_NOINTERFACE;
}

HRESULT ServObjDispatch::GetTypeInfoCount(UINT* pctinfo)
{
	if (pctinfo == NULL) 
		return E_POINTER;

	*pctinfo = 0;
	return S_OK;
}

HRESULT ServObjDispatch::GetTypeInfo(UINT itinfo, LCID lcid, ITypeInfo** pptinfo)
{
	return DISP_E_BADINDEX;
}

int ServObjDispatch::FindField(const wchar_t* name) const
{
   int index = -1;
   if( data != 0 )
   {
      index = data->format->FindMember(name);
      if( index >= 0 )
      {
         switch( data->format->at(index).type )
         {
         case MemberFormat::mtDateTime:
         case MemberFormat::mtNumber:
         case MemberFormat::mtString:
         case MemberFormat::mtObject:
            break;
         default:
            index = -1;
            break;
         }
      }
   }

   return index;
}

HRESULT ServObjDispatch::GetIDsOfNames(REFIID riid, LPOLESTR* rgszNames, UINT cNames, LCID lcid, DISPID* rgdispid)
{
   HRESULT res = DISP_E_UNKNOWNNAME;

   int i = 0;
   while( i < (int)cNames )
      rgdispid[i++] = DISPID_UNKNOWN;

   std::vector<DispData>::const_iterator ri = methods.begin();
   for( i=0; ri != methods.end(); ri++, i++ )
   {
      if( _wcsicmp(ri->name, *rgszNames) == 0 )
      {
         *rgdispid = i+1;
         return  S_OK;
      }
   }

   FieldDispID fdi;
   fdi.field.object = 0;
   i = FindField(*rgszNames);
   if( i >= 0 )
   {
      fdi.field.index = (WORD)(i);
      //fdi.field.index = (WORD)(i + PROP_BASE);
      res = S_OK;
   } else
   {
      ObjectList::const_iterator oi = objects.begin();

      fdi.field.object++;
      for( ; oi != objects.end(); oi++, fdi.field.object++ )
      {
         if( (i = oi->object->FindField(*rgszNames)) >= 0 )
         {
            fdi.field.index = (WORD)(i);
            //fdi.field.index = (WORD)(i + PROP_BASE);
            res = S_OK;
            break;
         }
      }
   }
   if( res == S_OK )
      *rgdispid = fdi.id;
	return res;
}

HRESULT ServObjDispatch::Invoke(DISPID dispidMember, REFIID riid,
	LCID lcid, WORD wFlags, DISPPARAMS* pdispparams, VARIANT* pvarResult,
	EXCEPINFO* pexcepinfo, UINT* puArgErr)
{
   CComVariant temp;
   if( pvarResult == NULL )
      pvarResult = &temp;

   HRESULT res = DISPATCH_PROPERTYGET;
   if( wFlags & DISPATCH_METHOD )
   {
      if( dispidMember > 0 && dispidMember <= (int)methods.size() )
      {
         DispData& dd = methods.at(dispidMember-1);
         res = (this->*dd.invoke)(pvarResult, *pdispparams);
      }
   } else 
   {
      FieldDispID fdi;
      fdi.id = dispidMember;

      ServObjDispatch* obj = (fdi.field.object == 0) ? this : objects[fdi.field.object-1].object;

      res = ((wFlags & DISPATCH_PROPERTYGET) != 0) ? 
         obj->ToVariant(pvarResult, fdi.field.index) : 
         obj->FromVariant(fdi.field.index, *pdispparams->rgvarg);
   }
   return res;
}

HRESULT ServObjDispatch::ToVariant(VARIANT* res, short index) const
{
   HRESULT hres = S_OK;

   V_VT(res) = VT_EMPTY;
   if( data == NULL || curObject < 0 || curObject >= (int)data->size() )
      return hres;

   const MemberFormat& mf = data->format->at(index);
   const Member& m = data->at(curObject)->at(index);

   switch( mf.type )
   {
   case MemberFormat::mtObject:
   {
      ObjectList::const_iterator i = objects.begin();
      for( ; i != objects.end(); i++ )
      {
         if( i->fieldIndex == index )
         {
            V_VT(res) = VT_DISPATCH;
            V_DISPATCH(res) = i->object;
            i->object->AddRef();
            break;
         }
      }
      break;
   }

   case MemberFormat::mtDateTime:
   {
      if( mf.format.dateFormat == MemberFormat::Stamp || mf.format.dateFormat == MemberFormat::Time)
      {
         wchar_t buf[20];
         SYSTEMTIME st;
         FileTimeToSystemTime(&m.datetime, &st);

         if( mf.format.dateFormat == MemberFormat::Stamp )
            wsprintf(buf, L"%d%02d%02d%02d%02d%02d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
         else
            wsprintf(buf, L"%02d:%02d:%02d", st.wHour, st.wMinute, st.wSecond);

         V_VT(res) = VT_BSTR;
         V_BSTR(res) = SysAllocString(buf);
      } else
      {
         double date = (double)(*(__int64*)&m.datetime);
         date /= 864000000000.0;
         date -= 109205.0;
         V_VT(res) = VT_DATE;
         V_DATE(res) = date;
      }
      break;
   }

   case MemberFormat::mtNumber:
      V_VT(res) = VT_R8;
      V_R8(res) = m.number;
      break;

   case MemberFormat::mtString:
      V_VT(res) = VT_BSTR;
      V_BSTR(res) = SysAllocString(m.str->c_str());
      break;
   }

   return hres;
}

HRESULT ServObjDispatch::FromVariant(short index, const VARIANT& _src)
{
   HRESULT hres = S_OK;

   if( data == NULL || curObject < 0 || curObject >= (int)data->size() )
      return hres;

   const MemberFormat& mf = data->format->at(index);
   Member& m = data->at(curObject)->at(index);

   VARIANT src(_src);
   switch( mf.type )
   {
   case MemberFormat::mtDateTime:
      VariantChangeType(&src, &src, 0, VT_DATE);
      src.date += 109205.0;
      src.date *= 864000000000.0;
      *((__int64*)&m.datetime) = (__int64)src.date;
      break;

   case MemberFormat::mtNumber:
      VariantChangeType(&src, &src, 0, VT_R8);
      m.number = V_R8(&src);
      break;

   case MemberFormat::mtString:
      if( _src.vt == VT_BSTR )
      {
         m.str->assign(_src.bstrVal);
      } else
      {
			std::wstring tbuf;
         VariantToStr(&tbuf, _src);
			m.str->assign(tbuf);
      }
      break;
   }

   return hres;
}

bool GRServer::VariantToStr(std::wstring *dest, const VARIANT& src)
{
   bool res = false;

   if( src.vt == VT_BSTR )
   {
      *dest = src.bstrVal;
      res = true;
   } else if( (src.vt & VT_BYREF) )
   {
      WORD type = (src.vt & ~(VT_BYREF));
      if( type == VT_BSTR )
      {
         *dest = *src.pbstrVal;
         res = true;
      } else if( type == VT_VARIANT )
         res = VariantToStr(dest, *src.pvarVal);
   } else
   {
      VARIANT temp;
      if( VariantChangeType(&temp, &src, 0, VT_BSTR) == S_OK )
      {
         *dest = temp.bstrVal;
         VariantClear(&temp);
         res = true;
      }
   }
   return res;
}

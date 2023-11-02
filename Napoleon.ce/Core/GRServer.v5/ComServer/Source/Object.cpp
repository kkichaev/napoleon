// Object.cpp: реализация CGRObject

#include "stdafx.h"
#include "Object.h"
#include "ObjCol.h"
#include "BinaryField.h"

const LONG PROP_ID_TAG = 0x1000;

// CGRObject

HRESULT CGRObject::ToVariant(int index, VARIANT *res)
{
   const MemberFormat& mf = object->GetFormat().at(index);
   Member& m = object->at(index);

   HRESULT hres = S_OK;
   V_VT(res) = VT_EMPTY;

   switch( mf.type )
   {
   case MemberFormat::mtObject:
   {
      V_VT(res) = VT_DISPATCH;
      if( (hres=CObjCol::CreateInstance(&res->pdispVal)) == S_OK )
      {
         if( m.object == NULL )
         {
            std::wstring fmtname = object->GetFormat().name + L"$" + mf.name;
            const_cast<Member&>(m).object = server->GetObjCreator().Create(fmtname);
         }
         ((CObjCol*)res->pdispVal)->SetObject(server, m.object, false);
      }
      break;
   }

   case MemberFormat::mtBinary:
   {
      V_VT(res) = VT_DISPATCH;
      if( (hres=CBinaryField::CreateInstance(&res->pdispVal)) == S_OK )
         ((CBinaryField*)res->pdispVal)->SetData(&m);
      break;
   }

   case MemberFormat::mtDateTime:
   {
      double date = (double)(*(__int64*)&m.datetime);
      date /= 864000000000.0;
      date -= 109205.0;
      V_VT(res) = VT_DATE;
      V_DATE(res) = date;
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

bool VariantToStr(GRServer::CString *dest, const VARIANT& src)
{
   bool res = false;

   if( src.vt == VT_BSTR )
   {
      std::wstring str(src.bstrVal);
      dest->assign(str);
      res = true;
   } else if( (src.vt & VT_BYREF) )
   {
      WORD type = (src.vt & ~(VT_BYREF));
      if( type == VT_BSTR )
      {
         std::wstring str(*src.pbstrVal);
         dest->assign(str);
         res = true;
      } else if( type == VT_VARIANT )
         res = VariantToStr(dest, *src.pvarVal);
   } else
   {
      VARIANT temp;
      if( VariantChangeType(&temp, &src, 0, VT_BSTR) == S_OK )
      {
         std::wstring str(*temp.pbstrVal);
         dest->assign(str);
         VariantClear(&temp);
         res = true;
      }
   }
   return res;
}

HRESULT CGRObject::FromVariant(int index, const VARIANT& _src)
{
   const MemberFormat& mf = object->GetFormat().at(index);
   Member& m = object->at(index);

   HRESULT hres = S_OK;
   VARIANT src(_src);
   switch( mf.type )
   {
   case MemberFormat::mtDateTime:
      VariantChangeType(&src, &src, 0, VT_DATE);
      src.date += 109205.0;
		src.date *= 86400.0;
		src.date = (DATE)((__int64)(src.date += 0.5));
		src.date *= 10000000.0;
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
         VariantToStr(m.str, _src);
      }
      break;
	//case MemberFormat::mtBinary:
	//	if(_src.vt == VT_DISPATCH)
	//	{
	//		IBinaryField *bf;
	//		if (_src.pdispVal->QueryInterface(IID_IBinaryField, (void**)&bf) == S_OK)
	//		{
	//			IBinary *data = ((CBinaryField*)bf)->data;
	//			if (data != NULL && data->Size() > 0)
	//			{
	//				if (m.binary != NULL)
	//					delete m.binary;
	//				Binary *b = new Binary();
	//				BYTE *pb = b->Alloc(data->Size());
	//				memcpy(pb, data->Bytes(), data->Size());
	//				MemoryBinary *mb = new MemoryBinary(b);
	//				m.binary = mb;
	//			}
	//			bf->Release();
	//		}
	//	}
	//	break;
	}
   return hres;
}

STDMETHODIMP CGRObject::Get(BSTR fieldName, VARIANT* res)
{
   int index = FindField(fieldName);
   if( index < 0 )
      return S_FALSE;

   return ToVariant(index, res);
}

STDMETHODIMP CGRObject::DateToString(BSTR fieldName, VARIANT* res)
{
   wchar_t buf[50];
   *buf = L'\0';

   int index = FindField(fieldName);
   if( index >= 0 )
   {
      const MemberFormat& mf = object->GetFormat().at(index);
      const Member& m = object->at(index);

      if( mf.type == MemberFormat::mtDateTime )
      {
         SYSTEMTIME st;
         FileTimeToSystemTime(&m.datetime, &st);
         wsprintf(buf, L"%d%02d%02d%02d%02d%02d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
      }
   }

   V_VT(res) = VT_BSTR;
   V_BSTR(res) = SysAllocString(buf);
   return S_OK;
}

STDMETHODIMP CGRObject::DateFromString(BSTR fieldName, BSTR dateValue)
{
	int index = FindField(fieldName);
	if (index >= 0)
	{
		const MemberFormat& mf = object->GetFormat().at(index);
		Member& m = object->at(index);

		if (mf.type == MemberFormat::mtDateTime && wcslen(dateValue) == 14)
		{
			SYSTEMTIME st = { 0 };
			swscanf_s(dateValue, L"%04d%02d%02d%02d%02d%02d", &st.wYear, &st.wMonth, &st.wDay, &st.wHour, &st.wMinute, &st.wSecond);
			SystemTimeToFileTime(&st, &m.datetime);
		}
	}

	return S_OK;
}


STDMETHODIMP CGRObject::Set(BSTR fieldName, VARIANT _src)
{
   int index = FindField(fieldName);
   if( index < 0 )
      return S_FALSE;

   return FromVariant(index, _src);
}

int CGRObject::FindField(const wchar_t* name) const
{
   return (object == NULL) ? -1 : object->GetFormat().FindMember(name, true);
}

STDMETHODIMP CGRObject::GetIDsOfNames(REFIID riid, LPOLESTR* rgszNames, UINT cNames, LCID lcid, DISPID* rgdispid)
{
   LPOLESTR* names = (LPOLESTR*)alloca(cNames * sizeof(LPOLESTR));
   for( UINT i = 0; i < cNames; i++ )
      names[i] =(wchar_t*) Aliases::GetAlias(rgszNames[i]);

	HRESULT res = _tih.GetIDsOfNames(riid, names, cNames, lcid, rgdispid);

   // check fields
   if( res == DISP_E_UNKNOWNNAME )
   {
      for( unsigned i=0; i<cNames; i++ )
      {
         if( rgdispid[i] == DISPID_UNKNOWN )
         {
            int index = FindField(names[i]);
            if( index < 0 )
               return res;

            rgdispid[i] = index | PROP_ID_TAG;
         }
      }
   }
   return S_OK;
}

STDMETHODIMP CGRObject::Invoke(DISPID dispidMember, REFIID riid, LCID lcid, WORD wFlags, 
                               DISPPARAMS* pdispparams, VARIANT* pvarResult,
                               EXCEPINFO* pexcepinfo, UINT* puArgErr)
{
   HRESULT hres = S_FALSE;
   if( (wFlags & DISPATCH_PROPERTYGET) != 0 || (wFlags & DISPATCH_PROPERTYPUT) != 0 )
   {
      if( dispidMember & PROP_ID_TAG )
      {
         int index = (dispidMember ^ PROP_ID_TAG);

         if( wFlags & DISPATCH_PROPERTYPUT )
         {
            if( pdispparams->cArgs == 1 )
               hres = FromVariant(index, *pdispparams->rgvarg);
         } else
         {
            hres = ToVariant(index, pvarResult);
         }
         return hres;
      }
   }

   return _tih.Invoke((IDispatch*)this, dispidMember, riid, lcid,	wFlags, pdispparams, pvarResult, pexcepinfo, puArgErr);
}

STDMETHODIMP CGRObject::Delete(void)
{
   if( owner )
      owner->NeedDeleted(object);
   return S_OK;
}

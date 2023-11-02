/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * ServObjDispatch
 *
 * ert   17/09/2010   creating
 */

#ifndef __SERV_OBJ_DISPATCH_H
#define __SERV_OBJ_DISPATCH_H

#include "Napoleon1C.h"

namespace GRServer {

const int LastMethod = 1;
//
// все подчиненные объекты удаляются в деструкторе. По этому Relase никогда не удаляет объект, даже если ссылка на него равна 0
// связанный объект (data) не удаляется.
//
class ServObjDispatch : public IDispatch
{
public:
   typedef HRESULT (GRServer::ServObjDispatch::* TInvoke)(VARIANT *,const DISPPARAMS &);
   struct DispData
   {
      wchar_t* name;
      TInvoke invoke;
   };

   ServObjDispatch();
   ~ServObjDispatch();

   void Init(Format* format, FormatList* list);
   void Init(ServObject* obj);
   int  FindField(const wchar_t* name) const;

	STDMETHOD_(ULONG, AddRef)();
	STDMETHOD_(ULONG, Release)();
	STDMETHOD(QueryInterface)(REFIID iid, void ** ppvObject);

	STDMETHOD(GetTypeInfoCount)(UINT* pctinfo);
	STDMETHOD(GetTypeInfo)(UINT itinfo, LCID lcid, ITypeInfo** pptinfo);
	STDMETHOD(GetIDsOfNames)(REFIID riid, LPOLESTR* rgszNames, UINT cNames, LCID lcid, DISPID* rgdispid);
	STDMETHOD(Invoke)(DISPID dispidMember, REFIID riid,
		LCID lcid, WORD wFlags, DISPPARAMS* pdispparams, VARIANT* pvarResult,
		EXCEPINFO* pexcepinfo, UINT* puArgErr);

   HRESULT MoveNext(VARIANT *res, const DISPPARAMS& params);
   HRESULT SetRow(VARIANT *res, const DISPPARAMS& params);
   HRESULT Count(VARIANT *res, const DISPPARAMS& params);
   HRESULT NewObject(VARIANT *res, const DISPPARAMS& params);
   HRESULT NewSubItem(VARIANT *res, const DISPPARAMS& params);

protected:
   HRESULT ToVariant(VARIANT* result, short index) const;
   HRESULT FromVariant(short index, const VARIANT& data);


protected:
   LONG rc;

   struct ObjectData
   {
      const wchar_t* name;
      int fieldIndex;

      ServObjDispatch* object;
   };

   typedef std::vector<ObjectData> ObjectList;

   int curObject;
   ObjectList objects;
   ServObject* data;
   Format* objFormat;

   std::vector<DispData> methods;
};

bool VariantToStr(std::wstring *dest, const VARIANT& src);

} // namespace GRServer

#endif

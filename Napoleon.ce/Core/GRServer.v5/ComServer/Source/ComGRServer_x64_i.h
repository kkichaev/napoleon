

/* this ALWAYS GENERATED file contains the definitions for the interfaces */


 /* File created by MIDL compiler version 8.01.0622 */
/* at Tue Jan 19 07:14:07 2038
 */
/* Compiler settings for ComServer/Source/ComGRServer.idl:
    Oicf, W1, Zp8, env=Win64 (32b run), target_arch=AMD64 8.01.0622 
    protocol : dce , ms_ext, c_ext, robust
    error checks: allocation ref bounds_check enum stub_data 
    VC __declspec() decoration level: 
         __declspec(uuid()), __declspec(selectany), __declspec(novtable)
         DECLSPEC_UUID(), MIDL_INTERFACE()
*/
/* @@MIDL_FILE_HEADING(  ) */

#pragma warning( disable: 4049 )  /* more than 64k source lines */


/* verify that the <rpcndr.h> version is high enough to compile this file*/
#ifndef __REQUIRED_RPCNDR_H_VERSION__
#define __REQUIRED_RPCNDR_H_VERSION__ 475
#endif

#include "rpc.h"
#include "rpcndr.h"

#ifndef __RPCNDR_H_VERSION__
#error this stub requires an updated version of <rpcndr.h>
#endif /* __RPCNDR_H_VERSION__ */

#ifndef COM_NO_WINDOWS_H
#include "windows.h"
#include "ole2.h"
#endif /*COM_NO_WINDOWS_H*/

#ifndef __ComGRServer_x64_i_h__
#define __ComGRServer_x64_i_h__

#if defined(_MSC_VER) && (_MSC_VER >= 1020)
#pragma once
#endif

/* Forward Declarations */ 

#ifndef __IServer_FWD_DEFINED__
#define __IServer_FWD_DEFINED__
typedef interface IServer IServer;

#endif 	/* __IServer_FWD_DEFINED__ */


#ifndef __IGRObjCol_FWD_DEFINED__
#define __IGRObjCol_FWD_DEFINED__
typedef interface IGRObjCol IGRObjCol;

#endif 	/* __IGRObjCol_FWD_DEFINED__ */


#ifndef __IGRObject_FWD_DEFINED__
#define __IGRObject_FWD_DEFINED__
typedef interface IGRObject IGRObject;

#endif 	/* __IGRObject_FWD_DEFINED__ */


#ifndef __ICollection_FWD_DEFINED__
#define __ICollection_FWD_DEFINED__
typedef interface ICollection ICollection;

#endif 	/* __ICollection_FWD_DEFINED__ */


#ifndef __IField_FWD_DEFINED__
#define __IField_FWD_DEFINED__
typedef interface IField IField;

#endif 	/* __IField_FWD_DEFINED__ */


#ifndef __IBinaryField_FWD_DEFINED__
#define __IBinaryField_FWD_DEFINED__
typedef interface IBinaryField IBinaryField;

#endif 	/* __IBinaryField_FWD_DEFINED__ */


#ifndef ___IServerEvents_FWD_DEFINED__
#define ___IServerEvents_FWD_DEFINED__
typedef interface _IServerEvents _IServerEvents;

#endif 	/* ___IServerEvents_FWD_DEFINED__ */


#ifndef __Server_FWD_DEFINED__
#define __Server_FWD_DEFINED__

#ifdef __cplusplus
typedef class Server Server;
#else
typedef struct Server Server;
#endif /* __cplusplus */

#endif 	/* __Server_FWD_DEFINED__ */


#ifndef __ObjCol_FWD_DEFINED__
#define __ObjCol_FWD_DEFINED__

#ifdef __cplusplus
typedef class ObjCol ObjCol;
#else
typedef struct ObjCol ObjCol;
#endif /* __cplusplus */

#endif 	/* __ObjCol_FWD_DEFINED__ */


#ifndef __GRObject_FWD_DEFINED__
#define __GRObject_FWD_DEFINED__

#ifdef __cplusplus
typedef class GRObject GRObject;
#else
typedef struct GRObject GRObject;
#endif /* __cplusplus */

#endif 	/* __GRObject_FWD_DEFINED__ */


#ifndef __Collection_FWD_DEFINED__
#define __Collection_FWD_DEFINED__

#ifdef __cplusplus
typedef class Collection Collection;
#else
typedef struct Collection Collection;
#endif /* __cplusplus */

#endif 	/* __Collection_FWD_DEFINED__ */


#ifndef __Field_FWD_DEFINED__
#define __Field_FWD_DEFINED__

#ifdef __cplusplus
typedef class Field Field;
#else
typedef struct Field Field;
#endif /* __cplusplus */

#endif 	/* __Field_FWD_DEFINED__ */


#ifndef __BinaryField_FWD_DEFINED__
#define __BinaryField_FWD_DEFINED__

#ifdef __cplusplus
typedef class BinaryField BinaryField;
#else
typedef struct BinaryField BinaryField;
#endif /* __cplusplus */

#endif 	/* __BinaryField_FWD_DEFINED__ */


/* header files for imported files */
#include "oaidl.h"
#include "ocidl.h"

#ifdef __cplusplus
extern "C"{
#endif 


#ifndef __IServer_INTERFACE_DEFINED__
#define __IServer_INTERFACE_DEFINED__

/* interface IServer */
/* [unique][helpstring][nonextensible][dual][uuid][object] */ 


EXTERN_C const IID IID_IServer;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("88E11081-5A40-4865-B2F7-1E74936E5155")
    IServer : public IDispatch
    {
    public:
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Connect( 
            /* [in] */ BSTR name,
            /* [in] */ USHORT port,
            /* [defaultvalue][in] */ BSTR login = (BSTR)L"",
            /* [defaultvalue][in] */ BSTR password = (BSTR)L"",
            /* [defaultvalue][in] */ BSTR category = (BSTR)L"",
            /* [defaultvalue][retval][out] */ VARIANT_BOOL *result = 0) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Get( 
            /* [in] */ BSTR name,
            /* [in] */ BSTR filter,
            /* [retval][out] */ IDispatch **collection) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Delete( 
            /* [in] */ BSTR name,
            /* [in] */ BSTR filter) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE New( 
            /* [in] */ BSTR name,
            /* [retval][out] */ IDispatch **collection) = 0;
        
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_ErrorMessage( 
            /* [retval][out] */ BSTR *pVal) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Report( 
            /* [in] */ BSTR name,
            /* [defaultvalue][in] */ IDispatch *params,
            /* [retval][out] */ IDispatch **collection) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Write( 
            /* [in] */ IDispatch *objcol) = 0;
        
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_Timeout( 
            /* [retval][out] */ int *pVal) = 0;
        
        virtual /* [helpstring][id][propput] */ HRESULT STDMETHODCALLTYPE put_Timeout( 
            /* [in] */ int iVal) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE EndSession( void) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE ReportStrParam( 
            /* [in] */ BSTR name,
            /* [in] */ BSTR params,
            /* [retval][out] */ IDispatch **collection) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Call( 
            /* [in] */ BSTR name,
            /* [defaultvalue][in] */ IDispatch *params,
            /* [retval][out] */ IDispatch **collection) = 0;
        
    };
    
    
#else 	/* C style interface */

    typedef struct IServerVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IServer * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            _COM_Outptr_  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IServer * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IServer * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IServer * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IServer * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IServer * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IServer * This,
            /* [annotation][in] */ 
            _In_  DISPID dispIdMember,
            /* [annotation][in] */ 
            _In_  REFIID riid,
            /* [annotation][in] */ 
            _In_  LCID lcid,
            /* [annotation][in] */ 
            _In_  WORD wFlags,
            /* [annotation][out][in] */ 
            _In_  DISPPARAMS *pDispParams,
            /* [annotation][out] */ 
            _Out_opt_  VARIANT *pVarResult,
            /* [annotation][out] */ 
            _Out_opt_  EXCEPINFO *pExcepInfo,
            /* [annotation][out] */ 
            _Out_opt_  UINT *puArgErr);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Connect )( 
            IServer * This,
            /* [in] */ BSTR name,
            /* [in] */ USHORT port,
            /* [defaultvalue][in] */ BSTR login,
            /* [defaultvalue][in] */ BSTR password,
            /* [defaultvalue][in] */ BSTR category,
            /* [defaultvalue][retval][out] */ VARIANT_BOOL *result);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Get )( 
            IServer * This,
            /* [in] */ BSTR name,
            /* [in] */ BSTR filter,
            /* [retval][out] */ IDispatch **collection);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Delete )( 
            IServer * This,
            /* [in] */ BSTR name,
            /* [in] */ BSTR filter);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *New )( 
            IServer * This,
            /* [in] */ BSTR name,
            /* [retval][out] */ IDispatch **collection);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_ErrorMessage )( 
            IServer * This,
            /* [retval][out] */ BSTR *pVal);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Report )( 
            IServer * This,
            /* [in] */ BSTR name,
            /* [defaultvalue][in] */ IDispatch *params,
            /* [retval][out] */ IDispatch **collection);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Write )( 
            IServer * This,
            /* [in] */ IDispatch *objcol);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_Timeout )( 
            IServer * This,
            /* [retval][out] */ int *pVal);
        
        /* [helpstring][id][propput] */ HRESULT ( STDMETHODCALLTYPE *put_Timeout )( 
            IServer * This,
            /* [in] */ int iVal);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *EndSession )( 
            IServer * This);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *ReportStrParam )( 
            IServer * This,
            /* [in] */ BSTR name,
            /* [in] */ BSTR params,
            /* [retval][out] */ IDispatch **collection);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Call )( 
            IServer * This,
            /* [in] */ BSTR name,
            /* [defaultvalue][in] */ IDispatch *params,
            /* [retval][out] */ IDispatch **collection);
        
        END_INTERFACE
    } IServerVtbl;

    interface IServer
    {
        CONST_VTBL struct IServerVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IServer_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IServer_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IServer_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IServer_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IServer_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IServer_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IServer_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IServer_Connect(This,name,port,login,password,category,result)	\
    ( (This)->lpVtbl -> Connect(This,name,port,login,password,category,result) ) 

#define IServer_Get(This,name,filter,collection)	\
    ( (This)->lpVtbl -> Get(This,name,filter,collection) ) 

#define IServer_Delete(This,name,filter)	\
    ( (This)->lpVtbl -> Delete(This,name,filter) ) 

#define IServer_New(This,name,collection)	\
    ( (This)->lpVtbl -> New(This,name,collection) ) 

#define IServer_get_ErrorMessage(This,pVal)	\
    ( (This)->lpVtbl -> get_ErrorMessage(This,pVal) ) 

#define IServer_Report(This,name,params,collection)	\
    ( (This)->lpVtbl -> Report(This,name,params,collection) ) 

#define IServer_Write(This,objcol)	\
    ( (This)->lpVtbl -> Write(This,objcol) ) 

#define IServer_get_Timeout(This,pVal)	\
    ( (This)->lpVtbl -> get_Timeout(This,pVal) ) 

#define IServer_put_Timeout(This,iVal)	\
    ( (This)->lpVtbl -> put_Timeout(This,iVal) ) 

#define IServer_EndSession(This)	\
    ( (This)->lpVtbl -> EndSession(This) ) 

#define IServer_ReportStrParam(This,name,params,collection)	\
    ( (This)->lpVtbl -> ReportStrParam(This,name,params,collection) ) 

#define IServer_Call(This,name,params,collection)	\
    ( (This)->lpVtbl -> Call(This,name,params,collection) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IServer_INTERFACE_DEFINED__ */


#ifndef __IGRObjCol_INTERFACE_DEFINED__
#define __IGRObjCol_INTERFACE_DEFINED__

/* interface IGRObjCol */
/* [unique][helpstring][nonextensible][dual][uuid][object] */ 


EXTERN_C const IID IID_IGRObjCol;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("F664D4F1-B7F8-45A0-851B-B6C4B704B029")
    IGRObjCol : public IDispatch
    {
    public:
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Get( 
            /* [in] */ LONG index,
            /* [retval][out] */ IDispatch **object) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE New( 
            /* [retval][out] */ IDispatch **object) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Write( 
            /* [defaultvalue][in] */ BSTR userid = (BSTR)L"") = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Replace( 
            /* [in] */ BSTR userid) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Delete( 
            /* [defaultvalue][in] */ BSTR userid = (BSTR)L"") = 0;
        
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_Count( 
            /* [retval][out] */ double *pVal) = 0;
        
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_Fields( 
            /* [retval][out] */ IDispatch **pVal) = 0;
        
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_KeyFields( 
            /* [retval][out] */ IDispatch **pVal) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE RemoveObject( 
            /* [in] */ ULONG index) = 0;
        
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_Type( 
            /* [retval][out] */ BSTR *pVal) = 0;
        
        virtual /* [helpstring][id][propput] */ HRESULT STDMETHODCALLTYPE put_Type( 
            /* [in] */ BSTR pVal) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE WriteDirect( void) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE ReplaceDirect( 
            /* [defaultvalue][in] */ BSTR where = (BSTR)L"") = 0;
        
    };
    
    
#else 	/* C style interface */

    typedef struct IGRObjColVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IGRObjCol * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            _COM_Outptr_  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IGRObjCol * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IGRObjCol * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IGRObjCol * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IGRObjCol * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IGRObjCol * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IGRObjCol * This,
            /* [annotation][in] */ 
            _In_  DISPID dispIdMember,
            /* [annotation][in] */ 
            _In_  REFIID riid,
            /* [annotation][in] */ 
            _In_  LCID lcid,
            /* [annotation][in] */ 
            _In_  WORD wFlags,
            /* [annotation][out][in] */ 
            _In_  DISPPARAMS *pDispParams,
            /* [annotation][out] */ 
            _Out_opt_  VARIANT *pVarResult,
            /* [annotation][out] */ 
            _Out_opt_  EXCEPINFO *pExcepInfo,
            /* [annotation][out] */ 
            _Out_opt_  UINT *puArgErr);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Get )( 
            IGRObjCol * This,
            /* [in] */ LONG index,
            /* [retval][out] */ IDispatch **object);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *New )( 
            IGRObjCol * This,
            /* [retval][out] */ IDispatch **object);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Write )( 
            IGRObjCol * This,
            /* [defaultvalue][in] */ BSTR userid);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Replace )( 
            IGRObjCol * This,
            /* [in] */ BSTR userid);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Delete )( 
            IGRObjCol * This,
            /* [defaultvalue][in] */ BSTR userid);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_Count )( 
            IGRObjCol * This,
            /* [retval][out] */ double *pVal);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_Fields )( 
            IGRObjCol * This,
            /* [retval][out] */ IDispatch **pVal);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_KeyFields )( 
            IGRObjCol * This,
            /* [retval][out] */ IDispatch **pVal);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *RemoveObject )( 
            IGRObjCol * This,
            /* [in] */ ULONG index);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_Type )( 
            IGRObjCol * This,
            /* [retval][out] */ BSTR *pVal);
        
        /* [helpstring][id][propput] */ HRESULT ( STDMETHODCALLTYPE *put_Type )( 
            IGRObjCol * This,
            /* [in] */ BSTR pVal);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *WriteDirect )( 
            IGRObjCol * This);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *ReplaceDirect )( 
            IGRObjCol * This,
            /* [defaultvalue][in] */ BSTR where);
        
        END_INTERFACE
    } IGRObjColVtbl;

    interface IGRObjCol
    {
        CONST_VTBL struct IGRObjColVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IGRObjCol_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IGRObjCol_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IGRObjCol_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IGRObjCol_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IGRObjCol_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IGRObjCol_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IGRObjCol_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IGRObjCol_Get(This,index,object)	\
    ( (This)->lpVtbl -> Get(This,index,object) ) 

#define IGRObjCol_New(This,object)	\
    ( (This)->lpVtbl -> New(This,object) ) 

#define IGRObjCol_Write(This,userid)	\
    ( (This)->lpVtbl -> Write(This,userid) ) 

#define IGRObjCol_Replace(This,userid)	\
    ( (This)->lpVtbl -> Replace(This,userid) ) 

#define IGRObjCol_Delete(This,userid)	\
    ( (This)->lpVtbl -> Delete(This,userid) ) 

#define IGRObjCol_get_Count(This,pVal)	\
    ( (This)->lpVtbl -> get_Count(This,pVal) ) 

#define IGRObjCol_get_Fields(This,pVal)	\
    ( (This)->lpVtbl -> get_Fields(This,pVal) ) 

#define IGRObjCol_get_KeyFields(This,pVal)	\
    ( (This)->lpVtbl -> get_KeyFields(This,pVal) ) 

#define IGRObjCol_RemoveObject(This,index)	\
    ( (This)->lpVtbl -> RemoveObject(This,index) ) 

#define IGRObjCol_get_Type(This,pVal)	\
    ( (This)->lpVtbl -> get_Type(This,pVal) ) 

#define IGRObjCol_put_Type(This,pVal)	\
    ( (This)->lpVtbl -> put_Type(This,pVal) ) 

#define IGRObjCol_WriteDirect(This)	\
    ( (This)->lpVtbl -> WriteDirect(This) ) 

#define IGRObjCol_ReplaceDirect(This,where)	\
    ( (This)->lpVtbl -> ReplaceDirect(This,where) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IGRObjCol_INTERFACE_DEFINED__ */


#ifndef __IGRObject_INTERFACE_DEFINED__
#define __IGRObject_INTERFACE_DEFINED__

/* interface IGRObject */
/* [unique][helpstring][nonextensible][dual][uuid][object] */ 


EXTERN_C const IID IID_IGRObject;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("8A4455B1-BA69-40D8-A09E-28D0259EE3CC")
    IGRObject : public IDispatch
    {
    public:
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Get( 
            /* [in] */ BSTR fieldName,
            /* [retval][out] */ VARIANT *value) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Set( 
            /* [in] */ BSTR fieldName,
            /* [in] */ VARIANT value) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Delete( void) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE DateToString( 
            /* [in] */ BSTR fieldName,
            /* [retval][out] */ VARIANT *value) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE DateFromString( 
            /* [in] */ BSTR fieldName,
            /* [in] */ BSTR dateValue) = 0;
        
    };
    
    
#else 	/* C style interface */

    typedef struct IGRObjectVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IGRObject * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            _COM_Outptr_  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IGRObject * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IGRObject * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IGRObject * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IGRObject * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IGRObject * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IGRObject * This,
            /* [annotation][in] */ 
            _In_  DISPID dispIdMember,
            /* [annotation][in] */ 
            _In_  REFIID riid,
            /* [annotation][in] */ 
            _In_  LCID lcid,
            /* [annotation][in] */ 
            _In_  WORD wFlags,
            /* [annotation][out][in] */ 
            _In_  DISPPARAMS *pDispParams,
            /* [annotation][out] */ 
            _Out_opt_  VARIANT *pVarResult,
            /* [annotation][out] */ 
            _Out_opt_  EXCEPINFO *pExcepInfo,
            /* [annotation][out] */ 
            _Out_opt_  UINT *puArgErr);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Get )( 
            IGRObject * This,
            /* [in] */ BSTR fieldName,
            /* [retval][out] */ VARIANT *value);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Set )( 
            IGRObject * This,
            /* [in] */ BSTR fieldName,
            /* [in] */ VARIANT value);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Delete )( 
            IGRObject * This);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *DateToString )( 
            IGRObject * This,
            /* [in] */ BSTR fieldName,
            /* [retval][out] */ VARIANT *value);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *DateFromString )( 
            IGRObject * This,
            /* [in] */ BSTR fieldName,
            /* [in] */ BSTR dateValue);
        
        END_INTERFACE
    } IGRObjectVtbl;

    interface IGRObject
    {
        CONST_VTBL struct IGRObjectVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IGRObject_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IGRObject_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IGRObject_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IGRObject_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IGRObject_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IGRObject_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IGRObject_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IGRObject_Get(This,fieldName,value)	\
    ( (This)->lpVtbl -> Get(This,fieldName,value) ) 

#define IGRObject_Set(This,fieldName,value)	\
    ( (This)->lpVtbl -> Set(This,fieldName,value) ) 

#define IGRObject_Delete(This)	\
    ( (This)->lpVtbl -> Delete(This) ) 

#define IGRObject_DateToString(This,fieldName,value)	\
    ( (This)->lpVtbl -> DateToString(This,fieldName,value) ) 

#define IGRObject_DateFromString(This,fieldName,dateValue)	\
    ( (This)->lpVtbl -> DateFromString(This,fieldName,dateValue) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IGRObject_INTERFACE_DEFINED__ */


#ifndef __ICollection_INTERFACE_DEFINED__
#define __ICollection_INTERFACE_DEFINED__

/* interface ICollection */
/* [unique][helpstring][nonextensible][dual][uuid][object] */ 


EXTERN_C const IID IID_ICollection;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("E8604670-E799-4BED-8535-F0FAFF6B88CA")
    ICollection : public IDispatch
    {
    public:
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_Count( 
            /* [retval][out] */ double *pVal) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Get( 
            /* [in] */ ULONG index,
            /* [retval][out] */ IDispatch **value) = 0;
        
    };
    
    
#else 	/* C style interface */

    typedef struct ICollectionVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ICollection * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            _COM_Outptr_  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ICollection * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ICollection * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            ICollection * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            ICollection * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            ICollection * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            ICollection * This,
            /* [annotation][in] */ 
            _In_  DISPID dispIdMember,
            /* [annotation][in] */ 
            _In_  REFIID riid,
            /* [annotation][in] */ 
            _In_  LCID lcid,
            /* [annotation][in] */ 
            _In_  WORD wFlags,
            /* [annotation][out][in] */ 
            _In_  DISPPARAMS *pDispParams,
            /* [annotation][out] */ 
            _Out_opt_  VARIANT *pVarResult,
            /* [annotation][out] */ 
            _Out_opt_  EXCEPINFO *pExcepInfo,
            /* [annotation][out] */ 
            _Out_opt_  UINT *puArgErr);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_Count )( 
            ICollection * This,
            /* [retval][out] */ double *pVal);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Get )( 
            ICollection * This,
            /* [in] */ ULONG index,
            /* [retval][out] */ IDispatch **value);
        
        END_INTERFACE
    } ICollectionVtbl;

    interface ICollection
    {
        CONST_VTBL struct ICollectionVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ICollection_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ICollection_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ICollection_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ICollection_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define ICollection_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define ICollection_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define ICollection_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define ICollection_get_Count(This,pVal)	\
    ( (This)->lpVtbl -> get_Count(This,pVal) ) 

#define ICollection_Get(This,index,value)	\
    ( (This)->lpVtbl -> Get(This,index,value) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ICollection_INTERFACE_DEFINED__ */


#ifndef __IField_INTERFACE_DEFINED__
#define __IField_INTERFACE_DEFINED__

/* interface IField */
/* [unique][helpstring][nonextensible][dual][uuid][object] */ 


EXTERN_C const IID IID_IField;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("C13B1B38-90FF-4768-8560-B8513CD04041")
    IField : public IDispatch
    {
    public:
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_Name( 
            /* [retval][out] */ BSTR *pVal) = 0;
        
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_Type( 
            /* [retval][out] */ double *pVal) = 0;
        
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_ChildObject( 
            /* [retval][out] */ IDispatch **pVal) = 0;
        
    };
    
    
#else 	/* C style interface */

    typedef struct IFieldVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IField * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            _COM_Outptr_  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IField * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IField * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IField * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IField * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IField * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IField * This,
            /* [annotation][in] */ 
            _In_  DISPID dispIdMember,
            /* [annotation][in] */ 
            _In_  REFIID riid,
            /* [annotation][in] */ 
            _In_  LCID lcid,
            /* [annotation][in] */ 
            _In_  WORD wFlags,
            /* [annotation][out][in] */ 
            _In_  DISPPARAMS *pDispParams,
            /* [annotation][out] */ 
            _Out_opt_  VARIANT *pVarResult,
            /* [annotation][out] */ 
            _Out_opt_  EXCEPINFO *pExcepInfo,
            /* [annotation][out] */ 
            _Out_opt_  UINT *puArgErr);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_Name )( 
            IField * This,
            /* [retval][out] */ BSTR *pVal);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_Type )( 
            IField * This,
            /* [retval][out] */ double *pVal);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_ChildObject )( 
            IField * This,
            /* [retval][out] */ IDispatch **pVal);
        
        END_INTERFACE
    } IFieldVtbl;

    interface IField
    {
        CONST_VTBL struct IFieldVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IField_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IField_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IField_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IField_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IField_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IField_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IField_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IField_get_Name(This,pVal)	\
    ( (This)->lpVtbl -> get_Name(This,pVal) ) 

#define IField_get_Type(This,pVal)	\
    ( (This)->lpVtbl -> get_Type(This,pVal) ) 

#define IField_get_ChildObject(This,pVal)	\
    ( (This)->lpVtbl -> get_ChildObject(This,pVal) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IField_INTERFACE_DEFINED__ */


#ifndef __IBinaryField_INTERFACE_DEFINED__
#define __IBinaryField_INTERFACE_DEFINED__

/* interface IBinaryField */
/* [unique][helpstring][nonextensible][dual][uuid][object] */ 


EXTERN_C const IID IID_IBinaryField;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("A8BA8CD7-C512-4EA4-8611-33179028AA63")
    IBinaryField : public IDispatch
    {
    public:
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Write( 
            /* [in] */ BSTR name) = 0;
        
        virtual /* [helpstring][id][propget] */ HRESULT STDMETHODCALLTYPE get_Size( 
            /* [retval][out] */ double *pVal) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE Read( 
            /* [in] */ BSTR name) = 0;
        
        virtual /* [helpstring][id] */ HRESULT STDMETHODCALLTYPE SetFrom( 
            /* [in] */ IDispatch *pVal) = 0;
        
    };
    
    
#else 	/* C style interface */

    typedef struct IBinaryFieldVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IBinaryField * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            _COM_Outptr_  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IBinaryField * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IBinaryField * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IBinaryField * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IBinaryField * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IBinaryField * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IBinaryField * This,
            /* [annotation][in] */ 
            _In_  DISPID dispIdMember,
            /* [annotation][in] */ 
            _In_  REFIID riid,
            /* [annotation][in] */ 
            _In_  LCID lcid,
            /* [annotation][in] */ 
            _In_  WORD wFlags,
            /* [annotation][out][in] */ 
            _In_  DISPPARAMS *pDispParams,
            /* [annotation][out] */ 
            _Out_opt_  VARIANT *pVarResult,
            /* [annotation][out] */ 
            _Out_opt_  EXCEPINFO *pExcepInfo,
            /* [annotation][out] */ 
            _Out_opt_  UINT *puArgErr);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Write )( 
            IBinaryField * This,
            /* [in] */ BSTR name);
        
        /* [helpstring][id][propget] */ HRESULT ( STDMETHODCALLTYPE *get_Size )( 
            IBinaryField * This,
            /* [retval][out] */ double *pVal);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *Read )( 
            IBinaryField * This,
            /* [in] */ BSTR name);
        
        /* [helpstring][id] */ HRESULT ( STDMETHODCALLTYPE *SetFrom )( 
            IBinaryField * This,
            /* [in] */ IDispatch *pVal);
        
        END_INTERFACE
    } IBinaryFieldVtbl;

    interface IBinaryField
    {
        CONST_VTBL struct IBinaryFieldVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IBinaryField_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IBinaryField_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IBinaryField_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IBinaryField_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IBinaryField_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IBinaryField_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IBinaryField_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IBinaryField_Write(This,name)	\
    ( (This)->lpVtbl -> Write(This,name) ) 

#define IBinaryField_get_Size(This,pVal)	\
    ( (This)->lpVtbl -> get_Size(This,pVal) ) 

#define IBinaryField_Read(This,name)	\
    ( (This)->lpVtbl -> Read(This,name) ) 

#define IBinaryField_SetFrom(This,pVal)	\
    ( (This)->lpVtbl -> SetFrom(This,pVal) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IBinaryField_INTERFACE_DEFINED__ */



#ifndef __ComGRServerLib_LIBRARY_DEFINED__
#define __ComGRServerLib_LIBRARY_DEFINED__

/* library ComGRServerLib */
/* [helpstring][version][uuid] */ 


EXTERN_C const IID LIBID_ComGRServerLib;

#ifndef ___IServerEvents_DISPINTERFACE_DEFINED__
#define ___IServerEvents_DISPINTERFACE_DEFINED__

/* dispinterface _IServerEvents */
/* [helpstring][uuid] */ 


EXTERN_C const IID DIID__IServerEvents;

#if defined(__cplusplus) && !defined(CINTERFACE)

    MIDL_INTERFACE("A30D75CD-5866-4D6D-81FB-2FF0AAA97A14")
    _IServerEvents : public IDispatch
    {
    };
    
#else 	/* C style interface */

    typedef struct _IServerEventsVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            _IServerEvents * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            _COM_Outptr_  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            _IServerEvents * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            _IServerEvents * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            _IServerEvents * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            _IServerEvents * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            _IServerEvents * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            _IServerEvents * This,
            /* [annotation][in] */ 
            _In_  DISPID dispIdMember,
            /* [annotation][in] */ 
            _In_  REFIID riid,
            /* [annotation][in] */ 
            _In_  LCID lcid,
            /* [annotation][in] */ 
            _In_  WORD wFlags,
            /* [annotation][out][in] */ 
            _In_  DISPPARAMS *pDispParams,
            /* [annotation][out] */ 
            _Out_opt_  VARIANT *pVarResult,
            /* [annotation][out] */ 
            _Out_opt_  EXCEPINFO *pExcepInfo,
            /* [annotation][out] */ 
            _Out_opt_  UINT *puArgErr);
        
        END_INTERFACE
    } _IServerEventsVtbl;

    interface _IServerEvents
    {
        CONST_VTBL struct _IServerEventsVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define _IServerEvents_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define _IServerEvents_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define _IServerEvents_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define _IServerEvents_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define _IServerEvents_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define _IServerEvents_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define _IServerEvents_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */


#endif 	/* ___IServerEvents_DISPINTERFACE_DEFINED__ */


EXTERN_C const CLSID CLSID_Server;

#ifdef __cplusplus

class DECLSPEC_UUID("5DC08783-F6AA-42DA-A677-312193D7E989")
Server;
#endif

EXTERN_C const CLSID CLSID_ObjCol;

#ifdef __cplusplus

class DECLSPEC_UUID("720CD65C-BEE7-491E-8CEE-28154362749A")
ObjCol;
#endif

EXTERN_C const CLSID CLSID_GRObject;

#ifdef __cplusplus

class DECLSPEC_UUID("5123E8DF-FDAB-4916-868E-6898957F9055")
GRObject;
#endif

EXTERN_C const CLSID CLSID_Collection;

#ifdef __cplusplus

class DECLSPEC_UUID("5D6950CD-82DD-475F-B47A-F82577BF924D")
Collection;
#endif

EXTERN_C const CLSID CLSID_Field;

#ifdef __cplusplus

class DECLSPEC_UUID("FABC81BD-994C-4206-A475-704C19774723")
Field;
#endif

EXTERN_C const CLSID CLSID_BinaryField;

#ifdef __cplusplus

class DECLSPEC_UUID("355356AA-413F-46F5-BBE1-3E9526264BC6")
BinaryField;
#endif
#endif /* __ComGRServerLib_LIBRARY_DEFINED__ */

/* Additional Prototypes for ALL interfaces */

unsigned long             __RPC_USER  BSTR_UserSize(     unsigned long *, unsigned long            , BSTR * ); 
unsigned char * __RPC_USER  BSTR_UserMarshal(  unsigned long *, unsigned char *, BSTR * ); 
unsigned char * __RPC_USER  BSTR_UserUnmarshal(unsigned long *, unsigned char *, BSTR * ); 
void                      __RPC_USER  BSTR_UserFree(     unsigned long *, BSTR * ); 

unsigned long             __RPC_USER  VARIANT_UserSize(     unsigned long *, unsigned long            , VARIANT * ); 
unsigned char * __RPC_USER  VARIANT_UserMarshal(  unsigned long *, unsigned char *, VARIANT * ); 
unsigned char * __RPC_USER  VARIANT_UserUnmarshal(unsigned long *, unsigned char *, VARIANT * ); 
void                      __RPC_USER  VARIANT_UserFree(     unsigned long *, VARIANT * ); 

/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif



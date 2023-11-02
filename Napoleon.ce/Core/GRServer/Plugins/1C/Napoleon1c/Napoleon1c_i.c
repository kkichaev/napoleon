

/* this ALWAYS GENERATED file contains the IIDs and CLSIDs */

/* link this file in with the server and any clients */


 /* File created by MIDL compiler version 7.00.0500 */
/* at Tue Mar 14 13:07:39 2017
 */
/* Compiler settings for GRServer/Plugins/1c/Napoleon1c/Napoleon1c.idl:
    Oicf, W1, Zp8, env=Win32 (32b run)
    protocol : dce , ms_ext, c_ext, robust
    error checks: allocation ref bounds_check enum stub_data 
    VC __declspec() decoration level: 
         __declspec(uuid()), __declspec(selectany), __declspec(novtable)
         DECLSPEC_UUID(), MIDL_INTERFACE()
*/
//@@MIDL_FILE_HEADING(  )

#pragma warning( disable: 4049 )  /* more than 64k source lines */


#ifdef __cplusplus
extern "C"{
#endif 


#include <rpc.h>
#include <rpcndr.h>

#ifdef _MIDL_USE_GUIDDEF_

#ifndef INITGUID
#define INITGUID
#include <guiddef.h>
#undef INITGUID
#else
#include <guiddef.h>
#endif

#define MIDL_DEFINE_GUID(type,name,l,w1,w2,b1,b2,b3,b4,b5,b6,b7,b8) \
        DEFINE_GUID(name,l,w1,w2,b1,b2,b3,b4,b5,b6,b7,b8)

#else // !_MIDL_USE_GUIDDEF_

#ifndef __IID_DEFINED__
#define __IID_DEFINED__

typedef struct _IID
{
    unsigned long x;
    unsigned short s1;
    unsigned short s2;
    unsigned char  c[8];
} IID;

#endif // __IID_DEFINED__

#ifndef CLSID_DEFINED
#define CLSID_DEFINED
typedef IID CLSID;
#endif // CLSID_DEFINED

#define MIDL_DEFINE_GUID(type,name,l,w1,w2,b1,b2,b3,b4,b5,b6,b7,b8) \
        const type name = {l,w1,w2,{b1,b2,b3,b4,b5,b6,b7,b8}}

#endif !_MIDL_USE_GUIDDEF_

MIDL_DEFINE_GUID(IID, IID_IInitDone,0xAB634001,0xF13D,0x11d0,0xA4,0x59,0x00,0x40,0x95,0xE1,0xDA,0xEA);


MIDL_DEFINE_GUID(IID, IID_IPropertyProfile,0xAB634002,0xF13D,0x11d0,0xA4,0x59,0x00,0x40,0x95,0xE1,0xDA,0xEA);


MIDL_DEFINE_GUID(IID, IID_IAsyncEvent,0xab634004,0xf13d,0x11d0,0xa4,0x59,0x00,0x40,0x95,0xe1,0xda,0xea);


MIDL_DEFINE_GUID(IID, IID_ILanguageExtender,0xAB634003,0xF13D,0x11d0,0xA4,0x59,0x00,0x40,0x95,0xE1,0xDA,0xEA);


MIDL_DEFINE_GUID(IID, IID_IStatusLine,0xab634005,0xf13d,0x11d0,0xa4,0x59,0x00,0x40,0x95,0xe1,0xda,0xea);


MIDL_DEFINE_GUID(IID, IID_IExtWndsSupport,0xefe19ea0,0x09e4,0x11d2,0xa6,0x01,0x00,0x80,0x48,0xda,0x00,0xde);


MIDL_DEFINE_GUID(IID, LIBID_Napoleon1cLib,0x61462706,0xE54E,0x4F3F,0x99,0x10,0x1D,0x5E,0xF0,0xC8,0x07,0xB8);


MIDL_DEFINE_GUID(CLSID, CLSID_Napoleon1C,0x916E8093,0x6C4E,0x49CD,0x9F,0x0D,0x23,0x3C,0xE0,0x8A,0xAE,0xC6);

#undef MIDL_DEFINE_GUID

#ifdef __cplusplus
}
#endif




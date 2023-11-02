

/* this ALWAYS GENERATED file contains the IIDs and CLSIDs */

/* link this file in with the server and any clients */


 /* File created by MIDL compiler version 8.01.0622 */
/* at Tue Jan 19 07:14:07 2038
 */
/* Compiler settings for ComServer/Source/ComGRServer.idl:
    Oicf, W1, Zp8, env=Win32 (32b run), target_arch=X86 8.01.0622 
    protocol : dce , ms_ext, c_ext, robust
    error checks: allocation ref bounds_check enum stub_data 
    VC __declspec() decoration level: 
         __declspec(uuid()), __declspec(selectany), __declspec(novtable)
         DECLSPEC_UUID(), MIDL_INTERFACE()
*/
/* @@MIDL_FILE_HEADING(  ) */

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
        EXTERN_C __declspec(selectany) const type name = {l,w1,w2,{b1,b2,b3,b4,b5,b6,b7,b8}}

#endif // !_MIDL_USE_GUIDDEF_

MIDL_DEFINE_GUID(IID, IID_IServer,0x88E11081,0x5A40,0x4865,0xB2,0xF7,0x1E,0x74,0x93,0x6E,0x51,0x55);


MIDL_DEFINE_GUID(IID, IID_IGRObjCol,0xF664D4F1,0xB7F8,0x45A0,0x85,0x1B,0xB6,0xC4,0xB7,0x04,0xB0,0x29);


MIDL_DEFINE_GUID(IID, IID_IGRObject,0x8A4455B1,0xBA69,0x40D8,0xA0,0x9E,0x28,0xD0,0x25,0x9E,0xE3,0xCC);


MIDL_DEFINE_GUID(IID, IID_ICollection,0xE8604670,0xE799,0x4BED,0x85,0x35,0xF0,0xFA,0xFF,0x6B,0x88,0xCA);


MIDL_DEFINE_GUID(IID, IID_IField,0xC13B1B38,0x90FF,0x4768,0x85,0x60,0xB8,0x51,0x3C,0xD0,0x40,0x41);


MIDL_DEFINE_GUID(IID, IID_IBinaryField,0xA8BA8CD7,0xC512,0x4EA4,0x86,0x11,0x33,0x17,0x90,0x28,0xAA,0x63);


MIDL_DEFINE_GUID(IID, LIBID_ComGRServerLib,0x31785177,0x6B2B,0x4A0C,0xB4,0x9C,0xFD,0x1F,0x13,0xA7,0xC0,0x0B);


MIDL_DEFINE_GUID(IID, DIID__IServerEvents,0xA30D75CD,0x5866,0x4D6D,0x81,0xFB,0x2F,0xF0,0xAA,0xA9,0x7A,0x14);


MIDL_DEFINE_GUID(CLSID, CLSID_Server,0x5DC08783,0xF6AA,0x42DA,0xA6,0x77,0x31,0x21,0x93,0xD7,0xE9,0x89);


MIDL_DEFINE_GUID(CLSID, CLSID_ObjCol,0x720CD65C,0xBEE7,0x491E,0x8C,0xEE,0x28,0x15,0x43,0x62,0x74,0x9A);


MIDL_DEFINE_GUID(CLSID, CLSID_GRObject,0x5123E8DF,0xFDAB,0x4916,0x86,0x8E,0x68,0x98,0x95,0x7F,0x90,0x55);


MIDL_DEFINE_GUID(CLSID, CLSID_Collection,0x5D6950CD,0x82DD,0x475F,0xB4,0x7A,0xF8,0x25,0x77,0xBF,0x92,0x4D);


MIDL_DEFINE_GUID(CLSID, CLSID_Field,0xFABC81BD,0x994C,0x4206,0xA4,0x75,0x70,0x4C,0x19,0x77,0x47,0x23);


MIDL_DEFINE_GUID(CLSID, CLSID_BinaryField,0x355356AA,0x413F,0x46F5,0xBB,0xE1,0x3E,0x95,0x26,0x26,0x4B,0xC6);

#undef MIDL_DEFINE_GUID

#ifdef __cplusplus
}
#endif




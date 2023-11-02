

/* this ALWAYS GENERATED file contains the proxy stub code */


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

#if defined(_M_AMD64)


#pragma warning( disable: 4049 )  /* more than 64k source lines */
#if _MSC_VER >= 1200
#pragma warning(push)
#endif

#pragma warning( disable: 4211 )  /* redefine extern to static */
#pragma warning( disable: 4232 )  /* dllimport identity*/
#pragma warning( disable: 4024 )  /* array to pointer mapping*/
#pragma warning( disable: 4152 )  /* function/data pointer conversion in expression */
#pragma warning( disable: 4100 ) /* unreferenced arguments in x86 call */

#pragma optimize("", off ) 

#define USE_STUBLESS_PROXY


/* verify that the <rpcproxy.h> version is high enough to compile this file*/
#ifndef __REDQ_RPCPROXY_H_VERSION__
#define __REQUIRED_RPCPROXY_H_VERSION__ 475
#endif


#include "rpcproxy.h"
#ifndef __RPCPROXY_H_VERSION__
#error this stub requires an updated version of <rpcproxy.h>
#endif /* __RPCPROXY_H_VERSION__ */


#include "ComGRServer_x64_i.h"

#define TYPE_FORMAT_STRING_SIZE   1051                              
#define PROC_FORMAT_STRING_SIZE   1477                              
#define EXPR_FORMAT_STRING_SIZE   1                                 
#define TRANSMIT_AS_TABLE_SIZE    0            
#define WIRE_MARSHAL_TABLE_SIZE   2            

typedef struct _ComGRServer_MIDL_TYPE_FORMAT_STRING
    {
    short          Pad;
    unsigned char  Format[ TYPE_FORMAT_STRING_SIZE ];
    } ComGRServer_MIDL_TYPE_FORMAT_STRING;

typedef struct _ComGRServer_MIDL_PROC_FORMAT_STRING
    {
    short          Pad;
    unsigned char  Format[ PROC_FORMAT_STRING_SIZE ];
    } ComGRServer_MIDL_PROC_FORMAT_STRING;

typedef struct _ComGRServer_MIDL_EXPR_FORMAT_STRING
    {
    long          Pad;
    unsigned char  Format[ EXPR_FORMAT_STRING_SIZE ];
    } ComGRServer_MIDL_EXPR_FORMAT_STRING;


static const RPC_SYNTAX_IDENTIFIER  _RpcTransferSyntax = 
{{0x8A885D04,0x1CEB,0x11C9,{0x9F,0xE8,0x08,0x00,0x2B,0x10,0x48,0x60}},{2,0}};


extern const ComGRServer_MIDL_TYPE_FORMAT_STRING ComGRServer__MIDL_TypeFormatString;
extern const ComGRServer_MIDL_PROC_FORMAT_STRING ComGRServer__MIDL_ProcFormatString;
extern const ComGRServer_MIDL_EXPR_FORMAT_STRING ComGRServer__MIDL_ExprFormatString;


extern const MIDL_STUB_DESC Object_StubDesc;


extern const MIDL_SERVER_INFO IServer_ServerInfo;
extern const MIDL_STUBLESS_PROXY_INFO IServer_ProxyInfo;


extern const MIDL_STUB_DESC Object_StubDesc;


extern const MIDL_SERVER_INFO IGRObjCol_ServerInfo;
extern const MIDL_STUBLESS_PROXY_INFO IGRObjCol_ProxyInfo;


extern const MIDL_STUB_DESC Object_StubDesc;


extern const MIDL_SERVER_INFO IGRObject_ServerInfo;
extern const MIDL_STUBLESS_PROXY_INFO IGRObject_ProxyInfo;


extern const MIDL_STUB_DESC Object_StubDesc;


extern const MIDL_SERVER_INFO ICollection_ServerInfo;
extern const MIDL_STUBLESS_PROXY_INFO ICollection_ProxyInfo;


extern const MIDL_STUB_DESC Object_StubDesc;


extern const MIDL_SERVER_INFO IField_ServerInfo;
extern const MIDL_STUBLESS_PROXY_INFO IField_ProxyInfo;


extern const MIDL_STUB_DESC Object_StubDesc;


extern const MIDL_SERVER_INFO IBinaryField_ServerInfo;
extern const MIDL_STUBLESS_PROXY_INFO IBinaryField_ProxyInfo;


extern const USER_MARSHAL_ROUTINE_QUADRUPLE UserMarshalRoutines[ WIRE_MARSHAL_TABLE_SIZE ];

#if !defined(__RPC_WIN64__)
#error  Invalid build platform for this stub.
#endif

#if !(TARGET_IS_NT50_OR_LATER)
#error You need Windows 2000 or later to run this stub because it uses these features:
#error   /robust command line switch.
#error However, your C/C++ compilation flags indicate you intend to run this app on earlier systems.
#error This app will fail with the RPC_X_WRONG_STUB_VERSION error.
#endif


static const ComGRServer_MIDL_PROC_FORMAT_STRING ComGRServer__MIDL_ProcFormatString =
    {
        0,
        {

	/* Procedure Connect */

			0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/*  2 */	NdrFcLong( 0x0 ),	/* 0 */
/*  6 */	NdrFcShort( 0x7 ),	/* 7 */
/*  8 */	NdrFcShort( 0x40 ),	/* x86 Stack size/offset = 64 */
/* 10 */	NdrFcShort( 0x6 ),	/* 6 */
/* 12 */	NdrFcShort( 0x22 ),	/* 34 */
/* 14 */	0x46,		/* Oi2 Flags:  clt must size, has return, has ext, */
			0x7,		/* 7 */
/* 16 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 18 */	NdrFcShort( 0x0 ),	/* 0 */
/* 20 */	NdrFcShort( 0x1 ),	/* 1 */
/* 22 */	NdrFcShort( 0x0 ),	/* 0 */
/* 24 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter name */

/* 26 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 28 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 30 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter port */

/* 32 */	NdrFcShort( 0x48 ),	/* Flags:  in, base type, */
/* 34 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 36 */	0x6,		/* FC_SHORT */
			0x0,		/* 0 */

	/* Parameter login */

/* 38 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 40 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 42 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter password */

/* 44 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 46 */	NdrFcShort( 0x20 ),	/* x86 Stack size/offset = 32 */
/* 48 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter category */

/* 50 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 52 */	NdrFcShort( 0x28 ),	/* x86 Stack size/offset = 40 */
/* 54 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter result */

/* 56 */	NdrFcShort( 0x2150 ),	/* Flags:  out, base type, simple ref, srv alloc size=8 */
/* 58 */	NdrFcShort( 0x30 ),	/* x86 Stack size/offset = 48 */
/* 60 */	0x6,		/* FC_SHORT */
			0x0,		/* 0 */

	/* Return value */

/* 62 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 64 */	NdrFcShort( 0x38 ),	/* x86 Stack size/offset = 56 */
/* 66 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Get */

/* 68 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 70 */	NdrFcLong( 0x0 ),	/* 0 */
/* 74 */	NdrFcShort( 0x8 ),	/* 8 */
/* 76 */	NdrFcShort( 0x28 ),	/* x86 Stack size/offset = 40 */
/* 78 */	NdrFcShort( 0x0 ),	/* 0 */
/* 80 */	NdrFcShort( 0x8 ),	/* 8 */
/* 82 */	0x47,		/* Oi2 Flags:  srv must size, clt must size, has return, has ext, */
			0x4,		/* 4 */
/* 84 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 86 */	NdrFcShort( 0x0 ),	/* 0 */
/* 88 */	NdrFcShort( 0x1 ),	/* 1 */
/* 90 */	NdrFcShort( 0x0 ),	/* 0 */
/* 92 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter name */

/* 94 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 96 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 98 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter filter */

/* 100 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 102 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 104 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter collection */

/* 106 */	NdrFcShort( 0x13 ),	/* Flags:  must size, must free, out, */
/* 108 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 110 */	NdrFcShort( 0x2a ),	/* Type Offset=42 */

	/* Return value */

/* 112 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 114 */	NdrFcShort( 0x20 ),	/* x86 Stack size/offset = 32 */
/* 116 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Delete */

/* 118 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 120 */	NdrFcLong( 0x0 ),	/* 0 */
/* 124 */	NdrFcShort( 0x9 ),	/* 9 */
/* 126 */	NdrFcShort( 0x20 ),	/* x86 Stack size/offset = 32 */
/* 128 */	NdrFcShort( 0x0 ),	/* 0 */
/* 130 */	NdrFcShort( 0x8 ),	/* 8 */
/* 132 */	0x46,		/* Oi2 Flags:  clt must size, has return, has ext, */
			0x3,		/* 3 */
/* 134 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 136 */	NdrFcShort( 0x0 ),	/* 0 */
/* 138 */	NdrFcShort( 0x1 ),	/* 1 */
/* 140 */	NdrFcShort( 0x0 ),	/* 0 */
/* 142 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter name */

/* 144 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 146 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 148 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter filter */

/* 150 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 152 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 154 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Return value */

/* 156 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 158 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 160 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure New */

/* 162 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 164 */	NdrFcLong( 0x0 ),	/* 0 */
/* 168 */	NdrFcShort( 0xa ),	/* 10 */
/* 170 */	NdrFcShort( 0x20 ),	/* x86 Stack size/offset = 32 */
/* 172 */	NdrFcShort( 0x0 ),	/* 0 */
/* 174 */	NdrFcShort( 0x8 ),	/* 8 */
/* 176 */	0x47,		/* Oi2 Flags:  srv must size, clt must size, has return, has ext, */
			0x3,		/* 3 */
/* 178 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 180 */	NdrFcShort( 0x0 ),	/* 0 */
/* 182 */	NdrFcShort( 0x1 ),	/* 1 */
/* 184 */	NdrFcShort( 0x0 ),	/* 0 */
/* 186 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter name */

/* 188 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 190 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 192 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter collection */

/* 194 */	NdrFcShort( 0x13 ),	/* Flags:  must size, must free, out, */
/* 196 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 198 */	NdrFcShort( 0x2a ),	/* Type Offset=42 */

	/* Return value */

/* 200 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 202 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 204 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure get_ErrorMessage */

/* 206 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 208 */	NdrFcLong( 0x0 ),	/* 0 */
/* 212 */	NdrFcShort( 0xb ),	/* 11 */
/* 214 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 216 */	NdrFcShort( 0x0 ),	/* 0 */
/* 218 */	NdrFcShort( 0x8 ),	/* 8 */
/* 220 */	0x45,		/* Oi2 Flags:  srv must size, has return, has ext, */
			0x2,		/* 2 */
/* 222 */	0xa,		/* 10 */
			0x3,		/* Ext Flags:  new corr desc, clt corr check, */
/* 224 */	NdrFcShort( 0x1 ),	/* 1 */
/* 226 */	NdrFcShort( 0x0 ),	/* 0 */
/* 228 */	NdrFcShort( 0x0 ),	/* 0 */
/* 230 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter pVal */

/* 232 */	NdrFcShort( 0x2113 ),	/* Flags:  must size, must free, out, simple ref, srv alloc size=8 */
/* 234 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 236 */	NdrFcShort( 0x48 ),	/* Type Offset=72 */

	/* Return value */

/* 238 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 240 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 242 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Report */

/* 244 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 246 */	NdrFcLong( 0x0 ),	/* 0 */
/* 250 */	NdrFcShort( 0xc ),	/* 12 */
/* 252 */	NdrFcShort( 0x28 ),	/* x86 Stack size/offset = 40 */
/* 254 */	NdrFcShort( 0x0 ),	/* 0 */
/* 256 */	NdrFcShort( 0x8 ),	/* 8 */
/* 258 */	0x47,		/* Oi2 Flags:  srv must size, clt must size, has return, has ext, */
			0x4,		/* 4 */
/* 260 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 262 */	NdrFcShort( 0x0 ),	/* 0 */
/* 264 */	NdrFcShort( 0x1 ),	/* 1 */
/* 266 */	NdrFcShort( 0x0 ),	/* 0 */
/* 268 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter name */

/* 270 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 272 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 274 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter params */

/* 276 */	NdrFcShort( 0xb ),	/* Flags:  must size, must free, in, */
/* 278 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 280 */	NdrFcShort( 0x2e ),	/* Type Offset=46 */

	/* Parameter collection */

/* 282 */	NdrFcShort( 0x13 ),	/* Flags:  must size, must free, out, */
/* 284 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 286 */	NdrFcShort( 0x2a ),	/* Type Offset=42 */

	/* Return value */

/* 288 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 290 */	NdrFcShort( 0x20 ),	/* x86 Stack size/offset = 32 */
/* 292 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Write */

/* 294 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 296 */	NdrFcLong( 0x0 ),	/* 0 */
/* 300 */	NdrFcShort( 0xd ),	/* 13 */
/* 302 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 304 */	NdrFcShort( 0x0 ),	/* 0 */
/* 306 */	NdrFcShort( 0x8 ),	/* 8 */
/* 308 */	0x46,		/* Oi2 Flags:  clt must size, has return, has ext, */
			0x2,		/* 2 */
/* 310 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 312 */	NdrFcShort( 0x0 ),	/* 0 */
/* 314 */	NdrFcShort( 0x0 ),	/* 0 */
/* 316 */	NdrFcShort( 0x0 ),	/* 0 */
/* 318 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter objcol */

/* 320 */	NdrFcShort( 0xb ),	/* Flags:  must size, must free, in, */
/* 322 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 324 */	NdrFcShort( 0x2e ),	/* Type Offset=46 */

	/* Return value */

/* 326 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 328 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 330 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure get_Timeout */

/* 332 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 334 */	NdrFcLong( 0x0 ),	/* 0 */
/* 338 */	NdrFcShort( 0xe ),	/* 14 */
/* 340 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 342 */	NdrFcShort( 0x0 ),	/* 0 */
/* 344 */	NdrFcShort( 0x24 ),	/* 36 */
/* 346 */	0x44,		/* Oi2 Flags:  has return, has ext, */
			0x2,		/* 2 */
/* 348 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 350 */	NdrFcShort( 0x0 ),	/* 0 */
/* 352 */	NdrFcShort( 0x0 ),	/* 0 */
/* 354 */	NdrFcShort( 0x0 ),	/* 0 */
/* 356 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter pVal */

/* 358 */	NdrFcShort( 0x2150 ),	/* Flags:  out, base type, simple ref, srv alloc size=8 */
/* 360 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 362 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Return value */

/* 364 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 366 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 368 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure RemoveObject */


	/* Procedure put_Timeout */

/* 370 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 372 */	NdrFcLong( 0x0 ),	/* 0 */
/* 376 */	NdrFcShort( 0xf ),	/* 15 */
/* 378 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 380 */	NdrFcShort( 0x8 ),	/* 8 */
/* 382 */	NdrFcShort( 0x8 ),	/* 8 */
/* 384 */	0x44,		/* Oi2 Flags:  has return, has ext, */
			0x2,		/* 2 */
/* 386 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 388 */	NdrFcShort( 0x0 ),	/* 0 */
/* 390 */	NdrFcShort( 0x0 ),	/* 0 */
/* 392 */	NdrFcShort( 0x0 ),	/* 0 */
/* 394 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter index */


	/* Parameter iVal */

/* 396 */	NdrFcShort( 0x48 ),	/* Flags:  in, base type, */
/* 398 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 400 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Return value */


	/* Return value */

/* 402 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 404 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 406 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure EndSession */

/* 408 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 410 */	NdrFcLong( 0x0 ),	/* 0 */
/* 414 */	NdrFcShort( 0x10 ),	/* 16 */
/* 416 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 418 */	NdrFcShort( 0x0 ),	/* 0 */
/* 420 */	NdrFcShort( 0x8 ),	/* 8 */
/* 422 */	0x44,		/* Oi2 Flags:  has return, has ext, */
			0x1,		/* 1 */
/* 424 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 426 */	NdrFcShort( 0x0 ),	/* 0 */
/* 428 */	NdrFcShort( 0x0 ),	/* 0 */
/* 430 */	NdrFcShort( 0x0 ),	/* 0 */
/* 432 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Return value */

/* 434 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 436 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 438 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure ReportStrParam */

/* 440 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 442 */	NdrFcLong( 0x0 ),	/* 0 */
/* 446 */	NdrFcShort( 0x11 ),	/* 17 */
/* 448 */	NdrFcShort( 0x28 ),	/* x86 Stack size/offset = 40 */
/* 450 */	NdrFcShort( 0x0 ),	/* 0 */
/* 452 */	NdrFcShort( 0x8 ),	/* 8 */
/* 454 */	0x47,		/* Oi2 Flags:  srv must size, clt must size, has return, has ext, */
			0x4,		/* 4 */
/* 456 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 458 */	NdrFcShort( 0x0 ),	/* 0 */
/* 460 */	NdrFcShort( 0x1 ),	/* 1 */
/* 462 */	NdrFcShort( 0x0 ),	/* 0 */
/* 464 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter name */

/* 466 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 468 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 470 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter params */

/* 472 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 474 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 476 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter collection */

/* 478 */	NdrFcShort( 0x13 ),	/* Flags:  must size, must free, out, */
/* 480 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 482 */	NdrFcShort( 0x2a ),	/* Type Offset=42 */

	/* Return value */

/* 484 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 486 */	NdrFcShort( 0x20 ),	/* x86 Stack size/offset = 32 */
/* 488 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Call */

/* 490 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 492 */	NdrFcLong( 0x0 ),	/* 0 */
/* 496 */	NdrFcShort( 0x12 ),	/* 18 */
/* 498 */	NdrFcShort( 0x28 ),	/* x86 Stack size/offset = 40 */
/* 500 */	NdrFcShort( 0x0 ),	/* 0 */
/* 502 */	NdrFcShort( 0x8 ),	/* 8 */
/* 504 */	0x47,		/* Oi2 Flags:  srv must size, clt must size, has return, has ext, */
			0x4,		/* 4 */
/* 506 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 508 */	NdrFcShort( 0x0 ),	/* 0 */
/* 510 */	NdrFcShort( 0x1 ),	/* 1 */
/* 512 */	NdrFcShort( 0x0 ),	/* 0 */
/* 514 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter name */

/* 516 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 518 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 520 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter params */

/* 522 */	NdrFcShort( 0xb ),	/* Flags:  must size, must free, in, */
/* 524 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 526 */	NdrFcShort( 0x2e ),	/* Type Offset=46 */

	/* Parameter collection */

/* 528 */	NdrFcShort( 0x13 ),	/* Flags:  must size, must free, out, */
/* 530 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 532 */	NdrFcShort( 0x2a ),	/* Type Offset=42 */

	/* Return value */

/* 534 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 536 */	NdrFcShort( 0x20 ),	/* x86 Stack size/offset = 32 */
/* 538 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Get */

/* 540 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 542 */	NdrFcLong( 0x0 ),	/* 0 */
/* 546 */	NdrFcShort( 0x7 ),	/* 7 */
/* 548 */	NdrFcShort( 0x20 ),	/* x86 Stack size/offset = 32 */
/* 550 */	NdrFcShort( 0x8 ),	/* 8 */
/* 552 */	NdrFcShort( 0x8 ),	/* 8 */
/* 554 */	0x45,		/* Oi2 Flags:  srv must size, has return, has ext, */
			0x3,		/* 3 */
/* 556 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 558 */	NdrFcShort( 0x0 ),	/* 0 */
/* 560 */	NdrFcShort( 0x0 ),	/* 0 */
/* 562 */	NdrFcShort( 0x0 ),	/* 0 */
/* 564 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter index */

/* 566 */	NdrFcShort( 0x48 ),	/* Flags:  in, base type, */
/* 568 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 570 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Parameter object */

/* 572 */	NdrFcShort( 0x13 ),	/* Flags:  must size, must free, out, */
/* 574 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 576 */	NdrFcShort( 0x2a ),	/* Type Offset=42 */

	/* Return value */

/* 578 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 580 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 582 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure New */

/* 584 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 586 */	NdrFcLong( 0x0 ),	/* 0 */
/* 590 */	NdrFcShort( 0x8 ),	/* 8 */
/* 592 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 594 */	NdrFcShort( 0x0 ),	/* 0 */
/* 596 */	NdrFcShort( 0x8 ),	/* 8 */
/* 598 */	0x45,		/* Oi2 Flags:  srv must size, has return, has ext, */
			0x2,		/* 2 */
/* 600 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 602 */	NdrFcShort( 0x0 ),	/* 0 */
/* 604 */	NdrFcShort( 0x0 ),	/* 0 */
/* 606 */	NdrFcShort( 0x0 ),	/* 0 */
/* 608 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter object */

/* 610 */	NdrFcShort( 0x13 ),	/* Flags:  must size, must free, out, */
/* 612 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 614 */	NdrFcShort( 0x2a ),	/* Type Offset=42 */

	/* Return value */

/* 616 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 618 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 620 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Read */


	/* Procedure Write */

/* 622 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 624 */	NdrFcLong( 0x0 ),	/* 0 */
/* 628 */	NdrFcShort( 0x9 ),	/* 9 */
/* 630 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 632 */	NdrFcShort( 0x0 ),	/* 0 */
/* 634 */	NdrFcShort( 0x8 ),	/* 8 */
/* 636 */	0x46,		/* Oi2 Flags:  clt must size, has return, has ext, */
			0x2,		/* 2 */
/* 638 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 640 */	NdrFcShort( 0x0 ),	/* 0 */
/* 642 */	NdrFcShort( 0x1 ),	/* 1 */
/* 644 */	NdrFcShort( 0x0 ),	/* 0 */
/* 646 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter name */


	/* Parameter userid */

/* 648 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 650 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 652 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Return value */


	/* Return value */

/* 654 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 656 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 658 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Replace */

/* 660 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 662 */	NdrFcLong( 0x0 ),	/* 0 */
/* 666 */	NdrFcShort( 0xa ),	/* 10 */
/* 668 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 670 */	NdrFcShort( 0x0 ),	/* 0 */
/* 672 */	NdrFcShort( 0x8 ),	/* 8 */
/* 674 */	0x46,		/* Oi2 Flags:  clt must size, has return, has ext, */
			0x2,		/* 2 */
/* 676 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 678 */	NdrFcShort( 0x0 ),	/* 0 */
/* 680 */	NdrFcShort( 0x1 ),	/* 1 */
/* 682 */	NdrFcShort( 0x0 ),	/* 0 */
/* 684 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter userid */

/* 686 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 688 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 690 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Return value */

/* 692 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 694 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 696 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Delete */

/* 698 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 700 */	NdrFcLong( 0x0 ),	/* 0 */
/* 704 */	NdrFcShort( 0xb ),	/* 11 */
/* 706 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 708 */	NdrFcShort( 0x0 ),	/* 0 */
/* 710 */	NdrFcShort( 0x8 ),	/* 8 */
/* 712 */	0x46,		/* Oi2 Flags:  clt must size, has return, has ext, */
			0x2,		/* 2 */
/* 714 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 716 */	NdrFcShort( 0x0 ),	/* 0 */
/* 718 */	NdrFcShort( 0x1 ),	/* 1 */
/* 720 */	NdrFcShort( 0x0 ),	/* 0 */
/* 722 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter userid */

/* 724 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 726 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 728 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Return value */

/* 730 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 732 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 734 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure get_Count */

/* 736 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 738 */	NdrFcLong( 0x0 ),	/* 0 */
/* 742 */	NdrFcShort( 0xc ),	/* 12 */
/* 744 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 746 */	NdrFcShort( 0x0 ),	/* 0 */
/* 748 */	NdrFcShort( 0x2c ),	/* 44 */
/* 750 */	0x44,		/* Oi2 Flags:  has return, has ext, */
			0x2,		/* 2 */
/* 752 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 754 */	NdrFcShort( 0x0 ),	/* 0 */
/* 756 */	NdrFcShort( 0x0 ),	/* 0 */
/* 758 */	NdrFcShort( 0x0 ),	/* 0 */
/* 760 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter pVal */

/* 762 */	NdrFcShort( 0x2150 ),	/* Flags:  out, base type, simple ref, srv alloc size=8 */
/* 764 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 766 */	0xc,		/* FC_DOUBLE */
			0x0,		/* 0 */

	/* Return value */

/* 768 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 770 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 772 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure get_Fields */

/* 774 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 776 */	NdrFcLong( 0x0 ),	/* 0 */
/* 780 */	NdrFcShort( 0xd ),	/* 13 */
/* 782 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 784 */	NdrFcShort( 0x0 ),	/* 0 */
/* 786 */	NdrFcShort( 0x8 ),	/* 8 */
/* 788 */	0x45,		/* Oi2 Flags:  srv must size, has return, has ext, */
			0x2,		/* 2 */
/* 790 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 792 */	NdrFcShort( 0x0 ),	/* 0 */
/* 794 */	NdrFcShort( 0x0 ),	/* 0 */
/* 796 */	NdrFcShort( 0x0 ),	/* 0 */
/* 798 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter pVal */

/* 800 */	NdrFcShort( 0x13 ),	/* Flags:  must size, must free, out, */
/* 802 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 804 */	NdrFcShort( 0x2a ),	/* Type Offset=42 */

	/* Return value */

/* 806 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 808 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 810 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure get_KeyFields */

/* 812 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 814 */	NdrFcLong( 0x0 ),	/* 0 */
/* 818 */	NdrFcShort( 0xe ),	/* 14 */
/* 820 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 822 */	NdrFcShort( 0x0 ),	/* 0 */
/* 824 */	NdrFcShort( 0x8 ),	/* 8 */
/* 826 */	0x45,		/* Oi2 Flags:  srv must size, has return, has ext, */
			0x2,		/* 2 */
/* 828 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 830 */	NdrFcShort( 0x0 ),	/* 0 */
/* 832 */	NdrFcShort( 0x0 ),	/* 0 */
/* 834 */	NdrFcShort( 0x0 ),	/* 0 */
/* 836 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter pVal */

/* 838 */	NdrFcShort( 0x13 ),	/* Flags:  must size, must free, out, */
/* 840 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 842 */	NdrFcShort( 0x2a ),	/* Type Offset=42 */

	/* Return value */

/* 844 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 846 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 848 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure get_Type */

/* 850 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 852 */	NdrFcLong( 0x0 ),	/* 0 */
/* 856 */	NdrFcShort( 0x10 ),	/* 16 */
/* 858 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 860 */	NdrFcShort( 0x0 ),	/* 0 */
/* 862 */	NdrFcShort( 0x8 ),	/* 8 */
/* 864 */	0x45,		/* Oi2 Flags:  srv must size, has return, has ext, */
			0x2,		/* 2 */
/* 866 */	0xa,		/* 10 */
			0x3,		/* Ext Flags:  new corr desc, clt corr check, */
/* 868 */	NdrFcShort( 0x1 ),	/* 1 */
/* 870 */	NdrFcShort( 0x0 ),	/* 0 */
/* 872 */	NdrFcShort( 0x0 ),	/* 0 */
/* 874 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter pVal */

/* 876 */	NdrFcShort( 0x2113 ),	/* Flags:  must size, must free, out, simple ref, srv alloc size=8 */
/* 878 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 880 */	NdrFcShort( 0x48 ),	/* Type Offset=72 */

	/* Return value */

/* 882 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 884 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 886 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure put_Type */

/* 888 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 890 */	NdrFcLong( 0x0 ),	/* 0 */
/* 894 */	NdrFcShort( 0x11 ),	/* 17 */
/* 896 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 898 */	NdrFcShort( 0x0 ),	/* 0 */
/* 900 */	NdrFcShort( 0x8 ),	/* 8 */
/* 902 */	0x46,		/* Oi2 Flags:  clt must size, has return, has ext, */
			0x2,		/* 2 */
/* 904 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 906 */	NdrFcShort( 0x0 ),	/* 0 */
/* 908 */	NdrFcShort( 0x1 ),	/* 1 */
/* 910 */	NdrFcShort( 0x0 ),	/* 0 */
/* 912 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter pVal */

/* 914 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 916 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 918 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Return value */

/* 920 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 922 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 924 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure WriteDirect */

/* 926 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 928 */	NdrFcLong( 0x0 ),	/* 0 */
/* 932 */	NdrFcShort( 0x12 ),	/* 18 */
/* 934 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 936 */	NdrFcShort( 0x0 ),	/* 0 */
/* 938 */	NdrFcShort( 0x8 ),	/* 8 */
/* 940 */	0x44,		/* Oi2 Flags:  has return, has ext, */
			0x1,		/* 1 */
/* 942 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 944 */	NdrFcShort( 0x0 ),	/* 0 */
/* 946 */	NdrFcShort( 0x0 ),	/* 0 */
/* 948 */	NdrFcShort( 0x0 ),	/* 0 */
/* 950 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Return value */

/* 952 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 954 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 956 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure ReplaceDirect */

/* 958 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 960 */	NdrFcLong( 0x0 ),	/* 0 */
/* 964 */	NdrFcShort( 0x13 ),	/* 19 */
/* 966 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 968 */	NdrFcShort( 0x0 ),	/* 0 */
/* 970 */	NdrFcShort( 0x8 ),	/* 8 */
/* 972 */	0x46,		/* Oi2 Flags:  clt must size, has return, has ext, */
			0x2,		/* 2 */
/* 974 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 976 */	NdrFcShort( 0x0 ),	/* 0 */
/* 978 */	NdrFcShort( 0x1 ),	/* 1 */
/* 980 */	NdrFcShort( 0x0 ),	/* 0 */
/* 982 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter where */

/* 984 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 986 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 988 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Return value */

/* 990 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 992 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 994 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Get */

/* 996 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 998 */	NdrFcLong( 0x0 ),	/* 0 */
/* 1002 */	NdrFcShort( 0x7 ),	/* 7 */
/* 1004 */	NdrFcShort( 0x20 ),	/* x86 Stack size/offset = 32 */
/* 1006 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1008 */	NdrFcShort( 0x8 ),	/* 8 */
/* 1010 */	0x47,		/* Oi2 Flags:  srv must size, clt must size, has return, has ext, */
			0x3,		/* 3 */
/* 1012 */	0xa,		/* 10 */
			0x7,		/* Ext Flags:  new corr desc, clt corr check, srv corr check, */
/* 1014 */	NdrFcShort( 0x1 ),	/* 1 */
/* 1016 */	NdrFcShort( 0x1 ),	/* 1 */
/* 1018 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1020 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter fieldName */

/* 1022 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 1024 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 1026 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter value */

/* 1028 */	NdrFcShort( 0x6113 ),	/* Flags:  must size, must free, out, simple ref, srv alloc size=24 */
/* 1030 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 1032 */	NdrFcShort( 0x3fe ),	/* Type Offset=1022 */

	/* Return value */

/* 1034 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 1036 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 1038 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Set */

/* 1040 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 1042 */	NdrFcLong( 0x0 ),	/* 0 */
/* 1046 */	NdrFcShort( 0x8 ),	/* 8 */
/* 1048 */	NdrFcShort( 0x20 ),	/* x86 Stack size/offset = 32 */
/* 1050 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1052 */	NdrFcShort( 0x8 ),	/* 8 */
/* 1054 */	0x46,		/* Oi2 Flags:  clt must size, has return, has ext, */
			0x3,		/* 3 */
/* 1056 */	0xa,		/* 10 */
			0x85,		/* Ext Flags:  new corr desc, srv corr check, has big byval param */
/* 1058 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1060 */	NdrFcShort( 0x1 ),	/* 1 */
/* 1062 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1064 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter fieldName */

/* 1066 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 1068 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 1070 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter value */

/* 1072 */	NdrFcShort( 0x10b ),	/* Flags:  must size, must free, in, simple ref, */
/* 1074 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 1076 */	NdrFcShort( 0x410 ),	/* Type Offset=1040 */

	/* Return value */

/* 1078 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 1080 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 1082 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Delete */

/* 1084 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 1086 */	NdrFcLong( 0x0 ),	/* 0 */
/* 1090 */	NdrFcShort( 0x9 ),	/* 9 */
/* 1092 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 1094 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1096 */	NdrFcShort( 0x8 ),	/* 8 */
/* 1098 */	0x44,		/* Oi2 Flags:  has return, has ext, */
			0x1,		/* 1 */
/* 1100 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 1102 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1104 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1106 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1108 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Return value */

/* 1110 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 1112 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 1114 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure DateToString */

/* 1116 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 1118 */	NdrFcLong( 0x0 ),	/* 0 */
/* 1122 */	NdrFcShort( 0xa ),	/* 10 */
/* 1124 */	NdrFcShort( 0x20 ),	/* x86 Stack size/offset = 32 */
/* 1126 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1128 */	NdrFcShort( 0x8 ),	/* 8 */
/* 1130 */	0x47,		/* Oi2 Flags:  srv must size, clt must size, has return, has ext, */
			0x3,		/* 3 */
/* 1132 */	0xa,		/* 10 */
			0x7,		/* Ext Flags:  new corr desc, clt corr check, srv corr check, */
/* 1134 */	NdrFcShort( 0x1 ),	/* 1 */
/* 1136 */	NdrFcShort( 0x1 ),	/* 1 */
/* 1138 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1140 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter fieldName */

/* 1142 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 1144 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 1146 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter value */

/* 1148 */	NdrFcShort( 0x6113 ),	/* Flags:  must size, must free, out, simple ref, srv alloc size=24 */
/* 1150 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 1152 */	NdrFcShort( 0x3fe ),	/* Type Offset=1022 */

	/* Return value */

/* 1154 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 1156 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 1158 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure DateFromString */

/* 1160 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 1162 */	NdrFcLong( 0x0 ),	/* 0 */
/* 1166 */	NdrFcShort( 0xb ),	/* 11 */
/* 1168 */	NdrFcShort( 0x20 ),	/* x86 Stack size/offset = 32 */
/* 1170 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1172 */	NdrFcShort( 0x8 ),	/* 8 */
/* 1174 */	0x46,		/* Oi2 Flags:  clt must size, has return, has ext, */
			0x3,		/* 3 */
/* 1176 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 1178 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1180 */	NdrFcShort( 0x1 ),	/* 1 */
/* 1182 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1184 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter fieldName */

/* 1186 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 1188 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 1190 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Parameter dateValue */

/* 1192 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 1194 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 1196 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Return value */

/* 1198 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 1200 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 1202 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure get_Count */

/* 1204 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 1206 */	NdrFcLong( 0x0 ),	/* 0 */
/* 1210 */	NdrFcShort( 0x7 ),	/* 7 */
/* 1212 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 1214 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1216 */	NdrFcShort( 0x2c ),	/* 44 */
/* 1218 */	0x44,		/* Oi2 Flags:  has return, has ext, */
			0x2,		/* 2 */
/* 1220 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 1222 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1224 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1226 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1228 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter pVal */

/* 1230 */	NdrFcShort( 0x2150 ),	/* Flags:  out, base type, simple ref, srv alloc size=8 */
/* 1232 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 1234 */	0xc,		/* FC_DOUBLE */
			0x0,		/* 0 */

	/* Return value */

/* 1236 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 1238 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 1240 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Get */

/* 1242 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 1244 */	NdrFcLong( 0x0 ),	/* 0 */
/* 1248 */	NdrFcShort( 0x8 ),	/* 8 */
/* 1250 */	NdrFcShort( 0x20 ),	/* x86 Stack size/offset = 32 */
/* 1252 */	NdrFcShort( 0x8 ),	/* 8 */
/* 1254 */	NdrFcShort( 0x8 ),	/* 8 */
/* 1256 */	0x45,		/* Oi2 Flags:  srv must size, has return, has ext, */
			0x3,		/* 3 */
/* 1258 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 1260 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1262 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1264 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1266 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter index */

/* 1268 */	NdrFcShort( 0x48 ),	/* Flags:  in, base type, */
/* 1270 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 1272 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Parameter value */

/* 1274 */	NdrFcShort( 0x13 ),	/* Flags:  must size, must free, out, */
/* 1276 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 1278 */	NdrFcShort( 0x2a ),	/* Type Offset=42 */

	/* Return value */

/* 1280 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 1282 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 1284 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure get_Name */

/* 1286 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 1288 */	NdrFcLong( 0x0 ),	/* 0 */
/* 1292 */	NdrFcShort( 0x7 ),	/* 7 */
/* 1294 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 1296 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1298 */	NdrFcShort( 0x8 ),	/* 8 */
/* 1300 */	0x45,		/* Oi2 Flags:  srv must size, has return, has ext, */
			0x2,		/* 2 */
/* 1302 */	0xa,		/* 10 */
			0x3,		/* Ext Flags:  new corr desc, clt corr check, */
/* 1304 */	NdrFcShort( 0x1 ),	/* 1 */
/* 1306 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1308 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1310 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter pVal */

/* 1312 */	NdrFcShort( 0x2113 ),	/* Flags:  must size, must free, out, simple ref, srv alloc size=8 */
/* 1314 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 1316 */	NdrFcShort( 0x48 ),	/* Type Offset=72 */

	/* Return value */

/* 1318 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 1320 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 1322 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure get_Size */


	/* Procedure get_Type */

/* 1324 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 1326 */	NdrFcLong( 0x0 ),	/* 0 */
/* 1330 */	NdrFcShort( 0x8 ),	/* 8 */
/* 1332 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 1334 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1336 */	NdrFcShort( 0x2c ),	/* 44 */
/* 1338 */	0x44,		/* Oi2 Flags:  has return, has ext, */
			0x2,		/* 2 */
/* 1340 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 1342 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1344 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1346 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1348 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter pVal */


	/* Parameter pVal */

/* 1350 */	NdrFcShort( 0x2150 ),	/* Flags:  out, base type, simple ref, srv alloc size=8 */
/* 1352 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 1354 */	0xc,		/* FC_DOUBLE */
			0x0,		/* 0 */

	/* Return value */


	/* Return value */

/* 1356 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 1358 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 1360 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure get_ChildObject */

/* 1362 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 1364 */	NdrFcLong( 0x0 ),	/* 0 */
/* 1368 */	NdrFcShort( 0x9 ),	/* 9 */
/* 1370 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 1372 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1374 */	NdrFcShort( 0x8 ),	/* 8 */
/* 1376 */	0x45,		/* Oi2 Flags:  srv must size, has return, has ext, */
			0x2,		/* 2 */
/* 1378 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 1380 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1382 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1384 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1386 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter pVal */

/* 1388 */	NdrFcShort( 0x13 ),	/* Flags:  must size, must free, out, */
/* 1390 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 1392 */	NdrFcShort( 0x2a ),	/* Type Offset=42 */

	/* Return value */

/* 1394 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 1396 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 1398 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure Write */

/* 1400 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 1402 */	NdrFcLong( 0x0 ),	/* 0 */
/* 1406 */	NdrFcShort( 0x7 ),	/* 7 */
/* 1408 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 1410 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1412 */	NdrFcShort( 0x8 ),	/* 8 */
/* 1414 */	0x46,		/* Oi2 Flags:  clt must size, has return, has ext, */
			0x2,		/* 2 */
/* 1416 */	0xa,		/* 10 */
			0x5,		/* Ext Flags:  new corr desc, srv corr check, */
/* 1418 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1420 */	NdrFcShort( 0x1 ),	/* 1 */
/* 1422 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1424 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter name */

/* 1426 */	NdrFcShort( 0x8b ),	/* Flags:  must size, must free, in, by val, */
/* 1428 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 1430 */	NdrFcShort( 0x1c ),	/* Type Offset=28 */

	/* Return value */

/* 1432 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 1434 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 1436 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

	/* Procedure SetFrom */

/* 1438 */	0x33,		/* FC_AUTO_HANDLE */
			0x6c,		/* Old Flags:  object, Oi2 */
/* 1440 */	NdrFcLong( 0x0 ),	/* 0 */
/* 1444 */	NdrFcShort( 0xa ),	/* 10 */
/* 1446 */	NdrFcShort( 0x18 ),	/* x86 Stack size/offset = 24 */
/* 1448 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1450 */	NdrFcShort( 0x8 ),	/* 8 */
/* 1452 */	0x46,		/* Oi2 Flags:  clt must size, has return, has ext, */
			0x2,		/* 2 */
/* 1454 */	0xa,		/* 10 */
			0x1,		/* Ext Flags:  new corr desc, */
/* 1456 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1458 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1460 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1462 */	NdrFcShort( 0x0 ),	/* 0 */

	/* Parameter pVal */

/* 1464 */	NdrFcShort( 0xb ),	/* Flags:  must size, must free, in, */
/* 1466 */	NdrFcShort( 0x8 ),	/* x86 Stack size/offset = 8 */
/* 1468 */	NdrFcShort( 0x2e ),	/* Type Offset=46 */

	/* Return value */

/* 1470 */	NdrFcShort( 0x70 ),	/* Flags:  out, return, base type, */
/* 1472 */	NdrFcShort( 0x10 ),	/* x86 Stack size/offset = 16 */
/* 1474 */	0x8,		/* FC_LONG */
			0x0,		/* 0 */

			0x0
        }
    };

static const ComGRServer_MIDL_TYPE_FORMAT_STRING ComGRServer__MIDL_TypeFormatString =
    {
        0,
        {
			NdrFcShort( 0x0 ),	/* 0 */
/*  2 */	
			0x12, 0x0,	/* FC_UP */
/*  4 */	NdrFcShort( 0xe ),	/* Offset= 14 (18) */
/*  6 */	
			0x1b,		/* FC_CARRAY */
			0x1,		/* 1 */
/*  8 */	NdrFcShort( 0x2 ),	/* 2 */
/* 10 */	0x9,		/* Corr desc: FC_ULONG */
			0x0,		/*  */
/* 12 */	NdrFcShort( 0xfffc ),	/* -4 */
/* 14 */	NdrFcShort( 0x1 ),	/* Corr flags:  early, */
/* 16 */	0x6,		/* FC_SHORT */
			0x5b,		/* FC_END */
/* 18 */	
			0x17,		/* FC_CSTRUCT */
			0x3,		/* 3 */
/* 20 */	NdrFcShort( 0x8 ),	/* 8 */
/* 22 */	NdrFcShort( 0xfff0 ),	/* Offset= -16 (6) */
/* 24 */	0x8,		/* FC_LONG */
			0x8,		/* FC_LONG */
/* 26 */	0x5c,		/* FC_PAD */
			0x5b,		/* FC_END */
/* 28 */	0xb4,		/* FC_USER_MARSHAL */
			0x83,		/* 131 */
/* 30 */	NdrFcShort( 0x0 ),	/* 0 */
/* 32 */	NdrFcShort( 0x8 ),	/* 8 */
/* 34 */	NdrFcShort( 0x0 ),	/* 0 */
/* 36 */	NdrFcShort( 0xffde ),	/* Offset= -34 (2) */
/* 38 */	
			0x11, 0xc,	/* FC_RP [alloced_on_stack] [simple_pointer] */
/* 40 */	0x6,		/* FC_SHORT */
			0x5c,		/* FC_PAD */
/* 42 */	
			0x11, 0x10,	/* FC_RP [pointer_deref] */
/* 44 */	NdrFcShort( 0x2 ),	/* Offset= 2 (46) */
/* 46 */	
			0x2f,		/* FC_IP */
			0x5a,		/* FC_CONSTANT_IID */
/* 48 */	NdrFcLong( 0x20400 ),	/* 132096 */
/* 52 */	NdrFcShort( 0x0 ),	/* 0 */
/* 54 */	NdrFcShort( 0x0 ),	/* 0 */
/* 56 */	0xc0,		/* 192 */
			0x0,		/* 0 */
/* 58 */	0x0,		/* 0 */
			0x0,		/* 0 */
/* 60 */	0x0,		/* 0 */
			0x0,		/* 0 */
/* 62 */	0x0,		/* 0 */
			0x46,		/* 70 */
/* 64 */	
			0x11, 0x4,	/* FC_RP [alloced_on_stack] */
/* 66 */	NdrFcShort( 0x6 ),	/* Offset= 6 (72) */
/* 68 */	
			0x13, 0x0,	/* FC_OP */
/* 70 */	NdrFcShort( 0xffcc ),	/* Offset= -52 (18) */
/* 72 */	0xb4,		/* FC_USER_MARSHAL */
			0x83,		/* 131 */
/* 74 */	NdrFcShort( 0x0 ),	/* 0 */
/* 76 */	NdrFcShort( 0x8 ),	/* 8 */
/* 78 */	NdrFcShort( 0x0 ),	/* 0 */
/* 80 */	NdrFcShort( 0xfff4 ),	/* Offset= -12 (68) */
/* 82 */	
			0x11, 0xc,	/* FC_RP [alloced_on_stack] [simple_pointer] */
/* 84 */	0x8,		/* FC_LONG */
			0x5c,		/* FC_PAD */
/* 86 */	
			0x11, 0xc,	/* FC_RP [alloced_on_stack] [simple_pointer] */
/* 88 */	0xc,		/* FC_DOUBLE */
			0x5c,		/* FC_PAD */
/* 90 */	
			0x11, 0x4,	/* FC_RP [alloced_on_stack] */
/* 92 */	NdrFcShort( 0x3a2 ),	/* Offset= 930 (1022) */
/* 94 */	
			0x13, 0x0,	/* FC_OP */
/* 96 */	NdrFcShort( 0x38a ),	/* Offset= 906 (1002) */
/* 98 */	
			0x2b,		/* FC_NON_ENCAPSULATED_UNION */
			0x9,		/* FC_ULONG */
/* 100 */	0x7,		/* Corr desc: FC_USHORT */
			0x0,		/*  */
/* 102 */	NdrFcShort( 0xfff8 ),	/* -8 */
/* 104 */	NdrFcShort( 0x1 ),	/* Corr flags:  early, */
/* 106 */	NdrFcShort( 0x2 ),	/* Offset= 2 (108) */
/* 108 */	NdrFcShort( 0x10 ),	/* 16 */
/* 110 */	NdrFcShort( 0x2f ),	/* 47 */
/* 112 */	NdrFcLong( 0x14 ),	/* 20 */
/* 116 */	NdrFcShort( 0x800b ),	/* Simple arm type: FC_HYPER */
/* 118 */	NdrFcLong( 0x3 ),	/* 3 */
/* 122 */	NdrFcShort( 0x8008 ),	/* Simple arm type: FC_LONG */
/* 124 */	NdrFcLong( 0x11 ),	/* 17 */
/* 128 */	NdrFcShort( 0x8001 ),	/* Simple arm type: FC_BYTE */
/* 130 */	NdrFcLong( 0x2 ),	/* 2 */
/* 134 */	NdrFcShort( 0x8006 ),	/* Simple arm type: FC_SHORT */
/* 136 */	NdrFcLong( 0x4 ),	/* 4 */
/* 140 */	NdrFcShort( 0x800a ),	/* Simple arm type: FC_FLOAT */
/* 142 */	NdrFcLong( 0x5 ),	/* 5 */
/* 146 */	NdrFcShort( 0x800c ),	/* Simple arm type: FC_DOUBLE */
/* 148 */	NdrFcLong( 0xb ),	/* 11 */
/* 152 */	NdrFcShort( 0x8006 ),	/* Simple arm type: FC_SHORT */
/* 154 */	NdrFcLong( 0xa ),	/* 10 */
/* 158 */	NdrFcShort( 0x8008 ),	/* Simple arm type: FC_LONG */
/* 160 */	NdrFcLong( 0x6 ),	/* 6 */
/* 164 */	NdrFcShort( 0xe8 ),	/* Offset= 232 (396) */
/* 166 */	NdrFcLong( 0x7 ),	/* 7 */
/* 170 */	NdrFcShort( 0x800c ),	/* Simple arm type: FC_DOUBLE */
/* 172 */	NdrFcLong( 0x8 ),	/* 8 */
/* 176 */	NdrFcShort( 0xff94 ),	/* Offset= -108 (68) */
/* 178 */	NdrFcLong( 0xd ),	/* 13 */
/* 182 */	NdrFcShort( 0xdc ),	/* Offset= 220 (402) */
/* 184 */	NdrFcLong( 0x9 ),	/* 9 */
/* 188 */	NdrFcShort( 0xff72 ),	/* Offset= -142 (46) */
/* 190 */	NdrFcLong( 0x2000 ),	/* 8192 */
/* 194 */	NdrFcShort( 0xe2 ),	/* Offset= 226 (420) */
/* 196 */	NdrFcLong( 0x24 ),	/* 36 */
/* 200 */	NdrFcShort( 0x2d8 ),	/* Offset= 728 (928) */
/* 202 */	NdrFcLong( 0x4024 ),	/* 16420 */
/* 206 */	NdrFcShort( 0x2d2 ),	/* Offset= 722 (928) */
/* 208 */	NdrFcLong( 0x4011 ),	/* 16401 */
/* 212 */	NdrFcShort( 0x2d0 ),	/* Offset= 720 (932) */
/* 214 */	NdrFcLong( 0x4002 ),	/* 16386 */
/* 218 */	NdrFcShort( 0x2ce ),	/* Offset= 718 (936) */
/* 220 */	NdrFcLong( 0x4003 ),	/* 16387 */
/* 224 */	NdrFcShort( 0x2cc ),	/* Offset= 716 (940) */
/* 226 */	NdrFcLong( 0x4014 ),	/* 16404 */
/* 230 */	NdrFcShort( 0x2ca ),	/* Offset= 714 (944) */
/* 232 */	NdrFcLong( 0x4004 ),	/* 16388 */
/* 236 */	NdrFcShort( 0x2c8 ),	/* Offset= 712 (948) */
/* 238 */	NdrFcLong( 0x4005 ),	/* 16389 */
/* 242 */	NdrFcShort( 0x2c6 ),	/* Offset= 710 (952) */
/* 244 */	NdrFcLong( 0x400b ),	/* 16395 */
/* 248 */	NdrFcShort( 0x2b0 ),	/* Offset= 688 (936) */
/* 250 */	NdrFcLong( 0x400a ),	/* 16394 */
/* 254 */	NdrFcShort( 0x2ae ),	/* Offset= 686 (940) */
/* 256 */	NdrFcLong( 0x4006 ),	/* 16390 */
/* 260 */	NdrFcShort( 0x2b8 ),	/* Offset= 696 (956) */
/* 262 */	NdrFcLong( 0x4007 ),	/* 16391 */
/* 266 */	NdrFcShort( 0x2ae ),	/* Offset= 686 (952) */
/* 268 */	NdrFcLong( 0x4008 ),	/* 16392 */
/* 272 */	NdrFcShort( 0x2b0 ),	/* Offset= 688 (960) */
/* 274 */	NdrFcLong( 0x400d ),	/* 16397 */
/* 278 */	NdrFcShort( 0x2ae ),	/* Offset= 686 (964) */
/* 280 */	NdrFcLong( 0x4009 ),	/* 16393 */
/* 284 */	NdrFcShort( 0x2ac ),	/* Offset= 684 (968) */
/* 286 */	NdrFcLong( 0x6000 ),	/* 24576 */
/* 290 */	NdrFcShort( 0x2aa ),	/* Offset= 682 (972) */
/* 292 */	NdrFcLong( 0x400c ),	/* 16396 */
/* 296 */	NdrFcShort( 0x2a8 ),	/* Offset= 680 (976) */
/* 298 */	NdrFcLong( 0x10 ),	/* 16 */
/* 302 */	NdrFcShort( 0x8002 ),	/* Simple arm type: FC_CHAR */
/* 304 */	NdrFcLong( 0x12 ),	/* 18 */
/* 308 */	NdrFcShort( 0x8006 ),	/* Simple arm type: FC_SHORT */
/* 310 */	NdrFcLong( 0x13 ),	/* 19 */
/* 314 */	NdrFcShort( 0x8008 ),	/* Simple arm type: FC_LONG */
/* 316 */	NdrFcLong( 0x15 ),	/* 21 */
/* 320 */	NdrFcShort( 0x800b ),	/* Simple arm type: FC_HYPER */
/* 322 */	NdrFcLong( 0x16 ),	/* 22 */
/* 326 */	NdrFcShort( 0x8008 ),	/* Simple arm type: FC_LONG */
/* 328 */	NdrFcLong( 0x17 ),	/* 23 */
/* 332 */	NdrFcShort( 0x8008 ),	/* Simple arm type: FC_LONG */
/* 334 */	NdrFcLong( 0xe ),	/* 14 */
/* 338 */	NdrFcShort( 0x286 ),	/* Offset= 646 (984) */
/* 340 */	NdrFcLong( 0x400e ),	/* 16398 */
/* 344 */	NdrFcShort( 0x28a ),	/* Offset= 650 (994) */
/* 346 */	NdrFcLong( 0x4010 ),	/* 16400 */
/* 350 */	NdrFcShort( 0x288 ),	/* Offset= 648 (998) */
/* 352 */	NdrFcLong( 0x4012 ),	/* 16402 */
/* 356 */	NdrFcShort( 0x244 ),	/* Offset= 580 (936) */
/* 358 */	NdrFcLong( 0x4013 ),	/* 16403 */
/* 362 */	NdrFcShort( 0x242 ),	/* Offset= 578 (940) */
/* 364 */	NdrFcLong( 0x4015 ),	/* 16405 */
/* 368 */	NdrFcShort( 0x240 ),	/* Offset= 576 (944) */
/* 370 */	NdrFcLong( 0x4016 ),	/* 16406 */
/* 374 */	NdrFcShort( 0x236 ),	/* Offset= 566 (940) */
/* 376 */	NdrFcLong( 0x4017 ),	/* 16407 */
/* 380 */	NdrFcShort( 0x230 ),	/* Offset= 560 (940) */
/* 382 */	NdrFcLong( 0x0 ),	/* 0 */
/* 386 */	NdrFcShort( 0x0 ),	/* Offset= 0 (386) */
/* 388 */	NdrFcLong( 0x1 ),	/* 1 */
/* 392 */	NdrFcShort( 0x0 ),	/* Offset= 0 (392) */
/* 394 */	NdrFcShort( 0xffff ),	/* Offset= -1 (393) */
/* 396 */	
			0x15,		/* FC_STRUCT */
			0x7,		/* 7 */
/* 398 */	NdrFcShort( 0x8 ),	/* 8 */
/* 400 */	0xb,		/* FC_HYPER */
			0x5b,		/* FC_END */
/* 402 */	
			0x2f,		/* FC_IP */
			0x5a,		/* FC_CONSTANT_IID */
/* 404 */	NdrFcLong( 0x0 ),	/* 0 */
/* 408 */	NdrFcShort( 0x0 ),	/* 0 */
/* 410 */	NdrFcShort( 0x0 ),	/* 0 */
/* 412 */	0xc0,		/* 192 */
			0x0,		/* 0 */
/* 414 */	0x0,		/* 0 */
			0x0,		/* 0 */
/* 416 */	0x0,		/* 0 */
			0x0,		/* 0 */
/* 418 */	0x0,		/* 0 */
			0x46,		/* 70 */
/* 420 */	
			0x13, 0x10,	/* FC_OP [pointer_deref] */
/* 422 */	NdrFcShort( 0x2 ),	/* Offset= 2 (424) */
/* 424 */	
			0x13, 0x0,	/* FC_OP */
/* 426 */	NdrFcShort( 0x1e4 ),	/* Offset= 484 (910) */
/* 428 */	
			0x2a,		/* FC_ENCAPSULATED_UNION */
			0x89,		/* 137 */
/* 430 */	NdrFcShort( 0x20 ),	/* 32 */
/* 432 */	NdrFcShort( 0xa ),	/* 10 */
/* 434 */	NdrFcLong( 0x8 ),	/* 8 */
/* 438 */	NdrFcShort( 0x50 ),	/* Offset= 80 (518) */
/* 440 */	NdrFcLong( 0xd ),	/* 13 */
/* 444 */	NdrFcShort( 0x70 ),	/* Offset= 112 (556) */
/* 446 */	NdrFcLong( 0x9 ),	/* 9 */
/* 450 */	NdrFcShort( 0x90 ),	/* Offset= 144 (594) */
/* 452 */	NdrFcLong( 0xc ),	/* 12 */
/* 456 */	NdrFcShort( 0xb0 ),	/* Offset= 176 (632) */
/* 458 */	NdrFcLong( 0x24 ),	/* 36 */
/* 462 */	NdrFcShort( 0x102 ),	/* Offset= 258 (720) */
/* 464 */	NdrFcLong( 0x800d ),	/* 32781 */
/* 468 */	NdrFcShort( 0x11e ),	/* Offset= 286 (754) */
/* 470 */	NdrFcLong( 0x10 ),	/* 16 */
/* 474 */	NdrFcShort( 0x138 ),	/* Offset= 312 (786) */
/* 476 */	NdrFcLong( 0x2 ),	/* 2 */
/* 480 */	NdrFcShort( 0x14e ),	/* Offset= 334 (814) */
/* 482 */	NdrFcLong( 0x3 ),	/* 3 */
/* 486 */	NdrFcShort( 0x164 ),	/* Offset= 356 (842) */
/* 488 */	NdrFcLong( 0x14 ),	/* 20 */
/* 492 */	NdrFcShort( 0x17a ),	/* Offset= 378 (870) */
/* 494 */	NdrFcShort( 0xffff ),	/* Offset= -1 (493) */
/* 496 */	
			0x21,		/* FC_BOGUS_ARRAY */
			0x3,		/* 3 */
/* 498 */	NdrFcShort( 0x0 ),	/* 0 */
/* 500 */	0x19,		/* Corr desc:  field pointer, FC_ULONG */
			0x0,		/*  */
/* 502 */	NdrFcShort( 0x0 ),	/* 0 */
/* 504 */	NdrFcShort( 0x1 ),	/* Corr flags:  early, */
/* 506 */	NdrFcLong( 0xffffffff ),	/* -1 */
/* 510 */	NdrFcShort( 0x0 ),	/* Corr flags:  */
/* 512 */	
			0x13, 0x0,	/* FC_OP */
/* 514 */	NdrFcShort( 0xfe10 ),	/* Offset= -496 (18) */
/* 516 */	0x5c,		/* FC_PAD */
			0x5b,		/* FC_END */
/* 518 */	
			0x1a,		/* FC_BOGUS_STRUCT */
			0x3,		/* 3 */
/* 520 */	NdrFcShort( 0x10 ),	/* 16 */
/* 522 */	NdrFcShort( 0x0 ),	/* 0 */
/* 524 */	NdrFcShort( 0x6 ),	/* Offset= 6 (530) */
/* 526 */	0x8,		/* FC_LONG */
			0x40,		/* FC_STRUCTPAD4 */
/* 528 */	0x36,		/* FC_POINTER */
			0x5b,		/* FC_END */
/* 530 */	
			0x11, 0x0,	/* FC_RP */
/* 532 */	NdrFcShort( 0xffdc ),	/* Offset= -36 (496) */
/* 534 */	
			0x21,		/* FC_BOGUS_ARRAY */
			0x3,		/* 3 */
/* 536 */	NdrFcShort( 0x0 ),	/* 0 */
/* 538 */	0x19,		/* Corr desc:  field pointer, FC_ULONG */
			0x0,		/*  */
/* 540 */	NdrFcShort( 0x0 ),	/* 0 */
/* 542 */	NdrFcShort( 0x1 ),	/* Corr flags:  early, */
/* 544 */	NdrFcLong( 0xffffffff ),	/* -1 */
/* 548 */	NdrFcShort( 0x0 ),	/* Corr flags:  */
/* 550 */	0x4c,		/* FC_EMBEDDED_COMPLEX */
			0x0,		/* 0 */
/* 552 */	NdrFcShort( 0xff6a ),	/* Offset= -150 (402) */
/* 554 */	0x5c,		/* FC_PAD */
			0x5b,		/* FC_END */
/* 556 */	
			0x1a,		/* FC_BOGUS_STRUCT */
			0x3,		/* 3 */
/* 558 */	NdrFcShort( 0x10 ),	/* 16 */
/* 560 */	NdrFcShort( 0x0 ),	/* 0 */
/* 562 */	NdrFcShort( 0x6 ),	/* Offset= 6 (568) */
/* 564 */	0x8,		/* FC_LONG */
			0x40,		/* FC_STRUCTPAD4 */
/* 566 */	0x36,		/* FC_POINTER */
			0x5b,		/* FC_END */
/* 568 */	
			0x11, 0x0,	/* FC_RP */
/* 570 */	NdrFcShort( 0xffdc ),	/* Offset= -36 (534) */
/* 572 */	
			0x21,		/* FC_BOGUS_ARRAY */
			0x3,		/* 3 */
/* 574 */	NdrFcShort( 0x0 ),	/* 0 */
/* 576 */	0x19,		/* Corr desc:  field pointer, FC_ULONG */
			0x0,		/*  */
/* 578 */	NdrFcShort( 0x0 ),	/* 0 */
/* 580 */	NdrFcShort( 0x1 ),	/* Corr flags:  early, */
/* 582 */	NdrFcLong( 0xffffffff ),	/* -1 */
/* 586 */	NdrFcShort( 0x0 ),	/* Corr flags:  */
/* 588 */	0x4c,		/* FC_EMBEDDED_COMPLEX */
			0x0,		/* 0 */
/* 590 */	NdrFcShort( 0xfde0 ),	/* Offset= -544 (46) */
/* 592 */	0x5c,		/* FC_PAD */
			0x5b,		/* FC_END */
/* 594 */	
			0x1a,		/* FC_BOGUS_STRUCT */
			0x3,		/* 3 */
/* 596 */	NdrFcShort( 0x10 ),	/* 16 */
/* 598 */	NdrFcShort( 0x0 ),	/* 0 */
/* 600 */	NdrFcShort( 0x6 ),	/* Offset= 6 (606) */
/* 602 */	0x8,		/* FC_LONG */
			0x40,		/* FC_STRUCTPAD4 */
/* 604 */	0x36,		/* FC_POINTER */
			0x5b,		/* FC_END */
/* 606 */	
			0x11, 0x0,	/* FC_RP */
/* 608 */	NdrFcShort( 0xffdc ),	/* Offset= -36 (572) */
/* 610 */	
			0x21,		/* FC_BOGUS_ARRAY */
			0x3,		/* 3 */
/* 612 */	NdrFcShort( 0x0 ),	/* 0 */
/* 614 */	0x19,		/* Corr desc:  field pointer, FC_ULONG */
			0x0,		/*  */
/* 616 */	NdrFcShort( 0x0 ),	/* 0 */
/* 618 */	NdrFcShort( 0x1 ),	/* Corr flags:  early, */
/* 620 */	NdrFcLong( 0xffffffff ),	/* -1 */
/* 624 */	NdrFcShort( 0x0 ),	/* Corr flags:  */
/* 626 */	
			0x13, 0x0,	/* FC_OP */
/* 628 */	NdrFcShort( 0x176 ),	/* Offset= 374 (1002) */
/* 630 */	0x5c,		/* FC_PAD */
			0x5b,		/* FC_END */
/* 632 */	
			0x1a,		/* FC_BOGUS_STRUCT */
			0x3,		/* 3 */
/* 634 */	NdrFcShort( 0x10 ),	/* 16 */
/* 636 */	NdrFcShort( 0x0 ),	/* 0 */
/* 638 */	NdrFcShort( 0x6 ),	/* Offset= 6 (644) */
/* 640 */	0x8,		/* FC_LONG */
			0x40,		/* FC_STRUCTPAD4 */
/* 642 */	0x36,		/* FC_POINTER */
			0x5b,		/* FC_END */
/* 644 */	
			0x11, 0x0,	/* FC_RP */
/* 646 */	NdrFcShort( 0xffdc ),	/* Offset= -36 (610) */
/* 648 */	
			0x2f,		/* FC_IP */
			0x5a,		/* FC_CONSTANT_IID */
/* 650 */	NdrFcLong( 0x2f ),	/* 47 */
/* 654 */	NdrFcShort( 0x0 ),	/* 0 */
/* 656 */	NdrFcShort( 0x0 ),	/* 0 */
/* 658 */	0xc0,		/* 192 */
			0x0,		/* 0 */
/* 660 */	0x0,		/* 0 */
			0x0,		/* 0 */
/* 662 */	0x0,		/* 0 */
			0x0,		/* 0 */
/* 664 */	0x0,		/* 0 */
			0x46,		/* 70 */
/* 666 */	
			0x1b,		/* FC_CARRAY */
			0x0,		/* 0 */
/* 668 */	NdrFcShort( 0x1 ),	/* 1 */
/* 670 */	0x19,		/* Corr desc:  field pointer, FC_ULONG */
			0x0,		/*  */
/* 672 */	NdrFcShort( 0x4 ),	/* 4 */
/* 674 */	NdrFcShort( 0x1 ),	/* Corr flags:  early, */
/* 676 */	0x1,		/* FC_BYTE */
			0x5b,		/* FC_END */
/* 678 */	
			0x1a,		/* FC_BOGUS_STRUCT */
			0x3,		/* 3 */
/* 680 */	NdrFcShort( 0x18 ),	/* 24 */
/* 682 */	NdrFcShort( 0x0 ),	/* 0 */
/* 684 */	NdrFcShort( 0xa ),	/* Offset= 10 (694) */
/* 686 */	0x8,		/* FC_LONG */
			0x8,		/* FC_LONG */
/* 688 */	0x4c,		/* FC_EMBEDDED_COMPLEX */
			0x0,		/* 0 */
/* 690 */	NdrFcShort( 0xffd6 ),	/* Offset= -42 (648) */
/* 692 */	0x36,		/* FC_POINTER */
			0x5b,		/* FC_END */
/* 694 */	
			0x13, 0x0,	/* FC_OP */
/* 696 */	NdrFcShort( 0xffe2 ),	/* Offset= -30 (666) */
/* 698 */	
			0x21,		/* FC_BOGUS_ARRAY */
			0x3,		/* 3 */
/* 700 */	NdrFcShort( 0x0 ),	/* 0 */
/* 702 */	0x19,		/* Corr desc:  field pointer, FC_ULONG */
			0x0,		/*  */
/* 704 */	NdrFcShort( 0x0 ),	/* 0 */
/* 706 */	NdrFcShort( 0x1 ),	/* Corr flags:  early, */
/* 708 */	NdrFcLong( 0xffffffff ),	/* -1 */
/* 712 */	NdrFcShort( 0x0 ),	/* Corr flags:  */
/* 714 */	
			0x13, 0x0,	/* FC_OP */
/* 716 */	NdrFcShort( 0xffda ),	/* Offset= -38 (678) */
/* 718 */	0x5c,		/* FC_PAD */
			0x5b,		/* FC_END */
/* 720 */	
			0x1a,		/* FC_BOGUS_STRUCT */
			0x3,		/* 3 */
/* 722 */	NdrFcShort( 0x10 ),	/* 16 */
/* 724 */	NdrFcShort( 0x0 ),	/* 0 */
/* 726 */	NdrFcShort( 0x6 ),	/* Offset= 6 (732) */
/* 728 */	0x8,		/* FC_LONG */
			0x40,		/* FC_STRUCTPAD4 */
/* 730 */	0x36,		/* FC_POINTER */
			0x5b,		/* FC_END */
/* 732 */	
			0x11, 0x0,	/* FC_RP */
/* 734 */	NdrFcShort( 0xffdc ),	/* Offset= -36 (698) */
/* 736 */	
			0x1d,		/* FC_SMFARRAY */
			0x0,		/* 0 */
/* 738 */	NdrFcShort( 0x8 ),	/* 8 */
/* 740 */	0x1,		/* FC_BYTE */
			0x5b,		/* FC_END */
/* 742 */	
			0x15,		/* FC_STRUCT */
			0x3,		/* 3 */
/* 744 */	NdrFcShort( 0x10 ),	/* 16 */
/* 746 */	0x8,		/* FC_LONG */
			0x6,		/* FC_SHORT */
/* 748 */	0x6,		/* FC_SHORT */
			0x4c,		/* FC_EMBEDDED_COMPLEX */
/* 750 */	0x0,		/* 0 */
			NdrFcShort( 0xfff1 ),	/* Offset= -15 (736) */
			0x5b,		/* FC_END */
/* 754 */	
			0x1a,		/* FC_BOGUS_STRUCT */
			0x3,		/* 3 */
/* 756 */	NdrFcShort( 0x20 ),	/* 32 */
/* 758 */	NdrFcShort( 0x0 ),	/* 0 */
/* 760 */	NdrFcShort( 0xa ),	/* Offset= 10 (770) */
/* 762 */	0x8,		/* FC_LONG */
			0x40,		/* FC_STRUCTPAD4 */
/* 764 */	0x36,		/* FC_POINTER */
			0x4c,		/* FC_EMBEDDED_COMPLEX */
/* 766 */	0x0,		/* 0 */
			NdrFcShort( 0xffe7 ),	/* Offset= -25 (742) */
			0x5b,		/* FC_END */
/* 770 */	
			0x11, 0x0,	/* FC_RP */
/* 772 */	NdrFcShort( 0xff12 ),	/* Offset= -238 (534) */
/* 774 */	
			0x1b,		/* FC_CARRAY */
			0x0,		/* 0 */
/* 776 */	NdrFcShort( 0x1 ),	/* 1 */
/* 778 */	0x19,		/* Corr desc:  field pointer, FC_ULONG */
			0x0,		/*  */
/* 780 */	NdrFcShort( 0x0 ),	/* 0 */
/* 782 */	NdrFcShort( 0x1 ),	/* Corr flags:  early, */
/* 784 */	0x1,		/* FC_BYTE */
			0x5b,		/* FC_END */
/* 786 */	
			0x1a,		/* FC_BOGUS_STRUCT */
			0x3,		/* 3 */
/* 788 */	NdrFcShort( 0x10 ),	/* 16 */
/* 790 */	NdrFcShort( 0x0 ),	/* 0 */
/* 792 */	NdrFcShort( 0x6 ),	/* Offset= 6 (798) */
/* 794 */	0x8,		/* FC_LONG */
			0x40,		/* FC_STRUCTPAD4 */
/* 796 */	0x36,		/* FC_POINTER */
			0x5b,		/* FC_END */
/* 798 */	
			0x13, 0x0,	/* FC_OP */
/* 800 */	NdrFcShort( 0xffe6 ),	/* Offset= -26 (774) */
/* 802 */	
			0x1b,		/* FC_CARRAY */
			0x1,		/* 1 */
/* 804 */	NdrFcShort( 0x2 ),	/* 2 */
/* 806 */	0x19,		/* Corr desc:  field pointer, FC_ULONG */
			0x0,		/*  */
/* 808 */	NdrFcShort( 0x0 ),	/* 0 */
/* 810 */	NdrFcShort( 0x1 ),	/* Corr flags:  early, */
/* 812 */	0x6,		/* FC_SHORT */
			0x5b,		/* FC_END */
/* 814 */	
			0x1a,		/* FC_BOGUS_STRUCT */
			0x3,		/* 3 */
/* 816 */	NdrFcShort( 0x10 ),	/* 16 */
/* 818 */	NdrFcShort( 0x0 ),	/* 0 */
/* 820 */	NdrFcShort( 0x6 ),	/* Offset= 6 (826) */
/* 822 */	0x8,		/* FC_LONG */
			0x40,		/* FC_STRUCTPAD4 */
/* 824 */	0x36,		/* FC_POINTER */
			0x5b,		/* FC_END */
/* 826 */	
			0x13, 0x0,	/* FC_OP */
/* 828 */	NdrFcShort( 0xffe6 ),	/* Offset= -26 (802) */
/* 830 */	
			0x1b,		/* FC_CARRAY */
			0x3,		/* 3 */
/* 832 */	NdrFcShort( 0x4 ),	/* 4 */
/* 834 */	0x19,		/* Corr desc:  field pointer, FC_ULONG */
			0x0,		/*  */
/* 836 */	NdrFcShort( 0x0 ),	/* 0 */
/* 838 */	NdrFcShort( 0x1 ),	/* Corr flags:  early, */
/* 840 */	0x8,		/* FC_LONG */
			0x5b,		/* FC_END */
/* 842 */	
			0x1a,		/* FC_BOGUS_STRUCT */
			0x3,		/* 3 */
/* 844 */	NdrFcShort( 0x10 ),	/* 16 */
/* 846 */	NdrFcShort( 0x0 ),	/* 0 */
/* 848 */	NdrFcShort( 0x6 ),	/* Offset= 6 (854) */
/* 850 */	0x8,		/* FC_LONG */
			0x40,		/* FC_STRUCTPAD4 */
/* 852 */	0x36,		/* FC_POINTER */
			0x5b,		/* FC_END */
/* 854 */	
			0x13, 0x0,	/* FC_OP */
/* 856 */	NdrFcShort( 0xffe6 ),	/* Offset= -26 (830) */
/* 858 */	
			0x1b,		/* FC_CARRAY */
			0x7,		/* 7 */
/* 860 */	NdrFcShort( 0x8 ),	/* 8 */
/* 862 */	0x19,		/* Corr desc:  field pointer, FC_ULONG */
			0x0,		/*  */
/* 864 */	NdrFcShort( 0x0 ),	/* 0 */
/* 866 */	NdrFcShort( 0x1 ),	/* Corr flags:  early, */
/* 868 */	0xb,		/* FC_HYPER */
			0x5b,		/* FC_END */
/* 870 */	
			0x1a,		/* FC_BOGUS_STRUCT */
			0x3,		/* 3 */
/* 872 */	NdrFcShort( 0x10 ),	/* 16 */
/* 874 */	NdrFcShort( 0x0 ),	/* 0 */
/* 876 */	NdrFcShort( 0x6 ),	/* Offset= 6 (882) */
/* 878 */	0x8,		/* FC_LONG */
			0x40,		/* FC_STRUCTPAD4 */
/* 880 */	0x36,		/* FC_POINTER */
			0x5b,		/* FC_END */
/* 882 */	
			0x13, 0x0,	/* FC_OP */
/* 884 */	NdrFcShort( 0xffe6 ),	/* Offset= -26 (858) */
/* 886 */	
			0x15,		/* FC_STRUCT */
			0x3,		/* 3 */
/* 888 */	NdrFcShort( 0x8 ),	/* 8 */
/* 890 */	0x8,		/* FC_LONG */
			0x8,		/* FC_LONG */
/* 892 */	0x5c,		/* FC_PAD */
			0x5b,		/* FC_END */
/* 894 */	
			0x1b,		/* FC_CARRAY */
			0x3,		/* 3 */
/* 896 */	NdrFcShort( 0x8 ),	/* 8 */
/* 898 */	0x7,		/* Corr desc: FC_USHORT */
			0x0,		/*  */
/* 900 */	NdrFcShort( 0xffc8 ),	/* -56 */
/* 902 */	NdrFcShort( 0x1 ),	/* Corr flags:  early, */
/* 904 */	0x4c,		/* FC_EMBEDDED_COMPLEX */
			0x0,		/* 0 */
/* 906 */	NdrFcShort( 0xffec ),	/* Offset= -20 (886) */
/* 908 */	0x5c,		/* FC_PAD */
			0x5b,		/* FC_END */
/* 910 */	
			0x1a,		/* FC_BOGUS_STRUCT */
			0x3,		/* 3 */
/* 912 */	NdrFcShort( 0x38 ),	/* 56 */
/* 914 */	NdrFcShort( 0xffec ),	/* Offset= -20 (894) */
/* 916 */	NdrFcShort( 0x0 ),	/* Offset= 0 (916) */
/* 918 */	0x6,		/* FC_SHORT */
			0x6,		/* FC_SHORT */
/* 920 */	0x8,		/* FC_LONG */
			0x8,		/* FC_LONG */
/* 922 */	0x40,		/* FC_STRUCTPAD4 */
			0x4c,		/* FC_EMBEDDED_COMPLEX */
/* 924 */	0x0,		/* 0 */
			NdrFcShort( 0xfe0f ),	/* Offset= -497 (428) */
			0x5b,		/* FC_END */
/* 928 */	
			0x13, 0x0,	/* FC_OP */
/* 930 */	NdrFcShort( 0xff04 ),	/* Offset= -252 (678) */
/* 932 */	
			0x13, 0x8,	/* FC_OP [simple_pointer] */
/* 934 */	0x1,		/* FC_BYTE */
			0x5c,		/* FC_PAD */
/* 936 */	
			0x13, 0x8,	/* FC_OP [simple_pointer] */
/* 938 */	0x6,		/* FC_SHORT */
			0x5c,		/* FC_PAD */
/* 940 */	
			0x13, 0x8,	/* FC_OP [simple_pointer] */
/* 942 */	0x8,		/* FC_LONG */
			0x5c,		/* FC_PAD */
/* 944 */	
			0x13, 0x8,	/* FC_OP [simple_pointer] */
/* 946 */	0xb,		/* FC_HYPER */
			0x5c,		/* FC_PAD */
/* 948 */	
			0x13, 0x8,	/* FC_OP [simple_pointer] */
/* 950 */	0xa,		/* FC_FLOAT */
			0x5c,		/* FC_PAD */
/* 952 */	
			0x13, 0x8,	/* FC_OP [simple_pointer] */
/* 954 */	0xc,		/* FC_DOUBLE */
			0x5c,		/* FC_PAD */
/* 956 */	
			0x13, 0x0,	/* FC_OP */
/* 958 */	NdrFcShort( 0xfdce ),	/* Offset= -562 (396) */
/* 960 */	
			0x13, 0x10,	/* FC_OP [pointer_deref] */
/* 962 */	NdrFcShort( 0xfc82 ),	/* Offset= -894 (68) */
/* 964 */	
			0x13, 0x10,	/* FC_OP [pointer_deref] */
/* 966 */	NdrFcShort( 0xfdcc ),	/* Offset= -564 (402) */
/* 968 */	
			0x13, 0x10,	/* FC_OP [pointer_deref] */
/* 970 */	NdrFcShort( 0xfc64 ),	/* Offset= -924 (46) */
/* 972 */	
			0x13, 0x10,	/* FC_OP [pointer_deref] */
/* 974 */	NdrFcShort( 0xfdd6 ),	/* Offset= -554 (420) */
/* 976 */	
			0x13, 0x10,	/* FC_OP [pointer_deref] */
/* 978 */	NdrFcShort( 0x2 ),	/* Offset= 2 (980) */
/* 980 */	
			0x13, 0x0,	/* FC_OP */
/* 982 */	NdrFcShort( 0x14 ),	/* Offset= 20 (1002) */
/* 984 */	
			0x15,		/* FC_STRUCT */
			0x7,		/* 7 */
/* 986 */	NdrFcShort( 0x10 ),	/* 16 */
/* 988 */	0x6,		/* FC_SHORT */
			0x1,		/* FC_BYTE */
/* 990 */	0x1,		/* FC_BYTE */
			0x8,		/* FC_LONG */
/* 992 */	0xb,		/* FC_HYPER */
			0x5b,		/* FC_END */
/* 994 */	
			0x13, 0x0,	/* FC_OP */
/* 996 */	NdrFcShort( 0xfff4 ),	/* Offset= -12 (984) */
/* 998 */	
			0x13, 0x8,	/* FC_OP [simple_pointer] */
/* 1000 */	0x2,		/* FC_CHAR */
			0x5c,		/* FC_PAD */
/* 1002 */	
			0x1a,		/* FC_BOGUS_STRUCT */
			0x7,		/* 7 */
/* 1004 */	NdrFcShort( 0x20 ),	/* 32 */
/* 1006 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1008 */	NdrFcShort( 0x0 ),	/* Offset= 0 (1008) */
/* 1010 */	0x8,		/* FC_LONG */
			0x8,		/* FC_LONG */
/* 1012 */	0x6,		/* FC_SHORT */
			0x6,		/* FC_SHORT */
/* 1014 */	0x6,		/* FC_SHORT */
			0x6,		/* FC_SHORT */
/* 1016 */	0x4c,		/* FC_EMBEDDED_COMPLEX */
			0x0,		/* 0 */
/* 1018 */	NdrFcShort( 0xfc68 ),	/* Offset= -920 (98) */
/* 1020 */	0x5c,		/* FC_PAD */
			0x5b,		/* FC_END */
/* 1022 */	0xb4,		/* FC_USER_MARSHAL */
			0x83,		/* 131 */
/* 1024 */	NdrFcShort( 0x1 ),	/* 1 */
/* 1026 */	NdrFcShort( 0x18 ),	/* 24 */
/* 1028 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1030 */	NdrFcShort( 0xfc58 ),	/* Offset= -936 (94) */
/* 1032 */	
			0x11, 0x0,	/* FC_RP */
/* 1034 */	NdrFcShort( 0x6 ),	/* Offset= 6 (1040) */
/* 1036 */	
			0x12, 0x0,	/* FC_UP */
/* 1038 */	NdrFcShort( 0xffdc ),	/* Offset= -36 (1002) */
/* 1040 */	0xb4,		/* FC_USER_MARSHAL */
			0x83,		/* 131 */
/* 1042 */	NdrFcShort( 0x1 ),	/* 1 */
/* 1044 */	NdrFcShort( 0x18 ),	/* 24 */
/* 1046 */	NdrFcShort( 0x0 ),	/* 0 */
/* 1048 */	NdrFcShort( 0xfff4 ),	/* Offset= -12 (1036) */

			0x0
        }
    };

static const USER_MARSHAL_ROUTINE_QUADRUPLE UserMarshalRoutines[ WIRE_MARSHAL_TABLE_SIZE ] = 
        {
            
            {
            BSTR_UserSize
            ,BSTR_UserMarshal
            ,BSTR_UserUnmarshal
            ,BSTR_UserFree
            },
            {
            VARIANT_UserSize
            ,VARIANT_UserMarshal
            ,VARIANT_UserUnmarshal
            ,VARIANT_UserFree
            }

        };



/* Object interface: IUnknown, ver. 0.0,
   GUID={0x00000000,0x0000,0x0000,{0xC0,0x00,0x00,0x00,0x00,0x00,0x00,0x46}} */


/* Object interface: IDispatch, ver. 0.0,
   GUID={0x00020400,0x0000,0x0000,{0xC0,0x00,0x00,0x00,0x00,0x00,0x00,0x46}} */


/* Object interface: IServer, ver. 0.0,
   GUID={0x88E11081,0x5A40,0x4865,{0xB2,0xF7,0x1E,0x74,0x93,0x6E,0x51,0x55}} */

#pragma code_seg(".orpc")
static const unsigned short IServer_FormatStringOffsetTable[] =
    {
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    0,
    68,
    118,
    162,
    206,
    244,
    294,
    332,
    370,
    408,
    440,
    490
    };

static const MIDL_STUBLESS_PROXY_INFO IServer_ProxyInfo =
    {
    &Object_StubDesc,
    ComGRServer__MIDL_ProcFormatString.Format,
    &IServer_FormatStringOffsetTable[-3],
    0,
    0,
    0
    };


static const MIDL_SERVER_INFO IServer_ServerInfo = 
    {
    &Object_StubDesc,
    0,
    ComGRServer__MIDL_ProcFormatString.Format,
    &IServer_FormatStringOffsetTable[-3],
    0,
    0,
    0,
    0};
CINTERFACE_PROXY_VTABLE(19) _IServerProxyVtbl = 
{
    &IServer_ProxyInfo,
    &IID_IServer,
    IUnknown_QueryInterface_Proxy,
    IUnknown_AddRef_Proxy,
    IUnknown_Release_Proxy ,
    0 /* IDispatch::GetTypeInfoCount */ ,
    0 /* IDispatch::GetTypeInfo */ ,
    0 /* IDispatch::GetIDsOfNames */ ,
    0 /* IDispatch_Invoke_Proxy */ ,
    (void *) (INT_PTR) -1 /* IServer::Connect */ ,
    (void *) (INT_PTR) -1 /* IServer::Get */ ,
    (void *) (INT_PTR) -1 /* IServer::Delete */ ,
    (void *) (INT_PTR) -1 /* IServer::New */ ,
    (void *) (INT_PTR) -1 /* IServer::get_ErrorMessage */ ,
    (void *) (INT_PTR) -1 /* IServer::Report */ ,
    (void *) (INT_PTR) -1 /* IServer::Write */ ,
    (void *) (INT_PTR) -1 /* IServer::get_Timeout */ ,
    (void *) (INT_PTR) -1 /* IServer::put_Timeout */ ,
    (void *) (INT_PTR) -1 /* IServer::EndSession */ ,
    (void *) (INT_PTR) -1 /* IServer::ReportStrParam */ ,
    (void *) (INT_PTR) -1 /* IServer::Call */
};


static const PRPC_STUB_FUNCTION IServer_table[] =
{
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2
};

CInterfaceStubVtbl _IServerStubVtbl =
{
    &IID_IServer,
    &IServer_ServerInfo,
    19,
    &IServer_table[-3],
    CStdStubBuffer_DELEGATING_METHODS
};


/* Object interface: IGRObjCol, ver. 0.0,
   GUID={0xF664D4F1,0xB7F8,0x45A0,{0x85,0x1B,0xB6,0xC4,0xB7,0x04,0xB0,0x29}} */

#pragma code_seg(".orpc")
static const unsigned short IGRObjCol_FormatStringOffsetTable[] =
    {
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    540,
    584,
    622,
    660,
    698,
    736,
    774,
    812,
    370,
    850,
    888,
    926,
    958
    };

static const MIDL_STUBLESS_PROXY_INFO IGRObjCol_ProxyInfo =
    {
    &Object_StubDesc,
    ComGRServer__MIDL_ProcFormatString.Format,
    &IGRObjCol_FormatStringOffsetTable[-3],
    0,
    0,
    0
    };


static const MIDL_SERVER_INFO IGRObjCol_ServerInfo = 
    {
    &Object_StubDesc,
    0,
    ComGRServer__MIDL_ProcFormatString.Format,
    &IGRObjCol_FormatStringOffsetTable[-3],
    0,
    0,
    0,
    0};
CINTERFACE_PROXY_VTABLE(20) _IGRObjColProxyVtbl = 
{
    &IGRObjCol_ProxyInfo,
    &IID_IGRObjCol,
    IUnknown_QueryInterface_Proxy,
    IUnknown_AddRef_Proxy,
    IUnknown_Release_Proxy ,
    0 /* IDispatch::GetTypeInfoCount */ ,
    0 /* IDispatch::GetTypeInfo */ ,
    0 /* IDispatch::GetIDsOfNames */ ,
    0 /* IDispatch_Invoke_Proxy */ ,
    (void *) (INT_PTR) -1 /* IGRObjCol::Get */ ,
    (void *) (INT_PTR) -1 /* IGRObjCol::New */ ,
    (void *) (INT_PTR) -1 /* IGRObjCol::Write */ ,
    (void *) (INT_PTR) -1 /* IGRObjCol::Replace */ ,
    (void *) (INT_PTR) -1 /* IGRObjCol::Delete */ ,
    (void *) (INT_PTR) -1 /* IGRObjCol::get_Count */ ,
    (void *) (INT_PTR) -1 /* IGRObjCol::get_Fields */ ,
    (void *) (INT_PTR) -1 /* IGRObjCol::get_KeyFields */ ,
    (void *) (INT_PTR) -1 /* IGRObjCol::RemoveObject */ ,
    (void *) (INT_PTR) -1 /* IGRObjCol::get_Type */ ,
    (void *) (INT_PTR) -1 /* IGRObjCol::put_Type */ ,
    (void *) (INT_PTR) -1 /* IGRObjCol::WriteDirect */ ,
    (void *) (INT_PTR) -1 /* IGRObjCol::ReplaceDirect */
};


static const PRPC_STUB_FUNCTION IGRObjCol_table[] =
{
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2
};

CInterfaceStubVtbl _IGRObjColStubVtbl =
{
    &IID_IGRObjCol,
    &IGRObjCol_ServerInfo,
    20,
    &IGRObjCol_table[-3],
    CStdStubBuffer_DELEGATING_METHODS
};


/* Object interface: IGRObject, ver. 0.0,
   GUID={0x8A4455B1,0xBA69,0x40D8,{0xA0,0x9E,0x28,0xD0,0x25,0x9E,0xE3,0xCC}} */

#pragma code_seg(".orpc")
static const unsigned short IGRObject_FormatStringOffsetTable[] =
    {
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    996,
    1040,
    1084,
    1116,
    1160
    };

static const MIDL_STUBLESS_PROXY_INFO IGRObject_ProxyInfo =
    {
    &Object_StubDesc,
    ComGRServer__MIDL_ProcFormatString.Format,
    &IGRObject_FormatStringOffsetTable[-3],
    0,
    0,
    0
    };


static const MIDL_SERVER_INFO IGRObject_ServerInfo = 
    {
    &Object_StubDesc,
    0,
    ComGRServer__MIDL_ProcFormatString.Format,
    &IGRObject_FormatStringOffsetTable[-3],
    0,
    0,
    0,
    0};
CINTERFACE_PROXY_VTABLE(12) _IGRObjectProxyVtbl = 
{
    &IGRObject_ProxyInfo,
    &IID_IGRObject,
    IUnknown_QueryInterface_Proxy,
    IUnknown_AddRef_Proxy,
    IUnknown_Release_Proxy ,
    0 /* IDispatch::GetTypeInfoCount */ ,
    0 /* IDispatch::GetTypeInfo */ ,
    0 /* IDispatch::GetIDsOfNames */ ,
    0 /* IDispatch_Invoke_Proxy */ ,
    (void *) (INT_PTR) -1 /* IGRObject::Get */ ,
    (void *) (INT_PTR) -1 /* IGRObject::Set */ ,
    (void *) (INT_PTR) -1 /* IGRObject::Delete */ ,
    (void *) (INT_PTR) -1 /* IGRObject::DateToString */ ,
    (void *) (INT_PTR) -1 /* IGRObject::DateFromString */
};


static const PRPC_STUB_FUNCTION IGRObject_table[] =
{
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2
};

CInterfaceStubVtbl _IGRObjectStubVtbl =
{
    &IID_IGRObject,
    &IGRObject_ServerInfo,
    12,
    &IGRObject_table[-3],
    CStdStubBuffer_DELEGATING_METHODS
};


/* Object interface: ICollection, ver. 0.0,
   GUID={0xE8604670,0xE799,0x4BED,{0x85,0x35,0xF0,0xFA,0xFF,0x6B,0x88,0xCA}} */

#pragma code_seg(".orpc")
static const unsigned short ICollection_FormatStringOffsetTable[] =
    {
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    1204,
    1242
    };

static const MIDL_STUBLESS_PROXY_INFO ICollection_ProxyInfo =
    {
    &Object_StubDesc,
    ComGRServer__MIDL_ProcFormatString.Format,
    &ICollection_FormatStringOffsetTable[-3],
    0,
    0,
    0
    };


static const MIDL_SERVER_INFO ICollection_ServerInfo = 
    {
    &Object_StubDesc,
    0,
    ComGRServer__MIDL_ProcFormatString.Format,
    &ICollection_FormatStringOffsetTable[-3],
    0,
    0,
    0,
    0};
CINTERFACE_PROXY_VTABLE(9) _ICollectionProxyVtbl = 
{
    &ICollection_ProxyInfo,
    &IID_ICollection,
    IUnknown_QueryInterface_Proxy,
    IUnknown_AddRef_Proxy,
    IUnknown_Release_Proxy ,
    0 /* IDispatch::GetTypeInfoCount */ ,
    0 /* IDispatch::GetTypeInfo */ ,
    0 /* IDispatch::GetIDsOfNames */ ,
    0 /* IDispatch_Invoke_Proxy */ ,
    (void *) (INT_PTR) -1 /* ICollection::get_Count */ ,
    (void *) (INT_PTR) -1 /* ICollection::Get */
};


static const PRPC_STUB_FUNCTION ICollection_table[] =
{
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    NdrStubCall2,
    NdrStubCall2
};

CInterfaceStubVtbl _ICollectionStubVtbl =
{
    &IID_ICollection,
    &ICollection_ServerInfo,
    9,
    &ICollection_table[-3],
    CStdStubBuffer_DELEGATING_METHODS
};


/* Object interface: IField, ver. 0.0,
   GUID={0xC13B1B38,0x90FF,0x4768,{0x85,0x60,0xB8,0x51,0x3C,0xD0,0x40,0x41}} */

#pragma code_seg(".orpc")
static const unsigned short IField_FormatStringOffsetTable[] =
    {
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    1286,
    1324,
    1362
    };

static const MIDL_STUBLESS_PROXY_INFO IField_ProxyInfo =
    {
    &Object_StubDesc,
    ComGRServer__MIDL_ProcFormatString.Format,
    &IField_FormatStringOffsetTable[-3],
    0,
    0,
    0
    };


static const MIDL_SERVER_INFO IField_ServerInfo = 
    {
    &Object_StubDesc,
    0,
    ComGRServer__MIDL_ProcFormatString.Format,
    &IField_FormatStringOffsetTable[-3],
    0,
    0,
    0,
    0};
CINTERFACE_PROXY_VTABLE(10) _IFieldProxyVtbl = 
{
    &IField_ProxyInfo,
    &IID_IField,
    IUnknown_QueryInterface_Proxy,
    IUnknown_AddRef_Proxy,
    IUnknown_Release_Proxy ,
    0 /* IDispatch::GetTypeInfoCount */ ,
    0 /* IDispatch::GetTypeInfo */ ,
    0 /* IDispatch::GetIDsOfNames */ ,
    0 /* IDispatch_Invoke_Proxy */ ,
    (void *) (INT_PTR) -1 /* IField::get_Name */ ,
    (void *) (INT_PTR) -1 /* IField::get_Type */ ,
    (void *) (INT_PTR) -1 /* IField::get_ChildObject */
};


static const PRPC_STUB_FUNCTION IField_table[] =
{
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2
};

CInterfaceStubVtbl _IFieldStubVtbl =
{
    &IID_IField,
    &IField_ServerInfo,
    10,
    &IField_table[-3],
    CStdStubBuffer_DELEGATING_METHODS
};


/* Object interface: IBinaryField, ver. 0.0,
   GUID={0xA8BA8CD7,0xC512,0x4EA4,{0x86,0x11,0x33,0x17,0x90,0x28,0xAA,0x63}} */

#pragma code_seg(".orpc")
static const unsigned short IBinaryField_FormatStringOffsetTable[] =
    {
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    1400,
    1324,
    622,
    1438
    };

static const MIDL_STUBLESS_PROXY_INFO IBinaryField_ProxyInfo =
    {
    &Object_StubDesc,
    ComGRServer__MIDL_ProcFormatString.Format,
    &IBinaryField_FormatStringOffsetTable[-3],
    0,
    0,
    0
    };


static const MIDL_SERVER_INFO IBinaryField_ServerInfo = 
    {
    &Object_StubDesc,
    0,
    ComGRServer__MIDL_ProcFormatString.Format,
    &IBinaryField_FormatStringOffsetTable[-3],
    0,
    0,
    0,
    0};
CINTERFACE_PROXY_VTABLE(11) _IBinaryFieldProxyVtbl = 
{
    &IBinaryField_ProxyInfo,
    &IID_IBinaryField,
    IUnknown_QueryInterface_Proxy,
    IUnknown_AddRef_Proxy,
    IUnknown_Release_Proxy ,
    0 /* IDispatch::GetTypeInfoCount */ ,
    0 /* IDispatch::GetTypeInfo */ ,
    0 /* IDispatch::GetIDsOfNames */ ,
    0 /* IDispatch_Invoke_Proxy */ ,
    (void *) (INT_PTR) -1 /* IBinaryField::Write */ ,
    (void *) (INT_PTR) -1 /* IBinaryField::get_Size */ ,
    (void *) (INT_PTR) -1 /* IBinaryField::Read */ ,
    (void *) (INT_PTR) -1 /* IBinaryField::SetFrom */
};


static const PRPC_STUB_FUNCTION IBinaryField_table[] =
{
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2,
    NdrStubCall2
};

CInterfaceStubVtbl _IBinaryFieldStubVtbl =
{
    &IID_IBinaryField,
    &IBinaryField_ServerInfo,
    11,
    &IBinaryField_table[-3],
    CStdStubBuffer_DELEGATING_METHODS
};

static const MIDL_STUB_DESC Object_StubDesc = 
    {
    0,
    NdrOleAllocate,
    NdrOleFree,
    0,
    0,
    0,
    0,
    0,
    ComGRServer__MIDL_TypeFormatString.Format,
    1, /* -error bounds_check flag */
    0x50002, /* Ndr library version */
    0,
    0x801026e, /* MIDL Version 8.1.622 */
    0,
    UserMarshalRoutines,
    0,  /* notify & notify_flag routine table */
    0x1, /* MIDL flag */
    0, /* cs routines */
    0,   /* proxy/server info */
    0
    };

const CInterfaceProxyVtbl * const _ComGRServer_ProxyVtblList[] = 
{
    ( CInterfaceProxyVtbl *) &_IFieldProxyVtbl,
    ( CInterfaceProxyVtbl *) &_ICollectionProxyVtbl,
    ( CInterfaceProxyVtbl *) &_IServerProxyVtbl,
    ( CInterfaceProxyVtbl *) &_IGRObjectProxyVtbl,
    ( CInterfaceProxyVtbl *) &_IBinaryFieldProxyVtbl,
    ( CInterfaceProxyVtbl *) &_IGRObjColProxyVtbl,
    0
};

const CInterfaceStubVtbl * const _ComGRServer_StubVtblList[] = 
{
    ( CInterfaceStubVtbl *) &_IFieldStubVtbl,
    ( CInterfaceStubVtbl *) &_ICollectionStubVtbl,
    ( CInterfaceStubVtbl *) &_IServerStubVtbl,
    ( CInterfaceStubVtbl *) &_IGRObjectStubVtbl,
    ( CInterfaceStubVtbl *) &_IBinaryFieldStubVtbl,
    ( CInterfaceStubVtbl *) &_IGRObjColStubVtbl,
    0
};

PCInterfaceName const _ComGRServer_InterfaceNamesList[] = 
{
    "IField",
    "ICollection",
    "IServer",
    "IGRObject",
    "IBinaryField",
    "IGRObjCol",
    0
};

const IID *  const _ComGRServer_BaseIIDList[] = 
{
    &IID_IDispatch,
    &IID_IDispatch,
    &IID_IDispatch,
    &IID_IDispatch,
    &IID_IDispatch,
    &IID_IDispatch,
    0
};


#define _ComGRServer_CHECK_IID(n)	IID_GENERIC_CHECK_IID( _ComGRServer, pIID, n)

int __stdcall _ComGRServer_IID_Lookup( const IID * pIID, int * pIndex )
{
    IID_BS_LOOKUP_SETUP

    IID_BS_LOOKUP_INITIAL_TEST( _ComGRServer, 6, 4 )
    IID_BS_LOOKUP_NEXT_TEST( _ComGRServer, 2 )
    IID_BS_LOOKUP_NEXT_TEST( _ComGRServer, 1 )
    IID_BS_LOOKUP_RETURN_RESULT( _ComGRServer, 6, *pIndex )
    
}

const ExtendedProxyFileInfo ComGRServer_ProxyFileInfo = 
{
    (PCInterfaceProxyVtblList *) & _ComGRServer_ProxyVtblList,
    (PCInterfaceStubVtblList *) & _ComGRServer_StubVtblList,
    (const PCInterfaceName * ) & _ComGRServer_InterfaceNamesList,
    (const IID ** ) & _ComGRServer_BaseIIDList,
    & _ComGRServer_IID_Lookup, 
    6,
    2,
    0, /* table of [async_uuid] interfaces */
    0, /* Filler1 */
    0, /* Filler2 */
    0  /* Filler3 */
};
#if _MSC_VER >= 1200
#pragma warning(pop)
#endif


#endif /* defined(_M_AMD64)*/


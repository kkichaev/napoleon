/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * MemberFormat & Member decl
 *
 * ert   16/09/2009   creating
 */ 
#ifndef __GR_SERVER_MEMBER_H
#define __GR_SERVER_MEMBER_H

#include <Binary.h>

namespace GRServer {

struct MemberFormat
{
   enum MemberType { mtNone, mtString, mtNumber, mtDateTime, mtObject, mtBinary };
   enum Flags { Hidden = 1, ExecOnPut = 2, ExecOnGet = 4, HiddenPut = 8, CanCreate = 0x10 };

   MemberFormat() : flags(0) { format.dateFormat = Stamp; }

   MemberFormat(const MemberFormat& src)
   {
      name = src.name;
      type = src.type;
      flags = src.flags;
      format = src.format;
   }

   std::wstring name;
   MemberType   type;

   enum DateFormat { Stamp, Date, Time };
   union
   {
      WORD fraction; // 0 - целое число
      DateFormat dateFormat;
   } format;

   DWORD flags;

	bool Equals(const MemberFormat& cmp) const
	{
		return (name.compare(cmp.name) == 0 && type == cmp.type && format.dateFormat == cmp.format.dateFormat);
	}
};

class ServObject;
class MemoryBinary : public IBinary
{
public:
   MemoryBinary() : b(NULL) {}
   MemoryBinary(Binary* _b) : b(_b) {}
   ~MemoryBinary() { Close(); }

   virtual void Assign(Binary* _b) { delete b; b = _b; }

   virtual DWORD Size() const { return (b == NULL) ? 0 : b->Size(); }
   virtual const BYTE* Bytes() const { return (b == NULL) ? (const BYTE*)NULL : (const BYTE*)*b; }

   virtual void Close()
   {
      if( b != NULL )
      {
         delete b;
         b = NULL;
      }
   }

protected:
   Binary* b;
};

class CString : protected std::wstring
{
public:
   CString() {}
   CString(const std::wstring& _src) : std::wstring(_src) {}
	CString(const wchar_t* src) : std::wstring(src) {}
	CString(const wchar_t* src, size_type len) : std::wstring(src, len) {}
	virtual ~CString() {}

	CString& assign(const CString& src) { return assign((const std::wstring&)src); }
	virtual CString& assign(const std::wstring& _src) { std::wstring::assign(_src); return *this; }
	virtual CString& assign(const wchar_t *_src) { std::wstring::assign(_src); return *this; }

	CString& append(const CString& src) { return append((const std::wstring&)src); }
	virtual CString& append(const std::wstring& _src) { std::wstring::append(_src); return *this; }
	virtual CString& append(wchar_t sym) { std::wstring::append(1, sym); return *this; }
	virtual CString& append(const wchar_t *_src, unsigned len) { std::wstring::append(_src, len); return *this; }
	virtual CString& append(const wchar_t *_src) { std::wstring::append(_src); return *this; }

   const wchar_t* c_str() const { return std::wstring::c_str(); }

   bool empty() const { return std::wstring::empty(); }

   int compare(const CString& src) const { return compare((const std::wstring&)src); }
   int compare(const std::wstring& src) const { return std::wstring::compare(src); }
	int compare(const wchar_t* src) const { return wcscmp(c_str(), src); }

   virtual void clear() { std::wstring::clear(); }

   size_t size() const { return std::wstring::size(); }
   size_t find(const wchar_t sym) const { return std::wstring::find(sym); }
   std::wstring substr(size_t off, size_t count = npos) const { return std::wstring::substr(off, count); }

   size_t find_last_of(wchar_t sym) const { return std::wstring::find_last_of(sym); }
};

union Member
{
   double        number;
   FILETIME      datetime;
   //std::wstring *str;
   CString      *str;
   ServObject   *object;
   IBinary      *binary; // m.b. NULL если len == 0
};

} // namespace GRServer

#endif
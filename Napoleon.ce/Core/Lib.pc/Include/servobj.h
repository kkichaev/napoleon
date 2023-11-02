/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Server object.
 *
 * ert   16/09/2009   creating
 */
#ifndef __GR_SERVER_OBJECT_H
#define __GR_SERVER_OBJECT_H

#include <string>
#include <list>
#include <vector>
#include <set>
#include <map>

#include <Binary.h>
#include <pstream.h>
#include <OutStream.h>

#include <ierrlog.h>

#include "member.h"

#ifdef UNIX
#include <algorithm>
#endif

namespace GRServer {

class Socket;
class FormatList;
class IServerConfig;

typedef __int64 RowID;
const RowID NO_ROWID = 0;
typedef std::vector<RowID> RID_LIST;


template <class T> class Pointer
{
public:
   Pointer() : p(NULL) {}
   Pointer(T* _pp) : p(_pp) {}
   Pointer(const Pointer& _pp)
   {
      p = _pp.p;
      const_cast<Pointer&>(_pp).p = NULL;
   }

   Pointer& operator= (const Pointer& _pp)
   {
      p = _pp.p;
      const_cast<Pointer&>(_pp).p = NULL;
      return *this;
   }

   bool operator< (const Pointer& _pp) const
   {
      return (*p) < (*_pp.p);
   }

   operator T* () { return p; }
   operator const T* () const { return p; }

   T* operator ->() { return p; }
   const T* operator ->() const { return p; }

   ~Pointer()
   {
      delete p;
   }

   T* p;
};

class Format : public std::vector<MemberFormat>
{
public:
   Format() {}
   Format(const Format& fmt)
   {
      name = fmt.name;
      this->assign(fmt.begin(), fmt.end());
   }

   Format(const std::wstring& _name) : name(_name) {}

   virtual ~Format();

   std::wstring name;

   bool operator< (const Format& item) const { return (name.compare(item.name) < 0); }

   bool Read(ParseStreamU& stream, FormatList *fmtList);
   void ToString(OutStream *str, const FormatList *fmtList) const;

   int FindMember(const wchar_t* name, bool ignoreCase = true) const;

   void MembersToString(OutStream *str, const FormatList *fmtList) const;

protected:
   virtual bool ReadMembers(ParseStreamU& stream, FormatList *fmtList);
   bool ReadType(MemberFormat *type, ParseStreamU& stream);
};

struct IFormatHolder
{
   virtual Format* GetFormat(const std::wstring& name) const = 0;
};

class FormatList : public std::vector<Pointer<Format> >, public IFormatHolder
{
public:
   FormatList() : formatHolder(NULL), cryptData(NULL) {}
   virtual ~FormatList();

   virtual Format* GetFormat(const std::wstring& name) const;
   void AddFormat(Format* format, bool pushFront);

   virtual Format* NewFormat(const std::wstring& name) const { return new Format(name); }

   void SetHolder(IFormatHolder *fh) { formatHolder = fh; }

   BYTE* cryptData;

protected:
   IFormatHolder *formatHolder;
};

class Object : protected std::vector<Member>
{
public:
   typedef std::vector<Member> Base;
   typedef Base::iterator iterator;
   typedef Base::const_iterator const_iterator;

   struct Field
   {
      Field(const MemberFormat& mf, const Member& m) : format(mf), member(m) {}

      const MemberFormat& format;
      const Member& member;
   };

   virtual ~Object();

   static Object* Read(ParseStreamU& stream, const Format& fmt, FormatList *fmtList);

   // set all member to default values
   static Object* Create(const Format& fmt);

   bool ReadMembers(ParseStreamU& stream, FormatList *fmtList);
   void ToString(OutStream *str, const FormatList *fmtList) const;

   const Member* operator[](const wchar_t* name) const;
   Member* operator[](const wchar_t* name);

   const Field* GetField(const wchar_t* name) const;

   void Assign(const Member& member, const wchar_t* name);

   // vector function
   const_reference at(size_type _Pos) const { return Base::at(_Pos); }
   reference at(size_type _Pos) { return Base::at(_Pos); }

#ifdef UNIX
    iterator _Make_iter(const_iterator ci)
    {
        iterator i = begin();
        advance(i, distance(((const Object*)this)->begin(), ci));
        return i;
    }
#endif

   iterator begin() { return Base::begin(); }
   const_iterator begin() const { return Base::begin(); }

   iterator end() { return Base::end(); }
   const_iterator end() const { return Base::end(); }

   void clear() { erase(begin(), end()); }

   bool IsEmpty() const { return (size() == 0); }

    iterator erase(const iterator _First_arg, const iterator _Last_arg)
   {
      FreeMembers(_First_arg, _Last_arg);
      return Base::erase(_First_arg, _Last_arg);
   }

   iterator erase(const iterator _First_arg)
   {
      FreeMember(_First_arg);
      return Base::erase(_First_arg);
   }

   bool MoveTo(Object* dest);

   // format of the objects must be equal
   bool CopyFrom(const Object& ref, const std::vector<int>& indexes);

   void Copy(Object* dest) const;

   const Format& GetFormat() const { return format; }

	size_t Size() const;

protected:
   Object(const Format& fmt) : format(fmt) {}

   void FreeMembers(iterator s, iterator e);
   void FreeMember(iterator s);
   void FreeMember(iterator i, Format::const_iterator fi);

   // члены в m не удаляются, перед вызовом функции, считается что m не инициализированн
   bool ReadMember(Member* m, const MemberFormat& mf, ParseStreamU& stream, FormatList *fmtList);

public:
   const Format& format;
};


#include <locale.h>
class ServObject : public std::vector<Pointer<Object> >
{
// methods
public:
   ServObject() : format(NULL) {}
   ServObject(Format* fmt) : format(fmt) {}

   virtual ~ServObject() {}

   void ToString(OutStream *str, const FormatList *fmtList) const;
   void MembersToString(OutStream *str, const FormatList* fmtList) const;

   const std::wstring& Name() const { return format->name; }

   virtual Object* CreateObject() const { return Object::Create(*format); }
   virtual Object* AddObject();

   const Object& operator[](int index) const { return *at(index); }
   Object& operator[](int index) { return *at(index); }

   virtual bool Read(ParseStreamU& stream, FormatList *fmtList);
   virtual bool ReadObjects(ParseStreamU& stream, FormatList *fmtList);
   
   virtual void MoveValuesTo(ServObject *so);

   virtual void Copy(ServObject *dest) const;

	virtual void PrepareToString(const Object& obj) const {}
	virtual void AfterToString(const Object& obj) const {}

	static _locale_t GetLocale();
	static bool InitLocale();

   // members
public:
	Format* format;
};

struct IServObjectCreator
{
   virtual ServObject* Create(const std::wstring &name) = 0;
};

class ExchangeList : public std::vector<Pointer<ServObject> >
{
public:
   ExchangeList(FormatList* fl) : fmtList(fl), readError(NULL) {}
   virtual ~ExchangeList() {}

   void ToString(OutStream *str) const;
   // для чтения формата добавляем pushEmptyObjects
   bool Read(Socket* socket, DWORD timeout, HANDLE evStop, IServObjectCreator* creator, bool pushEmptyObjects = false);
   bool Write(Socket* socket);

   // может вызываться из dll
   virtual void RemoveTo(const_iterator i);
   virtual void EraseFront();

   FormatList* GetFormatList() const { return fmtList; }

	const char* readError;

protected:
   bool Read(ParseStreamU& stream, IServObjectCreator* creator, bool pushEmptyObjects = false);

   bool EncryptConnection(Socket* socket, Binary* pubKey);

   FormatList *fmtList;
};

class FileField
{
public:
   FileField(int srcIndex, int meIndex, const char* folder, IErrorLogger* logger);
   ~FileField() { Close(); }

   void Close();
   bool WriteFile(const Object& src);
   bool ReadFile(Object* dest) const;

	int GetMeIndex() const { return meIndex; }

protected:
   std::string folder;
   int srcIndex, meIndex;
   IErrorLogger* logger;
};

} // namespace GRServer

#endif


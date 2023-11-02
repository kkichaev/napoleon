/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * Интерфесы объекта
 *
 * ert   21/08/2010   creating
 */

#ifndef __IOBJECT_H
#define __IOBJECT_H

namespace GRServer {

//class CString : public std::wstring
//{
//public:
//   CString(const std::wstring& _src) : std::wstring(_src) {}
//   virtual ~CString() {}
//};
//
template<class T> class CVector : public std::vector<T>
{
public:
   virtual ~CVector() {}
};

class ParamList;
struct IObjectData
{
   struct Field
   {
      enum Flags { Hex = 1, File = 2 };

      MemberFormat format;
      std::wstring data; // или имя поля, или имя объекта
      std::wstring dataFormat; // формат объекта для чтения записи (пока актуально только для полей даты в XML)
      std::wstring src; // аттрибут src для полей типа file
      //std::wstring baseFolder; // аттрибут folder для полей типа file
      WORD width;

      std::wstring execStmt;
      DWORD flags;
      int pass;

      bool operator< (const Field& item) const { return (_wcsicmp(format.name.c_str(), item.format.name.c_str()) < 0); }

      bool CanCreate() const
      {
         if( (format.flags & (MemberFormat::ExecOnGet | MemberFormat::ExecOnPut)) == MemberFormat::ExecOnGet ||
            (format.flags & MemberFormat::HiddenPut) != 0 )
         {
            return false;
         }

         wchar_t sym = *format.name.begin();
         if( sym == L':' || sym == L'^' )
            return false;

         if( format.type == MemberFormat::mtObject || (format.type == MemberFormat::mtBinary && (flags & Field::File) != 0) )
            return false;

         return true;
      }
   };

   typedef std::map<std::wstring, std::wstring> Members;
	typedef std::vector<std::wstring> ValueList;
	typedef std::map<std::wstring, ValueList> MemberArray;
   typedef std::set<Field> Fields;

   std::wstring name;
   std::wstring tableName;
   std::wstring alias;
   Members members;
	MemberArray memberArray;
   Fields fields;
   std::wstring parent;

   DWORD flags;

   typedef void (*AddMember)(CString* text, const MemberFormat& member);

   virtual ~IObjectData() {}

   virtual const Field* FindField(const std::wstring& name) const = 0;
   virtual bool LoadFK(CVector<MemberFormat>** formats, CVector<Field> **fields = NULL) const = 0;
   virtual void CreateFKConstraint(CString** fktext, CString** indexText, const CVector<MemberFormat>& fields, wchar_t escape = L'"') const = 0;
   virtual const ParamList* InternalSourceParams() const = 0;
   virtual bool IsOrderedSource() const = 0;
};

struct IObject
{
};

} // namespace GRServer

#endif

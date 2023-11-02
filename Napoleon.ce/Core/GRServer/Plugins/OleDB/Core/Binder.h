/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB binder
 *
 * ert   23/06/2012   creating
 */

#pragma once

namespace GRServer {

struct BindData
{
   ULONG length;
   ULONG status;
   BYTE  data[1];
};

class FieldBinder
{
public:
   static FieldBinder* Create(const MemberFormat& format, int index);

   FieldBinder(const MemberFormat& format, int _index) : name(format.name), data(NULL), index(_index) {}

   virtual ~FieldBinder() {}

   virtual bool Bind(BindData* d, CManualAccessor& accessor, int ord)
   {
      data = d;
      accessor.AddBindEntry(ord, DataType(), DataLength(), &data->data, &data->length, &data->status);
      return true;
   }

   virtual bool BindParam(BindData* d, CManualAccessor& accessor, int ord)
   {
      data = d;
      accessor.AddParameterEntry(ord, DataType(), DataLength(), &data->data, &data->length, &data->status);
      return true;
   }

   ULONG FieldLength() const { return sizeof(BindData) - 1 + DataLength(); }
   
   const std::wstring& Name() const { return name; }

   virtual bool Read(Object* m)
   {
      if( data == NULL )
         return false;
      GetValue(&(m->at(index)));
      return true;
   }

   // parent нужен для записи Child объектов (FK)
   virtual bool Write(const Object& o) { return false; }

   // полный размер данных
   virtual ULONG DataLength() const = 0;
   virtual DBTYPEENUM DataType() const = 0;

   virtual void GetType(MemberFormat *type) const = 0;
   virtual void GetValue(Member* value) const = 0;

protected:
   std::wstring name;
   BindData* data;
   int index;
};

class BinderBase
{
public:
   virtual ~BinderBase() { Close(); }

   virtual void Close();

   virtual bool MoveNext(Object *parentObject) { return (data == NULL || reader.m_spRowset == NULL) ? false : (reader.MoveNext() == S_OK); }
   virtual bool Read(Object* o) const;

   const MemberFormat* FieldType(const wchar_t* name) const;
   const Member* Value(const wchar_t* name) const;

protected:
   BinderBase();
   bool CreateAccessor(ULONG bufSize);

protected:
   mutable MemberFormat format;
   mutable Member value;

   std::vector<FieldBinder*> fields;

   CCommand<CManualAccessor> reader;
   BindData* data;
};

class Binder : public BinderBase
{
public:
   Binder();

   virtual bool PrepareRead(const ISessionObject& obj, const std::wstring& filter, CSession& session);
   
   virtual bool Read(Object* o) const;

   virtual void Close();
   virtual HRESULT OpenReader(CSession& session, const std::wstring& stmt) { return reader.Open(session, stmt.c_str()); }

protected:
   bool PrepareReadStmt(ULONG* bufSize, std::wstring* stmt, const ISessionObject& obj);
   bool CreateReader(ULONG bufSize, const std::wstring& stmt, CSession& session);

protected:
   std::vector<FileField*> files;
};

class ParamBinder : public Binder
{
public:
   ParamBinder();

   virtual bool WriteParams(const Object& obj);
   virtual void Close();
   
   void AddParam(std::wstring* paramStmt, const MemberFormat& mf, int fldIndex);
   
   bool BindParams();

protected:
   std::vector<FieldBinder*> params;
   BindData* paramData;
   ULONG dataSize;
};

} // namespace GRServer
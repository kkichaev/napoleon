/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB binder
 *
 * ert   23/06/2012   creating
 */

#pragma once

#include <token.h>
#include <mutex_t.h>

namespace GRServer {

class FieldBinder
{
public:
   FieldBinder(const IObjectData::Field& format, int objPos, int bindPos);
   virtual ~FieldBinder() {}

   int BindPos() const { return bindPos; }
   void SetIOType(SQLSMALLINT newType) { inputOutputType = newType; }
   int ObjPos() const { return objPos; }

	virtual void WriteFrom(const Token& src) = 0;

   virtual void Read(Object* obj) = 0;
   virtual void Write(const Object& obj) = 0;

   virtual bool BindRead(HSTMT stmt) = 0;
   virtual bool BindWrite(HSTMT stmt) = 0;

   virtual void GetType(MemberFormat *type) const = 0;
   virtual void GetValue(Member* value) const = 0;
   
   virtual void GetData(SQLHSTMT hstmt, Member* value) = 0;
   virtual void PutData(SQLHSTMT hstmt, const Object& obj) = 0;

   const std::wstring& Name() const { return name; }

	bool IsNull() const { return ind == SQL_NULL_DATA; }

   static FieldBinder* OrderBinder(DWORD* index, int bindPos);

protected:
   int objPos;
   int bindPos;
   std::wstring name;

   SQLLEN ind;
   SQLSMALLINT inputOutputType;
};

class BinaryBinder : public FieldBinder
{
public:
   BinaryBinder(const IObjectData::Field& format, int objPos, int bindPos);

   virtual void Read(Object* obj);
   virtual void PutData(SQLHSTMT hstmt, const Object& obj);
   virtual bool BindWrite(HSTMT stmt);
   virtual void GetType(MemberFormat *type) const;

   virtual void Write(const Object& obj) {}
	virtual void WriteFrom(const Token& src) {}

   virtual bool BindRead(HSTMT stmt) { hstmt = stmt; return true; }

   virtual void GetValue(Member* value) const {}
   virtual void GetData(SQLHSTMT hstmt, Member* value) {}

protected:
   BYTE buf[1];
   int length;
   HSTMT hstmt;
};

class Binder
{
public:
   Binder();
   virtual ~Binder() { Close(); }

	virtual bool MoveNext(Object *parentObject);

   virtual void Close();

   virtual bool Read(Object* o) const;

   virtual const MemberFormat* FieldType(const wchar_t* name) const;
   virtual const Member* Value(const wchar_t* name) const;

   SQLHSTMT GetHSTMT() const { return (SQLHSTMT)hstmt; }

protected:
   mutable MemberFormat format;
   mutable Member value;
   mutable CString strValue;

   HSTMT hstmt;

	//int recCount;

   std::vector<FieldBinder*> fields;
   //std::vector<FileField*> files;

protected:
   bool BindFields();
};

class ReadBinder : public Binder
{
public:
   ReadBinder();

	virtual bool PrepareRead(std::wstring* stmt, const ISessionObject& obj, const std::wstring& filter, SQLHDBC hDbc, ODBCFlavor* flavor);
   
   bool OpenReader(SQLHDBC hDbc, const std::wstring& stmt);

protected:
   bool PrepareReadFields(std::wstring* fields, const ISessionObject& obj, ODBCFlavor* flavor);
   virtual bool ExecuteReader(const std::wstring& stmt) { return (SQLExecDirect(hstmt, (SQLWCHAR*)stmt.c_str(), SQL_NTS) == SQL_SUCCESS); }

};

class ParamBinder : public ReadBinder
{
public:
   ParamBinder();

   virtual bool WriteParams(const Object& obj);
   virtual void Close();
   
   void AddParam(std::wstring* paramStmt, FieldBinder* param);
   
   bool BindParams();

protected:
   std::vector<FieldBinder*> params;
};

void SetFileFieldBaseFolder(std::string* dest, const IObjectData::Field& src, const IServerConfig& config);

struct BindData
{
   ULONG length;
   ULONG status;
   BYTE  data[1];
};

class FieldBinderOle
{
public:
   static FieldBinderOle* Create(const MemberFormat& format, int index);

   FieldBinderOle(const MemberFormat& format, int _index) : name(format.name), data(NULL), index(_index) {}

   virtual ~FieldBinderOle() {}

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

   std::vector<FieldBinderOle*> fields;

   CCommand<CManualAccessor> reader;
   BindData* data;
};

class BinderOle : public BinderBase
{
public:
   BinderOle();

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

class ParamBinderOle : public BinderOle
{
public:
   ParamBinderOle();

   virtual bool WriteParams(const Object& obj);
   virtual void Close();
   
   void AddParam(std::wstring* paramStmt, const MemberFormat& mf, int fldIndex);
   
   bool BindParams();

protected:
   std::vector<FieldBinderOle*> params;
   BindData* paramData;
   ULONG dataSize;
};

} // namespace GRServer
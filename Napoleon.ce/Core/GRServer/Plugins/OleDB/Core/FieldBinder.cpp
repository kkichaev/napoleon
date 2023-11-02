/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   22/06/2012   creating
 */
#include "stdafx.h"
#include "OleSource.h"
#include "Binder.h"

using namespace GRServer;
using namespace std;

class StringBinder : public FieldBinder
{
public:
   StringBinder(const MemberFormat& format, int index) : FieldBinder(format, index) {}

   virtual DBTYPEENUM DataType() const { return DBTYPE_WSTR; }
   virtual ULONG DataLength() const { return (MAX_STRING_LENGTH+1) * sizeof(wchar_t); }

   virtual bool Write(const Object& o)
   {
      data->status = DBSTATUS_S_OK;
      data->length = 0;
      const Member& m = o.at(index);
      if( m.str != NULL )
      {
         data->length = m.str->size() * sizeof(wchar_t);
         wcscpy((wchar_t*)data->data, m.str->c_str());
      }
      return true;
   }

   virtual void GetType(MemberFormat *type) const
   {
      type->name = name;
      type->type = MemberFormat::mtString;
   }

   virtual void GetValue(Member* value) const
   {
      if( data != NULL )
      {
         if(data->status == DBSTATUS_S_ISNULL )
            value->str->clear();
         else
            value->str->assign((wchar_t*)data->data);
      }
   }
};

class NumberBinder : public FieldBinder
{
public:
   NumberBinder(const MemberFormat& format, int index) : FieldBinder(format, index) {}

   virtual DBTYPEENUM DataType() const { return DBTYPE_R8; }
   virtual ULONG DataLength() const { return sizeof(double); }

   virtual bool Write(const Object& o)
   {
      const Member& m = o.at(index);
      data->status = DBSTATUS_S_OK;
      *(double*)data->data = m.number;
      return true;
   }

   virtual void GetType(MemberFormat *type) const
   {
      type->name = name;
      type->type = MemberFormat::mtNumber;
      type->format.fraction = 8;
   }

   virtual void GetValue(Member* value) const
   {
      if( data != NULL )
      {
         if(data->status == DBSTATUS_S_ISNULL )
            value->number = 0;
         else
            value->number = *((double*)data->data);
      }
   }
};


class DateTimeBinder : public FieldBinder
{
public:
   DateTimeBinder(const MemberFormat& format, int index) : FieldBinder(format, index) {}

   virtual DBTYPEENUM DataType() const { return DBTYPE_I8; }
   virtual ULONG DataLength() const { return sizeof(__int64); }

   virtual bool Write(const Object& o)
   {
      const Member& m = o.at(index);
      data->status = DBSTATUS_S_OK;
      *(__int64*)data->data = *(__int64*)&m.datetime;
      return true;
   }

   virtual void GetType(MemberFormat *type) const
   {
      type->name = name;
      type->type = MemberFormat::mtDateTime;
      type->format.dateFormat = MemberFormat::Stamp;
   }

   virtual void GetValue(Member* value) const
   {
      if( data != NULL )
      {
         if(data->status == DBSTATUS_S_ISNULL )
         {
            SYSTEMTIME st = {0};
            st.wYear = 1970;
            st.wDay = 1;
            st.wMonth = 1;
            SystemTimeToFileTime(&st, &value->datetime);
         } else
         {
            *(__int64*)&value->datetime = *(__int64*)data->data;
         }
      }
   }
};

class BinaryBinder : public FieldBinder
{
public:
   BinaryBinder(const MemberFormat& format, int index) : FieldBinder(format, index) {}

   virtual bool Bind(BindData* d, CManualAccessor& accessor, int ord)
   {
      data = d;
      DBBINDING* cur = accessor.m_pEntry + accessor.m_nEntry;
      accessor.AddBindEntry(ord, DataType(), DataLength(), &data->data, &data->length, &data->status);

      DBOBJECT *obj = new DBOBJECT();
      obj->dwFlags = STGM_READ;
      obj->iid = IID_ISequentialStream;
      cur->pObject = obj;
      cur->dwPart |= (DBPART_LENGTH | DBPART_STATUS);

      return true;
   }

   virtual bool BindParam(BindData* d, CManualAccessor& accessor, int ord)
   {
      data = d;
      DBBINDING* cur = accessor.m_pParameterEntry + accessor.m_nCurrentParameter;
      accessor.AddParameterEntry(ord, DataType(), DataLength(), &data->data, &data->length, &data->status);
      
      DBOBJECT *obj = new DBOBJECT();
      obj->dwFlags = STGM_READ;
      obj->iid = IID_ISequentialStream;
      cur->pObject = obj;
      cur->dwPart |= (DBPART_LENGTH | DBPART_STATUS);

      return true;
   }

   virtual bool Write(const Object& o)
   {
      const Member& m = o.at(index);
      if( !m.binary )
         data->status = DBSTATUS_S_ISNULL;
      else  
      {
         data->status = DBSTATUS_S_OK;
         data->length = m.binary->Size();
         *(IUnknown**)(data->data) = (IUnknown*)&writer;
         writer.SetSrc(m.binary);
      }
      return true; 
   }

   virtual DBTYPEENUM DataType() const { return DBTYPE_IUNKNOWN; }
   virtual ULONG DataLength() const { return sizeof(IUnknown*); }

   virtual void GetType(MemberFormat *type) const
   {
      type->name = name;
      type->type = MemberFormat::mtBinary;
   }

   virtual void GetValue(Member* value) const
   {
      if( data != NULL )
      {
         ISequentialStream *iss = *(ISequentialStream**)data->data;
         if(data->status == DBSTATUS_S_OK || iss != NULL )
         {
            Binary *b = new Binary();
            BYTE *pb = b->Alloc(data->length);
            ULONG rb;
            
            iss->Read(pb, data->length, &rb);

            if( value->binary == NULL )
               value->binary = new MemoryBinary();
            value->binary->Assign(b);

         }
      }
   }

   StreamWriter writer;
};

FieldBinder* FieldBinder::Create(const MemberFormat& format, int index)
{
   switch(format.type)
   {
   case MemberFormat::mtString:
      return new StringBinder(format, index);
   case MemberFormat::mtNumber:
      return new NumberBinder(format, index);
   case MemberFormat::mtDateTime:
      return new DateTimeBinder(format, index);
   case MemberFormat::mtBinary:
      return new BinaryBinder(format, index);
   default:
      return NULL;
   }
}


//
// ------------------------------------ BinderBase ----------------------------------------------------
//
BinderBase::BinderBase() : data(NULL)
{
}

void BinderBase::Close()
{
   if( data == NULL )
      return;

   value.str = NULL;

   std::vector<FieldBinder*>::iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
         delete (*i);
   fields.clear();

   reader.FreeRecordMemory();
   reader.Close();

   delete data;
   data = NULL;
}

bool BinderBase::CreateAccessor(ULONG bufSize)
{
   HRESULT hr;
   data = (BindData*)(new BYTE [bufSize]);
   hr = reader.CreateAccessor(fields.size(), data, bufSize);
   if( !SUCCEEDED(hr) )
   {
      delete data;
      data = NULL;
      return false;
   }
   
   BindData* cd = data;
   vector<FieldBinder*>::iterator i = fields.begin();
   int ord = 1;
   for( ; i != fields.end(); i++ )
   {
      (*i)->Bind(cd, reader, ord++);
      cd = (BindData*)((BYTE*)cd + (*i)->FieldLength());
   }

   return true;
}

const MemberFormat* BinderBase::FieldType(const wchar_t* name) const
{
   std::vector<FieldBinder*>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      const FieldBinder* fb = (*i);
      if( fb->Name().compare(name) == 0 )
      {
         fb->GetType(&format);
         return &format;
      }
   }

   return NULL;
}

const Member* BinderBase::Value(const wchar_t* name) const
{
   std::vector<FieldBinder*>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      const FieldBinder* fb = (*i);
      if( fb->Name().compare(name) == 0 )
      {
         fb->GetValue(&value);
         return &value;
      }
   }

   return NULL;
}

bool BinderBase::Read(Object* o) const
{
   if( data == NULL )
      return false;

   bool ret = true;

   std::vector<FieldBinder*>::const_iterator i = fields.begin();
   for( ; ret && i != fields.end(); i++ )
   {
      if( (*i)->Read(o) == false )
         ret = false;
   }

   return ret;
}
//
// ------------------------------------ Binder ----------------------------------------------------
//
Binder::Binder()
{
}

bool Binder::PrepareReadStmt(ULONG* dataSize, std::wstring* stmt, const ISessionObject& obj)
{
   const IObjectData* od = obj.GetObjectDef();
   if( od == NULL )
      return false;

   GRServer::Format *format = obj.Self()->format;

   wstring name;
   *dataSize = 0;
   IObjectData::Fields::const_iterator fi = od->fields.begin();
   for( ; fi != od->fields.end(); fi++ )
   {
      int fldIndex = format->FindMember(fi->format.name.c_str());
      if( fldIndex < 0 )
         continue;

      if( fi->CanCreate() )
      {
         const MemberFormat& mf = format->at(fldIndex);
         FieldBinder* fb = FieldBinder::Create(mf, fldIndex);
         if( fb != NULL )
         {
            fields.push_back(fb);
            (*dataSize) += fb->FieldLength();
            QuoteString(&name, mf.name);
            stmt->append(name);
            stmt->append(L",");
         }
      } else if( (fi->flags & IObjectData::Field::File) != 0 && !fi->src.empty() )
      {
         int srcidx = format->FindMember(fi->src.c_str());
         if( srcidx >= 0 && format->at(srcidx).type == MemberFormat::mtString )
            files.push_back(new FileField(srcidx, fldIndex, gServer->GetConfig().ExchangeFolder(), gServer));
      }
   }

   stmt->erase(stmt->size() - 1, 1);
   return true;
}

bool Binder::CreateReader(ULONG bufSize, const std::wstring& stmt, CSession& session)
{
   if( !CreateAccessor(bufSize) )
      return false;
   
   HRESULT hr = OpenReader(session, stmt);
   return (SUCCEEDED(hr) != 0);
}

bool Binder::PrepareRead(const ISessionObject& obj, const std::wstring& filter, CSession& session)
{
   wstring fields;
   ULONG bufSize = 0;

   if( !PrepareReadStmt(&bufSize, &fields, obj) || fields.size() == 0 )
      return false;

   wstring stmt(L"SELECT "), tname;
   stmt += fields; stmt += L" FROM ";
   QuoteString(&tname, obj.GetObjectDef()->tableName);
   stmt += tname;
   if( filter.size() > 0 )
   {
      stmt += L" WHERE ";
      stmt += filter;
   }

   return CreateReader(bufSize, stmt, session);
}

bool Binder::Read(Object* o) const
{
   if( !BinderBase::Read(o) )
      return false;

   std::vector<FileField*>::const_iterator fi = files.begin();
   for( ; fi != files.end(); fi++ )
      if( !(*fi)->ReadFile(o) )
         gServer->AddError(false, "Error while reading file");

   return true;
}

void Binder::Close()
{
   BinderBase::Close();

   std::vector<FileField*>::iterator fi = files.begin();
   for( ; fi != files.end(); fi++ )
      delete (*fi);
   files.clear();
}

//
// ------------------------------------ ParamBinder ----------------------------------------------------
//
ParamBinder::ParamBinder() : paramData(NULL), dataSize(0)
{
}

bool ParamBinder::WriteParams(const Object& obj)
{
   bool ret = true;
   std::vector<FieldBinder*>::iterator i = params.begin();
   for( ; i != params.end(); i++ )
   {
      if( !(*i)->Write(obj) )
         ret = false;
   }

   return ret;
}

bool ParamBinder::BindParams()
{
   HRESULT hr;
   paramData = (BindData*)(new BYTE [dataSize]);
   hr = reader.CreateParameterAccessor(params.size(), paramData, dataSize);
   if( !SUCCEEDED(hr) )
      return false;

   BindData* cd = paramData;
   vector<FieldBinder*>::iterator i = params.begin();
   int ord = 1;
   for( ; i != params.end(); i++ )
   {
      (*i)->BindParam(cd, reader, ord++);
      cd = (BindData*)((BYTE*)cd + (*i)->FieldLength());
   }

   return true;
}

void ParamBinder::AddParam(std::wstring* paramStmt, const MemberFormat& mf, int fldIndex)
{
   FieldBinder* fb = FieldBinder::Create(mf, fldIndex);
   if( fb == NULL )
      return;

   params.push_back(fb);
   dataSize += fb->FieldLength();

   if( paramStmt != NULL )
   {
      wstring tstr;
      QuoteString(&tstr, mf.name);
      if( !paramStmt->empty() )
         paramStmt->append(L" AND ");
      
      paramStmt->append(tstr);
      paramStmt->append(L" = ?");
   }
}

void ParamBinder::Close()
{
   if( paramData != NULL )
   {
      std::vector<FieldBinder*>::iterator i = params.begin();
      for( ; i != params.end(); i++ )
         delete (*i);
      params.clear();
      
      delete paramData;
      paramData = NULL;
   }

   Binder::Close();
}

//
// ------------------------------------ StreamWriter ----------------------------------------------------
//

ULONG	StreamWriter::Release(void)
{
   return --cRef;
}

HRESULT StreamWriter::QueryInterface( REFIID riid, void** ppv )
{
	*ppv = NULL;
	if ( riid == IID_IUnknown )			 *ppv = this;
	if ( riid == IID_ISequentialStream ) *ppv = this;
	if ( *ppv )
	{
		( (IUnknown*) *ppv )->AddRef();
		return S_OK;
	}
	return E_NOINTERFACE;
}

HRESULT StreamWriter::Read(void *pv, ULONG cb, ULONG* pcbRead)
{
	if ( pcbRead ) *pcbRead = 0;
	if ( 0 == cb ) return S_OK; 

   DWORD left = (src == NULL) ? 0 : src->Size() - cp;
   ULONG readed = cb > left ? left : cb;

	if ( 0 == left ) return S_FALSE;

   memcpy( pv, (void*)(src->Bytes() + cp), readed );
	cp += readed;

   if ( pcbRead ) *pcbRead = readed;
	if ( cb != readed )
      return S_FALSE; 
   return S_OK;
}

HRESULT StreamWriter::Write( const void *pv, ULONG cb, ULONG* pcbWritten )
{
   return S_OK;
}
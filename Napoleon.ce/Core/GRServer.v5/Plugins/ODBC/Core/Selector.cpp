/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   03/07/2012   creating
 */
#include "stdafx.h"
#include "Source.h"
#include "Binder.h"
#include <ServerDefs.h>

//using namespace GRServer;
//using namespace std;
//
//class SelectBinder : public ParamBinderOle
//{
//public:
//   SelectBinder();
//
//   bool Prepare(CSession& session, const std::wstring& stmt, const Format& selFmt, const Format& whereFormat, 
//      const std::vector<int>& selIdx, const std::vector<int>& whereIdx);
//
//   virtual bool WriteParams(const Object& obj)
//   {
//      reparse = true;
//      return ParamBinderOle::WriteParams(obj);
//   }
//
//   virtual bool MoveNext(Object *parentObject)
//   { 
//      if( reparse )
//         reader.Open();
//      return ParamBinderOle::MoveNext(parentObject);
//   }
//
//   virtual HRESULT OpenReader(CSession& session, const std::wstring& stmt) { return reader.Create(session, stmt.c_str()); }
//
//protected:
//   bool reparse;
//};
//
//class OleSelector : public IDataSource::ISelector
//{
//public:
//   OleSelector(const ISessionObject& object, CDataConnection& connection);
//
//   virtual ~OleSelector() {}
//
//   virtual bool Select(Object* selObject, const Object* whereObject, 
//      const std::wstring& selStr, const std::wstring& whereStr, const std::wstring* orderStr,
//      const Format& selFmt, const Format& whereFormat, 
//      const std::vector<int>& selIdx, const std::vector<int>& whereIdx);
//
//   virtual bool SelectNext(Object* selObject, const Object* whereObject);
//
//private:   
//   CSession& session;
//   std::wstring tableName;
//   bool prepared;
//   SelectBinder binder;
//};
//
//OleSelector::OleSelector(const ISessionObject& object, CDataConnection& connection) :
//   session(connection.m_session),
//   prepared(false)
//{
//   const IObjectData* data = object.GetObjectDef();
//   if( data != NULL )
//      tableName = data->tableName;
//}
//
//bool OleSelector::Select(Object* selObject, const Object* whereObject, 
//   const std::wstring& selStr, const std::wstring& whereStr, const std::wstring* orderStr,
//   const Format& selFmt, const Format& whereFormat, 
//   const std::vector<int>& selIdx, const std::vector<int>& whereIdx)
//{
//   std::wstring sql = L"SELECT ";
//   sql += selStr; sql += L" FROM \""; sql += tableName; sql += L"\"";
//   if( whereStr.empty() == false )
//   {
//      sql += L" WHERE "; sql += whereStr;
//   }
//
//   if( orderStr != NULL && !orderStr->empty() )
//   {
//      sql += L" ORDER BY "; sql += (*orderStr);
//   }
//
//
//   if( !binder.Prepare(session, sql, selFmt, whereFormat, selIdx, whereIdx) )
//      return false;
//   return SelectNext(selObject, whereObject);
//}
//
//SelectBinder::SelectBinder() : reparse(false)
//{
//}
//
//bool SelectBinder::Prepare(CSession& session, const std::wstring& stmt,
//         const Format& selFmt, const Format& whereFormat, const std::vector<int>& selIdx, const std::vector<int>& whereIdx)
//{
//   ULONG selSize = 0;
//   std::vector<int>::const_iterator i = selIdx.begin();
//   for( ; i != selIdx.end(); i++ )
//   {
//      const MemberFormat& mf = selFmt.at(*i);
//      FieldBinderOle* fb = FieldBinderOle::Create(mf, (*i));
//      if( fb != NULL )
//      {
//         fields.push_back(fb);
//         selSize += fb->FieldLength();
//      }
//   }
//
//   i = whereIdx.begin();
//   for( ; i != whereIdx.end(); i++ )
//   {
//      const MemberFormat& mf = selFmt.at(*i);
//      FieldBinderOle* fb = FieldBinderOle::Create(mf, (*i));
//      if( fb != NULL )
//         AddParam(NULL, mf, (*i));
//   }
//
//   if( whereIdx.size() > 0 )
//      BindParams();
//
//   return CreateReader(selSize, stmt, session);
//}
//
//bool OleSelector::SelectNext(Object* selObject, const Object* whereObject)
//{
//   if( whereObject != NULL )
//      binder.WriteParams(*whereObject);
//   
//   bool ret = false;
//   if( binder.MoveNext(NULL) )
//   {
//      binder.Read(selObject);
//      ret = true;
//   }
//
//   return ret;
//}

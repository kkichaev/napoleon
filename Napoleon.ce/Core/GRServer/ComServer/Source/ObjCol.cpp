// ObjCol.cpp: реализация CObjCol

#include "stdafx.h"
#include "ObjCol.h"
#include <socket.h>
#include <stdobjs.h>
#include <ServerDefs.h>
#include "Object.h"
#include "Collection.h"

using namespace GRServer;
// CObjCol

const wchar_t NULL_TAG[] = L"NULL";

void ObjList::Add(CGRObject* object, GRServer::Object* data)
{
   ObjData od;
   od.object = object;
   od.data = data;

   push_back(od);
}

GRServer::Object* ObjList::Remove(CGRObject* object)
{
   iterator i = begin();
   for( ; i != end(); i++ )
      if( i->object == object )
      {
         GRServer::Object* data = i->data;
         erase(i);
         return data;
      }

   return NULL;
}

void ObjList::RemoveData(GRServer::Object* data, Func doFunc)
{
   iterator i = begin();
   for( ; i != end(); i++ )
      if( i->data == data )
         ((*i->object).*doFunc)();
}

void ObjList::ForEach(Func doFunc)
{
   iterator i = begin();
   for( ; i != end(); i++ )
      ((*i->object).*doFunc)();
}


void CObjCol::FinalRelease()
{
   AddLog("CObjCol::FinalRelease %x size=%d", this, objects.size());

   if( canFreeObject ) 
   {
      objects.ForEach(&CGRObject::OwnerDeleted);
      delete servObject;
      server->Release();
   } else
      objects.ForEach(&CGRObject::FreeOwner);

   servObject = NULL;
}

STDMETHODIMP CObjCol::Get(LONG index, IDispatch** object)
{
   if( servObject == NULL )
   {
      return S_FALSE;
   }

   if( (unsigned)index >= servObject->size() )
   {
      std::wstring msg(L"Объект ");
      msg += servObject->format->name;
      msg += L" содержит меньше элементов";
      server->SetErrorMessage(msg.c_str());
      return S_FALSE;
   }

   HRESULT res = CGRObject::CreateInstance(object);
   if( SUCCEEDED(res) )
   {
      GRServer::Object* data = servObject->at(index);
      ((CGRObject*)*object)->SetData(server, this, data);
      objects.Add((CGRObject*)*object, data);
   }

   return res;
}

STDMETHODIMP CObjCol::New(IDispatch** object)
{
   if( servObject == NULL )
   {
      return S_FALSE;
   }

   HRESULT res = CGRObject::CreateInstance(object);
   if( SUCCEEDED(res) )
   {
      GRServer::Object* data = servObject->AddObject();
      ((CGRObject*)*object)->SetData(server, this, data);
      objects.Add((CGRObject*)*object, data);
   }
   return res;
}

static void MakeCommand(std::wstring *res, const wchar_t* cmd, const wchar_t* userid)
{
   *res = cmd;
   if( *userid != L'\0' )
   {
      res->append(L" "); res->append(IMPERSONATE); res->append(L" ");
      if( _wcsicmp(userid, NULL_TAG) == 0 )
         res->append(NULL_TAG);
      else
      {
         res->append(L"'");
         res->append(userid);
         res->append(L"'");
      }
   }
}

static void PrepareWithUserID(ExchangeList *res, const wchar_t* cmd, ServObject* object, const ConnectData& cd)
{
   int uindex = object->format->FindMember(USERID_MEMBER);
   if( uindex < 0 )
   {
      ServObject *so = cd.MakeCommand(res->GetFormatList(), cmd);
      res->push_back(so);
      res->push_back(object);

      return;
   }

   std::wstring uid, scmd;
   ServObject::iterator i = object->begin();
   for( ; i != object->end(); i++ )
   {
      GRServer::CString *cuid = (*i)->at(uindex).str;
      if( i == object->begin() || uid.compare((const std::wstring&)*cuid) != 0 )
      {
         std::wstring tcmd;

         uid = (const std::wstring&)*cuid;
         MakeCommand(&tcmd, cmd, uid.c_str());

         ServObject *so = cd.MakeCommand(res->GetFormatList(), tcmd.c_str());
         res->push_back(so);

         so = new ServObject(object->format);
         res->push_back(so);
      }
      res->back()->push_back(*i);
   }
}

static bool HaveObject(const ServObject& src, Object* o)
{
   ServObject::const_iterator i = src.begin();
   for( ; i != src.end(); i++ )
      if( (*i) == o )
         return true;

   return false;
}

static void RemoveExisting(ExchangeList *list, ServObject* src)
{
   ExchangeList::iterator li = list->begin();
   for( ; li != list->end(); li++ )
   {
      if( (*li) == src )
      {
         (*li) = NULL;
         continue;
      }

      ServObject::iterator oi = (*li)->begin();
      for( ; oi != (*li)->end(); oi++ )
      {
         if( HaveObject(*src, (*oi)) )
            (*oi) = NULL;
      }
   }
}

STDMETHODIMP CObjCol::WriteInt(BSTR userid, bool haveUserid)
{
   HRESULT res = S_FALSE;
   if( servObject == NULL || server == NULL )
   {
      //server->SetErrorMessage(L"Не назначен объект");
      return res;
   }

   if( servObject->size() == 0 )
   {
      std::wstring msg(L"Пустой объект ");
      msg += servObject->format->name;
      server->SetErrorMessage(msg.c_str());

      return res;
   }

   ExchangeList el(server->GetObjCreator().GetFormatList());
   const ConnectData& cd = server->GetConnectData();

   if( *userid != L'\0' || !haveUserid )
   {
      std::wstring cmd;

      MakeCommand(&cmd, PUT_NO_EXEC, userid);
//      MakeCommand(&cmd, FORCE_PUT, userid);
      ServObject *so = cd.MakeCommand(server->GetObjCreator().GetFormatList(), cmd.c_str());

      el.push_back(so);
      el.push_back(servObject);
   } else
   {
      PrepareWithUserID(&el, FORCE_PUT, servObject, cd);
   }

   bool ret;
   Socket s;
   if( s.Connect(cd.address, cd.port) )
   {
      el.Write(&s);
      if( ReadAnswer(&s, cd.timeout, &ret, NULL) )
      {
         cd.SendCommand(&s, BYE_COMMAND, L"");
         if( ret )
            res = S_OK;
      } else
         server->SetErrorMessage(L"Ошибка при записи");
   }

   RemoveExisting(&el, servObject);
   return res;
}

STDMETHODIMP CObjCol::WriteDirect()
{
	return WriteInt(L"", false);
}

STDMETHODIMP CObjCol::Write(BSTR userid)
{
	return WriteInt(userid, true);
}

STDMETHODIMP CObjCol::Replace(BSTR userid)
{
   if( *userid == L'\0' )
   {
      server->SetErrorMessage(L"Не указан userid для метода Replace");
      return S_FALSE;
   }

   if( servObject == NULL || server == NULL )
   {
      //server->SetErrorMessage(L"Не назначен объект");
      return S_FALSE;
   }

   ExchangeList el(server->GetObjCreator().GetFormatList());
   const ConnectData& cd = server->GetConnectData();

   std::wstring cmd;
   ServObject *so;
   
   // put remove command
   std::wstring param;
   param = servObject->format->name;
   param += L":userid ";
   if(_wcsicmp(userid, NULL_TAG) == 0)
      param += L"is null";
   else
   {
      param += L"= '";
      param += userid;
      param += L"'";
   }
   so = cd.MakeCommand(server->GetObjCreator().GetFormatList(), REMOVE_COMMAND, param.c_str());
   el.push_back(so);

   // put force put command
   MakeCommand(&cmd, FORCE_PUT, userid);
   so = cd.MakeCommand(server->GetObjCreator().GetFormatList(), cmd.c_str());
   el.push_back(so);

   // put data object
   el.push_back(servObject);

   HRESULT res = S_FALSE;
   bool ret;
   Socket s;
   if( s.Connect(cd.address, cd.port) )
   {
      el.Write(&s);
      if( ReadAnswer(&s, cd.timeout, &ret, NULL) )
      {
         cd.SendCommand(&s, BYE_COMMAND, L"");
         if( ret )
            res = S_OK;
      } else
         server->SetErrorMessage(L"Ошибка при выполнениие команды Replace");
   }
   el.at(2) = NULL;

   return res;
}

STDMETHODIMP CObjCol::ReplaceDirect(BSTR where)
{
   if (servObject == NULL || server == NULL)
   {
      //server->SetErrorMessage(L"Не назначен объект");
      return S_FALSE;
   }

   ExchangeList el(server->GetObjCreator().GetFormatList());
   const ConnectData& cd = server->GetConnectData();

   std::wstring cmd;
   ServObject* so;

   // put remove command
   std::wstring param(servObject->format->name);
   param.append(1, L':');
   if (*where != L'\0')
      param.append(where);

   so = cd.MakeCommand(server->GetObjCreator().GetFormatList(), REMOVE_COMMAND, param.c_str());
   el.push_back(so);

   // put force put command
   MakeCommand(&cmd, PUT_NO_EXEC, L"");
   so = cd.MakeCommand(server->GetObjCreator().GetFormatList(), cmd.c_str());
   el.push_back(so);

   // put data object
   el.push_back(servObject);

   HRESULT res = S_FALSE;
   bool ret;
   Socket s;
   if (s.Connect(cd.address, cd.port))
   {
      el.Write(&s);
      if (ReadAnswer(&s, cd.timeout, &ret, NULL))
      {
         cd.SendCommand(&s, BYE_COMMAND, L"");
         if (ret)
            res = S_OK;
      }
      else
         server->SetErrorMessage(L"Ошибка при выполнениие команды Replace");
   }
   el.at(2) = NULL;

   return res;
}

STDMETHODIMP CObjCol::Delete(BSTR userid)
{
   if( servObject == NULL || server == NULL )
   {
      //server->SetErrorMessage(L"Не назначен объект");
      return S_FALSE;
   }


   server->SetErrorMessage(L"Метод Delete не реализован");
   return S_FALSE;
   //return S_OK;
}

STDMETHODIMP CObjCol::get_Count(double* pVal)
{
   (*pVal) = (double)((servObject) ? servObject->size() : 0);
   return S_OK;
}

STDMETHODIMP CObjCol::get_Type(BSTR* pVal)
{
   *pVal = SysAllocString((servObject) ? servObject->Name().c_str() : L"");
   return S_OK;
}

//static void ReplaceObjectTypes(BSTR newObjName, GRServer::Format* format, GRServer::FormatList* formats)
//{
//	std::vector<MemberFormat>::iterator i = format->begin();
//	for (; i != format->end(); i++)
//	{
//		if (i->type == MemberFormat::mtObject)
//		{
//			GRServer::Format *chF = formats->GetFormat(format->name + L"$" + i->name);
//			if (chF != NULL)
//				ReplaceObjectTypes(newObjName)
//		}
//	}
//}

STDMETHODIMP CObjCol::put_Type(BSTR pVal)
{
	//std::vector<MemberFormat>::iterator i = servObject->format->begin();
	//for (; i != servObject->format->end(); i++)
	//{
	//	if (i->type == MemberFormat::mtObject)
	//}
	servObject->format->name = pVal;
   return S_OK;
}

STDMETHODIMP CObjCol::get_Fields(IDispatch** pVal)
{
   if( servObject == NULL || server == NULL )
   {
      //server->SetErrorMessage(L"Не назначен объект");
      return S_FALSE;
   }


   HRESULT res = CCollection::CreateInstance(pVal);
   if( SUCCEEDED(res) )
      ((CCollection*)*pVal)->SetData(*servObject->format, servObject->format->name.c_str(), server->GetObjCreator().GetFormatList());

   return res;
}

STDMETHODIMP CObjCol::get_KeyFields(IDispatch** pVal)
{
   //server->SetErrorMessage(L"Не назначен объект");
   return S_FALSE;
   //return S_OK;
}

STDMETHODIMP CObjCol::GetIDsOfNames(REFIID riid, LPOLESTR* rgszNames, UINT cNames, LCID lcid, DISPID* rgdispid)
{
   LPOLESTR* names = (LPOLESTR*)alloca(cNames * sizeof(LPOLESTR));
   for( UINT i = 0; i < cNames; i++ )
      names[i] =(wchar_t*) Aliases::GetAlias(rgszNames[i]);

	return _tih.GetIDsOfNames(riid, names, cNames, lcid, rgdispid);
}


STDMETHODIMP CObjCol::RemoveObject(ULONG index)
{
   if( servObject == NULL || server == NULL || index >= servObject->size() )
   {
      //server->SetErrorMessage(L"Не назначен объект");
      return S_FALSE;
   }

   NeedDeleted(servObject->at(index));
   return S_OK;
}

void CObjCol::NeedDeleted(GRServer::Object* data)
{
   objects.RemoveData(data, &CGRObject::ObjectDeleted);

   ServObject::iterator i = servObject->begin();
   for( ; i != servObject->end(); i++ )
   {
      if( (*i) == data )
      {
         (*i) = NULL;
         servObject->erase(i);
         break;
      }
   }

   delete data;
}
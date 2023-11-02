// Server.cpp: реализация CServer

#include "stdafx.h"
#include "Server.h"
#include <socket.h>
#include <ServerDefs.h>
#include "ObjCol.h"
#include "Collection.h"

using namespace GRServer;
// CServer

static Aliases aliases[] = 
{
   { L"Подключить", L"Connect" },
   { L"Новый", L"New" },
   { L"Удалить", L"Delete" },
   { L"Получить", L"Get" },
   { L"Установить", L"Set" },
   { L"Удалить", L"Delete" },
   { L"Ошибка", L"ErrorMessage" },
   { L"Сохранить", L"Write" },
   { L"Прочитать", L"Read" },
   { L"Размер", L"Size" },
   { L"Переписать", L"Replace" },
   { L"Количество", L"Count" },
   { L"Поля", L"Fields" },
   { L"КлючевыеПоля", L"KeyFields" },
   { L"УдалитьОбъект", L"RemoveObject" },
   { L"Имя", L"Name" },
   { L"Тип", L"Type" },
   { L"Объект", L"ChildObject" },
   { NULL, NULL }
};

const wchar_t* Aliases::GetAlias(const wchar_t *name)
{
   Aliases* a = aliases;
   while( a->name )
   {
      if( _wcsicmp(a->name, name) == 0 ) return a->src;
      a++;
   }
   return name;
}

static int GetDuration()
{
   char buf[MAX_PATH];
   GetTempPathA(sizeof(buf)/sizeof(buf[0]), buf);
   strcat(buf, "\\grserver.tmp");

   FILE* f = fopen(buf, "rb");
   int res = 0;
   if( f )
   {
      fread(&res, sizeof(res), 1, f);
      fclose(f);
   }

   return res;
}

static void SaveDuration(int duration)
{
   char buf[MAX_PATH];
   GetTempPathA(sizeof(buf)/sizeof(buf[0]), buf);
   strcat(buf, "\\grserver.tmp");

   FILE* f = fopen(buf, "wb");
   int res = GetTickCount();
   if( f )
   {
      fwrite(&duration, sizeof(duration), 1, f);
      fclose(f);
   }
}

bool ConnectData::SendCommand(Socket* socket, const wchar_t* cmd, const wchar_t* param) const
{
	FormatList flist;
	ExchangeList obj(&flist);

	Format *fmt = new ServerCommandFormat();
	flist.AddFormat(fmt, true);

	ServObject *so = new ServObject(fmt);
	Object *ocmd = so->AddObject();

	(*ocmd)[COMMAND_MEMBER]->str->assign(cmd);
	(*ocmd)[PARAM_MEMBER]->str->assign(param);
	(*ocmd)[USERID_MEMBER]->str->assign(login);
	(*ocmd)[PASSWORD_MEMBER]->str->assign(password);
	(*ocmd)[DURATION_MEMBER]->number = duration;
	(*ocmd)[CATEGORY_MEMBER]->str->assign(category);

	obj.push_back(so);

	return obj.Write(socket);
}


ServObject* ConnectData::MakeCommand(FormatList* list, const wchar_t* cmd, const wchar_t* param) const
{
   Format* fmt = list->GetFormat(ServerCommandFormat::Name());
   if( fmt == NULL )
   {
      fmt = new ServerCommandFormat();
      list->AddFormat(fmt, true);
   }

   ServObject *so = new ServObject(fmt);
   Object *ocmd = so->AddObject();

   (*ocmd)[COMMAND_MEMBER]->str->assign(cmd);
   (*ocmd)[PARAM_MEMBER]->str->assign(param);
   (*ocmd)[USERID_MEMBER]->str->assign(login);
   (*ocmd)[PASSWORD_MEMBER]->str->assign(password);
   (*ocmd)[DURATION_MEMBER]->number = duration;
	(*ocmd)[CATEGORY_MEMBER]->str->assign(category);

   return so;
}

#include <AES.h>
extern Key uploadKey;
inline wchar_t ToHex(unsigned char sym)
{
   static wchar_t syms[] = L"0123456789ABCDEF";
   return sym >= 16 ? L' ' : syms[sym];
}
static void MakeCOMPassword(std::wstring* pwd)
{
   for (int i = 0; uploadKey[i] && i < sizeof(uploadKey); i++)
   {
      pwd->append(1, ToHex((uploadKey[i] & 0xF0) >> 4));
      pwd->append(1, ToHex((uploadKey[i] & 0xF)));
   }
}

STDMETHODIMP CServer::Connect(BSTR name, USHORT port, BSTR login, BSTR password, BSTR category, VARIANT_BOOL* result)
{
   std::wstring pwd(password);
   bool comLogin = (*login == L'\0');

   connectData.address = name;
   connectData.port = port;
   connectData.login = comLogin ? COM_LOGIN : login;

#ifdef VERSION_5
   if (comLogin && pwd.empty())
      MakeCOMPassword(&pwd);
#endif

   connectData.password = pwd;
	connectData.category = (*category == L'\0') ? L"pda" : category;

#ifdef DEBUG
   MessageBox(NULL, L"!", L"!", MB_OK);
#endif

   *result = VARIANT_FALSE;

   Socket s;
   if( s.Connect(connectData.address, connectData.port) )
   {
      connectData.duration = GetDuration();
		if (connectData.SendCommand(&s, GET_COMMAND, L"Agents"))
      {
         bool ret;
         std::wstring answ;
         if( ReadAnswer(&s, connectData.timeout, &ret, &answ, &connectData.duration) )
         {
				connectData.SendCommand(&s, BYE_COMMAND, L"");
            if( ret )
            {
               *result =  VARIANT_TRUE;
               SaveDuration(connectData.duration);
            }
            else
               errorMessage = answ.c_str();
         } else
            errorMessage = "Нет ответа от сервера";
      } else
      {
         errorMessage = L"Не могу отправить команду";
      }
   } else
   {
      errorMessage = L"Не могу подключиться ";
      errorMessage += connectData.address;
   }
   return S_OK;
}

static void GetServerError(CAtlStringW *err, const ExchangeList& el)
{
   if( el.size() > 0 )
   {
      const ServObject* curObj = el.at(0);
      int i = curObj->format->FindMember(MESSAGE_MEMBER);
      int ir = curObj->format->FindMember(RESPONSE_MEMBER);
      if( i >= 0 )
      {
         const Object* co = curObj->at(0);
         GRServer::CString *str = co->at(i).str;
         bool response = (co->at(ir).number > 0);
         (*err) = ( str->empty() || response ) ? L"Объект отсутствует или пустой" : str->c_str();
      }
   } else
   {
      *err = L"Сервер не отвечает";
   }
}

STDMETHODIMP CServer::ReadObject(const wchar_t* cmd, const wchar_t* param, IDispatch** collection)
{
   HRESULT res = S_FALSE;

   Socket s;
   *collection = NULL;

	if (s.Connect(connectData.address, connectData.port) && connectData.SendCommand(&s, cmd, param))
   {
      ExchangeList el(objCreator.GetFormatList());
      bool rc = el.Read(&s, connectData.timeout, NULL, &objCreator, true);
      if( rc )
      {
			connectData.SendCommand(&s, BYE_COMMAND, L"");
         if( el.size() > 1 )
         {
            ServObject* curObj = el.at(1);
				if (curObj->format->name.compare(L"ReqServerData") == 0)
				{
					errorMessage = L"Объект отсутствует или пустой";
				}
				else
				{
					if ((res = CObjCol::CreateInstance(collection)) == S_OK)
					{
						((CObjCol*)*collection)->SetObject(this, curObj, true);
						el.at(1) = NULL;
					}
				}

         } else
         {
            GetServerError(&errorMessage, el);
         }
      }
   } else
      errorMessage = L"Сервер не отвечает";

   return res;
}

STDMETHODIMP CServer::Get(BSTR name, BSTR filter, IDispatch** collection)
{
   std::wstring param(name);
   param += L":";
   if( *filter )
      param += filter;

   return ReadObject(SELECT_COMMAND, param.c_str(), collection);
}

STDMETHODIMP CServer::New(BSTR name, IDispatch** collection)
{
   return ReadObject(GET_OBJ_FORMAT, name, collection);
}

static MemberFormat::MemberType FromVariant(VARTYPE vt)
{
	switch (vt)
	{
	case VT_I2:
	case VT_I4:
	case VT_R4:
	case VT_R8:
	case VT_CY:
	case VT_BOOL:
	case VT_I1:
	case VT_UI1:
	case VT_UI2:
	case VT_UI4:
	case VT_I8:
	case VT_UI8:
	case VT_INT:
	case VT_UINT:
		return MemberFormat::mtNumber;
	case VT_DATE:
		return MemberFormat::mtDateTime;

	default:
		return MemberFormat::mtString;
	}
}

static double ToDouble(const VARIANT &src)
{
	switch (src.vt)
	{
	case VT_I2:
		return src.iVal;
	case VT_I4:
		return src.lVal;
	case VT_R4:
		return src.fltVal;
	case VT_R8:
		return src.dblVal;
	case VT_CY:
		return (double)src.cyVal.int64;
	case VT_BOOL:
		return src.boolVal;
	case VT_I1:
		return src.cVal;
	case VT_UI1:
		return src.bVal;
	case VT_UI2:
		return src.uiVal;
	case VT_UI4:
		return src.ulVal;
	case VT_I8:
		return (double)src.llVal;
	case VT_UI8:
		return (double)src.ullVal;
	case VT_INT:
		return src.intVal;
	case VT_UINT:
		return src.uintVal;
	}
	return 0;
}

static void WriteValue(Member& dest, VARIANT& src, MemberFormat::MemberType type)
{
	switch(type)
	{
	case MemberFormat::mtString:
		VariantToStr(dest.str, src);
		break;
	case MemberFormat::mtNumber:
	{
		dest.number = ToDouble(src);
		break;
	}
	case MemberFormat::mtDateTime:
	{
		if(src.vt == VT_DATE)
		{
			SYSTEMTIME st;
			VariantTimeToSystemTime(src.date, &st);
			SystemTimeToFileTime(&st, &dest.datetime);
		}
		break;
	}
	}
}

static ServObject* MakeServObject(IDispatch *params, Format* format)
{
   HRESULT hr;
   ITypeInfo *ti;
   hr = params->GetTypeInfo(0, 0, &ti);
   if( !SUCCEEDED(hr) )
   {
      return NULL;
   }

   TYPEATTR *tt;
   hr = ti->GetTypeAttr(&tt);
   if( !SUCCEEDED(hr) )
   {
      ti->Release();
      return NULL;
   }

   format->clear();
   //std::vector<MEMBERID> ids;
	std::vector<VARIANT> values;
	for( int i=0; i<tt->cVars; i++ )
   {
      VARDESC *vd;
      if( SUCCEEDED(ti->GetVarDesc(i, &vd)) )
      {
			BSTR name;
			UINT n;

			UINT idx;
			VARIANT res;
			DISPPARAMS dp;

			VariantInit(&res);

			dp.cArgs = 0;
			dp.cNamedArgs = 0;
			
         ti->GetNames(vd->memid, &name, 1, &n);

			if (params->Invoke(vd->memid, IID_NULL, 0, DISPATCH_PROPERTYGET, &dp, &res, NULL, &idx) == S_OK)
			{
				MemberFormat mf;
				mf.name = name;
				mf.type = FromVariant(res.vt);

				format->push_back(mf);
				values.push_back(res);
			}


         SysFreeString(name);
         //ids.push_back(vd->memid);
         ti->ReleaseVarDesc(vd);
      }
   }

   ti->ReleaseTypeAttr(tt);   
   ti->Release();

   ServObject* so = new ServObject(format);
   GRServer::Object *o = so->AddObject();

   int index = 0;
	std::vector<VARIANT>::iterator ii = values.begin();
	for (; ii != values.end(); ii++, index++)
	{
		WriteValue(o->at(index), *ii, format->at(index).type);
		VariantClear(&(*ii));
	}

   //std::vector<MEMBERID>::const_iterator ii = ids.begin();
   //for( ; ii != ids.end(); ii++, index++ )
   //{
   //   UINT idx;
   //   VARIANT res;
   //   DISPPARAMS dp;

   //   dp.cArgs = 0;
   //   dp.cNamedArgs = 0;

   //   VariantInit(&res);
   //   if( params->Invoke(*ii, IID_NULL, 0, DISPATCH_PROPERTYGET, &dp, &res, NULL, &idx) == S_OK )
   //   {
   //      VariantToStr(o->at(index).str, res);
   //   }
   //}
   return so;
}

STDMETHODIMP CServer::Write(IDispatch *objcol)
{
   CComPtr<IGRObjCol> check;
   if( objcol->QueryInterface(IID_IGRObjCol, (void**)&check) != S_OK )
      return E_INVALIDARG;

   CObjCol* oc = (CObjCol*)objcol;
   const GRServer::ServObject* servObject = oc->GetServObject();
   if( servObject == NULL )
      return E_POINTER;

   ExchangeList el(oc->GetObjCreator().GetFormatList());
   ServObject *so = connectData.MakeCommand(objCreator.GetFormatList(), PUT_NO_EXEC);
   el.push_back(so);
   el.push_back((GRServer::ServObject*)servObject);

   Socket s;
   HRESULT res = S_FALSE;
   if( s.Connect(connectData.address, connectData.port) )
   {
      bool ret;
      el.Write(&s);
      if( ReadAnswer(&s, connectData.timeout, &ret, NULL) )
      {
			connectData.SendCommand(&s, BYE_COMMAND, L"");
         if( ret )
            res = S_OK;
      } else
         SetErrorMessage(L"Ошибка при записи");
   }
   el.at(1) = NULL;
   return res;
}

HRESULT CServer::DoReport(BSTR name, ServObject* params, IDispatch** collection)
{
	HRESULT res = S_FALSE;

	Socket s;
	*collection = NULL;

	ExchangeList cmd(objCreator.GetFormatList());
	cmd.push_back(connectData.MakeCommand(objCreator.GetFormatList(), GET_REPORT, name));
	if (params != NULL)
	{
		cmd.push_back(params);
	}

	if (s.Connect(connectData.address, connectData.port) && cmd.Write(&s))
	{
		ExchangeList el(objCreator.GetFormatList());
		if (el.Read(&s, connectData.timeout, NULL, &objCreator, true) && el.size() > 1)
		{
			connectData.SendCommand(&s, BYE_COMMAND, L"");

			if ((res = CCollection::CreateInstance(collection)) == S_OK)
			{
				for (unsigned i = 1; i < el.size(); i++)
				{
					CObjCol* oc = NULL;
					if ((res = CObjCol::CreateInstance((IDispatch**)&oc)) == S_OK)
					{
						ServObject* curObj = el.at(i);
						oc->SetObject(this, curObj, true);
						el.at(i) = NULL;

						((CCollection*)*collection)->Add(oc);
					}
				}
			}
		}
		else
		{
			GetServerError(&errorMessage, el);
		}
	}
	else
		errorMessage = L"Сервер не отвечает";

	return res;
}

STDMETHODIMP CServer::ReportStrParam(BSTR name, BSTR params, IDispatch** collection)
{
	Format format(L"ReportParam");
	format.clear();

	ServObject* so = NULL;
	if (params != NULL)
	{
		MemberFormat mf;
		mf.name = L"param";
		mf.type = MemberFormat::mtString;
		format.push_back(mf);

		so = new ServObject(&format);
		GRServer::Object *o = so->AddObject();
		o->at(0).str->assign(params);
	}

	return DoReport(name, so, collection);
}

STDMETHODIMP CServer::Call(BSTR name, IDispatch* params, IDispatch** collection)
{
   return Report(name, params, collection);
}

STDMETHODIMP CServer::Report(BSTR name, IDispatch *params, IDispatch** collection)
{
	Format fmt(L"ReportParam");
	ServObject* so = NULL;
	if (params != NULL)
	{
		so = MakeServObject(params, &fmt);
	}
	
	return DoReport(name, so, collection);

}

STDMETHODIMP CServer::EndSession(void)
{
	Socket s;

	if (s.Connect(connectData.address, connectData.port) && connectData.SendCommand(&s, QUIT_COMMAND, L""))
	{
      bool res;
      ReadAnswer(&s, connectData.timeout, &res, NULL);
      connectData.SendCommand(&s, BYE_COMMAND, L"");
	}
	return S_OK;
}

STDMETHODIMP CServer::Delete(BSTR name, BSTR filter)
{
   Socket s;

   std::wstring param(name);
   param += L":";
   param += filter;

   HRESULT res = S_FALSE;
	if (s.Connect(connectData.address, connectData.port) && connectData.SendCommand(&s, REMOVE_COMMAND, param.c_str()))
   {
      bool res;
      ReadAnswer(&s, connectData.timeout, &res, NULL);
		connectData.SendCommand(&s, BYE_COMMAND, L"");
      res = S_OK;
   }
   return res;
}

STDMETHODIMP CServer::get_Timeout(int* pVal)
{
   *pVal = connectData.timeout;
   return S_OK;
}

STDMETHODIMP CServer::put_Timeout(int val)
{
   connectData.timeout = val;
   return S_OK;
}

STDMETHODIMP CServer::get_ErrorMessage(BSTR* pVal)
{
   *pVal = errorMessage.AllocSysString();
   return S_OK;
}

STDMETHODIMP CServer::GetIDsOfNames(REFIID riid, LPOLESTR* rgszNames, UINT cNames, LCID lcid, DISPID* rgdispid)
{
   //MessageBox(NULL, L"!", L"!", MB_OK);

   LPOLESTR* names = (LPOLESTR*)alloca(cNames * sizeof(LPOLESTR));
   for( UINT i = 0; i < cNames; i++ )
      names[i] =(wchar_t*) Aliases::GetAlias(rgszNames[i]);

	return _tih.GetIDsOfNames(riid, names, cNames, lcid, rgdispid);
}

void AddLog(const char* msg, ... )
{
#ifdef DEBUG
   va_list args;
   std::wstring fileName;

   va_start(args, msg);

   FILE *file = fopen("C:\\log.txt", "at");
   if( file != NULL )
   {
      vfprintf(file, msg, args);
      fputs("\n", file);
      fclose(file);
   }
#endif
}
/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Action impl
 *
 * ert   12/03/2010   creating
 */
#include "stdafx.h"
#include "xml.h"
#include "event.h"
#include "objdef.h"
#include "sessobj.h"
#include "session.h"
#include "parse.h"
#include "actresolver.h"
#include "dispatcher.h"

#include "srvdata.h"
#include <idatasource.h>
#include <atlconv.h>

using namespace GRServer;

class PutAction : public IActionExecutor
{
public:
   PutAction(SourceType type, const std::wstring& object, const std::wstring& params);

   virtual bool Do(Session* session, SessionObject* thisObject, const std::vector<Token>& params, Action& action);

protected:
   SourceType type;
   std::wstring object;
   std::wstring params;
};

class PutLoader : public IActionExecutorLoader, IXmlHandler
{
public:
   PutLoader();
   virtual void Load(ActionLoader* prevHandler, const IXmlHandler::Attributes& attributes);

   virtual void StartElement(const std::wstring& name, const Attributes& atts) {}
   virtual void EndElement(const std::wstring& name);
   virtual void CharacterData(const std::wstring& name);

   virtual bool IsError() const { return (error.empty() == false); }
   virtual const wchar_t* GetError() const { return error.c_str(); }

protected:
   std::wstring error;
   ActionLoader* handler;

   std::wstring object;
   std::wstring params;
   SourceType type;
};

class ExecuteAction : public IActionExecutor
{
public:
   ExecuteAction(const std::wstring& stmt);

   virtual bool Do(Session* session, SessionObject* thisObject, const std::vector<Token>& params, Action& action);

protected:
   std::wstring stmt;
};

class ExecuteLoader : public IActionExecutorLoader, IXmlHandler
{
public:
   ExecuteLoader();
   virtual void Load(ActionLoader* prevHandler, const IXmlHandler::Attributes& attributes);

   virtual void StartElement(const std::wstring& name, const Attributes& atts) {}
   virtual void EndElement(const std::wstring& name);
   virtual void CharacterData(const std::wstring& name);

   virtual bool IsError() const { return (error.empty() == false); }
   virtual const wchar_t* GetError() const { return error.c_str(); }

protected:
   std::wstring error;
   ActionLoader* handler;

   std::wstring stmt;
};

class RunReportAction : public IActionExecutor
{
public:
	RunReportAction(const std::wstring& report);

	virtual bool Do(Session* session, SessionObject* thisObject, const std::vector<Token>& params, Action& action);

protected:
	std::wstring report;
};

class RunReportLoader : public IActionExecutorLoader, IXmlHandler
{
public:
	RunReportLoader();
	virtual void Load(ActionLoader* prevHandler, const IXmlHandler::Attributes& attributes);

	virtual void StartElement(const std::wstring& name, const Attributes& atts) {}
	virtual void EndElement(const std::wstring& name);
	virtual void CharacterData(const std::wstring& name) {}

	virtual bool IsError() const { return (error.empty() == false); }
	virtual const wchar_t* GetError() const { return error.c_str(); }

protected:
	std::wstring error;
	ActionLoader* handler;

	std::wstring report;
};

static PutLoader putLoader;
static ExecuteLoader execLoader;
static RunReportLoader rrLoader;

Action::Action(const std::vector<std::wstring>& p, const std::vector<IActionExecutor*>& a) : params(p), actions(a)
{
}

Action::~Action()
{
   std::vector<IActionExecutor*>::iterator i = actions.begin();
   for( ; i != actions.end(); i++ )
      delete (*i);
}

bool Action::Do(GRServer::Session *session, SessionObject* thisObject, const std::vector<Token> &params)
{
   bool res = true;
   std::vector<IActionExecutor*>::iterator i = actions.begin();
   for( ; i != actions.end(); i++ )
   {
      if( (*i)->Do(session, thisObject, params, *this) == false )
      {
         res = false;
         break;
      }
   }

   return res;
}

//
//----------------------- ActionResolver --------------------------
//
ActionResolver::ActionResolver(const std::vector<std::wstring>& prmName, const std::vector<Token> &inParams)
{
   for( int i=(int)(prmName.size()-1); (int)i>=0; i-- )
   {
      params[prmName[i]] = inParams[i];
   }
}

bool ActionResolver::SetObject(SessionObject* so, Session* session, SessionObject* src, const std::wstring& expr)
{
	if( so == src )
		return true;

   this->session = session;
   params[L"source"] = src;

	Token res;
	const wchar_t *p = expr.c_str();
	const wchar_t *ep = p + expr.size();
	StringStream ss(p, ep);

	return ParseStr(&res, ss, so, *(IResolver*)this, L";", ((src != NULL) ? src->NeedDebug() : false));

	//bool ret = true;

	//int si = 0;
	//int di = so->size();
	//for( ; si < (int)src->size() && ret; si++, di++ )
	//{
	//	Token res;
	//	const wchar_t *p = expr.c_str();
	//	const wchar_t *ep = p + expr.size();
	//	StringStream ss(p, ep);

	//	so->AddObject();
	//	so->SetCurrentObject(di);
	//	src->SetCurrentObject(si);

	//	ret = ParseStr(&res, ss, so, *(IResolver*)this, L";");
	//}

	//return ret;
}

bool ActionResolver::ParseParam(Token *dest, Session* session, SessionObject* src, const std::wstring& expr)
{
   this->session = session;

   const wchar_t *p = expr.c_str();
   const wchar_t *ep = p + expr.size();
   StringStream ss(p, ep);

   params[L"source"] = src;
   return ParseStr(dest, ss, NULL, *(IResolver*)this, L"");
}

bool ActionResolver::Resolve(Token *dest, StringStream &stream, const std::wstring &val, const SessionObject *thisObject) const
{
   stream.EatWhite();
   wchar_t nextSym = stream.Next();

   bool ret = false;
   if( nextSym == L'=' )
      ret = session->Resolve(dest, val, thisObject, false);
   else if( nextSym == L'(' )
   {
      stream.MoveNext();
      ret = DoFunction(dest, stream, val, session, thisObject);
   } else
   {
      size_t ppos = val.find(L'.');
      std::wstring prm(val.substr(0, ppos));
      std::map<std::wstring, Token>::const_iterator fnd = params.find(prm);
      if( fnd != params.end() )
      {
			if( ppos != std::wstring::npos )
         {
            if( (fnd->second).type == Token::ttServObject )
            {
               ret = (fnd->second).value.object->GetValue(dest, val.substr(ppos+1), true);
            }
         } else
         {
            *dest = fnd->second;
            ret = true;
         }
      } else
      {
         ret = session->Resolve(dest, val, thisObject, false);
      }
   }

   return ret;
}

bool ActionResolver:: EndStatement(Token &result, StringStream &stream, wchar_t endSym)
{
   return false;
}

//
//----------------------- PutAction --------------------------
//
PutLoader::PutLoader()
{
   Action::Register(L"put", this);
}

void PutLoader::Load(ActionLoader* prevHandler, const IXmlHandler::Attributes& atts)
{
   error.clear();

   handler = prevHandler;
   handler->owner->SetHandler(this);

   params.clear();

   std::wstring val;
   if( !atts.Find(&object, L"object") )
   {
      error = L"Нет тэга object.action";
   } else
   {
      type = stCommon;
      if( atts.Find(&val, L"sourceType") )
         type = SourceTypeFromString(val);
   }
}

void PutLoader::EndElement(const std::wstring& name)
{
   if( error.empty() )
      handler->Add(new PutAction(type, object, params));

	handler->owner->SetHandler(handler);
}

void PutLoader::CharacterData(const std::wstring& name)
{
   if( error.empty() )
   {
      params += name;
   }
}

PutAction::PutAction(SourceType type, const std::wstring& object, const std::wstring& params)
{
   this->type = type;
   this->object = object;
   this->params = params;
}

bool PutAction::Do(Session* session, SessionObject* sourceObject, const std::vector<Token>& params, Action& action)
{
   const std::vector<std::wstring>& pNames = action.Params();
	if (pNames.size() != params.size() || object.empty())
	{
		//return false;
		return true;
	}

   ActionResolver ar(pNames, params);
   std::wstring objectName;
   SessionObject *so = NULL;

   bool ret = true;
   if( *object.begin() == L'$' )
   {
      Token var;
      if( ar.ParseParam(&var, session, sourceObject, object) )
      {
         if( var.type == Token::ttServObject )
            so = var.value.object;
         else if( var.type == Token::ttString )
            objectName = *var.value.str;
			else
			{
				ret = false;
			}
		}
		else
		{
			ret = false;
		}
   } else
      objectName = object;

   if( ret )
   {
      if( so == NULL )
      {
		   so = session->FindInTemp(objectName);
			if (so == NULL)
			{
				so = session->Build(objectName, false);
				session->AddToTemp(so);
			}
      }

		if (so == NULL)
		{
			ret = false;
			if (sourceObject->NeedDebug())
			{
				USES_CONVERSION;
				gServer->AddLog(IErrorLogger::Full, "PutAction no object (%s)", W2A(objectName.c_str()));
			}
		}
		else
		{
			ObjectSource *src = so->GetSource();
			if (src->type != type)
			{
				// пишет только если тип истоцника другой иначе получается цикл
				if (!ar.SetObject(so, session, sourceObject, this->params))
				{
					ret = false;
					if (sourceObject->NeedDebug())
					{
						USES_CONVERSION;
						gServer->AddLog(IErrorLogger::Full, "PutAction can't set params");
					}
				}
				else
				{
					if ((src->writer == NULL && !so->CreateWriter(NULL, type)) || !so->Write(true))
					{
						ret = false;
						if (sourceObject->NeedDebug())
						{
							USES_CONVERSION;
							gServer->AddLog(IErrorLogger::Full, "PutAction can't write");
						}
					}
				}
			}
		}

      //if( so == NULL || !ar.SetObject(so, session, sourceObject, this->params) || !so->CreateWriter(NULL, type) || !so->Write(true) )
      //   ret = false;

      //so->CloseWriter();
      //if( removeObject )
      //   delete so;
	}
	else
	{
		//if (sourceObject->NeedDebug())
		//{
		//	USES_CONVERSION;
		//	gServer->AddLog(IErrorLogger::Full, "PutAction error parse obj(%s)", W2A(object.c_str()));
		//}

	}
   //return ret;
	return true;
}

//
//----------------------- ExecuteAction --------------------------
//
ExecuteLoader::ExecuteLoader()
{
   Action::Register(L"execute", this);
}

void ExecuteLoader::Load(ActionLoader* prevHandler, const IXmlHandler::Attributes& atts)
{
   error.clear();

   handler = prevHandler;
	handler->owner->SetHandler(this);
}

void ExecuteLoader::CharacterData(const std::wstring& name)
{
   if( error.empty() )
   {
      stmt = name;
   }
}

void ExecuteLoader::EndElement(const std::wstring& name)
{
   if( error.empty() )
      handler->Add(new ExecuteAction(stmt));

	handler->owner->SetHandler(handler);
}

ExecuteAction::ExecuteAction(const std::wstring& stmt)
{
   this->stmt = stmt;
}

bool ExecuteAction::Do(Session* session, SessionObject* sourceObject, const std::vector<Token>& params, Action& action)
{
   CString *stmt = NULL;
   if( session->Parse(&stmt, this->stmt, sourceObject) == false )
      return false;

   bool ret = internalDataSource->Execute(stmt->c_str(), session);
   delete stmt;
   return ret;
}

//
//----------------------- RunReportAction --------------------------
//
RunReportLoader::RunReportLoader()
{
	Action::Register(L"runReport", this);
}

void RunReportLoader::Load(ActionLoader* prevHandler, const IXmlHandler::Attributes& atts)
{
	error.clear();
	std::wstring str;
	if (atts.Find(&str, L"name"))
		report = str;

	handler = prevHandler;
	handler->owner->SetHandler(this);
}

void RunReportLoader::EndElement(const std::wstring& name)
{
	if (error.empty() && !report.empty())
		handler->Add(new RunReportAction(report));

	handler->owner->SetHandler(handler);
}

RunReportAction::RunReportAction(const std::wstring& report)
{
	this->report = report;
}

bool RunReportAction::Do(Session* session, SessionObject* sourceObject, const std::vector<Token>& params, Action& action)
{
	CString *stmt = NULL;
	Member repName;
	CString rn(report);
	repName.str = &rn;
	return session->GetDispatcher()->HandleCommand(GET_REPORT, &repName, session);
}

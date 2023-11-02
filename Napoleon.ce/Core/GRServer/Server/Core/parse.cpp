/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Session::Parse & Token impl
 *
 * ert   29/09/2009   creating
 */
#include "stdafx.h"
#include "sessobj.h"
#include "session.h"
#include "objdef.h"
#include "parse.h"
#include "objects.h"
#include <atlconv.h>

#include "grftrs.h"

using namespace GRServer;


//
//------------------------------------------ StringStream ----------------------------------------------------
//
bool StringStream::EatWhite()
{
   while( !EOS() )
   {
      if( !IsSpace(*startI) ) return true;
      startI++;
   }
   return false;
}

bool StringStream::CopyNumber(double *dest)
{
   std::wstring tstr;
   const wchar_t* i = startI;
   while( !EOS() )
   {
      wchar_t sym = (*i);
      if( iswdigit(sym) || sym == L'-' || sym == L'.' || toupper(sym) == L'E' )
         tstr.append(1, (*i));
      else
         break;
      i++;
   }
   if( tstr.empty() ) return false;

   const wchar_t *ep;
   *dest = wcstod(tstr.c_str(), (wchar_t**)&ep);
   while( *ep != '\0' )
   {
      ep++;
      i--;
   }

   // мы находимся на первом символе после числа, перейдем на последний символ числа
   if( startI != i ) i--;
   startI = i;
   return true;
}

bool StringStream::CopyUntilSpace(std::wstring* str)
{
   while( !EOS() )
   {
      wchar_t sym = *startI;
      if( IsSpace(sym) )
         break;

      str->append(1, sym);
      startI++;
   }

   return true;
}

bool StringStream::CheckString(const wchar_t *str, bool ignoreCase)
{
	wchar_t bufs[2], bufd[2];
	bufs[1] = 0;
	bufd[1] = 0;

	wchar_t dest = *str;
   const wchar_t* i = startI;
   while( !EOS() )
   {
      dest = *str++;
      if( dest == '\0' )
         break;

      wchar_t sym = (*i);
      if( ignoreCase )
      {
#ifdef UNIX
         if( towupper(sym) != towupper(dest) )
            break;
#else
			*bufs = sym;
			*bufd = dest;
			CharUpperW(bufs);
			CharUpperW(bufd);
			if (*bufs != *bufd)
				break;
         //WORD s = LOWORD((DWORD)CharUpperW((LPWSTR)MAKELONG(sym, 0)));
         //WORD d = LOWORD((DWORD)CharUpperW((LPWSTR)MAKELONG(dest, 0)));
         //if( d != s )
         //   break;
#endif
      } else
      {
         if( sym != dest )
            break;
      }
      i++;
   }

   if( dest == '\0' )
   {
      startI = i;
      return true;
   }
   return false;
}

//
//------------------------------------------ Session::Parse ----------------------------------------------------
//
static void CopyVarName(std::wstring* str, StringStream& stream)
{
   while(true)
   {
      stream.MoveNext();
      if( stream.EOS() ) break;

      wchar_t sym = stream.Current();
      if( iswalnum(sym) || sym == L'.' || sym == L'_' || sym == L']' )
         str->append(1, sym);
      else
      {
         stream.Back();
         break;
      }
   }
}

bool GRServer::SymbolHandler(wchar_t sym, Token *dest, StringStream &stream, const SessionObject* object, IResolver &resolver, const wchar_t *endStmt, bool needDebug)
{
	bool error = false, done = false, ret = true;
	std::wstring var, val;
	Token t;
	TResolver tresolver(resolver);

	switch (sym)
	{
	case L'$':
		var.clear();
		CopyVarName(&var, stream);
		error = !resolver.Resolve(dest, stream, var, object);
		if (error && needDebug)
		{
			USES_CONVERSION;
			gServer->AddLog(IErrorLogger::Full, "Can't resolve (%s)", W2A(var.c_str()));
		}
		break;
	case L'(':
		stream.MoveNext();
		error = !ParseStr(dest, stream, object, tresolver, L")");
		break;
	case L'+':
		stream.MoveNext();
		error = (!ParseStr(&t, stream, object, tresolver, endStmt) || !dest->Add(t));
		if (error && needDebug)
		{
			USES_CONVERSION;
			std::wstring tv;
			t.ToString(&tv);
			gServer->AddLog(IErrorLogger::Full, "Can't plus %s", W2A(tv.c_str()));
		}
		if (!stream.EOS()) stream.Back();
		break;
	case L'-':
		stream.MoveNext();
		error = (!ParseStr(&t, stream, object, tresolver, endStmt) || !dest->Sub(t));
		if (error && needDebug)
		{
			USES_CONVERSION;
			std::wstring tv;
			t.ToString(&tv);
			gServer->AddLog(IErrorLogger::Full, "Can't minus %s", W2A(tv.c_str()));
		}
		if (!stream.EOS()) stream.Back();
		break;
	case L'*':
		stream.MoveNext();
		error = (!ParseStr(&t, stream, object, tresolver, endStmt) || !dest->Mul(t));
		if (error && needDebug)
		{
			USES_CONVERSION;
			std::wstring tv;
			t.ToString(&tv);
			gServer->AddLog(IErrorLogger::Full, "Can't mul %s", W2A(tv.c_str()));
		}
		if (!stream.EOS()) stream.Back();
		break;
	case L'=':
		stream.MoveNext();
		error = (!ParseStr(&t, stream, object, tresolver, endStmt) || !dest->Assign(t));
		if (error && needDebug)
		{
			USES_CONVERSION;
			std::wstring tv;
			t.ToString(&tv);
			gServer->AddLog(IErrorLogger::Full, "Can't assign %s", W2A(tv.c_str()));
		}
		if (!stream.EOS()) stream.Back();
		break;

	default:
		ret = false;
	}

	return ret;
}

bool GRServer::ParseStr(Token *dest, StringStream &stream, const SessionObject* object, IResolver &resolver, const wchar_t *endStmt, bool needDebug, TSymHandler handler)
{
   bool error = false, done = false;
   std::wstring var, val;
   Token t;
   TResolver tresolver(resolver);

   for( ; !done && !stream.EOS() && !error; stream.MoveNext() )
   {
      if( !stream.EatWhite() )
         break;

      wchar_t sym = stream.Current();
      if( sym == L'"' || sym == L'\'' )
      {
         stream.MoveNext();
         val.clear();
         stream.CopyUntill(&val, sym);
         (*dest) = val;
         continue;
      }
		if (handler(sym, dest, stream, object, resolver, endStmt, needDebug))
			continue;
		if (wcschr(endStmt, sym) != 0)
		{
			bool retOnEndStmt = resolver.EndStatement(*dest, stream, sym);
			if (retOnEndStmt)
			{
				done = true;
				stream.Back();
			}
			else
				dest->Clear();
		}
		else if (iswdigit(sym))
		{
			double dval;
			stream.CopyNumber(&dval);
			(*dest) = dval;
		}
		else
		{
			var.clear();
			var.append(1, sym);
			CopyVarName(&var, stream);
			error = !resolver.Resolve(dest, stream, var, object);
			if (error && needDebug)
			{
				USES_CONVERSION;
				gServer->AddLog(IErrorLogger::Full, "Can't resolve (%s)", W2A(var.c_str()));
			}
		}
		//switch (sym)
  //    {
   //   case L'$':
   //      var.clear();
   //      CopyVarName(&var, stream);
   //      error = !resolver.Resolve(dest, stream, var, object);
			//if (error && needDebug)
			//{
			//	USES_CONVERSION;
			//	gServer->AddLog(IErrorLogger::Full, "Can't resolve (%s)", W2A(var.c_str()));
			//}
   //      break;
   //   case L'(':
   //      stream.MoveNext();
   //      error = !ParseStr(dest, stream, object, tresolver, L")");
   //      break;
   //   case L'+':
   //      stream.MoveNext();
   //      error = (!ParseStr(&t, stream, object, tresolver, endStmt) || !dest->Add(t));
			//if (error && needDebug)
			//{
			//	USES_CONVERSION;
			//	std::wstring tv;
			//	t.ToString(&tv);
			//	gServer->AddLog(IErrorLogger::Full, "Can't plus %s", W2A(tv.c_str()));
			//}
			//if (!stream.EOS()) stream.Back();
   //      break;
   //   case L'-':
   //      stream.MoveNext();
   //      error = (!ParseStr(&t, stream, object, tresolver, endStmt) || !dest->Sub(t));
			//if (error && needDebug)
			//{
			//	USES_CONVERSION;
			//	std::wstring tv;
			//	t.ToString(&tv);
			//	gServer->AddLog(IErrorLogger::Full, "Can't minus %s", W2A(tv.c_str()));
			//}
			//if (!stream.EOS()) stream.Back();
   //      break;
   //   case L'*':
   //      stream.MoveNext();
   //      error = (!ParseStr(&t, stream, object, tresolver, endStmt) || !dest->Mul(t));
			//if (error && needDebug)
			//{
			//	USES_CONVERSION;
			//	std::wstring tv;
			//	t.ToString(&tv);
			//	gServer->AddLog(IErrorLogger::Full, "Can't mul %s", W2A(tv.c_str()));
			//}
			//if (!stream.EOS()) stream.Back();
   //      break;
   //   case L'=':
   //      stream.MoveNext();
   //      error = (!ParseStr(&t, stream, object, tresolver, endStmt) || !dest->Assign(t));
			//if (error && needDebug)
			//{
			//	USES_CONVERSION;
			//	std::wstring tv;
			//	t.ToString(&tv);
			//	gServer->AddLog(IErrorLogger::Full, "Can't assign %s", W2A(tv.c_str()));
			//}
			//if (!stream.EOS()) stream.Back();
   //      break;
   //   default:
   //      if( wcschr(endStmt, sym) != 0 )
   //      {
   //         bool retOnEndStmt = resolver.EndStatement(*dest, stream, sym);
   //         if( retOnEndStmt )
   //         {
   //            done = true;
   //            stream.Back();
   //         } else
   //            dest->Clear();
   //      } else if( iswdigit(sym) )
   //      {
   //         double dval;
   //         stream.CopyNumber(&dval);
   //         (*dest) = dval;
   //      } else
   //      {
   //         var.clear();
   //         var.append(1, sym);
   //         CopyVarName(&var, stream);
   //         error = !resolver.Resolve(dest, stream, var, object);
			//	if (error && needDebug)
			//	{
			//		USES_CONVERSION;
			//		gServer->AddLog(IErrorLogger::Full, "Can't resolve (%s)", W2A(var.c_str()));
			//	}
			//}
   //      break;
   //   }
   }
	return !error;
}

bool Session::Parse(Token* res, const std::wstring& expr, const ISessionObject* thisObject) const
{
   const wchar_t *p = expr.c_str();
   const wchar_t *ep = p + expr.size();
   StringStream ss(p, ep);
   return ParseStr(res, ss, (const SessionObject*)thisObject->Self(), *(IResolver*)this);
}

bool CompareHandler(wchar_t sym, Token *dest, StringStream &stream, const SessionObject* object, IResolver &resolver, const wchar_t *endStmt, bool needDebug)
{
	bool error = false, done = false;
	std::wstring var, val;
	Token t;
	TResolver tresolver(resolver);

	switch (sym)
	{
	case '=':
	{
		stream.MoveNext();
		if (ParseStr(&t, stream, object, tresolver, endStmt, needDebug, CompareHandler))
		{
			int cmp = dest->Compare(t);
			dest->Assign(cmp == 0);
		}
		else
		{
			error = true;
		}
		if (error && needDebug)
		{
			USES_CONVERSION;
			std::wstring tv;
			t.ToString(&tv);
			gServer->AddLog(IErrorLogger::Full, "Wrong compare %s", W2A(tv.c_str()));
		}
		if (!stream.EOS()) stream.Back();
		break;
	}

	default:
		return SymbolHandler(sym, dest, stream, object, resolver, endStmt, needDebug);
	}
	return error;
}

bool Session::CheckCondition(const std::wstring& expr, const ISessionObject* thisObject) const
{
	Token res;
	const wchar_t *p = expr.c_str();
	const wchar_t *ep = p + expr.size();
	StringStream ss(p, ep);
	ParseStr(&res, ss, (const SessionObject*)thisObject->Self(), *(IResolver*)this, L"", false, CompareHandler);
	return (res.type == Token::ttBoolean && res.value.result);
}

bool Session::Resolve(Token *dest, StringStream &stream, const std::wstring &val, const SessionObject *thisObject) const
{
   stream.EatWhite();
   if( stream.Next() == L'(' )
   {
      stream.MoveNext();
      return DoFunction(dest, stream, val, const_cast<Session*>(this), thisObject);
   }

   return Resolve(dest, val, thisObject, true);
}

static bool IsParam(const std::wstring& val) {
   std::wstring::const_iterator i = val.begin();
   for (; i != val.end(); i++) {
      if (!iswdigit(*i))
         return false;
   }
   return true;
}

bool Session::Resolve(Token* dest, const std::wstring& val, const SessionObject* thisObject, bool toValue) const
{
   // special case for func param resolve
   // UserDataFilter('idItem','Price', $01, 'and')
   const ParamHelper* ph;
   if (IsParam(val) && thisObject && (ph = thisObject->GetParamHelper()) != NULL)
   {
      CString* res = ph->Substitute(val.c_str(), false);
      if (res != NULL)
      {
         (*dest) = (*res);
         delete res;
         return true;
      }
   }

   size_t ppos = val.find(L'.');
   std::wstring tval(val);
   do
   {
      std::wstring obj = tval.substr(0, ppos);

		if (obj.compare(L"features") == 0)
		{
			bool ret = false;
			if (ppos != std::wstring::npos)
			{
				tval = tval.substr(ppos + 1);
				ret = GetFeatureValue(dest, tval);
			}
			return ret;
		}

      const SessionObject* so = (_wcsicmp(obj.c_str(), L"object") == 0) ? thisObject :
         FindObject(obj, thisObject);

      if( so == NULL )
         return false;
		if( ppos == std::wstring::npos )
      {
         (*dest) = (SessionObject*)so;
         return true;
      }

      tval = tval.substr(ppos + 1);
      size_t chkPos = tval.find(L'.');
		if( chkPos > 0 && chkPos != std::wstring::npos )
      {
         // есть еще один объект разрешим и его
         ppos = chkPos;
         thisObject = so;
         continue;
      }

      if( ppos >= 0 )
         return so->GetValue(dest, tval, toValue);
   } while(true);
}

bool Session::EndStatement(Token &result, StringStream &stream, wchar_t sym)
{
   return false;
}

const SessionObject* Session::FindObject(const std::wstring& name, const ISessionObject* thisObject) const
{
   const wchar_t *tname = name.c_str();

   if( thisObject != NULL )
   {
		if (_wcsicmp(tname, L"parent") == 0)
		{
			ISessionObject * so = thisObject->Parent();
			return (so == NULL) ? NULL : (const SessionObject*)so->Self();
		}
   }

   if( _wcsicmp(tname, L"user") == 0 )
      return user;

   ExchangeList::const_iterator i = response.begin();
   for( ; i != response.end(); i++ )
   {
      if( (*i)->Name().compare(tname) == 0 )
         return (const SessionObject*)((const ServObject*)(*i));
   }

   i = ack.begin();
   for( ; i != ack.end(); i++ )
   {
      if( (*i)->Name().compare(tname) == 0 )
         return (const SessionObject*)((const ServObject*)(*i));
   }

   return NULL;
}

bool SessionObject::GetValue(Token* dest, const std::wstring& member, bool toValue) const
{
   if( _wcsicmp(member.c_str(), L"source") == 0 )
   {
      dest->value.source = (ObjectSource*)&source;
      dest->type = Token::ttSource;
      return true;
   }

	int mi = format->FindMember(member.c_str());
	if (_wcsicmp(member.c_str(), L"type") == 0)
   {
		// если в документе завести поле type то стрельнет.
		if (mi < 0)
		{
			(*dest) = format->name;
			return true;
		}
   }

	//if (mi < 0)
	//{
	//	USES_CONVERSION;
	//	gServer->AddLog(IErrorLogger::Full, "(%s) no member (%s) all(%d)", W2A(format->name.c_str()), W2A(member.c_str()), format->size());
	//	Format::const_iterator i = format->begin();
	//	for (; i != format->end(); i++)
	//	{
	//		gServer->AddLog(IErrorLogger::Full, " have member (%s)", W2A(i->name.c_str()));
	//	}
	//}

   if( !toValue )
   {
      if( mi >= 0 )
      {
         Object* o;
         if( size() == 0 )
         {
            o = Object::Create(*format);
            const_cast<SessionObject*>(this)->push_back(o);
         } else
         {
            int index = curObjIndex;
            if( index >= (int)size() )
               index = (int)size() - 1;
            o = (Object*)const_cast<SessionObject*>(this)->at(index);
         }

         MemberData md;
         md.format = &format->at(mi);
         md.member = &o->at(mi);

         (*dest) = md;

         return true;
      }

      return false;
   }

   if( size() > 0 )
   {
      bool finded = false;

      int index = curObjIndex;
      if( index >= (int)size() )
         index = (int)size() - 1;
      const Object& o = *at(index);

      const Object::Field* f = o.GetField(member.c_str());

      if( f != NULL )
      {
         switch(f->format.type)
         {
         case MemberFormat::mtNumber:
            (*dest) = f->member.number;
            finded = true;
            break;
         case MemberFormat::mtString:
            (*dest) = (const std::wstring&)*f->member.str;
            finded = true;
            break;
         case MemberFormat::mtDateTime:
            (*dest) = f->member.datetime;
            finded = true;
            break;
         case MemberFormat::mtBinary:
            (*dest) = f->member.binary;
            finded = true;
            break;
         default: break;
         }

         delete f;
         if( finded ) return true;
      }
   }
   if( objDef == NULL ) return false;

   ObjectDef::Members::const_iterator i = objDef->members.begin();
   for( ; i != objDef->members.end(); i++ )
      if( _wcsicmp(i->first.c_str(), member.c_str()) == 0 )
      {
         return session->Parse(dest, i->second, this);
      }
   return false;
}

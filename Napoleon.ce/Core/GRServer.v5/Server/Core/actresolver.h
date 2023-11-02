/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * ActionResolver
 *
 * ert   03/07/2010   creating
 */ 
#ifndef __ACION_RESOLVER_H
#define __ACION_RESOLVER_H

namespace GRServer {

class ActionResolver : public IResolver
{
public:
   ActionResolver(const std::vector<std::wstring>& params, const std::vector<Token> &inParams);

   bool SetObject(SessionObject* so, Session* session, SessionObject* sourceObject, const std::wstring& expr);
   bool ParseParam(Token *dest, Session* session, SessionObject* sourceObject, const std::wstring& expr);

   virtual bool Resolve(Token *dest, StringStream &stream, const std::wstring &val, const SessionObject *thisObject) const;
   virtual bool EndStatement(Token &result, StringStream &stream, wchar_t endSym);

protected:
   std::map<std::wstring, Token> params;
   Session* session;
};

} // namespace GRServer

#endif
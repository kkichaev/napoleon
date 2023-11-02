/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Parser
 *
 * ert   12/03/2010   creating
 */ 
#ifndef _GR_PARSER_H
#define _GR_PARSER_H

#include "token.h"
#include <pstream.h>

namespace GRServer {

class SessionObject;
class Session;

class StringStream : public ParseStreamW
{
public:
   StringStream(const std::wstring& str) : ParseStreamW(str.c_str(), str.c_str() + str.size()) {}
   StringStream(const wchar_t *s, const wchar_t *e) : ParseStreamW(s, e) {}

   bool EatWhite();
   bool CopyNumber(double *dest);
   bool CheckString(const wchar_t *str, bool ignoreCase);

   bool CopyUntilSpace(std::wstring* str);

   void Back() { startI--; }
};

struct IResolver
{
   virtual bool Resolve(Token *dest, StringStream &stream, const std::wstring &val, const SessionObject *thisObject) const = 0;
   virtual bool EndStatement(Token &result, StringStream &stream, wchar_t endSym) = 0;
};

struct IFunction
{
   virtual const wchar_t* Name() const = 0;
   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject) = 0;
   virtual void Close() {}
};

bool ParseStr(Token *dest, StringStream &stream, const SessionObject* thisObject, IResolver &resolver,
              const wchar_t *endStmt = L"", bool needDebug = false);

bool DoFunction(Token* res, StringStream& stream, const std::wstring& funcName, Session* session, const SessionObject *thisObject);

void InitFunctions();
void CloseFunctions();
bool AddFunction(IFunction* f);

class ParamResolver : public IResolver
{
public:
   ParamResolver(std::vector<Token>* params, Session* session, const SessionObject* object);

   bool Do(const std::wstring& str);
   bool Do(StringStream& str);

   virtual bool Resolve(Token *dest, StringStream &stream, const std::wstring &val, const SessionObject* thisObject) const;
   virtual bool EndStatement(Token &result, StringStream &stream, wchar_t endSym);

protected:
   std::vector<Token>* params;
   Session* session;
   const SessionObject* object;
};

class TResolver : public IResolver
{
public:
   TResolver(IResolver& _parent) : parent(_parent)
   {
   }

   virtual bool Resolve(Token *dest, StringStream &stream, const std::wstring &val, const SessionObject *thisObject) const
   {
      return parent.Resolve(dest, stream, val, thisObject);
   }

   virtual bool EndStatement(Token &result, StringStream &stream, wchar_t endSym) { return true; }

protected:
   const IResolver& parent;
};

} // namespace GRServer

#endif

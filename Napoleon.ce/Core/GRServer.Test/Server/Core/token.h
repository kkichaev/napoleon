/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Token
 *
 * ert   10/03/2010   выделение из servobj.h
 */ 
#ifndef _GR_TOKEN_H
#define _GR_TOKEN_H

#include <string>

struct IBinary;

namespace GRServer {

class SessionObject;
struct ObjectSource;
struct MemberFormat;
class MemoryBinary;
union Member;

struct MemberData
{
   Member *member;
   MemberFormat *format;

   bool CopyFrom(const MemberData& src);
};


//
// класс Token сделан не связанным с Member & MemberFormat, они не идентичны
//
struct Token
{
   enum Type { ttNone, ttString, ttNumber, ttDateTime, ttServObject, ttSource, ttMember, ttBinary };

   Token();
   Token(const Token& src);

   ~Token();

   Token& operator= (const Token& token);
   Token& operator= (double val);
   Token& operator= (const FILETIME& val);
   Token& operator= (const std::wstring& val);
   Token& operator= (SessionObject* obj);
   Token& operator= (const MemberData& data);
   Token& operator= (const IBinary* data); // данные копируются

   bool Add(const Token& token);
   bool Sub(const Token& token);
   bool Mul(const Token& token);
   bool Assign(const Token& token);
   
   bool ToString(std::wstring* value) const;
   bool ToNumber(double* value) const;

   bool CopyTo(MemberData *md) const;

   void Clear();

   Type type;

   union
   {
      double         number;
      FILETIME       datetime;
      std::wstring*  str;
      SessionObject* object;
      ObjectSource*  source;
      MemberData     member;
      MemoryBinary*  binary;
   } value;
};

} // namespace GRServer

#endif

/*
 * Copyright (C), 2006-2010, Денис Мосягин
 *
 * реализация работы с БД
 *
 *  ert   04/09/2010   creating
 */ 
#ifndef __NAPOLEON_OBJECT_IMPL_H
#define __NAPOLEON_OBJECT_IMPL_H

#include <Exchange.h>
#include <DBImpl.h>

class AgentImpl : public DBImpl<Agents>
{
public:
   AgentImpl() : DBImpl(L"agents") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class ServerImpl : public DBImpl<Server>
{
public:
   ServerImpl() : DBImpl(L"servers") {}
   virtual const wchar_t*  KeyFields() const { return L""; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class PriceImpl : public DBImpl<Price>
{
public:
   PriceImpl() : DBImpl(L"price"), isWeight(false), weight(0) {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }

   bool Get(const wchar_t* barcode, bool searchExact);
   bool IsWeight() const { return isWeight; }
   int GetWeight() const { return weight; }

private:
   bool isWeight;
   int weight;
};

class PriceRcvImpl : public DBImpl<PriceRcv>
{
public:
   PriceRcvImpl() : DBImpl(L"") {}
   virtual const wchar_t*  KeyFields() const { return NULL; }
   virtual const wchar_t** Indexes() const { return NULL; }
};




class DocTypeImpl : public DBImpl<DocTypes>
{
public:
   DocTypeImpl() : DBImpl(L"doctypes") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class WhAgentsImpl : public DBImpl<WhAgents>
{
public:
   WhAgentsImpl() : DBImpl(L"agents") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class ConfigImpl : public DBImpl<Config>
{
public:
   ConfigImpl() : DBImpl(L"config") {}
   virtual const wchar_t*  KeyFields() const { return L"key"; }
   virtual const wchar_t** Indexes() const { return NULL; }

   const wchar_t* LoadIP(const wchar_t* ip);

   // Значение ip в LoadIP
   static const wchar_t* IP1;
   static const wchar_t* IP2;
};
#endif

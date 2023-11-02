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

class AgentsImpl : public DBImpl<Agents>
{
public:
   AgentsImpl() : DBImpl(L"agents") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class PartnerImpl : public DBImpl<Partner>
{
public:
   PartnerImpl() : DBImpl(L"orgs") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class BoardsImpl : public DBImpl<Boards>
{
public:
   BoardsImpl() : DBImpl(L"boards") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class SkladImpl : public DBImpl<Sklad>
{
public:
   SkladImpl() : DBImpl(L"skald") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class WhDocImpl : public DBImpl<WHDoc>
{
public:
   WhDocImpl();

   virtual const wchar_t*  KeyFields() const { return L"created"; }
   virtual const wchar_t** Indexes() const { return NULL; }

   DWORD GetQty(const wchar_t* board, const wchar_t* item) const;

   void Add(const wchar_t* board, const wchar_t* item, DWORD qty); // 0 - remove
};

#endif

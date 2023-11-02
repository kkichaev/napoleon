/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * реализация работы с БД
 *
 *  ert   12/06/2009   creating
 */ 
#ifndef __NAPOLEON_OBJECT_IMPL_H
#define __NAPOLEON_OBJECT_IMPL_H

#include <Exchange.h>
#include <DBImpl.h>

class FolderImpl : public DBImpl<FolderObj>
{
public:
   FolderImpl() : DBImpl(L"folders") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class OrgImpl : public DBImpl<Org>
{
public:
   OrgImpl() : DBImpl(L"orgs") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { static const wchar_t *index[] = { L"name", NULL }; return index; }
};

class OrgFolderImpl : public DBImpl<OrgFolder>
{
public:
   OrgFolderImpl() : DBImpl(L"orgfolders") {}
   virtual const wchar_t*  KeyFields() const { return L"name"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class PriceImpl : public DBImpl<Price>
{
public:
   PriceImpl() : DBImpl(L"price") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { static const wchar_t *index[] = { L"folderID,name", NULL }; return index; }
};

struct Preference;
class ConfigImpl : public DBImpl<Config>
{
public:
   ConfigImpl() : DBImpl(L"config") {}
   virtual const wchar_t*  KeyFields() const { return L"key"; }
   virtual const wchar_t** Indexes() const { return NULL; }

   const wchar_t* LoadIP(const wchar_t* ip, const Preference& p);

   // Значение ip в LoadIP
   static const wchar_t* IP1;
   static const wchar_t* IP2;
};

class PricePhotoImpl : public DBImpl<PricePhoto>
{
public:
   PricePhotoImpl() : DBImpl(L"pricePhoto") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

#ifdef PRICE_MATRIX
class MatrixImpl : public DBImpl<Matrix>
{
public:
   MatrixImpl() : DBImpl(L"matrix") {}
   virtual const wchar_t*  KeyFields() const { return L"name"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};
#endif

#ifdef RCV_MESSAGE
class MessageImpl : public DBImpl<Message>
{
public:
   MessageImpl() : DBImpl(L"messages") {}
   virtual const wchar_t*  KeyFields() const { return L"date"; }
   virtual const wchar_t** Indexes() const { return NULL; }

   void Show();
};
#endif

#ifdef ORG_NOTE
class OrgNoteImpl : public DBImpl<OrgNote>
{
public:
   OrgNoteImpl() : DBImpl(L"orgNotes") {}

   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};
#endif

#ifdef FIRMS_TABLE
class FirmImpl : public DBImpl<Firm>
{
public:
   FirmImpl() : DBImpl(L"firms") {}

   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};
#endif

#ifdef AGENT_TASK
class TaskCategoryImpl : public DBImpl<TaskCategory>
{
public:
   TaskCategoryImpl() : DBImpl(L"TaskCategory") {}

   virtual const wchar_t*  KeyFields() const { return L"name"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class SVTaskImpl : public DBImpl<SVTask>
{
public:
   SVTaskImpl() : DBImpl(L"AgentTask")
   {
   }

   virtual const wchar_t*  KeyFields() const { return L"id,date,category"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};
#endif

#endif

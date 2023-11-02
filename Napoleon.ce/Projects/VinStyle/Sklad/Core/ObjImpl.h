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

class PriceImpl : public DBImpl<Price>
{
public:
   PriceImpl() : DBImpl(L"price") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }

	static bool GetItems(std::vector<ROWID> *ridList, const wchar_t* barcode);
	bool ReadMark(const wchar_t* mark);
	bool ReadBarcode(const wchar_t* barcode);
};

class WhSkaldRestImpl : public DBImpl<WhSkaldRest>
{
public:
   WhSkaldRestImpl() : DBImpl(L"sklrests") {}
   virtual const wchar_t*  KeyFields() const { return L"id,idRack,idWh"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class WhSkladImpl : public DBImpl<WhSklad>
{
public:
   WhSkladImpl() : DBImpl(L"sklads") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class SkMarksImpl : public DBImpl<SkMarks>
{
public:
	SkMarksImpl() : DBImpl(L"skMarks") {}
   virtual const wchar_t*  KeyFields() const { return L"markBegin,markEnd"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class PartCodeImpl : public DBImpl<PartCode>
{
public:
	PartCodeImpl() : DBImpl(L"PartCode") {}
   virtual const wchar_t*  KeyFields() const { return L"code"; }
   virtual const wchar_t** Indexes() const { return NULL; }

	static void MakePartyCode(std::wstring* dest, const wchar_t* src) { dest->assign(src, 31); }
};

class ControlDocImpl : public DBImpl<ControlDoc>
{
public:
   ControlDocImpl() : DBImpl(L"controlDocs") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }

   const OrderItem* FindItem(const wchar_t* rack, const wchar_t* id) const;
   bool HaveRack(const wchar_t* rack) const;
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

class ItemGroupsImpl : public DBImpl<ItemGroups>
{
public:
	ItemGroupsImpl() : DBImpl(L"ItemGroups") {}

	virtual const wchar_t*  KeyFields() const { return L"barcode"; }
   virtual const wchar_t** Indexes() const { return NULL; }

	void LoadPriceItems(std::vector<std::wstring>* items, const wchar_t* barcode);
	bool HaveItem(const wchar_t* groupID, const wchar_t* itemID);
};

class PalletsImpl : public DBImpl<Pallets>
{
public:
	PalletsImpl() : DBImpl(L"pallets") {}

	virtual const wchar_t*  KeyFields() const { return L"barcode"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class SyncConfigImpl : public DBImpl<Config>
{
public:
	SyncConfigImpl() : DBImpl(L"syncConfig") {}

	virtual const wchar_t*  KeyFields() const { return L"key"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

#endif

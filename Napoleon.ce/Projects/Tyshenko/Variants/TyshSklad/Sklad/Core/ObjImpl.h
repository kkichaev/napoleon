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
	bool ReadBarcode(const wchar_t* barcode);
	bool ReadMark(const wchar_t* mark);
};


class ReqDocAnswerImpl : public DBImpl<ReqDocAnswer>
{
public:
   ReqDocAnswerImpl() : DBImpl(L"") {}
   virtual const wchar_t*  KeyFields() const { return NULL; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

#endif

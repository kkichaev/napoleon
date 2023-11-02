/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * Автоптека add-in
 *
 *  ert   01/06/2009   creating
 */ 
#ifndef __AUTOPTEKA_H
#define __AUTOPTEKA_H

#define COST_FILE ".\\NapoleonCosts"

#include <Exchange.h>
#include <DBImpl.h>
#include <Costs.h>

#ifdef Autopteka
class IncomeImpl : public DBImpl<Incomes>
{
public:
   IncomeImpl() : DBImpl(L"income") {}

   virtual const wchar_t*  KeyFields() const { return L"date"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

void OpenIncomes(const wchar_t* docType);
#endif

extern wchar_t dtOrgInfo[];
extern wchar_t dtStock[];

void OpenOrgInfo(const wchar_t *id);
void OpenOrgStock(const wchar_t *id);

void OpenNote(HWND parent, const wchar_t *orgID, bool openIfExist);
int DateDiff(const FILETIME &ft1, const FILETIME &ft2);

bool CheckSync();
void MarkSynced();
void ClearSyncFile();

#endif

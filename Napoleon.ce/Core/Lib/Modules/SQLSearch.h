/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Полнотекстовый поиск
 *
 *  ert   19/06/2009   creating
 */
#ifndef _SQL_FTSEARCH_H
#define _SQL_FTSEARCH_H

#include <SQLTable.h>

class SQLTextSearcher
{
public:
   SQLTextSearcher();

   void SetData(const std::wstring& table, const std::wstring& field);
   bool Search(std::vector<ROWID> *result, const std::wstring& text, const std::wstring *whereStr = NULL);
   void Clear();

protected:
   std::wstring table;
   std::wstring field;
};

#endif

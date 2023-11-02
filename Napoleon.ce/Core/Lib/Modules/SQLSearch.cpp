/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Полнотекстовый поиск
 *
 *  ert   19/06/2009   creating
 */
#include "stdafx.h"
#include "SQLTable.h"
#include "SQLSearch.h"

SQLTextSearcher::SQLTextSearcher()
{
}

void SQLTextSearcher::SetData(const std::wstring& table, const std::wstring& field)
{
   this->table = table;
   this->field = field;
}

bool SQLTextSearcher::Search(std::vector<ROWID> *outRes, const std::wstring& srchText, const std::wstring *whereStr)
{
   FTSTable t(table);
   if( !t.Searching(outRes, srchText, field, whereStr) )
      return false;

   return true;
}

void SQLTextSearcher::Clear()
{
}

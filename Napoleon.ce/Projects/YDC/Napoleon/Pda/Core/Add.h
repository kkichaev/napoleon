/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Информаиця по организации
 * 
 *  ert   10/09/2009   creating
 */ 
#ifndef _ADD_H
#define _ADD_H

struct StockItem : public IReflectableData
{
   wchar_t *name;
   wchar_t *id;
   DWORD    folder;

   DWORD qty;

   bool operator < (const StockItem &_item) const { return (wcscmp(id, _item.id) < 0); }

   DECLARE_TYPE_REFLECTION(StockItem)
};

DWORD LoadStock(std::vector<StockItem>* res, const wchar_t* id, StringHolder *sh);

#endif

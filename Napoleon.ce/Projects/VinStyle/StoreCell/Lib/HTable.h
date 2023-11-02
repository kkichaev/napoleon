/*
 * Copyright (C), 2007-2008, ƒенис ћос€гин
 *
 * –абота с иерархическими таблицами
 *
 *  ert   13/08/2008   creating
 */

#ifndef __H_TABLE_H
#define __H_TABLE_H

#include <Table.h>

/*
“аблица специального вида имеет колонки id, level, sort, firstID, size
записи при сортировке sort наход€тс€ в иерархическом пор€дке

struct Folder : public IReflectableData
{
   wchar_t *name;
   DWORD    id;
   WORD     size;
   WORD     level;
   DWORD    sort;

   DWORD    firstID; // первый элемент прайс-листа

   DECLARE_TYPE_REFLECTION(Folder);
};


 */
class HTable : public CETable
{
 public:
   HTable(const IDBFormat& _format);

   // возвращает индекс первой записи и кол-во листов в текущей папке и во всех волженных
   void GetLeaf(CEOID folder, DWORD *firstIndex, DWORD *size);
};

#endif

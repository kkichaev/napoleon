/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Работа с матрицами
 *
 *  ert   14/12/2009   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "PriceForm.h"
#include "FormEntries.h"

void PriceFormData::SetMatrix(int matrix)
{
   if( matrix >= (int)matrixes.size() || matrix == curMatrix ) return;

   curMatrix = matrix;
   if( curMatrix == 0 )
   {
      SelectFolder(0, true);
      state &= (~pdInMatrix);
   }
   else
   {
      folders.clear();
      leafs = matrixes[curMatrix].items;
      state |= pdInMatrix;
   }
   //LoadFolder(0);
}

void PriceFormData::LoadMatrix()
{
   curMatrix = 0;
   MatrixImpl dm;

   Matrix m;
   m.name = L"весь прайс";
   matrixes.push_back(m);

   PriceImpl price;

   if( order != NULL )
   {
      OrgImpl oi;
      oi.id = order->id;
      if( oi.Read() )
      {
         m.name = L"матрица клиента";
         vector_t<MatrixItem>::const_iterator pi = oi.matrix.begin();
         for( ; pi != oi.matrix.end(); pi ++ )
         {
            price.id = pi->id;
            if( price.Read(false) )
               m.items.push_back(price.RID());
         }
         matrixes.push_back(m);
      }
   }

   std::vector<ROWID> rids;
   std::vector<ROWID>::iterator i;
   SQLTable t(dm.Name());
   t.RIDList(&rids);

   for( i = rids.begin(); i != rids.end(); i++ )
   {
      dm.Read((*i));

      m.items.clear();
      m.name = dm.name;

      vector_t<MatrixItem>::const_iterator ii = dm.items.begin();
      for( ; ii != dm.items.end(); ii++ )
      {
         price.id = ii->id;
         if( price.Read(false) )
            m.items.push_back(price.RID());
      }

      matrixes.push_back(m);
   }
}

void PriceFormData::Matrixes(std::vector<wchar_t*> *m) const
{
   std::vector<Matrix>::const_iterator i = matrixes.begin();
   for( ; i != matrixes.end(); i++ )
      m->push_back((wchar_t*)i->name.c_str());
}

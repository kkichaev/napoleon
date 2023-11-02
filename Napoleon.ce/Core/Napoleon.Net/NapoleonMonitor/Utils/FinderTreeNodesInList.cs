/*
 * Copyright (C), 2010, Гильдия разработчиков
 *
 * поиск в объекте TreeView
 * 
 * kki   01/09/2010   creating
 */

using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager.Utils
{
   //Икслючение, может возникнуть когда объекту поиска в конструкторе передан пустой список
   class EmptyListForFinderException : Exception { }

   //Метод поиска
   delegate TreeNode FindMethod();

   //Класс реализующий поиск в объекте TreeView
   class FinderTreeNodesInList
   {
      private int index;
      private List<TreeNode> findedList;

      public FinderTreeNodesInList(List<TreeNode> findedList)
      {
         if (findedList.Count <= 0)
            throw new EmptyListForFinderException();

         index = -1;
         this.findedList = findedList;
      }

      //Перейти на след позицию поиска
      public TreeNode Next()
      {
         if (index >= findedList.Count - 1)
            index = -1;

         index++;

         return findedList[index];
      }

      //Перейти на предыдущую позицию поиска
      public TreeNode Prev()
      {
         if (index <= 0)
            index = findedList.Count;

         index--;

         return findedList[index];
      }

      //Возвращает метод поиска в соответствиис направлением
      public FindMethod GetFindMethod(Direction dir)
      {
         switch (dir)
         {
            case Direction.DOWN: return new FindMethod(Prev);
            case Direction.UP: return new FindMethod(Next);
            default: throw new Exception("Unknown direction");
         }
      }
   }

}

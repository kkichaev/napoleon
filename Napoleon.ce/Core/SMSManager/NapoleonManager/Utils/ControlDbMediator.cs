/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Родительский класс для посредника 
 * элементов управления над базой данных
 * 
 * kki   01/12/2010   creating
 */
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager.Utils
{
   abstract class ControlDbMediator
   {
      private bool isDbOpened = false;

      /// <summary>Обновить статус элементов управления/// </summary>
      public abstract void Update();

      /// <summary>
      /// Установит статус как открытое соединение с базой данных/// </summary>
      public virtual void Open()
      {
         isDbOpened = true;
         Update();
      }

      /// <summary>
      /// Возвращает статус было ли открыто соединение с базой данных
      /// </summary>
      /// <returns>статус соединения</returns>
      protected bool isOpen() 
      {
         return isDbOpened;
      }
   }
}

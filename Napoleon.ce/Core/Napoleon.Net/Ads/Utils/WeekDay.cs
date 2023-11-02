/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * День недели
 * 
 * kki   09/02/2011   creating
 */
using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;

namespace GRSoft.NapoleonManager
{
   /// <summary>
   /// Попытка создания объекта WeekDay
   /// из неправильной строки
   /// </summary>
   public class ENonWeekDay : Exception
   {
      public ENonWeekDay(string text) : base("Нет дня недели '" + text + "'")
      {
      }
   }

   /// <summary>
   /// День недели
   /// </summary>
   public class WeekDay
   {
      /// <summary>
      /// Полное имя дней недели
      /// </summary>
      public static string[] fullnames = {"Понедельник", "Вторник", "Среда", 
         "Четверг", "Пятница", "Суббота", "Воскресенье"};

      /// <summary>
      /// Короткое имя дня недели
      /// </summary>
      private string[] shortnames = {"пн", "вт", "ср", "чт", "пт", "сб", "вс"};

      /// <summary>
      /// Числовое представление дня недели пн = 0, вс = 6
      /// </summary>
      private int index;

      /// <summary>
      /// Констрор WeekDay из строкового представления
      /// строка должна быть одной из тех что содержит в себе
      /// массив fullnames
      /// </summary>
      /// <param name="name">Полное имя дня недели например: "Понедельник"</param>
      public WeekDay(string name)
      {
         if (Char.IsNumber(name[0]))
            name = name.Substring(1);

         for (int i = 0; i < fullnames.Length; i++)
         { 
            if (name.Equals(fullnames[i], StringComparison.CurrentCultureIgnoreCase))
            {
               index = i;
               return;
            }
         }

         throw new ENonWeekDay(name);
      }

      public static bool CheckDay(string name)
      {
         if (Char.IsNumber(name[0]))
            name = name.Substring(1);

         for (int i = 0; i < fullnames.Length; i++)
         {
            if (name.Equals(fullnames[i], StringComparison.CurrentCultureIgnoreCase))
               return true;
         }

         return false;
      }

      /// <summary>
      /// Конструктора WeekDay из номера дня недели
      /// пн = 1, вс = 7
      /// </summary>
      /// <param name="number">Номер дня недели</param>
      public WeekDay(int number)
         : this(fullnames[number-1])
      {
      }

      /// <summary>
      /// Конструктор WeekDay из объекта System.DayOfWeek
      /// </summary>
      /// <param name="dayOfWeek">System.DayOfWeek</param>
      public WeekDay(DayOfWeek dayOfWeek) : 
         this(IndexDayOfWeek(dayOfWeek))
      {
      }

      /// <summary>
      /// Получение порядкового номера дня недели
      /// из System.DayOfWeek 
      /// пн = 1, вс = 7
      /// </summary>
      /// <param name="dayOfWeek">System.DayOfWeek</param>
      /// <returns>Номер дня недели</returns>
      private static int IndexDayOfWeek(DayOfWeek dayOfWeek)
      {
         switch (dayOfWeek)
         {
            case DayOfWeek.Monday: return 1;
            case DayOfWeek.Tuesday: return 2;
            case DayOfWeek.Wednesday: return 3;
            case DayOfWeek.Thursday: return 4;
            case DayOfWeek.Friday: return 5;
            case DayOfWeek.Saturday: return 6;
            case DayOfWeek.Sunday: return 7;
            default:
               throw new Exception(String.Format(
                  "DayOfWeek not inmplemented: {0}", dayOfWeek));
         }
      }

      /// <summary>
      /// Полное имя дня недели
      /// "Понедельник", "Вторник", "Среда", ....
      /// </summary>
      public string FullName { get { return fullnames[index]; } }

      /// <summary>
      /// Короткое имя дня недели
      /// "пн", "вт", "ср"...
      /// </summary>
      public string ShortName { get { return shortnames[index]; } }

      /// <summary>
      /// Номер дня недели
      /// пн = 1, вс = 7
      /// </summary>
      public int Number { get { return index + 1; } }

      /// <summary>
      /// Сравнивает объекты
      /// </summary>
      /// <param name="obj">WeekDay</param>
      /// <returns>tru - оба объекта представляют один день недели</returns>
      public override bool Equals(object obj)
      {
         WeekDay wd = obj as WeekDay;
         return wd == null ? false : index == ((WeekDay)obj).index;
      }

      /// <summary>
      /// base.GetHashCode()
      /// </summary>
      /// <returns>hash code</returns>
      public override int GetHashCode()
      {
         return base.GetHashCode();
      }
   }
}

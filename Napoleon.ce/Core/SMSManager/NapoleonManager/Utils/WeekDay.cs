using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;

namespace GRSoft.NapoleonManager.Utils
{
   public class ENonWeekDay : Exception
   {
      public ENonWeekDay(string text) : base("Нет дня недели '" + text + "'")
      {
      }
   }
   public class WeekDay
   {
      private static string[] fullnames = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};
      private string[] shortnames = {"пн", "вт", "ср", "чт", "пт", "сб", "вс"};

      private int index;

      public WeekDay(string name)
      {
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

      public WeekDay(int number)
         : this(fullnames[number-1])
      {
         
      }

      public string FullName { get { return fullnames[index]; } }
      public string ShortName { get { return shortnames[index]; } }
      public int Number { get { return index + 1; } }

      public override bool Equals(object obj)
      {
         return index == ((WeekDay)obj).index;
      }

      public override int GetHashCode()
      {
         return base.GetHashCode();
      }

   }
}

using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager.Utils
{
   public class StringUtil
   {
      public static string EscapeQuotes(string str)
      {
         const char QUOTES = '\"';
         const int CHAR_NOT_FOUND = -1;
         const string BACK_SLASH = "\\";
         int i = CHAR_NOT_FOUND;

         while ((i = str.IndexOf(QUOTES, i + 1)) != CHAR_NOT_FOUND)
         {
            str = str.Insert(i, BACK_SLASH);
            i++;
         }

         return str;
      }

      public static string IntToWithAddLeadingZero(int val)
      {
         if (val > 10)
            return val.ToString();
         else
            return String.Format("0{0}", val.ToString());
      }
   }
}

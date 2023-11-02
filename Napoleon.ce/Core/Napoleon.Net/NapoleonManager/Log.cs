using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class Log
   {
      public static void Write(String str)
      {
         TextWriter tw = new StreamWriter("NapoleonManager.log", true);
         tw.Write(DateTime.Now.ToShortDateString() + " " + DateTime.Now.ToString("HH:mm:ss") + "\n");
         tw.Write(str);
         tw.Write("\n");
         tw.Close();
      }
   }
}

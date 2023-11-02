using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Threading;
using System.Globalization;

namespace GRSoft.Ads.Dispatcher
{
   static class Program
   {
      /// <summary>
      /// Главная точка входа для приложения.
      /// </summary>
      [STAThread]
      static void Main(string[] args)
      {
         try
         {
            const string LANGUAGE = "-l";

            if (args.Length > 0)
            {
               for (int i = 0; i < args.Length; i++)
               {
                  if (args[i].Equals(LANGUAGE) && args.Length > i)
                     Thread.CurrentThread.CurrentUICulture = new CultureInfo(args[i + 1]);
               }
            }
         }
         catch (Exception) { }

         Application.EnableVisualStyles();
         Application.SetCompatibleTextRenderingDefault(false);
         Application.Run(new Main());
      }
   }
}

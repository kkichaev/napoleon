/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * ert   21/04/2010   creating
 */

using System;
using System.Collections.Generic;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   static class Program
   {
      /// <summary>
      /// Главная точка входа для приложения.
      /// </summary>
      [STAThread]
      static void Main()
      {
         Application.EnableVisualStyles();
         Application.SetCompatibleTextRenderingDefault(false);
         Application.Run(new MainForm());
      }
   }
}

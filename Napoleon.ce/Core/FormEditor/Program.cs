using System;
using System.Collections.Generic;
using System.Windows.Forms;

namespace NFormEditor
{
   static class Program
   {
      private static float scale = 1.0F;
      //public static float FontSize = 18.7F;

      /// <summary>
      /// The main entry point for the application.
      /// </summary>
      [STAThread]
      static void Main()
      {
         Application.EnableVisualStyles();
         Application.SetCompatibleTextRenderingDefault(false);
         Application.Run(new FormDeisgner());
      }

      public static float ScaleFactor
      {
         get { return scale; }
         set { scale = value; }
      }
   }
}
using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Text;

using System.IO;
using System.IO.Compression;
using ICSharpCode.SharpZipLib.Zip.Compression.Streams;
using ICSharpCode.SharpZipLib.Checksums;
using System.Reflection;

namespace GRSoft.NapoleonAdmin
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
         Type mainFormType =typeof(MainForm);
         ConstructorInfo ci = mainFormType.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(null);
         Application.Run(fm);
      }
   }
}

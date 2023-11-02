/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * ert   21/04/2010   creating
 */

using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Threading;
using System.Diagnostics;
using System.Runtime.InteropServices;
using System.IO;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   static class Program
   {
      [DllImport("user32.dll")]
      [return: MarshalAs(UnmanagedType.Bool)]
      static extern bool SetForegroundWindow(IntPtr hWnd);

      private readonly static string PROGRAMM_NAME = "NapoleonManager";
      private static readonly string unhFile = "unh.txt";

      /// <summary>
      /// Главная точка входа для приложения.
      /// </summary>
      [STAThread]
      static void Main()
      {
         bool onlyInstance = false;
         Config cfg = Config.GetConfig();

#if Peplov
         const string EMAIL_JOB_ARG = "-email";
         List<string> arguments = new List<string>(Environment.GetCommandLineArgs());

         if (arguments.Contains(EMAIL_JOB_ARG))
         {
            Environment.Exit(new EmailJob().DoJob());
         }
#endif

         using (Mutex mtx = new Mutex(true, PROGRAMM_NAME, out onlyInstance))
         {
            if (!cfg.onlyInstance || onlyInstance)
            {
               //AppDomain.CurrentDomain.UnhandledException += new UnhandledExceptionEventHandler(CurrentDomain_UnhandledException);
               //Application.ThreadException += new ThreadExceptionEventHandler(Application_ThreadException);

               Type pinit = FormEntries.GetFormType(typeof(ProgramInitializer));
               ProgramInitializer pi = (ProgramInitializer)pinit.GetConstructor(Type.EmptyTypes).Invoke(null);
               if( pi.Initialize())
               {
                  Application.EnableVisualStyles();
                  Application.SetCompatibleTextRenderingDefault(false);
                  Type mainFormType = FormEntries.GetFormType(typeof(MainForm));
                  ConstructorInfo ci = mainFormType.GetConstructor(Type.EmptyTypes);
                  Form mf = (Form)ci.Invoke(null);
                  Application.Run(mf);
               }
            }
            else
            {
               Process current = Process.GetCurrentProcess();
               foreach (Process process in Process.GetProcessesByName(current.ProcessName))
               {
                  if (process.Id != current.Id)
                  {
                     SetForegroundWindow(process.MainWindowHandle);
                     break;
                  }
               }
            }
         }
      }

      static void CurrentDomain_UnhandledException(object sender, UnhandledExceptionEventArgs e)
      {
         SaveLog(e.ExceptionObject.ToString());
      }

      static void Application_ThreadException(object sender, ThreadExceptionEventArgs e)
      {
         SaveLog(e.Exception.ToString());
      }

      static void SaveLog(String text)
      {
         using (StreamWriter outfile = new StreamWriter(unhFile))
         {
            outfile.Write(text);
         }
      }
   }

   public class ProgramInitializer
   {
      public virtual bool Initialize() { return true; }
   }
}

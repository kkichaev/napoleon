/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Работа с вэб браузером
 * 
 * kki   28/12/2010   creating
 */
using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using Microsoft.Win32;

namespace GRSoft.NapoleonManager.Utils
{
   public class OpenLink
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(
      IntPtr hwnd,
      string lpOperation,
      string lpFile,
      string lpParameters,
      string lpDirectory,
      int nShowCmd);

      /// <summary> 
      /// Открывает веб ссылку в новом окне броузера по умолчанию 
      /// </summary> 
      /// <param name="url">string url</param> 
      public static void NewWindow(string url)
      {
         string browser = (string)Registry.GetValue(Registry.ClassesRoot +
         @"\http\shell\open\command",
         "",
         "nothing");
         browser = browser.Trim('"').ToLower();
         int end = browser.IndexOf(".exe");
         browser = browser.Substring(0, end + 4);
         ShellExecute(IntPtr.Zero, "open", browser, url, "", 1);
      }
   }
}

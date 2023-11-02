using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using Microsoft.Win32;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
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

      private static string QuoteString(string url)
      {
         if (url[0] == '"') return url;
         return '"' + url + '"';
      }
      /// <summary> 
      /// Открывает веб ссылку в новом окне броузера по умолчанию 
      /// </summary> 
      /// <param name="url">string url</param> 
      public static void NewWindow(string url)
      {
         if(url != null && url != "")
            ShellExecute(IntPtr.Zero, "open", QuoteString(url), "", "", 1);
         //string browser = (string)Registry.GetValue(Registry.ClassesRoot +
         //@"\http\shell\open\command",
         //"",
         //"nothing");
         //browser = browser.Trim('"').ToLower();
         //int end = browser.IndexOf(".exe");
         //browser = browser.Substring(0, end + 4);
         //int res = (int)ShellExecute(IntPtr.Zero, "open", browser, QuoteString(url), "", 1);

         //MessageBox.Show(browser + " \n-----\n" + QuoteString(url) + " \n-----\n" + res.ToString());
      }

      public static void OpenFile(string file)
      {
         ShellExecute(IntPtr.Zero, "open", QuoteString(file), "", "", 1);
      }
   }
}

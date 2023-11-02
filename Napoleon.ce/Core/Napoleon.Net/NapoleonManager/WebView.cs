using Microsoft.Web.WebView2.Core;
using Microsoft.Web.WebView2.WinForms;
using System;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public class WebView : WebView2
   {
      bool showError = false;
      bool canNavigate = false;
      string currentHtml = "";
      OnInitCompletedHandler handler;

      public bool IsWebViewExists()
      {
         bool ret = false;
         try
         {
            string ver = CoreWebView2Environment.GetAvailableBrowserVersionString();
            if(ver != null && ver.Length != 0)
               ret = true;
         } catch(Exception e)
         {
            //string text = e.Message + "\n" + e.StackTrace;
            //MessageBox.Show(text);
         }
         return ret;
      }

      public WebView()
      {
      }

      public bool Inited { get { return canNavigate; } }

      public void ExecuteScript(string text)
      {
         CoreWebView2.ExecuteScriptAsync(text);
      }

      public void AddHostObjectToScript(string objName, object obj)
      {
         CoreWebView2.AddHostObjectToScript(objName, obj);
      }

      public void Init(bool showError, OnInitCompletedHandler handler = null)
      {
         this.showError = showError;
         this.handler = handler;

         try
         {
            var env = CoreWebView2Environment.CreateAsync(null, Config.AppFolder()).Result;
            CoreWebView2InitializationCompleted += Wb_CoreWebView2InitializationCompleted;
            EnsureCoreWebView2Async(env);
         } catch(Exception e)
         {
            if(showError)
            {
               MessageBox.Show(e.ToString());
            }
         }
      }
      private void Wb_CoreWebView2InitializationCompleted(object sender, Microsoft.Web.WebView2.Core.CoreWebView2InitializationCompletedEventArgs e)
      {
         if(handler != null)
         {
            handler.Invoke(this);
         }
         if (!e.IsSuccess && showError)
         {
            MessageBox.Show(e.InitializationException.Message);
            return;
         }

         canNavigate = true;
         if (currentHtml.Length > 0)
            NavigateToString(currentHtml);
      }

      public string CurrentHTML { get { return currentHtml; } }

      public void Navigate(string txt)
      {
         currentHtml = txt;
         if (canNavigate)
            NavigateToString(txt);
      }
   }

   public delegate void OnInitCompletedHandler(WebView sender);
}

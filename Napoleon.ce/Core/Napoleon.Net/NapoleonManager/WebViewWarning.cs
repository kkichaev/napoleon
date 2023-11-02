using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class WebViewWarning : Form
   {
      public WebViewWarning()
      {
         InitializeComponent();
         rtb.Rtf = @"{\rtf1\ansi\ansicpg1251\deff0\nouicompat\deflang1049\deflangfe1049{\fonttbl{\f0\fswiss\fprq2\fcharset204 Arial;}}
{\colortbl ;\red0\green77\blue187;}
{\*\generator Riched20 10.0.22621}{\*\mmathPr\mnaryLim0\mdispDef1\mwrapIndent1440 }\viewkind4\uc1 
\pard\nowidctlpar\sa200\f0\fs24\'c2\'ed\'e8\'ec\'e0\'ed\'e8\'e5!\par
\'c4\'eb\'ff \'ee\'f2\'ee\'e1\'f0\'e0\'e6\'e5\'ed\'e8\'ff \'ea\'e0\'f0\'f2 \'e2 \'ef\'f0\'e8\'eb\'ee\'e6\'e5\'ed\'e8\'e8 \'cd\'e0\'ef\'ee\'eb\'e5\'ee\'ed \'cc\'e5\'ed\'e5\'e4\'e6\'e5\'f0 \'ed\'e5\'ee\'e1\'f5\'ee\'e4\'e8\'ec\'ee \'f3\'f1\'f2\'e0\'ed\'ee\'e2\'e8\'f2\'fc \'e4\'ee\'ef\'ee\'eb\'ed\'e8\'f2\'e5\'eb\'fc\'ed\'fb\'e5 \'ea\'ee\'ec\'ef\'ee\'ed\'e5\'ed\'f2\'fb. \'d1\'e2\'ff\'e6\'e8\'f2\'e5\'f1\'fc \'f1 \'e2\'e0\'f8\'e8\'ec \'f1\'e8\'f1\'f2\'e5\'ec\'ed\'fb\'ec \'e0\'e4\'ec\'e8\'ed\'e8\'f1\'f2\'f0\'e0\'f2\'ee\'f0\'ee\'ec \'e8\'eb\'e8 \'f3\'f1\'f2\'e0\'ed\'ee\'e2\'e8\'f2\'e5 \'e8\'f5 \'f1\'e0\'ec\'ee\'f1\'f2\'ee\'ff\'f2\'e5\'eb\'fc\'ed\'ee.\par
\'c4\'eb\'ff \'f1\'e0\'ec\'ee\'f1\'f2\'ee\'ff\'f2\'e5\'eb\'fc\'ed\'ee\'e9 \'f3\'f1\'f2\'e0\'ed\'ee\'e2\'ea\'e8 \'e2\'ee\'f1\'ef\'ee\'eb\'fc\'e7\'f3\'e9\'f2\'e5\'f1\'fc \'e8\'ed\'f1\'f2\'f0\'f3\'ea\'f6\'e8\'e5\'e9 \'ef\'ee \cf1\ul\'f1\'f1\'fb\'eb\'ea\'e5\cf0\ulnone .\par
}
 ";
         rtb.Click += Rtb_Click;
         rtb.KeyDown += WebViewWarning_KeyDown;

         rtb.GotFocus += Rtb_GotFocus;
         rtb.Enter += Rtb_GotFocus;

         BackColor = rtb.BackColor;

      }

      private void Rtb_GotFocus(object sender, EventArgs e)
      {
         label1.Focus();
      }

      private void Rtb_Click(object sender, EventArgs e)
      {
         string link = @"https://grsoft.ru/wiki/doku.php?id=maps4";
         System.Diagnostics.Process.Start(link);
      }

      public static void Open()
      {
         new WebViewWarning().ShowDialog();
      }

      private void button1_Click(object sender, EventArgs e)
      {
         Close();
      }

      private void WebViewWarning_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Escape)
         {
            Close();
         }
      }

      public static bool IsWebViewExists()
      {
         bool ret = false;
         string err;
         try
         {
            Type tp = typeof(WebViewWarning).Assembly.GetType("GRSoft.NapoleonManager.WebView");
            object var = tp.GetConstructor(Type.EmptyTypes).Invoke(null);
            object res = tp.InvokeMember("IsWebViewExists", 
               BindingFlags.InvokeMethod | BindingFlags.Public | BindingFlags.Instance,
               null, var, null);
            if(res is Boolean)
               ret = (bool)res;
         }
         catch (Exception e)
         {
            //string text = e.Message + "\n" + e.StackTrace;
            //MessageBox.Show(text);
            //err = e.Message;
         }
         return ret;
      }
   }
}

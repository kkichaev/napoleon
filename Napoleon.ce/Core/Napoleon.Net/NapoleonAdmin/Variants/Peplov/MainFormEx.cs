using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   class MainFormEx : MainForm
   {
      public MainFormEx()
      {
         TabPage tp = new TabPage();
         tp.Name = "smtpSetting";
         tp.Padding = new System.Windows.Forms.Padding(3);
         tp.Location = new System.Drawing.Point(4, 22);
         tp.Size = new System.Drawing.Size(1238, 503);
         tp.Text = "Почтовые настройки";
         tp.UseVisualStyleBackColor = true;

         tp.Controls.Add(new SmtpSetting(this));
         tabControl1.Controls.Add(tp);

      }
   }
}
